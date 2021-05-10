package com.haylion.android.data.util;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import com.haylion.android.data.model.Order;
import com.haylion.android.mvp.util.LogUtils;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BusinessUtils {

    /**
     * 格式化剩余到达时间
     *
     * @param estimateTime 剩余到达时间（单位：秒）
     */
    public static String formatEstimateTimeText(long estimateTime) {
        if (estimateTime > 60 * 60) {
            long hours = estimateTime / (60 * 60);
            long minutes = (estimateTime % (60 * 60)) / 60;
            if (minutes == 0) {
                return String.format(Locale.getDefault(), "%d小时", hours);
            } else {
                return String.format(Locale.getDefault(), "%d小时%d分钟", hours, minutes);
            }
        } else if (estimateTime > 60) {
            long minutes = estimateTime / 60;
            return String.format(Locale.getDefault(), "%d分钟", minutes);
        } else {
//            return "1分钟";
            return estimateTime + "秒";
        }
    }

    public enum PrecisionMode {
        SECOND,
        MINUTE,
//        HOURE
    }

    /**
     * 格式化剩余到达时间
     *
     * @param estimateTime 剩余到达时间（单位：秒）
     */
    public static String formatEstimateTimeText(long estimateTime, PrecisionMode precision) {
        if (estimateTime > 60 * 60) {
            long hours = estimateTime / (60 * 60);
            long minutes = (estimateTime % (60 * 60)) / 60;
            if (minutes == 0) {
                return String.format(Locale.getDefault(), "%d小时", hours);
            } else {
                return String.format(Locale.getDefault(), "%d小时%d分钟", hours, minutes);
            }
        } else if (estimateTime > 60) {
            long minutes = estimateTime / 60;
            return String.format(Locale.getDefault(), "%d分钟", minutes);
        } else {
//            return "1分钟";
            if (estimateTime > 0) {
                if (precision == PrecisionMode.SECOND) {
                    return estimateTime + "秒";
                } else {
                    return "1分";
                }
            } else {
                if (precision == PrecisionMode.SECOND) {
                    return "0秒";
                } else {
                    return "0分";
                }
            }

        }
    }


    /**
     * 格式化距离
     *
     * @param distance 距离（单位：米）
     */
    public static String formatDistance(double distance) {
        if (distance > 1000) {
            return String.format(Locale.getDefault(), "%.1f 公里", distance / 1000);
        } else {
            return String.format(Locale.getDefault(), "%.0f 米", distance);
        }
    }

    /**
     * 格式化距离
     *
     * @param distance 距离（单位：米）
     */
    public static String formatDistanceForVoice(double distance) {
        if (distance > 1000) {
            return String.format(Locale.getDefault(), "%.1f公里", distance / 1000);
        } else {
            return String.format(Locale.getDefault(), "%.0f米", distance);
        }
    }

    /**
     * 格式化距离 不带单位
     *
     * @param distance 距离（单位：米）
     * @return
     */
    public static String formatDistanceNoUnit(double distance) {
        if (distance > 1000) {
            return String.format(Locale.getDefault(), "%.1f", distance / 1000);
        } else {
            return String.format(Locale.getDefault(), "%.0f", distance);
        }
    }

    /**
     * 时间友好化
     *
     * @param millis 时间戳
     */
    public static String getFriendlyTime(final long millis) {
        long now = System.currentTimeMillis();
        long span = now - millis;
        if (span < 0) { // 前后端时间戳取法不一样？
            return "刚刚";
        }
        if (span < MIN) { // 一分钟内
            return "刚刚";
        }
        if (span < HOUR) {
            return String.format(Locale.getDefault(), "%d分钟前", span / MIN);
        }
        Calendar today = getCalendarOfToday();
        if (millis >= today.getTimeInMillis()) { // 今天
            return String.format(Locale.getDefault(), "%d小时前", span / HOUR);
        }
        if (millis >= today.getTimeInMillis() - DAY) { // 昨天
            return String.format("昨天%tR", millis);
        }
        today.set(Calendar.MONTH, 0);
        today.set(Calendar.DAY_OF_MONTH, 1);
        if (millis >= today.getTimeInMillis()) {
            return String.format(Locale.getDefault(), "%1$tm-%1$td", millis);
        }
        return String.format("%tF", millis);
    }

    /**
     * 今天  00:00:00
     */
    private static Calendar getCalendarOfToday() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    /**
     * 当线路没有实时数据时的文案
     */
    public static String getArrivalErrorText(String firstTime, String lastTime) {
        if (TextUtils.isEmpty(firstTime) || TextUtils.isEmpty(lastTime)) {
            return "时间数据为空";
        }
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        Calendar current = Calendar.getInstance();
        String currentTime = dateFormat.format(current.getTime());

        Calendar first = Calendar.getInstance();
        try {
            first.setTime(dateFormat.parse(firstTime));
            first.add(Calendar.MINUTE, -30);
        } catch (ParseException e) {
            e.printStackTrace();
            return "首班车时间有误";
        }
        String deltaFirst = dateFormat.format(first.getTime());
        if (currentTime.compareTo(deltaFirst) >= 0 && currentTime.compareTo(firstTime) < 0) {
            return "首班车尚未发车";
        }

        Calendar last = Calendar.getInstance();
        try {
            last.setTime(dateFormat.parse(lastTime));
            last.add(Calendar.HOUR_OF_DAY, 1);
        } catch (ParseException e) {
            e.printStackTrace();
            return "末班车时间有误";
        }
        String deltaLast = dateFormat.format(last.getTime());

        int compareResult = firstTime.compareTo(lastTime);
        if (compareResult > 0) {  // 首班车时间字面上更大，比如 06:00:00-00:00:00
            if (currentTime.compareTo(deltaLast) >= 0 && currentTime.compareTo(deltaFirst) < 0) {
                return "非运营时间";
            }
        } else if (compareResult < 0) { // 首班车车时间字面上更小，比如 06:00:00-23:00:00
            if (deltaLast.compareTo(firstTime) <= 0) { // 跨天了
                if (currentTime.compareTo(deltaLast) >= 0 && currentTime.compareTo(deltaFirst) < 0) {
                    return "非运营时间";
                }
            } else { // 还是同一天
                if ((currentTime.compareTo(deltaLast) >= 0 && currentTime.compareTo("24:00:00") < 0) ||
                        (currentTime.compareTo("00:00:00") >= 0 && currentTime.compareTo(deltaFirst) < 0)) {
                    return "非运营时间";
                }
            }

        } else { // 首末班车时间一样，常见于高快，例如 06:30:00-06:30:00
            if ((currentTime.compareTo(deltaLast) >= 0 && currentTime.compareTo("24:00:00") < 0) ||
                    (currentTime.compareTo("00:00:00") >= 0 && currentTime.compareTo(deltaFirst) < 0)) {
                return "非运营时间";
            }
        }
        return "暂未发车";
    }


    /**
     * 时间戳转成提示性日期格式（昨天、今天……)
     */
    public static String getDateToString(long milSecond, String pattern) {
        Date date = new Date(milSecond);
        SimpleDateFormat format;
        String hintDate = "";
        //先获取年份
        int year = Integer.parseInt(new SimpleDateFormat("yyyy").format(date));
        //获取一年中的第几天
        int day = Integer.parseInt(new SimpleDateFormat("d").format(date));
        //获取当前年份 和 一年中的第几天
        Date currentDate = new Date(System.currentTimeMillis());
        int currentYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(currentDate));
        int currentDay = Integer.parseInt(new SimpleDateFormat("d").format(currentDate));
        //计算 如果是去年的
        if (currentYear - year == 1) {
            //如果当前正好是 1月1日 计算去年有多少天，指定时间是否是一年中的最后一天
            if (currentDay == 1) {
                int yearDay;
                if (year % 400 == 0) {
                    yearDay = 366;//世纪闰年
                } else if (year % 4 == 0 && year % 100 != 0) {
                    yearDay = 366;//普通闰年
                } else {
                    yearDay = 365;//平年
                }
                if (day == yearDay) {
                    hintDate = "昨天";
                }
            }
        } else {
            hintDate = getDateHint(date);
           /* if (currentDay - day == 1) {
                hintDate = "昨天";
            }
            if (currentDay - day == 0) {
                hintDate = "今天";
            }
            if (currentDay - day == -1) {
                hintDate = "明天";
            }*/
        }
        if (TextUtils.isEmpty(hintDate)) {
            format = new SimpleDateFormat("MM月dd日 HH:mm");
            return format.format(date);
        } else {
            format = new SimpleDateFormat("HH:mm");
            return hintDate + " " + format.format(date);
        }

    }

    /**
     * 时间戳转成提示性日期格式（昨天、今天……),跨年是需要展示年信息, 如果是今天，则只显示小时和分，其他则不显示小时和分。
     */
    public static String getDateToStringOnlyHourWhenToday(long milSecond) {
        Date date = new Date(milSecond);
        SimpleDateFormat format;
        String hintDate = "";
        //先获取年份
        int year = Integer.parseInt(new SimpleDateFormat("yyyy").format(date));
        //获取一年中的第几天
        int day = Integer.parseInt(new SimpleDateFormat("d").format(date));
        //获取当前年份 和 一年中的第几天
        Date currentDate = new Date(System.currentTimeMillis());
        int currentYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(currentDate));
        int currentDay = Integer.parseInt(new SimpleDateFormat("d").format(currentDate));
        //计算 如果是去年的
        if (currentYear - year == 1) {
            //如果当前正好是 1月1日 计算去年有多少天，指定时间是否是一年中的最后一天
            if (currentDay == 1) {
                int yearDay;
                if (year % 400 == 0) {
                    yearDay = 366;//世纪闰年
                } else if (year % 4 == 0 && year % 100 != 0) {
                    yearDay = 366;//普通闰年
                } else {
                    yearDay = 365;//平年
                }
                if (day == yearDay) {
                    hintDate = "昨天";
                }
            }
        } else {
            hintDate = getDateHint(date);
        }
        if (TextUtils.isEmpty(hintDate)) {
            if (currentYear == year) {
                format = new SimpleDateFormat("MM月dd日");
            } else {
                format = new SimpleDateFormat("yyyy年MM月dd日");
            }
            return format.format(date);
        } else {
            if ("今天".equals(hintDate)) {
                format = new SimpleDateFormat("HH:mm");
                return format.format(date);
            } else {
                return hintDate;
            }
        }
    }

    /**
     * 时间戳转成提示性日期格式（昨天、今天……),跨年是需要展示年信息
     */
    public static String getDateToStringIncludeYearWhenCrossYear(long milSecond, String pattern) {
        Date date = new Date(milSecond);
        SimpleDateFormat format;
        String hintDate = "";
        //先获取年份
        int year = Integer.parseInt(new SimpleDateFormat("yyyy").format(date));
        //获取一年中的第几天
        int day = Integer.parseInt(new SimpleDateFormat("d").format(date));
        //获取当前年份 和 一年中的第几天
        Date currentDate = new Date(System.currentTimeMillis());
        int currentYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(currentDate));
        int currentDay = Integer.parseInt(new SimpleDateFormat("d").format(currentDate));
        //计算 如果是去年的
        if (currentYear - year == 1) {
            //如果当前正好是 1月1日 计算去年有多少天，指定时间是否是一年中的最后一天
            if (currentDay == 1) {
                int yearDay;
                if (year % 400 == 0) {
                    yearDay = 366;//世纪闰年
                } else if (year % 4 == 0 && year % 100 != 0) {
                    yearDay = 366;//普通闰年
                } else {
                    yearDay = 365;//平年
                }
                if (day == yearDay) {
                    hintDate = "昨天";
                }
            }
        } else {
            hintDate = getDateHint(date);
            /*if (currentDay - day == 1) {
                hintDate = "昨天";
            }
            if (currentDay - day == 0) {
                hintDate = "今天";
            }
            if (currentDay - day == -1) {
                hintDate = "明天";
            }*/
        }
        if (TextUtils.isEmpty(hintDate)) {
            if (currentYear == year) {
                format = new SimpleDateFormat("MM月dd日 HH:mm");
            } else {
                format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
            }
            return format.format(date);
        } else {
            format = new SimpleDateFormat("HH:mm");
            return hintDate + " " + format.format(date);
        }

    }

    /**
     * 时间戳转成提示性日期格式（昨天、今天……),包含年信息
     */
    public static String getDateToStringIncludeYear(long milSecond, String pattern) {
        Date date = new Date(milSecond);
        SimpleDateFormat format;
        String hintDate = "";
        //先获取年份
        int year = Integer.parseInt(new SimpleDateFormat("yyyy").format(date));
        //获取一年中的第几天
        int day = Integer.parseInt(new SimpleDateFormat("d").format(date));
        //获取当前年份 和 一年中的第几天
        Date currentDate = new Date(System.currentTimeMillis());
        int currentYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(currentDate));
        int currentDay = Integer.parseInt(new SimpleDateFormat("d").format(currentDate));
        //计算 如果是去年的
        if (currentYear - year == 1) {
            //如果当前正好是 1月1日 计算去年有多少天，指定时间是否是一年中的最后一天
            if (currentDay == 1) {
                int yearDay;
                if (year % 400 == 0) {
                    yearDay = 366;//世纪闰年
                } else if (year % 4 == 0 && year % 100 != 0) {
                    yearDay = 366;//普通闰年
                } else {
                    yearDay = 365;//平年
                }
                if (day == yearDay) {
                    hintDate = "昨天";
                }
            }
        } else {
            hintDate = getDateHint(date);
            /*if (currentDay - day == 1) {
                hintDate = "昨天";
            }
            if (currentDay - day == 0) {
                hintDate = "今天";
            }
            if (currentDay - day == -1) {
                hintDate = "明天";
            }*/
        }
        if (TextUtils.isEmpty(hintDate)) {
            format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
            return format.format(date);
        } else {
            format = new SimpleDateFormat("HH:mm");
            return hintDate + " " + format.format(date);
        }

    }

    /**
     * 时间戳转成提示性日期格式（昨天、今天……) 只获取日期信息，不获取时分
     */
    public static String getDateString(long milSecond, String pattern) {
        Date date = new Date(milSecond);
        SimpleDateFormat format;
        String hintDate = "";
        //先获取年份
        int year = Integer.parseInt(new SimpleDateFormat("yyyy").format(date));
        //获取一年中的第几天
        int day = Integer.parseInt(new SimpleDateFormat("d").format(date));
        //获取当前年份 和 一年中的第几天
        Date currentDate = new Date(System.currentTimeMillis());
        int currentYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(currentDate));
        int currentDay = Integer.parseInt(new SimpleDateFormat("d").format(currentDate));
        //计算 如果是去年的
        if (currentYear - year == 1) {
            //如果当前正好是 1月1日 计算去年有多少天，指定时间是否是一年中的最后一天
            if (currentDay == 1) {
                int yearDay;
                if (year % 400 == 0) {
                    yearDay = 366;//世纪闰年
                } else if (year % 4 == 0 && year % 100 != 0) {
                    yearDay = 366;//普通闰年
                } else {
                    yearDay = 365;//平年
                }
                if (day == yearDay) {
                    hintDate = "昨天";
                }
            }
        } else {
            hintDate = getDateHint(date);
            /*if (currentDay - day == 1) {
                hintDate = "昨天";
            }
            if (currentDay - day == 0) {
                hintDate = "今天";
            }
            if (currentDay - day == -1) {
                hintDate = "明天";
            }*/
        }
        if (TextUtils.isEmpty(hintDate)) {
            format = new SimpleDateFormat("MM月dd日 HH:mm");
            return format.format(date).substring(0, 6);
        } else {
            return hintDate;
        }

    }

    /**
     * 时间戳转成提示性日期格式（昨天、今天……) 只获取日期信息，不获取时分--跨年需要显示年信息
     */
    public static String getDateStringOnlyDay(long milSecond, String pattern) {
        Date date = new Date(milSecond);
        SimpleDateFormat format;
        String hintDate = "";
        //先获取年份
        int year = Integer.parseInt(new SimpleDateFormat("yyyy").format(date));
        //获取一年中的第几天
        int day = Integer.parseInt(new SimpleDateFormat("d").format(date));
        //获取当前年份 和 一年中的第几天
        Date currentDate = new Date(System.currentTimeMillis());
        int currentYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(currentDate));
        int currentDay = Integer.parseInt(new SimpleDateFormat("d").format(currentDate));
        //计算 如果是去年的
        if (currentYear - year == 1) {
            //如果当前正好是 1月1日 计算去年有多少天，指定时间是否是一年中的最后一天
            if (currentDay == 1) {
                int yearDay;
                if (year % 400 == 0) {
                    yearDay = 366;//世纪闰年
                } else if (year % 4 == 0 && year % 100 != 0) {
                    yearDay = 366;//普通闰年
                } else {
                    yearDay = 365;//平年
                }
                if (day == yearDay) {
                    hintDate = "昨天";
                }
            }
        } else {
            hintDate = getDateHint(date);
            /*if (currentDay - day == 1) {
                hintDate = "昨天";
            }
            if (currentDay - day == 0) {
                hintDate = "今天";
            }
            if (currentDay - day == -1) {
                hintDate = "明天";
            }*/
        }
        if (TextUtils.isEmpty(hintDate)) {
            if (currentYear == year) {
                format = new SimpleDateFormat("MM月dd日 HH:mm");
                return format.format(date).substring(0, 6);
            } else {
                format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
                return format.format(date).substring(0, 11);
            }
        } else {
            return hintDate;
        }

    }

    /**
     * 时间戳转成提示性日期格式, 只获取日期信息
     */
    public static String getDateStringOnlyMonthAndDay(long milSecond, String pattern) {
        Date date = new Date(milSecond);
        SimpleDateFormat format;
        format = new SimpleDateFormat("MM月dd日");
        return format.format(date);

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

    //正则表达式
    public static boolean isNumber(String str) {
        Log.d("isNumber", "" + str);
        String reg = "^[0-9]*(.[0-9]+)?$";
        return str.matches(reg);
    }

    public static String formatDouble(double d) {
        NumberFormat nf = NumberFormat.getInstance();
        //设置保留多少位小数
        nf.setMaximumFractionDigits(2);
        // 取消科学计数法
        nf.setGroupingUsed(false);
        //返回结果
        return nf.format(d);
    }

    public static final int MSEC = 1;
    public static final int SEC = 1000;
    public static final int MIN = 60000;
    public static final int HOUR = 3600000;
    public static final int DAY = 86400000;


    public static String getDateToString2(long milSecond, String pattern) {
        Date date = new Date(milSecond);
        SimpleDateFormat format;
        String hintDate = "";
        //先获取年份
        int year = Integer.parseInt(new SimpleDateFormat("yyyy").format(date));
        //获取一年中的第几天
        int day = Integer.parseInt(new SimpleDateFormat("d").format(date));
        //获取当前年份 和 一年中的第几天
        Date currentDate = new Date(System.currentTimeMillis());
        int currentYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(currentDate));
        int currentDay = Integer.parseInt(new SimpleDateFormat("d").format(currentDate));
        //计算 如果是去年的
        if (currentYear - year == 1) {
            //如果当前正好是 1月1日 计算去年有多少天，指定时间是否是一年中的最后一天
            if (currentDay == 1) {
                int yearDay;
                if (year % 400 == 0) {
                    yearDay = 366;//世纪闰年
                } else if (year % 4 == 0 && year % 100 != 0) {
                    yearDay = 366;//普通闰年
                } else {
                    yearDay = 365;//平年
                }
                if (day == yearDay) {
                    hintDate = "昨天";
                }
            }
        } else {
            hintDate = getDateHint(date);
            /*if (currentDay - day == 1) {
                hintDate = "昨天";
            }
            if (currentDay - day == 0) {
                hintDate = "今天";
            }
            if (currentDay - day == -1) {
                hintDate = "明天";
            }*/
        }
        if (TextUtils.isEmpty(hintDate)) {
            format = new SimpleDateFormat("MM-dd HH:mm");
            return format.format(date);
        } else {
            format = new SimpleDateFormat("HH:mm");
            return hintDate + " " + format.format(date);
        }
    }

    /**
     * 根据时间戳 只获取 HH：mm
     *
     * @param milSecond
     * @return
     */
    public static String getDataHm(long milSecond) {
        Date date = new Date(milSecond);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(date);
    }

    /**
     * 全局金钱显示的样式
     * 保留2位小数，不足两位，用0补充。
     *
     * @param d
     * @return
     */
    @SuppressLint("DefaultLocale")
    public static String moneySpec(double d) {
        return String.format("%.2f", d);
    }


    /**
     * 判断日期是今天，昨天 还是明天。如果都不是，返回""。
     *
     * @param date
     * @return
     */
    public static String getDateHint(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String hintDate = "";
        String dateStr = format.format(date);
        if (dateStr.equals(DateUtils.getCurrentDate())) {
            hintDate = "今天";
        } else if (dateStr.equals(DateUtils.getYesterdayDate())) {
            hintDate = "昨天";
        } else if (dateStr.equals(DateUtils.getTomorrowDate())) {
            hintDate = "明天";
        }
        return hintDate;
    }
}
