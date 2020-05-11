---
title: "JDBC(占坑)"
layout: post
date: 2016-03-12 11:03:00
category: Java
tags:
 - Java
 - JDBC

share: true
comments: true
---

# JDBC的使用步骤
##  注册驱动 只做一次

**方式一**：

`Class.forName("com.MySQL.jdbc.Driver");`

推荐这种方式，不会对具体的驱动类产生依赖。

**方式二**：

`DriverManager.registerDriver("com.MySQL.jdbc.Driver");`

会造成DriverManager中产生两个一样的驱动，并会对具体的驱动类产生依赖。

**方式三**：

`System.setProperty(“jdbc.drivers”, “driver1:driver2”);`

虽然不会对具体的驱动类产生依赖；但注册不太方便，所以很少使用。 



Jar包地址:

```xml

```





##  建立连接Connection

`Connection conn = DriverManager.getConnection(url, user, password);  `

**url格式：**
`JDBC:子协议:子名称//主机名:端口/数据库名？属性名=属性值&…`
User,password可以用“属性名=属性值”方式告诉数据库；
其他参数如：`useUnicode=true&characterEncoding=GBK`。

##  创建执行SQL的语句Statement

Statement
```java  
Statement st = conn.createStatement();  
st.executeQuery(sql);  
```

PreparedStatement
```java
String sql = “select * from table_name where col_name=?”;  
PreparedStatement ps = conn.preparedStatement(sql);  
ps.setString(1, “col_value”);  
ps.executeQuery(); 
```



##  处理执行结果ResultSet

```java
ResultSet rs = statement.executeQuery(sql);  
While(rs.next()){  
rs.getString(“col_name”);  
rs.getInt(“col_name”);  
//…  
} 
```



##  释放资源

释放ResultSet, Statement,Connection.

数据库连接（Connection）是非常稀有的资源，用完后必须马上释放，如果Connection不能及时正确的关闭将导致系统宕机。Connection的使用原则是尽量晚创建，尽量早的释放。



# 使用JDBC来实现CRUD的操作

## 建表



在这里定义了一个用户信息表

```sql
CREATE TABLE `tb_user_info` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(10) DEFAULT NULL COMMENT '姓名',
  `gender` enum('男','女') DEFAULT NULL COMMENT '性别',
  `birthday` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '生日',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8 COMMENT='用户信息表'
```

## Entity

与该表对应的Entity为:

![][uml-jdbc-sample-01]

## JDBC的CRUD

```java
public class JDBCDemo {
    /**
     * database connection
     */
    private Connection connection;

    public Connection connection(final String url, final String name, final String password) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        Connection connection = DriverManager.getConnection(url, name, password);
        this.connection = connection;
        return connection;
    }

    public List<UserInfo> list(int p, int ps) throws SQLException {
        int start = p * ps;
        PreparedStatement preparedStatement = connection.prepareStatement("select * from tb_user_info limit " + start + "," + ps);
        ResultSet resultSet = preparedStatement.executeQuery();
        ArrayList<UserInfo> list = new ArrayList<UserInfo>();
        while (resultSet.next()) {
            int userId = resultSet.getInt("user_id");
            String name = resultSet.getString("name");
            Timestamp birthday = resultSet.getTimestamp("birthday");
            String gender = resultSet.getString("gender");
            UserInfo userInfo = new UserInfo(userId, name, gender, birthday);
            list.add(userInfo);
        }
        return list;
    }

    public List<UserInfo> findByName(String name) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("select * from tb_user_info where name = ?");
        preparedStatement.setString(1, name);
        ResultSet resultSet = preparedStatement.executeQuery();
        ArrayList<UserInfo> list = new ArrayList<UserInfo>();
        while (resultSet.next()) {
            int userId = resultSet.getInt("user_id");
            Timestamp birthday = resultSet.getTimestamp("birthday");
            String gender = resultSet.getString("gender");
            UserInfo userInfo = new UserInfo(userId, name, gender, birthday);
            list.add(userInfo);
        }
        return list;
    }

    public int addUser(UserInfo userInfo) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("insert into tb_user_info values(default,?,?,?)");
        preparedStatement.setString(1, userInfo.getName());
        preparedStatement.setString(2, userInfo.getGender());
        preparedStatement.setTimestamp(3, userInfo.getBirthday());
        int influenceLines = preparedStatement.executeUpdate();
        return influenceLines;
    }

    public int delete(int userId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("delete from tb_user_info where user_id = ?");
        preparedStatement.setInt(1, userId);
        int influenceLines = preparedStatement.executeUpdate();
        return influenceLines;
    }

    public int update(UserInfo userInfo) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("update tb_user_info set name=? , gender = ? , birthday = ? where user_id = ?");
        preparedStatement.setString(1, userInfo.getName());
        preparedStatement.setString(2, userInfo.getGender());
        preparedStatement.setTimestamp(3, userInfo.getBirthday());
        preparedStatement.setInt(4, userInfo.getUserId());
        int influenceLines = preparedStatement.executeUpdate();
        return influenceLines;
    }
}
```

