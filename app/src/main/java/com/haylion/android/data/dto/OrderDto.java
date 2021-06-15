package com.haylion.android.data.dto;

import com.haylion.android.common.aibus_location.data.GpsData;
import com.haylion.android.data.model.CancelReason;
import com.haylion.android.data.model.AchievementList;
import com.haylion.android.data.model.AddressForSuggestLine;
import com.haylion.android.data.model.AddressInfo;
import com.haylion.android.data.model.BackHomeAddress;
import com.haylion.android.data.model.IncomeDetail;
import com.haylion.android.data.model.ListenOrderSetting;
import com.haylion.android.data.model.ListenStatus;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.model.OrderAbstract;
import com.haylion.android.data.model.OrderAbstractByMonth;
import com.haylion.android.data.model.OrderDetail;
import com.haylion.android.data.model.NewOrder;
import com.haylion.android.data.model.OrderDetailExt;
import com.haylion.android.data.model.OrderForCargoAndPassenger;
import com.haylion.android.data.model.OrderHistory;
import com.haylion.android.data.model.OrderForMainActivity;
import com.haylion.android.data.model.PaymentResult;
import com.haylion.android.data.model.ServiceFee;
import com.haylion.android.data.model.ServiceNumber;
import com.haylion.android.data.model.SwitchVehicleJudgeBean;
import com.haylion.android.data.repo.PrefserHelper;
import com.haylion.android.mvp.base.BaseResponse;
import com.haylion.android.mvp.base.ListData;
import com.haylion.android.mvp.base.ListReponse;
import com.haylion.android.mvp.base.ListRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @class OrderDto
 * @description 订单相关
 * @date: 2019/12/17 10:04
 * @author: tandongdong
 */
public class OrderDto {


    public static class OrderListRequest extends ListRequest {

        public OrderListRequest(long dutyId, int page, String status) {
            super(page);
            params.put("timesId", dutyId);
            params.put("vehicleId", status);
        }

        public OrderListRequest(long timesId, int vehicleId) {
            super();
            params.put("timesId", timesId);
            params.put("vehicleId", vehicleId);
        }

        public OrderListRequest(boolean showCompletionOnly, boolean showPassenger, int index, int page, int size) {
            super(page, size);
            params.put("showCompletionOnly", showCompletionOnly);
            params.put("showPassenger", showPassenger);
            params.put("index", index);
        }

        public OrderListRequest(boolean showCompletionOnly, int index, int page, int size) {
            super(page, size);
            params.put("showCompletionOnly", showCompletionOnly);
            params.put("index", index);
        }
    }


    public static class OrderForMainActivityListResponse extends BaseResponse<List<OrderForMainActivity>> {

        public OrderForMainActivityListResponse(String msg, int code, List<OrderForMainActivity> data) {
            super(msg, code, data);
        }
    }

    public static class OrderForCargoMainActivityResponse extends BaseResponse<OrderForCargoAndPassenger> {

        public OrderForCargoMainActivityResponse(String msg, int code, OrderForCargoAndPassenger data) {
            super(msg, code, data);
        }
    }


    public static class AchievementListRequest extends ListRequest {

        public AchievementListRequest(int page, int size, int index) {
            super(page, size);
            params.put("index", index);
        }
    }

    public static class AchievementListResponse extends ListReponse<OrderAbstract> {

        public AchievementListResponse(String msg, int code, ListData<OrderAbstract> data) {
            super(msg, code, data);
        }
    }

    //历史成就，按月查询
    public static class AchievementByMonthResponse extends BaseResponse<OrderAbstractByMonth> {

        public AchievementByMonthResponse(String msg, int code, OrderAbstractByMonth data) {
            super(msg, code, data);
        }
    }

    public static class AchievementResponse extends BaseResponse<AchievementList> {

        public AchievementResponse(String msg, int code, AchievementList data) {
            super(msg, code, data);
        }
    }


    public static class StartOrderRequest {

        private int orderId;

        public StartOrderRequest(int orderId) {
            this.orderId = orderId;
        }
    }

    public static class OrderIdRequest {

        private int orderId;

