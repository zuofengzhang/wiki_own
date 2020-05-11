---
title: "Flink:Table API"
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

# flink-table-api

[官方文档翻译](https://github.com/crestofwave1/oneFlink/blob/master/doc/table/Concept%20%26%20Common%20API.md)


## Concept  & Common API
Table API和SQL集成在一个联合的API中。这个API核心概念是Table，
Table可以作为查询的输入和输出。这篇文章展示了使用Table API和SQL查询的通用结构，
如何去进行表的注册，如何去进行表的查询，并且展示如何去进行表的输出。



## 1. Structure of Table API and SQL Programs

​	所有使用批量和流式相关的Table API和SQL的程序都有以下相同模式。下面的代码实例展示了Table API和SQL程序的通用结构。

```scala
// 在批处理程序中使用ExecutionEnvironment代替StreamExecutionEnvironment
val env = StreamExecutionEnvironment.getExecutionEnvironment

// 创建TableEnvironment对象
val tableEnv = TableEnvironment.getTableEnvironment(env)

// 注册表
tableEnv.registerTable("table1", ...)           // or
tableEnv.registerTableSource("table2", ...)     // or
tableEnv.registerExternalCatalog("extCat", ...) 

// 基于Table API的查询创建表
val tapiResult = tableEnv.scan("table1").select(...)
// 从SQL查询创建表
val sqlResult  = tableEnv.sqlQuery("SELECT ... FROM table2 ...")

// 将表操作API查询到的结果表输出到TableSink，SQL查询到的结果一样如此
tapiResult.writeToSink(...)

// 执行
env.execute()
```

注意：Table API和SQL查询很容易集成并被嵌入到DataStream或者DataSet程序中。查看[将DataStream和DataSet API进行整合](https://ci.apache.org/projects/flink/flink-docs-release-1.6/dev/table/common.html#integration-with-datastream-and-dataset-api)章节
学习DataSteams和DataSets是如何转换成Table以及Table是如何转换为DataStream或DataSet



## 2. Create a TableEnvironment
TableEnvironment是Table API与SQL整合的核心概念之一，它主要有如下功能：
 - 在internal catalog注册表
 - 注册external catalog
 - 执行SQL查询
 - 注册UDF函数（user-defined function)，例如 标量, 表或聚合
 - 将DataStream或者DataSet转换为表
 - 保持ExecutionEnvironment或者StreamExecutionEnvironment的引用指向
 
一个表总是与一个特定的TableEnvironment绑定在一块，
相同的查询不同的TableEnvironment是无法通过join、union合并在一起。

创建TableEnvironment的方法通常是通过StreamExecutionEnvironment，ExecutionEnvironment对象调用其中的静态方法TableEnvironment.getTableEnvironment()，或者是TableConfig来创建。
TableConfig可以用作配置TableEnvironment或是对自定义查询优化器或者是编译过程进行优化(详情查看[查询优化](https://ci.apache.org/projects/flink/flink-docs-release-1.6/dev/table/common.html#query-optimization))

```scala
// ***************
// 流式查询
// ***************
val sEnv = StreamExecutionEnvironment.getExecutionEnvironment
// 为流式查询创建一个TableEnvironment对象
val sTableEnv = TableEnvironment.getTableEnvironment(sEnv)

// ***********
// 批量查询
// ***********
val bEnv = ExecutionEnvironment.getExecutionEnvironment
// 为批量查询创建一个TableEnvironment对象
val bTableEnv = TableEnvironment.getTableEnvironment(bEnv)
```
## Register Tables in the Catalog
TableEnvironment包含了通过名称注册表时的表的catalog信息。通常情况下有两种表，一种为输入表，
一种为输出表。输入表主要是在使用Table API和SQL查询时提供输入数据，输出表主要是将Table API和
SQL查询的结果作为输出结果对接到外部系统。

输入表有多种不同的输入源进行注册：
- 已经存在的Table对象，通常是是作为Table API和SQL查询的结果
- TableSource，可以访问外部数据如文件，数据库或者是消息系统
- 来自DataStream或是DataSet程序中的DataStream或DataSet，讨论DataStream或是DataSet
可以[整合DataStream和DataSet API](https://ci.apache.org/projects/flink/flink-docs-release-1.6/dev/table/common.html#integration-with-datastream-and-dataset-api)了解到

输出表可使用TableSink进行注册

## Register a Table
Table是如何注册到TableEnvironment中如下所示：
```scala
// 获取(创建)TableEnvironment对象
val tableEnv = TableEnvironment.getTableEnvironment(env)

// 从简单的查询结果中作为表
val projTable: Table = tableEnv.scan("X").select(...)

// 将创建的表projTable命名为projectedTable注册到TableEnvironment中
tableEnv.registerTable("projectedTable", projTable)
```
注意：一张注册过的Table就跟关系型数据库中的视图性质相同，定义表的查询未进行优化，但在另一个查询引用已注册的表时将进行内联。
如果多表查询引用了相同的Table，它就会将每一个引用进行内联并且多次执行，已注册的Table的结果之间不会进行共享。

## Register a TableSource
TableSource可以访问外部系统存储例如数据库（Mysql,HBase），特殊格式编码的文件(CSV, Apache [Parquet, Avro, ORC], …)
或者是消息系统 (Apache Kafka, RabbitMQ, …)中的数据。

Flink旨在为通用数据格式和存储系统提供TableSource。请查看[此处](https://ci.apache.org/projects/flink/flink-docs-release-1.6/dev/table/sourceSinks.html)
了解支持的TableSource类型与如何去自定义TableSour。

TableSource是如何注册到TableEnvironment中如下所示：
```scala
// 获取(创建)TableEnvironment对象
val tableEnv = TableEnvironment.getTableEnvironment(env)

// 创建TableSource对象
val csvSource: TableSource = new CsvTableSource("/path/to/file", ...)

// 将创建的TableSource作为表并命名为csvTable注册到TableEnvironment中
tableEnv.registerTableSource("CsvTable", csvSource)
```
## Register a TableSink
注册过的TableSink可以将SQL查询的结果以表的形式输出到外部的存储系统，例如关系型数据库，
Key-Value数据库(Nosql)，消息队列，或者是其他文件系统(使用不同的编码, 例如CSV, Apache [Parquet, Avro, ORC], …)

Flink使用TableSink的目的是为了将常用的数据进行清洗转换然后存储到不同的存储介质中。详情请查看[此处](https://ci.apache.org/projects/flink/flink-docs-release-1.6/dev/table/sourceSinks.html)
去深入了解哪些sinks是可用的，并且如何去自定义TableSink。
```scala
// 获取(创建)TableEnvironment对象
val tableEnv = TableEnvironment.getTableEnvironment(env)

// 创建TableSink对象
val csvSink: TableSink = new CsvTableSink("/path/to/file", ...)

// 定义字段的名称和类型
val fieldNames: Array[String] = Array("a", "b", "c")
val fieldTypes: Array[TypeInformation[_]] = Array(Types.INT, Types.STRING, Types.LONG)

// 将创建的TableSink作为表并命名为CsvSinkTable注册到TableEnvironment中
tableEnv.registerTableSink("CsvSinkTable", fieldNames, fieldTypes, csvSink)
```

## Register an External Catalog
外部目录可以提供有关外部数据库和表的信息，
例如其名称，模式，统计以及有关如何访问存储在外部数据库，表或文件中的数据的信息。

外部目录的创建方式可以通过实现ExternalCatalog接口，并且注册到TableEnvironment中，详情如下所示:
```scala
// 获取(创建)TableEnvironment对象
val tableEnv = TableEnvironment.getTableEnvironment(env)

// 创建一个External Catalog目录对象
val catalog: ExternalCatalog = new InMemoryExternalCatalog

// 将ExternalCatalog注册到TableEnvironment中
tableEnv.registerExternalCatalog("InMemCatalog", catalog)
```
一旦将External Catalog注册到TableEnvironment中，所有在ExternalCatalog中
定义的表可以通过完整的路径如catalog.database.table进行Table API和SQL的查询操作 

目前，Flink提供InMemoryExternalCatalog对象用来做demo和测试，然而，
ExternalCatalog对象还可用作Table API来连接catalogs，例如HCatalog 或 Metastore

## Query a Table
### Table API
Table API是Scala和Java语言集成查询的API，与SQL查询不同之处在于，它的查询不是像
SQL一样使用字符串进行查询，而是在语言中使用语法进行逐步组合使用

Table API是基于展示表（流或批处理）的Table类，它提供一些列操作应用相关的操作。
这些方法返回一个新的Table对象，该对象表示在输入表上关系运算的结果。一些关系运算是
由多个方法组合而成的，例如 table.groupBy(...).select()，其中groupBy()指定
表的分组，select()表示在分组的结果上进行查询。

[Table API](https://ci.apache.org/projects/flink/flink-docs-release-1.6/dev/table/tableApi.html)
描述了所有支持表的流式或者批处理相关的操作。

下面给出一个简单的实例去说明如何去使用Table API进行聚合查询：
```scala
// 获取(创建)TableEnvironment对象
val tableEnv = TableEnvironment.getTableEnvironment(env)

// 注册Orders表

// 扫描注册过的Orders表
val orders = tableEnv.scan("Orders")

// 计算表中所有来自法国的客户的收入
val revenue = orders
  .filter('cCountry === "FRANCE")
  .groupBy('cID, 'cName)
  .select('cID, 'cName, 'revenue.sum AS 'revSum)

// 将结果输出成一张表或者是转换表

// 执行查询
```
注意：Scala的Table API使用Scala符号，它使用单引号加字段('cID)来表示表的属性的引用，
如果使用Scala的隐式转换的话，确保引入了org.apache.flink.api.scala._ 和 org.apache.flink.table.api.scala._
来确保它们之间的转换。

### SQL
Flink的SQL操作基于实现了SQL标准的[Apache Calcite](https://calcite.apache.org/)，SQL查询通常是使用特殊且有规律的字符串。
[SQL](https://ci.apache.org/projects/flink/flink-docs-release-1.6/dev/table/sql.html)
描述了所有支持表的流式或者批处理相关的SQL操作。
```scala
// 获取(创建)TableEnvironment对象
val tableEnv = TableEnvironment.getTableEnvironment(env)

// 注册Orders表

// 计算表中所有来自法国的客户的收入
val revenue = tableEnv.sqlQuery("""
  |SELECT cID, cName, SUM(revenue) AS revSum
  |FROM Orders
  |WHERE cCountry = 'FRANCE'
  |GROUP BY cID, cName
  """.stripMargin)

// 将结果输出成一张表或者是转换表

// 执行查询
```
下面的例子展示了如何去使用更新查询去插入数据到已注册的表中
```scala
// 获取(创建)TableEnvironment对象
val tableEnv = TableEnvironment.getTableEnvironment(env)

// 注册"Orders"表
// 注册"RevenueFrance"输出表

// 计算表中所有来自法国的客户的收入并且将结果作为结果输出到"RevenueFrance"中
tableEnv.sqlUpdate("""
  |INSERT INTO RevenueFrance
  |SELECT cID, cName, SUM(revenue) AS revSum
  |FROM Orders
  |WHERE cCountry = 'FRANCE'
  |GROUP BY cID, cName
  """.stripMargin)

// 执行查询
```

## Mixing Table API and SQL
Table API和SQL可以很轻松的混合使用因为他们两者返回的结果都为Table对象：
- 可以在SQL查询返回的Table对象上定义Table API查询
- 通过在TableEnvironment中注册结果表并在SQL查询的FROM子句中引用它，
可以在Table API查询的结果上定义SQL查询。

## Emit a Table
通过将Table写入到TableSink来作为一张表的输出，TableSink是做为多种文件类型 (CSV, Apache Parquet, Apache Avro),
存储系统(JDBC, Apache HBase, Apache Cassandra, Elasticsearch), 或者是消息系统 (Apache Kafka, RabbitMQ).输出的通用接口，

Batch Table只能通过BatchTableSink来进行数据写入，而Streaming Table可以
选择AppendStreamTableSink，RetractStreamTableSink，UpsertStreamTableSink
中的任意一个来进行。

请查看[Table Source & Sinks](https://ci.apache.org/projects/flink/flink-docs-release-1.6/dev/table/sourceSinks.html)
来更详细的了解支持的Sinks并且如何去实现自定义的TableSink。

可以使用两种方式来输出一张表：

- Table.writeToSink(TableSink sink)方法使用提供的TableSink自动配置的表的schema来
进行表的输出
- Table.insertInto（String sinkTable）方法查找在TableEnvironment目录中提供的名称下使用特定模式注册的TableSink。 
将输出表的模式将根据已注册的TableSink的模式进行验证

下面的例子展示了如何去查询结果作为一张表输出
```scala
// 获取(创建)TableEnvironment对象
val tableEnv = TableEnvironment.getTableEnvironment(env)

// 使用Table API或者SQL 查询来查找结果
val result: Table = ...
// 创建TableSink对象
val sink: TableSink = new CsvTableSink("/path/to/file", fieldDelim = "|")

// 方法1: 使用TableSink的writeToSink()方法来将结果输出为一张表
result.writeToSink(sink)

// 方法2: 注册特殊schema的TableSink
val fieldNames: Array[String] = Array("a", "b", "c")
val fieldTypes: Array[TypeInformation] = Array(Types.INT, Types.STRING, Types.LONG)
tableEnv.registerTableSink("CsvSinkTable", fieldNames, fieldTypes, sink)
// 调用注册过的TableSink中insertInto() 方法来将结果输出为一张表
result.insertInto("CsvSinkTable")

// 执行
```

## Translate and Execute a Query
Table API和SQL查询的结果转换为[DataStream](https://ci.apache.org/projects/flink/flink-docs-release-1.6/dev/datastream_api.html)
或是[DataSet](https://ci.apache.org/projects/flink/flink-docs-release-1.6/dev/batch/)
取决于它的输入是流式输入还是批处理输入。查询逻辑在内部表示为逻辑执行计划，并分为两个阶段进行转换：
- 优化逻辑执行计划
- 转换为DataStream或DataSet

Table API或SQL查询在下面请看下进行转换：
- 当调用Table.writeToSink() 或 Table.insertInto()进行查询结果表输出的时候
- 当调用TableEnvironment.sqlUpdate()进行SQL更新查询时
- 当表转换为DataSteam或DataSet时，详情查看[Integration with DataStream and DataSet API](https://ci.apache.org/projects/flink/flink-docs-release-1.6/dev/table/common.html#integration-with-dataStream-and-dataSet-api)

一旦进行转换后，Table API或SQL查询的结果就会在StreamExecutionEnvironment.execute() 或 ExecutionEnvironment.execute()
被调用时被当做DataStream或DataSet一样被进行处理

## Integration with DataStream and DataSet API
Table API或SQL查询的结果很容易被[DataStream](https://ci.apache.org/projects/flink/flink-docs-release-1.6/dev/datastream_api.html)
或是[DataSet](https://ci.apache.org/projects/flink/flink-docs-release-1.6/dev/batch/)内嵌整合。举个例子，
我们会进行外部表的查询(像关系型数据库)，然后做像过滤，映射，聚合或者是元数据关联的一些预处理。
然后使用DataStream或是DataSet API(或者是基于这些基础库开发的上层API库, 例如CEP或Gelly)进一步对数据进行处理。
同样，Table API或SQL查询也可以应用于DataStream或DataSet程序的结果。

##implicit Conversion for Scala
Scala Table API具有DataSet，DataStream和Table Class之间的隐式转换，流式操作API中只要引入org.apache.flink.table.api.scala._ 
和 org.apache.flink.api.scala._ 便可以进行相应的隐式转换

## Register a DataStream or DataSet as Table
DataStream或DataSet也可以作为Table注册到TableEnvironment中。结果表的模式取决于已注册的DataStream或DataSet的数据类型，
详情请查看[mapping of data types to table schema](https://ci.apache.org/projects/flink/flink-docs-release-1.6/dev/table/common.html#mapping-of-data-types-to-table-schema)

```scala
// 获取(创建)TableEnvironment对象
// 注册如表一样的DataSet

val tableEnv = TableEnvironment.getTableEnvironment(env)

val stream: DataStream[(Long, String)] = ...

// 将DataStream作为具有"f0", "f1"字段的"myTable"表注册到TableEnvironment中
tableEnv.registerDataStream("myTable", stream)

// 将DataStream作为具有"myLong", "myString"字段的"myTable2"表注册到TableEnvironment中
tableEnv.registerDataStream("myTable2", stream, 'myLong, 'myString)
```
注意：DataStream表的名称必须与^ _DataStreamTable_ [0-9] +模式不匹配，
并且DataSet表的名称必须与^ _DataSetTable_ [0-9] +模式不匹配。 
这些模式仅供内部使用。

## Convert a DataStream or DataSet into a Table
如果你使用Table API或是SQL查询，你可以直接将DataStream或DataSet直接转换为表而不需要
再将它们注册到TableEnvironment中。
```scala
// 获取(创建)TableEnvironment对象
// 注册如表一样的DataSet
val tableEnv = TableEnvironment.getTableEnvironment(env)

val stream: DataStream[(Long, String)] = ...

// 使用默认的字段'_1, '_2将DataStram转换为Table
val table1: Table = tableEnv.fromDataStream(stream)

// 使用默认的字段'myLong, 'myString将DataStram转换为Table

val table2: Table = tableEnv.fromDataStream(stream, 'myLong, 'myString)
```

## Convert a Table into a DataStream or DataSet
表可以转换为DataStream或DataSet，通过这种方式，自定义DataStream或DataSet
同样也可以作为Table API或SQL查询结果的结果。
当把表转换为DataStream或DataSet时，你需要指定生成的DataStream或DataSet的数据类型。
例如，表格行所需转换的数据类型，通常最方便的转换类型也最常用的是Row。
以下列表概述了不同选项的功能：
- Row：字段按位置，任意数量的字段映射，支持空值，无类型安全访问。
- POJO：字段按名称(POJO字段必须与Table字段保持一致)，任意数量的字段映射，支持空值，类型安全访问。
- Case Class：字段按位置，任意数量的字段映射，不支持空值，类型安全访问。
- Tuple：字段按位置，Scala支持22个字段，Java 25个字段映射，不支持空值，类型安全访问。
- Atomic Type：表必须具有单个字段，不支持空值，类型安全访问。
### Convert a Table into a DataStream
作为流式查询结果的表将动态更新，它随着新记录到达查询的输入流而改变，于是，转换到这样的动态查询DataStream
需要对表的更新进行编码。
将表转换为DataStream有两种模式：
- Append Mode：这种模式仅用于动态表仅仅通过INSERT来进行表的更新，它是仅可追加模式，
并且之前输出的表不会进行更改
- Retract Mode：这种模式经常用到。它使用布尔值的变量来对INSERT和DELETE对表的更新做标记
```scala
// 获取(创建)TableEnvironment对象 
// 注册如表一样的DataSet
val tableEnv = TableEnvironment.getTableEnvironment(env)

// 表中有两个字段(String name, Integet age)
val table: Table = ...

// 将表转换为列的 append DataStream
val dsRow: DataStream[Row] = tableEnv.toAppendStream[Row](table)

// 将表转换为Tubple2[String,Int]的 append DataStream
// convert the Table into an append DataStream of Tuple2[String, Int]
val dsTuple: DataStream[(String, Int)] dsTuple = 
  tableEnv.toAppendStream[(String, Int)](table)

// convert the Table into a retract DataStream of Row.
// Retract Mode下将表转换为列的 append DataStream
// 判断A retract stream X是否为DataStream[(Boolean, X)]
//  布尔只表示数据类型的变化,True代表为INSERT，false表示为删除
val retractStream: DataStream[(Boolean, Row)] = tableEnv.toRetractStream[Row](table)
```
注意：关于动态表和它的属性详情参考[Streaming Queries](https://ci.apache.org/projects/flink/flink-docs-release-1.6/dev/table/streaming.html)

### Convert a Table into a DataSet
表转换为DataSet如下所示：
```scala
// 获取(创建)TableEnvironment对象 
// 注册如表一样的DataSet
val tableEnv = TableEnvironment.getTableEnvironment(env)

// 表中有两个字段(String name, Integet age)
val table: Table = ...

// 将表转换为列的DataSet
val dsRow: DataSet[Row] = tableEnv.toDataSet[Row](table)

// 将表转换为Tubple2[String,Int]的DataSet
val dsTuple: DataSet[(String, Int)] = tableEnv.toDataSet[(String, Int)](table)
```
### Mapping of Data Types to Table Schema
Flink的DataStream和DataSet API支持多种类型。组合类型像Tuple(内置Scala元组和Flink Java元组)，
POJOs，Scala case classes和Flink中具有可在表表达式中访问的多个字段允许嵌套数据结构的Row类型，
其他类型都被视为原子类型。接下来，我们将会描述Table API是如何将这些类型转换为内部的列展现并且
举例说明如何将DataStream转换为Table

#### Position-based Mapping
基于位置的映射通常在保持顺序的情况下给字段一个更有意义的名称，这种映射可用于有固定顺序的组合数据类型，
也可用于原子类型。复合数据类型（如元组，行和Case Class）具有此类字段顺序.然而，POJO的字段必须与映射的
表的字段名相同。

当定义基于位置的映射，输入的数据类型不得存在指定的名称，不然API会认为这些映射应该按名称来进行映射。
如果未指定字段名称，则使用复合类型的默认字段名称和字段顺序，或者使用f0作为原子类型。
```scala
// 获取(创建)TableEnvironment对象 
val tableEnv = TableEnvironment.getTableEnvironment(env)

val stream: DataStream[(Long, Int)] = ...

// 使用默认的字段'_1, '_2将DataStram转换为Table
val table1: Table = tableEnv.fromDataStream(stream)

// 使用默认的字段'myLong, 'myInt将DataStram转换为Table
val table: Table = tableEnv.fromDataStream(stream, 'myLong 'myInt)
```
#### Name-based Mapping
基于名称的映射可用于一切数据类型包括POJOs，它是定义表模式映射最灵活的一种方式。虽然查询结果的字段可能会使用别名，但
这种模式下所有的字段都是使用名称进行映射的。使用别名的情况下会进行重排序。
如果未指定字段名称，则使用复合类型的默认字段名称和字段顺序，或者使用f0作为原子类型。
```scala
// 获取(创建)TableEnvironment对象 
val tableEnv = TableEnvironment.getTableEnvironment(env)

val stream: DataStream[(Long, Int)] = ...

// 使用默认的字段'_1 和 '_2将DataStram转换为Table
val table: Table = tableEnv.fromDataStream(stream)

// 只使用'_2字段将DataStream转换为Table
val table: Table = tableEnv.fromDataStream(stream, '_2)

// 交换字段将DataStream转换为Table
val table: Table = tableEnv.fromDataStream(stream, '_2, '_1)

// 交换后的字段给予别名'myInt, 'myLong将DataStream转换为Table
val table: Table = tableEnv.fromDataStream(stream, '_2 as 'myInt, '_1 as 'myLong)
```
#### Atomic Types
Flink将基础类型(Integer, Double, String)和通用类型(不能被分析和拆分的类型)视为原子类型。
原子类型的DataStream或DataSet转换为只有单个属性的表。从原子类型推断属性的类型，并且可以指定属性的名称。
```scala
// 获取(创建)TableEnvironment对象
val tableEnv = TableEnvironment.getTableEnvironment(env)

val stream: DataStream[Long] = ...

// 将DataStream转换为带默认字段"f0"的表
val table: Table = tableEnv.fromDataStream(stream)

// 将DataStream转换为带字段"myLong"的表
val table: Table = tableEnv.fromDataStream(stream, 'myLong)
```
#### Tuples (Scala and Java) and Case Classes (Scala only)
Flink支持内建的Tuples并且提供了自己的Tuple类给Java进行使用。DataStreams和DataSet这两种
Tuple都可以转换为表。提供所有字段的名称(基于位置的映射)字段可以被重命名。如果没有指定字段的名称，
就使用默认的字段名称。如果原始字段名(f0, f1, … for Flink Tuples and _1, _2, … for Scala Tuples)被引用了的话，
API就会使用基于名称的映射来代替位置的映射。基于名称的映射可以起别名并且会进行重排序。
```scala
// 获取(创建)TableEnvironment对象 
val tableEnv = TableEnvironment.getTableEnvironment(env)

val stream: DataStream[(Long, String)] = ...

// 将默认的字段重命名为'_1，'_2的DataStream转换为Table
val table: Table = tableEnv.fromDataStream(stream)

// 将字段名为'myLong，'myString的DataStream转换为Table(基于位置)
val table: Table = tableEnv.fromDataStream(stream, 'myLong, 'myString)

// 将重排序后字段为'_2，'_1 的DataStream转换为Table(基于名称)
val table: Table = tableEnv.fromDataStream(stream, '_2, '_1)

// 将映射字段'_2的DataStream转换为Table(基于名称)
val table: Table = tableEnv.fromDataStream(stream, '_2)

// 将重排序后字段为'_2给出别名'myString，'_1给出别名'myLong 的DataStream转换为Table(基于名称)
val table: Table = tableEnv.fromDataStream(stream, '_2 as 'myString, '_1 as 'myLong)

// 定义 case class
case class Person(name: String, age: Int)
val streamCC: DataStream[Person] = ...

// 将默认字段'name, 'age的DataStream转换为Table
val table = tableEnv.fromDataStream(streamCC)

// 将字段名为'myName，'myAge的DataStream转换为Table(基于位置)
val table = tableEnv.fromDataStream(streamCC, 'myName, 'myAge)

将重排序后字段为'_age给出别名'myAge，'_name给出别名'myName 的DataStream转换为Table(基于名称)
val table: Table = tableEnv.fromDataStream(stream, 'age as 'myAge, 'name as 'myName)
```
#### POJO (Java and Scala)
Flink支持POJO作为符合类型。决定POJO规则的文档请参考[这里](https://ci.apache.org/projects/flink/flink-docs-release-1.6/dev/api_concepts.html#pojos)

当将一个POJO类型的DataStream或者DataSet转换为Table而不指定字段名称时，Table的字段名称将采用JOPO原生的字段名称作为字段名称。
重命名原始的POJO字段需要关键字AS，因为POJO没有固定的顺序，名称映射需要原始名称并且不能通过位置来完成。
```scala
// 获取(创建)TableEnvironment对象
val tableEnv = TableEnvironment.getTableEnvironment(env)

// Person 是一个有两个字段"name"和"age"的POJO
val stream: DataStream[Person] = ...

// 将 DataStream 转换为带字段 "age", "name" 的Table(字段通过名称进行排序)
val table: Table = tableEnv.fromDataStream(stream)

// 将DataStream转换为重命名为"myAge", "myName"的Table(基于名称)
val table: Table = tableEnv.fromDataStream(stream, 'age as 'myAge, 'name as 'myName)

// 将带映射字段'name的DataStream转换为Table(基于名称)
val table: Table = tableEnv.fromDataStream(stream, 'name)

// 将带映射字段'name并重命名为'myName的DataStream转换为Table(基于名称)
val table: Table = tableEnv.fromDataStream(stream, 'name as 'myName)
```
#### Row
Row数据类型可以支持任意数量的字段，并且这些字段支持null值。当进行Row DataStream或Row DataSet
转换为Table时可以通过RowTypeInfo来指定字段的名称。Row Type支持基于位置和名称的两种映射方式。
通过提供所有字段的名称可以进行字段的重命名(基于位置)，或者是单独选择列来进行映射/重排序/重命名(基于名称)
```scala
// 获取(创建)TableEnvironment对象
val tableEnv = TableEnvironment.getTableEnvironment(env)

// 在`RowTypeInfo`中指定字段"name" 和 "age"的Row类型DataStream
val stream: DataStream[Row] = ...

// 将 DataStream 转换为带默认字段 "age", "name" 的Table
val table: Table = tableEnv.fromDataStream(stream)

// 将 DataStream 转换为重命名字段 'myName, 'myAge 的Table(基于位置)
val table: Table = tableEnv.fromDataStream(stream, 'myName, 'myAge)

// 将 DataStream 转换为重命名字段 'myName, 'myAge 的Table(基于名称)
val table: Table = tableEnv.fromDataStream(stream, 'name as 'myName, 'age as 'myAge)

// 将 DataStream 转换为映射字段 'name的Table(基于名称)
val table: Table = tableEnv.fromDataStream(stream, 'name)

// 将 DataStream 转换为映射字段 'name并重命名为'myName的Table(基于名称)
val table: Table = tableEnv.fromDataStream(stream, 'name as 'myName)
```
#### Query Optimization
Apache Flink 基于 Apache Calcite 来做转换和查询优化。当前的查询优化包括投影、过滤下推、
相关子查询和各种相关的查询重写。Flink不去做join优化，但是会让他们去顺序执行(FROM子句中表的顺序或者WHERE子句中连接谓词的顺序)

可以通过提供一个CalciteConfig对象来调整在不同阶段应用的优化规则集，
这个可以通过调用CalciteConfig.createBuilder())获得的builder来创建，
并且可以通过调用tableEnv.getConfig.setCalciteConfig(calciteConfig)来提供给TableEnvironment。

#### Explaining a Table
Table API为计算Table提供了一个机制来解析逻辑和优化查询计划，这个可以通过TableEnvironment.explain(table)
来完成。它返回描述三个计划的字符串信息：
- 关联查询抽象语法树，即未优化过的逻辑执行计划
- 优化过的逻辑执行计划
- 物理执行计划

下面的实例展示了相应的输出：
```scala
val env = StreamExecutionEnvironment.getExecutionEnvironment
val tEnv = TableEnvironment.getTableEnvironment(env)

val table1 = env.fromElements((1, "hello")).toTable(tEnv, 'count, 'word)
val table2 = env.fromElements((1, "hello")).toTable(tEnv, 'count, 'word)
val table = table1
  .where('word.like("F%"))
  .unionAll(table2)

val explanation: String = tEnv.explain(table)
println(explanation)
```
对应的输出如下：
```
== 抽象语法树 ==
LogicalUnion(all=[true])
  LogicalFilter(condition=[LIKE($1, 'F%')])
    LogicalTableScan(table=[[_DataStreamTable_0]])
  LogicalTableScan(table=[[_DataStreamTable_1]])

== 优化后的逻辑执行计划 ==
DataStreamUnion(union=[count, word])
  DataStreamCalc(select=[count, word], where=[LIKE(word, 'F%')])
    DataStreamScan(table=[[_DataStreamTable_0]])
  DataStreamScan(table=[[_DataStreamTable_1]])

== 物理执行计划 ==
Stage 1 : Data Source
  content : collect elements with CollectionInputFormat

Stage 2 : Data Source
  content : collect elements with CollectionInputFormat

  Stage 3 : Operator
    content : from: (count, word)
    ship_strategy : REBALANCE

    Stage 4 : Operator
      content : where: (LIKE(word, 'F%')), select: (count, word)
      ship_strategy : FORWARD

      Stage 5 : Operator
        content : from: (count, word)
        ship_strategy : REBALANCE
```


# Flink用户自定义函数

用户自定义函数是非常重要的一个特征，因为他极大地扩展了查询的表达能力。

在大多数场景下，用户自定义函数在使用之前是必须要注册的。对于Scala的Table API，udf是不需要注册的。
调用TableEnvironment的registerFunction()方法来实现注册。Udf注册成功之后，会被插入TableEnvironment的function catalog，这样table API和sql就能解析他了。
本文会主要讲三种udf：
* ScalarFunction
* TableFunction
* AggregateFunction

## 1. Scalar Functions 标量函数

标量函数，是指指返回一个值的函数。标量函数是实现讲0，1，或者多个标量值转化为一个新值。

实现一个标量函数需要继承ScalarFunction，并且实现一个或者多个evaluation方法。标量函数的行为就是通过evaluation方法来实现的。evaluation方法必须定义为public，命名为eval。evaluation方法的输入参数类型和返回值类型决定着标量函数的输入参数类型和返回值类型。evaluation方法也可以被重载实现多个eval。同时evaluation方法支持变参数，例如：eval(String... strs)。

下面给出一个标量函数的例子。例子实现的事一个hashcode方法。
```java
public class HashCode extends ScalarFunction {
  private int factor = 12;
  
  public HashCode(int factor) {
      this.factor = factor;
  }
  
  public int eval(String s) {
      return s.hashCode() * factor;
  }
}

BatchTableEnvironment tableEnv = TableEnvironment.getTableEnvironment(env);

// register the function
tableEnv.registerFunction("hashCode", new HashCode(10));

// use the function in Java Table API
myTable.select("string, string.hashCode(), hashCode(string)");

// use the function in SQL API
tableEnv.sqlQuery("SELECT string, HASHCODE(string) FROM MyTable");

````

默认情况下evaluation方法的返回值类型是由flink类型抽取工具决定。对于基础类型，简单的POJOS是足够的，但是更复杂的类型，自定义类型，组合类型，会报错。这种情况下，返回值类型的TypeInformation，需要手动指定，方法是重载
ScalarFunction#getResultType()。

下面给一个例子，通过复写ScalarFunction#getResultType()，将long型的返回值在代码生成的时候翻译成Types.TIMESTAMP。

```java
public static class TimestampModifier extends ScalarFunction {
  public long eval(long t) {
    return t % 1000;
  }

  public TypeInformation<?> getResultType(signature: Class<?>[]) {
    return Types.TIMESTAMP;
  }
}
```

## 2. Table Functions 表函数

与标量函数相似之处是输入可以0，1，或者多个参数，但是不同之处可以输出任意数目的行数。返回的行也可以包含一个或者多个列。

为了自定义表函数，需要继承TableFunction，实现一个或者多个evaluation方法。表函数的行为定义在这些evaluation方法内部，函数名为eval并且必须是public。TableFunction可以重载多个eval方法。Evaluation方法的输入参数类型，决定着表函数的输入类型。Evaluation方法也支持变参，例如：eval(String... strs)。返回表的类型取决于TableFunction的基本类型。Evaluation方法使用collect(T)发射输出的rows。

在Table API中，表函数在scala语言中使用方法如下：.join(Expression) 或者 .leftOuterJoin(Expression)，在java语言中使用方法如下：.join(String) 或者.leftOuterJoin(String)。

Join操作算子会使用表值函数(操作算子右边的表)产生的所有行进行(cross) join 外部表(操作算子左边的表)的每一行。

leftOuterJoin操作算子会使用表值函数(操作算子右边的表)产生的所有行进行(cross) join 外部表(操作算子左边的表)的每一行，并且在表函数返回一个空表的情况下会保留所有的outer rows。

在sql语法中稍微有点区别：
cross join用法是LATERAL TABLE(<TableFunction>)。
LEFT JOIN用法是在join条件中加入ON TRUE。

下面的理智讲的是如何使用表值函数。
```java
// The generic type "Tuple2<String, Integer>" determines the schema of the returned table as (String, Integer).
public class Split extends TableFunction<Tuple2<String, Integer>> {
    private String separator = " ";
    
    public Split(String separator) {
        this.separator = separator;
    }
    
    public void eval(String str) {
        for (String s : str.split(separator)) {
            // use collect(...) to emit a row
            collect(new Tuple2<String, Integer>(s, s.length()));
        }
    }
}

BatchTableEnvironment tableEnv = TableEnvironment.getTableEnvironment(env);
Table myTable = ...         // table schema: [a: String]

// Register the function.
tableEnv.registerFunction("split", new Split("#"));

// Use the table function in the Java Table API. "as" specifies the field names of the table.
myTable.join("split(a) as (word, length)").select("a, word, length");
myTable.leftOuterJoin("split(a) as (word, length)").select("a, word, length");

// Use the table function in SQL with LATERAL and TABLE keywords.
join.md
tableEnv.sqlQuery("SELECT a, word, length FROM MyTable, LATERAL TABLE(split(a)) as T(word, length)");
// LEFT JOIN a table function (equivalent to "leftOuterJoin" in Table API).
tableEnv.sqlQuery("SELECT a, word, length FROM MyTable LEFT JOIN LATERAL TABLE(split(a)) as T(word, length) ON TRUE");

```

需要注意的是PROJO类型不需要一个确定的字段顺序。意味着你不能使用as修改表函数返回的pojo的字段的名字。

默认情况下TableFunction返回值类型是由flink类型抽取工具决定。对于基础类型，简单的POJOS是足够的，但是更复杂的类型，自定义类型，组合类型，会报错。这种情况下，返回值类型的TypeInformation，需要手动指定，方法是重载
TableFunction#getResultType()。

下面的例子，我们通过复写TableFunction#getResultType()方法使得表返回类型是RowTypeInfo(String, Integer)。
```java
public class CustomTypeSplit extends TableFunction<Row> {
    public void eval(String str) {
        for (String s : str.split(" ")) {
            Row row = new Row(2);
            row.setField(0, s);
            row.setField(1, s.length);
            collect(row);
        }
    }

    @Override
    public TypeInformation<Row> getResultType() {
        return Types.ROW(Types.STRING(), Types.INT());
    }
}
```
## 3. Aggregation Functions 聚合函数

用户自定义聚合函数聚合一张表(一行或者多行，一行有一个或者多个属性)为一个标量的值。



上图中是讲的一张饮料的表这个表有是那个字段五行数据，现在要做的事求出所有饮料的最高价。

聚合函数需要继承AggregateFunction。聚合函数工作方式如下：
首先，需要一个accumulator，这个是保存聚合中间结果的数据结构。调用AggregateFunction函数的createAccumulator()方法来创建一个空的accumulator.
随后，每个输入行都会调用accumulate()方法来更新accumulator。一旦所有的行被处理了，getValue()方法就会被调用，计算和返回最终的结果。

对于每个AggregateFunction，下面三个方法都是比不可少的：
createAccumulator()
accumulate()
getValue()

flink的类型抽取机制不能识别复杂的数据类型，比如，数据类型不是基础类型或者简单的pojos类型。所以，类似于ScalarFunction 和TableFunction，AggregateFunction提供了方法去指定返回结果类型的TypeInformation，用的是AggregateFunction#getResultType()。Accumulator类型用的是AggregateFunction#getAccumulatorType()。

除了上面的方法，这里有一些可选的方法。尽管有些方法是让系统更加高效的执行查询，另外的一些在特定的场景下是必须的。例如，merge()方法在会话组窗口上下文中是必须的。当一行数据是被视为跟两个回话窗口相关的时候，两个会话窗口的accumulators需要被join。

AggregateFunction的下面几个方法，根据使用场景的不同需要被实现：
retract()：在bounded OVER窗口的聚合方法中是需要实现的。
merge()：在很多batch 聚合和会话窗口聚合是必须的。
resetAccumulator(): 在大多数batch聚合是必须的。

AggregateFunction的所有方法都是需要被声明为public，而不是static。定义聚合函数需要实现org.apache.flink.table.functions.AggregateFunction同时需要实现一个或者多个accumulate方法。该方法可以被重载为不同的数据类型，并且支持变参。

在这里就不贴出来AggregateFunction的源码了。

下面举个求加权平均的栗子
为了计算加权平均值，累加器需要存储已累积的所有数据的加权和及计数。在栗子中定义一个WeightedAvgAccum类作为accumulator。尽管，retract(), merge(), 和resetAccumulator()方法在很多聚合类型是不需要的，这里也给出了栗子。
```java

/**
 * Accumulator for WeightedAvg.
 */
public static class WeightedAvgAccum {
    public long sum = 0;
    public int count = 0;
}

/**
 * Weighted Average user-defined aggregate function.
 */
public static class WeightedAvg extends AggregateFunction<Long, WeightedAvgAccum> {

    @Override
    public WeightedAvgAccum createAccumulator() {
        return new WeightedAvgAccum();
    }

    @Override
    public Long getValue(WeightedAvgAccum acc) {
        if (acc.count == 0) {
            return null;
        } else {
            return acc.sum / acc.count;
        }
    }

    public void accumulate(WeightedAvgAccum acc, long iValue, int iWeight) {
        acc.sum += iValue * iWeight;
        acc.count += iWeight;
    }

    public void retract(WeightedAvgAccum acc, long iValue, int iWeight) {
        acc.sum -= iValue * iWeight;
        acc.count -= iWeight;
    }
    
    public void merge(WeightedAvgAccum acc, Iterable<WeightedAvgAccum> it) {
        Iterator<WeightedAvgAccum> iter = it.iterator();
        while (iter.hasNext()) {
            WeightedAvgAccum a = iter.next();
            acc.count += a.count;
            acc.sum += a.sum;
        }
    }
    
    public void resetAccumulator(WeightedAvgAccum acc) {
        acc.count = 0;
        acc.sum = 0L;
    }
}

// register function
StreamTableEnvironment tEnv = ...
tEnv.registerFunction("wAvg", new WeightedAvg());

// use function
tEnv.sqlQuery("SELECT user, wAvg(points, level) AS avgPoints FROM userScores GROUP BY user");

```
## 4. 实现udf的最佳实践经验

Table API和SQL 代码生成器内部会尽可能多的尝试使用原生值。用户定义的函数可能通过对象创建、强制转换(casting)和拆装箱((un)boxing)引入大量开销。因此，强烈推荐参数和返回值的类型定义为原生类型而不是他们包装类型(boxing class)。Types.DATE 和Types.TIME可以用int代替。Types.TIMESTAMP可以用long代替。

我们建议用户自定义函数使用java编写而不是scala编写，因为scala的类型可能会有不被flink类型抽取器兼容。

用Runtime集成UDFs

有时候udf需要获取全局runtime信息或者在进行实际工作之前做一些设置和清除工作。Udf提供了open()和close()方法，可以被复写，功能类似Dataset和DataStream API的RichFunction方法。

Open()方法是在evaluation方法调用前调用一次。Close()是在evaluation方法最后一次调用后调用。

Open()方法提共一个FunctionContext，FunctionContext包含了udf执行环境的上下文，比如，metric group，分布式缓存文件，全局的job参数。

通过调用FunctionContext的相关方法，可以获取到相关的信息：

方法描述
* getMetricGroup() - 并行子任务的指标组
* getCachedFile(name) -分布式缓存文件的本地副本
* getJobParameter(name, defaultValue) - 给定key全局job参数。

下面，给出的例子就是通过FunctionContext在一个标量函数中获取全局job的参数。
```java
public class HashCode extends ScalarFunction {

    private int factor = 0;

    @Override
    public void open(FunctionContext context) throws Exception {
        // access "hashcode_factor" parameter
        // "12" would be the default value if parameter does not exist
        factor = Integer.valueOf(context.getJobParameter("hashcode_factor", "12")); 
    }

    public int eval(String s) {
        return s.hashCode() * factor;
    }
}

ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
BatchTableEnvironment tableEnv = TableEnvironment.getTableEnvironment(env);

// set job parameter
Configuration conf = new Configuration();
conf.setString("hashcode_factor", "31");
env.getConfig().setGlobalJobParameters(conf);

// register the function
tableEnv.registerFunction("hashCode", new HashCode());

// use the function in Java Table API
myTable.select("string, string.hashCode(), hashCode(string)");

// use the function in SQL
tableEnv.sqlQuery("SELECT string, HASHCODE(string) FROM MyTable");
```

# 内置函数

## scala
###  三元运算符

sql或者table API筛选数据，必须保证每个字段不为空，
Flink内部，中间结果都是通过case class传递，而case class的字段必须保证不能为空

```scala
BOOLEAN.?(VALUE1, VALUE2)
'is_active_user.isNull.?("0", "1")
```

### 等值判断

```scala
'Fuin === 'active_user
```

scala中的`===`是运算符重构



