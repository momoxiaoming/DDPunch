package com.al.autoleve.core.util;

import android.content.Context;

import com.al.autoleve.SharpData;
import com.al.autoleve.core.automator.exception.UIException;
import com.al.autoleve.core.automator.ext.LuaUtils;
import com.al.autoleve.core.data.UiDataCenter;
import com.andr.tool.apk.ApkUtil;
import com.andr.tool.cmd.CmdUtils;
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

    /**
     * 执行屏幕点击打卡
     *
     * @param order 1为上班打卡,2为下班打卡
     */
    public static boolean doSignClick(int order) {

        int heightPixels = SharpData.getHeightmetrics(UiDataCenter.getInstance().getMainApkContext());
        int widthPixels = SharpData.getWidthmetrics(UiDataCenter.getInstance().getMainApkContext());

        double t_x;
        double t_y;

        double b_x;
        double b_y;

        if (widthPixels == com.al.autoleve.AppConfig.widthmetrics_defult) {
            t_x = 240;
            b_x = 262;
        } else {
            t_x = ((float) 240 / com.al.autoleve.AppConfig.widthmetrics_defult) * widthPixels;
            b_x = ((float) 262 / com.al.autoleve.AppConfig.widthmetrics_defult) * widthPixels;
        }

        if (heightPixels == com.al.autoleve.AppConfig.heightmetrics_defult) {
            t_y = 314;
            b_y = 556;
        } else {
            t_y = ((float) 314 / com.al.autoleve.AppConfig.heightmetrics_defult) * heightPixels;
            b_y = ((float) 556 / com.al.autoleve.AppConfig.heightmetrics_defult) * heightPixels;
        }


        int t_x_i = (int) t_x;
        int t_y_i = (int) t_y;
        int b_x_i = (int) b_x;
        int b_y_i = (int) b_y;


        if (order == 1) {
            //上班打卡
            return ClickXy(t_x_i + "", t_y_i + "");
        } else {
            //下班卡
            return ClickXy(b_x_i + "", b_y_i + "");
        }



    }

    //模拟点击坐标
    public static boolean ClickXy(String x, String y) {
        LogUtil.d("点击的坐标:x"+x+"  y:"+y);
        String cmd = "input tap " + x + " " + y;

        try {


            return CmdUtils.execRootCmdSilent(cmd) == 1 ? true : false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
