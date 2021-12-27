package com.haylion.android.data.model;

import com.amap.api.maps.model.LatLng;

import java.util.List;

/**
 * @class Order
 * @description 订单信息类，网络接口返回的数据，统一转换成本地定义的订单类。
 * @date: 2019/12/17 10:27
 * @author: tandongdong
 */
public class Order {
    //订单类型
    public static final int ORDER_TYPE_REALTIME_CARPOOL = 1; //实时拼车单
    public static final int ORDER_TYPE_REALTIME = 2; //实时订单
    public static final int ORDER_TYPE_BOOK = 3; //预约不拼车
    public static final int ORDER_TYPE_CARGO = 4; //货单
    public static final int ORDER_TYPE_CARGO_PASSENGER = 5; //货拼客中的客单
    public static final int ORDER_TYPE_SEND_CHILD = 6; // 送小孩上学单
    public static final int ORDER_TYPE_ACCESSIBILITY = 7; // 女性专车订单
    public static final int ORDER_TYPE_SHUNFENG = 8; // 顺丰订单
    public static final int ORDER_TYPE_BOOK_CARPOOL = 99; //预约拼车，预留，暂时未用到

    //订单状态
    public static final int ORDER_STATUS_INIT = 6; // 初始状态，未接单
    public static final int ORDER_STATUS_READY = 3; // 预约订单，存在的状态。在时间到达前，会进入状态 7
    public static final int ORDER_STATUS_WAIT_CAR = 7; // 等待司机接驾
    public static final int ORDER_STATUS_ARRIVED_START_ADDR = 8; //到达上车点，等待乘客上车
    public static final int ORDER_STATUS_GET_ON = 4; //乘客已上车
    public static final int ORDER_STATUS_GET_OFF = 9; //乘客已下车
    public static final int ORDER_STATUS_WAIT_PAY = 2; // 待支付
    public static final int ORDER_STATUS_UNSTART = 0; // 顺丰单未开始

    public static final int ORDER_STATUS_CLOSED = 1; //订单已完成
    public static final int ORDER_STATUS_CANCELED = 5; //订单已取消

    //订单子状态 	订单子状态(8-0 等待乘客上车 8-1等待确认 9-0到达终点 9-1等待确认 9-2确认完成 2-0待支付 2-1待支付（争议）)
    public static final int ORDER_SUB_STATUS_ARRIVED_START_ADDR = 0; // 8-0
    public static final int ORDER_SUB_STATUS_ARRIVED_START_ADDR_WAIT_PARENT_CONFIRM = 1; // 8-1
    public static final int ORDER_SUB_STATUS_GET_OFF = 0; // 9-0
    public static final int ORDER_SUB_STATUS_GET_OFF_WAIT_PARENT_CONFIRM = 1; // 9-1
    public static final int ORDER_SUB_STATUS_GET_OFF_PARENT_CONFIRMED = 2; // 9-2
    public static final int ORDER_SUB_STATUS_CLOSED = 0; // 1-0
    public static final int ORDER_SUB_STATUS_CLOSED_COMPLAIN = 1; // 1-1 争议订单
    public static final int ORDER_SUB_STATUS_WAIT_PAY_CANCAL_FEE = 1; //2-1 取消费待支付
    public static final int ORDER_SUB_STATUS_MEITUAN_GRABING = 1; //6-1 美团订单抢单中
    //
    public static final int ORDER_SUB_STATUS_CLOSED_ABNORMAL = 2; //1-2 异常结束
    public static final int ORDER_SUB_STATUS_WAIT_PAY_ABNORMAL = 2; //2-2 异常结束待支付

    //订单来源
    public static final int ORDER_CHANNEL_MEITUAN = 1; //美团
    public static final int ORDER_CHANNEL_PENGCHENG = 0; //鹏电+货单

    private int orderId; //订单ID
    private int cargoOrderId; //货拼客单对应的货单ID

    private String orderCode; //订单编号
    private OrderCostInfo costDetail; //费用详情
    private UserInfo userInfo;    //乘客信息
    private AddressInfo startAddr;  //上车点地址
    private AddressInfo endAddr;    //下车点地址
    private int orderType; //订单类型
    private int channel; //订单来源
    private int orderStatus;    //订单状态
    private int orderSubStatus;  //订单子状态(8-0 等待乘客上车 8-1等待确认 9-0到达终点 9-1等待确认 9-2确认完成 2-0待支付 2-1待支付（争议）)
    private String actualGetOnTime;
    private String actualGetOffTime;
    private String orderTime; //预约时间
    private int totalTime;    //总耗时
    private int passengerNum;  //乘客数量
    private double totalMoney;  //拼车单-总价格
    private double distance;      //全程距离
    private long costTime;      //行程时间
    private long distanceFromCar;  //距离车的距离
    private String orderCancellerMsg;  //订单取消原因
    private int orderCancellerType;    //订单取消类型
    private String headerNameDisplay;
    private String estimateArriveTime;  //预计货物送达时间 时间格式  YYYY-MM-DD hh:mm
    private String estimatePickUpTime;  //司机预计到达上车点时间  时间格式  YYYY-MM-DD hh:mm
    private String boardingPlaceArriveTime;  //司机实际达到上车点时间 时间格式  YYYY-MM-DD hh:mm
    private String takeTime;  //取货时间
    private String deliveryTime;  //货物送达时间
    private String actualDeliveryTime;  //货物实际送达时间

