package com.haylion.android.orderlist;

import android.util.Log;

import com.google.gson.Gson;
import com.haylion.android.Constants;
import com.haylion.android.data.bean.ClaimBean;
import com.haylion.android.data.bean.ClaimResult;
import com.haylion.android.data.event.AppointmentChangedEvent;
import com.haylion.android.data.model.AppointmentList;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.model.OrderForCargoAndPassenger;
import com.haylion.android.data.model.OrderForMainActivity;
import com.haylion.android.data.model.ShunfengBean;
import com.haylion.android.data.repo.OrderRepository;
import com.haylion.android.data.util.OrderConvert;
import com.haylion.android.mvp.base.BasePresenter;
import com.haylion.android.mvp.rx.ApiSubscriber;
import com.haylion.android.mvp.util.LogUtils;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import static com.haylion.android.data.model.Order.ORDER_TYPE_SHUNFENG;

public class AppointmentListPresenter extends BasePresenter<AppointmentListContract.View,
        OrderRepository> implements AppointmentListContract.Presenter {

    private List<Order> mUnfinishedOrders = null;
    private List<Order> mFinishedOrders = null;

    private List<Order> mHallOrders = null;
    private List<Order> mShunfengOrders = null;

    /**
     * 送小孩单
     */
    private List<Order> mChildrenOrders = null;

    /**
     * 女性专车订单
     */
    private List<Order> mAccessibilityOrders = null;

    AppointmentListPresenter(AppointmentListContract.View view) {
        super(view, OrderRepository.INSTANCE);
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
    }

    /**
     * 获取司机个人的预约单（包括 “已完成” 和 “未完成”）
     */
    @Override
    public void getAppointmentList() {
        if (mUnfinishedOrders == null && mFinishedOrders == null) {
            getAppointmentListInternal();
        } else {
            view.showAppointmentList(mUnfinishedOrders, mFinishedOrders);
        }
    }

    private void getAppointmentListInternal() {
        repo.getToadyOrderAll(new ApiSubscriber<OrderForCargoAndPassenger>() {
            @Override
            public void onSuccess(OrderForCargoAndPassenger orderForCargoAndPassenger) {
                AppointmentList appointmentList = orderForCargoAndPassenger.getAppointmentList();
                handleAppointmentList(appointmentList);
                view.showAppointmentList(mUnfinishedOrders, mFinishedOrders);
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.e("获取预约列表出错：" + code + ", " + msg);
                toast("获取预约订单失败");
                view.showAppointmentList(mUnfinishedOrders, mFinishedOrders);
            }
        });
    }

    private void handleAppointmentList(AppointmentList appointmentList) {
        mUnfinishedOrders = new ArrayList<>();
        mFinishedOrders = new ArrayList<>();
        if (appointmentList == null) {
            return;
        }
        List<OrderForMainActivity> unfinished = appointmentList.getUnfinishedList();
        if (unfinished != null) {
            for (int i = 0; i < unfinished.size(); i++) {
                Order order = OrderConvert.orderForMainActivityToOrder(unfinished.get(i));
                mUnfinishedOrders.add(order);
            }
        }
        List<OrderForMainActivity> finished = appointmentList.getFinishedList();
        if (finished != null) {
            for (int i = 0; i < finished.size(); i++) {
                Order order = OrderConvert.orderForMainActivityToOrder(finished.get(i));
                mFinishedOrders.add(order);
            }
        }
    }

    @Override
    protected boolean useEventBus() {
        return true;
    }

    @Subscribe
    public void onAppointmentChanged(AppointmentChangedEvent event) {
        if (event.isCanceled()) { // 订单被取消，重新回到订单池
            appointmentHallInternal();
        }
        getAppointmentListInternal();
    }

    @Override
    public void refreshAppointmentList() {
        getAppointmentListInternal();
    }

    @Override
    public void refreshShunfengList() {
        getShunfengOrder();
    }

    /**
     * 订单池 - 获取预约订单
     */
    @Override
    public void appointmentHall() {
        if (mHallOrders == null) {
            appointmentHallInternal();
        } else {
            view.showAppointmentHall(mHallOrders);
        }
    }

    private void appointmentHallInternal() {
        repo.appointmentCenter(new ApiSubscriber<List<OrderForMainActivity>>() {
            @Override
            public void onSuccess(List<OrderForMainActivity> orderForMainActivities) {
                mHallOrders = convertOrders(orderForMainActivities);
                view.showAppointmentHall(mHallOrders);
            }

            @Override
            public void onError(int code, String msg) {
                toast("获取预约订单失败");
                LogUtils.e("获取大厅订单出错：" + code + ", " + msg);
                view.showAppointmentHall(mHallOrders);
            }
        });
    }

    private List<Order> convertOrders(List<OrderForMainActivity> orderForMainActivities) {
        List<Order> orders = null;
        if (orderForMainActivities != null && !orderForMainActivities.isEmpty()) {
            orders = new ArrayList<>();
            for (int i = 0; i < orderForMainActivities.size(); i++) {
                Order order = OrderConvert.orderForMainActivityToOrder(orderForMainActivities.get(i));
                orders.add(order);
            }
        }
        return orders;
    }

    private List<Order> convertShunfengOrders(List<ShunfengBean> shunfengBeanList) {
        List<Order> orders = null;
        if (shunfengBeanList != null && !shunfengBeanList.isEmpty()) {
            orders = new ArrayList<>();
            for (int i = 0; i < shunfengBeanList.size(); i++) {
                Order order = OrderConvert.orderShunfengToOrder(shunfengBeanList.get(i));
                orders.add(order);
            }
        }
        return orders;
    }

    @Override
    public void refreshAppointmentHall() {
        appointmentHallInternal();
    }

    /**
     * 抢预约订单、送小孩单
     *
     * @param order 订单
     */
    @Override
    public void grabOrder(Order order,List<String> chosedDates) {
        int orderType = order.getOrderType();
        String orderCode = order.getOrderCode();
        Log.d("aaa","orderType = " + orderType);
        Log.d("aaa","orderCode = " + orderCode);
        if (orderType == Order.ORDER_TYPE_SEND_CHILD) {
            // 送小孩单，调用专门的抢单接口
            repo.grabChildrenOrder(order, new GrabOrderCallback(orderType));
        } else if (orderType == Order.ORDER_TYPE_ACCESSIBILITY) {
            repo.grabAccessibilityOrder(orderCode,
                    order.isParentOrder(), new GrabOrderCallback(orderType));
        } else if (orderType == ORDER_TYPE_SHUNFENG){
            ClaimBean claimBean = new ClaimBean(order.getOrderId(), chosedDates);
            String paramJson = new Gson().toJson(claimBean);
            Log.d("aaa","paramJson = " + paramJson);
            repo.grabShunfengOrder(claimBean, new ApiSubscriber<ClaimResult>() {
                @Override
                public void onSuccess(ClaimResult claimResult) {
                    if (claimResult.isClaimResult()){
                        Log.d("aaa","抢单成功~");
                        toast("抢单成功");
                    }else {
                        toast("订单已被抢走~");
                    }
                    getShunfengOrder();
                }

                @Override
                public void onError(int code, String msg) {
                    Log.d("aaa","抢单失败~  code = " + code + " msg = " + msg);
                }
            });
        }else {
            repo.grabAppointment(orderCode,
                    order.isParentOrder(), new GrabOrderCallback(orderType));
        }
    }

    /**
     * 抢单接口回调
     */
    private class GrabOrderCallback extends ApiSubscriber<Boolean> {

        private int orderType; // 送小孩单

        private GrabOrderCallback(int orderType) {
            this.orderType = orderType;
        }

        @Override
        public void onSuccess(Boolean aBoolean) {
            view.dismissGrabDialog();
            if (aBoolean != null && aBoolean) {
                toast("抢单成功");
                if (orderType == Order.ORDER_TYPE_SEND_CHILD) {
                    refreshChildrenOrderCenter();
                } else if (orderType == Order.ORDER_TYPE_ACCESSIBILITY) {
                    refreshAccessibilityOrder();
                } else {
                    refreshAppointmentHall();
                }
//                refreshAppointmentList(); // 司机个人订单标签已不再展示
            } else {
                toast("抢单失败");
            }
        }

        @Override
        public void onError(int code, String msg) {
            view.dismissGrabDialog();
            if (code == Constants.ErrorCode.ACCESSIBILITY_SERVICE_IS_DISABLED) {
                toast("未开通女性专车订单功能");
            } else {
                toast("抢单出错");
            }
            LogUtils.e("抢单出错：" + code + ", " + msg);
        }

    }

    @Override
    public void childrenOrderCenter() {
        if (mChildrenOrders == null) {
            childrenOrderCenterInternal();
        } else {
            view.showChildrenOrderCenter(mChildrenOrders);
        }
    }

    private void childrenOrderCenterInternal() {
        repo.childrenOrderCenter(new ApiSubscriber<List<OrderForMainActivity>>() {
            @Override
            public void onSuccess(List<OrderForMainActivity> orderForMainActivities) {
                mChildrenOrders = convertOrders(orderForMainActivities);
                view.showChildrenOrderCenter(mChildrenOrders);
            }

            @Override
            public void onError(int code, String msg) {
                toast("获取送小孩订单失败");
                LogUtils.e("获取送小孩订单出错：" + code + ", " + msg);
                view.showChildrenOrderCenter(mChildrenOrders);
            }
        });
    }

    @Override
    public void refreshChildrenOrderCenter() {
        childrenOrderCenterInternal();
    }

    @Override
    public void getAccessibilityOrder() {
        if (mAccessibilityOrders == null) {
            getAccessibilityOrderInternal();
        } else {
            view.showAccessibilityOrders(mAccessibilityOrders);
        }
    }

    @Override
    public void getShunfengOrder() {
        repo.shunfengOrder(new ApiSubscriber<List<ShunfengBean>>() {
            @Override
            public void onSuccess(List<ShunfengBean> data) {
                mShunfengOrders = convertShunfengOrders(data);
                view.showShunfengOrders(mShunfengOrders);
            }

            @Override
            public void onError(int code, String msg) {
                toast("获取顺丰订单失败");
                LogUtils.e("获取顺丰订单出错：" + code + ", " + msg);
                view.showShunfengOrders(mShunfengOrders);

            }
        });
    }

    private void getAccessibilityOrderInternal() {
        repo.accessibilityOrderCenter(new ApiSubscriber<List<OrderForMainActivity>>() {
            @Override
            public void onSuccess(List<OrderForMainActivity> orderForMainActivities) {
                mAccessibilityOrders = convertOrders(orderForMainActivities);
                view.showAccessibilityOrders(mAccessibilityOrders);
            }

            @Override
            public void onError(int code, String msg) {
                toast("获取女性专车订单失败");
                LogUtils.e("获取女性专车订单出错：" + code + ", " + msg);
                view.showAccessibilityOrders(mAccessibilityOrders);
            }
        });
    }

    @Override
    public void refreshAccessibilityOrder() {
        getAccessibilityOrderInternal();
    }

    @Override
    public void onViewDestroy() {
        super.onViewDestroy();

    }

}
