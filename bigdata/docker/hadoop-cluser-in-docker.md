---
title: 在Docker上搭建Hadoop集群container
date: 2017-02-23 00:00:00
category: Distributed
tags:
 - Distributed
 - Hadoop
 - Docker

share: true
comments: true
---

> 为了学习Hadoop，尝试使用Vbox搭建环境，不是很方便。后面转向Docker。本文将使用Docker搭建Hadoop集群记录下来，以备后用

基础环境
- MacOS 10.12
- Docker 17.03.1-ce

网易镜像 https://c.163yun.com/hub#/m/home/


# 安装Docker
在MacOS 上安装Docker即为简单，从官网上下载dmg包，拖到应用目录启动即可。

安装完成后 查看docker 版本:

```shell
% docker version

Client:
 Version:      17.03.1-ce
 API version:  1.27
 Go version:   go1.7.5
 Git commit:   c6d412e
 Built:        Tue Mar 28 00:40:02 2017
 OS/Arch:      darwin/amd64

Server:
 Version:      17.03.1-ce
 API version:  1.27 (minimum version 1.12)
 Go version:   go1.7.5
 Git commit:   c6d412e
 Built:        Fri Mar 24 00:00:50 2017
 OS/Arch:      linux/amd64
 Experimental: true

```




# docker 建立镜像

建立三个镜像，存储目录如下:

```shell
% tree
.
├── centos-ssh-root
│   └── Dockerfile
├── centos-ssh-root-jdk
│   ├── Dockerfile
│   └── jdk-7u79.tar.gz
└── centos-ssh-root-jdk-hadoop
    ├── Dockerfile
    └── hadoop-2.7.3.tar.gz
```

## 建立CentOS-SSH-root基础镜像
建立一个带有ssh和root账户的CentOS镜像

1. 新建Dockerfile文件
```shell
% mkdir  centos-ssh-root
% cd centos-ssh-root
# 新建 Dockerfile 文件
% vi  Dockerfile
```

Dockerfile的内容为:

```shell
# 选择一个已有的os镜像作为基础  国内首选仓库 c.163.com
FROM hub.c.163.com/public/centos:7.2.1511 
RUN yum clean all
RUN yum install -y yum-plugin-ovl || true
# 安装基础的工具包
RUN yum install -y vim tar wget curl rsync bzip2 iptables tcpdump less telnet net-tools lsof sysstat cronie python-setuptools
RUN yum clean all
RUN easy_install supervisor
RUN cp -f /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
EXPOSE 22
RUN mkdir -p /etc/supervisor/conf.d/
RUN /usr/bin/echo_supervisord_conf > /etc/supervisord.conf
RUN echo [include] >> /etc/supervisord.conf
RUN echo 'files = /etc/supervisor/conf.d/*.conf' >> /etc/supervisord.conf
#COPY sshd.conf /etc/supervisor/conf.d/sshd.conf
CMD ["/usr/bin/supervisord"]



# 镜像的作者  
MAINTAINER avery 

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

```

2. 创建镜像

```shell
# 在Dockerfile同级目录下执行下面的命令，最后的.必须有
docker build -t="avery/centos-ssh-root" .
```

查看刚刚创建成功的镜像

```shell
% docker images
REPOSITORY                         TAG                 IMAGE ID            CREATED             SIZE
avery/centos-ssh-root              latest              ba72ccadf508        35 hours ago        776 MB
```

# 创建带有JDK的镜像

1. 准备 下载JDK
此处使用JDK7u79
```shell
% mkdir centos-ssh-root-jdk
% cd centos-ssh-root-jdk
% cp ~/Download/jdk-7u79.tar.gz .
% vi Dockerfile
```

2. Dockerfile文件

```shell
FROM avery/centos-ssh-root
ADD jdk-7u79.tar.gz /usr/local/
RUN mv /usr/local/jdk1.7.0_79 /usr/local/jdk1.7
# 添加环境变量
ENV JAVA_HOME /usr/local/jdk1.7
ENV PATH $JAVA_HOME/bin:$PATH
# 可能上面的设置不会生效
RUN echo 'JAVA_HOME=/usr/local/jdk1.7/' >> .bash_profile 
RUN echo 'PATH=$JAVA_HOME/bin:$PATH' >> .bash_profile 
RUN echo 'export JAVA_HOME' >> .bash_profile 
RUN echo 'export PATH' >> .bash_profile
```

3. 创建镜像

```shell
docker build -t="avery/centos-ssh-root" .
```

4. 查看镜像

```shell
% docker images
REPOSITORY                         TAG                 IMAGE ID            CREATED             SIZE
avery/centos-ssh-root-jdk          latest              e773519d0452        34 hours ago        1.39 GB
avery/centos-ssh-root              latest              ba72ccadf508        35 hours ago        776 MB
```

## 构建hadoop镜像

1. 准备

官网下载 hadoop-2.7.3.tar.gz

```shell
% mkdir centos-ssh-root-jdk-hadoop 
% cd centos-ssh-root-jdk-hadoop 
% cp ../hadoop-2.7.3.tar.gz . 
% vi Dockerfile
```

2. Dockerfile

