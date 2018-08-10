package com.al.ddpunch;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.al.ddpunch.email.EmaiUtil;
import com.al.ddpunch.util.LogUtil;
import com.al.ddpunch.util.SharpData;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhangxiaoming on 2018/8/9.
 */

@SuppressLint("OverrideAbstract")
public class NotService extends NotificationListenerService {


    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);

        if(sbn.getPackageName().equals(Comm.dingding_PakeName)){
            Notification notification =sbn.getNotification();
            if(notification==null){
                return;
            }
            String tikeText = notification.tickerText == null ? "" : notification.tickerText.toString();
            String notTitle = notification.extras.getString("android.title") == null ? "" : notification.extras
                    .getString("android.title");//标题
            String subText = notification.extras.getString("android.subText") == null ? "" : notification.extras
                    .getString("android.subText");//摘要
            String text = notification.extras.getString("android.text") == null ? "" : notification.extras
                    .getString("android.text");  //正文
            String postTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date(notification.when));   //通知时间
            LogUtil.D("通知时间-->" + postTime);
            LogUtil.D("通知-->tikeText:" + tikeText);
            LogUtil.D("通知-->标题:" + notTitle + "--摘要--" + subText + "--正文--" + text);

            SharpData.setNotData(getApplicationContext(), "通知时间-->" + postTime + "-通知-->tikeText:" + tikeText +
                    "通知-->标题:" + notTitle + "--摘要--" + subText + "--正文--" + text);

            String emmail = SharpData.getEmailData(getApplicationContext()).equals("") ? Comm.EmailInfo :
                    SharpData.getEmailData(getApplicationContext());

            //首先判断通知时间是不是当前时间
            String nowTime = new SimpleDateFormat("yyyy-MM-dd").format(System.currentTimeMillis());

            //如果是当天
            if (nowTime.equals(postTime)) {
                if (text.contains("上班打卡成功")) {
                    SharpData.setIsCompent(getApplicationContext(), 1);
                    EmaiUtil.sendMsg("服务通知2:"+text, emmail);
                }
                if (text.contains("下班打卡成功")) {
                    SharpData.setIsCompent(getApplicationContext(), 2);
                    EmaiUtil.sendMsg("服务通知2:"+text, emmail);

                }
            }

            cancelAllNotifications();

        }


    }
}
