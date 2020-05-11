---
layout: post
title: 图片上传与接收
date: 2016-07-13 11:00:00
category: JavaWeb
tag:
 - upload

share: true
comments: true
---


# 服务端接收图片

```java
boolean excu;
try {
    MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest)request;
    MultipartFile multipartFile = multipartRequest.getFile(CommonConstant.FORM_NAME);
    LogUtil.trace("-->上传图片=multipartFile:" + multipartFile);

    String originalName = multipartFile.getOriginalFilename();
    LogUtil.trace("-->上传图片=originaName:" + originalName+" multipartFileSize:" + multipartFile.getSize());

    InputStream inputStream = multipartFile.getInputStream();
    long size = inputStream.available();
    LogUtil.trace("-->上传图片=file size:" + size);

    if(0 == size || size == -1 || null == userId || "".equals(userId)) {
        excu = false;
    }else {
        excu = true;
    }

    if(excu) {
        originalName = System.currentTimeMillis() + "" + new Random().nextInt(100)
                + originalName.substring(originalName.lastIndexOf("."));
        boolean isUploadSuccess = QiniuUploadUtil.uploadFileToQiniu(“userImgBucketName”, originalName,
                inputStream);

        if(isUploadSuccess) {
            String imageUrl = “userImgURL” + originalName;
        }
    }
}catch(Exception e){
}
```