package com.andr.tool.encode;

import java.io.File;
import java.io.FileInputStream;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
	protected static char[] hexDigits = new char[] { '0', '1', '2', '3', '4',
			'5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };;
	protected static MessageDigest messagedigest = null;

	static {
		try {
			MD5Util.messagedigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException v0) {

		}
	}

	private static void appendHexPair(byte bt, StringBuffer strBuf) {
		char hChr = MD5Util.hexDigits[(bt & 0xF0) >> 4];
		char lChr = MD5Util.hexDigits[bt & 0x0F];
		strBuf.append(hChr);
		strBuf.append(lChr);
	}

	private static String bufferToHex(byte[] bytes) {
		return MD5Util.bufferToHex(bytes, 0, bytes.length);
	}

	private static String bufferToHex(byte[] bytes, int offSet, int len) {
		StringBuffer StrBuf = new StringBuffer(len * 2);
		int endIdx = offSet + len;

		for (int i = offSet; i < endIdx; i++) {
			MD5Util.appendHexPair(bytes[i], StrBuf);
		}

		return StrBuf.toString();
	}

	public static boolean checkPassword(String pwd, String md5PwdStr) {
		return MD5Util.getMD5String(pwd).equals(md5PwdStr);
	}

	public static String getFileMD5String(File file) {
		String rlt;
		try {
			MD5Util.messagedigest.update(new FileInputStream(file).getChannel()
					.map(FileChannel.MapMode.READ_ONLY, 0, file.length()));
			rlt = MD5Util.bufferToHex(MD5Util.messagedigest.digest());
		} catch (Exception v1) {
			rlt = "";
		}

		return rlt;
	}

	public static String getMD5String(String cnt) {
//		try {
//			return MD5Util.getMD5String(cnt.getBytes("UTF-8"));
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		return MD5Util.getMD5String(cnt.getBytes());
	}

	public static String getMD5String(byte[] bytes) {
		MD5Util.messagedigest.update(bytes);
		return MD5Util.bufferToHex(MD5Util.messagedigest.digest());
	}
}
