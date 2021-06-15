package com.haylion.android.mvp.rx;


import android.util.Log;

import com.google.gson.JsonSyntaxException;
import com.haylion.android.mvp.BuildConfig;
import com.haylion.android.mvp.exception.ApiException;
import com.haylion.android.mvp.exception.NoConnectivityException;
import com.haylion.android.mvp.exception.NullDataException;
import com.haylion.android.mvp.net.ErrorMessageHandler;
import com.haylion.android.mvp.util.BusUtils;
import com.haylion.android.mvp.util.LogUtils;

import java.io.EOFException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import javax.net.ssl.SSLException;

import io.reactivex.exceptions.Exceptions;
import io.reactivex.observers.DisposableObserver;
import retrofit2.HttpException;

public abstract class ApiSubscriber<T> extends DisposableObserver<T> {

    private static final int DEFAULT_ERROR_CODE = -23333;

    @Override
    public void onComplete() {
    }

    /**
     * 处理所有的error
     * 包括 API异常 和 非API异常
     * 1.HttpException 统一toast，"系统维护中"
     */
    @Override
    public final void onError(Throwable e) {
        Log.e("onError","e = " + e.getMessage());
        if (isDisposed()) {
            return;
        }
        if (e instanceof NullDataException) {
            onNext(null);
            return;
        }

        int code = DEFAULT_ERROR_CODE;
        String msg = e.getMessage();

        if (e instanceof HttpException) {
            code = ((HttpException) e).code();
            msg = "系统维护中";
           // msg = ErrorMessageHandler.handle(e);
        } else if (e instanceof ConnectException) {
            msg = "连接失败";
        }else if (e instanceof EOFException) {
            msg = "请求中断";
        } else if (e instanceof SSLException) {
            msg = "SSL连接异常";
        } else if (e instanceof JsonSyntaxException) {
            msg = "响应数据格式不正确";
        } else if (e instanceof SocketTimeoutException) {
            msg = "请求超时";
        } else if (e instanceof ApiException) {
            code = ((ApiException) e).getCode();
            if (code == 406) {
                BusUtils.post(new ReloginEvent());
                return;
            }
            msg = ErrorMessageHandler.handle(e);
        }else if (e instanceof NoConnectivityException) {
            msg = "当前网络不可用";
        }
        if (BuildConfig.DEBUG) {
            e.printStackTrace();
        }
        onError(code, msg);
        dispose();
    }

    @Override
    public final void onNext(T t) {
        if (!isDisposed()) {
            try {
                onSuccess(t);
            } catch (Throwable e) {
                Exceptions.throwIfFatal(e);
                onError(e);
                dispose();
            }
        }
    }

    public abstract void onSuccess(T t);

    public abstract void onError(int code, String msg);

}
