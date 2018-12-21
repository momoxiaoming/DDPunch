package com.al.autoleve.core.util;

import java.io.File;

import static com.al.autoleve.AppConfig.MAIN_URL;

/**
 *
 * 各种配置常亮
 * Created by zhangxiaoming on 2018/9/17.
 */

public class AppConfig {
    public static final String DOCOMPENT_NAME= "深圳市火星网络有限公司";// 要打卡的公司名


    public static final String AUTO_LUA_EXCEPT_NAME= "exception.lua";// 异常处理的lua文件名
    public static final String AUTO_LUA_WORK_NAME= "test.lua";// 工作lua文件名
    public static final String AUTO_LUA_LOGIN_NAME= "auto_login_signIn.lua";// 设备登录
    public static final String AUTO_LUA_SIGN_NAME= "auto_signIn.lua";// 打卡文件



//    public static final String AUTO_LUA_UP_SIGNIN_NAME= "auto_up_signIn.lua";// 上班签到文件
//    public static final String AUTO_LUA_DOWN_SIGNIN_NAME= "auto_down_signIn.lua";// 下班签到文件
//    public static final String AUTO_LUA_DOWN_ADV_SIGNIN_NAME= "auto_down_adv_signIn.lua";// 下版本早退签到文件
//    public static final String AUTO_LUA_DOWN_UPDATE_SIGNIN_NAME= "auto_down_update_signIn.lua";// 更新下班卡执行文件



    public static final String AUTO_MAIN_MK_NAME = "ddAuto";// 工作主目录名称

    public static final String AUTO_EXCEPT_MK_NAME = "except";//异常目录
    public static final String AUTO_WORK_MK_NAME = "work";//lua 工作目录
    public static final String AUTO_OTHER_MK_NAME = "other";//其他


    public static final String AUTO_EXCEPT_FILE_NAME= "exception.lua";// 异常lua文件
    public static final String AUTO_WOEK_FILE_NAME= "work.zip";// 工作的压缩文件



    // 工作主路径名称
    public static final String ATUO_MAIN_FILE_PATH = At.getExStoragePath()+ File.separator+AUTO_MAIN_MK_NAME;
    // 异常目录路径
    public static final String ATUO_MAIN_FILE_EXCEPT_PATH =ATUO_MAIN_FILE_PATH+File.separator+AUTO_EXCEPT_MK_NAME;
    //工作目录路径
    public static final String ATUO_MAIN_FILE_WORK_PATH =ATUO_MAIN_FILE_PATH+File.separator+AUTO_WORK_MK_NAME;
    //其他目录路径
    public static final String ATUO_MAIN_FILE_OTHER_PATH =ATUO_MAIN_FILE_PATH+File.separator+AUTO_OTHER_MK_NAME;



    //工作的异常lua文件路径
    public static final String ATUO_MAIN_FILE_EXCEPT_FILE_PATH =ATUO_MAIN_FILE_EXCEPT_PATH+File.separator+AUTO_EXCEPT_FILE_NAME;
    // 工作的lua压缩包路径
    public static final String ATUO_MAIN_FILE_WORK_FILE_PATH =ATUO_MAIN_FILE_WORK_PATH+File.separator+AUTO_WOEK_FILE_NAME;


    public static final String ATUO_MAIN_FILE_EXCEPT_ALL_PATH =ATUO_MAIN_FILE_EXCEPT_PATH+File.separator+AUTO_LUA_EXCEPT_NAME;

    public static final String ATUO_MAIN_FILE_WOEK_ALL_PATH =ATUO_MAIN_FILE_WORK_PATH+File.separator+AUTO_LUA_WORK_NAME;

    //设备登录
    public static final String ATUO_MAIN_FILE_LOGIN_ACCOUNT_PATH =ATUO_MAIN_FILE_WORK_PATH+File.separator+AUTO_LUA_LOGIN_NAME;
    //上班签到
    public static final String ATUO_MAIN_FILE_SIGNIN_ALL_PATH =ATUO_MAIN_FILE_WORK_PATH+File.separator+AUTO_LUA_SIGN_NAME;

//    public static final String ATUO_MAIN_FILE_UP_SIGNIN_ALL_PATH =ATUO_MAIN_FILE_WORK_PATH+File.separator+AUTO_LUA_UP_SIGNIN_NAME;
//    //下班签到
//    public static final String ATUO_MAIN_FILE_DOWN_SIGNIN_ALL_PATH =ATUO_MAIN_FILE_WORK_PATH+File.separator+AUTO_LUA_DOWN_SIGNIN_NAME;
//    //下班早退
//    public static final String ATUO_MAIN_FILE_DOWN_ADV_SIGNIN_ALL_PATH =ATUO_MAIN_FILE_WORK_PATH+File.separator+AUTO_LUA_DOWN_ADV_SIGNIN_NAME;
//    //更新下班卡
//    public static final String ATUO_MAIN_FILE_DOWN_UPDATE_SIGNIN_ALL_PATH =ATUO_MAIN_FILE_WORK_PATH+File.separator+AUTO_LUA_DOWN_UPDATE_SIGNIN_NAME;


    //拉取任务的地址
    public static final String REQ_TASK_MAIN_URL=MAIN_URL+"taskQury/";

    //注册设备
//    public static final String REQ_REGIEST_URL=MAIN_URL+"regDev/";

    // 提交任务
    public static final String REQ_SUBMIT_SIGN_TASK_URL=MAIN_URL+"taskSubmt/";


}
