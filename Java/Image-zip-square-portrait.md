---
title: Java图片处理一(压缩图片与生成微信头像)
layout: post
date: 2016-07-14 14:58:00
category: Java
tags:
 - Java
 - Image

share: true
comments: true
---

# 读入图片(本地图片和网络图片)
```java
InputStream readImageFromPath(String filePath)
            throws IOException {
        if (filePath.startsWith("http")) {
            URL url = null;
            HttpURLConnection conn;
            try {
                url = new URL(filePath);
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream inputStream = conn.getInputStream();
                return inputStream;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else {
            return new FileInputStream(new File(filePath));
        }
        return null;
    }

// BufferedImage bi = ImageIO.read(readImageFromPath(filePath));
```

# 压缩图像

```java
/**
 * 图片缩放
 * 
 * @param filePath
 *            图片路径
 * @param height
 *            高度
 * @param width
 *            宽度
 * @param bb
 *            比例不对时是否需要补白
 */
BufferedImage resize2(String filePath, int height, int width, boolean bb) {
	try {
		double ratio = 0; // 缩放比例
		BufferedImage bi = ImageIO.read(readImageFromPath(filePath));
		Image itemp = bi.getScaledInstance(width, height,
				Image.SCALE_SMOOTH);
		// 计算比例
		if ((bi.getHeight() > height) || (bi.getWidth() > width)) {
			if (bi.getHeight() > bi.getWidth()) {
				ratio = (new Integer(height)).doubleValue()
						/ bi.getHeight();
			} else {
				ratio = (new Integer(width)).doubleValue() / bi.getWidth();
			}
			AffineTransformOp op = new AffineTransformOp(
					AffineTransform.getScaleInstance(ratio, ratio), null);
			itemp = op.filter(bi, null);
		}
		if (bb) {
			BufferedImage image = new BufferedImage(width, height,
					BufferedImage.TYPE_INT_RGB);
			Graphics2D g = image.createGraphics();
			g.setColor(Color.white);
			g.fillRect(0, 0, width, height);
			if (width == itemp.getWidth(null))
				g.drawImage(itemp, 0, (height - itemp.getHeight(null)) / 2,
						itemp.getWidth(null), itemp.getHeight(null),
						Color.white, null);
			else
				g.drawImage(itemp, (width - itemp.getWidth(null)) / 2, 0,
						itemp.getWidth(null), itemp.getHeight(null),
						Color.white, null);
			g.dispose();
			itemp = image;
		}
		return (BufferedImage) itemp;
	} catch (IOException e) {
		e.printStackTrace();
	}
	return null;
}
```

# 生成微信头像

```java
class ImageElement {
    int left;
	int top;
	BufferedImage bufferedImage;
}

/**
 * 最多行列数
 */
private int MAX_GRIDS = 3;
/**
 * 行间距
 */
private int spacing = 5;
/**
 * 边距
 */
private int border = 10;

/**
 * 图片的目标大小
 */
private int width = 200;

private ImageCombinationUtil() {
}

/**
 * 生成组合头像
 * 
 * @param paths
 *            用户图像
 * @throws IOException
 */
public void getCombinationOfhead(List<String> paths) throws IOException {
	int pathSize = paths.size();
	if (pathSize == 0) {
		return;
	}
	final int maxImageCount = MAX_GRIDS * MAX_GRIDS;
	int imageCount = pathSize > maxImageCount ? maxImageCount : pathSize;
	int grids = (int) Math.ceil(Math.sqrt(imageCount));
	grids = grids > MAX_GRIDS ? MAX_GRIDS : grids;
	int hrow = imageCount / grids;
	int rows = hrow + (imageCount - hrow * grids > 0 ? 1 : 0);
	int columnCountOfLastRow = imageCount - (rows - 1) * grids;
	int columns = imageCount / rows;
	columns = imageCount - columns * rows > 0 ? columns + 1 : columns;
	System.out.println("imageCount :" + pathSize + "\t grids :" + grids
			+ "\trows:" + rows + "\t columns:" + columns
			+ "\t columnsOfLastRow:" + columnCountOfLastRow);

	int[] matrix = new int[rows];
	Arrays.fill(matrix, grids);
	matrix[0] = columnCountOfLastRow;

	// 计算每个图片的尺寸
	int imageMeasure = (width - 2 * border - (grids - 1)
			* spacing)
			/ grids;

	List<ImageElement> imageElements = new ArrayList<ImageElement>();
	for (int i = 0; i < imageCount; i++) {
		ImageElement imageMatrix = new ImageElement();
		imageMatrix.bufferedImage = ImageCombinationUtil.resize2(paths.get(i),
				imageMeasure, imageMeasure, true);
		imageElements.add(imageMatrix);
	}

	// 计算坐标
	int top = border;
	top = rows < grids ? top + (spacing + imageMeasure) * (grids - rows)
			/ 2 : top;
	int imageIndex = 0;
	for (int row = 0; row < matrix.length; row++) {
		int column = 0;
		int left = border;
		left = matrix[row] < grids ? left + (spacing + imageMeasure)
				* (grids - matrix[row])/2 : left;
		while (column < matrix[row]) {
			imageElements.get(imageIndex).left = left + column
					* (imageMeasure + spacing);
			imageElements.get(imageIndex).top = top + row
					* (imageMeasure + spacing);
			imageIndex++;
			column++;
		}
	}
	// 创建画板
	BufferedImage outImage1 = new BufferedImage(width,
			width, BufferedImage.TYPE_INT_RGB);
	// 生成画布
	Graphics g = outImage1.getGraphics();
	Graphics2D g2d = (Graphics2D) g;
	// 设置背景色
	g2d.setBackground(new Color(231, 231, 231));
	// 通过使用当前绘图表面的背景色进行填充来清除指定的矩形。
	g2d.clearRect(0, 0, width, width);
	// 绘制
	for (ImageElement imageElement : imageElements) {
		g2d.drawImage(imageElement.bufferedImage, imageElement.left,
				imageElement.top, null);
		// 需要改变颜色的话在这里绘上颜色。可能会用到AlphaComposite类
	}

	String outPath = "d:\\" + System.currentTimeMillis() + ".jpg";
	String format = "JPG";
	ImageIO.write(outImage1, format, new File(outPath));
}
```
效果

![][p-image-combin-result]

# 图像转化为InputStream
方便直接从内存上传到服务器
```java
InputStream image2InputStream(BufferedImage bufferedImage) {
	InputStream inputStream = null;
	ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	ImageOutputStream imageOutputStream = null;
	try {
		imageOutputStream = ImageIO.createImageOutputStream(outputStream);
		ImageIO.write(bufferedImage, "JPG", imageOutputStream);
		inputStream = new ByteArrayInputStream(outputStream.toByteArray());
	} catch (IOException e) {
		e.printStackTrace();
	}
	return inputStream;
}
```


----
[p-image-combin-result]: /images/java/util/imageCombinResult.png

