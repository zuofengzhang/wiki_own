---
layout: page
title: "Note 2016 - HTML JavaScript CSS AJAX"
date: 2016-03-10 00:00:00
category: note
tag:

share: false
comments: false
---

[返回](/note2016)

## Ajax应用和传统的web应用有何不同？
在传统的javascript中，如果想得到服务器端数据库或文件上的信息，或者发送客户端信息到服务器，需要建立一个HTML form然后Post或者get提交数据到服务端。用户需要点击submit 来发送或者接受数据信息，然后等待服务器响应请求，页面重写加载，因为服务器每次都要返回一个新的页面，所以传统的web应用有可能会很慢而且用户交互不友好。

使用ajax就可以使javascript通过XMLHttpRequest对象直接与服务器进行交互。通过HTTPRequest，一个web页面可以发送一个请求到web服务器并且接受web服务器返回的信息(不需要加载任何界面)，展示给用户的还是同一个页面，用户感觉不到页面刷新，也看不到Javascript后台进行的发送请求和接受的响应。

## javascript的作用？
表单验证、网页特效、网页游戏

## 为什么要有jquery？
1. jQuery是JavaScript的轻量级框架，对JavaScript进行了很好的封装，很多复杂的JavaScript代码不用写了，直接调用就可以，使开发简单、高效。
2. jQuery强大的选择器封装了DOM，操作网页元素更简单了。
3. 在大型JavaScript框架中，jQuery对性能的理解最好，大小不超过30KB。
4. 完善的ajax有着出色的浏览器兼容性，任何浏览器使用ajax都能兼容。
5. 基于jQuery开发的插件目前已经有大约数千个。开发者可使用插件来进行表单确认、图表种类、字段提示、动画、进度条等任务。

## jQuery选择器有多少种？
基本：
`$("#myELement")`  ID选择器
`$("div")`           标签选择器
`$(".myClass")`     类选择器
`$("*")`            通配符选择器
层级选择器
过滤选择器
子元素选择器

## jquery选择器有哪些优势？
简单的写法(‘#id’)用来代替document.getElementById()。
支持css选择器。
完善的处理机制，就算写错了Id也不会报错。

## 你是如何使用jquery中的ajax的？
如果是常规的ajax程序的话，使用load()、$.get()、$.post()，一般我会使用的是$.post()方法，如果需要设定，beforeSend（提交前回调函数），error（失败后处理）,success(成功后处理)，及complete（请求完成后处理）毁掉函数等，这个时候我会使用$.ajax()

## jquery中的$.get和$.post请求区别？
1）$.get方法使用get方法来进行一步请求，$.post是使用post方法来进行请求。
2）get请求会讲参数跟在url后进行传递，而post请求则是作为Http消息的实体.内容发送给web服务器的，这种传递是对用户不可见的。
3）get方式传输的数据大小不能超过2kb而post请求要大的多
4）get方式请求的数据会被浏览器缓存起来，因此有安全问题

## jquery中如何操作样式的？
addClass()来追加样式，removeClass()来删除样式，toggle()来切换样式。

## 如何设置和获取HTML和文本的值？
Html()方法，类似于innerHTML属性，可以用来读取或者设置某个元素中的HTML内容，text()类似于innerText属性，可以用来读取或这是某个元素的文本内容，val()可以用来设置和获取元素的值。

## Jquery能做些什么？
1）获取页面元素
2）修改页面的外观
3）修改页面的内容
4）响应页面的操作
5）为页面添加动态效果
6）无需刷新页面，即可从服务器获取信息
7）简化常见的javascript的任务

## 在ajax中data主要有哪几种？
html拼接、json数组、form表单经过serialize()序列化的

## jQuery中ajax由几部分组成？
1）请求url
2）请求参数
3）请求类型，get或post
4）回调函数
5）传输类型，html或json等
