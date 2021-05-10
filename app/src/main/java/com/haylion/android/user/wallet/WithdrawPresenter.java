package com.haylion.android.user.wallet;

import com.haylion.android.data.repo.WalletRepository;
import com.haylion.android.mvp.base.BasePresenter;
import com.haylion.android.mvp.rx.ApiSubscriber;
import com.haylion.android.mvp.util.LogUtils;

public class WithdrawPresenter extends BasePresenter<WithdrawContract.View, WalletRepository>
        implements WithdrawContract.Presenter {

    WithdrawPresenter(WithdrawContract.View view) {
        super(view, WalletRepository.INSTANCE);
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();

    }

    @Override
    public void withdrawToWechat(String amount) {
        try {
            double parsedAmount = Double.parseDouble(amount);
            withdrawToWechatReally(parsedAmount);
        } catch (NumberFormatException throwable) {
            LogUtils.e("提现金额格式有误：" + amount);
        }
    }

    private void withdrawToWechatReally(double amount) {
        repo.withdrawToWechat(amount, new ApiSubscriber<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                if (aBoolean != null && aBoolean) {
                    view.onWithdrawSuccess();
                } else {
                    toast("提现失败");
                }
            }

            @Override
            public void onError(int code, String msg) {
                toast("提现失败");
                LogUtils.e("提现失败：" + code + ", " + msg);
            }
        });
    }

    @Override
    public void onViewDestroy() {
        super.onViewDestroy();

    }

}
