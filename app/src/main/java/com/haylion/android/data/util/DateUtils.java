package com.haylion.android.data.util;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Author:wangjianming
 * Time:2018/12/29
 * Description:计算时间的工具类
 */
public class DateUtils {

    public static final String FORMAT_PATTERN_YMDHMS = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_PATTERN_YMDHM = "yyyy-MM-dd HH:mm";

    /**
     * @throws Exception
     * @功能：获取几个月后或者几个月前的日期
     * @param：mon:负数为往前，正数为往后
     * @return：
     */
    public static String getToMonDate(int mon) {
        // 定义日期格式
        SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        // 将date类型转换为BigDecimal类型（该类型对应oracle中的number类型）
        // 获取当前时间的前6个月
        calendar.add(Calendar.MONTH, mon);
        Date date02 = calendar.getTime();
        return matter.format(date02);
    }

    /**
     * @throws Exception
     * @功能：获取时间 yyyy-mm-dd
     * @param：
     * @return：
     */
    public static String getYearDayOne(long day) {
        // 定义日期格式
        SimpleDateFormat atter = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date(day);
        return atter.format(date);
    }

    /**
     * @throws Exception
     * @功能：获取时间 yyyy-mm-dd
     * @param：
     * @return：
     */
    public static String getYearDayTwo(long day) {
        // 定义日期格式
        SimpleDateFormat atter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(day);
        return atter.format(date);
    }

    public static String getAllDay(long day) {
        // 定义日期格式
        SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd   HH:mm:ss");
        Date date = new Date(day);
        return matter.format(date);
    }

    /**
     * @throws Exception
     * @功能：获取时间 yyyy-mm-dd
     * @param：
     * @return：
     */
    public static String getYearDayTwo(Date day) {
        // 定义日期格式
        SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd");
        return matter.format(day);
    }

    public static String getAllDay(Date day) {
        // 定义日期格式
        SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return matter.format(day);
    }

    /**
     * 将yyyy-MM-dd HH:mm:ss转换为yyyy年MM月dd日 HH:mm
     *
     * @param s
     * @return
     */
    public static String getStringToAllDay(String s) {
        SimpleDateFormat matter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        Date date = strToDateLong(s);
        return matter.format(date);
    }

    /**
     * 将yyyy-MM-dd HH:mm:ss转换为yyyy年MM月dd日 HH:mm
     *
     * @param s
     * @return
     */
    public static String getStringToMonAndDay(String s) {
        SimpleDateFormat matter = new SimpleDateFormat("MM月dd日 HH:mm");
        Date date = strToDateLong(s);
        return matter.format(date);
    }

    /**
     * @throws Exception
     * @功能：获取时间 hh:mm
     * @param：
     * @return：
     */
    public static String getTimeHHmm(long day) {
        // 定义日期格式
        SimpleDateFormat matter = new SimpleDateFormat("HH:mm");
        Date date = new Date(day);
        return matter.format(date);
    }

    /**
     * @throws Exception
     * @功能：获取时间 hh:mm:ss
     * @param：
     * @return：
     */
    public static String getTimeHHmmss(long day) {
        // 定义日期格式
        SimpleDateFormat matter = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(day);
        return matter.format(date);
    }

    /**
     * @param：
     * @return：
     */
    public static String getTimemmss(long day) {
        // 定义日期格式
        SimpleDateFormat matter = new SimpleDateFormat("mm:ss");
        Date date = new Date(day);
        return matter.format(date);
    }


