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
                            LogUtil.D("周天,不打卡");

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
                                setTempTime();
                            }
                        } else {
                            //其他时间,正常

                            setTempTime();
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


    public void setTempTime() {
        //将上班时间转为毫秒数

        //当前时间


        try {
            long now_time = new SimpleDateFormat("HH:mm").parse(new SimpleDateFormat("HH:mm").format(new java.util
                    .Date(System.currentTimeMillis()))).getTime();

            //每日晚11点定时重启
            long reset_time = new SimpleDateFormat("HH:mm").parse("23:00").getTime();
            if (now_time >= reset_time) {
                //判断今日是否重启过

                if (!SharpData.getResetSys(getApplicationContext())) { //重启
                    //初始化数据
                    SharpData.setIsCompent(getApplicationContext(), 0);
                    SharpData.setOrderType(getApplicationContext(), 0);
                    LogUtil.D("开始重启");
                    //休眠执行重启
                    Thread.sleep(5000);
                    CMDUtil.doBoot();
                    return;
                }

                LogUtil.D("今日已重启时间-->"+reset_time);

            }


            //上班时间段
            long up_time_1 = new SimpleDateFormat("HH:mm").parse(Comm.upJobTime[0]).getTime();
            long up_time_2 = new SimpleDateFormat("HH:mm").parse(Comm.upJobTime[1]).getTime();

            //下班时间段
            long down_time_1 = new SimpleDateFormat("HH:mm").parse(Comm.downJobTime[0]).getTime();
            long down_time_2 = new SimpleDateFormat("HH:mm").parse(Comm.downJobTime[1]).getTime();


            LogUtil.D("当前时间-->" + now_time + "上班时间-->" + up_time_1 + "-" + up_time_2 + " 下班时间-" + down_time_1 + "-" +
                    down_time_2);


            //判断打卡功能是否开启
            int isopen = SharpData.getOpenJob(getApplicationContext());

            //判断设定的时间是否正确
            if (up_time_2 > up_time_1) {
                if (now_time >= up_time_1 && now_time <= up_time_2) {
                    //开始执行上班打卡

                    if (isopen == 0 || isopen == 2) {
                        LogUtil.E("上班打开功能未开启");
                        SharpData.setOrderType(getApplicationContext(), 0);
                        return;
                    }

                    //判断是否已打卡成功
                    if (SharpData.getIsCompent(getApplicationContext()) == 1) {
                        LogUtil.E("今日已打上班卡");
                        SharpData.setOrderType(getApplicationContext(), 0);

                    } else {
                        SharpData.setOrderType(getApplicationContext(), 1);
                        LogUtil.E("符合上班打卡时间,开始执行上班打卡任务");
                        sendAction();

                    }
                    return;
                } else {
                    LogUtil.E("还未到上班打卡时间段");
                    SharpData.setOrderType(getApplicationContext(), 0);

                }


            } else {
                LogUtil.E("设置的上班打卡时间格式不符合");
                SharpData.setOrderType(getApplicationContext(), 0);

            }

            //将下班时间转为毫秒数


            if (down_time_2 > down_time_1) {
                if (now_time >= down_time_1 && now_time <= down_time_2) {
                    //开始执行上班打卡
                    if (isopen == 0 || isopen == 1) {
                        LogUtil.E("下班班打开功能未开启");
                        SharpData.setOrderType(getApplicationContext(), 0);
                        return;
                    }


                    //判断是否已打卡成功
                    if (SharpData.getIsCompent(getApplicationContext()) == 2) {
                        LogUtil.E("今日已打下班卡");
                        SharpData.setOrderType(getApplicationContext(), 0);
                    } else {
                        SharpData.setOrderType(getApplicationContext(), 2);
                        LogUtil.E("符合下班打卡时间,开始执行下班打卡任务");
                        sendAction();
                    }

                } else {
                    LogUtil.E("还未到下班打卡时间段");
                    SharpData.setOrderType(getApplicationContext(), 0);

                }
            } else {
                LogUtil.E("设置的下班班打卡时间格式不符合");
                SharpData.setOrderType(getApplicationContext(), 0);

            }


        } catch (Exception e) {
            e.printStackTrace();
        }


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
        int open = SharpData.getOpenJob(getApplicationContext());


        int job = SharpData.getIsCompent(getApplicationContext());
        if (apm == 0) {
            if (job == 1) {
                LogUtil.D("上班打卡任务已完成,等待下次任务");
                SharpData.setOrderType(getApplicationContext(), 0);
                return;
            }
            if (open == 0) {
                LogUtil.E("上下班打卡功能被关闭");
                SharpData.setOrderType(getApplicationContext(), 0);
                return;
            }


            if (open == 2) {
                LogUtil.E("上班打卡任务已关闭");
                SharpData.setOrderType(getApplicationContext(), 0);
                return;
            }
        } else {

            hour = hour + 12;
            LogUtil.D("当前时间--" + hour);
            if (hour == 23 && min == 5) {  //下午12点自动清除打卡数据
                SharpData.setIsCompent(getApplicationContext(), 0);
                try {
                    Thread.sleep(5000);
                    CMDUtil.doBoot(); //执行重启
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (open == 0) {
                LogUtil.E("上下班打卡功能被关闭");
                SharpData.setOrderType(getApplicationContext(), 0);
                return;
            }

            if (job == 2) {
                LogUtil.D("下班打卡任务已完成,等待下次任务");
                SharpData.setOrderType(getApplicationContext(), 0);
                return;
            }

            if (open == 1) {
                LogUtil.E("下班任务已关闭");
                SharpData.setOrderType(getApplicationContext(), 0);

                return;
            }


        }


        //
        String jobtime_1 = apm == 0 ? Comm.upJobTime[0] : Comm.downJobTime[0];
        String jobtime_2 = apm == 0 ? Comm.upJobTime[1] : Comm.downJobTime[1];

        String jobtime_hour_1 = jobtime_1.split(":")[0];
        String jobtime_hour_2 = jobtime_2.split(":")[0];


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
            min_2 = min_2.substring(1, 2);
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


        if (hour >= hour1 && hour <= hour2) {
            if (hour == hour1) { //等于最小
                SharpData.setOrderType(getApplicationContext(), apm == 0 ? 1 : 2);
                sendAction();
                LogUtil.E("已到设定时间,开始任务");
                return;
            } else if (hour == hour2) { //等于最大
                //比较分钟
                if (min <= min2) {
                    SharpData.setOrderType(getApplicationContext(), apm == 0 ? 1 : 2);
                    LogUtil.E("已到设定时间,开始任务");
                    sendAction();

                    return;
                }
            } else {  //在两个之间

                SharpData.setOrderType(getApplicationContext(), apm == 0 ? 1 : 2);
                LogUtil.E("已到设定时间,开始任务");
                sendAction();
                return;
            }

        }

        LogUtil.E("未到任务设定时间,等待任务");

        SharpData.setOrderType(getApplicationContext(), 0);


    }


    //发送一个动作
    private void sendAction() {
        Intent intent = new Intent(getApplicationContext(), MainAccessService.class);
        startService(intent);
    }
}
