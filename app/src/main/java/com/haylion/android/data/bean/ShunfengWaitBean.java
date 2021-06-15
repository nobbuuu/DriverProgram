package com.haylion.android.data.bean;

public class ShunfengWaitBean {

    /**
     * id : 7
     * orderNo : 202105082144320010007
     * orderType : 0
     * depotStartAddress : 广东省深圳市福田区益田(地铁站)
     * depotStartLongitude : 22.516287
     * depotStartLatitude : 114.050987
     * takeTime : 12:00
     * depotEndAddress : 正宗原创鸡煲王(福永桥南店)
     * depotEndLongitude : 113.810493
     * depotEndLatitude : 22.689307
     * deliveryTime : 18:00
     * actualDeliveryTime : null
     * startDate : 2021-05-25 00:00:00
     * endDate : 2021-05-25 00:00:00
     * price : 14000
     * createTime : 2021-05-08 23:32:09
     */

    private int id;
    private String orderNo;
    private int orderType;
    private String depotStartAddress;
    private double depotStartLongitude;
    private double depotStartLatitude;
    private String takeTime;
    private String depotEndAddress;
    private double depotEndLongitude;
    private double depotEndLatitude;
    private String deliveryTime;
    private String actualDeliveryTime;
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

    public double getDepotStartLongitude() {
        return depotStartLongitude;
    }

    public void setDepotStartLongitude(double depotStartLongitude) {
        this.depotStartLongitude = depotStartLongitude;
    }

    public double getDepotStartLatitude() {
        return depotStartLatitude;
    }

    public void setDepotStartLatitude(double depotStartLatitude) {
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

    public double getDepotEndLongitude() {
        return depotEndLongitude;
    }

    public void setDepotEndLongitude(double depotEndLongitude) {
        this.depotEndLongitude = depotEndLongitude;
    }

    public double getDepotEndLatitude() {
        return depotEndLatitude;
    }

    public void setDepotEndLatitude(double depotEndLatitude) {
        this.depotEndLatitude = depotEndLatitude;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getActualDeliveryTime() {
        return actualDeliveryTime;
    }

    public void setActualDeliveryTime(String actualDeliveryTime) {
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
