---
title: log4j
layout: post
date: 2017-06-29T12:26:00.000Z
category: Java
tags:
  - Java
  - log4j
share: true
comments: true
---



log4j 是目前最常见的日志系统之一，本文主要总结log4j的使用和配置.

# log4j的组件

- logger
- appender
- level
- layout

## logger-记录器 打印事件
logger 可以理解为日志记录器，它有name、appender和level等属性.
当调用
```java
Logger logger = LoggerFactory.getLogger(AClass.class);
```
时，`logger`变量即为名称为`AClass`类的全名的logger.

如果使用
```java
Logger logger = LoggerFactory.getLogger("myRedo");
```
logger变量即为名称为myRedo的logger

1. 所有的logger的根都是rootLogger
1. 一般情况下，取类名作为logger的名称. 父子关系遵从Java package的方法
1. 子logger默认继承父logger的所有的配置. 子logger的配置会覆盖父logger的配置
1. logger的名称可以为任意字符串
1. log4j可以使用多种配置方式: properties文件、xml文件和Java代码
1. 使用Java代码配置时，可以指定为基本配置: `BasicConfigurator.configure();`
1. 可以指定特定包的log
  样例: 使用代码的方式配置特定包的logger
  ```java
    Logger.getRootLogger().setLevel(Level.WARN);
  Logger.getLogger("cd.itcast.core").setLevel(Level.DEBUG);
  ```


## appender 打印目的地

log4j定义了多种appender:

- org.apache.log4j.ConsoleAppender(控制台)
- org.apache.log4j.FileAppender(文件)
- org.apache.log4j.DailyRollingFileAppender(每天产生一个日志文件)
- org.apache.log4j.RollingFileAppender(文件大小到达指定尺寸的时候产生一个新的文件)
- org.apache.log4j.WriterAppender(将日志信息以流格式发送到任意指定的地方)
- org.apache.log4j.jdbc.JDBCAppender(将日志信息写入数据库)

在properties文件中配置

```properties
log4j.appender.<appenderName> = fully.qualified.name.of.appender.class
```

Appender类的属性
### ConsoleAppender
   Threshold=DEBUG :指定日志消息的输出最低层次.
   ImmediateFlush=true :默认值是true,意谓着所有的消息都会被立即输出.
   Target=System.err :默认情况下是:System.out,指定输出控制台
### FileAppender
   Threshold=DEBUF    :指定日志消息的输出最低层次.
   ImmediateFlush=true   :默认值是true,意谓着所有的消息都会被立即输出.
   File=mylog.txt   :指定消息输出到mylog.txt文件.
   Append=false   :默认值是true,即将消息增加到指定文件中，false指将消息覆盖指定的文件内容.

### RollingFileAppender
   Threshold=DEBUG    :指定日志消息的输出最低层次.
   ImmediateFlush=true    :默认值是true,意谓着所有的消息都会被立即输出.
   File=mylog.txt    :指定消息输出到mylog.txt文件.
   Append=false    :默认值是true,即将消息增加到指定文件中，false指将消息覆盖指定的文件内容.
   MaxFileSize=100KB    : 后缀可以是KB, MB 或者是 GB. 在日志文件到达该大小时，将会自动滚动，即将原来的内容移到mylog.log.1文件.
   MaxBackupIndex=2   :指定可以产生的滚动文件的最大数.
   log4j.appender.A1.layout.ConversionPattern=%-4r %-5p %d{yyyy-MM-dd HH:mm:ssS} %c %m%n

### DailyRollingFileAppender
  DatePattern
  layout
  Encoding
  MaxBackupIndex


## level 输出级别

ERROR、WARN、INFO、DEBUG

- ERROR 为严重错误 主要是程序的错误
- WARN 为一般警告，比如session丢失
- INFO 为一般要显示的信息，比如登录登出
- DEBUG 为程序的调试信息

## layout

-X号: X信息输出时左对齐；

