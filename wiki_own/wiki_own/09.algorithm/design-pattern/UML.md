---
layout: post
title: "UML基础"
date: 2016-03-19 00:00:00
category: DesignPattern
tag:
 - regex

share: true
comments: true
---

    鉴于设计模式中常用UML表示类之间的关系, 在此重点学习和总结一下 UML类图

# UML基本概念
UML图可以分为九种:
1. 用例图(use case diagram)

  描述用户需求，从用户的角度描述系统的功能
1. 类图(use diagram)

  显示系统的静态结构，表示不同的实体是如何相关联的
1. 对象图(object diagram)

  类图的一个实例，描述系统在具体时间点上所包含的对象以及各个对象的关系
1. 序列图(顺序图,)

  描述对象之间的交互顺序，着重体现对象间消息传递的时间顺序
1. 协作图

  描述对象之间的合作关系，侧重对象之间的消息传递
1. 状态图(statechart diagrams)

  描述对象的所有状态以及事件发生而引起的状态之间的转移
1. 活动图(Activity diagrams)

  描述满足用例要求所要进行的活动以及活动时间的约束关系
1. 构件图(Component diagrams)

  描述代码构件的物理结构以及各构件之间的依赖关系
1. 部署图(Deployment diagrams)

  系统中硬件的物理体系结构

# 类图

类图的主要关系分为:
1. 泛化(Generalization)
1. 实现(Realization)
1. 关联(Association)
  1. 一般关联
  1. 聚集
  1. 组成
1. 依赖(Dependency)

![][p-class-diagram-sample]

各种关系的强弱顺序如下：

泛化 = 实现 > 组合 > 聚合 > 关联 > 依赖



----
参考文献:

1. [浅谈UML的概念和模型之UML类图关系 ](http://blog.csdn.net/jiuqiyuliang/article/details/8568303)


[p-class-diagram-sample]: /images/designPattern/UML/class_diagram_sample.png
