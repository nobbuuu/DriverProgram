package com.haylion.android.mvp.base;

import androidx.annotation.StringRes;

public interface AbstractView {

    void toast(CharSequence msg);

    void toast(@StringRes int msgResId);

    void showProgressDialog(@StringRes int stringResId);

    void showProgressDialog(CharSequence msg);

    void dismissProgressDialog();

}
