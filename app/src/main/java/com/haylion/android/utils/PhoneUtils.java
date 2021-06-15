package com.haylion.android.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.haylion.android.R;
import com.haylion.android.mvp.util.ToastUtils;

public class PhoneUtils {
    /**
     * 拨打电话
     */
    public static void call(Context mContext, String phoneNum) {
        if (TextUtils.isEmpty(phoneNum)) {
            ToastUtils.showShort(mContext, R.string.toast_phone_number_is_empty);
            return;
        }
        Uri data = Uri.parse("tel:" + phoneNum);
        //Intent intent = new Intent(Intent.ACTION_CALL); //直接拨打电话
        Intent intent = new Intent(Intent.ACTION_DIAL); //跳到拨号界面
        intent.setData(data);
        mContext.startActivity(intent);
    }
}
