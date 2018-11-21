package com.andr.tool.apk;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.text.TextUtils;

import com.andr.tool.cmd.CmdUtils;
import com.andr.tool.encode.SecUtils;
import com.andr.tool.encode.StrCharset;
import com.andr.tool.file.FileUtil;
import com.andr.tool.log.LogUtil;
import com.andr.tool.phone.PhoneUtil;
import com.andr.tool.util.StringUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangxiaoming on 2018/9/18.
 */

public class ApkUtil implements ApkInterface {

    private static ApkUtil mIntence;


    public static ApkUtil getInstance() {
        if (null == mIntence) {
            synchronized (ApkUtil.class) {
                if (null == mIntence) {
                    mIntence = new ApkUtil();
                }
            }
        }
        return mIntence;
    }


    @Override
    public PackageInfo getPackageInfo(Context context, String packageName, int flag) {
        PackageInfo rlt = null;
        if (null == context || StringUtil.isStringEmpty(packageName)) {
            return rlt;
        }
        try {
            PackageManager pm = context.getPackageManager();
            if (pm != null) {
                rlt = pm.getPackageInfo(packageName, flag);
            }
        } catch (PackageManager.NameNotFoundException e) {
            LogUtil.e("找不到该包名");
        }

        return rlt;
    }


    @Override
    public Signature[] getSignature(Context context, String pakeName) {
        Signature[] rlt = null;

        PackageInfo packInfo = getPackageInfo(context, pakeName, PackageManager.GET_SIGNATURES);
        if (null != packInfo) {
            rlt = packInfo.signatures;
        }

        return rlt;
    }


    @Override
    public String getSignatureMd5(Context context, String pakeName) {
        String rlt = null;

        Signature[] signs = getSignature(context, pakeName);
        if (null != signs && signs.length > 0) {

            StringBuffer sb = new StringBuffer();
            for (Signature sig : signs) {
                sb.append(sig.toCharsString());
            }

            rlt = SecUtils.getInformationFingerprintByMD5(sb.toString(), StrCharset.UTF_8);
        }
        return StringUtil.isStringEmpty(rlt) ? null : rlt;
    }

    @Override
    public String getAppName(Context context) {
        if (null == context) {
            return null;
        }

        PackageManager packageManager = context.getPackageManager();
        PackageInfo pakeInfo = getPackageInfo(context, context.getPackageName(), PackageManager
                .GET_UNINSTALLED_PACKAGES);
        if (pakeInfo != null) {
            ApplicationInfo appInfo = pakeInfo.applicationInfo;
            return appInfo.loadLabel(packageManager)
                    .toString();
        }
        return null;
    }


    @Override
    public String getAppVersionName(Context context) {
        if (null == context) {
            return null;
        }

        PackageInfo packageInfo = getPackageInfo(context, context.getPackageName(), PackageManager
                .GET_UNINSTALLED_PACKAGES);
        return null == packageInfo ? null : packageInfo.versionName;
    }


    @Override
    public int getAppVersionCode(Context context, String pakeName) {
        if (null == context) {
            return 0;
        }

        PackageInfo packageInfo = getPackageInfo(context, pakeName, PackageManager
                .GET_UNINSTALLED_PACKAGES);
        return null == packageInfo ? 0 : packageInfo.versionCode;
    }

