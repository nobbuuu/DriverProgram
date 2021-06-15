package com.haylion.android.data.api;


import com.haylion.android.data.bean.ChangeOrderStatusBean;
import com.haylion.android.data.bean.ClaimBean;
import com.haylion.android.data.bean.ClaimResult;
import com.haylion.android.data.bean.OrderDetailBean;
import com.haylion.android.data.bean.OrderIdBean;
import com.haylion.android.data.bean.PhoneBean;
import com.haylion.android.data.bean.ShunfengWaitBean;
import com.haylion.android.data.dto.OrderDto;
import com.haylion.android.data.model.HistoryOrderBean;
import com.haylion.android.data.model.ListenOrderSetting;
import com.haylion.android.data.model.OrderDetail;
import com.haylion.android.data.model.OrderForMainActivity;
import com.haylion.android.data.model.PayInfo;
import com.haylion.android.data.model.PaymentResult;
import com.haylion.android.data.model.ShunfengBean;
import com.haylion.android.mvp.base.BaseRepository;
import com.haylion.android.mvp.base.BaseResponse;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface OrderApi {

    /*
     * 	显示数目（0表示当天，1表示当前30天内，2表示60天，3表示90天，以此类推）
     * @return
     */
    //订单列表
    @GET("/driver/personal/historyOrders")
    Observable<OrderDto.OrderListResponse> getOrderList(@QueryMap Map<String, Object> map);

    /*
     * 	历史订单
     * @return
     */
    //订单列表
    @GET("/driver/freight-order/history")
    Observable<BaseResponse<HistoryOrderBean>> getHistoryOrderList(@QueryMap Map<String, Object> map);


    //历史成就 显示数目（0表示当天，1表示当前30天内，2表示60天，3表示90天，以此类推）
    @GET("/driver/personal/achievements")
    Observable<OrderDto.AchievementListResponse> getAchievementAList(@QueryMap Map<String, Object> map);

    //历史成就 （按月查询）
    @GET("/driver/personal/achievements/getByMonth")
    Observable<OrderDto.AchievementByMonthResponse> getAchievementAListByMonth(@Query("year") int year, @Query("month") int month);

    //当前订单/历史订单
    @GET("/driver/waitingOrders/getOrdersForHomePage")
    Observable<OrderDto.OrderForMainActivityListResponse> getCurrentOrder();

    //当前订单/历史订单(包括货物)
    @GET("/driver/waitingOrders/getOrdersForHomePage")
    Observable<OrderDto.OrderForCargoMainActivityResponse> getCurrentOrderAll();

    //开始听单(停止听单)
    @POST("/driver/waitingOrders/requestOrder")
    Observable<OrderDto.VoidResponse> changeListenOrderStatus(@Query("startRequesting") Boolean request, @Query("vehicleId") int vehicleId);

    //获取听单状态
    @GET("/driver/waitingOrders/getRequestingOrderStatus")
    Observable<OrderDto.ListenStatusResponse> getListenOrderStatus();

    //获取推荐的线路
    @GET("/driver/waitingOrders/getRecommendedRoute")
    Observable<OrderDto.SuggestLineResponse> getRecommendRoute();

    //抢单
    //todo
    @POST("/driver/get-order")
    Observable<OrderDto.BooleanResponse> grabOrder(@Body OrderDto.OrderIdRequest request/*@Query("orderId") int orderId*/);

    //获取下一个订单
    @GET("/driver/next-order")
    Observable<OrderDto.OrderDetailForDialogResponse> getNextOrder();

    //获取订单详情
    //todo
    @GET("/driver/waitingOrders/getOrderDetails")
    Observable<OrderDto.OrderDetailResponse> getWorkOrderDetail(@Query("orderId") int orderId);

    //获取顺丰订单详情
    //todo
    @POST("/driver/freight-order/detail")
    Observable<BaseResponse<OrderDetailBean>> getOrderDetail(@Body OrderIdBean orderIdBean);

    //获取听单设置
    @GET("/driver/waitingOrders/getDriverSettings")
    Observable<OrderDto.OrderListenSettingResponse> getOrderSetting();

    //todo get 还是 post ?
    //设置听单
    @POST("/driver/waitingOrders/modifyDriverSettings")
//    Observable<BaseResponse<String>> setOrderSetting(@Body OrderDto.OrderSettingRequest OrderSettingRequest);
    Observable<OrderDto.VoidResponse> setOrderSetting(@Body ListenOrderSetting setting);

    //添加/修改收车的地址
    @POST("/driver/waitingOrders/bindOffWorkAddress")
    Observable<OrderDto.VoidResponse> addBackHomeAddress(@Body OrderDto.BackHomeAddressRequest request);

    //获取收车地址
    @GET("/driver/waitingOrders/getOffWorkAddress")
    Observable<OrderDto.BackHomeAddressResponse> getBackHomeAddress();


    //todo
    //订单状态的改变  变更状态类型 1：到达乘车点；2：接到乘客；3：到达目的地
    @POST("/driver/travelling-order/update-order")
    Observable<OrderDto.BooleanResponse> updateOrderStatus(@Body OrderDto.OrderStatusRequest request);

    //todo
    //订单状态的改变  变更状态类型 1：到达乘车点；2：接到乘客；3：到达目的地
    @POST("/driver/freight-order/update-order")
    Observable<OrderDto.BooleanResponse> updateOrderStatus(@Body ChangeOrderStatusBean request);

    //todo
    //取消订单
    @POST("/driver/travelling-order/cancel-order")
    Observable<OrderDto.BooleanResponse> cancelOrder(@Body OrderDto.CancelOrderRequest request);

    //获取取消原因
    @GET("/driver/travelling-order/cancel-reason")
    Observable<OrderDto.CancelReasonResponse> getCancelOrderReason();

    //支付请求
    @POST("/driver/travelling-order/payment-request")
    Observable<OrderDto.PaymentResultResponse> paymentRequest(@Body PayInfo payInfo);

    //获取服务费
    @GET("/driver/travelling-order/calculate-service-fee")
    Observable<OrderDto.ServiceFeeResponse> getServiceCost(@Query("baseAmount") double baseCost, @Query("orderId") int orderId);

    //todo
    //确认支付
    @POST("/driver/travelling-order/confirm-offline-pay")
    Observable<OrderDto.BooleanResponse> paymentConfirm(@Body OrderDto.PayConfirmRequest request);

    //上报实时位置
    @POST("/driver/real-time-info/record-position")
    Observable<OrderDto.BooleanResponse> gpsDriverUpdata(@Body OrderDto.GpsDataRequest request);

    //获取客服电话
    @GET("/driver/travelling-order/service-phone")
    Observable<OrderDto.ServicePhoneResponse> getServicePhone();

    //上传 预计达到上车点时间
    @POST("/driver/travelling-order/boarding-place/arrival-time/estimate")
    Observable<OrderDto.BooleanResponse> setEstimatePickUpTime(@Body OrderDto.ArrivalTimeRequest request);

    //获取父订单信息
    @GET("/driver/waitingOrders/getParentOrderInfo")
    Observable<OrderDto.ParentOrderResponse> getParentOrderInfo(@Query("orderCode") String parentOrderCode);

    //是否允许更换听单车辆
    @GET("/driver/waitingOrders/isSwitchVehicleAllowed")
    Observable<OrderDto.SwitchVehicleResponse> isSwitchVehicleAllowed(@Query("currentOnlineVehicleId") int currentOnlineVehicleId);

    /**
     * 抢单池 - 预约订单
     */
    @GET("/driver/waitingOrders/appointmentCenter")
    Observable<BaseResponse<List<OrderForMainActivity>>> appointmentCenter();

    /**
     * 抢预约订单
     *
     * @param request 请求
     */
    @POST("/driver/get-appointment-order")
    Observable<BaseResponse<Boolean>> grabAppointment(@Body OrderDto.GrabAppointmentRequest request);

    /**
     * 抢预约顺丰订单
     *
     * @param request 请求
     */
    @POST("/driver/freight-order/claim")
    Observable<BaseResponse<ClaimResult>> grabShunfeng(@Body ClaimBean request);

    /**
     * 抢单池 - 送小孩单
     */
    @GET("/driver/waitingOrders/ChildrenCenter")
    Observable<BaseResponse<List<OrderForMainActivity>>> childrenOrderCenter();

    /**
     * 顺丰听单
     */
    @GET("/driver/freight-order/list")
    Observable<BaseResponse<List<ShunfengWaitBean>>> getShunfengWaitList();

    /**
     * 抢送小孩单
     */
    @POST("/driver/get-children-order")
    Observable<BaseResponse<Boolean>> grabChildrenOrder(@Body OrderDto.GrabAppointmentRequest request);

    /**
     * 抢单池 - 无障碍订单
     */
    @GET("/driver/waitingOrders/BarrierFreeCenter")
    Observable<BaseResponse<List<OrderForMainActivity>>> accessibilityOrderCenter();

    /**
     * 抢单池 - 顺丰订单
     */
    @GET("/driver/freight-order/grabList")
    Observable<BaseResponse<List<ShunfengBean>>> getShunfengOrders();

    /**
     * 抢无障碍订单
     */
    @POST("/driver/get-barrier-free-order")
    Observable<BaseResponse<Boolean>> grabAccessibilityOrder(@Body OrderDto.GrabAppointmentRequest request);

    /**
     * 获取拼车单详情
     */
    @GET("/driver/waitingOrders/getCarpoolOrderDetails")
    Observable<BaseResponse<List<OrderDetail>>> getCarpoolOrderDetails(
            @Query("carpoolCode") String carpoolCode);

    /**
     * 获取送你上学拼车单服务费
     */
    @GET("/driver/travelling-order/calculate-carpool-service-fee")
    Observable<OrderDto.ServiceFeeResponse> getCarpoolServiceFee(@Query("carpoolCode") String carpoolCode);

    /**
     * 送你上学拼车单支付请求
     */
    @POST("/driver/travelling-order/carpool-payment-request")
    Observable<BaseResponse<List<PaymentResult>>> paymentRequestCarpool(@Body PayInfo payInfo);

    /**
     * 获取客服电话
     */
    @GET("/driver/travelling-order/service-phone")
    Observable<BaseResponse<PhoneBean>> getServicePhoneNum();

}
