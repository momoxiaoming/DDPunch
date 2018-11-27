package com.al.autoleve.core.automator.util;

import android.util.Log;

import com.andr.tool.util.StringUtil;

public class LogUtil 
{

	//默认打开
	private static final boolean DEFULT_OPEN = true;

	private static final String DEFULT_TAG = "luaLog";


	public static void logic_i(String msg)
	{
		if (DEFULT_OPEN && StringUtil.isValidate(msg))
		{
			Log.i(DEFULT_TAG, msg);
		}
	}

	public static void logic_e(String msg)
	{
		if (DEFULT_OPEN && StringUtil.isValidate(msg) )
		{
			Log.e(DEFULT_TAG, msg);
		}
	}

	public static void update_i(String msg)
	{
		if(DEFULT_OPEN && StringUtil.isValidate(msg) )
		{
			Log.i(DEFULT_TAG, msg);
		}
	}

	public static void update_e(String msg)
	{
		if(DEFULT_OPEN && StringUtil.isValidate(msg) )
		{
			Log.e(DEFULT_TAG, msg);
		}
	}

	public static void common_i(String msg)
	{
		if(DEFULT_OPEN && StringUtil.isValidate(msg) )
		{
			Log.i(DEFULT_TAG,msg);
		}
	}

	public static void common_e(String msg)
	{
		if(DEFULT_OPEN && StringUtil.isValidate(msg) )
		{
			Log.e(DEFULT_TAG,msg);
		}
	}

	public static void i(String tag, String msg)
	{
		if(StringUtil.isValidate(tag) && StringUtil.isValidate(msg))
		{
			Log.i(tag, msg);
		}
	}

	public static void e(String tag, String msg)
	{
		if(StringUtil.isValidate(tag) && StringUtil.isValidate(msg))
		{
			Log.e(DEFULT_TAG, msg);
		}
	}

}