测试样例: 

```java
JDBCDemo jdbcDemo = new JDBCDemo();
jdbcDemo.connection("jdbc:mysql://127.0.0.1:3306/test?autoReconnect=true&autoReconnectForPools=true&useUnicode=true&characterEncoding=UTF-8",
        "root", "123456");
UserInfo obama = new UserInfo(0, "奥巴马", "男", new Timestamp(System.currentTimeMillis()));
UserInfo cirali = new UserInfo(0, "希拉里", "女", new Timestamp(System.currentTimeMillis()));
UserInfo trappes = new UserInfo(0, "特朗普", "女", new Timestamp(System.currentTimeMillis()));
System.out.println("[添加 奥巴马]");
jdbcDemo.addUser(obama);
System.out.println("[添加 希拉里]");

jdbcDemo.addUser(cirali);
System.out.println("[添加 特朗普]");
jdbcDemo.addUser(trappes);

System.out.println("\n 输出 所有用户信息：");
List<UserInfo> list = jdbcDemo.list(0, 10);
if (list != null) {
    for (UserInfo userInfo : list) {
        System.out.println(userInfo);
    }
}

System.out.println("根据姓名查询用户信息");
UserInfo tUserIndo = null;
List<UserInfo> resultList = jdbcDemo.findByName("特朗普");
if (resultList != null) {
    tUserIndo = resultList.get(0);
}
System.out.println(tUserIndo);

tUserIndo.setGender("男");

System.out.println("[更新]");
jdbcDemo.update(tUserIndo);

System.out.println(tUserIndo);
System.out.println("\n 输出 所有用户信息：");
list = jdbcDemo.list(0, 10);
if (list != null) {
    for (UserInfo userInfo : list) {
        System.out.println(userInfo);
    }
}
/** 
     * 释放资源 
     * @param rs 
     * @param st 
     * @param conn 
     */  
public static void free(ResultSet rs,Statement st,Connection conn){  
  try{  
    if(rs != null){  
      rs.close();  
    }  
  }catch(SQLException e){  
    e.printStackTrace();  
  }finally{  
    try{  
      if(st != null){  
        st.close();  
      }  
    }catch(SQLException e){  
      e.printStackTrace();  
    }finally{  
      try{  
        if(conn != null){  
          conn.close();  
        }  
      }catch(SQLException e){  
        e.printStackTrace();  
      }  
    }  
  }  

}  
```



输出结果为:

