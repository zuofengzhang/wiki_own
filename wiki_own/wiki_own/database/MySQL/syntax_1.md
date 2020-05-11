---
title: "【基础】MySQL语法基础"
layout: post
date: 2016-12-27 11:00:00
category: DataBase
tag:
 - MySQL
 - SQL

share: true
comments: true
---

本文主要介绍SQL语言以及部分MySQL的基础知识。

# MySQL
MySQL的默认的端口号是3306 超级用户是`root`

MySql引擎

![][p-MySql-Engine]


# MySQL提示符 命令与语法规范
MySQL的所有的指令都是以结束符结束的, 回车提交命令. 默认的提示符是`;`号.

## 修改提示符
```sql
prompt localhost;
将提示符修改为localhost
```
之后每一行的提示符由`mysql>`变为`localhost>`， 还可以使用通配符修改提示符。


## 常用指令

通过`SELECT`执行内置函数

```sql
SELECT VERSION(); //显示当前服务器版本
+------------+
| VERSION()  |
+------------+
| 5.5.19-log |
+------------+

SELECT NOW(); //显示当前日期时间
+---------------------+
| NOW()               |
+---------------------+
| 2016-04-12 08:51:44 |
+---------------------+

SELECT USER(); //显示当前用户
+----------------+
| USER()         |
+----------------+
| root@localhost |
+----------------+
```

## 语法规范：
- 关键字和函数名称必须全部大写
- 数据库名称 表名称 字段名称全部小写
- SQL语句必须以分号结尾

命名规则：
- 可读性原则：驼峰提高可读性
- 表意性原则：对象的名字能反应对象的意义
- 长名原则：少用缩写

# 操作数据库

创建数据库

```sql
CREATE {DATABASE|SCHEMA}[IF NOT EXISTS] db_name
[DEFAULT] CHARACTER SET [=] charset_name
--- MySQL中SCHEMA与DATABASE名称相同
```

查看当前MySql服务器下的数据库

```sql
SHOW {DATABASES|SCHEMAS}
[LIKE 'pattern'|WHERE EXPR]
```

修改数据库

```sql
ALTER {DATABASE|SCHEMA}[db_name]
[DEFAULT]CHARACTER SET [=] charset_name
```

删除数据库

```sql
DROP {DATABASE|SCHEMA}[IF NOT EXISTS] db_name
```

# 数据类型
MySQL中的数据类型主要有整形 浮点型 字符型和日期时间型.
字段的类型影响着存储容量和数据查询性能。当可以选择多种类型时，优先选择数字类型，
其次是日期和二进制类型，最后是字符类型。相同级别的类型优先选择省空间的类型。

![][p-datatype]

对于上述原则主要从下面两个方面考虑：
1. 在对数据进行比较时(查询条件、JOIN条件及排序)操作时，
   **同样的数据，字符处理往往比数字处理慢**
2. 在数据库中，数据处理以页为单位，**列的长度越小，利于性能提升**

char与varchar选择:
1. 如果列中要存储的数据长度差不多是一致的，则应该考虑用char；否则应该用varchar。
2. 如果列中的最大数据长度小于50Byte，则一般也考虑用char.(当然，如果这个列很少用，
   则基于节省空间和较少I/O的考虑，还是可以选择用varchar)
3. 一般不宜定义大于50Byte的char类型列。

## 整形

![][p-datatype-number]

## 浮点型

![][p-datatype-float]

## 日期时间型

![][p-datatype-dateTime]

## 字符型

![][p-datatype-string]



# 操作数据表

## 打开数据库

```sql
USE db_name; //打开数据库
```
## 创建数据表

```sql
CREATE TABLE [IF NOT EXISTS] table_name(
  column_name data_type,
  ....
)
```

查看创建当前数据库的语句:

```sql
CREATE TABLE provinces(
    id SMALLINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    pname VARCHAR(20) NOT NULL
  );
显示创建数据库的语句
SHOW CREATE TABLE provinces;
+-----------+-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| Table     | Create Table                                                                                                                                                                |
+-----------+-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| provinces | CREATE TABLE `provinces` (
  `id` smallint(5) unsigned NOT NULL AUTO_INCREMENT,
  `pname` varchar(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1 |
+-----------+-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
1 row in set (0.00 sec)
```

以表格的形式显示列和列的属性

```sql
show columns from provinces;
+-------+----------------------+------+-----+---------+----------------+
| Field | Type                 | Null | Key | Default | Extra          |
+-------+----------------------+------+-----+---------+----------------+
| id    | smallint(5) unsigned | NO   | PRI | NULL    | auto_increment |
| pname | varchar(20)          | NO   |     | NULL    |                |
+-------+----------------------+------+-----+---------+----------------+
2 rows in set (0.03 sec)
desc provinces;
+-------+----------------------+------+-----+---------+----------------+
| Field | Type                 | Null | Key | Default | Extra          |
+-------+----------------------+------+-----+---------+----------------+
| id    | smallint(5) unsigned | NO   | PRI | NULL    | auto_increment |
| pname | varchar(20)          | NO   |     | NULL    |                |
+-------+----------------------+------+-----+---------+----------------+
2 rows in set (0.00 sec)
```



