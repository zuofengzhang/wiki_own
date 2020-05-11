---
title: JDK内置工具的使用
layout: post
date: 2017-06-28 11:28:00
category: Java
tags:
 - Java

share: true
comments: true
---

参考: <http://blog.csdn.net/fenglibing/article/details/6411999>

1. javah (C Header and Stub File Generator:用于生成native方法对应的C头文件,见[JNI][a1]
1. [jps (Java Virtual Machine Process Status Tool)](http://blog.csdn.net/fenglibing/article/details/6411932)
1. [jstack (Java Stack Trace)](/Java/tools/jstack/)
1. [jstat (Java Virtual Machine Statistics Monitoring Tool)](http://blog.csdn.net/fenglibing/article/details/6411951)
1. jmap (Java Memory Map)
1. jinfo (Java Configuration Info)
1. jconsole (Java Monitoring and Management Console)
1. jvisualvm (Java Virtual Machine Monitoring, Troubleshooting, and Profiling Tool)
1. jhat (Java Heap Analyse Tool)
1. Jdb (The Java Debugger)
1. Jstatd (Java Statistics Monitoring Daemon)



//TODO
javap


```shell
set JAVA_OPTS=-Xms%INITIAL_HEAP_SIZE% -Xmx%MAXIMUM_HEAP_SIZE% -Xss%STACK_SIZE%
if not "%USER_LANGUAGE%"=="" set JAVA_OPTS=%JAVA_OPTS% -Duser.language=%USER_LANGUAGE%
if not "%USER_COUNTRY%"=="" set JAVA_OPTS=%JAVA_OPTS% -Duser.country=%USER_COUNTRY%

rem run Jude //注释 运行jude
start javaw %JAVA_OPTS% -jar "%JUDE_HOME%\%JUDE_JAR%"  %1 %2 %3 //
IF ERRORLEVEL 2 goto noJavaw
goto end
```





----

[源码]:

1. [源码百度云链接](http://pan.baidu.com/s/1c0YLi4S)     密码：yfo5

[参考文献]:

1. Think in Java

[a1]: /java-NIO/

[p0]: /images/java/io/java-io-inheritance.png
