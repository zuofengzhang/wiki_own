---
layout: page
title: "Note 2016 - Java"
date: 2016-03-10 00:00:00
category: note
tag:

share: false
comments: false
---

[返回](/note2016)


## 面向对象的基本特征
1. 抽象
2. 继承
3. 封装
4. 多态

## public protected default private

| | 当前类 |同包 |子类 |其他|
|----| ----| ----| ----| ----|
|public | Y | Y | Y | Y |
| protected | Y|Y|Y|X|
| default | Y|Y|X|X|
| private | Y|X|X|X|
## overload 与 override

（Overload）重载：发生在同一个类之中，方法名相同、参数列表不同，与返回值无关、与final无关、与修饰符无关、与异常无关。

（Override）重写：发生在子类和父类之间，方法名相同、参数列表相同、返回值相同、不能是final的方法、重写的方法不能有比父类方法更为严格的修饰符权限、重写的方法所抛出的异常不能比父类的更大。
如果父类私有的方法，子类拥有方法签名相同的方法，子类不属于重写父类的方法，该方法属于子类的新方法。

## 构造器可不可以被重载或重写？
构造器不能被继承，故不能被重写、但可以被重载。

## 抽象类和接口的区别？
1）抽象类继承与object接口不继承object.
2）抽象类有构造器，接口中没有构造器。
3）抽象类中可以有普通成员变量和常量，接口中只能有常量，而且只能是public static final 不写默认。
4）抽象类中可以有抽象方法，也可以由普通的方法，接口中只能有抽象的方法而且修饰符只能是public abstract 不写默认。
5）**抽象类中可以有final的方法,接口中不能有final的方法。**
6）抽象类只能是单继承，多实现，接口是可以多继承其他接口，但是不能实现接口，和不能继承其他类。
7）抽象类中可以有静态的方法，接口中不可以。

## java中实现多态的机制是什么？
重写、重载、父类的声明指向子类的对象。

## int和integer的区别？
int是java的基本数据类型,integer是1.4版本后提供的基本类型包装类，当两者作为成员变量时，初始值分别为;int是0；integer是null;其中integer提供了一些对整数操作的方法，还定义了integer型数值的最值，其他基本类型也有对应的包装类，基本类型包装类的出现，使得java完全面向对象.

## String和StringBuffer的区别？StringBuffer和StringBuilder区别？
1. String是不可变的，对String类的任何改变都会返回一个新的String对象。
StringBuffer是可变的，对StringBuffer中的内容修改都是当前这个对象。
2. String重写了equals方法和hashCode方法，StringBuffer没有重写equals方法。
3. String是final的类。StringBuffer不是。
4. **String创建的字符串是在常量池中，创建的变量初始化一次，如果再对该字符串改变会产生新的字符串地址值，StringBuffer是在堆中创建对象，当对字符串改变时不会产生新的字符串地址值，如果对字符串进行频繁修改的话建议使用StringBuffer，以节省内存。**
5. StringBuffer和StringBuilder，StringBuffer是线程安全的，StringBulider是线程不安全的。当不考虑并发问题时候，请使用StringBulider。

## try {}里有一个return语句，那么紧跟在这个try后的finally {}里的code会不会被执行，什么时候被执行，在return前还是后?

```java
import java.io.*;
class test  
{
	public static void main (String[] args)
	{
	   System.out.println("===============");
	   System.out.println("result 1:"+new test().fun1());
	   System.out.println("===============");
       System.out.println("result 2:"+new test().fun2());
       System.out.println("===============");
       System.out.println("result 3:"+new test().fun3());
	}
	public int fun1(){
	    try {
	        System.out.println("1");
	        if (true) {
	            return 1;
	        }
	        System.out.println("3");

	    } catch(Exception e) {
	    } finally {
	        System.out.println("2");
	         if (true) {
	            return 2;
	         }
	        System.out.println("4");
	        return 5;
	    }

	}
  public int fun2(){
	    try {
	        System.out.println("1");
	        if (true) {
	            return 1;
	        }
	        System.out.println("3");

	    } catch(Exception e) {
	    } finally {
	        System.out.println("2");

	    }
	    return 5;
	}
  public int fun3(){
	    try {
	        System.out.println("1");
	        if (true) {
	            return 1;
	        }
	        System.out.println("3");

	    } catch(Exception e) {
	    }
	    return 5;
	}
}
```
输出结果:
```java
===============
1
2
result 1:2
===============
1
2
result 2:1
===============
1
result 3:1
```

由上面的例子，可以得出：
1. 存在finally块，且finally块中存在return
执行到try中的return语句，不再执行try块中后续的语句。转而执行finally块，执行到finally块中的return语句，返回。
2. 存在finally块，finally块不存在return
执行到try中的return语句，不再执行try块中后续的语句。转而执行finally块，执行完finally块，再执行try块中的return语句。
3. 不存在finally块
执行到try中的return语句，则直接返回。


## 内部类访问的局部变量为什么要定义成final

## finalize方法
finalize是Object类的一个方法，在垃圾收集器执行的时候会调用被回收对象的此方法，可以覆盖此方法提供垃圾收集时的其他资源回收，例如关闭文件等。JVM不保证此方法总被调用

