package com.haylion.android.common.view.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.haylion.android.R;
import com.haylion.android.data.base.BaseDialog;
import com.haylion.android.main.MainActivity;
import com.haylion.android.service.FloatDialogService;

import butterknife.OnClick;

/**
 * @author dengzh
 * @date 2019/12/24
 * Description:订单超时弹窗
 */
public class OrderOverTimeDialog extends BaseDialog {

    private int orderId;

    public OrderOverTimeDialog(@NonNull Context context) {
        super(context, R.style.Translucent_NoTitle);
        setContentView(R.layout.layout_overtime_order);

        if (Build.VERSION.SDK_INT >= 26) {
            getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        } else {
            getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
    }

    public void setData(int orderId){
        this.orderId = orderId;
    }

    @OnClick(R.id.btn_confirm)
    public void onViewClicked() {
        dismiss();
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("overTimeOrderId", orderId);
        mContext.startActivity(intent);
    }
}
