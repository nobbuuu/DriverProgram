package com.haylion.android.common.view.dialog;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.haylion.android.R;
import com.haylion.android.mvp.util.ToastUtils;

/**
 * 真实号码弹窗
 * 1.跳到拨号界面，不需要权限
 * 2.直接拨打电话，需要权限. CALL_PHONE 权限判断有问题，永远为true
 */
public class RealCallDialog extends DBaseDialog implements View.OnClickListener {

    private String phoneNum;
    private TextView tvPhone;

    public RealCallDialog(Context mContext) {
        super(mContext, R.layout.dialog_customer_service_call, Gravity.CENTER, true);
        init();
    }

    private void init(){
        tvPhone = getView(R.id.tv_phone);
        getView(R.id.tv_cancel).setOnClickListener(this);
        getView(R.id.tv_confirm).setOnClickListener(this);
    }

    public void setPhoneNum(String phoneNum){
        this.phoneNum = phoneNum;
        if(phoneNum.length()==11){
            tvPhone.setText(phoneNum.substring(0,3) + " " + phoneNum.substring(3,7) + " " + phoneNum.substring(7));
        }else{
            tvPhone.setText(phoneNum);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_cancel:
                toggleDialog();
                break;
            case R.id.tv_confirm:
                toggleDialog();
                call();
                break;
        }
    }

    /**
     * 拨打电话
     */
    private void call() {
        if (TextUtils.isEmpty(phoneNum)) {
            ToastUtils.showShort(mContext,R.string.toast_phone_number_is_empty);
            return;
        }
        Uri data = Uri.parse("tel:" + phoneNum);
        //Intent intent = new Intent(Intent.ACTION_CALL); //直接拨打电话
        Intent intent = new Intent(Intent.ACTION_DIAL); //跳到拨号界面
        intent.setData(data);
        mContext.startActivity(intent);
    }
}
