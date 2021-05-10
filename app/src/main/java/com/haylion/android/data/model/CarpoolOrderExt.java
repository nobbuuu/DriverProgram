package com.haylion.android.data.model;

import java.util.List;

public class CarpoolOrderExt extends Order {

    private List<String> orderCodes;

    private List<String> addressList;
    private List<String> parentPhoneList;
    private List<String> parentMessageList;

    public List<String> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<String> addressList) {
        this.addressList = addressList;
    }

    public List<String> getParentPhoneList() {
        return parentPhoneList;
    }

    public void setParentPhoneList(List<String> parentPhoneList) {
        this.parentPhoneList = parentPhoneList;
    }

    public List<String> getParentMessageList() {
        return parentMessageList;
    }

    public void setParentMessageList(List<String> parentMessageList) {
        this.parentMessageList = parentMessageList;
    }

    public List<String> getOrderCodes() {
        return orderCodes;
    }

    public void setOrderCodes(List<String> orderCodes) {
        this.orderCodes = orderCodes;
    }

}
