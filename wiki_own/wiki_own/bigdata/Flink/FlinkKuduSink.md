---
title: "【草稿】FlinkKuduSink"
layout: post
date: 2010-08-10 14:58:00
category: Flink
tags:
 - Java
 - Metric

share: true
comments: true
---


# FlinkKuduSink

```java

org.apache.kudu.client.NonRecoverableException: Couldn't find a valid master in (9.7.185.196:7050). Exceptions received: [org.apache.kudu.client.RpcRemoteException: [peer master-] server sent error Service unavailable: service kudu.master.MasterService not registered on TabletServer]
	at org.apache.kudu.client.KuduException.transformException(KuduException.java:110)
	at org.apache.kudu.client.KuduClient.joinAndHandleException(KuduClient.java:413)
	at org.apache.kudu.client.KuduClient.tableExists(KuduClient.java:229)
	at org.apache.flink.connectors.kudu.connector.writer.KuduWriter.obtainTable(KuduWriter.java:74)
	at org.apache.flink.connectors.kudu.connector.writer.KuduWriter.<init>(KuduWriter.java:59)
	at org.apache.flink.connectors.kudu.streaming.KuduSink.open(KuduSink.java:62)
	at org.apache.flink.api.common.functions.util.FunctionUtils.openFunction(FunctionUtils.java:36)
	at org.apache.flink.streaming.api.operators.AbstractUdfStreamOperator.open(AbstractUdfStreamOperator.java:102)
	at org.apache.flink.streaming.api.operators.StreamSink.open(StreamSink.java:48)
	at org.apache.flink.streaming.runtime.tasks.StreamTask.openAllOperators(StreamTask.java:426)
	at org.apache.flink.streaming.runtime.tasks.StreamTask.invoke(StreamTask.java:292)
	at org.apache.flink.runtime.taskmanager.Task.run(Task.java:726)
	at java.lang.Thread.run(Thread.java:748)
	Suppressed: org.apache.kudu.client.KuduException$OriginalException: Original asynchronous stack trace
		at org.apache.kudu.client.ConnectToCluster.incrementCountAndCheckExhausted(ConnectToCluster.java:244)
		at org.apache.kudu.client.ConnectToCluster.access$100(ConnectToCluster.java:49)
		at org.apache.kudu.client.ConnectToCluster$ConnectToMasterErrCB.call(ConnectToCluster.java:363)
		at org.apache.kudu.client.ConnectToCluster$ConnectToMasterErrCB.call(ConnectToCluster.java:352)
		at com.stumbleupon.async.Deferred.doCall(Deferred.java:1280)
		at com.stumbleupon.async.Deferred.runCallbacks(Deferred.java:1259)
		at com.stumbleupon.async.Deferred.handleContinuation(Deferred.java:1315)
		at com.stumbleupon.async.Deferred.doCall(Deferred.java:1286)
		at com.stumbleupon.async.Deferred.runCallbacks(Deferred.java:1259)
		at com.stumbleupon.async.Deferred.callback(Deferred.java:1002)
		at org.apache.kudu.client.KuduRpc.handleCallback(KuduRpc.java:275)
		at org.apache.kudu.client.KuduRpc.errback(KuduRpc.java:329)
		at org.apache.kudu.client.RpcProxy.responseReceived(RpcProxy.java:247)
```

