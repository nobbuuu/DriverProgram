package com.haylion.android.data.model;

import java.util.List;

public class LoginData {

    private String token;
    private String userCode;
    private List<Vehicle> bindVehicleList;
    private int latestUsedVehicleId; //最近一次使用的听单车辆id（默认值为-1）

    public LoginData(String token, String userCode) {
        this.token = token;
        this.userCode = userCode;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public List<Vehicle> getBindVehicleList() {
        return bindVehicleList;
    }

    public void setBindVehicleList(List<Vehicle> bindVehicleList) {
        this.bindVehicleList = bindVehicleList;
    }

    public int getLatestUsedVehicleId() {
        return latestUsedVehicleId;
    }

    public void setLatestUsedVehicleId(int latestUsedVehicleId) {
        this.latestUsedVehicleId = latestUsedVehicleId;
    }

    @Override
    public String toString() {
        return "LoginData{" +
                "token='" + token + '\'' +
                ", userCode='" + userCode + '\'' +
                ", bindVehicleList=" + bindVehicleList +
                ", latestUsedVehicleId=" + latestUsedVehicleId +
                '}';
    }
}

