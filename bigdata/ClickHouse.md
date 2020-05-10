---
title: "ClickHouse： one of the fastest olap engine"
layout: post
date: 2016-08-10 14:58:00
category: bigdata
tags:
 - Java
 - Image
 - ClickHouse

share: true
comments: true
---


# ClickHouse
[官方文档](https://clickhouse.yandex/docs/zh/)


# Clickhouse
[offical website](https://clickhouse.yandex/) [github](https://github.com/ClickHouse/ClickHouse/)
[meetup backup](https://github.com/ClickHouse-China/ClickhouseMeetup)

## why do we need ClickHouse
- 交互式查询
- 持续追加数据

> Hypothesis
> If we have good enough column-oriented DBMS,  
we could store all our data in non-aggregated form  
(raw pageviews and sessions) and generate all the reports on the fly,  
to allow infinite customization.

愿景：
足够好的列式DBMS，可以存储所有非聚合数据(原始的浏览数据和会话)，可以在线生成所有的报告，拥有足够的个性化


### yandex数据量

*   >30 trillions of rows (as of 2019)
*   >600 servers
*   total throughput of query processing is up to two terabytes per second



## feature

*   column-oriented 列数存储
*   distributed 分布式
*   linearly scalable 线性扩展
*   fault-tolerant 容错
*   data ingestion in realtime 实时数据摄取
*   realtime (sub-second) queries 实时亚秒级查询
*   support of SQL dialect + extensions 支持SQL方言和扩展


## why fast

### High level architecture 架构

— Scale-out shared nothing; 横向伸缩无共享

— Massive Parallel Processing; MPP

### Data storage optimizations 存储优化

— Column-oriented storage; 列式存储

— Merge Tree;

— Sparse index; 稀疏index
 
— Data compression; 数据压缩

### Algorithmic optimizations 算法优化

Best algorithms in the world...  
... are happy to be used in ClickHouse.

— Volnitsky substring search

— Hyperscan and RE2

— SIMD JSON

— HDR Histograms

— Roaring Bitmaps

...


### Low-level optimizations 底层优化

Optimizations for CPU instruction sets  
using SIMD processing. 使用SIMD优化CPU指令集

— SIMD text parsing

— SIMD data filtering

— SIMD decompression

— SIMD string operations

...

### Specializations of algorithms...  
... and attention to detail:

— uniq, uniqExact, uniqCombined, uniqUpTo;

— quantile, quantileTiming, quantileExact, quantileTDigest, quantileWeighted;

— 40+ specializations of GROUP BY;

— algorithms optimize itself for data distribution:  
LZ4 decompression with Bayesian Bandits.


### Interfaces

HTTP REST

clickhouse-client

JDBC, ODBC

(new) MySQL protocol compatibility

Python, PHP, Perl, Go,  
Node.js, Ruby, C++, .NET, Scala, R, Julia, Rust