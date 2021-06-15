package com.haylion.android.data.bean;

import java.util.List;

public class ChangeOrderStatusBean {

    public ChangeOrderStatusBean(int orderId, int stateType, String url, List<String> cargoList) {
        this.orderId = orderId;
        this.stateType = stateType;
        this.url = url;
        this.cargoList = cargoList;
    }

    /**
     * orderId : 21
     * stateType : 2
     * url : https://pengcheng-test.oss-cn-shenzhen.aliyuncs.com/imgs/20210513224824305532.jpg
     * cargoList : ["111","222"]
     */


    private int orderId;
    private int stateType;
    private String url;
    private List<String> cargoList;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getStateType() {
        return stateType;
    }

    public void setStateType(int stateType) {
        this.stateType = stateType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getCargoList() {
        return cargoList;
    }

    public void setCargoList(List<String> cargoList) {
        this.cargoList = cargoList;
    }
}
