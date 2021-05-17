package com.haylion.android.data.model;

public class ShunfengBean {


    /**
     * id : 43
     * orderNo : 202105082234507470010
     * orderType : 0
     * depotStartAddress : 深圳市xx区xx街道
     * depotStartLongitude : 111.11
     * depotStartLatitude : 111.11
     * takeTime : 12:00:00
     * depotEndAddress : 深圳市xx区xx街道
     * depotEndLongitude : 111.11
     * depotEndLatitude : 111.11
     * deliveryTime : null
     * actualDeliveryTime : null
     * startDate : 2021-05-18 00:00:00
     * endDate : 2021-05-18 00:00:00
     * price : 14000
     * createTime : 2021-05-08 23:36:00
     */

    private int id;
    private String orderNo;
    private int orderType;
    private String depotStartAddress;
    private String depotStartLongitude;
    private String depotStartLatitude;
    private String takeTime;
    private String depotEndAddress;
    private String depotEndLongitude;
    private String depotEndLatitude;
    private Object deliveryTime;
    private Object actualDeliveryTime;
    private String startDate;
    private String endDate;
    private int price;
    private String createTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    public String getDepotStartAddress() {
        return depotStartAddress;
    }

    public void setDepotStartAddress(String depotStartAddress) {
        this.depotStartAddress = depotStartAddress;
    }

    public String getDepotStartLongitude() {
        return depotStartLongitude;
    }

    public void setDepotStartLongitude(String depotStartLongitude) {
        this.depotStartLongitude = depotStartLongitude;
    }

    public String getDepotStartLatitude() {
        return depotStartLatitude;
    }

    public void setDepotStartLatitude(String depotStartLatitude) {
        this.depotStartLatitude = depotStartLatitude;
    }

    public String getTakeTime() {
        return takeTime;
    }

    public void setTakeTime(String takeTime) {
        this.takeTime = takeTime;
    }

    public String getDepotEndAddress() {
        return depotEndAddress;
    }

    public void setDepotEndAddress(String depotEndAddress) {
        this.depotEndAddress = depotEndAddress;
    }

    public String getDepotEndLongitude() {
        return depotEndLongitude;
    }

    public void setDepotEndLongitude(String depotEndLongitude) {
        this.depotEndLongitude = depotEndLongitude;
    }

    public String getDepotEndLatitude() {
        return depotEndLatitude;
    }

    public void setDepotEndLatitude(String depotEndLatitude) {
        this.depotEndLatitude = depotEndLatitude;
    }

    public Object getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Object deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Object getActualDeliveryTime() {
        return actualDeliveryTime;
    }

    public void setActualDeliveryTime(Object actualDeliveryTime) {
        this.actualDeliveryTime = actualDeliveryTime;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
