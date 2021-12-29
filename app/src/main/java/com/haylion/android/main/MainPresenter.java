package com.haylion.android.main;

import android.util.Log;

import com.haylion.android.Constants;
import com.haylion.android.R;
import com.haylion.android.data.bean.ShunfengWaitBean;
import com.haylion.android.data.model.AddressForSuggestLine;
import com.haylion.android.data.model.AmapTrack;
import com.haylion.android.data.model.Driver;
import com.haylion.android.data.model.LatestVersionBean;
import com.haylion.android.data.model.ListenOrderSetting;
import com.haylion.android.data.model.ListenStatus;
import com.haylion.android.data.model.MessageDetail;
import com.haylion.android.data.model.MessageDetailSimple;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.model.OrderAbstract;
import com.haylion.android.data.model.OrderDetail;
import com.haylion.android.data.model.OrderForCargoAndPassenger;
import com.haylion.android.data.model.OrderForMainActivity;
import com.haylion.android.data.model.OrderHistory;
import com.haylion.android.data.model.ServiceNumber;
import com.haylion.android.data.model.SwitchVehicleJudgeBean;
import com.haylion.android.data.repo.OrderRepository;
import com.haylion.android.data.repo.PrefserHelper;

import com.haylion.android.data.util.OrderConvert;
import com.haylion.android.data.util.TTSUtil;
import com.haylion.android.mvp.base.BasePresenter;
import com.haylion.android.mvp.base.ListData;
import com.haylion.android.mvp.rx.ApiSubscriber;
import com.haylion.android.mvp.util.LogUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainPresenter extends BasePresenter<MainContract.View, OrderRepository>
        implements MainContract.Presenter {
    private static final String TAG = "MainPresenter";
    private static int listenOrderStatus = 0; //0：未听单 1：听单中

    MainPresenter(MainContract.View view) {
        super(view, new OrderRepository());
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();

    }

    @Override
    public void changeListenOrderStatus(boolean showDialog) {
        int vehicleId;
        if (PrefserHelper.getVehicleList() != null && PrefserHelper.getVehicleList().size() != 0) {
            //
        } else {
            toast(R.string.toast_not_bind_vehicle);
            return;
        }
        if (PrefserHelper.getVehicleInfo() != null) {
            vehicleId = PrefserHelper.getVehicleInfo().getId();
        } else {
            toast(R.string.toast_please_select_vehicle);
            return;
        }

        Boolean isStartListenRequest = (listenOrderStatus == 0);
        repo.startListenOrder(isStartListenRequest, vehicleId, new ApiSubscriber<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                listenOrderStatus = listenOrderStatus ^ 1;
                view.changeOrderActionSuccess(listenOrderStatus, showDialog);
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.e("开始或者结束听单失败：" + code + ", " + msg);
                view.dismissProgressDialog();
                if (code == 400401) {
                    //司机已经在听单状态
                    listenOrderStatus = 1;
                    view.changeOrderActionSuccess(listenOrderStatus, showDialog);
                } else if (code == 400402) {
                    //司机没有在听单状态
                    listenOrderStatus = 0;
                    view.changeOrderActionSuccess(listenOrderStatus, showDialog);
                } else if (code == 400407) {
                    //其他司机正在使用该车辆
                    view.showDialog();
                } else if (code == 400112) {
                    //车辆已停用
                    toast(R.string.toast_vehicle_disabled);
                    view.showVehcileIsStoppedDialog("" + PrefserHelper.getVehicleInfo().getNumber());
                } else if (code == 400111) {
                    //司机和该车辆未绑定
                    toast(R.string.toast_vehicle_and_driver_account_not_match);
                } else {
                    toastError("", msg);
                }
            }
        });
    }

    @Override
    public void getListenOrderStatus() {
        repo.getListenOrderStatus(new ApiSubscriber<ListenStatus>() {
            @Override
            public void onSuccess(ListenStatus listenStatus) {
                LogUtils.d("getListenOrderStatus：");
                if (listenStatus.getStatus() == 1) { //听单中
                    listenOrderStatus = 1;
                } else { //未听单
                    listenOrderStatus = 0;
                }
                view.changeOrderActionSuccess(listenOrderStatus, false);
            }

            @Override
            public void onError(int code, String msg) {
                Log.e(TAG, "code:" + code);
            }
        });
    }

    @Override
    public void getListenOrderSetting() {
        repo.getOrderSetting(new ApiSubscriber<ListenOrderSetting>() {
            @Override
            public void onSuccess(ListenOrderSetting setting) {
                if (setting != null) {
                    if (setting.getVoiceBroadcast() == 1) {
                        PrefserHelper.setCache(PrefserHelper.KEY_ORDER_VOICE_ENABLE, "enable");
                    } else {
                        PrefserHelper.setCache(PrefserHelper.KEY_ORDER_VOICE_ENABLE, "disable");
                    }

                    if (setting.getReceiveOnly() == 1) {
                        view.updateListenOrderType(true);
                    } else {
                        view.updateListenOrderType(false);
                    }
                }
            }

            @Override
            public void onError(int code, String msg) {
                toastError("", msg);
            }
        });
    }


    @Override
    public void getTodayAchieve() {
        repo.getAchievementList(1, 0, new ApiSubscriber<ListData<OrderAbstract>>() {
            @Override
            public void onSuccess(ListData<OrderAbstract> orderAbstractListData) {
                LogUtils.d("getTodayAchievement：orderAbstractListData.size = " + orderAbstractListData.getList().size());
                view.dismissProgressDialog();
                if (orderAbstractListData.getList().size() != 0) {
                    view.showTodayAchieve(orderAbstractListData.getList().get(0));
                } else {
                    view.showTodayAchieve(null);
                }
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.e("获取今日成就失败：" + code + ", " + msg);
                view.dismissProgressDialog();
            }
        });

    }

    @Override
    public void getSuggestLine() {
        repo.getSuggestLine(new ApiSubscriber<List<AddressForSuggestLine>>() {
            @Override
            public void onSuccess(List<AddressForSuggestLine> addressForSuggestLineList) {
                view.showSuggestLine(addressForSuggestLineList);
                Log.d(TAG, "getSuggestLine, onSuccess");
            }

            @Override
            public void onError(int code, String msg) {
                Log.e(TAG, "getSuggestLine: " + code + ", " + msg);
                view.dismissProgressDialog();
//                toast("获取推荐线路失败：" + msg);
            }
        });
    }


    /**
     * 今天所有订单， 包含货拼客，其他客单
     */
    @Override
    public void getTodayOrderList() {
        repo.getToadyOrderAll(new ApiSubscriber<OrderForCargoAndPassenger>() {
            @Override
            public void onSuccess(OrderForCargoAndPassenger orderForCargoAndPassenger) {
                Log.d(TAG, "getToadyOrder, onSuccess");
                view.dismissProgressDialog();

                List<Order> cargoOrderList = new ArrayList<>();
                List<OrderForMainActivity> orderForMainActivityList;

                //货单(包含货拼客中的客单)
                orderForMainActivityList = orderForCargoAndPassenger.getCargoList();
                for (int i = 0; i < orderForMainActivityList.size(); i++) {
                    Order order = new Order();
                    order = OrderConvert.orderForMainActivityToOrder(orderForMainActivityList.get(i));
                    cargoOrderList.add(order);
                }

/*                //todo
                cargoOrderList = OrderConvert.mockCargoOrderListData();*/

                //客单
                boolean firstBookOrder = false;
                boolean firstRealTimeOrder = false;
                boolean firstRealTimeCarpoolOrder = false;
                boolean firstSendChildOrder = false;
                boolean firstAccessibilityOrder = false; // 第一个女性专车订单标记
                orderForMainActivityList = orderForCargoAndPassenger.getPassengerList();

                List<Order> orderList = new ArrayList<>();
                for (int i = 0; i < orderForMainActivityList.size(); i++) {

                    OrderForMainActivity orderForMainActivity = orderForMainActivityList.get(i);
                    Order order;
                    order = OrderConvert.orderForMainActivityToOrder(orderForMainActivity);
                    orderList.add(order);
                }

/*                //todo
                orderList = OrderConvert.mockOrderListData();*/

                for (int i = 0; i < orderList.size(); i++) {
                    Order order = orderList.get(i);

                    if (firstBookOrder == false && order.getOrderType() == Order.ORDER_TYPE_BOOK) {
                        firstBookOrder = true;
                        order.setOrderTypeheaderDisplay(true);
                    }

                    if (firstRealTimeOrder == false && order.getOrderType() == Order.ORDER_TYPE_REALTIME) {
                        firstRealTimeOrder = true;
                        order.setOrderTypeheaderDisplay(true);
                    }

                    if (firstRealTimeCarpoolOrder == false && order.getOrderType() == Order.ORDER_TYPE_REALTIME_CARPOOL) {
                        firstRealTimeCarpoolOrder = true;
                        order.setOrderTypeheaderDisplay(true);
                    }

                    if (firstSendChildOrder == false && order.getOrderType() == Order.ORDER_TYPE_SEND_CHILD) {
                        firstSendChildOrder = true;
                        order.setOrderTypeheaderDisplay(true);
                    }
                    if (!firstAccessibilityOrder && order.getOrderType() == Order.ORDER_TYPE_ACCESSIBILITY) {
                        firstAccessibilityOrder = true;
                        order.setOrderTypeheaderDisplay(true);
                    }
                }

                view.updateTodayOrderListDisplay(orderList, cargoOrderList);
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.e("获取今日订单失败：" + code + ", " + msg);
                view.dismissProgressDialog();
                toastError("", msg);
            }
        });
    }


    @Override
    public void getDriverInfo() {
        repo.getDriverInfo(new ApiSubscriber<Driver>() {
            @Override
            public void onSuccess(Driver driver) {
                if (driver != null) {
                    Log.d(TAG,"driver = " + driver.toString());
                    PrefserHelper.saveDriverInfo(driver);
                    view.onGetDriverInfoSuccess(driver);
                }
            }

            @Override
            public void onError(int code, String msg) {
                Log.e(TAG, msg);

            }
        });
    }

    @Override
    public void getAmapTrackArgs() {
        repo.getAmapGpsTrackArgs(new ApiSubscriber<AmapTrack>() {
            @Override
            public void onSuccess(AmapTrack amapTrack) {
                LogUtils.d(TAG, "猎鹰：" + amapTrack.toString());
                view.startAmapTrackService(amapTrack);
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.d(TAG, "猎鹰：" + "code:" + code + ",msg:" + code);
                Log.e(TAG, "code:" + code + ",msg:" + code);
            }
        });

    }


    @Override
    public void getOrderDetail(int orderId) {
        repo.getWorkOrderDetail(orderId, new ApiSubscriber<OrderDetail>() {
            @Override
            public void onSuccess(OrderDetail orderDetail) {
                Order order;
                order = OrderConvert.orderDetailToOrder(orderDetail);
                view.getOrderDetailSuccess(order);
            }

            @Override
            public void onError(int code, String msg) {
                view.getOrderDetailFail(msg);
                toastError("", msg);
            }
        });
    }

    @Override
    public void getOrderDetailFromNotify(int orderId) {
        repo.getWorkOrderDetail(orderId, new ApiSubscriber<OrderDetail>() {
            @Override
            public void onSuccess(OrderDetail orderDetail) {
                Log.d(TAG, "getOrderDetail success");
                Order order;
                order = OrderConvert.orderDetailToOrder(orderDetail);
                view.getOrderDetailFromNotifySuccess(order);
            }

            @Override
            public void onError(int code, String msg) {
                toastError("", msg);
            }
        });
    }

    @Override
    public void getServiceTelNumber(boolean showDialog) {
        repo.getSericeTelNumber(new ApiSubscriber<ServiceNumber>() {
            @Override
            public void onSuccess(ServiceNumber serviceNumber) {
                PrefserHelper.setCache(PrefserHelper.KEY_SERVICE_NUMBER, serviceNumber.getPhone());
                Constants.SERVICE_PHONE = serviceNumber.getPhone();
                if (showDialog) {
                    view.showDialDialog(serviceNumber.getPhone());
                }
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.e(TAG, "getServiceTelNumber:" + code + "," + msg);
            }
        });
    }


    @Override
    public void onViewDestroy() {
        super.onViewDestroy();
    }


    /**
     * 模拟建议行驶的顺序
     *
     * @return
     */
    public List<AddressForSuggestLine> mockeSuggestLine() {
        List<AddressForSuggestLine> addressForSuggestLineList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            AddressForSuggestLine address = new AddressForSuggestLine();
            address.setLat(Constants.Gps.YITIAN_LAT);
            address.setLon(Constants.Gps.YITIAN_LNG);
            address.setLocationName(Constants.Gps.YITIAN + "地址一二三四五六七八九十");
            address.setLocationDescription("地址描述----------------------------");
            addressForSuggestLineList.add(address);
        }
        return addressForSuggestLineList;
    }

    public void getMessageList() {

        repo.getMessageList(1, new ApiSubscriber<ListData<MessageDetail>>() {
            @Override
            public void onSuccess(ListData<MessageDetail> list) {
                if (list != null) {
                    view.showMessageStatus(list.getUnviewedCount());
                }
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.e("获取数据失败：" + code + ", " + msg);
            }
        });
    }

    @Override
    public void messageHasReaded(List<MessageDetail> list) {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setViewed(1);
        }
        updateMessage(list);
    }

    /**
     * 判断是否允许切换车辆
     *
     * @param currentOnlineVehicleId
     */
    @Override
    public void isSwitchVehicleAllowed(int currentOnlineVehicleId) {
        repo.isSwitchVehicleAllowed(currentOnlineVehicleId, new ApiSubscriber<SwitchVehicleJudgeBean>() {

            @Override
            public void onSuccess(SwitchVehicleJudgeBean bean) {
                view.isSwitchVehicleAllowedSuccess(bean);
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.e("询问是否允许更换听单车辆失败：" + code + ", " + msg);
                if (code == 400403) {
                    view.isSwitchVehicleAllowedFail();
                }
                toastError("询问是否允许更换听单车辆失败", msg);
            }
        });
    }

    @Override
    public void checkUpdates() {
        repo.checkUpdates(new ApiSubscriber<LatestVersionBean>() {

            @Override
            public void onSuccess(LatestVersionBean version) {
                /*if(version!=null && version.hasNewVersion()){
                    //普通更新，24小时内只显示一次
                    if(!version.isForceUpdate()){
                        String lastRejectTime = PrefserHelper.getCache(PrefserHelper.APK_UPDATE_LAST_REJECT_DATE);
                        long currentTime = System.currentTimeMillis();
                        if(!TextUtils.isEmpty(lastRejectTime)
                                && currentTime - Long.valueOf(lastRejectTime) < 1000 * 3600 * 24){
                            LogUtils.d("普通更新，24小时内只显示一次");
                            return;
                        }
                    }
                    view.checkUpdatesSuccess(version);
                }else{
                    LogUtils.d("没有新版本");
                }*/

                // 据需求 #1156149801001003678
                if (version != null && version.hasNewVersion()) {
                    view.checkUpdatesSuccess(version);
                } else {
                    LogUtils.d(TAG, "没有新版本");
                }
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.d("检查更新出错:" + code + "," + msg);
            }
        });
    }

    @Override
    public void getOrderList() {
        repo.getOrderList(true, true, 1, 1, new ApiSubscriber<ListData<OrderHistory>>() {
            @Override
            public void onSuccess(ListData<OrderHistory> list) {
                Order order;
                if (list.getList().size() > 0) {
                    order = OrderConvert.orderHistoryToOrder(list.getList().get(0));
                    if (order.getOrderStatus() == Order.ORDER_STATUS_WAIT_PAY) {
                        view.haveWaitPayOrder(true);
                    } else {
                        view.haveWaitPayOrder(false);
                    }
                }
            }

            @Override
            public void onError(int code, String msg) {
            }
        });

    }

    private void updateMessage(List<MessageDetail> messageDetailList) {
        Log.d(TAG, "updateMessage:" + messageDetailList.get(0).toString());
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
                Log.d(TAG, messageDetailList.get(0).toString());
                view.enterNewActivity(messageDetailList.get(0));
            }

            @Override
            public void onError(int code, String msg) {
                Log.d(TAG, "操作失败");
            }
        });
    }

    /**
     * 查询新的预约订单
     */
    @Override
    public void queryNewOrder() {
        repo.appointmentCenter(new ApiSubscriber<List<OrderForMainActivity>>() {
            @Override
            public void onSuccess(List<OrderForMainActivity> orders) {
                if (orders == null) {
                    childrenOrderCenter(new ArrayList<>());
                } else {
                    childrenOrderCenter(orders);
                }
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.e("轮询新预约订单出错：" + code + ", " + msg);
                childrenOrderCenter(new ArrayList<>());
            }
        });
    }

    @Override
    public void queryShunfengOrder() {
        Log.d("aaa","123");
        getShunfengWaitList();
    }

    /**
     * 查询送你上学订单
     */
    private void childrenOrderCenter(List<OrderForMainActivity> orders) {
        repo.childrenOrderCenter(new ApiSubscriber<List<OrderForMainActivity>>() {
            @Override
            public void onSuccess(List<OrderForMainActivity> orderForMainActivities) {
                orders.addAll(orderForMainActivities);
                queryAccessibilityOrder(orders);
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.e("轮询送你上学订单出错：" + code + ", " + msg);
                queryAccessibilityOrder(orders);
            }
        });
    }

    /**
     * 查询顺丰听单列表
     */
    public void getShunfengWaitList() {
        repo.getShunfengWaitList(new ApiSubscriber<List<ShunfengWaitBean>>() {
            @Override
            public void onSuccess(List<ShunfengWaitBean> data) {
                Log.d("aaa","456  data = " + data);
                if (data != null && data.size() > 0) {
                    Log.d("aaa","000  data.size = " + data.size());
                    List<Order> orders = new ArrayList<>();
                    for (ShunfengWaitBean bean : data) {
                        Order order = OrderConvert.shufengConvertToOrder(bean);
                        orders.add(order);
                    }
                    view.onShunfengOrders(orders);
                }
            }

            @Override
            public void onError(int code, String msg) {
                Log.d("aaa","789  msg = " + msg);
            }
        });
    }

    private void queryAccessibilityOrder(List<OrderForMainActivity> orders) {
        repo.accessibilityOrderCenter(new ApiSubscriber<List<OrderForMainActivity>>() {
            @Override
            public void onSuccess(List<OrderForMainActivity> orderForMainActivities) {
                orders.addAll(orderForMainActivities);
                handleNewOrders(orders);
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.e("轮询女性专车订单出错：" + code + ", " + msg);
                handleNewOrders(orders);
            }
        });
    }

    private String lastVoiceOrderTime = "1970-01-01 00:00";

    private void handleNewOrders(List<OrderForMainActivity> orders) {
        String newOrderTime = PrefserHelper.getNewOrderTime();
        LogUtils.d("本地新订单时间：" + newOrderTime);
        List<OrderForMainActivity> newOrders = new ArrayList<>(filterNewOrders(orders, newOrderTime));
        if (!newOrders.isEmpty()) {
            Collections.sort(newOrders, new Comparator<OrderForMainActivity>() {
                @Override
                public int compare(OrderForMainActivity o1, OrderForMainActivity o2) {
                    return o2.getCreatedTime().compareTo(o1.getCreatedTime()); // 时间倒序排列
                }
            });
            for (OrderForMainActivity order : newOrders) {
                LogUtils.d("新订单时间：" + order.getCreatedTime());
            }
            OrderForMainActivity firstNewOrder = newOrders.get(0);
            newOrderTime = firstNewOrder.getCreatedTime();
            if (newOrderTime.compareTo(lastVoiceOrderTime) > 0) { // 有更新的订单
                String voiceContent;
                if (firstNewOrder.getOrderType() == Order.ORDER_TYPE_SEND_CHILD) {
                    voiceContent = "有新的送你上学订单，请尽快到抢单池中抢单";
                } else if (firstNewOrder.getOrderType() == Order.ORDER_TYPE_ACCESSIBILITY) {
                    voiceContent = "有新的女性专车订单，请尽快到抢单池中抢单";
                } else {
                    voiceContent = "有新的预约单，请尽快到抢单池中抢单";
                }
                LogUtils.d("播报语音：" + voiceContent + ", 新的时间：" + lastVoiceOrderTime);
                TTSUtil.play(voiceContent);
                lastVoiceOrderTime = newOrderTime;
            } else {
                LogUtils.d("不播报语音，订单时间：" + newOrderTime + ", 上次播报：" + lastVoiceOrderTime);
            }
        } else {
            LogUtils.d("没有新订单");
        }
        view.onNewOrder(newOrders.size(), newOrderTime);
    }

    private List<OrderForMainActivity> filterNewOrders(List<OrderForMainActivity> orders, String baseOrderTime) {
        List<OrderForMainActivity> newOrders = new ArrayList<>();
        if (orders == null || orders.isEmpty()) {
            return newOrders;
        }
        for (OrderForMainActivity order : orders) {
            if (order.getCreatedTime().compareTo(baseOrderTime) > 0) { // 订单时间更靠后
                newOrders.add(order);
            }
        }
        return newOrders;
    }


}

