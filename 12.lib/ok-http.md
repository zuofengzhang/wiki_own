# ok-http

```java
OkHttpClient okHttpClient = new OkHttpClient();
//设置媒体类型。application/json表示传递的是一个json格式的对象
MediaType mediaType = MediaType.parse("application/json");
//创建RequestBody对象，将参数按照指定的MediaType封装
RequestBody requestBody = RequestBody.create(mediaType, msgContent);
Request request = new Request
        .Builder()
        .post(requestBody)
        .url(url)
        .build();
try {
    Response response = okHttpClient.newCall(request).execute();
    ResponseBody responseBody = response.body();

    assert responseBody != null;
    String result = responseBody.string();
    long contentLength = requestBody.contentLength();
    logger.debug("responseLength", contentLength);
    logger.debug("result", result);
    responseBody.close();
} catch (IOException e) {
    logger.error("", e);
}
```
