package com.haylion.android.data.util;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.haylion.android.Constants;
import com.haylion.android.R;
import com.haylion.android.data.bean.OrderDetailBean;
import com.haylion.android.data.bean.ShunfengWaitBean;
import com.haylion.android.data.model.AddressInfo;
import com.haylion.android.data.model.CarpoolStatus;
import com.haylion.android.data.model.ChildInfo;
import com.haylion.android.data.model.HistoryOrderBean;
import com.haylion.android.data.model.NewOrder;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.model.OrderCostInfo;
import com.haylion.android.data.model.OrderDateSimple;
import com.haylion.android.data.model.OrderDetail;
import com.haylion.android.data.model.OrderDetailExt;
import com.haylion.android.data.model.OrderExt;
import com.haylion.android.data.model.OrderForMainActivity;
import com.haylion.android.data.model.OrderHistory;
import com.haylion.android.data.model.ShunfengBean;
import com.haylion.android.data.model.UserInfo;
import com.haylion.android.utils.AmapUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 将各个接口返回的数据类型转换成通用的订单类型。
 */

public class OrderConvert {
    private static final String TAG = "OrderConvert";

    /**
     * 主页订单数据转换成
     *
     * @param orderForMainActivity
     * @return
     */
    public static Order orderForMainActivityToOrder(OrderForMainActivity orderForMainActivity) {
        Order order = new Order();

        order.setOrderId(orderForMainActivity.getOrderId());
        order.setOrderCode(orderForMainActivity.getOrderCode());
        //起点和终点
        AddressInfo start = new AddressInfo();
        start.setLatLng(new LatLng(orderForMainActivity.getOnLat(), orderForMainActivity.getOnLng()));
        start.setName(orderForMainActivity.getOnLocation());
        start.setAddressDetail(orderForMainActivity.getOnLocationDescription());

        AddressInfo end = new AddressInfo();
        end.setLatLng(new LatLng(orderForMainActivity.getOffLat(), orderForMainActivity.getOffLng()));
        end.setName(orderForMainActivity.getOffLocation());
        end.setAddressDetail(orderForMainActivity.getOffLocationDescription());
        end.setLatLng(new LatLng(orderForMainActivity.getOffLat(), orderForMainActivity.getOffLng()));

        order.setStartAddr(start);
        order.setEndAddr(end);

        //用户信息
        UserInfo userInfo = new UserInfo();
        userInfo.setPhoneNum(orderForMainActivity.getMobile());
        order.setUserInfo(userInfo);
        order.setPassengerNum(orderForMainActivity.getCarpoolNum());

        //订单类型和时间
        order.setOrderType(orderForMainActivity.getOrderType());
        order.setOrderStatus(orderForMainActivity.getOrderStatus());
        order.setOrderSubStatus(orderForMainActivity.getOrderSubStatus());
        order.setOrderTime(orderForMainActivity.getTime());
        order.setEstimateArriveTime(orderForMainActivity.getEstimateArriveTime());

        //费用
        order.setTotalMoney(orderForMainActivity.getAmount());

        //送小孩的信息
        List<ChildInfo> childInfoList = new ArrayList<>();
        if (orderForMainActivity.getChildNameList() != null) {
            for (int i = 0; i < orderForMainActivity.getChildNameList().size(); i++) {
                ChildInfo childInfo = new ChildInfo();
                childInfo.setName(orderForMainActivity.getChildNameList().get(i));
                childInfoList.add(childInfo);
            }
        }
        order.setChildList(childInfoList);
        // 2020.8.25，拼车单、拼车码（迭代29）
        order.setCarpoolOrder(CarpoolStatus.isCarpool(orderForMainActivity.getCarpoolStatus()));
        order.setCarpoolCode(orderForMainActivity.getCarpoolCode());

        //附言
        order.setParentMessage(orderForMainActivity.getParentMessage());

        //多日订单
        order.setParentOrder(orderForMainActivity.isParentOrder());
        Log.d(TAG, "orderForMainActivity.isParentOrder():" + orderForMainActivity.isParentOrder()
                + ",isParentOrder:" + order.isParentOrder());
        order.setStartTime(orderForMainActivity.getStartTime());
        order.setEndTime(orderForMainActivity.getEndTime());

        //预估行程距离
        order.setDistance(orderForMainActivity.getTotalDistance());

        // 订单日期
        order.setOrderDates(orderForMainActivity.getOrderDates());

        return order;
    }

    /**
     * 顺丰订单数据转换成order
     *
     * @param shunfengOrder
     * @return
     */
    public static Order orderShunfengToOrder(ShunfengBean shunfengOrder) {
        Order order = new Order();

        order.setOrderId(shunfengOrder.getId());
//        order.setOrderCode(shunfengOrder.getOrderNo());
        //起点和终点
        AddressInfo start = new AddressInfo();
        start.setLatLng(new LatLng(shunfengOrder.getDepotStartLatitude(), shunfengOrder.getDepotStartLongitude()));
        start.setName(shunfengOrder.getDepotStartAddress());
        start.setAddressDetail(shunfengOrder.getDepotStartAddress());

        AddressInfo end = new AddressInfo();
        end.setLatLng(new LatLng(shunfengOrder.getDepotEndLatitude(), shunfengOrder.getDepotEndLongitude()));
        end.setName(shunfengOrder.getDepotEndAddress());
        end.setAddressDetail(shunfengOrder.getDepotEndAddress());
        end.setLatLng(new LatLng(shunfengOrder.getDepotEndLatitude(),
                shunfengOrder.getDepotEndLongitude()));

        order.setStartAddr(start);
        order.setEndAddr(end);
        order.setStartTime(shunfengOrder.getStartDate());
        order.setEndTime(shunfengOrder.getEndDate());

        //用户信息
        UserInfo userInfo = new UserInfo();
        userInfo.setPhoneNum(shunfengOrder.getDepotStartPhone1());
        order.setUserInfo(userInfo);
//        order.setPassengerNum(shunfengOrder.getCarpoolNum());

        //订单类型和时间
        order.setOrderType(shunfengOrder.getOrderType());
        order.setOrderStatus(shunfengOrder.getDriverStatus());
//        order.setOrderSubStatus(shunfengOrder.getOrderSubStatus());
//        order.setEstimateArriveTime(shunfengOrder.getEstimateArriveTime());
        order.setOrderTime(shunfengOrder.getTakeTime());
        order.setEstimateArriveTime(shunfengOrder.getDeliveryTime());//订单送达时间

        //费用
        order.setTotalMoney(shunfengOrder.getPrice());
        //送小孩的信息
        /*List<ChildInfo> childInfoList = new ArrayList<>();
        if (shunfengOrder.getChildNameList() != null) {
            for (int i = 0; i < shunfengOrder.getChildNameList().size(); i++) {
                ChildInfo childInfo = new ChildInfo();
                childInfo.setName(shunfengOrder.getChildNameList().get(i));
                childInfoList.add(childInfo);
            }
        }*/
//        order.setChildList(childInfoList);
        // 2020.8.25，拼车单、拼车码（迭代29）
//        order.setCarpoolOrder(CarpoolStatus.isCarpool(shunfengOrder.getCarpoolStatus()));
//        order.setCarpoolCode(shunfengOrder.getCarpoolCode());

        //附言
//        order.setParentMessage(shunfengOrder.getParentMessage());

        //多日订单
        /*order.setParentOrder(shunfengOrder.isParentOrder());
        Log.d(TAG, "orderForMainActivity.isParentOrder():" + shunfengOrder.isParentOrder()
                + ",isParentOrder:" + order.isParentOrder());
        order.setStartTime(shunfengOrder.getStartDate());*/
        order.setEndTime(shunfengOrder.getEndDate());

        // 订单日期
        List<String> dates = new ArrayList<>();
        for (ShunfengBean.GrabDateListBean bean : shunfengOrder.getGrabDateList()){
            dates.add(bean.getGrabDate());
        }
        order.setOrderDates(dates);

        return order;
    }


