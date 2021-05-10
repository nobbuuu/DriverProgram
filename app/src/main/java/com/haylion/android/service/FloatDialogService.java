package com.haylion.android.service;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.services.route.DistanceResult;
import com.amap.api.services.route.DistanceSearch;
import com.haylion.android.Constants;
import com.haylion.android.R;
import com.haylion.android.common.aibus_location.data.GpsData;
import com.haylion.android.common.map.AMapUtil;
import com.haylion.android.common.view.dialog.MeiTuanGrabingDialog;
import com.haylion.android.common.view.dialog.OrderExceptionCancelDialog;
import com.haylion.android.common.view.dialog.OrderCancelDialog;
import com.haylion.android.common.view.dialog.OrderChangeAddressDialog;
import com.haylion.android.common.view.dialog.OrderOverTimeDialog;
import com.haylion.android.common.view.dialog.OrderStatusToControversyDialog;
import com.haylion.android.common.view.dialog.ReLoginDialog;
import com.haylion.android.data.model.AddressInfo;
import com.haylion.android.data.model.EventBean;
import com.haylion.android.data.model.EventMsg;
import com.haylion.android.data.model.FloatwindowCmd;
import com.haylion.android.data.model.MessageDetail;
import com.haylion.android.data.model.MessageDetailSimple;
import com.haylion.android.data.model.NewOrder;
import com.haylion.android.data.model.NotificationData;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.model.OrderDetail;
import com.haylion.android.data.model.OrderTypeInfo;
import com.haylion.android.data.model.Vehicle;
import com.haylion.android.data.model.WebsocketData;
import com.haylion.android.data.model.WebsocketVoidData;
import com.haylion.android.data.repo.OrderRepository;
import com.haylion.android.data.repo.PrefserHelper;
import com.haylion.android.data.util.BusinessUtils;

import com.haylion.android.data.util.NumberUtil;
import com.haylion.android.data.util.OrderConvert;
import com.haylion.android.data.util.SpannableStringUtil;
import com.haylion.android.data.util.StatusBarUtils;
import com.haylion.android.data.util.TTSUtil;
import com.haylion.android.main.MainActivity;
import com.haylion.android.mvp.base.AppManager;
import com.haylion.android.mvp.rx.ApiSubscriber;
import com.haylion.android.mvp.util.LogUtils;
import com.haylion.android.orderdetail.OrderDetailActivity;
import com.haylion.android.orderdetail.map.ShowInMapNewActivity;
import com.haylion.android.orderdetail.trip.TripDetailActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.haylion.android.orderdetail.OrderDetailActivity.FORM_NOTIFICATION_CLICK;
import static com.haylion.android.orderdetail.OrderDetailActivity.FORM_NOTIFICATION_MESSAGE_ID;
import static com.haylion.android.orderdetail.trip.TripDetailActivity.ORDER_ID;
import static com.haylion.android.orderdetail.trip.TripDetailActivity.SHOW_LISTEN_BUTTON;


/**
 * 悬浮窗 Service
 * 1.抢单弹窗
 * 2.抢单状态弹窗：成功/失败/被抢
 * 3.美团抢单中弹窗
 * 4.订单超时弹窗
 * <p>
 * 非悬浮窗
 * 1.取消订单弹窗
 * 2.修改下车点弹窗
 * 3.支付争议弹窗
 * 4.重新登录弹窗
 */
public class FloatDialogService extends Service {
    private static final String TAG = "FloatDialogService";
    private Context mContext;
    int DIALOG_TYPE_WAITING_RESULT = 1;
    int DIAGLOG_TYPE_USER_CANCEL_ORDER = 2;
    int DIALOG_TYPE_FAIL = 3;
    int DIALOG_TYPE_SUCCESS = 4;
    int DIALOG_TYPE_ORDER_ALLOCATED = 5;
    int DIALOG_TYPE_ORDER_WILL_START = 6;
    int DIALOG_TYPE_ORDER_CARGO_CANCEL = 7;
    int DIALOG_TYPE_ORDER_CHANGE_ADDRESS = 8;

    public static final int GRAB_ORDER_TOTAL_TIME = 18 * 1000; //抢单总时间，包含等待的3秒
    public static final int GRAB_ORDER_TIME = 15 * 1000; //抢单倒计时时间，15秒

    OrderRepository orderRepository = new OrderRepository();
    Handler handler = new Handler();

    private ScheduledExecutorService mScheduledExecutorServiceOne;
    private int orderId;  //订单id
    private int channel;  //订单来源 美团-1
    private Order order;
    private static GpsData currentGps;  //当没有定位权限时，得到null
    private Dialog newOrderDialog;
    public static boolean overlayWindowFlag; //是否开启了悬浮窗

    private boolean isGetMeituanOrderResult = true;  //是否得到了美团抢单最终结果,默认为true，只有抢了美团单才置为false。

    private static int notificationId = 10000;  //通知ID，

    public static GpsData getCurrentGps() {
        return currentGps;
    }

    public static void setCurrentGps(GpsData currentGps) {
        FloatDialogService.currentGps = currentGps;
    }

    public FloatDialogService() {
    }

