package com.haylion.android.user.setting;

import com.haylion.android.data.model.LatestVersionBean;
import com.haylion.android.mvp.base.AbstractPresenter;
import com.haylion.android.mvp.base.AbstractView;

public class SettingContract {

    interface View extends AbstractView {
        void logoutSuccess();
        void logoutFail();
        void checkUpdatesSuccess(LatestVersionBean version);
        void showCheckingLoading();
        void closeCheckingLoading();

    }

    interface Presenter extends AbstractPresenter {
        void logout();
        void checkUpdates();
    }

}
