package com.haylion.android.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * 钱包账单记录
 */
public class Transaction {

    public static final int TYPE_INCOME = 0; // 收入
    public static final int TYPE_WITHDRAW = 1; // 提现

    private int type;

    @SerializedName("paymentMode")
    private int mode;

    private String date;

    private double amount;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

}
