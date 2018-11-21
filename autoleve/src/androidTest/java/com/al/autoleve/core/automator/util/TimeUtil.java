package com.al.autoleve.core.automator.util;

import android.annotation.SuppressLint;
import android.os.SystemClock;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 时间格式转化工具类
 */
@SuppressLint("SimpleDateFormat")
public class TimeUtil {

    private static final String DEFAULT_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DEFAULT_LOCAL_TIME_FORMAT = "yyyyMMddHHmmss";
    private static final String DEFAULT_TIME_ZONE = "UTC";
    public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat(DEFAULT_TIME_FORMAT);

    private TimeUtil() {
        throw new AssertionError();
    }

    public static String getTime(long timeInMillis, SimpleDateFormat dateFormat) {
        return null == dateFormat ? DEFAULT_DATE_FORMAT.format(new Date(timeInMillis)) : dateFormat.format(new Date
                (timeInMillis));
    }

    public static String getTime(long timeInMillis) {
        return getTime(timeInMillis, DEFAULT_DATE_FORMAT);
    }

    public static long getCurrentTimeInLong() {
        return System.currentTimeMillis();
    }

    public static String getCurrentTimeInString() {
        return getTime(getCurrentTimeInLong());
    }

    public static String getCurrentTimeInString(SimpleDateFormat dateFormat) {
        return getTime(getCurrentTimeInLong(), dateFormat);
    }

    /**
     * 获取手机开机运行的时间包括休眠时间
     */
    public static String getPhRunTime() {
        return String.valueOf(SystemClock.elapsedRealtime());
    }

    /**
     * @return long 当前手机utc时间毫秒数
     * @Title: getCurrentUtcLongTime
     * @Description: 获取手机的UTC时间毫秒
     * @ModifiedPerson LuoZhong
     * @date 2016年6月30日 上午10:57:01
     */
    public static long getCurrentUtcLongTime() {
        return convertTime(getCurrentUtcTime());
    }

    /**
     * 获取手机的UTC时间字符串
     */
    public static String getCurrentUtcTime() {
        Calendar cal = Calendar.getInstance();
        int zoneOffset = cal.get(Calendar.ZONE_OFFSET);
        int dstOffset = cal.get(Calendar.DST_OFFSET);
        cal.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        return DEFAULT_DATE_FORMAT.format(new Date(cal.getTimeInMillis()));
    }

