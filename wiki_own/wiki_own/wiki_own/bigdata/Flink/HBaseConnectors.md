---
title: "Flink:HBaseConnectors"
layout: post
date: 2019-08-10 14:58:00
category: bigdata
tags:
 - Java
 - Flink
 - HBase

share: true
comments: true
---

# Flink HBase connectors

## flighting

奇怪，没有依赖HBase-Client，而是依赖了HBase-server

```xml
<dependency>
	<groupId>org.apache.hbase</groupId>
	<artifactId>hbase-hadoop2-compat</artifactId>
	<version>${hbase.version}</version>
	<scope>test</scope>
	<type>test-jar</type>
</dependency>
```

```xml
<dependency>
	<groupId>org.apache.hbase</groupId>
	<artifactId>hbase-server</artifactId>
	<version>${hbase.version}</version>
</dependency>
```

### 添加文件到ClassLoader的classpath

```java
// Get the classloader actually used by HBaseConfiguration
ClassLoader classLoader = HBaseConfiguration.create().getClassLoader();
if (!(classLoader instanceof URLClassLoader)) {
	fail("We should get a URLClassLoader");
}

// Make the addURL method accessible
Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
method.setAccessible(true);

// Add the directory where we put the hbase-site.xml to the classpath
method.invoke(classLoader, directory.toURI().toURL());
```

 






