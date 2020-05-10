---
title: Dubbo基础
date: 2017-02-23 00:00:00
category: Distributed
tags:
 - Dubbo
 - Distributed

share: true
comments: true
---

# Dubbo-分布式服务治理框架

> 学习dubbo教程 笔记整理
>
> [Dubbo官方文档](http://dubbo.io/Developer+Guide-zh.htm)

关键词：分布式治理

Dubbo是阿里巴巴推出的一款分布式服务治理框架（虽然现在阿里内部一些部门已经不再使用）。

Dubbo将分布式服务分为四个角色：服务提供者、服务消费者、注册中心和监控中心。

# Dubbo角色

![][dubbo-roles]

1. 服务提供者注册到服务注册中心
2. 服务消费者从服务提供者订阅服务
3. 当服务提供者发生变化（出现新服务提供者、旧的服务提供者死掉等），注册中心通知服务消费者
4. 当服务消费者调用服务时，首先从注册中心查找服务，注册中心直接将选定的服务提供者的ip和端口等信息返回给服务消费者，服务消费者直接调用服务提供者
5. 服务提供者和服务消费者每个一分钟将收集的运行信息上报到监控中心

只有服务消费者调用服务生成者是同步调用，其他都是异步的。

Provider与Registry、Consumer与Register之间都保持着长连接，用于保持信息同步



# 简洁的项目结构



# Dubbo支持的RPC协议

1. 支持常见的传输协议：RMI、Dubbo、Hessain、WebService、Http等，
	其中Dubbo和RMI协议基于TCP实现，Hessian和WebService基于HTTP实现。
1. 传输框架：Netty、Mina、以及基于servlet等方式。
1. 序列化方式：Hessian2、dubbo、JSON（ fastjson 实现）、JAVA、SOAP 等。
4. 注册中心可以选择 zooKeeper Redis Dubbo Multicast

# Dubbo 服务降级
[Dubbo学习(七)：服务的升级和降级](http://blog.csdn.net/zuoanyinxiang/article/details/51027576)

服务降级方式：

- 服务接口拒绝服务：无用户特定信息，页面能访问，但是添加删除提示服务器繁忙。页面内容也可在Varnish或CDN内获取。
- 页面拒绝服务：页面提示由于服务繁忙此服务暂停。跳转到varnish或nginx的一个静态页面。
- 延迟持久化：页面访问照常，但是涉及记录变更，会提示稍晚能看到结果，将数据记录到异步队列或log，服务恢复后执行。
- 随机拒绝服务：服务接口随机拒绝服务，让用户重试，目前较少有人采用。因为用户体验不佳。

服务降级埋点的地方：

- 消息中间件：所有API调用可以使用消息中间件进行控制
- 前端页面：指定网址不可访问（NGINX+LUA）
- 底层数据驱动：拒绝所有增删改动作，只允许查询
