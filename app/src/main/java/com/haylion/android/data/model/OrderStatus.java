package com.haylion.android.data.model;

import com.haylion.android.R;
import com.haylion.android.data.util.ResourceUtil;

/**
 * @class OrderStatus
 * @description 订单状态
 * @date: 2019/12/17 10:37
 * @author: tandongdong
 */
public enum OrderStatus {
    UNKNOWN(0),
    ORDER_STATUS_INIT(6), // 初始状态，未接单
    ORDER_STATUS_READY(3), // 等待预约订单，存在的状态。在时间到达前，会进入状态 7
    ORDER_STATUS_WAIT_CAR(7), // 等待司机接驾
    ORDER_STATUS_ARRIVED_START_ADDR(8), // 到达目的地
    ORDER_STATUS_GET_ON(4), // 已上车
    ORDER_STATUS_GET_OFF(9), // 已下车
    ORDER_STATUS_WAIT_PAY(2), // 待支付
    ORDER_STATUS_CLOSED(1), // 订单完成
    ORDER_STATUS_CANCELED(5); // 订单取消

    private int status;

    OrderStatus(int status) {
        this.status = status;
    }

    public static OrderStatus forStatus(int status) {
        switch (status) {
            case 0:
                return UNKNOWN;
            case 1:
                return ORDER_STATUS_CLOSED;
            case 2:
                return ORDER_STATUS_WAIT_PAY;
            case 3:
                return ORDER_STATUS_READY;
            case 4:
                return ORDER_STATUS_GET_ON;
            case 5:
                return ORDER_STATUS_CANCELED;
            case 6:
                return ORDER_STATUS_INIT;
            case 7:
                return ORDER_STATUS_WAIT_CAR;
            case 8:
                return ORDER_STATUS_ARRIVED_START_ADDR;
            case 9:
                return ORDER_STATUS_GET_OFF;
        }
        return UNKNOWN;
    }

    public static String getStatusText(int status) {
        switch (OrderStatus.forStatus(status)) {
            case ORDER_STATUS_CLOSED:
                return "已完成";
            case ORDER_STATUS_WAIT_PAY:
                return "未支付";
            case ORDER_STATUS_READY:
                return "待开始";
            case ORDER_STATUS_GET_ON:
                return "已上车";
            case ORDER_STATUS_CANCELED:
                return "已取消";
            case ORDER_STATUS_INIT:
                return "初始状态";
            case ORDER_STATUS_WAIT_CAR:
                return "等车中";
            case ORDER_STATUS_ARRIVED_START_ADDR:
                return "到达目的地";
            case ORDER_STATUS_GET_OFF:
                return "乘客已下车";
            case UNKNOWN:
                return "未知状态";
        }
        return ResourceUtil.getString(R.string.order_status_unknown);
    }

    public static String getStatusText(int status, int orderType) {
        return getStatusText(OrderStatus.forStatus(status), orderType);
    }

    public static String getStatusText(OrderStatus status, int orderType) {
        switch (status) {
            case ORDER_STATUS_CLOSED:
                return "已完成";
            case ORDER_STATUS_WAIT_PAY:
                if (orderType == Order.ORDER_TYPE_SEND_CHILD) {
                    return "家长未支付";
                } else {
                    return "乘客未支付";
                }
            case ORDER_STATUS_READY:
                if (orderType == Order.ORDER_TYPE_BOOK) {
                    return "已派单";
                } else {
                    return "待开始";
                }
            case ORDER_STATUS_GET_ON:
                if (orderType == Order.ORDER_TYPE_BOOK) {
                    return "行程中";
                } else {
                    return "已上车";
                }
            case ORDER_STATUS_CANCELED:
                return "已取消";
            case ORDER_STATUS_INIT:
                return "初始状态";
            case ORDER_STATUS_WAIT_CAR:
                return "等车中";
            case ORDER_STATUS_ARRIVED_START_ADDR:
                return "到达目的地";
            case ORDER_STATUS_GET_OFF:
                return "乘客已下车";
            case UNKNOWN:
                return "未知状态";
//                return ResourceUtil.getString(R.string.order_status_rejected);
        }
        return ResourceUtil.getString(R.string.order_status_unknown);
    }

    /**
     * @param
     * @return
     * @method
     * @description 订单状态，
     * 根据订单主状态，子状态，订单类型不同有不同的显示。
     * @date: 2020/2/20 15:15
     * @author: tandongdong
     */
    public static String getOrderStatusText(Order order) {
        switch (OrderStatus.forStatus(order.getOrderStatus())) {
            case ORDER_STATUS_CLOSED:
                if (order.getOrderSubStatus() == Order.ORDER_SUB_STATUS_CLOSED_ABNORMAL) {
                    return "异常结束";
                } else if (order.getOrderSubStatus() == Order.ORDER_SUB_STATUS_CLOSED_COMPLAIN) {
                    return "已完成(支付争议)";
                } else {
                    return "已完成";
                }
            case ORDER_STATUS_WAIT_PAY:
                if (order.getOrderType() == Order.ORDER_TYPE_SEND_CHILD) {
                    if (order.getOrderSubStatus() == Order.ORDER_SUB_STATUS_WAIT_PAY_ABNORMAL) {
                        return "异常结束待支付";
                    } else {
                        return "家长未支付";
                    }
                } else {
                    return "乘客未支付";
                }
            case ORDER_STATUS_READY:
                return "待开始";
            case ORDER_STATUS_GET_ON:
                return "已上车";
            case ORDER_STATUS_CANCELED:
                return "已取消";
            case ORDER_STATUS_INIT:
                return "初始状态";
            case ORDER_STATUS_WAIT_CAR:
                return "等车中";
            case ORDER_STATUS_ARRIVED_START_ADDR:
                return "到达目的地";
            case ORDER_STATUS_GET_OFF:
                return "乘客已下车";
            case UNKNOWN:
                return "未知状态";
//                return ResourceUtil.getString(R.string.order_status_rejected);
        }
        return ResourceUtil.getString(R.string.order_status_unknown);

    }

    public int getStatus() {
        return status;
    }

}
