package com.haylion.android.data.repo;

import com.haylion.android.data.api.WalletApi;
import com.haylion.android.data.dto.WalletDto;
import com.haylion.android.data.model.IncomeDetail;
import com.haylion.android.data.model.IncomeGeneral;
import com.haylion.android.data.model.MessageDetail;
import com.haylion.android.data.model.MessageDetailSimple;
import com.haylion.android.data.model.MessageListData;
import com.haylion.android.data.model.Transaction;
import com.haylion.android.data.model.WalletTotal;
import com.haylion.android.mvp.base.BaseRepository;
import com.haylion.android.mvp.base.ListData;
import com.haylion.android.mvp.base.ListRequest;
import com.haylion.android.mvp.net.RetrofitHelper;
import com.haylion.android.mvp.rx.ApiSubscriber;
import com.haylion.android.mvp.rx.ApiTransformer;

import java.util.List;

/**
 * @class WalletRepository
 * @description 费用相关的网络请求
 * @date: 2019/12/17 10:12
 * @author: tandongdong
 */
public class WalletRepository extends BaseRepository {

    public static final WalletRepository INSTANCE = new WalletRepository();

    //收入明细列表
    public void getIncomeList(String month, int page, ApiSubscriber<ListData<IncomeDetail>> callback) {
        addDisposable(RetrofitHelper.getApi(WalletApi.class)
                .getIncomeDetail(new WalletDto.IncomeListRequest(month, page, 20).getQueryMap())
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }

    //收入明细的月份
    public void getIncomeMonthList(ApiSubscriber<List<IncomeGeneral>> callback) {
        addDisposable(RetrofitHelper.getApi(WalletApi.class)
                .getIncomeDetailMonthList()
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }

    //钱包首页的数据
    public void getWalletHome(ApiSubscriber<WalletTotal> callback) {
        addDisposable(RetrofitHelper.getApi(WalletApi.class)
                .getWalletHome()
                .compose(new ApiTransformer<>())
                .subscribeWith(callback)
        );
    }

    /**
     * 获取收支明细
     *
     * @param page     页码
     * @param pageSize 每页数据条数
     * @param type     类型
     * @param callback 回调
     */
    public void getTransactionList(int page, int pageSize, int type, ApiSubscriber<ListData<Transaction>> callback) {
        addDisposable(RetrofitHelper.getApi(WalletApi.class)
                .getTransactionList(page, pageSize, type)
                .compose(new ApiTransformer<>())
                .subscribeWith(callback));
    }

    /**
     * 提现到微信
     */
    public void withdrawToWechat(double amount, ApiSubscriber<Boolean> callback) {
        addDisposable(RetrofitHelper.getApi(WalletApi.class)
                .withdrawToWechat(new WalletDto.WithdrawToWechatRequest(amount))
                .compose(new ApiTransformer<>())
                .subscribeWith(callback));
    }


}
