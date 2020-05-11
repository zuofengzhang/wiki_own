---
layout: post
title: "MySQL SQL语法样例"
date: 2016-07-29 00:00:00
category: DataBase
tag:
 - database
 - MySQL
 - SQL

share: true
comments: true
---

建立如下的表格:

```sql
SELECT * FROM tdb_goods;
+----------+----------------------------------------------------------+---------------+------------+-------------+---------+------------+
| goods_id | goods_name                                                             | goods_cate    | brand_name | goods_price | is_show | is_saleoff |
+----------+-------------------------------------------------------------+---------------+------------+-------------+---------+------------+
|        1 | R510VC 15.6英寸笔记本                                                  | 笔记本        | 华硕       |    3399.000 |       1 |          0 |
|        2 | Y400N 14.0英寸笔记本电脑                                               | 笔记本        | 联想       |    4899.000 |       1 |          0 |
|        3 | G150TH 15.6英寸游戏本                                                  | 游戏本        | 雷神       |    8499.000 |       1 |          0 |
|        4 | X550CC 15.6英寸笔记本                                                  | 笔记本        | 华硕       |    2799.000 |       1 |          0 |
|        5 | X240(20ALA0EYCD) 12.5英寸超极本                                        | 超级本        | 联想       |    4999.000 |       1 |          0 |
|        6 | U330P 13.3英寸超极本                                                   | 超级本        | 联想       |    4299.000 |       1 |          0 |
|        7 | SVP13226SCB 13.3英寸触控超极本                                         | 超级本        | 索尼       |    7999.000 |       1 |          0 |
|        8 | iPad mini MD531CH/A 7.9英寸平板电脑                                    | 平板电脑      | 苹果       |    1998.000 |       1 |          0 |
|        9 | iPad Air MD788CH/A 9.7英寸平板电脑 （16G WiFi版）                      | 平板电脑      | 苹果       |    3388.000 |       1 |          0 |
|       10 |  iPad mini ME279CH/A 配备 Retina 显示屏 7.9英寸平板电脑 （16G WiFi版） | 平板电脑      | 苹果       |    2788.000 |       1 |          0 |
|       11 | IdeaCentre C340 20英寸一体电脑                                         | 台式机        | 联想       |    3499.000 |       1 |          0 |
|       12 | Vostro 3800-R1206 台式电脑                                             | 台式机        | 戴尔       |    2899.000 |       1 |          0 |
|       13 | iMac ME086CH/A 21.5英寸一体电脑                                        | 台式机        | 苹果       |    9188.000 |       1 |          0 |
|       14 | AT7-7414LP 台式电脑 （i5-3450四核 4G 500G 2G独显 DVD 键鼠 Linux ）     | 台式机        | 宏碁       |    3699.000 |       1 |          0 |
|       15 | Z220SFF F4F06PA工作站                                                  | 服务器/工作站 | 惠普       |    4288.000 |       1 |          0 |
|       16 | PowerEdge T110 II服务器                                                | 服务器/工作站 | 戴尔       |    5388.000 |       1 |          0 |
|       17 | Mac Pro MD878CH/A 专业级台式电脑                                       | 服务器/工作站 | 苹果       |   28888.000 |       1 |          0 |
|       18 |  HMZ-T3W 头戴显示设备                                                  | 笔记本配件    | 索尼       |    6999.000 |       1 |          0 |
|       19 | 商务双肩背包                                                           | 笔记本配件    | 索尼       |      99.000 |       1 |          0 |
|       20 | X3250 M4机架式服务器 2583i14                                           | 服务器/工作站 | IBM        |    6888.000 |       1 |          0 |
|       21 | 玄龙精英版 笔记本散热器                                                | 笔记本配件    | 九州风神   |       0.000 |       1 |          0 |
|       22 |  HMZ-T3W 头戴显示设备                                                  | 笔记本配件    | 索尼       |    6999.000 |       1 |          0 |
|       23 | 商务双肩背包                                                           | 笔记本配件    | 索尼       |      99.000 |       1 |          0 |
+----------+-----------------------------------------------+---------------+------------+-------------+---------+------------+
```

