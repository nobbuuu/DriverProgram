package com.haylion.android.data.model;

import java.util.List;

 /**
  * @class  OrderHistory
  * @description 订单历史--对应后台的历史订单列表接口
  * @date: 2019/12/17 10:36
  * @author: tandongdong
  */
public class OrderHistory {

    private int orderId;
    private String orderCode;
    private int orderType;
    private int channel;
    private int orderStatus;
    private int orderSubStatus;
    private String onPlace;
    private String offPlace;
    private String onPlaceDescription;
    private String offPlaceDescription;
    private String createdDate;
    private String mobile;
    private int carpoolNumber;
    private String appointmentDate;
    private String actualOnTime;
    private String actualOffTime;
    private String estimatedOnTime;
    private String estimatedOffTime;
    private int tripDuration;
    private double orderAmount;
    private int orderCancellerType;
    private String orderCancellerMessage;
    private String sfOrderId;
    private String pickupContactName;
    private String pickupContactMobile;
    private String dropOffContactName;
    private String dropOffContactMobile;
    private String goodsDescription;

    //送小孩专用字段
    private List<String> childNameList;
    private String parentMessage;

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
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

    public String getSfOrderId() {
        return sfOrderId;
    }

    public void setSfOrderId(String sfOrderId) {
        this.sfOrderId = sfOrderId;
    }

    public String getPickupContactName() {
        return pickupContactName;
    }

    public void setPickupContactName(String pickupContactName) {
        this.pickupContactName = pickupContactName;
    }

    public String getPickupContactMobile() {
        return pickupContactMobile;
    }

    public void setPickupContactMobile(String pickupContactMobile) {
        this.pickupContactMobile = pickupContactMobile;
    }

    public String getDropOffContactName() {
        return dropOffContactName;
    }

    public void setDropOffContactName(String dropOffContactName) {
        this.dropOffContactName = dropOffContactName;
    }

    public String getDropOffContactMobile() {
        return dropOffContactMobile;
    }

    public void setDropOffContactMobile(String dropOffContactMobile) {
        this.dropOffContactMobile = dropOffContactMobile;
    }

    public String getGoodsDescription() {
        return goodsDescription;
    }

    public void setGoodsDescription(String goodsDescription) {
        this.goodsDescription = goodsDescription;
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

    public void setOnPlace(String onPlace) {
        this.onPlace = onPlace;
    }

    public String getOnPlace() {
        return onPlace;
    }

    public void setOffPlace(String offPlace) {
        this.offPlace = offPlace;
    }

    public String getOffPlace() {
        return offPlace;
    }

    public void setOnPlaceDescription(String onPlaceDescription) {
        this.onPlaceDescription = onPlaceDescription;
    }

    public String getOnPlaceDescription() {
        return onPlaceDescription;
    }

    public void setOffPlaceDescription(String offPlaceDescription) {
        this.offPlaceDescription = offPlaceDescription;
    }

    public String getOffPlaceDescription() {
        return offPlaceDescription;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMobile() {
        return mobile;
    }

    public void setCarpoolNumber(int carpoolNumber) {
        this.carpoolNumber = carpoolNumber;
    }

    public int getCarpoolNumber() {
        return carpoolNumber;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setActualOnTime(String actualOnTime) {
        this.actualOnTime = actualOnTime;
    }

    public String getActualOnTime() {
        return actualOnTime;
    }

    public void setActualOffTime(String actualOffTime) {
        this.actualOffTime = actualOffTime;
    }

    public String getActualOffTime() {
        return actualOffTime;
    }

    public void setEstimatedOnTime(String estimatedOnTime) {
        this.estimatedOnTime = estimatedOnTime;
    }

    public String getEstimatedOnTime() {
        return estimatedOnTime;
    }

    public void setEstimatedOffTime(String estimatedOffTime) {
        this.estimatedOffTime = estimatedOffTime;
    }

    public String getEstimatedOffTime() {
        return estimatedOffTime;
    }

    public void setTripDuration(int tripDuration) {
        this.tripDuration = tripDuration;
    }

    public int getTripDuration() {
        return tripDuration;
    }

    public void setOrderAmount(double orderAmount) {
        this.orderAmount = orderAmount;
    }

    public double getOrderAmount() {
        return orderAmount;
    }

    public void setOrderCancellerType(int orderCancellerType) {
        this.orderCancellerType = orderCancellerType;
    }

    public int getOrderCancellerType() {
        return orderCancellerType;
    }

    public void setOrderCancellerMessage(String orderCancellerMessage) {
        this.orderCancellerMessage = orderCancellerMessage;
    }

    public String getOrderCancellerMessage() {
        return orderCancellerMessage;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
}