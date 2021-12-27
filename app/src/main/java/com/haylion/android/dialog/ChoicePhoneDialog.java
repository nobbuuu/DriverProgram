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
import com.haylion.android.utils.PhoneUtils;

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
        View line1 = findViewById(R.id.divider1);
        View line2 = findViewById(R.id.divider2);
        View line3 = findViewById(R.id.divider3);
        TextView phone2_tv = findViewById(R.id.phone2_tv);
        TextView phone3_tv = findViewById(R.id.phone3_tv);
        TextView cancel_tv = findViewById(R.id.cancel_tv);

        phone1_tv.setText(PhoneUtils.desensitizationName(order.getPickupContactName()) + "  " + PhoneUtils.desensitizationPhone(order.getPickupContactMobile()));
        phone2_tv.setText(PhoneUtils.desensitizationName(order.getPickupContactName1()) + "  " + PhoneUtils.desensitizationPhone(order.getPickupContactMobile1()));
        phone3_tv.setText(PhoneUtils.desensitizationName(order.getPickupContactName2()) + "  " + PhoneUtils.desensitizationPhone(order.getPickupContactMobile2()));
        if (order.getPickupContactMobile() == null || order.getPickupContactMobile().isEmpty()) {
            phone1_tv.setVisibility(View.GONE);
            line1.setVisibility(View.GONE);
        }
        if (order.getPickupContactMobile1() == null || order.getPickupContactMobile1().isEmpty()) {
            phone2_tv.setVisibility(View.GONE);
            line2.setVisibility(View.GONE);
        }
        if (order.getPickupContactMobile2() == null || order.getPickupContactMobile2().isEmpty()) {
            phone3_tv.setVisibility(View.GONE);
            line3.setVisibility(View.GONE);
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