计算所有商品的平均价格

```sql
SELECT AVG(goods_price) AS "average price" FROM tdb_goods;
```

评价价格保留两位小数

```sql
SELECT ROUND(AVG(goods_price),2) AS "average price" FROM tdb_goods;
```

计算高于平均价格的商品

```sql
SELECT goods_id,goods_name,goods_price FROM tdb_goods WHERE goods_price>=5635.36;
```

使用子查询查询价格高于平均价格的商品信息

```sql
SELECT goods_id,goods_name,goods_price FROM tdb_goods WHERE goods_price>=(
    SELECT AVG(goods_price) FROM tdb_goods
    );
+----------+----------------------------------+-------------+
| goods_id | goods_name                       | goods_price |
+----------+----------------------------------+-------------+
|        3 | G150TH 15.6英寸游戏本            |    8499.000 |
|        7 | SVP13226SCB 13.3英寸触控超极本   |    7999.000 |
|       13 | iMac ME086CH/A 21.5英寸一体电脑  |    9188.000 |
|       17 | Mac Pro MD878CH/A 专业级台式电脑 |   28888.000 |
|       18 |  HMZ-T3W 头戴显示设备            |    6999.000 |
|       20 | X3250 M4机架式服务器 2583i14     |    6888.000 |
|       22 |  HMZ-T3W 头戴显示设备            |    6999.000 |
+----------+----------------------------------+-------------+
7 rows in set (0.00 sec)
```
使用子查询 计算高于平均价格的所有商品的评价价格

```sql
SELECT AVG(goods_price) FROM tdb_goods WHERE goods_price>=(SELECT AVG(goods_price) FROM tdb_goods);
+------------------+
| AVG(goods_price) |
+------------------+
|    10780.0000000 |
+------------------+

使用avg函数时, 只有一条记录, 其他字段的信息只显示第一条.
SELECT goods_id,goods_name,goods_price,AVG(goods_price) FROM tdb_goods WHERE goods_price>=(SELECT AVG(goods_price) FROM tdb_goods);
+----------+-----------------------+-------------+------------------+
| goods_id | goods_name            | goods_price | AVG(goods_price) |
+----------+-----------------------+-------------+------------------+
|        3 | G150TH 15.6英寸游戏本 |    8499.000 |    10780.0000000 |
+----------+-----------------------+-------------+------------------+
```

利用子查询查询比"超极本"贵的商品信息(这里的"贵"就体现于多条记录比较时的处理)

