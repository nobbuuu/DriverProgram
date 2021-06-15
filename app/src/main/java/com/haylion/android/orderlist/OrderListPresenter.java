package com.haylion.android.orderlist;


import android.util.Log;

import com.haylion.android.data.model.HistoryOrderBean;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.model.OrderHistory;
import com.haylion.android.data.model.OrderTimeType;
import com.haylion.android.data.repo.OrderRepository;
import com.haylion.android.data.util.OrderConvert;
import com.haylion.android.mvp.base.BasePresenter;
import com.haylion.android.mvp.base.BaseResponse;
import com.haylion.android.mvp.base.ListData;
import com.haylion.android.mvp.rx.ApiSubscriber;
import com.haylion.android.mvp.util.LogUtils;
import com.haylion.android.mvp.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

public class OrderListPresenter extends BasePresenter<OrderListContract.View, OrderRepository>
        implements OrderListContract.Presenter {

    private static final String TAG = "OrderListPresenter";
    private OrderTimeType mOrderTimeType;
    private int mCurrPage = 1;

    private boolean mRefreshing = false;
    private boolean showPassenger = true;

    OrderListPresenter(OrderTimeType orderTimeType, OrderListContract.View view) {
        super(view, OrderRepository.INSTANCE);
        this.mOrderTimeType = orderTimeType;
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        getOrderList();
    }

    @Override
    public void loadMoreOrders() {
        mCurrPage++;
        getOrderList();
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

    public void getShunfengOrderList() {
        Log.d("aaa","getShunfengOrderList.........");
        repo.getHistoryOrder(false, 3, mCurrPage, new ApiSubscriber<HistoryOrderBean>() {
            @Override
            public void onSuccess(HistoryOrderBean list) {
                List<HistoryOrderBean.ListBean> dataList = list.getList();
                Log.d("aaa","dataList.size() = " + dataList.size());
                List<Order> orderList = new ArrayList<>();
                for (int i = 0; i < dataList.size(); i++) {
                    Order order;
                    order = OrderConvert.orderHistoryToOrder(dataList.get(i));
                    orderList.add(order);
                }
                Log.d("aaa","orderList.size() = " + orderList.size());
                if (list.getCurrent() == 1) {
                    view.setOrderList(orderList);
                } else {
                    view.addMoreOrders(orderList);
                }
                if (list.getCurrent() == list.getPageCount()) {
                    view.noMoreOrders();
                }
                mCurrPage = list.getCurrent();
            }

            @Override
            public void onError(int code, String msg) {
                Log.d("aaa","获取顺丰订单失败");
            }
        });
    }


    @Override
    public void refreshOrderList() {
        mCurrPage = 1;
        mRefreshing = true;
        getOrderList();
    }

    @Override
    public void setOrderType(boolean showPassenger) {
        this.showPassenger = showPassenger;
    }

    @Override
    public void getShunfengOrders() {
        getShunfengOrderList();
    }


    @Override
    public void onViewDestroy() {
        super.onViewDestroy();
    }

}
