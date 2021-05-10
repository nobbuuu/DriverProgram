package com.haylion.android.orderlist;


import android.util.Log;

import com.haylion.android.data.model.Order;
import com.haylion.android.data.model.OrderForCargoAndPassenger;
import com.haylion.android.data.model.OrderForMainActivity;
import com.haylion.android.data.model.OrderHistory;
import com.haylion.android.data.model.OrderTimeType;
import com.haylion.android.data.repo.OrderRepository;
import com.haylion.android.data.util.OrderConvert;
import com.haylion.android.mvp.base.BasePresenter;
import com.haylion.android.mvp.base.ListData;
import com.haylion.android.mvp.rx.ApiSubscriber;
import com.haylion.android.mvp.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class BookOrderListPresenter extends BasePresenter<BookOrderListContract.View, OrderRepository>
        implements BookOrderListContract.Presenter {

    private static final String TAG = "OrderListPresenter";
    private OrderTimeType mOrderTimeType;
    private int mCurrPage = 1;

    private boolean mRefreshing = false;
    private boolean showPassenger = true;

    BookOrderListPresenter(OrderTimeType orderTimeType, BookOrderListContract.View view) {
        super(view, OrderRepository.INSTANCE);
        this.mOrderTimeType = orderTimeType;
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        getAppointmentOrderList();
    }

    @Override
    public void loadMoreOrders() {
        mCurrPage++;
        getAppointmentOrderList();
    }

    private void getOrderList() {
/*        List<Order> list = mockOrderListData();
        view.setOrderList(list);*/

        int index = 0; //当天还是几个月的
        boolean isShowOnlyCompleted = false;
        if (mOrderTimeType == OrderTimeType.TODAY) {
            index = 0;
            isShowOnlyCompleted = true;
            showPassenger = true;
        } else {
            isShowOnlyCompleted = false;
            index = 3;
        }
        repo.getOrderList(isShowOnlyCompleted, showPassenger, index, mCurrPage, new ApiSubscriber<ListData<OrderHistory>>() {
            @Override
            public void onSuccess(ListData<OrderHistory> list) {
/*                for (Order order : list.getList()) {
                    order.setDialable(mOrderTimeType != OrderTimeType.HISTORY &&
                            !order.isCompleted() && !order.isCompleted()
                    );
                }*/
                boolean findFirstData = false;
                List<Order> orderList = new ArrayList<>();
                for (int i = 0; i < list.getList().size(); i++) {
                    Order order;
                    Log.d(TAG, "orderResponseList.get(), i=" + i + list.getList().get(i).getOffPlace());
                    order = OrderConvert.orderHistoryToOrder(list.getList().get(i));
                    //首页才展示header信息
                    if (list.getPageNumber() == 1) {
                        if (order.getOrderStatus() == Order.ORDER_STATUS_WAIT_PAY) {
                            if (i == 0) {
                                order.setHeaderNameDisplay("未支付订单");
                            }
                        } else {
                            if (findFirstData == false) {
                                order.setHeaderNameDisplay("已完成订单");
                                findFirstData = true;
                            }
                        }
                    }
                    orderList.add(order);
                }
                if (list.getPageNumber() == 1) {
                    view.setOrderList(orderList);
                    if (mRefreshing && list.isListEmpty()) {
                        mRefreshing = false;
                    }
                } else {
                    view.addMoreOrders(orderList);
                }
                if (list.getPageNumber() == list.getPageCount()) {
                    view.noMoreOrders();
                }
                mCurrPage = list.getPageNumber();
            }

            @Override
            public void onError(int code, String msg) {
                view.setOrderList(null);
                mRefreshing = false;
//                toast("获取订单出错：" + msg);
                LogUtils.e("获取订单列表出错：" + code + ", " + msg);
            }
        });
    }

    /**
     * 预约订单
     */
    @Override
    public void getAppointmentOrderList() {
        repo.getToadyOrderAll(new ApiSubscriber<OrderForCargoAndPassenger>() {
            @Override
            public void onSuccess(OrderForCargoAndPassenger orderForCargoAndPassenger) {
                Log.d(TAG, "getToadyOrder, onSuccess");
                view.dismissProgressDialog();

                List<Order> orderList = new ArrayList<>();
                List<OrderForMainActivity> orderForMainActivityList;

                //预约订单
//                orderForMainActivityList = orderForCargoAndPassenger.getAppointmentList();
//                for (int i = 0; i < orderForMainActivityList.size(); i++) {
//                    Order order;
//                    order = OrderConvert.orderForMainActivityToOrder(orderForMainActivityList.get(i));
//                    orderList.add(order);
//                }
                
//                orderList = OrderConvert.mockOrderListData();

                view.setOrderList(orderList);
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.e("获取预约订单失败：" + code + ", " + msg);
                view.setOrderList(null);
                mRefreshing = false;
//                toast("获取订单出错：" + msg);
            }
        });
    }


    @Override
    public void refreshOrderList() {
        mCurrPage = 1;
        mRefreshing = true;
        getAppointmentOrderList();
    }

    @Override
    public void setOrderType(boolean showPassenger) {
        this.showPassenger = showPassenger;
    }


    @Override
    public void onViewDestroy() {
        super.onViewDestroy();
    }

}
