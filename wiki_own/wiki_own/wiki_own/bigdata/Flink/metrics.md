---
title: "Flink:指标监控"
layout: post
date: 2019-08-10 14:58:00
category: bigdata
tags:
 - Java
 - Flink
 - HBase

share: true
comments: true
---

# flink-metrics


[参考文献]

- [Flink源码系列-指标监控](https://blog.csdn.net/qq_21653785/article/details/79625601)
- [自定义metric-report](https://www.cnblogs.com/0x12345678/p/10561039.html)
- [深入理解Flink之metrics](http://www.mamicode.com/info-detail-2317943.html)
- [聊聊Flink的MertricsQueryServiceGateway](https://my.oschina.net/go4it/blog/3023586)
- [Flink指标](https://www.jianshu.com/p/e50586fff515)


Flink Metrics是通过引入`com.codahale.metrics`包实现的，它将收集的metrics分为四大类：`Counter`，`Gauge`，`Histogram`和`Meter`下面分别说明：

- `Counter计数器`
    用来统计一个metrics的总量。
    拿flink中的指标来举例，像Task/Operator中的numRecordsIn（此task或者operator接收到的record总量）和numRecordsOut（此task或者operator发送的record总量）就属于Counter。
- `Gauge指标值`
    用来记录一个metrics的瞬间值。
    拿flink中的指标举例，像JobManager或者TaskManager中的`JVM.Heap.Used`就属于`Gauge`，记录某个时刻JobManager或者TaskManager所在机器的JVM堆使用量。
-  `Histogram直方图`
    有的时候我们不满足于只拿到metrics的总量或者瞬时值，当想得到metrics的最大值，最小值，中位数等信息时，我们就能用到Histogram了。
    Flink中属于Histogram的指标很少，但是最重要的一个是属于operator的latency。此项指标会记录数据处理的延迟信息，对任务监控起到很重要的作用。
- `Meter平均值`
     用来记录一个metrics某个时间段内平均值。
     flink中类似指标有task/operator中的numRecordsInPerSecond，字面意思就可以理解，指的是此task或者operator每秒接收的记录数。


### com.tencent.oceanus.metastore.metrics.CustomMetricsRegistry


