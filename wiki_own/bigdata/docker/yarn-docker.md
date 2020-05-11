---
title: 在Docker上搭建Yarn集群和HDFS集群
date: 2019-05-23 00:00:00
category: Distributed
tags:
 - Distributed
 - Hadoop
 - Docker
 - yarn

share: true
comments: true
---

yarn作为目前最流行的分布式计算资源管理平台，为Hadoop MR、spark、Flink等提供了资源容器




## Docker File

```dockerfile
# 基础镜像
FROM hub.c.163.com/public/centos
RUN yum clean all
RUN yum install -y yum-plugin-ovl || true
# 安装基础的工具包
RUN yum install -y vim tar wget curl rsync bzip2 iptables tcpdump less telnet net-tools lsof sysstat cronie python-setuptools
RUN yum clean all

RUN cp -f /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
EXPOSE 22
RUN mkdir -p /etc/supervisor/conf.d/
RUN echo [include] >> /etc/supervisord.conf
RUN echo 'files = /etc/supervisor/conf.d/*.conf' >> /etc/supervisord.conf
#COPY sshd.conf /etc/supervisor/conf.d/sshd.conf
CMD ["/usr/bin/supervisord"]



# 镜像的作者  
MAINTAINER averyzhang
# 安装openssh-server和sudo软件包，并且将sshd的UsePAM参数设置成no  
RUN yum install -y openssh-server sudo  
RUN sed -i 's/UsePAM yes/UsePAM no/g' /etc/ssh/sshd_config  
#安装openssh-clients
RUN yum  install -y openssh-clients

# 添加测试用户root，密码abc.123，并且将此用户添加到sudoers里  
RUN echo "root:abc.123" | chpasswd  
RUN echo "root   ALL=(ALL)       ALL" >> /etc/sudoers  
# 下面这两句比较特殊，
# 在centos6上必须要有，否则创建出来的容器sshd不能登录  

# 为了避免文件已存在报错，首先删掉私钥文件
RUN rm -rf /etc/ssh/ssh_host_rsa_key
RUN rm -rf /etc/ssh/ssh_host_dsa_key

RUN ssh-keygen -t dsa -f /etc/ssh/ssh_host_dsa_key  
RUN ssh-keygen -t rsa -f /etc/ssh/ssh_host_rsa_key  

# 启动sshd服务并且暴露22端口  
# RUN mkdir /var/run/sshd  
EXPOSE 22  
CMD ["/usr/sbin/sshd", "-D"]
###### 以上建立centos-ssh

# 安装java
RUN yum install -y java-1.8.0-openjdk.x86_64 java-1.8.0-openjdk-devel.x86_64
## 添加环境变量
ENV JAVA_HOME /usr/lib/jvm/jre-openjdk/
ENV JRE_HOME ${JAVA_HOME}
ENV PATH $JAVA_HOME/bin:$PATH
ENV CLASSPATH $CLASSPATH:.:$JAVA_HOME/lib

RUN echo "export JAVA_HOME=/usr/lib/jvm/jre-openjdk/" >> /etc/profile.d/java.sh 
RUN echo "export JRE_HOME=${JAVA_HOME}" >> /etc/profile.d/java.sh 
RUN echo "export PATH=$JAVA_HOME/bin:$PATH" >> /etc/profile.d/java.sh 
RUN echo "export CLASSPATH=$CLASSPATH:.:$JAVA_HOME/lib" >> /etc/profile.d/java.sh 


## 安装scala
RUN mkdir -p /data/
RUN wget https://downloads.lightbend.com/scala/2.10.7/scala-2.10.7.rpm
RUN yum install -y scala-2.10.7.rpm
ENV SCALA_HOME /usr/share/java
RUN echo "export SCALA_HOME=/usr/share/java" >> /etc/profile.d/java.sh 


## 安装Hadoop
## RUN wget http://mirror.bit.edu.cn/apache/hadoop/common/hadoop-2.7.7/hadoop-2.7.7.tar.gz
ADD hadoop-2.7.7.tar /data/hadoop
# RUN tar zxvf /data/hadoop-2.7.7.tar -C /data/hadoop
# RUN ln -s /data/hadoop-2.7.7 /data/hadoop
ENV HADOOP_HOME /data/hadoop
ENV HADOOP_CONF_DIR ${HADOOP_HOME}/etc/hadoop
ENV YARN_HOME ${HADOOP_HOME}
ENV YARN_CONF_DIR ${YARN_HOME}/etc/hadoop


 
RUN echo "export HADOOP_HOME=/data/hadoop" >> /etc/profile.d/hadoop.sh
RUN echo "export HADOOP_CONF_DIR=${HADOOP_HOME}/etc/hadoop" >> /etc/profile.d/hadoop.sh
RUN echo "export YARN_HOME=${HADOOP_HOME}" >> /etc/profile.d/hadoop.sh
RUN echo "export YARN_CONF_DIR=${YARN_HOME}/etc/hadoop" >> /etc/profile.d/hadoop.sh
```

