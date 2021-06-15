package com.haylion.android.data.model;

import com.haylion.android.R;
import com.haylion.android.data.util.ResourceUtil;

/**
 * @class OrderTypeInfo
 * @description 订单类型
 * @date: 2019/12/17 10:40
 * @author: tandongdong
 */
public enum OrderTypeInfo {
    UNKNOWN(-1),
    ORDER_TYPE_REALTIME_CARPOOL(1), // 实时拼车
    ORDER_TYPE_REALTIME(2), // 实时订单
    ORDER_TYPE_BOOK(3), // 预约订单
    ORDER_TYPE_BOOK_CARPOOL(99), // 预约拼车，预留， 暂时未用到
    ORDER_TYPE_CARGO_PASSENGER(5), // 货拼客中的客单
    ORDER_TYPE_CARGO(4),// 货单
    ORDER_TYPE_SEND_CHILD(6), // 送你上学
    ORDER_TYPE_ACCESSIBILITY(7); // 无障碍

    private int type; //订单类型

    OrderTypeInfo(int type) {
        this.type = type;
    }

    public static OrderTypeInfo forStatus(int type) {
        switch (type) {
            case 1:
                return ORDER_TYPE_REALTIME_CARPOOL;
            case 2:
                return ORDER_TYPE_REALTIME;
            case 3:
                return ORDER_TYPE_BOOK;
            case 4:
                return ORDER_TYPE_CARGO;
            case 5:
                return ORDER_TYPE_CARGO_PASSENGER;
            case 6:
                return ORDER_TYPE_SEND_CHILD;
            case 7:
                return ORDER_TYPE_ACCESSIBILITY;
        }
        return UNKNOWN;
    }

    public static String getStatusText(int type) {
        switch (OrderTypeInfo.forStatus(type)) {
            case ORDER_TYPE_REALTIME_CARPOOL:
                return "实时拼车";
            case ORDER_TYPE_REALTIME:
                return "实时订单";
            case ORDER_TYPE_BOOK:
                return "预约订单";
            case ORDER_TYPE_BOOK_CARPOOL:
                return "预约拼车";
            case ORDER_TYPE_CARGO_PASSENGER:
                return "实时订单";
            case ORDER_TYPE_CARGO:
                return "货单";
            case ORDER_TYPE_SEND_CHILD:
                return "送你上学";
            case ORDER_TYPE_ACCESSIBILITY:
                return "无障碍";
            case UNKNOWN:
                return "顺丰订单";
        }
        return ResourceUtil.getString(R.string.order_status_unknown);
    }

    public static String getStatusText(int type, int channel) {
        switch (OrderTypeInfo.forStatus(type)) {
            case ORDER_TYPE_REALTIME_CARPOOL:
                return "实时拼车";
            case ORDER_TYPE_REALTIME:
               /* if(channel == Order.ORDER_CHANNEL_MEITUAN){
                    return "实时订单 美团";
                }*/
                return "实时订单";
            case ORDER_TYPE_BOOK:
                return "预约订单";
            case ORDER_TYPE_BOOK_CARPOOL:
                return "预约拼车";
            case ORDER_TYPE_CARGO_PASSENGER:
                /*if(channel == Order.ORDER_CHANNEL_MEITUAN){
                    return "实时订单 美团";
                }*/
                return "实时订单";
            case ORDER_TYPE_CARGO:
                return "货单";
            case ORDER_TYPE_SEND_CHILD:
                return "送你上学";
            case ORDER_TYPE_ACCESSIBILITY:
                return "无障碍";
            case UNKNOWN:
                return "抢单";//顺丰订单
        }
        return ResourceUtil.getString(R.string.order_status_unknown);
    }


    public int getStatus() {
        return type;
    }

}
