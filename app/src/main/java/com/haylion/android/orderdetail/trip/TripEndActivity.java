package com.haylion.android.orderdetail.trip;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.gyf.immersionbar.ImmersionBar;
import com.haylion.android.MyApplication;
import com.haylion.android.R;
import com.haylion.android.common.view.CargoRestTimeView2;
import com.haylion.android.data.model.EventMsg;
import com.haylion.android.data.model.MessageDetail;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.model.PayInfo;
import com.haylion.android.data.model.WebsocketData;
import com.haylion.android.data.util.BusinessUtils;
import com.haylion.android.data.util.DateUtils;
import com.haylion.android.main.MainActivity;
import com.haylion.android.data.base.BaseActivity;
import com.haylion.android.orderdetail.OrderDetailActivity;
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
import static com.haylion.android.orderdetail.OrderDetailActivity.ORDER_ID;


/**
 * @author dengzh
 * @date 2019/10/23
 * Description: 行程结束页
 */
public class TripEndActivity extends BaseActivity<TripContract.Presenter> implements TripContract.View {

    private final String TAG = getClass().getSimpleName();


    @BindView(R.id.iv_status)
    ImageView ivStatus;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.tv_amt)
    TextView tvAmt;

    @BindView(R.id.tv_confirm)
    TextView tvConfirm;    //普通订单的按钮


    /**
     * 货拼客单 按钮
     * */
    @BindView(R.id.ll_cargo_btn_container)
    LinearLayout llCargoBtnContainer;
    @BindView(R.id.tv_cargo_paid)
    TextView tvCargoPaid;

    /**
     * 货拼客单页面需要展示货单送达的时间。
     */
    @BindView(R.id.cargo_rest_time_view)
    CargoRestTimeView2 mCargoRestTimeView;


    /**
     * 订单
     */
    private Order order;
    private int orderId;


    private static final String IS_FROM_PAYMAIN = "isFromPayMain";
    private boolean isFromPayMain; //是否来自支付页面

    /**
     * 跳转
     * @param context
     * @param orderId 订单id
     * @param isFromPayMain 是否来自支付页面
     */
    public static void go(Context context,int orderId,boolean isFromPayMain){
        Intent intent = new Intent(context,TripEndActivity.class);
        intent.putExtra(OrderDetailActivity.ORDER_ID, orderId);
        intent.putExtra(IS_FROM_PAYMAIN,isFromPayMain);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_end);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        ImmersionBar.with(this).statusBarColor(R.color.c_4b8afb)
                .statusBarDarkFont(true, 0.2f)
                .init();

        orderId = getIntent().getIntExtra(ORDER_ID, 0);
        isFromPayMain = getIntent().getBooleanExtra(IS_FROM_PAYMAIN,false);

        //上报消息已读
        boolean notificationClick = getIntent().getBooleanExtra(FORM_NOTIFICATION_CLICK, false);
        int messageId = getIntent().getIntExtra(FORM_NOTIFICATION_MESSAGE_ID, 0);
        if(notificationClick){
            MessageDetail messageDetail = new MessageDetail();
            messageDetail.setMessageId(messageId);
            messageDetail.setViewed(1);
            messageDetail.setDisplay(1);
            messageDetail.setDeleted(0);
            EventBus.getDefault().post(messageDetail);
        }

    }

    @Override
    protected  void onNewIntent(Intent intent){
        Log.d(TAG, "onNewIntent");
        super.onNewIntent(intent);
        setIntent(intent);
        orderId = getIntent().getIntExtra(ORDER_ID, 0);
        isFromPayMain = getIntent().getBooleanExtra(IS_FROM_PAYMAIN,false);

        //上报消息已读
        boolean notificationClick = getIntent().getBooleanExtra(FORM_NOTIFICATION_CLICK, false);
        int messageId = getIntent().getIntExtra(FORM_NOTIFICATION_MESSAGE_ID, 0);
        if(notificationClick){
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
     * 由于启动模式是 SingleTop,在栈顶再次跳转入此页面，执行 onNewIntent()，需要把所有属性设置为初始状态
     */
    private void resetInitView(){
        tvConfirm.setVisibility(View.GONE);
        mCargoRestTimeView.setVisibility(View.GONE);
        mCargoRestTimeView.stopCountDown();
        llCargoBtnContainer.setVisibility(View.GONE);
        tvCargoPaid.setVisibility(View.GONE);
    }

    @Override
    protected TripContract.Presenter onCreatePresenter() {
        return new TripPresenter(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.getOrderDetail(orderId);
        MyApplication.setOrderIdRunning(orderId);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(WebsocketData websocketData) {
       // Log.d(TAG, "on event:" + websocketData.toString());
        if (websocketData.getCmdSn() == WsCommands.DRIVER_ORDER_PAID.getSn()) {
            Log.d(TAG, "支付完成");
            int orderId = websocketData.getData().getOrderId();
            if (order != null && orderId == order.getOrderId()) {
                presenter.getOrderDetail(orderId);
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventMsg data) {
        if (data.getCmd() == EventMsg.CMD_ORDER_GRAB_SUCCESS) {
            //抢单成功,页面销毁
            finish();
        }
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

    /**
     * 订单详情成功
     *
     * @param orderInfo
     */
    @Override
    public void getOrderDetailSuccess(Order orderInfo) {
        order = orderInfo;
        if (order.getOrderStatus() == Order.ORDER_STATUS_WAIT_PAY) {
            ivStatus.setImageResource(R.mipmap.ic_trip_wait_pay);
            if (order.getOrderType() == Order.ORDER_TYPE_SEND_CHILD) { //送你上学
                tvStatus.setText(R.string.trip_end_status_parent_not_paid);
            } else if (order.getOrderType() == Order.ORDER_TYPE_CARGO_PASSENGER) { //货拼客
                tvStatus.setText(R.string.trip_end_status_passenger_not_paid);
                tvConfirm.setVisibility(View.GONE);
                llCargoBtnContainer.setVisibility(View.VISIBLE);
                tvCargoPaid.setVisibility(View.VISIBLE);
            } else {
                tvStatus.setText(R.string.trip_end_status_passenger_not_paid);
                tvConfirm.setText(R.string.passengers_have_paid_offline);
                tvConfirm.setVisibility(View.VISIBLE);
            }
        } else {
            ivStatus.setImageResource(R.mipmap.ic_trip_paid);
            tvStatus.setText(R.string.trip_end_status_paid);
            tvConfirm.setText(R.string.complete_to_listen);
            tvConfirm.setVisibility(View.VISIBLE);
            if (order.getOrderType() == Order.ORDER_TYPE_CARGO_PASSENGER) {
                tvConfirm.setVisibility(View.GONE);
                llCargoBtnContainer.setVisibility(View.VISIBLE);
                tvCargoPaid.setVisibility(View.GONE);
            }
        }
        tvAmt.setText(BusinessUtils.moneySpec(order.getTotalMoney()));

        if(isFromPayMain){
            if (order.getOrderType() == Order.ORDER_TYPE_CARGO_PASSENGER) {
                presenter.getCargoOrderSendDeadTime(order.getCargoOrderId());
            }
        }else{
            //非支付页面进来的，不显示可操作的按钮
            tvConfirm.setVisibility(View.GONE);
            llCargoBtnContainer.setVisibility(View.GONE);
            mCargoRestTimeView.setVisibility(View.GONE);
        }
    }

    /**
     * 订单详情失败
     *
     * @param msg
     */
    @Override
    public void getOrderDetailFail(String msg) {
        finish();
    }

    /**
     * 确认乘客已支付成功
     */
    @Override
    public void payConfirmSuccess() {
        presenter.getOrderDetail(orderId);
    }

    @Override
    public void getServiceTelNumberSuccess(String phoneNum, boolean isNeedShowDialog) {

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
            Date estimateArriveTime = new SimpleDateFormat(DateUtils.FORMAT_PATTERN_YMDHM)
                    .parse(estimateArriveTimeStr);
            Date current = new Date(System.currentTimeMillis());
/*            long restTimeMillis = (estimateArriveTime.getTime() - current.getTime()) > 0
                    ? (estimateArriveTime.getTime() - current.getTime()) : 0;*/
            long restTimeMillis = estimateArriveTime.getTime() - current.getTime();
            mCargoRestTimeView.startCountDown(restTimeMillis);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void exceptionFinishOrderSuccess() {

    }

    @OnClick({R.id.iv_back, R.id.tv_trip_detail, R.id.tv_confirm, R.id.tv_cargo_paid, R.id.tv_send_cargo})
    public void onViewClicked(View view) {
        if (order == null) return;
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.tv_trip_detail:  //行程详情
                if(isFromPayMain){
                    TripDetailActivity.go(this, orderId, true, true);
                }else{
                    TripDetailActivity.go(this, orderId, false, false);
                }
                break;
            case R.id.tv_confirm:  //确认
                if (order.getOrderStatus() == Order.ORDER_STATUS_WAIT_PAY) {
                    presenter.payConfirm(orderId, PayInfo.OFFLINE_PAY_CONFIRM_PAIED);
                } else {
                    goMainActivity();
                }
                break;
            case R.id.tv_cargo_paid: //货单乘客已线下支付
                presenter.payConfirm(orderId, PayInfo.OFFLINE_PAY_CONFIRM_PAIED);
                break;
            case R.id.tv_send_cargo:  //去送货
                goOrderDetailActivityToSend();
                break;
            default:
                break;
        }
    }

    /**
     * 重写返回键逻辑
     */
    @Override
    public void onBackPressed() {
        if(isFromPayMain){
            //从支付页面过俩，需要判断订单类型
            //送小孩单，不跳转到主页，因为可能是多日订单进入的。
            if (order != null && order.getOrderType() == Order.ORDER_TYPE_SEND_CHILD) {
                finish();
            } else {
                goMainActivity();
            }
        }else{
            finish();
        }
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
     * 跳转到订单详情页-送货
     * 此时订单是货拼客单，所以传货物订单id
     */
    private void goOrderDetailActivityToSend() {
        OrderDetailActivity.go(this,order.getCargoOrderId());
        finish();
    }

}


