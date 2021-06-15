package com.haylion.android.orderdetail;

import android.text.TextUtils;
import android.util.Log;

import com.amap.api.maps.model.LatLng;
import com.haylion.android.Constants;
import com.haylion.android.R;
import com.haylion.android.data.bean.ChangeOrderStatusBean;
import com.haylion.android.data.bean.OrderDetailBean;
import com.haylion.android.data.model.AddressInfo;
import com.haylion.android.data.model.ChildInfo;
import com.haylion.android.data.model.EventBean;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.model.OrderDetail;
import com.haylion.android.data.model.ServiceNumber;
import com.haylion.android.data.repo.OrderRepository;
import com.haylion.android.data.repo.PrefserHelper;
import com.haylion.android.data.util.OrderConvert;
import com.haylion.android.mvp.base.BasePresenter;
import com.haylion.android.mvp.rx.ApiSubscriber;
import com.haylion.android.mvp.util.LogUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailPresenter extends BasePresenter<OrderDetailContract.View, OrderRepository>
        implements OrderDetailContract.Presenter {

    private static final String TAG = "OrderDetailPresenter";
    /**
     * 记录哪个界面改变了订单状态
     */
    public static final int OPERATAR_BY_DETAIL = 0;    //详情页改变
    public static final int OPERATAR_BY_AMAP_NAVI = 1;  //导航页改变
    public static int operatorId = OPERATAR_BY_DETAIL;


    /**
     * 订单
     */
    private Order mOrder;
    private int orderId;
    private int currentOrderStatus = Order.ORDER_STATUS_WAIT_CAR;  //当前订单状态

    private boolean mCarpoolFlag;

    /**
     * 拼车单列表（包含每个子订单）
     */
    private List<OrderDetail> mCarpoolOrders;
    private String mCarpoolCode;
    /**
     * 拼车单行驶路线
     */
    private List<AddressInfo> mCarpoolPath;
    /**
     * 拼单 - 小孩信息
     */
    private List<ChildInfo> mChildInfos;
    /**
     * 拼单 - 家长电话
     */
    private List<String> mParentPhones;

    public OrderDetailPresenter(OrderDetailContract.View view, boolean carpoolFlag) {
        super(view, new OrderRepository());
        this.mCarpoolFlag = carpoolFlag;
        if (carpoolFlag) {
            LogUtils.d("拼车单");
        } else {
            LogUtils.d("普通单");
        }
    }

    /**
     * 获取订单详情
     *
     * @param orderId 订单id
     */
    @Override
    public void getOrderDetail(int orderId) {
        this.orderId = orderId;
/*
        Order order = OrderConvert.mockOrderListData().get(5);
        view.getOrderDetailSuccess(order);*/

        repo.getWorkOrderDetail(orderId, new ApiSubscriber<OrderDetail>() {
            @Override
            public void onSuccess(OrderDetail orderDetail) {
                LogUtils.d(TAG, "getOrderDetail success");
                Order order;
                order = OrderConvert.orderDetailToOrder(orderDetail);
                mOrder = order;
                view.getOrderDetailSuccess(order);
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.e(TAG, "getOrderDetail FAIL, " + msg);
                view.getOrderDetailFail(msg);
                toastError(R.string.toast_get_order_info_fail, msg);
            }
        });
    }

    @Override
    public void getShunfengOrderDetail(int orderId) {
        this.orderId = orderId;
        repo.getShunfengOrderDetail(orderId, new ApiSubscriber<OrderDetailBean>() {
            @Override
            public void onSuccess(OrderDetailBean orderDetail) {
                LogUtils.d(TAG, "getOrderDetail success");
                Order order;
                order = OrderConvert.orderShunfengDetailToOrder(orderDetail);
                mOrder = order;
                view.getOrderDetailSuccess(order);
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.e(TAG, "getOrderDetail FAIL, " + msg);
                view.getOrderDetailFail(msg);
                toastError(R.string.toast_get_order_info_fail, msg);
            }
        });
    }

    /**
     * 获取货单剩余时间
     *
     * @param orderId 货单id ，如果是货拼客，取cargoOrderId
     */
    @Override
    public void getCargoOrderSendDeadTime(int orderId) {
        repo.getWorkOrderDetail(orderId, new ApiSubscriber<OrderDetail>() {
            @Override
            public void onSuccess(OrderDetail orderDetail) {
                LogUtils.d(TAG, "getCargoOrderSendDeadTime success");
                /*Order order;
                order = OrderConvert.orderDetailToOrder(orderDetail);
                LogUtils.d(TAG, "order: " + order.toString());*/
                view.getCargoRestTimeSuccess(orderDetail.getEstimateArriveTime());
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.e(TAG, "getCargoOrderSendDeadTime FAIL, " + msg);
            }
        });
    }


    /**
     * 订单状态的操作
     *
     * @param cargoOrderId 货单id
     * @param operatorId2  操作者，记录是导航页面操作 还是 详情页面操作
     */
    @Override
    public void changeOrderStatus(int cargoOrderId, int operatorId2) {
        LogUtils.d(TAG, "changeOrderStatus:" + mOrder.getOrderStatus() + ",mOrder.getOrderType():"
                + mOrder.getOrderType() + "cargoOrderId:" + cargoOrderId);
        int action;
        if (mOrder.getOrderType() != -1) {
            currentOrderStatus = mOrder.getOrderStatus();
            if (currentOrderStatus == Order.ORDER_STATUS_READY) { // 订单初始状态
                if (mOrder.getOrderType() == Order.ORDER_TYPE_SEND_CHILD) {
                    action = 4; // 送小孩单专用
                } else if (mOrder.getOrderType() == Order.ORDER_TYPE_BOOK ||
                        mOrder.getOrderType() == Order.ORDER_TYPE_ACCESSIBILITY) {
                    action = 1; // 预约单走正常流程
                } else {
                    toast("订单状态有误");
                    view.dismissProgressDialog();
                    return;
                }
            } else if (currentOrderStatus == Order.ORDER_STATUS_WAIT_CAR) { //去接乘客 -> 到达上车地点
                action = 1;
            } else if (currentOrderStatus == Order.ORDER_STATUS_ARRIVED_START_ADDR) { //到达上车地点 -> 乘客已上车
                action = 2; // 接到了乘客
            } else if (currentOrderStatus == Order.ORDER_STATUS_GET_ON) { //乘客已上车 -> 到达目的地
                action = 3; // 到达目的地
            } else if ((currentOrderStatus == Order.ORDER_STATUS_WAIT_PAY ||
                    currentOrderStatus == Order.ORDER_STATUS_CLOSED)) {
                //当前处于待支付 or 已完成状态
                if (mOrder.getOrderType() == Order.ORDER_TYPE_CARGO_PASSENGER) {
                    getGoodsOrder(cargoOrderId);
                }
                return;
            } else {
                LogUtils.d(TAG, "操作失败，当前状态：" + currentOrderStatus);
                view.dismissProgressDialog();
                return;
            }

            repo.changeOrderStatus(orderId, action, new ApiSubscriber<Boolean>() {
                @Override
                public void onSuccess(Boolean ret) {
                    operatorId = operatorId2;
                    view.dismissProgressDialog();
                    if (ret) {
                        //改变订单状态成功,实时订单/货拼客单在某些状态改变时，要做特殊操作
                        handleStatusChangeVoice();
                        if (action == 3) {
                            //到达目的地
                            view.changeOrderStatusSuccess(Order.ORDER_STATUS_GET_OFF);
                            if (mOrder.getOrderType() == Order.ORDER_TYPE_SEND_CHILD) {
                                refreshOrderDetails();
                            }
                        } else {
                            refreshOrderDetails();
                        }
                    }
                }

                @Override
                public void onError(int code, String msg) {
                    LogUtils.e(TAG, "changeOrderStatus, error: " + code + ", " + msg);
                    view.dismissProgressDialog();
                    view.changeOrderStatusFail();
                    if (code == 400021) {
                        //送小孩单
                        toastError("请在用车时间前6小时开始订单", msg);
                    } else {
                        toastError("改变订单状态失败", msg);
                    }
                }
            });

        }else {
            view.showProgressDialog("加载中...");
            Log.d("aaa","orderId = " + orderId);
            repo.changeOrderStatus(new ChangeOrderStatusBean(orderId, operatorId2, null, null), new ApiSubscriber<Boolean>() {
                @Override
                public void onSuccess(Boolean aBoolean) {
                    Log.d("netLog", "changeOrderStatus success");
                    view.dismissProgressDialog();
                    view.changeShunFengOrderStatusSuccess(operatorId2);
                }

                @Override
                public void onError(int code, String msg) {
                    view.dismissProgressDialog();
                    if (code == 402001){
                        toast("非今日订单不可操作");
                    }
                    view.changeOrderStatusFail();
                }
            });
        }
    }

    private void refreshOrderDetails() {
        if (mCarpoolFlag) {
            getCarpoolOrderDetails(mCarpoolCode);
        } else {
            getOrderDetail(orderId);
        }
    }

    /**
     * 处理订单状态改变时的语音播报
     */
    private void handleStatusChangeVoice() {
        //实时订单 or 货拼客单
        if (mOrder.getOrderType() == Order.ORDER_TYPE_REALTIME || mOrder.getOrderType() == Order.ORDER_TYPE_CARGO_PASSENGER) {
            if (currentOrderStatus == Order.ORDER_STATUS_WAIT_CAR) {
                //去接乘客 -> 到达上车地点，改变成功，播报等待乘客上车语音
                if (operatorId == OrderDetailPresenter.OPERATAR_BY_AMAP_NAVI) {
                    EventBus.getDefault().post(new EventBean(Constants.EventCode.VOICE_PROMPT_WAITING_PASSENGER_GET_ON, 1000));
                } else {
                    EventBus.getDefault().post(new EventBean(Constants.EventCode.VOICE_PROMPT_WAITING_PASSENGER_GET_ON, null));
                }
            } else if (currentOrderStatus == Order.ORDER_STATUS_ARRIVED_START_ADDR) {
                //到达上车地点 -> 乘客已上车，播报乘客已上车语音
                if (OrderDetailPresenter.operatorId == OrderDetailPresenter.OPERATAR_BY_AMAP_NAVI) {
                    EventBus.getDefault().post(new EventBean(Constants.EventCode.VOICE_PROMPT_PASSENGER_GET_ON, 1000));
                } else {
                    EventBus.getDefault().post(new EventBean(Constants.EventCode.VOICE_PROMPT_PASSENGER_GET_ON, null));
                }
            } else if (currentOrderStatus == Order.ORDER_STATUS_GET_ON) {
                //乘客已上车 -> 到达目的地，播报已到达目的地语音
                if (OrderDetailPresenter.operatorId == OrderDetailPresenter.OPERATAR_BY_AMAP_NAVI) {
                    EventBus.getDefault().post(new EventBean(Constants.EventCode.VOICE_PROMPT_ARRIVE_AT_DESTINATION, 1000));
                } else {
                    EventBus.getDefault().post(new EventBean(Constants.EventCode.VOICE_PROMPT_ARRIVE_AT_DESTINATION, null));
                }
            }
        } else if (mOrder.getOrderType() == Order.ORDER_TYPE_SEND_CHILD) {
            //小孩单
            if (currentOrderStatus == Order.ORDER_STATUS_WAIT_CAR) {
                //状态转换：到达上车地点 -> 拍上车照片
                if (OrderDetailPresenter.operatorId == OrderDetailPresenter.OPERATAR_BY_AMAP_NAVI) {
                    EventBus.getDefault().post(new EventBean(Constants.EventCode.VOICE_PROMPT_WAITING_DRIVER_TAKE_PHOTO_FOR_GET_ON, 1000));
                } else {
                    EventBus.getDefault().post(new EventBean(Constants.EventCode.VOICE_PROMPT_WAITING_DRIVER_TAKE_PHOTO_FOR_GET_ON, null));
                }
            }
        }
    }

    /**
     * 获取客服电话号码
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
                toast(R.string.toast_get_customer_service_phone_error);
                /*if(code == 501501){
                    toast(R.string.toast_get_customer_service_phone_error);
                }else{
                    toast(msg);
                }*/
            }
        });
    }

    /**
     * 获取商品详情
     *
     * @param cargoOrderId
     */
    @Override
    public void getGoodsOrder(int cargoOrderId) {
        LogUtils.d(TAG, "getGoodsOrder:" + mOrder.getOrderStatus() + ",mOrder.getOrderType():" + mOrder.getOrderType());
        if ((currentOrderStatus == Order.ORDER_STATUS_WAIT_PAY || currentOrderStatus == Order.ORDER_STATUS_CLOSED)) {
            if (mOrder.getOrderType() == Order.ORDER_TYPE_CARGO_PASSENGER) {
                if (cargoOrderId == 0) {
                    LogUtils.d(TAG, "getGoodsOrder, cargoOrderId:" + PrefserHelper.getCache(PrefserHelper.KEY_GOODS_ORDER_ID));
                    if (PrefserHelper.getCache(PrefserHelper.KEY_GOODS_ORDER_ID) != null) {
                        cargoOrderId = Integer.parseInt(PrefserHelper.getCache(PrefserHelper.KEY_GOODS_ORDER_ID));
                        LogUtils.d(TAG, "cargoOrderId:" + cargoOrderId);
                    }
                }
                //货物ID
                if (cargoOrderId != 0) {
                    LogUtils.d(TAG, "cargoOrderId:" + cargoOrderId);
                    getOrderDetail(cargoOrderId);
                }
            }
        } else {
            LogUtils.d(TAG, "操作失败，当前状态：" + currentOrderStatus);
            view.dismissProgressDialog();
        }
    }


    @Override
    public void payConfirm(int orderId, int state) {

        repo.paymentConfirm(orderId, state, new ApiSubscriber<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                LogUtils.d(TAG, "确认支付消息上报成功");
                view.payConfirmSuccess();
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.e(TAG, "确认支付消息上报失败" + msg);
                toast(msg);
                //   toast(R.string.toast_order_confirm_pay_error);
            }
        });

    }

    /**
     * 设置 预计达到上车点时间
     *
     * @param orderId
     * @param timestamp 预计到达上车点时间戳，单位毫秒
     */
    @Override
    public void setEstimatePickUpTime(int orderId, long timestamp) {
        repo.setEstimatePickUpTime(orderId, timestamp, new ApiSubscriber<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                LogUtils.d(TAG, "设置到达上车时间点成功");
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.e(TAG, "code:" + code + ",msg:" + msg);
            }
        });
    }

    @Override
    public void getCarpoolOrderDetails(String carpoolCode) {
        mCarpoolCode = carpoolCode;
        repo.getCarpoolDetails(carpoolCode, new ApiSubscriber<List<OrderDetail>>() {
            @Override
            public void onSuccess(List<OrderDetail> orderDetails) {
                mCarpoolOrders = orderDetails;
                if (orderDetails == null || orderDetails.isEmpty()) {
                    toast("拼车单信息有误");
                    view.getOrderDetailFail(null);
                } else {
                    handleCarpoolOrder(orderDetails);
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

    /**
     * 接送小孩，共分为五步
     * 1、去接小孩（3待出行）
     * 2、确认到达上车点（7等待司机接驾）
     * 3、拍摄上车照片（8等待乘客上车，8-0 等待乘客上车， 8-1等待确认照片 ）
     * 4、拍摄下车照片（4已乘车）
     * 5、到达终点（9，9-0到达终点， 9-1等待确认照片， 9-2确认完成）
     */
    private void handleCarpoolOrder(List<OrderDetail> orderDetails) {
        mCarpoolOrders = orderDetails;

        mChildInfos = new ArrayList<>();
        mParentPhones = new ArrayList<>();
        mCarpoolPath = new ArrayList<>();

        OrderDetail tempOrder;
        OrderDetail onOrder = null; // 上车订单
        OrderDetail onConfirmOrder = null; // 上车照片待确认订单
        OrderDetail offOrder = null; // 下车订单
        OrderDetail offConfirmOrder = null; // 下车照片待确认订单
        for (int i = 0; i < orderDetails.size(); i++) {
            tempOrder = orderDetails.get(i);

            mChildInfos.addAll(tempOrder.getChildList());
            mParentPhones.add(tempOrder.getMobile());

            AddressInfo onAddress = new AddressInfo();
            onAddress.setLatLng(new LatLng(tempOrder.getPassengerOnLat(),
                    tempOrder.getPassengerOnLon()));
            onAddress.setName(tempOrder.getOnLocation());
            onAddress.setAddressDetail(tempOrder.getOnLocationDescription());
            mCarpoolPath.add(i, onAddress);

            AddressInfo offAddress = new AddressInfo();
            offAddress.setLatLng(new LatLng(tempOrder.getPassengerOffLat(),
                    tempOrder.getPassengerOffLon()));
            offAddress.setName(tempOrder.getOffLocation());
            offAddress.setAddressDetail(tempOrder.getOffLocationDescription());
            mCarpoolPath.add(offAddress);

            if (onOrder == null) {
                if (tempOrder.getOrderStatus() == Order.ORDER_STATUS_READY ||
                        tempOrder.getOrderStatus() == Order.ORDER_STATUS_WAIT_CAR ||
                        (tempOrder.getOrderStatus() == Order.ORDER_STATUS_ARRIVED_START_ADDR &&
                                tempOrder.getOrderSubStatus() == Order.ORDER_SUB_STATUS_ARRIVED_START_ADDR)) {
                    onOrder = tempOrder;
                }
            }
            if (onConfirmOrder == null) {
                if (tempOrder.getOrderStatus() == Order.ORDER_STATUS_ARRIVED_START_ADDR &&
                        tempOrder.getOrderSubStatus() == Order.ORDER_SUB_STATUS_ARRIVED_START_ADDR_WAIT_PARENT_CONFIRM) {
                    onConfirmOrder = tempOrder;
                }
            }
            if (offOrder == null) {
                if (tempOrder.getOrderStatus() == Order.ORDER_STATUS_GET_ON) {
                    offOrder = tempOrder;
                }
            }
            if (offConfirmOrder == null) {
                if ((tempOrder.getOrderStatus() == Order.ORDER_STATUS_GET_OFF &&
                        tempOrder.getOrderSubStatus() == Order.ORDER_SUB_STATUS_GET_OFF_WAIT_PARENT_CONFIRM)) {
                    offConfirmOrder = tempOrder;
                }
            }
        }
        OrderDetail currentOrder;
        if (onOrder != null) {
            currentOrder = onOrder;
            LogUtils.d("有未上车订单：" + onOrder.getOrderId());
        } else if (onConfirmOrder != null) {
            currentOrder = onConfirmOrder;
            LogUtils.d("有上车待确认订单：" + onConfirmOrder.getOrderId());
        } else if (offOrder != null) {
            currentOrder = offOrder;
            LogUtils.d("有未下车订单：" + offOrder.getOrderId());
        } else if (offConfirmOrder != null) {
            currentOrder = offConfirmOrder;
            LogUtils.d("有下车待确认订单：" + offConfirmOrder.getOrderId());
        } else {
            currentOrder = orderDetails.get(0);
            LogUtils.d("取第一个订单：" + currentOrder.getOrderId());
        }
        LogUtils.d("当前订单id：" + currentOrder.getOrderId());
        mOrder = OrderConvert.orderDetailToOrder(currentOrder);
        orderId = mOrder.getOrderId();
        view.getOrderDetailSuccess(mOrder);
    }

    @Override
    public List<String> getParentPhones() {
        return mParentPhones;
    }

    @Override
    public List<String> getChildPhones() {
        if (mChildInfos != null && !mChildInfos.isEmpty()) {
            List<String> childPhones = new ArrayList<>();
            for (ChildInfo childInfo : mChildInfos) {
                childPhones.add(childInfo.getMobile());
            }
            return childPhones;
        }
        return null;
    }

    @Override
    public List<AddressInfo> getCarpoolPath() {
        return mCarpoolPath;
    }

    @Override
    public List<Order> getOnPicOrders() {
        List<Order> onPicOrders = new ArrayList<>();
        if (mCarpoolOrders != null) {
            for (OrderDetail carpoolOrder : mCarpoolOrders) {
                if (carpoolOrder.getOrderStatus() == Order.ORDER_STATUS_ARRIVED_START_ADDR &&
                        carpoolOrder.getOrderSubStatus() == Order.ORDER_SUB_STATUS_ARRIVED_START_ADDR_WAIT_PARENT_CONFIRM) {
                    onPicOrders.add(OrderConvert.orderDetailToOrder(carpoolOrder));
                }
            }
        }
        return onPicOrders;
    }

    @Override
    public List<Order> getOffPicOrders() {
        List<Order> offPicOrders = new ArrayList<>();
        if (mCarpoolOrders != null) {
            for (OrderDetail carpoolOrder : mCarpoolOrders) {
                if (carpoolOrder.getOrderStatus() == Order.ORDER_STATUS_GET_OFF &&
                        carpoolOrder.getOrderSubStatus() == Order.ORDER_SUB_STATUS_GET_OFF_WAIT_PARENT_CONFIRM) {
                    offPicOrders.add(OrderConvert.orderDetailToOrder(carpoolOrder));
                }
            }
        }
        return offPicOrders;
    }

    @Override
    public String getChildPhoneSimple() {
        if (mOrder.getOrderType() == Order.ORDER_TYPE_SEND_CHILD) {
            List<ChildInfo> childInfoList = mOrder.getChildList();
            if (childInfoList == null || childInfoList.isEmpty()) {
                return "";
            }
            String childPhone = mOrder.getChildList().get(0).getMobile();
            if (TextUtils.isEmpty(childPhone) || childPhone.length() < 4) {
                return "";
            }
            return childPhone.substring(childPhone.length() - 4);
        }
        return "";
    }

    @Override
    public boolean isCarpoolOrderStared() {
        if (mCarpoolOrders != null) {
            for (OrderDetail orderDetail : mCarpoolOrders) {
                if (orderDetail.getOrderStatus() != Order.ORDER_STATUS_READY) {
                    return true;
                }
            }
        }
        return false;
    }


}
