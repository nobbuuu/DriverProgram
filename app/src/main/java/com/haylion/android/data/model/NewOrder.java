package com.haylion.android.data.model;

/**
 * 调用下一个订单时返回的类
 */
public class NewOrder {
/*    id	int	订单id
    orderType	int	订单类型。1-实时拼单订单、2-实时拼单订单、3-预约订单
    boardingPlace	string	出发点名称
    boardingAddress	string	出发点地址
    arrivalPlace	string	目的地名称
    arrivalAddress	string	目的地地址
    totalDistance	long	总距离
    boardingLongitude	decimal	上车点经度
    boardingLatitude	decimal	上车点纬度
    arrivalLongitude	decimal	下车点经度
    arrivalLatitude	decimal	下车点纬度
    startTime	date	订单开始时间(type为预约订单时)*/

    private int id;
    private int orderType;
    private int channel;
    private String boardingPlace;
    private String boardingAddress;
    private String arrivalPlace;
    private String arrivalAddress;
    private long totalDistance;
    private double boardingLongitude;
    private double boardingLatitude;
    private double arrivalLongitude;
    private double arrivalLatitude;
    private String startTime;
    private int carpoolNumber;
    private double orderAmount;
    private long minPreservedSeconds;

    private int roadLevel; //顺路得分 （货拼客单用）
    private int amountLevel; //收入得分（货拼客单用）
    private int starLevel; //推荐星级（货拼客单用）

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

    public long getMinPreservedSeconds() {
        return minPreservedSeconds;
    }

    public void setMinPreservedSeconds(long minPreservedSeconds) {
        this.minPreservedSeconds = minPreservedSeconds;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public double getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(double orderAmount) {
        this.orderAmount = orderAmount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    public String getBoardingPlace() {
        return boardingPlace;
    }

    public void setBoardingPlace(String boardingPlace) {
        this.boardingPlace = boardingPlace;
    }

    public String getBoardingAddress() {
        return boardingAddress;
    }

    public void setBoardingAddress(String boardingAddress) {
        this.boardingAddress = boardingAddress;
    }

    public String getArrivalPlace() {
        return arrivalPlace;
    }

    public void setArrivalPlace(String arrivalPlace) {
        this.arrivalPlace = arrivalPlace;
    }

    public String getArrivalAddress() {
        return arrivalAddress;
    }

    public void setArrivalAddress(String arrivalAddress) {
        this.arrivalAddress = arrivalAddress;
    }

    public long getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(long totalDistance) {
        this.totalDistance = totalDistance;
    }

    public double getBoardingLongitude() {
        return boardingLongitude;
    }

    public void setBoardingLongitude(double boardingLongitude) {
        this.boardingLongitude = boardingLongitude;
    }

    public double getBoardingLatitude() {
        return boardingLatitude;
    }

    public void setBoardingLatitude(double boardingLatitude) {
        this.boardingLatitude = boardingLatitude;
    }

    public double getArrivalLongitude() {
        return arrivalLongitude;
    }

    public void setArrivalLongitude(double arrivalLongitude) {
        this.arrivalLongitude = arrivalLongitude;
    }

    public double getArrivalLatitude() {
        return arrivalLatitude;
    }

    public void setArrivalLatitude(double arrivalLatitude) {
        this.arrivalLatitude = arrivalLatitude;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getCarpoolNumber() {
        return carpoolNumber;
    }

    public void setCarpoolNumber(int carpoolNumber) {
        this.carpoolNumber = carpoolNumber;
    }

    @Override
    public String toString() {
        return "NewOrder{" +
                "id=" + id +
                ", orderType=" + orderType +
                ", channel=" + channel +
                ", boardingPlace='" + boardingPlace + '\'' +
                ", boardingAddress='" + boardingAddress + '\'' +
                ", arrivalPlace='" + arrivalPlace + '\'' +
                ", arrivalAddress='" + arrivalAddress + '\'' +
                ", totalDistance=" + totalDistance +
                ", boardingLongitude=" + boardingLongitude +
                ", boardingLatitude=" + boardingLatitude +
                ", arrivalLongitude=" + arrivalLongitude +
                ", arrivalLatitude=" + arrivalLatitude +
                ", startTime='" + startTime + '\'' +
                ", carpoolNumber=" + carpoolNumber +
                ", orderAmount=" + orderAmount +
                ", minPreservedSeconds=" + minPreservedSeconds +
                ", roadLevel=" + roadLevel +
                ", amountLevel=" + amountLevel +
                ", starLevel=" + starLevel +
                '}';
    }
}
