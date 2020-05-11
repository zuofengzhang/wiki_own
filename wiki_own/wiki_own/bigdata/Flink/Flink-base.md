---
title: "Flink核心知识点梳理"
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

# Flink

## stateful stream processing

processFunction

低阶API
构建一些新的组件
比如 利用定时做一定情况下的匹配和缓存
灵活，开发比较复杂

凌晨更新  注册定时器 


## state

状态托管
operator state 算子状态
keyed state
State Backend (rocksdb + hdfs)

## checkpoint

barrier

## DataStream 广播


## CoLocationGroup

## SlotSharingGroup



### 如何自定义 Window？

1、Window Assigner

负责将元素分配到不同的 window。

Window API 提供了自定义的 WindowAssigner 接口，我们可以实现 WindowAssigner 的

```
public abstract Collection<W> assignWindows(T element, long timestamp)
```

方法。同时，对于基于 Count 的 window 而言，默认采用了 GlobalWindow 的 window assigner，例如：

```
keyBy.window(GlobalWindows.create())
```

2、Trigger

Trigger 即触发器，定义何时或什么情况下移除 window

我们可以指定触发器来覆盖 WindowAssigner 提供的默认触发器。 请注意，指定的触发器不会添加其他触发条件，但会替换当前触发器。

3、Evictor（可选）

驱逐者，即保留上一 window 留下的某些元素

4、通过 apply WindowFunction 来返回 DataStream 类型数据。

利用 Flink 的内部窗口机制和 DataStream API 可以实现自定义的窗口逻辑，例如 session window。



### EventTime & ProcessTime & IngestionTime

 Event time programs must specify how to generate *Event Time Watermarks*, which is the mechanism that signals progress in event time

Internally, *ingestion time* is treated much like *event time*, but with automatic timestamp assignment and automatic watermark generation.

## watermark

kafka的每个分区event-time到来的时间不一致

using Flink’s Kafka-partition-aware watermark generation. watermarks are generated inside the Kafka consumer, per Kafka partition, and the per-partition watermarks are merged in the same way as watermarks are merged on stream shuffles.

watermark在每个kafka分区的kafka消费者中产生，每个分区的watermark合并起来，如在stream shuffle中合并watermark一样



## HadoopOutputFormat

flink hbase connector

## 算子之间数据传递，是如何序列化的

