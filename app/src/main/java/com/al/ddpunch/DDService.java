package com.al.ddpunch;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.al.ddpunch.util.CMDUtil;
import com.al.ddpunch.util.LogUtil;
import com.al.ddpunch.util.SharpData;
import com.al.ddpunch.util.TimeUtil;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by zhangxiaoming on 2018/7/26.
 */

public class DDService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        //添加一个定时器


        TimeUtil.timerSet(getApplicationContext(), 0, 5 * 60, DDService.class);

        startThread();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public void startThread() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                //循环请求服务器指令

                while (true) {

                    try {


                        Date date = new Date(System.currentTimeMillis());
                        SimpleDateFormat format = new SimpleDateFormat("E");
                        String timeStr = format.format(date);
                        if (timeStr.equals("周日")) {
                            SharpData.setOrderType(getApplicationContext(), 0);

                        } else if (timeStr.equals("周六")) {
                            //判断是否是单双数
                            String s = new SimpleDateFormat("dd").format(date);
                            if (Integer.valueOf(s) % 2 == 0) {
                                //双
                                LogUtil.D("监测到周六是双周,免打卡");
                                SharpData.setOrderType(getApplicationContext(), 0);

                            } else {
                                //单,需要上班
                                LogUtil.D("监测到周六是单周周规定时间启动");
                                JudgeTime();
                            }
                        } else {
                            //其他时间,正常

                            JudgeTime();
                        }
                        //1分钟间隔,检查一下当前时间
                        Thread.sleep(Comm.JugeTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }


            }
        }).start();

    }

    private void JudgeTime() {
        long time = System.currentTimeMillis();
        final Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(time);

        int hour = mCalendar.get(Calendar.HOUR);
        int min = mCalendar.get(Calendar.MINUTE);
        int apm = mCalendar.get(Calendar.AM_PM);
        LogUtil.D("时间--" + hour + ":" + min);


        timTime(hour, min, apm);
    }


    private void timTime(int hour, int min, int apm) {
        int open=SharpData.getOpenJob(getApplicationContext());



        int job=SharpData.getIsCompent(getApplicationContext());
        if (apm == 0) {
            if(job==1){
                LogUtil.D("上班打卡任务已完成,等待下次任务");
                SharpData.setOrderType(getApplicationContext(), 0);
                return;
            }

            if(open==0){
                LogUtil.E("上下班打卡功能被关闭");
                SharpData.setOrderType(getApplicationContext(), 0);
                return;
            }




            if (open==2) {
                LogUtil.E("上班打卡任务已关闭");
                SharpData.setOrderType(getApplicationContext(), 0);
                return;
            }
        } else {

            hour=hour+12;
            if (hour == 23&&min==5)
            {  //下午12点自动清除打卡数据
                SharpData.setIsCompent(getApplicationContext(), 0);
                try {
                    Thread.sleep(5000);
                    CMDUtil.doBoot(); //执行重启
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if(open==0){
                LogUtil.E("上下班打卡功能被关闭");
                SharpData.setOrderType(getApplicationContext(), 0);
                return;
            }

            if(job==2){
                LogUtil.D("下班打卡任务已完成,等待下次任务");
                SharpData.setOrderType(getApplicationContext(), 0);
                return;
            }

            if (open==1) {
                LogUtil.E("下班任务已关闭");
                SharpData.setOrderType(getApplicationContext(), 0);

                return;
            }



        }


        //
        String jobtime_1 = apm == 0 ? Comm.upJobTime[0] : Comm.downJobTime[0];
        String jobtime_2 = apm == 0 ? Comm.upJobTime[1] : Comm.downJobTime[1];

        String jobtime_hour_1=jobtime_1.split(":")[0];
        String jobtime_hour_2=jobtime_2.split(":")[0];


        if (jobtime_hour_1.startsWith("0")) {
            jobtime_hour_1 = jobtime_hour_1.substring(1, 2);
        } else {
            jobtime_hour_1 = jobtime_hour_1.substring(0, 2);
        }


        if (jobtime_hour_2.startsWith("0")) {
            jobtime_hour_2 = jobtime_hour_2.substring(1, 2);
        }

        int hour1 = Integer.valueOf(jobtime_hour_1);
        int hour2 = Integer.valueOf(jobtime_hour_2);


        String min_1 = jobtime_1.split(":")[1];
        String min_2 = jobtime_2.split(":")[1];

        if (min_1.startsWith("0")) {
            min_1 = min_1.substring(min_1.length() - 1, min_1.length());
        } else {
            min_1 = min_1.substring(min_1.length() - 2, min_1.length());

        }
        if (min_2.startsWith("0")) {
            min_2 = min_2.substring(1,2);
        }


        int min1 = Integer.valueOf(min_1);
        int min2 = Integer.valueOf(min_2);

//        LogUtil.D("下午截取的设定时间1--" + hour1 + ":" + min1);
//        LogUtil.D("下午截取的设定时间2--" + hour2 + ":" + min2);

        if (hour1 > hour2) {
            LogUtil.E("设置的时间有误");
            return;
        } else if (hour1 == hour2) {
            if (min1 >= min2) {
                LogUtil.E("设置的时间有误");
                return;
            }
        }


        if (hour  >= hour1 && hour <= hour2) {
            if ((hour + 12) == hour1) { //等于最小
                SharpData.setOrderType(getApplicationContext(), apm==0?1:2);
                sendAction();
                LogUtil.E("已到设定时间,开始任务");
                return;
            } else if (hour == hour2) { //等于最大
                //比较分钟
                if (min <= min2) {
                    SharpData.setOrderType(getApplicationContext(), apm==0?1:2);
                    LogUtil.E("已到设定时间,开始任务");
                    sendAction();

                    return;
                }
            } else {  //在两个之间
                SharpData.setOrderType(getApplicationContext(), apm==0?1:2);
                LogUtil.E("已到设定时间,开始任务");
                sendAction();
                return;
            }

        }

        LogUtil.E("未到任务设定时间,等待任务");

        SharpData.setOrderType(getApplicationContext(), 0);




    }


    //发送一个动作
    private void sendAction(){
        Intent intent=new Intent(getApplicationContext(),MainAccessService.class);
        startService(intent);
    }
}
