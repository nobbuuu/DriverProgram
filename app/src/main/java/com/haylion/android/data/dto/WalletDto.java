package com.haylion.android.data.dto;

import com.haylion.android.data.model.IncomeDetail;
import com.haylion.android.data.model.IncomeGeneral;
import com.haylion.android.data.model.ListenOrderSetting;
import com.haylion.android.data.model.MessageDetail;
import com.haylion.android.data.model.MessageDetailSimple;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.model.OrderAbstract;
import com.haylion.android.data.model.WalletTotal;
import com.haylion.android.mvp.base.BaseResponse;
import com.haylion.android.mvp.base.ListData;
import com.haylion.android.mvp.base.ListReponse;
import com.haylion.android.mvp.base.ListRequest;

import java.util.ArrayList;
import java.util.List;


 /**
  * @class  WalletDto
  * @description 支付相关
  * @date: 2019/12/17 10:05
  * @author: tandongdong
  */
public class WalletDto {


    public static class IncomeListRequest extends ListRequest {

        public IncomeListRequest(String month, int page) {
            super(page);
            params.put("yearMonth", month);
        }

        public IncomeListRequest(String month) {
            super();
            params.put("yearMonth", month);
        }

        public IncomeListRequest(String month, int page, int size) {
            super(page, size);
            params.put("yearMonth", month);
        }
    }

    public static class UpdateMessageStatusRequest {

        private List<MessageDetailSimple> list = new ArrayList<>();

        public UpdateMessageStatusRequest(List<MessageDetail> messageDetailList) {
            for(int i = 0; i < messageDetailList.size(); i++){
                MessageDetailSimple simple = new MessageDetailSimple();
                MessageDetail messageDetail = new MessageDetail();
                messageDetail = messageDetailList.get(i);
                simple.setMessageId(messageDetail.getMessageId());
                simple.setDisplay(messageDetail.getDisplay());
                simple.setViewed(messageDetail.getViewed());
                simple.setDeleted(messageDetail.getDeleted());
                list.add(simple);
            }
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


    //听单设置
    //获取听单配置
    public static class OrderListenSettingResponse extends BaseResponse<ListenOrderSetting> {

        public OrderListenSettingResponse(String msg, int code, ListenOrderSetting data) {
            super(msg, code, data);
        }
    }


    //听单设置
    //设置听单配置
    public static class OrderSettingRequest {

        private ListenOrderSetting setting;

        public OrderSettingRequest(ListenOrderSetting setting) {
            this.setting = setting;
        }
    }



    //历史成就
    public static class OrderAchieveListResponse extends BaseResponse<List<OrderAbstract>> {

        public OrderAchieveListResponse(String msg, int code, List<OrderAbstract> data) {
            super(msg, code, data);
        }
    }



    public static class IncomeListResponse extends ListReponse<IncomeDetail> {

        public IncomeListResponse(String msg, int code, ListData<IncomeDetail> data) {
            super(msg, code, data);
        }
    }

    public static class MessageListResponse extends ListReponse<MessageDetail> {

        public MessageListResponse(String msg, int code, ListData<MessageDetail> data) {
            super(msg, code, data);
        }
    }

    //钱包首页信息
    public static class WalletTotalResponse extends BaseResponse<WalletTotal> {

        public WalletTotalResponse(String msg, int code, WalletTotal data) {
            super(msg, code, data);
        }
    }

    public static class IncomeGeneralResponse extends BaseResponse<List<IncomeGeneral>> {

        public IncomeGeneralResponse(String msg, int code, List<IncomeGeneral> data) {
            super(msg, code, data);
        }
    }

     /**
      * 提现到微信请求
      */
    public static class WithdrawToWechatRequest {

        private double amount;

        public WithdrawToWechatRequest(double amount) {
            this.amount = amount;
        }

    }


}
