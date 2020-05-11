---
title: "HDFS基础"
layout: post
date: 2016-08-10 14:58:00
category: bigdata
tags:
 - Java
 - Image
 - HDFS

share: true
comments: true
---


- NameNode 保存这个文件的数据块信息
- DataNode 存储实际的文件
- 当MR启动的时候，首先获取所要处理的文件的块信息和节点信息，然后在这些节点上同时启动任务，在数据所在节点上同时处理

MapReduce是底层编程语言，您需要专业知识才能在此级别工作。工程师在Map Reduce上开发了一个名为Hive的层，以便用户可以使用SQL来处理HDFS中存储的数据。Hive将这些SQL查询转换为一系列Map Reduce，然后调度执行。因此，Hive极大地简化了数据处理任务。但是Hive使用底层Map Reduce体系结构，这是用于处理数据的另一层

Impala 一旦部署在Hadoop集群中，它将在每个数据节点上运行自己的进程，并完全跳过MapReduce阶段。一旦收到一个SQL，Impala master节点调度数据所在的节点上直接执行任务。因为它跳过MapReduce转换阶段并直接在节点上执行，所以它可以完成每秒钟的SQL查询

当您在Hive中启动查询时，在背景地图中，Reduce会出现在启动查询需要花费大量时间的画面中，就像您在Impala中启动查询一样，它使用自己的体系结构，即MPP(大规模并行处理)而不是Map Reduce来从HDFS获取结果。它在内存中执行查询，总是比磁盘快。

It’s an HDFS quirk. A file that’s currently being written to will appear to have a size of 0 but once it’s closed it will show its true size
