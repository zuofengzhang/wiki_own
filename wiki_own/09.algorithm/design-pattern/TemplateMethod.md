---
title: TemplateMethod(模板方法)
layout: post
date: 2016-03-18 00:00:00
category: DesignPattern
tags:
 - Java
 - Design Pattern
 - Template Method

share: true
comments: true
---


模板方法模式

在执行某些逻辑过程时，存在诸多的相同的部分逻辑。定义一个抽象类，将这些相同的逻辑以具体方法以及具体构造函数的形式实现，声明一些抽象方法来迫使子类实现这些不同的逻辑。不同的子类可以以不同的方式实现这些抽象方法，从而对剩余的逻辑有不同的实现。在执行时，就类似模板，执行相同的过程，而在过程中的一部分过程不同的子类又存在不同实现。这就是模板方法。

### 例子
![](/images/designPattern-TemplateMethod.png)

```java
public abstract class TemplateParent{
  public void entireInvoke(){
    templateMethod1();
    diversityMethod();
    templateMethod2();
  }
  public void templateMethod1(){
    System.out.print("step 1\t");
  }
  public void templateMethod2(){
    System.out.print("\tStep 3");
  }
  public abstract void diversityMethod();
}
```
模板方法父类定义了一个主方法`entireInvoke`,这个方法把基本操作方法组合在一起形成一个总算法或一个总行为的方法。
方法`templateMethod1`与`templateMethod2`是相同的步骤。方法`diversityMethod`是不同的步骤，定义为抽象方法。
因此，子类继承了模板方法父类`TemplateParent`之后，按照各自的执行过程实现`diversityMethod`。

```java
public class Process1 extends TemplateParent{
  public void diversityMethod(){
    System.out.print("Process1： Step 2");
  }
}
public class Process2 extends TemplateParent{
  public void diversityMethod(){
    System.out.print("Process2： Step 2");
  }
}
```

测试类

```java
TemplateParent tp1=new Process1();
tp1.entireInvoke();
//Step 1    Process1：Step 2   Step3
TemplateParent tp2=new Process2();
tp2.entireInvoke();
//Step 1    Process2: Step2   Step3
```
上述的两个方法都实现了相同的执行过程，而又在其中的一个过程提供了自己的实现方式。
