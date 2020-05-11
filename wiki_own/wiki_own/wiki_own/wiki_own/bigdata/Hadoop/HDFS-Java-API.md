---
title: HDFS Java API
date: 2017-02-25 00:00:00
layout: post
category: Distributed
tags:
 - Hadoop
 - HDFS

share: true
comments: true
---

Hadoop是采用Java实现的，所有的命令和操作全部采用Java完成。
HDFS: Hadoop Distributed File System 是Hadoop提供的分布式文件系统。

本文介绍HDFS的Java API 

```xml
<dependency>
    <groupId>org.apache.hadoop</groupId>
    <artifactId>hadoop-core</artifactId>
    <version>1.2.1</version>
</dependency>
```

首先，看一个例子:

# putMerge: 本地文件合并并保存到HDFS

```java
/**
 * copy files in <code>srcDir</code> 
 * and merge to <code>objectFileName</code> in HDFS
 *
 * @param srcDir    本地文件夹
 * @param objectFileName HDFS文件名
 */
private static void putMerge(String srcDir, String objectFileName) {
    System.out.println("srcDir:" + srcDir+"\t objDir:" + objectFileName);
    // 读取HDFS的默认配置, 配置文件主要包括: 
    // core-default.xml, core-site.xml, mapred-default.xml, mapred-site.xml, yarn-default.xml, yarn-site.xml, hdfs-default.xml, hdfs-site.xml
    Configuration conf = new Configuration();
    FileSystem hdfs = null;
    LocalFileSystem local = null;

    try {
        hdfs = FileSystem.get(conf);// 获取HDFS配置的FileSystem
        local = FileSystem.getLocal(conf);// 获取本地配置FileSystem
        Path inputDir = new Path(srcDir); // 设定输入目录
        System.out.println("inputDir: " + inputDir);
        System.out.println("local.homeDirectory: " + local.getHomeDirectory());
        System.out.println("local.workingDirectory: " + local.getWorkingDirectory());
        System.out.println("local.uri: " + local.getUri());
        System.out.println("local.conf: " + local.getConf());

        FileStatus[] inputFiles = local.listStatus(inputDir);
        System.out.println("inputFiles: " + inputFiles);
        for (FileStatus inputFile : inputFiles) {
            System.out.println("inputFile: " + inputFile);
        }
        if (inputFiles.length == 0) {
            System.err.println("input file path is empty!");
            System.exit(1);
        }
        Path hdfsFile = new Path(objectFileName); // 设定输出文件名称
        FSDataOutputStream out = hdfs.create(hdfsFile, new Progressable() {
            @Override
            public void progress() {
                System.out.print("*");
            }
        });
        for (FileStatus inputFile : inputFiles) {
            System.out.println("inputFile.path.name: " + inputFile.getPath().getName());
            FSDataInputStream in = local.open(inputFile.getPath());
            byte buffer[] = new byte[256];
            int bytesRead = 0;
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            in.close();
            System.out.println();
        }
        out.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
    System.out.println();
}
```

执行:

1. 打包

```shell 
mvn package
```

2. 复制到container

```shell 
docker cp ~/IdeaProjects/MyTest/out/artifacts/HadoopTest_jar/HadoopTest.jar hadoop0:/root/putMerge.jar
```

3. 执行

```shell 
hadoop jar putMerge.jar en002 en002.txt
```

执行结果

