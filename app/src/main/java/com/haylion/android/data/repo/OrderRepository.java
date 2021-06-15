package com.haylion.android.data.repo;

import android.util.Log;

import com.haylion.android.common.aibus_location.data.GpsData;
import com.haylion.android.data.bean.ChangeOrderStatusBean;
import com.haylion.android.data.bean.ClaimBean;
import com.haylion.android.data.bean.ClaimResult;
import com.haylion.android.data.bean.OrderDetailBean;
import com.haylion.android.data.bean.OrderIdBean;
import com.haylion.android.data.bean.PhoneBean;
import com.haylion.android.data.bean.ShunfengWaitBean;
import com.haylion.android.data.model.CancelReason;
import com.haylion.android.data.api.AccountApi;
import com.haylion.android.data.api.OrderApi;
import com.haylion.android.data.api.WalletApi;
import com.haylion.android.data.dto.OrderDto;
import com.haylion.android.data.model.AddressForSuggestLine;
import com.haylion.android.data.model.AddressInfo;
import com.haylion.android.data.model.AmapTrack;
import com.haylion.android.data.model.BackHomeAddress;
import com.haylion.android.data.model.Driver;
import com.haylion.android.data.model.HistoryOrderBean;
import com.haylion.android.data.model.LatestVersionBean;
import com.haylion.android.data.model.ListenOrderSetting;
import com.haylion.android.data.model.ListenStatus;
import com.haylion.android.data.model.MessageDetail;
import com.haylion.android.data.model.MessageDetailSimple;
import com.haylion.android.data.model.NewOrder;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.model.OrderAbstract;
import com.haylion.android.data.model.OrderAbstractByMonth;
import com.haylion.android.data.model.OrderDetail;
import com.haylion.android.data.model.OrderDetailExt;
import com.haylion.android.data.model.OrderForCargoAndPassenger;
import com.haylion.android.data.model.OrderForMainActivity;
import com.haylion.android.data.model.OrderHistory;
import com.haylion.android.data.model.PayInfo;
import com.haylion.android.data.model.PaymentResult;
import com.haylion.android.data.model.ServiceFee;
import com.haylion.android.data.model.ServiceNumber;
import com.haylion.android.data.model.ShunfengBean;
import com.haylion.android.data.model.SwitchVehicleJudgeBean;
import com.haylion.android.mvp.base.BaseRepository;
import com.haylion.android.mvp.base.ListData;
import com.haylion.android.mvp.base.ListRequest;
import com.haylion.android.mvp.net.RetrofitHelper;
import com.haylion.android.mvp.rx.ApiSubscriber;
import com.haylion.android.mvp.rx.ApiTransformer;

import java.util.List;
import java.util.Map;

/**
 * @class OrderRepository
 * @description 订单相关的网络请求
 * @date: 2019/12/17 10:10
 * @author: tandongdong
 */
public class OrderRepository extends BaseRepository {

    public static final OrderRepository INSTANCE = new OrderRepository();

