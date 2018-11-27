package com.andr.tool.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import com.andr.tool.util.StringUtil;

import java.util.Properties;


public class NetUtil 
{

    private static final String PROPER_PROXY = "http.proxyHost";
    private static final String PROPER_PORT  = "http.proxyPort";
    private static final String HTTP_AGENT   = "http.agent";

    public static boolean isValidateUrl(String url)
    { 
        boolean rlt = false;
        
        if(StringUtil.isStringEmpty(url))
        {
            return rlt;
        }
        
        String regEx = "^(http|https|ftp)\\://([a-zA-Z0-9\\.\\-]+(\\:[a-zA-"
                + "Z0-9\\.&%\\$\\-]+)*@)?((25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{"   
                + "2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}"   
                + "[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|"   
                + "[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-"   
                + "4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])|([a-zA-Z0"   
                + "-9\\-]+\\.)*[a-zA-Z0-9\\-]+\\.[a-zA-Z]{2,4})(\\:[0-9]+)?(/"   
                + "[^/][a-zA-Z0-9\\.\\,\\?\\'\\\\/\\+&%\\$\\=~_\\-@]*)*$";
        
        return url.matches(regEx);
    }
    
    
    public static String getUserAgent()
    {
        return getProperty(HTTP_AGENT);
    }

    public static NetType getNetType(Context context)
    {
        NetType rlt = NetType.UNKOWN;

        if (NetUtil.isMobileData(context))
        {
            return NetType.MOBILE_DATA;
        } else if (NetUtil.isWifiData(context))
        {
            return NetType.WIFI;
        }

        return rlt;
    }

    public enum NetType {
        MOBILE_DATA(0), WIFI(1), UNKOWN(-1);

        private int type;

        private NetType(int type) {
            this.type = type;
        }

        public int type() {
            return type;
        }
    }

    public static String getMacAddress(Context context) {
        WifiManager wifiMgr = (WifiManager) context.getSystemService("wifi");

        return wifiMgr.getConnectionInfo().getMacAddress();
    }

    public static ProxyInfo getCurrentProxyInfo(Context context) {
        ProxyInfo info = null;

        if (null != context) {
            if (NetUtil.isWifiData(context)) {
                info = getProxyInfoWhenWifi();
            } else if (NetUtil.isMobileData(context)) {
                info = getgetProxyInfoWhenMobileData(context);
            }
        }

        return info;
    }

    public static boolean isNetConnected(Context context) {
        if (null != context) {
            NetworkInfo info = getNetWorkInfo(context);
            if (null != info && info.isConnected()) {
                if (NetworkInfo.State.CONNECTED == info.getState()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isWifiData(Context context) {
        if (null != context) {
            NetworkInfo info = getNetWorkInfo(context);
            if (null != info && info.isConnected()) {
                if (ConnectivityManager.TYPE_WIFI == info.getType()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isMobileData(Context context) {
        if (null != context) {
            NetworkInfo info = getNetWorkInfo(context);
            if (null != info && info.isConnected()) {
                if (ConnectivityManager.TYPE_MOBILE == info.getType()) {
                    return true;
                }
            }
        }
        return false;
    }

    private static NetworkInfo getNetWorkInfo(Context context){
        NetworkInfo rlt = null;

        if (null != context) 
        {
            try 
            {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (null != connectivityManager) 
                {
                    rlt = connectivityManager.getActiveNetworkInfo();
                }
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return rlt;
    }

    private static ProxyInfo getgetProxyInfoWhenMobileData(Context context) {
        ProxyInfo rlt = null;

        try {
            Apn apn = ApnUtil.getCurrentConnectApn(context);
            String proxyIp = ApnUtil.getProxyIp(apn);
            if (!StringUtil.isStringEmpty(proxyIp) && !StringUtil.isStringEmpty(apn.port)) {
                rlt = new ProxyInfo();
                rlt.proxyIp = proxyIp;
                rlt.proxyPort = Integer.parseInt(apn.port);
            }
        } catch (NumberFormatException e) {
            rlt.proxyPort = 80;
        }

        return rlt;
    }

    private static ProxyInfo getProxyInfoWhenWifi() {
        ProxyInfo rlt = null;

        try {
            String host = getProperty(NetUtil.PROPER_PROXY);
            String port = getProperty(NetUtil.PROPER_PORT);

            if (!StringUtil.isStringEmpty(host) && !StringUtil.isStringEmpty(port)) {
                rlt = new ProxyInfo();
                rlt.proxyIp = host;
                rlt.proxyPort = Integer.parseInt(port);
            }
        } catch (NumberFormatException e) {
            rlt.proxyPort = 80;
        }

        return rlt;
    }

    private static String getProperty(String name) {
        String rlt = null;

        if (!StringUtil.isStringEmpty(name)) {
            Properties p = System.getProperties();
            rlt = p.getProperty(name, StringUtil.UNKNOWN);
        }

        return rlt;
    }
}
