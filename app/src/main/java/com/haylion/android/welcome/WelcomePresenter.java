package com.haylion.android.welcome;

import com.haylion.android.data.model.LatestVersionBean;
import com.haylion.android.data.model.Vehicle;
import com.haylion.android.data.repo.AccountRepository;
import com.haylion.android.data.repo.CommonRepository;
import com.haylion.android.mvp.base.BasePresenter;
import com.haylion.android.mvp.rx.ApiSubscriber;
import com.haylion.android.mvp.util.LogUtils;
import com.haylion.android.user.vehicle.MyVehiclePresenter;

import java.util.List;


public class WelcomePresenter extends BasePresenter<WelcomeContract.View, CommonRepository>
        implements WelcomeContract.Presenter {
    private static final String TAG = "WelcomePresenter";

    WelcomePresenter(WelcomeContract.View view) {
        super(view, new CommonRepository());
    }

    @Override
    public void checkUpdates() {
        repo.checkUpdates(new ApiSubscriber<LatestVersionBean>() {

            @Override
            public void onSuccess(LatestVersionBean version) {
                if (version != null) {
                    view.checkUpdatesSuccess(version);
                } else {
                    LogUtils.e("欢迎页面检查更新失败：version==null");
                    //toastError("检查更新出错,请稍候重试","");
                    view.checkUpdatesFail();
                }
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.e("欢迎页面检查更新失败：" + code + "," + msg);
                //toastError("检查更新出错,请稍候重试",msg);
                view.checkUpdatesFail();
            }
        });
    }

    @Override
    public void updateVehicleList() {
        AccountRepository.INSTANCE.getVehicleList(new ApiSubscriber<List<Vehicle>>() {
            @Override
            public void onSuccess(List<Vehicle> vehicleList) {
                MyVehiclePresenter.handleVehicleList(vehicleList);
                view.openMainActivity();
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.e("更新车辆数据出错：" + code + ", " + msg);
                view.openMainActivity();
            }
        });
    }


}