## ‘==’和equals的区别？
‘==’比较的是两个变量的内容和在内存中的地址值是否全部相等，如果要比较两个基本数据类型那必须用’==’
equals如果没有重写，则和’==’的意义一样，如果重写了，则会会按照重写的内容进行比较，javaBean规定当重写equals时候必须重写hashCode，如果不重写会出现对象相同但是hashCode不同，这样会出现问题，eg:HashSet存储元素时候是按照hashCode，如果重写equals不重写hashCode会导致同一个对象，存储了两次。

## error和exception有什么区别？
Error（错误）表示系统级的错误和程序不必处理的异常，是java运行环境中的内部错误或者硬件问题。比如：内存资源不足等。对于这种错误，程序基本无能为力，除了退出运行外别无选择，它是由Java虚拟机抛出的。

Exception（违例）表示需要捕捉或者需要程序进行处理的异常，它处理的是因为程序设计的瑕疵而引起的问题或者在外的输入等引起的一般性问题，是程序必须处理的。
Exception又分为运行时异常，受检查异常。
运行时异常，表示无法让程序恢复的异常，导致的原因通常是因为执行了错误的操作，建议终止程序，因此，编译器不检查这些异常。
受检查异常，是表示程序可以处理的异常，也即表示程序可以修复（由程序自己接受异常并且做出处理）， 所以称之为受检查异常。


## 什么是内部类？分为哪几种？
内部类是指在一个外部类的内部再定义一个类。内部类作为外部类的一个成员，并且依附于外部类而存在的。内部类可为静态，可用protected和private修饰（而外部类只能使用public和缺省的包访问权限）。
内部类主要有以下几类：成员内部类、局部内部类、静态内部类、匿名内部类。


## 数字转字符有多少种方式，分别是什么
1）String.valueOf()
2）""  + 数字
3）Integer.toString()

## Java创建对象有几种方式
1）new关键字
2）反射
3）克隆
4）反序列化

## 用synchronized关键字修饰同步方法

# Java线程
## stop 与 suspend
不使用stop()，是因为它不安全。它会解除由线程获取的所有锁定，而且如果对象处于一种不连贯状态，那么其他线程能在那种状态下检查和修改它们。结果很难检查出真正的问题所在。
suspend()方法容易发生死锁。调用suspend()的时候，目标线程会停下来，但却仍然持有在这之前获得的锁定。此时，其他任何线程都不能访问锁定的资源，除非被"挂起"的线程恢复运行。对任何线程来说，如果它们想恢复目标线程，同时又试图使用任何一个锁定的资源，就会造成死锁。所以不应该使用suspend()，而应在自己的Thread类中置入一个标志，指出线程应该活动还是挂起。若标志指出线程应该挂起，便用wait()命其进入等待状态。若标志指出线程应当恢复，则用一个notify()重新启动线程。

## 当一个线程进入一个对象的一个synchronized方法后，其它线程是否可进入此对象的其它方法?
分几种情况：
     1）其他方法前是否加了synchronized关键字，如果没加，则能。
     2）如果这个方法内部调用了wait，则可以进入其他synchronized方法。
     3）如果其他个方法都加了synchronized关键字，并且内部没有调用wait，则不能。
	 4）如果其他方法是static，它用的同步锁是当前类的字节码，与非静态的方法不能同步，因为非静态的方法用的是this。

## 程序、进程、线程之间的关系
程序是一段静态的代码，是应用软件执行的蓝本。
进程是程序一次动态执行的过程，它对应了从代码加载、执行完毕的一个完整过程，这也是进程开始到消亡的过程。
线程是进程中独立、可调度的执行单元，是执行中最小单位。
一个程序一般是一个进程，但可以一个程序中有多个进程。
一个进程中可以有多个线程，但只有一个主线程。
Java应用程序中默认的主线程是main方法，如果main方法中创建了其他线程，JVM就会执行其他的线程。

## 创建线程有几种方式，分别是什么？
创建线程有三种方式：
1）是继承Thread类，创建格式如下：
Thread thread = new Thread();
2）是实现Runnable接口，创建格式如下：
Thread thread = new Thread(new Runnable());
其实Thread类实现了Runnable接口
3）通过线程池方式，获取线程
 package com.myjava.thread;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class ThreadPool {
    private  static int POOL_NUM = 10;
    public static void main(String[] agrs){
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for (int i = 0; i < POOL_NUM; i++) {
            RunnableThread thread = new RunnableThread();
            executorService.execute(thread);
        }
    }
}
class RunnableThread implements  Runnable{
    private   int THREAD_NUM = 10;
    public void run() {
        for (int i = 0; i <THREAD_NUM; i++) {
            System.out.println("线程"+Thread.currentThread()+i);
        }
        
    }
}

## 线程currentThread()与interrupt()方法的使用
currentThread()方法是获取当前线程
interrupt()唤醒休眠线程，休眠线程发生InterruptedException异常  ？？

# Java IO
# JDBC
# Java集合
# Java反射
