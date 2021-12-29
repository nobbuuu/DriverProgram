package com.haylion.android.data.bean;

/**
 * @author tiaozi
 * @date 2021/12/20
 * description
 */
public class DateLenthBean {
    private long time;
    private String unit;
    private long time1;
    private String unit1;

    @Override
    public String toString() {
        return "DateLenthBean{" +
                "time=" + time +
                ", unit='" + unit + '\'' +
                ", time1=" + time1 +
                ", unit1='" + unit1 + '\'' +
                '}';
    }

    public long getTime1() {
        return time1;
    }

    public void setTime1(long time1) {
        this.time1 = time1;
    }

    public String getUnit1() {
        return unit1;
    }

    public void setUnit1(String unit1) {
        this.unit1 = unit1;
    }

    public DateLenthBean(long time, String unit) {
        this.time = time;
        this.unit = unit;
    }
    public DateLenthBean() {
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
