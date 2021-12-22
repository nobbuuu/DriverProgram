package com.haylion.android.orderdetail.trip;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.services.route.DriveRouteResult;
import com.haylion.android.MyApplication;
import com.haylion.android.R;
import com.haylion.android.common.map.BaseMapActivity;
import com.haylion.android.common.view.CargoRestTimeView;
import com.haylion.android.common.view.dialog.DialogUtils;
import com.haylion.android.data.model.EventMsg;
import com.haylion.android.data.model.MessageDetail;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.model.OrderCostInfo;
import com.haylion.android.data.model.OrderStatus;
import com.haylion.android.data.model.OrderTimeType;
import com.haylion.android.data.model.OrderTypeInfo;
import com.haylion.android.data.model.PayInfo;
import com.haylion.android.data.model.WebsocketData;
import com.haylion.android.data.util.BusinessUtils;
import com.haylion.android.data.util.DateUtils;
import com.haylion.android.data.base.ConfirmDialog;
import com.haylion.android.main.MainActivity;
import com.haylion.android.mvp.base.AppManager;
import com.haylion.android.mvp.util.SizeUtil;
import com.haylion.android.orderdetail.OrderDetailActivity;
import com.haylion.android.orderdetail.OrderDetailPresenter;
import com.haylion.android.orderlist.OrderListActivity;
import com.haylion.android.service.WsCommands;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.haylion.android.orderdetail.OrderDetailActivity.FORM_NOTIFICATION_CLICK;
import static com.haylion.android.orderdetail.OrderDetailActivity.FORM_NOTIFICATION_MESSAGE_ID;


/**
 * @author dengzh
 * @date 2019/10/23
 * Description: 行程详情页 - 行程结束
 * 替换之前的历史订单详情页
 * 状态： 未支付/已完成/已取消。
 */
