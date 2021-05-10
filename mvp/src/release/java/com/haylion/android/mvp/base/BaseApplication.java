package com.haylion.android.mvp.base;

import android.app.Application;
import com.hjq.toast.ToastUtils;
import com.haylion.android.mvp.util.LogUtils;
import com.haylion.android.mvp.callback.SimpleActivityLifecycleCallbacks;


public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
//        LogUtils.init();
    }

}
