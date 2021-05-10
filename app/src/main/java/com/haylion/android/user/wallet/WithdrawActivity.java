package com.haylion.android.user.wallet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.haylion.android.R;
import com.haylion.android.data.base.BaseActivity;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.OnClick;

public class WithdrawActivity extends BaseActivity<WithdrawContract.Presenter>
        implements WithdrawContract.View {

    @OnClick({R.id.btn_back, R.id.btn_withdraw})
    void onButtonClick(View view) {
        if (view.getId() == R.id.btn_back) {
            finish();
        } else if (view.getId() == R.id.btn_withdraw) {
            toast("确认提现");
        }
    }

    @BindView(R.id.wx_avatar)
    ImageView mWxAvatar;
    @BindView(R.id.wx_nickname)
    TextView mWxNickname;

    @BindView(R.id.withdraw_amount)
    EditText mWithdrawAmount;
    @BindView(R.id.wallet_balance)
    TextView mWalletBalance;

    public static void start(Context context) {
        Intent intent = new Intent(context, WithdrawActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);
    }

    @Override
    protected WithdrawContract.Presenter onCreatePresenter() {
        return new WithdrawPresenter(this);
    }


    @Override
    public void onWithdrawSuccess() {

        finish();
    }


}
