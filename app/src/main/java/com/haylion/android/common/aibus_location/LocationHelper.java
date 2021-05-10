package com.haylion.android.common.aibus_location;

import android.app.Notification;
import android.content.Context;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import java.util.HashSet;


/**
 *
 */
public class LocationHelper implements AMapLocationListener {
    private static final String TAG = "LocationHelper";
    private static final long LOCATION_INTERVAL_TIME = 5000; //默认5秒定位一次

    //声明mlocationClient对象
    public AMapLocationClient mlocationClient;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;

    private static LocationHelper instance;

    private HashSet<AMapLocationListener> mLocationListeners = new HashSet<>();

    private LocationHelper() {

    }

    public static LocationHelper getInstance() {
        if (instance == null)
            instance = new LocationHelper();
        return instance;
    }

    public void initLocation(Context context, AMapLocationListener listener) {
        Log.d(TAG, "initLocation");
        mlocationClient = new AMapLocationClient(context);

        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位监听
        mLocationListeners.add(listener);
        mlocationClient.setLocationListener(this);

        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //https://lbs.amap.com/faq/android/android-location/311
        //使用设备传感器,这样在高精度定位模式下也可以返回速度、角度、海拔数据。默认是false。
        //返回的方向角指的是手机朝向。
        mLocationOption.setSensorEnable(true);


        //设置定位间隔,单位毫秒,默认为5000ms
        mLocationOption.setInterval(LOCATION_INTERVAL_TIME);

        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);

    }

    public void setInterval(long time ){
        //设置定位间隔,单位毫秒,默认为5000ms
        if(mlocationClient != null && mLocationOption != null){
            mLocationOption.setInterval(time);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
        }
    }

    public void startLocation() {
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //启动定位
        mlocationClient.startLocation();
    }

    public void stopLocation() {
        mlocationClient.stopLocation();
    }


    public void enableBackgroundLocation(int num, Notification notification) {
        mlocationClient.enableBackgroundLocation(num, notification);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        for (AMapLocationListener locationListener : mLocationListeners) {
            locationListener.onLocationChanged(aMapLocation);
        }
    }

    public void addLocationListener(AMapLocationListener listener) {
        if (listener == null) {
            return;
        }
        mLocationListeners.add(listener);
    }


    public void removeLocationListener(AMapLocationListener listener) {
        if (listener == null) {
            return;
        }
        mLocationListeners.remove(listener);
    }

}
