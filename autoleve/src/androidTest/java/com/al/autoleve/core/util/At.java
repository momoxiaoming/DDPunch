package com.al.autoleve.core.util;

import android.content.Context;

import com.al.autoleve.core.automator.exception.UIException;
import com.al.autoleve.core.automator.ext.LuaUtils;
import com.al.autoleve.core.data.UiDataCenter;
import com.andr.tool.apk.ApkUtil;
import com.andr.tool.file.FileUtil;
import com.andr.tool.log.LogUtil;
import com.andr.tool.phone.PhoneUtil;

import org.keplerproject.luajava.LuaState;
import org.keplerproject.luajava.LuaStateFactory;

/**
 * 这里集合和应用用到的所有工具类的方法
 * Created by zhangxiaoming on 2018/9/17.
 */

public class At {

    public static String getExStoragePath() {
        return FileUtil.getInstance().getExStoragePath();
    }

    public static boolean makeDir(String path) {
        return FileUtil.getInstance().makeDirs(path);
    }

    public static void deleteDirContent(String path) {
        FileUtil.getInstance().deleteDirContent(path);
    }
    public static void deleteDir(String path) {
        FileUtil.getInstance().deleteDir(path);
    }

    public static void deleteFile(String path) {
        FileUtil.getInstance().deleteFile(path);
    }

    public static void copyAssetsToFile(Context context, String fileName, String path) {
        FileUtil.getInstance().copyAssetsToPath(context, fileName, path);
    }

    public static boolean unZip(String zipPath, String fileDir) {
        return FileUtil.getInstance().unZipFiles(zipPath, fileDir);
    }

    public static int getSdk() {
        return PhoneUtil.getInstance().getSysSdkVer();
    }

    public static String getImei(Context context) {
        return PhoneUtil.getInstance().getImei(context);
    }


    public static String getAndId(Context context) {
        return PhoneUtil.getInstance().getAndId(context);
    }

    public static String getDeviceName() {
        return PhoneUtil.getInstance().getDeviceName();
    }

    public static boolean isRoot() {
        return PhoneUtil.getInstance().isRoot();
    }

    public static int getAppVersionCode(Context context, String pakeName) {
        return ApkUtil.getInstance().getAppVersionCode(context, pakeName);
    }

    public static boolean doLuaFile(String luaPath) {
        //开始工作
        LuaState l = LuaStateFactory.newLuaState();
        try {
            LuaUtils.setLuaState(UiDataCenter.getInstance().getTestApkContext(), UiDataCenter.getInstance().getDevice
                    (), l, AppConfig.ATUO_MAIN_FILE_WORK_PATH);
            boolean flg = LuaUtils.exeLuaFile(l, luaPath, AppConfig.ATUO_MAIN_FILE_EXCEPT_ALL_PATH);
            LogUtil.d(flg ? "脚本执行成功:" : "脚本执行失败:" + luaPath);

            return flg;
        } catch (UIException e) {
            LogUtil.d(e.getDescription());
            return false;
        }
    }

    public static String getToken() {
        String token = null;
        token = FileUtil.getInstance().readFile(com.al.autoleve.AppConfig.TOKEN_FILE_PATH);
        return token;
    }
}
