---
title: "Flink: 数据源重放"
layout: post
date: 2016-08-10 14:58:00
category: bigdata
tags:
 - Java
 - Image
 - Flink

share: true
comments: true
---


# flink-table-api



## Hippo和Tube如何实现重放?


### FlinkHippoConsumer




## 举一反三

### org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction

构建并行数据流的基础类，在执行时，运行时将执行与源配置的并行一样多的该函数的并行实例。

## org.apache.flink.types.Row

一行数据

```java
Object[]  fields
```

