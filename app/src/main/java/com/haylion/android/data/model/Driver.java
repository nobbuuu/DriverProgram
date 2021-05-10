package com.haylion.android.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * @class Driver
 * @description 司机信息
 * @date: 2019/12/17 10:21
 * @author: tandongdong
 */
public class Driver {
    @SerializedName("driverFirstName")
    private String firstName; //名
    @SerializedName("driverLastName")
    private String lastName; //姓
    private String code; //准许证号
    private String phoneNumber; //手机号码
    private String plateNumber; //车牌
    private String photoUrl; //头像图片

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
