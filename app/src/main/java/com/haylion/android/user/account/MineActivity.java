package com.haylion.android.user.account;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.haylion.android.R;
import com.haylion.android.data.model.Driver;
import com.haylion.android.data.model.OrderTimeType;
import com.haylion.android.data.base.BaseActivity;
import com.haylion.android.orderlist.OrderListActivity;
import com.haylion.android.user.money.WalletHomeActivity;
import com.haylion.android.user.setting.SettingActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MineActivity extends BaseActivity<MineContract.Presenter>
        implements MineContract.View {
    private static final String TAG = "MineActivity";
    @BindView(R.id.work_number)
    TextView tvWorkNumber;
    @BindView(R.id.vehicle_number)
    TextView tvVehicleNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        presenter.getDriverInfo();
    }

    @Override
    protected MineContract.Presenter onCreatePresenter() {
        return new MinePresenter(this);
    }

    @OnClick(R.id.iv_back)
    public void clickBackMenu() {
        finish();
    }

    @OnClick({R.id.ll_money, R.id.ll_setting, R.id.ll_history_order, R.id.ll_history_achieve})
    public void onClick(View view) {
        Log.d(TAG, "onClick view id: " + view.getId());
        Intent intent;
        switch (view.getId()) {
            case R.id.ll_money:
                Log.d(TAG, "start WalletHomeActivity");
                intent = new Intent(this, WalletHomeActivity.class);
                intent.putExtra("header", "钱包");
                startActivity(intent);
                break;
            case R.id.ll_history_order:
                Log.d(TAG, "start OrderListActivity");
/*                intent = new Intent(this, OrderListActivity.class);
                intent.putExtra("header", "历史订单");
                startActivity(intent);*/
                OrderListActivity.start(this, OrderTimeType.HISTORY);
                break;
            case R.id.ll_history_achieve:
                //todo
/*                Log.d(TAG, "start OrderListActivity");
                intent = new Intent(this, OrderListActivity.class);
                intent.putExtra("header", "历史成绩");
                startActivity(intent);*/
                toast("开发中");
                break;
            case R.id.ll_setting:
                intent = new Intent(this, SettingActivity.class);
                intent.putExtra("header", "设置");
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onGetDriverInfoSuccess(Driver driver) {
        tvWorkNumber.setText(driver.getCode());
        tvVehicleNumber.setText(driver.getPlateNumber());
    }
}
