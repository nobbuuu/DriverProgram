package com.haylion.android.data.api;


import com.haylion.android.data.dto.OrderDto;
import com.haylion.android.data.dto.WalletDto;
import com.haylion.android.data.model.MessageDetail;
import com.haylion.android.data.model.MessageDetailSimple;
import com.haylion.android.data.model.Transaction;
import com.haylion.android.mvp.base.BaseResponse;
import com.haylion.android.mvp.base.ListData;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface WalletApi {

    //钱包首页
    @GET("/driver/personal/wallet/home")
    Observable<WalletDto.WalletTotalResponse> getWalletHome();

    //收支列表
    @GET("/driver/personal/wallet/getBalanceStatementList")
    Observable<WalletDto.IncomeListResponse> getIncomeDetail(@QueryMap Map<String, Object> map);


    @GET("/driver/waitingOrders/getMessageInfoList")
    Observable<WalletDto.MessageListResponse> getMessageList(@QueryMap Map<String, Object> map);

    @POST("/driver/waitingOrders/updateMessageInfoList")
    Observable<OrderDto.VoidResponse> updateMessageList(@Body List<MessageDetailSimple> list);

    @POST("/driver/waitingOrders/markAllRead")
    Observable<OrderDto.VoidResponse> allMessageReaded();

    //获取月度收支概况
    @GET("/driver/personal/wallet/getYearMonthList")
    Observable<WalletDto.IncomeGeneralResponse> getIncomeDetailMonthList();

    //绑定银行
    @POST("/driver/personal/wallet/bindBankCard")
    Observable<BaseResponse<String>> bindBankCard();

    //解绑银行
    @POST("/driver/personal/wallet/unbindBankCard")
    Observable<BaseResponse<String>> unbindBankCard(@Query("cardNumber") String cardNumber, @Query("mobile") String mobile);

    //提现
    @POST("/driver/personal/wallet/withdraw")
    Observable<BaseResponse<String>> withdraw(@Query("amount") String amount, @Query("cardNumber") String cardNumber);

    /**
     * 获取收支明细
     */
    @GET("/driver/personal/wallet/summary")
    Observable<BaseResponse<ListData<Transaction>>> getTransactionList(@Query("page") int page, @Query("size") int pageSize, @Query("summaryType") int type);

    /**
     * 提现到微信
     */
    @POST("/enterprise/pay/personal")
    Observable<BaseResponse<Boolean>> withdrawToWechat(@Body WalletDto.WithdrawToWechatRequest request);

}
