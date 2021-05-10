package com.haylion.android.user.account;


import com.haylion.android.mvp.base.AbstractPresenter;
import com.haylion.android.mvp.base.AbstractView;

class ChangePasswordContract {

    interface View extends AbstractView {
        void onChangePasswordSuccess();
        void onChangePwdFail(String errorMsg);
    }

    interface Presenter extends AbstractPresenter {
        void changePassword(String oldPassword, String newPassword, String passwordAgain);

    }

}