```
[添加 奥巴马]
[添加 希拉里]
[添加 特朗普]

 输出 所有用户信息：
UserInfo{userId=10009, name='奥巴马', gender=男, birthday=2016-11-27 10:03:00.0}
UserInfo{userId=10010, name='希拉里', gender=女, birthday=2016-11-27 10:03:00.0}
UserInfo{userId=10011, name='特朗普', gender=女, birthday=2016-11-27 10:03:00.0}
根据姓名查询用户信息
UserInfo{userId=10011, name='特朗普', gender=女, birthday=2016-11-27 10:03:00.0}
[更新]
UserInfo{userId=10011, name='特朗普', gender=男, birthday=2016-11-27 10:03:00.0}

 输出 所有用户信息：
UserInfo{userId=10009, name='奥巴马', gender=男, birthday=2016-11-27 10:03:00.0}
UserInfo{userId=10010, name='希拉里', gender=女, birthday=2016-11-27 10:03:00.0}
UserInfo{userId=10011, name='特朗普', gender=男, birthday=2016-11-27 10:03:00.0}
```



**注意**

1. 使用prepareStatement预处理执行SQL语句时，`?`的索引是从`1`开始的

# 
# JDBC中特殊数据类型的操作问题
## 第一个是日期问题

JDBC接收的时间类型是sql下的类型，与Java中的类型不同，通常需要转化。

如 java.sql.Date <—> java.util.Date



## 第二个问题就是大文本数据的问题

读写大文本数据:

```java
/** 
         * 插入大文本 
         */  
static void insert(){  
  Connection conn = null;  
  PreparedStatement ps = null;  
  ResultSet rs = null;  
  try{  
    conn = JdbcUtils.getConnection();  
    String sql = "insert into clob_test(bit_text) values(?)";  
    ps = conn.prepareStatement(sql);  
    File file = new File("src/com/weijia/type/ClubDemo.java");  
    Reader reader = new BufferedReader(new FileReader(file));  
    //ps.setAsciiStream(1, new FileInputStream(file), (int)file.length());//英文的文档  
    ps.setCharacterStream(1, reader, (int)file.length());  
    ps.executeUpdate();  
    reader.close();  
  }catch(Exception e){  
    e.printStackTrace();  
  }finally{  
    JdbcUtils.free(rs,ps,conn);  
  }  
}  
```

```java
Clob clob = rs.getClob(1);  
InputStream is = clob.getAsciiStream();  
```



# JDBC中事务的概念
# JDBC中调用存储过程
# JDBC来实现批处理功能

```java
static void createBatch() throws Exception{  
  //建立一个连接的是很耗时间的  
  //执行一个sql语句也是很耗时间的  
  //优化的措施：批处理  
  Connection conn = null;  
  PreparedStatement ps = null;  
  ResultSet rs = null;  
  try{  
    conn = JdbcUtils.getConnection();  
    String sql = "insert user(name,birthday,money) values(?,?,?)";  
    ps = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);  

    //打包的话容量也不是越大越好，因为可能会内存溢出的，同时网络传输的过程中也是会进行拆包传输的，这个包的大小是不一定的  
    //有时候打包的效率不一定就会高，这个和数据库的类型，版本都有关系的，所以我们在实践的过程中需要检验的  
    for(int i=0;i<100;i++){  
      ps.setString(1,"jiangwei");  
      ps.setDate(2,new Date(System.currentTimeMillis()));  
      ps.setFloat(3,400);  
      //ps.addBatch(sql);  
      ps.addBatch();  
    }  
    ps.executeBatch();  
  }catch(Exception e){  
    e.printStackTrace();  
  }finally{  
    JdbcUtils.free(rs, ps, conn);  
  }  
}  
```



# JDBC中的滚动结果集和分页技术

