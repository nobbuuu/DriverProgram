package com.haylion.android.user.shift;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.haylion.android.R;
import com.haylion.android.data.base.BaseActivity;
import com.haylion.android.data.base.BaseDialog;
import com.haylion.android.data.model.AddressInfo;
import com.haylion.android.data.model.ShiftInfo;
import com.haylion.android.pay.PayMainActivity;
import com.jwenfeng.library.pulltorefresh.util.DisplayUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.OnClick;

public class ShiftInfoActivity extends BaseActivity<ShiftInfoContract.Presenter>
        implements ShiftInfoContract.View {

    @BindView(R.id.mobile_phone)
    TextView mMobilePhone;

    @BindView(R.id.turnover_time_day)
    TextView mTurnoverTimeDay;
    @BindView(R.id.turnover_address_day)
    TextView mTurnoverAddressDay;

    @BindView(R.id.turnover_time_night)
    TextView mTurnoverTimeNight;
    @BindView(R.id.turnover_address_night)
    TextView mTurnoverAddressNight;

    @BindView(R.id.mailing_address)
    TextView mMailingAddress;

    public static void start(Context context) {
        Intent intent = new Intent(context, ShiftInfoActivity.class);
        context.startActivity(intent);
    }

    private ShiftInfo mShiftInfo;

    @OnClick({R.id.btn_back, R.id.mobile_phone_layout,
            R.id.turnover_time_day_layout, R.id.turnover_address_day_layout,
            R.id.turnover_time_night_layout, R.id.turnover_address_night_layout,
            R.id.mailing_address_layout})
    void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.mobile_phone_layout:
                new EditMobileDialog(getContext()).show();
                break;

            case R.id.turnover_time_day_layout:
                TurnoverTimePicker dayPicker = new TurnoverTimePicker(
                        getContext(), getString(R.string.label_shift_turnover_time_day));
                dayPicker.setCallback((startTime, endTime) -> {
                    ShiftInfo dayTime = new ShiftInfo();
                    dayTime.setStartTime(startTime);
                    dayTime.setEndTime(endTime);
                    presenter.modifyShiftInfo(dayTime);
                });
                dayPicker.showAtLocation(view, Gravity.BOTTOM, 0, 0);
                break;

            case R.id.turnover_address_day_layout:
                if (mShiftInfo == null) {
                    toast("个人信息为空");
                    return;
                }
                AddressInfo turnoverAddressDay = null;
                if (!TextUtils.isEmpty(mShiftInfo.getAddressTitle()) ||
                        !TextUtils.isEmpty(mShiftInfo.getAddressDesc())) {
                    turnoverAddressDay = new AddressInfo();
                    turnoverAddressDay.setName(mShiftInfo.getAddressTitle());
                    turnoverAddressDay.setAddressDetail(mShiftInfo.getAddressDesc());
                }
                ShiftAddressActivity.start(
                        getContext(),
                        ShiftAddressActivity.ADDRESS_TYPE_TURNOVER_DAY,
                        turnoverAddressDay
                );
                break;

            case R.id.turnover_time_night_layout:
                TurnoverTimePicker nightPicker = new TurnoverTimePicker(
                        getContext(), getString(R.string.label_shift_turnover_time_night));
                nightPicker.setCallback((startTime, endTime) -> {
                    ShiftInfo nightTime = new ShiftInfo();
                    nightTime.setSecondStartTime(startTime);
                    nightTime.setSecondEndTime(endTime);
                    presenter.modifyShiftInfo(nightTime);
                });
                nightPicker.showAtLocation(view, Gravity.BOTTOM, 0, 0);
                break;

            case R.id.turnover_address_night_layout:
                if (mShiftInfo == null) {
                    toast("个人信息为空");
                    return;
                }
                AddressInfo turnoverAddressNight = null;
                if (!TextUtils.isEmpty(mShiftInfo.getSecondAddressTitle()) ||
                        !TextUtils.isEmpty(mShiftInfo.getSecondAddressDesc())) {
                    turnoverAddressNight = new AddressInfo();
                    turnoverAddressNight.setName(mShiftInfo.getSecondAddressTitle());
                    turnoverAddressNight.setAddressDetail(mShiftInfo.getSecondAddressDesc());
                }
                ShiftAddressActivity.start(
                        getContext(),
                        ShiftAddressActivity.ADDRESS_TYPE_TURNOVER_NIGHT,
                        turnoverAddressNight
                );
                break;

            case R.id.mailing_address_layout:
                if (mShiftInfo == null) {
                    toast("个人信息为空");
                    return;
                }
                AddressInfo mailingAddress = null;
                if (!TextUtils.isEmpty(mShiftInfo.getMailingAddress())) {
                    mailingAddress = new AddressInfo();
                    mailingAddress.setName(mShiftInfo.getMailingAddress());
                    mailingAddress.setAddressDetail(mShiftInfo.getMailingAddress());
                }
                ShiftAddressActivity.start(
                        getContext(),
                        ShiftAddressActivity.ADDRESS_TYPE_MAILING,
                        mailingAddress
                );
                break;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift_info);
    }

    @Override
    protected ShiftInfoContract.Presenter onCreatePresenter() {
        return new ShiftInfoPresenter(this);
    }

    @Override
    public void showShiftInfo(ShiftInfo shiftInfo) {
        mShiftInfo = shiftInfo;
//        mMobilePhone.setText(maskMobileNumber(shiftInfo.getMobile()));
        mMobilePhone.setText(shiftInfo.getMobile());

        if (TextUtils.isEmpty(shiftInfo.getStartTime()) &&
                TextUtils.isEmpty(shiftInfo.getEndTime())) {
            mTurnoverTimeDay.setText(R.string.label_select_shift_time);
        } else {
            String turnoverTimeDay = String.format("%s - %s",
                    shiftInfo.getStartTime(), shiftInfo.getEndTime());
            mTurnoverTimeDay.setText(turnoverTimeDay);
        }
        if (TextUtils.isEmpty(shiftInfo.getAddressTitle()) &&
                TextUtils.isEmpty(shiftInfo.getAddressDesc())) {
            mTurnoverAddressDay.setText(R.string.label_select_shift_address);
        } else {
            mTurnoverAddressDay.setText(shiftInfo.getAddressTitle());
        }

        if (TextUtils.isEmpty(shiftInfo.getSecondStartTime()) &&
                TextUtils.isEmpty(shiftInfo.getSecondEndTime())) {
            mTurnoverTimeNight.setText(R.string.label_select_shift_time);
        } else {
            String turnoverTimeNight = String.format("%s - %s",
                    shiftInfo.getSecondStartTime(), shiftInfo.getSecondEndTime());
            mTurnoverTimeNight.setText(turnoverTimeNight);
        }
        if (TextUtils.isEmpty(shiftInfo.getSecondAddressTitle()) &&
                TextUtils.isEmpty(shiftInfo.getSecondAddressDesc())) {
            mTurnoverAddressNight.setText(R.string.label_select_shift_address);
        } else {
            mTurnoverAddressNight.setText(shiftInfo.getSecondAddressTitle());
        }

        if (TextUtils.isEmpty(shiftInfo.getMailingAddress())) {
            mMailingAddress.setText(R.string.label_select_shift_address);
        } else {
            mMailingAddress.setText(shiftInfo.getMailingAddress());
        }
    }

    private String maskMobileNumber(String mobile) {
        if (TextUtils.isEmpty(mobile) || mobile.length() != 11) {
            return mobile;
        }
        return mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

    class EditMobileDialog extends BaseDialog {

        @BindView(R.id.phone_number)
        EditText phoneNumberView;

        @OnClick({R.id.btn_negative, R.id.btn_positive})
        void onButtonClick(View view) {
            if (view.getId() == R.id.btn_positive) {
                String phoneNumber = phoneNumberView.getText().toString();
                if (TextUtils.isEmpty(phoneNumber) || phoneNumber.length() != 11) {
                    toast("请输入11位数字");
                    return;
                }
                ShiftInfo shiftInfo = new ShiftInfo();
                shiftInfo.setMobile(phoneNumber);
                presenter.modifyShiftInfo(shiftInfo);
            }
            dismiss();
        }

        private Context context;

        private EditMobileDialog(@NonNull Context context) {
            super(context);
            this.context = context;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.edit_mobile_dialog);
            setOnShowListener(dialog -> showSoftKeyboard());
            setOnDismissListener(dialog -> hideSoftKeyboard());
        }

        private void showSoftKeyboard() {
            PayMainActivity.showSoftInput(phoneNumberView, context);
        }

        private void hideSoftKeyboard() {
            PayMainActivity.hideSoftInput(ShiftInfoActivity.this);
        }


    }


}
