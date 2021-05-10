package com.haylion.android.data.model;

import com.google.gson.annotations.SerializedName;

public class ShiftInfo {

    private String mobile = null; // 手机号码

    @SerializedName("shiftStartTime")
    private String startTime = null; // 早交接班时间
    @SerializedName("shiftEndTime")
    private String endTime = null;

    @SerializedName("secondShiftStartTime")
    private String secondStartTime = null; // 晚交接班时间
    @SerializedName("secondShiftEndTime")
    private String secondEndTime = null;

    @SerializedName("shiftAddressTitle")
    private String addressTitle = null; // 早交接班地址
    @SerializedName("shiftAddressDescription")
    private String addressDesc = null;

    @SerializedName("secondShiftAddressTitle")
    private String secondAddressTitle = null; // 晚交接班地址
    @SerializedName("secondShiftAddressDescription")
    private String secondAddressDesc = null;

    private String mailingAddress = null; // 通信地址
    private Double mailingAddressLat = null; // 通信地址纬度
    private Double mailingAddressLng = null; // 通信地址经度

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getSecondStartTime() {
        return secondStartTime;
    }

    public void setSecondStartTime(String secondStartTime) {
        this.secondStartTime = secondStartTime;
    }

    public String getSecondEndTime() {
        return secondEndTime;
    }

    public void setSecondEndTime(String secondEndTime) {
        this.secondEndTime = secondEndTime;
    }

    public String getAddressTitle() {
        return addressTitle;
    }

    public void setAddressTitle(String addressTitle) {
        this.addressTitle = addressTitle;
    }

    public String getAddressDesc() {
        return addressDesc;
    }

    public void setAddressDesc(String addressDesc) {
        this.addressDesc = addressDesc;
    }

    public String getSecondAddressTitle() {
        return secondAddressTitle;
    }

    public void setSecondAddressTitle(String secondAddressTitle) {
        this.secondAddressTitle = secondAddressTitle;
    }

    public String getSecondAddressDesc() {
        return secondAddressDesc;
    }

    public void setSecondAddressDesc(String secondAddressDesc) {
        this.secondAddressDesc = secondAddressDesc;
    }

    public String getMailingAddress() {
        return mailingAddress;
    }

    public void setMailingAddress(String mailingAddress) {
        this.mailingAddress = mailingAddress;
    }

    public double getMailingAddressLat() {
        return mailingAddressLat;
    }

    public void setMailingAddressLat(double mailingAddressLat) {
        this.mailingAddressLat = mailingAddressLat;
    }

    public double getMailingAddressLng() {
        return mailingAddressLng;
    }

    public void setMailingAddressLng(double mailingAddressLng) {
        this.mailingAddressLng = mailingAddressLng;
    }

}