## 查看数据库中的数据表列表

```sql
SHOW TABLES [FROM db_name]
[LIKE 'pattern'|WHERE expr]
```

## 插入记录

```sql
INSERT [INTO] table_name [(column_name,...)] VALUES(var,...)
```

## 记录查找

```sql
SELECT expr,... FROM table_name
```

select 子句中可以包含子查询



## 约束

1. 约束保证数据的完整性和一致性
2. 约束分为表级约束和列级约束
3. 约束类型包括:
    - NOT NULL 非空元素
    - PRIMARY KEY 主键约束
    - UNIQUE KEY 唯一约束
    - DEFAULT 默认约束
    - FOREIGN KEY 外键约束.

### 自增长 AUTO_INCREMENT
1. 自动编号，且该字段必须是主键：
   AUTO_INCREMENT必须是主键，主键不一定AUTO_INCREMENT
2. 默认情况下，其实值是1，每次增量是1

插入数据时可以使用`DEFAULT`或者`NULL`为自增长字段赋值，以生成自增长数据。

例如在上面的`provinces`表中, 插入数据

```sql
insert into provinces values(null,'');
Query OK, 1 row affected (0.08 sec)
insert into provinces values(DEFAULT,'');
Query OK, 1 row affected (0.08 sec)
 select * from provinces;
+----+-------+
| id | pname |
+----+-------+
|  1 |       |
|  2 |       |
+----+-------+
2 rows in set (0.00 sec)
```

### 主键约束 PRIMARY KEY
1. 主键约束
2. 每张数据表只能存在一个主键
3. 主键保证记录的唯一性
4. 主键自动为`NOT NULL`

### 唯一约束
1. 保证记录的唯一性
2. 字段可以为空值(NULL) 且可以多个为空
3. 每张表可以有多个唯一约束

### 默认值约束 DEFAULT
当插入记录时， 如果没有明确为字段赋值，则自动赋值为默认值。

```sql
CREATE TABLE tb6(
    id SMALLINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(20) NOT NULL UNIQUE KEY,
    sex ENUM('1','2','3') DEFAULT '3'
);
```
查看表格结构

```sql
SHOW COLUMNS FROM tb6;
+----------+----------------------+------+-----+---------+----------------+
| Field    | Type                 | Null | Key | Default | Extra          |
+----------+----------------------+------+-----+---------+----------------+
| id       | smallint(5) unsigned | NO   | PRI | NULL    | auto_increment |
| username | varchar(20)          | NO   | UNI | NULL    |                |
| sex      | enum('1','2','3')    | YES  |     | 3       |                |
+----------+----------------------+------+-----+---------+----------------+
```

使用默认值插入记录

```sql
//使用DEFAULT来生成AUTO_INCREMENT
INSERT INTO tb6 VALUES(Default,"李四",DEFAULT);
Query OK, 1 row affected (0.08 sec)

Select *  from tb6;
+----+----------+------+
| id | username | sex  |
+----+----------+------+
|  1 | 李四   | 3    |
+----+----------+------+
1 row in set (0.00 sec)
```

指定字段为枚举类型: 与Java中的枚举类型不同, sql中的枚举只是限制取值范围.

### 外键约束
要求
1. 父表和字表必须使用相同的存储引擎, 而且禁止使用临时表
2. 数据表的引擎必须只能是InnoDB
3. 外键列和参照列必须具有相似的数据类型, 其中数字的长度或是否有符号位必须相同;
    而字符的长度则可以不同.
4. 外键列和参照列必须创建索引.如果外键列不存在索引, MySQL将自动创建索引.

实例

```sql
CREATE TABLE provinces(
    id SMALLINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    pname VARCHAR(20) NOT NULL
  );

CREATE TABLE users(
   id SMALLINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
   username VARCHAR(10) NOT NULL,
   pid SMALLINT UNSIGNED,
   FOREIGN KEY(pid) REFERENCES provinces(id) );
Query OK, 0 rows affected (0.08 sec)

SHOW INDEXES FROM provinces\G
*************************** 1. row ***************************
       Table: provinces
  Non_unique: 0
    Key_name: PRIMARY
Seq_in_index: 1
 Column_name: id
   Collation: A
 Cardinality: 0
    Sub_part: NULL
      Packed: NULL
        Null:
  Index_type: BTREE
     Comment:
Index_comment:
1 row in set (0.00 sec)
```

