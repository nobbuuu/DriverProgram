package com.haylion.android.user.vehicle;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.haylion.android.R;
import com.haylion.android.data.base.BaseDialog;
import com.haylion.android.data.model.Vehicle;
import com.haylion.android.pay.PayMainActivity;
import com.haylion.android.user.shift.ShiftInfoActivity;

import java.net.URL;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.OnClick;

public class AddVehicleDialog extends BaseDialog {

    private Vehicle mVehicle;
    private OnConfirmedListener mListener;

    @OnClick({R.id.btn_negative, R.id.btn_positive})
    void onButtonClick(View view) {
        if (view.getId() == R.id.btn_positive && mListener != null) {
            String areaCode = mAreaCode.getText().toString();
            String orderNumber = mOrderNumber.getText().toString();
            String vehicleNumber = areaCode + orderNumber;
            if (checkVehicleNumber(vehicleNumber)) {
                mVehicle.setNumber(vehicleNumber);
                mListener.onConfirmed(mVehicle);
            } else {
                toast("车牌号格式错误");
                return;
            }
        }
        dismiss();
    }

    private static final Pattern VEHICLE_NUMBER = Pattern.compile("(" +
            "[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-Z]" +
            "(([0-9]{5}[DF])|([DFA]([A-HJ-NP-Z0-9])[0-9]{4}))" +
            ")" +
            "|" +
            "(" +
            "[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-Z]" +
            "[A-HJ-NP-Z0-9]{4}[A-HJ-NP-Z0-9挂学警港澳]" +
            ")"
    );

    @BindView(R.id.tv_title)
    TextView mTitle;
    @BindView(R.id.area_code)
    TextView mAreaCode;
    @BindView(R.id.order_number)
    EditText mOrderNumber;

    AddVehicleDialog(@NonNull Context context) {
        this(context, null);
    }

    AddVehicleDialog(@NonNull Context context, Vehicle vehicle) {
        super(context);
        this.mVehicle = vehicle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_vehicle_dialog);
        if (mVehicle == null) {
            mVehicle = new Vehicle();
            mAreaCode.setText("粤B");
            mTitle.setText(R.string.action_add_vehicle);
        } else {
            mTitle.setText(R.string.action_modify_vehicle_number);
            String vehicleNumber = mVehicle.getNumber();
            if (checkVehicleNumber(vehicleNumber)) {
                String areaCode = vehicleNumber.substring(0, 2);
                mAreaCode.setText(areaCode);
                String orderNumber = vehicleNumber.substring(2);
                mOrderNumber.setText(orderNumber);
                mOrderNumber.setSelection(orderNumber.length());
            } else {
                toast("车牌号格式错误");
            }
        }
        setCanceledOnTouchOutside(false);

        setOnShowListener(dialog -> PayMainActivity.showSoftInput(mOrderNumber, getContext()));
    }

    private boolean checkVehicleNumber(String vehicleNumber) {
        if (TextUtils.isEmpty(vehicleNumber)) {
            return false;
        }
        // 应产品人员要求，暂时去除车牌号格式校验
//        return VEHICLE_NUMBER.matcher(vehicleNumber).matches();
        return true;
    }

    interface OnConfirmedListener {

        void onConfirmed(Vehicle vehicle);

    }

    public AddVehicleDialog setListener(OnConfirmedListener listener) {
        this.mListener = listener;
        return this;
    }


}