```java
static void test() throws Exception{  
  Connection conn = null;  
  Statement st = null;  
  ResultSet rs = null;  
  try{  
    conn = JdbcUtils.getConnection();  
    //结果集可滚动的  
    /** 
             * 参数的含义： 
             *  ResultSet.RTYPE_FORWORD_ONLY：这是缺省值，只可向前滚动；  
                ResultSet.TYPE_SCROLL_INSENSITIVE：双向滚动，但不及时更新，就是如果数据库里的数据修改过，并不在ResultSet中反应出来。  
                ResultSet.TYPE_SCROLL_SENSITIVE：双向滚动，并及时跟踪数据库的更新,以便更改ResultSet中的数据。 
                ResultSet.CONCUR_READ_ONLY：这是缺省值，指定不可以更新 ResultSet  
                ResultSet.CONCUR_UPDATABLE：指定可以更新 ResultSet 
             */  
    st = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);  
    rs = st.executeQuery("select id,name,money,birthday from user");  
    //开始的时候这个游标的位置是第一条记录之前的一个位置  
    //当执行rs.next的时候这个游标的位置就到第一条记录了  
    /*while(rs.next()){ 
                //print result 
            }*/  
    //上面的代码执行之后，这个游标就到最后一条记录的下一个位置了  
    //所以这里在调用previous方法之后，这个游标就回到了最后一条记录中，所以打印了最后一条记录的值  
    /*if(rs.previous()){ 
                System.out.println("id="+rs.getInt("id")+"\tname="+rs.getString("name")+"\tbirthday="+rs.getDate("birthday")+"\tmoney="+rs.getFloat("money")); 
            }*/  

    //绝对定位到第几行结果集  
    //这里传递的参数的下标是从1开始的，比如这里查询出来的记录有3条，那么这里的参数的范围是:1-3,如果传递的参数不在这个范围内就会报告异常的  
    rs.absolute(2);  
    System.out.println("id="+rs.getInt("id")+"\tname="+rs.getString("name")+"\tbirthday="+rs.getDate("birthday")+"\tmoney="+rs.getFloat("money"));  

    //滚到到第一行的前面(默认的就是这种情况)  
    rs.beforeFirst();  

    //滚动到最后一行的后面  
    rs.afterLast();  

    rs.isFirst();//判断是不是在第一行记录  
    rs.isLast();//判断是不是在最后一行记录  
    rs.isAfterLast();//判断是不是第一行前面的位置  
    rs.isBeforeFirst();//判断是不是最后一行的后面的位置  

    //以上的api可以实现翻页的效果(这个效率很低的，因为是先把数据都查询到内存中，然后再进行分页显示的)  

    //效率高的话是直接使用数据库中的分页查询语句：  
    //select * from user limit 150,10;  

    //以上的api实现的分页功能是针对于那些本身不支持分页查询功能的数据库的，如果一个数据库支持分页功能，上面的代码就不能使用的，因为效率是很低的  
  }catch(Exception e){  
    e.printStackTrace();  
  }finally{  
    JdbcUtils.free(rs,st,conn);  
  }  
} 
```



# JDBC中的可更新以及对更新敏感的结果集操作
# 元数据的相关知识
##  数据库的元数据信息
##  查询参数的元数据信息
##  结果集中元数据信息
# JDBC中的数据源
# JDBC中CRUD的模板模式
# Spring框架中的JdbcTemplate
## 加强版的JdbcTemplate
###    NamedParameterJdbcTemplate
###    SimpleJdbcTemplate

# 事务
# 批量

# 常见错误
- `java.sql.SQLException: Incorrect string value: '\xE3\x80\x90\xE9\x80\x9A...' for column 'msg' at row 1`
  编码问题： 检查数据库编码，数据表编码，列编码以及连接数据库使用的`characterEncoding`