    /**
     * Called by the system when the service is first created.  Do not call this method directly.
     */
    @Override
    public void onCreate() {
        LogUtils.d(TAG, "onCreate");
        super.onCreate();
        EventBus.getDefault().register(this);
        mContext = this;

        mockWebsocketData(); //调试用
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * @param
     * @return
     * @method
     * @description 调试用，用来模拟websocket消息
     * @date: 2019/12/17 11:12
     * @author: tandongdong
     */
    private void mockWebsocketData() {
        mScheduledExecutorServiceOne = Executors.newSingleThreadScheduledExecutor();
        mScheduledExecutorServiceOne.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
/*                LogUtils.d(TAG, "mScheduledExecutorServiceOne");
                WebsocketOrderInfo wsOrderInfo = new WebsocketOrderInfo();
                wsOrderInfo.setOrderId(1000113888);
                List<Integer> overTimeOrderIdList = new ArrayList<>();
                overTimeOrderIdList.add(1000);
                overTimeOrderIdList.add(1001);
                wsOrderInfo.setOrderIds(overTimeOrderIdList);
                wsOrderInfo.setOldAddress("旧地址描述");
                wsOrderInfo.setOldPlace("旧地址");

                WebsocketData websocketData = new WebsocketData(WsCommands.DRIVER_ORDER_DESTINATION_CHANGED.getSn(),
                        "", "", wsOrderInfo);
                EventBus.getDefault().post(websocketData);*/
            }
        }, 10, 10, TimeUnit.SECONDS);
    }

    /**
     * 取消订单弹窗
     *
     * @param order
     */
    private void orderCancelDialog(Order order) {
        if (order == null) {
            LogUtils.e(TAG, "订单取消弹窗，订单为空");
            return;
        }
        LogUtils.d(TAG, "orderCancelDialog, " + order.toString());
        if (AppManager.getAppManager().currentActivity() != null) {
            OrderCancelDialog dialog = new OrderCancelDialog(AppManager.getAppManager().currentActivity());
            dialog.setData(order);
            dialog.show();
        }
    }

    /**
     * 系统异常结束订单弹窗
     *
     * @param order
     */
    private void orderExceptionCancelDialog(Order order) {
        if (order == null) {
            LogUtils.e(TAG, "订单异常取消弹窗，订单为空");
            return;
        }
        LogUtils.d(TAG, "orderExceptionCancelDialog, " + order.toString());
        if (AppManager.getAppManager().currentActivity() != null) {
            OrderExceptionCancelDialog dialog = new OrderExceptionCancelDialog(AppManager.getAppManager().currentActivity());
            dialog.setData(order);
            dialog.show();
        }
    }

    /**
     * 司机信息发生改变重新登录弹窗
     */
    private void driverInfoChangeReLoginDialog(String hint){
        if (AppManager.getAppManager().currentActivity() != null) {
            ReLoginDialog dialog = new ReLoginDialog(AppManager.getAppManager().currentActivity());
            dialog.setData(hint);
            dialog.show();
        }
    }


    /**
     * 订单超时的弹窗
     *
     * @param orderId
     */

    OrderOverTimeDialog overTimeDialog;

    private void orderOvertimeDialog(int orderId) {
        LogUtils.d(TAG, "orderOvertimeDialog,orderId: " + orderId);
        if (orderId == 0) {
            LogUtils.e(TAG, "订单超时弹窗，订单为空");
            return;
        }
        if (overTimeDialog == null) {
            overTimeDialog = new OrderOverTimeDialog(this);
        }
        overTimeDialog.setData(orderId);
        overTimeDialog.show();

/*
        if (overTimeDialog == null) {
            overTimeDialog = new Dialog(this, R.style.Translucent_NoTitle);
        }
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.layout_overtime_order, null);

        //操作
        TextView btnConfirm = view.findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overTimeDialog.dismiss();
                Intent intent = new Intent(FloatDialogService.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("overTimeOrderId", orderId);
                startActivity(intent);
            }
        });

        overTimeDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            Thread.currentThread().interrupt();
                        }
                    }
                }).start();
            }
        });

        if (Build.VERSION.SDK_INT >= 26) {
            overTimeDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        } else {
            overTimeDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        overTimeDialog.show();
        overTimeDialog.getWindow().setContentView(view);*/

    }

    /**
     * 修改上下车地址
     * 1.在订单流程页面，如订单详情页/行程详情页/导航页，是当前正在执行的订单，相关逻辑交由各自页面做处理
     * 2.其他情况，进行页面跳转，跳到 听单页面
     *
     * @param order
     */
    private void orderChangeAddressDialog(Order order, AddressInfo oldAddress) {
        if (order == null) {
            LogUtils.e(TAG, "上下车点已修改弹窗，订单为空");
            return;
        }
        LogUtils.d(TAG, "orderChangeAddressDialog, " + order.toString());
        if (AppManager.getAppManager().currentActivity() != null) {
            OrderChangeAddressDialog dialog = new OrderChangeAddressDialog(AppManager.getAppManager().currentActivity());
            dialog.setData(order, oldAddress);
            dialog.show();
        }
    }

    /**
     * 有新的订单到来，弹窗提示。
     * 实时，非拼车
     * 实时，拼车
     * 预约，非拼车
     *
     * @param order
     */
    public static long grabOrderResetTime = 0;  //抢单剩余时间,单位ms
    private long totalDistance = 0;  //全程距离
    CountDownTimer timer;

    private void newOrderCommingDialog(Order order) {
        if (order == null || order.getOrderId() == 0) {
            LogUtils.e(TAG, "订单为空");
            return;
        }
        order.setDistanceFromCar(mGrabOrderDistance);
        LogUtils.d(TAG, "newOrderCommingDialog,order: " + order.toString());
        orderId = order.getOrderId();
        channel = order.getChannel();
        this.order = order;

        if (newOrderDialog == null) {
            newOrderDialog = new Dialog(this, R.style.Translucent_NoTitle);
        }
        newOrderDialog.setCanceledOnTouchOutside(false);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_new_order, null);

        //填充状态栏高度，动态设置高度。
        View statusBarView = view.findViewById(R.id.view_status_bar);
        RelativeLayout.LayoutParams statusParams = (RelativeLayout.LayoutParams) statusBarView.getLayoutParams();
        statusParams.height = StatusBarUtils.getStatusBarHeight(this);
        statusBarView.setLayoutParams(statusParams);
        //关闭按钮
        ImageView ivCloseDialog = view.findViewById(R.id.iv_close_dialog);
        //订单类型
        TextView tvOrderType = view.findViewById(R.id.tv_order_type);
        //距离
        TextView tv_distance_fixed = view.findViewById(R.id.tv_distance_fixed);
        TextView tv_distance_fixed_unit = view.findViewById(R.id.tv_distance_fixed_unit);
        //全程
        TextView tv_distance = view.findViewById(R.id.tv_distance);
        TextView tv_distance_unit = view.findViewById(R.id.tv_distance_unit);
        //地址内容
        TextView tvStartAddress = view.findViewById(R.id.tv_order_get_on_address);
        TextView tvStartAddressDescribe = view.findViewById(R.id.tv_order_get_on_address_describe);
        TextView tvEndAddress = view.findViewById(R.id.tv_order_get_off_address);
        TextView tvEndAddressDescribe = view.findViewById(R.id.tv_order_get_off_address_describe);
        //查看地图
        TextView tvEnterMapView = view.findViewById(R.id.tv_find_in_map);
        //抢单
        RelativeLayout btnGrabOrder = view.findViewById(R.id.btn_grab_order);
        TextView tv_waiting_time = view.findViewById(R.id.tv_waiting_time);
        LinearLayout ll_waiting_time = view.findViewById(R.id.ll_waiting_time);
        TextView tv_rob = view.findViewById(R.id.tv_rob);
        TextView tv_rob_time = view.findViewById(R.id.tv_rob_time);
        //货拼客剩余送货时间tips
        TextView tvRestTimeSendCargo = view.findViewById(R.id.tv_rest_time_send_cargo);
        //实时拼车单价格
        LinearLayout llPrice = view.findViewById(R.id.ll_price);
        TextView tvTotalCost = view.findViewById(R.id.total_cost);


        //显示信息
        if (order.getOrderType() == Order.ORDER_TYPE_REALTIME_CARPOOL) {
            //实时拼车单
            tvOrderType.setText("实时拼车 " + "(" + order.getPassengerNum() + "人)");
            llPrice.setVisibility(View.VISIBLE);
            tvTotalCost.setText(BusinessUtils.moneySpec(order.getTotalMoney()));
        } else if (order.getOrderType() == Order.ORDER_TYPE_REALTIME) {
            //实时订单
            tvOrderType.setText("实时订单");
        } else if (order.getOrderType() == Order.ORDER_TYPE_CARGO_PASSENGER) {
            //货拼客单
            tvOrderType.setText("速运速行");
            tvRestTimeSendCargo.setVisibility(View.VISIBLE);
            String time = BusinessUtils.formatEstimateTimeText(order.getMinPreservedSeconds());
            String str = "已预留" + time + "停车上门送货。";
            SpannableString spannableString = SpannableStringUtil.setSpan(str, R.color.c_ffc800, 3, time.length() + 3);
            tvRestTimeSendCargo.setText(spannableString);
        } else {
            tvOrderType.setText("新订单");
        }

        //计算上车点离车辆的距离
        if (order.getDistanceFromCar() > 1000) {
            tv_distance_fixed.setText(NumberUtil.roundHalfUp(order.getDistanceFromCar() / 1000.0, 1) + "");
            tv_distance_fixed_unit.setText("公里");
        } else {
            tv_distance_fixed.setText(order.getDistanceFromCar() + "");
            tv_distance_fixed_unit.setText("米");
        }
        //计算全程公里数
        totalDistance = order.getDistance();
        if (totalDistance > 1000) {
            tv_distance.setText(NumberUtil.roundHalfUp(totalDistance / 1000.0, 1) + "");
            tv_distance_unit.setText("公里");
        } else {
            tv_distance.setText(totalDistance + "");
            tv_distance_unit.setText("米");
        }
        /*Log.e(TAG,"totalDistance1:" + totalDistance);
        totalDistance = 0;
        if(totalDistance != 0){
            if (totalDistance > 1000) {
                tv_distance.setText(NumberUtil.roundHalfUp(totalDistance / 1000.0, 1) + "");
                tv_distance_unit.setText("公里");
            } else {
                tv_distance.setText(totalDistance + "");
                tv_distance_unit.setText("米");
            }
        }else{
            //为0，需要去检测一次
            AMapUtil.calculateDistance(this, order.getStartAddr().getLatLng(),
                    order.getEndAddr().getLatLng(), (distanceResult, i) -> {
                        if (i == AMapUtil.RESPONSE_SUCCESS) {
                            totalDistance = (long) distanceResult.getDistanceResults().get(0).getDistance();
                        } else {
                            totalDistance = 0;
                        }
                        Log.e(TAG,"totalDistance2:" + totalDistance);
                        if (totalDistance > 1000) {
                            tv_distance.setText(NumberUtil.roundHalfUp(totalDistance / 1000.0, 1) + "");
                            tv_distance_unit.setText("公里");
                        } else {
                            tv_distance.setText(totalDistance + "");
                            tv_distance_unit.setText("米");
                        }
                    });
        }*/

        //上车和下车位置
        tvStartAddress.setText(order.getStartAddr().getName());
        tvStartAddressDescribe.setText(AMapUtil.getAddress(order.getStartAddr().getAddressDetail()));
        tvEndAddress.setText(order.getEndAddr().getName());
        tvEndAddressDescribe.setText(AMapUtil.getAddress(order.getEndAddr().getAddressDetail()));

        if (order.getOrderType() == Order.ORDER_TYPE_CARGO_PASSENGER) {
            //推荐指数
            LinearLayout rlOrderLevel = view.findViewById(R.id.order_level);
            rlOrderLevel.setVisibility(View.VISIBLE);

            RatingBar ratingBar = view.findViewById(R.id.rb_star);
            TextView amountLevel = view.findViewById(R.id.tv_amount_level);
            TextView roadLevel = view.findViewById(R.id.tv_road_level);
            ratingBar.setRating(order.getStarLevel());

            //顺路指数
            roadLevel.setText(order.getRoadLevel() + "");

            //收益指数
            amountLevel.setText(order.getAmountLevel() + "");
        }

        //操作
        btnGrabOrder.setOnClickListener(new CustomClickListener());
        tvEnterMapView.setOnClickListener(new CustomClickListener());
        ivCloseDialog.setOnClickListener(new CustomClickListener());

        /**
         *  抢单倒计时
         *  总共18s，前3s准备，后15s允许抢单
         * */
        if (timer == null) {
            timer = new CountDownTimer(GRAB_ORDER_TOTAL_TIME, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    grabOrderResetTime = millisUntilFinished + 1000;
                    Log.d(TAG, "onTick:" + grabOrderResetTime / 1000);

                    if (millisUntilFinished > GRAB_ORDER_TIME) {  //处于等待的3s中
                        tv_waiting_time.setText(((grabOrderResetTime - GRAB_ORDER_TIME) / 1000) + "");
                        btnGrabOrder.setEnabled(false);
                    } else { //处于抢单的15s中
                        ll_waiting_time.setVisibility(View.GONE);
                        tv_rob.setVisibility(View.VISIBLE);
                        tv_rob_time.setVisibility(View.VISIBLE);
                        tv_rob_time.setText((grabOrderResetTime / 1000) + "s");
                        btnGrabOrder.setEnabled(true);
                    }
                }

                @Override
                public void onFinish() {
                    grabOrderResetTime = 0;
                    //LogUtils.d(TAG, "onTickFinish:" + grabOrderResetTime / 1000);
                    if (newOrderDialog != null) {
                        newOrderDialog.dismiss();
                    }
                    LogUtils.d(TAG, "time out, get next order");
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getNewOrderData();
                        }
                    }, 2000);//2秒后执行Runnable中的run方法
                }
            }.start();
        }

        //屏蔽返回键
        newOrderDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                }
                return false;
            }
        });

        /**
         * 消失事件监听
         * */
        newOrderDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (timer != null) {
                    LogUtils.d(TAG, "newOrderDialog onDismiss timer cancel");
                    timer.cancel();
                    timer = null;
                }
                newOrderDialog = null;
                //停止播报
                TTSUtil.stop();
            }
        });

        if (Build.VERSION.SDK_INT >= 26) {
            newOrderDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        } else {
            newOrderDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        LogUtils.d(TAG, "newOrderDialog show");
        newOrderDialog.show();
        newOrderDialog.getWindow().setContentView(view);
        //让Dialog 填满宽高,放在setContentView之后
        newOrderDialog.getWindow().setLayout((ViewGroup.LayoutParams.MATCH_PARENT), ViewGroup.LayoutParams.MATCH_PARENT);
        //让window占满整个手机屏幕，不留任何边界(border)
        newOrderDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        //让window进行全屏显示
        //newOrderDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //语音播报
        String ttsContent = "";
        String orderTypeText = OrderTypeInfo.getStatusText(order.getOrderType(), order.getChannel());
        if (order.getOrderType() == Order.ORDER_TYPE_CARGO_PASSENGER) {
            //货拼客单,
            orderTypeText = "速运速行，";
        }
        ttsContent = ttsContent + orderTypeText;

        ttsContent = ttsContent + order.getStartAddr().getName() + "到" + order.getEndAddr().getName()
                + ",距您" + BusinessUtils.formatDistanceForVoice(order.getDistanceFromCar())
                + ",全程" + BusinessUtils.formatDistanceForVoice(order.getDistance()) + ",";

        if (order.getOrderType() == Order.ORDER_TYPE_REALTIME_CARPOOL) {
            ttsContent = ttsContent + order.getPassengerNum() + "人拼车价" + BusinessUtils.moneySpec(order.getTotalMoney()) + "元";
        } else if (order.getOrderType() == Order.ORDER_TYPE_CARGO_PASSENGER) {
            String time = BusinessUtils.formatEstimateTimeText(order.getMinPreservedSeconds());
            ttsContent = ttsContent + "已预留" + time + "停车上门送货。";
        }
        TTSUtil.play(ttsContent);
    }

    private long mGrabOrderDistance; //抢单距离

    /**
     * 计算抢单的距离，只有距离计算成功才将新订单展示出来
     *
     * @param order
     */
    private void calculateDistance(Order order) {
        mGrabOrderDistance = 0;
        if (currentGps == null) {
//            newOrderCommingDialog(order);
            return;
        }
        GpsData destGps = new GpsData(order.getStartAddr().getLatLng().latitude, order.getStartAddr().getLatLng().longitude);
        LogUtils.d(TAG, "开始抢单距离计算");
        LogUtils.d(TAG, "当前位置信息：" + currentGps.toString());
        LogUtils.d(TAG, "上车点位置信息：" + destGps.toString());
        AMapUtil.calculateDistance(this, currentGps, destGps, new DistanceSearch.OnDistanceSearchListener() {
            @Override
            public void onDistanceSearched(DistanceResult distanceResult, int i) {
                if (i == AMapUtil.RESPONSE_SUCCESS) {
                    mGrabOrderDistance = (long) distanceResult.getDistanceResults().get(0).getDistance();
                    LogUtils.d(TAG, "抢单距离计算成功(米):" + mGrabOrderDistance);
                    newOrderCommingDialog(order);
                } else {
                    LogUtils.d(TAG, "抢单距离计算失败");
                    mGrabOrderDistance = 0;
                }
//                newOrderCommingDialog(order);
            }
        });
    }

    /**
     * 弹窗点击统一处理
     */
    private class CustomClickListener implements View.OnClickListener {

        /**
         * Called when a view has been clicked.
         *
         * @param view The view that was clicked.
         */
        @Override
        public void onClick(View view) {
//            Toast.makeText(getApplicationContext(), "点击: " + view.getTransitionName(), Toast.LENGTH_SHORT).show();
            switch (view.getId()) {
                case R.id.btn_grab_order:  //抢单
                    LogUtils.d(TAG, "btn_grab_order orderId: " + orderId);
                    if (newOrderDialog != null) {
                        newOrderDialog.cancel();
                    }
                    grabOrder(orderId);
                    break;
                case R.id.tv_find_in_map:
                    LogUtils.d(TAG, "iv_find_in_map orderId: " + orderId);
                    //todo 什么时候重新展示弹窗？
                    Intent intent = new Intent(mContext, ShowInMapNewActivity.class);
                    intent.putExtra(ShowInMapNewActivity.ORDER_START_ADDR, order.getStartAddr());
                    intent.putExtra(ShowInMapNewActivity.ORDER_END_ADDR, order.getEndAddr());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;
           /*     case R.id.next_order:
                    LogUtils.d(TAG, "next order");
                    if (newOrderDialog != null) {
                        newOrderDialog.cancel();
                    }
                    getNewOrderData();
                    break;*/
                case R.id.iv_close_dialog:
                    if (newOrderDialog != null) {
                        newOrderDialog.cancel();
                    }
                    //迭代8，关闭弹窗后立即获取下一个新订单
                    getNewOrderData();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * @param
     * @return
     * @method
     * @description 抢单状态的弹窗
     * --抢单失败，抢单成功,订单被抢。
     * a. 抢单中已废弃。
     * b. 用户取消订单已废弃，走 cancelOrder。
     * @date: 2019/12/17 11:26
     * @author: tandongdong
     */
    private void grabingOrderDialog(int orderId, int dialogType) {
        Dialog dialog = new Dialog(this, R.style.Translucent_NoTitle);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.layout_grab_order, null);

        //update view
        TextView header = view.findViewById(R.id.header_text);
        ImageView ivClose = view.findViewById(R.id.iv_close);
        ImageView ivTipsImage = view.findViewById(R.id.iv_tips_image);
        TextView tvTips = view.findViewById(R.id.tv_tips_text);
        TextView tvDialogCloseRestTime = view.findViewById(R.id.tv_close_rest_time);

        if (dialogType == DIALOG_TYPE_SUCCESS) {
            header.setText("抢单成功");
            ivTipsImage.setImageResource(R.mipmap.ic_grab_order_success);
            TTSUtil.playVoice("抢单成功, 请尽快查看订单详情");
        } else if (dialogType == DIALOG_TYPE_FAIL) {
            header.setText("抢单失败");
            ivTipsImage.setImageResource(R.mipmap.ic_grab_order_fail);
            TTSUtil.playVoice("抢单失败");
        } else if (dialogType == DIALOG_TYPE_ORDER_ALLOCATED) {
            header.setText("订单已被抢走");
            ivTipsImage.setImageResource(R.mipmap.ic_order_is_cancel);
            tvTips.setText("订单已被抢走");
            tvTips.setVisibility(View.VISIBLE);
        } else if (dialogType == DIALOG_TYPE_WAITING_RESULT) {
            header.setText("抢单中");
            ivTipsImage.setVisibility(View.INVISIBLE);
            tvDialogCloseRestTime.setVisibility(View.INVISIBLE);
            tvTips.setText("正在计算抢单结果");
            return;
        }

        if (Build.VERSION.SDK_INT >= 26) {
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        } else {
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        dialog.show();
        dialog.getWindow().setContentView(view);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        CountDownTimer timer = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvDialogCloseRestTime.setText((millisUntilFinished / 1000 + 1) + "s");
                if (dialogType == DIALOG_TYPE_SUCCESS || dialogType == DIAGLOG_TYPE_USER_CANCEL_ORDER) {
                    EventMsg eventMsg = new EventMsg();
                    eventMsg.setType(0);
                    eventMsg.setCmd(EventMsg.CMD_ORDER_GRAB_SUCCESS);
                    eventMsg.setMessage("刷新订单信息");
                    eventMsg.setOrderId(orderId);
                    EventBus.getDefault().post(eventMsg);
                }
            }

            @Override
            public void onFinish() {
                dialog.dismiss();
            }
        }.start();

        //弹窗消失后的处理
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (timer != null) {
                    timer.cancel();
                }

                if (dialogType == DIALOG_TYPE_ORDER_ALLOCATED) {
                    //如果订单被抢，立刻获取下一个订单
                    getNewOrderData();
                } else if (dialogType == DIALOG_TYPE_SUCCESS) {
                    //抢单成功，如果是货拼客订单，且当前处于送货中页面，跳到首页。
                    String currentActivityName = AppManager.getAppManager().currentActivity().toString();
                    LogUtils.e(TAG, "currentActivityName:" + currentActivityName);
                    if (!TextUtils.isEmpty(currentActivityName) && currentActivityName.contains("OrderDetailActivity")) {
                        if (order.getOrderType() != Order.ORDER_TYPE_CARGO_PASSENGER) {
                            LogUtils.d(TAG, "order is running, do nothing, order type:" + order.getOrderType());
                        } else {
                            Intent intent = new Intent(FloatDialogService.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    } else {
                        Intent intent = new Intent(FloatDialogService.this, OrderDetailActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(OrderDetailActivity.ORDER_ID, orderId);
                        startActivity(intent);
                        return;
                    }
                }

                //在查看地图页面，则直接返回到主页
                int msgType = -1;
                if (dialogType == DIALOG_TYPE_ORDER_ALLOCATED) {
                    msgType = EventMsg.CMD_ORDER_HAS_ALLOCATED;
                } else if (dialogType == DIALOG_TYPE_SUCCESS) {
                    msgType = EventMsg.CMD_ORDER_GRAB_SUCCESS;
                } else if (dialogType == DIALOG_TYPE_FAIL) {
                    msgType = EventMsg.CMD_ORDER_GRAB_FAIL;
                }
                if (msgType != -1) {
                    EventMsg eventMsg = new EventMsg();
                    eventMsg.setType(0);
                    eventMsg.setCmd(msgType);
                    eventMsg.setMessage("订单消息");
                    EventBus.getDefault().post(eventMsg);
                }
            }
        });
    }

    /**
     * Called by the system to notify a Service that it is no longer used and is being removed.  The
     * service should clean up any resources it holds (threads, registered
     * receivers, etc) at this point.  Upon return, there will be no more calls
     * in to this Service object and it is effectively dead.  Do not call this method directly.
     */
    @Override
    public void onDestroy() {
        LogUtils.d(TAG, "onDestroy");
        super.onDestroy();
        EventBus.getDefault().unregister(this);

        //调试用
        if (mScheduledExecutorServiceOne != null) {
            mScheduledExecutorServiceOne.shutdown();
            mScheduledExecutorServiceOne = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    /**
     * 检查抢单中的美团订单状态
     */
    public void onCheckMeituanOrderStatus() {
        LogUtils.d(TAG, "onCheckMeituanOrderStatus");
        if (orderRepository == null) {
            orderRepository = new OrderRepository();
        }
        orderRepository.getWorkOrderDetail(orderId, new ApiSubscriber<OrderDetail>() {
            @Override
            public void onSuccess(OrderDetail orderDetail) {
                LogUtils.d(TAG, "onCheckMeituanOrderStatus Success");
                Order order = OrderConvert.orderDetailToOrder(orderDetail);
                if (order.getOrderStatus() == Order.ORDER_STATUS_INIT && order.getOrderSubStatus() == Order.ORDER_SUB_STATUS_MEITUAN_GRABING) {
                    //6-1，继续转圈
                    showMeiTuanGrabingDialog();
                } else if (order.getOrderStatus() == Order.ORDER_STATUS_WAIT_CAR) {
                    //7-0，提示抢单成功
                    LogUtils.e(TAG, "美团计时弹窗，主动查询状态，抢单成功，关闭");
                    closeMeiTuanGrabingDialog();
                    grabingOrderDialog(orderId, DIALOG_TYPE_SUCCESS);
                } else if (order.getOrderStatus() == Order.ORDER_STATUS_CANCELED) {
                    //5-0，提示抢单失败
                    LogUtils.e(TAG, "美团计时弹窗，主动查询状态，抢单失败，关闭");
                    closeMeiTuanGrabingDialog();
                    grabingOrderDialog(orderId, DIALOG_TYPE_FAIL);
                }
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.e(TAG, "getWorkOrderDetail fail, " + msg);
            }
        });
    }


    /**
     * 取消订单
     *
     * @param orderId
     */
    public void cancelOrder(int orderId) {
        LogUtils.d(TAG, "cancelOrder");
        if (orderRepository == null) {
            orderRepository = new OrderRepository();
        }

        orderRepository.getWorkOrderDetail(orderId, new ApiSubscriber<OrderDetail>() {
            @Override
            public void onSuccess(OrderDetail orderDetail) {
                LogUtils.d(TAG, "getOrderDetail");
                Order order;
                order = OrderConvert.orderDetailToOrder(orderDetail);
                LogUtils.d(TAG, "order: " + order.getStartAddr().toString());
                //如果该订单是 当前正在抢的美团订单，如果是处于美团抢单计时中，显示抢单失败；处于其他状态，显示订单已取消
                if (order.getOrderId() == orderId && channel == Order.ORDER_CHANNEL_MEITUAN && !isGetMeituanOrderResult) {
                    LogUtils.e(TAG, "美团计时弹窗，接收到订单取消消息，抢单失败，关闭");
                    closeMeiTuanGrabingDialog();
                    grabingOrderDialog(orderId, DIALOG_TYPE_FAIL);
                    return;
                }
                orderCancelDialog(order);
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.e(TAG, "getWorkOrderDetail fail, " + msg);
            }
        });
    }

    /**
     * 乘客改变目的地地址
     *
     * @param orderId
     */
    public void changeOrderAddress(int orderId, AddressInfo oldAddress) {
        LogUtils.d(TAG, "changeOrderAddress");
        if (orderRepository == null) {
            orderRepository = new OrderRepository();
        }

        orderRepository.getWorkOrderDetail(orderId, new ApiSubscriber<OrderDetail>() {
            @Override
            public void onSuccess(OrderDetail orderDetail) {
                LogUtils.d(TAG, "getOrderDetail");
                Order order;
                order = OrderConvert.orderDetailToOrder(orderDetail);

                orderChangeAddressDialog(order, oldAddress);
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.e(TAG, "getWorkOrderDetail fail, " + msg);
            }
        });
    }

    /**
     * 获取下一个新的订单
     */
    public void getNewOrderData() {
        if (WebsocketService.isScreenLock) {
            LogUtils.d(TAG, "屏幕锁屏，不请求下一个新订单，不显示弹窗和语音播报");
            return;
        }
        if (!overlayWindowFlag) {
            LogUtils.e(TAG, "没有悬浮窗权限，不请求下一个新订单，不显示弹窗和语音播报");
            return;
        }
        LogUtils.d(TAG, "getNewOrderData");
        if (orderRepository == null) {
            orderRepository = new OrderRepository();
        }
        orderRepository.getNextNewOrder(new ApiSubscriber<NewOrder>() {
            @Override
            public void onSuccess(NewOrder newOrder) {
                LogUtils.d(TAG, "getNextNewOrder success");
                if (newOrder == null) {
                    LogUtils.d(TAG, "getNextNewOrderSuccess 订单为空");
                    return;
                }
                Order order;
                order = OrderConvert.newOrderConvertToOrder(newOrder);
                calculateDistance(order);
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.e(TAG, "getNextNewOrder fail" + "code=" + code + ",msg:" + msg);
            }
        });
    }

    /**
     * 抢单
     *
     * @param orderId 订单id
     */
    public void grabOrder(int orderId) {
        LogUtils.d(TAG, "grabOrder orderId: " + orderId);
        if (orderRepository == null) {
            orderRepository = new OrderRepository();
        }

        //非美团订单，也需要展示抢单中状态。
        final boolean[] needShowGrabingDialog = {true}; //是否展示抢单中的计时弹窗
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (channel != Order.ORDER_CHANNEL_MEITUAN && needShowGrabingDialog[0]) {
                    showMeiTuanGrabingDialog();
                }
            }
        }, 1000);

        orderRepository.grabOrder(orderId, new ApiSubscriber<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                LogUtils.d(TAG, "grabOrder onSuccess status: " + result);

                //非美团订单，关闭抢单中的弹窗
                if (channel != Order.ORDER_CHANNEL_MEITUAN) {
                    closeMeiTuanGrabingDialog();
                    needShowGrabingDialog[0] = false;
                }

                if (result) {
                    //成功
                    if (channel == Order.ORDER_CHANNEL_MEITUAN) { //美团单，弹出计时弹窗
                        showMeiTuanGrabingDialog();
                    } else {
                        grabingOrderDialog(orderId, DIALOG_TYPE_SUCCESS);
                    }
                } else {
                    //失败
                    grabingOrderDialog(orderId, DIALOG_TYPE_FAIL);
                }

            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.e(TAG, "grabOrder fail");

                //非美团订单，关闭抢单中的弹窗
                if (channel != Order.ORDER_CHANNEL_MEITUAN) {
                    closeMeiTuanGrabingDialog();
                    needShowGrabingDialog[0] = false;
                }

            }
        });
    }

    /**
     * 处理eventbus发送过来的推送消息
     * <p>
     * Gps处理
     *
     * @param gpsData
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GpsData gpsData) {
        LogUtils.d(TAG, "on event, gpsData:" + gpsData.toString());
//        currentGps = gpsData;
        setCurrentGps(gpsData);
    }

 /*   @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ScreenLockEvent event) {
        LogUtils.d(TAG, "on event, ScreenLockEvent:" + event.isScreenLock());
        if(newOrderDialog!=null){
            if(event.isScreenLock() && newOrderDialog.isShowing()){
                newOrderDialog.hide();
            }else{
                newOrderDialog.show();
            }
        }
    }*/


    /**
     * 对悬浮窗 Dialog 做对应处理
     * 根据类型 和 操作 做判断
     *
     * @param
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(FloatwindowCmd cmd) {
        LogUtils.d(TAG, "on event, FloatwindowCmd:" + cmd.toString());
        if (cmd.getWindowType() == FloatwindowCmd.WINDOW_TYPE_NEW_ORDER_DIALOG) {
            if (cmd.getOperation() == FloatwindowCmd.OPERATION_SHOW) {
                //显示弹窗
                if (newOrderDialog != null) {
                    newOrderDialog.show();
                }
            } else if (cmd.getOperation() == FloatwindowCmd.OPERATION_CANCEL) {
                //取消弹窗
                if (newOrderDialog != null) {
                    newOrderDialog.cancel();
                }
                grabOrder(orderId);
            } else if (cmd.getOperation() == FloatwindowCmd.OPERATION_HIDE) {
                //隐藏弹窗,延迟一段时间
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (newOrderDialog != null && newOrderDialog.isShowing()) {
                            newOrderDialog.hide();
                        }
                    }
                }, 500);
            }
        } else if (cmd.getWindowType() == FloatwindowCmd.WINDOW_TYPE_MEITUAN_GRABING_DIALOG) {
            //美团抢单中弹窗异常处理
            if (cmd.getOperation() == FloatwindowCmd.OPERATION_CANCEL) {
                LogUtils.e(TAG, "美团计时弹窗，进入详情页必定进行关闭");
                closeMeiTuanGrabingDialog();
            }
        }
    }

    long lastNewMsgTime = System.currentTimeMillis();

    /**
     * 处理eventbus发送过来的新订单推送消息 or 查询美团抢单状态消息
     * 消息流：server-->websocket service-->eventbus-->floatingDiaog
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(WebsocketVoidData data) {
        if (data.getCmdSn() == WsCommands.WEBSOCKET_CLOSE_TO_CONNECT.getSn()) {  //断流重连
            //如果正在抢美团单，且没得到抢单结果，则主动查询订单状态
            if (order != null && channel == Order.ORDER_CHANNEL_MEITUAN && !isGetMeituanOrderResult) {
                onCheckMeituanOrderStatus();
            }
        } else {
            LogUtils.d(TAG, "收到新订单消息");
            //新订单推送消息
            if (System.currentTimeMillis() - lastNewMsgTime < 5000) {
                LogUtils.d(TAG, "message is too frequency, just drop");
                return;
            } else {
                lastNewMsgTime = System.currentTimeMillis();
            }
            if (newOrderDialog != null && newOrderDialog.isShowing()) {
                //新订单弹窗显示中
                LogUtils.d(TAG, "new dialog is showing, just waiting");
            } else if ((meiTuanGrabingDialog != null && meiTuanGrabingDialog.isShowing()) ||
                    (channel == Order.ORDER_CHANNEL_MEITUAN && !isGetMeituanOrderResult)) {
                //美团抢单中，在没得到最终返回结果之前，都不处理这些新订单
                LogUtils.d(TAG, "美团抢单中，等待最终结果，不显示新的订单");
            } else {
                LogUtils.d(TAG, "get next new order");
                newOrderDialog = null;
                meiTuanGrabingDialog = null;
                getNewOrderData();
            }
        }
    }

    /*    *//**
     * 处理eventbus发送过来的消息 订单超时
     * 消息流：server-->websocket service-->eventbus-->floatingDiaog
     *//*
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(WebsocketListData websocketData) {
        if (websocketData.getData() == null) {
            LogUtils.e(TAG, "websocketData.getData is null");
            return;
        }
        int orderId = websocketData.getData().getOrderIds().get(0);
        LogUtils.d(TAG, "订单超时， orderId:" + orderId);
        orderOvertimeDialog(orderId);
    }*/

    /**
     * 处理eventbus发送过来的推送消息--(取消/支付完成/美团单抢单成功 or 失败)
     * 消息流：server-->websocket service-->eventbus-->floatingDiaog
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(WebsocketData websocketData) {
        LogUtils.d(TAG, "on event WebsocketData:" + websocketData.toString());
        int messageId = 0;
        if (null != websocketData.getData()) {
            messageId = websocketData.getData().getMessageId();
        }

        if (websocketData.getCmdSn() == WsCommands.GOODS_ORDER_PICKED_UP.getSn()) {
            LogUtils.d(TAG, "已收货" + websocketData.getData());
        } else if (websocketData.getCmdSn() == WsCommands.GOODS_ORDER_DELIVERED.getSn()) {
            LogUtils.d(TAG, "货物已投递" + websocketData.getData());
        } else if (websocketData.getCmdSn() == WsCommands.DRIVER_ORDER_CANCEL_BY_USER.getSn()
                || websocketData.getCmdSn() == WsCommands.DRIVER_ORDER_CANCEL_BY_SYSTEM.getSn()
                || websocketData.getCmdSn() == WsCommands.DRIVER_ORDER_CANCEL_BY_USER_BEFORE_TAKE_ORDER.getSn()
                || websocketData.getCmdSn() == WsCommands.GOODS_ORDER_CANCELLED.getSn()) {

            //订单取消，系统 or 用户 or 货单
            int orderId = websocketData.getData().getOrderId();
            if (isAppShowing()) {
                cancelOrder(orderId);
                updateMessage(messageId, true);
            } else {
                showInNotification(websocketData, websocketData.getData().getOrderId());
            }
        } else if (websocketData.getCmdSn() == WsCommands.DRIVER_ORDER_PAID.getSn()) {
            if (isAppShowing()) {
                updateMessage(messageId, true);
            } else {
                showInNotification(websocketData, websocketData.getData().getOrderId());
            }
            LogUtils.d(TAG, "订单已被支付" + websocketData.getData());
        } else if (websocketData.getCmdSn() == WsCommands.DRIVER_ORDER_ALLOCATED.getSn()) {
            LogUtils.d(TAG, "订单已被抢" + websocketData.getData());
            //抢单已经被抢
            if (newOrderDialog != null && newOrderDialog.isShowing()) {
                newOrderDialog.cancel();
                newOrderDialog = null;
                if (websocketData.getData() != null) {
                    grabingOrderDialog(websocketData.getData().getOrderId(), DIALOG_TYPE_ORDER_ALLOCATED);
                }
            }
        } else if (websocketData.getCmdSn() == WsCommands.DRIVER_VEHICLE_DISABLE.getSn()) {
            LogUtils.d(TAG, "车辆已经停用" + websocketData.getData());
            handleVehcileleStopped(websocketData.getData().getVehicleId());
        } else if (websocketData.getCmdSn() == WsCommands.DRIVER_ORDER_OVERTIME.getSn()) {
            if (websocketData.getData() != null) {
                int orderId = websocketData.getData().getOrderIds().get(0);
                LogUtils.d(TAG, "订单超时， orderId:" + orderId);
                orderOvertimeDialog(orderId);
            }
        } else if (websocketData.getCmdSn() == WsCommands.DRIVER_ORDER_DESTINATION_CHANGED.getSn()) {
            if (isAppShowing()) {
                updateMessage(messageId, true);

                int orderId = websocketData.getData().getOrderId();
                LogUtils.d(TAG, "改变地址" + orderId);
                AddressInfo addressInfo = new AddressInfo();
                addressInfo.setName(websocketData.getData().getOldPlace());
                addressInfo.setAddressDetail(websocketData.getData().getOldAddress());
                changeOrderAddress(orderId, addressInfo);
            } else {
                showInNotification(websocketData, websocketData.getData().getOrderId());
            }
        } else if (websocketData.getCmdSn() == WsCommands.DRIVER_UNPAID_ORDER_TO_CONTROVERSY_ORDER.getSn()) {
            //争议订单
            if (isAppShowing()) {
                updateMessage(messageId, true);

                int orderId = websocketData.getData().getOrderId();
                orderStatusToControversy(orderId);
            } else {
                showInNotification(websocketData, websocketData.getData().getOrderId());
            }
        } else if (websocketData.getCmdSn() == WsCommands.DRIVER_NEW_ORDER_MEITUAN_GRAB_SUCCESS.getSn()) {
            //实时订单-美团，抢单成功,取消美团抢单中弹窗,显示抢单成功弹窗
            closeMeiTuanGrabingDialog();
            int orderId = websocketData.getData().getOrderId();
            grabingOrderDialog(orderId, DIALOG_TYPE_SUCCESS);
        }else if (websocketData.getCmdSn()==WsCommands.DRIVER_INFO_CHANGE.getSn()){
            //如果司机信息发生改变，则弹窗提示重新登录
            driverInfoChangeReLoginDialog(mContext.getResources().getString(R.string.relogin_hint));
        }else if (websocketData.getCmdSn()==WsCommands.DRIVER_ORDER_EXCEPTION_CANCEL.getSn()){
            //系统异常结束订单
            int orderId = websocketData.getData().getOrderId();
            if (isAppShowing()) {
                exceptionCancelOrder(orderId);
                updateMessage(messageId, true);
            } else {
                showInNotification(websocketData, websocketData.getData().getOrderId());
            }
        }
    }

    /**
     * 系统异常取消订单
     * @param orderId
     */
    private void exceptionCancelOrder(int orderId) {
        LogUtils.d(TAG, "exceptionCancelOrder");
        if (orderRepository == null) {
            orderRepository = new OrderRepository();
        }

        orderRepository.getWorkOrderDetail(orderId, new ApiSubscriber<OrderDetail>() {
            @Override
            public void onSuccess(OrderDetail orderDetail) {
                LogUtils.d(TAG, "getOrderDetail");
                Order order;
                order = OrderConvert.orderDetailToOrder(orderDetail);
                LogUtils.d(TAG, "order: " + order.getStartAddr().toString());
                orderExceptionCancelDialog(order);
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.e(TAG, "getWorkOrderDetail fail, " + msg);
            }
        });
    }

    /**
     * @param
     * @return
     * @method
     * @description 上报消息已显示
     * @date: 2019/12/12 10:38
     * @author: tandongdong
     */
    public void updateMessage(int messageId, boolean displayed) {

        List<MessageDetailSimple> list = new ArrayList<>();
        MessageDetailSimple simple = new MessageDetailSimple();

        simple.setMessageId(messageId);
        if (displayed) {
            simple.setDisplay(0); //0表示不展示
        }
        simple.setViewed(0);
        simple.setDeleted(0);
        list.add(simple);

        orderRepository.updateMessageList(list, new ApiSubscriber<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                LogUtils.d(TAG, "消息已显示上报成功");
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.d(TAG, "消息已显示上报失败：" + code + msg);
            }
        });
    }

    private void showInNotification(WebsocketData websocketData, int orderId) {
        LogUtils.d(TAG, "getOrderDetailByNotification");
        if (orderRepository == null) {
            orderRepository = new OrderRepository();
        }
        orderRepository.getWorkOrderDetail(orderId, new ApiSubscriber<OrderDetail>() {
            @Override
            public void onSuccess(OrderDetail orderDetail) {
                LogUtils.d(TAG, "getOrderDetail");
                Order order;
                order = OrderConvert.orderDetailToOrder(orderDetail);

                //获取订单后
                int notiId = notificationId++;
                Notification notification = buildNotification(websocketData, order);
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(notiId, notification);
                LogUtils.d(TAG, "notificationId：" + notificationId);
                //todo 计时
                CountDownTimer timer = new CountDownTimer(1000 * 60 * 10, 1000 * 60) {
                    /**
                     * Callback fired on regular interval.
                     *
                     * @param millisUntilFinished The amount of time until finished.
                     */
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    /**
                     * Callback fired when the time is up.
                     */
                    @Override
                    public void onFinish() {
                        if (mNotificationManager != null) {
                            mNotificationManager.cancel(notiId);
                        }
                    }
                };
                timer.start();
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.e(TAG, "getWorkOrderDetail fail, " + msg);
            }
        });
    }

    private static final String NOTIFICATION_CHANNEL_NAME = "通知消息";
    private NotificationManager notificationManager = null;
    boolean isCreateChannel = false;

    /**
     * 针对8.0以后版本，将定位服务设置横前台服务，以防止服务被后台杀掉。
     * 通过这个通知栏跳转的，需要先判断App是否存活。
     *
     * @return
     */
    @SuppressLint("NewApi")
    private Notification buildNotification(WebsocketData websocketData, Order order) {

        Notification.Builder builder = null;
        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            //Android O上对Notification进行了修改，如果设置的targetSDKVersion>=26建议使用此种方式创建通知栏
            if (null == notificationManager) {
                notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            }
//            String channelId = mContext.getPackageName();
            String channelId = "id_user_msg";
            if (!isCreateChannel) {
                NotificationChannel notificationChannel = new NotificationChannel(channelId,
                        NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.enableLights(true);//是否在桌面icon右上角展示小圆点
                notificationChannel.setLightColor(Color.BLUE); //小圆点颜色
                notificationChannel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
                notificationManager.createNotificationChannel(notificationChannel);
                isCreateChannel = true;
            }
            builder = new Notification.Builder(mContext, channelId);
        } else {
            builder = new Notification.Builder(mContext);
        }

        //点击通知栏，跳转到对应的页面
        String content = "";
        Intent intent_target = new Intent(); //todo
        intent_target.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (websocketData.getCmdSn() == WsCommands.DRIVER_ORDER_PAID.getSn()) {
            if (order.getOrderType() == Order.ORDER_TYPE_SEND_CHILD) {
                content = "家长已完成支付，点击查看订单详情";
            } else {
                content = "乘客已完成支付，点击查看订单详情";
            }
            intent_target.putExtra(ORDER_ID, orderId);
            intent_target.putExtra(SHOW_LISTEN_BUTTON, false);
            intent_target.setClass(this, TripDetailActivity.class);
            intent_target.putExtra(OrderDetailActivity.ORDER_ID, websocketData.getData().getOrderId());
        } else if (websocketData.getCmdSn() == WsCommands.DRIVER_ORDER_CANCEL_BY_USER.getSn()
                || websocketData.getCmdSn() == WsCommands.DRIVER_ORDER_CANCEL_BY_SYSTEM.getSn()
                || websocketData.getCmdSn() == WsCommands.DRIVER_ORDER_CANCEL_BY_USER_BEFORE_TAKE_ORDER.getSn()) {
            content = "订单已被取消，点击查看订单详情";
            intent_target.putExtra(ORDER_ID, orderId);
            intent_target.putExtra(SHOW_LISTEN_BUTTON, false);
            intent_target.setClass(this, TripDetailActivity.class);
            intent_target.putExtra(OrderDetailActivity.ORDER_ID, websocketData.getData().getOrderId());

        } else if (websocketData.getCmdSn() == WsCommands.DRIVER_ORDER_DESTINATION_CHANGED.getSn()) {
            intent_target.putExtra(ORDER_ID, orderId);
            intent_target.setClass(this, OrderDetailActivity.class);
            intent_target.putExtra(OrderDetailActivity.ORDER_ID, websocketData.getData().getOrderId());
            content = "乘客已修改下车点，点击查看新的下车点";
        } else if (websocketData.getCmdSn() == WsCommands.DRIVER_UNPAID_ORDER_TO_CONTROVERSY_ORDER.getSn()) {
            intent_target.setClass(this, TripDetailActivity.class);
            intent_target.putExtra(ORDER_ID, orderId);
            intent_target.putExtra(SHOW_LISTEN_BUTTON, false);
            intent_target.putExtra(OrderDetailActivity.ORDER_ID, websocketData.getData().getOrderId());
            content = "订单已更新成支付争议状态，点击查看订单详情";
        }

        NotificationData notificationData = new NotificationData();
        notificationData.setMessageId(websocketData.getData().getMessageId());
        intent_target.putExtra(FORM_NOTIFICATION_CLICK, true);
        intent_target.putExtra(FORM_NOTIFICATION_MESSAGE_ID, websocketData.getData().getMessageId());


        //调转到指定的页面
        Intent[] intents = new Intent[2];
        Intent intent_main = new Intent(getApplicationContext(), MainActivity.class);
        intents[0] = intent_main;
        intents[1] = intent_target;
        PendingIntent contentIntent = PendingIntent.getActivities(this, 0, intents, PendingIntent.FLAG_UPDATE_CURRENT);

        LogUtils.d(TAG, "content:" + content);
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("麦诗鹏电司机")
                .setContentText(content)
                .setOngoing(true)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis());

        if (android.os.Build.VERSION.SDK_INT >= 16) {
            notification = builder.build();
        } else {
            return builder.getNotification();
        }

        return notification;
    }

    /**
     * @class FloatDialogService
     * @description 描述一下类的作用
     * @date: 2019/12/6 16:47
     * @author: tandongdong
     */
    private boolean isAppShowing() {
        //toodo
        boolean background = false;
        background = isBackground(this);
        boolean ScreenOff = isScreenOff(this);
        LogUtils.d(TAG, "background:" + background + ",ScreenOff:" + ScreenOff);

        return (!background && !ScreenOff);
    }

    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                /*
                BACKGROUND=400 EMPTY=500 FOREGROUND=100
                GONE=1000 PERCEPTIBLE=130 SERVICE=300 ISIBLE=200
                 */
                LogUtils.i(context.getPackageName(), "此appimportace ="
                        + appProcess.importance
                        + ",context.getClass().getName()="
                        + context.getClass().getName());
                if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    LogUtils.i(context.getPackageName(), "处于后台"
                            + appProcess.processName);
                    return true;
                } else {
                    LogUtils.i(context.getPackageName(), "处于前台"
                            + appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }


    /**
     * 屏幕是否黑屏（完全变黑那种，屏幕变暗不算）
     *
     * @param context 上下文
     * @return 屏幕变黑，则返回true；屏幕变亮，则返回false
     */
    public static boolean isScreenOff(Context context) {
        KeyguardManager manager = (KeyguardManager) context.getSystemService(context.KEYGUARD_SERVICE);
        return manager.inKeyguardRestrictedInputMode();
    }


    /**
     * 订单状态转换成待争议状态
     *
     * @param orderId
     */
    public void orderStatusToControversy(int orderId) {
        LogUtils.d(TAG, "controversyOrder");
        if (orderRepository == null) {
            orderRepository = new OrderRepository();
        }

        orderRepository.getWorkOrderDetail(orderId, new ApiSubscriber<OrderDetail>() {
            @Override
            public void onSuccess(OrderDetail orderDetail) {
                LogUtils.d(TAG, "getOrderDetail");
                Order order;
                order = OrderConvert.orderDetailToOrder(orderDetail);
                LogUtils.d(TAG, "order: " + order.getStartAddr().toString());

                orderStatusToControversyDialog(order);
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.e(TAG, "getWorkOrderDetail fail, " + msg);
            }
        });
    }


    /**
     * 订单状态转换成待争议状态的弹窗
     * 1）如果当前页面是状态更新订单的页面，点击“我知道了”返回听单页
     * 2）如果当前页面不是状态更新订单的页面，点击“我知道了”返回到原来显示的页面
     *
     * @param order
     */
    private void orderStatusToControversyDialog(Order order) {
        if (order == null) {
            LogUtils.e(TAG, "争议弹窗，订单为空");
            return;
        }
        LogUtils.d(TAG, "orderStatusToControversyDialog, " + order.toString());
        if (AppManager.getAppManager().currentActivity() != null) {
            OrderStatusToControversyDialog dialog = new OrderStatusToControversyDialog(AppManager.getAppManager().currentActivity());
            dialog.setData(order);
            dialog.show();
        }
        
    }


    /**
     * 车辆已经停用
     */
    private void handleVehcileleStopped(int vehicleId) {
        String stoppedNum = "";
        //清除听单车辆
        if (PrefserHelper.getVehicleInfo() != null && (PrefserHelper.getVehicleInfo().getId() == vehicleId)) {
            PrefserHelper.removeVehicleInfo();
        }

        //清除本地存储的车辆列表中的这辆车
        List<Vehicle> list = PrefserHelper.getVehicleList();
        for (int i = list.size() - 1; i >= 0; i--) {
            Vehicle vehicle = list.get(i);
            LogUtils.d(TAG, "更新前： i=" + i + ", " + vehicle.toString());
        }

        for (int i = list.size() - 1; i >= 0; i--) {
            Vehicle vehicle = list.get(i);
            if (vehicleId == vehicle.getId()) {
                stoppedNum = vehicle.getNumber();
                list.remove(vehicle);
            }
        }

        LogUtils.d(TAG, "list:" + list.toString());

        if (list == null || list.size() == 0) {
            PrefserHelper.removeVehicleInfoList();
        } else {
            PrefserHelper.saveVehicleList(list);
        }

        List<Vehicle> listPost = PrefserHelper.getVehicleList();
        for (int i = listPost.size() - 1; i >= 0; i--) {
            Vehicle vehicle = listPost.get(i);
            LogUtils.d(TAG, "更新后： i=" + i + ", " + vehicle.toString());
        }


        String status = PrefserHelper.getCache(PrefserHelper.KEY_ORDER_LISTEN_STATUS);
        if (("1").equals(status)) {
            String message = "您绑定的车辆 " + stoppedNum + " 已停用，请选择其他车辆";
            showVehicleHasStoppedTipsDialog(message, 0);
        } else {
            LogUtils.d(TAG, "车辆停用");
            EventMsg eventMsg = new EventMsg();
            eventMsg.setType(0);
            eventMsg.setCmd(EventMsg.CMD_ORDER_VEHICLE_DISABLE);
            eventMsg.setMessage("车辆停用");
            EventBus.getDefault().post(eventMsg);
        }

    }


    /**
     * @param
     * @return
     * @method
     * @description 车辆停用的消息弹窗
     * @date: 2019/12/17 11:57
     * @author: tandongdong
     */
    private void showVehicleHasStoppedTipsDialog(String message, int dialogType) {
        LogUtils.d(TAG, "showVehicleHasStoppedTipsDialog");
        Dialog dialog = new Dialog(this, R.style.Translucent_NoTitle);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.info_dialog_frag, null);

        //view 显示
        TextView tvMessage = view.findViewById(R.id.tv_message);
        TextView btnOK = view.findViewById(R.id.btn_positive);
        TextView btnConfirm = view.findViewById(R.id.btn_confirm);
        TextView btnCancel = view.findViewById(R.id.btn_negative);
        ImageView btnCloseDialog = view.findViewById(R.id.iv_close_dialog);

        tvMessage.setText(message);
        btnOK.setText("我知道了");
        btnCloseDialog.setVisibility(View.VISIBLE);

        //操作
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(FloatDialogService.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(FloatDialogService.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        btnCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                LogUtils.d(TAG, "onDismiss");
                EventMsg eventMsg = new EventMsg();
                eventMsg.setType(0);
                eventMsg.setCmd(EventMsg.CMD_ORDER_VEHICLE_DISABLE);
                eventMsg.setMessage("车辆停用");
                EventBus.getDefault().post(eventMsg);
            }
        });

        if (Build.VERSION.SDK_INT >= 26) {
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        } else {
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        dialog.show();
        dialog.getWindow().setContentView(view);
    }


    /********************************* 美团单 ******************************************************/
    /**
     * 美团抢单中弹窗
     */
    private MeiTuanGrabingDialog meiTuanGrabingDialog;

    /**
     * 美团抢单中弹窗 - 显示
     * 美团抢单流程如下：
     * 1.向后台发起抢单，显示计时弹窗
     * 2.后台推送抢单结果，隐藏计时弹窗（如果断流重连，主动查询该订单状态）
     * 异常情况：
     * 1.后台先推送了抢单结果，再返回显示计时，导致计时弹窗永远不关闭，须在详情页做异常处理。（少见）
     */
    private void showMeiTuanGrabingDialog() {
        isGetMeituanOrderResult = false;
        if (meiTuanGrabingDialog != null && meiTuanGrabingDialog.isShowing()) {
            return;
        }
        if (meiTuanGrabingDialog == null) {
            meiTuanGrabingDialog = new MeiTuanGrabingDialog(this);
        }
        meiTuanGrabingDialog.showDialog();
    }

    /**
     * 美团抢单中弹窗 - 关闭
     * 执行这个方法，必须是得到了该美团单的抢单结果
     */
    private void closeMeiTuanGrabingDialog() {
        isGetMeituanOrderResult = true;
        if (meiTuanGrabingDialog != null) {
            meiTuanGrabingDialog.dismiss();
            meiTuanGrabingDialog = null;
        }
    }

    /********************************* 美团单 ******************************************************/

    /**
     * 与订单详情页语音有关的播报
     * 订单详情页 和 导航页操作互相影响，且导航页销毁时去播放语音，会出现播报不完整的问题。
     * 因此，凡是导航页操作的下一个步骤要播放语音，都要延迟一段事件进行播放。
     *
     * @param eventBean
     */
    private String orderVoiceContent;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVoiceEvent(EventBean eventBean) {
        if (eventBean.getCode() == Constants.EventCode.VOICE_PROMPT_WAITING_PASSENGER_GET_ON
                || eventBean.getCode() == Constants.EventCode.VOICE_PROMPT_PASSENGER_GET_ON
                || eventBean.getCode() == Constants.EventCode.VOICE_PROMPT_WAITING_DRIVER_TAKE_PHOTO_FOR_GET_ON
                || eventBean.getCode() == Constants.EventCode.VOICE_PROMPT_ARRIVE_AT_DESTINATION
                || eventBean.getCode() == Constants.EventCode.VOICE_PROMPT_GO_TO_DESTINATION
                || eventBean.getCode() == Constants.EventCode.VOICE_PROMPT_WATTING_PASSENGER_CONFIRM_GET_ON_PHOTO
                || eventBean.getCode() == Constants.EventCode.VOICE_PROMPT_WATTING_PASSENGER_CONFIRM_GET_OFF_PHOTO) {

            orderVoiceContent = "";
            if (eventBean.getCode() == Constants.EventCode.VOICE_PROMPT_WAITING_PASSENGER_GET_ON) {
                //到达上车点，等待乘客上车
                orderVoiceContent = getString(R.string.voice_prompt_waiting_passenger_get_on);
            } else if (eventBean.getCode() == Constants.EventCode.VOICE_PROMPT_PASSENGER_GET_ON) {
                //乘客已上车
                orderVoiceContent = getString(R.string.voice_prompt_passenger_get_on);
            } else if (eventBean.getCode() == Constants.EventCode.VOICE_PROMPT_ARRIVE_AT_DESTINATION) {
                //达到目的地
                orderVoiceContent = getString(R.string.voice_prompt_arrive_at_destination);
            }

            switch (eventBean.getCode()) {
                case Constants.EventCode.VOICE_PROMPT_WAITING_DRIVER_TAKE_PHOTO_FOR_GET_ON:
                    orderVoiceContent = getString(R.string.voice_prompt_take_get_on_photo);
                    break;
                case Constants.EventCode.VOICE_PROMPT_GO_TO_DESTINATION:
                    orderVoiceContent = getString(R.string.voice_prompt_go_to_destination);
                    break;
                case Constants.EventCode.VOICE_PROMPT_WATTING_PASSENGER_CONFIRM_GET_ON_PHOTO:
                    orderVoiceContent = getString(R.string.voice_prompt_waiting_confirm_get_on_photo);
                    break;
                case Constants.EventCode.VOICE_PROMPT_WATTING_PASSENGER_CONFIRM_GET_OFF_PHOTO:
                    orderVoiceContent = getString(R.string.voice_prompt_waiting_confirm_get_off_photo);
                    break;
                default:
                    break;
            }

            if (eventBean.getData() != null) {
                //延迟1s执行，防止因正在销毁语音而导致此语音中断
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        TTSUtil.playVoice(orderVoiceContent);
                    }
                }, 1000);
            } else {
                TTSUtil.playVoice(orderVoiceContent);
            }
        }
    }

    /**
     * @param
     * @return
     * @method
     * @description 乘客点击通知栏的消息，上报消息已读
     * @date: 2019/12/17 11:32
     * @author: tandongdong
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotificationClickEvent(MessageDetail messageDetail) {
        updateMessage(messageDetail);

    }

    /**
     * @param
     * @return
     * @method
     * @description 更新系统消息的状态，包含已读，已删除，已显示
     * @date: 2019/12/17 11:32
     * @author: tandongdong
     */
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

        orderRepository.updateMessageList(list, new ApiSubscriber<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                LogUtils.d(TAG, "刷新成功");
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.d(TAG, "操作失败");
            }
        });
    }

    /**
     * @param
     * @return
     * @method
     * @description 更新某一条通知消息的状态
     * @date: 2019/12/17 11:32
     * @author: tandongdong
     */
    public void updateMessage(MessageDetail messageDetail) {
        List<MessageDetail> list = new ArrayList<MessageDetail>();
        list.add(messageDetail);
        updateMessage(list);
    }

}
