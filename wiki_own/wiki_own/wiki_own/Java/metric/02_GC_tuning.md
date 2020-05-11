---
title: "GC调优"
layout: post
date: 2020-02-05 14:58:00
category: java
tags:
 - Java
 - Metric
 - GC
 - tuning

share: true
comments: true
---


# GC调优


## TODO

内容：

1. 教程
2. 压测+调优
3. 实际样例

tip:

- collector
- gc logs
- gc viewer
- jmeter
- 压测与调优

## JVM内存结构

[jvm整体架构图文详解](https://blog.csdn.net/qq_40368860/article/details/84447085)

[Java8 语言规范](https://docs.oracle.com/javase/specs/jls/se8/html/index.html)
[Java8 JVM规范](https://docs.oracle.com/javase/specs/jvms/se8/html/index.html)
[Java8 JVM规范-内存结构](https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-2.html#jvms-2.5)

运行时数据区

![](_v_images/20200205234634496_2051245788.png =526x)


### 程序计数器
### 虚拟机栈JVM Stacks
栈帧分为哪些快？每块又保存什么内容？

### 堆

### 方法区

非堆

- JDK6 perm区
- JDK7 perm区
- JDK8 metaspace

### 常量池Run-Time Constant Pool (方法区中)


### 本地方法栈Native Method Stacks

### JVM的内存结构

![](_v_images/20200205235938340_210349887.png =500x)

- CCS：只有启用了短指针的时候，才存在
- CodeCache：只有启用了JIT和有JNI调用Native代码的时候，才存在
    - `-Xcomp`：JIT完全编译执行
    - `-Xint`完全解释执行
    - `-Xmixed`编译和解释混合




### 非堆区

![](_v_images/20200206000408818_592467847.png =516x)





### 标准参数

```
-version -showversion
-help
-cp -classpath
-server -client
```





### -X

-Xint: 解释执行模式
-Xcomp: 编译执行模式, 第一次使用就编译成本地代码, 编译结果保存在metaspace的code cache空间
-Xmixed: 混合执行模式, JVM决定是否编译成本地代码

```bash
> java -Xint -version
openjdk version "1.8.0_232"
OpenJDK Runtime Environment (build 1.8.0_232-b09)
OpenJDK 64-Bit Server VM (build 25.232-b09, interpreted mode)

> java -Xcomp -version
openjdk version "1.8.0_232"
OpenJDK Runtime Environment (build 1.8.0_232-b09)
OpenJDK 64-Bit Server VM (build 25.232-b09, compiled mode)
```

### -XX

|               参数               |                   作用                   |
| -------------------------------- | --------------------------------------- |
| -Xms                             | 最小堆内存                               |
| -Xmx                             | 最大堆内存                               |
| -XX:NewSize                     | 新生代大小                               |
| -XX:MaxNewSize                  | 最大新生代大小                            |
| -XX:NewRatio                    | new区和old区的比例                        |
| -XX:SurvivorRatio               | eden区与survivor区大小比例                |
| -XX:MetaspaceSize               | Metaspace大小                           |
| -XX:MaxMetaspaceSize            | Metaspace最大大小                        |
| -XX:+UseCompressedClassPointers | 压缩类指针                               |
| -XX:CompressedClassSpaceSize    | 压缩类空间(`CCS`)的大小,默认1G             |
| -XX:InitialCodeCacheSize        | code cache的初始大小                     |
| -XX:ReservedCodeCacheSize       | code cache的最大的大小                    |
| -XX:PretenureSizeThreshold      | 大对象直接进入老年代，大对象的大小阈值       |
| -XX:MaxTenuringThreshold        | 长期存活的对象进入老年代，晋升年龄阈值       |
| -XX:+PrintTrnuringDistuibution  | youngGC时打印年龄分布情况                 |
| -XX:TargetSurvivorRatio         | survivor垃圾回收存活的比例，超过值将直接晋升 |


### 垃圾回收算法

[gc调优官方指南](https://docs.oracle.com/javase/8/docs/technotes/guides/vm/gctuning/toc.html)

GC Root：

- 类加载器：由类加载器生成的对象，都持有指针
- Thread：线程运行会持有很多对象
- 虚拟机栈的本地变量表
- static成员
- 常量引用
- 本地方法栈的变量



#### 引用计数

缺点：
无法处理循环引用

#### 标记清除
先标记需要回收的对象，在统一回收所有对象

**缺点：**

效率不高:标记和清除两个过程效率都不高；碎片：导致提前GC

#### 复制

内存划分为大小相同的两块，每次只使用其中一块，一块用完复制存活的对象到另一块，然后再把已使用的内存空间一次清理掉

**缺点：**

使用简单，效率高，空间利用率不高

#### 标记整理

先标记需要回收的对象，让所有存活的对象都向一端移动，然后清理掉端边界外的内存

**缺点：**
无内存碎片，比较耗时


### 分带垃圾回收

young区朝生夕死，生命周期端，用复制算法：效率高
Old区生命周期长，用标记清除或标记整理

- 对象优先分配在eden区
- 大对象直接进入老年代：`-XX:PretenureSizeThreshold`
- 长期存活的对象进入老年代：
    - `-XX:MaxTenuringThreshold`: 晋升年龄代数阈值
    - `-XX:+PrintTenuringDistribution`：ygc打印存活对象的分布情况
    - `-XX:TargetSurvivorRatio`：Survivor区存活对象比例，动态调整，取存活对象的平均值与晋升年龄阈值间的最小值


### 垃圾收集器



枚举根节点，做可达性分析
根节点: 类加载器、Thread、虚拟机栈的本地变量表、static成员、常量引用、本地方法栈的变量

- 串行收集器Serial: Serial、 Serial old
- 并行收集器Parallel: Parallel Scavenge、Parallel old，吞吐量优先
- 并发收集器Concurrent: CMS、G1,停顿时间优先


#### 并行 vs 并发
并行(Parallel): 多条垃圾收集线程并行工作，但此时用户线程仍然处于等待状态。适合科学计算、后台处理等弱交互的场景

并发(Concurrent): 用户线程和垃圾收集线程同时执行（但不一定是并行的，可能会交替执行），垃圾收集线程在执行的时候不会停顿用户程序的运行。适合对响应时间有要求的场景，如web。

#### 停顿时间 vs 吞吐量

停顿时间：垃圾收集器做垃圾回收中断应用执行的时间。`-XX:MaxGCPauseMillis`

吞吐量：花在垃圾收集的时间和花在应用时间的占比。 `-XX:GCTimeRatio=<n>`, 垃圾收集时间占: `1/(1+n)`

### 串行收集器

-XX:+UseSerialGC
-XX:+UseSerialOldGC

采用串行收集器，默认old区采用串行收集器



### 并行收集器 ParallelCollector

吞吐量优先

```
-XX:+UseParallelGC
-XX:+UseParallelOldGC

Server模式下的默认收集器
```

```
-XX:ParallelGCThreads=<N> 多少个GC线程

CPU>8 N=5/8
CPU<8 N=CPU
```

### 并发收集器

响应时间优先

CMS:  -XX:+UseConcMarkSweepGC  -XX:+UseParNewGC
G1:  -XX:+UseG1GC

### 如何选择垃圾收集器

[如何选择垃圾收集器](https://docs.oracle.com/javase/8/docs/technotes/guides/vm/gctuning/collectors.html#sthref27)


- 优先调整堆的大小让服务器自己选择
- 如果内存小于100M，使用串行收集器
- 如果是单核，并且没有停顿时间的要求，串行或者jvm自己选
- 如果允许停顿时间超过1s，选择并行或者jvm自己选
- 如果响应时间最重要，并且不能超过1s，则使用并发收集器




|        young         |   Tenured    |                JVM options                |
| -------------------- | ------------ | ----------------------------------------- |
| Serial               | Serial       | -XX:+UseSerialGC                          |
| Parallel Scavenge    | Serial       | -XX:+UseParallelGC -XX:-UseParallelOldGC |
| Parallel Scavenge    | Parallel Old | -XX:+UseParallelGC -XX:+UseParallelOldGC |
| Parallel New或Serial | CMS          | -XX:+UseParNewGC -XX:+UseConcMarkSweepGC |
| G1                   | G1           | -XX:+UseG1GC                              |


![](_v_images/20200202130338468_927467357.png =500x)

垃圾回收器从线程运行情况分类有三种

串行回收: Serial回收器，单线程回收，全程stw；
并行回收: 名称以Parallel开头的回收器，多线程回收，全程stw；
并发回收: cms与G1，多线程分阶段回收，只有某阶段会stw；




## 并行收集器 Parallel Collector

暂停应用程序，开启多个垃圾收集线程开始垃圾回收

- `-XX:+UseParallelGC` 手动开启，Server默认开启
- `-XX:ParallelGCThreads=<N>`多少个GC线程
    - `CPU>8 N=5/8`
    - `CPU<8 N=CPU`


查找使用ParallelGC的进程
`jps -v  | grep -v grep | awk '{print $1}'   | xargs -L 1 -t jinfo -flag UseParallelGC`

### Parallel Collector Ergonomics自适应

- `-XX:MaxGCPauseMillis=<N>`：最大停顿时间
- `-XX:GCTimeRatio=<N>`: GC时间占比，代表吞吐量
- `-Xmx<N>`: 堆最大大小

优先满足停顿时间，再满足吞吐量的要求，最后再调整满足堆最大大小

动态调整每个分区的大小

### 动态内存调整

- `-XX:YoungGenerationSizeIncrement=<Y>` 年轻代大小调整增量，默认值20%
- `-XX:TenuredGenerationSizeIncrement=<T>` 老年代大小调整增量，默认值
- `-XX:AdaptiveSizeDecrementScaleFactor=<D>` 减少增量，默认值4%

## CMS



```shell
java -XX:+UseConcMarkSweepGC  -jar -server console.jar
```

```shell
jps -l | grep buried | awk '{print $1}' | xargs -L 1 -t /usr/local/soft/jdk1.8.0_191/bin/jinfo  -flags
/usr/local/soft/jdk1.8.0_191/bin/jinfo -flags 4893
Attaching to process ID 4893, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 25.191-b12
Non-default VM flags: 
-XX:CICompilerCount=3 
-XX:InitialHeapSize=524288000 
-XX:MaxHeapSize=8363442176 
-XX:MaxNewSize=348913664 
-XX:MaxTenuringThreshold=6 
-XX:MinHeapDeltaBytes=196608 
-XX:NewSize=174718976 
-XX:OldPLABSize=16 
-XX:OldSize=349569024 
-XX:+UseCompressedClassPointers 
-XX:+UseCompressedOops 
-XX:+UseConcMarkSweepGC 
-XX:+UseFastUnorderedTimeStamps 
-XX:+UseParNewGC
Command line:  -XX:+UseConcMarkSweepGC 
```

```shell
jps -l | grep buried | awk '{print $1}' | xargs -L 1 -t /usr/local/soft/jdk1.8.0_191/bin/jinfo  -flag CMSInitiatingOccupancyFraction
/usr/local/soft/jdk1.8.0_191/bin/jinfo -flag CMSInitiatingOccupancyFraction 4893
-XX:CMSInitiatingOccupancyFraction=-1
```
cms是一种预处理垃圾回收器，它不能等到old内存用尽时回收，需要在内存用尽前，完成回收操作，否则会导致并发回收失败；所以cms垃圾回收器开始执行回收操作，有一个触发阈值，默认是老年代或永久带达到92%

- 并发收集
- 低停顿 低延迟
- 老年代收集器


**CMS垃圾收集过程**

1. CMS inital mark: 初始标记Root  STW
2. CMS concurrent mark：并发标记
3. CMS-concurrent-preclean: 并发预清理
4. CMS remark: 重新标记 STW
5. CMS concurrent sweep：并发清除
6. CMS-concurrent-reset：并发重置

**缺点**

- 低停顿 低延迟
- CPU敏感
- 浮动垃圾：边运行应用程序，边回收
- 空间碎片

**调优参数**

|                参数                 |          备注          |
| ----------------------------------- | ---------------------- |
| -XX:ConcGCThreads                  | 并发的GC线程数          |
| -XX:+UseCMSCompactAtFullCollection | FullGC之后做压缩        |
| -XX:CMSFullGCsBeforeCompaction     | 多少次FullGC之后压缩一次 |
| -XX:CMSInitiatingOccupancyFraction | 触发FullGC  92%        |
| -XX:+UseCMSInitiatingOccupancyOnly | 是否动态调              |
| -XX:+CMSScavengeBeforeRemark       | FullGC之前先做YGC       |
| -XX:+CMSClassUnloadingEnabled      | 启用回收Perm区          |

## G1

大内存(大于6G)，优先延迟(小于0.5s)



![](_v_images/20200206210747611_907596705.png =421x)

H区：大对象，如果对象超过了region的一半大小

Region

SATB：snapshot-at-the-beginning, 通过Root tracing得到的，GC开始时候存活对象的快照。垃圾回收以此为基础回收

RSet：记录了其他Region中的对象引用本Region中对象的关系，属于points-into结构（谁引用了我的对象）

**YoungGC**

- 新独享进入Eden区
- 存活对象拷贝到s区
- 存活时间达到年龄阈值时，对象晋升到old区

**mixedGC**

没有full gc

- 不是FullGC，回收所有的Young和部分Old
- global concurrent marking

**global concurrent marking**

1. Initial marking phase：标记GC Root ，STW
2. Root region scanning phase：标记存活Region
3. Concurrent marking phase：标记存活的对象
4. Remark phase：重新标记 STW
5. Cleanup phase：部分STW

**MixedGC时机**

- InitiatingHeapOccupancyPercent: 堆占有率达到这个数值则触发global concurrent marking，默认45%
- G1HeapWastePercent：在gloabl concurrent marking结束之后，可以知道区有多少空间要被回收，在每次YGC之后和再次发生MixedGC之前，会检查垃圾占比是否达到此参数，只有达到了，下次才会发生MixedGC
- G1MixedGCLiveThresholdPercent: Old区的region被回收时候的存活对象占比
- G1MixedGCCountTarget：一次global concurrent marking之后，最多执行MixedGC的次数
![](_v_images/20200206214458093_401476294.png =473x)

![](_v_images/20200206215456237_1513279165.png =412x)

![](_v_images/20200206215646888_1034094734.png =400x)



### 调优最佳实践

![](_v_images/20200206220804596_445039167.png =408x)

![](_v_images/20200206220825238_87737268.png =427x)

![](_v_images/20200206221006981_1426481434.png =384x)



## 可视化GC日志分析工具

![](_v_images/20200206222823823_366026130.png =490x)


吞吐量与延迟时间的权衡





## Tomcat调优实例












[CMS垃圾回收器详解](https://blog.csdn.net/zqz_zqz/article/details/70568819)