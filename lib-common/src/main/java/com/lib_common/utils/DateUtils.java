package com.lib_common.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.lib_common.constants.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


@SuppressLint("SimpleDateFormat")
public class DateUtils {

    /**
     * 获取当前时间戳
     *
     * @return long
     */
    public static long getCurrentTimeStamp() {
        return System.currentTimeMillis();
    }

    /**
     * 获取格式化的当前系统时间
     *
     * @return String
     */
    public static String getCurrentDateStr() {
        return getFormatDate(getCurrentTimeStamp(), Constants.DATE_FORMAT_LINE);
    }

    /**
     * 获取当前时间 yyyy-MM-dd HH:mm:ss格式
     */
    public static String getCurrentDateTimeStr() {
        return getFormatDate(getCurrentTimeStamp(), Constants.DATE_FORMAT_TIME);
    }

    /**
     * 获取当前时间 yyyy-MM-dd HH:mm:ss:SSS格式
     */
    public static String getCurrentDateMilTimeStr() {
        return getFormatDate(getCurrentTimeStamp(), Constants.DATE_FORMAT_MILE_TIME);
    }

    public static Date getStringToDate(String time, String pattern) {
        if (TextUtils.isEmpty(pattern)) {
            pattern = "yyyy-MM";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        try {
            return simpleDateFormat.parse(time);
        } catch (ParseException e) {
            return new Date();
        }
    }

    public static Date getStringToDate(String time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        try {
            return simpleDateFormat.parse(time);
        } catch (ParseException e) {
            return new Date();
        }
    }

    public static Date getStringDayToDate(String time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return simpleDateFormat.parse(time);
        } catch (ParseException e) {
            return new Date();
        }
    }

