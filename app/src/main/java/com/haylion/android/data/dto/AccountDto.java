package com.haylion.android.data.dto;

import com.haylion.android.data.model.AmapTrack;
import com.haylion.android.data.model.Driver;
import com.haylion.android.data.model.LatestVersionBean;
import com.haylion.android.data.model.LoginData;
import com.haylion.android.data.model.Vehicle;
import com.haylion.android.mvp.base.BaseResponse;

import java.util.List;

/**
 * @class AccountDto
 * @description 账号相关
 * @date: 2019/12/17 10:04
 * @author: tandongdong
 */
public class AccountDto {

    /**
     * 登录请求
     */
    public static class LoginRequest {

        private String code;

        private String password;

        private int vehicleId;

        public LoginRequest(String username, String password, int vehicleId) {
            this.code = username;
            this.password = password;
            this.vehicleId = vehicleId;
        }

    }

    public static class LoginResponse extends BaseResponse<LoginData> {

        public LoginResponse(String msg, int code, LoginData data) {
            super(msg, code, data);
        }
    }

    public static class VehicleResponse extends BaseResponse<List<Vehicle>> {

        public VehicleResponse(String msg, int code, List<Vehicle> data) {
            super(msg, code, data);
        }
    }


    /**
     * 修改密码
     */
    public static class ChangePasswordRequest {

        private String oldPassword;
        private String newPassword;

        public ChangePasswordRequest(String oldPassword, String newPassword) {
            this.oldPassword = oldPassword;
            this.newPassword = newPassword;
        }
    }

    public static class ChangePasswordResponse extends BaseResponse<Void> {

        public ChangePasswordResponse(String msg, int code, Void data) {
            super(msg, code, data);
        }
    }

    public static class LogoutResponse extends BaseResponse<Void> {

        public LogoutResponse(String msg, int code, Void data) {
            super(msg, code, data);
        }
    }

    public static class DriverResponse extends BaseResponse<Driver> {

        public DriverResponse(String msg, int code, Driver data) {
            super(msg, code, data);
        }
    }

    public static class AmapTrackResponse extends BaseResponse<AmapTrack> {

        public AmapTrackResponse(String msg, int code, AmapTrack data) {
            super(msg, code, data);
        }
    }

    public static class LatestVersionResponse extends BaseResponse<LatestVersionBean> {

        public LatestVersionResponse(String msg, int code, LatestVersionBean data) {
            super(msg, code, data);
        }
    }


}