```shell
version 1
args:en002
args:en002.txt
srcDir:en002
objDir:en002.txt
inputDir: en002
local.homeDirectory: file:/root
local.workingDirectory: file:/root
local.uri: file:///
local.conf: Configuration: core-default.xml, core-site.xml, mapred-default.xml, mapred-site.xml, yarn-default.xml, yarn-site.xml, hdfs-default.xml, hdfs-site.xml
inputFiles: [Lorg.apache.hadoop.fs.FileStatus;@4073c6c9
inputFile: DeprecatedRawLocalFileStatus{path=file:/root/en002/shengjing.txt; isDirectory=false; length=4467663; replication=1; blocksize=33554432; modification_time=1495535613000; access_time=0; owner=; group=; permission=rw-rw-rw-; isSymlink=false}
inputFile: DeprecatedRawLocalFileStatus{path=file:/root/en002/at.txt; isDirectory=false; length=829203; replication=1; blocksize=33554432; modification_time=1495535613000; access_time=0; owner=; group=; permission=rw-rw-rw-; isSymlink=false}
inputFile: DeprecatedRawLocalFileStatus{path=file:/root/en002/abc.txt; isDirectory=false; length=0; replication=1; blocksize=33554432; modification_time=1495543277000; access_time=0; owner=; group=; permission=rw-rw-rw-; isSymlink=false}
inputFile: DeprecatedRawLocalFileStatus{path=file:/root/en002/a.txt; isDirectory=false; length=18516; replication=1; blocksize=33554432; modification_time=1495535613000; access_time=0; owner=; group=; permission=rw-rw-rw-; isSymlink=false}
inputFile: DeprecatedRawLocalFileStatus{path=file:/root/en002/av.txt; isDirectory=false; length=189407; replication=1; blocksize=33554432; modification_time=1495535613000; access_time=0; owner=; group=; permission=rw-rw-rw-; isSymlink=false}
inputFile: DeprecatedRawLocalFileStatus{path=file:/root/en002/David.txt; isDirectory=false; length=1519616; replication=1; blocksize=33554432; modification_time=1495535613000; access_time=0; owner=; group=; permission=rw-rw-rw-; isSymlink=false}
inputFile: DeprecatedRawLocalFileStatus{path=file:/root/en002/Oliver.txt; isDirectory=false; length=981553; replication=1; blocksize=33554432; modification_time=1495535613000; access_time=0; owner=; group=; permission=rw-rw-rw-; isSymlink=false}
inputFile: DeprecatedRawLocalFileStatus{path=file:/root/en002/Jane.txt; isDirectory=false; length=1114997; replication=1; blocksize=33554432; modification_time=1495535613000; access_time=0; owner=; group=; permission=rw-rw-rw-; isSymlink=false}
inputFile: DeprecatedRawLocalFileStatus{path=file:/root/en002/Romeo.txt; isDirectory=false; length=145397; replication=1; blocksize=33554432; modification_time=1495535613000; access_time=0; owner=; group=; permission=rw-rw-rw-; isSymlink=false}
inputFile.path.name: shengjing.txt

inputFile.path.name: at.txt
*******
inputFile.path.name: abc.txt

inputFile.path.name: a.txt

inputFile.path.name: av.txt
*******
inputFile.path.name: David.txt
******************************************************************
inputFile.path.name: Oliver.txt
***************************
inputFile.path.name: Jane.txt
**********************************
inputFile.path.name: Romeo.txt
**
**
```

可能出现的问题:

1. `LocalFileSystem.listStatus`返回为 `empty`, 可能的原因有:
	- Hadoop没有权限读取目录或目录中的文件
	- 本地目录中的文件是中文的，而系统不支持显示中文


# FileSystem

