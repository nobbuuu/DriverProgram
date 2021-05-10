package com.haylion.android.data.model;

import com.haylion.android.mvp.base.BaseWebsocketResponse;

public class WebsocketVoidData extends BaseWebsocketResponse<Void> {

    public WebsocketVoidData(int cmdSn, String type, String cmd, Void data) {
        super(cmdSn, type, cmd, data);
    }
}
