---
title: Java 反射
layout: post
date: 2015-10-14 00:00:00
category: Java
tags:
 - Java
 - reflection

share: true
comments: true
---

## 在多级类继承中, 如何找到方法是在哪个类实现的?

currentObject 是ContextWrapper 的子类的对象, `mBase`是定义在 `currentObject`某一个父类的字段,
`mBase`中的某个方法`getSharedPerences()`是定义在某个父类中的抽象方法,
如何确定该方法是在哪个父类中实现的?

```java
try{
  Field field = ContextWrapper.class.getDeclaredField("mBase");
  field.setAccessible(true);
  Object object = field.get(currentObject);
  Log.i(TAG,"the real implement class is " + filed.getClass().getName());
}catch(Exception e){
}
```

`getSharedPerences()`方法一定定义在`mBase`类中. 妙哉!
获得`mBase`对象的类名就是该方法的实现类.
