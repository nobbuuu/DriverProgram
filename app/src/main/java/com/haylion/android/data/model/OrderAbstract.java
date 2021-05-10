package com.haylion.android.data.model;

public class OrderAbstract {
    private String date;
    private int onlineTime;
    private int orderCompletionCount;
    private double income;
    private boolean showMonthView; //显示用，用来判断是否需要显示header月份

    public boolean isShowMonthView() {
        return showMonthView;
    }

    public void setShowMonthView(boolean showMonthView) {
        this.showMonthView = showMonthView;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setOnlineTime(int onlineTime) {
        this.onlineTime = onlineTime;
    }

    public int getOnlineTime() {
        return onlineTime;
    }

    public void setOrderCompletionCount(int orderCompletionCount) {
        this.orderCompletionCount = orderCompletionCount;
    }

    public int getOrderCompletionCount() {
        return orderCompletionCount;
    }

    public void setIncome(double income) {
        this.income = income;
    }

    public double getIncome() {
        return income;
    }

}