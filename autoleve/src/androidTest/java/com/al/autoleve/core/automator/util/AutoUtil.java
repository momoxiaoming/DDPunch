package com.al.autoleve.core.automator.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.EditText;

import com.al.autoleve.core.data.UiDataCenter;
import com.al.autoleve.core.util.At;
import com.andr.tool.util.StringUtil;

/**
 * 这里主要存放一些我们和lua的数据交换接口
 * Created by zhangxiaoming on 2018/9/21.
 */

public class AutoUtil {

    /**
     * 检查app是否存在
     *
     * @param pakeName
     * @return
     */
    public static boolean checkApkExist(String pakeName) {
        Context context = UiDataCenter.getInstance().getMainApkContext();

        PackageInfo rlt = null;
        if (null == context || StringUtil.isStringEmpty(pakeName)) {
            return rlt == null ? false : true;
        }
        try {
            PackageManager pm = context.getPackageManager();
            if (pm != null) {
                rlt = pm.getPackageInfo(pakeName, PackageManager.GET_UNINSTALLED_PACKAGES);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return rlt == null ? false : true;
    }


    public static void cleanTextForEditText(EditText editText) {
//        editText.sett
    }

    public static void inputEditText(Object editText, String msg) {
        com.andr.tool.log.LogUtil.d("要输出的:" + msg);
        if (editText != null) {
            ((EditText) editText).setText(msg);
        }
    }

    /**
     * 设置任务结果
     */
    public static void setTaskActionResult(boolean rlt) {
        UiDataCenter.getInstance().setTaskResult(rlt);

    }

    /**
     * 设置任务结果,并附带详情秒速
     */
    public static void setTaskActionResult(boolean rlt, String desc) {
        UiDataCenter.getInstance().setTaskResult(rlt);
        UiDataCenter.getInstance().setTaskDesc(desc);
    }


    /**
     * 用于lua层获取打卡类型
     *
     * @return
     */
    public static int getPushType() {
        return UiDataCenter.getInstance().getPushType();
    }

    /**
     * 用于lua层获取钉钉账号
     *
     * @return
     */
    public static String getDingDingAccount() {
        return UiDataCenter.getInstance().getDdAccount();
    }

    /**
     * 用于lua层获取钉钉密码
     *
     * @return
     */
    public static String getDingDingPwd() {
        return UiDataCenter.getInstance().getDdpwd();
    }

    /**
     * 通过屏幕点击执行打卡操作
     * @param signType 1是上班卡,2是下班卡
     * @return
     */
    public static boolean doSignClick(final int signType) {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                //必须在子线程才可实现点击操作
                com.andr.tool.log.LogUtil.d("执行点击打卡:"+signType);
                boolean flg=At.doSignClick(signType);
                com.andr.tool.log.LogUtil.d(flg?"点击成功":"点击失败");
            }
        }).start();
        try
        {
            Thread.sleep(3000);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        return true;
    }


}
