---
title: Android网络编程
date: 2015-09-23 00:00:00
category: Android
tags:
 - Android

share: true
comments: true
---

Android 网络开发

网络功能是 Android 的最基本的功能之一, 本文将详细介绍 Android 网络开发.

1. Android网络编程专题之二:搭建测试服务器 目的是为了测试 Android 网络代码
2. Android网络编程专题之三:模拟简单的网络应用
  - 下载并显示图片
  - HttpURLConnection GET和POST请求
  - HttpClient: apache-commons-httpclient GET和POST请求
  - WebView
3. Android网络编程专题之四:第三方框架
  - AsyncHttpClient
    - post
    - get
    - 文件上传
  - SmartImage
4. Android网络编程专题之五:多线程断点下载

# Android配置

网络连接需要声明网络服务权限:
```xml
<uses-permission android:name="android.permission.INTERNET"/>
```

## 简单的服务器端

### 要求

为了支持Android的网络测试，搭建一个服务器端工程，满足如下几个要求：
1. 并支持Session和Cookie
2. 支持文件上传
3. 图片查看

访问`/SimpleServer`进入登陆界面

![][p-SimpleServer]

登陆成功，显示相关的信息

![][p-SimpleServer-login-success]

登陆失败，显示失败信息

![][p-SimpleServer-login-failure]

下载图片地址

`/SimpleServer/gyy.jpg`

![](/images/android/network/android-network-SimpleServer-image.png)

文件上传

`/SimpleServer/uploadFile.jsp`

![](/images/android/network/android-network-SimpleServer-uploadServer1.png)

### 实现

Eclipse工程目录如下

![](/images/android/network/android-network-SimpleServer-project.png)

1. 创建打开`/SimpleServer/`的默认页面`login.jsp`

```html
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>登陆</title>
</head>
<body>
	<form action="login" method="get">
		姓名:<input type="text" name="name" ><br>
		密码:<input type="password" name="password" ><br>
		<input type="submit" value="get 提交">
	</form>
	<hr>
	<form action="login" method="post">
		姓名:<input type="text" name="name" ><br>
		密码:<input type="password" name="password" ><br>
		<input type="submit" value="post 提交">
	</form>
</body>
</html>
```
上面有两个表单，分别用于使用`get`方法和`post`方法请求，发送请求到action--`login`

2. 在`web.xml`中注册

在`web-app`节点下创建`welcome-file-list`子节点
添加页面
```xml
<web-app ...>
  <welcome-file-list>
    <welcome-file>login.jsp</welcome-file>
  </welcome-file-list>
  ...
</web-app>
```
`welcome-file`是默认的欢迎页面，在不指定子页面的情况下，按照`welcome-file-list`的顺序寻找页面，直到找到页面。
如果所有的页面都找不到，则只能显示404了


3. 在`web.xml`中注册login action

```xml
<servlet>
  <servlet-name>login</servlet-name>
  <servlet-class>club.guadazi.ss.LoginAction</servlet-class>
</servlet>
<servlet-mapping>
  <servlet-name>login</servlet-name>
  <url-pattern>/login</url-pattern>
</servlet-mapping>
```

4. LoginAction

页面登陆，登陆成功后将用户名记录到Session中，如果Session中用户名不为null则代表已经登陆。
如果登陆失败，输出错误信息。

```java
package club.guadazi.ss;

public class LoginAction extends HttpServlet {
	public LoginAction() {
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
   throws ServletException, IOException {
		HttpSession session = req.getSession();
		Object pwdAttribute = req.getParameter("password");
		Object nameAttribute = req.getParameter("name");
		if (pwdAttribute != null && "abc.123".equals((String) pwdAttribute)
    && nameAttribute != null) {
			String name = (String) nameAttribute;
			session.setAttribute("name", name);
			resp.setContentType("text/html;charset=utf-8");
			resp.sendRedirect("info.jsp");
		} else {
			resp.setContentType("text/html;charset=utf-8");
			PrintWriter writer = resp.getWriter();
			writer.print("<html><head>");
			writer.print("<title>" + "欢迎 post" + "</title>");
			writer.print("</head>");
			writer.print("<center>登陆失败!</center>");
			writer.flush();
			writer.close();
		}
	}
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
   throws ServletException, IOException {
		doPost(req, resp);
	}
}
```
Servlet实现的过程，继承HttpServlet，HttpServlet类含有处理post和get请求的方法，重写相应的方法即可。
Session用于保存一次会话的所有信息，Session以键值对的形式保存信息。常常用于记录登陆信息，当登陆成功后记录用户信息到Session，
在其他的页面上可以判断是否登陆，如果未登陆，则显示登陆页面。
`HttpServletRequest.getParameter`可以获取页面表单在发请求时携带的参数。
`resp.sendRedirect("info.jsp");`页面重定向

