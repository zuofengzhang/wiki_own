---
title: "大规模分布式系统故障恢复和容错架构探究"
date: 2019-05-26 00:00:00
layout: post
category: Distributed
tags:
 - Distributed
 - Hadoop
 - mapReducer

share: true
comments: true
---

在大规模数据处理的分布式系统中，如何保障数据的高可用、数据的一致性和幂等性(exactly once)是系统的一大难题!
使用廉价机器构建集群成为大数据平台的标配，故障恢复和容错（failover recovery）机制成为防止消息丢失和快速恢复服务必不可少的组成部分; 在通用的大数据架构中，也是保障数据高可用、一致性和幂等性的基础。

分布式系统故障恢复需要解决的问题:

1. 吞吐量大的场景，当出现了失败时，需要保证失败的数据可以重放，且状态可恢复。
2. 某一个时刻，多个计算节点存在处理速度不一致的问题，一条数据可能经过多个计算节点才能完成计算。如何保存多个计算节点的状态，且保证数据对齐？
3. 如何保证数据经过各个计算节点的顺序性和不重复？
4. 如何回放数据？

虽然大规模分布式系统的侧重各不相同，但是failover recovery的机制却如出一辙, 主要有ack模式、异步checkpoint模式、CL模式、补偿模式等，接下来就这四种模式分别总结。

# Ack模式
在分布式系统中，为了确保一条(批)数据被正确处理，且当出现任何故障，保障数据不丢。ack机制是最简单的方式，每一条(批)数据正确处理完后，发送一条确认标示。

## TCP ack

在TCP握手时，当收到客户端发起的握手报文时, 返回一个Acknowledgement Number, 标示客户端的请求已经收到，并返回给客户端。
而后客户端返回Acknowledgement Number以确保服务端请求正确接受。
当网络抖动或者服务器和客户端故障时，报文可能丢失。此时就要依赖于与ack机制相配合的faiover-recovery机制。

如果服务器没有收到客户端的最终ACK确认报文，会一直处于`SYN_RECV`状态，将客户端IP加入等待列表，
并重发`SYN+ACK`报文。
重发一般进行3-5次，大约间隔30秒左右轮询一次等待列表重试所有客户端。
另一方面，服务器在自己发出了`SYN+ACK`报文后，会预分配资源为即将建立的TCP连接储存信息做准备，
这个资源在等待重试期间一直保留。更为重要的是，服务器资源有限，可以维护的`SYN_RECV`状态超过极限后就不再接受新的SYN报文，
也就是拒绝新的TCP连接建立。

然而著名的`SYNC Flood`的DDos攻击正式利用上述的`failover-recovery`机制。
攻击者伪装大量的IP地址给服务器发送SYN报文，由于伪造的IP地址几乎不可能存在，也就几乎没有设备会给服务器返回任何应答了。
因此，服务器将会维持一个庞大的等待列表，不停地重试发送SYN+ACK报文，同时占用着大量的资源无法释放。
更为关键的是，被攻击服务器的`SYN_RECV`队列被恶意的数据包占满，不再接受新的SYN请求，合法用户无法完成三次握手建立起TCP连接。
也就是说，这个服务器被SYN Flood拒绝服务了。

SYN Flood攻击大量消耗服务器的CPU、内存资源，并占满SYN等待队列。相应的，我们修改内核参数即可有效缓解。主要参数如下：

```properties
net.ipv4.tcp_syncookies = 1
net.ipv4.tcp_max_syn_backlog = 8192
net.ipv4.tcp_synack_retries = 2
```
分别为启用SYN Cookie、设置SYN最大队列长度以及设置SYN+ACK最大重试次数。
SYNC Cookie主要是在服务端缓冲基于时间种子的SYN号，只有客户端发送的SYN+ACK与缓冲完全匹配才完成握手，否则直接丢弃。
`tcp_max_syn_backlog`则是增加等待队列的长度。

## Apache storm中的ack机制

Apache storm是首个真正意义上的流式处理引擎，在spark/Flink出现之前，是实时计算领域的一枝独秀。

