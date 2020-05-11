---
title: mapReducer
date: 2017-02-26 00:00:00
layout: post
category: Distributed
tags:
 - Distributed
 - Hadoop
 - mapReducer

share: true
comments: true
---

# mapReducer 处理数据的流程

Map Reducer shuffle 

partition sorting combiner


shuffle过程

MapReducer过程

参考文献: http://www.cnblogs.com/ljy2013/articles/4435657.html

# MapReducer 实践

1. wordCount解析
2. Mapper class, CombinerClass,ReducerClass 
3. 分析 TokenCountMapper  与 LongSumReducer
4. 常用的系列化类: 实现 WriteComparable 的类

# 文件的读写


## InputFormat

- TextInputFormat 是 InputFormat 的默认实现, TextInputFormat 
返回的键是每行的字节偏移量，返回的值是该行的数据。
key: LongWritable
value: Text

- `KeyValueTextInputFormat` 使用分隔符分割每行，分隔符之前的是键，之后的是值。默认的分隔符是制表符(\T)，分离器的属性通过
`key.value.separator.in.input.line`中指定

key: Text
value: Text

- `SequenceFileInputFormat<K,V>` 用户自定义的序列化格式, 序列化文件为Hadoop专用的压缩二进制文件格式

key: K 用户定义
value: V 用户定义

- `NLineInputFormat` key为分片的偏移量，value为包含N行数据的片段，N通过属性 `mapred.line.inout.format.linespermap`中指定，默认为1
key: LongWritable
value: Text


通过 `JobConf.setInputFormat(KeyValueTextInputFormat.class)`指定

## 自定义 InputFormat

```java
public interface InputFormat<K,V> {
	/**
	* 将输入的文件分割成 numSplits 个片段
	*/
	InputSplit[] getSplits(JobConf job, int numSplits) throws IOException;
	/**
	* 
	*/
	RecordReader<K,V> getRecordReader( InputSplit split, 
		JobConf job,
		Reporter reporter) throws IOException;
}
```

上述所有的 `InputFormat` 都是 `FileInputFormat` 类的一个子类，`FileInputFormat`默认实现了 `InputFormat` 接口，且实现了 `getSplits` 方法,把输入的数据粗略的划分为一组分片, 每个分片的大小必须大于`mapred.min.split.size`个字节,且小于文件系统的块, 在实际情况下,一个分片的大小总是一个块的大小,在HDFS中默认为64MB; 而 `getRecordReader`为抽象方法,  在自定义实现`InputFormat`时, 可以通过继承 `FileInputFormat` , 自定义 `getRecordReader` 方法.

- `isSplitable` 方法

- `RecordReader`

- 编写 InputFormat 实例


# OutputFormat












---
【参考文献】

1. [ref-title][ref-01]

[ref-01]: ref-url  "ref-alt-title"