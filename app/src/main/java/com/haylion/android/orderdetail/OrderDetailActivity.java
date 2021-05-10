package com.haylion.android.orderdetail;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.AmapPageType;
import com.amap.api.navi.INaviInfoCallback;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.haylion.android.Constants;
import com.haylion.android.MyApplication;
import com.haylion.android.R;
import com.haylion.android.common.aibus_location.data.GpsData;
import com.haylion.android.common.map.AMapUtil;
import com.haylion.android.common.map.BaseMapActivity;
import com.haylion.android.common.view.CargoRestTimeView;
import com.haylion.android.common.view.dialog.DialogUtils;
import com.haylion.android.data.base.ConfirmDialog;
import com.haylion.android.data.model.AddressInfo;
import com.haylion.android.data.model.EventBean;
import com.haylion.android.data.model.EventMsg;
import com.haylion.android.data.model.FloatwindowCmd;
import com.haylion.android.data.model.MessageDetail;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.model.OrderPayInfo;
import com.haylion.android.data.model.PayInfo;
import com.haylion.android.data.model.WebsocketData;
import com.haylion.android.data.util.BusinessUtils;
import com.haylion.android.data.util.DateUtils;
import com.haylion.android.data.util.SpannableStringUtil;
import com.haylion.android.data.widgt.SlideView;
import com.haylion.android.main.MainActivity;
import com.haylion.android.mvp.util.LogUtils;
import com.haylion.android.mvp.util.SizeUtil;
import com.haylion.android.orderdetail.amapNavi.AMapNaviViewActivity;
import com.haylion.android.orderdetail.trip.TripDetailActivity;
import com.haylion.android.orderdetail.trip.TripOperateActivity;
import com.haylion.android.pay.PayMainActivity;
import com.haylion.android.service.FloatDialogService;
import com.haylion.android.service.WsCommands;
import com.haylion.android.uploadPhoto.UploadChildImgActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * 订单详情页
 */
public class OrderDetailActivity extends BaseMapActivity<OrderDetailContract.Presenter> implements OrderDetailContract.View {

    private static final String TAG = "OrderDetailActivity";
    /**
     * handler msg code
     */
    //提示司机可以开始打标计时
    private final int HANDLER_MSG_TIMING_REMINDER = 1111;
    //司机到达上车点，等待乘客上车，计时
    private final int HANDLER_MSG_WAITING_PASSENGER_COUNTING_TIME = 1002;

    public final static String ORDER_ID = "EXTRA_ORDER_ID";
    public final static String CARGO_ORDER_ID = "EXTRA_CARGO_ORDER_ID";
    public final static String ORDER_IS_NEW = "EXTRA_ORDER_IS_NEW";
    public final static String ORDER_START_ADDR = "EXTRA_ORDER_START_ADDR";
    public final static String ORDER_END_ADDR = "EXTRA_ORDER_END_ADDR";

    /**
     * 拼车单 - 拼车码
     */
    public final static String EXTRA_CARPOOL_CODE = "carpool_code";
    /**
     * 是否为拼车单
     */
    public final static String EXTRA_CARPOOL_FLAG = "carpool_flag";

    public final static String FORM_NOTIFICATION_CLICK = "EXTRA_NOTIFICATION_CLICK";
    public final static String FORM_NOTIFICATION_MESSAGE_ID = "EXTRA_NOTIFICATION_MESSAGE_ID";

    /**
     * 标题
     */
    @BindView(R.id.tv_title)
    TextView tvHeaderName;
    @BindView(R.id.tv_title_right)
    TextView tvTitleRight;

    /**
     * 订单初始状态，订单时间和上下车点
     */
    @BindView(R.id.ll_order_init_info)
    LinearLayout llOrderInitInfo;
    @BindView(R.id.tv_order_time)
    TextView tvOrderTime;       //订单时间
    @BindView(R.id.fl_order_time)
    FrameLayout flOrderTime;       //订单时间
    @BindView(R.id.tv_address_on)
    TextView tvAddressOn;       //上车点
    @BindView(R.id.tv_address_off)
    TextView tvAddressOff;       //下车点

    /**
     * 地址信息
     */
    @BindView(R.id.rl_address_info)
    RelativeLayout rlAddressInfo;
    @BindView(R.id.navi_action)
    ImageView ivNaviAction;         //导航按钮
    @BindView(R.id.fr_get_on_addr)
    LinearLayout frGetOnAddr;        //上车地址-布局
    @BindView(R.id.tv_get_on_addr)
    TextView tvOrderGetOn;          //上车地址
    @BindView(R.id.tv_get_on_desc)
    TextView tvGetOnDesc;           //上车详细地址
    @BindView(R.id.fr_get_off_addr)
    LinearLayout frGetOffAddr;        //下车地址-布局
    @BindView(R.id.tv_get_off_addr)
    TextView tvOrderGetOff;         //下车地址
    @BindView(R.id.tv_get_off_desc)
    TextView tvGetOffDesc;          //下车详细地址
    @BindView(R.id.tv_get_off_addr_prefix)
    TextView tvGetOffAddrPrefix;

    @BindView(R.id.tv_get_on_addr_prefix)
    TextView tvGetOnAddrPrefix;
    @BindView(R.id.tv_get_on_addr_suffix)
    TextView tvGetOnAddrSuffix;


    @BindView(R.id.iv_order_type_icon)
    ImageView ivOrderTypeIcon;   //订单类型icon
    @BindView(R.id.tv_order_total_time_new)
    TextView tvOrderTotalTimeNew;  //行程总耗时 - 新
    @BindView(R.id.ll_total_time)
    LinearLayout llTotalTime;


    /**
     * 预计剩余距离
     */
    @BindView(R.id.ll_rest_distance)
    LinearLayout llRestDistance;     //剩余距离布局
    /* @BindView(R.id.iv_rest_distance_icon_placeholder)
     ImageView ivRestDistanceIconPlaceholder;  //剩余距离栏 占位用*/
    @BindView(R.id.tv_rest_distance)
    TextView tvRestDistance;  //剩余距离显示
    @BindView(R.id.tv_rest_distance_unit)
    TextView tvRestDistanceUnit;  //剩余距离-单位
    /**
     * 预计剩余时间
     */
    @BindView(R.id.tv_rest_time_prefix)
    TextView tvRestTimePrefix;
    @BindView(R.id.tv_rest_time_hour)
    TextView tvRestTimeHour;
    @BindView(R.id.tv_rest_time_hour_unit)
    TextView tvRestTimeHourUnit;
    @BindView(R.id.tv_rest_time_minute)
    TextView tvRestTimeMinute;
    @BindView(R.id.tv_rest_time_minute_unit)
    TextView tvRestTimeMinuteUnit;
    @BindView(R.id.tv_rest_time_second)
    TextView tvRestTimeSecond;
    @BindView(R.id.tv_rest_time_second_unit)
    TextView tvRestTimeSecondUnit;

    /**
     * 订单提示View
     */
    @BindView(R.id.ll_order_tips)
    LinearLayout llOrderTips;
    @BindView(R.id.tv_order_tips)
    TextView tvOrderTips;   //预计到达上车点时间

    /**
     * 上下车照片确认倒计时
     */
    @BindView(R.id.countdown_layout)
    LinearLayout mCountdownLayout;


    /**
     * 货单显示信息
     */
    @BindView(R.id.cargo_rest_time_view)
    CargoRestTimeView mCargoRestTimeView;
    @BindView(R.id.ll_cargo_order_info)
    LinearLayout llCargoOrderInfo;
    @BindView(R.id.rl_cargo_contact)
    RelativeLayout rlCargoContact;
    @BindView(R.id.tv_receive_phone_number_running)
    TextView tvReceivePhoneNumberRunning; //收货用户尾号
    @BindView(R.id.tv_cargo_goods_type)
    TextView tvGoodsType;      //货物类型
    @BindView(R.id.tv_cargo_order_number)
    TextView tvCargoOrderNumber;  //货单编号

    /**
     * 货拼客单
     */
    @BindView(R.id.ll_cargo_action)
    LinearLayout llCargoAction;
    @BindView(R.id.tv_pay_offline)
    TextView tvPayOffline;
    @BindView(R.id.tv_send_cargo_action)
    TextView tvSendCargoAction;   //去送货按钮


    /**
     * 底部按钮
     */
    @BindView(R.id.ll_bottom_view)
    LinearLayout llBottomView;
    @BindView(R.id.iv_click_zoom)
    ImageView ivClickZoom;    //定位按钮，视图返回当前位置
    @BindView(R.id.rl_status_action)
    RelativeLayout rlStatusAction; //底部按钮
    @BindView(R.id.slideview)
    SlideView slideview;    //滑动按钮
    @BindView(R.id.order_status_action)
    Button tvOrderStatusAction;  //固定按钮

    /**
     * 小孩联系View
     */
    @BindView(R.id.fl_child_contact_container)
    FrameLayout flChildContactContainer;
    @BindView(R.id.ll_child_contact_style1)
    LinearLayout llChildContactStyle1;
    @BindView(R.id.tv_child_contact)
    TextView tvChildContact;
    @BindView(R.id.ll_child_parent_contact2)
    LinearLayout llChildParentContact2;

    /**
     * 实时单联系View
     */
    @BindView(R.id.tv_realtime_contact_number)
    TextView tvRealtimeContactNumber;
    @BindView(R.id.rl_realtime_contact)
    RelativeLayout rlRealtimeContact;


    /**
     * 订单
     */
    private Order order;
    private int orderId;    //订单id
    private int cargoOrderId = 0;  //货单id,目前按理不会有实际使用到的时机，暂时保留，只在此页面处于货拼客待支付or已完成情况下有用
    private double linearDistance = -1;  //直线距离
    private GpsData currentGps;
    private boolean isOnResume;
    private boolean isNeedHandlerFromNotification; //是否已处理从通知进来的判断，从通知过来置为true，判断过后置为false

    /**
     * 拼车码
     */
    private String carpoolCode;
    /**
     * 拼车标记
     */
    private boolean carpoolFlag = false;

    /**
     * 跳转
     *
     * @param context
     * @param orderId 订单id
     */
    public static void go(Context context, int orderId) {
        Intent intent = new Intent(context, OrderDetailActivity.class);
        intent.putExtra(ORDER_ID, orderId);
        context.startActivity(intent);
    }