5. info.jsp 登陆成功后显示的页面

需要在头部增加Session中用户名判断信息

```html
<%
	HttpSession session2 = request.getSession();
	Object nameObject = session2.getAttribute("name");
	if (nameObject == null) {
		/* 		response.sendRedirect("/SimpleServer"); */
%>
<center>
	未登录 <a href="/SimpleServer">点击登陆</a>
</center>
<%
	return;
	}
%>
```
当为登陆时不再显示下面的信息直接return

6. 上传文件页面

```html
<form action="uploadFileAction" enctype="multipart/form-data"
  method="post">
  <input type="file" name="filename"><input type="submit"
    value="Press"> to upload the file!
</form>
```

`enctype`必须指定为`multipart/form-data`

7. 上传文件Action

使用Apache的commons-fileupload工具包在doPost方法中实现上传文件
commons-fileupload依赖commons-io包

```java
DiskFileItemFactory factory = new DiskFileItemFactory();
ServletFileUpload fileUpload = new ServletFileUpload(factory);
if (!ServletFileUpload.isMultipartContent(req)) {
  PrintWriter writer = resp.getWriter();
  writer.println("is not multipart content!");
  return;
}
try {
  List<FileItem> list = fileUpload.parseRequest(req);
  for (FileItem fileItem : list) {
    if (fileItem.isFormField()) {
      String name = fileItem.getName();
      String value = fileItem.getString();
      ServletOutputStream outputStream = resp.getOutputStream();
      outputStream.write((name + ":" + value).getBytes());
      outputStream.close();
    } else {
      String fileName = fileItem.getName();
      String realPath = /* this.getServletContext().getRealPath */("/Users/Mariaaron/upload/");
      System.out.println(realPath);
      FileOutputStream out = new FileOutputStream(realPath + fileName);
      InputStream in = fileItem.getInputStream();
      byte buffer[] = new byte[1024];
      int len = 0;
      while ((len = in.read(buffer)) > 0) {
        out.write(buffer, 0, len);
      }
      out.close();
      in.close();
    }
  }
} catch (FileUploadException e) {
  e.printStackTrace();
}

```
源文件与war包的[下载地址][2]


# 简单网络操作

Android 网络

## Android 网络连接线程

1. 在 Android 中, 主线程负责更新 UI, 因此,主线程也称为 UI 线程.
2. 除了ProcessBar等几个少数的控件外, 几乎所有的 UI 控件必须在主线程更新.
3. 为了防止 UI 载入和更新出现卡顿, 网络连接/大图片载入以及一些大型的计算必须在子线程进行.
4. 网络操作必须放在子线程, 在子线程使用 Handler 更新 UI.
异步框架`AsyncHttpClient`就是基于这个原理实现的.


Android提供了在子线程更新 UI 的 API:
`Activity.runOnUiThread(Thread thread)` 方法,将 Runnable 中的任务放到UI 线程执行:<br/>
// 如果当前线程就是UI 线程立即执行,否者将把 Runnable 线程 join 到UI 线程中执行.


## Java Socket
> android简单聊天工具


## URL
类 URL 代表一个统一资源定位符，它是指向互联网“资源”的指针。

创建一个到 URL 的连接需要几个步骤：

  openConnection() ------------ 时间 ---------------->	connect()
对影响到远程资源连接的参数进行操作 ------------ 时间 ----------------> 与资源交互；查询头字段和内容。

