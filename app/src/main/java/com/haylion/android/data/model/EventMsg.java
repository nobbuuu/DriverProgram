package com.haylion.android.data.model;

 /**
  * @class  EventMsg
  * @description EventBus 传递类
  * @date: 2019/12/17 10:23
  * @author: tandongdong
  */
public class EventMsg {
    public final static int CMD_ORDER_CANCELED = 0; //订单取消
    public final static int CMD_ORDER_HAS_ALLOCATED = 1; //订单被抢
    public final static int CMD_ORDER_OVERTIME = 2; //订单被抢
    public final static int CMD_ORDER_GRAB_SUCCESS = 3; //订单被抢
    public final static int CMD_ORDER_GRAB_FAIL = 4; //订单被抢
    public final static int CMD_ORDER_VEHICLE_DISABLE = 5; //车辆停用
    public final static int CMD_ORDER_FRESH_ORDER_DETAIL = 6; //刷新订单详情
    public final static int CMD_ORDER_ADDRESS_HAS_CHANGED = 7; //修改下车地址
    public final static int CMD_ORDER_STATUS_TO_CONTROVERSY = 8; //订单变成争议状态

    private int type; //消息类型
    private int cmd; //消息ID
    private String message; //消息描述
    private int orderId; //

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    @Override
    public String toString() {
        return "EventMsg{" +
                "type=" + type +
                ", cmd=" + cmd +
                ", message='" + message + '\'' +
                ", orderId=" + orderId +
                '}';
    }
}
