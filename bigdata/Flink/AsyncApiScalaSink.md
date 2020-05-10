---
title: "Flink AsyncApiScalaSink"
layout: post
date: 2016-08-10 14:58:00
category: bigdata
tags:
 - Java
 - Flink

share: true
comments: true
---

# Flink AsyncApiScalaSink


andrew建议：
- onCompeleted没有把IOException暴露给用户
- 链接配置之类的参数如何传递进去
- 反压机制如何做？sink经常遇到的问题，下游抖动会直接导致数据丢失
- 反压 限流 重试
- 支持batch，条数控制
- checkpoint时，需要batch立即flush出去

## Http Client

```xml
<dependency>
    <groupId>org.apache.httpcomponents</groupId>
    <artifactId>httpclient</artifactId>
    <version>4.5.2</version>
    <exclusions>
        <exclusion>
            <artifactId>commons-logging</artifactId>
            <groupId>commons-logging</groupId>
        </exclusion>
    </exclusions>
</dependency>

<dependency>
    <groupId>org.apache.httpcomponents</groupId>
    <artifactId>httpcore</artifactId>
    <version>4.4.5</version>
</dependency>

<dependency>
    <groupId>org.apache.httpcomponents</groupId>
    <artifactId>httpcore-nio</artifactId>
    <version>4.4.5</version>
</dependency>

<dependency>
    <groupId>org.apache.httpcomponents</groupId>
    <artifactId>httpasyncclient</artifactId>
    <version>4.1.2</version>
    <exclusions>
        <exclusion>
            <artifactId>commons-logging</artifactId>
            <groupId>commons-logging</groupId>
        </exclusion>
    </exclusions>
</dependency>
```

## AsyncApiScalaSink

```scala
import java.io.IOException
import java.util

import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.sink.{RichSinkFunction, SinkFunction}
import org.apache.http._
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.{HttpEntityEnclosingRequestBase, HttpPost}
import org.apache.http.concurrent.FutureCallback
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy
import org.apache.http.impl.nio.client.{CloseableHttpAsyncClient, HttpAsyncClients}
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils

class AsyncApiScalaSink[E](httpInvoker: HttpInvoker[E]) extends RichSinkFunction[E] {


  var httpClient: CloseableHttpAsyncClient = _

  override def open(parameters: Configuration): Unit = {
    httpClient = HttpAsyncClients.custom.setKeepAliveStrategy(DefaultConnectionKeepAliveStrategy.INSTANCE).build
    httpClient.start()
  }

  override def invoke(value: E, context: SinkFunction.Context[_]): Unit = {
    var httpEntity: HttpEntityEnclosingRequestBase = null
    val method: String = httpInvoker.getMethod
    val url: String = httpInvoker.getUrl
    if ("GET" == method) {
    }
    else if ("POST" == method) {
      val params1: util.Map[String, String] = httpInvoker.getParams(value)
      val params: util.List[NameValuePair] = new util.ArrayList[NameValuePair]
      if (params1 != null) {
        import scala.collection.JavaConversions._
        for (entry <- params1.entrySet) {
          val key: String = entry.getKey
          val value1: String = entry.getValue
          params.add(new BasicNameValuePair(key, value1))
        }
      }
      val entity: UrlEncodedFormEntity = new UrlEncodedFormEntity(params, Consts.UTF_8)
      httpEntity = new HttpPost(url)
      httpEntity.setEntity(entity)
    }

    httpClient.execute(httpEntity, new FutureCallback[HttpResponse]() {
      override def completed(response: HttpResponse): Unit = {
        val statusLine: StatusLine = response.getStatusLine
        val httpStatusCode: Int = statusLine.getStatusCode
        var content: String = null
        try {
          val entity: HttpEntity = response.getEntity
          content = EntityUtils.toString(entity)
        } catch {
          case e: IOException =>
            System.err.print(e.getMessage)
        }
        httpInvoker.onCompleted(value, httpStatusCode, content)
      }

      override

      def failed(ex: Exception): Unit = {
        httpInvoker.onFailed(value, ex)
      }

      override

      def cancelled(): Unit = {
        httpInvoker.onCanceled(value)
      }
    })
  }

  override def close(): Unit = {
    httpClient.close()
  }
}
```

## HttpInvoker

```scala
import java.io.Serializable;
import java.util.Map;

public interface HttpInvoker<E> extends Serializable {
    Map<String, String> getParams(E e);

    String getMethod();

    String getUrl();

    void onCompleted(E value, int httpStatusCode, String content);

    void onFailed(E value, Exception ex);

    void onCanceled(E value);
}
```