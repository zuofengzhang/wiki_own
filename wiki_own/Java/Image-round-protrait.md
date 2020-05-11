---
title: "Java图片处理二--合成圆形头像(类似于QQ群聊)"
layout: post
date: 2016-08-10 14:58:00
category: Java
tags:
 - Java
 - Image

share: true
comments: true
---

>  [上一篇][a-last-Java-Image] 介绍了Java合成方形群头像的方法，方形群头像由于易于定位且不涉及形状填充和拼接，实现起来相对简单，本篇介绍的圆形头像方案则复杂很多。

先看一下圆形头像的样子

![圆形头像样例][p-round-portrait]

从例子可以看出有如下几个特性:

1. 每张图片都是圆形的，涉及图片的剪切和图片填充到形状
2. 当图片的个数多于1时，就要考虑图片的遮盖。
3. 图片个数多余1时，需考虑图片的位置：2个是正对角线方向，3、4、5分别是正三角形、正四边形和正五边形



接下来，从上面的三点介绍：



# 图片的形状填充

在Java图形库(`java.awt`)中，存在图片形状填充的API，提供了[Area][a-java-api-area]工具，通过Area可以方便的实现图片的填充.  Area 类可以根据指定的 Shape 对象创建区域几何形状。如果 Shape 还不是封闭的，则显式地封闭几何形状。由 Shape 的几何形状指定的填充规则（奇偶或缠绕）用于确定得到的封闭区域。 

## Area填充

步骤如下:

- 创建Shape
- 创建Area
- 建立画板，设置与Area的交集
- 绘制待填充的图片

```java
int objectImageSize = 260;
int originImageSize = 260;
// 1. 创建Shape，这里定义了一个Polygon的Shape
int xpoints[] = { 20, 70, 130, 240 };
int ypoints[] = { 20, 150, 100, 130 };
Polygon polygon = new Polygon(xpoints, ypoints, 4);
// 2. 创建Area，并将Shape添加到Area
Area tempArea = new Area();
tempArea.add(new Area(polygon));
// 3. 建立画板
BufferedImage tempBufferImage = new BufferedImage(objectImageSize, objectImageSize, BufferedImage.TYPE_INT_RGB); // 定义画板的大小和颜色
Graphics2D g2d = tempBufferImage.createGraphics(); // 创建

g2d.setColor(Color.pink);
g2d.fillRect(0, 0, objectImageSize, objectImageSize); //使用颜色填充，作为背景色
// 消除锯齿
g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
// 设置与Area的交集
g2d.setClip(tempArea);
// 4. 绘制待填充的图片：设置了与Area的交集之后，绘制图片将只在交集内绘制
BufferedImage bufferedImage = ImageUtil.resize2("lt.png", originImageSize, originImageSize, true);
g2d.drawImage(bufferedImage, 0, 0, null);
g2d.dispose(); // 关闭绘图区
// 5. 输出绘图区
String format = "JPG";
ImageIO.write(tempBufferImage, format, new File("result.jpg"));
```

见上面的结果:
![][p-area-1]

## Area剪切

Area可以实现区域的交集、并集、互减和组合区域并减去其交集， 本例使用Area互减实现圆形的互减。下面演示

实现月牙形状

```java
Ellipse2D.Double circle1 = new Ellipse2D.Double(50, 50, 200, 200);
tempArea.add(new Area(circle1)); // 定义Area 1
Ellipse2D.Double circle2 = new Ellipse2D.Double(100, 100, 200, 200);
tempArea.subtract(new Area(circle2)); // 减去Area 2
```

看效果

![][p-area-crescent]



# 圆形头像

接下来实现圆形头像，基本上就是图片的定位，亲，高中几何学的怎么样？

## 图形Item坐标信息

```
public class ItemConfig {
		/**
		 * item的大小, 圆的最小外切矩形
		 */
		int itemSize;
		/**
		 * 留白区域大小
		 */
		int whiteSize;
		/**
		 * item起始坐标
		 */
		int[][] itemLoaction;
		/**
		 * 切口圆的坐标
		 */
		int[][] subtractLocation;
	}
```

## 计算坐标

