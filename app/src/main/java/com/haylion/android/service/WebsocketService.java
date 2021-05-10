package com.haylion.android.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.google.gson.Gson;
import com.haylion.android.BuildConfig;
import com.haylion.android.data.model.WebsocketData;
import com.haylion.android.data.model.WebsocketVoidData;
import com.haylion.android.data.repo.PrefserHelper;
import com.haylion.android.mvp.util.LogUtils;
import com.tencent.bugly.crashreport.CrashReport;

import org.greenrobot.eventbus.EventBus;

import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.Nullable;
import io.crossbar.autobahn.websocket.WebSocketConnection;
import io.crossbar.autobahn.websocket.WebSocketConnectionHandler;
import io.crossbar.autobahn.websocket.exceptions.WebSocketException;
import io.crossbar.autobahn.websocket.types.ConnectionResponse;
import io.crossbar.autobahn.websocket.types.WebSocketOptions;

/**
 * @class WebsocketService
 * @description websocket服务
 * @date: 2019/12/17 10:48
 * @author: tandongdong
 */
public class WebsocketService extends Service {
    private static final String TAG = "WebsocketService";

    public final static String WEB_SOCKET_HOST = BuildConfig.API_WEBSOCKET_URL + "driver/";//这个是后台给的地址，根据自家的地址写上去即可
    private static WebSocketConnection webSocketConnection; // websocket 对应的类
    private static WebSocketOptions options = new WebSocketOptions(); //websocket的个选项，声明出来即可使用了
    Timer mHeartBeatSendTimer; //发送心跳消息定时器
    Timer mHeartBeatCheckTimer; //心跳检测定时器

    private boolean isOpen;//websocket打开的状态
    private String token; //登录的token
    private final static int HEARTHEAT_INTERVAL = 10 * 1000; //10秒
    private final static int MESSAGE_RECONNECT = 1001; //重新连接

    private long mLastHeartbeat = System.currentTimeMillis();  //最近一次收到数据的时间

