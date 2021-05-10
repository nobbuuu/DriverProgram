package com.haylion.android.user.money;

import com.haylion.android.data.model.WalletTotal;
import com.haylion.android.mvp.base.AbstractPresenter;
import com.haylion.android.mvp.base.AbstractView;

public class WalletHomeContract {

    public interface View extends AbstractView {
        void showWalletInfo(WalletTotal walletTotal);
    }

    public interface Presenter extends AbstractPresenter {
        void getWalletTotalInfo();

    }

}
