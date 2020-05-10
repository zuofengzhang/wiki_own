---
title: "Spark开发集锦"
layout: post
date: 2019-08-10 14:58:00
category: bigdata
tags:
 - Java
 - Image
 - Spark

share: true
comments: true
---

# Spark

[Spark 2.2.x中文文档](https://spark-reference-doc-cn.readthedocs.io/zh_CN/latest/programming-guide/rdd-guide.html)
spark两个重要概念

- RDD
- 共享变量

## 共享变量
### 广播变量
### 累加器

##  dev

### 依赖
spark core依赖
```xml
<dependency>
    <groupId>org.apache.spark</groupId>
    <artifactId>spark-core_2.11</artifactId>
    <version>2.3.1</version>
</dependency>
```
hdfs client
```xml
<dependency>
    <groupId>org.apache.hadoop</groupId>
    <artifactId>hadoop-hdfs-client</artifactId>
    <version>3.2.0</version>
    <scope>provided</scope>
</dependency>
```
导包
```scala
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
```
### 初始化

每个JVM进程中，只能有一个活跃（active）的 SparkContext 对象。如果你非要再新建一个，那首先必须将之前那个活跃的 SparkContext 对象stop()掉。

> 如何保证SparkContext是单例

```scala
val conf = new SparkConf().setAppName(appName).setMaster(master)
val sc = new SparkContext(conf)
```

```shell
spark-shell –help 可以查看完整的选项列表
```
## RDD: 弹性分布式数据集
可容错、可并行操作的分布式元素集合

有两种方法可以创建 RDD 对象：由驱动程序中的集合对象通过并行化操作创建，或者从外部存储系统中数据集加载（如：共享文件系统、HDFS、HBase或者其他Hadoop支持的数据源）。

###  并行集合
