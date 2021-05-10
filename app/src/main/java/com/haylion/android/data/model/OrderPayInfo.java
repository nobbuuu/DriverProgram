package com.haylion.android.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class OrderPayInfo implements Parcelable {

    private boolean carpoolOrder = false; // 拼车单标记

    private int orderId; //订单ID
    private String carpoolCode = null; // 拼车码
    private int orderType; //订单类型
    private int orderChannel; //订单来源
    private double cost; //费用

    public OrderPayInfo() {
    }

    protected OrderPayInfo(Parcel in) {
        carpoolOrder = in.readByte() != 0;
        orderId = in.readInt();
        carpoolCode = in.readString();
        orderType = in.readInt();
        orderChannel = in.readInt();
        cost = in.readDouble();
    }

    public static final Creator<OrderPayInfo> CREATOR = new Creator<OrderPayInfo>() {
        @Override
        public OrderPayInfo createFromParcel(Parcel in) {
            return new OrderPayInfo(in);
        }

        @Override
        public OrderPayInfo[] newArray(int size) {
            return new OrderPayInfo[size];
        }
    };

    public boolean isCarpoolOrder() {
        return carpoolOrder;
    }

    public void setCarpoolOrder(boolean carpoolOrder) {
        this.carpoolOrder = carpoolOrder;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getCarpoolCode() {
        return carpoolCode;
    }

    public void setCarpoolCode(String carpoolCode) {
        this.carpoolCode = carpoolCode;
    }

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    public int getOrderChannel() {
        return orderChannel;
    }

    public void setOrderChannel(int orderChannel) {
        this.orderChannel = orderChannel;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "OrderPayInfo{" +
                "carpoolOrder=" + carpoolOrder +
                ", orderId=" + orderId +
                ", carpoolCode='" + carpoolCode + '\'' +
                ", orderType=" + orderType +
                ", orderChannel=" + orderChannel +
                ", cost=" + cost +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (carpoolOrder ? 1 : 0));
        dest.writeInt(orderId);
        dest.writeString(carpoolCode);
        dest.writeInt(orderType);
        dest.writeInt(orderChannel);
        dest.writeDouble(cost);
    }

}
