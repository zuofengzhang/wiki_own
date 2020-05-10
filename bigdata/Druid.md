---
title: "Druid基础"
layout: post
date: 2019-08-10 14:58:00
category: bigdata
tags:
 - Java
 - Druid

share: true
comments: true
---


# Druid

## 什么样的业务适合用 Druid?

建议如下：

时序化数据：Druid 可以理解为时序数据库，所有的数据必须有时间字段。
实时数据接入可容忍丢数据（tranquility）： tranquility 有丢数据的风险，所以建议实时和离线一起用，实时接当天数据，离线第二天把今天的数据全部覆盖，保证数据完备性。
OLAP 查询而不是 OLTP 查询：Druid 查询并发有限，不适合 OLTP 查询。
非精确的去重计算：目前 Druid 的去重都是非精确的。
无 Join 操作：Druid 适合处理星型模型的数据，不支持关联操作。
数据没有 update 更新操作，只对 segment 粒度进行覆盖：由于时序化数据的特点，Druid 不支持数据的更新

## 离线批量入库脚本

### spark

https://github.com/Fokko/druid-indexing-on-spark.git

### pyspark

Druid是一款高性能的列式存储时序数据库，其支持实时数据分析并在OLAP数据分析领域有其特有的优势。Druid除了支持实时摄入数据外也支持离线批量导入数据，主要通过离线MR任务去HDFS上拉取数据并做聚合roll up处理入库。
该脚本可作为通用的druid入库离线任务脚本，方便在配置离线任务流即数据写到HDFS后起对应的入库任务。该脚本可运行在tesla平台作为pyspark任务执行。

