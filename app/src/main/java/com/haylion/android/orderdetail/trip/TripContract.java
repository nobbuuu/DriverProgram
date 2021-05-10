package com.haylion.android.orderdetail.trip;

import com.haylion.android.data.model.CarpoolOrderExt;
import com.haylion.android.data.model.Order;
import com.haylion.android.mvp.base.AbstractPresenter;
import com.haylion.android.mvp.base.AbstractView;

import java.util.List;

public class TripContract {

    interface View extends AbstractView {
        //订单详情
        void getOrderDetailSuccess(Order orderInfo);
        void getOrderDetailFail(String msg);

        //确认支付成功
        void payConfirmSuccess();
        //获取客服电话成功
        void getServiceTelNumberSuccess(String phoneNum,boolean isNeedShowDialog);
        //获取货单的限定送达时间 成功
        void updateCargoOrderRestTime(String deadTime);

        void exceptionFinishOrderSuccess();
    }

    interface Presenter extends AbstractPresenter {
        void getOrderDetail(int orderId); //获取订单详情
        void payConfirm(int orderId, int state);  //确认乘客已支付
        void getServiceTelNumber(boolean isNeedShowDialog);      //获取客服电话
        void getCargoOrderSendDeadTime(int orderId); //货单的限定送达时间
        void exceptionFinishOrder(int orderId, String reason, double meterPrice);

        void getCarpoolOrderDetails(String carpoolCode);

        List<String> getParentPhones();
    }
}