外键约束的操作操作
1. CASCADE: 从父表删除或更新且自动删除或更新子表中匹配的行
2. SET NULL: 从父表删除或更新行, 并设置子表中的外键列为NULL.  如果使用该选项, 必须保证子表列没有指定NOT NULL
3. RESTRICT: 拒绝对父表的删除或更新操作
4. NO ACTION: 标准SQL关键字, 在MySQL中与RESTRICT相同

### 表级约束与列级约束 ??
对于一个数据列建立的约束, 称为列级约束.
对多个数据列建立的约束, 称为表级约束.
列级约束既可以在列定义时声明,也可以在列定义后声明.
表级约束只能在列级约束后声明.

## 修改数据表

```sql
ALTER TABLE table_name ADD [COLUMN] column_name
  column_definition [FIRST|AFTER column_name]
添加多列
ALTER TABLE table_name ADD [COLUMN]
  (column_name column_definition, ....)
删除列
ALTER TABLE table_name DROP [COLUMN] column_name
添加主键约束
ALTER TABLE table_name ADD [CONSTRAINT [symbol]]
  PRIMARY KEY [Index_type](index_column_name,...)
添加唯一约束
ALTER TABLE table_name ADD [CONSTRAINT [symbol]]
  UNIQUE [INDEX|KEY][index_name][index_type]
  (index_column_name,...)
添加/删除默认约束
ALTER TABLE table_name ALTER [COLUMN] column_name
{SET DEFAULT litera}
如:
  ALTER TABLE users2 ALTER age DROP DEFAULT;
删除主键约束
ALTER TABLE table_name DROP PRIMARY Key
删除唯一约束
ALTER TABLE table_name DROP {INDEX|KEY} index_name
删除外键约束
ALTER TABLE table_name DROP FOREIGN KEY fk_symbol
修改列定义
ALTER TABLE table_name MODIFY [COLUMN] column_name
  column_definition [FIRST|AFTER column_name]
修改列名称
ALTER TABLE table_name CHANGE [COLUMN] old_column_name
  new_column_name column_definition [FIRST|AFTER column_name]
数据表更名
ALTER TABLE table_name RENAME [TO|AS] new_table_name
或
RENAME TABLE table_name TO new_table_name
  [, table_name2 TO new_table_name2] ...
```

# 操作数据表中的记录

## 插入记录

```sql
INSERT [INTO] table_name [(column_name,....)] {VALUES|VALUE}
({expr|DEFAULT},...),(...),...

INSERT [INTO] table_name SET column_name={expr|DEFAULT},...
此方法可以使用子查询

INSERT [INTO] table_name [(column_name,...)] SELECT ...
```

## 单表更新

单表更新，可以根据字段的当前值，进行判断。

```sql
-- 将user_info表中，user_id>1000的信息状态置反。
UPDATE user_info
SET `status`= (CASE WHEN 'status'=1 THEN 0 WHEN `status`=0 THEN 1 END)
WHERE `user_id` > 1000 ;
-- 将user_info表中的real_name去掉空格
UPDATE user_info
SET real_name=TRIM(real_name);
```





## 单表删除

## 查询表达式解析

## where语句进行条件查询

## group by语句对查询结果分组

## having语句设置分组条件

## order by语句对查询结果排序

## limit语句限制查询数量

```sql
--语法：
SELECT * FROM table LIMIT [offset,] rows | rows OFFSET offset
--举例：
select * from table limit 5; --返回前5行
select * from table limit 0,5; --同上，返回前5行
---为了检索从某一个偏移量到记录集的结束所有的记录行，可以指定第二个参数为 -1：
mysql> SELECT * FROM table LIMIT 95,-1; --- 检索记录行 96-last.
select * from table limit 5,10; --返回6-15行
```

当一个查询语句偏移量offset很大的时候，如`select * from table limit 10000,10 `,
最好不要直接使用limit，而是先获取到offset的id后，再直接使用limit size来获取数据。
效果会好很多。** 想办法减少offset **

```sql
select * From customers Where customer_id >=(
  --- 查找第1000条记录对应的id, id是索引, 检索速度快
  select customer_id From customers Order By customer_id limit 10000,1
) limit 10;
```

** 通过将查询条件(即where子句)的字段加索引,可以大大提高查询速度. **


# 子查询与连接

## 内连接 join从句
SQL标准中有5种连接：

- 内连接 inner
- 全外连接 full outer
- 左外连接 left outer
- 右外连接 right outer
- 交叉连接 cross

### 内连接 inner join
取两个表的交集， 即公共部分

![][p-inner-join]

```sql
SELECT a.`column1`, a.`column2`, b.`column3`
FROM `table_name1` AS a
INNER JOIN `table_name2` AS b
ON a.`column4`=b.`column5`;
```

### 左外连接 left outer join
![][p-left-outer-join]

右图中的部分， 可以用于替换`NOT IN`

