package com.haylion.android.data.model;


import java.util.List;


public class WebsocketOrderListData {

    private List<Integer> orderIds;

    public void setOrderIds(List<Integer> orderIds) {
        this.orderIds = orderIds;
    }

    public List<Integer> getOrderIds() {
        return orderIds;
    }

}