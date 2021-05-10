package com.haylion.android.user.wallet;

import com.haylion.android.data.model.WalletTotal;
import com.haylion.android.mvp.base.AbstractPresenter;
import com.haylion.android.mvp.base.AbstractView;

class MyWalletContract {

    interface View extends AbstractView {

        void showWalletInfo(WalletTotal walletTotal);

    }

    interface Presenter extends AbstractPresenter {

        boolean isBindWechat();

    }

}
