package com.haylion.android.notification;


import android.util.Log;

import com.haylion.android.R;
import com.haylion.android.data.model.CarpoolStatus;
import com.haylion.android.data.model.MessageDetail;
import com.haylion.android.data.model.MessageDetailSimple;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.model.OrderDetail;
import com.haylion.android.data.repo.OrderRepository;

import com.haylion.android.data.util.OrderConvert;
import com.haylion.android.mvp.base.BasePresenter;
import com.haylion.android.mvp.base.ListData;
import com.haylion.android.mvp.rx.ApiSubscriber;
import com.haylion.android.mvp.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class NotificationListPresenter extends BasePresenter<NotificationContract.View, OrderRepository>
        implements NotificationContract.Presenter {

    private static final String TAG = "NotiListPresenter";
    private int mCurrPage = 1;

    private boolean mRefreshing = false;

    NotificationListPresenter(NotificationContract.View view) {
        super(view, OrderRepository.INSTANCE);
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        getMessageList();
    }

    @Override
    public void loadMoreOrders() {
        mCurrPage++;
        getMessageList();
    }


    private void getMessageList() {

        repo.getMessageList(mCurrPage, new ApiSubscriber<ListData<MessageDetail>>() {
            @Override
            public void onSuccess(ListData<MessageDetail> list) {
                if (list.getPageNumber() == 1) {
                    view.setMessageList(list.getList(), list.getUnviewedCount());
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
                view.setMessageList(null, 0);
                mRefreshing = false;
                toast("获取数据失败：" + msg);
                LogUtils.e("获取数据失败：" + code + ", " + msg);
            }
        });
    }

    @Override
    public void refreshOrderList() {
        mCurrPage = 1;
        mRefreshing = true;
        getMessageList();
    }

    /**
     * @param
     * @return
     * @method
     * @description 上报消息已读
     * @date: 2019/12/12 13:10
     * @author: tandongdong
     */
    @Override
    public void messageHasReaded(List<MessageDetail> list) {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setViewed(1);
        }

        messageHasReaded(list, true);
    }

    /**
     * @param startActivity 是否需要开启新页面
     * @return
     * @method
     * @description 上报消息已读
     * @date: 2019/12/12 13:10
     * @author: tandongdong
     */
    public void messageHasReaded(List<MessageDetail> list, boolean startActivity) {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setViewed(1);
        }

        List<MessageDetailSimple> simpleList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            MessageDetailSimple simple = new MessageDetailSimple();
            MessageDetail messageDetail;
            messageDetail = list.get(i);
            simple.setMessageId(messageDetail.getMessageId());
            simple.setDisplay(messageDetail.getDisplay());
            simple.setViewed(messageDetail.getViewed());
            simple.setDeleted(messageDetail.getDeleted());
            simpleList.add(simple);
        }

        repo.updateMessageList(simpleList, new ApiSubscriber<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (startActivity) {
                    //跳转到新页面
                    view.enterNewActivity(list.get(0));
                } else {
                    getMessageList();
                }

            }

            @Override
            public void onError(int code, String msg) {
                Log.d(TAG, "操作失败");
            }
        });
    }

    @Override
    public void allMessageReaded() {
        repo.allMessageReaded(new ApiSubscriber<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                getMessageList();
            }

            @Override
            public void onError(int code, String msg) {
                Log.d(TAG, "code:" + code + msg);
            }
        });
    }

    /**
     * 获取订单详情
     *
     * @param detail 消息
     */
    @Override
    public void getOrderDetail(final MessageDetail detail) {
/*
        Order order = OrderConvert.mockOrderListData().get(5);
        view.getOrderDetailSuccess(order);*/

        repo.getWorkOrderDetail(detail.getOrderId(), new ApiSubscriber<OrderDetail>() {
            @Override
            public void onSuccess(OrderDetail orderDetail) {
                LogUtils.d(TAG, "getOrderDetail success");
                Order order = OrderConvert.orderDetailToOrder(orderDetail);
                order.setCarpoolOrder(CarpoolStatus.isCarpool(detail.getCarpoolStatus()));
                order.setCarpoolCode(detail.getCarpoolCode());
                view.getOrderDetailSuccess(order);
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.e(TAG, "getOrderDetail FAIL, " + msg);
                view.getOrderDetailFail(msg);
                toastError(R.string.toast_get_order_info_fail, msg);
            }
        });
    }

    /**
     * @param
     * @return
     * @method
     * @description 更新系统消息的状态，包含已读，已删除，已显示
     * @date: 2019/12/12 13:13
     * @author: tandongdong
     */
    @Override
    public void updateMessage(List<MessageDetail> messageDetailList) {

        List<MessageDetailSimple> list = new ArrayList<>();
        for (int i = 0; i < messageDetailList.size(); i++) {
            MessageDetailSimple simple = new MessageDetailSimple();
            MessageDetail messageDetail = new MessageDetail();
            messageDetail = messageDetailList.get(i);
            simple.setMessageId(messageDetail.getMessageId());
            simple.setDisplay(messageDetail.getDisplay());
            simple.setViewed(messageDetail.getViewed());
            simple.setDeleted(messageDetail.getDeleted());
            list.add(simple);
        }

        repo.updateMessageList(list, new ApiSubscriber<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                refreshOrderList();
            }

            @Override
            public void onError(int code, String msg) {
                Log.d(TAG, "操作失败");
            }
        });
    }

    /**
     * @param
     * @return
     * @method
     * @description 更新某一条消息的状态
     * @date: 2019/12/12 13:15
     * @author: tandongdong
     */
    @Override
    public void updateMessage(MessageDetail messageDetail) {
        List<MessageDetail> list = new ArrayList<MessageDetail>();
        list.add(messageDetail);
        updateMessage(list);
    }

    @Override
    public void onViewDestroy() {
        super.onViewDestroy();
    }

}
