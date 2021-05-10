package com.haylion.android.user.money;


import android.util.Log;

import com.haylion.android.data.model.WalletTotal;
import com.haylion.android.data.repo.WalletRepository;
import com.haylion.android.mvp.base.BasePresenter;
import com.haylion.android.mvp.rx.ApiSubscriber;

public class WalletHomePresenter extends BasePresenter<WalletHomeContract.View, WalletRepository>
        implements WalletHomeContract.Presenter {
    private static final String TAG = "WalletHomePresenter";

    WalletHomePresenter(WalletHomeContract.View view) {
        super(view, WalletRepository.INSTANCE);
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
    public void getWalletTotalInfo() {
        repo.getWalletHome(new ApiSubscriber<WalletTotal>() {
            @Override
            public void onSuccess(WalletTotal walletTotal) {
                view.showWalletInfo(walletTotal);
            }

            @Override
            public void onError(int code, String msg) {
                Log.e(TAG, "msg: " + msg);
            }
        });

    }
}
