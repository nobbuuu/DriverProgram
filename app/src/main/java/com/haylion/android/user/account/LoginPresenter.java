package com.haylion.android.user.account;

import android.text.TextUtils;
import android.util.Log;

import com.haylion.android.Constants;
import com.haylion.android.R;
import com.haylion.android.data.model.Driver;
import com.haylion.android.data.model.LoginData;
import com.haylion.android.data.model.ServiceNumber;
import com.haylion.android.data.model.Vehicle;
import com.haylion.android.data.repo.AccountRepository;
import com.haylion.android.data.repo.PrefserHelper;
import com.haylion.android.data.util.HashUtil;
import com.haylion.android.mvp.base.BasePresenter;
import com.haylion.android.mvp.rx.ApiSubscriber;
import com.haylion.android.mvp.util.LogUtils;

import java.util.List;


public class LoginPresenter extends BasePresenter<LoginContract.View, AccountRepository>
        implements LoginContract.Presenter {
    private static final String TAG = "LoginPresenter";
    private List<Vehicle> vehicleList;

    LoginPresenter(LoginContract.View view) {
        super(view, new AccountRepository());
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();

    }

    @Override
    public void login(String username, String password, String number) {
        //找到对应的车辆ID
        int vehicleId = 0;
        view.showProgressDialog("正在登录，请稍候……");
        String passwordEncrypt = password;
        try {
            passwordEncrypt = HashUtil.md5(password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        repo.login(username, vehicleId, passwordEncrypt, new ApiSubscriber<LoginData>() {
            @Override
            public void onSuccess(LoginData data) {
                Log.d(TAG, "login data: " + data.toString());
                //保存Token
                PrefserHelper.saveToken(data.getToken());
                //保存司机信息
                Driver driver = new Driver();
                driver.setCode(data.getUserCode());
                PrefserHelper.saveDriverInfo(driver);
                //保存车辆列表
                PrefserHelper.saveVehicleList(data.getBindVehicleList());
                //保存最近一次使用的听单车辆id
                if(data.getBindVehicleList()!=null && data.getLatestUsedVehicleId()!=-1){
                    for (Vehicle vehicle:data.getBindVehicleList()){
                        if(vehicle.getId() == data.getLatestUsedVehicleId()){
                            PrefserHelper.saveVehicleInfo(vehicle);
                            break;
                        }
                    }
                }

                if (data.getBindVehicleList()!= null && data.getBindVehicleList().size() == 1) {
                    PrefserHelper.saveVehicleInfo(data.getBindVehicleList().get(0));
                }

                view.dismissProgressDialog();
                view.onLoginSuccess();
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.e(TAG, "登录出错：" + code + ", " + msg);
                view.dismissProgressDialog();
                String tips;
                switch (code) {
                    case 200102:
                        tips = "该准许证号存在多个对应的司机";
                        break;
                    case 200103:
                        tips = "密码错误";
                        break;
                    case 200105:
                        tips = "准许证号不存在";
                        break;
                    case 200106:
                        tips = "准许证号已停用";
                        break;
                    case 200107:
                        tips = "准许证号和车牌不匹配";
                        break;
                    default:
                        tips = msg;
                        break;
                }
                view.showLoginFailTips(tips);
            }
        });
    }

    @Override
    public void getVehicleList(String code) {
        /*repo.getVehicleList(code, new ApiSubscriber<List<Vehicle>>() {
            @Override
            public void onSuccess(List<Vehicle> vehicles) {
                view.updateVehicleView(vehicles);
                vehicleList = vehicles;
            }

            @Override
            public void onError(int code, String msg) {
                String tips;
                switch (code) {
                    case 200102:
                        tips = "该准许证号存在多个对应的司机";
                        break;
                    case 200103:
                        tips = "密码错误";
                        break;
                    case 200105:
                        tips = "准许证号不存在";
                        break;
                    case 200106:
                        tips = "准许证号已停用";
                        break;
                    case 200107:
                        tips = "准许证号和车牌不匹配";
                        break;
                    case 200108:
                        tips = "没有绑定车辆";
                        break;
                    default:
                        tips = msg;
                        break;
                }
                view.showLoginFailTips(tips);
            }
        });*/
    }

    @Override
    public void getServiceTelNumber(boolean showDialog) {
        repo.getSericeTelNumber(new ApiSubscriber<ServiceNumber>() {
            @Override
            public void onSuccess(ServiceNumber serviceNumber) {
                PrefserHelper.setCache(PrefserHelper.KEY_SERVICE_NUMBER, serviceNumber.getPhone());
                Constants.SERVICE_PHONE = serviceNumber.getPhone();
                if(showDialog){
                    view.showDialDialog(serviceNumber.getPhone());
                }

            }

            @Override
            public void onError(int code, String msg) {
                toastError(R.string.toast_get_customer_service_phone_error,msg);
                LogUtils.e(TAG, "getServiceTelNumber:" + code + "," + msg);
            }
        });
    }

    @Override
    public void onViewDestroy() {
        super.onViewDestroy();

    }

}
