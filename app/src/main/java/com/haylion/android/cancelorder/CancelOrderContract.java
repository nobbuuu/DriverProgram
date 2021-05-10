package com.haylion.android.cancelorder;

import com.haylion.android.data.model.CancelReason;
import com.haylion.android.data.model.Order;
import com.haylion.android.mvp.base.AbstractPresenter;
import com.haylion.android.mvp.base.AbstractView;

import java.util.List;

public class CancelOrderContract {

    interface View extends AbstractView {
        void cancelOrderSuccess();
        void showCancelReason(List<CancelReason> list);

        /**
         * 订单详情
         * */
        void getOrderDetailSuccess(Order orderInfo);
        void getOrderDetailFail(String msg);
    }

    interface Presenter extends AbstractPresenter {
        void cancelOrder(int orderId, String reason,String picUrl);
        void getCancelReasonList();

        /**
         * 获取订单详情
         * */
        void getOrderDetail(int orderId);

    }
}
