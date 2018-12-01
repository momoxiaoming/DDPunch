package com.al.autoleve;

import android.content.Context;
import android.content.SharedPreferences;

import com.andr.tool.log.LogUtil;

/**
 * Created by zhangxiaoming on 2018/11/26.
 */

public class SharpData {
    private static final String Heightmetrics = "Heightmetrics";

    private static final String widthmetrics = "widthmetrics";

    private static final String spName = "auto_sharp";


    private static final String NOTRLT = "notrlt";

    private static final String active = "active";

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


    //根据心跳判断程序是否在运行--以相差10秒为基准
    public static boolean getActive(Context context) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        long order = sp.getLong(active, 0);
        if(order==0){
            return false;
        }
        order=System.currentTimeMillis()/1000-order;
        LogUtil.d("相差秒数:"+order);


        return order<10?true:false;
    }

    //心跳获取,存入一个时间--秒数
    public static boolean setActive(Context context, long rlt) {

        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        return sp.edit().putLong(active, rlt).commit();
    }
}
