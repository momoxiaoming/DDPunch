package com.andr.tool.phone;

import android.content.Context;

/**
 * Created by zhangxiaoming on 2018/9/18.
 */

public interface PhoneInterface {


    /*-------------------手机信息相关--------------------*/

    /**
     * 获取获取android id
     *
     * @return
     */
    String getAndId(Context context);

    /**
     * 获取手机imei
     *
     * @return 错误时返回为空
     */
    String getImei(Context context);

    /**
     * 获取imei
     *
     * @param context
     * @return
     */
    String getImsi(Context context);

    /**
     * 获取设备型号
     *
     * @return
     */
    String getDeviceName();

    /**
     * 获取GSM Ril 实现版本
     *
     * @return
     */
    String getGsmVersionRilImpl();


    /**
     * 获取系统属性
     *
     * @return
     */
    String getSystemPropertie(String propertie);

    /**
     * 获取手机硬件信息
     *
     * @return
     */
    String getHardwareName();

    /**
     * 获取系统sdk版本
     *
     * @return
     */
    int getSysSdkVer();

    /**
     * 获取放手机分辨率
     *
     * @return 1920_880
     */
    String getScreenSize(Context context);

    /**
     * 获取手机序列号
     *
     * @return
     */
    String getDevicesSerialNumber();

    /**
     * 获取全球运营商MCC+MNC
     *
     * @return
     */
    String getNetWorkOperator(Context context);

    /**
     * 获取iccid
     *
     * @return
     */
    String getIccid(Context context);

    /**
     * 判断手机是否是mtk平台
     *
     * @return
     */
    boolean isMtkPlatform(Context context);


    /**
     * 判断手机是否root
     *
     * @return
     */
    boolean isRoot();

    /**
     * 获取sd 卡大小
     * @return
     */
    String getSDTotalSize(Context context);

    /**
     * 获取sd 卡剩余容量
     * @return
     */
    long getSDAvailableSize(Context context);

    /**
     * 判断是否处于桌面
     * @param context
     * @return
     */
    boolean isHome(Context context);
}
