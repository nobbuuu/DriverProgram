package com.haylion.android.common.aibus_location;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.haylion.android.R;
import com.haylion.android.common.aibus_location.data.GpsData;

/**
 * 封装高德地图的位置服务，对外提供统一的接口
 * 单列模式，
 */
public class DeviceLocationManager implements AMapLocationListener {
    private static final String TAG = "DeviceLocationManager";
    private Context mContext;
    private LocationHelper locationHelper;
    private Boolean mockLocationEnable = false;
    private Boolean naviEmulatorEnable = false;
    private GpsData mockGps;
    private LocationListener locationListener;
    private static DeviceLocationManager instance;

    private DeviceLocationManager(Context context) {
        mContext = context;

    }

    public static DeviceLocationManager getInstance(Context context) {
        if (instance == null)
            instance = new DeviceLocationManager(context.getApplicationContext());
        return instance;
    }

    public void setInterval(int interval) {
        locationHelper.setInterval(interval);
    }

    public void init() {
        SharedPreferenceUtil.initSp(mContext, "DeviceLocationManager");

        locationHelper = LocationHelper.getInstance();
        locationHelper.initLocation(mContext, this);
    }

    /**
     * 设置模拟定位的GPS数据
     * @param gps
     */
    public void setMockLocationGps(GpsData gps) {
        mockGps = gps;
    }

    /**
     * 是否使能模拟定位功能
     * @param mock
     */
    public void setMockLocationEnable(Boolean mock) {
        mockLocationEnable = mock;
        LocationConfig.enableMockLocation(mock);
    }

    /**
     * 设置模拟导航
     * @param type
     */
    public void setNaviEmulatorEnable(Boolean type) {
        naviEmulatorEnable = type;
        LocationConfig.setNaviEmulator(type);
    }

    /**
     * 设置定位监听器
     * @param listener
     */
    public void setLocationListener(LocationListener listener) {
        locationListener = listener;
    }

    /**
     * 高德API的定位回调方法
     * 成功，返回定位的GPS数据，
     * 失败，则返回空数据
     *
     * @param aMapLocation
     */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        //Log.d(TAG, "onLocationChanged");
        if ((aMapLocation != null) && (aMapLocation.getErrorCode() == 0)) {
            GpsData locationGps;
            if (mockLocationEnable && mockGps != null) {
                locationGps = mockGps;
            } else {
                locationGps = new GpsData();
                locationGps.setLatitude(aMapLocation.getLatitude());
                locationGps.setLongitude(aMapLocation.getLongitude());
                locationGps.setBearing(aMapLocation.getBearing());

                /*if(aMapLocation.getSpeed() > 0){
                    locationGps.setBearing(aMapLocation.getBearing());
                }else{
                    //速度为0，传-1，用于告知各端不要改变旋转角度，保持上一次的状态。
                    locationGps.setBearing(-1);
                }*/
            }
            //回调
            updateLocationGpsData(locationGps);
        } else {
            Log.e(TAG, "定位错误：" + aMapLocation);
            //失败返回空数据
            updateLocationGpsData(null);
        }
    }

    private int count = 0;
    private void updateLocationGpsData(GpsData gpsData) {
        if(locationListener == null) {
            Log.e(TAG, "locationListener is null");
            return;
        }
        // 模拟导航中的经纬度上传， 十个传1个
        if (naviEmulatorEnable) {
            if (count % 10 == 0) {
                count = 0;
                Log.d(TAG, "sendGps, count " + count);
                locationListener.updateGpsData(gpsData);
            }
            count++;
        } else {
            Log.d(TAG, "sendGps");
            locationListener.updateGpsData(gpsData);
        }
    }

    public void startLocation() {
        locationHelper.startLocation();
        locationHelper.enableBackgroundLocation(2001, buildNotification());
    }

    public void stopLocation() {
        if (locationHelper != null) {
            locationHelper.stopLocation();
        }
    }

    /**
     * 销毁定位管理
     * 1.停止定位
     * 2.移除监听
     */
    public void destroyLocation() {
        stopLocation();
        locationListener = null;
    }

    private static final String NOTIFICATION_CHANNEL_NAME = "BackgroundLocation";
    private NotificationManager notificationManager = null;
    boolean isCreateChannel = false;
    /**
     * 针对8.0以后版本，将定位服务设置横前台服务，以防止服务被后台杀掉。
     *
     * @return
     */
    @SuppressLint("NewApi")
    private Notification buildNotification() {

        Notification.Builder builder = null;
        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            //Android O上对Notification进行了修改，如果设置的targetSDKVersion>=26建议使用此种方式创建通知栏
            if (null == notificationManager) {
                notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            }
//            String channelId = mContext.getPackageName();
            String channelId = "id_background_location";
            if (!isCreateChannel) {
                NotificationChannel notificationChannel = new NotificationChannel(channelId,
                        NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.enableLights(true);//是否在桌面icon右上角展示小圆点
                notificationChannel.setLightColor(Color.BLUE); //小圆点颜色
                notificationChannel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
                notificationManager.createNotificationChannel(notificationChannel);
                isCreateChannel = true;
            }
            builder = new Notification.Builder(mContext, channelId);
        } else {
            builder = new Notification.Builder(mContext);
        }
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("麦诗鹏电")
                .setContentText("正在后台运行")
                .setOngoing(true)
                .setWhen(System.currentTimeMillis());

        if (android.os.Build.VERSION.SDK_INT >= 16) {
            notification = builder.build();
        } else {
            return builder.getNotification();
        }
        return notification;
    }

}
