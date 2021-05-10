package com.haylion.android.user.vehicle;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.haylion.android.R;
import com.haylion.android.common.view.popwindow.BasePopupWindow;

import androidx.annotation.NonNull;

public class VehicleOperationDialog extends BasePopupWindow implements
        View.OnClickListener {

    private OnSelectListener mSelectListener;

    VehicleOperationDialog(@NonNull Context mContext, @NonNull OnSelectListener listener) {
        super(mContext, R.layout.vehicle_operation_dialog,
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        this.mSelectListener = listener;
        initDialog();
    }

    private void initDialog() {
        getView(R.id.rl_main).setOnClickListener(this);
        getView(R.id.modify_vehicle_number).setOnClickListener(this);
        getView(R.id.delete_vehicle).setOnClickListener(this);
        getView(R.id.tv_cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (v.getId() == R.id.modify_vehicle_number) {
            mSelectListener.onSelect(VehicleOperation.MODIFY);
        } else if (v.getId() == R.id.delete_vehicle) {
            mSelectListener.onSelect(VehicleOperation.DELETE);
        }
    }

    public interface OnSelectListener {

        void onSelect(VehicleOperation operation);

    }


}
