package com.andr.tool.util;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 时间格式转化工具类
 */
@SuppressLint("SimpleDateFormat")
public class TimeUtils 
{

    private static final String DEFAULT_TIME_FORMAT       = "yyyy-MM-dd HH:mm:ss";
    private static final String DEFAULT_LOCAL_TIME_FORMAT = "yyyyMMddHHmmss";
    private static final String DEFAULT_TIME_ZONE         = "UTC";
    public static final SimpleDateFormat DEFAULT_DATE_FORMAT       = new SimpleDateFormat(DEFAULT_TIME_FORMAT);

    private TimeUtils() 
    {
        throw new AssertionError();
    }

    public static String getTime(long timeInMillis, SimpleDateFormat dateFormat)
    {
        return null == dateFormat ? DEFAULT_DATE_FORMAT.format(new Date(timeInMillis)) : dateFormat.format(new Date(timeInMillis));
    }

    public static String getTime(long timeInMillis)
    {
        return getTime(timeInMillis, DEFAULT_DATE_FORMAT);
    }

    public static long getCurrentTimeInLong() 
    {
        return System.currentTimeMillis();
    }

    public static String getCurrentTimeInString()
    {
        return getTime(getCurrentTimeInLong());
    }

    public static String getCurrentTimeInString(SimpleDateFormat dateFormat)
    {
        return getTime(getCurrentTimeInLong(), dateFormat);
    }

    /**
     * 获取手机开机运行的时间包括休眠时间
     */
    public static String getPhRunTime()
    {
        return String.valueOf(SystemClock.elapsedRealtime());
    }

    /**
     * @Title: getCurrentUtcLongTime
     * @Description: 获取手机的UTC时间毫秒
     * @return long 当前手机utc时间毫秒数
     * @ModifiedPerson LuoZhong
     * @date 2016年6月30日 上午10:57:01
     */
    public static long getCurrentUtcLongTime() 
    {
        return convertTime(getCurrentUtcTime());
    }

    /**
     * 获取手机的UTC时间字符串
     */
    public static String getCurrentUtcTime()
    {
        Calendar cal = Calendar.getInstance();
        int zoneOffset = cal.get(Calendar.ZONE_OFFSET);
        int dstOffset = cal.get(Calendar.DST_OFFSET);
        cal.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        return DEFAULT_DATE_FORMAT.format(new Date(cal.getTimeInMillis()));
    }

    /**
     * @Title: convertUtcTime
     * @Description: 将utc时间转为毫秒数
     * @param utcTime
     * @return long
     * @ModifiedPerson LuoZhong
     * @date 2016年6月30日 上午10:56:23
     */
    public static long convertTime(String utcTime)
    {
        long rlt = 0;
        if (null == utcTime || ("").equals(utcTime.trim())) 
        {
            return rlt;
        }
        try 
        {
            Date gpsUTCDate = DEFAULT_DATE_FORMAT.parse(utcTime);
            if (null != gpsUTCDate)
            {
                Calendar c = Calendar.getInstance();
                c.setTime(gpsUTCDate);
                rlt = c.getTimeInMillis();
            }
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        return rlt;
    }

    /**
     * utcTimeToReal (utc时间转换成本地时间，只需要传2个参数)
     * 
     * @param utcTime
     *            utc时间
     * @param localTimePatten
     *            本地时间的显示格式
     * @return String
     * @ModifiedPerson ZhangShuo
     * @date 2016年7月8日 上午11:18:49
     */
    public static String utcTimeToReal(String utcTime, String localTimePatten)
    {
        return utcTimeToReal(utcTime, DEFAULT_TIME_FORMAT, localTimePatten);
    }

    /**
     * utcTimeToReal (utc时间转换成本地时间，传一个参数)
     * 
     * @param utcTime
     * @return String
     * @ModifiedPerson ZhangShuo
     * @date 2016年7月8日 下午2:30:12
     */
    public static String utcTimeToReal(String utcTime)
    {
        return utcTimeToReal(utcTime, DEFAULT_TIME_FORMAT, DEFAULT_LOCAL_TIME_FORMAT);
    }

    /**
     * utcTimeToReal (把utc时间转换成本地时间)
     * 
     * @param utcTime
     *            utc时间
     * @param utcTimePatten
     *            utc时间的格式，用于解析utc时间成Data对象
     * @param localTimePatten
     *            本地时间的显示格式
     * @return String
     * @ModifiedPerson ZhangShuo
     * @date 2016年7月8日 上午11:16:11
     */

    private static String utcTimeToReal(String utcTime, String utcTimePatten, String localTimePatten)
    {
        String localTime = "0";
        if (null != utcTime && null != localTimePatten) 
        {

            SimpleDateFormat utcFormater = new SimpleDateFormat(utcTimePatten);
            utcFormater.setTimeZone(TimeZone.getTimeZone(DEFAULT_TIME_ZONE));
            Date gpsUTCDate = null;
            try {
                gpsUTCDate = utcFormater.parse(utcTime);
                if (null != gpsUTCDate) 
                {
                    SimpleDateFormat localFormater = new SimpleDateFormat(localTimePatten);
                    localTime = localFormater.format(gpsUTCDate.getTime());
                }
            } catch (ParseException e)
            {
                e.printStackTrace();
            }
        }
        return localTime;
    }

    public static void sleep(long time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置闹钟定时启动服务
     *
     * @param context   className
     * @param fristTime 首次执行时间 秒
     * @param cyctime  重复执行时间 秒
     * @param
     */
    @SuppressLint("ShortAlarm")
    public static void timerSet(Context context, long fristTime, long cyctime,Intent intent) {
        Log.d("allen","开启闹钟");
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
    public static void timerCancel(Context context,Intent intent) {
        PendingIntent sender = PendingIntent.getService(context, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);

    }

}