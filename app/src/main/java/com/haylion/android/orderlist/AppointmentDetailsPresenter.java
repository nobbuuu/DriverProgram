package com.haylion.android.orderlist;

import com.haylion.android.data.event.AppointmentChangedEvent;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.model.OrderDetail;
import com.haylion.android.data.model.OrderStatus;
import com.haylion.android.data.repo.OrderRepository;
import com.haylion.android.data.util.OrderConvert;
import com.haylion.android.mvp.base.BasePresenter;
import com.haylion.android.mvp.rx.ApiSubscriber;
import com.haylion.android.mvp.util.BusUtils;
import com.haylion.android.mvp.util.LogUtils;

import org.greenrobot.eventbus.EventBus;

public class AppointmentDetailsPresenter extends BasePresenter<AppointmentDetailsContract.View,
        OrderRepository> implements AppointmentDetailsContract.Presenter {

    private int mOrderId;

    private Order mOrder;

    AppointmentDetailsPresenter(AppointmentDetailsContract.View view, int orderId) {
        super(view, OrderRepository.INSTANCE);
        this.mOrderId = orderId;
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        getOrderDetails();
    }

    private void getOrderDetails() {
        repo.getWorkOrderDetail(mOrderId, new ApiSubscriber<OrderDetail>() {
            @Override
            public void onSuccess(OrderDetail orderDetail) {
                Order order = OrderConvert.orderDetailToOrder(orderDetail);
                mOrder = order;
                view.showOrderDetails(order);
            }

            @Override
            public void onError(int code, String msg) {
                toast("获取订单详情失败");
                LogUtils.e("获取订单详情出错: " + code + ", " + msg);
            }
        });
    }

    @Override
    public void processOrder() {
        if (mOrder == null) {
            view.toast("订单数据有误");
            return;
        }
        OrderStatus status = OrderStatus.forStatus(mOrder.getOrderStatus());
        if (status == OrderStatus.ORDER_STATUS_READY) { // 已派单，开始订单
            updateOrderStatus(5);
        } else if (status == OrderStatus.ORDER_STATUS_GET_ON) { // 行程中，到达目的地
            updateOrderStatus(6);
        }
    }

    private void updateOrderStatus(int orderStatus) {
        repo.changeOrderStatus(mOrderId, orderStatus, new ApiSubscriber<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                getOrderDetails();
                BusUtils.post(new AppointmentChangedEvent());
            }

            @Override
            public void onError(int code, String msg) {
                toast("更新订单状态失败");
                LogUtils.e("更新订单状态出错：" + code + ", " + msg);
            }
        });
    }

    @Override
    public void cancelOrder(String reason) {
        repo.cancelOrder(mOrderId, reason, null, new ApiSubscriber<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                if (aBoolean != null && aBoolean) {
                    BusUtils.post(new AppointmentChangedEvent(true));
                    toast("取消成功");
                    view.closePage();
                } else {
                    toast("取消失败");
                }
            }

            @Override
            public void onError(int code, String msg) {
                toast("取消失败");
                LogUtils.e("订单取消失败：" + code + ", " + msg);
            }
        });
    }

    @Override
    public void onViewDestroy() {
        super.onViewDestroy();

    }


}
