package com.haylion.android.data.model;

/**
 * @class BackHomeAddress
 * @description 收车地址
 * @date: 2019/12/17 10:20
 * @author: tandongdong
 */
public class BackHomeAddress {

    private int driverId;
    private String offWorkAddressTitle;
    private String offWorkAddressDescription;
    private double offWorkAddressLon;
    private double offWorkAddressLat;

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setOffWorkAddressTitle(String offWorkAddressTitle) {
        this.offWorkAddressTitle = offWorkAddressTitle;
    }

    public String getOffWorkAddressTitle() {
        return offWorkAddressTitle;
    }

    public void setOffWorkAddressDescription(String offWorkAddressDescription) {
        this.offWorkAddressDescription = offWorkAddressDescription;
    }

    public String getOffWorkAddressDescription() {
        return offWorkAddressDescription;
    }

    public void setOffWorkAddressLon(double offWorkAddressLon) {
        this.offWorkAddressLon = offWorkAddressLon;
    }

    public double getOffWorkAddressLon() {
        return offWorkAddressLon;
    }

    public void setOffWorkAddressLat(double offWorkAddressLat) {
        this.offWorkAddressLat = offWorkAddressLat;
    }

    public double getOffWorkAddressLat() {
        return offWorkAddressLat;
    }

}