        public OrderIdRequest(int orderId) {
            this.orderId = orderId;
        }
    }

    public static class FinishOrderRequest {
        private int orderId;

        public FinishOrderRequest(int orderId) {
            this.orderId = orderId;
        }
    }

    public static class StartOrderResponse extends BaseResponse<String> {

        public StartOrderResponse(String msg, int code, String data) {
            super(msg, code, data);
        }
    }


    public static class SuggestLineResponse extends BaseResponse<ArrayList<AddressForSuggestLine>> {

        public SuggestLineResponse(String msg, int code, ArrayList<AddressForSuggestLine> data) {
            super(msg, code, data);
        }
    }

    public static class ListenStatusResponse extends BaseResponse<ListenStatus> {

        public ListenStatusResponse(String msg, int code, ListenStatus data) {
            super(msg, code, data);
        }
    }

    public static class FinishOrderResponse extends BaseResponse<String> {

        public FinishOrderResponse(String msg, int code, String data) {
            super(msg, code, data);
        }
    }

    public static class WorkOrderDetailResponse extends BaseResponse<Order> {

        public WorkOrderDetailResponse(String msg, int code, Order data) {
            super(msg, code, data);
        }
    }


    public static class getTodayTotalRequest {
        private int orderId;

        public getTodayTotalRequest(int orderId) {
            this.orderId = orderId;
        }
    }

    public static class getTodayTotalResponse extends BaseResponse<OrderAbstract> {

        public getTodayTotalResponse(String msg, int code, OrderAbstract data) {
            super(msg, code, data);
        }
    }

    //获取听单配置
    public static class OrderListenSettingResponse extends BaseResponse<ListenOrderSetting> {

        public OrderListenSettingResponse(String msg, int code, ListenOrderSetting data) {
            super(msg, code, data);
        }
    }

    //设置听单配置
    public static class OrderSettingRequest {

        private ListenOrderSetting setting;

        public OrderSettingRequest(ListenOrderSetting setting) {
            this.setting = setting;
        }
    }

    //获取客服电话
    public static class ServicePhoneResponse extends BaseResponse<ServiceNumber> {

        public ServicePhoneResponse(String msg, int code, ServiceNumber data) {
            super(msg, code, data);
        }
    }


    //历史成就
    public static class OrderAchieveListResponse extends BaseResponse<List<OrderAbstract>> {

        public OrderAchieveListResponse(String msg, int code, List<OrderAbstract> data) {
            super(msg, code, data);
        }
    }

    //历史订单
    public static class OrderListResponse extends ListReponse<OrderHistory> {

        public OrderListResponse(String msg, int code, ListData<OrderHistory> data) {
            super(msg, code, data);
        }
    }


    //收入列表
    public static class IncomeListResponse extends BaseResponse<List<IncomeDetail>> {

        public IncomeListResponse(String msg, int code, List<IncomeDetail> data) {
            super(msg, code, data);
        }
    }

    public static class VoidResponse extends BaseResponse<Void> {

        public VoidResponse(String msg, int code, Void data) {
            super(msg, code, data);
        }
    }

    public static class BooleanResponse extends BaseResponse<Boolean> {

        public BooleanResponse(String msg, int code, Boolean data) {
            super(msg, code, data);
        }
    }

    public static class ServiceFeeResponse extends BaseResponse<ServiceFee> {

        public ServiceFeeResponse(String msg, int code, ServiceFee data) {
            super(msg, code, data);
        }
    }

    public static class PaymentResultResponse extends BaseResponse<PaymentResult> {

        public PaymentResultResponse(String msg, int code, PaymentResult data) {
            super(msg, code, data);
        }
    }


    public static class StringResponse extends BaseResponse<String> {

        public StringResponse(String msg, int code, String data) {
            super(msg, code, data);
        }
    }

    public static class BackHomeAddressResponse extends BaseResponse<BackHomeAddress> {

        public BackHomeAddressResponse(String msg, int code, BackHomeAddress data) {
            super(msg, code, data);
        }
    }


    public static class CancelReasonResponse extends BaseResponse<List<CancelReason>> {

