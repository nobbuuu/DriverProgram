package com.haylion.android.user.setting;

import android.util.Log;

import com.haylion.android.data.model.LatestVersionBean;
import com.haylion.android.data.repo.AccountRepository;
import com.haylion.android.data.repo.PrefserHelper;
import com.haylion.android.mvp.base.BasePresenter;
import com.haylion.android.mvp.rx.ApiSubscriber;
import com.haylion.android.mvp.util.LogUtils;

public class SettingPresenter extends BasePresenter<SettingContract.View, AccountRepository>
        implements SettingContract.Presenter {
    private static final String TAG = "SettingPresenter";

    SettingPresenter(SettingContract.View view) {
        super(view, new AccountRepository());
    }

    @Override
    public void logout() {
        repo.logout(new ApiSubscriber<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "退出登录成功");
                PrefserHelper.clearPrefser();
                view.logoutSuccess();
            }

            @Override
            public void onError(int code, String msg) {
                Log.d(TAG, "退出登录失败");
                if(code == 200104){
                    toast("司机未登录");
                }
                view.logoutFail();
            }
        });

    }

    @Override
    public void checkUpdates() {
        Log.d(TAG, "checkUpdates");
        view.showCheckingLoading();
        repo.checkUpdates(new ApiSubscriber<LatestVersionBean>() {

            @Override
            public void onSuccess(LatestVersionBean version) {
                view.closeCheckingLoading();
                if(version!=null){
                    if(!version.hasNewVersion()){
                        toast("当前已是最新版本");
                    }else{
                        view.checkUpdatesSuccess(version);
                    }
                }
            }

            @Override
            public void onError(int code, String msg) {
                view.closeCheckingLoading();
                toastError("检查更新出错",msg);
            }
        });
    }
}
