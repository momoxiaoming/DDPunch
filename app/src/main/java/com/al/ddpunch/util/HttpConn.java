package com.al.ddpunch.util;

import android.util.Log;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class HttpConn {
	// post����
	public static String post(String url, String content) {
		HttpURLConnection conn = null;
		// ����һ��URL����
		String response = "";
		InputStream is = null;
		OutputStream out = null;

		try {
			URL murl = new URL(url);
			trustAllHosts();
			if (murl.getProtocol().toLowerCase().equals("https")) {
				HttpsURLConnection https = (HttpsURLConnection) murl
						.openConnection();
				https.setHostnameVerifier(DO_NOT_VERIFY);
				conn = https;
			} else {
				conn = (HttpURLConnection) murl.openConnection();
			}
			conn.setRequestMethod("POST");// �������󷽷�Ϊpost

			conn.setDoOutput(true);// ���ô˷���,������������������// post����Ĳ���
			// ���һ�������,�������д����,Ĭ�������,ϵͳ��������������������
			out = conn.getOutputStream();// ���һ�������,�������д����out.write(data.getBytes());
			out.write(content.getBytes());
			out.flush();
			out.close();
			int responseCode = conn.getResponseCode();// ���ô˷����Ͳ�����ʹ��conn.connect()����if
			if (responseCode == 200) {
				is = conn.getInputStream();
				response = getStringFromInputStream(is);
				if (response.startsWith("\"")) {
					response = response.replace("\"", "");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
		return response;

	}

	// get����
	public static String get(String url) {
		String response = "";
		InputStream is = null;
		HttpURLConnection conn = null;
		try {
			URL murl = new URL(url);
			trustAllHosts();
			if (murl.getProtocol().toLowerCase().equals("https")) {
				HttpsURLConnection https = (HttpsURLConnection) murl
						.openConnection();
				https.setHostnameVerifier(DO_NOT_VERIFY);
				conn = https;
			} else {
				conn = (HttpURLConnection) murl.openConnection();
			}
			conn.setRequestMethod("GET");
			conn.setReadTimeout(5000);
			conn.setConnectTimeout(10000);

			int responseCode = conn.getResponseCode();
			if (responseCode == 200) {
				is = conn.getInputStream();
				response = getStringFromInputStream(is);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (conn != null) {
				conn.disconnect();
				conn = null;
			}
		}
		return response;
	}

	private static String getStringFromInputStream(InputStream is) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		String state = "";
		try {
			byte[] buffer = new byte[1024];
			int len = -1;
			while ((len = is.read(buffer)) != -1) {
				os.write(buffer, 0, len);
			}
			is.close();
			state = os.toString().trim();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return state;
	}

//
	final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {

		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};

	/**
	 * ��������֤�� #http://blog.csdn.net/jianglili611/article/details/46290431
	 */
	private static void trustAllHosts() {
		final String TAG = "trustAllHosts";
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

			public X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[] {};
			}

			public void checkClientTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
				Log.i(TAG, "checkClientTrusted");
			}

			public void checkServerTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
				Log.i(TAG, "checkServerTrusted");
			}
		} };

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection
					.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



}
