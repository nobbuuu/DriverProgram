package com.haylion.android.user.shift;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.haylion.android.R;
import com.haylion.android.common.view.popwindow.BasePopupWindow;
import com.haylion.android.mvp.util.ToastUtils;

import java.util.Locale;

public class TurnoverTimePicker extends BasePopupWindow implements View.OnClickListener {

    private String mTitle;

    private Context mContext;

    private TimePicker mStartTimePicker;
    private TimePicker mEndTimePicker;

    private Callback mCallback;

    TurnoverTimePicker(Context context, String title) {
        super(context, R.layout.turnover_time_picker,
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        this.mContext = context;
        this.mTitle = title;
        initDialog();
    }

    private void initDialog() {
        mStartTimePicker = getView(R.id.start_time_picker);
        mStartTimePicker.setIs24HourView(true);
        mEndTimePicker = getView(R.id.end_time_picker);
        mEndTimePicker.setIs24HourView(true);

        TextView title = getView(R.id.title);
        title.setText(mTitle);

        getView(R.id.mask).setOnClickListener(this);
        getView(R.id.cancel).setOnClickListener(this);
        getView(R.id.save).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mask:
            case R.id.cancel:
                dismiss();
                break;
            case R.id.save:
                callbackTurnoverTime();
                break;
        }
    }

    private void callbackTurnoverTime() {
        int startHour;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            startHour = mStartTimePicker.getHour();
        } else {
            startHour = mStartTimePicker.getCurrentHour();
        }
        int endHour;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            endHour = mEndTimePicker.getHour();
        } else {
            endHour = mEndTimePicker.getCurrentHour();
        }
        if (endHour < startHour) {
            ToastUtils.showShort(mContext, "结束时间应晚于开始时间");
            return;
        }

        int startMinute;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            startMinute = mStartTimePicker.getMinute();
        } else {
            startMinute = mStartTimePicker.getCurrentMinute();
        }
        int endMinute;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            endMinute = mEndTimePicker.getMinute();
        } else {
            endMinute = mEndTimePicker.getCurrentMinute();
        }
        if (endHour == startHour && endMinute <= startMinute) {
            ToastUtils.showShort(mContext, "结束时间应晚于开始时间");
            return;
        }

        if (mCallback != null) {
            String startTime = String.format(Locale.getDefault(),
                    "%d:%d:00", startHour, startMinute);
            String endTime = String.format(Locale.getDefault(),
                    "%d:%d:00", endHour, endMinute);
            mCallback.onTimeSet(startTime, endTime);
        }
        dismiss();
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    interface Callback {

        void onTimeSet(String startTime, String endTime);

    }


}
