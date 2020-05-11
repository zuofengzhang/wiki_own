---
title: 【转载】Roaring Bitmaps
layout: post
date: 2019-01-26 00:00:00
category: DataStruct
tags:
 - DataStruct
 
share: true
comments: true
---

# 0x00 前言

位图索引被广泛用于数据库和搜索引擎中，通过利用位级并行，它们可以显著加快查询速度。但是，位图索引会占用大量的内存，因此我们会更喜欢压缩位图索引。 Roaring Bitmaps 就是一种十分优秀的压缩位图索引，后文统称 RBM。

压缩位图索引有很多种，比如基于 RLE（Run-Length Encoding，运行长度编码）的WAH (Word Aligned Hybrid Compression Scheme) 和 Concise (Compressed ‘n’ Composable Integer Set)。相比较前者， RBM 能提供更优秀的压缩性能和更快的查询效率。

# 0x01 用途

RBM 的用途和 Bitmap 很差不多（比如说索引），只是说从性能、空间利用率各方面更优秀了。目前 RBM 已经在很多成熟的开源大数据平台中使用，简单列几个作为参考：

- Apache Lucene and derivative systems such as Solr and Elasticsearch,
- Metamarkets’ Druid,
- Apache Spark,
- Apache Hive,
- eBay’s Apache Kylin,
- ……

总之 RBM 很优秀，大家都在用，学一学可能自己写代码用不到，但是对于理解这些常用的开源大数据系统没有坏处。

# 0x02 原理

## 一、英文版

原理的话先直接上一段论文的原文，两三段基本把整个 RBM 的设计思想给讲清楚了。不想看英文了可以直接跳过看后面的中文总结。

>  We partition the range of 32-bit indexes ([0; n)) into chunks of 216 integers sharing the same 16 most significant digits. We use specialized containers to store their 16 least significant bits.  When a chunk contains no more than 4096 integers, we use a sorted array of packed 16-bit integers. When there are more than 4096 integers, we use a 216-bit bitmap. Thus, we have two types of containers: an array container for sparse chunks and a bitmap container for dense chunks. The 4096 threshold insures that at the level of the containers, each integer uses no more than 16 bits: we either use 216 bits for more than 4096 integers, using less than 16 bits/integer, or else we use exactly 16 bits/integer.  The containers are stored in a dynamic array with the shared 16 most-significant bits: this serves as a first-level index. The array keeps the containers sorted by the 16 most-significant bits.We expect this first-level index to be typically small: when n = 1 000 000, it contains at most 16 entries. Thus it should often remain in the CPU cache. The containers themselves should never use much more than 8 kB.  

## 二、主要思想

RBM 的主要思想并不复杂，简单来讲，有如下三条：

1. 我们将 32-bit 的范围 ([0, n)) 划分为 2^16 个桶，每一个桶有一个 Container 来存放一个数值的低16位；
2. 在存储和查询数值的时候，我们将一个数值 k 划分为高 16 位`(k % 2^16)`和低 16 位`(k mod 2^16)`，取高 16 位找到对应的桶，然后在低 16 位存放在相应的 Container 中；
3. 容器的话， RBM 使用两种容器结构： Array Container 和 Bitmap Container。Array Container 存放稀疏的数据，Bitmap Container 存放稠密的数据。即，若一个 Container 里面的 Integer 数量小于 4096，就用 Short 类型的有序数组来存储值。若大于 4096，就用 Bitmap 来存储值。

如下图，就是官网给出的一个例子，三个容器分别代表了三个数据集：

1. the list of the first 1000 multiples of 62
2. all integers [216, 216 + 100)
3. all even numbers in [2216, 3216)

![img](/images/DataStruct/roaringBitmaps/01.png)

# 0x03 举个栗子

看完前面的还不知道在说什么？没关系，举个栗子说明就好了。现在我们要将 821697800 这个 32 bit 的整数插入 RBM 中，整个算法流程是这样的：

1. 821697800 对应的 16 进制数为 30FA1D08， 其中高 16 位为 30FA， 低16位为 1D08。
2. 我们先用二分查找从一级索引（即 Container Array）中找到数值为 30FA 的容器（如果该容器不存在，则新建一个），从图中我们可以看到，该容器是一个 Bitmap 容器。
3. 找到了相应的容器后，看一下低 16 位的数值 1D08，它相当于是 7432，因此在 Bitmap 中找到相应的位置，将其置为 1 即可。

![img](/images/DataStruct/roaringBitmaps/02.png)

是不是很简单？然后换一个数值插入，比如说 191037，它的 16 进制的数值是 0002EA3D ，插入流程和前面的例子一样，不同的就在于， 高 16 位对应的容器是一个 Array Container，我们仍然用二分查找找到相应的位置再插入即可。

# 0x04 原理补充

RBM 的基本原理就这些，基于这种设计原理会有一些额外的操作要提一下。

请注意上文提到的一句话：

>  若一个 Container 里面的 Integer 数量小于 4096，就用 Short 类型的有序数组来存储值。若大于 4096，就用 Bitmap 来存储值。  

先解释一下为什么这里用的 4096 这个阈值？因为一个 Integer 的低 16 位是 2Byte，因此对应到 Arrary Container 中的话就是 2Byte * 4096 = 8KB；同样，对于 Bitmap Container 来讲，2^16 个 bit 也相当于是 8KB。

然后，基于前面提到的两种 Container，在两个 Container 之间的 Union (bitwise OR)  或者 Intersection (bitwise AND) 操作又会出现下面三种场景：

- Bitmap vs Bitmap
- Bitmap vs Array
- Array vs Array

RBM 提供了相应的算法来高效地实现这些操作，比如下图是 Bitmap vs Bitmap，这里暂不再深入讨论，感兴趣的可以看一下论文原文。

![img](/images/DataStruct/roaringBitmaps/03.png)

# 0xFF 总结

好了，RBM 的大致原理就这些，不深入也没有简单代码的实现。仅能做一个入门的参考。

本文是参考论文《Better bitmap performance with Roaring bitmaps》，该论文中提到的是 Bitmap 和 Array 两种容器，算是包含了 RBM 的主要思想。然后，在另一篇论文《Consistently faster and smaller compressed bitmaps with Roaring》中会对 RBM 有更深入的探讨，并引入了一种新的容器： Run，感兴趣的童鞋可以深入看一看。