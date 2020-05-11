---
title: "【转】基于协议和配置的优化"
layout: post
date: 2016-02-02 00:00:00
category: Java
tags:
 - Java
 - 计算机网络

share: true
comments: true
---





RTT(Round-Trip Time): 往返时延。表示从发送端发送数据开始，到发送端收到来自接收端的确认（接收端收到数据后便立即发送确认），总共经历的时延。



## HTTPS 访问速度优化

### Tcp fast open

HTTPS 和 HTTP 使用 TCP 协议进行传输，也就意味着必须通过三次握手建立 TCP 连接，但一个 RTT 的时间内只传输一个 syn 包是不是太浪费？能不能在 syn 包发出的同时捎上应用层的数据？其实是可以的，这也是 tcp fast open 的思路，简称 TFO。具体原理可以参考 rfc7413。

遗憾的是 TFO 需要高版本内核的支持，linux 从 3.7 以后支持 TFO，但是目前的 windows 系统还不支持 TFO，所以只能在公司内部服务器之间发挥作用。

### HSTS

前面提到过将用户 HTTP 请求 302 跳转到 HTTPS，这会有两个影响：

> 1、不安全，302 跳转不仅暴露了用户的访问站点，也很容易被中间者支持。
>
> 2、降低访问速度，302 跳转不仅需要一个 RTT，浏览器执行跳转也需要执行时间。

由于 302 跳转事实上是由浏览器触发的，服务器无法完全控制，这个需求导致了 HSTS 的诞生：

HSTS(HTTP Strict Transport Security)。服务端返回一个 HSTS 的 http header，浏览器获取到 HSTS 头部之后，在一段时间内，不管用户输入www.baidu.com还是http://www.baidu.com，都会默认将请求内部跳转成https://www.baidu.com。

Chrome, firefox, ie 都支持了 HSTS（http://caniuse.com/#feat=stricttransportsecurity）。

### Session resume

Session resume 顾名思义就是复用 session，实现简化握手。复用 session 的好处有两个：

> 1、减少了 CPU 消耗，因为不需要进行非对称密钥交换的计算。
>
> 2、提升访问速度，不需要进行完全握手阶段二，节省了一个 RTT 和计算耗时。

TLS 协议目前提供两种机制实现 session resume，分别介绍一下。

#### Session cache

Session cache 的原理是使用 client hello 中的 session id 查询服务端的 session cache, 如果服务端有对应的缓存，则直接使用已有的 session 信息提前完成握手，称为简化握手。

Session cache 有两个缺点：

> 1、需要消耗服务端内存来存储 session 内容。
>
> 2、目前的开源软件包括 nginx,apache 只支持单机多进程间共享缓存，不支持多机间分布式缓存，对于百度或者其他大型互联网公司而言，单机 session cache 几乎没有作用。

Session cache 也有一个非常大的优点：

> session id 是 TLS 协议的标准字段，市面上的浏览器全部都支持 session cache。

百度通过对 TLS 握手协议及服务器端实现的优化，已经支持全局的 session cache，能够明显提升用户的访问速度，节省服务器计算资源。

#### Session ticket

上节提到了 session cache 的两个缺点，session ticket 能够弥补这些不足。

Session ticket 的原理参考 RFC4507。简述如下：

> server 将 session 信息加密成 ticket 发送给浏览器，浏览器后续握手请求时会发送 ticket，server 端如果能成功解密和处理 ticket，就能完成简化握手。

显然，session ticket 的优点是不需要服务端消耗大量资源来存储 session 内容。

Session ticket 的缺点：

> 1、session ticket 只是 TLS 协议的一个扩展特性，目前的支持率不是很广泛，只有 60% 左右。
>
> 2、session ticket 需要维护一个全局的 key 来加解密，需要考虑 KEY 的安全性和部署效率。

总体来讲，session ticket 的功能特性明显优于 session cache。希望客户端实现优先支持 session ticket。

### OCSP stapling

OCSP 全称在线证书状态检查协议 (rfc6960)，用来向 CA 站点查询证书状态，比如是否撤销。通常情况下，浏览器使用 OCSP 协议发起查询请求，CA 返回证书状态内容，然后浏览器接受证书是否可信的状态。

这个过程非常消耗时间，因为 CA 站点有可能在国外，网络不稳定，RTT 也比较大。那有没有办法不直接向 CA 站点请求 OCSP 内容呢？ocsp stapling 就能实现这个功能。

