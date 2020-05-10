---
title: MySQL架构1 主从复制
layout: post
date: 2016-04-03 11:30:00
category: DataStruct
tags:
 - Java
 - DataStruct
 - algrithom

share: true
comments: true
---



**主从复制的几种方式**

**同步复制**

所谓的同步复制，意思是master的变化，必须等待slave-1,slave-2,...,slave-n完成后才能返回。

这样，显然不可取，也不是MYSQL复制的默认设置。比如，在WEB前端页面上，用户增加了条记录，需要等待很长时间。

**异步复制**

如同AJAX请求一样。master只需要完成自己的数据库操作即可。至于slaves是否收到二进制日志，是否完成操作，不用关心。MYSQL的默认设置。

**半同步复制**

master只保证slaves中的一个操作成功，就返回，其他slave不管。这个功能，是由google为MYSQL引入的。



![](/images/database/MySQL/master_slave_01.png)

当master的二进制日志每产生一个事件，都需要发往slave，如果我们有N个slave,那是发N次，还是只发一次？

如果只发一次，发给了slave-1，那slave-2,slave-3,...它们怎么办？

显然，应该发N次。实际上，在MYSQL   master内部，维护N个线程，每一个线程负责将二进制日志文件发往对应的slave。master既要负责写操作，还的维护N个线程，负担会很重。可以这样，slave-1是master的从，slave-1又是slave-2,slave-3,...的主，同时slave-1不再负责select。slave-1将master的复制线程的负担，转移到自己的身上。这就是所谓的多级复制的概念。