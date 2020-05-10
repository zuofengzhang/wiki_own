
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 图像的通用处理类(支持图像压缩大小和合并为头像)
 *
 */
public class ImageUtil {
	static class MyX509TrustManager implements X509TrustManager {

		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	}

	/**
	 * 图像转换为输入流
	 *
	 * @param bufferedImage
	 * @return 输出流
	 */
	public static InputStream image2InputStream(BufferedImage bufferedImage) {
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


	/**
	 * 图片缩放
	 *
	 * @param filePath 图片路径
	 * @param height   高度
	 * @param width    宽度
	 * @param bb       比例不对时是否需要补白
	 * @throws IOException
	 */
	public static BufferedImage resize2(String filePath, int height, int width, boolean bb) throws IOException {
		double ratio = 0; // 缩放比例
		BufferedImage bi = ImageIO.read(readImageFromPath(filePath));
		Image itemp = bi.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		// 计算比例
		if ((bi.getHeight() > height) || (bi.getWidth() > width)) {
			if (bi.getHeight() > bi.getWidth()) {
				ratio = (new Integer(height)).doubleValue() / bi.getHeight();
			} else {
				ratio = (new Integer(width)).doubleValue() / bi.getWidth();
			}
			AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(ratio, ratio), null);
			itemp = op.filter(bi, null);
		}
		if (bb) {
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = image.createGraphics();
			g.setColor(Color.white);
			g.fillRect(0, 0, width, height);
			if (width == itemp.getWidth(null))
				g.drawImage(itemp, 0, (height - itemp.getHeight(null)) / 2, itemp.getWidth(null), itemp.getHeight(null),
						Color.white, null);
			else
				g.drawImage(itemp, (width - itemp.getWidth(null)) / 2, 0, itemp.getWidth(null), itemp.getHeight(null),
						Color.white, null);
			g.dispose();
			itemp = image;
		}
		return (BufferedImage) itemp;
	}

	private static DefaultHttpClient wrapClient(org.apache.http.client.HttpClient base) {
		try {
			SSLContext ctx = SSLContext.getInstance("TLS");
			X509TrustManager tm = new MyX509TrustManager();
			ctx.init(null, new TrustManager[]{tm}, null);
			SSLSocketFactory ssf = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("https", 443, ssf));
			ThreadSafeClientConnManager mgr = new ThreadSafeClientConnManager(registry);
			return new DefaultHttpClient(mgr, base.getParams());
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	private static InputStream httpRequest(String httpUrl, Map<String, String> params) {
		java.io.InputStream in = null;
		HttpClient hc = null;

		try {
			if (httpUrl.toLowerCase().startsWith("https//")) {
				hc = wrapClient(new DefaultHttpClient());
			} else {
				hc = new DefaultHttpClient();
			}
			HttpPost post = new HttpPost(httpUrl);

			List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
			if (params != null) {
				Set<String> keySet = params.keySet();
				for (String key : keySet) {
					nvps.add(new BasicNameValuePair(key, params.get(key)));
				}
			}
			post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			hc.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 6000);
			HttpResponse response = hc.execute(post);
			in = response.getEntity().getContent();
		} catch (Exception ignored) {
		} finally {
			return in;
		}

	}

	private static InputStream readImageFromPath(String filePath) throws IOException {
		if (filePath.toLowerCase().startsWith("https://") || filePath.toLowerCase().startsWith("http://")) {
			return httpRequest(filePath, null);
		} else {
			return new FileInputStream(new File(filePath));
		}
	}

	public static void saveImage(BufferedImage bufferedImage, String objectFileName)
			throws IOException {
		String imageProfix = objectFileName.substring(objectFileName.lastIndexOf('.') + 1);

		String format = "JPG";
		if ("jpg".equalsIgnoreCase(imageProfix) || "jpeg".equalsIgnoreCase(imageProfix)) {
			format = "JPG";
		} else if ("png".equalsIgnoreCase(imageProfix)) {
			format = "PNG";
		}
		ImageIO.write(bufferedImage, format, new File(objectFileName));
	}

	public static void main(String[] args) throws IOException {
		InputStream inputStream = readImageFromPath("https://o7geosdk6.qnssl.com/147257121725648.jpg");
		File file = new File(System.currentTimeMillis() + ".jpg");
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		byte[] buffer = new byte[1024];
		int size = 1;
		while ((size = inputStream.read(buffer)) != 0) {
			fileOutputStream.write(buffer, 0, size);
		}
		inputStream.close();
		fileOutputStream.close();
	}
}