```python
#!/usr/bin/env python

from __future__ import print_function
from pyspark import SparkContext

import json
import re
import sys
import time
import urllib2
import urlparse
import datetime

def read_task_file(filename):
    with open(filename, 'r') as f:
        contents = f.read()
        # We don't use the parsed data, but we want to throw early if it's invalid
        try:
            json.loads(contents)
        except Exception, e:
            print('Invalid JSON in task file "{0}": {1}\n'.format(filename, repr(e)))
            sys.exit(1)
        return contents

# Keep trying until timeout_at, maybe die then
def post_task(url, task_json, timeout_at):
    try:
        task_url = url.rstrip("/") + "/druid/indexer/v1/task"
        req = urllib2.Request(task_url, task_json, {'Content-Type' : 'application/json'})
        timeleft = timeout_at - time.time()
        response_timeout = min(max(timeleft, 5), 10)
        response = urllib2.urlopen(req, None, response_timeout)
        return response.read().rstrip()
    except urllib2.URLError as e:
        if isinstance(e, urllib2.HTTPError) and e.code >= 400 and e.code <= 500:
            # 4xx (problem with the request) or 500 (something wrong on the server)
            raise_friendly_error(e)
        elif time.time() >= timeout_at:
            # No futher retries
            raise_friendly_error(e)
        elif isinstance(e, urllib2.HTTPError) and e.code in [301, 302, 303, 305, 307] and \
                        e.info().getheader("Location") is not None:
            # Set the new location in args.url so it can be used by await_task_completion and re-issue the request
            location = urlparse.urlparse(e.info().getheader("Location"))
            url = "{0}://{1}".format(location.scheme, location.netloc)
            print("Redirect response received, setting url to [{0}]\n".format(url))
            return post_task(url, task_json, timeout_at)
        else:
            # If at first you don't succeed, try, try again!
            sleep_time = 30
            extra = ''
            if hasattr(e, 'read'):
                extra = e.read().rstrip()
            print("Waiting up to {0}s for indexing service to become available. [Got: {1} {2}]".format(max(sleep_time, int(timeout_at - time.time())), str(e), extra).rstrip())
            print("\n")
            time.sleep(sleep_time)
            return post_task(url, task_json, timeout_at)

# Keep trying until timeout_at, maybe die then
def await_task_completion(url, task_id, timeout_at):
    while True:
        task_url = url.rstrip("/") + "/druid/indexer/v1/task/{0}/status".format(task_id)
        req = urllib2.Request(task_url)
        timeleft = timeout_at - time.time()
        response_timeout = min(max(timeleft, 5), 30)
        response = urllib2.urlopen(req, None, response_timeout)
        response_obj = json.loads(response.read())
        response_status_code = response_obj["status"]["status"]
        if response_status_code in ['SUCCESS', 'FAILED']:
            return response_status_code
        else:
            if time.time() < timeout_at:
                print("Task {0} still running...".format(task_id))
                timeleft = timeout_at - time.time()
                time.sleep(min(30, timeleft))
            else:
                raise Exception("Task {0} did not finish in time!".format(task_id))

def raise_friendly_error(e):
    if isinstance(e, urllib2.HTTPError):
        text = e.read().strip()
        reresult = re.search(r'<pre>(.*?)</pre>', text, re.DOTALL)
        if reresult:
            text = reresult.group(1).strip()
        raise Exception("HTTP Error {0}: {1}, check overlord log for more details.\n{2}".format(e.code, e.reason, text))
    raise e

def get_task_json(content, hdfspath, data_source, date, segment, query):
    input_json = json.loads(content)
    input_json["spec"]["ioConfig"]["inputSpec"]["paths"] = hdfspath + "/" + date
    input_json["spec"]["dataSchema"]["dataSource"] = data_source

    date_array = []
    date_time = datetime.datetime(int(date[0:4]),int(date[4:6]),int(date[6:8]))
    date_time_next = date_time + datetime.timedelta(days=1)

    date_array.append(date_time.strftime('%Y-%m-%dT%H:%M:%S+08:00') + "/" + date_time_next.strftime('%Y-%m-%dT%H:%M:%S+08:00'))
    input_json["spec"]["dataSchema"]["granularitySpec"]["segmentGranularity"] = segment
    input_json["spec"]["dataSchema"]["granularitySpec"]["queryGranularity"] = query
    input_json["spec"]["dataSchema"]["granularitySpec"]["intervals"] = date_array
    return json.dumps(input_json, indent=2)

def main():
    """
    Usage: druid_task.py <url> <task_file> <date> <submit_timeout> <complete_timeout> <hdfs_path> <data_source>
    """
    if len(sys.argv) < 10:
        print("Usage: druid_task.py <url> <task_file> <date> <submit_timeout> <complete_timeout> <hdfs_path> <data_source> <segment> <query>")
        exit(1)
    print(sys.argv)

    url = sys.argv[1].strip()
    task_file = sys.argv[2].strip()
    date = sys.argv[3].strip()
    submit_timeout = sys.argv[4].strip()
    complete_timeout = sys.argv[5].strip()
    hdfspath = sys.argv[6].strip()
    data_source = sys.argv[7].strip()
    date_segment = sys.argv[8].strip()
    date_query = sys.argv[9].strip()

    # data path
    datapath = hdfspath + "/" + date

    # init spark context
    sc = SparkContext(appName="druid_index_task_day")

    datafiles_rdd = sc.wholeTextFiles(datapath)
    is_empty = datafiles_rdd.isEmpty()
    print(is_empty)
    print("datapath:" + datapath)
    if is_empty == False:
        submit_timeout_at = time.time() + float(submit_timeout)
        complete_timeout_at = time.time() + float(complete_timeout)
        task_json = get_task_json(read_task_file(task_file), hdfspath, data_source, date, date_segment, date_query)
        print(task_json)

        task_id = json.loads(post_task(url, task_json, submit_timeout_at))["task"]
        sys.stderr.write('\033[1m' + "Task started: " + '\033[0m' + "{0}\n".format(task_id))
        sys.stderr.write('\033[1m' + "Task log:     " + '\033[0m' + "{0}/druid/indexer/v1/task/{1}/log\n".format(url.rstrip("/"),task_id))
        sys.stderr.write('\033[1m' + "Task status:  " + '\033[0m' + "{0}/druid/indexer/v1/task/{1}/status\n".format(url.rstrip("/"),task_id))

        task_status = await_task_completion(url, task_id, complete_timeout_at)
        print("Task finished with status: {0}\n".format(task_status))
        if task_status != 'SUCCESS':
            sys.exit(1)
    else:
        print("Task finished with no data.")

if __name__ == "__main__":
    main()
```

