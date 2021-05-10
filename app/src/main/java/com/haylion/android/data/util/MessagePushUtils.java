package com.haylion.android.data.util;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.haylion.android.BuildConfig;
import com.haylion.android.data.repo.PrefserHelper;
import com.haylion.android.mvp.util.LogUtils;
import com.haylion.android.notification.NotificationListActivity;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

import org.android.agoo.huawei.HuaWeiRegister;
import org.android.agoo.oppo.OppoRegister;
import org.android.agoo.vivo.VivoRegister;
import org.android.agoo.xiaomi.MiPushRegistar;

 /**
  * @class  MessagePushUtils
  * @description 消息推送功能的工具类
  * @date: 2019/12/16 16:48
  * @author: tandongdong
  */
public class MessagePushUtils {
    private static final String TAG = "MessagePushUtils";

    /**
     * @param
     * @return
     * @method
     * @description 设备厂商通道的推送功能的初始化
     * @date: 2019/12/16 16:47
     * @author: tandongdong
     */
    public static void deviceChannelMessagePushInit(Application context) {
        //华为通道
        HuaWeiRegister.register(context);

        //OPPO通道，参数1为app key，参数2为app secret
        OppoRegister.register(context, "0890e5ddc8284de7bd58c4052917f63b", "b710ab1e929e454db7e3b79fc3b6a1f7");

        //vivo 通道
        VivoRegister.register(context);

        //小米
        MiPushRegistar.register(context, "2882303761518276851", "5491827636851");
    }

    /**
     * @param
     * @return
     * @method
     * @description 友盟消息推送初始化配置
     * @date: 2019/12/10 10:20
     * @author: tandongdong
     */
    public static void umengPushInit(Application context) {

        //设备厂商通道
        deviceChannelMessagePushInit(context);

        //友盟自身的推送通道
        // 在此处调用基础组件包提供的初始化函数 相应信息可在应用管理 -> 应用信息 中找到 http://message.umeng.com/list/apps
        // 参数一：当前上下文context；
        // 参数二：应用申请的Appkey（需替换）；
        // 参数三：渠道名称；
        // 参数四：设备类型，必须参数，传参数为UMConfigure.DEVICE_TYPE_PHONE则表示手机；传参数为UMConfigure.DEVICE_TYPE_BOX则表示盒子；默认为手机；
        // 参数五：Push推送业务的secret 填充Umeng Message Secret对应信息（需替换）

        LogUtils.d(TAG, "umeng app key:" + BuildConfig.UMENG_PUSH_APP_KEY +  ", Umeng app message secret:" + BuildConfig.UMENG_PUSH_MESSAGE_SECRET);
        UMConfigure.init(context, BuildConfig.UMENG_PUSH_APP_KEY, "Umeng", UMConfigure.DEVICE_TYPE_PHONE, BuildConfig.UMENG_PUSH_MESSAGE_SECRET);
        //产品环境
//        UMConfigure.init(context, "5de77025570df3004e00044a", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, "17337a9e858dc62ba4a90f7f0c00de4d");

        //开发环境
//        UMConfigure.init(context, "5df7348f0cafb2b770000eeb", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, "16706e342ce1eccceeb2725b52e59ce9");

        //测试环境
//        UMConfigure.init(context, "5df6facf3fc195f39b000060", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, "1b6111fe7f8d748aef947131202203f2");

        //陈承杰账号申请的
//        UMConfigure.init(context, "5de9fd493fc195276b000872", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, "9c2517212200b759d69fa1889e918f49");

        //获取消息推送代理示例
        PushAgent mPushAgent = PushAgent.getInstance(context);
        mPushAgent.setResourcePackageName("com.haylion.android");

        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回deviceToken deviceToken是推送消息的唯一标志
                Log.i(TAG, "注册成功：deviceToken：-------->  " + deviceToken);
                PrefserHelper.setCache(PrefserHelper.KEY_UMENG_PUSH_TOKEN, deviceToken);
//                Log.d(TAG, "login token:" + PrefserHelper.getToken());
                if (!TextUtils.isEmpty(PrefserHelper.getToken())) {
                    mPushAgent.setAlias(PrefserHelper.getToken(), "token", new UTrack.ICallBack() {
                        @Override
                        public void onMessage(boolean b, String s) {
                            Log.d(TAG, "别名绑定, isSuccess:" + b + ",s:" + s);
                        }
                    });
                }
            }

            @Override
            public void onFailure(String s, String s1) {
                Log.e(TAG, "注册失败：-------->  " + "s:" + s + ",s1:" + s1);
            }
        });


        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {

            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(context, NotificationListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        };
        mPushAgent.setNotificationClickHandler(notificationClickHandler);
    }
}
