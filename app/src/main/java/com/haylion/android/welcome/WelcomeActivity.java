package com.haylion.android.welcome;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.haylion.android.BuildConfig;
import com.haylion.android.R;
import com.haylion.android.data.base.BaseActivity;
import com.haylion.android.data.base.ConfirmDialog;
import com.haylion.android.data.model.LatestVersionBean;
import com.haylion.android.data.repo.PrefserHelper;
import com.haylion.android.data.util.DateUtils;
import com.haylion.android.data.util.NotificationUtils;
import com.haylion.android.main.MainActivity;
import com.haylion.android.mvp.util.LogUtils;
import com.haylion.android.updateApk.VersionDialog;
import com.haylion.android.user.account.LoginActivity;
import com.haylion.maastaxi.WxSdkUtils;


/**
 * 欢迎页面
 * 检查悬浮窗等权限 -> 检查新版本 -> 检查登陆状态
 */
public class WelcomeActivity extends BaseActivity<WelcomeContract.Presenter> implements WelcomeContract.View {

    private static final String TAG = "WelcomeActivity";

    private int HANDLER_MSG_CHECK_VERSION = 100;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == HANDLER_MSG_CHECK_VERSION) {
                presenter.checkUpdates();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        isHandlePermissionByBase = false; //不用 BaseActivity 的 onStart()权限监听
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestMustPermission();
    }

    /**
     * 某些手机判断悬浮窗开启有延迟，延迟0.5s去请求
     * 因为特殊处理，所以此页面禁掉BaseActivity 的 权限监听
     */
    private void requestMustPermission() {
        handler.postDelayed(mPermissionRunnable, 500);
    }

    private Runnable mPermissionRunnable = () -> permissionManager.check();

    @Override
    protected void onResume() {
        super.onResume();
        if (permissionManager.checkOverLayOpenAndPermissionGrant()) {
            Log.e(TAG, "悬浮窗已开启，权限已获取");
            checkNotificationIsOpen();
        } else {
            Log.e(TAG, "悬浮窗未开启 or 权限未获取");
        }
    }

    /**
     * 每日第一次启动，检查通知栏权限是否开启
     */
    private void checkNotificationIsOpen() {
        String appStartUpDate = PrefserHelper.getCache(PrefserHelper.APP_LAST_STARTUP_DATE);
        String currentDate = DateUtils.getCurrentDate();
        if (!currentDate.equals(appStartUpDate)) {
            PrefserHelper.setCache(PrefserHelper.APP_LAST_STARTUP_DATE, currentDate);
            if (!NotificationUtils.isNotificationEnabled(this)) {
                showNotificationTipDialog();
            } else {
                Log.e(TAG, "通知栏权限已获取");
                checkUpdateVersion();
            }
        } else {
            checkUpdateVersion();
        }
    }

    private void showNotificationTipDialog() {
        String msg = "打开消息通知后能第一时间获取订单取消、下车点变更信息";
        ConfirmDialog.newInstance().setMessage(msg)
                .setOnClickListener(new ConfirmDialog.OnClickListener() {
                    @Override
                    public void onPositiveClick(View view) {
                        NotificationUtils.goNotificationSetting(getContext());
                    }

                    @Override
                    public void onNegativeClick(View view) {
                        checkUpdateVersion();
                    }
                })
                .setPositiveText("去开启")
                .setNegativeText(R.string.close)
                .show(getSupportFragmentManager(), "");
    }

    /**
     * 检测新版本
     */
    private void checkUpdateVersion() {
        presenter.checkUpdates();
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(mPermissionRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    protected WelcomeContract.Presenter onCreatePresenter() {
        return new WelcomePresenter(this);
    }


    @Override
    public void checkUpdatesSuccess(LatestVersionBean version) {
        if (!version.hasNewVersion()) {
            checkLoginStatus();
            return;
        }
        if (version.isForceUpdate()) { //强制更新
            PrefserHelper.setCache(PrefserHelper.APK_UPDATE_LAST_VERSION_CODE, version.getVersionCode() + "");
            PrefserHelper.removeKey(PrefserHelper.APK_UPDATE_LAST_REJECT_DATE);
            PrefserHelper.removeKey(PrefserHelper.APK_UPDATE_REJECT_NUMBER_IN_SAME_VERSION);
            showVersionDialog(version);
        } else {//非强制更新
            String lastVersion = PrefserHelper.getCache(PrefserHelper.APK_UPDATE_LAST_VERSION_CODE);
            if (!String.valueOf(version.getVersionCode()).equals(lastVersion)) {
                Log.e(TAG, "非强制更新，第一次显示");
                //不同版本,必定是第一次显示
                PrefserHelper.setCache(PrefserHelper.APK_UPDATE_LAST_VERSION_CODE, version.getVersionCode() + "");
                PrefserHelper.removeKey(PrefserHelper.APK_UPDATE_LAST_REJECT_DATE);
                PrefserHelper.removeKey(PrefserHelper.APK_UPDATE_REJECT_NUMBER_IN_SAME_VERSION);
                showVersionDialog(version);
            } else {
                //同一个版本，多次显示
                int rejectNum = PrefserHelper.getIntCache(PrefserHelper.APK_UPDATE_REJECT_NUMBER_IN_SAME_VERSION);
                Log.e(TAG, "非强制更新，拒绝次数:" + rejectNum);
                if (rejectNum >= VersionDialog.REJECT_LIMIT_NUM) {
                    checkLoginStatus();
                } else {
                    String lastRejectTime = PrefserHelper.getCache(PrefserHelper.APK_UPDATE_LAST_REJECT_DATE);
                    long currentTime = System.currentTimeMillis();
                    if (!TextUtils.isEmpty(lastRejectTime)
                            && currentTime - Long.valueOf(lastRejectTime) < 1000 * 3600 * 24) {
                        Log.e(TAG, "普通更新，24小时内只显示一次");
                        checkLoginStatus();
                    } else {
                        showVersionDialog(version);
                    }
                }
            }
        }
    }

    private VersionDialog mVersionDialog; //版本检测弹窗

    private void showVersionDialog(LatestVersionBean version) {
        if (mVersionDialog == null) {
            mVersionDialog = new VersionDialog(this, 0);
            mVersionDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (!version.isForceUpdate()) {
                        checkLoginStatus();
                    }
                }
            });
        }
        mVersionDialog.setData(version);
        mVersionDialog.show();
    }

    /**
     * 检查版本失败
     */
    @Override
    public void checkUpdatesFail() {
        checkLoginStatus();
        //handler.sendEmptyMessageDelayed(HANDLER_MSG_CHECK_VERSION,5000);
    }

    /**
     * 检查登陆状态
     */
    private void checkLoginStatus() {
        if (PrefserHelper.isLoggedIn()) {
            presenter.updateVehicleList();
        } else {
            LoginActivity.start(getContext());
            finish();
        }
    }

    @Override
    public void openMainActivity() {
        // 打开主页面
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }



}

