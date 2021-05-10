package com.haylion.android.data.model;

 /**
  * @class  IncomeGeneral
  * @description 业绩概览
  * @date: 2019/12/17 10:24
  * @author: tandongdong
  */
public class IncomeGeneral {

    private String yearMonth;
    private double onlineAmount;
    private double offlineAmount;

    public String getYearMonth() {
        return yearMonth;
    }

    public void setYearMonth(String yearMonth) {
        this.yearMonth = yearMonth;
    }

    public double getOnlineAmount() {
        return onlineAmount;
    }

    public void setOnlineAmount(double onlineAmount) {
        this.onlineAmount = onlineAmount;
    }

    public double getOfflineAmount() {
        return offlineAmount;
    }

    public void setOfflineAmount(double offlineAmount) {
        this.offlineAmount = offlineAmount;
    }
}
