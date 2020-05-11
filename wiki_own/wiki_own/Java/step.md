---
title: Java软件工程师知识结构
layout: post
date: 2014-05-30T12:26:00.000Z
category: Java
tags:
  - Java
  - Job
share: true
comments: true
---

# 1 Java基础

## 1.1 Collection和Map

1. 掌握Collection和Map的继承体系。[Java Collection与map](/Java/collection/collection-map/ "Java Collection与Map")
2. 掌握ArrayList、LinkedList、Vector、Stack、PriorityQueue、HashSet、LinkedHashSet、TreeSet、HashMap、LinkedHashMap、TreeMap、WeakHashMap、EnumMap、TreeMap(红黑树)、HashTable的特点和实现原理。
3. 掌握CopyOnWriteArrayList、CopyOnWriteArraySet、[ConcurrentHashMap](/Java/collection/ConcurrentHashMap/)的实现原理和适用场景。
4. Java并发容器

## 1.2 IO

1. 掌握InputStream、OutputStream、Reader、Writer的继承体系。 [Java IO](/Java/io/BIO/ "Java IO") , [Java 压缩](/Java/io/java-zip/) , [Java XML与JSON](/Java/io/java-XML-JSON/), [Java序列化](/Java/io/object-serialization/)
2. 掌握字节流\(FileInputStream、DataInputStream、BufferedInputStream、FileOutputSteam、DataOutputStream、BufferedOutputStream\)和 字符流\(BufferedReader、InputStreamReader、FileReader、BufferedWriter、OutputStreamWriter、PrintWriter、FileWriter\)，并熟练运用。
3. 掌握NIO实现原理及使用方法。 [IO模式](/Java/io/IO-Model/) [Java NIO](/Java/io/NIO/)
4. Netty原理 [这可能是目前最透彻的Netty原理架构解析](/JavaWeb/Netty/Netty-theory/)
5. IO理论 [高性能网络编程](/Java/io/high-performance-network-programming/)

## 1.3 异常

