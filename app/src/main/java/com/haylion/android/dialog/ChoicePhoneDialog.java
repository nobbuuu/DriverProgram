package com.haylion.android.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.haylion.android.R;
import com.haylion.android.data.model.Order;
import com.haylion.android.mvp.util.ToastUtils;

import java.util.List;

public class ChoicePhoneDialog extends Dialog {

    public ChoicePhoneDialog(@NonNull Context context, Order order) {
        super(context, R.style.ActionSheetDialogStyle);
        setContentView(R.layout.dialog_choice_phone);
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams attr = window.getAttributes();
            if (attr != null) {
                attr.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                attr.width = ViewGroup.LayoutParams.MATCH_PARENT;
                attr.gravity = Gravity.BOTTOM;
                window.setAttributes(attr);
            }
        }
        TextView phone1_tv = findViewById(R.id.phone1_tv);
        TextView phone2_tv = findViewById(R.id.phone2_tv);
        TextView phone3_tv = findViewById(R.id.phone3_tv);
        TextView cancel_tv = findViewById(R.id.cancel_tv);
        phone1_tv.setText(order.getPickupContactName() + "  " + order.getPickupContactMobile());
        phone2_tv.setText(order.getPickupContactName1() + "  " + order.getPickupContactMobile1());
        phone3_tv.setText(order.getPickupContactName2() + "  " + order.getPickupContactMobile2());
        if (phone1_tv.getText().toString().isEmpty()){
            phone1_tv.setVisibility(View.GONE);
        }
        if (phone2_tv.getText().toString().isEmpty()){
            phone2_tv.setVisibility(View.GONE);
        }
        if (phone3_tv.getText().toString().isEmpty()){
            phone3_tv.setVisibility(View.GONE);
        }
        phone1_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                call(context, order.getPickupContactMobile());
            }
        });
        phone2_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                call(context, order.getPickupContactMobile1());
            }
        });
        phone3_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                call(context, order.getPickupContactMobile2());
            }
        });
        cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

    }

    /**
     * 拨打电话
     */
    private void call(Context mContext, String phoneNum) {
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

    public ChoicePhoneDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

}
