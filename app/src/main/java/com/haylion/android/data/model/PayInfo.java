package com.haylion.android.data.model;

public class PayInfo {
    private int orderId;
    private String carpoolCode;
    private double money; //打表费用
    private double otherMoney; //其他费用
    private double serviceAmount; //服务费用
    private int paymentMode;

    public static final int OFFLINE_PAY_CONFIRM_PAIED = 1;      //确认账单已支付
    public static final int OFFLINE_PAY_CONFIRM_NOT_PAIED = 2;  //确认账单未支付

    public double getServiceAmount() {
        return serviceAmount;
    }

    public void setServiceAmount(double serviceAmount) {
        this.serviceAmount = serviceAmount;
    }

    public double getOtherMoney() {
        return otherMoney;
    }

    public void setOtherMoney(double otherMoney) {
        this.otherMoney = otherMoney;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public int getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(int paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getCarpoolCode() {
        return carpoolCode;
    }

    public void setCarpoolCode(String carpoolCode) {
        this.carpoolCode = carpoolCode;
    }
}
