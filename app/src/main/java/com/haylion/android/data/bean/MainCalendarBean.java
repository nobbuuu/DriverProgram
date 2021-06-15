package com.haylion.android.data.bean;

import java.util.Date;

/**
 * Created by Administrator on 2017/11/22 0022.
 */
public class MainCalendarBean {
    private Date date;
    private boolean isWork;
    private boolean isBlack;
    private boolean isSelect;

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public boolean isBlack() {
        return isBlack;
    }

    public void setBlack(boolean black) {
        isBlack = black;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isWork() {
        return isWork;
    }

    public void setWork(boolean work) {
        isWork = work;
    }
}
