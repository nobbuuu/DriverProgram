package com.haylion.android.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WalletTotal {
    private double balance;
    private int withdrawing;
    private List<BankCardOutputFormList> bankCardOutputFormList;

    @SerializedName("bindWechat")
    private boolean isBindWechat;

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getBalance() {
        return balance;
    }

    public void setWithdrawing(int withdrawing) {
        this.withdrawing = withdrawing;
    }

    public int getWithdrawing() {
        return withdrawing;
    }

    public void setBankCardOutputFormList(List<BankCardOutputFormList> bankCardOutputFormList) {
        this.bankCardOutputFormList = bankCardOutputFormList;
    }

    public List<BankCardOutputFormList> getBankCardOutputFormList() {
        return bankCardOutputFormList;
    }

    public boolean isBindWechat() {
        return isBindWechat;
    }

    public void setBindWechat(boolean bindWechat) {
        isBindWechat = bindWechat;
    }

}
