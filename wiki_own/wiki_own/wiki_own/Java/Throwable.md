---
title: Java异常
layout: post
date: 2016-04-12 11:28:00
category: Java
tags:
 - Java
 - Throwable

share: true
comments: true
---

请参考[深入理解java异常处理机制][a-ref-1]


异常是指当程序中某些地方出错时创建的一种特殊的运行时错误对象。Java创建异常对象后，就发送给Java程序，即抛出异常(throwing an exception)。程序捕捉到这个异常后，可以编写相应的异常处理代码进行处理。使用异常处理可以使得程序更加健壮，有助于调试和后期维护。
# Throwable的继承体系
Throwable类派生了两个类：Exception类和Error类，其中Error类系统保留，而Exception类供应用程序使用，它下面又派生出几个具体的异常类，都对应着一项具体的运行错误

![][p-Throwable-inherit]

# 异常的工作原理
抛出异常：当一个方法出现错误引发异常时，方法创建异常对象并交付运行时系统，异常对象中包含了异常类型和异常出现时的程序状态等异常信息。运行时系统负责寻找处置异常的代码并执行。

捕获异常：在方法抛出异常之后，运行时系统将转为寻找合适的异常处理器（exception handler）。潜在的异常处理器是异常发生时依次存留在调用栈中的方法的集合。当异常处理器所能处理的异常类型与方法抛出的异常类型相符时，即为合适 的异常处理器。运行时系统从发生异常的方法开始，依次回查调用栈中的方法，直至找到含有合适异常处理器的方法并执行。当运行时系统遍历调用栈而未找到合适 的异常处理器，则运行时系统终止。同时，意味着Java程序的终止。


# 常见的受检异常
 除了RuntimeException及其子类以外，其他的Exception类及其子类都属于可查异常。这种异常的特点是Java编译器会检查它，也就是说，当程序中可能出现这类异常，要么用try-catch语句捕获它，要么用throws子句声明抛出它，否则编译不会通过。


IOException：操作输入流和输出流时可能出现的异常。

EOFException   文件已结束异常

FileNotFoundException   文件未找到异常

# 常见的非受检异常
运行时异常：都是RuntimeException类及其子类异常，如NullPointerException(空指针异常)、IndexOutOfBoundsException(下标越界异常)等，这些异常是不检查异常，程序中可以选择捕获处理，也可以不处理。这些异常一般是由程序逻辑错误引起的，程序应该从逻辑角度尽可能避免这类异常的发生。

ArithmeticException

ArrayIndexOutOfBoundsException
1、 java.lang.ArrayIndexOutOfBoundsException
 数组索引越界异常。当对数组的索引值为负数或大于等于数组大小时抛出。
 2、java.lang.ArithmeticException
 算术条件异常。譬如：整数除零等。
 3、java.lang.NullPointerException
 空指针异常。当应用试图在要求使用对象的地方使用了null时，抛出该异常。譬如：调用null对象的实例方法、访问null对象的属性、计算null对象的长度、使用throw语句抛出null等等
 4、java.lang.ClassNotFoundException
 找不到类异常。当应用试图根据字符串形式的类名构造类，而在遍历CLASSPAH之后找不到对应名称的class文件时，抛出该异常。

5、java.lang.NegativeArraySizeException  数组长度为负异常

6、java.lang.ArrayStoreException 数组中包含不兼容的值抛出的异常

7、java.lang.SecurityException 安全性异常

8、java.lang.IllegalArgumentException 非法参数异常


综合实例:
```java
public class TestException {
    public TestException() {
    }

    boolean testEx() throws Exception {
        boolean ret = true;
        try {
            ret = testEx1();
        } catch (Exception e) {
            System.out.println("testEx, catch exception");
            ret = false;
            throw e;
        } finally {
            System.out.println("testEx, finally; return value=" + ret);
            return ret;
        }
    }

    boolean testEx1() throws Exception {
        boolean ret = true;
        try {
            ret = testEx2();
            if (!ret) {
                return false;
            }
            System.out.println("testEx1, at the end of try");
            return ret;
        } catch (Exception e) {
            System.out.println("testEx1, catch exception");
            ret = false;
            throw e;
        } finally {
            System.out.println("testEx1, finally; return value=" + ret);
            return ret;
        }
    }

    boolean testEx2() throws Exception {
        boolean ret = true;
        try {
            int b = 12;
            int c;
            for (int i = 2; i >= -2; i--) {
                c = b / i;
                System.out.println("i=" + i);
            }
            return true;
        } catch (Exception e) {
            System.out.println("testEx2, catch exception");
            ret = false;
            throw e;
        } finally {
            System.out.println("testEx2, finally; return value=" + ret);
            return ret;
        }
    }

    public static void main(String[] args) {
        TestException testException1 = new TestException();
        try {
            testException1.testEx();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```
输出结果:
```
i=2
i=1
testEx2, catch exception
testEx2, finally; return value=false
testEx1, finally; return value=false
testEx, finally; return value=false
```

----

[参考文献]:

1. Think in Java
2. [深入理解java异常处理机制][a-ref-1]

[p-Throwable-inherit]: /images/java/Throwable/inherit.png

[a-ref-1]: http://blog.csdn.net/hguisu/article/details/6155636 "深入理解java异常处理机制"
