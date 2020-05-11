# Jpa

## 创建时间 更新时间

1.实体类加注解

```
/**
 * 创建时间
 */
@CreatedDate
@Column(name = "create_time")
private Date createTime;

/**
 * 修改时间
 */
@LastModifiedDate
@Column(name = "modify_time")
private Date modifyTime;

```

2.实体类头加注解

```
@EntityListeners(AuditingEntityListener.class)

```

3.SpringBoot启动类加注解

```
@EnableJpaAuditing

```

另外数据库添加相应控制也可以：  
createTime ： CURRENT_TIMESTAMP  
modifyTime ： CURRENT\_TIMESTAMP ON UPDATE CURRENT\_TIMESTAMP

