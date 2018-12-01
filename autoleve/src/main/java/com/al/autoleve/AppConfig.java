package com.al.autoleve;

import com.andr.tool.file.FileUtil;

import java.io.File;

/**
 * Created by zhangxiaoming on 2018/11/1.
 */

public class AppConfig {

    public static final String RESTART_MAIN_LOGICAL_APK_CMD = "am instrument -w -r   -e debug false -e class " +
            "com.al.autoleve.StartApp#runTestApp com.al.autoleve.test/android.support.test.runner.AndroidJUnitRunner";

    public static final String MAINT_TEST_PAKE = "com.al.autoleve.test";

    public static final String dingding_PakeName="com.alibaba.android.rimet";

    public static final String BROCAST_ACTION = "com.al.autoleve.AutoBrocast.action";

    public static final String MAIN_URL="http://api.momoxiaoming.com:9102/ddPush_andr/";

    public static final String AUTO_MAIN_MK_NAME = FileUtil.getInstance().getExStoragePath()+File.separator+"ddAuto";// 工作主目录名称

    public static final String AUTO_OTHER_MK_NAME =AUTO_MAIN_MK_NAME+File.separator+ "other";//下载的文件存放位置

    public static final String REQ_REGIEST_URL = MAIN_URL + "regDev/";
    public static final String UPDATE_APK_URL = MAIN_URL + "updateApk/";


    public static final String TEST_APP_PAKENAME = "com.al.autoleve.test";

    //token保存路径
    public static final String TOKEN_FILE_PATH = FileUtil.getInstance().getExStoragePath() + File
            .separator + "autoLeve"+File.separator+"token";

    //默认的4G+手机宽高
    public static  int  widthmetrics_defult=480;
    public static  int  heightmetrics_defult=854;
}