```java
ItemConfig itemConfig = new ItemConfig();
iconCount = iconCount > 5 ? 5 : iconCount;// 图标最多五个
int borderWidth = 10;
int whiteSize = 10;
int r;
switch (iconCount) {
    case 1:
      int itemSize = imageSize - 2 * borderWidth;
      int x = borderWidth, y = borderWidth;
      itemConfig.itemSize = itemSize;
      itemConfig.whiteSize = 10;
      itemConfig.itemLoaction = new int[1][2];
      itemConfig.itemLoaction[0][0] = x;
      itemConfig.itemLoaction[0][1] = y;
      itemConfig.subtractLocation = null;
    break;
    case 2:
      itemConfig.whiteSize = 10;
      r = (int) ((imageSize - 2 * borderWidth + 2 * itemConfig.whiteSize) / (2 + Math.sqrt(2)));
      itemConfig.itemSize = r * 2;
      itemConfig.itemLoaction = new int[2][2];
      itemConfig.itemLoaction[0][0] = borderWidth;
      itemConfig.itemLoaction[0][1] = borderWidth;
      itemConfig.itemLoaction[1][0] = imageSize - 2 * r - itemConfig.whiteSize - borderWidth;
      itemConfig.itemLoaction[1][1] = imageSize - 2 * r - itemConfig.whiteSize - borderWidth;

      itemConfig.subtractLocation = new int[2][2];
      itemConfig.subtractLocation[0][0] = (int) (itemConfig.itemLoaction[1][0] - 1.5 * itemConfig.whiteSize);
      itemConfig.subtractLocation[0][1] = (int) (itemConfig.itemLoaction[1][0] - 1.5 * itemConfig.whiteSize);
      itemConfig.subtractLocation[1] = null;

    break;
    case 3:
      itemConfig.whiteSize = whiteSize;
      r = (int) ((imageSize - 2 * borderWidth) / (2 + Math.sqrt(3))) + itemConfig.whiteSize / 2;
      itemConfig.itemSize = 2 * r;

      itemConfig.itemLoaction = new int[3][2];
      itemConfig.itemLoaction[0][0] = (imageSize - 2 * borderWidth) / 2 + borderWidth - r;
      itemConfig.itemLoaction[0][1] = borderWidth;
      itemConfig.itemLoaction[1][0] = borderWidth;
      itemConfig.itemLoaction[1][1] = imageSize - borderWidth - 2 * r;
      itemConfig.itemLoaction[2][0] = imageSize - borderWidth - 2 * r;
      itemConfig.itemLoaction[2][1] = imageSize - borderWidth - 2 * r;

      itemConfig.subtractLocation = new int[3][2];
      itemConfig.subtractLocation[0][0] = itemConfig.itemLoaction[1][0] - itemConfig.itemLoaction[0][0];
      itemConfig.subtractLocation[0][1] = itemConfig.itemLoaction[1][1] - itemConfig.itemLoaction[0][1];
      itemConfig.subtractLocation[1][0] = itemConfig.itemLoaction[2][0] - itemConfig.itemLoaction[1][0];
      itemConfig.subtractLocation[1][1] = itemConfig.itemLoaction[2][1] - itemConfig.itemLoaction[1][1];
      itemConfig.subtractLocation[2][0] = itemConfig.itemLoaction[0][0] - itemConfig.itemLoaction[2][0];
      itemConfig.subtractLocation[2][1] = itemConfig.itemLoaction[0][1] - itemConfig.itemLoaction[2][1];
    break;
    case 4:
      whiteSize = 10;
      itemConfig.whiteSize = whiteSize;
      r = (int) ((imageSize - 2.0 * borderWidth) / 4 + whiteSize);
      itemConfig.itemSize = 2 * r;

      itemConfig.itemLoaction = new int[4][2];
      itemConfig.itemLoaction[0][0] = borderWidth;
      itemConfig.itemLoaction[0][1] = borderWidth;
      itemConfig.itemLoaction[1][0] = itemConfig.itemLoaction[0][0];
      itemConfig.itemLoaction[1][1] = imageSize - borderWidth - whiteSize - 2 * r;
      itemConfig.itemLoaction[2][0] = imageSize - borderWidth - whiteSize - 2 * r;
      itemConfig.itemLoaction[2][1] = itemConfig.itemLoaction[1][1];
      itemConfig.itemLoaction[3][0] = itemConfig.itemLoaction[2][0];
      itemConfig.itemLoaction[3][1] = itemConfig.itemLoaction[0][1];

      itemConfig.subtractLocation = new int[4][2];
      itemConfig.subtractLocation[0][0] = 0;
      itemConfig.subtractLocation[0][1] = itemConfig.itemLoaction[1][1] - itemConfig.itemLoaction[0][1];
      itemConfig.subtractLocation[1][0] = itemConfig.itemLoaction[2][0] - itemConfig.itemLoaction[1][0];
      itemConfig.subtractLocation[1][1] = 0;
      itemConfig.subtractLocation[2][0] = 0;
      itemConfig.subtractLocation[2][1] = itemConfig.itemLoaction[3][1] - itemConfig.itemLoaction[2][1];
      itemConfig.subtractLocation[3][0] = itemConfig.itemLoaction[0][0] - itemConfig.itemLoaction[3][0];
      itemConfig.subtractLocation[3][1] = 0;
    break;
    case 5:
      whiteSize = 10;
      itemConfig.whiteSize = whiteSize;
      r = (int) ((imageSize - 2.0 * borderWidth)
      / (2 * (Math.cos(Math.toRadians(18)) + Math.cos(Math.toRadians(54))) + 2)) + whiteSize;
      itemConfig.itemSize = 2 * r;

      itemConfig.itemLoaction = new int[5][2];
      itemConfig.itemLoaction[0][0] = (imageSize - 2 * borderWidth) / 2 - r + borderWidth;
      itemConfig.itemLoaction[0][1] = borderWidth;
      itemConfig.itemLoaction[1][0] = borderWidth;
      itemConfig.itemLoaction[1][1] = (int) (2 * r * Math.cos(Math.toRadians(54))) + borderWidth;
      itemConfig.itemLoaction[2][0] = (int) (borderWidth + 2 * r * Math.sin(Math.toRadians(18)));
      itemConfig.itemLoaction[2][1] = imageSize - borderWidth - 2 * r;
      itemConfig.itemLoaction[3][0] = (int) (imageSize - borderWidth - 2 * r
      - 2 * r * Math.sin(Math.toRadians(18)));
      itemConfig.itemLoaction[3][1] = itemConfig.itemLoaction[2][1];
      itemConfig.itemLoaction[4][0] = imageSize - borderWidth - 2 * r;
      itemConfig.itemLoaction[4][1] = itemConfig.itemLoaction[1][1];

      itemConfig.subtractLocation = new int[5][2];
      itemConfig.subtractLocation[0][0] = itemConfig.itemLoaction[1][0] - itemConfig.itemLoaction[0][0];
      itemConfig.subtractLocation[0][1] = itemConfig.itemLoaction[1][1] - itemConfig.itemLoaction[0][1];
      itemConfig.subtractLocation[1][0] = itemConfig.itemLoaction[2][0] - itemConfig.itemLoaction[1][0];
      itemConfig.subtractLocation[1][1] = itemConfig.itemLoaction[2][1] - itemConfig.itemLoaction[1][1];
      itemConfig.subtractLocation[2][0] = itemConfig.itemLoaction[3][0] - itemConfig.itemLoaction[2][0];
      itemConfig.subtractLocation[2][1] = itemConfig.itemLoaction[3][1] - itemConfig.itemLoaction[2][1];
      itemConfig.subtractLocation[3][0] = itemConfig.itemLoaction[4][0] - itemConfig.itemLoaction[3][0];
      itemConfig.subtractLocation[3][1] = itemConfig.itemLoaction[4][1] - itemConfig.itemLoaction[3][1];
      itemConfig.subtractLocation[4][0] = itemConfig.itemLoaction[0][0] - itemConfig.itemLoaction[4][0];
      itemConfig.subtractLocation[4][1] = itemConfig.itemLoaction[0][1] - itemConfig.itemLoaction[4][1];
    break;
}
```



