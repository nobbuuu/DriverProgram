package com.haylion.android.data.model;

import java.util.List;

/**
 * @class OrderDetail
 * @description 订单详情，和后台的订单详情接口对应。
 * @date: 2019/12/17 10:34
 * @author: tandongdong
 */
public class OrderDetail {
    private int driverId;
    private int orderId;
    private int cargoOrderId; //货拼客对应的货单
    private String orderCode; //订单编号
    private String onLocation;
    private String onLocationDescription;
    private String offLocation;
    private String offLocationDescription;
    private long totalDistance;
    private String mobile;       //真实号码
    private String virtualMobile; //虚拟号码
    private int orderType;
    private int channel; //订单来源
    private int orderStatus;
    private int orderSubStatus;
    private String createdTime;
    private String appointmentTime;
    private double passengerOnLat;
    private double passengerOnLon;
    private double passengerOffLat;
    private double passengerOffLon;
    private double amount;
    private double regularAmount;
    private double serviceAmount;
    private double otherAmount;
    private int passengerNum;
    private String orderCancellerMsg;
    private int orderCancellerType; //订单取消人类型（1：乘客，2：司机，3：系统, 4：后台管理）
    private int orderDuration;//秒
    private String cargoDescription;
    private String pickupContactName;
    private String pickupContactMobile;
    private String dropOffContactName;
    private String dropOffContactMobile;
    private String estimateArriveTime;  //预计到达时间
    private String estimatePickUpTime;  //司机预计到达上车点的时间
    private String boardingPlaceArriveTime;  //司机实际达到上车点时间
    private List<TrackLatLon> trackCoordinateList;  //车辆行驶轨迹 经纬度列表
    List<ChildInfo> childList;
    List<ChildInfo> guardianList;
    String childMobile;
    private int onAutoCheck;
    private int offAutoCheck;
    private String onPic;
    private long onPicTime; // 上车拍照时间
    private String offPic;
    private long offPicTime; // 下车拍照时间
    private String parentMessage;
    private double cancelAmount;   //送孩子上学 取消费金额

    //小孩订单专有
    private String startTime;
    private String endTime;

    public String getBoardingPlaceArriveTime() {
        return boardingPlaceArriveTime;
    }

    public void setBoardingPlaceArriveTime(String boardingPlaceArriveTime) {
        this.boardingPlaceArriveTime = boardingPlaceArriveTime;
    }

    public String getVirtualMobile() {
        return virtualMobile;
    }

