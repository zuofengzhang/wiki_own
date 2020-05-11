---
title: ProxyPattern(代理模式)与Java动态代理
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



# 代理模式
Provide a surrogate or placeholder for another object to controlaccess to it(为其他对象提供一种代理以控制对这个对象的访问)。使用代理模式创建代理对象，让代理对象控制目标对象的访问(目标对象可以是远程的对象、创建开销大的对象或需要安全控制的对象),并且可以在不改变目标对象的情况下添加一些额外的功能。

书写执行一个功能的函数时，经常需要在其中写入与功能不是直接相关但很有必要的代 码，如日志记录，信息发送，安全和事务支持等，这些枝节性代码虽然是必要的，但它会带 来以下麻烦：^[java代理机制]

1. 枝节性代码游离在功能性代码之外，它下是函数的目的，这是对OO是一种破坏
2. 枝节性代码会造成功能性代码对其它类的依赖，加深类之间的耦合，而这是OO系统所竭 力避免的
3. 枝节性代码带来的耦合度会造成功能性代码移植困难，可重用性降低
4. 从法理上说，枝节性代码应该"监视"着功能性代码，然后采取行动，而不是功能性代码 "通知"枝节性代码采取行动，这好比吟游诗人应该是主动记录骑士的功绩而不是骑士主 动要求诗人记录自己的功绩

## 常见代理
1. 远程代理(Remote Proxy)：对一个位于不同的地址空间对象提供一个局域代表对象，如RMI中的stub
1. 虚拟代理(Virtual Proxy)：根据需要将一个资源消耗很大或者比较复杂的对象，延迟加 载，在真正需要的时候才创建
1. 保护代理(Protect or Access Proxy)：控制对一个对象的访问权限。
1. 智能引用(Smart Reference Proxy)：提供比目标对象额外的服务和功能。

## 代理模式演示

![][puml1]

```java
public interface AbstractObject {
    void operation();
}
public class RealObject implements AbstractObject {
    public void operation() {
        System.out.println(this.getClass().getSimpleName() + ": operation");
    }
}
public class ProxyObject implements AbstractObject {
    private AbstractObject realObject;
    public void setRealObject(AbstractObject realObject) {
        this.realObject = realObject;
    }
    private void doBefore() {
        System.out.println(getClass().getSimpleName() + ": doBefore");
    }
    private void doAfter() {
        System.out.println(getClass().getSimpleName() + ": doAfter");
    }
    public void operation() {
        System.out.println(this.getClass().getSimpleName() + ": operation");
        if (realObject == null) {
            realObject = new RealObject();
        }
        doBefore();
        realObject.operation();
        doAfter();
    }
}
```

客户端调用

```java
AbstractObject proxyObject = new ProxyObject();
proxyObject.operation();
```

## 代理模式的应用形式
1. 远程代理(Remote Proxy) -可以隐藏一个对象存在于不同地址空间的事实。也使得客户端可以访问在远程机器上的对象，远程机器可能具有更好的计算性能与处理速度，可以快速响应并处理客户端请求。
2. 虚拟代理(Virtual Proxy) – 允许内存开销较大的对象在需要的时候创建。只有我们真正需要这个对象的时候才创建。
3. 写入时复制代理(Copy-On-Write Proxy) – 用来控制对象的复制，方法是延迟对象的复制，直到客户真的需要为止。是虚拟代理的一个变体。
4. 保护代理(Protection (Access)Proxy) – 为不同的客户提供不同级别的目标对象访问权限
5. 缓存代理(Cache Proxy) – 为开销大的运算结果提供暂时存储，它允许多个客户共享结果，以减少计算或网络延迟。
6. 防火墙代理(Firewall Proxy) – 控制网络资源的访问，保护主题免于恶意客户的侵害。
7. 同步代理(SynchronizationProxy) – 在多线程的情况下为主题提供安全的访问。
8. 智能引用代理(Smart ReferenceProxy) - 当一个对象被引用时，提供一些额外的操作，比如将对此对象调用的次数记录下来等。
9. 复杂隐藏代理(Complexity HidingProxy) – 用来隐藏一个类的复杂集合的复杂度，并进行访问控制。有时候也称为外观代理(Façade Proxy)，这不难理解。复杂隐藏代理和外观模式是不一样的，因为代理控制访问，而外观模式是不一样的，因为代理控制访问，而外观模式只提供另一组接口。

