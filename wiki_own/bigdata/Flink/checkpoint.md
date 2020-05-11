---
title: "Flink-checkpoint与savepoint"
layout: post
date: 2019-08-10 14:58:00
category: bigdata
tags:
 - Java
 - Flink

share: true
comments: true
---

# Flink Checkpoint与savepoint


![barrier](_v_images/20190826155132363_1296122979.png)



Checkpoint指定触发生成时间间隔后，每当需要触发Checkpoint时，会向Flink程序运行时的多个分布式的Stream Source中插入一个Barrier标记，这些Barrier会根据Stream中的数据记录一起流向下游的各个Operator。
当一个Operator接收到一个Barrier时，它会暂停处理Steam中新接收到的数据记录。
因为一个Operator可能存在多个输入的Stream，而每个Stream中都会存在对应的Barrier，该Operator要等到所有的输入Stream中的Barrier都到达。(**对齐**)
当所有Stream中的Barrier都已经到达该Operator，这时所有的Barrier在时间上看来是同一个时刻点（表示已经对齐），在等待所有Barrier到达的过程中，
Operator的Buffer中可能已经缓存了一些比Barrier早到达Operator的数据记录（Outgoing Records），这时该Operator会将数据记录（Outgoing Records）发射（Emit）出去，作为下游Operator的输入，
最后将Barrier对应Snapshot发射（Emit）出去作为此次Checkpoint的结果数据。




[参考文献]
------

1. [Flink Checkpoint、Savepoint配置与实践](http://shiyanjun.cn/archives/1855.html)