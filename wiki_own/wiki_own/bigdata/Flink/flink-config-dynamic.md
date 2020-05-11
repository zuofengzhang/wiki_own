---
title: "Flink/Storm 动态更新配置实现方案"
date: 2019-06-09 00:00:00
layout: post
category: Distributed
tags:
 - Distributed
 - Hadoop
 - mapReducer
 - flink

share: true
comments: true
---

实时计算处理无限数据流，对系统可用性十分敏感，然而业务需求具有必然的更新需求，动态更新实时计算的配置是常见的需求，比如动态增加用户白名单、业务数据在线 debug、新增广告位统计等等。然而，实现并不简单，Apache Strom 和 Apache Storm 具有不同的架构，实现方式也不尽相同。

Apache Strom(这里不包括 Trident)，所有的计算逻辑都是通过实现spout 和 bolt，运行在 task节点上，因此与业务逻辑相关的状态管理、配置管理以及输入输出的控制等均可以定义在 bolt 中，如定时一分钟轮询配置变化、定时 checkpoint 近似数据集等。此外，配置同样可以通过控制流的方式与数据流 uion 到一起，在计算节点检测判断控制流的关键字来实现配置更新的目的。

Apache Flink 的函数式 API 封装要比 Storm 要完善，而灵活性不比 Strom。实现配置动态更新有两种方式，一是在算子中定时轮询拉取配置信息，二是利用广播状态和控制流。



# asyncIO
















# stateBroadcast