```sql
select goods_price from tdb_goods where goods_cate='超级本';
+-------------+
| goods_price |
+-------------+
|    4999.000 |
|    4299.000 |
|    7999.000 |
+-------------+

SELECT goods_id,goods_name,goods_price FROM tdb_goods 
WHERE goods_price >ANY (
    select goods_price from tdb_goods where goods_cate='超级本'
    );
+----------+----------------------------------+-------------+
| goods_id | goods_name                       | goods_price |
+----------+----------------------------------+-------------+
|        2 | Y400N 14.0英寸笔记本电脑         |    4899.000 |
|        3 | G150TH 15.6英寸游戏本            |    8499.000 |
|        5 | X240(20ALA0EYCD) 12.5英寸超极本  |    4999.000 |
|        7 | SVP13226SCB 13.3英寸触控超极本   |    7999.000 |
|       13 | iMac ME086CH/A 21.5英寸一体电脑  |    9188.000 |
|       16 | PowerEdge T110 II服务器          |    5388.000 |
|       17 | Mac Pro MD878CH/A 专业级台式电脑 |   28888.000 |
|       18 |  HMZ-T3W 头戴显示设备            |    6999.000 |
|       20 | X3250 M4机架式服务器 2583i14     |    6888.000 |
|       22 |  HMZ-T3W 头戴显示设备            |    6999.000 |
+----------+----------------------------------+-------------+

SELECT goods_id,goods_name,goods_price FROM tdb_goods WHERE goods_price > ALL (
    select goods_price from tdb_goods where goods_cate='超级本'
    );
+----------+----------------------------------+-------------+
| goods_id | goods_name                       | goods_price |
+----------+----------------------------------+-------------+
|        3 | G150TH 15.6英寸游戏本            |    8499.000 |
|       13 | iMac ME086CH/A 21.5英寸一体电脑  |    9188.000 |
|       17 | Mac Pro MD878CH/A 专业级台式电脑 |   28888.000 |
+----------+----------------------------------+-------------+

 SELECT goods_id,goods_name,goods_price FROM tdb_goods WHERE goods_price = ANY (
    select goods_price from tdb_goods where goods_cate='超级本'
    );
+----------+---------------------------------+-------------+
| goods_id | goods_name                      | goods_price |
+----------+---------------------------------+-------------+
|        5 | X240(20ALA0EYCD) 12.5英寸超极本 |    4999.000 |
|        6 | U330P 13.3英寸超极本            |    4299.000 |
|        7 | SVP13226SCB 13.3英寸触控超极本  |    7999.000 |
+----------+---------------------------------+-------------+

```

多表更新:
参照分类表更新商品表

