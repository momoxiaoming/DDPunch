package com.al.autoleve.core.bean;

import android.content.Context;

import com.al.autoleve.core.util.At;
import com.google.gson.Gson;

/**
 * 设备信息
 * Created by zhangxiaoming on 2018/9/18.
 */

public class DevInfoBean {


    public String dev_andId; //android id

    public String dev_imei; //imei

    public String dev_isRt;  //1是root,0 未root

    public String dev_name;  //机型

    public String dev_sdk; //sdk版本

    public String app_ver; //app 版本


    public DevInfoBean(Context context) {

        this.dev_andId = At.getAndId(context);
        this.dev_sdk = At.getSdk() + "";
        this.dev_imei = At.getImei(context);
        this.dev_name = At.getDeviceName();
        this.dev_isRt = At.isRoot() ? "1" : "0";
        this.app_ver=At.getAppVersionCode(context,context.getPackageName())+"";
    }

    @Override
    public String toString() {
        return "DevInfoBean{" +
                "dev_andid='" + dev_andId + '\'' +
                ", dev_imei='" + dev_imei + '\'' +
                ", dev_isRt='" + dev_isRt + '\'' +
                ", dev_Name='" + dev_name + '\'' +
                ", dev_sdk='" + dev_sdk + '\'' +
                '}';
    }


    public String toJsonString() {

        String jsonStr = new Gson().toJson(this);
        return jsonStr;
    }
}
