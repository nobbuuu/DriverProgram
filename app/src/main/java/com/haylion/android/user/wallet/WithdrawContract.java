package com.haylion.android.user.wallet;

import com.haylion.android.mvp.base.AbstractPresenter;
import com.haylion.android.mvp.base.AbstractView;

class WithdrawContract {

    interface View extends AbstractView {

        void onWithdrawSuccess();

    }

    interface Presenter extends AbstractPresenter {

        void withdrawToWechat(String amount);

    }

}
