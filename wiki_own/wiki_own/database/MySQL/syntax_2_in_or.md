---
title: "MySQL in or优化"
layout: post
date: 2017-02-16 22:40:00
category: DataBase
tag:
 - MySQL
 - SQL
 - in
 - or

share: true
comments: true
---

MySQL会对sql语句做优化， 

1. in 后面的条件不超过一定数量仍然会使用索引。mysql 会根据索引长度和in后面条件数量判断是否使用索引。
2. 如果是in后面是子查询，则不会使用索引。此时采用`join`来替换
3. 使用`union all`代替`in`和`or`


# 使用`union all`优化的样例

一个文章库，里面有两个表：category和article。category里面有10条分类数据。article里面有 20万条。article里面有一个"article_category"字段是与category里的"category_id"字段相对应的。 article表里面已经把 article_category字义为了索引。数据库大小为1.3G。

**问题描述：**

执行一个很普通的查询： 
```sql
Select * FROM `article` Where article_category=11 orDER BY article_id DESC LIMIT 5 
```
执行时间大约要5秒左右

**解决方案：**
建一个索引：
```sql
create index idx_u on article (article_category,article_id);
```
```sql
Select * FROM `article` Where article_category=11 orDER BY article_id DESC LIMIT 5 
```
减少到0.0027秒

**继续问题：**

```sql
Select * FROM `article` Where article_category IN (2,3) orDER BY article_id DESC LIMIT 5 
```
执行时间要11.2850秒。

**使用OR:**

```sql
select * from article
where article_category=2
or article_category=3
order by article_id desc
limit 5
```
执行时间：11.0777

**解决方案：**
避免使用in 或者 or (or会导致扫表)，使用union all

**使用UNION ALL：**

```sql
(select * from article where article_category=2 order by article_id desc limit 5)
UNION ALL (select * from article where article_category=3 order by article_id desc limit 5)
orDER BY article_id desc
limit 5
```
执行时间：0.0261
