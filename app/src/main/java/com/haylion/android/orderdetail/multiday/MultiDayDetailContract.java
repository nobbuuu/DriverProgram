package com.haylion.android.orderdetail.multiday;

import com.haylion.android.data.model.CarpoolOrderExt;
import com.haylion.android.data.model.CarpoolStatus;
import com.haylion.android.data.model.OrderExt;
import com.haylion.android.mvp.base.AbstractPresenter;
import com.haylion.android.mvp.base.AbstractView;

public class MultiDayDetailContract {

    interface View extends AbstractView {
        void getMultiDayOrderDetailSuccess(OrderExt orderInfo);

        void getOrderDetailFail(String msg);

        void showDialDialog(String phoneNum);

        void payConfirmSuccess();    //确认支付成功

        void showCarpoolOrderDetails(CarpoolOrderExt orderInfo);
    }

    interface Presenter extends AbstractPresenter {
        void getMultiDayOrderDetail(String orderNum); //获取订单详情

        void payConfirm(int orderId, int state);  //确认乘客已支付

        void getServiceTelNumber();

        void getCarpoolOrderDetails(String carpoolCode);
    }

}
