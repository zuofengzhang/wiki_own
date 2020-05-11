---
title: Java编程tips
layout: post
date: 2017-10-17 10:00:00
category: Java
tags:
 - Java

share: true
comments: true
---

1. Calendar
获取小时数 [2017-10-17 16:10:36 星期二]
```java
Calendar calendar = Calendar.getInstance();
// 获取12小时制的小时数
int hour12 = calendar.get(Calendar.HOUR);
// 获取24小时制的小时数
int hour24 = calendar.get(Calendar.HOUR_OF_DAY);
```

2. 读锁(共享锁)与写锁(独占锁)

[2017-10-17 16:10:41 星期二]

虽然读锁可以允许多个线程读取，写锁会独占，但是当一个线程对资源加了读锁后，另一个线程要加独占锁也必须等待读锁释放。

3. Java web 获取 classes 目录

```java
String path = Thread.currentThread().getContextClassLoader().getResource("/").toURI().getPath();
```
