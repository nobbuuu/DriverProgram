package com.haylion.android.presenter;

import android.util.Log;

import com.haylion.android.constract.PreSignContract;
import com.haylion.android.data.bean.OrderDetailBean;
import com.haylion.android.data.bean.PhoneBean;
import com.haylion.android.data.repo.OrderRepository;
import com.haylion.android.mvp.base.BasePresenter;
import com.haylion.android.mvp.rx.ApiSubscriber;

public class PreSignPresenter extends BasePresenter<PreSignContract.View, OrderRepository>
        implements PreSignContract.Presenter {
    private static final String TAG = "PreSignPresenter";
    private static int listenOrderStatus = 0; //0：未听单 1：听单中

    public PreSignPresenter(PreSignContract.View view) {
        super(view, new OrderRepository());
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();

    }

    @Override
    public void getOrderDetail(int orderId) {
        repo.getShunfengOrderDetail(orderId, new ApiSubscriber<OrderDetailBean>() {
            @Override
            public void onSuccess(OrderDetailBean orderDetailBean) {
                view.showOrderInfo(orderDetailBean);
            }

            @Override
            public void onError(int code, String msg) {

            }
        });
    }

    @Override
    public void getServicePhoneNum() {
        repo.getServicePhoneNum(new ApiSubscriber<PhoneBean>() {
            @Override
            public void onSuccess(PhoneBean data) {
                Log.d("aaa","phone = " + data.getPhone());
                view.showServicePhoneNum(data);
            }

            @Override
            public void onError(int code, String msg) {
                toast("获取客服电话失败");
            }
        });
    }
}

