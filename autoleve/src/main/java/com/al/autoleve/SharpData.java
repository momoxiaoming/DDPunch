package com.al.autoleve;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by zhangxiaoming on 2018/11/26.
 */

public class SharpData {
    private static final String Heightmetrics = "Heightmetrics";

    private static final String widthmetrics = "widthmetrics";

    private static final String spName = "auto_sharp";


    private static final String NOTRLT = "notrlt";


    //获取屏幕高度
    public static int getHeightmetrics(Context context) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        int order = sp.getInt(Heightmetrics, AppConfig.heightmetrics_defult);
        return order;
    }


    public static boolean setHeightmetrics(Context context, int height) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        return sp.edit().putInt(Heightmetrics, height).commit();
    }

    //获取屏幕宽度
    public static int getWidthmetrics(Context context) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        int order = sp.getInt(widthmetrics, AppConfig.widthmetrics_defult);
        return order;
    }


    public static boolean setWidthmetrics(Context context, int width) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        return sp.edit().putInt(widthmetrics, width).commit();
    }

    //获取通知结果 0是失败,1是成功
    public static int getNotRlt(Context context) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        int order = sp.getInt(NOTRLT, 0);
        return order;
    }

    //设置通知结果
    public static boolean setNotRlt(Context context, int rlt) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        return sp.edit().putInt(NOTRLT, rlt).commit();
    }
}
