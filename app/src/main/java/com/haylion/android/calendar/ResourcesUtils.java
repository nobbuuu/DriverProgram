package com.haylion.android.calendar;

import android.graphics.drawable.Drawable;

import com.haylion.android.MyApplication;

/**
 * Created by Administrator on 2017/8/7 0007.
 */
public class ResourcesUtils {

    public static int getColor(int resId){
        return MyApplication.getContext().getResources().getColor(resId);
    }
    public static Drawable getDrable(int resId){
        return MyApplication.getContext().getResources().getDrawable(resId);
    }
    public static String getString(int resId){
        return MyApplication.getContext().getResources().getString(resId);
    }
}
