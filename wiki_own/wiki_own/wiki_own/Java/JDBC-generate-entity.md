---
title: JDBC-根据数据表生成Entity
layout: post
date: 2016-08-12 11:03:00
category: Java
keywords:
 - Java
 - JDBC

share: true
comments: true
---

[上文][a-jdbc] 主要介绍了JDBC的使用和目前框架中的优化等，本上主要介绍一个非常实用的例子: **根据数据表生成Entity**



# Entity

`Entity`或者`DTO`是Java中与数据表相对应的实体，将对象的属性封装有利于数据操作(如从数据库中读取某个实体，对这个实体操作，修改，新增等)，根据数据表生成Entity是必须应对的问题（当然可以选择手动敲），目前常用的方法有：

- Hibernate工具: Eclipse和Intellij IDEA有相应的插件
- Mybatis-generator: 是生成Mybatis的Mapper、Entity和Dao的工具，[关于如何与Maven结合使用请参考][a-maven-mybatis-generator]

本文编写的工具，直接使用JDBC API编写，不依赖IDE和ORM。



# DatabaseMetaData

[`DatabaseMetaData`][a-DatabaseMetaData]是`java.sql`中提供的关于数据库整体信息的API，这个接口使用驱动程序实现的，数据库的开发商对这个接口提供支持并使用不同的方式实现。在本例中使用的是`MySQL`数据库，也是由`mysql-connector-java.jar`提供的。

[`DatabaseMetaData`][a-DatabaseMetaData] 提高了数据库的整体信息，包括了数据表的信息。一个重要的方法:

```java
getColumns

ResultSet getColumns(String catalog,
                     String schemaPattern,
                     String tableNamePattern,
                     String columnNamePattern)
                     throws SQLException
获取可在指定类别中使用的表列的描述。
仅返回与类别、模式、表和列名称标准匹配的列描述。它们根据 TABLE_CAT、TABLE_SCHEM、TABLE_NAME 和 ORDINAL_POSITION 进行排序。

每个列描述都有以下列：

  TABLE_CAT String => 表类别（可为 null）
  TABLE_SCHEM String => 表模式（可为 null）
  TABLE_NAME String => 表名称
  COLUMN_NAME String => 列名称
  DATA_TYPE int => 来自 java.sql.Types 的 SQL 类型
  TYPE_NAME String => 数据源依赖的类型名称，对于 UDT，该类型名称是完全限定的
  COLUMN_SIZE int => 列的大小。
  BUFFER_LENGTH 未被使用。
  DECIMAL_DIGITS int => 小数部分的位数。对于 DECIMAL_DIGITS 不适用的数据类型，则返回 Null。
  NUM_PREC_RADIX int => 基数（通常为 10 或 2）
  NULLABLE int => 是否允许使用 NULL。
  columnNoNulls - 可能不允许使用 NULL 值
  columnNullable - 明确允许使用 NULL 值
  columnNullableUnknown - 不知道是否可使用 null
  REMARKS String => 描述列的注释（可为 null）
  COLUMN_DEF String => 该列的默认值，当值在单引号内时应被解释为一个字符串（可为 null）
  SQL_DATA_TYPE int => 未使用
  SQL_DATETIME_SUB int => 未使用
  CHAR_OCTET_LENGTH int => 对于 char 类型，该长度是列中的最大字节数
  ORDINAL_POSITION int => 表中的列的索引（从 1 开始）
  IS_NULLABLE String => ISO 规则用于确定列是否包括 null。
  YES --- 如果参数可以包括 NULL
  NO --- 如果参数不可以包括 NULL
  空字符串 --- 如果不知道参数是否可以包括 null
  SCOPE_CATLOG String => 表的类别，它是引用属性的作用域（如果 DATA_TYPE 不是 REF，则为 null）
  SCOPE_SCHEMA String => 表的模式，它是引用属性的作用域（如果 DATA_TYPE 不是 REF，则为 null）
  SCOPE_TABLE String => 表名称，它是引用属性的作用域（如果 DATA_TYPE 不是 REF，则为 null）
  SOURCE_DATA_TYPE short => 不同类型或用户生成 Ref 类型、来自 java.sql.Types 的 SQL 类型的源类型（如果 DATA_TYPE 不是 DISTINCT 或用户生成的 REF，则为 null）
  IS_AUTOINCREMENT String => 指示此列是否自动增加
    YES --- 如果该列自动增加
    NO --- 如果该列不自动增加
    空字符串 --- 如果不能确定该列是否是自动增加参数
  COLUMN_SIZE 列表示给定列的指定列大小。对于数值数据，这是最大精度。对于字符数据，这是字符长度。对于日期时间数据类型，这是 String 表示形式的字符长度（假定允许的最大小数秒组件的精度）。对于二进制数据，这是字节长度。对于 ROWID 数据类型，这是字节长度。对于列大小不适用的数据类型，则返回 Null。

参数：
  catalog - 类别名称；它必须与存储在数据库中的类别名称匹配；该参数为 "" 表示获取没有类别的那些描述；为 null 则表示该类别名称不应该用于缩小搜索范围
  schemaPattern - 模式名称的模式；它必须与存储在数据库中的模式名称匹配；该参数为 "" 表示获取没有模式的那些描述；为 null 则表示该模式名称不应该用于缩小搜索范围
  tableNamePattern - 表名称模式；它必须与存储在数据库中的表名称匹配
  columnNamePattern - 列名称模式；它必须与存储在数据库中的列名称匹配
返回：
	ResultSet - 每一行都是一个列描述
抛出：
	SQLException - 如果发生数据库访问错误
另请参见：
	getSearchStringEscape()
```