    /**
     * 订单详情数据转换成order
     *
     * @param orderDetail
     * @return
     */
    public static Order orderDetailToOrder(OrderDetail orderDetail) {
        Log.d(TAG, "orderDetail:" + orderDetail.toString());
        Order order = new Order();
        order.setOrderId(orderDetail.getOrderId());
        order.setOrderCode(orderDetail.getOrderCode());
        order.setCargoOrderId(orderDetail.getCargoOrderId());

        //订单类型和时间
        order.setOrderType(orderDetail.getOrderType());
        order.setChannel(orderDetail.getChannel());
        order.setOrderStatus(orderDetail.getOrderStatus());
        order.setOrderSubStatus(orderDetail.getOrderSubStatus());
        order.setStartTime(orderDetail.getStartTime());
        order.setEndTime(orderDetail.getEndTime());

        //起点和终点
        AddressInfo start = new AddressInfo();
        start.setLatLng(new LatLng(orderDetail.getPassengerOnLat(), orderDetail.getPassengerOnLon()));
        start.setName(orderDetail.getOnLocation());
        start.setAddressDetail(orderDetail.getOnLocationDescription());

        order.setDistance(orderDetail.getTotalDistance());

        AddressInfo end = new AddressInfo();
        end.setLatLng(new LatLng(orderDetail.getPassengerOffLat(), orderDetail.getPassengerOffLon()));
        end.setName(orderDetail.getOffLocation());
        end.setAddressDetail(orderDetail.getOffLocationDescription());

        order.setStartAddr(start);
        order.setEndAddr(end);

        //用户信息
        UserInfo userInfo = new UserInfo();
        userInfo.setPhoneNum(orderDetail.getMobile());
        userInfo.setVirtualNum(orderDetail.getVirtualMobile());
        order.setUserInfo(userInfo);
        order.setPassengerNum(orderDetail.getPassengerNum());

        //货物 收发货人信息
        order.setPickupContactMobile(orderDetail.getPickupContactMobile());
        order.setDropOffContactMobile(orderDetail.getDropOffContactMobile());

        //订单耗时
        order.setTotalTime(orderDetail.getOrderDuration());

        //司机预计到达上车点时间
        order.setEstimatePickUpTime(orderDetail.getEstimatePickUpTime());
        //司机实际到达上车点时间
        order.setBoardingPlaceArriveTime(orderDetail.getBoardingPlaceArriveTime());

        //最晚达到时间
        order.setEstimateArriveTime(orderDetail.getEstimateArriveTime());

        if (orderDetail.getOrderType() == Order.ORDER_TYPE_REALTIME
                || orderDetail.getOrderType() == Order.ORDER_TYPE_REALTIME_CARPOOL
                || orderDetail.getOrderType() == Order.ORDER_TYPE_CARGO
                || orderDetail.getOrderType() == Order.ORDER_TYPE_CARGO_PASSENGER) {
            order.setOrderTime(orderDetail.getCreatedTime());
        } else {
            order.setOrderTime(orderDetail.getAppointmentTime());
        }

        //费用信息
        OrderCostInfo costInfo = new OrderCostInfo();
        costInfo.setTotalCost(orderDetail.getAmount());
        costInfo.setBaseCost(orderDetail.getRegularAmount());
        costInfo.setServiceCost(orderDetail.getServiceAmount());
        costInfo.setOtherCost(orderDetail.getOtherAmount());
        order.setCostDetail(costInfo);

        //货物描述
        order.setGoodsDescription(orderDetail.getCargoDescription());

        //送小孩的专有信息
        order.setChildList(orderDetail.getChildList());
        order.setGuardianList(orderDetail.getGuardianList());
        order.setParentMessage(orderDetail.getParentMessage());
        order.setGetOnAutoCheck(orderDetail.getOnAutoCheck());
        order.setGetOffAutoCheck(orderDetail.getOffAutoCheck());

        //联系电话//todo

        order.setGetOnPicUrl(orderDetail.getOnPic());
        order.setGetOffPicUrl(orderDetail.getOffPic());
        order.setOnPicTime(orderDetail.getOnPicTime());
        order.setOffPicTime(orderDetail.getOffPicTime());

        //取消原因
        order.setOrderCancellerType(orderDetail.getOrderCancellerType());
        order.setOrderCancellerMsg(orderDetail.getOrderCancellerMsg());

        order.setTotalMoney(orderDetail.getAmount());

        //订单行驶的轨迹
        if (orderDetail.getTrackCoordinateList() != null && orderDetail.getTrackCoordinateList().size() >= 10) {
            List<LatLng> list = new ArrayList<>();
            for (int i = 0; i < orderDetail.getTrackCoordinateList().size(); i++) {
                list.add(new LatLng(orderDetail.getTrackCoordinateList().get(i).getLat(),
                        orderDetail.getTrackCoordinateList().get(i).getLon()));
                order.setTrackCoordinateList(list);
            }
        } else {
            order.setTrackCoordinateList(null);
        }

        Log.d(TAG, "order: " + order.getStartAddr().toString());

        // 未支付取消费状态 2-1 ，状态置为 - 已取消
        if (order.getOrderStatus() == Order.ORDER_STATUS_WAIT_PAY && order.getOrderSubStatus() == Order.ORDER_SUB_STATUS_WAIT_PAY_CANCAL_FEE) {
            order.setOrderStatus(Order.ORDER_STATUS_CANCELED);
        }
        return order;
    }
    /**
     * 订单详情数据转换成order
     *
     * @param orderDetail
     * @return
     */
    public static Order orderShunfengDetailToOrder(OrderDetailBean orderDetail) {
        Order order = new Order();
        order.setOrderId(orderDetail.getId());
        order.setOrderCode(orderDetail.getOrderCode());
//        order.setCargoOrderId(orderDetail.getCargoOrderId());

        //订单类型和时间
        order.setOrderType(-1);
//        order.setChannel(orderDetail.getChannel());
        order.setOrderStatus(orderDetail.getDriverStatus());
//        order.setOrderSubStatus(orderDetail.getOrderSubStatus());
        order.setOrderTime(orderDetail.getTakeTime());

        //起点和终点
        AddressInfo start = new AddressInfo();
        start.setLatLng(new LatLng(orderDetail.getDepotStartLatitude(), orderDetail.getDepotStartLongitude()));
        start.setName(orderDetail.getDepotStartAddress());
//        start.setAddressDetail(orderDetail.getOnLocationDescription());

//        order.setDistance(orderDetail.getTotalDistance());

        AddressInfo end = new AddressInfo();
        end.setLatLng(new LatLng(orderDetail.getDepotEndLatitude(), orderDetail.getDepotEndLongitude()));
        end.setName(orderDetail.getDepotEndAddress());
//        end.setAddressDetail(orderDetail.getOffLocationDescription());

        order.setStartAddr(start);
        order.setEndAddr(end);
        order.setStartTime(orderDetail.getStartDate());
        order.setEndTime(orderDetail.getEndDate());

        //用户信息
        UserInfo userInfo = new UserInfo();
        userInfo.setPhoneNum(orderDetail.getDepotStartPhone1());
        userInfo.setPhoneNum2(orderDetail.getDepotStartPhone2());
        userInfo.setPhoneNum3(orderDetail.getDepotStartPhone3());
        order.setUserInfo(userInfo);
//        order.setPassengerNum(orderDetail.getPassengerNum());

        //货物 收发货人信息
//        order.setPickupContactMobile(orderDetail.getPickupContactMobile());
//        order.setDropOffContactMobile(orderDetail.getDropOffContactMobile());
        order.setPickupCode(orderDetail.getPickupCode());

        //订单耗时
//        order.setTotalTime(orderDetail.getOrderDuration());

        //司机预计到达上车点时间
        order.setEstimatePickUpTime(orderDetail.getTakeTime());
        //司机实际到达上车点时间
        order.setBoardingPlaceArriveTime(orderDetail.getTakeTime());

        //最晚达到时间
        order.setEstimateArriveTime(orderDetail.getDeliveryTime());

       /* if (orderDetail.getOrderType() == Order.ORDER_TYPE_REALTIME
                || orderDetail.getOrderType() == Order.ORDER_TYPE_REALTIME_CARPOOL
                || orderDetail.getOrderType() == Order.ORDER_TYPE_CARGO
                || orderDetail.getOrderType() == Order.ORDER_TYPE_CARGO_PASSENGER) {
            order.setOrderTime(orderDetail.getCreatedTime());
        } else {
            order.setOrderTime(orderDetail.getAppointmentTime());
        }*/

        //费用信息
        OrderCostInfo costInfo = new OrderCostInfo();
//        costInfo.setTotalCost(orderDetail.getAmount());
//        costInfo.setBaseCost(orderDetail.getRegularAmount());
//        costInfo.setServiceCost(orderDetail.getServiceAmount());
//        costInfo.setOtherCost(orderDetail.getOtherAmount());
        order.setCostDetail(costInfo);

        //货物描述
//        order.setGoodsDescription(orderDetail.getCargoDescription());

        //送小孩的专有信息
//        order.setChildList(orderDetail.getChildList());
//        order.setGuardianList(orderDetail.getGuardianList());
//        order.setParentMessage(orderDetail.getParentMessage());
//        order.setGetOnAutoCheck(orderDetail.getOnAutoCheck());
//        order.setGetOffAutoCheck(orderDetail.getOffAutoCheck());

        //联系电话//todo

        order.setPickupContactMobile(orderDetail.getDepotStartPhone1());
        order.setPickupContactMobile1(orderDetail.getDepotStartPhone2());
        order.setPickupContactMobile2(orderDetail.getDepotStartPhone3());
        order.setPickupContactName(orderDetail.getDepotStartConcat1());
        order.setPickupContactName1(orderDetail.getDepotStartConcat2());
        order.setPickupContactName2(orderDetail.getDepotStartConcat3());
//        order.setGetOnPicUrl(orderDetail.getOnPic());
//        order.setGetOffPicUrl(orderDetail.getOffPic());
//        order.setOnPicTime(orderDetail.getOnPicTime());
//        order.setOffPicTime(orderDetail.getOffPicTime());

        //取消原因
//        order.setOrderCancellerType(orderDetail.getOrderCancellerType());
//        order.setOrderCancellerMsg(orderDetail.getOrderCancellerMsg());

        order.setTotalMoney(orderDetail.getPrice());

        //订单行驶的轨迹
       /* if (orderDetail.getTrackCoordinateList() != null && orderDetail.getTrackCoordinateList().size() >= 10) {
            List<LatLng> list = new ArrayList<>();
            for (int i = 0; i < orderDetail.getTrackCoordinateList().size(); i++) {
                list.add(new LatLng(orderDetail.getTrackCoordinateList().get(i).getLat(),
                        orderDetail.getTrackCoordinateList().get(i).getLon()));
                order.setTrackCoordinateList(list);
            }
        } else {
            order.setTrackCoordinateList(null);
        }*/

        // 未支付取消费状态 2-1 ，状态置为 - 已取消
        if (order.getOrderStatus() == Order.ORDER_STATUS_WAIT_PAY && order.getOrderSubStatus() == Order.ORDER_SUB_STATUS_WAIT_PAY_CANCAL_FEE) {
            order.setOrderStatus(Order.ORDER_STATUS_CANCELED);
        }
        return order;
    }

