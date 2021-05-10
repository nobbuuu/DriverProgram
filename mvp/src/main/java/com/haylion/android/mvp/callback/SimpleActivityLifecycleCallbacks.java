package com.haylion.android.mvp.callback;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.haylion.android.mvp.model.AppStatusChangedEvent;
import com.haylion.android.mvp.util.BusUtils;

/**
 * Activity 生命周期监听
 * 1）应用处于前台 or 后台
 * 2）应用是否由后台 -> 前台
 */
public class SimpleActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

    private static int numRunningActivities = 0;
    public static boolean isFirstForeground; //是否由后台->前台

    public static boolean isForeground() {
        return numRunningActivities > 0;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        numRunningActivities++;
        BusUtils.post(new AppStatusChangedEvent(numRunningActivities));
        //数值从0变到1说明是从后台切到前台
        isFirstForeground = numRunningActivities == 1;
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        numRunningActivities--;
        isFirstForeground = false;
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

}
