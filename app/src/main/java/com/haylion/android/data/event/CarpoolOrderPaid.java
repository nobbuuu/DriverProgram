package com.haylion.android.data.event;

public class CarpoolOrderPaid {

    private String carpoolCode;

    public CarpoolOrderPaid(String carpoolCode) {
        this.carpoolCode = carpoolCode;
    }

    public String getCarpoolCode() {
        return carpoolCode;
    }

    public void setCarpoolCode(String carpoolCode) {
        this.carpoolCode = carpoolCode;
    }

}
