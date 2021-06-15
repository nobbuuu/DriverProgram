package com.haylion.android.data.bean;

import java.util.List;

public class OrderDetailBean {

    /**
     * id : 119
     * orderCode : 202105250918205130004
     * status : 1
     * driverStatus:1
     * lineName : 货站A=货站B
     * depotStartName : 会展中心
     * depotStartAddress : 深圳会展中心
     * depotStartLongitude : 114.059812
     * depotStartLatitude : 22.530799
     * depotStartConcat1 : 联系人1
     * depotStartPhone1 : 18867003000
     * depotStartConcat2 : 联系人2
     * depotStartPhone2 : 18867003001
     * depotStartConcat3 : 联系人3
     * depotStartPhone3 : 18867003002
     * takeTime : 12:00
     * actualTakeTime : null
     * depotEndName : 新增货站
     * depotEndAddress : 深圳市鹏电工程咨询有限公司
     * depotEndLongitude : 114.102109
     * depotEndLatitude : 22.568048
     * depotEndConcat1 : Sdays
     * depotEndPhone1 : 15874784638
     * depotEndConcat2 : 杰
     * depotEndPhone2 : 15874784638
     * depotEndConcat3 : 阿杰
     * depotEndPhone3 : 15874784638
     * deliveryTime : 18:00
     * actualDeliveryTime : null
     * startDate : 2021-05-28 00:00:00
     * endDate : 2021-05-28 00:00:00
     * price : 14000
     * todaySumPrice : null
     * createTime : 2021-05-25 09:18:20
     * claimTime : 2021-05-28 14:08:37
     * beginTime : null
     * beginDate : null
     * driverId : 100027
     * driverName : 曹静
     * driverCarNumberStr : 粤B123456
     * pickupCode : null
     * complainList : null
     * cargoList : null
     * detailList : null
     */

    private int id;
    private String orderCode;
    private int status;
    private int driverStatus;
    private String lineName;
    private String depotStartName;
    private String depotStartAddress;
    private double depotStartLongitude;
    private double depotStartLatitude;
    private String depotStartConcat1;
    private String depotStartPhone1;
    private String depotStartConcat2;
    private String depotStartPhone2;
    private String depotStartConcat3;
    private String depotStartPhone3;
    private String takeTime;
    private Object actualTakeTime;
    private String depotEndName;
    private String depotEndAddress;
    private double depotEndLongitude;
    private double depotEndLatitude;
    private String depotEndConcat1;
    private String depotEndPhone1;
    private String depotEndConcat2;
    private String depotEndPhone2;
    private String depotEndConcat3;
    private String depotEndPhone3;
    private String deliveryTime;
    private String actualDeliveryTime;
    private String startDate;
    private String endDate;
    private double price;
    private double todaySumPrice;
    private String createTime;
    private String claimTime;
    private Object beginTime;
    private Object beginDate;
    private int driverId;
    private String driverName;
    private String driverCarNumberStr;
    private String pickupCode;
    private Object complainList;
    private List<String> cargoList;
    private Object detailList;

    public int getDriverStatus() {
        return driverStatus;
    }

    public void setDriverStatus(int driverStatus) {
        this.driverStatus = driverStatus;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public String getDepotStartName() {
        return depotStartName;
    }

    public void setDepotStartName(String depotStartName) {
        this.depotStartName = depotStartName;
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

    public String getDepotStartConcat1() {
        return depotStartConcat1;
    }

    public void setDepotStartConcat1(String depotStartConcat1) {
        this.depotStartConcat1 = depotStartConcat1;
    }

    public String getDepotStartPhone1() {
        return depotStartPhone1;
    }

    public void setDepotStartPhone1(String depotStartPhone1) {
        this.depotStartPhone1 = depotStartPhone1;
    }

    public String getDepotStartConcat2() {
        return depotStartConcat2;
    }

    public void setDepotStartConcat2(String depotStartConcat2) {
        this.depotStartConcat2 = depotStartConcat2;
    }

    public String getDepotStartPhone2() {
        return depotStartPhone2;
    }

    public void setDepotStartPhone2(String depotStartPhone2) {
        this.depotStartPhone2 = depotStartPhone2;
    }

    public String getDepotStartConcat3() {
        return depotStartConcat3;
    }

    public void setDepotStartConcat3(String depotStartConcat3) {
        this.depotStartConcat3 = depotStartConcat3;
    }

    public String getDepotStartPhone3() {
        return depotStartPhone3;
    }

    public void setDepotStartPhone3(String depotStartPhone3) {
        this.depotStartPhone3 = depotStartPhone3;
    }

    public String getTakeTime() {
        return takeTime;
    }

    public void setTakeTime(String takeTime) {
        this.takeTime = takeTime;
    }

    public Object getActualTakeTime() {
        return actualTakeTime;
    }

    public void setActualTakeTime(Object actualTakeTime) {
        this.actualTakeTime = actualTakeTime;
    }

    public String getDepotEndName() {
        return depotEndName;
    }

    public void setDepotEndName(String depotEndName) {
        this.depotEndName = depotEndName;
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

    public String getDepotEndConcat1() {
        return depotEndConcat1;
    }

    public void setDepotEndConcat1(String depotEndConcat1) {
        this.depotEndConcat1 = depotEndConcat1;
    }

    public String getDepotEndPhone1() {
        return depotEndPhone1;
    }

    public void setDepotEndPhone1(String depotEndPhone1) {
        this.depotEndPhone1 = depotEndPhone1;
    }

    public String getDepotEndConcat2() {
        return depotEndConcat2;
    }

    public void setDepotEndConcat2(String depotEndConcat2) {
        this.depotEndConcat2 = depotEndConcat2;
    }

    public String getDepotEndPhone2() {
        return depotEndPhone2;
    }

    public void setDepotEndPhone2(String depotEndPhone2) {
        this.depotEndPhone2 = depotEndPhone2;
    }

    public String getDepotEndConcat3() {
        return depotEndConcat3;
    }

    public void setDepotEndConcat3(String depotEndConcat3) {
        this.depotEndConcat3 = depotEndConcat3;
    }

    public String getDepotEndPhone3() {
        return depotEndPhone3;
    }

    public void setDepotEndPhone3(String depotEndPhone3) {
        this.depotEndPhone3 = depotEndPhone3;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public double getTodaySumPrice() {
        return todaySumPrice;
    }

    public void setTodaySumPrice(double todaySumPrice) {
        this.todaySumPrice = todaySumPrice;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getClaimTime() {
        return claimTime;
    }

    public void setClaimTime(String claimTime) {
        this.claimTime = claimTime;
    }

    public Object getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Object beginTime) {
        this.beginTime = beginTime;
    }

    public Object getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Object beginDate) {
        this.beginDate = beginDate;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverCarNumberStr() {
        return driverCarNumberStr;
    }

    public void setDriverCarNumberStr(String driverCarNumberStr) {
        this.driverCarNumberStr = driverCarNumberStr;
    }

    public String getPickupCode() {
        return pickupCode;
    }

    public void setPickupCode(String pickupCode) {
        this.pickupCode = pickupCode;
    }

    public Object getComplainList() {
        return complainList;
    }

    public void setComplainList(Object complainList) {
        this.complainList = complainList;
    }

    public List<String> getCargoList() {
        return cargoList;
    }

    public void setCargoList(List<String> cargoList) {
        this.cargoList = cargoList;
    }

    public Object getDetailList() {
        return detailList;
    }

    public void setDetailList(Object detailList) {
        this.detailList = detailList;
    }
}