详细介绍参考 RFC6066 第 8 节。简述原理就是浏览器发起 client hello 时会携带一个 certificate status request 的扩展，服务端看到这个扩展后将 OCSP 内容直接返回给浏览器，完成证书状态检查。

由于浏览器不需要直接向 CA 站点查询证书状态，这个功能对访问速度的提升非常明显。

Nginx 目前已经支持这个 ocsp stapling file，只需要配置 ocsp stapling file 的指令就能开启这个功能：

> - ssl_stapling on;ssl_stapling_file ocsp.staple;

### False start

通常情况下，应用层数据必须等完全握手全部结束之后才能传输。这个其实比较浪费时间，那能不能类似 TFO 一样，在完全握手的第二个阶段将应用数据一起发出来呢？google 提出了 false start 来实现这个功能。详细介绍参考https://tools.ietf.org/html/draft-bmoeller-tls-falsestart-00。

简单概括 False start 的原理就是在 client_key_exchange 发出时将应用层数据一起发出来，能够节省一个 RTT。

False start 依赖于 PFS（perfect forward secrecy 完美前向加密），而 PFS 又依赖于 DHE 密钥交换系列算法（DHE_RSA, ECDHE_RSA, DHE_DSS, ECDHE_ECDSA），所以尽量优先支持 ECDHE 密钥交换算法实现 false start。

### 使用 SPDY 或者 HTTP2

SPDY 是 google 推出的优化 HTTP 传输效率的协议（https://www.chromium.org/spdy），它基本上沿用了 HTTP 协议的语义, 但是通过使用帧控制实现了多个特性，显著提升了 HTTP 协议的传输效率。

SPDY 最大的特性就是多路复用，能将多个 HTTP 请求在同一个连接上一起发出去，不像目前的 HTTP 协议一样，只能串行地逐个发送请求。Pipeline 虽然支持多个请求一起发送，但是接收时依然得按照顺序接收，本质上无法解决并发的问题。

HTTP2 是 IETF 2015 年 2 月份通过的 HTTP 下一代协议，它以 SPDY 为原型，经过两年多的讨论和完善最终确定。

本文就不过多介绍 SPDY 和 HTTP2 的收益，需要说明两点：

> 1、SPDY 和 HTTP2 目前的实现默认使用 HTTPS 协议。
>
> 2、SPDY 和 HTTP2 都支持现有的 HTTP 语义和 API，对 WEB 应用几乎是透明的。

Google 宣布 chrome 浏览器 2016 年将放弃 SPDY 协议，全面支持 HTTP2，但是目前国内部分浏览器厂商进度非常慢，不仅不支持 HTTP2，连 SPDY 都没有支持过。

百度服务端和百度手机浏览器现在都已经支持 SPDY3.1 协议。



## HTTPS 计算性能优化

### 优先使用 ECC

ECC 椭圆加密算术相比普通的离散对数计算速度性能要强很多。下表是 NIST 推荐的密钥长度对照表。

![HTTPS HTTPS协议 https和http有什么区别 HTTPS证书申请](http://upload.chinaz.com/2015/0505/1430805926143.png)

表格 2 NIST 推荐使用的密钥长度

对于 RSA 算法来讲，目前至少使用 2048 位以上的密钥长度才能保证安全性。ECC 只需要使用 224 位长度的密钥就能实现 RSA2048 位长度的安全强度。在进行相同的模指数运算时速度显然要快很多。

### 使用最新版的 openssl

一般来讲，新版的 openssl 相比老版的计算速度和安全性都会有提升。比如 openssl1.0.2 采用了 intel 最新的优化成果，椭圆曲线 p256 的计算性能提升了 4 倍。(https://eprint.iacr.org/2013/816.pdf)

Openssl 2014 年就升级了 5 次，基本都是为了修复实现上的 BUG 或者算法上的漏洞而升级的。所以尽量使用最新版本，避免安全上的风险。

### 硬件加速方案

现在比较常用的 TLS 硬件加速方案主要有两种：

> 1、SSL 专用加速卡。
>
> 2、GPU SSL 加速。

上述两个方案的主流用法都是将硬件插入到服务器的 PCI 插槽中，由硬件完成最消耗性能的计算。但这样的方案有如下缺点：

1、支持算法有限。比如不支持 ECC，不支持 GCM 等。

2、升级成本高。

> a)  出现新的加密算法或者协议时，硬件加速方案无法及时升级。
>
> b)  出现比较大的安全漏洞时，部分硬件方案在无法在短期内升级解决。比如 2014 年暴露的 heartbleed 漏洞。

