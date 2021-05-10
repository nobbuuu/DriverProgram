package com.haylion.android.orderdetail;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haylion.android.R;
import com.haylion.android.common.view.popwindow.BasePopupWindow;
import com.haylion.android.mvp.util.SizeUtil;
import com.haylion.android.mvp.util.ToastUtils;
import com.haylion.android.user.vehicle.VehicleOperation;

import java.util.List;

import androidx.core.content.ContextCompat;

public class ContactParentsChooser extends BasePopupWindow implements
        View.OnClickListener {

    private TextView mTitle;
    private LinearLayout mPhoneNumbers;

    private OnSelectListener mOnSelectListener;

    public ContactParentsChooser(Context mContext, OnSelectListener listener) {
        super(mContext, R.layout.chooser_contact_parents,
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        this.mOnSelectListener = listener;
        init();
    }

    public void setData(final CharSequence title, final List<String> phoneNumbers) {
        mTitle.setText(title);
        if (phoneNumbers == null || phoneNumbers.isEmpty()) {
            throw new IllegalArgumentException("Phone numbers can not be null!");
        }
        for (int i = 0; i < phoneNumbers.size(); i++) {
            final String phoneNumber = phoneNumbers.get(i);
            View divider = new View(mContext);
            divider.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, SizeUtil.dp2px(1)
            ));
            divider.setBackgroundColor(ContextCompat.getColor(mContext, R.color.line_e5e5e5));
            mPhoneNumbers.addView(divider);

            FrameLayout container = new FrameLayout(mContext);
            container.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            TextView textView = new TextView(mContext);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
            textView.setLayoutParams(layoutParams);
            textView.setPadding(0, SizeUtil.dp2px(16), 0, SizeUtil.dp2px(16));
            textView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.ic_contact_parent, 0, 0, 0);
            textView.setCompoundDrawablePadding(SizeUtil.dp2px(8));
            textView.setText(phoneNumber);
            textView.setTextColor(ContextCompat.getColor(mContext, R.color.maas_text_deep_gray));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 19);
            container.addView(textView);

            mPhoneNumbers.addView(container);
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnSelectListener != null) {
                        mOnSelectListener.onSelect(phoneNumber);
                    }
                    dismiss();
                }
            });
        }
    }

    private void init() {
        mTitle = getView(R.id.title);
        mPhoneNumbers = getView(R.id.phone_numbers);
        getView(R.id.rl_main).setOnClickListener(this);
        getView(R.id.cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }

    public interface OnSelectListener {

        void onSelect(String phoneNumber);

    }

}
