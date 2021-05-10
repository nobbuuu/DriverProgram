package com.haylion.android.pay;


import android.util.Log;

import com.haylion.android.R;
import com.haylion.android.data.event.CarpoolOrderPaid;
import com.haylion.android.data.model.PayInfo;
import com.haylion.android.data.model.PaymentResult;
import com.haylion.android.data.model.ServiceFee;
import com.haylion.android.data.repo.OrderRepository;

import com.haylion.android.mvp.base.BasePresenter;
import com.haylion.android.mvp.rx.ApiSubscriber;
import com.haylion.android.mvp.util.BusUtils;
import com.haylion.android.mvp.util.LogUtils;

import java.util.List;

public class PayMainPresenter extends BasePresenter<PayMainContract.View, OrderRepository>
        implements PayMainContract.Presenter {
    private static final String TAG = "PayMainPresenter";

    PayMainPresenter(PayMainContract.View view) {
        super(view, OrderRepository.INSTANCE);
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
                LogUtils.e(TAG, "确认支付消息上报失败:" + code + "," + msg);
                toastError("确认失败", msg);
            }
        });
    }

    @Override
    public void payRequest(PayInfo payInfo) {
        repo.paymentRequest(payInfo, new ApiSubscriber<PaymentResult>() {

            @Override
            public void onSuccess(PaymentResult paymentResult) {
                Log.d(TAG, "payRequest onSuccess");
                view.payRequestSuccess(payInfo.getPaymentMode());
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.e(TAG, "payRequest 失败:" + code + "," + msg);
                view.payRequestFail();
                LogUtils.e(TAG, "msg:" + msg);
                if (code == 400001) {
                    toast(R.string.toast_order_not_exist);
                } else if (code == 400002) {
                    toast("订单状态错误");
                } else if (code == 400015) {
                    toast("服务费金额有误");
                } else {
                    toastError("", msg);
                }
            }
        });

    }

    @Override
    public void payRequestCarpool(PayInfo payInfo) {
        repo.paymentRequestCarpool(payInfo, new ApiSubscriber<List<PaymentResult>>() {
            @Override
            public void onSuccess(List<PaymentResult> paymentResults) {
                BusUtils.post(new CarpoolOrderPaid(payInfo.getCarpoolCode()));
                view.onCarpoolOrderPaid();
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.e(TAG, "付款失败:" + code + "," + msg);
                toast("提交账单失败");
            }
        });
    }

    @Override
    public void getServiceCost(Double cost, int orderId) {
        repo.getServiceCost(cost, orderId, new ServiceFeeCallback());
    }

    private class ServiceFeeCallback extends ApiSubscriber<ServiceFee> {

        @Override
        public void onSuccess(ServiceFee serviceFee) {
            Log.d(TAG, "serviceFee:" + serviceFee.getServiceFee() + ", showServiceFee:" + serviceFee.getShowServiceFee());
            view.updateServiceCost(serviceFee);
        }

        @Override
        public void onError(int code, String msg) {
            LogUtils.e(TAG, "获取服务费失败:" + code + "," + msg);
            toastError("获取服务费失败", msg);
        }

    }

    @Override
    public void getCarpoolServiceFee(String carpoolCode) {
        repo.getCarpoolServiceFee(carpoolCode, new ServiceFeeCallback());
    }

}
