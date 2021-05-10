package com.haylion.android.data.util;

import android.content.Context;

/**
 * @author dengzh
 * @date 2019/11/1
 * Description: 状态栏工具类
 */
public class StatusBarUtils {

    /**
     * 获取状态栏高度
     *
     * @param context context
     * @return 状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        Context mContext = context.getApplicationContext();
        // 获得状态栏高度
        int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return mContext.getResources().getDimensionPixelSize(resourceId);
    }


}
