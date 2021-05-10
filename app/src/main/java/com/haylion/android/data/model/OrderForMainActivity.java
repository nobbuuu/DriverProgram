package com.haylion.android.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderForMainActivity {

    private int orderId;
    private String orderCode;
    private int orderType;
    private int orderStatus;
    private int orderSubStatus;
    private String onLocation;
    private String onLocationDescription;
    private String offLocationDescription;
    private String offLocation;
    private int carpoolNum;
    private String time;
    private double amount;
    private String estimateArriveTime;

    //小孩订单专有
    private List<String> childNameList;
    private String parentMessage;
    private boolean parentOrder = false;
    private String startTime;
    private String endTime;
    /**
     * 送你上学 - 拼车状态
     * 0-不拼单， 1-拼单中，2-拼单成功，3-拼单失败
     */
    private int carpoolStatus = -1;
    /**
     * 送你上学 - 拼车码
     */
    private String carpoolCode;

    private long totalDistance;      //全程距离
    private String mobile;      //电话

    /**
     * 多日预约订单增加字段。包括日期列表、上下车经纬度
     */
    private List<String> orderDates;
    private double onLat;
    private double onLng;
    private double offLat;
    private double offLng;

    /**
     * 订单创建时间
     */
    @SerializedName("createdAt")
    private String createdTime;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public boolean isParentOrder() {
        return parentOrder;
    }

    public void setParentOrder(boolean parentOrder) {
        this.parentOrder = parentOrder;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public int getOrderSubStatus() {
        return orderSubStatus;
    }

    public void setOrderSubStatus(int orderSubStatus) {
        this.orderSubStatus = orderSubStatus;
    }

    public List<String> getChildNameList() {
        return childNameList;
    }

    public void setChildNameList(List<String> childNameList) {
        this.childNameList = childNameList;
    }

    public String getParentMessage() {
        return parentMessage;
    }

    public void setParentMessage(String parentMessage) {
        this.parentMessage = parentMessage;
    }

    public String getEstimateArriveTime() {
        return estimateArriveTime;
    }

    public void setEstimateArriveTime(String estimateArriveTime) {
        this.estimateArriveTime = estimateArriveTime;
    }


    public String getOnLocationDescription() {
        return onLocationDescription;
    }

    public void setOnLocationDescription(String onLocationDescription) {
        this.onLocationDescription = onLocationDescription;
    }

    public String getOffLocationDescription() {
        return offLocationDescription;
    }

    public void setOffLocationDescription(String offLocationDescription) {
        this.offLocationDescription = offLocationDescription;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    public int getOrderType() {
        return orderType;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOnLocation(String onLocation) {
        this.onLocation = onLocation;
    }

    public String getOnLocation() {
        return onLocation;
    }

    public void setOffLocation(String offLocation) {
        this.offLocation = offLocation;
    }

    public String getOffLocation() {
        return offLocation;
    }

    public void setCarpoolNum(int carpoolNum) {
        this.carpoolNum = carpoolNum;
    }

    public int getCarpoolNum() {
        return carpoolNum;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
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

    public long getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(long totalDistance) {
        this.totalDistance = totalDistance;
    }

    public List<String> getOrderDates() {
        return orderDates;
    }

    public void setOrderDates(List<String> orderDates) {
        this.orderDates = orderDates;
    }

    public double getOnLat() {
        return onLat;
    }

    public void setOnLat(double onLat) {
        this.onLat = onLat;
    }

    public double getOnLng() {
        return onLng;
    }

    public void setOnLng(double onLng) {
        this.onLng = onLng;
    }

    public double getOffLat() {
        return offLat;
    }

    public void setOffLat(double offLat) {
        this.offLat = offLat;
    }

    public double getOffLng() {
        return offLng;
    }

    public void setOffLng(double offLng) {
        this.offLng = offLng;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public int getCarpoolStatus() {
        return carpoolStatus;
    }

    public void setCarpoolStatus(int carpoolStatus) {
        this.carpoolStatus = carpoolStatus;
    }

    public String getCarpoolCode() {
        return carpoolCode;
    }

    public void setCarpoolCode(String carpoolCode) {
        this.carpoolCode = carpoolCode;
    }

}