storm中是没有checkpoint机制的，但storm以大名鼎鼎的ack算法来保证at least once语义，(在[Trident](http://storm.apache.org/releases/2.0.0-SNAPSHOT/Trident-tutorial.html)出现之前，storm是没有办法保证exactly once语义的)。ack需要spout节点保存每条数据，当所有的计算节点处理完毕，再发送给spout节点，因此与Chandy-Lamport算法相比，每条数据都需要保存和反复发送，而状态和数据回滚需要用户来保证。

实时大数据处理，数据源源不断的流入系统。无法在一个线程中串行的处理并确认一条或一批数据。
strom采用的是异步并行处理的模式(这里以JStorm的实现分析),
当excutor节点（executor节点是storm的进程，spout和bolt都是executor启动的task线程）收到消息时，首先将消息压入disruptor队列，disruptor的消费者从队列中获取数据，执行转发或者计算。


![](/images/bigdata/storm/strom-queue.png)

strom引入ack机制来确保数据不丢，但是对系统整体架构也带来了很大的影响，那么问题来了：
- 消息量大，如何保存消息？
- 消息可能流过多个节点，如何保证每个节点都正确处理？
- spout节点重启，如何确保消息不丢失？
- 消息堆积，如何确保集群的稳定？

ack机制是如何巧妙解决这写问题的呢？

![](https://images2015.cnblogs.com/blog/639357/201612/639357-20161207181349866-1482908747.png)
```
A xor A = 0.
A xor B … xor B xor A = 0，
```
其中每一个操作数出现且仅出现两次。

strom的ack机制，巧妙的利用了两个相同的值异或为0的原理.

理解下整个大体节奏分为几部分:

- 步骤1和2 spout把一条信息同时发送给了bolt1和bolt2，步骤3表示spout emit成功后去 acker bolt里注册本次根消息，ack值设定为本次发送的消息对应的64位id的异或运算值，上图对应的是T1^T2。

- 步骤4表示bolt1收到T1后，单条tuple被拆成了三条消息T3T4T5发送给bolt3。步骤6 bolt1在ack()方法调用时会向acker bolt提交T1^T3^T4^T5的ack值。

- 步骤5和7的bolt都没有产生新消息，所以ack()的时候分别向acker bolt提交了T2 和T3^T4^T5的ack值。综上所述，本次spout产生的tuple树对应的ack值经过的运算为 T1^T2^T1^T3^T4^T5^T2^T3^T4^T5按照异或运算的规则，ack值最终正好归零。

- 步骤8为acker bolt发现根spout最终对应的的ack是0以后认为所有衍生出来的数据都已经处理成功，它会通知对应的spout，spout会调用相应的ack方法。

storm这个机制的实现方式保证了无论一个tuple树有多少个节点，一个根消息对应的追踪ack值所占用的空间大小是固定的，极大地节约了内存空间。

通过ack机制，spout发出的每一条消息，都可以确定是被成功或失败处理。但是，需要备份每条消息，来确认消息是否处理完成，如果消息流过的每个节点都备份数据，总数据量将翻几倍。spout作为消息流入到topology的起点，在这里备份数据既可以节省内存，又可以验证整条链路。此外，Ack机制还常用于**限流**作用： 为了避免spout发送数据太快，而bolt处理太慢，常常设置pending数，当spout有等于或超过pending数的tuple没有收到ack或fail响应时，跳过执行nextTuple， 从而限制spout发送数据。

strom逐条发送逐条处理逐条ack，这也是吞吐量不及spark和flink。

# checkpoint

通俗来讲:  就是在分布式系统中，通过状态的checkpoint来确保数据的高可用。

checkpoint俗称检查点，是指定时将数据快照保存到持久化存储介质中，来提供数据的可靠性和与增量文件结合快速恢复数据。


## Hadoop NameNode 的checkpoint

NameNode负责管理Hadoop的元数据(workspace信息、blockMap信息、network topology等)信息，是HDFS的心脏。
checkpoint机制是NameNode数据故障恢复的方案。



![HDFS namenode 1.x](/images/bigdata/hdfs/nameNode_1.x.png)

HDFS 2.x 引入了HA来解决NameNode的单点问题，社区也涌现了多种共享内存方案来保存editlog，而namenode的元数据的数据结构几乎没有变化。

![name node workspace 内存结构](/images/bigdata/hdfs/namenode-workspace-memory.png)

workspace信息常驻内存，并定时checkpoint成fsimage文件, 当HDFS-Client发起修改文件目录的请求时，直接修改内存中的数据， 并将修改记录写到editlog文件中。可以将name node的workspace的维护过程简单理解为分布式系统中消息处理的过程，

# Chandy-Lamport算法

在实时流式处理中，简单的使用checkpoint没办法保证exactly once语义，主要是由于在某一个时刻：
1. 消息还在处理(没有合并到状态中)，source接收数据的偏移量不能准确的与状态做到数据一致性。
2. 每个子任务处理进度也难以统一。

理想情况下，停止接收新数据并排干整个流处理系统，再做checkpoint，才能保证数据一致性和exactly once。停机显然是不可能的！Chandy-Lamport算法使用巧妙的方法，在不停止流处理的前提下拿到每个子任务的状态并checkpoint下来。

著名的一致性算法 Paxos 的作者Leslie Lamport与Chandy合作发表了算法论文: [Distributed snapshots: determining global states of distributed systems](https://dl.acm.org/ft_gateway.cfm?id=214456&ftid=20679&dwn=1&CFID=72171565&CFTOKEN=2bfe026b198b5dc8-AEFB79E7-EF87-025C-C01DC78635F21DF8), 在该论文中提出了分布式快照算法: **Chandy-Lamport**

> A **snapshot algorithm** is used to create a consistent snapshot of the global state of a [distributed system](https://en.wikipedia.org/wiki/Distributed_system). Due to the lack of globally shared memory and a global clock, this isn't trivially possible.

Chandy-Lamport算法用于在缺乏全局共享内存和全局时钟的分布式系统中创建一致性的全局分布式快照。而这个算法正是1978年提出的[**Time, Clocks and the Ordering of Events in a Distributed System**](http://lamport.azurewebsites.net/pubs/pubs.html?spm=a2c4e.11153940.blogcont688764.10.4f964568O0SyIm#time-clocks)的直接应用。在分布式系统中，为了确保数据在不同计算节点的有序性，引入barrier机制，当相同的barrier到达每一个计算节点时，认为全局节点处理结束。

Chandy-Lamport算法将全局的状态简化为有限个节点以及节点与节点之间的channel组成，也就是有向图。节点是进程，边是channel；分布式系统中，进程运行在不同的物理机器上，一个分布式的系统中的全局状态由进程的状态和channel中的message组成，这些都是分布式快照要保存的内容。

因为是有向图，一个节点的channel包含了input channel和output channel，流经channel的数据源源不断，假设channel是FIFO队列，保证不重复，那么只需要保存每个节点的局部状态和input message的偏移量，合并起来就是全局的分布式快照。

## Flink中的Chandy-Lamport算法

Chandy-Lamport算法在flink中用于实现at least once语义。具体工作流程如下:

1. 在checkpoint触发时刻，Job Manager会往所有Source的流中放入一个barrier（图中三角形）。barrier包含当前checkpoint的ID
![](https://upload-images.jianshu.io/upload_images/1431048-f1583d01e8fad051.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/521)

2. 当barrier经过一个subtask时，即表示当前这个subtask处于checkpoint触发的“时刻”，他就会立即将barrier法往下游，并执行checkpoint方法将当前的state存入backend storage。图中Source1和Source2就是完成了checkpoint动作。
![](https://upload-images.jianshu.io/upload_images/1431048-29b12d52fb1ccf05.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/523)

3. 如果一个subtask有多个上游节点，这个subtask就需要等待所有上游发来的barrier都接收到，才能表示这个subtask到达了checkpoint触发“时刻”。但所有节点的barrier不一定一起到达，这时候就会面临“是否要对齐barrier”的问题（Barrier Alignment）。如图中的Task1.1，他有2个上游节点，Source1和Source2。假设Source1的barrier先到，这时候Task1.1就有2个选择：

  - 是马上把这个barrier发往下游并等待Source2的barrier来了再做checkpoint
  - 还是把Source1这边后续的event全都cache起来，等Source2的barrier来了，在做checkpoint，完了再继续处理Source1和Source2的event，当前Source1这边需要先处理cache里的event。


# WAL

WAL是一种常见的故障恢复方式，如NameNode的元数据、HBase WAL、kafka消息中间件、SQLite WAL等。

> "In computer science, write-ahead logging (WAL) is a family of techniques for providing atomicity and durability (two of the ACID properties) in database systems."——维基百科

## HBase中的WAL

这里介绍一下HBase WAL(write ahead log)机制，Hbase的RegionServer在处理数据插入和删除的过程中用来记录操作内容的一种日志。在每次Put、Delete等一条记录时，首先将其数据写入到RegionServer对应的HLog文件中去。

客户端向RegionServer端提交数据的时候，会先写入WAL日志，只有当WAL日志写入成功的时候，客户端才会被告诉提交数据成功。如果写WAL失败会告知客户端提交失败，这其实就是数据落地的过程。

在一个RegionServer上的所有Region都共享一个HLog，一次数据的提交先写入WAL，写入成功后，再写入menstore之中。当menstore的值达到一定的时候，就会形成一个个StoreFile。
![](https://img-blog.csdn.net/20180419134053354)

WAL记载了每一个RegionServer对应的HLog。RegionServer1或者RegionServer1上某一个regiong挂掉了，都会迁移到其它的机器上处理，重新操作，进行恢复。

当RegionServer意外终止的时候，Master会通过Zookeeper感知到，Master首先会处理遗留下来的HLog文件，将其中不同Region的Log数据进行拆分，分别放到相应的Region目录下，然后再将实效的Region重新分配，领取到这些Regio你的RegionMaster发现有历史的HLog需要处理，因此会Replay HLog的数据到Memstore之中，然后flush数据到StoreFiles，完成数据的恢复。

飞行日志+补偿机制，也是常用的方法，如基于消息的分布式事务是保证最终一致性的方式之一、Quartz中的恢复执行等。



[参考文献]
---
1. [深入浅出DDoS攻击防御](https://elf8848.iteye.com/blog/2067774)
2. [《Storm源码分析》]()
3. [Flink Checkpoint](https://www.jianshu.com/p/9b10313fde10)
4. [什么是WAL](https://www.cnblogs.com/hzmark/p/wal.html)
5. [Write-Ahead Logging in SQLite](https://www.sqlite.org/wal.html)
