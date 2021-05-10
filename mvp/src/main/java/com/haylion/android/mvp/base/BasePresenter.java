package com.haylion.android.mvp.base;

import android.text.TextUtils;

import com.haylion.android.mvp.rx.ApiSubscriber;
import com.haylion.android.mvp.util.BusUtils;
import com.haylion.android.mvp.util.LogUtils;

import androidx.annotation.StringRes;

public abstract class BasePresenter<V extends AbstractView, R extends BaseRepository>
        implements AbstractPresenter {

    protected V view;
    protected R repo;

    public BasePresenter() {
    }

    public BasePresenter(V view) {
        this.view = view;
    }

    public BasePresenter(V view, R repo) {
        this.view = view;
        this.repo = repo;
    }

    @Override
    public void onViewCreated() {
        if (repo != null) {
            repo.onCreate();
        }
        if (useEventBus()) {
            BusUtils.register(this);
        }
    }

    protected void toast(CharSequence msg) {
        if (view != null) {
            view.toast(msg);
        } else {
            LogUtils.e(getClass().getSimpleName() + " has null view!");
        }
    }

    protected void toast(@StringRes int strResId) {
        if (view != null) {
            view.toast(strResId);
        } else {
            LogUtils.e(getClass().getSimpleName() + " has null view!");
        }
    }

    /**
     * 提示给用户看的
     * @param apiMsg
     * @param errorMsg
     */
    protected void toastError(String apiMsg,String errorMsg){
        if (view != null) {
            if(!TextUtils.isEmpty(errorMsg) &&
                    (("系统维护中").equals(errorMsg) || ("当前网络不可用").equals(errorMsg))){
                view.toast(errorMsg);
            }else if(!TextUtils.isEmpty(apiMsg)){
                view.toast(apiMsg);
            }
        } else {
            LogUtils.e(getClass().getSimpleName() + " has null view!");
        }
    }

    protected void toastError(@StringRes int strResId,String errorMsg){
        if (view != null) {
            if(!TextUtils.isEmpty(errorMsg) &&
                    (("系统维护中").equals(errorMsg) || ("当前网络不可用").equals(errorMsg))){
                view.toast(errorMsg);
            }else{
                view.toast(strResId);
            }
        } else {
            LogUtils.e(getClass().getSimpleName() + " has null view!");
        }
    }

    @Override
    public void onViewDestroy() {
        if (repo != null) {
            repo.onDestroy();
        }
        if (useEventBus()) {
            BusUtils.unregister(this);
        }
    }

    /**
     * 是否使用事件总线（默认使用）
     */
    protected boolean useEventBus() {
        return false;
    }

}