```shell
FROM avery/centos-ssh-root-jdk
ADD hadoop-2.7.3.tar.gz /usr/local
RUN mv /usr/local/hadoop-2.7.3 usr/local/hadoop
ENV HADOOP_HOME /usr/local/hadoop
ENV PATH $HADOOP_HOME/bin:$PATH

# 为了防止环境变量失效
RUN  echo 'HADOOP_HOME=/usr/local/hadoop/' >>.bash_profile
RUN  echo 'PATH=$HADOOP_HOME/bin:$PATH' >> .bash_profile 
RUN  echo 'export HADOOP_HOME' >> .bash_profile
RUN  echo 'export PATH' >> .bash_profile
```

3. 创建镜像
```shell
docker build -t="avery/centos-ssh-root-jdk-hadoop" .
```

4. 查看

```shell
% docker images
REPOSITORY                         TAG                 IMAGE ID            CREATED             SIZE
avery/centos-ssh-root-jdk-hadoop   latest              c93aadf2b6c5        1 hours ago        2.05 GB
avery/centos-ssh-root-jdk          latest              e773519d0452        1 hours ago        1.39 GB
avery/centos-ssh-root              latest              ba72ccadf508        1 hours ago        776 MB
```

# docker 搭建 Hadoop集群

此处搭建的集群只在本机使用，各个Docker container没有独立的ip，如需要通过IP访问可以参考[使用docker搭建hadoop分布式集群](http://blog.csdn.net/xu470438000/article/details/50512442)

在MacOS上修改系统文件，需要关闭Rootless:

重启按住 Command+R，进入恢复模式，打开Terminal。
```shell 
csrutil disable
```
重启即可。如果要恢复默认，那么
```shell
csrutil enable
```


## 创建3台container

```shell
docker run --name hadoop0 --hostname hadoop0 -d -P -p 50070:50070 -p 8088:8088 avery/centos-ssh-root-jdk-hadoop

docker run --name hadoop1 --hostname hadoop1 -d -P avery/centos-ssh-root-jdk-hadoop

docker run --name hadoop2 --hostname hadoop2 -d -P avery/centos-ssh-root-jdk-hadoop
```

查看三个container的信息

```shell
docker container ls
CONTAINER ID        IMAGE                              COMMAND               CREATED             STATUS              PORTS                                                                     NAMES
78e5e35adfa4        avery/centos-ssh-root-jdk-hadoop   "/usr/sbin/sshd -D"   3 minutes ago       Up 3 minutes        0.0.0.0:32776->22/tcp                                                     hadoop2
5f27fd91c7ba        avery/centos-ssh-root-jdk-hadoop   "/usr/sbin/sshd -D"   3 minutes ago       Up 3 minutes        0.0.0.0:32775->22/tcp                                                     hadoop1
1d7fd37371cc        avery/centos-ssh-root-jdk-hadoop   "/usr/sbin/sshd -D"   3 minutes ago       Up 3 minutes        0.0.0.0:8088->8088/tcp, 0.0.0.0:50070->50070/tcp, 0.0.0.0:32774->22/tcp   hadoop0
```

查看三个container的ip:

```shell
% docker inspect --format='{{.NetworkSettings.IPAddress}}' hadoop0
172.17.0.2
% docker inspect --format='{{.NetworkSettings.IPAddress}}' hadoop1
172.17.0.3
% docker inspect --format='{{.NetworkSettings.IPAddress}}' hadoop2
172.17.0.4
```


## 集群规划 
准备搭建一个具有三个节点的集群，一主两从 

- 主节点：hadoop0 ip：172.17.0.2
- 从节点1：hadoop1 ip：172.17.0.3
- 从节点2：hadoop2 ip：172.17.0.4

## 连接container

验证ssh连接

```shell
% ssh root@localhost -p 32774
# 输入密码 abc.123
% ssh root@localhost -p 32775
# 输入密码 abc.123
% ssh root@localhost -p 32776
# 输入密码 abc.123
```

使用exec

```shell
docker exec -it hadoop0 /bin/bash
```


## 修改container的主机名
分别修改三个container的hosts

```shell
vi /etc/hosts 
```
添加下面配置
```
172.17.0.2    hadoop0
172.17.0.3    hadoop1
172.17.0.4    hadoop2
```

## ssh 免密

设置ssh免密码登录 
在hadoop0上执行下面操作

```shell
cd  ~
mkdir .ssh
cd .ssh
ssh-keygen -t rsa
# (一直按回车即可)
ssh-copy-id -i localhost
ssh-copy-id -i hadoop0
ssh-copy-id -i hadoop1
ssh-copy-id -i hadoop2
```

在hadoop1上执行下面操作
```shell
cd  ~
cd .ssh
ssh-keygen -t rsa
# (一直按回车即可)
ssh-copy-id -i localhost
ssh-copy-id -i hadoop1
```

在hadoop2上执行下面操作
```shell
cd  ~
cd .ssh
ssh-keygen -t rsa
# (一直按回车即可)
ssh-copy-id -i localhost
ssh-copy-id -i hadoop2
```

至此，Docker搭建Hadoop集群的准备工作


---
【参考文献】

1. [使用docker搭建hadoop分布式集群](http://blog.csdn.net/xu470438000/article/details/50512442)


