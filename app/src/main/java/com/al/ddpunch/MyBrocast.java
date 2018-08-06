package com.al.ddpunch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.al.ddpunch.util.LogUtil;
import com.al.ddpunch.util.SharpData;

/**
 * Created by zhangxiaoming on 2018/7/30.
 */

public class MyBrocast extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.D("监听到开机广播");
        context.startService(new Intent(context,DDService.class));
        SharpData.setResetSys(context);
    }
}
