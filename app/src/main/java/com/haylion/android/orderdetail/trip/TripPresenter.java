package com.haylion.android.orderdetail.trip;

import android.text.TextUtils;

import com.amap.api.maps.model.LatLng;
import com.haylion.android.Constants;
import com.haylion.android.R;
import com.haylion.android.data.model.AddressInfo;
import com.haylion.android.data.model.CarpoolOrderExt;
import com.haylion.android.data.model.ChildInfo;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.model.OrderDetail;
import com.haylion.android.data.model.ServiceNumber;
import com.haylion.android.data.repo.OrderRepository;
import com.haylion.android.data.repo.PrefserHelper;
import com.haylion.android.data.util.OrderConvert;
import com.haylion.android.mvp.base.BasePresenter;
import com.haylion.android.mvp.rx.ApiSubscriber;
import com.haylion.android.mvp.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class TripPresenter extends BasePresenter<TripContract.View, OrderRepository> implements TripContract.Presenter {

    private final String TAG = getClass().getSimpleName();

    TripPresenter(TripContract.View view) {
        super(view, new OrderRepository());
    }

    /**
     * 获取订单详情
     *
     * @param orderId
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
                view.getOrderDetailFail(msg);
                toastError(R.string.toast_get_order_info_fail, msg);
            }
        });
    }

    /**
     * 确认乘客已线下支付
     *
     * @param orderId
     * @param state
     */
    @Override
    public void payConfirm(int orderId, int state) {
        repo.paymentConfirm(orderId, state, new ApiSubscriber<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                view.payConfirmSuccess();
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.e("确认支付消息上报失败:" + code + "," + msg);
                toastError("确认失败", msg);
            }
        });
    }

    /**
     * 获取客服电话
     */
    @Override
    public void getServiceTelNumber(boolean isNeedShowDialog) {
        repo.getSericeTelNumber(new ApiSubscriber<ServiceNumber>() {
            @Override
            public void onSuccess(ServiceNumber serviceNumber) {
                PrefserHelper.setCache(PrefserHelper.KEY_SERVICE_NUMBER, serviceNumber.getPhone());
                Constants.SERVICE_PHONE = serviceNumber.getPhone();
                view.getServiceTelNumberSuccess(serviceNumber.getPhone(), isNeedShowDialog);
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.e("客服电话获取失败:" + code + "," + msg);
                toastError(R.string.toast_get_customer_service_phone_error, msg);
               /* if(code == 501501){
                    toast(R.string.toast_get_customer_service_phone_error);
                }else{
                    toast(msg);
                }*/
                // LoggerUtils.e(TAG, "getServiceTelNumber:" + code + "," + msg);
            }
        });
    }

    @Override
    public void getCargoOrderSendDeadTime(int orderId) {
        repo.getWorkOrderDetail(orderId, new ApiSubscriber<OrderDetail>() {
            @Override
            public void onSuccess(OrderDetail orderDetail) {
                Order order;
                order = OrderConvert.orderDetailToOrder(orderDetail);
                view.updateCargoOrderRestTime(order.getEstimateArriveTime());
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.e("货物送达时间获取失败:" + code + "," + msg);
                toastError("", msg);
            }
        });
    }

    @Override
    public void exceptionFinishOrder(int orderId, String reason, double meterPrice) {
        repo.exceptionFinishOrder(orderId, (short) 1/*异常结束*/, reason, meterPrice, new ApiSubscriber<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                toast(R.string.toast_exception_finish_order_success);
                view.exceptionFinishOrderSuccess();
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.e("异常结束订单失败：" + code + "," + msg);
                toastError(R.string.toast_exception_finish_order_fail, msg);
            }
        });
    }

    @Override
    public void getCarpoolOrderDetails(String carpoolCode) {
        repo.getCarpoolDetails(carpoolCode, new ApiSubscriber<List<OrderDetail>>() {
            @Override
            public void onSuccess(List<OrderDetail> orderDetails) {
                if (orderDetails == null || orderDetails.isEmpty()) {
                    toast("拼车单信息有误");
                    view.getOrderDetailFail(null);
                } else {
                    handleCarpoolOrders(orderDetails);
                }
            }

            @Override
            public void onError(int code, String msg) {
                toast("获取拼车单信息失败");
                LogUtils.d("查询拼车单信息出错：" + code + ", " + msg);
                view.getOrderDetailFail(null);
            }
        });
    }

    private List<String> parentPhones;

    private void handleCarpoolOrders(List<OrderDetail> orderDetails) {
        OrderDetail orderDetail;
        List<ChildInfo> childList = new ArrayList<>();
        StringBuilder parentMessage = new StringBuilder();
        StringBuilder orderCodes = new StringBuilder();
        parentPhones = new ArrayList<>();
        Order order = new Order();
        for (int i = 0; i < orderDetails.size(); i++) {
            orderDetail = orderDetails.get(i);
            if (i == 0) {
                order.setOrderType(orderDetail.getOrderType());
                order.setOrderTime(orderDetail.getAppointmentTime());
                order.setOrderStatus(orderDetail.getOrderStatus());
                order.setOrderSubStatus(orderDetail.getOrderSubStatus());

                AddressInfo start = new AddressInfo();
                start.setLatLng(new LatLng(orderDetail.getPassengerOnLat(), orderDetail.getPassengerOnLon()));
                start.setName(orderDetail.getOnLocation());
                start.setAddressDetail(orderDetail.getOnLocationDescription());
                order.setStartAddr(start);
            } else if (i == orderDetails.size() - 1) {
                AddressInfo end = new AddressInfo();
                end.setLatLng(new LatLng(orderDetail.getPassengerOffLat(), orderDetail.getPassengerOffLon()));
                end.setName(orderDetail.getOffLocation());
                end.setAddressDetail(orderDetail.getOffLocationDescription());
                order.setEndAddr(end);
            }
            if (TextUtils.isEmpty(orderDetail.getParentMessage())) {
                parentMessage.append("无");
            } else {
                parentMessage.append(orderDetail.getParentMessage());
            }
            orderCodes.append(orderDetail.getOrderCode());
            if (i != orderDetails.size() - 1) {
                parentMessage.append("\n");
                orderCodes.append("\n");
            }
            childList.addAll(orderDetail.getChildList());
            parentPhones.add(orderDetail.getMobile());
        }
        order.setOrderCode(orderCodes.toString());
        order.setParentMessage(parentMessage.toString());
        order.setChildList(childList);
        view.getOrderDetailSuccess(order);
    }

    @Override
    public List<String> getParentPhones() {
        return parentPhones;
    }

}
