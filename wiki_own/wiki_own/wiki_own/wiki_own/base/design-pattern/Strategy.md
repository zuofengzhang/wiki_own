---
title: 策略模式(对象行为型模式)
layout: post
date: 2016-03-18 00:00:00
category: DesignPattern
tags:
 - Java
 - Design Pattern
 - Proxy Pattern

share: true
comments: true
---

< [设计模式][a-designPattern]

策略模式是Java中最常见的设计模式之一, 如`File.list(FileNameFilter fileNameFilter)`和Swing中的`ActionListener`等.
策略模式就类似于C/C++中的钩子或者回调, 由于Java中不存在显式指针, 策略模式是最好的实现`钩子`的方法.

策略模式本意上是指:`用于某一个具体的对象上有可供选择的算法策略,客户端可以在其运行时根据不同的需求决定使用某一个具体的算法策略`. 策略模式封装了变化的概念, 并将这些放入实现了统一接口的策略中. 此外, 在调用时是对接口的调用,而不是对接口的实现调用.

# 应用场景
[该例子来自网络][a-sample]

刘备要到江东娶老婆了，走之前诸葛亮给赵云（伴郎）三个锦囊妙计，说是按天机拆开能解决棘手问题，嘿，还别说，真解决了大问题，搞到最后是周瑜陪了夫人又折兵，那咱们先看看这个场景是什么样子的。

先说说这个场景中的要素：三个妙计，一个锦囊，一个赵云，妙计是亮哥给的，妙计放在锦囊里，俗称就是锦囊妙计嘛，那赵云就是一个干活的人，从锦囊取出妙计，执行，然后获胜。用java程序怎么表现这些呢？

那我们先来看看图？

![][p-uml]

三个妙计是同一类型的东西，那咱就写个接口：

```java
/**
 * 首先定义一个策略接口，这是诸葛亮老人家给赵云的三个锦囊妙计的接口。
 */  
interface IStrategy {  
    //每个锦囊妙计都是一个可执行的算法。  
    public void operate();  
}  
```

然后再写三个实现类，有三个妙计嘛：

妙计一：初到吴国：
```java
/**
 * 找乔国老帮忙，使孙权不能杀刘备。
 */  
class BackDoor implements IStrategy {  
    @Override  
    public void operate() {  
        System.out.println("找乔国老帮忙，让吴国太给孙权施加压力，使孙权不能杀刘备...");  
    }  
}
```

妙计二：求吴国太开个绿灯，放行：
```java
/**
 * 求吴国太开个绿灯。
 */  
class GivenGreenLight implements IStrategy {  
    @Override  
    public void operate() {  
        System.out.println("求吴国太开个绿灯，放行！");  
    }  
}
```

 妙计三：孙夫人断后，挡住追兵：
```java
/**
 * 孙夫人断后，挡住追兵。
 */  
public class BlackEnemy implements IStrategy {  
    @Override  
    public void operate() {  
        System.out.println("孙夫人断后，挡住追兵...");  
    }  
}  
```
好了，大家看看，三个妙计是有了，那需要有个地方放妙计啊，放锦囊里：

```java
class Context {  
    private IStrategy strategy;  
    //构造函数，要你使用哪个妙计  
    public Context(IStrategy strategy){  
        this.strategy = strategy;  
    }  
    public void operate(){  
        this.strategy.operate();  
    }  
}  
```
然后就是赵云雄赳赳的揣着三个锦囊，拉着已步入老年行列，还想着娶纯情少女的，色咪咪的刘备老爷子去入赘了，嗨，还别说，亮哥的三个妙计还真不错，瞧瞧：

```java
class ZhaoYun {  
  /**   * 赵云出场了，他根据诸葛亮给他的交代，依次拆开妙计   */  
  public static void main(String[] args) {  
      Context context;  
      //刚到吴国的时候拆开第一个  
      System.out.println("----------刚刚到吴国的时候拆开第一个---------------");  
      context = new Context(new BackDoor());  
      context.operate();//拆开执行  
      System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n");  
      //当刘备乐不思蜀时，拆开第二个  
      System.out.println("----------刘备乐不思蜀，拆第二个了---------------");  
      context = new Context(new GivenGreenLight());  
      context.operate();//拆开执行  
      System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n");  
      //孙权的小追兵了，咋办？拆开第三个锦囊  
      System.out.println("----------孙权的小追兵了，咋办？拆开第三个锦囊---------------");  
      context = new Context(new BlackEnemy());  
      context.operate();//拆开执行  
      System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n");  
  }  
}  
```

后话：就这三招，搞得的周郎是“赔了夫人又折兵”呀！这就是策略模式，高内聚低耦合的特点也表现出来了，还有一个就是扩展性，也就是OCP原则，策略类可以继续添加下去气，只是修改Context.java就可以了，这个不多说了，自己领会吧。


## 总结
在上面的例子中, 提供的三种策略 `BackDoor` `GivenGreenLight`和`BlackEnemy`, 这三种策略封装了每种策略不同的处理, 相同的处理全部位于Context中; 从另一个角度来说, 将三个对象传递到`Context`类, 当调用`Context.operate()`方法时, 会调用三个对象的`operate`方法, 这就类似于C/C++中的钩子: 将函数指针传递进来来调用函数.


---
参考文献:

1. [Java策略模式 ][a-sample]
2. [Java IO](/java-IO/)

[a-designPattern]: /designPattern/
[a-sample]: http://yangguangfu.iteye.com/blog/815107
[p-uml]: /images/designPattern/strategy/uml.jpg