    /**
     * 订单详情数据转换成order
     *
     * @param orderDetail
     * @return
     */
    public static OrderExt orderDetailExtToOrderExt(OrderDetailExt orderDetail) {
        Log.d(TAG, "OrderDetailExt:" + orderDetail.toString());
        OrderExt order = new OrderExt();
        order.setOrderId(orderDetail.getOrderId());
        order.setOrderCode(orderDetail.getOrderCode());
        order.setCargoOrderId(orderDetail.getCargoOrderId());

        //订单类型和时间
        order.setOrderType(orderDetail.getOrderType());
        order.setChannel(orderDetail.getChannel());
        order.setOrderStatus(orderDetail.getOrderStatus());
        order.setOrderSubStatus(orderDetail.getOrderSubStatus());

        //起点和终点
        AddressInfo start = new AddressInfo();
        start.setLatLng(new LatLng(orderDetail.getPassengerOnLat(), orderDetail.getPassengerOnLon()));
        start.setName(orderDetail.getOnLocation());
        start.setAddressDetail(orderDetail.getOnLocationDescription());

        AddressInfo end = new AddressInfo();
        end.setLatLng(new LatLng(orderDetail.getPassengerOffLat(), orderDetail.getPassengerOffLon()));
        end.setName(orderDetail.getOffLocation());
        end.setAddressDetail(orderDetail.getOffLocationDescription());

        order.setStartAddr(start);
        order.setEndAddr(end);

        //用户信息
        UserInfo userInfo = new UserInfo();
        userInfo.setPhoneNum(orderDetail.getMobile());
        userInfo.setVirtualNum(orderDetail.getVirtualMobile());
        order.setUserInfo(userInfo);
        order.setPassengerNum(orderDetail.getPassengerNum());

        //货物 收发货人信息
        order.setPickupContactMobile(orderDetail.getPickupContactMobile());
        order.setDropOffContactMobile(orderDetail.getDropOffContactMobile());

        //订单耗时
        order.setTotalTime(orderDetail.getOrderDuration());

        //预计到达上车点时间
        order.setEstimatePickUpTime(orderDetail.getEstimatePickUpTime());
        //最晚达到时间
        order.setEstimateArriveTime(orderDetail.getEstimateArriveTime());

        if (orderDetail.getOrderType() == Order.ORDER_TYPE_REALTIME
                || orderDetail.getOrderType() == Order.ORDER_TYPE_REALTIME_CARPOOL
                || orderDetail.getOrderType() == Order.ORDER_TYPE_CARGO
                || orderDetail.getOrderType() == Order.ORDER_TYPE_CARGO_PASSENGER) {
            order.setOrderTime(orderDetail.getCreatedTime());
        } else {
            order.setOrderTime(orderDetail.getAppointmentTime());
        }

        //费用信息
        OrderCostInfo costInfo = new OrderCostInfo();
        costInfo.setTotalCost(orderDetail.getAmount());
        costInfo.setBaseCost(orderDetail.getRegularAmount());
        costInfo.setServiceCost(orderDetail.getServiceAmount());
        costInfo.setOtherCost(orderDetail.getOtherAmount());
        order.setCostDetail(costInfo);

        //货物描述
        order.setGoodsDescription(orderDetail.getCargoDescription());

        //送小孩的专有信息
        order.setChildList(orderDetail.getChildList());
        order.setGuardianList(orderDetail.getGuardianList());
        order.setParentMessage(orderDetail.getParentMessage());
        order.setGetOnAutoCheck(orderDetail.getOnAutoCheck());
        order.setGetOffAutoCheck(orderDetail.getOffAutoCheck());

        //联系电话//todo

        order.setGetOnPicUrl(orderDetail.getOnPic());
        order.setGetOffPicUrl(orderDetail.getOffPic());

        //取消原因
        order.setOrderCancellerType(orderDetail.getOrderCancellerType());
        order.setOrderCancellerMsg(orderDetail.getOrderCancellerMsg());

        order.setTotalMoney(orderDetail.getAmount());

        //订单行驶的轨迹
        if (orderDetail.getTrackCoordinateList() != null && orderDetail.getTrackCoordinateList().size() >= 10) {
            List<LatLng> list = new ArrayList<>();
            for (int i = 0; i < orderDetail.getTrackCoordinateList().size(); i++) {
                list.add(new LatLng(orderDetail.getTrackCoordinateList().get(i).getLat(),
                        orderDetail.getTrackCoordinateList().get(i).getLon()));
                order.setTrackCoordinateList(list);
            }
        } else {
            order.setTrackCoordinateList(null);
        }

        //多日订单中的其他信息
        if (orderDetail.getTravelDateOutputFormList() != null) {
/*
            //todooooooooooooooooooooooooooo
            List<OrderDateSimple> list = new ArrayList<>();
            for(int i = 0; i < 31; i++){
                list.add(orderDetail.getTravelDateOutputFormList().get(i % 2));
            }
            order.setTravelDateOutputFormList(list);*/

            order.setTravelDateOutputFormList(orderDetail.getTravelDateOutputFormList());
        }
        order.setStartTime(orderDetail.getStartTime());
        order.setEndTime(orderDetail.getEndTime());

        // 未支付取消费状态 2-1 ，状态置为 - 已取消
        if (order.getOrderStatus() == Order.ORDER_STATUS_WAIT_PAY && order.getOrderSubStatus() == Order.ORDER_SUB_STATUS_WAIT_PAY_CANCAL_FEE) {
            order.setOrderStatus(Order.ORDER_STATUS_CANCELED);
        }
        return order;
    }

