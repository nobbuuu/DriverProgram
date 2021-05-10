package com.haylion.android.data.repo;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.haylion.android.data.model.AmapNaviSettingBean;
import com.haylion.android.data.model.Driver;
import com.haylion.android.data.model.ListenOrderSetting;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.model.Vehicle;

import com.haylion.android.mvp.util.LogUtils;
import com.haylion.prefser.Prefser;
import com.haylion.prefser.TypeToken;
import com.xiaomi.mipush.sdk.PushConfiguration;

import java.util.ArrayList;
import java.util.List;


public final class PrefserHelper {
    private static final String TAG = "PrefserHelper";

    private static Prefser sPrefser;

    private static Prefser sDriverPrefser;

    private static final Object mLock = new Object();

    private volatile static String token = null;
    private static final String KEY_TOKEN = "token";

    //app最近一次启动的日期
    public static final String APP_LAST_STARTUP_DATE = "app_last_startup_date";
    //apk更新弹窗，最近一次点击稍后再说的时间戳
    public static final String APK_UPDATE_LAST_REJECT_DATE = "apk_update_last_reject_date";
    //apk更新弹窗，最近一次的VersionCode
    public static final String APK_UPDATE_LAST_VERSION_CODE = "apk_update_last_version_code";
    //apk更新弹窗，最近一次的版本拒绝次数
    public static final String APK_UPDATE_REJECT_NUMBER_IN_SAME_VERSION = "apk_update_reject_number_in_same_version";


    // 司机信息
    private static final String KEY_DRIVER_INFO = "driver_info";

    private static final String KEY_FIRST_ORDER_INFO = "first_order_info";

    public static final String KEY_ORDER_LISTEN_STATUS = "order_listen_status";

    public static final String KEY_ORDER_VOICE_ENABLE = "order_voice_enable";

    //车辆信息
    private static final String KEY_VEHICLE_INFO = "vehicle_info";

    private static final String KEY_VEHICLE_LIST = "vehicle_list";

    public static final String KEY_VEHICLE_LIST_EMPTY_TIPS_IS_CLOSE = "vehicle_list_empty_tips_is_close";

    private static final String KEY_ORDER_SETTING_INFO = "order_listen_setting";

    public static final String KEY_GOODS_ORDER_ID = "goods_order_id"; //货物ID

    public static final String KEY_SERVICE_NUMBER = "service_phone"; //服务电话

    public static final String KEY_UMENG_PUSH_TOKEN = "umeng_push_token"; //友盟推送的token

    //地图导航相关
    public static final String KEY_AMAP_NAVI_SET_INFO = "amap_navi_setting_info";  //导航设置信息
    public static final String KEY_AMAP_NAVI_VOICE_ENABLE = "map_navi_voice_enable"; //语音播放

    /**
     * 最近一个新订单的创建时间（用于首页新订单计数）
     */
    private static final String KEY_NEW_ORDER_TIME = "new_order_time";


    private PrefserHelper() {
        throw new UnsupportedOperationException("hey, buddy, easy...");
    }

    /**
     * 初始化默认的SharedPreferences文件
     */
    public static void initPrefser(Context context) {
        if (context == null) {
            throw new NullPointerException("Context can not be null!");
        }
        context = context.getApplicationContext();
        if (sPrefser == null) {
            sPrefser = new Prefser(context);
            sDriverPrefser = new Prefser(getDriverSharedPreferences(context));
        }
    }

    private static SharedPreferences getDriverSharedPreferences(Context context) {
        return context.getSharedPreferences(KEY_DRIVER_INFO, Context.MODE_PRIVATE);
    }

    /**
     * 清空默认的SharedPreferences文件
     */
    public static void clearPrefser() {
        sPrefser.clear();
        token = null;
    }

    public static void removeKey(String key) {
        sPrefser.remove(key);
    }

    public static String getToken() {
        if (token == null) {
            synchronized (mLock) {
                if (token == null) {
                    return token = sPrefser.get(KEY_TOKEN, String.class, "");
                }
            }
        }

        LogUtils.d(TAG, "getToken: " + token);
        return token;
    }

    public static void saveToken(String token) {
        LogUtils.d(TAG, "saveToken:" + token);
        PrefserHelper.token = token;
        sPrefser.put(KEY_TOKEN, token);
    }

    public static boolean isLoggedIn() {
        return !TextUtils.isEmpty(getToken());
    }


    public static void setCache(String key, String value) {
        sPrefser.put(key, value);
    }

    public static String getCache(String key) {
        return sPrefser.get(key, String.class, null);
    }

    public static void setIntCache(String key, int value) {
        sPrefser.put(key, value);
    }

    public static int getIntCache(String key) {
        return sPrefser.get(key, Integer.class, 0);
    }

    public static void setBoolean(String key, boolean value) {
        sPrefser.put(key, value);
    }

    public static boolean getBoolean(String key) {
        return sPrefser.get(key, Boolean.class, false);
    }

    /**
     * 司机相关信息
     */
    public static Driver getDriverInfo() {
        return sDriverPrefser.get(KEY_DRIVER_INFO, Driver.class, null);
    }

