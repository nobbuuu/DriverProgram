package com.haylion.android.mvp.util;


import org.greenrobot.eventbus.EventBus;

public final class BusUtils {

    private BusUtils() {
    }

    public static void register(Object subscriber) {
        if (subscriber == null) {
            return;
        }
        try {
            EventBus.getDefault().register(subscriber);
        } catch (IllegalArgumentException e) {
            // ignore
        }
    }

    public static void unregister(Object subscriber) {
        if (subscriber == null) {
            return;
        }
        try {
            EventBus.getDefault().unregister(subscriber);
        } catch (IllegalArgumentException e) {
            // ignore
        }
    }

    public static void post(Object event) {
        if (event == null) {
            return;
        }
        EventBus.getDefault().post(event);
    }

    public static void postSticky(Object event) {
        if (event == null) {
            return;
        }
        EventBus.getDefault().postSticky(event);
    }

    public static <T> T removeStickyEvent(Class<T> eventType) {
        return EventBus.getDefault().removeStickyEvent(eventType);
    }
}
