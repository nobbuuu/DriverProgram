package com.haylion.android.orderdetail.map;

import com.haylion.android.data.model.Order;
import com.haylion.android.mvp.base.AbstractPresenter;
import com.haylion.android.mvp.base.AbstractView;

public class ShowInMapContract {

    interface View extends AbstractView {
        /*void getOrderDetailSuccess(Order orderInfo);
        void getOrderDetailFail(String msg);

        void payConfirmSuccess();    //确认支付成功

        void showDialDialog(String phoneNum);  //显示客服电话Dialog*/
    }

    interface Presenter extends AbstractPresenter {
      /*  void getOrderDetail(int orderId); //获取订单详情

        void payConfirm(int orderId, int state);  //确认乘客已支付

        void getServiceTelNumber();   //获取客服电话*/
    }
}
