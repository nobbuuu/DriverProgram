package com.haylion.android.main;

import com.haylion.android.data.bean.ShunfengWaitBean;
import com.haylion.android.data.model.AddressForSuggestLine;
import com.haylion.android.data.model.AmapTrack;
import com.haylion.android.data.model.Driver;
import com.haylion.android.data.model.LatestVersionBean;
import com.haylion.android.data.model.MessageDetail;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.model.OrderAbstract;
import com.haylion.android.data.model.SwitchVehicleJudgeBean;
import com.haylion.android.mvp.base.AbstractPresenter;
import com.haylion.android.mvp.base.AbstractView;

import java.util.List;

public class MainContract {

    interface View extends AbstractView {
        void showTodayAchieve(OrderAbstract orderAbstract);

        void updateTodayOrderListDisplay(List<Order> passengerList, List<Order> cargoList);

        void updateTodayCargoOrderListDisplay(List<Order> list);

        void changeOrderActionSuccess(int status, boolean showDialog);

        void updateListenOrderType(boolean backhomeOrder);

        void showSuggestLine(List<AddressForSuggestLine> suggestLine);

        void onGetDriverInfoSuccess(Driver driver);

        void showDialog();

        void showVehcileIsStoppedDialog(String vehicleNumber);

        void startAmapTrackService(AmapTrack amapTrack);

        void getOrderDetailSuccess(Order orderInfo);

        void getOrderDetailFail(String msg);

        void showDialDialog(String phoneNum);

        void showMessageStatus(int unviewedCount);

        void enterNewActivity(MessageDetail messageDetail);

        void getOrderDetailFromNotifySuccess(Order orderInfo);

        void isSwitchVehicleAllowedSuccess(SwitchVehicleJudgeBean bean);

        void isSwitchVehicleAllowedFail();

        void checkUpdatesSuccess(LatestVersionBean version);

        void haveWaitPayOrder(boolean have);

        void onNewOrder(int newOrderCount, String latestOrderTime);

        void onShunfengOrders(List<Order> list);
    }

    interface Presenter extends AbstractPresenter {
        void changeListenOrderStatus(boolean showDialog); //修改听单状态

        void getListenOrderStatus(); //获取听单状态

        void getTodayAchieve(); //今日订单概况

        void getSuggestLine(); //获取建议线路

        void getTodayOrderList(); //今日订单详情

        void getDriverInfo();

        void getAmapTrackArgs(); //获取轨迹上传的参数

        void getOrderDetail(int orderId); //获取订单详情

        void getOrderDetailFromNotify(int orderId); //获取订单详情,来源通知

        void getServiceTelNumber(boolean showDialog); //获取客服电话

        void getListenOrderSetting(); //获取听单设置

        void getMessageList();

        void messageHasReaded(List<MessageDetail> messageDetails);

        void isSwitchVehicleAllowed(int currentOnlineVehicleId); //是否允许更换听单车辆

        void checkUpdates();

        void getOrderList();

        void queryNewOrder();

        void queryShunfengOrder();
    }

}


