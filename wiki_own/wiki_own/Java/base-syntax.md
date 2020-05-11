---
title: Java基本语法与小错误
layout: post
date: 2016-08-18 14:58:00
category: Java
tags:
 - Java

share: true
comments: true
---

# 语法

# 常见的小错误

##　装箱类型为`null`时与基本类型比较

```java
Integer i = null;
if (i == 1) {
	System.out.println("success!");
}
```
当装箱类型遇到比较运算符的时候，会首先调用相应的方法将装箱类型转换为基本类型，如果装箱类型变量为null，运行将会抛出`java.lang.NullPointerException`异常；而对于非装箱类型如`String`则没有这种情况。

```java
String string = null;
if (string == "") {
  	System.out.println("success!");
}
```



