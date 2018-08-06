package com.al.ddpunch.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhangxiaoming on 2018/7/26.
 */

public class SharpData {
    private static final String spName = "spName";


    private static final String ORDER_TYPE = "order_type";

    private static final String Compent_TYPE = "compent_type";

    private static final String open_TYPE = "open_type";
    private static final String email_adress = "email_adress";

    private static final String open_job = "oepn_job";
    private static final String reset_sys = "reset_sys";
    private static final String reset_sys_day = "reset_sys_day";


    // 0默认什么不做,1为打上班卡,2为打下班卡
    //获取指令类型
    public static int getOrderType(Context context) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        int order = sp.getInt(ORDER_TYPE, 0);
        return order;
    }


    //存储
    public static void setOrderType(Context context, int order) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        sp.edit().putInt(ORDER_TYPE, order).commit();
    }


    //1,代表已打上班卡,2,代表已打下班卡,0代表还未打卡
    //获取指令类型
    public static int getIsCompent(Context context) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        int order = sp.getInt(Compent_TYPE, 0);
        return order;
    }


    //存储
    public static void setIsCompent(Context context, int order) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        sp.edit().putInt(Compent_TYPE, order).commit();
    }


    //0,开启工作,1代表关闭工作
    //获取指令类型
    public static int getOpenApp(Context context) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        int order = sp.getInt(open_TYPE, 0);
        return order;
    }


    //存储邮箱
    public static void setOpenApp(Context context, int order) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        sp.edit().putInt(open_TYPE, order).commit();
    }

    public static String getEmailData(Context context) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        String order = sp.getString(email_adress, "");
        return order;
    }


    //存储邮箱
    public static boolean setEmailData(Context context, String order) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        return sp.edit().putString(email_adress, order).commit();
    }


    //存储邮箱
    public static boolean setNotData(Context context, String order) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        return sp.edit().putString("notdata", order).commit();
    }


    //上班打卡开启状态,0,都关闭,1是上班,2是下班,3是上下班,
    public static int getOpenJob(Context context) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        int order = sp.getInt(open_job, 0);
        return order;
    }


    //存储邮箱
    public static boolean setOpenJob(Context context, int order) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        return sp.edit().putInt(open_job, order).commit();
    }


    //0表示当日未重启,1表示当日已重启
    public static boolean getResetSys(Context context) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        int order = sp.getInt(reset_sys, 0);
        String sys_dat = sp.getString(reset_sys_day, "");
        String s = new SimpleDateFormat("dd").format(new Date(System.currentTimeMillis()));

        LogUtil.D("当天日期-->"+s+"--存储日期-->"+sys_dat);
        if (s.equals(sys_dat)) {
            //判断为当天
            if (order == 0) {
                return false;
            } else {
                return true;
            }


        }
        return false;

    }


    //
    public static boolean setResetSys(Context context) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        String s = new SimpleDateFormat("dd").format(new Date(System.currentTimeMillis()));
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(reset_sys_day, s);
        editor.putInt(reset_sys, 1);

        return editor.commit();
    }


}