    /**
     * 新订单数据类型转换成订单类型
     *
     * @param newOrder
     * @return
     */
    public static Order newOrderConvertToOrder(NewOrder newOrder) {
        Log.d(TAG, "newOrderConvertToOrder:" + newOrder.toString());

        Order order = new Order();
        order.setOrderId(newOrder.getId());
        //起点和终点
        AddressInfo start = new AddressInfo();
        start.setLatLng(new LatLng(newOrder.getBoardingLatitude(), newOrder.getBoardingLongitude()));
        start.setName(newOrder.getBoardingPlace());
        start.setAddressDetail(newOrder.getBoardingAddress());

        AddressInfo end = new AddressInfo();
        end.setLatLng(new LatLng(newOrder.getArrivalLatitude(), newOrder.getArrivalLongitude()));
        end.setName(newOrder.getArrivalPlace());
        end.setAddressDetail(newOrder.getArrivalAddress());

        order.setStartAddr(start);
        order.setEndAddr(end);

        //用户信息
        UserInfo userInfo = new UserInfo();
        userInfo.setPhoneNum("18612345678");
        order.setUserInfo(userInfo);

        order.setPassengerNum(newOrder.getCarpoolNumber());

        //订单类型和时间
        order.setOrderType(newOrder.getOrderType());
        order.setChannel(newOrder.getChannel());
        order.setOrderTime(newOrder.getStartTime());
        order.setDistance(newOrder.getTotalDistance());

        //货单，预计剩余时间
        order.setMinPreservedSeconds(newOrder.getMinPreservedSeconds());

        //价格
        order.setTotalMoney(newOrder.getOrderAmount());

        //推荐指数
        order.setRoadLevel(newOrder.getRoadLevel());
        order.setAmountLevel(newOrder.getAmountLevel());
        order.setStarLevel(newOrder.getStarLevel());

/*        //for test
        order.setOrderType(Order.ORDER_TYPE_CARGO_PASSENGER);
        order.setRoadLevel(3);
        order.setAmountLevel(3);
        order.setStarLevel(3);*/


        return order;
    }