%p: 输出日志信息优先级，即DEBUG，INFO，WARN，ERROR，FATAL,
%d: 输出日志时间点的日期或时间，默认格式为ISO8601，也可以在其后指定格式，比如:%d{yyy MMM dd HH:mm:ss,SSS}，输出类似:2002年10月18日 22:10:28，921
%r: 输出自应用启动到输出该log信息耗费的毫秒数
%c: 输出日志信息所属的类目，通常就是所在类的全名
%t: 输出产生该日志事件的线程名
%l: 输出日志事件的发生位置，相当于%C.%M(%F:%L)的组合,包括类目名、发生的线程，以及在代码中的行数. 举例:Testlog4.main (TestLog4.java:10)
%x: 输出和当前线程相关联的NDC(嵌套诊断环境),尤其用到像java servlets这样的多客户多线程的应用中.
%%: 输出一个"%"字符
%F: 输出日志消息产生时所在的文件名称
%L: 输出代码中的行号
%m: 输出代码中指定的消息,产生的日志具体信息
%n: 输出一个回车换行符，Windows平台为"/r/n"，Unix平台为"/n"输出日志信息换行

日志信息格式中几个符号所代表的含义:
可以在%与模式字符之间加上修饰符来控制其最小宽度、最大宽度、和文本的对齐方式. 如:

1)   %20c:指定输出category的名称，最小的宽度是20，如果category的名称小于20的话，默认的情况下右对齐.
2)   %-20c:指定输出category的名称，最小的宽度是20，如果category的名称小于20的话，"-"号指定左对齐.
3)   %.30c:指定输出category的名称，最大的宽度是30，如果category的名称大于30的话，就会将左边多出的字符截掉，但小于30的话也不会有空格.
4)   %20.30c:如果category的名称小于20就补空格，并且右对齐，如果其名称长于30字符，就从左边较远输出的字符截掉.


## log4j性能优化

1. 为了避免多次打开文件引起资源的浪费，log4j当打开文件或连接数据库后，会始终保持着连接，直到引用关闭.
因此， 当引用运行过程中，删除文件. log4j不会再次创建该文件.
2. logger文件是懒生成模式:当打印时，判断是否需要创建新文件

# 典型应用

## 配置非类名logger及代码获取logger的信息

```properties
###################
# rootLogger的级别为INFO，appender为terminal01和allToFile
###################
log4j.rootLogger=INFO,terminal01,allToFile
###################
# Console Appender
###################
log4j.appender.terminal01=org.apache.log4j.ConsoleAppender
log4j.appender.terminal01.Target=System.out
log4j.appender.terminal01.layout=org.apache.log4j.PatternLayout
log4j.appender.terminal01.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss.sss},%c,%-p,%m %n
########################
# Rolling File
########################
log4j.appender.allToFile=org.apache.log4j.DailyRollingFileAppender
log4j.appender.allToFile.File=/data/logs/blog-all.log
log4j.appender.allToFile.Append=true
log4j.appender.allToFile.MaxFileSize=100MB
log4j.appender.allToFile.MaxBackupIndex=7
log4j.appender.allToFile.Encoding=UTF-8  
log4j.appender.allToFile.layout=org.apache.log4j.PatternLayout
log4j.appender.allToFile.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss.sss},%c,%-p,%m %n
########################
# 指定某个包的logger
########################
log4j.logger.com.abc.engin=INFO,engin
########################
# appender engin
########################
log4j.additivity.engin=false
## 是否追加到root appender中
log4j.appender.engin=org.apache.log4j.DailyRollingFileAppender
log4j.appender.engin.File=/data/logs/engin.log
log4j.appender.engin.Append=true
log4j.appender.engin.MaxFileSize=500MB
log4j.appender.engin.MaxBackupIndex=7
log4j.appender.engin.Encoding=UTF-8
log4j.appender.engin.layout=org.apache.log4j.PatternLayout
log4j.appender.engin.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss.sss},%c,%-p,%m %n
########################
# 非类名logger
########################
log4j.logger.error=ERROR,errorAppender
########################
# appender errorAppender
########################
log4j.appender.errorAppender=org.apache.log4j.DailyRollingFileAppender
log4j.appender.errorAppender.File=/data/logs/blog-error.log
log4j.appender.errorAppender.Append=true
log4j.appender.errorAppender.MaxFileSize=500MB
log4j.appender.errorAppender.MaxBackupIndex=7
log4j.appender.errorAppender.Encoding=UTF-8
log4j.appender.errorAppender.layout=org.apache.log4j.PatternLayout
log4j.appender.errorAppender.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss.sss},%c,%-p,%m %n
```