```java
Connection conn = DatabaseUtils.openConnection(); // 得到数据库连接
DatabaseMetaData databaseMetaData = conn.getMetaData();// 获取数据库 MetaData
//获取数据表的表列描述,存放到 ResultSet
ResultSet resultSet = databaseMetaData.getColumns(null, "%", tableName, "%");
while (resultSet.next()) {
  String columnName = resultSet.getString("COLUMN_NAME"); //获取列名
  String dataType = resultSet.getString("TYPE_NAME"); //列的数据类型
  int columnSize = resultSet.getInt("COLUMN_SIZE"); //列的大小
  String remarks = resultSet.getString("REMARKS"); //列的备注
  colnames.add(getCamelStr(columnName));
  colTypes.add(dataType);
  colSizes.add(columnSize);
  comments.add(remarks);
}
```

# 生成类

生成POJO，包含字段和Setter和Getter

## 生成字段

```java
/**
     * 解析输出属性
     *
     * @return
     */
private void processAllAttrs(StringBuffer sb) {
  for (int i = 0; i < colnames.size(); i++) {
    String colName = colnames.get(i);
    String colType = colTypes.get(i);
    String comment = comments.get(i);
    if (comments != null && colName != null) {
      sb.append("\t/**\n\t *  " + comment + "\n\t **/\n");
    }
    sb.append("\tprivate " + sqlType2JavaType(colType) + " " + colName + ";\r\n");
  }
}
```



## 生成Getter和Setter

```java
for (int i = 0; i < colnames.size(); i++) {
  String colName = colnames.get(i);
  String colType = colTypes.get(i);
  sb.append("\tpublic void set" + initcap(colName) + "(" + sqlType2JavaType(colType) + " " + colName
            + "){\r\n");
  sb.append("\t\tthis." + colName + "=" + colName + ";\r\n");
  sb.append("\t}\r\n\r\n");

  sb.append("\tpublic " + sqlType2JavaType(colType) + " get" + initcap(colName) + "(){\r\n");
  sb.append("\t\treturn " + colName + ";\r\n");
  sb.append("\t}\r\n\r\n");
}
```

## 组合成类文件

```java
StringBuffer sb = new StringBuffer();
sb.append("package " + packagePath + ";\r\n\r\n");
if (needUtilPacket) {
	sb.append("import java.util.Date;\r\n");
}
if (needSqlPacket) {
	sb.append("import java.sql.*;\r\n\r\n\r\n");
}
sb.append("public class " + initcap(tableName) + " {\r\n\r\n");
processAllAttrs(sb);//生成字段
sb.append("\r\n");
generatGetterSetter(sb);//生成getter和setter
sb.append("}\r\n");
System.out.println(sb.toString());
return sb.toString();
```



---

【参考文献】

[a-jdbc]: /Java/jdbc/
[a-maven-mybatis-generator]: JavaWeb/MyBatis/generator/
[a-DatabaseMetaData]: http://tool.oschina.net/uploads/apidocs/jdk-zh/java/sql/DatabaseMetaData.html