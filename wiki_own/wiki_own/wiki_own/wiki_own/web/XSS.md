---
title: XSS漏洞与防御
layout: post
date: 2016-07-20 10:30:00
category: web
tags:
 - XSS

share: true
comments: true
---
# XSS攻防


[XSS的原理分析与解剖][a-xss-basic]


# 防御与实现


##　HttpServletRequest请求参数

1. request.getRequestURL()
返回的是完整的url，包括Http协议，端口号，servlet名字和映射路径，但它不包含请求参数。
2. request.getRequestURI()
得到的是request URL的部分值，并且web容器没有decode过的
3. request.getContextPath()
返回 the context of the request.
4. request.getServletPath()
返回调用servlet的部分url.
5. request.getQueryString()
返回url路径后面的查询字符串


- 当前url：http://localhost:8080/CarsiLogCenter_new/idpstat.jsp?action=idp.sptopn
- request.getRequestURL() http://localhost:8080/CarsiLogCenter_new/idpstat.jsp
- request.getRequestURI() /CarsiLogCenter_new/idpstat.jsp
- request.getContextPath()/CarsiLogCenter_new
- request.getServletPath() /idpstat.jsp
- request.getQueryString()action=idp.sptopn



1. 如果请求的URL中queryString中含有敏感的js命令，则直接去掉queryString，重定向到URI(URI是没有经过decode的URL部分值，不包含queryString)，当然重定向后还会经过第2步
2. 如果过滤所有请求的parameterValues、Parameter和Header Value值，将所有的敏感js命令处理。

处理方法为在js敏感命令后添加空格。

疑问:
- post方式的参数经过filter是否可以过滤

## 实现

XSS过滤器
```java
public class xssfilter implements Filter {

    private static String[] exceptRequestArray = {"addNotice.do", "publishMessage.do", "sendMail.do"};

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse rep = (HttpServletResponse)response;
        String requestURI = req.getRequestURI();
// 将所有queryString中的js敏感命令转换为小写
        String qurString = XSSUtils.ignoreCaseUpOrLowToUp(req.getQueryString(), 'N');
        requestURI = requestURI.substring(requestURI.lastIndexOf("/") + 1);
// 判断queryString中是否包含js敏感命令
        if(XSSUtils.containString(qurString)) {
// 包含则去掉queryString
            rep.sendRedirect(requestURI);
            return;
        }
        Arrays.asList(exceptRequestArray);
        if(Arrays.asList(exceptRequestArray).contains(requestURI)) {
// 绕过过滤器
            chain.doFilter(request, response);
        }else {
// XSS包装过滤 parameterValues、Parameter和Header Value
            chain.doFilter(new xssencode((HttpServletRequest)request), response);
        }
    }
}
```

包装请求参数
```java
public class xssencode extends HttpServletRequestWrapper {

    public xssencode(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String[] getParameterValues(String parameter) {
        String[] values = super.getParameterValues(parameter);
        if(values == null) {
            return null;
        }
        int count = values.length;
        String[] encodedValues = new String[count];

        for(int i = 0; i < count; i++) {
            encodedValues[i] = XSSUtils.xssEncode(values[i]);

        }
        return encodedValues;
    }

    @Override
    public String getParameter(String parameter) {
        String value = super.getParameter(parameter);
        return XSSUtils.xssEncode(value);
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        return XSSUtils.xssEncode(value);
    }
}
```

XSS编码过滤js敏感命令
```java
String xssEncode(String value) {
        if(null != value) {
            value = value.replaceAll("<", "《");
            value = value.replaceAll(">", "》");
            value = value.replaceAll("(?i)alert", "a lert");
            value = value.replaceAll("(?i)script", "s cript");
            value = value.replaceAll("(?i)onmouseover", "o nmouseover");
            value = value.replaceAll("(?i)onmouseenter", "o nmouseenter");
            value = value.replaceAll("(?i)onmouseleave", "o nmouseleave");
            value = value.replaceAll("(?i)onmousewheel", "o nmousewheel");
            value = value.replaceAll("(?i)onscroll", "o nscroll");
            value = value.replaceAll("(?i)onfocusin", "o nfocusin");
            value = value.replaceAll("(?i)onfocusout", "o nfocusout");
            value = value.replaceAll("(?i)onbeforecut", "o nbeforecut");
            value = value.replaceAll("(?i)onstart", "o nstart");
            value = value.replaceAll("(?i)onbeforeeditfocus", "o nbeforeeditfocus");
            value = value.replaceAll("(?i)oncontextmenu", "o ncontextmenu");
            value = value.replaceAll("(?i)oncopy", "o ncopy");
            value = value.replaceAll("(?i)oncut", "o ncut");
            value = value.replaceAll("(?i)ondrag", "o ndrag");
            value = value.replaceAll("(?i)ondragend", "o ndragend");
            value = value.replaceAll("(?i)ondragenter", "o ndragenter");
            value = value.replaceAll("(?i)ondragleave", "o ndragleave");
            value = value.replaceAll("(?i)ondragover", "o ndragover");
            value = value.replaceAll("(?i)ondragstart", "o ndragstart");
            value = value.replaceAll("(?i)ondrop", "o ndrop");
            value = value.replaceAll("(?i)onlosecapture", "o nlosecapture");
            value = value.replaceAll("(?i)onpaste", "o npaste");
            value = value.replaceAll("(?i)onselectstart", "o nselectstart");
            value = value.replaceAll("(?i)onhelp", "o nhelp");
            value = value.replaceAll("(?i)onEnd", "o nEnd");
            value = value.replaceAll("(?i)onBegin", "o nBegin");
            value = value.replaceAll("(?i)onactivate", "o nactivate");
            value = value.replaceAll("(?i)onfilterchange", "o nfilterchange");
            value = value.replaceAll("(?i)onbeforeactivate", "o nbeforeactivate");
            value = value.replaceAll("(?i)onbeforedeactivate", "o nbeforedeactivate");
            value = value.replaceAll("(?i)ondeactivate", "o ndeactivate");
            value = value.replaceAll("(?i)onbeforecut", "o nbeforecut");
   }
   return value;
}
```
注册过滤器
```xml
<!-- 解决xss漏洞 -->
<filter>
    <filter-name>xssFilter</filter-name>
    <filter-class>com.paic.saas.common.filter.xssfilter</filter-class>
</filter>
    
<!-- 解决xss漏洞 -->
<filter-mapping>
    <filter-name>xssFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```


----
[参考文献]

1. [XSS的原理分析与解剖][a-xss-basic]
2. [HttpServletRequest常用获取URL的方法](http://blog.csdn.net/gris0509/article/details/6340987)

[a-xss-basic]: http://netsecurity.51cto.com/art/201408/448305_all.htm
