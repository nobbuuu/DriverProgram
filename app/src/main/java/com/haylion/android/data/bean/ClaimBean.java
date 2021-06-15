package com.haylion.android.data.bean;

import java.util.List;

public class ClaimBean {

    public ClaimBean(int orderId, List<String> grabDateList) {
        this.orderId = orderId;
        this.grabDateList = grabDateList;
    }

    /**
     * orderId : 2
     * grabDateList : ["2021-05-09 00:00:00","2021-05-10 00:00:00"]
     */



    private int orderId;
    private List<String> grabDateList;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public List<String> getGrabDateList() {
        return grabDateList;
    }

    public void setGrabDateList(List<String> grabDateList) {
        this.grabDateList = grabDateList;
    }
}
