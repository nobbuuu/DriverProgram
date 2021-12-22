package com.haylion.android.data.bean;

/**
 * @author tiaozi
 * @date 2021/12/20
 * description
 */
public class DateLenthBean {
    private long time;
    private String unit;

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
