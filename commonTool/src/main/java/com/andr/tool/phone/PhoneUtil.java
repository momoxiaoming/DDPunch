package com.andr.tool.phone;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.andr.tool.cmd.CmdUtils;
import com.andr.tool.util.PermissionsUtil;
import com.andr.tool.util.StringUtil;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangxiaoming on 2018/9/18.
 */

public class PhoneUtil implements PhoneInterface {

    private static final String TEL_MANAGER = "android.telephony.TelephonyManager";
    private static final String GET_SUB_IMEI = "getDeviceIdGemini";


    private static PhoneUtil mIntence;


    public static PhoneUtil getInstance() {
        if (null == mIntence) {
            synchronized (PhoneUtil.class) {
                if (null == mIntence) {
                    mIntence = new PhoneUtil();
                }
            }
        }
        return mIntence;
    }



    @SuppressLint("HardwareIds")
    @Override
    public String getAndId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    @SuppressLint("MissingPermission")
    @Override
    public String getImei(Context context) {
        String imei = null;

        if (PermissionsUtil.hasPermissions(context, new String[]{"android.permission.READ_PHONE_STATE"})) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null) {
                imei = tm.getDeviceId();
            }
        }
        return (StringUtil.isValidate(imei) ? imei : "");
    }

    @SuppressLint("MissingPermission")
    @Override
    public String getImsi(Context context) {
        String imsi = null;

        if (PermissionsUtil.hasPermissions(context, new String[]{"android.permission.READ_PHONE_STATE"})) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            if (tm != null)// && isSimReady(context))
            {
                imsi = tm.getSubscriberId();
            }
        }
        return (StringUtil.isValidate(imsi) ? imsi : "");
    }

    @Override
    public String getDeviceName() {
        return Build.MODEL;
    }

    @Override
    public String getGsmVersionRilImpl() {
        return getSystemPropertie("gsm.version.ril-impl");
    }


    @Override
    public String getSystemPropertie(String propertie) {
        String value = "unknown";

        value = System.getProperty(propertie);
        if (null == value) {

            try {
                Class<?> classType = Class.forName("android.os.SystemProperties");
                Method getMethod = classType.getDeclaredMethod("get", new Class<?>[]{String.class});

                value = (String) getMethod.invoke(classType, new Object[]{propertie});

                value = value == null ? "unknown" : value;

            } catch (Exception exp) {
            }
        }

        return value;
    }

    @Override
    public String getHardwareName() {
        return getSystemPropertie("ro.hardware");
    }


    @Override
    public int getSysSdkVer() {
        return Build.VERSION.SDK_INT;
    }


    @Override
    public String getScreenSize(Context context) {
        if (null != context) {
            WindowManager wndManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wndManager.getDefaultDisplay();
            if (null != display) {
                DisplayMetrics displayMetrics = new DisplayMetrics();
                display.getMetrics(displayMetrics);
                return displayMetrics.widthPixels + "_" + displayMetrics.heightPixels;
            }
        }
        return null;
    }


    @Override
    public String getDevicesSerialNumber() {
        return getSystemPropertie("ro.serialno");
    }


    @Override
    public String getNetWorkOperator(Context context) {
        String rlt = null;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (null != tm) {
            rlt = tm.getNetworkOperator();
        }
        return rlt;
    }

    @SuppressLint("MissingPermission")
    @Override
    public String getIccid(Context context) {
        String rlt = null;

        if (PermissionsUtil.hasPermissions(context, new String[]{"android.permission.READ_PHONE_STATE"})) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null) {
                rlt = tm.getSimSerialNumber();
            }
        }
        return (StringUtil.isValidate(rlt) ? rlt : "");
    }

    @Override
    public boolean isMtkPlatform(Context context) {
        boolean ret = false;
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Class<?> mLoadClass = Class.forName(TEL_MANAGER);
            Class<?>[] parameter = new Class[1];
            parameter[0] = int.class;
            Method getImei = mLoadClass.getMethod(GET_SUB_IMEI, parameter);

            Object[] obParameter = new Object[1];
            obParameter[0] = 0;
            getImei.invoke(telephonyManager, obParameter);
            ret = true;
        } catch (Exception e) {
            ret = false;
        }
        return ret;
    }


    @Override
    public boolean isRoot() {
        int ret = CmdUtils.execRootCmdSilent("echo test"); // 通过执行测试命令来检测
        if (ret != -1) {
            return true;
        } else {
            return false;
        }

    }


    @Override
    public String getSDTotalSize(Context context) {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return Formatter.formatFileSize(context, blockSize * totalBlocks);
    }

    @Override
    public long getSDAvailableSize(Context context) {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return blockSize * availableBlocks;
    }

    @Override
    public boolean isHome(Context context) {
        if(null != context)
        {
            List<String> packages = new ArrayList<String>();
            PackageManager packageManager = context.getPackageManager();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo info : resolveInfo)
            {
                packages.add(info.activityInfo.packageName);
                System.out.println(info.activityInfo.packageName);
            }
            List<String> homes = packages;
            ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
            if(null != homes && homes.size() > 0 && null != rti && rti.size() > 0)
            {
                return homes.contains(rti.get(0).topActivity.getPackageName());
            }
        }
        return false;
    }
}