## 制作image

```shell
docker build -t="avery/centos-yarn" .
```

## 创建yarn节点


```shell
docker run --name yarn0 --hostname yarn0 -d -P -p 50070:50070 -p 8088:8088 avery/centos-yarn
docker run --name yarn1 --hostname yarn1 -d -P avery/centos-yarn
docker run --name yarn2 --hostname yarn2 -d -P avery/centos-yarn
docker run --name yarn3 --hostname yarn3 -d -P avery/centos-yarn
```

查看三个container的ip:

```shell
% docker inspect --format='{{.NetworkSettings.IPAddress}}' yarn0
172.17.0.2
% docker inspect --format='{{.NetworkSettings.IPAddress}}' yarn1
172.17.0.3
% docker inspect --format='{{.NetworkSettings.IPAddress}}' yarn2
172.17.0.4
% docker inspect --format='{{.NetworkSettings.IPAddress}}' yarn3
172.17.0.5
```

| 节点 | 备注 | ip |
| --- | --- | --- |
|  yarn0 | master | 172.17.0.2 |
| yarn1 | slaver | 172.17.0.3 |
| yarn2 | slaver | 172.17.0.4 |
| yarn3 | slaver | 172.17.0.5 |

查看端口

```shell
docker container ls -a                                        
CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS                     PORTS                                                                     NAMES
e150f9141cbd        avery/centos-yarn   "/usr/sbin/sshd -D"      2 minutes ago       Up 2 minutes               0.0.0.0:32771->22/tcp                                                     yarn3
9fe0eb86b3e9        avery/centos-yarn   "/usr/sbin/sshd -D"      2 minutes ago       Up 2 minutes               0.0.0.0:32770->22/tcp                                                     yarn2
1d5c0ea3d3b3        avery/centos-yarn   "/usr/sbin/sshd -D"      2 minutes ago       Up 2 minutes               0.0.0.0:32769->22/tcp                                                     yarn1
60a9cb47ff0c        avery/centos-yarn   "/usr/sbin/sshd -D"      2 minutes ago       Up 2 minutes               0.0.0.0:8088->8088/tcp, 0.0.0.0:50070->50070/tcp, 0.0.0.0:32768->22/tcp   yarn0
```



## 连接container

验证ssh连接

```shell
% ssh root@localhost -p 32774
# 输入密码 abc.123
```

使用exec

```shell
docker exec -it yarn0 /bin/bash
```


## 修改container的主机名
分别修改三个container的hosts

```shell
vi /etc/hosts 
```
添加下面配置
```
172.17.0.2      yarn0
172.17.0.3      yarn1
172.17.0.4      yarn2
172.17.0.5      yarn3
```

## ssh 免密

设置ssh免密码登录 
在yarn0上执行下面操作

```shell
cd  ~
mkdir .ssh
cd .ssh
ssh-keygen -t rsa
# (一直按回车即可)
ssh-copy-id -i localhost
ssh-copy-id -i yarn0
ssh-copy-id -i yarn1
ssh-copy-id -i yarn2
ssh-copy-id -i yarn3
```

在yarn1上执行下面操作
```shell
cd  ~
cd .ssh
ssh-keygen -t rsa
# (一直按回车即可)
ssh-copy-id -i localhost
ssh-copy-id -i yarn1
ssh-copy-id -i yarn2
ssh-copy-id -i yarn3
```

在yarn2上执行下面操作
```shell
cd  ~
cd .ssh
ssh-keygen -t rsa
# (一直按回车即可)
ssh-copy-id -i localhost
ssh-copy-id -i yarn0
ssh-copy-id -i yarn1
ssh-copy-id -i yarn2
ssh-copy-id -i yarn3
```

至此，Docker搭建Hadoop集群的准备工作

## Hadoop 安装