```java
org.apache.kudu.client.NonRecoverableException: must specify at least one key column
	at org.apache.kudu.client.KuduException.transformException(KuduException.java:110)
	at org.apache.kudu.client.KuduClient.joinAndHandleException(KuduClient.java:413)
	at org.apache.kudu.client.KuduClient.createTable(KuduClient.java:118)
	at org.apache.flink.connectors.kudu.connector.writer.KuduWriter.obtainTable(KuduWriter.java:78)
	at org.apache.flink.connectors.kudu.connector.writer.KuduWriter.<init>(KuduWriter.java:59)
	at org.apache.flink.connectors.kudu.streaming.KuduSink.open(KuduSink.java:62)
	at org.apache.flink.api.common.functions.util.FunctionUtils.openFunction(FunctionUtils.java:36)
	at org.apache.flink.streaming.api.operators.AbstractUdfStreamOperator.open(AbstractUdfStreamOperator.java:102)
	at org.apache.flink.streaming.api.operators.StreamSink.open(StreamSink.java:48)
	at org.apache.flink.streaming.runtime.tasks.StreamTask.openAllOperators(StreamTask.java:426)
	at org.apache.flink.streaming.runtime.tasks.StreamTask.invoke(StreamTask.java:292)
	at org.apache.flink.runtime.taskmanager.Task.run(Task.java:726)
	at java.lang.Thread.run(Thread.java:748)
	Suppressed: org.apache.kudu.client.KuduException$OriginalException: Original asynchronous stack trace
		at org.apache.kudu.client.RpcProxy.dispatchMasterError(RpcProxy.java:386)
		at org.apache.kudu.client.RpcProxy.responseReceived(RpcProxy.java:279)
		at org.apache.kudu.client.RpcProxy.access$000(RpcProxy.java:59)
		at org.apache.kudu.client.RpcProxy$1.call(RpcProxy.java:149)
		at org.apache.kudu.client.RpcProxy$1.call(RpcProxy.java:145)
		at org.apache.kudu.client.Connection.messageReceived(Connection.java:390)
		at org.apache.kudu.shaded.org.jboss.netty.channel.SimpleChannelUpstreamHandler.handleUpstream(SimpleChannelUpstreamHandler.java:70)
		at org.apache.kudu.client.Connection.handleUpstream(Connection.java:238)
		at org.apache.kudu.shaded.org.jboss.netty.channel.DefaultChannelPipeline.sendUpstream(DefaultChannelPipeline.java:564)
		at org.apache.kudu.shaded.org.jboss.netty.channel.DefaultChannelPipeline$DefaultChannelHandlerContext.sendUpstream(DefaultChannelPipeline.java:791)
		at org.apache.kudu.shaded.org.jboss.netty.channel.Channels.fireMessageReceived(Channels.java:296)
		at org.apache.kudu.shaded.org.jboss.netty.handler.codec.oneone.OneToOneDecoder.handleUpstream(OneToOneDecoder.java:70)
		at org.apache.kudu.shaded.org.jboss.netty.channel.DefaultChannelPipeline.sendUpstream(DefaultChannelPipeline.java:564)
		at org.apache.kudu.shaded.org.jboss.netty.channel.DefaultChannelPipeline$DefaultChannelHandlerContext.sendUpstream(DefaultChannelPipeline.java:791)
		at org.apache.kudu.shaded.org.jboss.netty.channel.Channels.fireMessageReceived(Channels.java:296)
		at org.apache.kudu.shaded.org.jboss.netty.handler.codec.frame.FrameDecoder.unfoldAndFireMessageReceived(FrameDecoder.java:462)
		at org.apache.kudu.shaded.org.jboss.netty.handler.codec.frame.FrameDecoder.callDecode(FrameDecoder.java:443)
		at org.apache.kudu.shaded.org.jboss.netty.handler.codec.frame.FrameDecoder.messageReceived(FrameDecoder.java:303)
		at org.apache.kudu.shaded.org.jboss.netty.channel.SimpleChannelUpstreamHandler.handleUpstream(SimpleChannelUpstreamHandler.java:70)
		at org.apache.kudu.shaded.org.jboss.netty.channel.DefaultChannelPipeline.sendUpstream(DefaultChannelPipeline.java:564)
		at org.apache.kudu.shaded.org.jboss.netty.channel.DefaultChannelPipeline$DefaultChannelHandlerContext.sendUpstream(DefaultChannelPipeline.java:791)
		at org.apache.kudu.shaded.org.jboss.netty.channel.Channels.fireMessageReceived(Channels.java:296)
		at org.apache.kudu.shaded.org.jboss.netty.handler.codec.frame.FrameDecoder.unfoldAndFireMessageReceived(FrameDecoder.java:462)
		at org.apache.kudu.shaded.org.jboss.netty.handler.codec.frame.FrameDecoder.callDecode(FrameDecoder.java:443)
		at org.apache.kudu.shaded.org.jboss.netty.handler.codec.frame.FrameDecoder.messageReceived(FrameDecoder.java:303)
		at org.apache.kudu.shaded.org.jboss.netty.channel.SimpleChannelUpstreamHandler.handleUpstream(SimpleChannelUpstreamHandler.java:70)
		at org.apache.kudu.shaded.org.jboss.netty.channel.DefaultChannelPipeline.sendUpstream(DefaultChannelPipeline.java:564)
		at org.apache.kudu.shaded.org.jboss.netty.channel.DefaultChannelPipeline.sendUpstream(DefaultChannelPipeline.java:559)
		at org.apache.kudu.shaded.org.jboss.netty.channel.Channels.fireMessageReceived(Channels.java:268)
		at org.apache.kudu.shaded.org.jboss.netty.channel.Channels.fireMessageReceived(Channels.java:255)
		at org.apache.kudu.shaded.org.jboss.netty.channel.socket.nio.NioWorker.read(NioWorker.java:88)
		at org.apache.kudu.shaded.org.jboss.netty.channel.socket.nio.AbstractNioWorker.process(AbstractNioWorker.java:108)
		at org.apache.kudu.shaded.org.jboss.netty.channel.socket.nio.AbstractNioSelector.run(AbstractNioSelector.java:337)
		at org.apache.kudu.shaded.org.jboss.netty.channel.socket.nio.AbstractNioWorker.run(AbstractNioWorker.java:89)
		at org.apache.kudu.shaded.org.jboss.netty.channel.socket.nio.NioWorker.run(NioWorker.java:178)
		at org.apache.kudu.shaded.org.jboss.netty.util.ThreadRenamingRunnable.run(ThreadRenamingRunnable.java:108)
		at org.apache.kudu.shaded.org.jboss.netty.util.internal.DeadLockProofWorker$1.run(DeadLockProofWorker.java:42)
		at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
		at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
		... 1 more

Rows per page:
5
1 - 1 of 1
```