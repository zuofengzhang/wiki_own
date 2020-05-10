---
title: "【读书笔记】Java程序性能优化"
date: 2017-06-27 00:00:00
category: java
tags:
 - Linux
 - JStack


share: true
comments: true
---


# Java程序性能优化



# 栈

## OverStack
默认xss

```shell
java -XX:+PrintFlagsFinal -version | grep -i 'stack'
     intx CompilerThreadStackSize                   = 0                                   {pd product}
    uintx GCDrainStackTargetSize                    = 64                                  {product}
     bool JavaMonitorsInStackTrace                  = true                                {product}
    uintx MarkStackSize                             = 4194304                             {product}
    uintx MarkStackSizeMax                          = 536870912                           {product}
     intx MaxJavaStackTraceDepth                    = 1024                                {product}
     bool OmitStackTraceInFastThrow                 = true                                {product}
     intx OnStackReplacePercentage                  = 140                                 {pd product}
     intx StackRedPages                             = 1                                   {pd product}
     intx StackShadowPages                          = 20                                  {pd product}
     bool StackTraceInThrowable                     = true                                {product}
     intx StackYellowPages                          = 2                                   {pd product}
     intx ThreadStackSize                           = 1024                                {pd product}
     bool UseOnStackReplacement                     = true                                {pd product}
     intx VMThreadStackSize                         = 1024                                {pd product}
java version "1.8.0_211"
Java(TM) SE Runtime Environment (build 1.8.0_211-b12)
Java HotSpot(TM) 64-Bit Server VM (build 25.211-b12, mixed mode)
```

<https://docs.oracle.com/javase/8/docs/technotes/tools/unix/java.html>

-Xss is translated in a VM flag named ThreadStackSize, 是-XX:ThreadStackSize的简写形式, 即线程栈的大小,  单位kb

-Xsssize  

   Sets the thread stack size (in bytes). Append the
   letter k or K to indicate KB, m or M to indicate MB, g or G to
   indicate GB. The default value depends on the platform:

- Linux/ARM (32-bit): 320 KB
- Linux/i386 (32-bit): 320 KB
- Linux/x64 (64-bit): 1024 KB
- OS X (64-bit): 1024 KB
- Oracle Solaris/i386 (32-bit): 320 KB
- Oracle Solaris/x64 (64-bit): 1024 KB

The following examples set the thread stack size to 1024 KB in different units:

-Xss1m
-Xss1024k
-Xss1048576

This option is equivalent to -XX:ThreadStackSize.

## OverStack样例

```java
public class OverStackTest {
    private int cnt = 0;

    private void recursion() {
        cnt++;
        recursion();
    }

    private void testOverStack() {
        try {
            recursion();
        } catch (Throwable e) {
            System.out.println("deep of stack is " + cnt);
        }
    }

    public static void main(String[] args) {
        new OverStackTest().testOverStack();
    }
}
```

```shell
> javac OverStackTest.java
> java -Xss256k  OverStackTest
deep of stack is 1887
> java -Xss512k  OverStackTest
deep of stack is 7428
> java -Xss670k  OverStackTest
deep of stack is 7616
```



- 标记复制: 新生代垃圾回收的通用算法
- 标记压缩: 老年代垃圾回收

## CMS收集器

三种操作:

- 年轻代回收（暂停所有应用线程）
- 启动并发线程回收老年带空间
- 如有必要，full gc
