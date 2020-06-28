package com.tobot.common.util;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author houdeming
 * @date 2019/2/27
 */
public class DateTool {
    /**
     * 获取星期
     *
     * @param currentMinute
     * @return
     */
    public static String getSendWeekDay(long currentMinute) {
        String week = "";
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        // 获取星期几
        int weekId = calendar.get(Calendar.DAY_OF_WEEK);
        switch (weekId) {
            case 1:
                // 星期日
                week = "星期日";
                break;
            case 2:
                // 星期一
                week = "星期一";
                break;
            case 3:
                // 星期二
                week = "星期二";
                break;
            case 4:
                // 星期三
                week = "星期三";
                break;
            case 5:
                // 星期四
                week = "星期四";
                break;
            case 6:
                // 星期五
                week = "星期五";
                break;
            case 7:
                // 星期六
                week = "星期六";
                break;
            default:
                break;
        }
        return week;
    }

    @SuppressLint("SimpleDateFormat")
    public static int getCurrentHour(long currentMinute) {
        Date date = new Date(currentMinute);
        SimpleDateFormat format = new SimpleDateFormat("HH");
        String dateTime = format.format(date);
        int hour = 0;
        if (!TextUtils.isEmpty(dateTime)) {
            hour = Integer.parseInt(dateTime);
        }
        return hour;
    }

    /**
     * 得到当前的分钟，当10分钟之内时，系统默认读的是一位数字
     *
     * @param currentMinute
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static int getCurrentMinute(long currentMinute) {
        Date date = new Date(currentMinute);
        SimpleDateFormat format = new SimpleDateFormat("mm");
        String dateTime = format.format(date);
        int minute = 0;
        if (!TextUtils.isEmpty(dateTime)) {
            minute = Integer.parseInt(dateTime);
        }
        return minute;
    }

    /**
     * 获取2位数的分钟，当10分钟之内时，系统默认读的是一位数字
     *
     * @param minute
     * @return
     */
    public static String get2DigitMinute(long minute) {
        int currentMinute = getCurrentMinute(minute);
        String tempMinute = String.valueOf(currentMinute);
        String minuteTwo;
        if (tempMinute.length() == 1) {
            minuteTwo = "0" + tempMinute;
        } else {
            minuteTwo = tempMinute;
        }
        return minuteTwo;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getCurrentDate(long currentMinute) {
        Date date = new Date(currentMinute);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    @SuppressLint("SimpleDateFormat")
    public static String getCurrentTime(long currentMinute) {
        Date date = new Date(currentMinute);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(date);
    }

    @SuppressLint("SimpleDateFormat")
    public static String getCurrentTimeDetail(long currentMinute) {
        Date date = new Date(currentMinute);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    @SuppressLint("SimpleDateFormat")
    public static String getCurrentTimeDetail(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    @SuppressLint("SimpleDateFormat")
    public static long getLongTime(String dateString) {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString);
            return date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 根据日期、时间获取Calendar   data:2016-05-08 time:09:05:00
     *
     * @param data
     * @param time
     * @return
     */
    public static Calendar getCalendar(String data, String time) {
        Calendar calendar = Calendar.getInstance();
        String[] dataArray = getDataArray(data);
        String[] timeArray = getTimeArray(time);

        if (dataArray != null && dataArray.length > 0) {
            // 当前年
            calendar.set(Calendar.YEAR, Integer.parseInt(dataArray[0]));
            // 当前月，从0开始 【0-11】
            calendar.set(Calendar.MONTH, Integer.parseInt(dataArray[1]) - 1);
            // 当前日
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dataArray[2]));
        }

        if (timeArray != null && timeArray.length > 0) {
            // 当前小时
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]));
            // 当前分钟
            calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]));
            // 当前秒
            calendar.set(Calendar.SECOND, 0);
        }
        return calendar;
    }

    /**
     * 得到日期的数组 data:2016-05-08
     *
     * @param data
     * @return
     */
    private static String[] getDataArray(String data) {
        String[] dataArray = null;
        if (!TextUtils.isEmpty(data)) {
            dataArray = data.split("-");
            if (dataArray != null && dataArray.length > 0) {
                String data1 = dataArray[1];
                if (data1.startsWith("0")) {
                    // 月
                    dataArray[1] = data1.substring(1);
                }
                String data2 = dataArray[2];
                if (data2.startsWith("0")) {
                    // 日
                    dataArray[2] = data2.substring(1);
                }
            }
        }
        return dataArray;
    }

    /**
     * 得到时间的数组 time:09:05:00
     *
     * @param time
     * @return
     */
    private static String[] getTimeArray(String time) {
        String[] timeArray = null;
        if (!TextUtils.isEmpty(time)) {
            timeArray = time.split("\\:");
            if (timeArray != null && timeArray.length > 0) {
                String time1 = timeArray[0];
                if (time1.startsWith("0")) {
                    // 时
                    timeArray[0] = time1.substring(1);
                }
                String time2 = timeArray[1];
                if (time2.startsWith("0")) {
                    // 分
                    timeArray[1] = time2.substring(1);
                }
            }
        }
        return timeArray;
    }
}
