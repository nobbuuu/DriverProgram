package com.haylion.android.data.api;


import com.haylion.android.data.dto.AccountDto;
import com.haylion.android.data.model.ShiftInfo;
import com.haylion.android.data.model.Vehicle;
import com.haylion.android.data.model.WxOpenId;
import com.haylion.android.mvp.base.BaseRepository;
import com.haylion.android.mvp.base.BaseResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface AccountApi {

    @POST("account/driver/login")
    Observable<AccountDto.LoginResponse> login(@Body AccountDto.LoginRequest request);

    @GET("/account/driver/getVehicles")
    Observable<AccountDto.VehicleResponse> getVehicleList(@Query("code") String code);

/*    @POST("account/driver/forgot-password/sms/send")
    Observable<AccountDto.SendCaptChaResponse> sendCaptcha(@Body AccountDto.SendCaptchaRequest request);

    @POST("account/driver/forgot-password")
    Observable<AccountDto.ResetPasswordResponse> resetPassword(@Body AccountDto.ResetPasswordRequest request);*/

    @POST("/account/driver/changePassword")
    Observable<AccountDto.ChangePasswordResponse> changePassword(@Body AccountDto.ChangePasswordRequest request);


    @POST("/account/driver/logout")
    Observable<AccountDto.LogoutResponse> logout();

    @GET("/driver/personal/home")
    Observable<AccountDto.DriverResponse> getPersonalInfo();

    @GET("/driver/real-time-info/amap-args")
    Observable<AccountDto.AmapTrackResponse> getAmapGpsTrackArgs();

    @GET("/account/latest-version")
    Observable<AccountDto.LatestVersionResponse> checkUpdates(@Query("target") int target,
                                                              @Query("platform") int platform);

    /**
     * 获取司机绑定的车辆列表
     */
    @GET("driver/personal/getVehicleList")
    Observable<BaseResponse<List<Vehicle>>> getVehicleList();

    /**
     * 更新司机绑定的车辆
     *
     * @param params 请求参数
     */
    @POST("driver/personal/modifyVehicles")
    Observable<BaseResponse<Boolean>> updateVehicle(@QueryMap Map<String, Object> params);

    /**
     * 获取司机班次信息
     */
    @GET("/driver/personal/personalInfo")
    Observable<BaseResponse<ShiftInfo>> getShiftInfo();

    /**
     * 修改司机班次信息
     */
    @POST("/driver/personal/modifyPersonalInfo")
    Observable<BaseResponse<Boolean>> modifyShiftInfo(@Body ShiftInfo shiftInfo);

    /**
     * 绑定微信
     */
    @GET("/account/driver/bindWechat")
    Observable<BaseResponse<WxOpenId>> bindWechat(@Query("wechatCode") String wxCode);


}
