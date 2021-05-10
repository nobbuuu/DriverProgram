package com.haylion.android.mvp.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.StringRes;
import androidx.core.app.NotificationManagerCompat;

import com.haylion.android.mvp.callback.SimpleActivityLifecycleCallbacks;

/**
 * 1.解决小米带appName问题。
 * 2.Android 8.0以上，短时间重复文字，系统认为误操作不显示问题。
 * 3.华为等手机，禁止通知栏，toast无法显示问题。不使用反射
 * 4.应用在后台，限制不toast信息。
 * 5.Toast 应该持有ApplicationContext，否则造成内存泄漏
 */
public class ToastUtils {

    private static String oldMsg;
    private static Toast mToast = null;

    public static void showShort(Context context, @StringRes int stringResId) {
        showShort(context.getApplicationContext(), context.getApplicationContext().getString(stringResId));
    }

    public static void showShort(Context context, final CharSequence text) {
        showToast(context.getApplicationContext(), text, Toast.LENGTH_SHORT);
    }

    public static void showLong(Context context, @StringRes int stringResId) {
        showLong(context.getApplicationContext(), context.getApplicationContext().getString(stringResId));
    }

    public static void showLong(Context context, final CharSequence text) {
        showToast(context.getApplicationContext(), text, Toast.LENGTH_LONG);
    }

    @SuppressLint("ShowToast")
    private static Toast createToast(Context context, CharSequence text, int duration) {
        Toast toast = Toast.makeText(context, null, duration);  //先置null，在设值，解决小米带appName问题
        toast.setGravity(Gravity.CENTER, 0, 0);
        return toast;
    }

    @SuppressLint("ShowToast")
    private static void showToast(Context context, final CharSequence text, int duration) {
        //应用在后台，不toast信息
        if(!SimpleActivityLifecycleCallbacks.isForeground()){
            return;
        }
        if(!isNotificationEnabled(context)){
            //华为等手机，禁止通知栏，toast无法显示
            Toast toast = com.hjq.toast.ToastUtils.getToast();
            toast.setDuration(duration);
            toast.setText(text);
            toast.show();
            return;
        }

        if (mToast != null) {
            mToast.cancel();
        }
        if (mToast == null) {
            mToast = createToast(context, text, duration);
        }else{
            //Android 8.0以上，文字相同且当前Toast正在显示时，系统会认为是误触操作，从而屏蔽当前显示Toast请求
            //所以要重新创建显示
//            if(text.toString().equals(oldMsg)){
                mToast = createToast(context, text, duration);
//            }
        }
        mToast.setDuration(duration);
        mToast.setText(text);
        mToast.show();
        oldMsg = text.toString();
    }


    /**
     * 判断通知栏是否开启
     * 华为等手机，禁止通知栏，toast无法显示
     * @return
     */
    private static boolean isNotificationEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return NotificationManagerCompat.from(context).getImportance()
                    != android.app.NotificationManager.IMPORTANCE_NONE;
        }
        return NotificationManagerCompat.from(context).areNotificationsEnabled();
    }

}
