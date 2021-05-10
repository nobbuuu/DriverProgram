package com.haylion.android.data.model;

public class UserInfo {
    private String name;
    private String nickName;
    private String phoneNum; //真实的号码
    private String virtualNum; //虚拟号码

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
