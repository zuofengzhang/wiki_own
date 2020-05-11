---
title: "Flink:时间"
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



# Flink Time

![北京时间](_v_images/20190731222505723_213254839.png =968x)

> 划定时间窗口和时间戳以毫秒为单位

```scala
eventStream.assignTimestampsAndWatermarks(assigner = new AscendingTimestampExtractor[Event]() {
  override def extractAscendingTimestamp(t: Event): Long = {
    t.getTime * 1000
  }
})
```

## processFunction中的时间

```scala
new ProcessFunction[Event, (String, Long)] {

        override def processElement(event: Event, context: ProcessFunction[Event,
          (String, Long)]#Context, collector: Collector[(String, Long)]): Unit = {
```

```
context.timestamp=1565064599999
context.timerService().currentWatermark()=-9223372036854775808
context.timerService().currentProcessingTime()=1565064607343

Event(time=1565064350, count=9, url=/apply/main, channelId=1057)
```
以事件事件，窗口大小5min

一条消息，事件的时间是 1565064350(2019-08-06 12:05:50)
`Context.timestamp`是1565064599999(2019-08-06 12:09:59), 可以理解为窗口结束的时间
`context.timerService().currentWatermark()` 莫名其妙，这么大
`context.timerService().currentProcessingTime()`是当前处理事件 2019-08-06 12:10:07


### `Context.timestamp`

```java
Timestamp of the element currently being processed or timestamp of a firing timer.
This might be {@code null}, for example if the time characteristic of your program
```
