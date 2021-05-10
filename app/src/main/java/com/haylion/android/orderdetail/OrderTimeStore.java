package com.haylion.android.orderdetail;

import android.content.Context;

import com.haylion.android.mvp.util.LogUtils;
import com.haylion.prefser.Prefser;
import com.haylion.prefser.TypeToken;

import java.util.HashMap;
import java.util.Map;

public final class OrderTimeStore {

    private static OrderTimeStore sInstance;

    /**
     * 记录订单的事件信息，用于倒计时。目前只用于送小孩单
     */
    private Prefser sOrderTimePrefser;

    private static final String ORDER_TIME = "order_time";

    /**
     * 订单时间集合
     */
    private Map<Long, Long> mOrderTimeMap;
    private final TypeToken<Map<Long, Long>> mTypeOfMap = new TypeToken<Map<Long, Long>>() {
    };

    private static final Object mLock = new Object();

    /**
     * 送小孩单5分钟后自动确认
     */
    private static final Long TIME_THRESHOLD = 5 * 60L;

    public static OrderTimeStore getInstance(Context context) {
        synchronized (mLock) {
            if (sInstance == null) {
                sInstance = new OrderTimeStore(context);
            }
            return sInstance;
        }
    }

    private OrderTimeStore(Context context) {
        sOrderTimePrefser = new Prefser(
                context.getSharedPreferences(ORDER_TIME, Context.MODE_PRIVATE)
        );
        mOrderTimeMap = initOrderTimeMap();
    }

    private Map<Long, Long> initOrderTimeMap() {
        Map<Long, Long> orderTimeMap = sOrderTimePrefser.get(ORDER_TIME, mTypeOfMap, null);
        if (orderTimeMap == null) {
            return new HashMap<>();
        }
        if (orderTimeMap.isEmpty()) {
            return orderTimeMap;
        }
        Map<Long, Long> map = new HashMap<>();
        for (Map.Entry<Long, Long> entry : orderTimeMap.entrySet()) {
            long diffSeconds = orderTimeUntilNow(entry.getValue());
            if (diffSeconds > 0 && diffSeconds < TIME_THRESHOLD) { // 5分钟内的订单才保留
                map.put(entry.getKey(), entry.getValue());
            } else {
                LogUtils.d("订单时间不在倒计时范围内：" + diffSeconds);
            }
        }
        sOrderTimePrefser.put(ORDER_TIME, map);
        return map;
    }

    private Long orderTimeUntilNow(Long orderTime) {
        return (System.currentTimeMillis() - orderTime) / 1000;
    }

    public Long orderCountdownTime(Long orderId) {
        if (mOrderTimeMap.containsKey(orderId)) {
            Long orderTime = mOrderTimeMap.get(orderId);
            return TIME_THRESHOLD - orderTimeUntilNow(orderTime);
        }
        return null;
    }

    public void saveOrderTime(Long orderId) {
        mOrderTimeMap.put(orderId, System.currentTimeMillis());
        sOrderTimePrefser.put(ORDER_TIME, mOrderTimeMap);
    }

    public void removeOrderTime(Long orderId) {
        mOrderTimeMap.remove(orderId);
        sOrderTimePrefser.put(ORDER_TIME, mOrderTimeMap);
    }


}
