package com.haylion.android.data.model;

import com.haylion.android.mvp.base.BaseWebsocketResponse;

public class WebsocketData extends BaseWebsocketResponse<WebsocketOrderInfo> {

    public WebsocketData(int cmdSn, String type, String cmd, WebsocketOrderInfo data) {
        super(cmdSn, type, cmd, data);
    }


}