    private String sfOrderId;
    private String pickupContactName;   //取货人姓名 (司机去此人处取货)
    private String pickupContactName1;   //取货人姓名 (司机去此人处取货)
    private String pickupContactName2;   //取货人姓名 (司机去此人处取货)
    private String pickupContactMobile;  //取货人手机号
    private String pickupContactMobile1;  //取货人手机号
    private String pickupContactMobile2;  //取货人手机号
    private String pickupCode;  //取货码
    private String dropOffContactName;    //送货人姓名（司机把货物送到此人手上）
    private String dropOffContactMobile;  //送货人手机号
    private String goodsDescription;       //货物描述
    private long minPreservedSeconds;

    //小孩订单专有
    private List<ChildInfo> childList;    //小孩信息
    private List<ChildInfo> guardianList; //陪乘人信息
    private String parentMessage; //附言
    private int getOnAutoCheck; //出发免确认（0：需要确认 1：免确认）
    private int getOffAutoCheck; //到达免确认（0：需要确认 1：免确认）
    private String getOnPicUrl; //上车照片url
    private long onPicTime; // 上车拍照时间
    private String getOffPicUrl; //下车照片url
    private long offPicTime; // 下车拍照时间
    private boolean isParentOrder; //是否为父订单
    /**
     * 送你上学 - 拼车状态
     * 0-不拼单， 1-拼单中，2-拼单成功，3-拼单失败
     */
    private boolean carpoolOrder = false;
    /**
     * 送你上学 - 拼车码
     */
    private String carpoolCode;

    private String startTime; //开始时间
    private String endTime;   //结束时间

    private boolean orderTypeheaderDisplay;  //显示用

    private double cancelAmount;   //送孩子上学 取消费金额

    private List<LatLng> trackCoordinateList;   //订单轨迹

    private int roadLevel; //顺路得分 （货拼客单用）
    private int amountLevel; //收入得分（货拼客单用）
    private int starLevel; //推荐星级（货拼客单用）

    /**
     * 日期列表，多日预约订单增加字段
     */
    private List<String> orderDates;

    public String getTakeTime() {
        return takeTime;
    }


    public String getActualDeliveryTime() {
        return actualDeliveryTime;
    }

    public void setActualDeliveryTime(String actualDeliveryTime) {
        this.actualDeliveryTime = actualDeliveryTime;
    }

