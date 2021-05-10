package com.haylion.android.mvp.net;


import android.text.TextUtils;

import com.haylion.android.mvp.exception.ApiException;

import retrofit2.HttpException;

public final class ErrorMessageHandler {

    private ErrorMessageHandler() {
        // hide constructor
    }

    /**
     * 处理HTTP错误
     *
     * @param code 状态码
     */
    private static String handleHttpError(int code) {
        switch (code) {
            case 400:
                return "错误的请求";
            case 401:
                return "请求未授权";
            case 404:
                return "请求地址不存在";
            case 405:
                return "请求方法不允许";
            case 500:
                return "系统繁忙";
            case 502:
                return "系统维护中";
        }
        return null;
    }

    public static String handle(Throwable e) {
        String msg = null;
        if (e instanceof HttpException) {
            msg = handleHttpError(((HttpException) e).code());
        } else if (e instanceof ApiException) {
            msg = handleApiError(((ApiException) e).getCode());
        }
        if (TextUtils.isEmpty(msg)) {
            //此处不要返回空，不然无法定位到具体问题
            return e.getMessage();
        }
        return msg;
    }

    /**
     * 处理API错误
     *
     * @param code 错误码
     */
    private static String handleApiError(int code) {
        switch (code) {

        }
        return null;
    }


}