## 绘制圆形头像

```java
ItemConfig itemConfig = calcLocation(480, 5);
BufferedImage imageNew = new BufferedImage(480, 480, BufferedImage.TYPE_INT_RGB);
Graphics2D graphics2DNew = imageNew.createGraphics();
graphics2DNew.setColor(Color.white);
graphics2DNew.fillRect(0, 0, 480, 480);
graphics2DNew.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
graphics2DNew.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

for (int i = 0; i < itemConfig.itemLoaction.length; i++) {
  String fileName = urls.get(i);
  Area tempArea = new Area();
  Ellipse2D.Double circle0 = new Ellipse2D.Double(10, 10, itemConfig.itemSize - 2 * itemConfig.whiteSize,
  itemConfig.itemSize - 2 * itemConfig.whiteSize);
  tempArea.add(new Area(circle0));

  if (itemConfig.subtractLocation != null && itemConfig.subtractLocation[i] != null) {
    Ellipse2D.Double circle1 = new Ellipse2D.Double(itemConfig.subtractLocation[i][0],
    itemConfig.subtractLocation[i][1], itemConfig.itemSize, itemConfig.itemSize);
    tempArea.subtract(new Area(circle1));
  }
  graphics2DNew.drawImage(fillImageToModel(fileName, itemConfig.itemSize, tempArea),
  itemConfig.itemLoaction[i][0], itemConfig.itemLoaction[i][1], null);
}
graphics2DNew.dispose();
return imageNew;
```



效果上面已经有了...







---

参考文献:



[a-last-Java-Image]: /Java/Image-zip-square-portrait/
[p-round-portrait]: /images/java/image/round-portrait.png
[p-area-1]: /images/java/image/area-clip.jpg
[p-area-crescent]: /images/java/image/area-crescent.jpg
[a-java-api-area]: http://tool.oschina.net/apidocs/apidoc?api=jdk-zh
