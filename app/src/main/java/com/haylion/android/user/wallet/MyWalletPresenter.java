package com.haylion.android.user.wallet;

import com.haylion.android.data.model.WalletTotal;
import com.haylion.android.data.model.WxOpenId;
import com.haylion.android.data.repo.AccountRepository;
import com.haylion.android.data.repo.WalletRepository;
import com.haylion.android.mvp.base.BasePresenter;
import com.haylion.android.mvp.rx.ApiSubscriber;
import com.haylion.android.mvp.util.LogUtils;
import com.haylion.maastaxi.event.WxAuthEvent;
import com.tencent.mm.opensdk.modelbase.BaseResp;

import org.greenrobot.eventbus.Subscribe;

public class MyWalletPresenter extends BasePresenter<MyWalletContract.View, WalletRepository>
        implements MyWalletContract.Presenter {

    private WalletTotal mWalletTotal;

    MyWalletPresenter(MyWalletContract.View view) {
        super(view, WalletRepository.INSTANCE);
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        getWalletInfo();
    }

    private void getWalletInfo() {
        repo.getWalletHome(new ApiSubscriber<WalletTotal>() {
            @Override
            public void onSuccess(WalletTotal walletTotal) {
                mWalletTotal = walletTotal;
                if (walletTotal == null) {
                    toast("钱包数据为空");
                } else {
                    view.showWalletInfo(walletTotal);
                }
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.e("获取钱包数据出错：" + code + ", " + msg);
                toast("获取钱包数据失败");
            }
        });
    }

    private void bindWechat(String wxCode) {
        AccountRepository.INSTANCE.bindWechat(wxCode, new ApiSubscriber<WxOpenId>() {
            @Override
            public void onSuccess(WxOpenId openId) {
                toast("绑定成功");
                LogUtils.d("绑定微信成功，openid：" + openId.getOpenid());
                getWalletInfo();
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.e("绑定出错：" + code + ", " + msg);
                toast("绑定失败");
            }
        });
    }

    @Subscribe
    public void onWxLoginResult(WxAuthEvent result) {
        if (result.getErrCode() == BaseResp.ErrCode.ERR_OK) {
            bindWechat(result.getCode());
        } else {
            LogUtils.e("微信登录失败：" + result.getErrCode() + ", " + result.getErrMsg());
        }
    }

    @Override
    protected boolean useEventBus() {
        return true;
    }

    @Override
    public boolean isBindWechat() {
        if (mWalletTotal != null) {
            return mWalletTotal.isBindWechat();
        }
        return false;
    }

    @Override
    public void onViewDestroy() {
        super.onViewDestroy();

    }


}
