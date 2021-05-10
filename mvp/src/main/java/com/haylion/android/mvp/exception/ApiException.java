package com.haylion.android.mvp.exception;


import java.io.IOException;

public class ApiException extends IOException {

    private int code;

    public ApiException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        if (code == 500) {
          //  return "系统繁忙";
            return "系统维护中";
        }
        return super.getMessage();
    }

}
