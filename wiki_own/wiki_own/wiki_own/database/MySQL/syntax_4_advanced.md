---
layout: post
title: "MySQL高级语法"
date: 2016-12-27 11:00:00
category: DataBase
tag:
 - MySQL
 - SQL

share: true
comments: true
---

# replace into
`replace into t(id, update_time) values(1, now());`
或
`replace into t(id, update_time) select 1, now();`

`replace into` 跟 `insert` 功能类似，不同点在于：
`replace into` 首先尝试插入数据到表中，
1. 如果发现表中已经有此行数据（根据主键或者唯一索引判断）则先删除此行数据，然后插入新的数据。
2. 否则，直接插入新数据。

要注意的是：插入数据的表必须有主键或者是唯一索引！否则的话，`replace into` 会直接插入数据，这将导致表中出现重复的数据。
MySQL replace into 有三种形式：

1. `replace into tbl_name(col_name, ...) values(...)`
2. `replace into tbl_name(col_name, ...) select ...`
3. `replace into tbl_name set col_name=value, ...`

前两种形式用的多些。其中 “into” 关键字可以省略，不过最好加上 “into”，这样意思更加直观。另外，对于那些没有给予值的列，MySQL 将自动为这些列赋上默认值。

# MySQL高级
# 理解SQL语句
# 创建外键的SQL语句

## 创建数据表
```sql
CREATE TABLE a(
  id INT PRIMARY KEY,
  b_id INT,
  CONSTRAINT b_fk FOREIGN KEY (b_id) REFERENCES b(id)
);
```
## 修改数据表
```sql
[CONSTRAINT symbol] FOREIGN KEY [id] (index_col_name, ...)  
    REFERENCES tbl_name (index_col_name, ...)  
    [ON DELETE {RESTRICT | CASCADE | SET NULL | NO ACTION}]  
    [ON UPDATE {RESTRICT | CASCADE | SET NULL | NO ACTION}]  
```

```sql
ALTERT TABLE a
ADD CONSTRAINT b_fk FOREIGN KEY b_id REFERENCES b(id);
```

```sql
ALTERT TABLE a
ADD FOREIGN KEY b_fk(b_id)  REFERENCES b(id);
```

## 删除外键
```sql
ALTERT TABLE a DROP FOREIGN KEY b_fk;
```

MySQL `KEY...REFERENCES`修饰符添加一个`ON DELETE` 或`ON UPDATE`子句简化任务，它告诉了数据库在这种情况如何处理孤立任务

| 关键字       | 含义                                       |
| --------- | ---------------------------------------- |
| CASCADE   | 删除包含与已删除键值有参照关系的所有记录                     |
| SET NULL  | 修改包含与已删除键值有参照关系的所有记录，使用NULL值替换（只能用于已标记为NOT NULL的字段） |
| RESTRICT  | 拒绝删除要求，直到使用删除键值的辅助表被手工删除，并且没有参照时(这是默认设置，也是最安全的设置) |
| NO ACTION | 啥也不做                                     |

请注意，通过ON UPDATE 和 ON DELETE规则，设置MySQL能够实现自动操作时，如果键的关系没有设置好，可能会导致严重的数据破坏，
例如：如果一系列的表通过外键关系和ON DELETE CASCADE 规则连接时，任意一个主表的变化都会导致甚至只和原始删除有一些将要联系的记录在没有警告的情况被删除，所以，我们在操作之前还要检查这些规则的，操作之后还要再次检查.



# 事务
事务回滚

# 视图
# 触发器
# 序列号
# 权限



## 使用技巧

1. 如何更新使用过滤条件中包括自身的表?
   在MySQL中, 不允许更新的表不能出现在FROM从句中, 但是可以使用JOIN从句中.

  联合更新

```sql
UPDATE user1 a JOIN(
  SELECT b.`user_name`
  FROM user1 a INNER JOIN user2 b
  ON a.`user_name`= b.`user_name`
) b ON a.`user_name`=b.`user_name`
SET a.`over`=`齐天大圣`;
```

1. 优化子查询
2. 优化聚合查询

```sql
select a.user_name, b.timestr,b.kills  
from user a  
join user_kills b
on a.id=b.user_id
where b.kills=(
  select max(c.kills) from user_kills c where c.user_id=b.user_id
);  
```

```sql
SELECT a.user_name,b.timestr,b.kills
FROM user1 a
JOIN user_kills b ON a.id=b.user_id
JOIN user_kills c ON c.user_id=b.user_id
GROUP BY a.user_name,b.timestr,c.kills
HAVING b.kills=MAX(c.kills);
```
1. 如何实现分组选择

```sql
SELECT d.user_name, c.timestr , kills
FROM (
  SELECT user_id,timestr,kills,
  (SELECT COUNT(*) FROM user_kills b WHERE
    b.user_id=a.user_id AND a.kills<=b.kills) AS cnt
    FROM user_kills a
    GROUP BY user_id,timestr,kills
) c JOIN user1 d ON c.user_id=d.id
WHERE cnt<=2
```

1. 删除重复记录

2. 行列转换



MySQL

# SQL语句类型:

1. DDL 数据定义语言
2. TPL 事务处理语言
3. DCL 数据控制语言
4. DML 数据操作语言

# 事务

# 索引

# JOIN

只写JOIN, 默认是inner join

1. 更新使用过滤条件中包括自身的表?
   不能更新from从句的表

```sql
update user1 set over='齐天大圣' where user1.user_name in (
        select b.user_name from user1 a join user2 b on a.user_name=b.user_name
        );
```

会报错!

使用JOIN可以解决此问题

```sql
update user1 a join(
        select b.user_name
        from user1 a
        join user2 b
        on a.user_name=b.user_name
        ) b
on a.user_name=b.user_name
set a.over='齐天大圣';
```


表结构：

```sql
CREATE TABLE [dbo].[Exam](
    [S_date] [datetime] NOT NULL,
    [Order_Id] [varchar](50) NOT NULL,
    [Product_Id] [varchar](50) NOT NULL,
    [Amt] [numeric](18, 0) NOT NULL
) ON [PRIMARY]
```

题目一: 写一条Sql语句查询前出100到199的记录
题目二: 写一条Sql语句删除重复[除时间外的所有字段字段相同]的记录,保留重复记录中时间最大的记录
题目三: 一条Sql语句查出年份,1月,2月,3月....12月的订单总数列表
题目四: 一条sql语句查询出年份,本月销量,上月销量,环比%,去年同期销量,同比%列表

1:

	select * from Exam limit 100,100

2 :

3 :

   select year(s_date) as '年' ,month(s_date) as '月', count(Amt) as '订单数' from Exam group by year(s_date),Month(s_date);

## 导入/导出

```sql
#导出整个库的表结构如下：
mysqldump -uroot -p -d databasename > createtab.sql，

#如果只想导出 表 test1，test2，test3 的 表结构 和 数据呢？
#该如何导出？

mysqldump -uroot -p -d databasename test1 test2 test3 > createtab.sql

-- 上面的是导出指定表结构，下面这个可以导出指定表结构和数据
mysqldump -uroot -p --tables databasename > createtab.sql

mysqldump -uroot -p -d databasename test1 test2 test3 > createtab.sql
```

# exist 与 in的区别



# union与union all

# in的长度

# in 与exist是否使用索引

# select  distinct  只能单个字段
