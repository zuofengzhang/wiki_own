---
title: 搭建webDriver+firefox爬虫服务器
date: 2018-02-04T19:57:00.000Z
category: Distributed
tags:
  - Spider
  - Crawling

share: true
comments: true
---


> 基于Docker搭建的Centos 7平台
> 最新版的firefox 59, 新版的Chrome已经不再支持Linux, Opera半死不活

Java + Selenium + GeckoDriver + firefox 版本太难兼容
本文的版本供参考(验证可用)
- Java 1.8.0 update 91
- Selenium-Java 3.9.1
- GeckoDriver 0.19.1
- Firefox 59.0b8

# 安装 Java 8

```shell
yum -y install java-1.8.0-openjdk.x86_64
```
# Selenium-Java

```xml
<dependency>
    <groupId>org.seleniumhq.selenium</groupId>
    <artifactId>selenium-java</artifactId>
    <version>3.9.1</version>
</dependency>
```

# GeckoDriver
Gecko是Firefox的内核，顾名思义，[GeckoDriver](https://github.com/mozilla/geckodriver/releases)就是驱动
启动后监听6411端口， Selenium通过IPC调用，访问GeckoDriver


```shell
wget https://github.com/mozilla/geckodriver/releases/download/v0.19.1/geckodriver-v0.19.1-linux64.tar.gz
tar -zxvf geckodriver-v0.19.1-linux64.tar.gz
mv geckodriver /usr/local/bin/
```

# Firefox

```shell
https://ftp.mozilla.org/pub/firefox/releases/59.0b8/linux-x86_64/zh-CN/firefox-59.0b8.tar.bz2
tar -xvf firefox-59.0b8.tar.bz2
mv firefox /usr/local/
ln /usr/local/firefox/firefox /usr/bin/firefox
```

## 安装虚拟桌面

```shell
#安装Xvfb及其他依赖
yum install xorg-x11-server-Xvfb bzip gtk3
```
## 安装字体，支持中文
```shell
yum groupinstall "Fonts"
# 安装后 重启
```

# 上代码

```java
System.setProperty("webdriver.gecko.driver", "/usr/local/bin/geckodriver");

FirefoxOptions firefoxOptions = new FirefoxOptions();
firefoxOptions.setHeadless(true);
firefoxOptions.setAcceptInsecureCerts(true);
firefoxOptions.addArguments("--disable-gpu", "--window-size=1920,1200", "--ignore-certificate-errors");
WebDriver driver = new FirefoxDriver(firefoxOptions);
System.out.println("init chromeDriver");
driver.get("https://www.coinone.com");
System.out.println("open url");

try {
    Thread.sleep(6000);
} catch (InterruptedException e) {
}
System.out.println("sleep over");

WebElement closeBtn = null;

    try {
        closeBtn = driver.findElement(By.id("close_btn"));
            closeBtn.click();
    } catch (NoSuchElementException e) {
    }


    List<WebElement> elements = driver.findElements(By.cssSelector(".intro_chart_price table td"));
    for (int i = 0; i < elements.size(); i += 2) {
        String value = elements.get(i + 1).getText().trim();
            System.out.println("empty value , try " + tryCount);
        System.out.println(elements.get(i).getText().trim() + "\t" + value);
    }
driver.close();
```

输出结果
```
1518339710700	geckodriver	INFO	geckodriver 0.19.1
1518339710706	geckodriver	INFO	Listening on 127.0.0.1:6411
1518339711356	mozrunner::runner	INFO	Running command: "/usr/local/firefox/firefox" "-marionette" "-headless" "--disable-gpu" "--window-size=1920,1200" "--ignore-certificate-errors" "-profile" "/tmp/rust_mozprofile.1ZVD24PplvDp"
*** You are running in headless mode.
1518339712100	Marionette	INFO	Enabled via --marionette
1518339715188	Marionette	INFO	Listening on port 36443
1518339715212	Marionette	WARN	TLS certificate errors will be ignored for this session
Feb 11, 2018 9:01:55 AM org.openqa.selenium.remote.ProtocolHandshake createSession
INFO: Detected dialect: W3C
init chromeDriver
open url
sleep over

BTC	8,918,000
BCH	1,197,500
ETH	814,200
ETC	22,720
XRP	1,056
QTUM	29,710
LTC	165,050
IOTA	1,910
BTG	370,350
```

[参考文献]
---

太多