# 链接数据库
```java
package com.seecen.stream;
import java.sql.*;
public class TestJDBC {
	/**
	 * 1、实例话驱动类
	 * 2、建立到数据库的连接
	 * 3、将数据发送到数据库中
	 * 4、执行语句（select语句）
	 * 5、关闭
	 * @param args
	 */
	public static void main(String[] args) {
		  ResultSet rs = null;  
		  Statement stmt = null;  
		  Connection conn = null;  
		  try {  
		   Class.forName("oracle.jdbc.driver.OracleDriver");  
	conn = DriverManager.getConnection("jdbc:oracle:thin:@192.168.0.1:1521:yuewei", "scott", "tiger");  
		   stmt = conn.createStatement();  
		   rs = stmt.executeQuery("select * from dept");  
		   while(rs.next()) {  
		    System.out.println(rs.getString("deptno"));  
		   }  
		  } catch (ClassNotFoundException e) {  
		   e.printStackTrace();  
		  } catch (SQLException e) {  
		   e.printStackTrace();  
		  } finally {  
		   try {  
		    if(rs != null) {  
		     rs.close();  
		     rs = null;  
		    }  
		    if(stmt != null) {  
		     stmt.close();  
		     stmt = null;  
		    }  
		    if(conn != null) {  
		     conn.close();  
		     conn = null;  
		    }  
		   } catch (SQLException e) {  
		    e.printStackTrace();  
		   }  
		  }  
	}
}
```

# 调用存储过程
```java
package com.huawei.interview.lym;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Types;

public class JdbcTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Connection cn = null;
		CallableStatement cstmt = null;		
		try {
			//这里最好不要这么干，因为驱动名写死在程序中了
			Class.forName("com.mysql.jdbc.Driver");
			//实际项目中，这里应用DataSource数据，如果用框架，
			//这个数据源不需要我们编码创建，我们只需Datasource ds = context.lookup()
			//cn = ds.getConnection();			
			cn = DriverManager.getConnection("jdbc:mysql:///test","root","root");
			cstmt = cn.prepareCall("{call insert_Student(?,?,?)}");
			cstmt.registerOutParameter(3,Types.INTEGER);
			cstmt.setString(1, "wangwu");
			cstmt.setInt(2, 25);
			cstmt.execute();
			//get第几个，不同的数据库不一样，建议不写
			System.out.println(cstmt.getString(3));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			/*try{cstmt.close();}catch(Exception e){}
			try{cn.close();}catch(Exception e){}*/
			try {
				if(cstmt != null)
					cstmt.close();
				if(cn != null)				
					cn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
```

# JDBC中的PreparedStatement相比Statement的好处
1）提高性能：在使用preparedStatement对象执行sql时候，命令被数据库编译和解析，然后被放到命令缓冲区，然后每当执行同一个preparedStatement时候，他就被再解析一次，但不会在编译，在缓冲区中可以发现预编译的命令，并且可以重新使用。
如果你要写Insert update delete 最好使用preparedStatement，在有大量用户的企业级应用软件中，经常会执行相同的sql,使用preparedStatement会增加整体的性能。
2）安全性：PreparedStatement 可以防止sql注入。


# JDBC原理
调用Class.forName("com.mysql.jdbc.Driver");   加载mysql的驱动类进内存，那么就会在DriverManager中注册自己，注册的意思简单来说就是DriverManager中保持一个Driver引用指向了自己，但是具体的实现可能不同。

然后嗲用DriverManager.getConnection方法得到连接对象，  这里运用到了简单工厂方法，即根据传进去得参数来具体实例化哪个驱动类。

可能是mysql的驱动类， 也可能是Oracle的驱动类， 具体的由传进去的参数来决定。

当得到Connection对象后就没DriverManager和Driver类什么事了。

Connection一个接口，但是它指向了具体的Connection子类对象。

通过Connection中定义的接口，就能够访问数据库了。



所以总得来说，如果要改变当前使用的数据库，那么只需要改变两个地方，

Class.forName(具体的参数)

DriverManager.getConnection(具体的参数)

所以我们可以在配置文件中配置这两个参数，那么我们就可以在程序运行的时候动态地改变所使用的数据库，只需要更改配置文件就行了。

当然了，程序肯定要有数据库第三方jar包。



----
参考文献：

1. [J2EE学习篇之--JDBC详解 ][http://blog.csdn.net/jiangwei0910410003/article/details/26164629]

[uml-jdbc-sample-01]: /images/java/jdbc/sample-01-uml.png

