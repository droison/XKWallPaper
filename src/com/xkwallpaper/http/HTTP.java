package com.xkwallpaper.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.alibaba.fastjson.JSON;
import com.xkwallpaper.http.base.HttpResponseEntity;

import android.graphics.Bitmap;
import android.util.Log;

public class HTTP {
	private static final String TAG = "HTTP";

	private static DefaultHttpClient httpClient = createHttpClient();

	private static HttpContext localContext = new BasicHttpContext();

	private static CookieStore cookieStore = new BasicCookieStore();

	private static DefaultHttpClient createHttpClient() {
		HttpParams params = new BasicHttpParams();

		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, "UTF-8");
		HttpProtocolParams.setUseExpectContinue(params, true);

		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schReg.register(new Scheme("https", PlainSocketFactory.getSocketFactory(), 433));
		ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);
		DefaultHttpClient hc = new DefaultHttpClient(conMgr, params);
		hc.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 8000);

		hc.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 80000);
		return hc;
	};

	static {
		localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
	}

	public static HttpResponseEntity postByHttpUrlConnection(String pathUrl, Object o) {

		HttpResponseEntity hre = new HttpResponseEntity();
		InputStream input = null;
		HttpURLConnection httpConn = null;
		try {
			// 建立连接
			URL url = new URL(pathUrl);
			httpConn = (HttpURLConnection) url.openConnection();

			// //设置连接属性
			httpConn.setDoOutput(true);// 使用 URL 连接进行输出
			httpConn.setDoInput(true);// 使用 URL 连接进行输入
			httpConn.setUseCaches(false);// 忽略缓存
			httpConn.setConnectTimeout(8000); // 抛出ConnectTimeoutException,MalformedURLException这个异常。
			httpConn.setReadTimeout(8000);
			httpConn.setRequestMethod("POST");// 设置URL请求方法

			String requestString = JSON.toJSONString(o);
			Log.d("HTTp", requestString);
			byte[] requestStringBytes = requestString.getBytes("utf-8");// 获得数据字节数据，请求数据流的编码，必须和下面服务器端处理请求流的编码一致
			httpConn.setRequestProperty("Content-length", "" + requestStringBytes.length);
			httpConn.setRequestProperty("Content-Type", "application/json");
			httpConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
			httpConn.setRequestProperty("Charset", "UTF-8");

			httpConn.setRequestProperty("name", "request");

			OutputStream outputStream = httpConn.getOutputStream(); // 建立输出流，并写入数据
			outputStream.write(requestStringBytes);
			outputStream.close();

			hre.setHttpResponseCode(httpConn.getResponseCode());
			input = httpConn.getInputStream();
			hre.setB(readInputStream(input));
			return hre;
		} catch (Exception ex) {
			ex.printStackTrace();
			return hre;
		} finally {
			if (httpConn != null)
				httpConn.disconnect();
			try {
				if (input != null)
					input.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// 上传代码，第一个参数，为要使用的URL，第二个参数，为表单内容，第三个参数为要上传的文件，可以上传多个文件，这根据需要页定
	public static HttpResponseEntity put(String actionUrl, String token, Bitmap bm) {
		DataOutputStream outStream = null;
		HttpResponseEntity hre = null;
		InputStream input = null;
		HttpURLConnection conn = null;
		try {
			String BOUNDARY = java.util.UUID.randomUUID().toString();
			String PREFIX = "--", LINEND = "\r\n";
			String MULTIPART_FROM_DATA = "multipart/form-data";
			String CHARSET = "UTF-8";
			URL uri = new URL(actionUrl);
			conn = (HttpURLConnection) uri.openConnection();
			conn.setReadTimeout(8 * 1000);
			conn.setDoInput(true);// 允许输入
			conn.setDoOutput(true);// 允许输出
			conn.setUseCaches(false);
			conn.setRequestMethod("PUT"); // Post方式
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Charsert", "UTF-8");
			conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA + ";boundary=" + BOUNDARY);
			// 首先组拼文本类型的参数
			StringBuilder sb = new StringBuilder();

			sb.append(PREFIX);
			sb.append(BOUNDARY);
			sb.append(LINEND);
			sb.append("Content-Disposition: form-data; name=\"private_token\"" + LINEND);
			sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
			sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
			sb.append(LINEND);
			sb.append(token);
			sb.append(LINEND);

			outStream = new DataOutputStream(conn.getOutputStream());
			outStream.write(sb.toString().getBytes());
			// 发送文件数据

			StringBuilder sb1 = new StringBuilder();
			sb1.append(PREFIX);
			sb1.append(BOUNDARY);
			sb1.append(LINEND);
			sb1.append("Content-Disposition: form-data; name=\"face\"; filename=\"head.jpg\"" + LINEND);
			sb1.append("Content-Type: multipart/form-data; charset=" + CHARSET + LINEND);
			sb1.append(LINEND);
			outStream.write(sb1.toString().getBytes());

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			InputStream is = new ByteArrayInputStream(baos.toByteArray());
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = is.read(buffer)) != -1) {
				outStream.write(buffer, 0, len);
			}
			is.close();
			outStream.write(LINEND.getBytes());

			// 请求结束标志
			byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
			outStream.write(end_data);
			outStream.flush();

			// 得到响应码
			hre = new HttpResponseEntity();
			hre.setHttpResponseCode(conn.getResponseCode());

			input = conn.getInputStream();
			hre.setB(readInputStream(input));
			return hre;
		} catch (Exception e) {
			return hre;
		} finally {
			if (conn != null)
				conn.disconnect();
			try {
				if (outStream != null)
					outStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				if (input != null)
					input.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static HttpResponseEntity putByHttpUrlConnection(String pathUrl, Object o) {

		HttpResponseEntity hre = new HttpResponseEntity();
		InputStream input = null;
		HttpURLConnection httpConn = null;
		try {
			// 建立连接
			URL url = new URL(pathUrl);
			httpConn = (HttpURLConnection) url.openConnection();

			// //设置连接属性
			httpConn.setDoOutput(true);// 使用 URL 连接进行输出
			httpConn.setDoInput(true);// 使用 URL 连接进行输入
			httpConn.setUseCaches(false);// 忽略缓存
			httpConn.setConnectTimeout(8000); // 抛出ConnectTimeoutException,MalformedURLException这个异常。
			httpConn.setReadTimeout(8000);
			httpConn.setRequestMethod("PUT");// 设置URL请求方法

			String requestString = JSON.toJSONString(o);
			byte[] requestStringBytes = requestString.getBytes("utf-8");// 获得数据字节数据，请求数据流的编码，必须和下面服务器端处理请求流的编码一致
			httpConn.setRequestProperty("Content-length", "" + requestStringBytes.length);
			httpConn.setRequestProperty("Content-Type", "application/json");
			httpConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
			httpConn.setRequestProperty("Charset", "UTF-8");

			httpConn.setRequestProperty("name", "request");

			OutputStream outputStream = httpConn.getOutputStream(); // 建立输出流，并写入数据
			outputStream.write(requestStringBytes);
			outputStream.close();

			hre.setHttpResponseCode(httpConn.getResponseCode());
			input = httpConn.getInputStream();
			hre.setB(readInputStream(input));
			return hre;
		} catch (Exception ex) {
			ex.printStackTrace();
			return hre;
		} finally {
			if (httpConn != null)
				httpConn.disconnect();
			try {
				if (input != null)
					input.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static boolean deleteMothod(String pathUrl) {

		InputStream input = null;
		HttpURLConnection httpConn = null;
		try {
			// 建立连接
			URL url = new URL(pathUrl);
			httpConn = (HttpURLConnection) url.openConnection();

			// //设置连接属性
			httpConn.setDoInput(true);// 使用 URL 连接进行输入
			httpConn.setUseCaches(false);// 忽略缓存
			httpConn.setConnectTimeout(8000); // 抛出ConnectTimeoutException,MalformedURLException这个异常。
			httpConn.setReadTimeout(8000);
			httpConn.setRequestMethod("DELETE");// 设置URL请求方法

			httpConn.setRequestProperty("Content-Type", "application/json");
			httpConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
			httpConn.setRequestProperty("Charset", "UTF-8");

			httpConn.setRequestProperty("name", "request");

			input = httpConn.getInputStream();
			if (httpConn.getResponseCode() == 200)
				return true;
			else
				return false;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		} finally {
			if (httpConn != null)
				httpConn.disconnect();
			try {
				if (input != null)
					input.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static HttpResponseEntity post(String URL, List<NameValuePair> pairList) {

		String url = URL;
		HttpResponse response = null;
		HttpResponseEntity hre = new HttpResponseEntity();
		InputStream input = null;
		try {
			HttpEntity requestHttpEntity = new UrlEncodedFormEntity(pairList);
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(requestHttpEntity);
			HttpClient httpClient = new DefaultHttpClient();
			response = httpClient.execute(httpPost);

			int code = response.getStatusLine().getStatusCode();

			if (code != 204) {
				input = response.getEntity().getContent();
				byte[] b = readInputStream(input);
				hre.setHttpResponseCode(code);
				hre.setB(b);
			}
			return hre;
		} catch (Exception e) {
			e.printStackTrace();
			return hre;
		} finally {
			if (input != null) {
				try {
					input.close();
					Log.d(TAG, "CONNECTIONCLOSE");
				} catch (IOException e) {
					Log.e(TAG, "CONNECTIONCLOSE", e);
				}
			}
		}

	}

	public static HttpResponseEntity get(String URL) {
		String url = URL;
		Log.i("HTTP_URL", url);

		HttpGet listGet = new HttpGet(url);
		HttpResponse response;
		HttpResponseEntity hre = new HttpResponseEntity();
		InputStream input = null;
		try {
			response = httpClient.execute(listGet, localContext);
			int code = response.getStatusLine().getStatusCode();

			hre.setHttpResponseCode(code);
			if (code != 204) {
				input = response.getEntity().getContent();
				byte[] b = readInputStream(input);
				hre.setB(b);
			}
			return hre;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "HTTPCONNECTION", e);
			return null;
		} finally {
			if (input != null) {
				try {
					input.close();
					Log.d(TAG, "CONNECTIONCLOSE");
				} catch (IOException e) {
					Log.e(TAG, "CONNECTIONCLOSE", e);
				}
			}
		}

	}

	public static boolean download(String URL, File oldfile) throws IOException {
		String url = URL;
		Log.i("HTTP_URL", url);

		HttpGet listGet = new HttpGet(url);
		HttpResponse response;
		InputStream input = null;

		response = httpClient.execute(listGet, localContext);
		int code = response.getStatusLine().getStatusCode();

		if (code == 200) {
			input = response.getEntity().getContent();
			FileOutputStream fos = new FileOutputStream(oldfile);
			byte buf[] = new byte[1024];
			int numread = 0;
			while ((numread = input.read(buf)) != -1) {
				fos.write(buf, 0, numread);
			}
			fos.close();

			return true;
		} else {
			return false;
		}

	}

	public static List<Cookie> getCookie() {
		return cookieStore.getCookies();
	}

	public static void clearCookie() {
		if (cookieStore != null) {
			cookieStore.clear();
		}
	}

	static void addCookie(HttpGet get) {
		StringBuilder tmpcookies = new StringBuilder();

		List<Cookie> cookies = getCookie();
		for (Cookie c : cookies) {
			tmpcookies.append(c.getName());
			tmpcookies.append("=");
			tmpcookies.append(c.getValue());
			tmpcookies.append(";");
			tmpcookies.append("domain=");
			tmpcookies.append(c.getDomain());
		}

		get.setHeader("Cookie", tmpcookies.toString());
	}

	public static byte[] readInputStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		inStream.close();
		return outStream.toByteArray();
	}
}
