package com.haylion.android.common.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.CountDownTimer;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.haylion.android.R;
import com.haylion.android.common.map.AMapUtil;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.model.OrderTypeInfo;
import com.haylion.android.data.util.BusinessUtils;

import com.haylion.android.data.util.NumberUtil;
import com.haylion.android.data.util.SpannableStringUtil;
import com.haylion.android.data.util.StatusBarUtils;
import com.haylion.android.data.util.TTSUtil;
import com.haylion.android.mvp.util.LogUtils;
import com.haylion.android.service.FloatDialogService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * @author dengzh
 * @date 2019/12/2
 * Description: 抢单弹窗，拆分 FloatDialogService
 * 未完整
 */
public class NewOrderDialog extends Dialog {

    private String TAG = getClass().getSimpleName();

    /**
     * 填充状态栏高度
     * */
    @BindView(R.id.view_status_bar)
    View viewStatusBar;
    @BindView(R.id.tv_order_type)
    TextView tvOrderType;  //订单类型


    /**
     * 距离
     * */
    @BindView(R.id.tv_distance_fixed)
    TextView tvDistanceFixed;  //距你
    @BindView(R.id.tv_distance_fixed_unit)
    TextView tvDistanceFixedUnit;
    @BindView(R.id.tv_distance)
    TextView tvDistance;  //全程
    @BindView(R.id.tv_distance_unit)
    TextView tvDistanceUnit;

    /**
     * 地址
     * */
    @BindView(R.id.tv_order_get_on_address)
    TextView tvStartAddress;
    @BindView(R.id.tv_order_get_on_address_describe)
    TextView tvStartAddressDescribe;
    @BindView(R.id.tv_order_get_off_address)
    TextView tvEndAddress;
    @BindView(R.id.tv_order_get_off_address_describe)
    TextView tvEndAddressDescribe;

    /**
     * 价格-拼车单显示
     * */
    @BindView(R.id.ll_price)
    LinearLayout llPrice;
    @BindView(R.id.total_cost)
    TextView tvTotalCost;

    /**
     * 推荐级别-货拼客单显示
     * */
    @BindView(R.id.tv_road_level)
    TextView tvRoadLevel;
    @BindView(R.id.tv_amount_level)
    TextView tvAmountLevel;
    @BindView(R.id.rb_star)
    RatingBar rbStar;
    @BindView(R.id.tv_rest_time_send_cargo)
    TextView tvRestTimeSendCargo;
    @BindView(R.id.order_level)
    LinearLayout orderLevel;


    /**
     * 抢单按钮
     * */
    @BindView(R.id.btn_grab_order)
    RelativeLayout btnGrabOrder;
    @BindView(R.id.ll_waiting_time)
    LinearLayout llWaitingTime;
    @BindView(R.id.tv_waiting_time)
    TextView tvWaitingTime;
    @BindView(R.id.tv_rob)
    TextView tvRob;
    @BindView(R.id.tv_rob_time)
    TextView tvRobTime;


    private Context mContext;

    public NewOrderDialog(@NonNull Context context) {
        super(context, R.style.Translucent_NoTitle);
        setContentView(R.layout.dialog_new_order);
        ButterKnife.bind(this);
        mContext = context;
        initView();
    }

