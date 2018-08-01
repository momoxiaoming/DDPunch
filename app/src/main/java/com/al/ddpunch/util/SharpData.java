package com.al.ddpunch.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by zhangxiaoming on 2018/7/26.
 */

public class SharpData {
    private static final String spName="spName";


    private static final String ORDER_TYPE="order_type";

    private static final String Compent_TYPE="compent_type";

    private static final String open_TYPE="open_type";
    private static final String email_adress="email_adress";

    private static final String open_job="oepn_job";




    // 0默认什么不做,1为打上班卡,2为打下班卡
    //获取指令类型
    public static int getOrderType(Context context){
        SharedPreferences sp=context.getSharedPreferences(spName,Context.MODE_PRIVATE);
       int order= sp.getInt(ORDER_TYPE,0);
       return order;
    }


    //存储
    public static void setOrderType(Context context,int order){
        SharedPreferences sp=context.getSharedPreferences(spName,Context.MODE_PRIVATE);
        sp.edit().putInt(ORDER_TYPE,order).commit();
    }


    //1,代表已打上班卡,2,代表已打下班卡,0代表还未打卡
    //获取指令类型
    public static int getIsCompent(Context context){
        SharedPreferences sp=context.getSharedPreferences(spName,Context.MODE_PRIVATE);
        int order= sp.getInt(Compent_TYPE,0);
        return order;
    }


    //存储
    public static void setIsCompent(Context context,int order){
        SharedPreferences sp=context.getSharedPreferences(spName,Context.MODE_PRIVATE);
        sp.edit().putInt(Compent_TYPE,order).commit();
    }


    //0,开启工作,1代表关闭工作
    //获取指令类型
    public static int getOpenApp(Context context){
        SharedPreferences sp=context.getSharedPreferences(spName,Context.MODE_PRIVATE);
        int order= sp.getInt(open_TYPE,0);
        return order;
    }


    //存储邮箱
    public static void setOpenApp(Context context,int order){
        SharedPreferences sp=context.getSharedPreferences(spName,Context.MODE_PRIVATE);
        sp.edit().putInt(open_TYPE,order).commit();
    }
    public static String getEmailData(Context context){
        SharedPreferences sp=context.getSharedPreferences(spName,Context.MODE_PRIVATE);
        String order= sp.getString(email_adress,"");
        return order;
    }


    //存储邮箱
    public static boolean setEmailData(Context context,String order){
        SharedPreferences sp=context.getSharedPreferences(spName,Context.MODE_PRIVATE);
        return sp.edit().putString(email_adress,order).commit();
    }



    //存储邮箱
    public static boolean setNotData(Context context,String order){
        SharedPreferences sp=context.getSharedPreferences(spName,Context.MODE_PRIVATE);
        return sp.edit().putString("notdata",order).commit();
    }



    //上班打卡开启状态,0,都关闭,1是上班,2是下班,3是上下班,
    public static int getOpenJob(Context context){
        SharedPreferences sp=context.getSharedPreferences(spName,Context.MODE_PRIVATE);
        int order= sp.getInt(open_job,0);
        return order;
    }


    //存储邮箱
    public static boolean setOpenJob(Context context,int order){
        SharedPreferences sp=context.getSharedPreferences(spName,Context.MODE_PRIVATE);
        return sp.edit().putInt(open_job,order).commit();
    }


}
