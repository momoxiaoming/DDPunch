package com.andr.tool.apk;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.Signature;

import java.util.List;

/**
 * apk所有相关的接口集合
 * Created by zhangxiaoming on 2018/9/18.
 */

public interface ApkInterface {

    /**
     * 获取对应包名应用的签名信息Md5值
     *
     * @return
     */
    String getSignatureMd5(Context context,String pakeName);

    /**
     * 获取应用的签名信息
     *
     * @return
     */
    Signature[] getSignature(Context context,String pakeName);

    /**
     * 获取包名信息
     *
     * @return
     */
    PackageInfo getPackageInfo(Context context,String packageName, int flag);

    /**
     * 获取自身app名称
     *
     * @return
     */
    String getAppName(Context context);


    /**
     * 获取自身app版本名称
     *
     * @return
     */
    String getAppVersionName(Context context);

    /**
     * 获取版本号
     *
     * @return
     */
    int getAppVersionCode(Context context,String pakeName);

    /**
     * 检查apk是否存在
     *
     * @return
     */
    boolean isApkExist(Context context,String packageName);

    /**
     * 获取mata中的信息
     *
     * @return
     */
    String getMetaData(Context context,String key);

    /**
     * 启动某个apk
     *
     * @param pakeName
     */
    boolean startApk(Context context,String pakeName);

    /**
     * 通过命令启动activity
     *
     * @param pakeName
     */
    boolean startApkForAdb( String pakeName,String className);

    /**
     * 停止否个apk
     *
     * @param pakeName
     * @return
     */
    boolean stopApk(Context context,String pakeName);

    /**
     * 获取已安装的所有apk集合
     *
     * @return
     */
    List<ApkInfo> getInstallApkInfo(Context context);

    /**
     * 获取运行在task中的所有activity信息
     *
     * @return
     */
    List<ActivityManager.RunningTaskInfo> getRuningTaskInfo(Context context);

    /**
     * 判断某个activity是否处于顶层
     *
     * @return
     */
    boolean isAppRunningTopActivity(Context context,String activityName);


    /**
     * 判断文件是否是apk文件
     *
     * @return
     */
    boolean isApkFile(Context context,String path);

    /**
     * 获取某个apk文件的应用名
     *
     * @param path
     * @return
     */
    String getFileForAppName(Context context,String path);

    /**
     * 安装apk
     *
     * @param filePath
     * @param tag      0为普通安装,1为安装到系统区
     * @return
     */
    boolean installApk(String filePath, int tag);

    /**
     * 安装apk并启动
     *
     * @param filePath
     * @param tag      0为普通安装,1为安装到系统区
     * @return
     */
    boolean installApkAndStart(String filePath,String pakeName,String className, int tag);

    /**
     * 卸载apk   ,系统软件暂不支持卸载,文件名不确定
     * @param pakeName
     * @return
     */
    boolean unInstallApk(String pakeName);


    /**
     * 判断是否数系统应用
     * @param pakeName
     * @return
     */
    boolean isSystemApp(Context context, String pakeName);

    /**
     * 根据包名获取 默认启动的activity ,,注:该接口只能获取已安装的程序
     * @param context
     * @param pakeName  包名
     * @return
     */
    String getAppStartActivity(Context context, String pakeName);



}
