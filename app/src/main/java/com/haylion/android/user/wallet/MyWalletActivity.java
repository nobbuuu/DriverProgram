package com.haylion.android.user.wallet;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.gyf.immersionbar.ImmersionBar;
import com.haylion.android.R;
import com.haylion.android.common.view.KKViewPager;
import com.haylion.android.data.base.BaseActivity;
import com.haylion.android.data.model.WalletTotal;
import com.haylion.android.data.util.StatusBarUtils;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import butterknife.BindView;
import butterknife.OnClick;

public class MyWalletActivity extends BaseActivity<MyWalletContract.Presenter>
        implements MyWalletContract.View {

    @BindView(R.id.title_layout)
    View mTitleLayout;

    @OnClick({R.id.btn_back, R.id.balance_info, R.id.withdraw})
    void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.withdraw:
                if (presenter.isBindWechat()) {
                    WithdrawActivity.start(getContext());
                } else {
                    BindWechatActivity.start(getContext());
                }
                break;
            case R.id.balance_info:
                toast("该部分仅统计线上收入");
                break;
        }
    }

    @BindView(R.id.balance)
    TextView mBalanceView;

    @BindView(R.id.tab_indicator)
    SmartTabLayout mTabIndicator;
    @BindView(R.id.view_pager)
    KKViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wallet);
        transparentStatusBar();

        FragmentManager fragmentManager = getSupportFragmentManager();
        WalletTabAdapter adapter = new WalletTabAdapter(fragmentManager, getContext());
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(adapter.getCount() - 1);
        mTabIndicator.setViewPager(mViewPager);
    }

    @Override
    protected MyWalletContract.Presenter onCreatePresenter() {
        return new MyWalletPresenter(this);
    }

    @Override
    protected boolean lightStatusBar() {
        return false;
    }

    private void transparentStatusBar() {
        ImmersionBar.with(this)
                .fitsSystemWindows(false) //设置为true，图片无法置顶状态栏
                .transparentStatusBar()
                .statusBarDarkFont(false, 0.2f)
                .titleBarMarginTop(mTitleLayout)
                .init();
    }

    @Override
    public void showWalletInfo(WalletTotal walletTotal) {
        String balanceText = String.valueOf(walletTotal.getBalance());
        mBalanceView.setText(balanceText);
    }


}
