package com.haylion.android.utils;

import android.os.Handler;

import com.haylion.android.calendar.DateFormatUtil;
import com.haylion.android.calendar.DateStyle;
import com.haylion.android.data.model.Order;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;

public class SynaTimeUtils {
    private Handler mHandler = new Handler();
    private static final SynaTimeUtils ourInstance = new SynaTimeUtils();

    private Date endTime = new Date();

    public static SynaTimeUtils getInstance() {
        return ourInstance;
    }

    private SynaTimeUtils() {
    }

    public void post(Order order) {
        endTime = DateFormatUtil.getTime(order.getEndTime() + " " + order.getDeliveryTime(), "yyyy-MM-dd HH:mm");
        mHandler.removeCallbacks(runnable);
        mHandler.post(runnable);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (endTime.after(new Date())) {
                mHandler.postDelayed(this, 1000);
            } else {
                EventBus.getDefault().post("timeout");
                removeScheckTime();
            }
        }
    };

    public void  removeScheckTime(){
        mHandler.removeCallbacks(runnable);
    }
}