1. 通过在 URL 上调用 openConnection 方法创建连接对象。
2. 处理设置参数和一般请求属性。
3. 使用 connect 方法建立到远程对象的实际连接。
4. 远程对象变为可用。远程对象的头字段和内容变为可访问。

使用以下方法修改设置参数：

   setAllowUserInteraction
   setDoInput
   setDoOutput
   setIfModifiedSince
   setUseCaches

使用以下方法修改一般请求属性：

   setRequestProperty

使用 setDefaultAllowUserInteraction 和 setDefaultUseCaches 可设置 AllowUserInteraction 和 UseCaches 参数的默认值。

上面每个 set 方法都有一个用于获取参数值或一般请求属性值的对应 get 方法。适用的具体参数和一般请求属性取决于协议。

在建立到远程对象的连接后，以下方法用于访问头字段和内容：

   getContent
   getHeaderField
   getInputStream
   getOutputStream

某些头字段需要经常访问。以下方法：

   getContentEncoding
   getContentLength
   getContentType
   getDate
   getExpiration
   getLastModifed

提供对这些字段的便捷访问。getContent 方法使用 getContentType 方法以确定远程对象类型；子类重写 getContentType 方法很容易。

一般情况下，所有的预连接参数和一般请求属性都可忽略：预连接参数和一般请求属性默认为敏感值。对于此接口的大多数客户端而言，只有两个需要的方法：getInputStream 和 getContent，它们通过便捷方法镜像到 URL 类中。


## URLConnection

例子: 布局文件中的Button被调用时，调用loadImage方法

在loadImage方法中
1. 判断收入的图片地址是否为空
2. 启动新的子线程，在子线程中

`URLConnection urlConnection = url.openConnection();'
返回一个 URLConnection 对象，它表示到 URL 所引用的远程对象的连接。
每次调用此 URL 的协议处理程序的 openConnection 方法都打开一个新的连接。

如果 URL 的协议（例如，HTTP 或 JAR）存在属于以下包或其子包之一的公共、专用 URLConnection 子类：java.lang、java.io、java.util、java.net，
返回的连接将为该子类的类型。例如，对于 HTTP，将返回 HttpURLConnection，对于 JAR，将返回 JarURLConnection。


'InputStream inputStream = urlConnection.getInputStream();'
从打开的连接读取输入流

> 'InputStream inputStream = urlConnection.openStream();'
> 是`openConnection().getInputStream()`的缩写形式。

'Bitmap bitmap = BitmapFactory.decodeStream(inputStream);`
把输入流解码成位图对象

3. 创建Message，将位图对象作为`Message.obj`, 向Handler发送消息

4. Handler执行，更新UI

```java
private ImageView imageView;
public void loadImage(View view) {
   final String imageUrl = imageUrlEdit.getText().toString().trim();
   if (TextUtils.isEmpty(imageUrl)) {
       return;
   }
   new Thread() {
       @Override
       public void run() {
           try {
               URL url = new URL(imageUrl);
               HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
               urlConnection.setRequestMethod("GET");
               if (urlConnection.getResponseCode() == 200) {
                   InputStream inputStream = urlConnection.getInputStream();
                   Bitmap bitmap;
                   bitmap = BitmapFactory.decodeStream(inputStream);
                   Message message = new Message();
                   message.what = SHOW_IMAGE_BY_URL;
                   message.obj = bitmap;
                   handler.sendMessage(message);
               } else {
                   Toast.makeText(MainActivity.this, "请求失败!", Toast.LENGTH_SHORT).show();
               }
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
   }.start();
}

public void loadImage2(View view) {
   final String imageUrl = imageUrlEdit.getText().toString().trim();
   if (TextUtils.isEmpty(imageUrl)) {
       return;
   }
   new Thread() {
       @Override
       public void run() {
           try {
               URL url = new URL(imageUrl);
               HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
               urlConnection.setRequestMethod("GET");
               if (urlConnection.getResponseCode() == 200) {
                   InputStream inputStream = urlConnection.getInputStream();
                   final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                   /**
                    * runOnUiThread 方法,将 Runnable 中的任务放到UI 线程执行:<br/>
                    * 如果当前线程就是UI 线程立即执行,否者将把 Runnable 线程 join 到UI 线程中执行.
                    */
                   runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           imageView.setImageBitmap(bitmap);
                       }
                   });
               }
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
   }.start();
}
private Handler handler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case showImageByUrl:
                Bitmap bitmap = (Bitmap) msg.obj;
                imageView.setImageBitmap(bitmap);
                break;
        }
    }
};

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    imageView = (ImageView) findViewById(R.id.iv);
}
```



