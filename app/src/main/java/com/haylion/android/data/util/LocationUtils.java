package com.haylion.android.data.util;

import android.content.Context;
import android.location.LocationManager;

/**
 * @author dengzh
 * @date 2019/12/7
 * Description:
 */
public class LocationUtils {

    /**
     * 检查定位服务是否开启
     * @param context
     * @return
     */
    public static boolean checkLocationIsOpen(Context context){
        return checkGPSIsOpen(context)||checkNetWorkLocationIsOpen(context);
    }

    /**
     * 检查GPS定位是否开启
     * @param context
     * @return
     */
    public static boolean checkGPSIsOpen(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * 检查网络定位是否开启
     * @param context
     * @return
     */
    public static boolean checkNetWorkLocationIsOpen(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
}
