---
title: "Docker构建Java调试环境"
layout: post
date: 2019-08-10 14:58:00
category: java
tags:
 - Java
 - Metric

share: true
comments: true
---



# Docker构建Java调试环境




[网易镜像仓库](https://c.163yun.com/hub#/home)

Docker动态给容器Container暴露端口

https://blog.csdn.net/lsziri/article/details/69396990


## Docker container





```shell
docker pull hub.c.163.com/public/centos:6.7-tools
docker tag hub.c.163.com/public/centos:6.7-tools  centos


# 增加参数解决不能获取ptrace的问题
# https://blog.csdn.net/russle/article/details/99708261
# 这种不可用 docker run --name centos-one --cap-add=SYS_PTRACE  -d -P centos
docker run --cap-add=SYS_PTRACE --security-opt seccomp:unconfined --name centos-two  -d -P centos
➜  ~ docker container ls -a
CONTAINER ID        IMAGE               COMMAND                  CREATED              STATUS              PORTS                   NAMES
3ae403bf463c        centos              "/usr/bin/supervisord"   About a minute ago   Up About a minute   0.0.0.0:32772->22/tcp   centos-one

# 进入容器
➜  ~ docker exec -it centos-one su deploy



```



##  Java环境

```shell
# 安装全部 yum install -y java-1.8.0-openjdk*
# 只安装需要的
yum install -y java-1.8.0-openjdk.x86_64 java-1.8.0-openjdk-devel.x86_64
```
 install git
```shell
yum install gcc gcc-c++ autoconf make automake -y
yum install curl-devel expat-devel gettext-devel openssl-devel zlib-devel -y
yum install perl docbook2X texinfo sgml2xml openjade perl-ExtUtils-MakeMaker -y
yum install asciidoc xmlto cpio expat-devel gettext-devel  -y
yum install perl-ExtUtils-MakeMaker

yum install -y tk zlib-devel openssl-devel perl cpio expat-devel gettext-devel  asciidoc xmlto autoconf gcc
wget https://mirrors.edge.kernel.org/pub/software/scm/git/git-2.9.5.tar.gz

tar zxvf git-2.9.5.tar.gz
cd git-2.9.5

make configure
./configure --prefix=/usr/local/git --with-iconv=/usr/local/libiconv
# 配置安装路径
make all doc
# 编译
make install install-doc install-html
# 安装

修改环境变量

# echo -e "# git\nexport PATH=/usr/local/git/bin:\$PATH"> /etc/profile.d/git.sh
# cat /etc/profile.d/git.sh
\# git        // 文件内容
export PATH=/usr/local/git/bin:$PATH
# source /etc/profile

```
安装oh-my-zsh
```shell


yum install -y zsh
chsh -s /bin/zsh



sh -c "$(curl -fsSL https://raw.githubusercontent.com/robbyrussell/oh-my-zsh/master/tools/install.sh)"


git clone https://github.com/zsh-users/zsh-autosuggestions ${ZSH_CUSTOM:-~/.oh-my-zsh/custom}/plugins/zsh-autosuggestions


```
编辑~/.zshrc文件

找到plugins=(git)这一行，然后再添加autosuggestions，最后为：

```prop
plugins=(git zsh-autosuggestions)
```


```shell
docker container cp target/demo-0.0.1-SNAPSHOT.jar centos-one:/home/deploy/
```




###  增加端口，做远程debug

```shell
docker commit 95c6d4eed5f0 jdk8-debug

docker run --cap-add=SYS_PTRACE --security-opt seccomp:unconfined --name centos-jdk8-debug  -d -p 8000:8000 -p  8001:8001 -p 8002:8002  -P jdk8-debug


docker exec -it centos-jdk8-debug zsh
```