    public static void saveDriverInfo(Driver driver) {
        sDriverPrefser.put(KEY_DRIVER_INFO, driver);
    }

    public static void removeDriverInfo() {
        sDriverPrefser.remove(KEY_DRIVER_INFO);
    }


    /**
     * 车辆信息
     */
    public static Vehicle getVehicleInfo() {
        return sDriverPrefser.get(KEY_VEHICLE_INFO, Vehicle.class, null);
    }

    /**
     * 保存选中的车辆信息
     *
     * @param vehicle
     */
    public static void saveVehicleInfo(Vehicle vehicle) {
        sDriverPrefser.put(KEY_VEHICLE_INFO, vehicle);
    }

    public static void removeVehicleInfo() {
        sDriverPrefser.remove(KEY_VEHICLE_INFO);
    }

    /**
     * 导航设置信息
     */
    public static AmapNaviSettingBean getNaviSetInfo() {
        return sDriverPrefser.get(KEY_AMAP_NAVI_SET_INFO, AmapNaviSettingBean.class, new AmapNaviSettingBean());
    }

    public static void saveNaviSetInfo(AmapNaviSettingBean info) {
        sDriverPrefser.put(KEY_AMAP_NAVI_SET_INFO, info);
    }

    public static void removeNaviSetInfo() {
        sDriverPrefser.remove(KEY_AMAP_NAVI_SET_INFO);
    }

    /**
     * 听单设置
     */
    public static ListenOrderSetting getOrderSettingInfo() {
        return sDriverPrefser.get(KEY_ORDER_SETTING_INFO, ListenOrderSetting.class, null);
    }

    public static void saveOrderSettingInfo(ListenOrderSetting setting) {
        sDriverPrefser.put(KEY_ORDER_SETTING_INFO, setting);
    }

    public static void removeOrderSettingInfo() {
        sDriverPrefser.remove(KEY_ORDER_SETTING_INFO);
    }

    public static void saveVehicleList(List<Vehicle> list) {
        LogUtils.d(TAG, "saveVehicleList:" + list.toString());
        setDataList(KEY_VEHICLE_LIST, list);
    }

    public static List<Vehicle> getVehicleList() {
        LogUtils.d(TAG, "getVehicleList: " + getDataList(KEY_VEHICLE_LIST).toString());
        return getDataList(KEY_VEHICLE_LIST);
    }

    public static void removeVehicleInfoList() {
        LogUtils.d(TAG, "removeVehicleInfoList");
        sPrefser.remove(KEY_VEHICLE_LIST);
    }


    public static void clearLoginInfo() {
        LogUtils.d(TAG, "clearLoginInfo");
        removeVehicleInfo();
        removeVehicleInfoList();
        removeDriverInfo();
        removeOrderSettingInfo();
    }

    /**
     * 第一个订单信息
     */
    public static Order getFirstOrderInfo() {
        return sDriverPrefser.get(KEY_FIRST_ORDER_INFO, Order.class, null);
    }

    public static void saveFirstOrderInfo(Order order) {
        sDriverPrefser.put(KEY_FIRST_ORDER_INFO, order);
    }

    /*
     */
/**
 * 保存List
 *
 * @param tag
 * @param datalist
 *//*

    public static <T> void setDataList(String tag, List<T> datalist) {
        if (null == datalist || datalist.size() <= 0)
            return;

        Gson gson = new Gson();
        //转换成json数据，再保存
        String strJson = gson.toJson(datalist);
        setCache(tag, strJson);

    }

    */
/**
 * 获取List
 *
 * @param tag
 * @return
 *//*

    public static  <T> List<T> getDataList(String tag) {
        List<T> datalist = new ArrayList<T>();
        String strJson = getCache(tag);
        if (null == strJson) {
            return datalist;
        }
        Gson gson = new Gson();
        datalist = gson.fromJson(strJson, new TypeToken<List<T>>() {
        }.getType());
        return datalist;
    }
*/

    /**
     * 保存List
     *
     * @param tag
     * @param datalist
     */
    public static <Vehicle> void setDataList(String tag, List<Vehicle> datalist) {
//        if (null == datalist || datalist.size() <= 0)
//            return; 2020/03/11 MAO，存在列表数据为空的情况

        Gson gson = new Gson();
        //转换成json数据，再保存
        String strJson = gson.toJson(datalist);
        setCache(tag, strJson);

    }

    /**
     * 获取List
     *
     * @param tag
     * @return
     */
    public static List<Vehicle> getDataList(String tag) {
        List<Vehicle> datalist = new ArrayList<Vehicle>();
        String strJson = getCache(tag);
        if (null == strJson) {
            return datalist;
        }
        Gson gson = new Gson();
        datalist = gson.fromJson(strJson, new TypeToken<List<Vehicle>>() {
        }.getType());
        return datalist;
    }

    public static String getNewOrderTime() {
        return sDriverPrefser.get(KEY_NEW_ORDER_TIME, String.class, "1970-01-01 00:00");
    }

    public static void saveNewOrderTime(String orderTime) {
        sDriverPrefser.put(KEY_NEW_ORDER_TIME, orderTime);
    }

}
