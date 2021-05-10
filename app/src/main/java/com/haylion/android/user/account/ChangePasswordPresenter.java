package com.haylion.android.user.account;

import android.text.TextUtils;

import com.haylion.android.data.repo.AccountRepository;
import com.haylion.android.data.util.HashUtil;
import com.haylion.android.mvp.base.BasePresenter;
import com.haylion.android.mvp.rx.ApiSubscriber;
import com.haylion.android.mvp.util.LogUtils;


public class ChangePasswordPresenter extends BasePresenter<ChangePasswordContract.View, AccountRepository> implements
        ChangePasswordContract.Presenter {

    ChangePasswordPresenter(ChangePasswordContract.View view) {
        super(view, AccountRepository.INSTANCE);
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
    }

    @Override
    public void changePassword(String oldPwd, String newPwd, String pwdAgain) {
        if (!verifyInputs(oldPwd, newPwd, pwdAgain)) {
            return;
        }
        String oldPwdEncrypt = oldPwd;
        try {
            oldPwdEncrypt = HashUtil.md5(oldPwd);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String newPwdEncrypt = newPwd;
        try {
            newPwdEncrypt = HashUtil.md5(newPwd);
        } catch (Exception e) {
            e.printStackTrace();
        }

        repo.changePassword(oldPwdEncrypt, newPwdEncrypt, new ApiSubscriber<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                view.onChangePasswordSuccess();
            }

            @Override
            public void onError(int code, String msg) {
                if(code == 200402){
                    msg = "新密码格式错误";
                } else if(code == 200103){
                    msg = "旧密码错误";
                }
                view.onChangePwdFail(msg);
                LogUtils.e("修改密码出错：" + code + ", " + msg);
            }
        });
    }

    private boolean verifyInputs(String oldPwd, String newPwd, String pwdAgain) {
        if (TextUtils.isEmpty(oldPwd)) {
          //  toast("请输入旧密码");
            view.onChangePwdFail("请输入旧密码");
            return false;
        }
        if (TextUtils.isEmpty(newPwd)) {
           // toast("请输入新密码");
            view.onChangePwdFail("请输入新密码");
            return false;
        }
        if (newPwd.length() < 6) {
       //     toast("新密码至少需要6位");
            view.onChangePwdFail("新密码至少需要6位");
            return false;
        }
        if (TextUtils.isEmpty(pwdAgain)) {
            //toast("请再次输入新密码");
            view.onChangePwdFail("请再次输入新密码");
            return false;
        }
        if (!TextUtils.equals(newPwd, pwdAgain)) {
         //   toast("两次输入的新密码不一致");
            view.onChangePwdFail("两次输入的新密码不一致");
            return false;
        }
        return true;
    }


    @Override
    public void onViewDestroy() {
        super.onViewDestroy();
    }

}