    //成就（今日/历史成就）
    public void getAchievementList(int page, int index, ApiSubscriber<ListData<OrderAbstract>> callback) {
        addDisposable(RetrofitHelper.getApi(OrderApi.class)
                .getAchievementAList(new OrderDto.AchievementListRequest(page, 20, index).getQueryMap())
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }

    //成就（按月请求数据）
    public void getAchievementListByMonth(int year, int month, ApiSubscriber<OrderAbstractByMonth> callback) {
        addDisposable(RetrofitHelper.getApi(OrderApi.class)
                .getAchievementAListByMonth(year, month)
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }

    /**
     * showCompletionOnly	是	Boolean	仅展示已完成的订单（包括已完成和待支付的）
     * index	是	Integer	显示数目（0表示当天，1表示当前30天内，2表示60天，3表示90天，以此类推）
     *
     * @param onlyShowCompleted
     * @param index
     * @param callback
     */
    //订单列表（历史/当天）
    public void getOrderList(boolean onlyShowCompleted, boolean showPassenger, int index, int page, ApiSubscriber<ListData<OrderHistory>> callback) {
        addDisposable(RetrofitHelper.getApi(OrderApi.class)
                .getOrderList(new OrderDto.OrderListRequest(onlyShowCompleted, showPassenger, index, page, 20).getQueryMap())
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }

    //首页订单列表
    public void getToadyOrder(ApiSubscriber<List<OrderForMainActivity>> callback) {
        addDisposable(RetrofitHelper.getApi(OrderApi.class)
                .getCurrentOrder()
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }

    //历史订单
    public void getHistoryOrder(boolean onlyShowCompleted, int index, int page,ApiSubscriber<HistoryOrderBean> callback) {
        addDisposable(RetrofitHelper.getApi(OrderApi.class)
                .getHistoryOrderList(new OrderDto.OrderListRequest(onlyShowCompleted, index, page, 20).getQueryMap())
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }

    //包含货单
    public void getToadyOrderAll(ApiSubscriber<OrderForCargoAndPassenger> callback) {
        addDisposable(RetrofitHelper.getApi(OrderApi.class)
                .getCurrentOrderAll()
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }

    //建议线路
    public void getSuggestLine(ApiSubscriber<List<AddressForSuggestLine>> callback) {
        addDisposable(RetrofitHelper.getApi(OrderApi.class)
                .getRecommendRoute()
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }

    //开始听单/停止听单 true 开始，false 停止
    public void startListenOrder(Boolean start, int vehicleId, ApiSubscriber<Void> callback) {
        addDisposable(RetrofitHelper.getApi(OrderApi.class)
                .changeListenOrderStatus(start, vehicleId)
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }

    //获取听单状态
    public void getListenOrderStatus(ApiSubscriber<ListenStatus> callback) {
        addDisposable(RetrofitHelper.getApi(OrderApi.class)
                .getListenOrderStatus()
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }

    //设置听单设置
    public void setOrderSetting(ListenOrderSetting setting, ApiSubscriber<Void> callback) {
        addDisposable(RetrofitHelper.getApi(OrderApi.class)
                .setOrderSetting(setting)
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }

    //获取听单设置
    public void getOrderSetting(ApiSubscriber<ListenOrderSetting> callback) {
        addDisposable(RetrofitHelper.getApi(OrderApi.class)
                .getOrderSetting()
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }

    //改变订单状态 到达乘车点/到达目的/接到乘客
    public void changeOrderStatus(int orderId, int orderStatus, ApiSubscriber<Boolean> callback) {
        addDisposable(RetrofitHelper.getApi(OrderApi.class)
                .updateOrderStatus(new OrderDto.OrderStatusRequest(orderId, orderStatus))
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }

    //改变订单(顺丰)状态
    public void changeOrderStatus(ChangeOrderStatusBean paramBean, ApiSubscriber<Boolean> callback) {
        addDisposable(RetrofitHelper.getApi(OrderApi.class)
                .updateOrderStatus(paramBean)
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }

    //取消订单
    public void cancelOrder(int orderId, String cancelReason, String picUrl, ApiSubscriber<Boolean> callback) {
        addDisposable(RetrofitHelper.getApi(OrderApi.class)
                .cancelOrder(new OrderDto.CancelOrderRequest(orderId, cancelReason, picUrl))
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }

    //异常结束订单
    public void exceptionFinishOrder(int orderId, short exceptionCancel, String cancelReason, double meterPrice, ApiSubscriber<Boolean> callback) {
        addDisposable(RetrofitHelper.getApi(OrderApi.class)
                .cancelOrder(new OrderDto.CancelOrderRequest(orderId, exceptionCancel, cancelReason, meterPrice))
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }

    //获取取消原因列表
    public void getCancelReason(ApiSubscriber<List<CancelReason>> callback) {
        addDisposable(RetrofitHelper.getApi(OrderApi.class)
                .getCancelOrderReason()
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }

    //抢单
    public void grabOrder(int orderId, ApiSubscriber<Boolean> callback) {
        addDisposable(RetrofitHelper.getApi(OrderApi.class)
                .grabOrder(new OrderDto.OrderIdRequest(orderId))
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }

    //获取订单详情
    public void getWorkOrderDetail(int orderId, ApiSubscriber<OrderDetail> callback) {
        Log.d("aaa","orderId = " + orderId);
        addDisposable(RetrofitHelper.getApi(OrderApi.class)
                .getWorkOrderDetail(orderId)
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }

    //获取顺丰订单详情
    public void getShunfengOrderDetail(int orderId, ApiSubscriber<OrderDetailBean> callback) {
        Log.d("aaa","orderId = " + orderId);
        addDisposable(RetrofitHelper.getApi(OrderApi.class)
                .getOrderDetail(new OrderIdBean(orderId))
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }

    //获取客服电话
    public void getServicePhoneNum(ApiSubscriber<PhoneBean> callback) {
        addDisposable(RetrofitHelper.getApi(OrderApi.class)
                .getServicePhoneNum()
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }

    //获取下一个订单
    public void getNextNewOrder(ApiSubscriber<NewOrder> callback) {
        addDisposable(RetrofitHelper.getApi(OrderApi.class)
                .getNextOrder()
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }

    //设置收车地址
    public void setBackHomeAddress(AddressInfo addr, ApiSubscriber<Void> callback) {
        addDisposable(RetrofitHelper.getApi(OrderApi.class)
                .addBackHomeAddress(new OrderDto.BackHomeAddressRequest(addr))
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }

    //获取收车地址
    public void getBackHomeAddress(ApiSubscriber<BackHomeAddress> callback) {
        addDisposable(RetrofitHelper.getApi(OrderApi.class)
                .getBackHomeAddress()
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }

    //获取服务费用
    public void getServiceCost(double baseCost, int orderId, ApiSubscriber<ServiceFee> callback) {
        addDisposable(RetrofitHelper.getApi(OrderApi.class)
                .getServiceCost(baseCost, orderId)
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }

    //支付请求
    public void paymentRequest(PayInfo payInfo, ApiSubscriber<PaymentResult> callback) {
        addDisposable(RetrofitHelper.getApi(OrderApi.class)
                .paymentRequest(payInfo)
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }

    //确认支付完成
    public void paymentConfirm(int orderId, int state, ApiSubscriber<Boolean> callback) {
        addDisposable(RetrofitHelper.getApi(OrderApi.class)
                .paymentConfirm(new OrderDto.PayConfirmRequest(orderId, state))
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }

    //车辆GPS定位上传
    public void gpsDriverUpdata(GpsData gpsData, ApiSubscriber<Boolean> callback) {
        addDisposable(RetrofitHelper.getApi(OrderApi.class)
                .gpsDriverUpdata(new OrderDto.GpsDataRequest(gpsData))
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }

    public void getDriverInfo(ApiSubscriber<Driver> callback) {
        addDisposable(RetrofitHelper.getApi(AccountApi.class)
                .getPersonalInfo()
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }

    //获取司机端高德服务的参数
    public void getAmapGpsTrackArgs(ApiSubscriber<AmapTrack> callback) {
        addDisposable(RetrofitHelper.getApi(AccountApi.class)
                .getAmapGpsTrackArgs()
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }

    //获取下一个订单
    public void getSericeTelNumber(ApiSubscriber<ServiceNumber> callback) {
        addDisposable(RetrofitHelper.getApi(OrderApi.class)
                .getServicePhone()
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }

    //设置 预计达到上车点时间
    public void setEstimatePickUpTime(int orderId, long timestamp, ApiSubscriber<Boolean> callback) {
        addDisposable(RetrofitHelper.getApi(OrderApi.class)
                .setEstimatePickUpTime(new OrderDto.ArrivalTimeRequest(orderId, timestamp))
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }

    //获取多日订单信息
    public void getParentOrderInfo(String parentOrderCode, ApiSubscriber<OrderDetailExt> callback) {
        addDisposable(RetrofitHelper.getApi(OrderApi.class)
                .getParentOrderInfo(parentOrderCode)
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }

    //获取系统消息列表
    public void getMessageList(int page, ApiSubscriber<ListData<MessageDetail>> callback) {
        addDisposable(RetrofitHelper.getApi(WalletApi.class)
                .getMessageList(new ListRequest(page, 20).getQueryMap())
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }

    //更新系统消息列表
    public void updateMessageList(List<MessageDetailSimple> list, ApiSubscriber<Void> callback) {
        addDisposable(RetrofitHelper.getApi(WalletApi.class)
                .updateMessageList(list)
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }

    //所有消息已读
    public void allMessageReaded(ApiSubscriber<Void> callback) {
        addDisposable(RetrofitHelper.getApi(WalletApi.class)
                .allMessageReaded()
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }

    //是否允许更换听单车辆
    public void isSwitchVehicleAllowed(int currentOnlineVehicleId, ApiSubscriber<SwitchVehicleJudgeBean> callback) {
        addDisposable(RetrofitHelper.getApi(OrderApi.class)
                .isSwitchVehicleAllowed(currentOnlineVehicleId)
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }

    /**
     * 最新版apk信息
     * 两个参数都传1，代码司机端、android平台
     *
     * @param callback
     */
    public void checkUpdates(ApiSubscriber<LatestVersionBean> callback) {
        addDisposable(RetrofitHelper.getApi(AccountApi.class)
                .checkUpdates(1, 1)
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }

    /**
     * 抢单池 - 预约订单
     *
     * @param callback 回调
     */
    public void appointmentCenter(ApiSubscriber<List<OrderForMainActivity>> callback) {
        addDisposable(RetrofitHelper.getApi(OrderApi.class)
                .appointmentCenter()
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }

    /**
     * 抢预约订单
     */
    public void grabAppointment(String orderCode, boolean parentOrder, ApiSubscriber<Boolean> callback) {
        addDisposable(RetrofitHelper.getApi(OrderApi.class)
                .grabAppointment(new OrderDto.GrabAppointmentRequest(orderCode, parentOrder))
                .compose(new ApiTransformer<>())
                .subscribeWith(callback));
    }

    /**
     * 抢预约订单
     */
    public void grabShunfengOrder(ClaimBean bean, ApiSubscriber<ClaimResult> callback) {
        addDisposable(RetrofitHelper.getApi(OrderApi.class)
                .grabShunfeng(bean)
                .compose(new ApiTransformer<>())
                .subscribeWith(callback));
    }

    /**
     * 抢单池 - 送小孩单
     *
     * @param callback 回调
     */
    public void childrenOrderCenter(ApiSubscriber<List<OrderForMainActivity>> callback) {
        addDisposable(RetrofitHelper.getApi(OrderApi.class)
                .childrenOrderCenter()
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }

    /**
     *
     * @param callback 回调
     */
    public void getShunfengWaitList(ApiSubscriber<List<ShunfengWaitBean>> callback) {
        addDisposable(RetrofitHelper.getApi(OrderApi.class)
                .getShunfengWaitList()
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }

    /**
     * 抢送小孩订单
     */
    public void grabChildrenOrder(Order order, ApiSubscriber<Boolean> callback) {
        addDisposable(RetrofitHelper.getApi(OrderApi.class)
                .grabChildrenOrder(new OrderDto.GrabAppointmentRequest(order))
                .compose(new ApiTransformer<>())
                .subscribeWith(callback));
    }

    /**
     * 抢单池 - 无障碍订单
     *
     * @param callback 回调
     */
    public void accessibilityOrderCenter(ApiSubscriber<List<OrderForMainActivity>> callback) {
        addDisposable(RetrofitHelper.getApi(OrderApi.class)
                .accessibilityOrderCenter()
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }

    /**
     * 抢单池 - 顺丰订单
     *
     * @param callback 回调
     */
    public void shunfengOrder(ApiSubscriber<List<ShunfengBean>> callback) {
        addDisposable(RetrofitHelper.getApi(OrderApi.class)
                .getShunfengOrders()
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }

    /**
     * 抢无障碍订单
     */
    public void grabAccessibilityOrder(String orderCode, boolean parentOrder, ApiSubscriber<Boolean> callback) {
        addDisposable(RetrofitHelper.getApi(OrderApi.class)
                .grabAccessibilityOrder(new OrderDto.GrabAppointmentRequest(orderCode, parentOrder))
                .compose(new ApiTransformer<>())
                .subscribeWith(callback));
    }

    /**
     * 获取拼车单详情
     *
     * @param carpoolCode 拼车码
     * @param callback    回调
     */
    public void getCarpoolDetails(String carpoolCode, ApiSubscriber<List<OrderDetail>> callback) {
        addDisposable(RetrofitHelper.getApi(OrderApi.class)
                .getCarpoolOrderDetails(carpoolCode)
                .compose(new ApiTransformer<>())
                .subscribeWith(callback));
    }

    /**
     * 获取送你上学拼车单服务费
     *
     * @param carpoolCode 拼车码
     * @param callback    回调
     */
    public void getCarpoolServiceFee(String carpoolCode, ApiSubscriber<ServiceFee> callback) {
        addDisposable(RetrofitHelper.getApi(OrderApi.class)
                .getCarpoolServiceFee(carpoolCode)
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }

    /**
     * 送你上学拼车单支付请求
     */
    public void paymentRequestCarpool(PayInfo payInfo, ApiSubscriber<List<PaymentResult>> callback) {
        addDisposable(RetrofitHelper.getApi(OrderApi.class)
                .paymentRequestCarpool(payInfo)
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }


}
