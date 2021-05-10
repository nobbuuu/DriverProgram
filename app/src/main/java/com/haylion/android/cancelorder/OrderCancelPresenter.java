package com.haylion.android.cancelorder;

import com.haylion.android.R;
import com.haylion.android.data.model.CancelReason;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.model.OrderDetail;
import com.haylion.android.data.repo.OrderRepository;
import com.haylion.android.data.util.OrderConvert;
import com.haylion.android.mvp.base.BasePresenter;
import com.haylion.android.mvp.rx.ApiSubscriber;
import com.haylion.android.mvp.util.LogUtils;

import java.util.List;

public class OrderCancelPresenter extends BasePresenter<CancelOrderContract.View, OrderRepository>
        implements CancelOrderContract.Presenter {

    OrderCancelPresenter(CancelOrderContract.View view) {
        super(view, new OrderRepository());
    }

    @Override
    public void cancelOrder(int orderId, String reason, String picUrl) {
        repo.cancelOrder(orderId, reason,picUrl, new ApiSubscriber<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                toast(R.string.toast_cancel_order_success);
                view.cancelOrderSuccess();
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.e("取消订单失败：" + code + "," + msg);
                toastError("取消订单失败",msg);
            }
        });
    }

    /**
     * 获取取消原因列表
     */
    @Override
    public void getCancelReasonList() {
        repo.getCancelReason(new ApiSubscriber<List<CancelReason>>() {
            @Override
            public void onSuccess(List<CancelReason> list) {
                view.showCancelReason(list);
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.e("获取取消原因列表失败：" + code + "," + msg);
                toastError("获取取消原因列表失败",msg);
            }
        });
    }

    /**
     * 获取订单详情
     * @param orderId 订单id
     */
    @Override
    public void getOrderDetail(int orderId) {
        repo.getWorkOrderDetail(orderId, new ApiSubscriber<OrderDetail>() {
            @Override
            public void onSuccess(OrderDetail orderDetail) {
                Order order;
                order = OrderConvert.orderDetailToOrder(orderDetail);
                view.getOrderDetailSuccess(order);
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.e("获取订单详情失败：" + code + "," + msg);
                view.getOrderDetailFail(msg);
                toastError(R.string.toast_get_order_info_fail,msg);
            }
        });
    }

}