    /**
     * 检查日期是否有效
     *
     * @param year  年
     * @param month 月
     * @param day   日
     * @return boolean
     */
    public static boolean getDateIsTrue(String year, String month, String day) {
        try {
            String data = year + month + day;
            SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyyMMdd");
            simpledateformat.setLenient(false);
            simpledateformat.parse(data);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static String getFormatDate(Date date) {
        return getFormatDate(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static String getFormatDate(Date date, String pattern) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat(pattern).format(date);
    }


    /**
     * 获取格式化时间
     *
     * @param timeStamp 时间戳
     * @param pattern   格式化格式（默认yyyy-MM-dd HH:mm:ss）
     */
    public static String getFormatDate(long timeStamp, String pattern) {

        String time;
        if (Long.toString(Math.abs(timeStamp)).length() < 11) {
            timeStamp *= 1000;
        }
        if (TextUtils.isEmpty(pattern)) {
            pattern = Constants.DATE_FORMAT_DEFAULT;
        }
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        format.setTimeZone(TimeZone.getDefault());
        time = format.format(timeStamp);
        return time;
    }

    /**
     * 格式化返回日期时间
     *
     * @param stringDate stringDate
     * @param pattern    pattern
     * @return String
     */
    public static String getFormatDate(String stringDate, String pattern) {
        if (TextUtils.isEmpty(stringDate)) {
            return "";
        }
        String parentPattern;
        if (stringDate.length() == 16) {
            parentPattern = "yyyy-MM-dd HH:mm";
        } else if (stringDate.length() == 19) {
            parentPattern = "yyyy-MM-dd HH:mm:ss";
        } else {
            return stringDate;
        }
        SimpleDateFormat sdf1 = new SimpleDateFormat(pattern);
        try {
            return sdf1.format(new SimpleDateFormat(parentPattern).parse(stringDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return stringDate;
    }

    /**
     * 获取系统的时间
     *
     * @return String
     */
    public static String getCurrentTimeStr() {
        SimpleDateFormat fort = new SimpleDateFormat("HH:mm:ss");
        fort.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        return fort.format(getCurrentTimeStamp());
    }

    /**
     * 获取年
     *
     * @return
     */
    public static int getYear() {
        Calendar cd = Calendar.getInstance();
        return cd.get(Calendar.YEAR);
    }

    /**
     * 获取月
     *
     * @return
     */
    public static int getMonth() {
        Calendar cd = Calendar.getInstance();
        return cd.get(Calendar.MONTH) + 1;
    }

    public static String getYearAndMonth() {
        Calendar cd = Calendar.getInstance();
        int month = cd.get(Calendar.MONTH) + 1;
        String newMonth = "";
        if (month < 10) {
            newMonth = "0" + month;
        } else {
            newMonth = "" + month;
        }
        return cd.get(Calendar.YEAR) + "-" + newMonth;
    }

    public static String getYearAndMonthDay() {
        Calendar cd = Calendar.getInstance();
        int date = cd.get(Calendar.DATE);
        int month = cd.get(Calendar.MONTH) + 1;
        String newMonth = "";
        if (month < 10) {
            newMonth = "0" + month;
        } else {
            newMonth = "" + month;
        }
        String newDate = "";
        if (date < 10) {
            newDate += "0" + date;
        } else {
            newDate = "" + date;
        }
        return cd.get(Calendar.YEAR) + "-" + newMonth + "-" + newDate;
    }

    /**
     * 获取日
     *
     * @return
     */
    public static int getDay() {
        Calendar cd = Calendar.getInstance();
        return cd.get(Calendar.DATE);
    }

    /**
     * 获取时
     *
     * @return
     */
    public static int getHour() {
        Calendar cd = Calendar.getInstance();
        return cd.get(Calendar.HOUR);
    }

    /**
     * 获取分
     *
     * @return
     */
    public static int getMinute() {
        Calendar cd = Calendar.getInstance();
        return cd.get(Calendar.MINUTE);
    }

    /**
     * 获取当前时间的下一天/ 前一天时间
     *
     * @param time time
     * @param day  正数为以后  负数为以前
     * @return
     */
    public static long getNextDayTimeStamp(long time, int day) {
        long timeStamp = 0;
        Calendar cal = Calendar.getInstance();
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        try {
            cal.setTime(date);
            cal.add(Calendar.DATE, 1);
            timeStamp = getStringToTimeStamp(sdf.format(cal.getTime()));
        } catch (Exception e) {
            e.printStackTrace();
            return timeStamp;
        }
        return timeStamp;
    }

    /**
     * 根据字符串时间获取时间戳
     *
     * @param stringDate stringDate
     * @return long
     */
    public static long getStringToTimeStamp(String stringDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date;
        long timeStamp = 0;
        if (TextUtils.isEmpty(stringDate)) {
            return timeStamp;
        }
        try {
            date = simpleDateFormat.parse(stringDate);
            timeStamp = date.getTime();
        } catch (ParseException e) {
            return timeStamp;
        }
        return timeStamp;
    }

    /**
     * 根据当前时间获取问候语
     *
     * @return 问候语
     */
    public static String getTimeTransformation() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour >= 0 && hour < 5) {
            return "凌晨";
        } else if (hour >= 5 && hour < 7) {
            return "清晨";
        } else if (hour >= 7 && hour < 9) {
            return "早上";
        } else if (hour >= 9 && hour < 12) {
            return "上午";
        } else if (hour >= 12 && hour < 14) {
            return "中午";
        } else if (hour >= 14 && hour < 17) {
            return "下午";
        } else if (hour >= 17 && hour < 19) {
            return "傍晚";
        } else if (hour >= 19 && hour < 21) {
            return "晚上";
        } else if (hour >= 21 && hour < 24) {
            return "深夜";
        }
        return "";
    }

    /**
     * 时间转换
     *
     * @param date 转换的时间戳
     */
    public static String dateTransformation(long date) {
        long difference = (getCurrentTimeStamp() / 1000) - (date / 1000);
        if (difference > 0) {
            if (difference < 60 * 60) {
                return difference / 60 + "分钟前";
            } else if (difference < 24 * 60 * 60) {

                return (int) (difference / 60 / 60) + "小时前";
            } else if (difference < 30 * 24 * 60 * 60) {

                return (int) (difference / 60 / 60 / 24) + "日前";
            } else {
                return getFormatDate(date, Constants.DATE_FORMAT_SLASH);
            }
        } else {
            return getFormatDate(date, Constants.DATE_FORMAT_SLASH);
        }
    }


    /**
     * 比较两个日期的大小，日期格式为yyyy-MM-dd
     *
     * @param str1 the first date
     * @param str2 the second date
     * @return true <br/>false
     */
    public static boolean isDateOneBigger(String str1, String str2) {
        boolean isBigger = false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dt1 = null;
        Date dt2 = null;
        try {
            dt1 = sdf.parse(str1);
            dt2 = sdf.parse(str2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (dt1.getTime() > dt2.getTime()) {
            isBigger = true;
        } else if (dt1.getTime() < dt2.getTime()) {
            isBigger = false;
        }
        return isBigger;
    }

    /**
     * 得到n天前的时间
     *
     * @param day
     * @return
     */
    public static Date getDateBefore(int day) {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
        return now.getTime();
    }

    public static String getDateBeforeToString(int day) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(getDateBefore(day));
    }

    public static String getStartDateBeforeToString(int day) {
        return getDateBeforeToString(day) + " 00:00:00";
    }

    public static String getEndDateBeforeToString(int day) {
        return getDateBeforeToString(day) + " 23:59:59";
    }

    /**
     * 获取 month 个月之前或者之后的日期
     * @param month 负数为之前 正数为之后
     */
    public static Date getDateBeforeForMonth(int month){
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MONTH, month);
        return now.getTime();
    }

    /**
     * 是否为当前月
     */
    public static boolean isCurrentMonth(String date) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
            return format.format(new Date()).equals(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 计算两个日期间隔年数
     *
     * @param str1
     * @param str2
     * @return
     */
    public static int getBetweenDateOfYears(String str1, String str2) {
        int year;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date beginDate = sdf.parse(str1);
            Date endDate = sdf.parse(str2);
            Calendar end = Calendar.getInstance();
            end.setTime(endDate);
            Calendar start = Calendar.getInstance();
            start.setTime(beginDate);
            if (start.after(end)) {
                return 0;
            } else {
                // 先计算年，再比较月，最后比较日期
                year = end.get(Calendar.YEAR) - start.get(Calendar.YEAR);
                if (end.get(Calendar.MONTH) >= start.get(Calendar.MONTH)) {
                    // 月份相同
                    if (end.get(Calendar.DATE) >= start.get(Calendar.DATE)) {
                        return year;
                    } else {
                        return year - 1;
                    }
                } else {
                    return year - 1;
                }
            }
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 检查发证日期是否大于当前日期
     *
     * @param beginDateStr
     * @return
     */
    public static boolean checkCardBeginDate(String beginDateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date();
        String dateStr = sdf.format(curDate);
        if (!DateUtils.isDateOneSmallerOrEquals(beginDateStr, dateStr)) {
            // 有效期开始日期不能晚于当前日期
            return true;
        }
        return false;
    }

    /**
     * 比较两个日期的大小，日期格式为yyyy-MM-dd
     * 前者比后者小或相等
     *
     * @param str1 the first date
     * @param str2 the second date
     * @return true <br/>false
     */
    public static boolean isDateOneSmallerOrEquals(String str1, String str2) {
        boolean isSmaller = false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dt1;
        Date dt2;
        try {
            dt1 = sdf.parse(str1);
            dt2 = sdf.parse(str2);
            if (dt1.getTime() <= dt2.getTime()) {
                isSmaller = true;
            } else if (dt1.getTime() > dt2.getTime()) {
                isSmaller = false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return isSmaller;
    }

    /**
     * 比较两个日期的大小，日期格式为yyyy-MM-dd HH:mm:ss
     * 前者比后者小或相等
     *
     * @param str1 the first date
     * @param str2 the second date
     * @return true <br/>false
     */
    public static boolean isDateOneSmallerOrEqualsYMDHMS(String str1, String str2) {
        boolean isSmaller = false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dt1;
        Date dt2;
        try {
            dt1 = sdf.parse(str1);
            dt2 = sdf.parse(str2);
            if (dt1.getTime() <= dt2.getTime()) {
                isSmaller = true;
            } else if (dt1.getTime() > dt2.getTime()) {
                isSmaller = false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return isSmaller;
    }

    /**
     * 是否为当日
     */
    public static boolean isCurrentDay(String date) {
        try {
            if (TextUtils.isEmpty(date)) {
                return false;
            }
            if (date.length() > 10) {
                date = date.substring(0, 10);
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            return format.format(new Date()).equals(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取两个时间的时间差 单位秒
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param format    格式
     * @return 时间差 秒
     */
    public static long getTimeDiff(String startTime, String endTime, String format) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.CHINA);
            Date startDate = dateFormat.parse(startTime);
            Date endDate = dateFormat.parse(endTime);
            return (endDate.getTime() - startDate.getTime()) / 1000;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 得到hours小时前的时间
     *
     * @param hours
     * @return
     */
    public static Date getDateHourBefore(int hours) {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY) - hours);
        return now.getTime();
    }
}
