---
layout: page
title: "Note 2016 - JSP and Servlet"
date: 2016-03-10 00:00:00
category: note
tag:

share: false
comments: false
---

[返回](/note2016)



## Tomcat的优化经验
去掉对web.xml的监视
把jsp提前编辑成Servlet。
有富余物理内存的情况，加大tomcat使用的jvm的内存

## 说一说Servlet的生命周期，执行过程?
Servlet生命周期分为三个阶段：
1. 初始化阶段 ：调用init()方法
2. 响应客户请求阶段：调用service()方法
3. 终止阶段：调用destroy()方法

Servlet初始化阶段：
在下列时刻Servlet容器装载Servlet：
1. Servlet容器启动时自动装载某些Servlet，实现它只需要在web.XML文件中的<Servlet></Servlet>之间添加如下代码：
<loadon-startup>1</loadon-startup>
2. 在Servlet容器启动后，客户首次向Servlet发送请求
3. Servlet类文件被更新后，重新装载Servlet

Servlet被装载后，Servlet容器创建一个Servlet实例并且调用Servlet的init()方法进行初始化。在Servlet的整个生命周期内，init()方法只被调用一次。

Servlet工作原理：

首先简单解释一下Servlet接收和响应客户请求的过程，首先客户发送一个请求，Servlet是调用service()方法对请求进行响应的，通过源代码可见，service()方法中对请求的方式进行了匹配，选择调用doGet,doPost等这些方法，然后再进入对应的方法中调用逻辑层的方法，实现对客户的响应。在Servlet接口和GenericServlet中是没有doGet,doPost等等这些方法的，HttpServlet中定义了这些方法，但是都是返回error信息，所以，我们每次定义一个Servlet的时候，都必须实现doGet或doPost等这些方法。

每一个自定义的Servlet都必须实现Servlet的接口，Servlet接口中定义了五个方法，其中比较重要的三个方法涉及到Servlet的生命周期，分别是上文提到的init(),service(),destroy()方法。GenericServlet是一个通用的，不特定于任何协议的Servlet,它实现了Servlet接口。而HttpServlet继承于GenericServlet，因此HttpServlet也实现了Servlet接口。所以我们定义Servlet的时候只需要继承HttpServlet即可。

Servlet接口和GenericServlet是不特定于任何协议的，而HttpServlet是特定于HTTP协议的类，所以HttpServlet中实现了service()方法，并将请求ServletRequest,ServletResponse强转为HttpRequest和HttpResponse。

```java
public void service(ServletRequest req,ServletResponse res)
throws ServletException,IOException{
HttpRequest request;
HttpResponse response;
try{
req = (HttpRequest)request;
res = (HttpResponse)response;
}
catch(ClassCastException e){
throw new ServletException("non-HTTP request response");
}
service(request,response);
}
```

代码的最后调用了HTTPServlet自己的service(request,response)方法，然后根据请求去调用对应的doXXX方法，因为HttpServlet中的doXXX方法都是返回错误信息，

```java
protected void doGet(HttpServletRequest res,HttpServletResponse resp)
throws ServletException,IOException{
String protocol = req.getProtocol();
if(protocol.equals("1.1")){
resp.sendError(HttpServletResponse.SC.METHOD.NOT.ALLOWED,msg);
}esle{
resp.sendError(HttpServletResponse.SC_BAD_REQUEST,msg);
}
}
```

## HTTP请求的GET与POST方式的区别
Form中的get和post方法，在数据传输过程中分别对应了HTTP协议中的GET和POST方法。二者主要区别如下：
-	1）Get是用来从服务器上获得数据，而Post是用来向服务器上传数据；
-	2）Get将表单中数据按照variable=value的形式，添加到action所指向的URL后面，并且两者使用“?”连接，而各个变量之间使用“&”连接；Post是将表单中的数据放在form的数据体中，按照变量和值相对应的方式，传递到action所指向URL；
-	3）Get是不安全的，因为在传输过程，数据被放在请求的URL中；Post的所有操作对用户来说都是不可见的；
-	4）Get传输的数据量小，这主要是因为受URL长度限制；而Post可以传输大量的数据，所以在上传文件只能使用Post；
-	5）Get限制Form表单的数据集必须为ASCII字符，而Post支持整个ISO10646字符集；
-	6）Get是Form的默认方法。

## JSP内置对象、指令、动作


