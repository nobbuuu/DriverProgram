package com.haylion.android.mvp.callback;

public interface CommonCallback<T> {

    void onSuccess(T data);

    void onError(int code, String msg);

}
