package com.haylion.android.mvp.base;

import com.google.gson.annotations.SerializedName;

public class BaseResponse<T> {

    @SerializedName("message")
    private String msg;

    private int code; // 状态码

    private T data;

    public BaseResponse(String msg, int code, T data) {
        this.msg = msg;
        this.code = code;
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
