package com.haylion.android.orderdetail;

import com.haylion.android.data.model.AddressInfo;
import com.haylion.android.data.model.MessageDetail;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.model.OrderDetail;
import com.haylion.android.mvp.base.AbstractPresenter;
import com.haylion.android.mvp.base.AbstractView;

import java.util.List;

public class OrderDetailContract {

    public interface View extends AbstractView {
        /**
         * 订单详情
         */
        void getOrderDetailSuccess(Order orderInfo);

        void getOrderDetailFail(String msg);

        /**
         * 改变订单状态
         */
        void changeOrderStatusSuccess(int status);

        void changeOrderStatusFail();

        /**
         * 获取货物剩余时间
         */
        void getCargoRestTimeSuccess(String deadTime);

        /**
         * 确认支付成功
         */
        void payConfirmSuccess();

        /**
         * 获取客服电话成功
         *
         * @param phoneNum
         */
        void getServiceTelNumberSuccess(String phoneNum, boolean isNeedShowDialog);
    }

    public interface Presenter extends AbstractPresenter {
        /**
         * 获取订单详情
         */
        void getOrderDetail(int orderId);


        /**
         * 货单的限定送达时间
         *
         * @param orderId 货单id
         */
        void getCargoOrderSendDeadTime(int orderId);

        /**
         * 改变订单状态
         *
         * @param cargoOrderId 货单id
         * @param operator     由那个页面进行的操作
         */
        void changeOrderStatus(int cargoOrderId, int operator); //改变订单状态，（乘客上车，下车等）

        /**
         * 获取货单信息
         *
         * @param orderId
         */
        void getGoodsOrder(int orderId);

        void payConfirm(int orderId, int state);

        /**
         * 获取客服电话
         */
        void getServiceTelNumber(boolean isNeedShowDialog);

        /**
         * 设置预计到达上车点时间
         *
         * @param orderId
         * @param timestamp
         */
        void setEstimatePickUpTime(int orderId, long timestamp);

        /**
         * 获取拼车单详情
         *
         * @param carpoolCode 拼车码
         */
        void getCarpoolOrderDetails(String carpoolCode);

        /**
         * 送你上学拼车专用 - 获取家长电话
         */
        List<String> getParentPhones();

        /**
         * 送你上学拼单专用 - 获取小孩电话
         */
        List<String> getChildPhones();

        /**
         * 获取拼车路线
         */
        List<AddressInfo> getCarpoolPath();

        /**
         * 送你上学拼车单 - 获取上车确认订单
         */
        List<Order> getOnPicOrders();

        /**
         * 送你上学拼车单 -获取下车确认订单
         */
        List<Order> getOffPicOrders();

        /**
         * 送你上学订单 - 小孩的手机尾号
         */
        String getChildPhoneSimple();

        /**
         * 判断订单是否已经开始（需要所有订单都未开始）
         *
         * @return
         */
        boolean isCarpoolOrderStared();

    }

}
