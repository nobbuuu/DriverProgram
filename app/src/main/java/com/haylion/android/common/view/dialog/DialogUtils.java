package com.haylion.android.common.view.dialog;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.haylion.android.R;
import com.haylion.android.mvp.util.ToastUtils;

/**
 * @author dengzh
 * @date 2019/12/4
 * Description:
 */
public class DialogUtils {

    /**
     * 显示联系客服 弹窗
     * @param context
     * @param phone    客服电话
     * @param showDialog 是否需要显示弹窗 为false，直接跳到拨号界面
     */
    public static void showCustomerServiceCallDialog(Context context,String phone,boolean showDialog){
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.showShort(context, R.string.toast_get_customer_service_phone_error);
            return;
        }
        if(showDialog){
            RealCallDialog dialog = new RealCallDialog(context);
            dialog.setPhoneNum(phone);
            dialog.toggleDialog();
        }else{
            Uri data = Uri.parse("tel:" + phone);
            Intent intent = new Intent(Intent.ACTION_DIAL); //跳到拨号界面
            intent.setData(data);
            context.startActivity(intent);
        }
    }

    /**
     * 显示真实号码 弹窗
     * @param context
     * @param phone
     */
    public static void showRealCallDialog(Context context,String phone){
        if (TextUtils.isEmpty(phone)) {
            ToastUtils.showShort(context, R.string.toast_phone_number_is_empty);
            return;
        }
        RealCallDialog dialog = new RealCallDialog(context);
        dialog.setPhoneNum(phone);
        dialog.toggleDialog();
    }

    /**
     * 显示虚拟号 弹窗
     * @param context
     * @param phone   虚拟号
     */
    public static void showVirtualCallDialog(Context context,String phone){
        VirtualCallDialog dialog = new VirtualCallDialog(context);
        dialog.setPhoneNum(phone);
        dialog.toggleDialog();
    }
}
