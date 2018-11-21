package com.andr.tool.util;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Created by zhangxiaoming on 2018/9/18.
 */

public class PermissionsUtil {

    /**
     * 判断是否有读取sim卡的权限
     *
     * @param context 上下文
     * @return boolean
     */
    public static synchronized boolean hasReadPhonePermission(Context context, String[] permission) {
        return hasPermissions(context, permission);
    }
    /**
     * 判断应用是否含有权限
     *
     * @param context
     * @param permissions
     * @return
     */
    public static synchronized boolean hasPermissions(Context context, String[] permissions) {

        if (null != context && null != permissions && permissions.length > 0) {
            PackageManager pm = context.getPackageManager();
            if (null != pm) {
                for (int i = 0; i < permissions.length; i++) {
                    if (PackageManager.PERMISSION_DENIED == pm.checkPermission(permissions[i], context.getPackageName
                            ())) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
