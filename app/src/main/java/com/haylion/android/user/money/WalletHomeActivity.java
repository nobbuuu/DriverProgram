package com.haylion.android.user.money;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.haylion.android.R;
import com.haylion.android.data.base.BaseActivity;
import com.haylion.android.data.model.BankCardOutputFormList;
import com.haylion.android.data.model.WalletTotal;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


public class WalletHomeActivity extends BaseActivity<WalletHomeContract.Presenter> implements
        WalletHomeContract.View {
    private static final String TAG = "WalletHomeActivity";

    @BindView(R.id.tv_rest_money)
    TextView tvRestMoney;
    @BindView(R.id.tv_withdraw)
    TextView tvWithdraw;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_home);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.getWalletTotalInfo();
    }

    private void initView() {

    }


    @OnClick({R.id.tv_add_bank_card, R.id.ll_add_bank_card, R.id.tv_wallet_detail, R.id.iv_back, R.id.ll_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_add_bank_card:
            case R.id.ll_add_bank_card:
                toast("需求变更，暂时不做了");
/*                Intent intent = new Intent(this, AddBankCardActivity.class);
                startActivity(intent);*/
                break;
            case R.id.ll_back:
            case R.id.iv_back:
                finish();
                return;
            case R.id.tv_wallet_detail:
                Intent intent = new Intent(this, IncomeActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }

    }


    public static void start(Context context, int dutyId, int orderType, double cost) {
        Log.d(TAG, "orderId: --- dutyId:" + dutyId);
        Intent intent = new Intent(context, WalletHomeActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected WalletHomeContract.Presenter onCreatePresenter() {
        return new WalletHomePresenter(this);
    }


    @Override
    public void showWalletInfo(WalletTotal wallet) {
        if (wallet != null) {
            tvRestMoney.setText(String.valueOf(wallet.getBalance()));
            tvWithdraw.setText(String.valueOf(wallet.getWithdrawing()));
            List<BankCardOutputFormList> list = wallet.getBankCardOutputFormList();
        } else {
            //沒有收入
        }
    }

}
