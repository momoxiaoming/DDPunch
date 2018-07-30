package com.al.ddpunch.util;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

/**
 * Created by zhangxiaoming on 2018/7/26.
 */

public class TimeUtil {
    /**
     * 设置闹钟定时启动服务
     *
     * @param context   className
     * @param fristTime  开始时间
     * @param cyctime   //重复间隔时间
     * @param
     */
    @SuppressLint("ShortAlarm")
    public static void timerSet(Context context, long fristTime, long cyctime,Class className) {
        Log.d("allen","开启闹钟");
        Intent intent = new Intent(context,className );
        PendingIntent sender = PendingIntent.getService(context, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + fristTime *
                1000, cyctime * 1000, sender);

    }

    /**
     * 取消闹钟服务
     *
     * @param context className
     * @param
     * @param
     * @param
     */
    @SuppressLint("ShortAlarm")
    public static void timerCancel(Context context,Class className) {
        Intent intent = new Intent(context, className);
        PendingIntent sender = PendingIntent.getService(context, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);

    }
}
