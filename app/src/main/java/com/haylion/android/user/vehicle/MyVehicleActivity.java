package com.haylion.android.user.vehicle;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import com.haylion.android.R;
import com.haylion.android.data.base.BaseActivity;
import com.haylion.android.data.base.ConfirmDialog;
import com.haylion.android.data.model.Vehicle;
import com.haylion.android.mvp.util.LogUtils;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.List;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.OnClick;
import io.nlopez.smartadapters.SmartAdapter;
import io.nlopez.smartadapters.adapters.RecyclerMultiAdapter;
import io.nlopez.smartadapters.utils.ViewEventListener;

public class MyVehicleActivity extends BaseActivity<MyVehicleContract.Presenter>
        implements MyVehicleContract.View, ViewEventListener<Vehicle> {

    @OnClick({R.id.btn_back, R.id.add_vehicle})
    void onButtonClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_back) {
            finish();
        } else if (id == R.id.add_vehicle) {
            showAddVehicleDialog(null);
        }
    }

    @BindView(R.id.vehicle_list)
    XRecyclerView mVehicleList;
    private RecyclerMultiAdapter mAdapter;

    @BindView(R.id.vehicle_empty)
    View mVehicleEmpty;

    public static void start(Context context) {
        Intent intent = new Intent(context, MyVehicleActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_vehicle);
        setVehicleAdapter();
    }

    private void setVehicleAdapter() {
        mAdapter = SmartAdapter.empty()
                .map(Vehicle.class, VehicleListItemView.class)
                .listener(this)
                .into(mVehicleList);
        mVehicleList.setPullRefreshEnabled(false);
    }

    @Override
    protected MyVehicleContract.Presenter onCreatePresenter() {
        return new MyVehiclePresenter(this);
    }

    @Override
    public void showVehicleList(List<Vehicle> vehicleList) {
        mAdapter.setItems(vehicleList);
        if (vehicleList == null || vehicleList.isEmpty()) {
            mVehicleEmpty.setVisibility(View.VISIBLE);
            mVehicleList.setVisibility(View.GONE);
        } else {
            mVehicleList.setVisibility(View.VISIBLE);
            mVehicleEmpty.setVisibility(View.GONE);
        }
    }

    @Override
    public void onViewEvent(int action, final Vehicle vehicle, int position, View view) {
        if (action == VehicleListItemView.ACTION_EDIT_VEHICLE) {
            VehicleOperationDialog operationDialog = new VehicleOperationDialog(getContext(), operation -> {
                if (operation == VehicleOperation.MODIFY) {
                    showAddVehicleDialog(vehicle);
                } else if (operation == VehicleOperation.DELETE) {
                    confirmDeleteVehicle(vehicle);
                }
            });
            operationDialog.showAtLocation(mVehicleList, Gravity.BOTTOM, 0, 0);
        }
    }

    private void showAddVehicleDialog(Vehicle vehicle) {
        VehicleOperation operation = vehicle == null ?
                VehicleOperation.ADD : VehicleOperation.MODIFY;
        AddVehicleDialog dialog = new AddVehicleDialog(getContext(), vehicle);
        dialog.setListener(vehicleModified -> {
            LogUtils.d(operation.getOperation() + ", " + vehicleModified.getNumber());
            presenter.updateVehicle(operation, vehicleModified);
        }).show();
    }

    private void confirmDeleteVehicle(Vehicle vehicle) {
        ConfirmDialog confirmDialog = ConfirmDialog.newInstance();
        confirmDialog.setMessage(getString(R.string.tips_confirm_delete_vehicle));
        confirmDialog.setOnClickListener(new ConfirmDialog.OnClickListener() {
            @Override
            public void onPositiveClick(View view) {
                presenter.updateVehicle(VehicleOperation.DELETE, vehicle);
            }
        }).setType(0).show(getSupportFragmentManager(), "");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


}