```sql
mysql> CREATE TABLE IF NOT EXISTS tdb_goods_cates(
    -> cate_id SMALLINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    -> cate_name VARCHAR(40) NOT NULL
    -> );

SHOW COLUMNS FROM tdb_goods_cates;
+-----------+----------------------+------+-----+---------+----------------+
| Field     | Type                 | Null | Key | Default | Extra          |
+-----------+----------------------+------+-----+---------+----------------+
| cate_id   | smallint(5) unsigned | NO   | PRI | NULL    | auto_increment |
| cate_name | varchar(40)          | NO   |     | NULL    |                |
+-----------+----------------------+------+-----+---------+----------------+

insert into tdb_goods_cates(cate_name)  select goods_cate from tdb_goods 
    group by goods_cate;
Query OK, 7 rows affected (0.07 sec)


SELECT * FROM tdb_goods_cates;
+---------+---------------+
| cate_id | cate_name     |
+---------+---------------+
|       1 | 台式机        |
|       2 | 游戏本        |
|       3 | 平板电脑      |
|       4 | 笔记本        |
|       5 | 笔记本配件    |
|       6 | 超级本        |
|       7 | 服务器/工作站 |
+---------+---------------+

UPDATE tdb_goods
  INNER JOIN tdb_goods_cates
    ON goods_cate = cate_name
    SET goods_cate=cate_id;
Query OK, 23 rows affected (0.08 sec)

SELECT * FROM tdb_goods\G;
*************************** 1. row ***************************
   goods_id: 1
 goods_name: R510VC 15.6英寸笔记本
 goods_cate: 4
 brand_name: 华硕
goods_price: 3399.000
    is_show: 1
 is_saleoff: 0
*************************** 2. row ***************************
   goods_id: 2
 goods_name: Y400N 14.0英寸笔记本电脑
 goods_cate: 4
 brand_name: 联想
goods_price: 4899.000
    is_show: 1
 is_saleoff: 0
*************************** 3. row ***************************
   goods_id: 3
 goods_name: G150TH 15.6英寸游戏本
 goods_cate: 2
 brand_name: 雷神
goods_price: 8499.000
    is_show: 1
 is_saleoff: 0
 ...

CREATE TABLE goods_brands(
    id TINYINT AUTO_INCREMENT PRIMARY KEY,
    brand_name VARCHAR(20)
    )
    SELECT brand_name FROM tdb_goods GROUP BY brand_name;

SELECT * FROM goods_brands;
+----+------------+
| id | brand_name |
+----+------------+
|  1 | 联想       |
|  2 | 雷神       |
|  3 | 索尼       |
|  4 | IBM        |
|  5 | 苹果       |
|  6 | 戴尔       |
|  7 | 宏碁       |
|  8 | 惠普       |
|  9 | 华硕       |
| 10 | 九州风神   |
+----+------------+

update tdb_goods INNER JOIN goods_brands
    -> on tdb_goods.brand_name=goods_brands.brand_name
    -> set tdb_goods.brand_name=goods_brands.id;
Query OK, 23 rows affected (0.09 sec)


SELECT * FROM tdb_goods\G;
*************************** 1. row ***************************
   goods_id: 1
 goods_name: R510VC 15.6英寸笔记本
 goods_cate: 4
 brand_name: 9
goods_price: 3399.000
    is_show: 1
 is_saleoff: 0
*************************** 2. row ***************************
   goods_id: 2
 goods_name: Y400N 14.0英寸笔记本电脑
 goods_cate: 4
 brand_name: 1
goods_price: 4899.000
    is_show: 1
 is_saleoff: 0
.....

mysql> DESC tdb_goods;
+-------------+------------------------+------+-----+---------+----------------+
| Field       | Type                   | Null | Key | Default | Extra          |
+-------------+------------------------+------+-----+---------+----------------+
| goods_id    | smallint(5) unsigned   | NO   | PRI | NULL    | auto_increment |
| goods_name  | varchar(150)           | NO   |     | NULL    |                |
| goods_cate  | varchar(40)            | NO   |     | NULL    |                |
| brand_name  | varchar(40)            | NO   |     | NULL    |                |
| goods_price | decimal(15,3) unsigned | NO   |     | 0.000   |                |
| is_show     | tinyint(1)             | NO   |     | 1       |                |
| is_saleoff  | tinyint(1)             | NO   |     | 0       |                |
+-------------+------------------------+------+-----+---------+----------------+


mysql> ALTER TABLE tdb_goods
    -> CHANGE goods_cate cate_id SMALLINT UNSIGNED NOT NULL,
    -> CHANGE brand_name brand_id SMALLINT UNSIGNED NOT NULL;
Query OK, 23 rows affected (0.17 sec)

mysql> DESC tdb_goods;
+-------------+------------------------+------+-----+---------+----------------+
| Field       | Type                   | Null | Key | Default | Extra          |
+-------------+------------------------+------+-----+---------+----------------+
| goods_id    | smallint(5) unsigned   | NO   | PRI | NULL    | auto_increment |
| goods_name  | varchar(150)           | NO   |     | NULL    |                |
| cate_id     | smallint(5) unsigned   | NO   |     | NULL    |                |
| brand_id    | smallint(5) unsigned   | NO   |     | NULL    |                |
| goods_price | decimal(15,3) unsigned | NO   |     | 0.000   |                |
| is_show     | tinyint(1)             | NO   |     | 1       |                |
| is_saleoff  | tinyint(1)             | NO   |     | 0       |                |
+-------------+------------------------+------+-----+---------+----------------+


修改类名
desc tdb_goods_brands;
+------------+-------------+------+-----+---------+----------------+
| Field      | Type        | Null | Key | Default | Extra          |
+------------+-------------+------+-----+---------+----------------+
| id         | tinyint(4)  | NO   | PRI | NULL    | auto_increment |
| brand_name | varchar(20) | YES  |     | NULL    |                |
+------------+-------------+------+-----+---------+----------------+

alter table tdb_goods_brands change id brand_id tinyint(4) not null ;
Query OK, 13 rows affected (0.20 sec)

desc tdb_goods_brands;
+------------+-------------+------+-----+---------+-------+
| Field      | Type        | Null | Key | Default | Extra |
+------------+-------------+------+-----+---------+-------+
| brand_id   | tinyint(4)  | NO   | PRI | NULL    |       |
| brand_name | varchar(20) | YES  |     | NULL    |       |
+------------+-------------+------+-----+---------+-------+
```

