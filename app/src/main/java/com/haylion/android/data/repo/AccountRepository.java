package com.haylion.android.data.repo;

import android.util.Log;

import com.google.gson.Gson;
import com.haylion.android.data.api.AccountApi;
import com.haylion.android.data.api.OrderApi;
import com.haylion.android.data.dto.AccountDto;
import com.haylion.android.data.model.Driver;
import com.haylion.android.data.model.LatestVersionBean;
import com.haylion.android.data.model.LoginData;
import com.haylion.android.data.model.ServiceNumber;
import com.haylion.android.data.model.ShiftInfo;
import com.haylion.android.data.model.Vehicle;
import com.haylion.android.data.model.WxOpenId;
import com.haylion.android.mvp.base.BaseRepository;
import com.haylion.android.mvp.net.RetrofitHelper;
import com.haylion.android.mvp.rx.ApiSubscriber;
import com.haylion.android.mvp.rx.ApiTransformer;
import com.haylion.android.user.vehicle.VehicleOperation;

import org.libsodium.jni.crypto.Hash;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @class AccountRepository
 * @description 账号相关的网络请求
 * @date: 2019/12/17 10:11
 * @author: tandongdong
 */
public class AccountRepository extends BaseRepository {

    public static final AccountRepository INSTANCE = new AccountRepository();

    public void login(String username, int vehicleId, String password, ApiSubscriber<LoginData> callback) {
        AccountDto.LoginRequest loginRequest = new AccountDto.LoginRequest(username, password, vehicleId);
        String param = new Gson().toJson(loginRequest, AccountDto.LoginRequest.class);
        Log.d("aaa","param = " + param);
        addDisposable(RetrofitHelper.getApi(AccountApi.class)
                .login(loginRequest)
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }

    public void getVehicleList(String code, ApiSubscriber<List<Vehicle>> callback) {
        addDisposable(RetrofitHelper.getApi(AccountApi.class)
                .getVehicleList(code)
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }

    //退出登录
    public void logout(ApiSubscriber<Void> callback) {
        addDisposable(RetrofitHelper.getApi(AccountApi.class)
                .logout()
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }

    //获取司机信息
    public void getDriverInfo(ApiSubscriber<Driver> callback) {
        addDisposable(RetrofitHelper.getApi(AccountApi.class)
                .getPersonalInfo()
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }


/*    public void sendCaptcha(String jobNumber, ApiSubscriber<Void> callback) {
        return RetrofitHelper.getApi(AccountApi.class)
                .sendCaptcha(new AccountDto.SendCaptchaRequest(jobNumber))
                .compose(new ApiTransformer<>())
                .subscribeWith(callback);
    }

    public void resetPassword(String jobNumber, String captcha, String newPassword, ApiSubscriber<Void> callback) {
        return RetrofitHelper.getApi(AccountApi.class)
                .resetPassword(new AccountDto.ResetPasswordRequest(jobNumber, captcha, newPassword))
                .compose(new ApiTransformer<>())
                .subscribeWith(callback);
    }*/

    //修改密码
    public void changePassword(String oldPassword, String newPassword, ApiSubscriber<Void> callback) {
        addDisposable(RetrofitHelper.getApi(AccountApi.class)
                .changePassword(new AccountDto.ChangePasswordRequest(oldPassword, newPassword))
                .compose(new ApiTransformer<>())
                .subscribeWith(callback));
    }

    //获取客服电话
    public void getSericeTelNumber(ApiSubscriber<ServiceNumber> callback) {
        addDisposable(RetrofitHelper.getApi(OrderApi.class)
                .getServicePhone()
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
     * 查询该司机绑定的车辆 #1423
     *
     * @param callback 回调
     */
    public void getVehicleList(ApiSubscriber<List<Vehicle>> callback) {
        addDisposable(RetrofitHelper.getApi(AccountApi.class)
                .getVehicleList()
                .compose(new ApiTransformer<>())
                .subscribeWith(callback));
    }

    /**
     * 更新司机绑定的车辆信息
     *
     * @param operation 操作类型
     * @param vehicle   车辆
     * @param callback  回调
     */
    public void updateVehicle(VehicleOperation operation, Vehicle vehicle, ApiSubscriber<Boolean> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("operationType", operation.getOperation());
        if (operation == VehicleOperation.ADD) {
            params.put("newVehicleNumber", vehicle.getNumber());
        } else if (operation == VehicleOperation.DELETE) {
            params.put("vehicleId", vehicle.getId());
        } else if (operation == VehicleOperation.MODIFY) {
            params.put("vehicleId", vehicle.getId());
            params.put("newVehicleNumber", vehicle.getNumber());
        }
        addDisposable(RetrofitHelper.getApi(AccountApi.class)
                .updateVehicle(params)
                .compose(new ApiTransformer<>())
                .subscribeWith(callback));
    }

    /**
     * 获取司机班次信息
     *
     * @param callback 回调
     */
    public void getShiftInfo(ApiSubscriber<ShiftInfo> callback) {
        addDisposable(RetrofitHelper.getApi(AccountApi.class)
                .getShiftInfo()
                .compose(new ApiTransformer<>())
                .subscribeWith(callback));
    }

    /**
     * 修改司机的排班信息
     *
     * @param shiftInfo 排班信息
     * @param callback  回调
     */
    public void modifyShiftInfo(ShiftInfo shiftInfo, ApiSubscriber<Boolean> callback) {
        addDisposable(RetrofitHelper.getApi(AccountApi.class)
                .modifyShiftInfo(shiftInfo)
                .compose(new ApiTransformer<>())
                .subscribeWith(callback));
    }

    /**
     * 绑定微信
     * @param wxCode 微信code
     * @param callback 回调
     */
    public void bindWechat(String wxCode, ApiSubscriber<WxOpenId> callback) {
        addDisposable(RetrofitHelper.getApi(AccountApi.class)
                .bindWechat(wxCode)
                .compose(new ApiTransformer<>())
                .subscribeWith(callback));
    }


}

