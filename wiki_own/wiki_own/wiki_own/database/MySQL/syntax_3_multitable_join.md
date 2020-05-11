---
layout: post
title: "MySQL多表关联与笛卡尔积"
date: 2016-10-13 00:00:00
category: DataBase
tag:
 - database
 - MySQL
 - SQL

share: true
comments: true
---

# 多表关联
内联接 外联接 左外联接 右外连接 全外连接 自然联接

#  笛卡尔

笛卡尔（Descartes）乘积又叫直积。假设集合A={a,b}，集合B={0,1,2}，则两个集合的笛卡尔积为
```
{
(a,0),
(a,1),
(a,2),
(b,0),
(b,1),
(b,2)
}
```
可以扩展到多个集合的情况。


## 内连接
```sql
select * from a,b where a.x = b.x; //内连接
```

与

```sql
select * from a inner join b on a.x=b.x; //内连接
```

效果是一样的，都是计算笛卡尔积，对上面笛卡尔积的每一条记录看它是否满足限制条件，如果满足，则它在结果集中。

## 外连接

当外连接，不加任何条件时，也会计算笛卡尔积。如：

```sql
select * from a left join b on 1=1;
```


## 交叉连接(CROSS JOIN)
没有WHERE 子句，它返回`连接表`中所有数据行的笛卡尔积
先返回 左表所有行，左表行在与右表行一一组合，等于两个表相乘.

```sql
select * from a cross join b where a.x=b.x;
```

等同于

```sql
select * from a inner join b on a.x=b.x;
```

> MySQL中不存在交叉连接



## 避免笛卡尔积的方法

由于笛卡尔积的结果集是各个查询表规模之积，往往是数量级的差别。

### 转换为子查询

假设存在如下三个表:

查询方式一:

```sql
SELECT DISTINCT
	u.user_id AS recommendUserId
FROM
	user_info u
WHERE
	1 = 1
AND u.user_id IN (
	SELECT
		a.entity_id
	FROM
		address_info a
	WHERE
		a.city_code = 10010
)
```

查询方式二：

```sql
SELECT DISTINCT
	u.user_id AS recommendUserId
FROM
	user_info u,
	address_info a
WHERE
	1 = 1
AND a.city_code = '10010'
```

对比发现： '查询方式一'使用了子查询，查询是分两步进行的，第一步先从`address_info`选出一个结果，然后在`user_info`中查询，规模为`user_info`的大小。
而`查询方式二`查询规模是笛卡尔积，即`user_info`与`address_info`规模之积。


# 内连接
内连 接只保留交叉积中满足连接条件的那些行。如果某行在一个表中存在，但在另一个表中不存在，则结果表中不包括该信息。

# 外连接

左外连 接包括内连 接和左表中未包括在内连 接中的那些行。

右外连 接包括内连 接和右表中未包括在内连 接中的那些行。

全外连 接包括内连 接以及左表和右表中未包括在内连 接中的行。
内连 接一般是检索两个表里连接字段都存在的数据。
左连接的意思是，查询左（语句前面）表里的所有内容，无论右边表里有没有。右边表里没有的内容用NULL代替。
右连接和左连接相反。

## 左外连接

## 右外连接

## 全外连接

# 交叉连接




----
[参考文献]：

1. [SQL中----SELECT语句中内连接,左连接,右连接,自连接和全连接 ](http://blog.sina.com.cn/s/blog_8250c39a0101k74c.html)


