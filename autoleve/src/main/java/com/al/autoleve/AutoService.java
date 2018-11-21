package com.al.autoleve;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.andr.tool.log.LogUtil;
import com.andr.tool.util.TimeUtils;

/**
 * Created by zhangxiaoming on 2018/11/1.
 */

public class AutoService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //设备10分钟闹钟
        LogUtil.d("service-->oncreate");
        TimeUtils.timerSet(getApplicationContext(), 30, 60 * 10, new Intent(getApplicationContext(), AutoService
                .class));


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}
