package com.haylion.android.mvp;

import android.app.Application;

import com.haylion.android.mvp.callback.SimpleActivityLifecycleCallbacks;
import com.hjq.toast.ToastUtils;

/**
 * @author dengzh
 * @date 2019/12/24
 * Description:在主项目的Application配置
 */
public class BaseConfig {

    public static void init(Application application){
        //第三方Toast
        ToastUtils.init(application);
        application.registerActivityLifecycleCallbacks(new SimpleActivityLifecycleCallbacks());
    }
}
