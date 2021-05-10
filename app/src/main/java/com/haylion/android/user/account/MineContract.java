package com.haylion.android.user.account;

import com.haylion.android.data.model.Driver;
import com.haylion.android.mvp.base.AbstractPresenter;
import com.haylion.android.mvp.base.AbstractView;

public class MineContract {

    interface View extends AbstractView {
        void onGetDriverInfoSuccess(Driver driver);
    }

    interface Presenter extends AbstractPresenter {
        void getDriverInfo();
    }

}
