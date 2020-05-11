---
title: "Flink VS Spark"
layout: post
date: 2019-08-10 14:58:00
category: bigdata
tags:
 - Java
 - Flink

share: true
comments: true
---


# Flink VS Spark

Spark Structure Streaming 是什么？

### 1、抽象 Abstraction

　　Spark中，对于批处理我们有RDD,对于流式，我们有DStream，不过内部实际还是RDD.所以所有的数据表示本质上还是RDD抽象。在Flink中，对于批处理有DataSet，对于流式我们有DataStreams。看起来和Spark类似，他们的不同点在于：

　　**（一）DataSet在运行时是表现为运行计划(runtime plans)的**

　　在Spark中，RDD在运行时是表现为java objects的。通过引入Tungsten，这块有了些许的改变。但是在Flink中是被表现为logical plan(逻辑计划)的, 就是类似于Spark中的dataframes。所以在Flink中你使用的类Dataframe api是被作为第一优先级来优化的。但是相对来说在Spark RDD中就没有了这块的优化了。
　　Flink中的Dataset，对标Spark中的Dataframe，在运行前会经过优化。在Spark 1.6，dataset API已经被引入Spark了，也许最终会取代RDD 抽象。

　　**(二）Dataset和DataStream是独立的API**

　　在Spark中，所有不同的API，例如DStream，Dataframe都是基于RDD抽象的。但是在Flink中，Dataset和DataStream是同一个公用的引擎之上两个独立的抽象。所以你不能把这两者的行为合并在一起操作，当然，Flink社区目前在朝这个方向努力(`https://issues.apache.org/jira/browse/Flink-2320`)，但是目前还不能轻易断言最后的结果。

### 2、内存管理

　　一直到1.5版本，Spark都是试用java的内存管理来做数据缓存，明显很容易导致OOM或者gc。所以从1.5开始，Spark开始转向精确的控制内存的使用，这就是tungsten项目了。

　　而Flink从第一天开始就坚持自己控制内存试用。这个也是启发了Spark走这条路的原因之一。Flink除了把数据存在自己管理的内存以外，还直接操作二进制数据。在Spark中，从1.5开始，所有的dataframe操作都是直接作用在tungsten的二进制数据上。

### 3、语言实现

- 实现语言

  Spark和Flink均有Scala/Java混合编程实现，Spark的核心逻辑由Scala完成，Flink的主要核心逻辑由Java完成

- 支持应用语言
   Flink主要支持Scala，和Java编程，部分API支持python应用
   Spark主要支持Scala，Java，Python,R语言编程，部分API暂不支持Python和R

### 4、API

| API对比        | Flink                |            | Spark       |                    |                |
| -------------- | -------------------- | ---------- | ----------- | ------------------ | -------------- |
| 应用类型       | Batch                | Streaming  | Batch       | Structed Streaming | SparkStreaming |
| 数据表示       | Dataset              | datastream | RDD,Dataset | Dataset            | Dtream         |
| 主要支持API    | map,filter,flatMap等 |            |             |                    |                |
| 转换后数据类型 | Dataset              | datastream | RDD,Dataset | Dataset            | Dtream         |

#### 批处理：

Spark批处理的数据表示经历了从`RDD -> DataFrame -> Dataset`的变化，均具有不可变，lazy执行，可分区等特性，是Spark框架的核心，rdd经过map等函数操作后，并没有改变而是生成新的RDD，Spark的Dataset（DataFrame是一种特殊的Dataset，已经不推荐使用）还包含数据类型信息

Flink批处理的API是Dataset,同样具有不可变，lazy执行，可分区等特性，是Flink框架的核心，Dataset经过map等函数操作后，并没有改变而是生成新的Dataset

#### 流处理

- Spark Streaming

  Spark在1.*版本引入的spark streaming作为流处理模块，抽象出Dstream的API来进行流数据处理，同时抽象出通过receiver获取消息数据，然后启动task处理的模式，以及直接启动task消费处理两种方式的流式数据处理。receiver模式由于稳定性不足被遗弃，推荐使用的是直接消费模式；然而本质上讲，Sparkstreaming的流处理是micro-batch的处理模式，将一定时间的流数据作为一个block/RDD，然后使用批处理的rdd的api来完成数据的处理。

- Structed streaming

  随着Spark在2.*版本的Structed streaming的推出，Spark streaming模块进入了维护模式，从Spark2.*版本以来没有已经没有更新，当前社区主推使用Structed streaming进行流处理。Structed streaming在流处理中有两种流处理模式，一种是microbatch模式；一种是continuous模式；
  
  - microbatch模式与spark streaming的microbatch模式大致相当，分批处理消息，但可通过设置连续的批次处理，即一个批次执行完之后立即进入下一个批次的处理
  
  - continuous模式，可以实现真正的流数据处理，端到端的毫秒级，当前处于Experiment状态，也只能支持简单的map,filter操作，当前不支持聚合，`current_timestamp`，`current_date`等操作
  - PS : microbatch <----> continuous 两种模式可以相互切换且无需改动代码


- Flink Streaming

  Flink Streaming以流的方式处理流数据，可以实现简单map,fliter等操作，也可以实现复杂的聚合，关联操作，以完善的处理模型及high throughout得到了广泛的应用。

　　Spark和Flink都在模仿scala的collection API.所以从表面看起来，两者都很类似。下面是分别用RDD和DataSet API实现的word count

```scala
// Spark wordcount
object WordCount {
 
  def main(args: Array[String]) {
    val env = new SparkContext("local","wordCount")
    val data = List("hi","how are you","hi")
    val dataSet = env.parallelize(data)
    val words = dataSet.flatMap(value => value.split("\\s+"))
    val mappedWords = words.map(value => (value,1))
    val sum = mappedWords.reduceByKey(_+_)
    println(sum.collect())
  }
}
 
// Flink wordcount
object WordCount {
 
def main(args: Array[String]) {
　　val env = ExecutionEnvironment.getExecutionEnvironment
　　val data = List("hi","how are you","hi")
　　val dataSet = env.fromCollection(data)
　　val words = dataSet.flatMap(value => value.split("\\s+"))
　　val mappedWords = words.map(value => (value,1))
　　val grouped = mappedWords.groupBy(0)
　　val sum = grouped.sum(1)
　　println(sum.collect())
　}
}
```

　　不知道是偶然还是故意的，API都长得很像，这样很方便开发者从一个引擎切换到另外一个引擎。我感觉以后这种Collection API会成为写data pipeline的标配。

### 5、Steaming

　　Spark把streaming看成是更快的批处理，而Flink把批处理看成streaming的special case。这里面的思路决定了各自的方向，其中两者的差异点有如下这些：

**实时 vs 近实时的角度**

　　Flink提供了基于每个事件的流式处理机制，所以可以被认为是一个真正的流式计算。它非常像storm的model。而Spark，不是基于事件的粒度，而是用小批量来模拟流式，也就是多个事件的集合。所以Spark被认为是近实时的处理系统。

　　Spark streaming 是更快的批处理，而Flink Batch是有限数据的流式计算。虽然大部分应用对准实时是可以接受的，但是也还是有很多应用需要event level的流式计算。这些应用更愿意选择storm而非Spark streaming，现在，Flink也许是一个更好的选择。

**流式计算和批处理计算的表示**

　　Spark对于批处理和流式计算，都是用的相同的抽象：RDD，这样很方便这两种计算合并起来表示。而Flink这两者分为了DataSet和DataStream，相比Spark，这个设计算是一个糟糕的设计。

**对 windowing 的支持**

　　因为Spark的小批量机制，Spark对于windowing的支持非常有限。只能基于process time，且只能对batches来做window。而Flink对window的支持非常到位，且Flink对windowing API的支持是相当给力的，允许基于process time,data time,record 来做windowing。我不太确定Spark是否能引入这些API，不过到目前为止，Flink的windowing支持是要比Spark好的。Steaming这部分Flink胜

| Window 类型    | Window 含义                                      | Flink Streaming | SparkStreaming | Structed Streaming | 备注                                                   |
| -------------- | ------------------------------------------------ | --------------- | -------------- | ------------------ | ------------------------------------------------------ |
| tumblingWindow | 一个滚动的window                                 | 支持            | 支持           | 支持               |                                                        |
| Sliding window | 滑动的window                                     | 支持            | 支持           | 支持               |                                                        |
| Global window  | 全局window                                       | 支持            | 间接实现       | 间接支持           | 间接支持的含义是可以时间类似功能，但没有抽象出该window |
| Session window | 以接收到数据开始，一定时间没有接收到数据，则结束 | 支持            | 不支持         | 不支持             |                                                        |



**流join分析：**

由于Spark streaming中不支持event time的概念，其只能支持window不同Dstream的RDD的join，不同window间无法join

| 模块                    | event-time | 流join | join实现方式                 | 处理方式                                              | 备注                                          |
| ----------------------- | ---------- | ------ | ---------------------------- | ----------------------------------------------------- | --------------------------------------------- |
| Spark streaming         | 不支持     | 支持   | window内                     | processingTime                                        | micro-batch处理                               |
| FLink1.5之前            | 支持       | 支持   | window内                     | native处理，join时(window触发)，watermark灵活         | Processing Time／ EventTime ／ element Number |
| FLink1.6之后            | 支持       | 支持   | window内，跨window           | native处理，join时(window触发)，watermark灵活         | Processing Time／ EventTime ／ element Number |
| Structed Streaming 2.2  | 支持       | 不支持 | 仅支持流数据和静态数据的join | native处理，join时(window触发)，watermark灵活         | Processing Time／ EventTime                   |
| Structed Streaming 2.3+ | 支持       | 支持   | 跨window                     | native处理，join时（proocessingTime（interval）触发） | Processing Time／ EventTime                   |

PS:

- Flink／structed streaming开发难度相当，FLink略复杂，但灵活度更高
- Flink的inteval join
- Structed Streaming支持数据去重（同个imsi的数据的多个不同join结果的去重）
- FLink的窗口操作相当于structed streaming的update模式
- Flink的单流的watermark更新时实时的，有专门线程处理
- Structed streaming的watermark更新时间基于批的，每个批次共用同一个watermark，如果有多个流，多个流共用一个watermark
- structed Streaming的watermark更新方法：
   基于每个流找出该流的watermark：Max_event_time - lateness
   找出所有流中最小/最大的watermark设置为batch的watermark
- Flink专门抽象了类以便不同场景下使用自定义的eventTime的waterMark获取/设置方法,且提供了一般场景下的的类以便使用
- Flink抽象了trigger和evictor来实现触发计算和清理数据的逻辑，以便自定义相关逻辑
- FLink 支持sideoutput输出，如迟到的数据可以单独输出

### 6、SQL interface

　　目前Spark-sql是Spark里面最活跃的组件之一，Spark提供了类似Hive的sql和Dataframe这种DSL来查询结构化数据，API很成熟，在流式计算中使用很广，预计在流式计算中也会发展得很快。至于Flink，到目前为止，Flink Table API只支持类似DataFrame这种DSL，并且还是处于beta状态，社区有计划增加SQL 的interface，但是目前还不确定什么时候才能在框架中用上。所以这个部分，Spark胜出。目前Flink已经支持SQL API

### 7、外部数据源的整合

　　Spark的数据源 API是整个框架中最好的，支持的数据源包括NoSql db,parquet,ORC等，并且支持一些高级的操作，例如predicate push down。Flink目前还依赖map/reduce InputFormat来做数据源聚合。这一场Spark胜，目前已经提供 

```scala
env.readTextFile(path_i)
env.writeTextFile(path_i)
```



### 8、Iterative processing
![Flink 迭代处理](_v_images/20190723102241286_1833770582.png =519x)
![Spark迭代处理](_v_images/20190723102318593_811318029.png =519x)
　　Spark对机器学习的支持较好，因为利用内存cache来加速机器学习算法。然而大部分机器学习算法其实是一个有环的数据流，但是在Spark中，实际是用无环图来表示的，一般的分布式处理引擎都是不鼓励试用有环图的。但是Flink这里又有点不一样，Flink支持在runtime中的有环数据流，这样表示机器学习算法更有效而且更有效率。这一点Flink胜出。



### 9、Stream as platform vs Batch as Platform

- Spark诞生在Map/Reduce的时代，数据都是以文件的形式保存在磁盘中，这样非常方便做容错处理。

- Flink把纯流式数据计算引入大数据时代，无疑给业界带来了一股清新的空气。这个idea非常类似akka-streams这种。



【参考文献】

1. [Apache Flink vs Apache Spark](https://www.iteblog.com/archives/1624.html)
2. [Flink vs Spark](https://www.jianshu.com/p/da1910535f73)