登录yarn0，修改Hadoop配置

配置 Hadoop，cd  ~/hadoop-2.7.2/etc/hadoop进入hadoop配置目录，需要配置有以下7个文件：hadoop-env.sh，yarn-env.sh，slaves，core-site.xml，hdfs-site.xml，maprd-site.xml，yarn-site.xml。

在hadoop-env.sh中配置JAVA_HOME

```shell
# The java implementation to use.
export JAVA_HOME=/usr/lib/jvm/jdk1.8.0_77
```
在yarn-env.sh中配置JAVA_HOME

```shell
# some Java parameters
export JAVA_HOME=/usr/lib/jvm/jdk1.8.0_77
```

在slaves中配置slave节点的ip或者host，

yarn1
yarn2
yarn3
 

修改core-site.xml
```xml
<configuration>
    <property>
        <name>fs.defaultFS</name>
        <value>hdfs://yarn0:9000/</value>
    </property>
    <property>
        <name>hadoop.tmp.dir</name>
        <value>file:/home/fang//hadoop-2.7.2/tmp</value>
    </property>
</configuration>
```
修改hdfs-site.xml
```xml
<configuration>
    <property>
        <name>dfs.namenode.secondary.http-address</name>
        <value>yarn0:9001</value>
    </property>
    <property>
        <name>dfs.namenode.name.dir</name>
        <value>file:/home/fang/hadoop-2.7.2/dfs/name</value>
    </property>
    <property>
        <name>dfs.datanode.data.dir</name>
        <value>file:/home/fang/hadoop-2.7.2/dfs/data</value>
    </property>
    <property>
        <name>dfs.replication</name>
        <value>3</value>
    </property>
</configuration>
```
修改mapred-site.xml
```xml
<configuration>
    <property>
        <name>mapreduce.framework.name</name>
        <value>yarn</value>
    </property>
</configuration>
```
修改yarn-site.xml
```shell
<configuration>
    <property>
        <name>yarn.nodemanager.aux-services</name>
        <value>mapreduce_shuffle</value>
    </property>
    <property>
        <name>yarn.nodemanager.aux-services.mapreduce.shuffle.class</name>
        <value>org.apache.hadoop.mapred.ShuffleHandler</value>
    </property>
    <property>
        <name>yarn.resourcemanager.address</name>
        <value>yarn0:8032</value>
    </property>
    <property>
        <name>yarn.resourcemanager.scheduler.address</name>
        <value>yarn0:8030</value>
    </property>
    <property>
        <name>yarn.resourcemanager.resource-tracker.address</name>
        <value>yarn0:8035</value>
    </property>
    <property>
        <name>yarn.resourcemanager.admin.address</name>
        <value>yarn0:8033</value>
    </property>
    <property>
        <name>yarn.resourcemanager.webapp.address</name>
        <value>yarn0:8088</value>
    </property>
</configuration>
```

将配置好的hadoop-2.7.2文件夹分发给所有slaves节点
```shell
scp -r hadoop/* root@yarn1:/data/hadoop
scp -r hadoop/* root@yarn2:/data/hadoop
scp -r hadoop/* root@yarn3:/data/hadoop
```

## 启动HDFS和yarn

格式化HDFS
```shell
bin/hadoop namenode -format    #格式化namenode
注：若格式化之后重新修改了配置文件，重新格式化之前需要删除tmp，dfs，logs文件夹。
sbin/start-dfs.sh              #启动dfs 
sbin/start-yarn.sh              #启动yarn
```

检查

```shell

[root@yarn0 hadoop]# jps
709 ResourceManager
984 Jps
346 NameNode
543 SecondaryNameNode
[root@yarn0 hadoop]# ssh yarn1
Last login: Sun Jun  2 23:21:51 2019 from yarn0
[root@yarn1 ~]# jps
305 NodeManager
449 Jps
200 DataNode
[root@yarn1 ~]# ssh yarn2
Last login: Sun Jun  2 23:22:18 2019 from yarn0
[root@yarn2 ~]# jps
193 DataNode
442 Jps
298 NodeManager
[root@yarn2 ~]# ssh yarn3
Last login: Sun Jun  2 23:22:25 2019 from yarn0
[root@yarn3 ~]# jps
178 DataNode
283 NodeManager
427 Jps
```

yarn web

浏览器打开 http://localhost:8088

![](/images/distributed/docker/docker-yarn-web.png)

搞定收工