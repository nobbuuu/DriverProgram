package com.haylion.android.data.repo;

import com.haylion.android.data.api.AccountApi;
import com.haylion.android.data.model.LatestVersionBean;
import com.haylion.android.mvp.base.BaseRepository;
import com.haylion.android.mvp.net.RetrofitHelper;
import com.haylion.android.mvp.rx.ApiSubscriber;
import com.haylion.android.mvp.rx.ApiTransformer;

/**
 * @class CommonRepository
 * @description 通用网络请求
 * @date: 2019/12/17 10:14
 * @author: tandongdong
 */
public class CommonRepository extends BaseRepository {

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

/*    public void getConfig(String deviceId, ApiSubscriber<ConfigInfo> callback) {
        RetrofitHelper.getApi(.class)
                .getConfig(*//*new ConfigDto.ConfigRequest(deviceId, " ")*//*)
                .compose(new ApiTransformer<>())
                .subscribeWith(callback);
    }*/


}
