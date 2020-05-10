---
title: "Flink状态管理"
layout: post
date: 2019-08-10 14:58:00
category: bigdata
tags:
 - Java
 - Flink

share: true
comments: true
---

# state-management



## org.apache.flink.streaming.api.checkpoint.CheckpointedFunction

- CheckpointedFunction是stateful transformation functions的核心接口，用于跨stream维护state
    - snapshotState 在checkpoint的时候会被调用，用于snapshot state，通常用于flush、commit、synchronize外部系统
    - initializeState 在parallel function初始化的时候(**第一次初始化或者从前一次checkpoint recover的时候**)被调用，通常用来初始化state，以及处理state recovery的逻辑

从checkpoint中恢复数据时，需要判断snapshot当前的情况，


FunctionSnapshotContext实现了ManagedSnapshotContext, 父类中的方法: `getCheckpointId`,`getCheckpointTimestamp`
FunctionInitializationContext实现了ManagedInitializationContext接口, 实现了`isRestored`、`getOperatorStateStore`、`getKeyedStateStore`方法


在初始化容器之后，我们使用上下文的`isrestore()`方法检查失败后是否正在恢复。如果是true，即正在恢复，则应用恢复逻辑。

> 样例: HBase写入OutPutFormat

```java
 class PortraitOutputFormat extends RichOutputFormat<EventItem> implements CheckpointedFunction {
    // 输出阈值，批量写入的条数
    private final int threshold;
    // 维护在状态中的数据
    private transient ListState<EventItem> checkpointState;
    // 内存中的数据
    private List<EventItem> bufferedEventItem;
    // HBase客户端
    private HBaseClient hbaseClient;

    public PortraitOutputFormat(HBaseClient hbaseClient) {
        this.hbaseClient = hbaseClient;
    }
    /**
    * checkpoint时调用
    * 执行snapshot操作，将内存中的数据写入到内存
    */
    @Override
    public void snapshotState(FunctionSnapshotContext functionSnapshotContext) throws Exception {
        checkpointState.clear();
        for (EventItem eventItem : bufferedEventItem) {
            checkpointState.add(eventItem);
        }
    }
    /**
    * 创建state，判断是否存在需要恢复的状态，如果有则需要恢复到bufferedEventItem
    */
    @Override
    public void initializeState(FunctionInitializationContext context) throws Exception {
        ListStateDescriptor<EventItem> descriptor = new ListStateDescriptor<>("buf-p", EventItem.class);
        checkpointState = context.getOperatorStateStore().getListState(descriptor);
        if (context.isRestored()) {
            for (EventItem eventItem : checkpointState.get()) {
                bufferedEventItem.add(eventItem);
            }
        }
    }

    /**
    *
    */
    @Override
    public void configure(Configuration configuration) {

    }
    /**
    *
    */
    @Override
    public void open(int taskNumber, int numTasks) throws IOException {
    }
    /**
    * 将新消息写入到缓存bufferedEventItem，缓存个数大约threshold,则执行sink写入，然后清空bufferedEventItem
    */
    @Override
    public void writeRecord(EventItem value) throws IOException {
        if (value.getAttachUserId() == null) {
            return;
        }

        bufferedEventItem.add(value);
        int size = bufferedEventItem.size();

        if (size >= threshold) {
            List<Put> puts = bufferedEventItem
                    .stream()
                    .map(eventItem -> {
                        String rowKey1 = portraitDataGenerator.rowKey(eventItem);
                        Map<String, String> data = portraitDataGenerator.data(eventItem);
                        Put put = new Put(rowKey1.getBytes());
                        for (String cfc : data.keySet()) {
                            String[] cfcs = cfc.split(":");
                            String cf = cfcs[0];
                            String c = cfcs[1];
                            String dataOne = data.get(cfc);
                            put.addColumn(cf.getBytes(), c.getBytes(), dataOne.getBytes());
                        }
                        return put;
                    })
                    .collect(Collectors.toList());
            try {
                hbaseClient.putAndFlush(puts);
            } catch (IOException e) {
                e.printStackTrace();
            }
            bufferedEventItem.clear();
        }
    }

    /**
    *
    */
    @Override
    public void close() throws IOException {
        if (hTable != null) {
            hTable.flushCommits();
            hTable.close();
        }
        if (connection != null) {
            connection.close();
        }
    }
}
```





## org.apache.flink.runtime.state.CheckpointListener

一旦所有checkpoint参与者确认完全，该接口必须由想要接收提交通知的功能/操作来实现。



# TTL

1.8 自动清理原理

Apache Flink的1.6.0版本引入了State TTL功能。它使流处理应用程序的开发人员配置过期时间，并在定义时间超时（Time to Live）之后进行清理。在Flink 1.8.0中，该功能得到了扩展，包括对RocksDB和堆状态后端（FSStateBackend和MemoryStateBackend）的历史数据进行持续清理，从而实现旧条目的连续清理过程（根据TTL设置）。

RocksDB后台压缩可以过滤掉过期状态
如果你的Flink应用程序使用RocksDB作为状态后端存储，则可以启用另一个基于Flink特定压缩过滤器的清理策略。RocksDB定期运行异步压缩以合并状态更新并减少存储。Flink压缩过滤器使用TTL检查状态条目的到期时间戳，并丢弃所有过期值。

激活此功能的第一步是通过设置以下Flink配置选项来配置RocksDB状态后端：

state.backend.rocksdb.ttl.compaction.filter.enabled

配置RocksDB状态后端后，将为状态启用压缩清理策略，如以下代码示例所示：

```
StateTtlConfig ttlConfig = StateTtlConfig
    .newBuilder(Time.days(7))
    .cleanupInRocksdbCompactFilter()
    .build();
```


【参考文献】
1. [Flink Streaming状态处理（Working with State）](https://www.jianshu.com/p/6ed0ef5e2b74)