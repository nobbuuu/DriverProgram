package com.haylion.android.orderdetail.trip;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gyf.immersionbar.ImmersionBar;
import com.haylion.android.R;
import com.haylion.android.data.base.BaseActivity;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.model.OrderDetail;
import com.haylion.android.data.repo.OrderRepository;
import com.haylion.android.data.util.BusinessUtils;
import com.haylion.android.data.util.StatusBarUtils;
import com.haylion.android.main.MainActivity;
import com.haylion.android.mvp.rx.ApiSubscriber;
import com.haylion.android.mvp.util.LogUtils;
import com.haylion.android.orderdetail.OrderDetailActivity;

import java.util.List;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.OnClick;

public class CarpoolTripEndActivity extends BaseActivity {

    @BindView(R.id.iv_status)
    ImageView ivStatus;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.tv_amount)
    TextView tvAmount;

    @OnClick({R.id.iv_back, R.id.tv_confirm})
    void onViewClick(View view) {
        if (view.getId() == R.id.tv_confirm) {
            startActivity(new Intent(getContext(), MainActivity.class));
        }
        finish();
    }

    public static final String EXTRA_CARPOOL_CODE = OrderDetailActivity.EXTRA_CARPOOL_CODE;

    public static void start(Context context, String carpoolCode) {
        Intent intent = new Intent(context, CarpoolTripEndActivity.class);
        intent.putExtra(EXTRA_CARPOOL_CODE, carpoolCode);
        context.startActivity(intent);
    }

    private OrderRepository mOrderRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carpool_trip_end);
        ImmersionBar.with(this).statusBarColor(R.color.c_4b8afb)
                .statusBarDarkFont(true, 0.2f)
                .init();
        String carpoolCode = parseCarpoolCode();
        if (TextUtils.isEmpty(carpoolCode)) {
            toast("拼单数据不正确");
            finish();
        }
        mOrderRepository = new OrderRepository();
        mOrderRepository.onCreate();
        getCarpoolDetails(parseCarpoolCode());
    }

    private void getCarpoolDetails(String carpoolCode) {
        mOrderRepository.getCarpoolDetails(carpoolCode, new ApiSubscriber<List<OrderDetail>>() {
            @Override
            public void onSuccess(List<OrderDetail> orderDetails) {
                if (orderDetails == null || orderDetails.isEmpty()) {
                    toast("拼单数据为空");
                    finish();
                } else {
                    handleCarpoolDetails(orderDetails);
                }
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.e("获取拼单详情出错：" + code + ", " + msg);
                toast("获取拼车信息失败");
                finish();
            }
        });
    }

    private void handleCarpoolDetails(List<OrderDetail> orderDetails) {
        double unpaidAmount = 0.0;
        for (OrderDetail orderDetail : orderDetails) {
            if (orderDetail.getOrderStatus() == Order.ORDER_STATUS_WAIT_PAY) {
                unpaidAmount += orderDetail.getAmount();
            }
        }
        if (unpaidAmount != 0.0) {
            ivStatus.setImageResource(R.mipmap.ic_trip_wait_pay);
            tvStatus.setText(R.string.trip_end_status_parent_not_paid);
            tvAmount.setText(BusinessUtils.moneySpec(unpaidAmount));
        } else {
            ivStatus.setImageResource(R.mipmap.ic_trip_paid);
            tvStatus.setText(R.string.trip_end_status_paid);
            tvAmount.setText(null);
        }
    }

    private String parseCarpoolCode() {
        return getIntent().getStringExtra(EXTRA_CARPOOL_CODE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mOrderRepository.onDestroy();
    }


}
