package com.haylion.android.data.model;

 /**
  * @class  OrderDateSimple
  * @description 多日订单的信息
  * @date: 2019/12/17 10:33
  * @author: tandongdong
  */
public class OrderDateSimple {

    private int orderId;
    private String date;
    private int status;
    private int subStatus;

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getSubStatus() {
        return subStatus;
    }

    public void setSubStatus(int subStatus) {
        this.subStatus = subStatus;
    }
}