    @Override
    public boolean isApkExist(Context context, String packageName) {
        PackageInfo packageInfo = getPackageInfo(context, packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
        return packageInfo == null ? false : true;
    }


    @Override
    public String getMetaData(Context context, String key) {

        PackageInfo packageInfo = getPackageInfo(context, context.getPackageName(), PackageManager.GET_META_DATA);

        ApplicationInfo appInfo = packageInfo.applicationInfo;
        Bundle metaData = (null != appInfo) ? appInfo.metaData : null;

        return (null != metaData && !StringUtil.isStringEmpty(key)) ? String.valueOf(metaData.get(key)) : null;

    }

    @Override
    public boolean startApk(Context context, String pakeName) {
        boolean rlt = false;

        if (isApkExist(context, pakeName)) {
            PackageManager pkgMgr = context.getPackageManager();
            if (null != pkgMgr) {
                Intent intent = pkgMgr.getLaunchIntentForPackage(pakeName);
                if (null != intent) {
                    context.startActivity(intent);
                    rlt = true;
                }
            }
        }

        return rlt;
    }

    @Override
    public boolean startApkForAdb(String pakeName, String className) {
        if (pakeName.isEmpty() || className.isEmpty()) {
            return false;
        }
        String cmd = "am start " + pakeName + "/" + className;
        if (!PhoneUtil.getInstance().isRoot()) {
            LogUtil.e("手机没有root!!");
            return false;
        }
        int code = CmdUtils.execRootCmdSilent(cmd);
        if (code != -1) {
            return true;
        }
        return false;
    }

    @Override
    public boolean stopApk(Context context, String pakeName) {
        boolean rlt = false;
        // 应用存在,且关闭的不是自己
        if (isApkExist(context, pakeName) && !context.getPackageName().equalsIgnoreCase(pakeName)) {
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (null != manager) {
                manager.killBackgroundProcesses(pakeName);
                rlt = true;
            }
        }

        return rlt;
    }


    @Override
    public List<ApkInfo> getInstallApkInfo(Context context) {
        ArrayList<ApkInfo> appList = new ArrayList<>(); //用来存储获取的应用信息数据
        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);

        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            ApkInfo tmpInfo = new ApkInfo();
            tmpInfo.apkName = packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
            tmpInfo.pkgName = packageInfo.packageName;
            tmpInfo.apkVerName = packageInfo.versionName;
            tmpInfo.verCode = packageInfo.versionCode;
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                appList.add(tmpInfo);//如果非系统应用，则添加至appList
            }
        }
        return appList;
    }


    @Override
    public List<ActivityManager.RunningTaskInfo> getRuningTaskInfo(Context context) {
        List<ActivityManager.RunningTaskInfo> rlt = null;

        if (null == context) {
            return rlt;
        }

        ActivityManager aManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (null != aManager) {
            rlt = aManager.getRunningTasks(100);
        }

        return rlt;
    }

    @Override
    public boolean isAppRunningTopActivity(Context context, String activityName) {
        boolean rlt = false;

        if (null == context || TextUtils.isEmpty(activityName)) {
            return rlt;
        }

        List<ActivityManager.RunningTaskInfo> runningTasks = getRuningTaskInfo(context);
        if (null != runningTasks && !runningTasks.isEmpty()) {
            String topClsName = runningTasks.get(0).topActivity.getClassName();
            LogUtil.e("topClsName:" + topClsName);
            rlt = !TextUtils.isEmpty(topClsName) && topClsName.equals(activityName);
        }

        return rlt;
    }

    @Override
    public boolean isApkFile(Context context, String path) {
        boolean rlt = false;
        if (null == context || StringUtil.isStringEmpty(path)) {
            return rlt;
        }
        try {
            if (FileUtil.getInstance().isFileExist(path)) {
                PackageManager pm = context.getPackageManager();
                PackageInfo info = pm.getPackageArchiveInfo(path,
                        PackageManager.GET_ACTIVITIES);
                if (info != null) {
                    rlt = true;
                }
            }
        } catch (Exception e) {
            rlt = false;
        }
        return rlt;
    }

    @Override
    public String getFileForAppName(Context context, String path) {
        if (isApkFile(context, path)) {
            return context.getPackageManager().getPackageArchiveInfo(path,
                    PackageManager.GET_ACTIVITIES).packageName;
        }
        return null;
    }


    @Override
    public boolean installApk(String filePath, int tag) {
        if (!FileUtil.getInstance().isFileExist(filePath)) {
            LogUtil.e("文件不存在");
            return false;
        }
        if (tag == 0) {
            return installApkForData(filePath);
        } else {
            return installApkForSys(filePath);

        }
    }

    @Override
    public boolean installApkAndStart(String filePath, String pakeName, String className, int tag) {
        if (!FileUtil.getInstance().isFileExist(filePath)) {
            LogUtil.e("文件不存在");
            return false;
        }
        if (tag == 0) {
            return installApkForDataAndStart(filePath, pakeName, className);
        } else {
            return installApkForSys(filePath);

        }
    }

    private boolean installApkForData(String filePath) {
        String cmd = "pm install -r -t " + filePath;
        if (!PhoneUtil.getInstance().isRoot()) {
            LogUtil.e("手机没有root!!");
            return false;
        }
        String code = "";
        try {
            code = CmdUtils.executeShellCommand(cmd);
            LogUtil.d("adb安装输出-" + code);
            if ("Success".equals(code)) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean installApkForDataAndStart(String filePath, String pakeName, String className) {
        String cmd = "pm install -r -t " + filePath + ";am start " + pakeName + "/" + className;
        if (!PhoneUtil.getInstance().isRoot()) {
            LogUtil.e("手机没有root!!");
            return false;
        }
        int code = CmdUtils.execRootCmdSilent(cmd);
        if (code != -1) {
            return true;
        }
        return false;
    }


    private boolean installApkForSys(String filePath) {
        //判断版本
        String install_path = "";
        if (PhoneUtil.getInstance().getSysSdkVer() >= 19) {//4.4
            install_path = "/system/priv-app/";
        } else {
            install_path = "/system/app/";
        }

        //改变文件权限
        String ret_1 = "chmod 777 " + filePath;
        CmdUtils.execRootCmdSilent(ret_1);


        //挂载system 使其可写
        String ret = "mount -o remount,rw /dev/block/stl6 /system";
        CmdUtils.execRootCmdSilent(ret);

        //拷贝文件到相应的系统路径
        String[] fileNames = filePath.split(File.separator);
        String fileName = fileNames[fileNames.length - 1];

        String sysPath = install_path + fileName;

        String ret_2 = "cp " + filePath + " " + sysPath;

        CmdUtils.execRootCmdSilent(ret_2);

        //修改文件权限644
        CmdUtils.execRootCmdSilent("chmod 644 " + sysPath);

        //恢复system 挂载
        CmdUtils.execRootCmdSilent("mount -o remount,ro /dev/block/stl6 /system");


        //判断系统sdk是否大于21, 大于则需要重启
        if (PhoneUtil.getInstance().getSysSdkVer() >= 21) {
            CmdUtils.execRootCmdSilent("reboot");
        }


        try {
            Thread.sleep(2000);  //休息两秒

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (FileUtil.getInstance().isFileExist(sysPath)) {
            CmdUtils.execRootCmdSilent("rm -r " + filePath);
            return true;
        } else {
            return false;
        }

    }


    @Override
    public boolean unInstallApk(String pakeName) {
        String cmd = "pm uninstall " + pakeName;
        if (!PhoneUtil.getInstance().isRoot()) {
            LogUtil.e("手机没有root!!");
            return false;
        }

        //首先执行卸载
        int code = CmdUtils.execRootCmdSilent(cmd);
        if (code != -1) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isSystemApp(Context context, String pakeName) {
        PackageInfo pi = getPackageInfo(context, pakeName, 0);
        if ((pi.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0) {
             return true;
        }
        return false;
    }

    @Override
    public String getAppStartActivity(Context context, String pakeName) {
        if (pakeName.isEmpty()) {
            return "";
        }

        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(pakeName);
        if (intent != null) {
            ComponentName componentName = intent.getComponent();
            String name = componentName.getClassName();
            return name;
        }
        return "";


    }
}