[DOCS-FileSystem](http://hadoop.apache.org/docs/stable/api/org/apache/hadoop/fs/FileSystem.html)
FileSystem 是Hadoop提供的操作本地文件和HDFS中文件的API，可以实现CRUD等操作

```java
/**
* Append to an existing file.
*/
public FSDataOutputStream append(Path f) throws IOException
/**
* Concat existing files together.
*/
public void concat(Path trg,Path[] psrcs)
            throws IOException

/** 
* Renames Path src to Path dst. 
* Can take place on local fs or remote DFS.
*/
public abstract boolean rename(Path src, Path dst) throws IOException




```









## 创建目录


```java
Configuration conf = new Configuration();  
FileSystem fs = FileSystem.get(conf);  
Path path = new Path("/user/hadoop/data/20130709");  
fs.create(path);  
fs.close();  
```



## 删除目录

```java
Configuration conf = new Configuration();  
FileSystem fs = FileSystem.get(conf);  
Path path = new Path("/user/hadoop/data/20130710");  
fs.delete(path);  
fs.close(); 
```

## 写文件

```java
Configuration conf = new Configuration();  
FileSystem fs = FileSystem.get(conf);  
Path path = new Path("/user/hadoop/data/write.txt");  
FSDataOutputStream out = fs.create(path);  
out.writeUTF("da jia hao,cai shi zhen de hao!");  
fs.close();
```

## 读文件

```java
Configuration conf = new Configuration();  
FileSystem fs = FileSystem.get(conf);  
Path path = new Path("/user/hadoop/data/write.txt");  
  
if(fs.exists(path)){  
    FSDataInputStream is = fs.open(path);  
    FileStatus status = fs.getFileStatus(path);  
    // status.getLen() 返回文件的长度 字节长度
    byte[] buffer = new byte[Integer.parseInt(String.valueOf(status.getLen()))];  
    is.readFully(0, buffer);  
    is.close();  
    fs.close();  
    System.out.println(buffer.toString());  
}  
```
 


## 上传本地文件到HDFS

```java
Configuration conf = new Configuration();  
FileSystem fs = FileSystem.get(conf);  
Path src = new Path("/home/hadoop/word.txt");  
Path dst = new Path("/user/hadoop/data/");  
// FileSystem可以直接将本地文件复制到HDFS的制定目录
fs.copyFromLocalFile(src, dst);  
fs.close(); 
```

## 删除文件

```java
Configuration conf = new Configuration();  
FileSystem fs = FileSystem.get(conf);  
// 删除文件和目录均是通过FileSystem.delete(Path path)
Path path = new Path("/user/hadoop/data/word.txt");  
fs.delete(path);  
fs.close();
```


## 获取给定目录下的所有子目录以及子文件
```java
Configuration conf = new Configuration(); 
FileSystem fs = FileSystem.get(conf);  
Path path = new Path("/user/hadoop");  
getFile(path,fs);  
//fs.close();   

...

public static void getFile(Path path,FileSystem fs) throws IOException {  
    FileStatus[] fileStatus = fs.listStatus(path);  
    for(int i=0;i<fileStatus.length;i++){  
        if(fileStatus[i].isDir()){  
            Path p = new Path(fileStatus[i].getPath().toString());  
            getFile(p,fs);  
        }else{  
            System.out.println(fileStatus[i].getPath().toString());  
        }  
    }  
} 

```


## 查找某个文件在HDFS集群的位置

```java
/** 
 * 查找某个文件在HDFS集群的位置 
 */  
public static void getFileLocal() throws IOException{  
    Configuration conf = new Configuration();  
    FileSystem fs = FileSystem.get(conf);  
    Path path = new Path("/user/hadoop/data/write.txt");  
      
    FileStatus status = fs.getFileStatus(path);  
    BlockLocation[] locations = fs.getFileBlockLocations(status, 0, status.getLen());  
      
    int length = locations.length;  
    for(int i=0;i<length;i++){  
        String[] hosts = locations[i].getHosts();  
        System.out.println("block_" + i + "_location:" + hosts[i]);  
    }  
}  
```


## HDFS集群上所有节点名称信息

```java
/** 
 * HDFS集群上所有节点名称信息 
 */  
public static void getHDFSNode() throws IOException{  
    Configuration conf = new Configuration();  
    FileSystem fs = FileSystem.get(conf);  

    DistributedFileSystem  dfs = (DistributedFileSystem)fs;  
    DatanodeInfo[] dataNodeStats = dfs.getDataNodeStats();  
      
    for(int i=0;i<dataNodeStats.length;i++){  
        System.out.println("DataNode_" + i + "_Node:" + dataNodeStats[i].getHostName());  
    }  
      
}  
```

# 读写

`FSDataInputStream` 与 `FSDataOutputStream`

`FSDataInputStream` 扩展了 `DataInputStream` 以支持随机读

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













---
【参考文献】

1. [][ref-01]

[ref-01]: ref-url  "ref-alt-title"