    private void initView(){
        if (Build.VERSION.SDK_INT >= 26) {
            getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        } else {
            getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        //让Dialog 填满宽高,放在setContentView之后
        getWindow().setLayout((ViewGroup.LayoutParams.MATCH_PARENT), ViewGroup.LayoutParams.MATCH_PARENT);
        //设置Dialog全屏显示，遮盖状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        setCanceledOnTouchOutside(false);

        setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (timer != null) {
                    LogUtils.d(TAG, "newOrderDialog onDismiss");
                    timer.cancel();
                    timer = null;
                }
                //停止播报
                TTSUtil.stop();
            }
        });

        //状态栏高度
        RelativeLayout.LayoutParams statusParams = (RelativeLayout.LayoutParams) viewStatusBar.getLayoutParams();
        statusParams.height = StatusBarUtils.getStatusBarHeight(mContext);
        viewStatusBar.setLayoutParams(statusParams);

    }


    private long mGrabOrderDistance;
    public void setData(long grabOrderDistance,Order order){
        mGrabOrderDistance = grabOrderDistance;

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
            //推荐指数
            orderLevel.setVisibility(View.VISIBLE);
            rbStar.setRating(order.getStarLevel());
            tvRoadLevel.setText(order.getRoadLevel() +"");
            tvAmountLevel.setText(order.getAmountLevel() +"");

            String time = BusinessUtils.formatEstimateTimeText(order.getMinPreservedSeconds());
            String str = "已预留" + time + "停车上门送货。";
            SpannableString spannableString = SpannableStringUtil.setSpan(str, R.color.c_ffc800, 3, time.length() + 3);
            tvRestTimeSendCargo.setText(spannableString);
        } else {
            tvOrderType.setText("新订单");
        }

        //计算上车点离车辆的距离
        if (mGrabOrderDistance > 1000) {
            tvDistanceFixed.setText(NumberUtil.roundHalfUp(mGrabOrderDistance / 1000.0, 1) + "");
            tvDistanceFixedUnit.setText("公里");
        } else {
            tvDistanceFixed.setText(mGrabOrderDistance + "");
            tvDistanceFixedUnit.setText("米");
        }
        //计算全程公里数
        long distance = order.getDistance();
        if (distance > 1000) {
            tvDistance.setText(NumberUtil.roundHalfUp(distance / 1000.0, 1) + "");
            tvDistanceUnit.setText("公里");
        } else {
            tvDistance.setText(distance + "");
            tvDistanceUnit.setText("米");
        }
        //上车和下车位置
        tvStartAddress.setText(order.getStartAddr().getName());
        tvStartAddressDescribe.setText(AMapUtil.getAddress(order.getStartAddr().getAddressDetail()));
        tvEndAddress.setText(order.getEndAddr().getName());
        tvEndAddressDescribe.setText(AMapUtil.getAddress(order.getEndAddr().getAddressDetail()));

        //倒计时
        onCountDown();
        //语音播报
        onVoicePlay(order);

        //显示
        LogUtils.d(TAG, "newOrderDialog show");
        show();
    }

    /**
     * 语音播报
     * @param order
     */
    private void onVoicePlay(Order order){
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


    private CountDownTimer timer;
    /**
     * 开始倒计时
     * 开始前，取消上一次的倒计时
     */
    private void onCountDown(){
        if(timer!=null){
            timer.cancel();
        }
        timer = new CountDownTimer(FloatDialogService.GRAB_ORDER_TOTAL_TIME, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                FloatDialogService.grabOrderResetTime = millisUntilFinished + 1000;
                Log.d(TAG, "onTick:" + FloatDialogService.grabOrderResetTime / 1000);

                if (millisUntilFinished > FloatDialogService.GRAB_ORDER_TIME) {  //处于等待的3s中
                    tvWaitingTime.setText(((FloatDialogService.grabOrderResetTime - FloatDialogService.GRAB_ORDER_TIME) / 1000)+"");
                    btnGrabOrder.setEnabled(false);
                } else { //处于抢单的15s中
                    llWaitingTime.setVisibility(View.GONE);
                    tvRob.setVisibility(View.VISIBLE);
                    tvRobTime.setVisibility(View.VISIBLE);
                    tvRobTime.setText((FloatDialogService.grabOrderResetTime / 1000) + "s");
                    btnGrabOrder.setEnabled(true);
                }
            }

            @Override
            public void onFinish() {
                FloatDialogService.grabOrderResetTime = 0;
                LogUtils.d(TAG, "onTickFinish:" + FloatDialogService.grabOrderResetTime / 1000);
                dismiss();
                //倒计时结束后，延迟2s，请求下一个新的订单

                /*handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getNewOrderData();
                    }
                }, 2000);*/
            }
        }.start();
    }

    @OnClick({R.id.iv_close_dialog, R.id.ll_find_in_map, R.id.btn_grab_order})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_close_dialog:
                cancel();
                //获取下一个订单
                break;
            case R.id.ll_find_in_map:
                break;
            case R.id.btn_grab_order:
                break;
        }
    }
}
