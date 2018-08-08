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

                LogUtil.D("今日已重启时间-->" + reset_time);

            }


            //上班时间段
            long up_time_1 = new SimpleDateFormat("HH:mm").parse(SharpData.getDDupTime(getApplicationContext()))
                    .getTime();
            long up_time_2 = up_time_1 + Comm.temp_time;

            //下班时间段
            long down_time_1 = new SimpleDateFormat("HH:mm").parse(SharpData.getDDdownTime(getApplicationContext()))
                    .getTime();
            long down_time_2 = down_time_1 + Comm.temp_time;


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


    //发送一个动作
    private void sendAction() {
        Intent intent = new Intent(getApplicationContext(), MainAccessService.class);
        startService(intent);
    }


}
