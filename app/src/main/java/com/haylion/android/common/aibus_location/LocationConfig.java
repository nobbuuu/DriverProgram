package com.haylion.android.common.aibus_location;

import android.util.Log;


public class LocationConfig {
    private static final String TAG = "LocationConfig";

    private final static String KEY_NAVI_EMULATOR_ENABLE = "navi_emulator_enable";
    private final static String KEY_LOCATION_SIMULATE_ENABLE = "location_mock_enable";
    private final static String KEY_LOCATION_SIMULATE_DETAIL = "location_mock_detail";


    public static void enableMockLocation(boolean enable) {
        Log.d(TAG, "enableMockLocation enable:" + enable);
        String value = SharedPreferenceUtil.getCache(KEY_LOCATION_SIMULATE_ENABLE);
        Log.d(TAG, "before, simulate enable:" + value);
        if (enable) {
            Log.d(TAG, "模拟定位开启");
            SharedPreferenceUtil.setCache(KEY_LOCATION_SIMULATE_ENABLE, "1");
        } else {
            Log.d(TAG, "模拟定位关闭");
            SharedPreferenceUtil.setCache(KEY_LOCATION_SIMULATE_ENABLE, "0");
        }
        value = SharedPreferenceUtil.getCache(KEY_LOCATION_SIMULATE_ENABLE);
        Log.d(TAG, "after, simulate enable:" + value);
    }

    public static boolean getMockLocationStatus() {
//        Log.d(TAG, "getMockLocationStatus ");
        String value = SharedPreferenceUtil.getCache(KEY_LOCATION_SIMULATE_ENABLE);
        if (value != null) {
            return value.equals("1");
        } else {
            return false;
        }
    }

    public static void setMockLocation(String location) {
        Log.d(TAG, "setMockLocation location:" + location);
        SharedPreferenceUtil.setCache(KEY_LOCATION_SIMULATE_DETAIL, location);
    }

    public static String getMockLocation() {
        String location = SharedPreferenceUtil.getCache(KEY_LOCATION_SIMULATE_DETAIL);
        Log.d(TAG, "getMockLocation location:" + location);
        if (location != null) {
            return location;
        } else {
            return "";
        }
    }


    public static boolean getNaviEmulatorStatus() {
        boolean enable;
        String mode = SharedPreferenceUtil.getCache(KEY_NAVI_EMULATOR_ENABLE);
        Log.d(TAG, "getNaviEmulator mode:" + mode);
        if (mode != null && mode.equals("1")) {
            Log.d(TAG, "模拟导航开启");
            enable = true;
        } else {
            Log.d(TAG, "模拟导航关闭");
            enable = false;
        }
        return enable;
    }

    public static void setNaviEmulator(boolean enable) {
        Log.d(TAG, "setNaviEmulator enable:" + enable);
        if (enable) {
            SharedPreferenceUtil.setCache(KEY_NAVI_EMULATOR_ENABLE, "1");
            Log.d(TAG, "模拟导航开启");
        } else {
            SharedPreferenceUtil.setCache(KEY_NAVI_EMULATOR_ENABLE, "0");
            Log.d(TAG, "模拟导航关闭");
        }
    }

}

