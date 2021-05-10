package com.haylion.android.data.model;


import com.haylion.android.mvp.base.BaseWebsocketResponse;


public class WebsocketListData extends BaseWebsocketResponse<WebsocketOrderListData> {

    public WebsocketListData(int cmdSn, String type, String cmd, WebsocketOrderListData data) {
        super(cmdSn, type, cmd, data);
    }
}
