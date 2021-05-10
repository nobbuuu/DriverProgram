package com.haylion.android.user.account;

import com.haylion.android.data.model.Vehicle;
import com.haylion.android.mvp.base.AbstractPresenter;
import com.haylion.android.mvp.base.AbstractView;

import java.util.List;

public class LoginContract {

    interface View extends AbstractView {
        void onLoginSuccess();
        void showLoginFailTips(String msg);

        void updateVehicleView(List<Vehicle> vehicles);

        void showDialDialog(String phone);
    }

    interface Presenter extends AbstractPresenter {
        void login(String username, String password, String vehicleNumber);

        void getVehicleList(String code);

        void getServiceTelNumber(boolean showDialog);
    }

}
