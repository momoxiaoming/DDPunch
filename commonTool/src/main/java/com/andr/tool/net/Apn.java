package com.andr.tool.net;


import com.andr.tool.util.StringUtil;

public class Apn
{
    public int    id       = -1;
    public String name     = StringUtil.UNKNOWN;
    public String numeric  = StringUtil.UNKNOWN;
    public String mcc      = StringUtil.UNKNOWN;
    public String mnc      = StringUtil.UNKNOWN;
    public String apn      = StringUtil.UNKNOWN;
    public String user     = StringUtil.UNKNOWN;
    public String server   = StringUtil.UNKNOWN;
    public String password = StringUtil.UNKNOWN;
    public String proxy    = StringUtil.UNKNOWN;
    public String port     = StringUtil.UNKNOWN;
    public String mmsproxy = StringUtil.UNKNOWN;
    public String mmsport  = StringUtil.UNKNOWN;
    public String mmsc     = StringUtil.UNKNOWN;
    public String type     = StringUtil.UNKNOWN;
    public int    current  = -1;

    public Apn() {
    }

    public Apn(int id, String name, String numeric, String mcc, String mnc, String apn, String user, String server, String password, String proxy, String port,
               String mmsproxy, String mmsport, String mmsc, String type, int current) {
        super();
        this.id = id;
        this.name = name;
        this.numeric = numeric;
        this.mcc = mcc;
        this.mnc = mnc;
        this.apn = apn;
        this.user = user;
        this.server = server;
        this.password = password;
        this.proxy = proxy;
        this.port = port;
        this.mmsproxy = mmsproxy;
        this.mmsport = mmsport;
        this.mmsc = mmsc;
        this.type = type;
        this.current = current;
    }

    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder("Apn [apn = ").append(apn);
        strBuilder.append(", id = ").append(id);
        strBuilder.append(", type = ").append(type);
        strBuilder.append(", current=").append(current);
        strBuilder.append(", mcc=").append(mcc);
        strBuilder.append(", mmsc = ").append(mmsc);
        strBuilder.append(", mmsport = ").append(mmsport);
        strBuilder.append(", mmsproxy = ").append(mmsproxy);
        strBuilder.append(", mnc = ").append(mnc);
        strBuilder.append(", name = ").append(name);
        strBuilder.append(", numeric = ").append(numeric);
        strBuilder.append(", password = ").append(password);
        strBuilder.append(", port = ").append(port);
        strBuilder.append(", proxy = ").append(proxy);
        strBuilder.append(", server = ").append(server);
        strBuilder.append(", user = ").append(user);
        strBuilder.append("]");

        return super.toString() + "\n" + strBuilder.toString();
    }

}
