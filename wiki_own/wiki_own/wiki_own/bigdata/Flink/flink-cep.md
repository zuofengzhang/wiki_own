---
title: "flink cep"
date: 2019-06-06 00:00:00
layout: post
category: Distributed
tags:
 - Distributed
 - Hadoop
 - mapReducer

share: true
comments: true
---

Flink cep

CEP的处理范例引起了人们的极大兴趣，并在各种用例中得到了应用。 最值得注意的是，CEP现在用于诸如股票市场趋势和信用卡欺诈检测等金融应用


模式，从流中查找符合某个pattern的个体事件。
可以将一个pattern sequence视为pattern组成的图, 基于用户定义的条件，从一个pattern传递到下一个pattern
一个match是事件必须流过复杂pattern图的所有的pattern。

**注意**
- 每一个pattern必须具有唯一的名称，用于标示符合条件的事件
- pattern名称不能包含:

