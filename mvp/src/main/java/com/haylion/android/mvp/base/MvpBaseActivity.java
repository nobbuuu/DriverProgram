package com.haylion.android.mvp.base;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.gyf.immersionbar.ImmersionBar;
import com.haylion.android.mvp.R;
import com.haylion.android.mvp.util.BusUtils;
import com.haylion.android.mvp.util.LogUtils;
import com.haylion.android.mvp.util.ToastUtils;
import com.haylion.android.mvp.widget.TitleBar;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class MvpBaseActivity<P extends AbstractPresenter> extends AppCompatActivity
        implements AbstractView, TitleBar.OnClickListener {

    private Toast mToast = null;
    private Unbinder unbinder;

    protected P presenter;
    private List<? extends AbstractPresenter> mMorePresenters;

    protected final RxPermissions rxPermissions = new RxPermissions(this);
    private CompositeDisposable permissionRequests;

    protected TitleBar mTitleBar;
    protected ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        AppManager.getAppManager().addActivity(this);
    }

    @Override
    public void setContentView(@LayoutRes int layoutId) {
        setContentView(View.inflate(getContext(), layoutId, null));
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        unbinder = ButterKnife.bind(this);
        mTitleBar = findViewById(R.id.title_bar);
        if (mTitleBar != null) {
            mTitleBar.setOnClickListener(this);
        }
        beforeCreatePresenter();
        presenter = onCreatePresenter();
        mMorePresenters = addMorePresenter();
        if (presenter != null) {
            presenter.onViewCreated();
        }
        if (mMorePresenters != null) {
            for (int i = 0; i < mMorePresenters.size(); i++) {
                if (mMorePresenters.get(0) != null) {
                    mMorePresenters.get(i).onViewCreated();
                }
            }
        }
        if (useEventBus()) {
            BusUtils.register(this);
        }

        if (lightStatusBar()) {
            ImmersionBar.with(this).statusBarColor(R.color.white)
                    .statusBarDarkFont(true, 0.2f)
                    .fitsSystemWindows(true)  //如果让View置顶状态栏，设为false
                    .init();
        }
        //添加了这句话，无法让内容置顶到状态栏,ImmersionBar相关设置失效
        //view.setFitsSystemWindows(true);
    }

    /**
     * 默认使用浅色状态栏
     */
    protected boolean lightStatusBar() {
        return true;
    }

    /**
     * 在创建presenter之前执行的逻辑
     */
    protected void beforeCreatePresenter() {
    }

    protected P onCreatePresenter() {
        return null;
    }

    /**
     * 添加更多Presenter（复用）
     */
    protected List<? extends AbstractPresenter> addMorePresenter() {
        return null;
    }

    @Override
    public void onLeftClick() {
        finish();
    }

    @Override
    public void onTitleClick() {

    }

    @Override
    public void onRightClick() {

    }

    protected void addPermissionRequest(Disposable disposable) {
        if (permissionRequests == null) {
            permissionRequests = new CompositeDisposable();
        }
        permissionRequests.add(disposable);
    }

    protected Context getContext() {
        return this;
    }

    public void toast(@StringRes int msgRes) {
        ToastUtils.showShort(getContext(), msgRes);
    }

    @SuppressLint("ShowToast")
    public void toast(CharSequence msg) {
        ToastUtils.showShort(getContext(), msg);
    }

    /**
     * 是否使用事件总线（默认不使用）
     */
    protected boolean useEventBus() {
        return false;
    }

    @Override
    public void showProgressDialog(@StringRes int stringResId) {
        showProgressDialog(getString(stringResId));
    }

    @Override
    public void showProgressDialog(CharSequence msg) {
        if (isProgressDialogShowing()) {
            mProgressDialog.setMessage(msg);
            return;
        }
        mProgressDialog = ProgressDialog.show(getContext(), null, msg, true, false);
    }

    private boolean isProgressDialogShowing() {
        return mProgressDialog != null && mProgressDialog.isShowing();
    }

    @Override
    public void dismissProgressDialog() {
        if (isProgressDialogShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    protected void willDestroy() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().removeActivity(this);
        willDestroy();
        if (useEventBus()) {
            BusUtils.unregister(this);
        }
        if (presenter != null) {
            presenter.onViewDestroy();
        }
        if (mMorePresenters != null) {
            for (int i = 0; i < mMorePresenters.size(); i++) {
                if (mMorePresenters.get(0) != null) {
                    mMorePresenters.get(i).onViewDestroy();
                }
            }
        }
        if (permissionRequests != null && !permissionRequests.isDisposed()) {
            permissionRequests.dispose();
            permissionRequests = null;
        }
        unbinder.unbind();
        dismissProgressDialog();
    }

    private static long lastClickTime = 0;
    private static final int CLICK_TIME = 500; //快速点击间隔时间

    /**
     * 判断按钮是否快速点击，取绝对值是因为用户可能改了系统时间
     *
     * @return true-是  false-不是
     */
    public static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if (Math.abs(time - lastClickTime) < CLICK_TIME) {//判断系统时间差是否小于点击间隔时间
            return true;
        }
        lastClickTime = time;
        return false;
    }


}