    /**
     * 顺丰订单数据类型转换成订单类型
     *
     * @param newOrder
     * @return
     */
    public static Order shufengConvertToOrder(ShunfengWaitBean newOrder) {
        Log.d(TAG, "newOrderConvertToOrder:" + newOrder.toString());

        Order order = new Order();
        order.setOrderId(newOrder.getId());
        //起点和终点
        AddressInfo start = new AddressInfo();
        start.setLatLng(new LatLng(newOrder.getDepotStartLatitude(), newOrder.getDepotStartLongitude()));
        start.setName(newOrder.getDepotStartAddress());
//        start.setAddressDetail(newOrder.getBoardingAddress());

        AddressInfo end = new AddressInfo();
        end.setLatLng(new LatLng(newOrder.getDepotEndLatitude(), newOrder.getDepotEndLongitude()));
        end.setName(newOrder.getDepotEndAddress());
//        end.setAddressDetail(newOrder.getArrivalAddress());

        order.setStartAddr(start);
        order.setEndAddr(end);
        order.setStartTime(newOrder.getStartDate());
        order.setEndTime(newOrder.getEndDate());

        //用户信息
//        UserInfo userInfo = new UserInfo();
//        userInfo.setPhoneNum("18612345678");
//        order.setUserInfo(userInfo);
//
//        order.setPassengerNum(newOrder.getCarpoolNumber());

        //订单类型和时间
        order.setOrderType(newOrder.getOrderType());
//        order.setChannel(newOrder.getChannel());
        order.setEstimateArriveTime(newOrder.getDeliveryTime());
        order.setOrderTime(newOrder.getTakeTime());
//        order.setDistance(newOrder.getTotalDistance());

        //货单，预计剩余时间
//        order.setMinPreservedSeconds(newOrder.getMinPreservedSeconds());

        //价格
        order.setTotalMoney(newOrder.getPrice());

        //推荐指数
//        order.setRoadLevel(newOrder.getRoadLevel());
//        order.setAmountLevel(newOrder.getAmountLevel());
//        order.setStarLevel(newOrder.getStarLevel());

/*        //for test
        order.setOrderType(Order.ORDER_TYPE_CARGO_PASSENGER);
        order.setRoadLevel(3);
        order.setAmountLevel(3);
        order.setStarLevel(3);*/


        return order;
    }

    /**
     * 历史订单接口返回的数据类型 转换成 订单类型
     *
     * @param orderHistory
     * @return
     */
    public static Order orderHistoryToOrder(OrderHistory orderHistory) {
        Order order = new Order();
        order.setOrderId(orderHistory.getOrderId());
        order.setOrderCode(orderHistory.getOrderCode());

        //起点和终点
        AddressInfo start = new AddressInfo();
        start.setName(orderHistory.getOnPlace());
        start.setAddressDetail(orderHistory.getOnPlaceDescription());

        AddressInfo end = new AddressInfo();
        end.setName(orderHistory.getOffPlace());
        end.setAddressDetail(orderHistory.getOffPlaceDescription());

        order.setStartAddr(start);
        order.setEndAddr(end);

        //用户信息
        UserInfo userInfo = new UserInfo();
        userInfo.setPhoneNum(orderHistory.getMobile());
        order.setUserInfo(userInfo);
        order.setPassengerNum(orderHistory.getCarpoolNumber());

        //订单状态
        order.setOrderStatus(orderHistory.getOrderStatus());
        order.setOrderSubStatus(orderHistory.getOrderSubStatus());

        //订单类型和时间
        order.setOrderType(orderHistory.getOrderType());
        order.setChannel(orderHistory.getChannel());
        if (orderHistory.getOrderType() == Order.ORDER_TYPE_BOOK
                || orderHistory.getOrderType() == Order.ORDER_TYPE_SEND_CHILD) {
            order.setOrderTime(orderHistory.getAppointmentDate());
        } else {
            order.setOrderTime(orderHistory.getCreatedDate());
        }
        order.setTotalTime(orderHistory.getTripDuration());

        order.setTotalMoney(orderHistory.getOrderAmount());

        //取消原因
        order.setOrderCancellerType(orderHistory.getOrderCancellerType());
        order.setOrderCancellerMsg(orderHistory.getOrderCancellerMessage());

        //货物信息
        order.setPickupContactMobile(orderHistory.getPickupContactMobile());
        order.setDropOffContactMobile(orderHistory.getDropOffContactMobile());
        order.setGoodsDescription(orderHistory.getGoodsDescription());

        //顺丰ID
        order.setSfOrderId(orderHistory.getSfOrderId());

        //小时信息
        //送小孩的信息
        List<ChildInfo> childInfoList = new ArrayList<>();
        if (orderHistory.getChildNameList() != null) {
            for (int i = 0; i < orderHistory.getChildNameList().size(); i++) {
                ChildInfo childInfo = new ChildInfo();
                childInfo.setName(orderHistory.getChildNameList().get(i));
                childInfoList.add(childInfo);
            }
        }
        order.setChildList(childInfoList);
        order.setParentMessage(orderHistory.getParentMessage());

        Log.d(TAG, "order: " + order.getStartAddr().toString());

        // 未支付取消费状态 2-1 ，当作已取消处理
        if (order.getOrderStatus() == Order.ORDER_STATUS_WAIT_PAY && order.getOrderSubStatus() == Order.ORDER_SUB_STATUS_WAIT_PAY_CANCAL_FEE) {
            order.setOrderStatus(Order.ORDER_STATUS_CANCELED);
        }

        return order;
    }

