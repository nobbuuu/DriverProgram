package com.haylion.android.orderdetail.multiday;

import android.text.TextUtils;
import android.util.Log;

import com.amap.api.maps.model.LatLng;
import com.haylion.android.Constants;
import com.haylion.android.R;
import com.haylion.android.data.model.AddressInfo;
import com.haylion.android.data.model.CarpoolOrderExt;
import com.haylion.android.data.model.ChildInfo;
import com.haylion.android.data.model.OrderDetail;
import com.haylion.android.data.model.OrderDetailExt;
import com.haylion.android.data.model.OrderExt;
import com.haylion.android.data.model.OrderForMainActivity;
import com.haylion.android.data.model.ServiceNumber;
import com.haylion.android.data.repo.OrderRepository;
import com.haylion.android.data.repo.PrefserHelper;

import com.haylion.android.data.util.OrderConvert;
import com.haylion.android.mvp.base.BasePresenter;
import com.haylion.android.mvp.rx.ApiSubscriber;
import com.haylion.android.mvp.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class MultiDayDetailPresenter extends BasePresenter<MultiDayDetailContract.View, OrderRepository> implements MultiDayDetailContract.Presenter {

    private final String TAG = getClass().getSimpleName();

    MultiDayDetailPresenter(MultiDayDetailContract.View view) {
        super(view, new OrderRepository());
    }

    /**
     * 获取多日订单
     *
     * @param orderNum
     */
    @Override
    public void getMultiDayOrderDetail(String orderNum) {
        Log.d(TAG, "getMultiDayOrderDetail, id:" + orderNum);
        repo.getParentOrderInfo(orderNum, new ApiSubscriber<OrderDetailExt>() {
            @Override
            public void onSuccess(OrderDetailExt orderDetailExt) {
                OrderExt order;
                order = OrderConvert.orderDetailExtToOrderExt(orderDetailExt);
                view.getMultiDayOrderDetailSuccess(order);
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.e(TAG, "getParentOrderInfo FAIL, code = " + code + " ,msg = " + msg);
                view.getOrderDetailFail(msg);
                toastError("获取多日订单失败", msg);
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
                LogUtils.e(TAG, "getServiceTelNumber:" + code + "," + msg);
                toastError(R.string.toast_get_customer_service_phone_error, msg);
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
                Log.d(TAG, "确认支付消息上报成功");
                view.payConfirmSuccess();
            }

            @Override
            public void onError(int code, String msg) {
                Log.e(TAG, "确认支付消息上报失败" + msg);
                toastError("确认支付失败", msg);
            }
        });
    }

    @Override
    public void getCarpoolOrderDetails(String carpoolCode) {
        repo.getCarpoolDetails(carpoolCode, new ApiSubscriber<List<OrderDetail>>() {
            @Override
            public void onSuccess(List<OrderDetail> orderForMainActivities) {
                CarpoolOrderExt carpoolOrderExt = convertCarpoolOrder2CarpoolOrderExt(orderForMainActivities);
                if (carpoolOrderExt == null) {
                    view.getOrderDetailFail(null);
                } else {
                    view.showCarpoolOrderDetails(carpoolOrderExt);
                }
            }

            @Override
            public void onError(int code, String msg) {
                view.getOrderDetailFail(null);
                LogUtils.d("查询拼车单信息出错：" + code + ", " + msg);
            }
        });
    }

    private CarpoolOrderExt convertCarpoolOrder2CarpoolOrderExt(List<OrderDetail> carpoolOrders) {
        if (carpoolOrders == null || carpoolOrders.isEmpty()) {
            return null;
        }
        CarpoolOrderExt orderExt = new CarpoolOrderExt();
        OrderDetail orderDetail;
        List<String> addressList = new ArrayList<>();
        List<ChildInfo> childList = new ArrayList<>();
        List<String> parentPhoneList = new ArrayList<>();
        List<String> parentMessageList = new ArrayList<>();
        List<String> orderCodes = new ArrayList<>();
        for (int i = 0; i < carpoolOrders.size(); i++) {
            orderDetail = carpoolOrders.get(i);
            orderCodes.add(orderDetail.getOrderCode());
            if (i == 0) {
                orderExt.setOrderType(orderDetail.getOrderType());
                orderExt.setOrderTime(orderDetail.getAppointmentTime());
                orderExt.setOrderCode(orderDetail.getOrderCode());
                orderExt.setOrderStatus(orderDetail.getOrderStatus());
                orderExt.setOrderSubStatus(orderDetail.getOrderSubStatus());

                AddressInfo start = new AddressInfo();
                start.setLatLng(new LatLng(orderDetail.getPassengerOnLat(), orderDetail.getPassengerOnLon()));
                start.setName(orderDetail.getOnLocation());
                start.setAddressDetail(orderDetail.getOnLocationDescription());
                orderExt.setStartAddr(start);

            } else if (i == carpoolOrders.size() - 1) {
                AddressInfo end = new AddressInfo();
                end.setLatLng(new LatLng(orderDetail.getPassengerOffLat(), orderDetail.getPassengerOffLon()));
                end.setName(orderDetail.getOffLocation());
                end.setAddressDetail(orderDetail.getOffLocationDescription());
                orderExt.setEndAddr(end);
            }

            childList.addAll(orderDetail.getChildList());
            parentPhoneList.add(orderDetail.getMobile());
            if (TextUtils.isEmpty(orderDetail.getParentMessage())) {
                parentMessageList.add("无");
            } else {
                parentMessageList.add(orderDetail.getParentMessage());
            }
            addressList.add(i, orderDetail.getOnLocation());
            addressList.add(orderDetail.getOffLocation());
        }
        orderExt.setOrderCodes(orderCodes);
        orderExt.setAddressList(addressList);
        orderExt.setChildList(childList);
        orderExt.setParentPhoneList(parentPhoneList);
        orderExt.setParentMessageList(parentMessageList);
        return orderExt;
    }

}