### 打印


```java
Logger logger = LoggerFactory.getLogger(AClass.class);
logger.error("this is an error log");
// 获取名称为error的logger
Logger loggerError = LoggerFactory.getLogger("error");
loggerError.error("this is an error log");
```

### 从Java代码获取logger的配置信息

```java

org.apache.log4j.Logger loggerError = org.apache.log4j.Logger.getLogger("error");
Appender appender = loggerError.getAppender("errorAppender");
DailyRollingFileAppender errorAppender = (DailyRollingFileAppender) appender;
String fileName = errorAppender.getFile();
String datePattern = errorAppender.getDatePattern();
```


## 每个小时打印一个文件

### 配置文件的方式

```properties
log4j.logger.redo=ERROR,redoLogger
# appender for redo logger
log4j.appender.redoLogger=org.apache.log4j.DailyRollingFileAppender
log4j.appender.redoLogger.File=/data/logs/my-redo.log
log4j.appender.redoLogger.Append=true
log4j.appender.redoLogger.MaxFileSize=500MB
log4j.appender.redoLogger.MaxBackupIndex=7
log4j.appender.redoLogger.Encoding=UTF-8
log4j.appender.redoLogger.layout=org.apache.log4j.PatternLayout
log4j.appender.redoLogger.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss.sss},%c,%-p,%m \n
log4j.appender.redoLogger.DatePattern='.'yyyy-MM-dd-HH
```

### Java代码的方式

```java
DailyRollingFileAppender redoAppender = new DailyRollingFileAppender();
redoAppender.setDatePattern("'.'yyyy-MM-dd-HH");

Logger loggerRedo = Logger.getLogger("redo");
loggerRedo.addAppender(appender);
```

使用

```Java
public static final Logger redoLogger = LoggerFactory.getLogger("redo");
...
redoLogger.error("something is wrong");
```

下面的列表
The following list shows all the date patterns which have defined by log4j,

| name       | DatePattern           | sample                                   |
| :--------- | :-------------------- | :--------------------------------------- |
| Minutely   | `'.'yyyy-MM-dd-HH-mm` | application.log.2013-02-28-13-54         |
| Hourly     | `'.'yyyy-MM-dd-HH`    | application.log.2013-02-28-13            |
| Half-daily | `'.'yyyy-MM-dd-a`     | application.log.2013-02-28-AM    app.log.2013-02-28-PM |
| Daily      | `'.'yyyy-MM-dd`       | application.log.2013-02-28               |
| Weekly     | `'.'yyyy-ww`          | application.log.2013-07 app.log.2013-08  |
| Monthly    | `'.'yyyy-MM`          | application.log.2013-01 app.log.2013-02  |


## 自定义 writerAppender
log4j将日志输出到swing控件

### 自定义 writer

```java
public class LogWriter extends Writer {

    private LogListModel model;

    public LogWriter(LogListModel model) {
        this.model = model;
    }

    @Override
    public void write(int c) throws IOException {
        model.addElement("" + c);
    }

    @Override
    public void write(char[] cbuf) throws IOException {
        model.addElement(new String(cbuf));
    }

    @Override
    public void write(String str) throws IOException {
        model.addElement(str);
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        model.addElement(str);
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        model.addElement(new String(cbuf));
    }

    @Override
    public void flush() throws IOException {
    }

    @Override
    public void close() throws IOException {
    }
}
```

### 创建logger与Swing组件

```java
private Logger loggerA = LoggerFactory.getLogger("A1");
private LogListModel logListModel;

logListModel = new LogListModel();
JList ml = new JList(logListModel);

WriterAppender writeappender = new WriterAppender(new SimpleLayout(), new LogWriter(logListModel));
writeappender.setName("A1");
writeappender.setImmediateFlush(true);
Logger.getRootLogger().addAppender(writeappender);
Logger.getRootLogger().setLevel(Level.INFO);
```








-

1. [How to rotate log files based on time rather than size in Log4j?
  ](https://stackoverflow.com/questions/1711423/how-to-rotate-log-files-based-on-time-rather-than-size-in-log4j)
2. [Log4j详细介绍(五)----输出地Appender](http://www.cnblogs.com/ArtsCrafts/archive/2013/06/07/log4j5.html)