    public void setTakeTime(String takeTime) {
        this.takeTime = takeTime;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getBoardingPlaceArriveTime() {
        return boardingPlaceArriveTime;
    }

    public void setBoardingPlaceArriveTime(String boardingPlaceArriveTime) {
        this.boardingPlaceArriveTime = boardingPlaceArriveTime;
    }

    public String getPickupContactMobile1() {
        return pickupContactMobile1;
    }

    public void setPickupContactMobile1(String pickupContactMobile1) {
        this.pickupContactMobile1 = pickupContactMobile1;
    }

    public String getPickupContactMobile2() {
        return pickupContactMobile2;
    }

    public void setPickupContactMobile2(String pickupContactMobile2) {
        this.pickupContactMobile2 = pickupContactMobile2;
    }

    public String getPickupContactName1() {
        return pickupContactName1;
    }

    public void setPickupContactName1(String pickupContactName1) {
        this.pickupContactName1 = pickupContactName1;
    }

    public String getPickupContactName2() {
        return pickupContactName2;
    }

    public void setPickupContactName2(String pickupContactName2) {
        this.pickupContactName2 = pickupContactName2;
    }

    public int getRoadLevel() {
        return roadLevel;
    }

    public void setRoadLevel(int roadLevel) {
        this.roadLevel = roadLevel;
    }

    public int getAmountLevel() {
        return amountLevel;
    }

    public void setAmountLevel(int amountLevel) {
        this.amountLevel = amountLevel;
    }

    public int getStarLevel() {
        return starLevel;
    }

    public void setStarLevel(int starLevel) {
        this.starLevel = starLevel;
    }

    public double getCancelAmount() {
        return cancelAmount;
    }

    public void setCancelAmount(double cancelAmount) {
        this.cancelAmount = cancelAmount;
    }

    public String getEstimatePickUpTime() {
        return estimatePickUpTime;
    }

    public String getPickupCode() {
        return pickupCode;
    }

    public void setPickupCode(String pickupCode) {
        this.pickupCode = pickupCode;
    }

    public void setEstimatePickUpTime(String estimatePickUpTime) {
        this.estimatePickUpTime = estimatePickUpTime;
    }

    public long getCostTime() {
        return costTime;
    }

    public void setCostTime(long costTime) {
        this.costTime = costTime;
    }

    public int getCargoOrderId() {
        return cargoOrderId;
    }

    public void setCargoOrderId(int cargoOrderId) {
        this.cargoOrderId = cargoOrderId;
    }

    public List<ChildInfo> getGuardianList() {
        return guardianList;
    }

    public void setGuardianList(List<ChildInfo> guardianList) {
        this.guardianList = guardianList;
    }

    public OrderCostInfo getCostDetail() {
        return costDetail;
    }

    public void setCostDetail(OrderCostInfo costDetail) {
        this.costDetail = costDetail;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public boolean isOrderTypeheaderDisplay() {
        return orderTypeheaderDisplay;
    }

    public void setOrderTypeheaderDisplay(boolean orderTypeheaderDisplay) {
        this.orderTypeheaderDisplay = orderTypeheaderDisplay;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public int getOrderSubStatus() {
        return orderSubStatus;
    }

    public void setOrderSubStatus(int orderSubStatus) {
        this.orderSubStatus = orderSubStatus;
    }

    public int getGetOnAutoCheck() {
        return getOnAutoCheck;
    }

    public void setGetOnAutoCheck(int getOnAutoCheck) {
        this.getOnAutoCheck = getOnAutoCheck;
    }

    public int getGetOffAutoCheck() {
        return getOffAutoCheck;
    }

    public void setGetOffAutoCheck(int getOffAutoCheck) {
        this.getOffAutoCheck = getOffAutoCheck;
    }

    public String getGetOnPicUrl() {
        return getOnPicUrl;
    }

    public void setGetOnPicUrl(String getOnPicUrl) {
        this.getOnPicUrl = getOnPicUrl;
    }

    public String getGetOffPicUrl() {
        return getOffPicUrl;
    }

    public void setGetOffPicUrl(String getOffPicUrl) {
        this.getOffPicUrl = getOffPicUrl;
    }

    public List<ChildInfo> getChildList() {
        return childList;
    }

    public void setChildList(List<ChildInfo> childList) {
        this.childList = childList;
    }

    public String getParentMessage() {
        return parentMessage;
    }

    public void setParentMessage(String parentMessage) {
        this.parentMessage = parentMessage;
    }


    public List<LatLng> getTrackCoordinateList() {
        return trackCoordinateList;
    }

    public void setTrackCoordinateList(List<LatLng> trackCoordinateList) {
        this.trackCoordinateList = trackCoordinateList;
    }

    public long getMinPreservedSeconds() {
        return minPreservedSeconds;
    }

    public void setMinPreservedSeconds(long minPreservedSeconds) {
        this.minPreservedSeconds = minPreservedSeconds;
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

    public String getEstimateArriveTime() {
        return estimateArriveTime;
    }

    public void setEstimateArriveTime(String estimateArriveTime) {
        this.estimateArriveTime = estimateArriveTime;
    }

    public String getHeaderNameDisplay() {
        return headerNameDisplay;
    }

    public void setHeaderNameDisplay(String headerNameDisplay) {
        this.headerNameDisplay = headerNameDisplay;
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

    public long getDistanceFromCar() {
        return distanceFromCar;
    }

    public void setDistanceFromCar(long distanceFromCar) {
        this.distanceFromCar = distanceFromCar;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public AddressInfo getStartAddr() {
        return startAddr;
    }

    public void setStartAddr(AddressInfo startAddr) {
        this.startAddr = startAddr;
    }

    public AddressInfo getEndAddr() {
        return endAddr;
    }

    public void setEndAddr(AddressInfo endAddr) {
        this.endAddr = endAddr;
    }

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getActualGetOnTime() {
        return actualGetOnTime;
    }

    public void setActualGetOnTime(String actualGetOnTime) {
        this.actualGetOnTime = actualGetOnTime;
    }

    public String getActualGetOffTime() {
        return actualGetOffTime;
    }

    public void setActualGetOffTime(String actualGetOffTime) {
        this.actualGetOffTime = actualGetOffTime;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public int getPassengerNum() {
        return passengerNum;
    }

    public void setPassengerNum(int passengerNum) {
        this.passengerNum = passengerNum;
    }

    public double getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(double totalMoney) {
        this.totalMoney = totalMoney;
    }

    public boolean isParentOrder() {
        return isParentOrder;
    }

    public void setParentOrder(boolean parentOrder) {
        isParentOrder = parentOrder;
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

    public List<String> getOrderDates() {
        return orderDates;
    }

    public void setOrderDates(List<String> orderDates) {
        this.orderDates = orderDates;
    }

    public boolean isCarpoolOrder() {
        return carpoolOrder;
    }

    public void setCarpoolOrder(boolean carpoolOrder) {
        this.carpoolOrder = carpoolOrder;
    }

    public String getCarpoolCode() {
        return carpoolCode;
    }

    public void setCarpoolCode(String carpoolCode) {
        this.carpoolCode = carpoolCode;
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
