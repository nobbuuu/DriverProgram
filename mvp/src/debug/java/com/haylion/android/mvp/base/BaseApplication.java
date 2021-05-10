package com.haylion.android.mvp.base;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.facebook.stetho.Stetho;
import com.haylion.android.mvp.callback.SimpleActivityLifecycleCallbacks;
import com.haylion.android.mvp.util.AppUtils;
import com.hjq.toast.ToastUtils;

import leakcanary.LeakCanary;

public abstract class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (AppUtils.isInMainProcess(this)) {
          //  LeakCanary.install(this);
//        WebView.setWebContentsDebuggingEnabled(true);
            Stetho.initializeWithDefaults(this);
        }
    }
}
