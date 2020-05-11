---
title: "HttpClient"
layout: post
date: 2016-12-12 12:26:00
category: Java
tags:
- Java
- network
share: true
comments: true
---

# HttpClient 2.3.6中 使用 SSL 3

```java
public String httpPost(String strUrl, List<NameValuePair> params) {
    RequestConfig.Builder bld = RequestConfig.custom();
    bld.setConnectTimeout(60000);
    bld.setConnectionRequestTimeout(60000);
    bld.setSocketTimeout(60000);

    RequestConfig config = bld.build();
    CloseableHttpClient closeableHttpClient = null;
    try {
        SSLContext sslcontext = SSLContexts.custom()
                .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                .loadKeyMaterial(null, null)
                .build();

        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslcontext, new String[]{"SSLv3", "TLSv1"}, null,
                SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

        if (closeableHttpClient == null) {
            closeableHttpClient = HttpClients.custom()
                    .setSSLSocketFactory(sslsf).build();
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    String result = null;
    CloseableHttpResponse response = null;
    try {
        HttpPost post = new HttpPost(strUrl);
        post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        response = closeableHttpClient.execute(post);
        int code = response.getStatusLine().getStatusCode();
        System.out.println("请求返回结果: Code:" + code);
        if (code == 200) {
            result = EntityUtils.toString(response.getEntity(), "UTF-8");
        }
        System.out.println("请求返回值为:=> " + result);
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        try {
            if (closeableHttpClient != null) {
                closeableHttpClient.close();
            }
            if (response != null) {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    return result;
}
```
# 