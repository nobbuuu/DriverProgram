package com.haylion.android.common.view.dialog;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;

import com.haylion.android.R;
import com.haylion.android.mvp.util.ToastUtils;

/**
 * 虚拟号拨号弹窗
 * 1.跳到拨号界面，不需要权限
 * 2.直接拨打电话，需要权限，CALL_PHONE 权限判断有问题，永远为true
 */
public class VirtualCallDialog extends DBaseDialog {

    private String phoneNum;

    public VirtualCallDialog(Context mContext) {
        super(mContext, R.layout.dialog_virtual_call, Gravity.CENTER, true);
        init();
    }

    private void init(){
        getView(R.id.tv_click_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDialog();
                call();
            }
        });
    }

    public void setPhoneNum(String phoneNum){
        this.phoneNum = phoneNum;
    }

    /**
     * 拨打电话
     */
    public void call() {
        if (TextUtils.isEmpty(phoneNum)) {
            return;
        }
        Uri data = Uri.parse("tel:" + phoneNum);
        Intent intent = new Intent(Intent.ACTION_DIAL); //跳到拨号界面
        intent.setData(data);
        mContext.startActivity(intent);
    }
}
