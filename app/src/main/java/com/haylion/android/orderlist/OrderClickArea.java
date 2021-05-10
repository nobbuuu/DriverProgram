package com.haylion.android.orderlist;


import androidx.annotation.IntDef;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

@Retention(SOURCE)
@IntDef({
        OrderClickArea.CONTACT_PASSENGER,
        OrderClickArea.ORDER_NAVIGATION
})
public @interface OrderClickArea {
    int ORDER_NAVIGATION = 364356;

    int CONTACT_PASSENGER = 1005;

    int CONTACT_SEND_PASSENGER = 1006;
    int CONTACT_RECEIVE_PASSENGER = 1007;

    int SHOW_IN_MAP = 1008;

    int PUSH_MESSAGE = 1009;

    int PUSH_MESSAGE_LONG_CLICK = 1010;

    int ORDER_DETAILS = 10011; // 点击了订单卡片

}