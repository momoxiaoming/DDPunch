package com.andr.tool.encode;


import com.andr.tool.util.StringUtil;

public class EnDecrypt
{
	public static String encode(String srcStr)
	{
		if (StringUtil.isStringEmpty(srcStr))
		{
			return "";
		}
		
		byte[] bytes = srcStr.getBytes();
		byte[] dealBytes = dealBytes(bytes,genKey(bytes));
		
		return new String(Base64Util.encode(dealBytes)).toString();
	}
	
	public static String decode(String srcStr)
	{
		if (StringUtil.isStringEmpty(srcStr))
		{
			return "";
		}
		
		byte[] bytes = Base64Util.decode(srcStr);
		byte[] dealBytes = dealBytes(bytes,genKey(bytes));
		
		return new String(dealBytes).toString();

	}
	
	private static String genKey(byte[] bytes)
	{
		try 
		{
			return String.valueOf(MD5Util.getMD5String(String.valueOf(bytes.length + 169).getBytes()).charAt(0));
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	private static byte[] dealBytes(byte[] input,String key)
	{
		if (null == input || input.length <= 0) {
			return null;
		}
		int len = input.length;
		for (int i = 0; i < len; i++) {
			input[i] = (byte) (input[i] ^ getKey(key,i));
		}
		return input;
	}

	private static int getKey(String key, int index) {
		if (null == key || key.length() == 0) {
			return 0;
		}
		int len = key.length();
		if (len > index) {
			return key.codePointAt(index);
		} else {
			return key.codePointAt(index % len);
		}
	}
}