    public void setVirtualMobile(String virtualMobile) {
        this.virtualMobile = virtualMobile;
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

    public double getCancelAmount() {
        return cancelAmount;
    }

    public void setCancelAmount(double cancelAmount) {
        this.cancelAmount = cancelAmount;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public int getCargoOrderId() {
        return cargoOrderId;
    }

    public void setCargoOrderId(int cargoOrderId) {
        this.cargoOrderId = cargoOrderId;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public List<ChildInfo> getGuardianList() {
        return guardianList;
    }

    public void setGuardianList(List<ChildInfo> guardianList) {
        this.guardianList = guardianList;
    }

    public String getParentMessage() {
        return parentMessage;
    }

    public void setParentMessage(String parentMessage) {
        this.parentMessage = parentMessage;
    }

    public int getOrderSubStatus() {
        return orderSubStatus;
    }

    public void setOrderSubStatus(int orderSubStatus) {
        this.orderSubStatus = orderSubStatus;
    }

    public int getOnAutoCheck() {
        return onAutoCheck;
    }

    public void setOnAutoCheck(int onAutoCheck) {
        this.onAutoCheck = onAutoCheck;
    }

    public int getOffAutoCheck() {
        return offAutoCheck;
    }

    public void setOffAutoCheck(int offAutoCheck) {
        this.offAutoCheck = offAutoCheck;
    }

    public String getOnPic() {
        return onPic;
    }

    public void setOnPic(String onPic) {
        this.onPic = onPic;
    }

    public String getOffPic() {
        return offPic;
    }

    public void setOffPic(String offPic) {
        this.offPic = offPic;
    }

    public List<ChildInfo> getChildList() {
        return childList;
    }

    public void setChildList(List<ChildInfo> childList) {
        this.childList = childList;
    }

    public String getChildMobile() {
        return childMobile;
    }

    public void setChildMobile(String childMobile) {
        this.childMobile = childMobile;
    }

    public List<TrackLatLon> getTrackCoordinateList() {
        return trackCoordinateList;
    }

    public void setTrackCoordinateList(List<TrackLatLon> trackCoordinateList) {
        this.trackCoordinateList = trackCoordinateList;
    }

    public String getEstimateArriveTime() {
        return estimateArriveTime;
    }

    public void setEstimateArriveTime(String estimateArriveTime) {
        this.estimateArriveTime = estimateArriveTime;
    }

    public String getEstimatePickUpTime() {
        return estimatePickUpTime;
    }

    public void setEstimatePickUpTime(String estimatePickUpTime) {
        this.estimatePickUpTime = estimatePickUpTime;
    }

    public String getCargoDescription() {
        return cargoDescription;
    }

    public void setCargoDescription(String cargoDescription) {
        this.cargoDescription = cargoDescription;
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

    public int getOrderDuration() {
        return orderDuration;
    }

    public void setOrderDuration(int orderDuration) {
        this.orderDuration = orderDuration;
    }

    public String getOrderCancellerMsg() {
        return orderCancellerMsg;
    }

    public void setOrderCancellerMsg(String orderCancellerMsg) {
        this.orderCancellerMsg = orderCancellerMsg;
    }

    public int getOrderCancellerType() {
        return orderCancellerType;
    }

    public void setOrderCancellerType(int orderCancellerType) {
        this.orderCancellerType = orderCancellerType;
    }

    public double getRegularAmount() {
        return regularAmount;
    }

    public void setRegularAmount(double regularAmount) {
        this.regularAmount = regularAmount;
    }

    public double getServiceAmount() {
        return serviceAmount;
    }

    public void setServiceAmount(double serviceAmount) {
        this.serviceAmount = serviceAmount;
    }

    public double getOtherAmount() {
        return otherAmount;
    }

    public void setOtherAmount(double otherAmount) {
        this.otherAmount = otherAmount;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getPassengerNum() {
        return passengerNum;
    }

    public void setPassengerNum(int passengerNum) {
        this.passengerNum = passengerNum;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOnLocation(String onLocation) {
        this.onLocation = onLocation;
    }

    public String getOnLocation() {
        return onLocation;
    }

    public void setOnLocationDescription(String onLocationDescription) {
        this.onLocationDescription = onLocationDescription;
    }

    public String getOnLocationDescription() {
        return onLocationDescription;
    }

    public void setOffLocation(String offLocation) {
        this.offLocation = offLocation;
    }

    public String getOffLocation() {
        return offLocation;
    }

    public void setOffLocationDescription(String offLocationDescription) {
        this.offLocationDescription = offLocationDescription;
    }

    public String getOffLocationDescription() {
        return offLocationDescription;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMobile() {
        return mobile;
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

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setPassengerOnLat(double passengerOnLat) {
        this.passengerOnLat = passengerOnLat;
    }

    public double getPassengerOnLat() {
        return passengerOnLat;
    }

    public void setPassengerOnLon(double passengerOnLon) {
        this.passengerOnLon = passengerOnLon;
    }

    public double getPassengerOnLon() {
        return passengerOnLon;
    }

    public void setPassengerOffLat(double passengerOffLat) {
        this.passengerOffLat = passengerOffLat;
    }

    public double getPassengerOffLat() {
        return passengerOffLat;
    }

    public void setPassengerOffLon(double passengerOffLon) {
        this.passengerOffLon = passengerOffLon;
    }

    public double getPassengerOffLon() {
        return passengerOffLon;
    }

    public long getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(long totalDistance) {
        this.totalDistance = totalDistance;
    }

    public long getOnPicTime() {
        return onPicTime;
    }

    public void setOnPicTime(long onPicTime) {
        this.onPicTime = onPicTime;
    }

    public long getOffPicTime() {
        return offPicTime;
    }

    public void setOffPicTime(long offPicTime) {
        this.offPicTime = offPicTime;
    }
}