```sql
SELECT a.`column1`, a.`column2`, b.`column3`
FROM `table_name1` AS a
LEFT JOIN `table_name2` AS b
ON a.`column4`=b.`column5`
WHERE b.`column3` IS NOT NULL;
```

### 右外连接 right outer join
![][p-right-outer-join]

右图中的部分， 可以用于替换`NOT IN`

```sql
SELECT a.`column1`, a.`column2`, b.`column3`
FROM `table_name1` AS a
RIGHT JOIN `table_name2` AS b
ON a.`column4`=b.`column5`
WHERE a.`column3` IS NOT NULL;
```
### 全外连接 FULL OUTER JOIN
![][p-full-outer-join]

MySQL 中默认是不包含全外连接查询的， 可以通过其他方式实现同样的效果。

```sql
SELECT a.`column1`, a.`column2`, b.`column3`
FROM `table_name1` AS a
LEFT JOIN `table_name2` AS b
ON a.`column4`=b.`column5`
UNION ALL
SELECT a.`column1`, a.`column2`, b.`column3`
FROM `table_name1` AS a
RIGHT JOIN `table_name2` AS b
ON a.`column4`=b.`column5`
```
### 交叉连接 CROSS JOIN
笛卡尔积
A表中的每一条记录和B表中的每一条记录组合: 结果的长度为A的长度×B的长度.

```sql
SELECT a.`column1`, a.`column2`, b.`column3`
FROM `table_name1` AS a
CROSS JOIN `table_name2` AS b
```



# 函数
函数主要分为字符函数 数值函数 日期时间函数 加密函数

## 字符函数

![][p-function-Stirng01]
![][p-function-Stirng02]

对特殊字符的Escape

```sql
SELECT * FROM test WHERE first_name LIKE '%1%%' ESCAPE '1'
表示1后面的%是真实的字符而不是转意字符
```

### substring

`SUBSTR (str, pos)`

由 <str> 中，选出所有从第 <pos> 位置开始的字元。请注意，这个语法不适用于 SQL Server 上。

`SUBSTR (str, pos, len)`

由 <str> 中的第 <pos> 位置开始，选出接下去的 <len> 个字元。

## 数值函数

![][p-function-Number01]

## 比较运算符和函数

![][p-function-Compare01]

## 日期时间函数

![][p-function-DateTime01]

[详细请看][a-date-time-function]

```sql
SELECT DATE_ADD('2016-04-12', INTERVAL 1 YEAR);
+-----------------------------------------+
| DATE_ADD('2016-04-12', INTERVAL 1 YEAR) |
+-----------------------------------------+
| 2017-04-12                              |
+-----------------------------------------+

SELECT DATE_ADD('2016-04-12', INTERVAL 3 WEEK);
+-----------------------------------------+
| DATE_ADD('2016-04-12', INTERVAL 3 WEEK) |
+-----------------------------------------+
| 2016-05-03                              |
+-----------------------------------------+

SELECT DATE_FORMAT('2016-3-2','%m/%d/%Y');
+------------------------------------+
| DATE_FORMAT('2016-3-2','%m/%d/%Y') |
+------------------------------------+
| 03/02/2016                         |
+------------------------------------+
```



### 时间与时间戳的转换

1. unix_timestamp

   将时间转化为时间戳。（date 类型数据转换成 timestamp 形式整数）

   没传时间参数则取当前时间的时间戳

   ```sql
   MySQL> select unix_timestamp();

   +------------------+
   | unix_timestamp() |
   +------------------+
   |       1361586358 |
   +------------------+
   1 row in set (0.01 sec)
   mysql> select unix_timestamp('2013-01-01 10:10:10');
   +---------------------------------------+
   | unix_timestamp('2013-01-01 10:10:10') |
   +---------------------------------------+
   |                            1357006210 |
   +---------------------------------------+
   1 row in set (0.00 sec)
   ```

2. from_unixtime

   将timestamp 形式整数 转化为 date类型

   ```sql
   mysql>  select from_unixtime(1355272360);
   +---------------------------+
   | from_unixtime(1355272360) |
   +---------------------------+
   | 2012-12-12 08:32:40       |
   +---------------------------+
   1 row in set (0.00 sec)
   ```

   当然也可以指定输出的时间格式：
   ```sql
   mysql>  select from_unixtime(1355272360,'%Y%m%d');
   +------------------------------------+
   | from_unixtime(1355272360,'%Y%m%d') |
   +------------------------------------+
   | 20121212                           |
   +------------------------------------+
   ```

3. 关于mysql 时间戳的限制

     目前timestamp 所能表示的范围在 1970年  -  2038年之间 。

     超过这个范围 得到的时间将会溢出 得到的时间是null.

   ```sql
   mysql>  select from_unixtime(0);
   +---------------------+
   | from_unixtime(0)    |
   +---------------------+
   | 1970-01-01 08:00:00 |
   +---------------------+
   mysql> select from_unixtime(2147483647);
   +---------------------------+
   | from_unixtime(2147483647) |
   +---------------------------+
   | 2038-01-19 11:14:07       |
   +---------------------------+
   1 row in set (0.00 sec)
   ```

