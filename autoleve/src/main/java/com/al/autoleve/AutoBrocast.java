package com.al.autoleve;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.andr.tool.cmd.CmdUtils;
import com.andr.tool.file.FileUtil;
import com.andr.tool.log.LogUtil;
import com.andr.tool.phone.PhoneUtil;

/**
 * Created by zhangxiaoming on 2018/11/1.
 */

public class AutoBrocast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //
        LogUtil.i("监听到开机广播,正在启动主服务");
        if (FileUtil.getInstance().readFile(AppConfig.TOKEN_FILE_PATH) != null && PhoneUtil.getInstance()
                .isRoot()) {
            CmdUtils.execRootCmdSilent(AppConfig.RESTART_MAIN_LOGICAL_APK_CMD);
        } else {
            LogUtil.d("程序未注册或未root");
        }

    }
}