## 与装饰者模式的区别


# Java动态代理(Dynamic Proxy)
Java动态代理分为JVM动态代理和CGLib代理, JVM代理是由JVM调用和实现的, 其限制条件是被代理对象必须实现接口, 在生成代理对象时必须将接口传递进来.
CGLib是第三方库, CGLib代理是通过继承被代理对象实现的.
## JVM代理
java动态代理类位于java.lang.reflect包下，一般主要涉及到以下两个类：

1. `Interface InvocationHandler`：该接口中仅定义了一个方法`Object：invoke(Object obj,Method method, Object[] args)`。在实际使用时，第一个参数obj一般是指代理类，method是被代理的方法，如上例中的`operation()`，args为该方法的参数数组。 这个抽象方法在代理类中动态实现。
2. `Proxy`：该类即为动态代理类，作用类似于上例中的`ProxyObject`。
3. `Protected Proxy(InvocationHandler h)`：构造函数，估计用于给内部的h赋值。
4. `Static Class getProxyClass(ClassLoader loader, Class[] interfaces)`：获得一个代理类，其中loader是类装载器，interfaces是真实类所拥有的全部接口的数组。
5. `Static Object newProxyInstance(ClassLoader loader, Class[] interfaces, InvocationHandler h)`：返回代理类的一个实例，返回后的代理类可以当作被代理类使用 (可使用被代理类的在Subject接口中声明过的方法)。

![][p-jvm-proxy-uml]

```java
interface BusinessBar {
    void bar(String message);
}
class BusinessBarImpl implements BusinessBar {
    public void bar(String message) {
        System.out.println(getClass().getSimpleName() + ": bar() : " + message);
    }
}
class BusinessImplProxy implements InvocationHandler {
    private Object obj;
    public BusinessImplProxy(Object obj) {
        this.obj = obj;
    }
    public static Object factory(Object obj) {
        Class cls = obj.getClass();
        return Proxy.newProxyInstance(cls.getClassLoader(), cls.getInterfaces(), new BusinessImplProxy(obj));
    }
    public void doBefore() {
        System.out.println("do something before Business Logic");
    }
    public void doAfter() {
        System.out.println("do something after Business Logic");
    }
    /**
    *实现InvocationHandler的方法
    */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        doBefore();
        result = method.invoke(obj, args);
        doAfter();
        return result;
    }
}
```

客户端调用
1. 逐步调用(可以看到每一步)
  将被代理对象的ClassLoader和接口以及实现InvocationHandler接口的代理对象传递进来生成代理对象
  ```java
  BusinessBar businessBar = new BusinessBarImpl();
  InvocationHandler proxy = new BusinessImplProxy(businessBar);
  Class<?> proxyClass = Proxy.getProxyClass(businessBar.getClass().getClassLoader(), businessBar.getClass().getInterfaces());
  try {
      Constructor<?> constructor = proxyClass.getConstructor(new Class[]{InvocationHandler.class});
      BusinessBar businessBar1 = (BusinessBar) constructor.newInstance(new Object[]{proxy});
      businessBar1.bar(" decomposable model ");
  } catch (NoSuchMethodException e) {
      e.printStackTrace();
  } catch (InvocationTargetException e) {
      e.printStackTrace();
  } catch (InstantiationException e) {
      e.printStackTrace();
  } catch (IllegalAccessException e) {
      e.printStackTrace();
  }
  ```
