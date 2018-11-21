package com.andr.tool.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.andr.tool.R;

import java.util.HashMap;
import java.util.Map;

/**
 * 通知管理器
 */
public class NotManager {

    private Context mContext;
    // NotificationManager ： 是状态栏通知的管理类，负责发通知、清楚通知等。
    private NotificationManager manager;
    // 定义Map来保存Notification对象
    private Map<Integer, NotificationCompat.Builder> map = null;

    public static int progressId = 91000;

    private static NotManager mIntence;



    public static NotManager getInstance(Context mContext) {
        if (null == mIntence) {
            synchronized (NotManager.class) {
                if (null == mIntence) {
                    mIntence = new NotManager(mContext);
                }
            }
        }
        return mIntence;
    }

    public NotManager(Context mContext) {
        // NotificationManager 是一个系统Service，必须通过 getSystemService()方法来获取。
        manager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
        map = new HashMap<Integer, NotificationCompat.Builder>();
        this.mContext = mContext;

    }

    public void initProgessNot( int notificationId, final String title) {
        sendProgessNot(notificationId, title);
    }

    public void updateProgressNot( int notificationId, final int progress) {
        updateProgress(notificationId, progress);

    }


    public void cancelNot( int notificationId) {
        cancel(notificationId);

    }

    /**
     * 设置通知标题
     * @param notificationId
     * @param msg
     */
    public void setNotontentText(int notificationId,String msg){
        NotificationCompat.Builder notify = map.get(notificationId);
        if (null != notify) {
            // 修改进度条
            notify.setContentText(msg);
            notify.setContentTitle(msg);
            manager.notify(notificationId, notify.build());

        }
    }


    /**
     * 发送一个进度条通知
     */
    private void sendProgessNot(int notificationId, String title) {


        if (!map.containsKey(notificationId)) {

            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, "1000");
            builder.setContentTitle(title).setSmallIcon(R.drawable.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable
                            .ic_launcher)) //设置通知的大图标
                    .setDefaults(Notification.DEFAULT_LIGHTS) //设置通知的提醒方式： 呼吸灯
                    .setPriority(NotificationCompat.PRIORITY_MAX) //设置通知的优先级：最大
                    .setAutoCancel(false)//设置通知被点击一次是否自动取消
                    .setContentText("下载进度:" + "0%")
                    .setProgress(100, 0, false);


            Notification notification = builder.build();
            manager.notify(notificationId, notification);
            map.put(notificationId, builder);
        }


    }


    /**
     * 取消通知操作
     *
     * @description：
     * @author ldm
     * @date 2016-5-3 上午9:32:47
     */
    private void cancel(int notificationId) {
        manager.cancel(notificationId);
        map.remove(notificationId);
    }

    private void updateProgress(int notificationId, int progress) {
        NotificationCompat.Builder notify = map.get(notificationId);
        if (null != notify) {
            // 修改进度条
            notify.setProgress(100, progress, false);
            notify.setContentText("下载进度:" + progress + "%");
            manager.notify(notificationId, notify.build());
        }
    }


}
