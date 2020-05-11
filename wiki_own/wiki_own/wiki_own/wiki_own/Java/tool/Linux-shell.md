---
title: "Linux常用shell"
layout: post
date: 2019-08-10 14:58:00
category: tool
tags:
 - Java
 - Linux
 - shell

share: true
comments: true
---


# Linux-shell

## curl


curl是一个很棒的命令.
例如目标网站Url:

    127.0.0.1:8080/check_your_status?user=Summer&passwd=12345678

### 通过Get方法请求: 

    curl protocol://address:port/url?args
    curl http://127.0.0.1:8080/check_your_status?user=Summer&passwd=12345678

发送数组参数

    curl http://127.0.0.1:8080/check_yours_status?user[]=avery&user[]=zhangsan

发送包含特殊字符的参数

    curl -G --data-urlencode 'user[]=avery@tencent#td' --data-urlencode 'user[]=zhangsan@tencent#bf' -d 'tableId=913' http://127.0.0.1:8080/check_yours_status

“-G”等价于”—get”，”-d”等价于”—data”

从文件中获取参数

    curl --get --data @data.txt http://aiezu.com/test.php

### 通过Post方法请求:

    curl -d “args” “protocol://address:port/url”
    curl -d “user=Summer&passwd=12345678” “http://127.0.0.1:8080/check_your_status“

### 自定义header
这种方法是参数直接在header里面的
如需将输出指定到文件可以通过重定向进行操作.

    curl -H "Content-Type:application/json" -X POST --data (json.data) URL
    curl -H "Content-Type:application/json" -X POST --data '{"message": "sunshine"}' http://localhost:8000/

这种方法是json数据直接在body里面的


### 输出到文件

```shell
curl -o tank_pos_meta.json 'http://9.22.24.233:8080/ec/v1/listTable?pageNum=1&pageSize=999&type=hippo&dbName=bank-pos-info&name=t_bank_pos_yyyymmdd'
```
> URL必须带分号



## date

两天前

```shell

```