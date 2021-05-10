package com.haylion.android.data.model;

public class ListenOrderSetting {

/*    data.driverId	Integer	司机id
    data.voiceBroadcast	Short	听单播报开关（0：关，1：开）
    data.departureOnly	Short	只听出车预约单（0：关，1：开）
    data.receiveOnly	Short	只听收车单（0：关，1：开）
    data.occupiedTime	Short	用车时间（0：全部，1：实时，2：预约）
    data.carpool	Short	是否拼单（0：全部，1：是，2：否)
    data.receivingRange	Double	实时听单范围*/

    private int driverId;
    private int voiceBroadcast;
    private int departureOnly;
    private int receiveOnly;
    private int occupiedTime;
    private int carpool;
    private double receivingRange;
    private int cargoPassengerServiceOn;
    private int cargoPassengerServiceStatus;

    private String offWorkAddressTitle; //收车地址名称
    private String offWorkAddressDescription; //收车地址描述
    private Double offWorkAddressLon;
    private Double offWorkAddressLat;

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public int getVoiceBroadcast() {
        return voiceBroadcast;
    }

    public void setVoiceBroadcast(int voiceBroadcast) {
        this.voiceBroadcast = voiceBroadcast;
    }

    public int getDepartureOnly() {
        return departureOnly;
    }

    public void setDepartureOnly(int departureOnly) {
        this.departureOnly = departureOnly;
    }

    public int getReceiveOnly() {
        return receiveOnly;
    }

    public void setReceiveOnly(int receiveOnly) {
        this.receiveOnly = receiveOnly;
    }

    public int getOccupiedTime() {
        return occupiedTime;
    }

    public void setOccupiedTime(int occupiedTime) {
        this.occupiedTime = occupiedTime;
    }

    public int getCarpool() {
        return carpool;
    }

    public void setCarpool(int carpool) {
        this.carpool = carpool;
    }

    public double getReceivingRange() {
        return receivingRange;
    }

    public void setReceivingRange(double receivingRange) {
        this.receivingRange = receivingRange;
    }

    public int getCargoPassengerServiceOn() {
        return cargoPassengerServiceOn;
    }

    public void setCargoPassengerServiceOn(int cargoPassengerServiceOn) {
        this.cargoPassengerServiceOn = cargoPassengerServiceOn;
    }

    public int getCargoPassengerServiceStatus() {
        return cargoPassengerServiceStatus;
    }

    public void setCargoPassengerServiceStatus(int cargoPassengerServiceStatus) {
        this.cargoPassengerServiceStatus = cargoPassengerServiceStatus;
    }

    public String getOffWorkAddressTitle() {
        return offWorkAddressTitle;
    }

    public void setOffWorkAddressTitle(String offWorkAddressTitle) {
        this.offWorkAddressTitle = offWorkAddressTitle;
    }

    public String getOffWorkAddressDescription() {
        return offWorkAddressDescription;
    }

    public void setOffWorkAddressDescription(String offWorkAddressDescription) {
        this.offWorkAddressDescription = offWorkAddressDescription;
    }

    public double getOffWorkAddressLon() {
        return offWorkAddressLon;
    }

    public void setOffWorkAddressLon(double offWorkAddressLon) {
        this.offWorkAddressLon = offWorkAddressLon;
    }

    public double getOffWorkAddressLat() {
        return offWorkAddressLat;
    }

    public void setOffWorkAddressLat(double offWorkAddressLat) {
        this.offWorkAddressLat = offWorkAddressLat;
    }

    @Override
    public String toString() {
        return "ListenOrderSetting{" +
                "driverId=" + driverId +
                ", voiceBroadcast=" + voiceBroadcast +
                ", departureOnly=" + departureOnly +
                ", receiveOnly=" + receiveOnly +
                ", occupiedTime=" + occupiedTime +
                ", carpool=" + carpool +
                ", receivingRange=" + receivingRange +
                ", cargoPassengerServiceOn=" + cargoPassengerServiceOn +
                ", cargoPassengerServiceStatus=" + cargoPassengerServiceStatus +
                ", offWorkAddressTitle='" + offWorkAddressTitle + '\'' +
                ", offWorkAddressDescription='" + offWorkAddressDescription + '\'' +
                ", offWorkAddressLon=" + offWorkAddressLon +
                ", offWorkAddressLat=" + offWorkAddressLat +
                '}';
    }
}