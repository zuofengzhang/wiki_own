---
title: "Impala: why faster than hive"
layout: post
date: 2016-08-10 14:58:00
category: bigdata
tags:
 - Java
 - Impala

share: true
comments: true
---

[TOC]
# Impala

> [How does impala provide faster query response compared to hive](https://stackoverflow.com/questions/16755599/how-does-impala-provide-faster-query-response-compared-to-hive)

Impala = SQL on HDFS
Hive = SQL on Hadoop

impala darmon running on datanode 
cache some of the data that is in HDFS

## why fast

> why Impala is faster than Hive in Query processing? Below are the some key points.

- While processing SQL-like queries, Impala does not write intermediate results on disk(like in Hive MapReduce); instead full SQL processing is done in memory, which makes it faster.
- With Impala, the query starts its execution instantly compared to MapReduce, which may take significant time to start processing larger SQL queries and this adds more time in processing.
- Impala Query Planner uses smart algorithms to execute queries in multiple stages in parallel nodes to provide results faster, avoiding sorting and shuffle steps, which may be unnecessary in most of the cases.
- Impala has information about each data block in HDFS, so when processing the query, it takes advantage of this knowledge to distribute queries more evenly in all DataNodes.
- There exists Impala daemon, which runs on each DataNode. These are responsible for processing queries.When query submitted, impalad(Impala daemon) reads and writes to data file and parallelizes the query by distributing the work to all other Impala nodes in the Impala cluster.
- Another key reason for fast performance is that Impala first generates assembly-level code for each query. The assembly code executes faster than any other code framework because while Impala queries are running natively in memory, having a framework will add additional delay in the execution due to the framework overhead.


1. 在处理类似sql的查询时，Impala不会在磁盘上写入中间结果(如Hive MapReduce);相反，SQL处理完全是在内存中完成的，这使它更快。
2. 不使用map/reduce, 将数据fork到jvm和流量穿透是非常昂贵的。Impala将查询任务分割成子任务，在数据所在的节点上，各个节点独立并行运行，最后再单个节点上汇总结果。
3. 与MapReduce相比，Impala查询立即开始执行，MapReduce可能会花费大量时间来开始处理较大的SQL查询，这会增加处理时间。
4. Impala Query Planner使用智能算法在并行节点中的多个stage执行查询，以更快地提供结果，避免sortinh和shuffle步骤，这在大多数情况下可能是不必要的。
5. Impala拥有关于HDFS每个数据块的信息，因此在处理查询时，它利用这些知识在所有数据节点中更均匀地分布查询。
6. 存在在每个DataNode上运行的Impala守护进程。它们负责处理查询。当提交查询时，impalad(Impala守护进程)对数据文件进行读写操作，并通过将工作分配给Impala集群中的所有其他Impala节点来并行化查询。
7. 快速性能的另一个关键原因是Impala首先为每个查询生成汇编级别的代码。汇编代码的执行速度比任何其他代码框架都快，因为Impala查询在本机的内存中运行时，拥有一个框架会由于框架开销而增加额外的执行延迟。
8. 它使用hdfs存储，对于大文件来说速度很快。它尽可能多地缓存查询、结果和数据。
9. 它支持新的文件格式，如parquet，即列式存储格式。使用这种格式，数据扫描量更少


Impala在内存中的处理所有查询，因此节点上的内存限制肯定是一个因素。必须有足够的内存来支持生成的数据集，在复杂的连接操作期间，数据集可能会成倍增长。如果查询开始处理数据，结果数据集无法装入可用内存，则查询将失败。


### disadvantage

1. 不支持UDF和自定义序列化
2. impala查询是HiveSQL的子集
3. hive支持存储和查询，而impala只能查询


## Impala doesn't provide fault-tolerance compared to Hive
so if there is a problem during your query then it's gone. 

> Definitely for ETL type of jobs where failure of one job would be costly I would recommend Hive, but Impala can be awesome for small ad-hoc queries, for example for data scientists or business analysts who just want to take a look and analyze some data without building robust jobs. Also from my personal experience, Impala is still not very mature, and I've seen some crashes sometimes when the amount of data is larger than available memory.

对于ETL类型的工作，如果一个工作的失败代价很高，我会推荐Hive，但是Impala对于小的特别查询来说可能是很棒的，例如对于那些只想查看和分析一些数据而不想构建健壮工作的数据科学家或业务分析师来说。从我个人的经验来看，Impala还不太成熟，有时当数据量超过可用内存时，我会看到一些崩溃。


> This means if your join, sort, or group by didn’t fit in memory, Impala would just kill the query. No warning. Just dead. Hive would never do that because it’s underlying processing architecture has no problem doing that.

这就意味着，内存不能承载join sort或group运算，Impala会无告警的立即kill掉查询，直接失败。而hive离线处理架构可以轻松处理，不会死掉


## MPP

> Impala uses MPP(massively parallel processing) unlike Hive which uses MapReduce under the hood, which involves some initial overheads (as Charles sir has specified). Massively parallel processing is a type of computing that uses many separate CPUs running in parallel to execute a single program where each CPU has it's own dedicated memory. The very fact that Impala, being MPP based, doesn't involve the overheads of a MapReduce jobs viz. job setup and creation, slot assignment, split creation, map generation etc., makes it blazingly fast.

Impala提供了更快的响应，因为它使用了MPP(大规模并行处理)，而Hive在底层使用了MapReduce，这涉及到一些初始开销(正如Charles sir所指定的)。大规模并行处理是一种计算类型，它使用许多单独的并行运行的CPU来执行单个程序，其中每个CPU都有自己的专用内存。Impala是基于MPP的，它不涉及MapReduce作业的开销，即作业设置和创建、插槽分配、分割创建、地图生成等，这使得它的运行速度非常快。

> But that doesn't mean that Impala is the solution to all your problems. Being highly memory intensive (MPP), it is not a good fit for tasks that require heavy data operations like joins etc., as you just can't fit everything into the memory. This is where Hive is a better fit.

MPP 作为高度内存密集型的运算，因为无法将所有的数据放入内存，并不适合需要大数据量的操作(如join)；反而Hive更适合

实时的对部分数据即时查询适合Impala，对大数据的批处理请求，适合Hive


## Impala not always faster than hive

> In my current job we used to make all the querys with Impala, the biggest query (like 800 million rows after 8 joins) was taking from 2 to 20 mins (depending if the tables where already on memory or not) I migrate that Query to Hive, partitioned the tables and create buckets on them, now it takes 3–4 mins, It may be a few more seconds than the best 2 mins with Impala but it’s much more fast if we consider that most of the time Impala takes 12–18 mins.
 
在我目前的工作中，使用Impala进行所有的查询，最大的查询(比如8个连接后的8亿行)需要2到20分钟(取决于表是否已经在内存中);我将该查询迁移到Hive，对表进行分区并在其上创建桶，现在需要3到4分钟，这可能比Impala最好的2分钟多几秒钟，但如果我们考虑Impala大部分时间需要12到18分钟，速度会快得多。 
 