3、无法充分利用硬件加速性能。硬件加速程序一般都运行在内核态，计算结果传递到应用层需要 IO 和内存拷贝开销，即使硬件计算性能非常好，上层的同步等待和 IO 开销也会导致整体性能达不到预期，无法充分利用硬件加速卡的计算能力。

4、维护性差。硬件驱动及应用层 API 大部分是由安全厂家提供，出现问题后还需要厂家跟进。用户无法掌握核心代码，比较被动。不像开源的 openssl，不管算法还是协议，用户都能掌握。

### TLS 远程代理计算

也正是因为上述原因，百度实现了专用的 SSL 硬件加速集群。基本思路是：

1、优化 TLS 协议栈，剥离最消耗 CPU 资源的计算，主要有如下部分：

> a)  RSA 中的加解密计算。
>
> b)  ECC 算法中的公私钥生成。
>
> c)  ECC 算法中的共享密钥生成。

2、优化硬件计算部分。硬件计算不涉及协议及状态交互，只需要处理大数运算。

3、Web server 到 TLS 计算集群之间的任务是异步的。即 web server 将待计算内容发送给加速集群后，依然可以继续处理其他请求，整个过程是异步非阻塞的。

## HTTPS 安全配置

### 协议版本选择

SSL2.0 早就被证明是不安全的协议了，统计发现目前已经没有客户端支持 SSL2.0，所以可以放心地在服务端禁用 SSL2.0 协议。

2014 年爆发了 POODLE 攻击，SSL3.0 因此被证明是不安全的。但是统计发现依然有 0.5% 的流量只支持 SSL3.0。所以只能有选择地支持 SSL3.0。

TLS1.1 及 1.2 目前为止没有发现安全漏洞，建议优先支持。

### 加密套件选择

加密套件包含四个部分：

> 1、非对称密钥交换算法。建议优先使用 ECDHE，禁用 DHE，次优先选择 RSA。
>
> 2、证书签名算法。由于部分浏览器及操作系统不支持 ECDSA 签名，目前默认都是使用 RSA 签名，其中 SHA1 签名已经不再安全，chrome 及微软 2016 年开始不再支持 SHA1 签名的证书 (http://googleonlinesecurity.blogspot.jp/2014/09/gradually-sunsetting-sha-1.html)。
>
> 3、对称加解密算法。优先使用 AES-GCM 算法，针对 1.0 以上协议禁用 RC4（ rfc7465）。
>
> 4、内容一致性校验算法。Md5 和 sha1 都已经不安全，建议使用 sha2 以上的安全哈希函数。

## HTTPS 防攻击

### 防止协议降级攻击

降级攻击一般包括两种：加密套件降级攻击 (cipher suite rollback) 和协议降级攻击（version roll back）。降级攻击的原理就是攻击者伪造或者修改 client hello 消息，使得客户端和服务器之间使用比较弱的加密套件或者协议完成通信。

为了应对降级攻击，现在 server 端和浏览器之间都实现了 SCSV 功能，原理参考https://tools.ietf.org/html/draft-ietf-tls-downgrade-scsv-00。

一句话解释就是如果客户端想要降级，必须发送 TLS_SCSV 的信号，服务器如果看到 TLS_SCSV，就不会接受比服务端最高协议版本低的协议。

### 防止重新协商攻击

重新协商（tls renegotiation）分为两种：加密套件重协商 (cipher suite renegotiation) 和协议重协商（protocol renegotiation）。

重新协商会有两个隐患：

> 1、重协商后使用弱的安全算法。这样的后果就是传输内容很容易泄露。
>
> 2、重协商过程中不断发起完全握手请求，触发服务端进行高强度计算并引发服务拒绝。

对于重协商，最直接的保护手段就是禁止客户端主动重协商，当然出于特殊场景的需求，应该允许服务端主动发起重协商。

**结束语**

HTTPS 的实践和优化涉及到了非常多的知识点，由于篇幅关系，本文对很多优化策略只是简单介绍了一下. 如果想要了解协议背后的原理，还是需要详细阅读 TLS 协议及 PKI 知识。对于大型站点来说，如果希望做到极致，HTTPS 的部署需要结合产品和基础设施的架构来进行详细的考虑，比起部署支持 HTTPS 的接入和对它的优化，在产品和运维层面上花费的功夫会更多





---
【参考文献】:

