package com.haylion.maastaxi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import com.haylion.android.BuildConfig;
import com.haylion.android.R;
import com.haylion.android.data.repo.PrefserHelper;
import com.haylion.android.mvp.util.ToastUtils;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

public class WxSdkUtils {

    private static final Object mLock = new Object();
    private Context mAppContext;
    private static WxSdkUtils mInstance;

    private IWXAPI mWXApi;

    public static WxSdkUtils getInstance(@NonNull Context context) {
        synchronized (mLock) {
            if (mInstance == null) {
                mInstance = new WxSdkUtils(context.getApplicationContext());
            }
            return mInstance;
        }
    }

    private WxSdkUtils(Context context) {
        this.mAppContext = context;
    }

    public void registerApp(String appId) {
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        mWXApi = WXAPIFactory.createWXAPI(mAppContext, appId, true);
        // 将应用的appId注册到微信
        mWXApi.registerApp(appId);

        //建议动态监听微信启动广播进行注册到微信
        mAppContext.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // 将该app注册到微信
                mWXApi.registerApp(appId);
            }
        }, new IntentFilter(ConstantsAPI.ACTION_REFRESH_WXAPP));
    }

    public boolean isWxInstalled() {
        return mWXApi.isWXAppInstalled();
    }

    public void handleIntent(Intent intent, IWXAPIEventHandler handler) {
        mWXApi.handleIntent(intent, handler);
    }

    /**
     * 发起微信授权请求
     */
    public void sendAuthRequest() {
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "anti_csrf";
        mWXApi.sendReq(req);
    }

    /**
     * 发送消息到微信会话列表
     *
     * @param transaction 会话id
     * @param msg         消息
     */
    public void sendMsgToSession(String transaction, WXMediaMessage msg) {
        sendMsgRequest(transaction, SendMessageToWX.Req.WXSceneSession, msg);
    }

    /**
     * 发送消息到微信
     *
     * @param transaction 会话id
     * @param scene       {@link SendMessageToWX.Req} 发送的事务场景
     * @param msg         消息
     */
    public void sendMsgRequest(String transaction, int scene, WXMediaMessage msg) {
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = transaction;
        req.message = msg;
        req.scene = scene;
        mWXApi.sendReq(req);
    }

    /**
     * 打开小程序
     *
     * @param userName 小程序原始id
     */
    public void launchMiniProgram(String userName, String path) {
        if (TextUtils.isEmpty(userName)) {
            ToastUtils.showShort(mAppContext, "小程序id不能为空");
            return;
        }
        WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
        req.userName = userName;
        if (TextUtils.isEmpty(path)) {
            path = "/pages/index/index";
        }
        req.path = path;
        if (TextUtils.equals(BuildConfig.BUILD_TYPE, "debug")) { // 测试版:1
            req.miniprogramType = WXMiniProgramObject.MINIPROGRAM_TYPE_TEST;
        } else if (TextUtils.equals(BuildConfig.BUILD_TYPE, "beta")) { // 体验版:2
            req.miniprogramType = WXMiniProgramObject.MINIPROGRAM_TYPE_PREVIEW;
        } else {
            req.miniprogramType = WXMiniProgramObject.MINIPTOGRAM_TYPE_RELEASE; // 正式版:0
        }
        mWXApi.sendReq(req);
    }

    /**
     * 发送请求
     *
     * @param request 请求
     */
    public void sendRequest(BaseReq request) {
        mWXApi.sendReq(request);
    }

}