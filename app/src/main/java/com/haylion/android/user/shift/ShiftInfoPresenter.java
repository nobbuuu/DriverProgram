package com.haylion.android.user.shift;

import com.amap.api.maps.model.LatLng;
import com.haylion.android.Constants;
import com.haylion.android.data.event.AddressChangedEvent;
import com.haylion.android.data.model.AddressInfo;
import com.haylion.android.data.model.ShiftInfo;
import com.haylion.android.data.repo.AccountRepository;
import com.haylion.android.mvp.base.BasePresenter;
import com.haylion.android.mvp.rx.ApiSubscriber;
import com.haylion.android.mvp.util.LogUtils;

import org.greenrobot.eventbus.Subscribe;

public class ShiftInfoPresenter extends BasePresenter<ShiftInfoContract.View, AccountRepository>
        implements ShiftInfoContract.Presenter {

    ShiftInfoPresenter(ShiftInfoContract.View view) {
        super(view, AccountRepository.INSTANCE);
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        getShiftInfo();
    }

    private void getShiftInfo() {
        repo.getShiftInfo(new ApiSubscriber<ShiftInfo>() {
            @Override
            public void onSuccess(ShiftInfo shiftInfo) {
                if (shiftInfo == null) {
                    toast("个人信息为空");
                } else {
                    view.showShiftInfo(shiftInfo);
                }
            }

            @Override
            public void onError(int code, String msg) {
                view.toast("获取个人信息失败");
                LogUtils.e("获取个人信息出错：" + code + ", " + msg);
            }
        });
    }

    @Subscribe
    public void onAddressChanged(AddressChangedEvent event) {
        ShiftInfo shiftInfo = new ShiftInfo();
        if (event.addressType == ShiftAddressActivity.ADDRESS_TYPE_TURNOVER_DAY) {
            shiftInfo.setAddressTitle(event.addressInfo.getName());
            shiftInfo.setAddressDesc(event.addressInfo.getAddressDetail());

        } else if (event.addressType == ShiftAddressActivity.ADDRESS_TYPE_TURNOVER_NIGHT) {
            shiftInfo.setSecondAddressTitle(event.addressInfo.getName());
            shiftInfo.setSecondAddressDesc(event.addressInfo.getAddressDetail());

        } else if (event.addressType == ShiftAddressActivity.ADDRESS_TYPE_MAILING) {
            shiftInfo.setMailingAddress(event.addressInfo.getAddressDetail());
            LatLng latLng = event.addressInfo.getLatLng();
            shiftInfo.setMailingAddressLat(latLng.latitude);
            shiftInfo.setMailingAddressLng(latLng.longitude);
        }
        modifyShiftInfo(shiftInfo);
    }

    @Override
    protected boolean useEventBus() {
        return true;
    }

    @Override
    public void modifyShiftInfo(ShiftInfo shiftInfo) {
        repo.modifyShiftInfo(shiftInfo, new ApiSubscriber<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                if (aBoolean) {
                    toast("修改成功");
                    getShiftInfo();
                } else {
                    view.toast("修改失败");
                }
            }

            @Override
            public void onError(int code, String msg) {
                if (code == Constants.ErrorCode.PHONE_NUMBER_EXISTS) {
                    toast("手机号码和其他司机重复，请再次确认");
                } else if (code == Constants.ErrorCode.PHONE_NUMBER_NO_CHANGE) {
                    toast("输入的手机号和当前号码相同");
                } else {
                    view.toast("修改失败");
                }
                LogUtils.e("修改排班信息出错：" + code + ", " + msg);
            }
        });
    }

    @Override
    public void onViewDestroy() {
        super.onViewDestroy();

    }


}
