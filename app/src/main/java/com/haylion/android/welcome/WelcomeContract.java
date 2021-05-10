package com.haylion.android.welcome;

import com.haylion.android.data.model.LatestVersionBean;
import com.haylion.android.mvp.base.AbstractPresenter;
import com.haylion.android.mvp.base.AbstractView;

public class WelcomeContract {

    interface View extends AbstractView {
        void checkUpdatesSuccess(LatestVersionBean version);

        void checkUpdatesFail();

        void openMainActivity();
    }

    interface Presenter extends AbstractPresenter {

        void checkUpdates();

        void updateVehicleList();

    }

}
