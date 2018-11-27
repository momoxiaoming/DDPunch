package com.andr.tool.net;

public class ProxyInfo {

    public static final int MOBILE_DATA_PROXY_PORT = 80;

    public String apnName                = null;
    public String proxyIp                = null;
    public int              proxyPort              = MOBILE_DATA_PROXY_PORT;

    public ProxyInfo() {
    }

    public ProxyInfo(String apnName, String proxyip, int port) {
        this.apnName = apnName;
        this.proxyIp = proxyip;
        this.proxyPort = port;
    }
}
