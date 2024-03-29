package com.haylion.android.utils;

import android.util.Log;

import com.haylion.android.data.bean.DateLenthBean;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tiaozi
 * @date 2021/12/20
 * description
 */
public class DateUtils {
    public static DateLenthBean getTimeLenth(long time) {
        DateLenthBean bean = new DateLenthBean();
        long second = time / 1000;
        if (second < 60) {
            bean.setTime(second);
            bean.setUnit("秒");
        } else {
            long minute = second / 60;
            if (minute < 60) {
                long sSecond = second % 60;
                if (sSecond == 0) {
                    bean.setTime(minute);
                    bean.setUnit("分钟");
                } else {
                    bean.setTime(minute);
                    bean.setUnit("分钟");
                    bean.setTime1(sSecond);
                    bean.setUnit1("秒");
                }
            } else {
                long hour = minute / 60;
                if (hour < 24) {
                    long sMinute = minute % 60;
                    long lSecond = sMinute % 60;
                    if (sMinute != 0) {
                        bean.setTime(hour);
                        bean.setUnit("小时");
                        bean.setTime1(sMinute);
                        bean.setUnit1("分钟");
                    } else {
                        bean.setTime(hour);
                        bean.setUnit("小时");
                    }

                } else {
                    long day = hour / 24;
                    long sHour = hour % 24;
                    if (sHour != 0) {
                        long thour = sHour / 60;
                        long sMinute = minute % 60;
                        long lSecond = sMinute % 60;
                        if (sMinute != 0) {
                            bean.setTime(thour);
                            bean.setUnit("小时");
                            bean.setTime1(sMinute);
                            bean.setUnit1("分钟");
                        } else {
                            bean.setTime(thour);
                            bean.setUnit("小时");
                        }
                    } else {
                        bean.setTime(day);
                        bean.setUnit("天");
                    }
                }
            }
        }
        return bean;
    }

    public static String getTimeLenthStr(long time) {
        StringBuffer buffer = new StringBuffer();
        long second = time / 1000;
        if (second < 60) {
            buffer.append(second + "秒");
        } else {
            long minute = second / 60;
            Log.d("aaa", "minute = " + minute);
            if (minute < 60) {
                long sSecond = second % 60;
                if (sSecond == 0) {
                    buffer.append(minute + "分钟");
                } else {
                    buffer.append(minute + "分钟" + sSecond + "秒");
                }
            } else {
                long hour = minute / 60;
                Log.d("aaa", "hour = " + hour);
                if (hour < 24) {
                    long sMinute = minute % 60;
                    long lSecond = sMinute % 60;
                    if (sMinute != 0) {
                        if (lSecond != 0) {
//                            buffer.append(hour + "小时" + sMinute + "分钟" + lSecond + "秒");
                            buffer.append(hour + "小时" + sMinute + "分钟");
                        } else {
                            buffer.append(hour + "小时" + sMinute + "分钟");
                        }
                    } else {
                        buffer.append(hour + "小时");
                    }

                } else {
                    long day = hour / 24;
                    long sHour = hour % 24;
                    long minutt = sHour % 60;
                    Log.d("aaa", "sHour = " + sHour);
                    if (sHour != 0) {
                        long lSecond = minutt % 60;
                        if (minutt != 0) {
                            if (lSecond != 0) {
//                            buffer.append(day + "天" + hour + "小时" + sMinute + "分钟" + lSecond + "秒");
                                buffer.append(day + "天" + sHour + "小时" + minutt + "分钟");
                            } else {
                                buffer.append(day + "天" + sHour + "小时" + minutt + "分钟");
                            }
                        } else {
                            buffer.append(day + "天" + sHour + "小时");
                        }
                    } else {
                        if (minutt != 0) {
                            buffer.append(day + "天" + minutt + "分钟");
                        } else {
                            buffer.append(day + "天");
                        }
                    }
                }
            }
        }
        return buffer.toString();
    }
}
