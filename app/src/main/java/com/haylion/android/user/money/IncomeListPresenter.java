package com.haylion.android.user.money;


import com.haylion.android.data.model.IncomeDetail;
import com.haylion.android.data.model.IncomeGeneral;
import com.haylion.android.data.repo.WalletRepository;
import com.haylion.android.mvp.base.BasePresenter;
import com.haylion.android.mvp.base.ListData;
import com.haylion.android.mvp.rx.ApiSubscriber;
import com.haylion.android.mvp.util.LogUtils;

import java.util.List;

public class IncomeListPresenter extends BasePresenter<IncomeListContract.View, WalletRepository>
        implements IncomeListContract.Presenter {

    private static final String TAG = "NotificationListPresenter";
    //    private OrderTimeType mOrderType;
    private int mCurrPage = 1;
    //todo
    private String month = "2019-01";

    private boolean mRefreshing = false;

    IncomeListPresenter(IncomeListContract.View view) {
        super(view, WalletRepository.INSTANCE);
//        this.mOrderType = null;
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
//        getIncomeList();
          getIncomeMonthList();
    }

    private void getIncomeMonthList() {
        repo.getIncomeMonthList(new ApiSubscriber<List<IncomeGeneral>>() {
            @Override
            public void onSuccess(List<IncomeGeneral> incomeGeneralList) {
                view.updateMonthView(incomeGeneralList);
            }

            @Override
            public void onError(int code, String msg) {
                toastError("获取收益明细年月列表失败",msg);
            }
        });
    }

    @Override
    public void loadMoreOrders() {
        mCurrPage++;
        getIncomeList();
    }

    private void getIncomeList() {
        getIncomeList(month);
    }

    private void getIncomeList(String month) {

        repo.getIncomeList(month, mCurrPage, new ApiSubscriber<ListData<IncomeDetail>>() {
            @Override
            public void onSuccess(ListData<IncomeDetail> list) {
/*                for (Order order : list.getList()) {
                    order.setDialable(mOrderType != OrderTimeType.HISTORY &&
                            !order.isCompleted() && !order.isCompleted()
                    );
                }*/
                if (list.getPageNumber() == 1) {
                    view.setIncomeList(list.getList());
                    if (mRefreshing && list.isListEmpty()) {
                       // toast("数据为空");
                        mRefreshing = false;
                    }
                } else {
                    view.addMoreOrders(list.getList());
                }
                if (list.getPageNumber() == list.getPageCount()) {
                    view.noMoreOrders();
                }
                mCurrPage = list.getPageNumber();
            }

            @Override
            public void onError(int code, String msg) {
                view.setIncomeList(null);
                mRefreshing = false;
                toastError("获取收益明细失败",msg);
                LogUtils.e("获取收益明细失败：" + code + ", " + msg);
            }
        });
    }

    @Override
    public void refreshOrderList() {
        mCurrPage = 1;
        mRefreshing = true;
        getIncomeList();
    }

    @Override
    public void setSelectedMonth(String month) {
        this.month = month;
    }


    @Override
    public void onViewDestroy() {
        super.onViewDestroy();
    }

}
