package com.haylion.android.data.widgt;

import android.app.Activity;


import java.lang.ref.WeakReference;

public class MyActivityManager {
    private static final String TAG = "MyActivityManager";

    private static MyActivityManager sInstance = new MyActivityManager();

    private WeakReference<Activity> sCurrentActivityWeakRef;


    private MyActivityManager() {

    }

    public static MyActivityManager getInstance() {
        return sInstance;
    }

    public Activity getCurrentActivity() {
        Activity currentActivity = null;
        if (sCurrentActivityWeakRef != null) {
            currentActivity = sCurrentActivityWeakRef.get();
        }
        return currentActivity;
    }

    public void setCurrentActivity(Activity activity) {
/*        try {
            LogUtils.d(TAG,"current activity:" + activity);
        } catch (Exception e){
            e.printStackTrace();
        }finally {

        }*/
        sCurrentActivityWeakRef = new WeakReference<Activity>(activity);
    }

}