    /**
     * 历史订单接口返回的数据类型 转换成 订单类型
     *
     * @param orderHistory
     * @return
     */
    public static Order orderHistoryToOrder(HistoryOrderBean.ListBean orderHistory) {
        Order order = new Order();
        order.setOrderId(orderHistory.getId());
        order.setOrderCode(orderHistory.getOrderNo());

        //起点和终点
        AddressInfo start = new AddressInfo();
        start.setName(orderHistory.getDepotStartAddress());
//        start.setAddressDetail(orderHistory.getOnPlaceDescription());

        AddressInfo end = new AddressInfo();
        end.setName(orderHistory.getDepotEndAddress());
//        end.setAddressDetail(orderHistory.getOffPlaceDescription());

        order.setStartAddr(start);
        order.setEndAddr(end);

        //用户信息
        UserInfo userInfo = new UserInfo();
//        userInfo.setPhoneNum(orderHistory.get());
        order.setUserInfo(userInfo);
//        order.setPassengerNum(orderHistory.getCarpoolNumber());

        //订单状态
        order.setOrderStatus(orderHistory.getDriverStatus());
//        order.setOrderSubStatus(orderHistory.getOrderSubStatus());

        //订单类型和时间
        order.setOrderType(-1);
//        order.setChannel(orderHistory.getChannel());
        if (orderHistory.getOrderType() == Order.ORDER_TYPE_BOOK
                || orderHistory.getOrderType() == Order.ORDER_TYPE_SEND_CHILD) {
            order.setOrderTime(orderHistory.getEndDate());
        } else {
            order.setOrderTime(orderHistory.getTakeTime());
        }
//        order.setTotalTime(orderHistory.get());

        order.setTotalMoney(orderHistory.getPrice());

        //取消原因
//        order.setOrderCancellerType(orderHistory.getOrderCancellerType());
//        order.setOrderCancellerMsg(orderHistory.getOrderCancellerMessage());

        //货物信息
//        order.setPickupContactMobile(orderHistory.getPickupContactMobile());
//        order.setDropOffContactMobile(orderHistory.getDropOffContactMobile());
//        order.setGoodsDescription(orderHistory.getGoodsDescription());

        //顺丰ID
//        order.setSfOrderId(orderHistory.getSfOrderId());

        //小时信息
        //送小孩的信息
        /*List<ChildInfo> childInfoList = new ArrayList<>();
        if (orderHistory.getChildNameList() != null) {
            for (int i = 0; i < orderHistory.getChildNameList().size(); i++) {
                ChildInfo childInfo = new ChildInfo();
                childInfo.setName(orderHistory.getChildNameList().get(i));
                childInfoList.add(childInfo);
            }
        }
        order.setChildList(childInfoList);
        order.setParentMessage(orderHistory.getParentMessage());*/

        Log.d(TAG, "order: " + order.getStartAddr().toString());

        // 未支付取消费状态 2-1 ，当作已取消处理
        if (order.getOrderStatus() == Order.ORDER_STATUS_WAIT_PAY && order.getOrderSubStatus() == Order.ORDER_SUB_STATUS_WAIT_PAY_CANCAL_FEE) {
            order.setOrderStatus(Order.ORDER_STATUS_CANCELED);
        }

        return order;
    }

    /**
     * 测试时模拟数据
     *
     * @return
     */
    public static List<Order> mockOrderListData() {

        List<Order> orderList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            Order order = new Order();
            order.setOrderId(100 + i);
            //起点和终点
            AddressInfo start = new AddressInfo();
            start.setLatLng(new LatLng(Constants.Gps.YITIAN_LAT, Constants.Gps.YITIAN_LNG));
            start.setName(Constants.Gps.YITIAN + "地址一二三四五六七八九十");
            start.setAddressDetail("地址描述信息地址描述信息地址描述信息地址描述信息地址描述信息地");

            AddressInfo end = new AddressInfo();
            end.setLatLng(new LatLng(Constants.Gps.SHENZHENWAN_LAT, Constants.Gps.SHENZHENWAN_LNG));
            end.setName(Constants.Gps.SHENZHENWAN + "地址一二三四五六七八九十");
            end.setAddressDetail("地址描述信息地址描述信息地址描述信息地址描述信息地址描述信息");

            order.setStartAddr(start);
            order.setEndAddr(end);

            //用户信息
            UserInfo userInfo = new UserInfo();
            userInfo.setPhoneNum("18612345678");
            order.setUserInfo(userInfo);
            order.setPassengerNum(2);
            //订单类型和时间
            order.setOrderType(Order.ORDER_TYPE_BOOK);
            order.setOrderStatus(Order.ORDER_STATUS_WAIT_CAR);
            order.setOrderTime("2019-12-01 10:00");
            order.setEstimateArriveTime("2019-09-04 15:00");
            order.setTotalTime(3671);

            order.setTotalMoney(50);

            //订单状态
            Log.d(TAG, "order: " + order.getStartAddr().toString());
            orderList.add(order);
        }

        orderList.get(0).setOrderType(Order.ORDER_TYPE_REALTIME_CARPOOL);
        orderList.get(1).setOrderType(Order.ORDER_TYPE_REALTIME_CARPOOL);
        orderList.get(2).setOrderType(Order.ORDER_TYPE_REALTIME_CARPOOL);
        orderList.get(3).setOrderType(Order.ORDER_TYPE_REALTIME);
        orderList.get(4).setOrderType(Order.ORDER_TYPE_REALTIME);
        orderList.get(5).setOrderType(Order.ORDER_TYPE_SEND_CHILD);
        orderList.get(6).setOrderType(Order.ORDER_TYPE_SEND_CHILD);

        orderList.get(5).setOrderTime("2019-09-17 15:00");
        orderList.get(6).setOrderTime("2019-09-20 15:00");

        //起点和终点
        AddressInfo start2 = new AddressInfo();
        start2.setLatLng(new LatLng(Constants.Gps.SHENZHENWAN_LAT, Constants.Gps.SHENZHENWAN_LNG));
        start2.setName(Constants.Gps.SHENZHENWAN);
        start2.setAddressDetail("深圳市滨海大道001号");

        AddressInfo end2 = new AddressInfo();
        end2.setLatLng(new LatLng(Constants.Gps.NANKEDA_LAT, Constants.Gps.NANKEDA_LNG));
        end2.setName(Constants.Gps.NANKEDA);
        end2.setAddressDetail("深圳市西丽塘朗地铁站附近");

        orderList.get(1).setStartAddr(start2);
        orderList.get(1).setEndAddr(end2);

        //订单5，小孩单
        List<ChildInfo> list = new ArrayList<>();
        ChildInfo childInfo = new ChildInfo();
        childInfo.setName("桑弘羊");
        childInfo.setMobile("18677885566");
        list.add(childInfo);
        childInfo = new ChildInfo();
        childInfo.setName("伊稚斜");
        childInfo.setMobile("18677885544");
        list.add(childInfo);
        childInfo = new ChildInfo();
        childInfo.setName("伊稚斜");
        childInfo.setMobile("18677885544");
        list.add(childInfo);
        childInfo = new ChildInfo();
        childInfo.setName("伊稚斜");
        childInfo.setMobile("18677885544");
        list.add(childInfo);
        orderList.get(5).setOrderSubStatus(Order.ORDER_SUB_STATUS_GET_OFF_WAIT_PARENT_CONFIRM);
        orderList.get(5).setOrderStatus(Order.ORDER_STATUS_GET_OFF);
        orderList.get(5).setChildList(list);
        orderList.get(6).setChildList(list);
        orderList.get(5).setParentMessage("请确保小孩进入学校");
        orderList.get(5).setGetOnPicUrl("https://timgsa.baidu.com/timg?image&quality=80&size=" +
                "b9999_10000&sec=1566802533&di=8a59944240d6b04fad784abcd7949dc7&imgtype=jpg&er=1&src=http%3A%2F%2Fr.bstatic.com%2Fimages%2Fhotel%2Fmax1024x768%2F987%2F98767654.jpg");
        orderList.get(5).setGetOffPicUrl("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1566802533&di=8a59944240d6b04fad784abcd7949dc7" +
                "&imgtype=jpg&er=1&src=http%3A%2F%2Fr.bstatic.com%2Fimages%2Fhotel%2Fmax1024x768%2F987%2F98767654.jpg");

