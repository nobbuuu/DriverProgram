package com.haylion.android.mvp.base;


public class ListReponse<T> extends BaseResponse<ListData<T>> {

    public ListReponse(String msg, int code, ListData<T> data) {
        super(msg, code, data);
    }


}