## HttpURLConnection

对于上面的例子，URL的scheme是HTTP类型的，`URL.openConnection`返回的对象就是`HttpURLConnection`对象。

get和post方式发送登录请求的代码和纯Java环境是一模一样的。

```java
private void loginUrlGet(final String name, final String password) {
    try {
        String path = loginPath + "?name=" + name + "&password=" + password;
        URL url = new URL(path);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        if (urlConnection.getResponseCode() == 200) {
            InputStream inputStream = urlConnection.getInputStream();
            String content = readAsString(inputStream);
            showResponseOnWebView(content);
        } else {
            Toast.makeText(MainActivity.this, "请求失败!", Toast.LENGTH_SHORT).show();
        }

    } catch (IOException e) {
        e.printStackTrace();
    }
}

private void loginUrlPost(final String name, final String password) {
    try {
        Map<String, String> requestParams = new HashMap<String, String>();
        requestParams.put("name", name);
        requestParams.put("password", password);
        StringBuilder params = new StringBuilder();
        for (Map.Entry<String, String> entry : requestParams.entrySet()) {
            params.append(entry.getKey());
            params.append("=");
            params.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            params.append("&");
        }
        if (params.length() > 0) params.deleteCharAt(params.length() - 1);
        byte[] data = params.toString().getBytes();
        URL url = new URL(loginPath);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");//必须大写
        conn.setDoOutput(true);
        conn.setRequestProperty("Connection", "Keep-Alive");//维持长连接
        conn.setRequestProperty("Charset", "UTF-8");
        conn.setRequestProperty("Content-Length", String.valueOf(data.length));
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
        outStream.write(data);
        outStream.flush();
        if (conn.getResponseCode() == 200) {
            String result = readAsString(conn.getInputStream());
            outStream.close();
            showResponseOnWebView(result);
        }
    } catch (MalformedURLException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```
上面的例子只是实现了登录，如果当服务器端使用了Session时，再访问其他页面登录信息是无法联系起来的。
而HttpClient可以

