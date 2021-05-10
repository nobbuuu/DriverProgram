package com.haylion.maastaxi.wxapi;

import android.app.Activity;
import android.os.Bundle;

import com.haylion.android.mvp.util.LogUtils;
import com.haylion.maastaxi.WxSdkUtils;
import com.haylion.maastaxi.event.WxAuthEvent;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import org.greenrobot.eventbus.EventBus;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private WxSdkUtils mWxHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWxHelper = WxSdkUtils.getInstance(this);
        mWxHelper.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        if (baseResp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
            notifyAuthResult((SendAuth.Resp) baseResp);
        } else {
            LogUtils.d("微信响应：" + baseResp.getType() + ", code: " + baseResp.errCode + ", msg: " + baseResp.errStr);
        }
    }

    private void notifyAuthResult(SendAuth.Resp authResp) {
        WxAuthEvent result = new WxAuthEvent();
        result.setTransaction(authResp.transaction);
        result.setErrCode(authResp.errCode);
        result.setErrMsg(authResp.errStr);
        result.setCode(authResp.code);
        result.setUrl(authResp.url);
        finish();
        EventBus.getDefault().post(result);
    }


}
