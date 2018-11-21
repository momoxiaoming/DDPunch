package com.andr.tool.apk;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

public class ApkInfo
{
    public String pkgName;
    public int verCode;
    public String apkName;
    public String apkVerName;

    public ApkInfo()
    {

    }

    public ApkInfo(Context context)
    {
        this.pkgName = context.getPackageName();
        this.verCode = ApkUtil.getInstance().getAppVersionCode(context, pkgName);
        this.apkName = ApkUtil.getInstance().getAppName(context);
        this.apkVerName = ApkUtil.getInstance().getAppVersionName(context);
    }

    public JSONObject toJson()
    {
        JSONObject json = new JSONObject();
        try
        {
            json.put("pkgName", pkgName);
            json.put("apkName", apkName);
            json.put("verName", apkVerName);
            json.put("verCode", verCode);
        } catch (JSONException e)
        {
            return null;
        }
        return json;
    }

    @Override
    public String toString()
    {
        return "ApkInfo{" +
                "pkgName='" + pkgName + '\'' +
                ", verCode=" + verCode +
                ", apkName='" + apkName + '\'' +
                ", apkVerName='" + apkVerName + '\'' +
                '}';
    }
}