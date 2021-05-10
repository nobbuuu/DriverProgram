package com.haylion.android.data.model;

import android.os.Parcel;
import android.os.Parcelable;

 /**
  * @class  OrderCostInfo
  * @description 费用信息
  * @date: 2019/12/17 10:33
  * @author: tandongdong
  */
public class OrderCostInfo implements Parcelable{
    private double totalCost;
    private double baseCost;
    private double otherCost;
    private double serviceCost;

    public OrderCostInfo() {
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public double getBaseCost() {
        return baseCost;
    }

    public void setBaseCost(double baseCost) {
        this.baseCost = baseCost;
    }

    public double getOtherCost() {
        return otherCost;
    }

    public void setOtherCost(double otherCost) {
        this.otherCost = otherCost;
    }

    public double getServiceCost() {
        return serviceCost;
    }

    public void setServiceCost(double serviceCost) {
        this.serviceCost = serviceCost;
    }

    protected OrderCostInfo(Parcel in) {
        totalCost = in.readDouble();
        baseCost = in.readDouble();
        otherCost = in.readDouble();
        serviceCost = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(totalCost);
        dest.writeDouble(baseCost);
        dest.writeDouble(otherCost);
        dest.writeDouble(serviceCost);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OrderCostInfo> CREATOR = new Creator<OrderCostInfo>() {
        @Override
        public OrderCostInfo createFromParcel(Parcel in) {
            return new OrderCostInfo(in);
        }

        @Override
        public OrderCostInfo[] newArray(int size) {
            return new OrderCostInfo[size];
        }
    };
}