public class TripDetailActivity extends BaseMapActivity<TripContract.Presenter>
        implements TripContract.View {

    private final String TAG = getClass().getSimpleName();

    public final static String ORDER_ID = "EXTRA_ORDER_ID";
    public final static String SHOW_LISTEN_BUTTON = "EXTRA_SHOW_GO_LISTEN_BUTTON";
    private final static String SHOW_CARGO_REST_TIME_VIEW = "EXTRA_SHOW_CARGO_REST_TIME_VIEW";
    private final static String BACK_TO_HISTORY_LIST = "BACK_TO_HISTORY_LIST";

    @BindView(R.id.rl_title)
    RelativeLayout rlTitle;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    /**
     * 地址信息
     */
    /*@BindView(R.id.iv_order_type_icon)
    ImageView ivOrderTypeIcon;   //订单类型icon*/
/*    @BindView(R.id.tv_get_on_addr)
    TextView tvOrderGetOn; //上车地址
    @BindView(R.id.tv_get_off_addr)
    TextView tvOrderGetOff; //下车地址*/

    /**
     * 订单信息
     */
    @BindView(R.id.ll_bottom_view)
    LinearLayout llBottomView;
    @BindView(R.id.fr_order_info)
    FrameLayout frOrderInfo;
    @BindView(R.id.tv_order_time)
    TextView tvOrderTime; //下单时间
    @BindView(R.id.tv_order_status)
    TextView tvOrderStatus; //订单状态
    @BindView(R.id.ll_order_total_time)
    LinearLayout llOrderTotalTime;
    @BindView(R.id.tv_order_total_time)
    TextView tvTotalTime; //行程总耗时

    /**
     * 费用信息
     */
    @BindView(R.id.ll_order_cost)
    LinearLayout llOrderCost;
    @BindView(R.id.tv_total_cost)
    TextView tvTotalCost;         //费用总计
    @BindView(R.id.tv_base_cost)
    TextView tvBaseCost;          //基础车费
    @BindView(R.id.tv_other_cost)
    TextView tvOtherCost;         //其他费用
    @BindView(R.id.tv_service_cost)
    TextView tvServiceCost;        //服务费
    @BindView(R.id.ll_base_cost)
    LinearLayout llBaseCost;
    @BindView(R.id.ll_other_cost)
    LinearLayout llOtherCost;
    @BindView(R.id.ll_service_cost)
    LinearLayout llServiceCost;
    @BindView(R.id.tv_order_type)
    TextView tvOrderType;         //订单类型
    @BindView(R.id.tv_order_number)
    TextView tvOrderNumber;       //订单编号

    /**
     * 订单取消信息
     */
    @BindView(R.id.rl_order_cancel_info)
    RelativeLayout rlOrderCancelInfo;
    @BindView(R.id.tv_order_cancel_reason)
    TextView tvOrderCancelReason;     //取消原因

    /**
     * 联系乘客 or 客服
     */
    @BindView(R.id.ll_contact_info)
    LinearLayout llContactInfo;
    @BindView(R.id.tv_contact_type)
    TextView tvContactType;
    @BindView(R.id.tv_contact_passenger)
    TextView tvContactPassenger;
    @BindView(R.id.tv_contact_customer)
    TextView tvContactCustomer;
    @BindView(R.id.tv_contact_child)
    TextView tvContactChild;
    @BindView(R.id.tv_contact_parent)
    TextView tvContactParent;

    /**
     * 货单信息
     */
    @BindView(R.id.fr_order_cargo_info)
    FrameLayout frlOrderCargoInfo;
    @BindView(R.id.tv_cargo_order_total_time)
    TextView tvCargoOrderTotalTime;
    @BindView(R.id.tv_order_cargo_goods_type)
    TextView tvOrderCargoGoodsType;
    @BindView(R.id.tv_order_cargo_number)
    TextView tvOrderCargoNumber;
    @BindView(R.id.ll_cargo_order_total_time)
    LinearLayout llCargoOrderTotalTime;
    @BindView(R.id.divided)
    View divided;

    /**
     * 底部按钮
     */
    @BindView(R.id.rl_status_action)
    RelativeLayout rlStatusAction;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;          //线下支付按钮 or 继续听单按钮
    @BindView(R.id.ll_cargo_btn_container)
    LinearLayout llCargoBtnContainer;  //货拼客 两按钮
    @BindView(R.id.tv_cargo_paid)
    TextView tvCargoPaid;         //货拼客 - 乘客已支付
    @BindView(R.id.tv_send_cargo)
    TextView tvSendCargo;     //货拼客 - 去送货


    /**
     * 货拼客单页面需要展示货单送达的时间。
     */
    @BindView(R.id.cargo_rest_time_view)
    CargoRestTimeView mCargoRestTimeView;


    /**
     * 订单
     */
    private Order order;
    private int orderId;  //订单id
    private boolean showGoListenButton = true;   //是否展示继续听单的button
    private boolean showCargoRestTimeView = false;  //是否显示货物剩余时间View
    private boolean isBackToHistoryList = false;     //是否返回历史订单页


    public static void go(Context context, int orderId, boolean showGoListenButton) {
        go(context, orderId, showGoListenButton, false);
    }

    public static void go(Context context, int orderId, boolean showGoListenButton, boolean showCargoRestTimeView) {
        go(context, orderId, showGoListenButton, showCargoRestTimeView, false);
    }

    /**
     * 跳转入口
     *
     * @param context
     * @param orderId               订单id
     * @param showGoListenButton    是否显示完成，继续听单按钮
     * @param showCargoRestTimeView 是否显示货物剩余时间
     * @param isBackToHistoryList   是否返回历史订单页
     */
    public static void go(Context context, int orderId, boolean showGoListenButton, boolean showCargoRestTimeView, boolean isBackToHistoryList) {
        Intent intent = new Intent(context, TripDetailActivity.class);
        intent.putExtra(ORDER_ID, orderId);
        intent.putExtra(SHOW_LISTEN_BUTTON, showGoListenButton);
        intent.putExtra(SHOW_CARGO_REST_TIME_VIEW, showCargoRestTimeView);
        intent.putExtra(BACK_TO_HISTORY_LIST, isBackToHistoryList);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);
        ButterKnife.bind(this);
        tvTitle.setText(R.string.trip_detail);
        EventBus.getDefault().register(this);
        //初始化地图，此方法必须重写
        setEndMarkerTitle("送货地址");
        initMap(savedInstanceState);
        initData();
    }

    private void initData() {
        orderId = getIntent().getIntExtra(ORDER_ID, 0);
        showGoListenButton = getIntent().getBooleanExtra(SHOW_LISTEN_BUTTON, true);
        showCargoRestTimeView = getIntent().getBooleanExtra(SHOW_CARGO_REST_TIME_VIEW, false);
        isBackToHistoryList = getIntent().getBooleanExtra(BACK_TO_HISTORY_LIST, false);

        //上报消息已读
        boolean notificationClick = getIntent().getBooleanExtra(FORM_NOTIFICATION_CLICK, false);
        int messageId = getIntent().getIntExtra(FORM_NOTIFICATION_MESSAGE_ID, 0);
        if (notificationClick) {
            MessageDetail messageDetail = new MessageDetail();
            messageDetail.setMessageId(messageId);
            messageDetail.setViewed(1);
            messageDetail.setDisplay(1);
            messageDetail.setDeleted(0);
//            presenter.updateMessage(messageDetail);
            EventBus.getDefault().post(messageDetail);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        initData();
        resetInitView();
    }


    /**
     * 由于启动模式是 SingleTop,在栈顶再次跳转入此页面，执行 onNewIntent()，需要把所有属性设置为初始状态
     * 1.属性设置为默认值
     * 2.View 凡是涉及到setVisible()的，都要设置初始状态
     */
    private void resetInitView() {
        mMapView.setVisibility(View.GONE);
        mCargoRestTimeView.setVisibility(View.GONE);
        mCargoRestTimeView.stopCountDown();

        //订单信息
        frOrderInfo.setVisibility(View.GONE);
        tvOrderTime.setVisibility(View.VISIBLE);
        tvOrderStatus.setVisibility(View.VISIBLE);
        llOrderTotalTime.setVisibility(View.GONE);

        //费用信息
        llOrderCost.setVisibility(View.GONE);
        llBaseCost.setVisibility(View.VISIBLE);
        llOtherCost.setVisibility(View.GONE);
        llServiceCost.setVisibility(View.GONE);
        //订单取消信息
        rlOrderCancelInfo.setVisibility(View.GONE);

        //联系乘客 or 客服
        llContactInfo.setVisibility(View.VISIBLE);
        tvContactPassenger.setVisibility(View.GONE);
        tvContactCustomer.setVisibility(View.GONE);
        tvContactChild.setVisibility(View.GONE);
        tvContactParent.setVisibility(View.GONE);

        //货单信息
        frlOrderCargoInfo.setVisibility(View.GONE);

        //底部按钮
        rlStatusAction.setVisibility(View.GONE);
        tvConfirm.setVisibility(View.GONE);
        llCargoBtnContainer.setVisibility(View.GONE);
        tvCargoPaid.setVisibility(View.VISIBLE);
        tvSendCargo.setVisibility(View.VISIBLE);
    }


    @Override
    protected void onDestroy() {
        if (mCargoRestTimeView != null) {
            mCargoRestTimeView.onDestroy();
        }
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        MyApplication.setOrderIdRunning(-1);
    }

    @OnClick({R.id.iv_back, R.id.tv_confirm, R.id.iv_click_zoom, R.id.tv_cargo_paid, R.id.tv_send_cargo})
    public void onButtonClick(View view) {
        if (isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.tv_confirm:  //确认
                if (order.getOrderStatus() == Order.ORDER_STATUS_WAIT_PAY) {
                    presenter.payConfirm(order.getOrderId(), PayInfo.OFFLINE_PAY_CONFIRM_PAIED);
                } else {  //继续听单
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;
            case R.id.tv_cargo_paid: //货单乘客已线下支付
                presenter.payConfirm(orderId, PayInfo.OFFLINE_PAY_CONFIRM_PAIED);
                break;
            case R.id.tv_send_cargo:  //去送货
                goOrderDetailActivityToSend();
                break;
            case R.id.iv_click_zoom: //定位
                if (routeOverlay != null) {
                    zoomToSpan();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 显示范围
     */
    @Override
    public void zoomToSpan() {
        if (routeOverlay != null) {
            rlTitle.post(new Runnable() {
                @Override
                public void run() {
                    // Log.e(TAG,"zoomToSpan:rlTitle.bottom = " + rlTitle.getBottom() + ",llBottomView.height = " + llBottomView.getHeight());
                    routeOverlay.zoomToSpan(200, 200, rlTitle.getBottom() + 100, llBottomView.getHeight());
                }
            });
        }
    }


    @Override
    protected TripContract.Presenter onCreatePresenter() {
        return new TripPresenter(this);
    }

    /**
     * 线下确认支付成功，重新查询订单详情
     */
    @Override
    public void payConfirmSuccess() {
        presenter.getOrderDetail(orderId);
    }

    /**
     * 获取客服电话
     *
     * @param phoneNum
     */
    @Override
    public void getServiceTelNumberSuccess(String phoneNum, boolean isNeedShowDialog) {
        DialogUtils.showCustomerServiceCallDialog(this, phoneNum, isNeedShowDialog);
    }

    /**
     * 更新货单收货 预计送达时间
     *
     * @param estimateArriveTimeStr
     */
    @Override
    public void updateCargoOrderRestTime(String estimateArriveTimeStr) {
        if (TextUtils.isEmpty(estimateArriveTimeStr)) {
            return;
        }
        mCargoRestTimeView.setVisibility(View.VISIBLE);
        try {
            Date estimateArriveTime = new SimpleDateFormat(DateUtils.FORMAT_PATTERN_YMDHM).parse(estimateArriveTimeStr);
            Date current = new Date(System.currentTimeMillis());
//            long restTimeMillis = (estimateArriveTime.getTime() - current.getTime()) > 0 ? (estimateArriveTime.getTime() - current.getTime()) : 0;
            long restTimeMillis = estimateArriveTime.getTime() - current.getTime();
            mCargoRestTimeView.startCountDown(restTimeMillis);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exceptionFinishOrderSuccess() {

    }


    /**
     * 获取订单详情失败
     *
     * @param msg
     */
    @Override
    public void getOrderDetailFail(String msg) {
        finish();
    }

    /**
     * 获取订单详情成功
     *
     * @param orderInfo
     */
    @Override
    public void getOrderDetailSuccess(Order orderInfo) {
        if (orderInfo != null) {
            order = orderInfo;
            //订单信息
            updateOrderInfoCardView();
            //地图信息
            showOrderInfoInMap();
            updateOrderContactView();
        }
    }


    /**
     * 订单信息展示
     */
    private void updateOrderInfoCardView() {
        frOrderInfo.setVisibility(View.VISIBLE);
        //订单时间
        String timeString = "";
        try {
            if (order.getOrderTime() != null) {
                long milliSecond = BusinessUtils.stringToLong(order.getOrderTime(), "yyyy-MM-dd HH:mm");
                timeString = BusinessUtils.getDateToStringIncludeYearWhenCrossYear(milliSecond, null);
                tvOrderTime.setText(timeString);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //地址
       /* tvOrderGetOn.setText(order.getStartAddr().getName());
        tvOrderGetOff.setText(order.getEndAddr().getName());*/
        //总耗时
        tvTotalTime.setText(BusinessUtils.formatEstimateTimeText(order.getTotalTime()));
        //订单类型,拼车单 + 人数
        if (order.getOrderType() == Order.ORDER_TYPE_REALTIME_CARPOOL) {
            tvOrderType.setText(OrderTypeInfo.getStatusText(order.getOrderType(), order.getChannel())
                    + getString(R.string.person_number_with_brackets, order.getPassengerNum() + ""));
        } else {
            tvOrderType.setText(OrderTypeInfo.getStatusText(order.getOrderType(), order.getChannel()));
        }
        //订单编号
        tvOrderNumber.setText(order.getOrderCode());

        //订单状态
        tvOrderStatus.setText(OrderStatus.getOrderStatusText(order));

        if (order.getOrderStatus() == Order.ORDER_STATUS_WAIT_PAY) {  //乘客未付款
/*            if (order.getOrderType() == Order.ORDER_TYPE_SEND_CHILD) {
                tvOrderStatus.setText(R.string.parents_not_pay);
            } else {
                tvOrderStatus.setText(R.string.passenger_not_paid);
            }*/
            llOrderTotalTime.setVisibility(View.VISIBLE);
            updateOrderCostInfoView();
            updateBottomBtnView();
        } else if (order.getOrderStatus() == Order.ORDER_STATUS_CLOSED) {   //订单完成，乘客已付款
/*            if (order.getOrderSubStatus() == Order.ORDER_SUB_STATUS_CLOSED_COMPLAIN) {
                tvOrderStatus.setText(OrderStatus.getStatusText(order.getOrderStatus(), order.getOrderType()) + getString(R.string.payment_dispute_with_brackets));
            } else {
                tvOrderStatus.setText(OrderStatus.getStatusText(order.getOrderStatus(), order.getOrderType()));
            }*/
            llOrderTotalTime.setVisibility(View.VISIBLE);
            updateOrderCostInfoView();
            updateBottomBtnView();
        } else if (order.getOrderStatus() == Order.ORDER_STATUS_CANCELED) { //取消订单
//            tvOrderStatus.setText(R.string.order_status_canceled);
            llOrderTotalTime.setVisibility(View.GONE);
            updateOrderCancelInfoView();
        } else { //其他状态都是异常状态，结束当前页面
            if (order.getOrderStatus() == Order.ORDER_STATUS_GET_OFF && order.getOrderType() == Order.ORDER_TYPE_CARGO) {
                //货单，完成的状态对应两种情况（1.已下车，2.已完成）
                tvOrderStatus.setText("已送达");
            } else {
                finish();
            }
        }

        if (order.getOrderType() == Order.ORDER_TYPE_CARGO) {
            //货单
            updateOrderCarGoInfoView();
        } else if (order.getOrderType() == Order.ORDER_TYPE_CARGO_PASSENGER) {
            //货拼客单
            if (showCargoRestTimeView) {
                presenter.getCargoOrderSendDeadTime(order.getCargoOrderId());
            }
        }
    }

    /**
     * 更新费用 InfoView
     */
    private void updateOrderCostInfoView() {
        if (order.getOrderType() == Order.ORDER_TYPE_CARGO) {
            llOrderCost.setVisibility(View.GONE);
            return;
        }
        llOrderCost.setVisibility(View.VISIBLE);

        //花费
        OrderCostInfo costDetail = order.getCostDetail();
        if (costDetail.getTotalCost() <= 0) {
            llOrderCost.setVisibility(View.GONE);
        }

        //总费用
        tvTotalCost.setText(BusinessUtils.moneySpec(costDetail.getTotalCost()));
        //基础车费
        tvBaseCost.setText(BusinessUtils.moneySpec(costDetail.getBaseCost()));
        //其他费用
        if (costDetail.getOtherCost() <= 0) {
            llOtherCost.setVisibility(View.GONE);
        } else {
            tvOtherCost.setText(BusinessUtils.moneySpec(costDetail.getOtherCost()));
            llOtherCost.setVisibility(View.VISIBLE);
        }
        //服务费用
        if (costDetail.getServiceCost() <= 0) {
            llServiceCost.setVisibility(View.GONE);
        } else {
            tvServiceCost.setText(BusinessUtils.moneySpec(costDetail.getServiceCost()));
            llServiceCost.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 更新底部按钮
     */
    private void updateBottomBtnView() {
        if (order.getOrderType() == Order.ORDER_TYPE_CARGO) {
            rlStatusAction.setVisibility(View.GONE);
            return;
        }
        if (order.getOrderStatus() == Order.ORDER_STATUS_WAIT_PAY) {  //乘客未付款
            if (order.getOrderType() == Order.ORDER_TYPE_SEND_CHILD) { //孩子单,没有可操作按钮
                rlStatusAction.setVisibility(View.GONE);
            } else if (order.getOrderType() == Order.ORDER_TYPE_CARGO_PASSENGER && showGoListenButton) {
                //货拼客单，需要区分是否需要显示去送货按钮
                //如果是从订单列表进入的，不需要显示去送货按钮;其他的情况，显示；这个按钮相当于继续听单按钮
                rlStatusAction.setVisibility(View.VISIBLE);
                tvConfirm.setVisibility(View.GONE);
                llCargoBtnContainer.setVisibility(View.VISIBLE);
            } else {
                tvConfirm.setText(R.string.passengers_have_paid_offline);
                rlStatusAction.setVisibility(View.VISIBLE);
                tvConfirm.setVisibility(View.VISIBLE);
            }
        } else if (order.getOrderStatus() == Order.ORDER_STATUS_CLOSED) {   //订单完成，乘客已付款
            if (showGoListenButton) {
                tvConfirm.setText(R.string.complete_to_listen);
                rlStatusAction.setVisibility(View.VISIBLE);
                tvConfirm.setVisibility(View.VISIBLE);
                if (order.getOrderType() == Order.ORDER_TYPE_CARGO_PASSENGER) {
                    //货拼客单，用去送货 替代 继续听单按钮
                    tvConfirm.setVisibility(View.GONE);
                    llCargoBtnContainer.setVisibility(View.VISIBLE);
                    tvCargoPaid.setVisibility(View.GONE);
                    tvSendCargo.setVisibility(View.VISIBLE);
                }
            } else {
                rlStatusAction.setVisibility(View.GONE);
            }
        } else if (order.getOrderStatus() == Order.ORDER_STATUS_CANCELED) { //取消订单
            rlStatusAction.setVisibility(View.GONE);
        }
    }

    /**
     * 更新货单信息
     */
    private void updateOrderCarGoInfoView() {
        frOrderInfo.setVisibility(View.GONE);
        frlOrderCargoInfo.setVisibility(View.VISIBLE);

        //总耗时，
        if (order.getOrderStatus() == Order.ORDER_STATUS_GET_OFF || order.getOrderStatus() == Order.ORDER_STATUS_CLOSED) {
            String timeString = "";
            timeString = BusinessUtils.formatEstimateTimeText(order.getTotalTime());
            tvCargoOrderTotalTime.setText(timeString);
        } else {
            String timeString = "";
            try {
                if (order.getOrderTime() != null) {
                    long milliSecond = BusinessUtils.stringToLong(order.getOrderTime(), "yyyy-MM-dd HH:mm");
                    timeString = BusinessUtils.getDateToStringIncludeYearWhenCrossYear(milliSecond, null);
                    tvCargoOrderTotalTime.setText(timeString);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        tvOrderCargoGoodsType.setText(order.getGoodsDescription());
        tvOrderCargoNumber.setText(order.getOrderCode());

        tvOrderTime.setVisibility(View.GONE);
        tvOrderStatus.setVisibility(View.GONE);
        // ivOrderTypeIcon.setVisibility(View.VISIBLE);

        if (order.getOrderStatus() == Order.ORDER_STATUS_CANCELED) {
            tvTitle.setText(R.string.goods_canceled);
            llCargoOrderTotalTime.setVisibility(View.GONE);
            divided.setVisibility(View.GONE);
        } else {
            tvTitle.setText(R.string.goods_delivered);
        }
    }

    /**
     * 更新取消 InfoView
     */
    private void updateOrderCancelInfoView() {
        llOrderCost.setVisibility(View.GONE);
        rlOrderCancelInfo.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(order.getOrderCancellerMsg())) {
            String reason;
            if (order.getOrderCancellerType() == 1) {
                reason = getString(R.string.order_cancelled_by_passengers);
            } else if (order.getOrderCancellerType() == 2) {
                reason = getString(R.string.order_cancelled_by_driver);
            } else {
                reason = getString(R.string.order_cancelled_by_platform);
            }
            tvOrderCancelReason.setText(reason + "  (" + order.getOrderCancellerMsg() + ")");
        }
    }

    /**
     * 更新Map
     * 地图中展示起点和终点，实际行驶路线信息
     */
    private void showOrderInfoInMap() {
        mAMap.clear();// 清理地图上的所有覆盖物
        if (order.getOrderType() == Order.ORDER_TYPE_CARGO) { //货单的Marker icon
            resIdStart = R.mipmap.ic_marker_goods_pick_up;
            resIdEnd = R.mipmap.ic_marker_goods_receive;
        }
        if (order.getOrderStatus() == Order.ORDER_STATUS_CANCELED) { //取消订单不显示路线
            routeOverlay.setStartAndEndIcon(resIdStart, resIdEnd);
            routeOverlay.showTrueLatLng(order.getStartAddr(), order.getEndAddr(), null);
        } else { //其他情况显示真实路线
            routeOverlay.setStartAndEndIcon(resIdStart, resIdEnd);
            routeOverlay.showTrueLatLng(order.getStartAddr(), order.getEndAddr(), order.getTrackCoordinateList());
        }
        zoomToSpan();
        mMapView.setVisibility(View.VISIBLE);
    }

    /*******************************  EventBus 事件  *********************************************/
    /**
     * 支付结果 消息监听
     *
     * @param websocketData
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(WebsocketData websocketData) {
        if (websocketData.getCmdSn() == WsCommands.DRIVER_ORDER_PAID.getSn()) {
            int orderId = websocketData.getData().getOrderId();
            if (order != null && orderId == order.getOrderId()) {
                presenter.getOrderDetail(orderId);
            }
        }
    }

    /**
     * 新订单抢单 成功与否监听
     *
     * @param data
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventMsg data) {
        if (data.getCmd() == EventMsg.CMD_ORDER_GRAB_SUCCESS) {
            //抢单成功,页面销毁
            finish();
        } else if (data.getCmd() == EventMsg.CMD_ORDER_STATUS_TO_CONTROVERSY) {
            //争议状态
            if (data.getOrderId() == orderId) {
                presenter.getOrderDetail(orderId);
            }
        }
    }


    /**
     * 地图路线规划
     *
     * @param driveRouteResult
     * @param i
     */
    @Override
    public void handleDriveRouteSearchedResult(DriveRouteResult driveRouteResult, int i) {
        routeOverlay.setColorFulLine(false);//是否用颜色展示交通拥堵情况
        routeOverlay.setStartAndEndIcon(resIdStart, resIdEnd);
        routeOverlay.setStartAndEndAddress(order.getStartAddr().getName(), order.getEndAddr().getName()); //设置起点终点地名
    }

    /**
     * 跳转到订单详情页-送货
     * 如果当前是货单，此时的orderId就是货单id
     * 如果当前是货拼客单，此时的cargoOrderId 才是货单id
     */
    private void goOrderDetailActivityToSend() {
        //如果是从TripEndActivity 进入，需要关闭 TripEndActivity
        AppManager.getAppManager().finishActivity(TripEndActivity.class);

        if (order.getOrderType() == Order.ORDER_TYPE_CARGO) { //货单
            OrderDetailActivity.go(this, order.getOrderId());
        } else if (order.getOrderType() == Order.ORDER_TYPE_CARGO_PASSENGER) { //货拼客单
            OrderDetailActivity.go(this, order.getCargoOrderId());
        }
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.getOrderDetail(orderId);
        MyApplication.setOrderIdRunning(orderId);
    }


    /**
     * 更新联系人信息
     */
    private void updateOrderContactView() {
        llContactInfo.setVisibility(View.VISIBLE);
        if (order.getOrderStatus() == Order.ORDER_STATUS_CANCELED) {
            tvContactCustomer.setVisibility(View.VISIBLE);
            tvContactType.setText(R.string.contact_customer_service);
        } else {
            tvContactType.setText(R.string.order_contact_passenger);
            if (order.getOrderType() == Order.ORDER_TYPE_SEND_CHILD) {
                tvContactChild.setVisibility(View.VISIBLE);
                tvContactParent.setVisibility(View.VISIBLE);
            } else if (order.getOrderType() == Order.ORDER_TYPE_REALTIME
                    || order.getOrderType() == Order.ORDER_TYPE_CARGO_PASSENGER
                    || order.getOrderType() == Order.ORDER_TYPE_REALTIME_CARPOOL
                    || order.getOrderType() == Order.ORDER_TYPE_BOOK) {
                tvContactPassenger.setVisibility(View.VISIBLE);
            }
        }
    }


    @OnClick({R.id.tv_contact_passenger, R.id.tv_contact_customer, R.id.tv_contact_child, R.id.tv_contact_parent})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_contact_passenger:
                if (!TextUtils.isEmpty(order.getUserInfo().getVirtualNum())) {
                    DialogUtils.showRealCallDialog(this, order.getUserInfo().getVirtualNum());
                } else {
                    //订单已结束，虚拟号码失效
                    ConfirmDialog.newInstance().setMessage("订单已结束，不能再联系乘客了，有事可以联系客服处理")
                            .setOnClickListener(new ConfirmDialog.OnClickListener() {
                                @Override
                                public void onPositiveClick(View view) {
                                    presenter.getServiceTelNumber(false);
                                }

                                @Override
                                public void onDismiss() {

                                }
                            }).setPositiveText(R.string.contact_customer_service).setNegativeText(R.string.close).show(getSupportFragmentManager(), "");
                }
                break;
            case R.id.tv_contact_customer:
                presenter.getServiceTelNumber(true);
                break;
            case R.id.tv_contact_child:
                DialogUtils.showRealCallDialog(this, order.getChildList().get(0).getMobile());
                break;
            case R.id.tv_contact_parent:
                DialogUtils.showRealCallDialog(this, order.getUserInfo().getPhoneNum());
                break;
        }
    }


    @Override
    public void onBackPressed() {
        if (isBackToHistoryList) {
            //进入历史订单页面
            OrderListActivity.start(this, OrderTimeType.HISTORY, true);
            finish();
        } else {
            super.onBackPressed();
        }
    }
}

