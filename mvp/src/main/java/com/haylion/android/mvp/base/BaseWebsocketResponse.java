package com.haylion.android.mvp.base;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;




public abstract class BaseWebsocketResponse<T> {

    private int cmdSn;

    private String type;

    private String cmd; // 状态码

    @Nullable
    private T data;

    public BaseWebsocketResponse(int cmdSn, String type, String cmd, T data) {
        this.cmdSn = cmdSn;
        this.type = type;
        this.cmd = cmd;
        this.data = data;
    }

    public int getCmdSn() {
        return cmdSn;
    }

    public void setCmdSn(int cmdSn) {
        this.cmdSn = cmdSn;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }


    @Override
    public String toString() {
        return "{" +
                "cmdSn=" + cmdSn +
                ", type='" + type + '\'' +
                ", cmd='" + cmd + '\'' +
                ", data=" + data +
                '}';
    }
}