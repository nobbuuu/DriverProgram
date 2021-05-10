package com.haylion.android.data.model;

import java.util.List;

/**
 * @class OrderForCargoAndPassenger
 * @description 货单和客单，对应首页的订单列表接口。
 * @date: 2019/12/17 10:35
 * @author: tandongdong
 */
public class OrderForCargoAndPassenger {
    private List<OrderForMainActivity> passengerList;
    private List<OrderForMainActivity> cargoList;

    //    private List<OrderForMainActivity> appointmentList;
    private AppointmentList appointmentList;

    public List<OrderForMainActivity> getPassengerList() {
        return passengerList;
    }

    public void setPassengerList(List<OrderForMainActivity> passengerList) {
        this.passengerList = passengerList;
    }

    public List<OrderForMainActivity> getCargoList() {
        return cargoList;
    }

    public void setCargoList(List<OrderForMainActivity> cargoList) {
        this.cargoList = cargoList;
    }

//    public List<OrderForMainActivity> getAppointmentList() {
//        return appointmentList;
//    }
//
//    public void setAppointmentList(List<OrderForMainActivity> appointmentList) {
//        this.appointmentList = appointmentList;
//    }

    public AppointmentList getAppointmentList() {
        return appointmentList;
    }

    public void setAppointmentList(AppointmentList appointmentList) {
        this.appointmentList = appointmentList;
    }



}