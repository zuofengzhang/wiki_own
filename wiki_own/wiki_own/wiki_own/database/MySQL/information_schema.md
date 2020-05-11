---
title: "MySQL information_schema数据库"
layout: post
date: 2019-03-25 11:00:00
category: DataBase
tag:
 - MySQL
 - SQL

share: true
comments: true
---



information_schema这这个数据库中保存了MySQL服务器所有数据库的信息。
如数据库名，数据库的表，表栏的数据类型与访问权限等。
再简单点，这台MySQL服务器上，到底有哪些数据库、各个数据库有哪些表，
每张表的字段类型是什么，各个数据库要什么权限才能访问，等等信息都保存在information_schema里面。

选用MySQL版本 5.6.25

```sql
select version();
+------------+
| version()  |
+------------+
| 5.6.25-log |
+------------+
1 row in set (0.00 sec)
```

```sql
use information_schema;
show tables;
+---------------------------------------+
| Tables_in_information_schema          |
+---------------------------------------+
| CHARACTER_SETS                        |
| COLLATIONS                            |
| COLLATION_CHARACTER_SET_APPLICABILITY |
| COLUMNS                               |
| COLUMN_PRIVILEGES                     |
| ENGINES                               |
| EVENTS                                |
| FILES                                 |
| GLOBAL_STATUS                         |
| GLOBAL_VARIABLES                      |
| KEY_COLUMN_USAGE                      |
| OPTIMIZER_TRACE                       |
| PARAMETERS                            |
| PARTITIONS                            |
| PLUGINS                               |
| PROCESSLIST                           |
| PROFILING                             |
| REFERENTIAL_CONSTRAINTS               |
| ROUTINES                              |
| SCHEMATA                              |
| SCHEMA_PRIVILEGES                     |
| SESSION_STATUS                        |
| SESSION_VARIABLES                     |
| STATISTICS                            |
| TABLES                                |
| TABLESPACES                           |
| TABLE_CONSTRAINTS                     |
| TABLE_PRIVILEGES                      |
| TRIGGERS                              |
| USER_PRIVILEGES                       |
| VIEWS                                 |
| INNODB_LOCKS                          |
| INNODB_TRX                            |
| INNODB_SYS_DATAFILES                  |
| INNODB_LOCK_WAITS                     |
| INNODB_SYS_TABLESTATS                 |
| INNODB_CMP                            |
| INNODB_METRICS                        |
| INNODB_CMP_RESET                      |
| INNODB_CMP_PER_INDEX                  |
| INNODB_CMPMEM_RESET                   |
| INNODB_FT_DELETED                     |
| INNODB_BUFFER_PAGE_LRU                |
| INNODB_SYS_FOREIGN                    |
| INNODB_SYS_COLUMNS                    |
| INNODB_SYS_INDEXES                    |
| INNODB_FT_DEFAULT_STOPWORD            |
| INNODB_SYS_FIELDS                     |
| INNODB_CMP_PER_INDEX_RESET            |
| INNODB_BUFFER_PAGE                    |
| INNODB_CMPMEM                         |
| INNODB_FT_INDEX_TABLE                 |
| INNODB_FT_BEING_DELETED               |
| INNODB_SYS_TABLESPACES                |
| INNODB_FT_INDEX_CACHE                 |
| INNODB_SYS_FOREIGN_COLS               |
| INNODB_SYS_TABLES                     |
| INNODB_BUFFER_POOL_STATS              |
| INNODB_FT_CONFIG                      |
+---------------------------------------+
59 rows in set (0.01 sec)
```

information_schema 数据库中有59张表， 分别存储了如下的信息:
[参考官方文档](https://dev.mysql.com/doc/refman/5.6/en/information-schema.html)

| SCHEMATA | 提供了当前mysql实例中所有数据库的信息，show databases的结果取之此表。|
| --- | --- |
| TABLES | 提供了关于数据库中的表的信息（包括视图），详细表述了某个表属于哪个schema，表类型，表引擎，创建时间等信息，show tables from schemaname的结果取之此表。 |
| COLUMNS | 提供了表中的列信息，详细表述了某张表的所有列以及每个列的信息，show columns from  schemaname.tablename的结果取之此表。|
| STATISTICS | 提供了关于表索引的信息，show index from schemaname.tablename的结果取之此表。|
| USER_PRIVILEGES（用户权限） | 给出了关于全程权限的信息，该信息源自mysql.user授权表，是非标准表。|
| SCHEMA_PRIVILEGES（方案权限） | 给出了关于方案（数据库）权限的信息，该信息来自mysql.db授权表，是非标准表。 |
| TABLE_PRIVILEGES（表权限） | 给出了关于表权限的信息，该信息源自mysql.tables_priv授权表，是非标准表。|
| COLUMN_PRIVILEGES（列权限） | 给出了关于列权限的信息，该信息源自mysql.columns_priv授权表，是非标准表。|
| CHARACTER_SETS（字符集） | 提供了mysql实例可用字符集的信息，SHOW CHARACTER SET结果集取之此表。|
| COLLATIONS | 提供了关于各字符集的对照信息。|
| COLLATION_CHARACTER_SET_APPLICABILITY | 指明了可用于校对的字符集，这些列等效于SHOW COLLATION的前两个显示字段。|
| TABLE_CONSTRAINTS | 描述了存在约束的表，以及表的约束类型。|
| KEY_COLUMN_USAGE | 描述了具有约束的键列。|
| ROUTINES | 提供了关于存储子程序（存储程序和函数）的信息，此时，ROUTINES表不包含自定义函数（UDF），名为|“mysql.proc name”的列指明了对应于INFORMATION_SCHEMA.ROUTINES表的mysql.proc表列。 |
| VIEWS | 给出了关于数据库中的视图的信息，需要有show views权限，否则无法查看视图信息。|
| TRIGGERS | 提供了关于触发程序的信息，必须有super权限才能查看该表。|



information_schema的表schemata中的列schema_name记录了所有数据库的名字
information_schema的表tables中的列table_schema记录了所有数据库的名字
information_schema的表tables中的列table_name记录了所有数据库的表的名字
information_schema的表columns中的列table_schema记录了所有数据库的名字
information_schema的表columns中的列table_name记录了所有数据库的表的名字
information_schema的表columns中的列column_name记录了所有数据库的表的列的名字