        public CancelReasonResponse(String msg, int code, List<CancelReason> data) {
            super(msg, code, data);
        }
    }

    public static class OrderDetailForDialogResponse extends BaseResponse<NewOrder> {

        public OrderDetailForDialogResponse(String msg, int code, NewOrder data) {
            super(msg, code, data);
        }
    }

    public static class OrderDetailResponse extends BaseResponse<OrderDetail> {

        public OrderDetailResponse(String msg, int code, OrderDetail data) {
            super(msg, code, data);
        }
    }


    public static class OrderStatusRequest {

        private int orderId;

        private int stateType;

        public OrderStatusRequest(int orderId, int stateType) {
            this.orderId = orderId;
            this.stateType = stateType;
        }

    }

    public static class BackHomeAddressRequest {

        private int driverId;
        private String offWorkAddressTitle;
        private String offWorkAddressDescription;
        private double offWorkAddressLon;
        private double offWorkAddressLat;

        public BackHomeAddressRequest(AddressInfo addressInfo) {
            this.driverId = 0;
            this.offWorkAddressTitle = addressInfo.getName();
            this.offWorkAddressDescription = addressInfo.getAddressDetail();
            this.offWorkAddressLon = addressInfo.getLatLng().longitude;
            this.offWorkAddressLat = addressInfo.getLatLng().latitude;
        }

    }


    public static class CancelOrderRequest {
        private int orderId;
        private String cancellerMsg;
        private String picUrl;
        private double meterPrice;
        private short exceptionCancel;

        public CancelOrderRequest(int orderId, String cancelReason, String picUrl) {
            this.orderId = orderId;
            this.cancellerMsg = cancelReason;
            this.picUrl = picUrl;
        }

        public CancelOrderRequest(int orderId, short exceptionCancel, String cancelReason, double meterPrice) {
            this.orderId = orderId;
            this.cancellerMsg = cancelReason;
            this.meterPrice = meterPrice;
            this.exceptionCancel = exceptionCancel;
        }

    }

    public static class PayConfirmRequest {
        private int orderId;
        private int state;

        public PayConfirmRequest(int orderId, int state) {
            this.orderId = orderId;
            this.state = state;
        }

    }

    public static class GpsDataRequest {
        private double lon;
        private double lat;
        private int ori;
        private int mode;

        public GpsDataRequest(GpsData gpsData) {
            this.lat = gpsData.getLatitude();
            this.lon = gpsData.getLongitude();
            this.ori = (int) gpsData.getBearing();
            this.mode = PrefserHelper.getNaviSetInfo().getDrivingMode();
        }

    }


    /**
     * 预计 到达上车点时间
     */
    public static class ArrivalTimeRequest {
        private int id;       //订单id
        private long timestamp;  //预计到达上车点时间戳，单位毫秒

        public ArrivalTimeRequest(int id, long timestamp) {
            this.id = id;
            this.timestamp = timestamp;
        }
    }

    public static class ParentOrderResponse extends BaseResponse<OrderDetailExt> {

        public ParentOrderResponse(String msg, int code, OrderDetailExt data) {
            super(msg, code, data);
        }
    }

    public static class SwitchVehicleResponse extends BaseResponse<SwitchVehicleJudgeBean> {

        public SwitchVehicleResponse(String msg, int code, SwitchVehicleJudgeBean data) {
            super(msg, code, data);
        }
    }

    /**
     * 抢预约订单请求
     */
    public static class GrabAppointmentRequest {

        private String orderCode; // 订单编号
        private int parentOrder; // 是否未父订单

        public GrabAppointmentRequest(String orderCode, boolean parentOrder) {
            this.orderCode = orderCode;
            this.parentOrder = parentOrder ? 1 : 0;
        }

        /**
         * 送你上学 - 拼车单专用构造器
         *
         * @param order 订单
         */
        public GrabAppointmentRequest(Order order) {
            if (order.isCarpoolOrder()) { // 拼车单
                this.orderCode = order.getCarpoolCode();
                this.parentOrder = 2;
            } else {
                this.orderCode = order.getOrderCode();
                this.parentOrder = order.isParentOrder() ? 1 : 0;
            }
        }

    }


}
