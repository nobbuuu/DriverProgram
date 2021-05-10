package com.haylion.android.data.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;

import androidx.annotation.ArrayRes;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import com.haylion.android.MyApplication;


public final class ResourceUtil {

    private ResourceUtil() {
        // hide constructor
    }

    public static String getString(Context context, @StringRes int stringResId) {
        return context.getString(stringResId);
    }

    public static String getString(@StringRes int id) {
        return getString(MyApplication.getContext(), id);
    }

    public static Drawable getDrawable(Context context, @DrawableRes int drawableResID) {
        return ContextCompat.getDrawable(context, drawableResID);
    }


    public static String formatString(@StringRes int id, Object... args) {
        return formatString(MyApplication.getContext(), id, args);
    }

    /**
     * 格式化字符串
     *
     * @param context 上下文
     * @param id      字符串id
     * @param args    字符串
     */
    public static String formatString(Context context, @StringRes int id, Object... args) {
        return formatString(getString(context, id), args);
    }

    public static String formatString(String format, Object... args) {
        return String.format(format, args);
    }

    public static int getColor(Context context, @ColorRes int colorResId) {
        return ContextCompat.getColor(context, colorResId);
    }

    //todo
    public static int getColor(@ColorRes int colorResId) {
        return ContextCompat.getColor(MyApplication.getContext(), colorResId);
    }

    public static ColorStateList getColorStateList(Context context, @ColorRes int colorResId) {
        return ContextCompat.getColorStateList(context, colorResId);
    }

    public static int[] getIntArray(Context context, @ArrayRes int arrayResId) {
        return context.getResources().getIntArray(arrayResId);
    }


}