## servlet中怎么定义forward 和redirect
转发：request.getRequestDispatcher (“demo.jsp"). forward(request, response);
重定向：response.sendRedirect(“demo.jsp");

##　过滤器有哪些作用？
可以验证客户是否来自可信的网络，可以对客户提交的数据进行重新编码，可以从系统里获得配置的信息，可以过滤掉客户的某些不应该出现的词汇，可以验证用户是否登录，可以验证客户的浏览器是否支持当前的应用，可以记录系统的日志等等。

## JSP的常用指令？

```
<%@page language=”java” contenType=”text/html;charset=gb2312” session=”true” buffer=”64kb” autoFlush=”true” isThreadSafe=”true” info=”text” errorPage=”error.jsp” isErrorPage=”true” isELIgnored=”true” pageEncoding=”gb2312” import=”java.sql.*”%>
isErrorPage：是否能使用Exception对象；isELIgnored：是否忽略EL表达式；
<%@include file=”filename”%>
<%@taglib prefix=”c”uri=”http://……”%>
```
## JSP和Servlet中的请求转发分别如何实现？

JSP中的请求转发可利用forward动作实现：<jsp:forward />；
Serlvet中实现请求转发的方式为：
getServletContext().getRequestDispatcher(path).forward(req,res)。

## JSP乱码如何解决？
　　1）JSP页面乱码
　　<%@ page contentType=”text/html ; charset=utf-8”%>
　　2）表单提交中文时出现乱码
　　request.setCharacterEncoding(“utf-8”);
　　3）数据库连接出现乱码
　　是数据库连接中加入useUnicode=true&characterEncoding=utf-8;

## session 和 application的区别？
1）两者的作用范围不同：
Session对象是用户级的，而Application是应用程序级别的
一个用户一个session对象，每个用户的session对象不同，在用户所访问的网站多个页面之间共享同一个session对象
一个Web应用程序一个application对象，每个Web应用程序的application对象不同，但一个Web应用程序的多个用户之间共享同一个application对象。
两者的生命周期不同：
session对象的生命周期：用户首次访问网站创建，用户离开该网站 (不一定要关闭浏览器) 消亡。
application对象的生命周期：启动Web服务器创建，关闭Web服务器销毁。
16、jsp有哪些内置对象?作用分别是什么?
JSP共有以下9种基本内置组件
　　request：用户端请求，此请求会包含来自GET/POST请求的参数；
　  response：网页传回用户端的回应；
　  pageContext：网页的属性是在这里管理；
　  session：与请求有关的会话期；
　  application：servlet正在执行的内容；
　  out：用来传送回应的输出；
　  config：servlet的构架部件；
　  page：JSP网页本身；
　  exception：针对错误网页，未捕捉的例外

## Jsp有哪些动作?作用分别是什么?
JSP共有以下6种基本动作
jsp:include：在页面被请求的时候引入一个文件。
jsp:useBean：寻找或者实例化一个JavaBean。
jsp:setProperty：设置JavaBean的属性。
jsp:getProperty：输出某个JavaBean的属性。
jsp:forward：把请求转到一个新的页面。
jsp:plugin：根据浏览器类型为Java插件生成OBJECT或EMBED标记

## JSP中动态INCLUDE与静态INCLUDE的区别？
- 动态INCLUDE用jsp:include动作实现，`<jsp:include page=included.jsp flush=true />` 它总是会检查所含文件中的变化，适合用于包含动态页面，并且可以带参数 ，先将嵌入的jsp页面编译，然后把编译后的内容放入到主页面进行处理，编译两次。
- 静态INCLUDE用include伪码实现，使用jsp指令引用`<%@ include file=included.htm %>`，不会检查所含文件的变化，适用于包含静态页面，先将内容先包含到主页面然后在一起编译，只编译一次。

## JSP和Servlet有哪些相同点和不同点，他们之间的联系是什么？
JSP是Servlet技术的扩展，本质上是Servlet的简易方式，更强调应用的外表表达。JSP编译后是"类servlet"。Servlet和JSP最主要的不同点在于，Servlet的应用逻辑是在Java文件中，并且完全从表示层中的HTML里分离开来。而JSP的情况是Java和HTML可以组合成一个扩展名为.jsp的文件。JSP侧重于视图，Servlet主要用于控制逻辑。

## 页面传递对象的方法？
Request、session、application、cookie等

## Cookied和session区别？
- 1）cookie数据存放在客户的浏览器上，session数据放在服务器上。
- 2）cookie不是很安全，别人可以分析存放在本地的COOKIE并进行COOKIE欺骗考虑到安全应当使用session。
- 3）session会在一定时间内保存在服务器上。当访问增多，会比较占用你服务器的性能考虑到减轻服务器性能方面，应当使用COOKIE。
- 4）单个cookie保存的数据不能超过4K，很多浏览器都限制一个站点最多保存20个cookie。
