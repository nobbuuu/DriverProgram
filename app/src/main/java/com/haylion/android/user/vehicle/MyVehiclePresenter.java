package com.haylion.android.user.vehicle;

import android.text.TextUtils;

import com.haylion.android.Constants;
import com.haylion.android.data.event.VehicleSyncEvent;
import com.haylion.android.data.model.Vehicle;
import com.haylion.android.data.repo.AccountRepository;
import com.haylion.android.data.repo.PrefserHelper;
import com.haylion.android.mvp.base.BasePresenter;
import com.haylion.android.mvp.rx.ApiSubscriber;
import com.haylion.android.mvp.util.BusUtils;
import com.haylion.android.mvp.util.LogUtils;

import java.util.List;

import androidx.annotation.Nullable;

public class MyVehiclePresenter extends BasePresenter<MyVehicleContract.View, AccountRepository>
        implements MyVehicleContract.Presenter {

    MyVehiclePresenter(MyVehicleContract.View view) {
        super(view, AccountRepository.INSTANCE);
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        getVehicleList();
    }

    private void getVehicleList() {
        repo.getVehicleList(new ApiSubscriber<List<Vehicle>>() {
            @Override
            public void onSuccess(List<Vehicle> vehicleList) {
                view.showVehicleList(vehicleList);
                handleVehicleList(vehicleList);
                BusUtils.post(new VehicleSyncEvent());
            }

            @Override
            public void onError(int code, String msg) {
                view.toast("获取车辆列表失败");
                LogUtils.e("获取车辆数据失败：" + code + ", " + msg);
            }
        });
    }

    public static void handleVehicleList(@Nullable List<Vehicle> vehicleList) {
        if (vehicleList == null || vehicleList.isEmpty()) {
            PrefserHelper.removeVehicleInfoList();
            PrefserHelper.removeVehicleInfo();
            return;
        }
        PrefserHelper.saveVehicleList(vehicleList);

        Vehicle selVehicle = PrefserHelper.getVehicleInfo();
        if (selVehicle == null) { // 当前没有选中车辆
            return;
        }
        boolean removed = true;
        for (Vehicle vehicle : vehicleList) {
            if (selVehicle.getId() == vehicle.getId()) {
                removed = false; // 选中的车辆依然存在
                break;
            }
        }
        if (removed) {
            PrefserHelper.removeVehicleInfo();
        }
    }

    @Override
    public void updateVehicle(VehicleOperation operation, Vehicle vehicle) {
        repo.updateVehicle(operation, vehicle, new ApiSubscriber<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                if (aBoolean) {
                    if (operation == VehicleOperation.ADD) {
                        toast("添加成功");
                    } else if (operation == VehicleOperation.DELETE) {
                        toast("车辆已删除");
                    } else if (operation == VehicleOperation.MODIFY) {
                        toast("修改成功");
                    }
                    getVehicleList();
                } else {
                    toast("操作失败");
                }
            }

            @Override
            public void onError(int code, String msg) {
                String popupMsg = null;
                if (operation == VehicleOperation.ADD) {
                    if (code == Constants.ErrorCode.VEHICLE_NOT_FOUND ||
                            code == Constants.ErrorCode.VEHICLE_IS_DISABLED) {
                        popupMsg = "不存在该车辆，请联系客服或车队长确认";
                    } else if (code == Constants.ErrorCode.VEHICLE_ALREADY_ATTACHED) {
                        popupMsg = "您已添加该车辆，请勿重复添加";
                    } else {
                        popupMsg = "添加失败";
                    }

                } else if (operation == VehicleOperation.DELETE) {
                    popupMsg = "删除失败";

                } else if (operation == VehicleOperation.MODIFY) {
                    if (code == Constants.ErrorCode.VEHICLE_NOT_FOUND ||
                            code == Constants.ErrorCode.VEHICLE_IS_DISABLED) {
                        popupMsg = "不存在该车辆，请联系客服或车队长确认";
                    } else if (code == Constants.ErrorCode.VEHICLE_ALREADY_ATTACHED) {
                        popupMsg = "您已添加该车辆，请修改为其他车辆";
                    } else {
                        popupMsg = "修改失败";
                    }
                }
                if (!TextUtils.isEmpty(popupMsg)) {
                    toast(popupMsg);
                }
            }
        });
    }

    @Override
    public void onViewDestroy() {
        super.onViewDestroy();

    }

}