    /**
     * Handler
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == HANDLER_MSG_TIMING_REMINDER) {
                //提示司机可以开始打标计时
                showTaxiMeterTipDialog(0);
            } else if (msg.what == HANDLER_MSG_WAITING_PASSENGER_COUNTING_TIME) {
                //司机到达上车点，等待乘客上车，计时
                updateWaitingPassengerTime();
            }
        }
    };

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.order_detail_act);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        tvHeaderName.setText(R.string.order_detail);
        tvTitleRight.setText(R.string.trip_detail);

        if (carpoolFlag) {
            carpoolCode = getIntent().getStringExtra(EXTRA_CARPOOL_CODE);
            if (TextUtils.isEmpty(carpoolCode)) {
                LogUtils.e("拼车码为空");
                finish();
                return;
            }
        } else {
            orderId = getIntent().getIntExtra(ORDER_ID, 0);
            cargoOrderId = getIntent().getIntExtra(CARGO_ORDER_ID, 0);
            if (orderId == 0) {
                LogUtils.d(TAG, "订单ID为空");
                finish();
                return;
            }
        }

        //上报消息已读
        boolean notificationClick = getIntent().getBooleanExtra(FORM_NOTIFICATION_CLICK, false);
        isNeedHandlerFromNotification = notificationClick;
        int messageId = getIntent().getIntExtra(FORM_NOTIFICATION_MESSAGE_ID, 0);
        if (notificationClick) {
            MessageDetail messageDetail = new MessageDetail();
            messageDetail.setMessageId(messageId);
            messageDetail.setViewed(1);
            messageDetail.setDisplay(1);
            messageDetail.setDeleted(0);
            EventBus.getDefault().post(messageDetail);
        }

        //初始化地图，必须保证有MapView
        initMap(bundle);
        //设置导航地图样式
        mMapView.getMap().setMapType(AMap.MAP_TYPE_NAVI);

        /**
         * SlideView 滑动监听->改变订单状态
         * */
        slideview.addSlideListener(new SlideView.OnSlideListener() {
            @Override
            public void onSlideSuccess() {
                if (order == null) {
                    slideview.reset();
                    refreshOrderDetails();
                    return;
                }
                linearDistance = getLineDistance();
                Log.e(TAG, "直线距离：" + linearDistance);
                if (order.getOrderType() == Order.ORDER_TYPE_SEND_CHILD) {
                    //送小孩单
                    handleChildOrderSlide();
                } else if (order.getOrderType() == Order.ORDER_TYPE_REALTIME
                        || order.getOrderType() == Order.ORDER_TYPE_REALTIME_CARPOOL
                        || order.getOrderType() == Order.ORDER_TYPE_CARGO_PASSENGER) {
                    //实时订单 or 货拼客单
                    handleRealTimeOrderSlide();
                } else {
                    presenter.changeOrderStatus(cargoOrderId, OrderDetailPresenter.OPERATAR_BY_DETAIL);
                }
            }
        });
        //悬浮窗异常情况处理
        handleFloatDialogException();
    }

    /**
     * 复用Activity时的生命周期回调
     **/
    @Override
    protected void onNewIntent(Intent intent) {
        LogUtils.d(TAG, "onNewIntent");
        super.onNewIntent(intent);
        setIntent(intent);
        orderId = intent.getIntExtra(ORDER_ID, 0);
        cargoOrderId = intent.getIntExtra(CARGO_ORDER_ID, 0);
        LogUtils.d(TAG, "onNewIntent, orderId: " + orderId + "cargoOrderId:" + cargoOrderId);
        if (orderId == 0) {
            finish();
            return;
        }

        //上报消息已读
        boolean notificationClick = getIntent().getBooleanExtra(FORM_NOTIFICATION_CLICK, false);
        int messageId = getIntent().getIntExtra(FORM_NOTIFICATION_MESSAGE_ID, 0);
        if (notificationClick) {
            MessageDetail messageDetail = new MessageDetail();
            messageDetail.setMessageId(messageId);
            messageDetail.setViewed(1);
            messageDetail.setDisplay(1);
            messageDetail.setDeleted(0);
            EventBus.getDefault().post(messageDetail);
        }

        resetInitView();
    }

    /**
     * 由于启动模式是 SingleTask, 执行 onNewIntent()，需要把所有属性设置为初始状态
     * 1.属性设置为默认值
     * 2.View设置默认值
     */
    private void resetInitView() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        //订单信息-初始
        llOrderInitInfo.setVisibility(View.GONE);
        flOrderTime.setVisibility(View.VISIBLE);

        //总耗时
        llTotalTime.setVisibility(View.GONE);

        //订单信息-执行
        rlAddressInfo.setVisibility(View.VISIBLE);
        ivNaviAction.setVisibility(View.VISIBLE);
        ivOrderTypeIcon.setVisibility(View.GONE);
        frGetOnAddr.setVisibility(View.VISIBLE);
        tvGetOnDesc.setVisibility(View.VISIBLE);
        frGetOffAddr.setVisibility(View.GONE);
        tvGetOffDesc.setVisibility(View.GONE);
        llRestDistance.setVisibility(View.VISIBLE);
        llOrderTips.setVisibility(View.GONE);

        //联系电话
        flChildContactContainer.setVisibility(View.GONE);
        rlRealtimeContact.setVisibility(View.GONE);

        //货单信息
        mCargoRestTimeView.setVisibility(View.GONE);
        mCargoRestTimeView.stopCountDown();
        llCargoOrderInfo.setVisibility(View.GONE);

        //底部按钮
        rlStatusAction.setVisibility(View.GONE);
        tvOrderStatusAction.setVisibility(View.GONE);
        slideview.setVisibility(View.VISIBLE);
        llCargoAction.setVisibility(View.GONE);
        tvPayOffline.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        // 预约订单，回到预约列表 #1003681
//        if (order.getOrderType() == Order.ORDER_TYPE_BOOK) {
//            BookOrderListActivity.start(getContext(), OrderTimeType.TODAY);
//            AppointmentListActivity.start(getContext());
//        } else {
        super.onBackPressed();