1. 利用`Proxy.newProxyInstance`直接产生代理对象
  ```java
  BusinessBar newProxyInstance = (BusinessBar) Proxy.newProxyInstance(businessBar.getClass().getClassLoader(), businessBar.getClass().getInterfaces(), new BusinessImplProxy(businessBar));
  /*调用代理对象的任何方法, JVM将调用代理对象的(实现InvocationHandler接口的)invoke方法*/
  newProxyInstance.bar("simple instance");
  ```
1. 利用工厂生成代理对象
  ```java
  BusinessBar businessBar = new BusinessBarImpl();
  BusinessBar businessBarxy = (BusinessBar) BusinessImplProxy.factory(businessBar);
  businessBarxy.bar("JVMProxyClient say hi!");
  ```
上面的例子中, `InvocationHandler`的实现类(即代理类)不只可以作为`BusinessBar`的代理, 可以适用于任何实现接口的类. 假设存在下面一个类和接口
```java
interface BusinessFoo {
    void foo();
}
class BusinessFooImpl implements BusinessFoo {
    public void foo() {
        System.out.println(this.getClass().getSimpleName()+": foo");
    }
}
```
也可以通过下面的方式创建代理
```java
BusinessFooImpl businessFoo = new BusinessFooImpl();
BusinessFoo businessFoo1 = (BusinessFoo) BusinessImplProxy.factory(businessFoo);
businessFoo1.foo();
```

**总结**
1. JVM代理不需要为每个被代理对象创建代理对象
2. 被代理对象需要实现接口
3. 可以在不修改被代理对象的情况下, 从外部实现代理
4. 是不是必须代理调用非接口的接口

## CGLib代理
JVM的动态代理机制只能代理实现了接口的类，而不能实现接口的类就不能实现JDK的动态代理，cglib是针对类来实现代理的，他的原理是对指定的目标类生成一个子类，并覆盖其中方法实现增强，但因为采用的是继承，所以不能对final修饰的类进行代理。

例子:
![][p-cglib-sample-uml]


```java
class BookFacadeImpl1 {
    public void addBook() {
        System.out.println("增加图书的普通方法...");
    }
}
/**
 * 使用cglib动态代理
 */
class BookFacadeProxy implements MethodInterceptor {
  private Object target;
  /**
   * 创建代理对象
   */
  public Object getInstance(Object target) {
      this.target = target;
      Enhancer enhancer = new Enhancer();
      //设定代理类的父类是被代理类
      enhancer.setSuperclass(this.target.getClass());
      // 回调方法
      enhancer.setCallback(this);
      // 创建代理对象
      return enhancer.create();
  }
  /**
   * 回调方法
   */
  public Object intercept(Object obj, Method method, Object[] args,
                          MethodProxy proxy) throws Throwable {
      System.out.println("事物开始");
      proxy.invokeSuper(obj, args);
      System.out.println("事物结束");
      return null;
  }
}
```
客户端
```java
BookFacadeProxy cglib = new BookFacadeProxy();
BookFacadeImpl1 bookCglib = (BookFacadeImpl1) cglib.getInstance(new BookFacadeImpl1());
bookCglib.addBook();
```

cgLib的Maven依赖:
```xml
<dependency>
    <groupId>cglib</groupId>
    <artifactId>cglib</artifactId>
    <version>2.2.2</version>
</dependency>
```

# 应用演示
## 加日志
## 延时调用



# Spring AOP
AOP 面向切面编程, 是Spring root的两大特性之一, Spring中的AOP底层就是通过Java动态代理实现的: 对于实现了接口的类,采用JVM代理;而没有实现接口的类采用Cglib.


---
参考文献:

1. [Java代理模式 ](http://blog.csdn.net/jackiehff/article/details/8621517)

[java代理机制]: http://www.cnblogs.com/machine/archive/2013/02/21/2921345.html

[puml1]: /images/designPattern/proxy/proxy_uml.png
[p-jvm-proxy-uml]: /images/designPattern/proxy/jvm-proxy01.png
[p-cglib-sample-uml]: /images/designPattern/proxy/cglib-sample-uml.png

[a-designPattern]: /designPattern/
