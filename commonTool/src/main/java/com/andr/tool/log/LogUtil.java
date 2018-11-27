package com.andr.tool.log;

import android.util.Log;

import com.andr.tool.util.StringUtil;

public class LogUtil {

    //默认打开
    private static final boolean DEFULT_OPEN = true;

    private static final String tag = "tool";


    public static void d(String msg) {
        if (StringUtil.isValidate(tag) && StringUtil.isValidate(msg)) {
            Log.i(tag, msg);
        }
    }

    public static void i(String msg) {
        if (StringUtil.isValidate(tag) && StringUtil.isValidate(msg)) {
            Log.i(tag, msg);
        }
    }

    public static void e(String msg) {
        if (StringUtil.isValidate(tag) && StringUtil.isValidate(msg)) {
            Log.e(tag, msg);
        }
    }

}
