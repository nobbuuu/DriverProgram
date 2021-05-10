package com.haylion.android.address;

import com.haylion.android.mvp.base.AbstractPresenter;
import com.haylion.android.mvp.base.AbstractView;

public class SearchAddrContract {

    interface View extends AbstractView {
        void getConfigSuccess();
        void getConfigFail();
    }

    interface Presenter extends AbstractPresenter {
        void getConfigInfo(String deviceId);
    }

}
