package com.haylion.android.user.account;


import android.util.Log;

import com.haylion.android.data.model.Driver;
import com.haylion.android.data.repo.AccountRepository;
import com.haylion.android.mvp.base.BasePresenter;
import com.haylion.android.mvp.rx.ApiSubscriber;

public class MinePresenter extends BasePresenter<MineContract.View, AccountRepository>
        implements MineContract.Presenter {
    private static final String TAG = "LoginPresenter";

    MinePresenter(MineContract.View view) {
        super(view, new AccountRepository());
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();

    }

    @Override
    public void getDriverInfo() {
        repo.getDriverInfo(new ApiSubscriber<Driver>() {
            @Override
            public void onSuccess(Driver driver) {
                view.onGetDriverInfoSuccess(driver);
            }

            @Override
            public void onError(int code, String msg) {
                Log.e(TAG, msg);
                toast(msg);
            }
        });
    }

    @Override
    public void onViewDestroy() {
        super.onViewDestroy();

    }

}
