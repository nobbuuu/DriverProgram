package com.haylion.android.orderlist.achievement;


import android.util.Log;

import com.haylion.android.data.model.OrderAbstract;
import com.haylion.android.data.model.OrderAbstractByMonth;
import com.haylion.android.data.model.OrderTimeType;
import com.haylion.android.data.repo.OrderRepository;
import com.haylion.android.mvp.base.BasePresenter;
import com.haylion.android.mvp.base.ListData;
import com.haylion.android.mvp.rx.ApiSubscriber;
import com.haylion.android.mvp.util.LogUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AchievementListPresenter extends BasePresenter<AchievementListContract.View, OrderRepository>
        implements AchievementListContract.Presenter {

    private static final String TAG = "OrderListPresenter";
    private OrderTimeType mOrderTimeType;
    private int mCurrPage = 1;
    private boolean mRefreshing = false;
    public static int lastYearAndMonth = 0; //标记上一个数据的月份信息

    AchievementListPresenter(OrderTimeType orderTimeType, AchievementListContract.View view) {
        super(view, OrderRepository.INSTANCE);
        this.mOrderTimeType = orderTimeType;
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
//        getAchievementList();
        loadCurrentMonthData();
    }

    @Override
    public void loadMoreOrders() {
        mCurrPage++;
//        getAchievementList();
        loadMoreDataByMonth();
    }

    private void getAchievementList() {
        int index = 0;
        if (mOrderTimeType == OrderTimeType.TODAY) {
            index = 0;
        } else {
            index = 3;
        }
        repo.getAchievementList(mCurrPage, index, new ApiSubscriber<ListData<OrderAbstract>>() {
            @Override
            public void onSuccess(ListData<OrderAbstract> list) {
                if (list.getPageNumber() == 1) {
                    view.setOrderList(list.getList());
                    if (mRefreshing && list.isListEmpty()) {
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
                LogUtils.e("获取历史成就列表出错：" + code + ", " + msg);
                view.setOrderList(null);
                mRefreshing = false;
                if (code == 400101) {
                    //该用户无历史成就
                } else {
                    toastError("获取历史成就失败", msg);
                    //  toast("");
                }
            }
        });
    }

    @Override
    public void refreshOrderList() {
        mCurrPage = 1;
        mRefreshing = true;
//        getAchievementList();
        loadCurrentMonthData();
    }

    private int currentYear;
    private int currentMonth;
    private boolean gotValidMonthData = false; //第一次获取到有效的数据
     /**
      * @method
      * @description 获取当前月份信息
      * @date: 2020/2/13 1:21
      * @author: tandongdong
      * @param
      * @return
      */
    public void loadCurrentMonthData() {
        //获取当前月份
        Calendar calendar = Calendar.getInstance();
        currentYear = calendar.get(Calendar.YEAR);
        currentMonth = calendar.get(Calendar.MONTH) + 1;

        gotValidMonthData = false;
        lastYearAndMonth = 0; //

        loadAchievementListByMonth();
    }

     /**
      * @method
      * @description 获取上一个月的数据
      * @date: 2020/2/13 1:21
      * @author: tandongdong
      * @param
      * @return
      */
    @Override
    public void loadMoreDataByMonth() {
        //计算上一个月
        currentMonth = currentMonth - 1;
        if (currentMonth < 1) {
            currentYear = currentYear - 1;
            currentMonth = 12;
        }

        loadAchievementListByMonth();
    }

    /**
     * @param
     * @return
     * @method
     * @description 分月获取历史成就
     * @date: 2020/2/12 10:18
     * @author: tandongdong
     */
    private void loadAchievementListByMonth() {
        repo.getAchievementListByMonth(currentYear, currentMonth, new ApiSubscriber<OrderAbstractByMonth>() {
            @Override
            public void onSuccess(OrderAbstractByMonth orderAbstractByMonth) {

                //是否需要展示月份信息
                for (int i = 0; i < orderAbstractByMonth.getList().size(); i++) {
                    String date = orderAbstractByMonth.getList().get(i).getDate();
                    int orderMonth = Integer.parseInt(date.substring(0, 4)) * 12 + Integer.parseInt(date.substring(5, 7));
                    if (orderMonth != lastYearAndMonth) {
                        //展示月份
                        orderAbstractByMonth.getList().get(i).setShowMonthView(true);
                        lastYearAndMonth = orderMonth;
                    }
                }

                if (!gotValidMonthData) {
                    view.setOrderList(orderAbstractByMonth.getList());
                } else {
                    view.addMoreOrders(orderAbstractByMonth.getList());
                }

                //第一次获取到有数据的月份。
                if (orderAbstractByMonth.getList().size() != 0) {
                    gotValidMonthData = true; //已获取到有数据的月份
                }

                //判断数据是否到头
                //最早数据的时间
                long date = orderAbstractByMonth.getEarliestAchievementDate();
                if(date <= 0){ //如果没有获取到最早的记录时间 //todo
                    view.noMoreOrders();
                    return;
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date(date * 1000));
                int earliestYear = calendar.get(Calendar.YEAR);
                int earliestMonth = calendar.get(Calendar.MONTH) + 1;
                Log.d(TAG, "earliestMonth:" + earliestMonth);
                if (earliestYear == currentYear && earliestMonth == currentMonth) {
                    //没有更多新数据了
                    view.noMoreOrders();
                } else {
                    if(!gotValidMonthData) { //没有获取到本月数据，则继续查询上一个月数据，直到查询到数据为止。
                        loadMoreDataByMonth(); //
                    }
                }
            }

            @Override
            public void onError(int code, String msg) {
/*                view.setOrderList(null);
                mRefreshing = false;*/
                if (code == 400101) {
                    //该用户无历史成就
                } else {
                    toastError("获取历史成就失败", msg);
                    //  toast("");
                }
            }
        });

    }


    @Override
    public void onViewDestroy() {
        super.onViewDestroy();
    }

}
