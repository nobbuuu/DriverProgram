package com.haylion.android.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.amap.api.track.AMapTrackClient;
import com.amap.api.track.ErrorCode;
import com.amap.api.track.OnTrackLifecycleListener;
import com.amap.api.track.TrackParam;
import com.haylion.android.R;

import com.haylion.android.main.MainActivity;
import com.haylion.android.mvp.util.LogUtils;

/**
 * 猎鹰sdk 轨迹上报
 */
public class AmapTrackUploadService extends Service {
    private static final String TAG = "AmapTrackUploadService";
    private static final String CHANNEL_ID_SERVICE_RUNNING = "CHANNEL_ID_SERVICE_RUNNING";

    private AMapTrackClient aMapTrackClient;

    private long serviceId;
    private long terminalId;
    private long trackId;
    private boolean isServiceRunning;
    private boolean isGatherRunning;

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            startGather();
        }
    };

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
        // 不要使用Activity作为Context传入
        aMapTrackClient = new AMapTrackClient(getApplicationContext());
        //定位信息采集周期设置为5s，上报周期设置为30s
        aMapTrackClient.setInterval(5, 30);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            serviceId = intent.getLongExtra("sid", serviceId);
            terminalId = intent.getLongExtra("tid", terminalId);
            trackId = intent.getLongExtra("trid", trackId);
        }
        Log.d(TAG, "serviceId:" + serviceId + ",terminalId:" + terminalId + ",trackId:" + trackId);
        startTrack();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 开启猎鹰服务
     */
    private void startTrack() {
        // trackId需要在启动服务后设置才能生效，因此这里不设置，而是在startGather之前设置了track id
        TrackParam trackParam = new TrackParam(serviceId, terminalId);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            trackParam.setNotification(createNotification());
        }
        //开启轨迹服务
        aMapTrackClient.startTrack(trackParam, onTrackListener);
    }

    /**
     * 开始采集
     */
    private void startGather() {
        //设置轨迹id
        aMapTrackClient.setTrackId(trackId);
        //开启轨迹采集
        aMapTrackClient.startGather(onTrackListener);
    }

    /**
     * 用于监听服务的绑定和启停，以及采集的启停事件的监听器
     * */
    private OnTrackLifecycleListener onTrackListener = new SimpleOnTrackLifecycleListener() {

        /**
         * 绑定服务回调接口
         * @param status  结果错误码
         * @param msg    结果描述
         */
        @Override
        public void onBindServiceCallback(int status, String msg) {
            Log.d(TAG, "onBindServiceCallback, status: " + status + ", msg: " + msg);
        }

        /**
         * 开启服务回调接口
         * @param status 结果错误码
         * @param msg    结果描述
         */
        @Override
        public void onStartTrackCallback(int status, String msg) {
            LogUtils.d(TAG, "onStartTrackCallback, status: " + status + ", msg: " + msg);
            if (status == ErrorCode.TrackListen.START_TRACK_SUCEE || status == ErrorCode.TrackListen.START_TRACK_SUCEE_NO_NETWORK) {
                // 成功启动
                isServiceRunning = true;
                //add by alec
                handler.sendEmptyMessageDelayed(100, 2000);

            } else if (status == ErrorCode.TrackListen.START_TRACK_ALREADY_STARTED) {
                // 已经启动
                isServiceRunning = true;
                //add by alec
                handler.sendEmptyMessageDelayed(100, 2000);
            } else {
                Log.e(TAG, "error onStartTrackCallback, status: " + status + ", msg: " + msg);
            }
        }

        /**
         * 停止服务回调接口
         * @param status
         * @param msg
         */
        @Override
        public void onStopTrackCallback(int status, String msg) {
            LogUtils.d(TAG, "onStopTrackCallback, status: " + status + ", msg: " + msg);
            if (status == ErrorCode.TrackListen.STOP_TRACK_SUCCE) {
                // 成功停止
                isServiceRunning = false;
                isGatherRunning = false;
            } else {
                Log.e(TAG, "error onStopTrackCallback, status: " + status + ", msg: " + msg);
            }
        }

        /**
         * 开启采集回调接口
         * @param status
         * @param msg
         */
        @Override
        public void onStartGatherCallback(int status, String msg) {
            LogUtils.d(TAG, "onStartGatherCallback, status: " + status + ", msg: " + msg);
            if (status == ErrorCode.TrackListen.START_GATHER_SUCEE) { //定位采集开启成功
                isGatherRunning = true;
            } else if (status == ErrorCode.TrackListen.START_GATHER_ALREADY_STARTED) {  //定位采集已经开启
                isGatherRunning = true;
            } else {
                Log.e(TAG, "error onStartGatherCallback, status: " + status + ", msg: " + msg);
            }
        }

        /**
         * 停止采集回调接口
         * @param status
         * @param msg
         */
        @Override
        public void onStopGatherCallback(int status, String msg) {
            LogUtils.d(TAG, "onStopGatherCallback, status: " + status + ", msg: " + msg);
            if (status == ErrorCode.TrackListen.STOP_GATHER_SUCCE) {
                isGatherRunning = false;
            } else {
                Log.e(TAG, "error onStopGatherCallback, status: " + status + ", msg: " + msg);
            }
        }
    };



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    /**
     * 在8.0以上手机，如果app切到后台，系统会限制定位相关接口调用频率
     * 可以在启动轨迹上报服务时提供一个通知，这样Service启动时会使用该通知成为前台Service，可以避免此限制
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private Notification createNotification() {
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID_SERVICE_RUNNING, "amap track service", NotificationManager.IMPORTANCE_LOW);
            nm.createNotificationChannel(channel);
            builder = new Notification.Builder(getApplicationContext(), CHANNEL_ID_SERVICE_RUNNING);
        } else {
            builder = new Notification.Builder(getApplicationContext());
        }
        Intent nfIntent = new Intent(this, MainActivity.class); //todo
        nfIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        builder.setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, 0))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("猎鹰sdk运行中")
                .setContentText("猎鹰sdk运行中");
        Notification notification = builder.build();
        return notification;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isServiceRunning) {
            aMapTrackClient.stopTrack(new TrackParam(serviceId, terminalId), new SimpleOnTrackLifecycleListener());
        }
    }
}