    /**
     * @throws Exception
     * @功能：计算相差的天数
     * @param：
     * @return：
     */
    public static long daysOfTwo(long date1, long date2) {

        Date smdate = new Date(date1);
        Date bdate = new Date(date2);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            smdate = sdf.parse(sdf.format(smdate));
            bdate = sdf.parse(sdf.format(bdate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();
        long between_days = (time1 - time2) / (1000 * 3600 * 24);

        return between_days;

    }

    /**
     * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm
     *
     * @param strDate
     * @return
     */
    public static Date strToDateLong(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * 获取当前年份
     *
     * @return
     */
    public static int getCurrentYear() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.YEAR);
    }

    /**
     * 获取当前月份
     *
     * @return
     */
    public static int getCurrentMONTH() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取当前日期
     *
     * @return
     */
    public static int getCurrentDay() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.DATE);
    }

    /**
     * 获取当前分钟
     *
     * @return
     */
    public static int currentMin() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.MINUTE);
    }

    /**
     * 获取当前小时
     *
     * @return
     */
    public static int currentHour() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取今天日期
     *
     * @return
     */
    public static String getCurrentDate() {
        // 定义日期格式
        SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd");
        return matter.format(new Date());
    }

    /**
     * 获取系统当前时间
     *
     * @return
     */
    public static Date getCurrentSystemDate() {
        return new Date();
    }


    /**
     * 获取今天日期MM-DD
     *
     * @return
     */
    public static String getCurrentDateMMDD() {
        // 定义日期格式
        SimpleDateFormat matter = new SimpleDateFormat("MM月dd日");
        return matter.format(new Date());
    }

    /**
     * 获取昨天日期
     *
     * @return
     */
    public static String getYesterdayDate() {
        // 定义日期格式
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(cal.DATE, -1);
        SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd");
        return matter.format(cal.getTime());
    }

    /**
     * 获取明天日期
     *
     * @return
     */
    public static String getTomorrowDate() {
        // 定义日期格式
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(cal.DATE, 1);
        SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd");
        return matter.format(cal.getTime());
    }

    /**
     * 获取明天日期MMDD
     *
     * @return
     */
    public static String getTomorrowDateMMDD() {
        // 定义日期格式
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(cal.DATE, 1);
        SimpleDateFormat matter = new SimpleDateFormat("MM月dd日");
        return matter.format(cal.getTime());
    }

    /**
     * 获取后天日期
     *
     * @return
     */
    public static String getAfterTomorrowDate() {
        // 定义日期格式
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(cal.DATE, 2);
        SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd");
        return matter.format(cal.getTime());
    }

    /**
     * 获取后天日期MMDD
     *
     * @return
     */
    public static String getAfterTomorrowDateMMDD() {
        // 定义日期格式
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(cal.DATE, 2);
        SimpleDateFormat matter = new SimpleDateFormat("MM月dd日");
        return matter.format(cal.getTime());
    }

    /**
     * 获取多少天后的日期yyyy-MM-dd
     *
     * @return
     */
    public static String getAfterSomeDateYYMMDD(int someDay) {
        // 定义日期格式
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(cal.DATE, someDay);
        SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd");
        return matter.format(cal.getTime());
    }

    /**
     * 获取多少天后的日期yyyy年MM月dd日
     *
     * @return
     */
    public static String getAfterSomeDateYMD(int someDay) {
        // 定义日期格式
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(cal.DATE, someDay);
        SimpleDateFormat matter = new SimpleDateFormat("yyyy年MM月dd日");
        return matter.format(cal.getTime());
    }

    /**
     * 判断多少天后是否是当月
     *
     * @param someDay
     * @return
     */
    public static boolean isCurrentMonth(int someDay) {
        if (getCurrentMONTH() == Integer.parseInt(getAfterSomeDateMM(someDay))) {
            return true;
        }
        return false;
    }

    /**
     * 获取多少天后的日期dd
     *
     * @return
     */
    public static String getAfterSomeDateDD(int someDay) {
        // 定义日期格式
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(cal.DATE, someDay);
        SimpleDateFormat matter = new SimpleDateFormat("dd");
        return matter.format(cal.getTime());
    }

    /**
     * 获取多少天后的月份
     *
     * @return
     */
    public static String getAfterSomeDateMM(int someDay) {
        // 定义日期格式
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(cal.DATE, someDay);
        SimpleDateFormat matter = new SimpleDateFormat("MM");
        return matter.format(cal.getTime());
    }

    /**
     * 根据毫秒获取格式化后的时间 大于1小时显示hh:mm:ss 小于1小时显示mm:ss
     *
     * @param time
     * @return
     */
    public static String getHHmmssOrmmssForLong(long time) {
        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;

        long hour = (time) / hh;
        long minute = (time - hour * hh) / mi;
        long second = (time - hour * hh - minute * mi) / ss;

        String strHour = hour < 10 ? "0" + hour : "" + hour;
        String strMinute = minute < 10 ? "0" + minute : "" + minute;
        String strSecond = second < 10 ? "0" + second : "" + second;
        if (hour > 0) {
            return strHour + ":" + strMinute + ":" + strSecond;
        } else {
            return strMinute + ":" + strSecond;
        }
    }


    public static void main(String[] args) {
        System.out.print(getHHmmssOrmmssForLong(640000));
    }






}