4. SQL中的timestamp与Java中的转换

   SQL中的时间戳是从1970年1月1日 8:00:00 开始的毫秒数

   | SQL中的时间戳                   | 1970-1-1 8:00:00 开始的毫秒数  |
   | -------------------------- | ------------------------ |
   | java.util.Date()           | 内置时间 getYear()是从1900年开始的 |
   | java.util.Date().getTime() | 从1970-1-1 8:00:00 开始的微妙数 |
   | java.sql.Timestamp()       | 与java.util.Date()相同      |
   | java.sql.Date              | 只保存年月日，没有时分秒             |
   |                            |                          |


   ```
   Timestamp是一个与 java.util.Date 类有关的瘦包装器 (thin wrapper)，它允许 JDBC API 将该类标识为 SQL TIMESTAMP 值。它添加保存 SQL TIMESTAMP 毫微秒值和提供支持时间戳值的 JDBC 转义语法的格式化和解析操作的能力。
   ```

   因此，在SQL中的时间戳与Java中传递进来的long型时间比较时，需要乘以1000.





## 信息函数

![][p-function-Information01]

## 聚合函数

![][p-function-Aggregate01]

## 加密函数

![][p-function-Encryption01]

password函数只适用于设置MySQL的密码

## XML支持

[xml支持](https://dev.mysql.com/doc/refman/5.5/en/xml-functions.html) :xml的函数主要有两个 ExtractValue 和  UpdateXML

### ExtractValue

​```sql
mysql> SELECT
    ->   ExtractValue('<a>ccc<b>ddd</b></a>', '/a') AS val1,
    ->   ExtractValue('<a>ccc<b>ddd</b></a>', '/a/b') AS val2,
    ->   ExtractValue('<a>ccc<b>ddd</b></a>', '//b') AS val3,
    ->   ExtractValue('<a>ccc<b>ddd</b></a>', '/b') AS val4,
    ->   ExtractValue('<a>ccc<b>ddd</b><b>eee</b></a>', '//b') AS val5;

+------+------+------+------+---------+
| val1 | val2 | val3 | val4 | val5    |
+------+------+------+------+---------+
| ccc  | ddd  | ddd  |      | ddd eee |
+------+------+------+------+---------+
   ```

### UpdateXML

​```sql
mysql> SELECT
    ->   UpdateXML('<a><b>ccc</b><d></d></a>', '/a', '<e>fff</e>') AS val1,
    ->   UpdateXML('<a><b>ccc</b><d></d></a>', '/b', '<e>fff</e>') AS val2,
    ->   UpdateXML('<a><b>ccc</b><d></d></a>', '//b', '<e>fff</e>') AS val3,
    ->   UpdateXML('<a><b>ccc</b><d></d></a>', '/a/d', '<e>fff</e>') AS val4,
    ->   UpdateXML('<a><d></d><b>ccc</b><d></d></a>', '/a/d', '<e>fff</e>') AS val5
    -> \G

*************************** 1. row ***************************
val1: <e>fff</e>
val2: <a><b>ccc</b><d></d></a>
val3: <a><e>fff</e><d></d></a>
val4: <a><b>ccc</b><e>fff</e></a>
val5: <a><d></d><b>ccc</b><d></d></a>
   ```



# 自定义函数

自定义函数(user-defined function,UDF)是一种对MySQL扩展的途径,
其用法与内置函数相同. 自定义函数的两个必要条件:
`参数`和`返回值`. 函数可以返回任意类型的值, 同样也可以接受这些类型的参数

```sql
CREATE FUNCTION function_name
RETURNS {STRING|INTEGER|REAL|DECIMAL}
routine_body
```
函数体
1. 由合法SQL语句构成
2. 可以是简单的SELECT或INSERT语句
3. 如果是复合结构,必须使用`BEGIN`...`END`语句
4. 复合结构可以包括声明,循环,控制结构

```sql
CREATE FUNCTION f1()
RETURNS VARCHAR(30)
RETURN DATE_FORMAT(NOW(),'%Y年%m月%d日 %H点%i分%s秒');

select f1();
+--------------------------------+
| f1()                           |
+--------------------------------+
| 2016年04月12日 10点51分24 |
+--------------------------------+
CREATE FUNCTION f2(num1 SMALLINT UNSIGNED, num2 SMALLINT UNSIGNED)
RETURNS FLOAT(10,2) UNSIGNED
RETURN (num1+num2)/2;

SELECT f2(35,46);
+-----------+
| f2(35,46) |
+-----------+
|     40.50 |
+-----------+

DELIMITER //
CREATE FUNCTION adduser(username VARCHAR(20))
RETURNS INT UNSIGNED
BEGIN
INSERT test(username) VALUES(username);
RETURN LAST_INSERT_ID();
END
//
DELIMITER ;
```


# 存储过程
SQL命令的执行过程

![][p-sql-execute]

存储过程是SQL语句和控制语句的预编译集合, 以一个名称存储并作为一个单元处理. 其优点有:
1. 增强SQL语句的功能和灵活性
2. 实现较快的执行速度
3. 减少网络流量

语法格式为:

```sql
CREATE
[DEFINER={USER|CURRENT_USER}]
PROCEDURE sp_name ([proc_parameter[,...]])
[characteristic ...] routine_body

proc_parameter:
[IN|OUT|INOUT]parameter_name type

调用
CALL sp_name([parameter[,...]])
CALL sp_name[()]
```

参数
- IN 表示该参数的值必须在调用存储过程时指定
- OUT 表示该参数的值可以被存储过程改变, 并且可以返回
- INOUT 表示该参数的调用时指定,并且可以被改变和返回

过程体
- 过程体由合法的SQL语句构成
- 过程体可以是任意SQL语句
- 过程体如果为复合结构则使用BEGIN...END语句
- 复合结构可以包含声明,循环,控制结果

实例

```sql
mysql> delimiter //
mysql> CREATE PROCEDURE removeUserById(IN ppid INT UNSIGNED)
    -> BEGIN
    -> DELETE FROM users WHERE id=ppid;
    -> END
    -> //
Query OK, 0 rows affected (0.00 sec)

mysql> DELIMITER ;
```

```sql
delimiter //
create procedure removeUserAndReturnUserNums(in pid int unsigned,out useNums int unsigned)
	begin
	delete from user where id=pid;
	select count(id) from user into useNums;
	end
	//
delimiter ;

调用
call removeUserAndReturnUserNums(27,@nums);
select @nums;
通过declare语句声明的变量是局部变量, 作用域只能在begin和end之间.
select @nums;
set @num=7;
这种声明方式声明的变量成为用户变量. 作用域是客户端,只在当前的客户端有效.
```

获取插入 删除 更新的记录总数.
select row_count();

```sql

delimiter //
create procedure removeUserAndReturnInfos(in p_age smallint unsigned,
    out deleteUsers smallint unsigned, out userCounts smallint unsigned)
begin
delete from user where age=p_age;
select row_count() into deleteUsers;
select count(id) from user into userCounts;
end
//
delimiter ;

call removeUserAndReturnInfos(20,@rm,@rl);
select @rm;
select @rl;
```

删除存储过程

```sql
DROP PROCEDURE [IF EXISTS] sp_name
```

存储过程与自定义函数的区别
1. 存储过程实现的功能更复杂一些 而函数的针对性更强
2. 存储过程可以返回多个值;函数只能有一个返回值
3. 存储过程一般独立的执行;而函数可以作为其他SQL语句的组成部分来出现.

创建存储过程或者自定义函数时需要通过`delimiter`语句修改定界符.





# 用户权限
在 MySQL5.7 中 user 表的 password 已换成了authentication_string。
注意：在注意需要执行 FLUSH PRIVILEGES 语句。 这个命令执行后会重新载入授权表。
如果你不使用该命令，你就无法使用新创建的用户来连接mysql服务器，除非你重启mysql服务器。
你可以在创建用户时，为用户指定权限，在对应的权限列中，在插入语句中设置为 'Y' 即可，用户权限列表如下：

```sql
Select_priv
Insert_priv
Update_priv
Delete_priv
Create_priv
Drop_priv
Reload_priv
Shutdown_priv
Process_priv
File_priv
Grant_priv
References_priv
Index_priv
Alter_priv
```

  除非你使用 LIKE 来比较字符串，否则MySQL的WHERE子句的字符串比较是不区分大小写的。 你可以使用 BINARY 关键字来设定WHERE子句的字符串比较是区分大小写的。
如下实例

```shell
root@host# mysql -u root -p password;
Enter password:*******
mysql> use RUNOOB;
Database changed
mysql> SELECT * from runoob_tbl \
          WHERE BINARY runoob_author='sanjay';
Empty set (0.02 sec)
```

为了处理这种情况，MySQL提供了三大运算符:

- IS NULL: 当列的值是NULL,此运算符返回true。
- IS NOT NULL: 当列的值不为NULL, 运算符返回true。
- <=>: 比较操作符（不同于=运算符），当比较的的两个值为NULL时返回true。
  关于 NULL 的条件比较运算是比较特殊的。你不能使用 = NULL 或 != NULL 在列中查找 NULL 值 。
  在MySQL中，NULL值与任何其它值的比较（即使是NULL）永远返回false，即 NULL = NULL 返回false 。
  MySQL中处理NULL使用IS NULL和IS NOT NULL运算符。

# 正则表达式
http://www.runoob.com/mysql/mysql-regexp.html

```sql
SELECT name FROM person_tbl WHERE name REGEXP '^st';
```

# 索引

索引分单列索引和组合索引。单列索引，即一个索引只包含单个列，一个表可以有多个单列索引，但这不是组合索引。组合索引，即一个索包含多个列。
创建索引时，你需要确保该索引是应用在 SQL 查询语句的条件(一般作为 WHERE 子句的条件)。
实际上，索引也是一张表，该表保存了主键与索引字段，并指向实体表的记录。
上面都在说使用索引的好处，但过多的使用索引将会造成滥用。因此索引也会有它的缺点：虽然索引大大提高了查询速度，同时却会降低更新表的速度，如对表进行INSERT、UPDATE和DELETE。因为更新表时，MySQL不仅要保存数据，还要保存一下索引文件。
建立索引会占用磁盘空间的索引文件。

## 创建索引
有四种方式来添加数据表的索引：

```sql
ALTER TABLE tbl_name ADD PRIMARY KEY (column_list):

该语句添加一个主键，这意味着索引值必须是唯一的，且不能为NULL。
ALTER TABLE tbl_name ADD UNIQUE index_name (column_list): 这条语句创建索引的值必须是唯一的（除了NULL外，NULL可能会出现多次）。
ALTER TABLE tbl_name ADD INDEX index_name (column_list): 添加普通索引，索引值可出现多次。
ALTER TABLE tbl_name ADD FULLTEXT index_name (column_list):该语句指定了索引为 FULLTEXT ，用于全文索引。
```

## 创建临时表
如果你退出当前MySQL会话，再使用 SELECT命令来读取原先创建的临时表数据，那你会发现数据库中没有该表的存在，因为在你退出时该临时表已经被销毁了。

```sql
CREATE TEMPORARY TABLE SalesSummary (
    -> product_name VARCHAR(50) NOT NULL
    -> , total_sales DECIMAL(12,2) NOT NULL DEFAULT 0.00
    -> , avg_unit_price DECIMAL(7,2) NOT NULL DEFAULT 0.00
    -> , total_units_sold INT UNSIGNED NOT NULL DEFAULT 0
);
```

# 复制表

```sql
SHOW CREATE TABLE runoob_tbl \G;
```

修改数据表名，执行SQL语句

```sql
INSERT INTO clone_tbl (runoob_id,
    ->                        runoob_title,
    ->                        runoob_author,
    ->                        submission_date)
    -> SELECT runoob_id,runoob_title,
    ->        runoob_author,submission_date
    -> FROM runoob_tbl;
```

# 元数据
查询结果信息  数据库和数据表的信息 服务器信息

```sql
SELECT VERSION( ) 服务器版本信息
SELECT DATABASE( )  当前数据库名 (或者返回空)
SELECT USER( )  当前用户名
SHOW STATUS 服务器状态
SHOW VARIABLES  服务器配置变量
show processList; 连接MySQL的所有线程的信息
SHOW  OPEN TABLES  WHERE In_use>0 所有打开的数据表

```

# 自增序列
在MySQL的客户端中你可以使用 SQL中的LAST_INSERT_ID( ) 函数来获取最后的插入表中的自增列的值 Java如何获取

## 重置序列

```shell
mysql> ALTER TABLE insect DROP id;
mysql> ALTER TABLE insect
    -> ADD id INT UNSIGNED NOT NULL AUTO_INCREMENT FIRST,
    -> ADD PRIMARY KEY (id);
```

## 设置序列的开始值
一般情况下序列的开始值为1，但如果你需要指定一个开始值100，那我们可以通过以下语句来实现：

```sql
mysql> CREATE TABLE insect
    -> (
    -> id INT UNSIGNED NOT NULL AUTO_INCREMENT = 100,
    -> PRIMARY KEY (id),
    -> name VARCHAR(30) NOT NULL, # type of insect
    -> date DATE NOT NULL, # date collected
    -> origin VARCHAR(30) NOT NULL # where collected
);
```

或者你也可以在表创建成功后，通过以下语句来实现：

```sql
mysql> ALTER TABLE t AUTO_INCREMENT = 100;
```



#  防止数据重复

设置字段为primary key或者unique索引

## INSERT IGNORE
INTO当插入数据时，在设置了记录的唯一性后，如果插入重复数据，将不返回错误，只以警告形式返回。 而REPLACE INTO into如果存在primary 或 unique相同的记录，则先删除掉。再插入新记录。

## 添加一个UNIQUE索引，如下所示：

```sql
CREATE TABLE person_tbl
(
   first_name CHAR(20) NOT NULL,
   last_name CHAR(20) NOT NULL,
   sex CHAR(10)
   UNIQUE (last_name, first_name)
);
```



# 统计重复数据

```sql
SELECT COUNT(*) as repetitions, last_name, first_name FROM person_tbl GROUP BY last_name, first_name HAVING repetitions > 1;
```

# 过滤重复数据

```sql
mysql> SELECT DISTINCT last_name, first_name
    -> FROM person_tbl
    -> ORDER BY last_name;
```
你也可以使用 GROUP BY 来读取数据表中不重复的数据：

```sql
mysql> SELECT last_name, first_name
    -> FROM person_tbl
    -> GROUP BY (last_name, first_name);
```

## 删除重复数据

如果你想删除数据表中的重复数据，你可以使用以下的SQL语句：

```sql
mysql> CREATE TABLE tmp SELECT last_name, first_name, sex
    ->                  FROM person_tbl;
    ->                  GROUP BY (last_name, first_name);
mysql> DROP TABLE person_tbl;
mysql> ALTER TABLE tmp RENAME TO person_tbl;
```

当然你也可以在数据表中添加 INDEX（索引） 和 PRIMAY KEY（主键）这种简单的方法来删除表中的重复记录。方法如下：

```sql
mysql> ALTER IGNORE TABLE person_tbl
    -> ADD PRIMARY KEY (last_name, first_name);
```




# SQL注入

防止SQL注入，我们需要注意以下几个要点：

1. 永远不要信任用户的输入。对用户的输入进行校验，可以通过正则表达式，或限制长度；对单引号和 双"-"进行转换等。
2. 永远不要使用动态拼装sql，可以使用参数化的sql或者直接使用存储过程进行数据查询存取。
3. 永远不要使用管理员权限的数据库连接，为每个应用使用单独的权限有限的数据库连接。
4. 不要把机密信息直接存放，加密或者hash掉密码和敏感的信息。
5. 应用的异常信息应该给出尽可能少的提示，最好使用自定义的错误信息对原始错误信息进行包装
6. sql注入的检测方法一般采取辅助软件或网站平台来检测，软件一般采用sql注入检测工具jsky，网站平台就有亿思网站安全平台检测工具。MDCSOFT SCAN等。采用MDCSOFT-IPS可以有效的防御SQL注入，XSS攻击等。

## SQL like语句注入
like查询时，如果用户输入的值有"_"和"%"，则会出现这种情况：用户本来只是想查询"abcd_"，查询结果中却有"abcd_"、"abcde"、"abcdf"等等；用户要查询"30%"（注：百分之三十）时也会出现问题。


# 备份与导入

```sql
mysqldump -u <USER_HOME> -p <DATABASE_NAME > news.sql   -- 输入后会让你输入进入MySQL的密码
-- 登陆
mysql> use <DATABASE_NAME>;
mysql> source news.sql;
-- 或者
mysql -u <USER_HOME> -p <DATABASE_NAME> < news.sql
-- 输入密码即可
```



> 更新记录：
> 1. 创建 2016-04-11
> 2. 添加 时间戳与Java中时间的区别  2016-12-27



----

[参考文献]:
1. [MySQL日期时间函数大全][a-date-time-function]

[a-date-time-function]: http://www.cnblogs.com/zeroone/archive/2010/05/05/1727659.html

[p-MySql-Engine]: /images/j2ee/MySQL/MySql-Engine.png
[p-datatype]: /images/j2ee/MySQL/MySql-DataType.png
[p-datatype-number]: /images/j2ee/MySQL/MySql-DataType-Number.png
[p-datatype-float]: /images/j2ee/MySQL/MySql-DataType-Float.png
[p-datatype-dateTime]: /images/j2ee/MySQL/MySql-DataType-DateTime.png
[p-datatype-string]: /images/j2ee/MySQL/MySql-DataType-String.png
[p-sql-execute]: /images/j2ee/MySQL/MySQL-execute.png
[p-function-Stirng01]: /images/j2ee/MySQL/MySQL-function-String01.png
[p-function-Stirng02]: /images/j2ee/MySQL/MySQL-function-String02.png
[p-function-Number01]: /images/j2ee/MySQL/MySQL-function-Number01.png
[p-function-Compare01]: /images/j2ee/MySQL/MySQL-function-Compare01.png
[p-function-DateTime01]: /images/j2ee/MySQL/MySQL-function-DateTime01.png
[p-function-Information01]: /images/j2ee/MySQL/MySQL-function-Information01.png
[p-function-Aggregate01]: /images/j2ee/MySQL/MySQL-function-Aggregate01.png
[p-function-Encryption01]: /images/j2ee/MySQL/MySQL-function-Encryption01.png
[p-inner-join]: /images/j2ee/MySQL/inner-join.png
[p-left-outer-join]: /images/j2ee/MySQL/left-outer-join.png
[p-right-outer-join]: /images/j2ee/MySQL/right-outer-join.png
[p-full-outer-join]: /images/j2ee/MySQL/full-outer-join.png
