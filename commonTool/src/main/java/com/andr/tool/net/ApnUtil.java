package com.andr.tool.net;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.andr.tool.util.StringUtil;


public class ApnUtil {

    private static final String NAME     = "name";
    private static final String NUMERIC  = "numeric";
    private static final String MCC      = "mcc";
    private static final String MNC      = "mnc";
    private static final String APN      = "apn";
    private static final String USER     = "user";
    private static final String SERVER   = "server";
    private static final String PASSWORD = "password";
    private static final String PROXY    = "proxy";
    private static final String PORT     = "port";
    private static final String MMSPROXY = "mmsproxy";
    private static final String MMSPORT  = "mmsport";
    private static final String MMSC     = "mmsc";
    private static final String TYPE     = "type";
    private static final String ID       = "_id";
    private static final String CURRENTS = "current";

    private static final String T1_C_P   = "content://telephony/carriers/preferapn";

    public static Apn getCurrentConnectApn(Context context) {
        try {
            return getApnByUri(context, Uri.parse(ApnUtil.T1_C_P));
        } catch (Exception e) {

        }

        return null;
    }

    public static String getProxyIp(Apn apn) {
        String ip = null;
        if (null != apn && StringUtil.isValidate(apn.proxy)) {
            ip = trim(apn.proxy);
        }
        return ip;
    }

    private static String trim(String ipstr) {
        String rlt = "";
        try {
            String[] ipseg = ipstr.trim().split("\\.");
            rlt = String.valueOf(Integer.parseInt(ipseg[0]));
            for (int i = 1; i < ipseg.length; i++) {
                rlt = rlt + "." + ipseg[i];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rlt;
    }

    private static Apn getApnByUri(Context context, Uri preferredApnUri) {
        Apn rlt = null;
        if (preferredApnUri != null) {
            try {
                Cursor cursor = context.getContentResolver().query(preferredApnUri, null, null, null, null);
                if (null != cursor) {
                    if (cursor.moveToFirst()) {
                        rlt = mkApn(cursor);
                    }
                    cursor.close();
                }
            } catch (Exception e) {
            }
        }
        return rlt;
    }

    private static Apn mkApn(Cursor cursor) {
        if (null != cursor) {
            int id = cursor.getInt(cursor.getColumnIndex(ID));
            String name = cursor.getString(cursor.getColumnIndex(NAME));
            String numeric = cursor.getString(cursor.getColumnIndex(NUMERIC));
            String mcc = cursor.getString(cursor.getColumnIndex(MCC));
            String mnc = cursor.getString(cursor.getColumnIndex(MNC));
            String apn = cursor.getString(cursor.getColumnIndex(APN));
            String user = cursor.getString(cursor.getColumnIndex(USER));
            String server = cursor.getString(cursor.getColumnIndex(SERVER));
            String password = cursor.getString(cursor.getColumnIndex(PASSWORD));
            String proxy = cursor.getString(cursor.getColumnIndex(PROXY));
            String port = cursor.getString(cursor.getColumnIndex(PORT));
            String mmsproxy = cursor.getString(cursor.getColumnIndex(MMSPROXY));
            String mmsport = cursor.getString(cursor.getColumnIndex(MMSPORT));
            String mmsc = cursor.getString(cursor.getColumnIndex(MMSC));
            String type = cursor.getString(cursor.getColumnIndex(TYPE));
            int current = cursor.getInt(cursor.getColumnIndex(CURRENTS));
            return new Apn(id, name, numeric, mcc, mnc, apn, user, server, password, proxy, port, mmsproxy, mmsport, mmsc, type, current);
        }
        return null;
    }
}
