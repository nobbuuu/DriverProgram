package com.haylion.android.ordersetting;

import android.util.Log;

import com.amap.api.maps.model.LatLng;
import com.haylion.android.R;
import com.haylion.android.data.model.AddressInfo;
import com.haylion.android.data.model.BackHomeAddress;
import com.haylion.android.data.model.ListenOrderSetting;
import com.haylion.android.data.repo.OrderRepository;
import com.haylion.android.mvp.base.BasePresenter;
import com.haylion.android.mvp.rx.ApiSubscriber;

import javax.crypto.MacSpi;

public class OrderSettingPresenter extends BasePresenter<OrderSettingContract.View, OrderRepository>
        implements OrderSettingContract.Presenter {
    private static final String TAG = "OrderSettingPresenter";
    private ListenOrderSetting mSetting;

    OrderSettingPresenter(OrderSettingContract.View view) {
        super(view, new OrderRepository());
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();

    }


    @Override
    public void onViewDestroy() {
        super.onViewDestroy();

    }

    @Override
    public void getListenOrderSetting() {
        repo.getOrderSetting(new ApiSubscriber<ListenOrderSetting>() {
            @Override
            public void onSuccess(ListenOrderSetting setting) {
                if(setting != null){
                    mSetting = setting;
                    Log.d(TAG, "mSetting:" + setting.toString());
                    view.updateSettingView(setting);
                } else {
                    toast(R.string.toast_failed_to_read_listening_order_settings);
                }
            }

            @Override
            public void onError(int code, String msg) {
                toastError("获取听单设置失败",msg);
            }
        });
    }

    @Override
    public void setListenOrderSetting(ListenOrderSetting setting) {
        repo.setOrderSetting(setting, new ApiSubscriber<Void>() {
            @Override
            public void onSuccess(Void s) {
                Log.d(TAG, "听单设置，设置成功");
                mSetting = setting;
//                view.updateSettingView(setting);
                view.saveSettingSuccess();
                toast("保存成功");
            }

            @Override
            public void onError(int code, String msg) {
                Log.e(TAG, "听单设置失败");
/*                if(code == 400303){
                    toast("请先添加收车地址！");
                    view.updateSettingView(mSetting);
                }*/
//                toast("请先添加收车地址！");
                toastError("保存失败",msg);
                view.updateSettingView(mSetting);
//                toast(msg);
            }
        });
    }

    /**
     * 获取收车地址
     */
    @Override
    public void getBackHomeAddr() {
        repo.getBackHomeAddress(new ApiSubscriber<BackHomeAddress>() {
            @Override
            public void onSuccess(BackHomeAddress backHomeAddress) {
                if(backHomeAddress != null){
                    AddressInfo addressInfo = new AddressInfo();
                    addressInfo.setName(backHomeAddress.getOffWorkAddressTitle());
                    addressInfo.setAddressDetail(backHomeAddress.getOffWorkAddressDescription());
                    addressInfo.setLatLng(new LatLng(backHomeAddress.getOffWorkAddressLat(), backHomeAddress.getOffWorkAddressLon()));
                    view.updateBackHomeAddr(addressInfo);
                }
            }

            @Override
            public void onError(int code, String msg) {
                if(code == 400303){
                    Log.d(TAG, "地址为空");
                }
//                toast(msg);
            }
        });

    }

    @Override
    public void setBackHomeAddr(AddressInfo addr) {
        repo.setBackHomeAddress(addr, new ApiSubscriber<Void>() {
            @Override
            public void onSuccess(Void s) {
                Log.d(TAG, "地址设置成功");
                toast("地址设置成功");
                view.updateBackHomeAddr(addr);
            }

            @Override
            public void onError(int code, String msg) {
                Log.e(TAG, "地址设置失败");
                toastError("地址设置失败", msg);
            }
        });

    }
}
