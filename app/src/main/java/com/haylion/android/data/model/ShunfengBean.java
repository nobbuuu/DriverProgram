package com.haylion.android.data.model;

import java.util.List;

public class ShunfengBean {

    /**
     * id : 2
     * depotStartAddress : 广东省深圳市罗湖区深圳站
     * depotStartLongitude : 22.531948
     * depotStartLatitude : 114.117751
     * depotStartConcat1 : 联系人1
     * depotStartPhone1 : 18867003000
     * depotStartConcat2 : 联系人2
     * depotStartPhone2 : 18867003001
     * depotStartConcat3 : 联系人3
     * depotStartPhone3 : 18867003002
     * takeTime : 12:00:00
     * depotEndAddress : 广东省深圳市福田区石厦(地铁站)
     * depotEndLongitude : 22.523708
     * depotEndLatitude : 114.053616
     * depotEndConcat1 : 联系人1
     * depotEndPhone1 : 18867003000
     * depotEndConcat2 : 联系人2
     * depotEndPhone2 : 18867003001
     * depotEndConcat3 : 联系人3
     * depotEndPhone3 : 18867003002
     * deliveryTime : 18:00:00
     * startDate : 2021-05-09 00:00:00
     * endDate : 2021-05-19 00:00:00
     * price : 14000
     * grabDateList : [{"grabDate":"2021-05-11 00:00:00"},{"grabDate":"2021-05-13 00:00:00"},{"grabDate":"2021-05-14 00:00:00"},{"grabDate":"2021-05-15 00:00:00"},{"grabDate":"2021-05-16 00:00:00"},{"grabDate":"2021-05-17 00:00:00"},{"grabDate":"2021-05-18 00:00:00"},{"grabDate":"2021-05-19 00:00:00"}]
     */

    private int id;
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
    private String startDate;
    private String endDate;
    private int price;
    private int orderType = -1;
    private List<GrabDateListBean> grabDateList;

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public List<GrabDateListBean> getGrabDateList() {
        return grabDateList;
    }

    public void setGrabDateList(List<GrabDateListBean> grabDateList) {
        this.grabDateList = grabDateList;
    }

    public static class GrabDateListBean {
        /**
         * grabDate : 2021-05-11 00:00:00
         */

        private String grabDate;

        public String getGrabDate() {
            return grabDate;
        }

        public void setGrabDate(String grabDate) {
            this.grabDate = grabDate;
        }
    }
}