        orderList.get(0).setOrderTime("2018-12-28 15:00");
        orderList.get(1).setOrderTime("2019-12-28 15:00");
        orderList.get(5).setOrderTime("2018-10-22 15:00");
        orderList.get(6).setOrderTime("2019-10-20 15:00");

        return orderList;
    }


    /**
     * 模拟多日订单
     *
     * @return
     */
    public static List<OrderExt> mockOrderExtData() {

        List<OrderExt> orderList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            OrderExt order = new OrderExt();
            order.setOrderId(100 + i);
            //起点和终点
            AddressInfo start = new AddressInfo();
            start.setLatLng(new LatLng(Constants.Gps.YITIAN_LAT, Constants.Gps.YITIAN_LNG));
            start.setName(Constants.Gps.YITIAN + "地址一二三四五六七八九十");
            start.setAddressDetail("地址描述信息地址描述信息地址描述信息地址描述信息地址描述信息地");

            AddressInfo end = new AddressInfo();
            end.setLatLng(new LatLng(Constants.Gps.SHENZHENWAN_LAT, Constants.Gps.SHENZHENWAN_LNG));
            end.setName(Constants.Gps.SHENZHENWAN + "地址一二三四五六七八九十");
            end.setAddressDetail("地址描述信息地址描述信息地址描述信息地址描述信息地址描述信息");

            order.setStartAddr(start);
            order.setEndAddr(end);

            //用户信息
            UserInfo userInfo = new UserInfo();
            userInfo.setPhoneNum("18612345678");
            order.setUserInfo(userInfo);
            order.setPassengerNum(2);
            //订单类型和时间
            order.setOrderType(Order.ORDER_TYPE_BOOK);
            order.setOrderStatus(Order.ORDER_STATUS_WAIT_CAR);
            order.setOrderTime("2019-12-01 10:00");
            order.setEstimateArriveTime("2019-09-04 15:00");
            order.setTotalTime(3671);

            order.setTotalMoney(50);

            //多日订单
            order.setStartTime("2019-12-02 18:00");
            order.setEndTime("2019-12-22 18:00");
            List<OrderDateSimple> dateSimpleList = new ArrayList<>();
            for (int j = 0; j < 2; j++) {
                OrderDateSimple orderDateSimple = new OrderDateSimple();
                orderDateSimple.setDate("2019-12-01 18:00");
                orderDateSimple.setOrderId(1000);
                orderDateSimple.setStatus(Order.ORDER_STATUS_WAIT_PAY);
                orderDateSimple.setSubStatus(0);
                dateSimpleList.add(orderDateSimple);
            }
            order.setTravelDateOutputFormList(dateSimpleList);

            //订单状态
            Log.d(TAG, "order: " + order.getStartAddr().toString());
            orderList.add(order);
        }

        orderList.get(0).setOrderType(Order.ORDER_TYPE_REALTIME_CARPOOL);
        orderList.get(1).setOrderType(Order.ORDER_TYPE_REALTIME_CARPOOL);
        orderList.get(2).setOrderType(Order.ORDER_TYPE_REALTIME_CARPOOL);
        orderList.get(3).setOrderType(Order.ORDER_TYPE_REALTIME);
        orderList.get(4).setOrderType(Order.ORDER_TYPE_REALTIME);
        orderList.get(5).setOrderType(Order.ORDER_TYPE_SEND_CHILD);
        orderList.get(6).setOrderType(Order.ORDER_TYPE_SEND_CHILD);

        orderList.get(5).setOrderTime("2019-09-17 15:00");
        orderList.get(6).setOrderTime("2019-09-20 15:00");

        //起点和终点
        AddressInfo start2 = new AddressInfo();
        start2.setLatLng(new LatLng(Constants.Gps.SHENZHENWAN_LAT, Constants.Gps.SHENZHENWAN_LNG));
        start2.setName(Constants.Gps.SHENZHENWAN);
        start2.setAddressDetail("深圳市滨海大道001号");

        AddressInfo end2 = new AddressInfo();
        end2.setLatLng(new LatLng(Constants.Gps.NANKEDA_LAT, Constants.Gps.NANKEDA_LNG));
        end2.setName(Constants.Gps.NANKEDA);
        end2.setAddressDetail("深圳市西丽塘朗地铁站附近");

        orderList.get(1).setStartAddr(start2);
        orderList.get(1).setEndAddr(end2);

        //订单5，小孩单
        List<ChildInfo> list = new ArrayList<>();
        ChildInfo childInfo = new ChildInfo();
        childInfo.setName("桑弘羊");
        childInfo.setMobile("18677885566");
        list.add(childInfo);
        childInfo = new ChildInfo();
        childInfo.setName("伊稚斜");
        childInfo.setMobile("18677885544");
        list.add(childInfo);
        childInfo = new ChildInfo();
        childInfo.setName("伊稚斜");
        childInfo.setMobile("18677885544");
        list.add(childInfo);
        childInfo = new ChildInfo();
        childInfo.setName("伊稚斜");
        childInfo.setMobile("18677885544");
        list.add(childInfo);
        orderList.get(5).setOrderSubStatus(Order.ORDER_SUB_STATUS_GET_OFF_WAIT_PARENT_CONFIRM);
        orderList.get(5).setOrderStatus(Order.ORDER_STATUS_GET_OFF);
        orderList.get(5).setChildList(list);
        orderList.get(6).setChildList(list);
        orderList.get(5).setParentMessage("请确保小孩进入学校");
        orderList.get(5).setGetOnPicUrl("https://timgsa.baidu.com/timg?image&quality=80&size=" +
                "b9999_10000&sec=1566802533&di=8a59944240d6b04fad784abcd7949dc7&imgtype=jpg&er=1&src=http%3A%2F%2Fr.bstatic.com%2Fimages%2Fhotel%2Fmax1024x768%2F987%2F98767654.jpg");
        orderList.get(5).setGetOffPicUrl("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1566802533&di=8a59944240d6b04fad784abcd7949dc7" +
                "&imgtype=jpg&er=1&src=http%3A%2F%2Fr.bstatic.com%2Fimages%2Fhotel%2Fmax1024x768%2F987%2F98767654.jpg");

        orderList.get(0).setOrderTime("2018-12-28 15:00");
        orderList.get(1).setOrderTime("2019-12-28 15:00");
        orderList.get(5).setOrderTime("2018-10-22 15:00");
        orderList.get(6).setOrderTime("2019-10-20 15:00");


        return orderList;
    }

    /**
     * 测试时模拟数据
     *
     * @return
     */
    public static List<Order> mockCargoOrderListData() {

        List<Order> orderList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Order order = new Order();
            order.setOrderId(100 + i);
            //起点和终点
            AddressInfo start = new AddressInfo();
            start.setLatLng(new LatLng(Constants.Gps.YITIAN_LAT, Constants.Gps.YITIAN_LNG));
            start.setName(Constants.Gps.YITIAN + "地址一二三四五六七八九十");
            start.setAddressDetail("地址描述信息地址描述信息地址描述信息地址描述信息地址描述信息地");

            AddressInfo end = new AddressInfo();
            end.setLatLng(new LatLng(Constants.Gps.SHENZHENWAN_LAT, Constants.Gps.SHENZHENWAN_LNG));
            end.setName(Constants.Gps.SHENZHENWAN + "地址一二三四五六七八九十");
            end.setAddressDetail("地址描述信息地址描述信息地址描述信息地址描述信息地址描述信息");

            order.setStartAddr(start);
            order.setEndAddr(end);

            //用户信息
            UserInfo userInfo = new UserInfo();
            userInfo.setPhoneNum("18612345678");
            order.setUserInfo(userInfo);
            order.setPassengerNum(2);
            //订单类型和时间
            order.setOrderType(Order.ORDER_TYPE_CARGO);
            order.setOrderStatus(Order.ORDER_STATUS_INIT);
            order.setOrderTime("2019-12-01 10:00");
            order.setEstimateArriveTime("2019-09-04 15:00");

            order.setTotalMoney(50);

            //订单状态
            order.setOrderStatus(Order.ORDER_STATUS_WAIT_CAR); //未上车
            Log.d(TAG, "order: " + order.getStartAddr().toString());
            orderList.add(order);
        }

        orderList.get(0).setOrderType(Order.ORDER_TYPE_CARGO);
