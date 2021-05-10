package com.haylion.android.data.model;

/**
 * EventBus 传递类
 * 使用于一些简单信息需要在各个页面传递
 */
public class EventBean {

    private int code;      //用code做判断,数据来源于 Constants.EventCode
    private Object data;   //携带的数据

    public EventBean(int code, Object data) {
        this.code = code;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
