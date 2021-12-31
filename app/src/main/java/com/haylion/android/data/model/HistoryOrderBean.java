package com.haylion.android.data.model;

import java.util.List;

public class HistoryOrderBean {

    /**
     * total : 2
     * current : 1
     * pageCount : 1
     * list : [{"id":5,"orderNo":"202106141115429820001","status":3,"orderType":1,"depotStartAddress":"深圳北站(东进站口)","depotStartLongitude":"114.030277","depotStartLatitude":"22.610406","takeTime":"12:15","depotEndAddress":"福田保税区","depotEndLongitude":"114.043839","depotEndLatitude":"22.509046","deliveryTime":"13:30","actualDeliveryTime":"11:17","startDate":"2021-06-14","endDate":"2021-06-14","price":9900,"createTime":"2021-06-14 11:15:42"},{"id":2,"orderNo":"202106141112028030001","status":1,"orderType":1,"depotStartAddress":"深圳北站(东进站口)","depotStartLongitude":"114.030277","depotStartLatitude":"22.610406","takeTime":"10:00","depotEndAddress":"福田保税区","depotEndLongitude":"114.043839","depotEndLatitude":"22.509046","deliveryTime":"12:10","actualDeliveryTime":null,"startDate":"2021-06-16","endDate":"2021-06-16","price":10100,"createTime":"2021-06-14 11:12:02"}]
     */

    private int total;
    private int current;
    private int pageCount;
    private List<ListBean> list;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * id : 5
         * orderNo : 202106141115429820001
         * status : 3
         * driverStatus : 3
         * orderType : 1
         * depotStartAddress : 深圳北站(东进站口)
         * depotStartLongitude : 114.030277
         * depotStartLatitude : 22.610406
         * takeTime : 12:15
         * depotEndAddress : 福田保税区
         * depotEndLongitude : 114.043839
         * depotEndLatitude : 22.509046
         * deliveryTime : 13:30
         * actualDeliveryTime : 11:17
         * startDate : 2021-06-14
         * endDate : 2021-06-14
         * price : 9900
         * createTime : 2021-06-14 11:15:42
         */

        private int id;
        private String orderNo;
        private int status;
        private int driverStatus;
        private int orderType;
        private String depotStartAddress;
        private String depotStartDetailAddress;
        private String depotStartLongitude;
        private String depotStartLatitude;
        private String takeTime;
        private String depotEndAddress;
        private String depotEndDetailAddress;
        private String depotEndLongitude;
        private String depotEndLatitude;
        private String deliveryTime;
        private String actualDeliveryTime;
        private String startDate;
        private String endDate;
        private int price;
        private String createTime;

        public String getDepotStartDetailAddress() {
            return depotStartDetailAddress;
        }

        public void setDepotStartDetailAddress(String depotStartDetailAddress) {
            this.depotStartDetailAddress = depotStartDetailAddress;
        }

        public String getDepotEndDetailAddress() {
            return depotEndDetailAddress;
        }

        public void setDepotEndDetailAddress(String depotEndDetailAddress) {
            this.depotEndDetailAddress = depotEndDetailAddress;
        }

        public int getDriverStatus() {
            return driverStatus;
        }

        public void setDriverStatus(int driverStatus) {
            this.driverStatus = driverStatus;
        }

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

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
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
}