1. 掌握Throwable继承体系。[深入理解java异常处理机制](http://blog.csdn.net/hguisu/article/details/6155636 "深入理解java异常处理机制")
2. 掌握异常工作原理。
3. 了解常见受检异常\(比如FileNotFoundException\)、非受检异常\(比如NullPointerException\)和错误\(比如IOError\)。

## 1.4 多线程

1. [线程池的实现原理、参数配置、平滑关机](/Java/multithread/05.ThreadPool/) 了解Executors可以创建的三种 \(JAVA8增加了一种，共四种\)线程池的特点及适用范围。 [Java多线程5: 线程池](/Java/multithread/05.ThreadPool/)

2. 掌握多线程同步机制，并熟练运用。

- CPU、操作系统锁机制与Java编译优化(指令重排与内存栅栏)
- [线程生命周期](/Java/multithread/01.thread-lifecycle/)
- [volatile](/Java/multithread/03.volatile/)
- [monitor与Synchronized: 实现原理、偏向锁-轻型锁-重型锁](/Java/multithread/01.1.monitor-synchronized/)
- [AQS](http://www.cnblogs.com/waterystone/p/4920797.html)
- CAS
- RetreentLock、ReadAndWriteLook
- CountDownLatch、Atomic、Semaphore
- Fork/Join
- [阻塞队列](/Java/multithread/06.BlockingQueue/)
- Disruptor队列
- 生产者消费者模式
- [Java多线程2: Lock、信号量、原子量与队列](/Java/multithread/02.Lock-Semaphore-Atomic/)
- [Java多线程4: 同步锁与Java线程同步方法比较](/Java/multithread/04.thread-synchronization/)
- [Java多线程6: Java阻塞队列与生产者消费者模式](/Java/multithread/06.BlockingQueue/)
- [Java多线程7: 分段锁](1)
- [CompletableFuture](https://www.jianshu.com/p/b3c4dd85901e) 、[深入解读CompletableFuture源码与原理](https://blog.csdn.net/CoderBruis/article/details/103181520)
- Quartz定时任务内部实现
- Guava并发包

3. 并发包

## 1.5 Socket

1. 掌握Socket通信原理。[Java Socket](/Java/Socket/)
2. 熟练使用多线程结合Socket进行编程。

## 1.6 Stream API

1. Lambda 实现原理
2. 懒加载实现原理

# 2 Java虚拟机

## 2.1 JVM内存区域划分

[深入理解JVM\(Java虚拟机\)](/Java/JVM/)
[Java性能专题](/Java/metric/01_tuner)

1. 掌握JMM分区: 程序计数器、堆、虚拟机栈、本地方法栈、方法区（JAVA8已移除）、元空间（JAVA8新增）的作用及基本原理。
2. 掌握堆的划分：新生代（Eden、Survivor1、Survivor2）和老年代的作用及工作原理。
3. 垃圾回收：常见算法与策略、[CMS GC](/Java/metric/02_CMS_GC/)、G1
4. [重点] 掌握JVM内存参数设置及调优
    - Eden与Survivor分配
    - [JStack](/Java/metric/jstack/)、Jstat、vmstat、jmap、jutil
    - 线上排查

## 2.2 类加载

1. 掌握类的加载阶段：加载、链接（验证、准备、解析）、初始化、使用、卸载。
2. 掌握类加载器分类及其应用：启动类加载器、扩展类加载器、应用程序类加载器、自定义加载器。
3. 双亲委派
4. 动态加载
5. CodeGen

# 3 J2EE

1. 掌握JSP内置对象、动作及相关特点和工作原理。[JSP](/JavaWeb/JSP/)
2. 掌握Servlet的特点和工作原理。 [Servlet](/JavaWeb/Servlet/)
3. Spring: IOC和AOP实现原理（控制反转和动态代理）。 [Spring](/JavaWeb/Spring/base/)
4. MVC框架（Spring MVC，Struts等）的工作原理，并熟练运用。 [Struts2](/JavaWeb/Struts2-basic) [Spring MVC](1)工作原理、事务
5. Spring MVC: 线程安全、请求原理、事务与传递、异步
6. ORM框架\(Hibernate，MyBatis等\)的工作原理，并熟练运用。 [Hibernate基本概念](/JavaWeb/Hibernate/concept) [Hibernate关联关系XML实现](/JavaWeb/Hibernate/Association-Relationship/) [Hibernate注解](/JavaWeb/Hibernate/Annotation/) [MyBatis原理与缓存](1)

# 4 数据结构与算法

1. 掌握[线性表](http://www.docin.com/p-1532910757.html)和[树](http://www.docin.com/p-682548027.html)的特点并熟练运用: B-Tree节点结构
2. 掌握[常用排序和查找算法](/data-struct/SortAndSearchAlgrithom/)：插入排序\(直接插入排序、希尔排序\)、选择排序\(直接选择排序、堆排序\)、交换排序\(冒泡排序、快速排序\)、归并排序，顺序查找、二分查找、二叉查找树、哈希查找。广度优先搜索(队列实现)
3. 熟练运用常见排序和查找算法思想解决编程问题: Top K问题、大数组查找TopN、数组去重、跳台阶问题、不定长字符串转定长字符串
4. 了解[几大基本算法](/data-struct/BasicAlgrithom/)：贪心算法、分治策略、动态规划、蓄水池抽样


# 5 计算机网络

1. 掌握网络的分层结构，及每层的功能特点。[计算机网络基础知识](/network/base/)
2. Http报文、状态码
3. 掌握TCP/IP的通信原理\(三次握手、四次挥手\)
4. [HTTPS](/network/security/HTTPS-SSL/ "HTTPS与SSL"),[加密、验签与证书](/network/security/encrypt-decrypt-signature-certificate/)

# 6 数据库

1. 掌握复杂的SQL语句编写。[MySQL](/database/MySQL/base/)   [数据库设计](/database/concept/)
2. 掌握数据库的优化（SQL层面和表设计层面）。[MySQL 性能优化的最佳20多条经验](http://www.jb51.net/article/24392.htm)
3. [MySQL](/database/MySQL/base)
    - InnoDB数据结构、索引
    - InnoDB与MyASIM区别
    - 行级锁与表级锁
4. MySQL集群
    - 集群
    - 读写分离与实现
    - 双写
5. 熟悉高并发、大数据情况下的数据库开发。

# 7 Web技术

1. 掌握[AJAX](/web/AJAX/)的工作原理。
2. 至少熟悉一款JS框架\(比如JQuery\)。[JQuery](/web/JQuery/)

# 8 设计模式

1. 熟悉常见的[设计模式](/design-pattern/base/)。
2. 会将设计模式理论应用到实际开发中。

# 9 Linux

1. 熟练运用Linux常见命令。
2. 熟悉Linux操作系统基本概念及特点。
3. 熟悉[Shell脚本](http://www.codeceo.com/article/shell-learn-30-mins.html)。

# 10 操作系统

1. 掌握操作系统的进程管理。
2. 了解操作系统的I/O。
3. Linux系统调优: 命令与方法

# 11 正则表达式

1. 掌握常见正则表达式符号。
2. 熟练运用正则表达式解决实际问题\(比如匹配电话号码、邮箱、域名等\)。[常用的正则表达式与Java中的运用](/Java/regular-expression/)

# 12 安全

1. [XSS](/web/XSS)
2. [CROS](/web/JSONP/)
3. RTFS
4. 加密解密与验签
5. HTTPS
6. 跨域请求的方法

# 13 分布式

1. 分布式事务
   - 分布式锁：基于DB事务、基于Redis、基于ZooKeeper
   - ACID
   - CAP、BASE
   - TCC
2. 微服务
3. Nginx: 原理、负载均衡算法、动态切换
4. 消息中间件 Kafka、ActiveMQ
5. 分布式缓存及其集群: 雪崩、穿透、Master选举切换
6. [Redis](/database/Redis/base/)：集群、并发竞争问题、事务与CAS操作、持久化、订阅、缓存失败策略
7. [Memcached](/database/memcached/base/): 数据结构
8. ZooKeeper: 数据结构、集群、Master选举切换
9. [RPC](/distributed/RPC/)：[WebService: SOAP、UUDI、WSDL](/)等
10. 服务治理SOA框架的使用与原理
     - [Dubbo](1): 架构、雪崩、服务异常与逻辑异常追踪、服务降级、服务发现
11. 分库分表
    - 常见分库分表方案：水平分表与垂直分表的业务设计
    - 扩容与迁移
    - MyCat
    - Sharding-JDBC
12. 唯一主键生成器
13. 算法: 一致性hash、轮询、Paxos、fast leader、raft
14. 系统设计
    - 高可用
    - 可扩展
    - 限流(滑动窗口、漏桶、令牌桶)
    - 幂等
15. 常见场景
    - 电商
16. Docker

# 14 大数据

1. 实时计算: 基数算法
2. Storm 原理
3. Flink 原理
4. Hadoop
   - NameNode动态切换
   - MapReduce与shuffle、数据倾斜问题
5. Yarn
6. HBase
7. Kylin
8. Hive
9. Lucene、solar、ElasticSearch
10. 爬虫
11. 推荐算法：协同过滤
12. 画像系统原理

# 15. 机器学习

# 16 其他

1. Quartz 原理：调度与分布式
2. 缓存: Ehcache、Spring Data cache、Guava-cache
    - LRUCache
    - FastLRUCache
3. 连接池：Druid
4. [虐菜](/interview/)
5. [软件工程基本知识](/software-engineering/base/)
6. CORBAR
7. WebSocket
8. Tomcat实现原理

# 17. 2018 中期计划

1. [apache Tomcat 源码分析](/distributed/tomcat/principle/)
2. [apache Storm 源码分析](/distributed/storm/principle/)
3. [disruptor源码分析](/Java/multithread/disruptor_principle/)
4. 分布式服务架构：原理、设计与实战
5. HBase



