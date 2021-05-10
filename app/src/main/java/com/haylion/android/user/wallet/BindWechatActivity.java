package com.haylion.android.user.wallet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.haylion.android.BuildConfig;
import com.haylion.android.R;
import com.haylion.android.data.base.BaseActivity;
import com.haylion.android.mvp.util.LogUtils;
import com.haylion.maastaxi.WxSdkUtils;
import com.haylion.maastaxi.event.WxAuthEvent;
import com.tencent.mm.opensdk.modelbase.BaseResp;

import org.greenrobot.eventbus.Subscribe;

import androidx.annotation.Nullable;
import butterknife.OnClick;

public class BindWechatActivity extends BaseActivity {

    @OnClick({R.id.btn_back, R.id.bind_wechat})
    void onBindWechat(View view) {
        if (view.getId() == R.id.btn_back) {
            finish();
        } else if (view.getId() == R.id.bind_wechat) {
            wxSdkUtils.sendAuthRequest();
        }
    }

    private WxSdkUtils wxSdkUtils;

    public static void start(Context context) {
        Intent intent = new Intent(context, BindWechatActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_wechat);
        wxSdkUtils = WxSdkUtils.getInstance(getContext());
        // 在微信里注册app
        wxSdkUtils.registerApp(BuildConfig.WX_APPID);
    }

    @Override
    protected boolean useEventBus() {
        return true;
    }

    @Subscribe
    public void onWxLoginResult(WxAuthEvent result) {
        switch (result.getErrCode()) {
            case BaseResp.ErrCode.ERR_OK:
                LogUtils.d("微信登录成功：" + result.getCode());
                finish();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                toast("拒绝微信登录");
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                toast("取消微信登录");
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}
