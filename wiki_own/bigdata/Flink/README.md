---
title: "Flink:文献与备忘"
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


# 参考文献

- [Flink官方文档翻译](https://github.com/crestofwave1/oneFlink)


# todo

## checkpoint与savepoint区别

##  flink report

##  storm trident

微批



barrier对齐

![flink-rm](_v_images/20190826164001201_1759200368.png =510x)


## 算子
### Reduce在流计算的应用

org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor$LoggingHandler:140 - 
Timestamp monotony violated: 1568772496000 < 1568772500000


ToDo list

# avro 与 kryo 序列化的区别
使用场景

# broadcast 

## storm allGrouping 元素广播

flink broadcast how to use 

broadcast vaiable value can not be modified

用法
1：初始化数据
DataSet<Integer> toBroadcast = env.fromElements(1, 2, 3)
2：广播数据
.withBroadcastSet(toBroadcast, "broadcastSetName");
3：获取数据
Collection<Integer> broadcastSet = getRuntimeContext().getBroadcastVariable("broadcastSetName");

- 广播变量不能太大
- 不能修改，保证数据一致性

# Accumulators & Counters

累加器与计数器


## spark accumulate

用法
1：创建累加器
private IntCounter numLines = new IntCounter(); 
2：注册累加器
getRuntimeContext().addAccumulator("num-lines", this.numLines);
3：使用累加器
this.numLines.add(1); 
4：获取累加器的结果
myJobExecutionResult.getAccumulatorResult("num-lines")

# Distributed Cache

> Spark 分布式缓存如何使用

Flink提供了一个分布式缓存，类似于hadoop，可以使用户在并行函数中很方便的读取本地文件
此缓存的工作机制如下：程序注册一个文件或者目录(本地或者远程文件系统，例如hdfs或者s3)，通过ExecutionEnvironment注册缓存文件并为它起一个名称。当程序执行，Flink自动将文件或者目录复制到所有taskmanager节点的本地文件系统，用户可以通过这个指定的名称查找文件或者目录，然后从taskmanager节点的本地文件系统访问它
用法
1：注册一个文件
env.registerCachedFile("hdfs:///path/to/your/file", "hdfsFile")  
2：访问数据
File myFile = getRuntimeContext().getDistributedCache().getFile("hdfsFile");

# at least once & exactly once

从容错和消息处理的语义上(at least once, exactly once)，Flink引入了state和checkpoint。

# state 
默认保存在java堆中
checkpoint: 把state持久化存储，全局快照
blink做的优化

失败恢复机制 

状态分为原始状态和托管状态

托管状态是由Flink框架管理的状态
而原始状态，由用户自行管理状态具体的数据结构，框架在做checkpoint的时候，使用byte[]来读写状态内容，对其内部数据结构一无所知。
通常在DataStream上的状态推荐使用托管的状态，当实现一个用户自定义的operator时，会使用到原始状态。

> ReducingState 如何使用

# checkpoint

Flink的checkpoint机制可以与(stream和state)的持久化存储交互的前提：
持久化的source，它需要支持在一定时间内重放事件。这种sources的典型例子是持久化的消息队列（比如Apache Kafka，RabbitMQ等）或文件系统（比如HDFS，S3，GFS等）
用于state的持久化存储，例如分布式文件系统（比如HDFS，S3，GFS等）

Checkpoint是Flink实现容错机制最核心的功能，它能够根据配置周期性地基于Stream中各个Operator/task的状态来生成快照，从而将这些状态数据定期持久化存储下来，当Flink程序一旦意外崩溃时，重新运行程序时可以有选择地从这些快照进行恢复，从而修正因为故障带来的程序数据异常

checkpoint开启之后，默认的checkPointMode是Exactly-once
checkpoint的checkPointMode有两种，Exactly-once和At-least-once
Exactly-once对于大多数应用来说是最合适的。At-least-once可能用在某些延迟超低的应用程序（始终延迟为几毫秒）

ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION:表示一旦Flink处理程序被cancel后，会保留Checkpoint数据，以便根据实际需要恢复到指定的Checkpoint
ExternalizedCheckpointCleanup.DELETE_ON_CANCELLATION: 表示一旦Flink处理程序被cancel后，会删除Checkpoint数据，只有job执行失败的时候才会保存checkpoint

# state backend

默认情况下，state会保存在taskmanager的内存中，checkpoint会存储在JobManager的内存中。
state 的store和checkpoint的位置取决于State Backend的配置
env.setStateBackend(…)
一共有三种State Backend
MemoryStateBackend
FsStateBackend
RocksDBStateBackend



MemoryStateBackend
state数据保存在java堆内存中，执行checkpoint的时候，会把state的快照数据保存到jobmanager的内存中
基于内存的state backend在生产环境下不建议使用
FsStateBackend
state数据保存在taskmanager的内存中，执行checkpoint的时候，会把state的快照数据保存到配置的文件系统中
可以使用hdfs等分布式文件系统
RocksDBStateBackend
RocksDB跟上面的都略有不同，它会在本地文件系统中维护状态，state会直接写入本地rocksdb中。同时它需要配置一个远端的filesystem uri（一般是HDFS），在做checkpoint的时候，会把本地的数据直接复制到filesystem中。fail over的时候从filesystem中恢复到本地
RocksDB克服了state受内存限制的缺点，同时又能够持久化到远端文件系统中，比较适合在生产中使用

修改State Backend的两种方式
第一种：单任务调整
修改当前任务代码
env.setStateBackend(new FsStateBackend("hdfs://namenode:9000/flink/checkpoints"));
或者new MemoryStateBackend()
或者new RocksDBStateBackend(filebackend, true);【需要添加第三方依赖】
第二种：全局调整
修改flink-conf.yaml
state.backend: filesystem
state.checkpoints.dir: hdfs://namenode:9000/flink/checkpoints
注意：state.backend的值可以是下面几种：jobmanager(MemoryStateBackend), filesystem(FsStateBackend), rocksdb(RocksDBStateBackend)



# Restart Strategies(重启策略)

默认重启策略通过 flink-conf.yaml 指定

常用的重启策略
固定间隔 (Fixed delay)
失败率 (Failure rate)
无重启 (No restart)
如果没有启用 checkpointing，则使用无重启 (no restart) 策略。 
如果启用了 checkpointing，但没有配置重启策略，则使用固定间隔 (fixed-delay) 策略，其中 Integer.MAX_VALUE 参数是尝试重启次数
重启策略可以在flink-conf.yaml中配置，表示全局的配置。也可以在应用代码中动态指定，会覆盖全局配置

```java
第一种：全局配置 flink-conf.yaml
restart-strategy: fixed-delay
restart-strategy.fixed-delay.attempts: 3
restart-strategy.fixed-delay.delay: 10 s
第二种：应用代码设置
env.setRestartStrategy(RestartStrategies.fixedDelayRestart(
  3, // 尝试重启的次数
  Time.of(10, TimeUnit.SECONDS) // 间隔
));
```





