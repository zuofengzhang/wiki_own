---
title: "【草稿】Stream processing with Apache Flink"
layout: post
date: 2010-08-10 14:58:00
category: Flink
tags:
 - Java
 - Image
 - rtc

share: true
comments: true
---


# Stream processing with Apache Flink

## 状态+重放是保证exactly once的基础
Connecting a stateful streaming application running on Flink and an event log is interesting for multiple reasons.
In this architecture the event log persists the input events and can replay them in deterministic order.
In case of a failure, Flink recovers a stateful streaming application by restoring its state from a previous checkpoint and resetting the read position on the event log.
The application will replay (and fast forward) the input events from the event log until it reaches the tail of the stream.
This technique is used to recover from failures but can also be leveraged to update an application, fix bugs and repair previously emitted results, migrate an application to a different cluster, or perform A/B tests with different application versions.

将运行在Flink上的有状态流应用程序与事件日志连接起来非常有趣，原因有很多。在这种体系结构中，事件日志保存输入事件，并可以以确定的顺序重播它们。在失败的情况下，Flink通过从以前的检查点恢复有状态流应用程序的状态并重新设置事件日志上的读位置来恢复有状态流应用程序。应用程序将重播(并快进)事件日志中的输入事件，直到它到达流的尾部。此技术用于从故障中恢复，但也可用于更新应用程序、修复bug和修复以前发出的结果、将应用程序迁移到不同的集群或使用不同的应用程序版本执行a/B测试。

## 事件驱动

Event-driven applications offer several benefits compared to transactional applications or microservices. Local state access provides very good performance compared to reading and writing queries against remote datastores. Scaling and fault tolerance are handled by the stream processor, and by leveraging an event log as the input source the complete input of an application is reliably stored and can be deterministically replayed. Furthermore, Flink can reset the state of an application to a previous savepoint, making it possible to evolve or rescale an application without losing its state.

与事务性应用程序或微服务相比，事件驱动应用程序有几个优点。与对远程数据存储读写查询相比，本地状态访问提供了非常好的性能。扩展和容错由流处理器处理，通过利用事件日志作为输入源，应用程序的完整输入被可靠地存储并可以确定地重播。此外，Flink可以将应用程序的状态重置为以前的保存点，从而可以在不丢失状态的情况下演化或重新调整应用程序
