package com.haylion.android.orderdetail.map;

import android.util.Log;

import com.haylion.android.Constants;
import com.haylion.android.R;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.model.OrderDetail;
import com.haylion.android.data.model.ServiceNumber;
import com.haylion.android.data.repo.OrderRepository;
import com.haylion.android.data.repo.PrefserHelper;

import com.haylion.android.data.util.OrderConvert;
import com.haylion.android.mvp.base.BasePresenter;
import com.haylion.android.mvp.rx.ApiSubscriber;

public class ShowInMapPresenter extends BasePresenter<ShowInMapContract.View, OrderRepository>
        implements ShowInMapContract.Presenter {

    private static final String TAG = "ShowInMapPresenter";

    ShowInMapPresenter(ShowInMapContract.View view) {
        super(view, new OrderRepository());
    }

/*

    @Override
    public void getOrderDetail(int orderId) {
        Log.d(TAG, "getOrderDetail, id:" + orderId);
*/
/*        List<Order> list = OrderConvert.mockOrderListData();
        mOrder = list.get(6);
        view.getOrderDetailSuccess(mOrder);*//*


        repo.getWorkOrderDetail(orderId, new ApiSubscriber<OrderDetail>() {
            @Override
            public void onSuccess(OrderDetail orderDetail) {
                Order order;
                order = OrderConvert.orderDetailToOrder(orderDetail);
                view.getOrderDetailSuccess(order);
            }

            @Override
            public void onError(int code, String msg) {
                Log.e(TAG, "getWorkOrderDetail FAIL, " + msg);
                view.getOrderDetailFail(msg);
                toast(msg);
            }
        });
    }

    @Override
    public void payConfirm(int orderId, int state) {
        repo.paymentConfirm(orderId, state, new ApiSubscriber<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                Log.d(TAG, "确认支付消息上报成功");
                view.payConfirmSuccess();
            }

            @Override
            public void onError(int code, String msg) {
                Log.e(TAG, "确认支付消息上报失败" + msg);
                toast(msg);
            }
        });

    }

    @Override
    public void getServiceTelNumber() {
        repo.getSericeTelNumber(new ApiSubscriber<ServiceNumber>() {
            @Override
            public void onSuccess(ServiceNumber serviceNumber) {
                PrefserHelper.setCache(PrefserHelper.KEY_SERVICE_NUMBER, serviceNumber.getPhone());
                Constants.SERVICE_PHONE = serviceNumber.getPhone();
                view.showDialDialog(serviceNumber.getPhone());

            }

            @Override
            public void onError(int code, String msg) {
                toast(R.string.toast_get_customer_service_phone_error);
            }
        });
    }
*/


}