//        }
    }

    @OnClick({R.id.navi_action, R.id.order_status_action, R.id.rl_realtime_contact,
            R.id.tv_receive_phone_number_running, R.id.tv_receive_phone_number_running_fix, R.id.iv_receive_phone_number, R.id.tv_send_cargo_action, R.id.tv_pay_offline,
            R.id.tv_title_right, R.id.iv_back, R.id.iv_click_zoom})
    public void onButtonClick(View view) {
        if (isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.tv_title_right: //行程详情
                if (carpoolFlag) {
                    Intent intent = new Intent(getContext(), TripOperateActivity.class);
                    intent.putExtra(EXTRA_CARPOOL_FLAG, true);
                    intent.putExtra(EXTRA_CARPOOL_CODE, carpoolCode);
                    startActivity(intent);
                } else {
                    TripOperateActivity.go(this, orderId);
                }
                break;
            case R.id.navi_action:  //导航
                if (order != null) {
                    AMapNaviViewActivity.go(this, orderId);
                    /*AddressInfo destAddr = new AddressInfo();
                    switch (OrderStatus.forStatus(order.getOrderStatus())) {
                        case ORDER_STATUS_CLOSED:
                        case ORDER_STATUS_WAIT_PAY:
                        case ORDER_STATUS_GET_ON:
                        case ORDER_STATUS_GET_OFF:
                        case ORDER_STATUS_CANCELED:
                            destAddr = order.getEndAddr();
                            break;
                        case ORDER_STATUS_INIT:
                        case ORDER_STATUS_READY:
                        case ORDER_STATUS_WAIT_CAR:
                        case ORDER_STATUS_ARRIVED_START_ADDR:
                            destAddr = order.getStartAddr();
                            break;
                        case UNKNOWN:
                            LogUtils.d(TAG, "订单状态错误, current status:" + order.getOrderStatus());
                            return;
                    }
                    if (order.getOrderType() == Order.ORDER_TYPE_REALTIME
                            || order.getOrderType() == Order.ORDER_TYPE_REALTIME_CARPOOL
                            || order.getOrderType() == Order.ORDER_TYPE_SEND_CHILD
                            || order.getOrderType() == Order.ORDER_TYPE_CARGO_PASSENGER) {
                        //新的导航页
                        AMapNaviViewActivity.go(this, orderId);
                    } else {
                        startNaviActivity(null, destAddr);
                    }*/
                }
                break;
            case R.id.order_status_action:  //改变状态按钮
                presenter.changeOrderStatus(cargoOrderId, OrderDetailPresenter.OPERATAR_BY_DETAIL);
                break;
            case R.id.rl_realtime_contact:  //打电话
                callPassenger();
                break;
            /*case R.id.tv_send_phone_number:  //打收货人电话
               this,order.getPickupContactMobile();
                break;*/
            case R.id.tv_receive_phone_number_running: //打取货人电话
            case R.id.tv_receive_phone_number_running_fix:
            case R.id.iv_receive_phone_number:
                DialogUtils.showRealCallDialog(this, order.getDropOffContactMobile());
                break;
            case R.id.tv_send_cargo_action:  //去送货
                presenter.changeOrderStatus(cargoOrderId, OrderDetailPresenter.OPERATAR_BY_DETAIL);
                break;
            case R.id.tv_pay_offline:  //线下支付
                presenter.payConfirm(order.getOrderId(), PayInfo.OFFLINE_PAY_CONFIRM_PAIED);
                break;
            case R.id.iv_click_zoom:   //返回当前位置 按钮
                zoomToSpan();
                break;
            default:
                break;
        }
    }


    /**
     * 联系乘客
     */
    private void callPassenger() {
        if (!TextUtils.isEmpty(order.getUserInfo().getVirtualNum())) {
            DialogUtils.showVirtualCallDialog(this, order.getUserInfo().getVirtualNum());
        } else if (!TextUtils.isEmpty(order.getUserInfo().getPhoneNum())) {
            if (order.getChannel() != Order.ORDER_CHANNEL_MEITUAN) {
                DialogUtils.showRealCallDialog(this, order.getUserInfo().getPhoneNum());
            }
        } else {
            //订单未结束，虚拟号失效
            ConfirmDialog.newInstance().setMessage("暂时无法联系乘客，先联系客服试试吧")
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
    }

    @Override
    protected OrderDetailContract.Presenter onCreatePresenter() {
        carpoolFlag = getIntent().getBooleanExtra(EXTRA_CARPOOL_FLAG, false);
        return new OrderDetailPresenter(this, carpoolFlag);
    }

    /**
     * 获取客服电话成功
     *
     * @param phoneNum 客服电话号码
     */
    @Override
    public void getServiceTelNumberSuccess(String phoneNum, boolean isNeedShowDialog) {
        DialogUtils.showCustomerServiceCallDialog(this, phoneNum, isNeedShowDialog);
    }


    /**
     * 确认支付成功
     */
    @Override
    public void payConfirmSuccess() {
        paymentActionCompleted(order.getOrderId());
    }

    /**
     * 支付完成
     *
     * @param orderId
     */
    private void paymentActionCompleted(int orderId) {
        presenter.changeOrderStatus(cargoOrderId, OrderDetailPresenter.OPERATAR_BY_DETAIL);
    }

    /**
     * 获取订单详情出错
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
        if (orderInfo == null) {
            return;
        }
        order = orderInfo;
        orderId = order.getOrderId();
        rlStatusAction.setVisibility(View.VISIBLE);

        if (isNeedHandlerFromNotification) {
            //如果是点击通知进来的，且订单不处于这三个状态，toast"消息已过期"。
            if (!(order.getOrderStatus() == Order.ORDER_STATUS_ARRIVED_START_ADDR
                    || order.getOrderStatus() == Order.ORDER_STATUS_GET_ON
                    || order.getOrderStatus() == Order.ORDER_STATUS_WAIT_CAR)) {
                toast(R.string.message_expired);
                finish();
                return;
            }
            isNeedHandlerFromNotification = false;
        }

        //地址信息卡片的展示
        updateAddrInfoCardView();

        //各个订单类型单独处理显示View
        if (order.getOrderType() == Order.ORDER_TYPE_REALTIME
                || order.getOrderType() == Order.ORDER_TYPE_REALTIME_CARPOOL) { //实时单
            updateRealTimeOrderView();
        } else if (order.getOrderType() == Order.ORDER_TYPE_SEND_CHILD) { //小孩单
            updateChildOrderView();
        } else if (order.getOrderType() == Order.ORDER_TYPE_CARGO) { //货单
            updateCargoOrderView();
        } else if (order.getOrderType() == Order.ORDER_TYPE_CARGO_PASSENGER) { //货拼客单
            updateRealTimeOrderView();
            presenter.getCargoOrderSendDeadTime(order.getCargoOrderId());
        } else { //其他订单
            updateRealTimeOrderView();
        }

        //button显示。
        changeOrderStatusSuccess(order.getOrderStatus());

    }

    private void payOrderNow() {
        OrderPayInfo orderPayInfo = new OrderPayInfo();
        orderPayInfo.setCarpoolOrder(carpoolFlag);
        orderPayInfo.setOrderId(order.getOrderId());
        orderPayInfo.setCarpoolCode(carpoolCode);
        orderPayInfo.setOrderType(order.getOrderType());
        orderPayInfo.setOrderChannel(order.getChannel());
        orderPayInfo.setCost(order.getTotalMoney());
        PayMainActivity.start(this, orderPayInfo);
        finish();
    }

    /**
     * 更新货单收货 预计送达时间
     *
     * @param estimateArriveTimeStr
     */
    @Override
    public void getCargoRestTimeSuccess(String estimateArriveTimeStr) {
        if (TextUtils.isEmpty(estimateArriveTimeStr)) {
            LogUtils.e(TAG, "estimateArriveTimeStr is empty");
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

    /**
     * 地址信息栏目的展示
     * 包括 上下车地址，详细地址，导航按钮，货物类型icon，以及剩余距离时间是否显示。
     */
    private void updateAddrInfoCardView() {

        //地址
        tvAddressOn.setText(order.getStartAddr().getName());
        tvAddressOff.setText(order.getEndAddr().getName());

        if (carpoolFlag) {
            tvGetOnAddrSuffix.setText(String.format("接乘客(%s)", presenter.getChildPhoneSimple()));
        } else {
            tvGetOnAddrSuffix.setText("接乘客");
        }
        tvOrderGetOn.setText(order.getStartAddr().getName());
        tvOrderGetOff.setText(order.getEndAddr().getName());

        if (order.getOrderType() == Order.ORDER_TYPE_CARGO) {
            tvGetOffAddrPrefix.setText("送货到");
        } else {
            tvGetOffAddrPrefix.setText("送乘客到");
        }
        //详细地址
        tvGetOnDesc.setText(AMapUtil.getAddress(order.getStartAddr().getAddressDetail()));
        tvGetOffDesc.setText(AMapUtil.getAddress(order.getEndAddr().getAddressDetail()));

        //2.图标类型展示
        if (order.getOrderType() == Order.ORDER_TYPE_CARGO) { //货单
            ivOrderTypeIcon.setImageResource(R.mipmap.huo);
            ivOrderTypeIcon.setVisibility(View.VISIBLE);
        } else if (order.getOrderType() == Order.ORDER_TYPE_SEND_CHILD) {
            ivOrderTypeIcon.setImageResource(R.mipmap.xiaohai);
            ivOrderTypeIcon.setVisibility(View.GONE);
        } else if (order.getOrderType() == Order.ORDER_TYPE_CARGO_PASSENGER) {
            ivOrderTypeIcon.setImageResource(R.mipmap.ke);
            ivOrderTypeIcon.setVisibility(View.VISIBLE);
        } else {
            ivOrderTypeIcon.setVisibility(View.GONE);
        }

        //3.剩余距离信息是否展示
        if (order.getOrderStatus() == Order.ORDER_STATUS_WAIT_CAR
                || (order.getOrderStatus() == Order.ORDER_STATUS_GET_ON)) {
            llRestDistance.setVisibility(View.VISIBLE);
        } else {
            llRestDistance.setVisibility(View.GONE);
        }

        //4.地址卡片的显示和隐藏
        llOrderInitInfo.setVisibility(View.GONE);
        flOrderTime.setVisibility(View.VISIBLE);
        rlAddressInfo.setVisibility(View.VISIBLE);
        if (order.getOrderStatus() == Order.ORDER_STATUS_INIT
                || order.getOrderStatus() == Order.ORDER_STATUS_READY) {
            frGetOnAddr.setVisibility(View.VISIBLE);
            tvGetOnDesc.setVisibility(View.GONE);
            frGetOffAddr.setVisibility(View.VISIBLE);
            tvGetOffDesc.setVisibility(View.GONE);
            ivNaviAction.setVisibility(View.VISIBLE);
            //送你上学单，导航按钮隐藏
            if (order.getOrderType() == Order.ORDER_TYPE_SEND_CHILD) {
                ivNaviAction.setVisibility(View.GONE);
            }
        } else if (order.getOrderStatus() == Order.ORDER_STATUS_GET_ON) {
            frGetOnAddr.setVisibility(View.GONE);
            tvGetOnDesc.setVisibility(View.GONE);
            frGetOffAddr.setVisibility(View.VISIBLE);
            tvGetOffDesc.setVisibility(View.VISIBLE);
            ivNaviAction.setVisibility(View.VISIBLE);
        } else if (order.getOrderStatus() == Order.ORDER_STATUS_ARRIVED_START_ADDR) {
            frGetOnAddr.setVisibility(View.GONE);
            tvGetOnDesc.setVisibility(View.GONE);
            frGetOffAddr.setVisibility(View.VISIBLE);
            tvGetOffDesc.setVisibility(View.VISIBLE);
            ivNaviAction.setVisibility(View.GONE);
        } else if (order.getOrderStatus() == Order.ORDER_STATUS_WAIT_PAY
                || order.getOrderStatus() == Order.ORDER_STATUS_CLOSED
                || order.getOrderStatus() == Order.ORDER_STATUS_GET_OFF
                || order.getOrderStatus() == Order.ORDER_STATUS_CANCELED) {
            llOrderInitInfo.setVisibility(View.VISIBLE);
            flOrderTime.setVisibility(View.GONE);
            rlAddressInfo.setVisibility(View.GONE);
            ivNaviAction.setVisibility(View.GONE);
        } else {
            frGetOnAddr.setVisibility(View.VISIBLE);
            tvGetOnDesc.setVisibility(View.VISIBLE);
            frGetOffAddr.setVisibility(View.GONE);
            tvGetOffDesc.setVisibility(View.GONE);
            ivNaviAction.setVisibility(View.VISIBLE);
        }

    }


    /**
     * 货单信息的展示
     */
    private void updateCargoOrderView() {
        rlStatusAction.setVisibility(View.GONE);
        llCargoOrderInfo.setVisibility(View.VISIBLE);
        tvGoodsType.setText(order.getGoodsDescription());
        tvCargoOrderNumber.setText(order.getOrderCode());

        if (order.getOrderStatus() == Order.ORDER_STATUS_CLOSED ||
                order.getOrderStatus() == Order.ORDER_STATUS_GET_OFF) {  //货物已到达
            //正常情况下，货物已到达不会进入此页面，暂时保留观察
            //货单信息卡片
            rlCargoContact.setVisibility(View.GONE);
            //倒计时关闭
            mCargoRestTimeView.setVisibility(View.GONE);
            mCargoRestTimeView.stopCountDown();
            //3.总耗时
            if (order.getTotalTime() > 0) {
                tvOrderTotalTimeNew.setText(getString(R.string.order_detail_total_time_prefix_2) + BusinessUtils.formatEstimateTimeText(order.getTotalTime()));
                llTotalTime.setVisibility(View.VISIBLE);
            } else {
                llTotalTime.setVisibility(View.GONE);
            }

        } else {  //送货中
            rlCargoContact.setVisibility(View.VISIBLE);
            //货单相关电话
            tvReceivePhoneNumberRunning.setVisibility(View.VISIBLE);
            tvReceivePhoneNumberRunning.setText(order.getDropOffContactMobile().substring(7));
            //倒计时开启
            getCargoRestTimeSuccess(order.getEstimateArriveTime());
            // tvRestTimePrefix.setText(R.string.order_detail_rest_time_prefix);
        }
    }


    /**
     * 处理送你上学单 点击事件
     *
     * @param view
     */
    @OnClick({R.id.ll_child_contact, R.id.ll_child_parent_contact, R.id.ll_child_parent_contact2})
    public void onChildInfoViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_child_contact:
                if (order.getOrderStatus() == Order.ORDER_STATUS_GET_OFF
                        && order.getOrderSubStatus() == Order.ORDER_SUB_STATUS_GET_OFF_WAIT_PARENT_CONFIRM) {
                    presenter.getServiceTelNumber(true);  //联系客服
                } else {  //联系小孩
                    if (carpoolFlag) { // 拼车单显示所有小孩电话
                        List<String> childPhones = presenter.getChildPhones();
                        if (childPhones == null || childPhones.isEmpty()) {
                            toast("没有小孩信息");
                        } else {
                            ContactParentsChooser chooser = new ContactParentsChooser(getContext(), phoneNumber -> {
                                DialogUtils.showRealCallDialog(this, phoneNumber);
                            });
                            chooser.setData("联系小孩", childPhones);
                            chooser.showAtLocation(flChildContactContainer, Gravity.BOTTOM, 0, 0);
                        }
                    } else {
                        if (order.getChildList() == null || order.getChildList().isEmpty()) {
                            toast("没有小孩信息");
                        } else {
                            DialogUtils.showRealCallDialog(this, order.getChildList().get(0).getMobile());
                        }
                    }
                }
                break;
            case R.id.ll_child_parent_contact:  //联系家长
            case R.id.ll_child_parent_contact2:
                if (carpoolFlag) { // 拼车单显示所有家长电话
                    List<String> parentPhones = presenter.getParentPhones();
                    if (parentPhones == null || parentPhones.isEmpty()) {
                        toast("没有家长信息");
                    } else {
                        ContactParentsChooser chooser = new ContactParentsChooser(getContext(), phoneNumber -> {
                            DialogUtils.showRealCallDialog(getContext(), phoneNumber);
                        });
                        chooser.setData("联系家长", parentPhones);
                        chooser.showAtLocation(flChildContactContainer, Gravity.BOTTOM, 0, 0);
                    }
                } else {
                    DialogUtils.showRealCallDialog(this, order.getUserInfo().getPhoneNum());
                }
                break;
        }
    }


    /*********************************** EventBus 事件 ********************************************/
    /**
     * 处理eventbus发送过来的推送消息
     *
     * @param gpsData
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GpsData gpsData) {
        LogUtils.d(TAG, "on event, gpsData:" + gpsData.getLatitude() + "," + gpsData.getLongitude());
        currentGps = gpsData;
        //更新线路规划
        if (isOnResume) {
            showOrderInfoInMap(order);
        }
    }

    /**
     * 刷新数据
     *
     * @param data
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventMsg data) {
        LogUtils.d(TAG, "on event:" + data.toString());
        if (data.getCmd() == EventMsg.CMD_ORDER_ADDRESS_HAS_CHANGED) {
            if (data.getOrderId() == orderId) {
                refreshOrderDetails();
            }
        } /*else if (data.getCmd() == EventMsg.CMD_ORDER_STATUS_TO_CONTROVERSY) {
            //货拼客单，收到订单争议消息后，需要跳转到送货页面
            if (data.getOrderId() == orderId
                    && (order.getOrderType() == Order.ORDER_TYPE_CARGO_PASSENGER)
                    && (order.getOrderStatus() == Order.ORDER_STATUS_WAIT_PAY || order.getOrderStatus() == Order.ORDER_STATUS_CLOSED)) {
                presenter.getOrderDetail(order.getCargoOrderId());
            }
        }*/
    }


    /**
     * 为了减少 startActivityForResult，而作的处理
     *
     * @param bean
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBean bean) {
        if (bean.getCode() == Constants.EventCode.UPLOAD_CHILD_ON_PHOTO_SUCCESS) {
            //小孩上车照片上传成功，弹窗提示司机可以开始计费
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showTaxiMeterTipDialog(1);
                }
            }, 1000);

        } else if (bean.getCode() == Constants.EventCode.UPLOAD_CHILD_OFF_PHOTO_SUCCESS) {
            // 非拼车单，
            // 小孩下车图片上传成功，且到达目的地，家长免确认，直接跳转到支付页面
            if (!carpoolFlag && order.getGetOffAutoCheck() == 1) {
                payOrderNow();
            } else {
                refreshOrderDetails();
            }
        }
    }

    /**
     * 处理eventbus发送过来的推送消息--(货物已投递)
     * 消息流：
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(WebsocketData websocketData) {
        LogUtils.d(TAG, "on event:" + websocketData.toString());

        if (websocketData.getCmdSn() == WsCommands.GOODS_ORDER_DELIVERED.getSn()) {
            LogUtils.d(TAG, "货物已送达, orderId:" + orderId);
            if (orderId == websocketData.getData().getOrderId()) {
                TripDetailActivity.go(this, orderId, true, true);
                finish();
            }
        } else if (websocketData.getCmdSn() == WsCommands.DRIVER_CUSTOMER_ON_CONFIRM.getSn()) {
            toast(R.string.order_detail_confirmed_get_on_photos_by_parent);

            //状态转换：等待家长确认照片 -> 家长确认上车照片
            EventBus.getDefault().post(new EventBean(Constants.EventCode.VOICE_PROMPT_GO_TO_DESTINATION, null));

            refreshOrderDetails();
        } else if (websocketData.getCmdSn() == WsCommands.DRIVER_CUSTOMER_OFF_CONFIRM.getSn()) {
            if (websocketData.getData().getOrderId() == order.getOrderId()) {
                toast(R.string.order_detail_confirmed_get_off_photos_by_parent);
                payOrderNow();
            }
        } else if (websocketData.getCmdSn() == WsCommands.DRIVER_ORDER_ARRIVED_BY_SYSTEM.getSn()) {
            toast(R.string.order_detail_confirmed_get_off_photos_by_customer_service);
            payOrderNow();
        } else if (websocketData.getCmdSn() == WsCommands.DRIVER_ORDER_PAID.getSn()) {
            LogUtils.d(TAG, "支付完成");
            int orderId = websocketData.getData().getOrderId();
            if (order != null && orderId == order.getOrderId()) {
                refreshOrderDetails();
            }
        }
    }

    /*********************************** EventBus 事件 ********************************************/

    /**
     * 司机可以开始计费提示弹窗
     * 时机：已经到用车时间 or 小孩已上车
     *
     * @param type 0-已经到用车时间  1-小孩已上车
     */
    private void showTaxiMeterTipDialog(int type) {
        if (order.getOrderType() == Order.ORDER_TYPE_SEND_CHILD) {
            return;  // #1003809，2020.4.23 送小孩单不再需要弹出该对话框
        }
        ConfirmDialog dialog = ConfirmDialog.newInstance();
        if (type == 0) {
            dialog.setMessage(getString(R.string.dialog_msg_calculate_fee_1));
        } else {
            dialog.setMessage(getString(R.string.dialog_msg_calculate_fee_2));
        }
        dialog.setType(ConfirmDialog.TYPE_ONE_BUTTON)
                .setPositiveText(R.string.i_got_it)
                .show(getSupportFragmentManager(), "");
        cancelTimingReminder();
/*
        InfoDialog dialog = InfoDialog.newInstance();
        dialog.setOnClickListener(new InfoDialog.OnClickListener() {

            @Override
            public void onPositiveClick(View view) {
                super.onPositiveClick(view);
            }

            @Override
            public void onNegativeClick(View view) {
                super.onNegativeClick(view);
            }

            @Override
            public void onCloseDialog(View view) {
                super.onCloseDialog(view);
                dialog.dismiss();
            }

            @Override
            public void onConfirm(View view) {
                super.onConfirm(view);
                dialog.dismiss();
            }
        });
        if (type == 0) {
            dialog.setMessage(getString(R.string.dialog_msg_calculate_fee_1));
        } else {
            dialog.setMessage(getString(R.string.dialog_msg_calculate_fee_2));
        }
        dialog.setPositiveText(R.string.i_got_it);
        dialog.show(getSupportFragmentManager(), "");
        cancelTimingReminder();*/
    }

    private boolean isNeedShowTrueLang; //是否需要显示真实轨迹

    /**
     * 在地图中展示起点和终点信息
     *
     * @param order --订单详情
     */
    private void showOrderInfoInMap(Order order) {
        if (order == null) {
            LogUtils.e(TAG, "showOrderInfoInMap, order is null");
            return;
        }
        AddressInfo startAddr = null;
        AddressInfo endAddr = null;
        int status = order.getOrderStatus();
        if (status == Order.ORDER_STATUS_WAIT_CAR
                || status == Order.ORDER_STATUS_ARRIVED_START_ADDR) {
            startAddr = null;
            endAddr = order.getStartAddr();
            mWayPoints = null;
        } else if (status == Order.ORDER_STATUS_GET_ON) {
            startAddr = null;
            endAddr = order.getEndAddr();
            mWayPoints = null;
        } else if (status == Order.ORDER_STATUS_GET_OFF
                || (status == Order.ORDER_STATUS_WAIT_PAY)
                || (status == Order.ORDER_STATUS_CLOSED)
                || (status == Order.ORDER_STATUS_READY)
                || (status == Order.ORDER_STATUS_INIT)) {
            if (carpoolFlag) { // 拼车单需设置途径点
                if (presenter.isCarpoolOrderStared()) {
                    LogUtils.d("拼车单已经开始");
                    startAddr = null;
                    endAddr = order.getStartAddr();
                    mWayPoints = null;

                } else { // 所有订单均未开始
                    List<AddressInfo> carpoolPath = presenter.getCarpoolPath();
                    mWayPoints = new ArrayList<>();
                    for (int i = 0; i < carpoolPath.size(); i++) {
                        if (i == 0) {
                            startAddr = carpoolPath.get(i);
                        } else if (i == carpoolPath.size() - 1) {
                            endAddr = carpoolPath.get(i);
                        } else {
                            LatLng latLng = carpoolPath.get(i).getLatLng();
                            LatLonPoint latLonPoint = new LatLonPoint(
                                    latLng.latitude, latLng.longitude
                            );
                            mWayPoints.add(latLonPoint);
                        }
                    }
                    LogUtils.d("拼车单尚未开始");
                }
            } else {
                startAddr = order.getStartAddr();
                endAddr = order.getEndAddr();
                mWayPoints = null;
            }
        } else {
            LogUtils.e(TAG, "订单状态异常,当前状态：" + status);
            return;
        }
        if (startAddr != null) {
            mStartPoint = new LatLonPoint(order.getStartAddr().getLatLng().latitude,
                    order.getStartAddr().getLatLng().longitude);
        } else {
            //当前车辆位置
            if (currentGps == null) {
                LogUtils.e(TAG, "current gps is null");
                return;
            }
            mStartPoint = new LatLonPoint(currentGps.getLatitude(), currentGps.getLongitude());
        }
        mEndPoint = new LatLonPoint(endAddr.getLatLng().latitude, endAddr.getLatLng().longitude);

        if (order.getOrderType() == Order.ORDER_TYPE_SEND_CHILD &&
                order.getOrderStatus() == Order.ORDER_STATUS_GET_OFF) {
            if (isNeedShowTrueLang) {
                //小孩单，乘客已下车，显示真实轨迹路线
                mAMap.clear();
                routeOverlay.removeFromMap();
                routeOverlay.setStartAndEndIcon(resIdStart, resIdEnd);
                routeOverlay.showTrueLatLng(order.getStartAddr(), order.getEndAddr(), order.getTrackCoordinateList());
                zoomToSpan();
                isNeedShowTrueLang = false;
            }
        } else {
            //开始路线规划
            searchRoute();
        }
    }

    /**
     * 更新订单状态的按钮 & 地图展示 & header信息
     *
     * @param status
     */
    @Override
    public void changeOrderStatusSuccess(int status) {
        int orderType = order.getOrderType();
        if (orderType == Order.ORDER_TYPE_CARGO_PASSENGER
                || orderType == Order.ORDER_TYPE_REALTIME
                || orderType == Order.ORDER_TYPE_REALTIME_CARPOOL
                || orderType == Order.ORDER_TYPE_BOOK
                || orderType == Order.ORDER_TYPE_ACCESSIBILITY
        ) {
            if (status == Order.ORDER_STATUS_WAIT_CAR || status == Order.ORDER_STATUS_READY) {
                // 检查订单是否可以开始
                if (checkOrderTime(order)) {
                    slideview.setVisibility(View.VISIBLE);
                    tvHeaderName.setText(R.string.order_detail_to_pick_up_passengers);
                    tvOrderStatusAction.setText(R.string.arrive_at_get_on_address);
                } else {
                    slideview.setVisibility(View.GONE);

                    SpannableStringBuilder statusTextBuilder = new SpannableStringBuilder();
                    SpannableString arriveText = new SpannableString(
                            getString(R.string.arrive_at_get_on_address));
                    arriveText.setSpan(new AbsoluteSizeSpan(SizeUtil.sp2px(20)),
                            0, arriveText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    statusTextBuilder.append(arriveText);

                    statusTextBuilder.append("\n");

                    SpannableString notYetText = new SpannableString(
                            getString(R.string.arrive_at_get_on_address_not_yet));
                    notYetText.setSpan(new AbsoluteSizeSpan(SizeUtil.sp2px(16)),
                            0, notYetText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    statusTextBuilder.append(notYetText);

                    tvOrderStatusAction.setText(statusTextBuilder);
                    tvOrderStatusAction.setVisibility(View.VISIBLE);
                }

            } else if (status == Order.ORDER_STATUS_ARRIVED_START_ADDR) {
                tvHeaderName.setText(R.string.waiting_for_passengers_to_get_on);
                tvOrderStatusAction.setText(R.string.receiving_passengers);
            } else if (status == Order.ORDER_STATUS_GET_ON) {
                tvHeaderName.setText(R.string.to_deliver_passengers);
                tvOrderStatusAction.setText(R.string.arrive_at_get_off_address);
            } else if (status == Order.ORDER_STATUS_GET_OFF) {
                //已到达目的地 -> 去支付
                payOrderNow();
                return;
            } else if (status == Order.ORDER_STATUS_WAIT_PAY) {
                tvOrderStatusAction.setText(R.string.to_deliver_goods);
                tvHeaderName.setText(R.string.passenger_not_paid);
                if (orderType == Order.ORDER_TYPE_CARGO_PASSENGER) {
                    //同时显示支付和送货按钮,去掉滑动按钮的显示
                    llCargoAction.setVisibility(View.VISIBLE);
                    slideview.setVisibility(View.GONE);
                }
            } else if (status == Order.ORDER_STATUS_CLOSED) {
                if (orderType == Order.ORDER_TYPE_CARGO_PASSENGER) {
                    llCargoAction.setVisibility(View.VISIBLE);
                    tvPayOffline.setVisibility(View.GONE);
                }
                tvOrderStatusAction.setText(R.string.to_deliver_goods);
                tvHeaderName.setText(R.string.order_completed);
            } else if (status == Order.ORDER_STATUS_CANCELED) {
                tvOrderStatusAction.setText(R.string.order_cancelled);
                tvHeaderName.setText(R.string.order_cancelled);
                goMainActivity();
            } else {
                LogUtils.e(TAG, "订单状态异常，orderStatus = " + status);
                toast(R.string.toast_get_order_info_fail);
                return;
            }
        } else if (orderType == Order.ORDER_TYPE_CARGO) { //货单
            //去掉送货的按钮显示
            llCargoAction.setVisibility(View.GONE);

            tvOrderStatusAction.setVisibility(View.GONE);
            slideview.setVisibility(View.GONE);
            if (status == Order.ORDER_STATUS_WAIT_CAR || status == Order.ORDER_STATUS_READY) {
                tvOrderStatusAction.setText(R.string.to_deliver_goods);
                tvHeaderName.setText(R.string.delivering);
            } else if (status == Order.ORDER_STATUS_ARRIVED_START_ADDR) {
                tvHeaderName.setText(R.string.arrival_at_receiving_place);
                tvOrderStatusAction.setText(R.string.delivering);
            } else if (status == Order.ORDER_STATUS_GET_ON) {
                tvOrderStatusAction.setText(R.string.arrive_at_get_off_address);
                tvHeaderName.setText(R.string.delivering);
            } else if (status == Order.ORDER_STATUS_GET_OFF) {
                tvHeaderName.setText(R.string.goods_delivered);
            } else if (status == Order.ORDER_STATUS_CLOSED) {
                tvHeaderName.setText(R.string.goods_delivered);
            } else if (status == Order.ORDER_STATUS_CANCELED) {
                tvOrderStatusAction.setText(R.string.order_cancelled);
                tvHeaderName.setText(R.string.order_cancelled);
                goMainActivity();
            } else {
                LogUtils.e(TAG, "订单状态异常，orderStatus = " + status);
                toast(R.string.toast_get_order_info_fail);
                return;
            }
        } else if (orderType == Order.ORDER_TYPE_SEND_CHILD) { //送你上学单
            tvOrderStatusAction.setVisibility(View.GONE);
            slideview.setVisibility(View.VISIBLE);
            if (status == Order.ORDER_STATUS_READY) {
                if (carpoolFlag) {
                    String statusText = getString(R.string.format_order_detail_to_pick_up_passengers_carpool_order,
                            order.getStartAddr().getName(), presenter.getChildPhoneSimple());
                    tvOrderStatusAction.setText(statusText);
                } else {
                    tvOrderStatusAction.setText(R.string.order_detail_to_pick_up_passengers);
                }
                tvHeaderName.setText(R.string.order_info);
            } else if (status == Order.ORDER_STATUS_WAIT_CAR) {
                if (carpoolFlag) {
                    tvOrderStatusAction.setText(getString(R.string.format_arrive_at_get_on_address_carpool_order,
                            order.getStartAddr().getName()));
                } else {
                    tvOrderStatusAction.setText(R.string.arrive_at_get_on_address);
                }
                tvHeaderName.setText(R.string.order_detail_to_pick_up_passengers);
            } else if (status == Order.ORDER_STATUS_ARRIVED_START_ADDR) {
                childGetOnOffConfirm();
            } else if (status == Order.ORDER_STATUS_GET_ON) {
                if (carpoolFlag) {
                    tvOrderStatusAction.setText(getString(R.string.format_take_a_picture_of_passengers_get_off_carpool_order,
                            presenter.getChildPhoneSimple()));
                } else {
                    tvOrderStatusAction.setText(R.string.take_a_picture_of_passengers_get_off);
                }
                tvHeaderName.setText(R.string.to_deliver_passengers);
            } else if (status == Order.ORDER_STATUS_GET_OFF) {
                childGetOnOffConfirm();
            } else if (status == Order.ORDER_STATUS_WAIT_PAY) {
                tvOrderStatusAction.setText(R.string.to_pay);
                tvHeaderName.setText(R.string.parents_not_pay);
            } else if (status == Order.ORDER_STATUS_CLOSED) {
                tvOrderStatusAction.setText(R.string.order_completed);
                tvHeaderName.setText(R.string.order_completed);
            } else if (status == Order.ORDER_STATUS_CANCELED) {
                tvOrderStatusAction.setText(R.string.order_cancelled);
                tvHeaderName.setText(R.string.order_cancelled);
                goMainActivity();
            } else {
                LogUtils.e(TAG, "订单状态异常，orderStatus = " + status);
                toast(R.string.toast_get_order_info_fail);
                return;
            }
        }

        isUpdateMapByRefreshData = true;
        showOrderInfoInMap(order);

        //实时订单/货拼客单，去接乘客滑动特殊显示
        if ((orderType == Order.ORDER_TYPE_REALTIME
                || orderType == Order.ORDER_TYPE_REALTIME_CARPOOL
                || orderType == Order.ORDER_TYPE_CARGO_PASSENGER)
                && order.getOrderStatus() == Order.ORDER_STATUS_ARRIVED_START_ADDR) {
            slideview.setBgAndIconResId(R.drawable.btn_bg_enable2, R.mipmap.btn_slide2);
        } else {
            slideview.setBgAndIconResId(R.drawable.btn_bg_enable, R.mipmap.btn_slide);
        }
        slideview.setBackgroundText(tvOrderStatusAction.getText().toString());
        slideview.reset();
    }

    private boolean checkOrderTime(@NonNull Order order) {
        if (order.getOrderType() != Order.ORDER_TYPE_BOOK ||
                order.getOrderType() != Order.ORDER_TYPE_ACCESSIBILITY) {
            return true;
        }
        if (order.getOrderStatus() != Order.ORDER_STATUS_READY) {
            return true;
        }
        long milliSecond;
        try {
            milliSecond = BusinessUtils.stringToLong(
                    order.getOrderTime(), "yyyy-MM-dd HH:mm");
        } catch (ParseException e) {
            e.printStackTrace();
            return true;
        }
        long currentMills = System.currentTimeMillis();
        return currentMills > milliSecond ||
                milliSecond - currentMills < TimeUnit.HOURS.toMillis(2);
    }

    /**
     * 小孩订单，需要给家长拍照确认
     */
    private void childGetOnOffConfirm() {
        tvOrderStatusAction.setVisibility(View.GONE);
        slideview.setVisibility(View.VISIBLE);
        //上车确认
        if (order.getOrderStatus() == Order.ORDER_STATUS_ARRIVED_START_ADDR) {
            if (order.getOrderSubStatus() == Order.ORDER_SUB_STATUS_ARRIVED_START_ADDR) {
                tvHeaderName.setText(R.string.waiting_for_passengers_to_get_on);
                if (carpoolFlag) {
                    tvOrderStatusAction.setText(getString(R.string.format_take_a_picture_of_passengers_get_on_carpool_order,
                            presenter.getChildPhoneSimple()));
                } else {
                    tvOrderStatusAction.setText(R.string.take_a_picture_of_passengers_get_on);
                }
                tvOrderStatusAction.setVisibility(View.GONE);
                slideview.setVisibility(View.VISIBLE);
                startTimingReminder();
            } else if (order.getOrderSubStatus() == Order.ORDER_SUB_STATUS_ARRIVED_START_ADDR_WAIT_PARENT_CONFIRM) {
                tvHeaderName.setText(R.string.waiting_for_parents_to_confirm);
                tvOrderStatusAction.setText(R.string.waiting_for_parents_to_confirm_the_get_on_photo);
                tvOrderStatusAction.setVisibility(View.VISIBLE);
                slideview.setVisibility(View.GONE);
            }
        }

        //下车确认
        if (order.getOrderStatus() == Order.ORDER_STATUS_GET_OFF) {
            if (order.getOrderSubStatus() == Order.ORDER_SUB_STATUS_GET_OFF) {
                tvHeaderName.setText(R.string.to_deliver_passengers);
                tvOrderStatusAction.setText(R.string.take_a_picture_of_passengers_get_off);
                tvOrderStatusAction.setVisibility(View.GONE);
                slideview.setVisibility(View.VISIBLE);
            } else if (order.getOrderSubStatus() == Order.ORDER_SUB_STATUS_GET_OFF_WAIT_PARENT_CONFIRM) {
                tvHeaderName.setText(R.string.waiting_for_parents_to_confirm);
                tvOrderStatusAction.setText(R.string.waiting_for_parents_to_confirm_the_get_off_photo);
                tvOrderStatusAction.setVisibility(View.VISIBLE);
                slideview.setVisibility(View.GONE);
            } else if (order.getOrderSubStatus() == Order.ORDER_SUB_STATUS_GET_OFF_PARENT_CONFIRMED) {
                payOrderNow();
            } else {
                //tvHeaderName.setText("到达目的地");
                tvHeaderName.setText(R.string.to_deliver_passengers);
                tvOrderStatusAction.setText(R.string.take_a_picture_of_passengers_get_off);
                tvOrderStatusAction.setVisibility(View.GONE);
                slideview.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 改变订单状态失败
     */
    @Override
    public void changeOrderStatusFail() {
        slideview.reset();
    }

    /**
     * 调用高德导航组件，进入到高德导航的activity, 此acitivity需要在AndroidManifest.xml中申明。
     */
    private void startNaviActivity(AddressInfo startAddress, AddressInfo endAddress) {
        LogUtils.d(TAG, "startNaviActivity, endAddress:" + endAddress.toString());

        /**终点传入的是北京站坐标,但是POI的ID "B000A83M61"对应的是北京西站，所以实际算路以北京西站作为终点**/
        /**Poi支持传入经纬度和PoiID，PoiiD优先级更高，使用Poiid算路，导航终点会更合理**/
//        Poi end = new Poi("深圳宝安国际机场", new LatLng(22.633285, 113.813576), "B000A83M61");
        Poi start = null;
        if (startAddress != null) {
            start = new Poi(startAddress.getName(), startAddress.getLatLng(), "");
        }
        Poi end = new Poi(endAddress.getName(), endAddress.getLatLng(), "");

        AmapNaviParams params = new AmapNaviParams(start, null, end, AmapNaviType.DRIVER, AmapPageType.NAVI);
        params.setUseInnerVoice(true);
        INaviInfoCallback iNaviInfoCallback = new NaviCallback();
        AmapNaviPage.getInstance().showRouteActivity(getApplicationContext(), params, iNaviInfoCallback);
    }


    public static class NaviCallback implements INaviInfoCallback {
        @Override
        public void onInitNaviFailure() {

        }

        @Override
        public void onGetNavigationText(String s) {

        }

        @Override
        public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

        }

        @Override
        public void onArriveDestination(boolean b) {

        }

        @Override
        public void onStartNavi(int i) {

        }

        @Override
        public void onCalculateRouteSuccess(int[] ints) {

        }

        @Override
        public void onCalculateRouteFailure(int i) {

        }

        @Override
        public void onStopSpeaking() {

        }

        @Override
        public void onReCalculateRoute(int i) {

        }

        @Override
        public void onExitPage(int i) {

        }

        @Override
        public void onStrategyChanged(int i) {

        }

        @Override
        public View getCustomNaviBottomView() {
            return null;
        }

        @Override
        public View getCustomNaviView() {
            return null;
        }

        @Override
        public void onArrivedWayPoint(int i) {

        }

        @Override
        public void onMapTypeChanged(int i) {

        }

        @Override
        public View getCustomMiddleView() {
            return null;
        }

        @Override
        public void onNaviDirectionChanged(int i) {

        }

        @Override
        public void onDayAndNightModeChanged(int i) {

        }

        @Override
        public void onBroadcastModeChanged(int i) {

        }

        @Override
        public void onScaleAutoChanged(boolean b) {

        }
    }

    /**
     * 处理路线规划,只需关注于业务逻辑代码
     *
     * @param driveRouteResult
     * @param i
     */
    @Override
    public void handleDriveRouteSearchedResult(DriveRouteResult driveRouteResult, int i) {
        Log.e(TAG, "路线规划成功");

        //1.起点和终点Marker的显示
        if (order.getOrderStatus() == Order.ORDER_STATUS_WAIT_CAR
                || order.getOrderStatus() == Order.ORDER_STATUS_ARRIVED_START_ADDR
                || order.getOrderStatus() == Order.ORDER_STATUS_INIT
                || (presenter.isCarpoolOrderStared() && // 拼车单并且已开始，当前单乘客未上车
                order.getOrderStatus() == Order.ORDER_STATUS_READY)
        ) {
            if (currentGps == null) {
                return;
            }
            //乘客上车前，起点是车辆当前位置，终点是乘客上车点，显示车辆朝向
            routeOverlay.setCarAndEndIcon(resIdCar, resIdStart, currentGps.getBearing()); //设置起点和终点的图标
            routeOverlay.setStartAndEndAddress("", order.getStartAddr().getName());
        } else if (order.getOrderStatus() == Order.ORDER_STATUS_GET_ON) {
            //乘客上车后，未下车前，起点是车辆当前位置，终点是乘客下车点，显示车辆朝向
            if (order.getOrderType() == Order.ORDER_TYPE_CARGO) {
                resIdEnd = R.mipmap.ic_marker_goods_receive;
            }
            if (currentGps == null) {
                return;
            }
            routeOverlay.setCarAndEndIcon(resIdCar, resIdEnd, currentGps.getBearing()); //设置起点和终点的图标
            routeOverlay.setStartAndEndAddress("", order.getEndAddr().getName());
        } else {
            //订单完成，显示上下车点，不显示车辆
            routeOverlay.setStartAndEndIcon(resIdStart, resIdEnd); //设置起点和终点的图标
            routeOverlay.setStartAndEndAddress(order.getStartAddr().getName(), order.getEndAddr().getName());
        }

        //2.是否显示路线
        if (order.getOrderStatus() == Order.ORDER_STATUS_ARRIVED_START_ADDR) {
            //等待乘客上车时不显示
            routeOverlay.setShowPolyline(false);
        } else {
            routeOverlay.setShowPolyline(true);
        }

        //3.是否显示实时路线
        if (order.getOrderType() == Order.ORDER_TYPE_SEND_CHILD) {
            //小孩单，初始状态，不显示实时路线
            if (order.getOrderStatus() == Order.ORDER_STATUS_INIT ||
                    order.getOrderStatus() == Order.ORDER_STATUS_READY) {
                routeOverlay.setColorFulLine(false);
            } else {
                routeOverlay.setColorFulLine(true);
            }
        } else {
            routeOverlay.setColorFulLine(true);
        }

        //4.起点和终点Marker是否需要更新
        if (isUpdateMapByRefreshData) {
            routeOverlay.setUpdateStartMarker(true)
                    .setUpdateEndMarker(true);
        } else {
            routeOverlay.setUpdateStartMarker(false)
                    .setUpdateEndMarker(false);
        }

        //更新剩余距离 和 时间
        updateDistanceView(driveRouteResult.getPaths().get(0));
    }

    /**
     * 显示范围
     */
    @Override
    public void zoomToSpan() {
        llBottomView.post(new Runnable() {
            @Override
            public void run() {
                // Log.e(TAG, "zoomToSpan:llBottomView.height = " + llBottomView.getHeight() + ",height=" + SizeUtil.getScreenHeight(getContext()));
                routeOverlay.zoomToSpan(200, 200, 300, llBottomView.getHeight());
            }
        });
    }

    private double distance;      //距离x米

    /**
     * 更新路线距离 View
     *
     * @param drivePath
     */
    public void updateDistanceView(DrivePath drivePath) {
        //Log.e(TAG, "测量距离：" + drivePath.getDistance() + "   " + "测量时间:" + drivePath.getDuration());
        distance = drivePath.getDistance();
        //剩余距离
        if (distance > 1000) {
            tvRestDistance.setText(String.format(Locale.getDefault(), "%.1f", distance / 1000));
            tvRestDistanceUnit.setText(R.string.km);
        } else {
            tvRestDistance.setText(String.format(Locale.getDefault(), "%.0f", distance));
            tvRestDistanceUnit.setText(R.string.m);
        }
        //剩余时间
        updatePathRetainTime(drivePath.getDuration());

        //实时订单 or 货拼客单，如果预计到达时间点为空，则传一个时间给后台
        if (order.getOrderType() == Order.ORDER_TYPE_REALTIME ||
                order.getOrderType() == Order.ORDER_TYPE_REALTIME_CARPOOL ||
                order.getOrderType() == Order.ORDER_TYPE_CARGO_PASSENGER ||
                order.getOrderType() == Order.ORDER_TYPE_BOOK ||
                order.getOrderType() == Order.ORDER_TYPE_ACCESSIBILITY
        ) {
            if (TextUtils.isEmpty(order.getEstimatePickUpTime()) &&
                    (order.getOrderStatus() == Order.ORDER_STATUS_WAIT_CAR || order.getOrderStatus() == Order.ORDER_STATUS_READY)) {
                long timeStamp = (long) (System.currentTimeMillis() + drivePath.getDuration() * 1000);
                //手动设置订单预计到达上车点时间
                order.setEstimatePickUpTime(timeStamp + "");
                presenter.setEstimatePickUpTime(orderId, timeStamp);
                //显示
                String time = BusinessUtils.getDataHm(Long.parseLong(order.getEstimatePickUpTime()));
                String str = getString(R.string.order_detail_tips_arrive_at_get_on_before_time, time);
                SpannableString spannableString = SpannableStringUtil.getSpanString(str, 3, time.length() + 4, R.color.maas_text_blue, Typeface.BOLD, 1f);

                tvOrderTips.setText(spannableString);
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        isOnResume = true;
        //先获取当前的位置
        currentGps = FloatDialogService.getCurrentGps();
        refreshOrderDetails();
        MyApplication.setOrderIdRunning(orderId);
    }

    private void refreshOrderDetails() {
        if (carpoolFlag) {
            presenter.getCarpoolOrderDetails(carpoolCode);
        } else {
            presenter.getOrderDetail(orderId);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isOnResume = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopOrderCountdownTimer();
    }

    @Override
    protected void onDestroy() {
        if (mCargoRestTimeView != null) {
            mCargoRestTimeView.onDestroy();
        }
        super.onDestroy();
        MyApplication.setOrderIdRunning(-1);
        EventBus.getDefault().unregister(this);
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }


    /**
     * 开始打表计时提醒
     * 如果当前时间已经到用车时间，提示司机可以开始打表计时。
     */
    private void startTimingReminder() {
        try {
            long orderTime = BusinessUtils.stringToLong(order.getOrderTime(), DateUtils.FORMAT_PATTERN_YMDHM);
            if (orderTime <= System.currentTimeMillis()) {  //超过预约时间,立即显示
                showTaxiMeterTipDialog(0);
                return;
            }
            //到预约时间，再显示
            long delayMills = orderTime - System.currentTimeMillis();
            handler.sendEmptyMessageDelayed(HANDLER_MSG_TIMING_REMINDER, delayMills);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * 取消计时提醒
     */
    private void cancelTimingReminder() {
        if (handler != null) {
            handler.removeMessages(HANDLER_MSG_TIMING_REMINDER);
        }
    }


    private ConfirmDialog mArriveDialog;  //到达上车点 or 到达目的地提示弹窗

    /**
     * 处理 实时订单/货拼客单 的显示
     */
    private void updateRealTimeOrderView() {
        handler.removeMessages(HANDLER_MSG_WAITING_PASSENGER_COUNTING_TIME);
        tvTitleRight.setVisibility(View.VISIBLE);
        //实时订单tips View
        if (order.getOrderStatus() == Order.ORDER_STATUS_WAIT_CAR || order.getOrderStatus() == Order.ORDER_STATUS_READY) {
            //去接乘客，显示预计到达上车点时间tips
            llOrderTips.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(order.getEstimatePickUpTime()) && order.getEstimatePickUpTime().length() > 11) {
                String time;
                if (order.getOrderType() == Order.ORDER_TYPE_BOOK ||
                        order.getOrderType() == Order.ORDER_TYPE_ACCESSIBILITY) { // 增加无障碍订单
                    time = order.getOrderTime().split(" ")[1];
                } else {
                    time = order.getEstimatePickUpTime().substring(11);
                }
                String str = getString(R.string.order_detail_tips_arrive_at_get_on_before_time, time);
                SpannableString spannableString = SpannableStringUtil.getSpanString(str, 3, time.length() + 4, R.color.maas_text_blue, Typeface.BOLD, 1f);
                tvOrderTips.setText(spannableString);
            }
        } else if (order.getOrderStatus() == Order.ORDER_STATUS_ARRIVED_START_ADDR) {
            //到达上车点，显示等待乘客上车tips，且开启倒计时
            llOrderTips.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(order.getBoardingPlaceArriveTime())) {
                try {
                    //把到达时间转为时间戳
                    long milliSecond = BusinessUtils.stringToLong(order.getBoardingPlaceArriveTime(), DateUtils.FORMAT_PATTERN_YMDHMS);
                    //用当前时间戳 - 到达时间戳,由于是本地当前时间，可能为负数
                    mWaitingTimeMillis = System.currentTimeMillis() - milliSecond;
                    if (mWaitingTimeMillis < 0) {
                        mWaitingTimeMillis = 0;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(HANDLER_MSG_WAITING_PASSENGER_COUNTING_TIME);
            }
        } else {
            llOrderTips.setVisibility(View.GONE);
        }
        //联系电话
        rlRealtimeContact.setVisibility(View.VISIBLE);
        if (order.getOrderStatus() == Order.ORDER_STATUS_GET_ON) {
            rlRealtimeContact.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(order.getUserInfo().getPhoneNum())) {
            if (order.getUserInfo().getPhoneNum().length() > 7) {
                tvRealtimeContactNumber.setText(order.getUserInfo().getPhoneNum().substring(7));
            } else {
                tvRealtimeContactNumber.setText(order.getUserInfo().getPhoneNum());
            }
        }
    }

    /**
     * 处理 实时订单 滑动按钮逻辑
     */
    private void handleRealTimeOrderSlide() {
        //实时订单，到达上车点和到达目的地前显示弹窗
        if (order.getOrderStatus() == Order.ORDER_STATUS_WAIT_CAR || order.getOrderStatus() == Order.ORDER_STATUS_READY) {
            //到达上车点
            String msg = getString(linearDistance > 500 ? R.string.get_on_location_more_than_limit_tips : R.string.get_on_location_confirm_tips);
            showArriveDialog(msg);
        } else if (order.getOrderStatus() == Order.ORDER_STATUS_GET_ON) {
            //到达下车点，且距离超过500
            if (linearDistance > 500) {
                showArriveDialog(getString(R.string.get_off_location_more_than_limit_tips));
            } else {
                presenter.changeOrderStatus(cargoOrderId, OrderDetailPresenter.OPERATAR_BY_DETAIL);
            }
        } else {
            presenter.changeOrderStatus(cargoOrderId, OrderDetailPresenter.OPERATAR_BY_DETAIL);
        }
    }

    ///////////////////////////////////////////////
    // 送你上学确认上/下车等待倒计时
    private Disposable mOrderCountdownTimer;

    private void stopOrderCountdownTimer() {
        if (mOrderCountdownTimer != null && !mOrderCountdownTimer.isDisposed()) {
            mOrderCountdownTimer.dispose();
            mOrderCountdownTimer = null;
        }
    }

    private void startOrderCountDownTimer(long onPicTime) {
        if (!verifyChildOrderStatus()) {
            LogUtils.d("订单状态不对");
            return;
        }
//        OrderTimeStore orderTimeStore = OrderTimeStore.getInstance(getContext());
//        final Long countdownSeconds = orderTimeStore.orderCountdownTime((long) orderId);
//        LogUtils.d("查询保存的剩余秒数：" + countdownSeconds);
//        if (countdownSeconds == null || countdownSeconds <= 0) { // 已经到时间，但是状态还没改过来
//            LogUtils.d("已经到时间，订单状态仍未改变");
//            refreshOrderCountdownTime(0L);
//            return;
//        }
        final long countdownSeconds = (onPicTime - System.currentTimeMillis()) / 1000;
        if (countdownSeconds <= 0) {
            LogUtils.d("时间不正确，已过确认时间");
            return;
        }
        stopOrderCountdownTimer();
        mOrderCountdownTimer = Observable.interval(1, TimeUnit.SECONDS)
                .take(countdownSeconds)
                .map(v -> countdownSeconds - v)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long remainingSeconds) throws Exception {
                        LogUtils.d("剩余秒数：" + remainingSeconds);
                        CharSequence countdownText = getCountdownText(order, remainingSeconds);
                        if (TextUtils.isEmpty(countdownText)) {
                            llOrderTips.setVisibility(View.GONE);
                        } else {
                            tvOrderTips.setText(countdownText);
                            llOrderTips.setVisibility(View.VISIBLE);
                        }
                    }
                }, throwable -> {
                    LogUtils.e("计时出错：" + throwable.getMessage());
                    throwable.printStackTrace();
                }, new Action() {
                    @Override
                    public void run() throws Exception {
//                        orderTimeStore.removeOrderTime((long) orderId);
                        LogUtils.d("倒计时结束！");
                        refreshOrderDetails();
                    }
                });
    }

    private CharSequence getCountdownText(Order order, long remainingSeconds) {
        int minutes = (int) (remainingSeconds / 60);
        int seconds = (int) (remainingSeconds % 60);
        String countdownText = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        String line1 = null;
        String line2 = null;
        if (order.getOrderStatus() == Order.ORDER_STATUS_ARRIVED_START_ADDR &&
                order.getOrderSubStatus() == Order.ORDER_SUB_STATUS_ARRIVED_START_ADDR_WAIT_PARENT_CONFIRM) {
            // 上车确认
            if (carpoolFlag) {
                String userPhone = order.getUserInfo().getPhoneNum();
                line1 = "等待家长(" + userPhone.substring(userPhone.length() - 4) + ")确认上车照片 " + countdownText;
            } else {
                line1 = "等待家长确认上车照片 " + countdownText;
            }
            line2 = "倒计时结束后将自动确认上车";

        } else if (order.getOrderStatus() == Order.ORDER_STATUS_GET_OFF &&
                order.getOrderSubStatus() == Order.ORDER_SUB_STATUS_GET_OFF_WAIT_PARENT_CONFIRM) {
            // 下车确认
            if (carpoolFlag) {
                String userPhone = order.getUserInfo().getPhoneNum();
                line1 = "等待家长(" + userPhone.substring(userPhone.length() - 4) + ")确认下车照片 " + countdownText;
            } else {
                line1 = "等待家长确认下车照片 " + countdownText;
            }
            line2 = "倒计时结束后将自动确认下车";
        }

        if (!TextUtils.isEmpty(line1) && !TextUtils.isEmpty(line2)) {
            SpannableStringBuilder builder = new SpannableStringBuilder();
            SpannableString countdownString = new SpannableString(line1);
            countdownString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.maas_text_primary)),
                    0, countdownString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            countdownString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.maas_text_blue)),
                    countdownString.length() - 5, countdownString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            countdownString.setSpan(new AbsoluteSizeSpan(SizeUtil.sp2px(16)),
                    0, countdownString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.append(countdownString);

            builder.append("\n");

            SpannableString spannableString = new SpannableString(line2);
            spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.maas_text_gray)),
                    0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new AbsoluteSizeSpan(SizeUtil.sp2px(14)),
                    0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.append(spannableString);

            return builder;
        }
        return null;
    }

    private boolean verifyChildOrderStatus() {
        if (order == null) {
            return false;
        }
        if (order.getOrderType() == Order.ORDER_TYPE_SEND_CHILD) {
            if (order.getOrderStatus() == Order.ORDER_STATUS_ARRIVED_START_ADDR &&
                    order.getOrderSubStatus() ==
                            Order.ORDER_SUB_STATUS_ARRIVED_START_ADDR_WAIT_PARENT_CONFIRM) {
                return true; // 上车确认
            }
            return order.getOrderStatus() == Order.ORDER_STATUS_GET_OFF &&
                    order.getOrderSubStatus() ==
                            Order.ORDER_SUB_STATUS_GET_OFF_WAIT_PARENT_CONFIRM; // 下车确认
        }
        return false;
    }
    ///////////////////////////////////////////////

    private CompositeDisposable mCarpoolOrderCountdownTimers;

    /**
     * 启动拼车单上下车照片确认倒计时器
     *
     * @param onOrders  拼车单 - 上车照片确认
     * @param offOrders 拼车单 - 下车照片确认
     */
    private void startCarpoolOrderCountdownTimer(@NonNull List<Order> onOrders, @NonNull List<Order> offOrders) {
        stopCarpoolOrderCountdownTimer();
        if (!onOrders.isEmpty() || !offOrders.isEmpty()) {
            mCarpoolOrderCountdownTimers = new CompositeDisposable();
            mCountdownLayout.removeAllViews();
            // 添加上车照片确认倒计时
            for (Order onOrder : onOrders) {
                View view = startCarpoolOrderCountdownTimerReally(onOrder, true);
                if (view != null) {
                    mCountdownLayout.addView(view);
                }
            }
            // 添加下车照片确认倒计时
            for (Order offOrder : offOrders) {
                View view = startCarpoolOrderCountdownTimerReally(offOrder, false);
                if (view != null) {
                    mCountdownLayout.addView(view);
                }
            }
            llOrderTips.setVisibility(View.GONE); // 隐藏原有的提示控件
            mCountdownLayout.setVisibility(View.VISIBLE);
        } else {
            mCountdownLayout.setVisibility(View.GONE);
        }
    }

    private View startCarpoolOrderCountdownTimerReally(Order order, boolean onOrOff) {
        long countdownSeconds;
        if (onOrOff) {
            countdownSeconds = (order.getOnPicTime() - System.currentTimeMillis()) / 1000;
        } else {
            countdownSeconds = (order.getOffPicTime() - System.currentTimeMillis()) / 1000;
        }
        if (countdownSeconds <= 0) {
            return null;
        }
        View itemLayout = View.inflate(getContext(), R.layout.layout_order_tips, null);
        TextView countdownView = itemLayout.findViewById(R.id.countdown_text);
        mCarpoolOrderCountdownTimers.add(Observable.interval(1, TimeUnit.SECONDS)
                .take(countdownSeconds)
                .map(v -> countdownSeconds - v)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long remainingSeconds) throws Exception {
                        LogUtils.d("剩余秒数：" + remainingSeconds);
                        CharSequence countdownText = getCountdownText(order, remainingSeconds);
                        countdownView.setText(countdownText);
                    }
                }, throwable -> {
                    LogUtils.e("计时出错：" + throwable.getMessage());
                    throwable.printStackTrace();
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        LogUtils.d("倒计时结束！");
                        refreshOrderDetails();
                    }
                }));
        return itemLayout;
    }

    private void stopCarpoolOrderCountdownTimer() {
        mCountdownLayout.setVisibility(View.GONE);
        if (mCarpoolOrderCountdownTimers != null && !mCarpoolOrderCountdownTimers.isDisposed()) {
            mCarpoolOrderCountdownTimers.dispose();
            mCarpoolOrderCountdownTimers = null;
        }
    }

    ///////////////////////////////////////////////

    /**
     * 处理 送你上学 订单的显示
     */
    private void updateChildOrderView() {
        tvTitleRight.setVisibility(View.VISIBLE);
        //1.订单提示View的显示
        if (order.getOrderStatus() == Order.ORDER_STATUS_WAIT_CAR) {
            //去接乘客，显示预计到达上车点时间，此处和实时订单不同，要取订单的时间。
            llOrderTips.setVisibility(View.VISIBLE);
            //订单时间(均展示日期和时分)
            String time = "";
            try {
                long milliSecond = BusinessUtils.stringToLong(order.getOrderTime(), DateUtils.FORMAT_PATTERN_YMDHM);
                time = BusinessUtils.getDataHm(milliSecond);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String str = getString(R.string.order_detail_tips_arrive_at_get_on_before_time, time);
            SpannableString spannableString = SpannableStringUtil.getSpanString(str, 3, time.length() + 4, R.color.maas_text_blue, Typeface.BOLD, 1f);
            tvOrderTips.setText(spannableString);
        } else if (order.getOrderStatus() == Order.ORDER_STATUS_ARRIVED_START_ADDR) { // 到达上车点
            if (order.getOrderSubStatus() == Order.ORDER_SUB_STATUS_ARRIVED_START_ADDR) { // 等待小孩上车
                //到达上车点，显示等待乘客上车tips
                llOrderTips.setVisibility(View.VISIBLE);
                tvOrderTips.setText(R.string.get_on_location_waiting_tips);
            } else if (order.getOrderSubStatus() ==
                    Order.ORDER_SUB_STATUS_ARRIVED_START_ADDR_WAIT_PARENT_CONFIRM) {
                // todo:等待家长确认上车
                LogUtils.d("等待家长确认上车");
                if (!carpoolFlag) { // 拼车单，可能会有多个倒计时
                    startOrderCountDownTimer(order.getOnPicTime());
                }
            } else {
                llOrderTips.setVisibility(View.GONE);
            }
        } else if (order.getOrderStatus() == Order.ORDER_STATUS_GET_OFF) { // 到达下车点
            if (order.getOrderSubStatus() ==
                    Order.ORDER_SUB_STATUS_GET_OFF_WAIT_PARENT_CONFIRM) {
                // todo:等待家长确认下车
                LogUtils.d("等待家长确认下车");
                if (!carpoolFlag) { // 非拼车单倒计时，拼车单将单独批量倒计时
                    startOrderCountDownTimer(order.getOffPicTime());
                }
            } else {
                llOrderTips.setVisibility(View.GONE);
            }
        } else {
            llOrderTips.setVisibility(View.GONE);
        }

        if (carpoolFlag) {
            // 检查上车照片确认倒计时
            List<Order> onPicOrders = presenter.getOnPicOrders();
            // 检查下车照片确认倒计时
            List<Order> offPicOrders = presenter.getOffPicOrders();
            startCarpoolOrderCountdownTimer(onPicOrders, offPicOrders);
        } else {
            stopCarpoolOrderCountdownTimer();
        }

        //2.初始状态
        if (order.getOrderStatus() == Order.ORDER_STATUS_INIT ||
                order.getOrderStatus() == Order.ORDER_STATUS_READY) {
            llOrderInitInfo.setVisibility(View.VISIBLE);
            rlAddressInfo.setVisibility(View.GONE);
            //订单时间(均展示日期和时分)
            String timeString = "";
            try {
                if (order.getOrderTime() != null) {
                    long milliSecond = BusinessUtils.stringToLong(order.getOrderTime(), DateUtils.FORMAT_PATTERN_YMDHM);
                    timeString = BusinessUtils.getDateToStringIncludeYearWhenCrossYear(milliSecond, null);
                    LogUtils.d(TAG, "" + "timeString: " + timeString);
                    tvOrderTime.setText(timeString);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (order.getOrderStatus() == Order.ORDER_STATUS_WAIT_PAY
                || order.getOrderStatus() == Order.ORDER_STATUS_CLOSED
                || order.getOrderStatus() == Order.ORDER_STATUS_GET_OFF
                || order.getOrderStatus() == Order.ORDER_STATUS_CANCELED) {
            llOrderInitInfo.setVisibility(View.VISIBLE);
            rlAddressInfo.setVisibility(View.GONE);
            flOrderTime.setVisibility(View.GONE);
        } else {
            llOrderInitInfo.setVisibility(View.GONE);
            rlAddressInfo.setVisibility(View.VISIBLE);
        }

        //3.总耗时,只有在等待家长确认下车照片状态，才会显示
        if (order.getTotalTime() > 0) {
            tvOrderTotalTimeNew.setText(getString(R.string.order_detail_total_time_prefix_2) + BusinessUtils.formatEstimateTimeText(order.getTotalTime()));
            llTotalTime.setVisibility(View.VISIBLE);
        } else {
            llTotalTime.setVisibility(View.GONE);
        }

        //3.联系按钮的显示和隐藏，规则如下
        //1)"去送乘客"前，显示联系小孩 和 联系家长
        //2)"去送乘客"时，只显示 联系家长
        //3)"等待家长确认下车照片"时，显示 联系客服 和 联系家长
        flChildContactContainer.setVisibility(View.VISIBLE);
        switch (order.getOrderStatus()) {
            case Order.ORDER_STATUS_INIT:  //订单初始化
            case Order.ORDER_STATUS_READY:  //订单准备中
            case Order.ORDER_STATUS_WAIT_CAR:  //去接乘客，尚未到达上车地点
            case Order.ORDER_STATUS_ARRIVED_START_ADDR:  //已到达上车点，乘客未上车
                llChildContactStyle1.setVisibility(View.VISIBLE);
                llChildParentContact2.setVisibility(View.GONE);
                break;
            case Order.ORDER_STATUS_GET_ON:            //乘客已上车，尚未到达目的地
            case Order.ORDER_STATUS_WAIT_PAY:       //乘客未付款
            case Order.ORDER_STATUS_CLOSED:        //订单完成，乘客已付款
                llChildContactStyle1.setVisibility(View.GONE);
                llChildParentContact2.setVisibility(View.VISIBLE);
                break;
            case Order.ORDER_STATUS_GET_OFF:          //到达目的地，尚未设置收款金额
                isNeedShowTrueLang = true;
                if (order.getOrderSubStatus() == Order.ORDER_SUB_STATUS_GET_OFF_WAIT_PARENT_CONFIRM) {
                    //等待家长确认
                    tvChildContact.setText(R.string.contact_customer_service);
                    llChildContactStyle1.setVisibility(View.VISIBLE);
                    llChildParentContact2.setVisibility(View.GONE);
                } else {
                    //家长已确认
                    llChildContactStyle1.setVisibility(View.GONE);
                    llChildParentContact2.setVisibility(View.VISIBLE);
                }
                break;
            case Order.ORDER_STATUS_CANCELED:      //取消订单
                break;
            default:
                break;
        }
    }

    /**
     * 处理 送你上学 滑动按钮逻辑
     */
    private void handleChildOrderSlide() {
/*        //离订单开始时间的6小时内才可以操作。
        try {
            long orderTime = BusinessUtils.stringToLong(order.getOrderTime(), DateUtils.FORMAT_PATTERN_YMDHM);
            if (orderTime - System.currentTimeMillis() > 6 * 3600 * 1000) {
                toast("请在用车时间前6小时开始订单");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }*/

        if (order.getOrderStatus() == Order.ORDER_STATUS_WAIT_CAR) {
            //到达上车点，显示上车点确认弹窗
            String msg = getString(linearDistance > 500 ? R.string.get_on_location_more_than_limit_tips : R.string.get_on_location_confirm_tips);
            showArriveDialog(msg);
        } else if (order.getOrderStatus() == Order.ORDER_STATUS_GET_ON) {
            //到达目的地，拍下车图片
            UploadChildImgActivity.go(OrderDetailActivity.this, orderId, Order.ORDER_STATUS_GET_OFF,
                    order.getGetOffAutoCheck(), order.getGetOnAutoCheck(), order.getOrderType());
        } else if (order.getOrderStatus() == Order.ORDER_STATUS_ARRIVED_START_ADDR
                && order.getOrderSubStatus() == Order.ORDER_SUB_STATUS_ARRIVED_START_ADDR) {
            //拍上车图片
            UploadChildImgActivity.go(OrderDetailActivity.this, orderId, order.getOrderStatus(),
                    order.getGetOffAutoCheck(), order.getGetOnAutoCheck(), order.getOrderType());
        } else if (order.getOrderStatus() == Order.ORDER_STATUS_GET_OFF
                && order.getOrderSubStatus() == Order.ORDER_SUB_STATUS_GET_OFF) {
            //拍下车图片
            UploadChildImgActivity.go(OrderDetailActivity.this, orderId, order.getOrderStatus(),
                    order.getGetOffAutoCheck(), order.getGetOnAutoCheck(), order.getOrderType());
        } else {
            presenter.changeOrderStatus(cargoOrderId, OrderDetailPresenter.OPERATAR_BY_DETAIL);
        }
    }


    private boolean isChangeOrderStatus;  //是否改变订单状态

    /**
     * 到达上车点 or 到达目的地 弹窗
     */
    private void showArriveDialog(String msg) {
        isChangeOrderStatus = false;
        mArriveDialog = ConfirmDialog.newInstance();
        mArriveDialog.setMessage(msg).setOnClickListener(new ConfirmDialog.OnClickListener() {
            @Override
            public void onPositiveClick(View view) {
                isChangeOrderStatus = true;
                presenter.changeOrderStatus(cargoOrderId, OrderDetailPresenter.OPERATAR_BY_DETAIL);
                /*if (order.getOrderStatus() == Order.ORDER_STATUS_GET_ON) {
                    EventBus.getDefault().post(new EventBean(EventBean.EVENT_CODE_ARRIVE_AT_DESTINATION_VOICE_PROMPT,null));
                }*/
            }

            @Override
            public void onDismiss() {
                if (!isChangeOrderStatus) {
                    slideview.reset();
                }
            }
        }).setPositiveText(R.string.confirm_arrival).setNegativeText(R.string.cancel).show(getSupportFragmentManager(), "");
    }


    /**
     * 到听单页
     */
    private void goMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    /**
     * 更新行程预计时间
     *
     * @param estimateTime
     */
    private void updatePathRetainTime(long estimateTime) {
        tvRestTimePrefix.setVisibility(View.VISIBLE);
        if (estimateTime > 60 * 60) {
            long hours = estimateTime / (60 * 60);
            long minutes = (estimateTime % (60 * 60)) / 60;
            if (minutes == 0) {  //只显示小时
                tvRestTimeHour.setText(hours + "");
                tvRestTimeHour.setVisibility(View.VISIBLE);
                tvRestTimeHourUnit.setVisibility(View.VISIBLE);
                tvRestTimeMinute.setVisibility(View.GONE);
                tvRestTimeMinuteUnit.setVisibility(View.GONE);
                tvRestTimeSecond.setVisibility(View.GONE);
                tvRestTimeSecondUnit.setVisibility(View.GONE);
            } else {
                //显示小时和分钟
                tvRestTimeHour.setText(hours + "");
                tvRestTimeMinute.setText(minutes + "");
                tvRestTimeHour.setVisibility(View.VISIBLE);
                tvRestTimeHourUnit.setVisibility(View.VISIBLE);
                tvRestTimeMinute.setVisibility(View.VISIBLE);
                tvRestTimeMinuteUnit.setVisibility(View.VISIBLE);
                tvRestTimeSecond.setVisibility(View.GONE);
                tvRestTimeSecondUnit.setVisibility(View.GONE);
            }
        } else if (estimateTime > 60) {  //只显示分钟
            long minutes = estimateTime / 60;
            tvRestTimeMinute.setText(minutes + "");
            tvRestTimeHour.setVisibility(View.GONE);
            tvRestTimeHourUnit.setVisibility(View.GONE);
            tvRestTimeMinute.setVisibility(View.VISIBLE);
            tvRestTimeMinuteUnit.setVisibility(View.VISIBLE);
            tvRestTimeSecond.setVisibility(View.GONE);
            tvRestTimeSecondUnit.setVisibility(View.GONE);

        } else { //只显示秒
            tvRestTimeSecond.setText(estimateTime + "");
            tvRestTimeHour.setVisibility(View.GONE);
            tvRestTimeHourUnit.setVisibility(View.GONE);
            tvRestTimeMinute.setVisibility(View.GONE);
            tvRestTimeMinuteUnit.setVisibility(View.GONE);
            tvRestTimeSecond.setVisibility(View.VISIBLE);
            tvRestTimeSecondUnit.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 已在上车点等待乘客多长时间的  时间戳
     */
    private long mWaitingTimeMillis = 0L;

    /**
     * 等待乘客上车时间计时 - 实时订单
     */
    private void updateWaitingPassengerTime() {
        mWaitingTimeMillis = mWaitingTimeMillis + 1000;
        String content = getString(R.string.order_detail_tips_waiting_for_passengers_time) + " " + DateUtils.getHHmmssOrmmssForLong(mWaitingTimeMillis);
        SpannableString spannableString = SpannableStringUtil.getSpanString(content, 10, content.length(), R.color.maas_text_blue, Typeface.BOLD, 1f);
        tvOrderTips.setText(spannableString);
        handler.sendEmptyMessageDelayed(HANDLER_MSG_WAITING_PASSENGER_COUNTING_TIME, 1000);
    }

    /**
     * 获得当前 到 终点 的直线距离
     */
    private int getLineDistance() {
        if (currentGps == null) {
            return 0;
        }
        LatLng currentLatLng = new LatLng(currentGps.getLatitude(), currentGps.getLongitude());
        LatLng endLatLng;
        if (order.getOrderStatus() == Order.ORDER_STATUS_GET_ON) {
            endLatLng = order.getEndAddr().getLatLng();
        } else {
            endLatLng = order.getStartAddr().getLatLng();
        }
        return (int) AMapUtils.calculateLineDistance(currentLatLng, endLatLng);
    }


    /**
     * 对悬浮窗口的异常情况处理
     * 1.某些悬浮窗不允许用户关闭，需要做异常处理，尽可能不影响app的使用。
     * 2.考虑是否断网情况下，所以不做任何依靠后台数据的判断。
     */
    private void handleFloatDialogException() {
        //取消美团计时弹窗的显示
        EventBus.getDefault().post(new FloatwindowCmd(FloatwindowCmd.WINDOW_TYPE_MEITUAN_GRABING_DIALOG, FloatwindowCmd.OPERATION_CANCEL));
    }
}