### 多线程下载
见 [下文](#nulti_download)




## HttpClient: apache-commons-httpclient
HttpClient类似于Http的客户端，也就是浏览器。
它会保存会话，记录登录信息，Cookie等

看登录并访问的例子

```java
private void loginHttpClientPost(String name, String password) {
    HttpClient httpClient = new DefaultHttpClient();
    HashMap<String, String> map = new HashMap<String, String>();
    map.put("name", name);
    map.put("password", password);
    String path = loginPath;
    path += "?";
    Set<Map.Entry<String, String>> entries = map.entrySet();
    for (Map.Entry<String, String> entry : entries) {
        String key = entry.getKey();
        String value = entry.getValue();
        path += key + "=" + value;
        path += "&";
    }
    path = path.substring(0, path.length() - 1);
    HttpPost httpPost = new HttpPost(path);
    String responseString = "";
    try {
        HttpResponse response = httpClient.execute(httpPost);
        if (response.getStatusLine().getStatusCode() == 200) {
            responseString = EntityUtils.toString(response.getEntity());
        } else {
            responseString = "Error Response: "
                    + response.getStatusLine().toString();
        }

    } catch (IOException e) {
        e.printStackTrace();
    }
    showResponseOnWebView(responseString);
}

private void loginHttpClientGet(final String name, final String password) {
    // 创建 HttpParams 以用来设置 HTTP 参数（这一部分不是必需的）
    BasicHttpParams httpParams = new BasicHttpParams();
    // 设置连接超时和 Socket 超时，以及 Socket 缓存大小
    HttpConnectionParams.setConnectionTimeout(httpParams, 20 * 1000);
    HttpConnectionParams.setSoTimeout(httpParams, 20 * 1000);
    HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
    // 设置重定向，缺省为 true
    HttpClientParams.setRedirecting(httpParams, true);
    // 设置 user agent
    String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2) Gecko/20100115 Firefox/3.6";
    HttpProtocolParams.setUserAgent(httpParams, userAgent);
    // 创建一个 HttpClient 实例
    // 注意 HttpClient httpClient = new HttpClient(); 是Commons HttpClient
    // 中的用法，在 Android 1.5 中我们需要使用 Apache 的缺省实现 DefaultHttpClient
    HttpClient httpClient = new DefaultHttpClient(httpParams);
    Map params2 = new HashMap();
    params2.put("name", name);
    params2.put("password", password);
    String url = loginPath;
     /* 建立HTTPGet对象 */
    String paramStr = "";
    Iterator iter = params2.entrySet().iterator();
    while (iter.hasNext()) {
        Map.Entry entry = (Map.Entry) iter.next();
        Object key = entry.getKey();
        Object val = entry.getValue();
        paramStr += paramStr = "&" + key + "=" + val;
    }
    if (!paramStr.equals("")) {
        paramStr = paramStr.replaceFirst("&", "?");
        url += paramStr;
    }
    HttpGet httpGet = new HttpGet(url);
    String strResult = "doGetError";
    try {
        /* 发送请求并等待响应 */
        HttpResponse httpResponse = httpClient.execute(httpGet);
        /* 若状态码为200 ok */
        if (httpResponse.getStatusLine().getStatusCode() == 200) {
            /* 读返回数据 */
            strResult = EntityUtils.toString(httpResponse.getEntity());
        } else {
            strResult = "Error Response: "
                    + httpResponse.getStatusLine().toString();
        }
    } catch (ClientProtocolException e) {
        strResult = e.getMessage().toString();
        e.printStackTrace();
    } catch (IOException e) {
        strResult = e.getMessage().toString();
        e.printStackTrace();
    } catch (Exception e) {
        strResult = e.getMessage().toString();
        e.printStackTrace();
    }
    showResponseOnWebView(strResult);
}
```
在[示例工程](3)中, 服务器端在登录成功后会跳转到info.jsp页面，详见第一节

使用URLConnection是无法实现的。而HttpClient可以实现跳转并显示登录信息。


## WebView

WebView是Android内置的WebKit内核网页显示控件，使用WebView可以显示本地和网站页面。so powerful!
默认使用UTF-8编码


### 显示本地页面中文乱码
显示本地页面可以使用的API有：loadData 和loadDataWithBaseURL

WebView.loadData(content, "text/html", "UTF-8");出现中文乱码了，
改成 loadData(data, "text/html; charset=UTF-8", null);就不会乱码

```java
WebView.loadDataWithBaseURL(basePath, content, "text/html", "utf-8", null);// 可以正常显示
WebView.loadDataWithBaseURL(null, content, "text/html", "utf-8", null);// 无乱码 但无法显示图片
```
使用loadDataWithBaseURL时,如果不传入URL时,刷新会造成白屏,因为刷新时调用的的是reload方法,
reload是根据传入的URL进行一次重新加载即再次loadUrl(url),不传入URL时,默认的的URL是about:blank

使用loadData,刷新只是从缓存里面取，但是在4.0以上的,如果按照API里所写的loadData(data, “UTF-8”, null);时会乱码,
如果写成`loadData(data, "text/html; charset=UTF-8", null);`
loadData最终的机制是会把传入的三个参数拼接在一起,
然后再进行loadUrl操作,参数就是data, "text/html; charset=UTF-8", null这三个进行拼装,加入text/html; charset=UTF-8就相当于限定了页面的字符

loadData(detail,"text/html;charset=UTF-8", null); 在小米 One S上仍然存在中文乱码的情况，
改成 WebView.loadDataWithBaseURL(null, detail, "text/html", "UTF-8", null); 就没问题。
在小米手机上设置`settings.setDefaultTextEncodingName("utf-8")`
在 webview 的 settings 属性里指定 utf-8 就可以了






# 第三方框架

## AsyncHttpClient

[Github的地址](https://github.com/loopj/android-async-http)


### get

```java
private String loginASyncHttpGet(String name, String password) {
    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    RequestParams requestParams = new RequestParams();
    requestParams.put("name", name);
    requestParams.put("password", password);
    asyncHttpClient.get(loginPath, requestParams, new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(String s) {
            return s;
        }
    });
}
```

### post

```java
private String loginASyncHttpPost(String name, String password) {
    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    RequestParams requestParams = new RequestParams();
    requestParams.put("name", name);
    requestParams.put("password", password);
    asyncHttpClient.post(loginPath, requestParams, new AsyncHttpResponseHandler() {
        @Override
        public void onSuccess(String s) {
          return s;
        }
    });
}
```

### 文件上传

```java
public void uploadFileAsyncHttpClient(String  selectFilePath) {
    File file = new File(selectFilePath);
    if (file.exists() && file.length() > 0) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        try {
            params.put("profile_picture", file);

            client.post(uploadUrlEditTex.getText().toString(), params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String s) {
                    Toast.makeText(MainActivity.this, "文件上传成功!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Throwable throwable, String s) {
                    Toast.makeText(MainActivity.this, "文件上传失败!", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    } else {
        Toast.makeText(this, "文件不存在", Toast.LENGTH_SHORT).show();
    }
}
```


## SmartImage

[Github地址](https://github.com/loopj/android-smart-image-view)

[官网](http://loopj.com/android-smart-image-view/)


# 多线程断点下载

[示例代码下载地址][6]

## 多线程下载
<a name="nulti_download">多线程下载</a>即将一个文件分割成若干部分，交给多个线程同时去下载，每个线程下载文件的一部分。以达到提速的目的。

为了实现多线程下载，需要如下几个步骤：

1. 为每个线程分配要下载的文件的位置
3. 在本地创建同等大小的文件区域
2. 从服务器下载文件的某一段数据
4. 每个线程下载指定位置的数据并写入文件对应的位置

### 为每个线程分配要下载的文件的位置

![](/images/android/network/android-network-multidownload-dispatcher.png)

### 在本地创建同等大小的文件区域

```java

/**
* 创建文件，大小与待下载的文件大小一致
* @param objPath 创建文件文件名，包含完整路径
* @param fileLength 文件的长度
*/
private void createFile(String  objPath,long fileLength) {
	RandomAccessFile raf = null;
	try {
		raf = new RandomAccessFile(objPath, "rwd");
		raf.setLength(fileLength);
		raf.close();
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
}
```

### 从服务器下载文件的某一段数据

在HTTP协议中， RequestProperty `Range`用于指定下载文件片段的范围
格式为`"bytes=<开始位置>-<结束位置>"`
一旦设定了Range属性，服务器会返回 206，表示支持部分下载。
不设定Range属性，服务器返回 200，表示连接正常, 可以下载。

因此发送请求的格式为：

```java
//downloadUrl为下载文件的网址
URL url = new URL(downloadUrl);
//打开远程连接
HttpURLConnection conn = (HttpURLConnection) url.openConnection();
conn.setRequestMethod("GET");
//设置下载的位置
conn.setRequestProperty("Range", "bytes=" + startPosition + "-" + endPosition);
if (206 == conn.getResponseCode())
// DO SOMETHING
```

### 每个线程下载指定位置的数据并写入文件对应的位置

衔接上一段程序
```java
// 从打开的连接读入输入流
InputStream inputStream = conn.getInputStream();
// 创建写入文件的RandomAccessFile对象
RandomAccessFile raf = new RandomAccessFile(objPath, "rwd");
//随机写文件，跳到相应的位置
raf.seek(startPosition);
int len = 0;
int lenCount = 0;
byte[] buffer = new byte[1024];
while ((len = inputStream.read(buffer)) != -1) {
	lenCount += len;
	raf.write(buffer, 0, len);
}
//最后记得关闭掉输入流
raf.close();
inputStream.close();
```



### RandomAccessFile

RandomAccessFile是一个强大的随机文件读写API，创建该对象时的参数设置为`"rwd"`表示立即写入硬盘

```java
RandomAccessFile
public RandomAccessFile(File file,
                        String mode)
                 throws FileNotFoundException
创建从中读取和向其中写入（可选）的随机访问文件流，该文件由 File 参数指定。将创建一个新的 FileDescriptor 对象来表示此文件的连接。
mode 参数指定用以打开文件的访问模式。允许的值及其含意为：
值含意
"r"	以只读方式打开。调用结果对象的任何 write 方法都将导致抛出 IOException。
"rw"	打开以便读取和写入。如果该文件尚不存在，则尝试创建该文件。
"rws"	打开以便读取和写入，对于 "rw"，还要求对文件的内容或元数据的每个更新都同步写入到底层存储设备。
"rwd"  	打开以便读取和写入，对于 "rw"，还要求对文件内容的每个更新都同步写入到底层存储设备。 "rws" 和 "rwd" 模式的工作方式极其类似 FileChannel 类的 force(boolean) 方法，分别传递 true 和 false 参数，除非它们始终应用于每个 I/O 操作，并因此通常更为高效。如果该文件位于本地存储设备上，那么当返回此类的一个方法的调用时，可以保证由该调用对此文件所做的所有更改均被写入该设备。这对确保在系统崩溃时不会丢失重要信息特别有用。如果该文件不在本地设备上，则无法提供这样的保证。
"rwd" 模式可用于减少执行的 I/O 操作数量。使用 "rwd" 仅要求更新要写入存储的文件的内容；使用 "rws" 要求更新要写入的文件内容及其元数据，这通常要求至少一个以上的低级别 I/O 操作。
如果存在安全管理器，则使用 file 参数的路径名作为其参数调用它的 checkRead 方法，以查看是否允许对该文件进行读取访问。如果该模式允许写入，那么还使用该路径参数调用该安全管理器的 checkWrite 方法，以查看是否允许对该文件进行写入访问。
参数：
file - 该文件对象
mode - 访问模式，如 上所述
抛出：
IllegalArgumentException - 如果此模式参数与 "r"、 "rw"、 "rws" 或 "rwd" 的其中一个不相等
FileNotFoundException - 如果该模式为 "r"，但给定的文件对象不表示一个现有的常规文件，或者该模式以 "rw" 开头，但给定的文件对象不表示一个现有的可写常规文件，而且无法创建具有该名称的新常规文件，或者在打开或创建该文件时发生一些其他错误
SecurityException - 如果存在安全管理器，并且其 checkRead 方法拒绝对该文件的读取访问，或者该模式为 "rw"，并且该安全管理器的 checkWrite 方法拒绝对该文件的写入访问
```

```java
seek
public void seek(long pos)
          throws IOException
```
seek是个牛逼的，像一个游标，跳到指定的位置写数据

## 断点下载

当下载大文件时，异常下载中断或暂停下载， 为了防止造成下载资源浪费，为每一个线程提供一个记录，用于记住已经下载文件的位置。当再次启动下载时，读取记录，从中断位置继续下载。

为了兼容不同设备(PC和Android)存储记录文件，本文定义一个接口为不同的存储提供不同的对象，类似于适配器模式.
- SameDirFileBreakPointManager： 在目标文件同目录下建立记录文件(PC和Android)
- SqliteBreakPointManager： 使用Sqlite记录(Android)
- PropertyFileBreakPointManager：使用SharedPerferences记录(Android)

![](/images/android/network/android-network-multidownload-breakpointmanager-uml.png)

工程的调用关系

![](/images/android/network/android-network-multidownload-uml.png)


    参考文献

1. [itheima的教程][3] (密码: yfqk)

![](/images/android/network/android-network-multidownload-itheima.png)

2. [SimpleServer源码](http://pan.baidu.com/s/1qW89zyc)

[3]:http://pan.baidu.com/s/1sjOMIgl "Android network 项目工程"
[2]:http://pan.baidu.com/s/1qW89zyc "SimpleServer源码与war包下载"
[6]:http://pan.baidu.com/s/1pJDvG6Z "多线程下载"

[p-SimpleServer]: /images/android/network/android-network-SimpleServer-index.png
[p-SimpleServer-login-success]: /images/android/network/android-network-SimpleServer-loginSuccess.png
[p-SimpleServer-login-failure]: /images/android/network/android-network-SimpleServer-loginFailure.png
