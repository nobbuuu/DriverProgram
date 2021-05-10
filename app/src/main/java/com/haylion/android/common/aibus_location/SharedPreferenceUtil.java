package com.haylion.android.common.aibus_location;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * 存储轻量的数据
 */
public class SharedPreferenceUtil {
    private static final String TAG = "SharedPreferenceUtil";
    private static SharedPreferences sp;
    private static Context mContext;
    private static String mFileName;

    private final static String TIMESTAMP = "timestamp";

    public static void initSp(Context context, String fileName) {
        mContext = context;
        mFileName = fileName;
        sp = mContext.getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    public static void setCache(String key, String value){
        if(sp == null){
            initSp(mContext, mFileName);
        }
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(key, value);
        boolean success = edit.commit();
        if(!success) {
            Log.e(TAG, "shared preference commit fail:"+ ",key=" + key + ",value:" + value);
        }
    }

    public static String getCache(String key){
        if(sp == null){
            initSp(mContext, mFileName);
        }
        return sp.getString(key,null);
    }

    /**
     * 保存请求头的timestamp，时间戳差值
     * @param timestamp
     */
    public static void saveTimestamp(String timestamp) {
        if(sp == null){
            initSp(mContext,mFileName);
        }
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(TIMESTAMP, timestamp);
        edit.commit();
    }


    /**
     * 获取请求头的timestamp
     * @param mContext
     * @return
     */
    public static String getTimestamp(Context mContext){
        if(sp == null){
            initSp(mContext, mFileName);
        }
        return sp.getString(TIMESTAMP,null);
    }
}

