---
title: 在Docker上搭建Jenkins环境
date: 2017-02-23 00:00:00
category: Distributed
tags:
 - Distributed
 - Hadoop
 - Docker

share: true
comments: true
---


1. 从网易镜像仓库下载Jenkins

[网易镜像仓库](https://c.163yun.com/hub#/m/search/?keyword=jenkins)

拉取到本地

```shell
docker pull hub.c.163.com/library/jenkins:latest
```

查看镜像

```shell
docker images
```

```
REPOSITORY                      TAG                 IMAGE ID            CREATED             SIZE
hub.c.163.com/library/jenkins   latest              88d9d8a30b47        8 months ago        810MB
```

从官网下载最新的Jenkins

```shell
wget http://updates.jenkins-ci.org/download/war/2.107.2/jenkins.war
```

DockerFile

```shell
FROM hub.c.163.com/library/jenkins
# 将最新的Jenkins拷贝到`/usr/share/jenkins/`目录
ADD jenkins.war /usr/share/jenkins/
```

常见镜像

```shell
docker build -t avery/jenkins .
```

启动一个Container

```shell
docker run --name prod_jenkins2 -d -p 9005:8080 -p 9006:50000 -v  /Users/avery/docker/jenkins/backup/jenkins_home:/var/jenkins_home avery/jenkins
```

1. 创建本地Jenkins数据目录，将该目录映射到docker中的Jenkins目录，方便备份数据
2. 8080端口是Jenkins web service的默认端口，将宿主机的端口映射到8080端口，可以直接访问
3. --name 命名为prod_jenkins2
4. -d 后台运行
5. -p 端口映射
6. -v 文件目录映射

查看

```shell
docker container ls -a
CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS                     PORTS                   NAMES
cb8cdf3be078        avery/jenkins       "/bin/tini -- /usr/l…"   3 hours ago         Exited (143) 3 hours ago                           prod_jenkins2
```

启动后，可以直接通过url 'http://localhost:9001' 访问，Jenkins首次打开，需要获取随机密码

```shell
# 获取jenkins密码
docker container exec cb8cdf3be078 cat /var/jenkins_home/secrets/initialAdminPassword
a7cefff7d2634cc6a9542491f465eb52
# 或者直接进入shell查看
docker exec -it cb8cdf3be078 bash
```
