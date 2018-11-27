package com.andr.tool.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import java.io.UnsupportedEncodingException;

/**
 * @ClassName: StringsUtil
 * @Model : (所属模块名称)
 * @Description: (这里用一句话描述这个类的作用)
 * @author LuoZhong
 * @date 2016年6月2日 上午11:48:49
 */
public class StringUtil {

    public static final String UNKNOWN = "uk";

    /**
     * getRealStr (检查输入字符是否符合最大长度，如果超大最大长度，截取前面到最大长度的字串)
     * 
     * @param str
     * @param maxLength
     * @return
     *         String
     * @ModifiedPerson ZhangShuo
     * @date 2016年7月19日 下午2:00:47
     */
    public static String getRealStr(String str, int maxLength) {
        String rlt = StringUtil.UNKNOWN;
        if (null != str && maxLength > 0) {
            if (str.length() < maxLength) {
                rlt = str;
            } else {
                rlt = str.substring(0, maxLength);
            }
        }
        return rlt;
    }

    /**
     * @Title: isStringEmpty
     * @Description: 判断字符串是否为空
     * @param strObj
     *            字符串
     * @return boolean true:表示字串为null或者字串为空串,false表示字串不为空
     * @ModifiedPerson LuoZhong
     * @date 2016年6月2日 上午11:44:41
     */
    public static boolean isStringEmpty(String strObj) {
        return (null == strObj || strObj.trim().length() == 0);
    }

    /**
     * @Title: isValidate
     * @Description: 判断字符串是否有效
     * @param strObj
     *            字符串
     * @return boolean true:表示字串有效,false表示字串为空或者未知
     * @ModifiedPerson LuoZhong
     * @date 2016年6月2日 上午11:44:41
     */
    public static boolean isValidate(String strObj) {
        return (!StringUtil.isStringEmpty(strObj) && !UNKNOWN.equalsIgnoreCase(strObj.trim()));
    }

    public static boolean isValidates(String... strObjs)
    {
        if(null != strObjs && strObjs.length > 0)
        {
            for(int i = 0; i < strObjs.length;i++)
            {
                if(StringUtil.isStringEmpty(strObjs[i]))
                {
                    return false;
                }
            }
        }
        return true;
    }

    public static String setStringIfEmpty(String strObj) {
        return StringUtil.isStringEmpty(strObj) ? StringUtil.UNKNOWN : strObj;
    }

    public static String setStringIfEmpty(String strObj, String defaultValue) {
        return StringUtil.isStringEmpty(strObj) ? defaultValue : strObj;
    }

    /**
     * 
     * @Title: getUTF8Bytes
     * @Description: 将字串转为utf_8编码的字节流
     * @param str
     *            字串
     * @return byte[] 字节流
     * @ModifiedPerson LuoZhong
     * @date 2016年6月2日 上午11:47:50
     */
    public static byte[] getUTF8Bytes(String str) {
        return getBytes(str, "UTF-8");
    }

    public static String getString(byte[] bytes) {
        String rlt = StringUtil.UNKNOWN;
        if (null == bytes || bytes.length == 0) {
            return StringUtil.UNKNOWN;
        }

        try {
            rlt = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return rlt;
    }

    /**
     * @Title: getBytes
     * @Description: 将字串转为指定编码格式的字节流
     * @param str
     *            字串
     * @param charset
     *            编码格式
     * @return byte[] 字节流
     * @ModifiedPerson LuoZhong
     * @date 2016年6月2日 上午11:48:51
     */
    public static byte[] getBytes(String str, String charset) {
        byte[] rlt = new byte[0];

        if (StringUtil.isStringEmpty(str) || StringUtil.isStringEmpty(charset)) {
            return rlt;
        }

        try {
            rlt = str.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return rlt;
    }
    
    public static String[] getUrl(String[] url_dee, String url_sec) {
        String[] url = new String[url_dee.length];
        if(null != url_dee && url_dee.length > 0 && isValidate(url_sec)) {
            for(int i = 0; i < url_dee.length; i++) {
                url[i] = url_dee[i] + url_sec;
            }
            return url;
        }
        return null;
    }
    
    public static String getMetaData(Context context, String name) {
        String rlt = null;
        PackageManager pm = context.getPackageManager();
        try
        {
            ApplicationInfo appInfo = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo != null) {
                Bundle metaData = appInfo.metaData;
                if (metaData != null) {
                    Object obj = metaData.get(name);
                    if (null != obj) rlt = obj.toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (isStringEmpty(rlt) ? UNKNOWN : rlt);
    }
    
    public static String cutStr(String targetStr, String positionStr) {
        String rltStr = targetStr == null || !targetStr.contains(positionStr) ? null : targetStr.substring(targetStr.indexOf(positionStr) + positionStr.length()).trim();
        return rltStr;
    }
    
}