删除商品名称重复记录

```sql
查找名称相同的记录
select * from tdb_goods as t1 group by t1.goods_name having count(t1.goods_name)>=2;
+----------+-----------------------+---------+----------+-------------+---------+------------+
| goods_id | goods_name            | cate_id | brand_id | goods_price | is_show | is_saleoff |
+----------+-----------------------+---------+----------+-------------+---------+------------+
|       18 |  HMZ-T3W 头戴显示设备 |       5 |        3 |    6999.000 |       1 |          0 |
|       19 | 商务双肩背包          |       5 |        3 |      99.000 |       1 |          0 |
+----------+-----------------------+---------+----------+-------------+---------+------------+

错误的写法:
delete tt1 from tdb_goods as tt1 left join
 (select * from tdb_goods as t1 group by t1.goods_name having count(t1.goods_name)>=2) as tt2
 on tt1.goods_name=tt2.goods_name and tt1.goods_id>tt2.goods_id;
Query OK, 24 rows affected (0.07 sec)

正确的写法:
delete tt1 from tdb_goods as tt1 left join (
    select * from tdb_goods as t1 group by t1.goods_name 
    having count(t1.goods_name)>=2) as tt2 
    on tt1.goods_name=tt2.goods_name 
    where tt1.goods_id>tt2.goods_id;
Query OK, 0 rows affected (0.00 sec)
```

## Delete中包含left join
1. 左侧的表是待删除的表
2. 不能关联成功的要过滤掉

```sql
delete from tbl_pps_nc_group_member
WHERE user_id = #fromUserId#
and group_id in (SELECT group_id FROM tbl_pps_nc_group where user_id = #myUserId# );
```

与

```sql
select * FROM tbl_pps_nc_group_member nm
LEFT JOIN tbl_pps_nc_group ng ON nm.user_id = '' AND nm.group_id=ng.group_id AND ng.user_id = ''
WHERE ng.group_id is NOT null
```


# 在好友关系中获取对方的信息

对于好友关系来说，是彼此的关系，可以不分先后，如果要区分邀请和被邀请，就必须考虑。
假设存在两个表格: 
第一个表格是用户信息表`t_user_info`，用于记录用户的信息。
第二个表格是好友关系表`t_friend`, 用于记录用户的好友关系。

```sql
CREATE TABLE t_user_info (
	user_id INT PRIMARY KEY auto_increment,
	`name` VARCHAR(10),
	phone VARCHAR(11)
);

CREATE TABLE t_friend (
	 friend_ref_id INT PRIMARY KEY auto_increment,
 	 from_user_id int,
     to_user_id int,
	 `status` TINYINT(1)
);
```

如果要获取对方的信息，邀请者获取得到的是被邀请者的信息，而被邀请者获取得到的是邀请者的信息。
状态值`status`代表 0：待验证，1：已添加，2：已拒绝，4：待对方验证，5: 对方已添加，6：对方已拒绝

```sql
SELECT
	u.`name` AS userName,
	u.phone AS userPhone,
	u.user_id AS userId,
	CASE WHEN f.from_user_id = '1018' THEN 	f.`status` + 4 ELSE f.`status` END AS `status`
FROM
	t_friend f
LEFT JOIN t_user_info u 
	ON (u.user_id = f.from_user_id 	AND f.to_user_id = '1018' )
		OR (u.user_id = f.to_user_id AND f.from_user_id = '1018' )
WHERE
	u.user_id IS NOT NULL
```

**[总结]**

1. `left join`中的`on`语句, 是在被关联表满足`on`条件才会关联，否则显示为`null`, 因此通过判断右侧是否为空，可以判断是否满足条件
2. 关联条件是另一个技巧, 取对方的`id`条件


# Explain解析查询
[MYSQL explain详解](http://blog.csdn.net/zhuxineli/article/details/14455029)