/*        orderList.get(1).setOrderType(Order.ORDER_TYPE_CARGO_PASSENGER);
        orderList.get(2).setOrderType(Order.ORDER_TYPE_CARGO_PASSENGER);*/

        //起点和终点
        AddressInfo start2 = new AddressInfo();
        start2.setLatLng(new LatLng(Constants.Gps.SHENZHENWAN_LAT, Constants.Gps.SHENZHENWAN_LNG));
        start2.setName(Constants.Gps.SHENZHENWAN);
        start2.setAddressDetail("深圳市滨海大道001号");

        AddressInfo end2 = new AddressInfo();
        end2.setLatLng(new LatLng(Constants.Gps.NANKEDA_LAT, Constants.Gps.NANKEDA_LNG));
        end2.setName(Constants.Gps.NANKEDA);
        end2.setAddressDetail("深圳市西丽塘朗地铁站附近");

        orderList.get(1).setStartAddr(start2);
        orderList.get(1).setEndAddr(end2);
        orderList.get(1).setOrderStatus(Order.ORDER_STATUS_GET_OFF);

        return orderList;
    }

    /**
     * 小孩名字用、分割
     *
     * @param order
     * @return
     */
    public static String getChildNames(Order order) {
        String names = "";
        if (order.getChildList() != null) {
            for (int i = 0; i < order.getChildList().size(); i++) {
                names = names + order.getChildList().get(i).getName();
                if (i < order.getChildList().size() - 1) {
                    if (order.getChildList().size() == 1) {

                    } else {
                        names = names + "、";
                    }
                }
            }
        }
        //陪乘人名字
        if (order.getGuardianList() != null) {
            for (int i = 0; i < order.getGuardianList().size(); i++) {
                names = names + "、" + order.getGuardianList().get(i).getName();
            }
        }
        return names;
    }

    /**
     * 获取送你上学 - 乘客头像
     *
     * @param order
     * @return
     */
    public static List<String> getChildPhotos(Order order) {
        List<String> mPhotoList = new ArrayList<>();
        //小孩头像
        if (order.getChildList() != null && order.getChildList().size() > 0) {
            for (ChildInfo childInfo : order.getChildList()) {
                if (!TextUtils.isEmpty(childInfo.getPhoto())) {
                    mPhotoList.add(childInfo.getPhoto());
                }
            }
        }
        //家长头像
        if (order.getGuardianList() != null && order.getGuardianList().size() > 0) {
            for (ChildInfo bean : order.getGuardianList()) {
                if (!TextUtils.isEmpty(bean.getPhoto())) {
                    mPhotoList.add(bean.getPhoto());
                }
            }
        }
        return mPhotoList;
    }

    public static List<String> getChildPhones(Order order) {
        List<String> phoneList = new ArrayList<>();
        if (order != null && order.getChildList() != null
                && !order.getChildList().isEmpty()) {
            for (ChildInfo childInfo : order.getChildList()) {
                phoneList.add(childInfo.getMobile());
            }
        }
        return phoneList;
    }


    /**
     * 修改上车地址样式
     *
     * @param context
     * @param addrText
     * @return
     */
    public static SpannableString startAddrFormat(Context context, String addrText) {
        //去接乘客
        String getOnAddress = "去 " + addrText + " 接乘客";
        int startIndex = 1;
        int endIndex = addrText.length() + 2;
        return SpannableStringUtil.getSpanString(getOnAddress, startIndex, endIndex, R.color.maas_text_primary, Typeface.BOLD, 1.1f);
    }

    /**
     * 修改下车地址样式
     *
     * @param context
     * @param addrText
     * @return
     */
    public static SpannableString endAddrFormat(Context context, String addrText) {
        //去送乘客
        String getOffAddress = "送乘客到 " + addrText;
        int startIndex = 4;
        int endIndex = addrText.length() + 5;
        return SpannableStringUtil.getSpanString(getOffAddress, startIndex, endIndex, R.color.maas_text_primary, Typeface.BOLD, 1.1f);
    }

    /**
     * 模拟轨迹线数据
     *
     * @return
     */
    public static List<LatLng> mockTrackCoordinateListData() {
        List<LatLng> list = new ArrayList<>();
        list.add(new LatLng(22.548277, 114.111366));
        list.add(new LatLng(22.546412, 114.110435));
        list.add(new LatLng(22.546349, 114.11039));
        list.add(new LatLng(22.546324, 114.110321));
        list.add(new LatLng(22.546303, 114.110184));
        list.add(new LatLng(22.546303, 114.110184));
        list.add(new LatLng(22.546598, 114.110161));
        list.add(new LatLng(22.546593, 114.110054));
        list.add(new LatLng(22.546267, 114.110092));
        list.add(new LatLng(22.546015, 114.110168));

        list.add(new LatLng(22.545811, 114.110237));
        list.add(new LatLng(22.544909, 114.110619));
        list.add(new LatLng(22.544197, 114.110931));
        list.add(new LatLng(22.543945, 114.111038));
        list.add(new LatLng(22.543657, 114.111137));
        list.add(new LatLng(22.543575, 114.111153));
        list.add(new LatLng(22.543095, 114.111237));
        list.add(new LatLng(22.542526, 114.111305));
        list.add(new LatLng(22.542049, 114.111351));
        list.add(new LatLng(22.541981, 114.111359));

        return list;
    }
}
