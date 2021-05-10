package com.haylion.android.common.view.popwindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.haylion.android.R;
import com.haylion.android.data.model.Vehicle;
import com.haylion.android.main.VehicleSelectAdapter;
import com.haylion.android.mvp.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dengzh
 * @date 2019/11/10
 * Description: 听单页-车辆列表弹窗
 */
public class VehiclePopUpWindow extends BasePopupWindow implements View.OnClickListener {

    private List<Vehicle> mVehicleList = new ArrayList<>();
    private RecyclerView recyclerView;
    private VehicleSelectAdapter mAdapter;

    public VehiclePopUpWindow(Context mContext) {
        super(mContext, R.layout.vehicle_select_pop_window,
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        init();
    }

    private void init() {
        recyclerView = getView(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setHasFixedSize(true);
        mAdapter = new VehicleSelectAdapter(mContext, mVehicleList);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new VehicleSelectAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (position == -1) {
                    ToastUtils.showShort(mContext, R.string.toast_please_select_vehicle);
                    return;
                }
                if (listener != null) {
                    listener.onConfirm(position);
                }
                mPopupWindow.dismiss();
            }
        });
        getView(R.id.rl_main).setOnClickListener(this);
        getView(R.id.tv_cancel).setOnClickListener(this);
    }

    public void setData(List<Vehicle> list) {
        mVehicleList.clear();
        mVehicleList.addAll(list);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_main:
            case R.id.tv_cancel:
                mPopupWindow.dismiss();
                break;
        }
    }

    public OnSelectListener listener;

    public void setOnSelectListener(OnSelectListener listener) {
        this.listener = listener;
    }

    public interface OnSelectListener {
        void onConfirm(int position);
    }
}