    /**
     * 解锁屏广播
     */
    public static boolean isScreenLock;
    private ScreenLockReceiver mLockReceiver;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MESSAGE_RECONNECT) {
                webSocketConnect();
            }
        }
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onCreate() {
        super.onCreate();
        registerReceiver();
        isScreenLock = false;
        startHeartBeatCheck();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        webSocketConnect();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        LogUtils.d(TAG, "onDestroy");
        super.onDestroy();
        unRegisterReceiver();
        isScreenLock = false;
        closeWebsocket();
        stopHeartBeat();
    }

    /**
     * 连接websocket
     */
    public void webSocketConnect() {

        if (webSocketConnection == null) {
            webSocketConnection = new WebSocketConnection();
        }

        try {
            token = PrefserHelper.getToken();
            String websocket = WEB_SOCKET_HOST + token;
            LogUtils.d(TAG, "websocket connect  URL: " + websocket);

            webSocketConnection.connect(websocket, new WebSocketConnectionHandler() {
                @Override
                public void onConnect(ConnectionResponse response) {
                    super.onConnect(response);
                }

                /**
                 * Fired when the WebSockets connection has been established.
                 * After this happened, messages may be sent.
                 */
                @Override
                public void onOpen() {
                    super.onOpen();
                    isOpen = true;
                    LogUtils.d(TAG, "websocket 连接打开！");

                    //打开后立刻发送一次心跳数据。
                    sendMsg("连接打开后，立刻发送一次心跳数据");

                    sendHeatBeatMessage(); //启动心跳定时器

                    LogUtils.d(TAG, "websocket 打开后，立刻拉取订单信息！");
                    WebsocketVoidData websocketData = new WebsocketVoidData(WsCommands.DRIVER_NEW_ORDER.getSn(), "notification", "newOrder", null);
                    EventBus.getDefault().post(websocketData);

                    //断流重连
                    WebsocketVoidData meituanWsvd = new WebsocketVoidData(WsCommands.WEBSOCKET_CLOSE_TO_CONNECT.getSn(), "notification", "newOrder", null);
                    EventBus.getDefault().post(meituanWsvd);
                }

                /**
                 * Fired when the WebSockets connection has deceased (or could
                 * not established in the first place).
                 *
                 * @param code   Close code.
                 * @param reason Close reason (human-readable).
                 */
                @Override
                public void onClose(int code, String reason) {
                    super.onClose(code, reason);
                    isOpen = false;
                    LogUtils.d(TAG, "websocket 连接关闭！" + "code:" + code + ",reason:" + reason);
                    token = PrefserHelper.getToken();
//                    LogUtils.d(TAG, "token:" + token);
                    if (token == null || token.length() == 0) {
                        LogUtils.e(TAG, "token is null");
                        return;
                    }
                }

                @Override
                public void onMessage(String payload) {
                    super.onMessage(payload);
                    LogUtils.d(TAG, "收到的websocket消息： " + payload);
                    //处理收到的消息
                    handleReceivedMessage(payload);
                    mLastHeartbeat = System.currentTimeMillis();
                }

            }, options);
        } catch (WebSocketException e) {
            e.printStackTrace();
            LogUtils.d(TAG, "websocket 打开异常");
            isOpen = false;
            closeWebsocket();
        }
    }

    /**
     * 关闭websocket
     */
    public void closeWebsocket() {
        if (webSocketConnection != null && webSocketConnection.isConnected()) {
            webSocketConnection.sendClose();
            webSocketConnection = null;
        }
    }

    /**
     * websocket是否连接
     *
     * @return
     */
    public boolean isOpen() {
        return isOpen;
    }

    /**
     * 发送信息，我们发送都是使用base64加密
     *
     * @param base64
     */
    public void sendMsg(String base64) {
        try {
            if (webSocketConnection != null && webSocketConnection.isConnected()) {
                LogUtils.d(TAG, "websocket 发送  message" + base64);
                webSocketConnection.sendMessage(base64);
            }
        } catch (Throwable throwable) {
            CrashReport.postCatchedException(throwable);
            throwable.printStackTrace();
        }
    }


    /**
     * 处理收到的websocket消息
     *
     * @param payload
     */
    public void handleReceivedMessage(String payload) {
        LogUtils.d(TAG, "websocket payload: " + payload);

        Gson gson = new Gson();
        if (payload.indexOf("newOrder") > 0) {
            WebsocketVoidData websocketData = gson.fromJson(payload, (WebsocketVoidData.class));

            if (("response").equals(websocketData.getType())) {
                if (("linkError").equals(websocketData.getCmd())) {
                    LogUtils.e(TAG, "连接失败" + websocketData.getData());
                }
            } else if (("notification").equals(websocketData.getType())) {
                if (("newOrder").equals(websocketData.getCmd())) {
                    LogUtils.d(TAG, "新订单," + websocketData.getData());
                }
            }
            EventBus.getDefault().post(websocketData);
        } else {
            WebsocketData websocketData = gson.fromJson(payload, (WebsocketData.class));
            LogUtils.d(TAG, "websocketData: " + websocketData.getData());
            EventBus.getDefault().post(websocketData);
        }
    }

    /**
     * 启动发送心跳数据的定时器
     */
    public void sendHeatBeatMessage() {
        LogUtils.d(TAG, "sendHeatBeatMessage");
        if (mHeartBeatSendTimer == null) {
            mHeartBeatSendTimer = new Timer();
            mHeartBeatSendTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    sendMsg(HEARTHEAT_INTERVAL / 1000 + "秒发送一次心跳数据");
                }
            }, 0, HEARTHEAT_INTERVAL);
        }
    }

    /**
     * 启动心跳检测的定时器
     */
    private void startHeartBeatCheck() {
        if (mHeartBeatCheckTimer == null) {
            mHeartBeatCheckTimer = new Timer();
        }
        mHeartBeatCheckTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                LogUtils.d(TAG, "心跳检测：");
                if (System.currentTimeMillis() - mLastHeartbeat > (HEARTHEAT_INTERVAL + 2000)) {
                    //进到自己就说明websocket的心跳在规定的时间内 未 接收信息，看自己的需求了
                    //比如stopService   ， 在startService
                    //或者将有关websocket获取的数据的一系列操作给Gone掉，屏蔽掉，提个示 whatever...
                    //这样就保证了 activity这边的关于websocket的一系列操作都是可行的！有反应的！！！及 以activity为主导进行操作！
                    LogUtils.e(TAG, "收不到心跳，websocket 已经断掉");

                    LogUtils.d(TAG, "websocket 开始重新连接");
                    Message message = new Message();
                    message.what = MESSAGE_RECONNECT;
                    handler.sendMessage(message);
                }
            }
        }, 0, 3 * 1000L);//
    }

    /**
     * @param
     * @return
     * @method
     * @description 停止心跳检测
     * @date: 2019/12/17 10:58
     * @author: tandongdong
     */
    private void stopHeartBeat() {
        if (mHeartBeatCheckTimer != null) {
            mHeartBeatCheckTimer.cancel();
        }

        if (mHeartBeatSendTimer != null) {
            mHeartBeatSendTimer.cancel();
        }
    }


    /**
     * 注册广播
     */
    private void registerReceiver() {
        LogUtils.d(TAG, "注册屏幕打开和关闭的广播");
        mLockReceiver = new ScreenLockReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_USER_PRESENT); //开屏
        registerReceiver(mLockReceiver, filter);
    }

    /**
     * 注销广播
     */
    public void unRegisterReceiver() {
        if (mLockReceiver != null) {
            LogUtils.d(TAG, "注销屏幕打开和关闭的广播");
            unregisterReceiver(mLockReceiver);
        }
    }

    /**
     * 广播接收者
     * 接收锁屏/解锁广播
     */
    class ScreenLockReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null) {
                return;
            }
            switch (intent.getAction()) {
                case Intent.ACTION_SCREEN_OFF:  //锁屏
                    LogUtils.d(TAG, "锁屏----");
                    isScreenLock = true;
                    //   EventBus.getDefault().post(new ScreenLockEvent(true));
                    break;
                case Intent.ACTION_SCREEN_ON:  //解锁
                    LogUtils.d(TAG, "解锁----");
                    break;
                case Intent.ACTION_USER_PRESENT: //开屏
                    LogUtils.d(TAG, "开屏----");
                    isScreenLock = false;
                    //   EventBus.getDefault().post(new ScreenLockEvent(false));
                    break;
            }
        }
    }

}

