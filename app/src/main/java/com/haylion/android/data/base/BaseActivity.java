package com.haylion.android.data.base;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.haylion.android.data.util.PermissionManager;
import com.haylion.android.mvp.base.AbstractPresenter;
import com.haylion.android.mvp.base.MvpBaseActivity;
import com.haylion.android.mvp.callback.SimpleActivityLifecycleCallbacks;
import com.haylion.android.mvp.util.AppUtils;
import com.haylion.android.mvp.util.LogUtils;

/**
 * 继承MvpBaseActivity
 * 在此基类做此项目的基类处理
 */
public abstract class BaseActivity<P extends AbstractPresenter> extends MvpBaseActivity<P> {

    /**
     * 权限管理，实时监控
     */
    protected PermissionManager permissionManager;
    protected boolean isHandlePermissionByBase = true; //是否需要BaseActivity做权限监控

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //应用使用过程中，关闭权限，会异常销毁所有界面，因而相当于重新打开启动页（微信思想）
        if (null != savedInstanceState) {
            AppUtils.restartSelf(this);
            /*AppManager.getAppManager().finishAllActivity();
            Intent intent = new Intent(this, WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);*/
        }
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        permissionManager = new PermissionManager(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPermission();
    }

    private void checkPermission() {
        if (SimpleActivityLifecycleCallbacks.isFirstForeground
                && permissionManager != null && isHandlePermissionByBase) {
            LogUtils.d("APP由后台->前台:检查悬浮窗和必要权限");
            permissionManager.check();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (permissionManager != null) {
            permissionManager.onDestroy();
        }
    }


    /**
     * @param
     * @return
     * @method
     * @description 重写 getResource 方法，防止系统字体影响
     * @date: 2019/12/24 11:28
     * @author: tandongdong
     */
    @Override
    public Resources getResources() {
        //禁止app字体大小跟随系统字体大小调节
        Resources resources = super.getResources();
        if (resources != null && resources.getConfiguration().fontScale >= 1.2f) {
            android.content.res.Configuration configuration = resources.getConfiguration();
            Log.d("BaseActivity", "system setting, fontScale:" + resources.getConfiguration().fontScale);
            configuration.fontScale = 1.15f;
            Log.d("BaseActivity", "fontScale is too big, just set max fontScale to " + resources.getConfiguration().fontScale);
            resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        }
        return resources;

    }
}
