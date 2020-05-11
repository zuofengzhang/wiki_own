---
title: PhamtomJs
date: 2017-01-10 00:00:00
category: BigData
tags:
 - BigData

share: true
comments: true
---

PhamtomJs 是服务端的无界面的Chrome浏览器，提供JS API和shell命令行，方便操作

> [官方的文档](http://phantomjs.org/quick-start.html)


# Java 操作

## 阻塞式
```java
private void openWithPhanatomjs(String viewUrl) {
      logger.info("openWithPhanatomjs start" + viewUrl);
      InputStreamReader inReader = null;
      InputStream inputStream = null;
      BufferedReader reader = null;
      InputStream errorStream = null;
      try {
          String path = null;
          try {
              path = Thread.currentThread().getContextClassLoader().getResource("/").toURI().getPath();
          } catch (URISyntaxException e) {
              e.printStackTrace();
          }
          final String s = path + File.separator + "otwp.js";
//            logger.info(s);
          Process process = Runtime.getRuntime().exec(phanatomjsHome + " " + s + " " + viewUrl);
          inputStream = process.getInputStream();
          inReader = new InputStreamReader(inputStream, "utf-8");
          reader = new BufferedReader(inReader);
          String line = null;
          while ((line = reader.readLine()) != null) {
              logger.info(line);
          }
          errorStream = process.getErrorStream();
          final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(errorStream));
          while ((line = bufferedReader.readLine()) != null) {
              logger.info(line);
          }
      } catch (IOException e) {
          logger.error("", e);
      } finally {
          try {
              if (inReader != null) {
                  inReader.close();
              }
              if (inputStream != null) {
                  inputStream.close();
              }
              if (reader != null) {
                  reader.close();
              }
          } catch (IOException e) {
              logger.error("", e);
          }
      }
      logger.info("openWithPhanatomjs end" + viewUrl);
  }
```


# 样例

```javascript
// run this js with command:
// phantomjs openTableauWithPhantomjs.js <Tableau view url>

console.log("start open with Phantomjs");

var system = require('system');
if (system.args.length === 1) {
    console.log('Please input view url');
    console.log('exit');
    phantom.exit();
} else {
    var viewUrl = system.args[1];
    console.log('Open url: ' + viewUrl);
    var page = require('webpage').create();
    var t = Date.now();

    page.onConsoleMessage = function (msg) {
        console.log(msg);
    };
    page.onLoadFinished = function (status) {
        console.log('加载完毕, 状态: ' + status);

    };
    page.settings.resourceTimeout=100000;

    page.open(viewUrl, function (status) {
        var title = page.evaluate(function () {
            return document.title;
        });
        console.log(status + '\ttitle:' + title);
        if (status !== 'success') {
            console.log('Fail to load the address!');
        } else {
            t = Date.now() - t;
            console.log('open success: elase time : ' + t);
        }
        console.log('exit');
        phantom.exit();

    });
}
```

















---
[参考文献]

1. [PhantomJS 基础及示例](https://www.qcloud.com/community/article/743451001489391682)
