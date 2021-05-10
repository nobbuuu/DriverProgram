package com.haylion.android.address;

import android.text.TextUtils;

import com.haylion.android.data.repo.CommonRepository;
import com.haylion.android.mvp.base.BasePresenter;


public class SearchAddrPresenter extends BasePresenter<SearchAddrContract.View, CommonRepository>
        implements SearchAddrContract.Presenter {
    private static final String TAG = "SearchAddrPresenter";

    SearchAddrPresenter(SearchAddrContract.View view) {
        super(view, new CommonRepository());
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();

    }

    @Override
    public void onViewDestroy() {
        super.onViewDestroy();
    }

    @Override
    public void getConfigInfo(String deviceId) {
        if (TextUtils.isEmpty(deviceId)) {
            toast("设备ID为空");
            return;
        }

 /*       ConfigInfo configInfo = new ConfigInfo();
        configInfo.setBusRealTimeGetInterval(10000);
        configInfo.setReportInterval(10000);
        configInfo.setLoginName("DB005");
        configInfo.setLoginPassword("root1234");
        PreferencesHelper.saveConfigInfo(configInfo);
*/
        //todo
        //view.getConfigSuccess();
/*
        repo.getConfig(deviceId, new ApiSubscriber<ConfigInfo>() {
            @Override
            public void onSuccess(ConfigInfo data) {
                view.dismissProgressDialog();

                Log.d(TAG, data.toString());

                try {
                    String name = DesUtil.decode(data.getLoginName(), Constant.decodeKey);
                    String password = DesUtil.decode(data.getLoginPassword(), Constant.decodeKey);
                    data.setLoginName(name);
                    data.setLoginPassword(password);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.d(TAG, "decode:" + data.toString());

                //保存配置信息
                PreferencesHelper.saveConfigInfo(data);

                view.getConfigSuccess();
            }

            @Override
            public void onError(int code, String msg) {
                view.dismissProgressDialog();
                Log.e(TAG,"获取配置出错：" + code + "," + msg);
                view.getConfigFail();
                toast("获取配置出错：" + msg);
            }
        });*/
    }
}
