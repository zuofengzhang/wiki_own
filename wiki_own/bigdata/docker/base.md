---
title: Docker
date: 2017-03-24 00:00:00
layout: post
category: Distributed
tags:
 - Distributed
 - Docker

share: true
comments: true
---

网易镜像 https://c.163yun.com/hub#/m/home/


# 修改数据源

```shell
chmod a+w /etc/sysconfig/docker
## 增加 ADD_REGISTRY='--add-registry hub.c.163.com'
```

# 基本命令

## image

```shell
docker image ls
```




## docker

```shell
docker container ls -a
CONTAINER ID        IMAGE                         COMMAND               CREATED             STATUS              PORTS                   NAMES
f7c2d84561dd        hub.c.163.com/public/centos   "/usr/sbin/sshd -D"   7 minutes ago       Up 7 minutes        0.0.0.0:32768->22/tcp   centos

docker exec -it centos /bin/bash
```



## 文件拷贝

1. 本地copy文件到container

```shell
docker cp ~/putMerge.jar hadoop0:~/
```
2. container文件copy到本地

```shell
docker cp hadoop0:~/putMerge.jar ~/
```

3. images 重命名

```shell
docker tag IMAGEID(镜像id) REPOSITORY:TAG（仓库：标签）

#例子
docker tag ca1b6b825289 reffs/xxxxxxx:v1.0
```

```shell
yum list installed | grep docker
卸载：

yum -y remove docker.x86_64
重装：

yum install docker-io
启动：

service docker start
```

Docker 核心基础技术

- Namespace
- cgroup

linux Namespace


```cpp
setns(fd,..)
```

```shell
ls -l /proc/1/ns

ll /proc/1/ns/
total 0
lrwxrwxrwx 1 root root 0 Jun  5 15:20 cgroup -> cgroup:[4026531835]
lrwxrwxrwx 1 root root 0 Jun  5 15:20 ipc -> ipc:[4026531839]
lrwxrwxrwx 1 root root 0 Apr 15 19:28 mnt -> mnt:[4026531840]
lrwxrwxrwx 1 root root 0 Apr 15 19:28 net -> net:[4026531957]
lrwxrwxrwx 1 root root 0 Jun  5 15:20 pid -> pid:[4026531836]
lrwxrwxrwx 1 root root 0 Apr 15 16:20 uts -> uts:[4026531838]
```



## cgroup 子系统

```shell
lssubsys -m

lssubsys -m
cpuset /sys/fs/cgroup/cpuset
cpu,cpuacct /sys/fs/cgroup/cpu,cpuacct
memory /sys/fs/cgroup/memory
devices /sys/fs/cgroup/devices
freezer /sys/fs/cgroup/freezer
net_cls /sys/fs/cgroup/net_cls
blkio /sys/fs/cgroup/blkio
perf_event /sys/fs/cgroup/perf_event
hugetlb /sys/fs/cgroup/hugetlb


ls /sys/fs/cgroup/cpu/mytest

cgcreate -g cpu:mytest
```
