---
title: GC  
layout: post
date: 2016-05-04 12:26:00
category: Java
tags:
 - Java
 - JVM

share: true
comments: true
---

heap分代的目的是，应对不同生命周期的对象，大部分对象是朝生夕死的，也用一些常量是长期占用内存的，如数据值的枚举
动态调整：
