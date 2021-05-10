package com.haylion.android.data.model;

 /**
  * @class  IncomeDetail
  * @description 收入信息
  * @date: 2019/12/17 10:24
  * @author: tandongdong
  */
public class IncomeDetail {

    private int type;
    private String date; //收支类型（0：收入，1：提现）
    private int paymentMode; //支付方式（支付方式:1-微信支付，2-支付宝支付，3-线下支付，4-银行卡支付，5-无）如果为提现，此项为空
    private double amount;

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    public int getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(int paymentMode) {
        this.paymentMode = paymentMode;
    }
}
