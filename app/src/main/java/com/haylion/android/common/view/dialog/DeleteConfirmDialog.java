package com.haylion.android.common.view.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.haylion.android.R;
import com.haylion.android.mvp.util.ToastUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

/**
 * 联系客服弹窗
 * 1.跳到拨号界面，不需要权限
 * 2.直接拨打电话，需要权限.
 */
public class DeleteConfirmDialog extends DBaseDialog implements View.OnClickListener {

    private String phoneNum;
    private TextView tvPhone;
    private RxPermissions rxPermissions;

    public DeleteConfirmDialog(Context mContext) {
        super(mContext, R.layout.delete_message_dialog_frag, Gravity.BOTTOM, true, true);
        init();
    }

    private void init(){
        rxPermissions = new RxPermissions((FragmentActivity) mContext);
        tvPhone = getView(R.id.tv_phone);
        TextView tvConfirm = getView(R.id.tv_confirm);
        tvConfirm.setText("删除此条消息");
        TextView tvCancel = getView(R.id.tv_cancel);
        tvCancel.setText("取消");
        getView(R.id.tv_cancel).setOnClickListener(this);
        getView(R.id.tv_confirm).setOnClickListener(this);
    }

    public void setPhoneNum(String phoneNum){
        this.phoneNum = phoneNum;
        if (phoneNum.length() == 11) {
            tvPhone.setText(phoneNum.substring(0, 3) + "****" + phoneNum.substring(7, 11));
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
                call();
                break;
        }
    }

    /**
     * 拨打电话
     */
    public void call() {
        if(listener!=null){
            listener.onConfirm();
        }
        toggleDialog();
    }

    public DeleteConfirmDialog.OnDialogSelectListener listener;

    public void setOnDialogSelectListener(DeleteConfirmDialog.OnDialogSelectListener listener){
        this.listener = listener;
    }
    public interface OnDialogSelectListener{
        void onConfirm();
    }
}