    /**
     * @param utcTime
     * @return long
     * @Title: convertUtcTime
     * @Description: 将utc时间转为毫秒数
     * @ModifiedPerson LuoZhong
     * @date 2016年6月30日 上午10:56:23
     */
    public static long convertTime(String utcTime) {
        long rlt = 0;
        if (null == utcTime || ("").equals(utcTime.trim())) {
            return rlt;
        }
        try {
            Date gpsUTCDate = DEFAULT_DATE_FORMAT.parse(utcTime);
            if (null != gpsUTCDate) {
                Calendar c = Calendar.getInstance();
                c.setTime(gpsUTCDate);
                rlt = c.getTimeInMillis();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return rlt;
    }

    /**
     * utcTimeToReal (utc时间转换成本地时间，只需要传2个参数)
     *
     * @param utcTime         utc时间
     * @param localTimePatten 本地时间的显示格式
     * @return String
     * @ModifiedPerson ZhangShuo
     * @date 2016年7月8日 上午11:18:49
     */
    public static String utcTimeToReal(String utcTime, String localTimePatten) {
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
    public static String utcTimeToReal(String utcTime) {
        return utcTimeToReal(utcTime, DEFAULT_TIME_FORMAT, DEFAULT_LOCAL_TIME_FORMAT);
    }

    /**
     * utcTimeToReal (把utc时间转换成本地时间)
     *
     * @param utcTime         utc时间
     * @param utcTimePatten   utc时间的格式，用于解析utc时间成Data对象
     * @param localTimePatten 本地时间的显示格式
     * @return String
     * @ModifiedPerson ZhangShuo
     * @date 2016年7月8日 上午11:16:11
     */

    private static String utcTimeToReal(String utcTime, String utcTimePatten, String localTimePatten) {
        String localTime = "0";
        if (null != utcTime && null != localTimePatten) {

            SimpleDateFormat utcFormater = new SimpleDateFormat(utcTimePatten);
            utcFormater.setTimeZone(TimeZone.getTimeZone(DEFAULT_TIME_ZONE));
            Date gpsUTCDate = null;
            try {
                gpsUTCDate = utcFormater.parse(utcTime);
                if (null != gpsUTCDate) {
                    SimpleDateFormat localFormater = new SimpleDateFormat(localTimePatten);
                    localTime = localFormater.format(gpsUTCDate.getTime());
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return localTime;
    }


    //-------

    /**
     * 获取年份
     * @return
     */
    public static String getCurYear()
    {
        Calendar cl = Calendar.getInstance();
        return cl.get(Calendar.YEAR) + "";
    }

    /**
     * 获取月日
     * @return
     */
    public static String getCurMonthDay()
    {
        Calendar cl = Calendar.getInstance();
        int month = cl.get(Calendar.MONTH) + 1; // 0代表1月，11代表12月
        int day = cl.get(Calendar.DAY_OF_MONTH);
        return String.format("%02d-%02d", month, day);
    }

    /**
     * 获取昨天对应的月日，月日各占两位,如：06-01：表示6月1日，如果今天为6.1日，算出的昨天为05.31日
     * @return
     */
    public static String getYesterdayMonthDay()
    {
        Calendar cl = Calendar.getInstance();

        int day = cl.get(Calendar.DATE);
        cl.set(Calendar.DATE, day - 1);

        int yesMonth = cl.get(Calendar.MONTH) + 1; // 0代表1月，11代表12月
        int yesDay = cl.get(Calendar.DAY_OF_MONTH);

        return String.format("%02d-%02d", yesMonth, yesDay);
    }

    /**
     * 获取几年的日期，格式：2008-6-16
     *
     * @return
     */
    public static String getTodaysDate()
    {
        Date date = new Date();
        DateFormat d1 = DateFormat.getDateInstance(); //默认语言（汉语）下的默认风格（MEDIUM风格，比如：2008-6-16 20:54:53）
        return d1.format(date);
    }

    /**
     * 返回两个日期间相差的天数
     *
     * @param date1 格式："yyyy-MM-dd" 或 "yyyy-MM-dd HH:mm:ss"等
     * @param date2
     * @return 返回正整数
     */
    public static int getDaysBetweenDates(String date1,String date2)
    {
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        long time1 = 0;
        long time2 = 0;

        try{
            cal.setTime(dateFormat.parse(date1));
            time1 = cal.getTimeInMillis();
            cal.setTime(dateFormat.parse(date2));
            time2 = cal.getTimeInMillis();
        }catch(Exception e){
            e.printStackTrace();
        }
        long between_days=Math.abs(time2-time1)/(1000*3600*24);

        return Integer.parseInt(String.valueOf(between_days));
    }

    // 获取距离今天的任何一天，正数往未来推，负数往已过完的天推
    public static String getMonthDayOfDistanceToday(int offDay)
    {
        Calendar cl = Calendar.getInstance();

        int day = cl.get(Calendar.DATE);
        cl.set(Calendar.DATE, day - offDay);

        int yesMonth = cl.get(Calendar.MONTH) + 1; // 0代表1月，11代表12月
        int yesDay = cl.get(Calendar.DAY_OF_MONTH);

        return String.format("%02d-%02d", yesMonth, yesDay);
    }

    // date类型转换为String类型
    // formatType格式为yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
    // data Date类型的时间
    public static String dateToString(Date data, String formatType) {
        return new SimpleDateFormat(formatType).format(data);
    }

    // long类型转换为String类型
    // currentTime要转换的long类型的时间
    // formatType要转换的string类型的时间格式
    public static String longToString(long currentTime, String formatType)
            throws ParseException {
        Date date = longToDate(currentTime, formatType); // long类型转成Date类型
        String strTime = dateToString(date, formatType); // date类型转成String
        return strTime;
    }

    // string类型转换为date类型
    // strTime要转换的string类型的时间，formatType要转换的格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日
    // HH时mm分ss秒，
    // strTime的时间格式必须要与formatType的时间格式相同
    public static Date stringToDate(String strTime, String formatType)
            throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        date = formatter.parse(strTime);
        return date;
    }

    // long转换为Date类型
    // currentTime要转换的long类型的时间
    // formatType要转换的时间格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
    public static Date longToDate(long currentTime, String formatType)
            throws ParseException {
        Date dateOld = new Date(currentTime); // 根据long类型的毫秒数生命一个date类型的时间
        String sDateTime = dateToString(dateOld, formatType); // 把date类型的时间转换为string
        Date date = stringToDate(sDateTime, formatType); // 把String类型转换为Date类型
        return date;
    }

    // string类型转换为long类型
    // strTime要转换的String类型的时间
    // formatType时间格式
    // strTime的时间格式和formatType的时间格式必须相同
    public static long stringToLong(String strTime, String formatType)
            throws ParseException {
        Date date = stringToDate(strTime, formatType); // String类型转成date类型
        if (date == null) {
            return 0;
        } else {
            long currentTime = dateToLong(date); // date类型转成long类型
            return currentTime;
        }
    }

    // date类型转换为long类型
    // date要转换的date类型的时间
    public static long dateToLong(Date date) {
        return date.getTime();
    }

}