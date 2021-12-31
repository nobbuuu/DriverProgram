package com.haylion.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.haylion.android.R;
import com.haylion.android.adapter.GoodsAdapter;
import com.haylion.android.calendar.DateFormatUtil;
import com.haylion.android.calendar.DateStyle;
import com.haylion.android.constract.PreSignContract;
import com.haylion.android.data.base.BaseActivity;
import com.haylion.android.data.bean.OrderDetailBean;
import com.haylion.android.data.bean.PhoneBean;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.util.OrderConvert;
import com.haylion.android.data.widgt.SlideView;
import com.haylion.android.dialog.ChoicePhoneDialog;
import com.haylion.android.mvp.util.ToastUtils;
import com.haylion.android.orderdetail.OrderDetailActivity;
import com.haylion.android.presenter.PreSignPresenter;
import com.haylion.android.utils.AmapUtils;
import com.haylion.android.utils.PhoneUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.haylion.android.orderdetail.OrderDetailActivity.ORDER_ID;

public class OrderPreSignActivity extends BaseActivity<PreSignContract.Presenter> implements PreSignContract.View {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_title_right)
    TextView tvTitleRight;
    @BindView(R.id.rl_title)
    RelativeLayout rlTitle;
    @BindView(R.id.tv_arrive_time)
    TextView tvArriveTime;
    @BindView(R.id.ordertime_tv)
    TextView ordertimeTv;
    @BindView(R.id.phone_number1)
    TextView phoneNumber1;
    @BindView(R.id.phone_take)
    ImageView phoneTake;
    @BindView(R.id.phone_number2)
    TextView phoneNumber2;
    @BindView(R.id.goods_name)
    TextView goods_name;
    @BindView(R.id.phone_sevice)
    ImageView phoneSevice;
    @BindView(R.id.goods_rv)
    RecyclerView goodsRv;
    @BindView(R.id.slideview)
    SlideView slideview;


    private int mOrderId;
    private List<String> carGoList = new ArrayList<>();
    private Order mOrder;
    private String mServicePhone;
    public static String arriveTime;
    public static String totalTime;

    public static void go(Context context, int orderId) {
        Intent intent = new Intent(context, OrderPreSignActivity.class);
        intent.putExtra(ORDER_ID, orderId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordervalify);
        ButterKnife.bind(this);
        tvTitle.setText("订单验签");
        mOrderId = getIntent().getIntExtra(ORDER_ID, 0);
        onEvent();
        presenter.getOrderDetail(mOrderId);
        presenter.getServicePhoneNum();
    }

    @Override
    protected PreSignContract.Presenter onCreatePresenter() {
        return new PreSignPresenter(this);
    }

    private void onEvent() {
        slideview.addSlideListener(new SlideView.OnSlideListener() {
            @Override
            public void onSlideSuccess() {
                OrderSignActivity.go(getContext(), mOrderId, 6, carGoList);
                finish();
            }
        });
    }

    @Override
    public void showOrderInfo(OrderDetailBean data) {
        mOrder = OrderConvert.orderShunfengDetailToOrder(data);
        String actualDeliveryTime = data.getActualDeliveryTime();
        long l = System.currentTimeMillis();
        if (!TextUtils.isEmpty(actualDeliveryTime)) {
            tvArriveTime.setText("送达时间：" + actualDeliveryTime);
        } else {
            String time = DateFormatUtil.getTime(l, DateStyle.HH_MM.getValue());
            tvArriveTime.setText("送达时间：" + time);
        }
        arriveTime = tvArriveTime.getText().toString();
        String actualDeliveryTimeSecond = data.getActualDeliveryTimeSecond();
        String beginTimeSecond = data.getBeginTimeSecond();
        if (actualDeliveryTimeSecond != null && beginTimeSecond != null) {
            long costTime = Long.valueOf(actualDeliveryTimeSecond) - Long.valueOf(beginTimeSecond);
            String matchTime = AmapUtils.matchTime(costTime / 1000);
            ordertimeTv.setText("订单耗时：" + matchTime);
        }
        totalTime = ordertimeTv.getText().toString();
        carGoList = data.getCargoList();
        Log.d("aaa", "carGoList = " + carGoList);
        if (carGoList != null && carGoList.size() > 0) {
            Log.d("aaa", "carGoList.size = " + carGoList.size());
            goods_name.setText("货物列表（" + carGoList.size() + "）");
            GoodsAdapter mGoodsAdapter = new GoodsAdapter(this, carGoList, R.layout.rvitem_goods);
            goodsRv.setAdapter(mGoodsAdapter);
        }
        String mobile = mOrder.getPickupContactMobile();
        String mobile1 = mOrder.getPickupContactMobile1();
        String mobile2 = mOrder.getPickupContactMobile2();

        String mobile3 = mOrder.getDeliveryContactMobile();
        String mobile4 = mOrder.getDeliveryContactMobile1();
        String mobile5 = mOrder.getDeliveryContactMobile2();
        List<String> phoneList = new ArrayList<>();
        List<String> deliveryPhoneList = new ArrayList<>();
        if (mobile != null && !mobile.isEmpty()) {
            phoneList.add(mobile);
        }
        if (mobile1 != null && !mobile1.isEmpty()) {
            phoneList.add(mobile1);
        }
        if (mobile2 != null && !mobile2.isEmpty()) {
            phoneList.add(mobile2);
        }
        if (mobile3 != null && !mobile3.isEmpty()) {
            deliveryPhoneList.add(mobile3);
        }
        if (mobile4 != null && !mobile4.isEmpty()) {
            deliveryPhoneList.add(mobile4);
        }
        if (mobile5 != null && !mobile5.isEmpty()) {
            deliveryPhoneList.add(mobile5);
        }
        if (mOrder.getOrderStatus() < 4) {
            if (phoneList.size() > 0) {
                phoneNumber1.setText("取货电话（" + phoneList.size() + "位联系人）");
            }
        } else {
            if (deliveryPhoneList.size() > 0) {
                phoneNumber1.setText("送货电话（" + deliveryPhoneList.size() + "位联系人）");
            }
        }
    }

    @Override
    public void showServicePhoneNum(PhoneBean bean) {
        mServicePhone = bean.getPhone();
        phoneNumber2.setText("客服电话：" + bean.getPhone());
    }

    @OnClick({R.id.phone_take, R.id.phone_sevice, R.id.iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.phone_take:
                if (mOrder != null) {
                    if (mOrder.getOrderStatus() < 4) {
                        String mobile = mOrder.getPickupContactMobile();
                        String mobile1 = mOrder.getPickupContactMobile1();
                        String mobile2 = mOrder.getPickupContactMobile2();
                        if (mobile.isEmpty() && mobile1.isEmpty() && mobile2.isEmpty()) {
                            ToastUtils.showLong(getContext(), "暂无电话数据");
                            return;
                        }
                    } else {
                        String mobile = mOrder.getDeliveryContactMobile();
                        String mobile1 = mOrder.getDeliveryContactMobile1();
                        String mobile2 = mOrder.getDeliveryContactMobile2();
                        if (mobile.isEmpty() && mobile1.isEmpty() && mobile2.isEmpty()) {
                            ToastUtils.showLong(getContext(), "暂无电话数据");
                            return;
                        }
                    }
                    new ChoicePhoneDialog(getContext(), mOrder).show();
                }
                break;
            case R.id.phone_sevice:
                if (!TextUtils.isEmpty(mServicePhone)) {
                    PhoneUtils.call(getContext(), mServicePhone);
                }
                break;
        }
    }
}
