package com.haylion.android.data.model;

public class UserInfo {
    private String name;
    private String nickName;

    //取货电话
    private String phoneNum;
    private String phoneNum2;
    private String phoneNum3;

    //收货电话
    private String receivePhone1;
    private String receivePhone2;
    private String receivePhone3;
    private String virtualNum; //虚拟号码


    public String getReceivePhone1() {
        return receivePhone1;
    }

    public void setReceivePhone1(String receivePhone1) {
        this.receivePhone1 = receivePhone1;
    }

    public String getReceivePhone2() {
        return receivePhone2;
    }

    public void setReceivePhone2(String receivePhone2) {
        this.receivePhone2 = receivePhone2;
    }

    public String getReceivePhone3() {
        return receivePhone3;
    }

    public void setReceivePhone3(String receivePhone3) {
        this.receivePhone3 = receivePhone3;
    }

    public String getPhoneNum2() {
        return phoneNum2;
    }

    public void setPhoneNum2(String phoneNum2) {
        this.phoneNum2 = phoneNum2;
    }

    public String getPhoneNum3() {
        return phoneNum3;
    }

    public void setPhoneNum3(String phoneNum3) {
        this.phoneNum3 = phoneNum3;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getVirtualNum() {
        return virtualNum;
    }

    public void setVirtualNum(String virtualNum) {
        this.virtualNum = virtualNum;
    }

}
