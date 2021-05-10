package com.haylion.android.service;

public enum WsCommands {
    /*司机端的消息*/
    WEBSOCKET_CLOSE_TO_CONNECT(-22, "websocket断流重连"),  //websocket 断流重连
    RESPONSE_LINK_ERROR_TOKEN_INVALID(-1, "auth fail"),
    RESPONSE_LINK_ERROR_DUPLICATED_LOGIN(-2, "duplicate login"),
    DRIVER_NEW_ORDER(1, "newOrder"),
    DRIVER_ORDER_CANCEL_BY_USER(2, "orderCanceledByUser"),
    DRIVER_ORDER_PAID(3,  "orderPaid"),
    DRIVER_ORDER_ALLOCATED(4,"orderAllocated"),
    DRIVER_ORDER_OVERTIME(5,"orderOvertime"),
    DRIVER_ORDER_DESTINATION_CHANGED(6,"destinationChangedByPassenger"),
    DRIVER_ACCOUNT_DISABLE(7,"driverAccountDisable"),
    DRIVER_VEHICLE_DISABLE(8,"driverVehicleDisable"),
    DRIVER_ORDER_CANCEL_BY_SYSTEM(9, "orderCanceledBySystem"),
    DRIVER_ORDER_ASSIGNED(10, "assignOrder"),
    DRIVER_ORDER_ARRIVED_BY_SYSTEM(11, "orderConfirmedBySystem"),
    DRIVER_ORDER_CANCEL_BY_USER_BEFORE_TAKE_ORDER(12, "orderCanceledByUserBeforeDriverTakeOrder"),
    DRIVER_INFO_CHANGE(49,"driverInfoChangedRelogin"),//司机信息发生改变，需要重新登录
    DRIVER_ORDER_EXCEPTION_CANCEL(52,"exceptionCancel"),//订单异常结束

    /*乘客端消息，司机端不用*/
    PASSENGER_ORDER_ALLOCATED(20,"orderAllocated"),
    PASSENGER_ORDER_CANCELED_BY_DRIVER(21,"orderCanceledByDriver"),
    PASSENGER_DRIVER_ARRIVE_DEPARTURE_PLACE(22,"departurePlace"),
    PASSENGER_ON_CAR(23,"passengerOnCar"),
    PASSENGER_DESTINATION(24,"destination"),
    PASSENGER_CHOOSE_PAY_METHOD(25,"payMethod"),
    PASSENGER_PAY_ORDER(26,"payOrder"),
    PASSENGER_FEE_PAID(27,"feePaid"),
    PASSENGER_ORDER_CANCELED_BY_SYSTEM(28,"orderCanceledBySystem"),
    PASSENGER_NEW_CARPOOL_PASSENGER(29,"newCarpoolPassenger"),
    GOODS_ORDER_PICKED_UP(40, "goodsPickedUp"),
    GOODS_ORDER_DELIVERED(41, "goodsDelivered"),
    GOODS_ORDER_CANCELLED(42, "goodsOrderCancelled"),
    GOODS_ORDER_KILLED(43, "goodsOrderKilledBySys"),
    DRIVER_CUSTOMER_ON_CONFIRM(44, "customerOnConfirm"),
    DRIVER_CUSTOMER_OFF_CONFIRM(45, "customerOffConfirm"),
    DRIVER_UNPAID_ORDER_TO_CONTROVERSY_ORDER(48, "controversyOrder"), //订单转换成争议状态
    DRIVER_NEW_ORDER_MEITUAN_GRAB_SUCCESS(60, "meituanGrabSuccess"); //美团单抢单成功



    WsCommands(int sn, String detail) {
        this.sn = sn;
        this.detail = detail;
    }
    private int sn;
    private String detail;
    public int getSn() {
        return sn;
    }
    public String getDetail() {
        return detail;
    }
    public Object getCmdMark() {
        return detail;
    }
}
