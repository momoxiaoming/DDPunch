package com.al.autoleve.core.data;

import android.content.Context;
import android.support.test.uiautomator.UiDevice;

/**
 * 数据管理类
 * Created by zhangxiaoming on 2018/9/14.
 */

public class UiDataCenter {

    //数据中心单例类
    private static UiDataCenter dataControlInstance;

    public static UiDataCenter getInstance() {
        if (null == dataControlInstance) {
            synchronized (UiDataCenter.class) {
                if (null == dataControlInstance) {
                    dataControlInstance = new UiDataCenter();
                }
            }
        }
        return dataControlInstance;
    }

    //测试apk上下文
    private Context mTestApkContext;

    public void setTestApkContext(Context mTestApkContext) {
        this.mTestApkContext = mTestApkContext;
    }

    public Context getTestApkContext() {
        return mTestApkContext;
    }

    //主apk上下文
    private Context mMainApkContext;

    public Context getMainApkContext() {
        return mMainApkContext;
    }

    public void setMainApkContext(Context mMainApkContext) {
        this.mMainApkContext = mMainApkContext;
    }

    //uiautomator点击提供接口类
    private UiDevice mDevice;

    public void setUiDevice(UiDevice device) {
        mDevice = device;
    }

    public UiDevice getDevice() {
        return mDevice;
    }


    public boolean taskResult; //任务结果
    public String taskDesc; //任务详情描述

    //清除任务数据,每次任务开始时都应调用该方法
    public void cleanTaskState(){
        taskResult=false;
        taskDesc="无";
    }

    public boolean isTaskResult() {
        return taskResult;
    }
    public void setTaskResult(boolean taskResult) {
        this.taskResult = taskResult;
    }
    public String getTaskDesc() {
        return taskDesc;
    }
    public void setTaskDesc(String taskDesc) {
        this.taskDesc = taskDesc;
    }

    public int pushType;  //打卡类型
    public int getPushType() {
        return pushType;
    }
    public void setPushType(int pushType) {
        this.pushType = pushType;
    }

    public boolean loginResult;  //登录结果
    public boolean isLoginResult() {
        return loginResult;
    }
    public void setLoginResult(boolean loginResult) {
        this.loginResult = loginResult;
    }

    public String ddAccount; //登录账号
    public String ddpwd;   //登录密码

    //清除登陆数据,每次执行登录都应调用该方法
    public void cleanAccAndPwd(){
        ddAccount="";
        ddpwd="";
    }

    public String getDdAccount() {
        return ddAccount;
    }
    public void setDdAccount(String ddAccount) {
        this.ddAccount = ddAccount;
    }
    public String getDdpwd() {
        return ddpwd;
    }
    public void setDdpwd(String ddpwd) {
        this.ddpwd = ddpwd;
    }
}
