package com.al.ddpunch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by zhangxiaoming on 2018/7/30.
 */

public class MyBrocast extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context,DDService.class));
    }
}
