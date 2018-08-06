package com.al.ddpunch;

/**
 * Created by zhangxiaoming on 2018/7/25.
 */

public class Comm {

    //间隔检查时间
    public static final int  JugeTime=30*1000;



    //发送方邮箱
    public static final String EmailInfo="245545357@qq.com";


    //上班打卡时间段,一般20分钟间隔即可,注意格式
    public static final  String[] upJobTime=new String[]{"08:40","09:00"};

    //下班打卡时间段,注意格式
    public static final  String[] downJobTime=new String[]{"19:00","19:20"};

    //钉钉包名
    public static final String dingding_PakeName="com.alibaba.android.rimet";

    //手机启动页
    public static final String launcher_PakeName="com.android.launcher3";
}
