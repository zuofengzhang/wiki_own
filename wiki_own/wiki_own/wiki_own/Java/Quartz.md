---
title: Quartz应用与原理
layout: post
date: 2016-08-29 14:58:00
category: Java
tags:
 - Java

share: true
comments: true
---



使用

配置

集群

原理



Quartz是一个大名鼎鼎的Java版开源定时调度器，功能强悍，使用方便。

# 核心概念

Quartz的原理不是很复杂，只要搞明白几个概念，然后知道如何去启动和关闭一个调度程序即可。

1. Job

   表示一个工作，要执行的具体内容。此接口中只有一个方法
   void execute(JobExecutionContext context)

2. JobDetail

   JobDetail表示一个具体的可执行的调度程序，Job是这个可执行程调度程序所要执行的内容，另外JobDetail还包含了这个任务调度的方案和策略。

3. Trigger代表一个调度参数的配置，什么时候去调。

4. Scheduler代表一个调度容器，一个调度容器中可以注册多个JobDetail和Trigger。当Trigger与JobDetail组合，就可以被Scheduler容器调度了。




# 应用

```java
public class SimpleQuartzJob implements Job {

    public SimpleQuartzJob() {
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("In SimpleQuartzJob - executing its JOB at "
                + new Date() + " by " + context.getTrigger().getDescription());
    }
}
```



## 普通

```java
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;

/**
* quartz定时器测试
*/
public class MyJob implements Job {
        public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
                System.out.println(new Date() + ": doing something...");
        }
}
```

调用的代码

```java
//1、创建JobDetial对象
JobDetail jobDetail = new JobDetail();
//设置工作项
jobDetail.setJobClass(MyJob.class);
jobDetail.setName("MyJob_1");
jobDetail.setGroup("JobGroup_1");

//2、创建Trigger对象
SimpleTrigger strigger = new SimpleTrigger();
strigger.setName("Trigger_1");
strigger.setGroup("Trigger_Group_1");
strigger.setStartTime(new Date());
//设置重复停止时间，并销毁该Trigger对象
java.util.Calendar c = java.util.Calendar.getInstance();
c.setTimeInMillis(System.currentTimeMillis() + 1000 * 1L);
strigger.setEndTime(c.getTime());
strigger.setFireInstanceId("Trigger_1_id_001");
//设置重复间隔时间
strigger.setRepeatInterval(1000 * 1L);
//设置重复执行次数
strigger.setRepeatCount(3);

//3、创建Scheduler对象，并配置JobDetail和Trigger对象
SchedulerFactory sf = new StdSchedulerFactory();
Scheduler scheduler = null;
try {
        scheduler = sf.getScheduler();
        scheduler.scheduleJob(jobDetail, strigger);
        //4、并执行启动、关闭等操作
        scheduler.start();

} catch (SchedulerException e) {
        e.printStackTrace();
}
// try {
//   //关闭调度器
//   scheduler.shutdown(true);
// } catch (SchedulerException e) {
//    e.printStackTrace();
// }
```

另外一种创建的方法

```java
try {
  // 创建调度器
  Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
  // Create a JobDetail for the Job
  JobDetail jobDetail = newJob(SimpleQuartzJob.class).withIdentity("myJob", "group1").build();

  // Date runTime = evenMinuteDate(new Date());

  Trigger trigger = newTrigger().withIdentity("myTrigger", "group1").startNow()
    .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(5).withRepeatCount(10)).build();

  // Trigger trigger = newTrigger().withIdentity("myTrigger","group1").startAt(RunTime).build();

  scheduler.scheduleJob(jobDetail, trigger);

  scheduler.start();

  //            Thread.sleep(60000);
  System.out.println("goto shutdown");
  scheduler.shutdown(true);
} catch (SchedulerException e) {
  e.printStackTrace();
  //        } catch (InterruptedException e) {
  e.printStackTrace();
}
```



## Crontab



```java
// 创建调度器
try {
  Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
  JobDetail jobDetail = newJob(SimpleQuartzJob.class).withIdentity("myJob2", "group1").build();

  Trigger trigger = newTrigger().withIdentity("myTrigger3", "group1").withSchedule(CronScheduleBuilder.cronSchedule("0/1 * * * * ?")).build();

  scheduler.scheduleJob(jobDetail, trigger);
  if (scheduler.isStarted()) {
    System.out.println("has started!");
  }
  scheduler.start();
  Thread.sleep(60000);
  scheduler.shutdown();

} catch (SchedulerException e) {
  e.printStackTrace();
} catch (InterruptedException e) {
  e.printStackTrace();
}
```





## 配置文件

## 与Spring结合






# 原理

 

通过研读Quartz的源代码，和本实例，终于悟出了Quartz的工作原理。

 1. scheduler是一个计划调度器容器（总部），容器里面可以盛放众多的JobDetail和trigger，当容器启动后，里面的每个JobDetail都会根据trigger按部就班自动去执行。 
 2. JobDetail是一个可执行的工作，它本身可能是有状态的。 
 3. Trigger代表一个调度参数的配置，什么时候去调。 
 4. 当JobDetail和Trigger在scheduler容器上注册后，形成了装配好的作业（JobDetail和Trigger所组成的一对儿），就可以伴随容器启动而调度执行了。 
 5. scheduler是个容器，容器中有一个线程池，用来并行调度执行每个作业，这样可以提高容器效率。
 6. 将上述的结构用一个图来表示，如下：

![][p-quartz-central]



----

[参考文献]:

1. [深入解读Quartz的原理][a-sample-01]


[a-sample-01]: http://lavasoft.blog.51cto.com/62575/181907/

[p-quartz-central]: /images/java/quartz-central.png