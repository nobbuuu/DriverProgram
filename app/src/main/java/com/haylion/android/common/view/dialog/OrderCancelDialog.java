package com.haylion.android.common.view.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.haylion.android.MyApplication;
import com.haylion.android.R;
import com.haylion.android.common.map.AMapUtil;
import com.haylion.android.data.base.BaseDialog;
import com.haylion.android.data.model.EventMsg;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.model.OrderTypeInfo;
import com.haylion.android.data.util.BusinessUtils;
import com.haylion.android.mvp.util.LogUtils;
import com.haylion.android.data.util.TTSUtil;
import com.haylion.android.main.MainActivity;
import com.haylion.android.mvp.base.AppManager;
import com.haylion.android.orderdetail.OrderDetailActivity;
import com.haylion.android.orderdetail.trip.TripDetailActivity;
import com.haylion.android.orderlist.AppointmentDetailsActivity;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;

/**
 * @author dengzh
 * @date 2019/12/12
 * Description:FloatService - 订单取消弹窗
 * <p>
 * 取消影响：
 * 1) 订单流程相关页面：OrderDetailActivity、AMapNaviViewActivity、CancelOrderActivity、TripOperateActivity。
 * 2) 数据影响页面：MainActivity、MultiDayDetailActivity
 * <p>
 * 规则：
 * a：点击“我知道了”
 * 1）如果是当前正在执行的订单，返回听单页。
 * 2）如果不是，不做任何处理。
 * b：点击“查看详情”
 * 进入行程详情页，点击返回，跳转到历史订单页，点击返回，进入听单页。
 * c：消失时，停止语音播报，且发通知给听单页、多日订单页更新数据。
 */
public class OrderCancelDialog extends BaseDialog {

    private String TAG = "OrderCancelDialog";

    private View view;

    private Context context;
    private TextView tvOrderType;
    private TextView tvTimeData;
    private TextView tvTimeHour;
    private TextView tvStartAddress;
    private TextView tvStartAddressDescribe;
    private TextView tvEndAddress;
    private TextView tvEndAddressDescribe;
    private TextView tvCancelReason;
    private TextView tvCancelReasonFixed;
    private TextView tvHeader;

    //货物描述
    private TextView tvCargoDetail;
    private TextView tvCargoDetailFixed;

    private TextView btnIKnow;
    private TextView tvSeeDetailAction;

    public OrderCancelDialog(@NonNull Context context) {
        super(context, R.style.Translucent_NoTitle);
        this.context = context;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.layout_cancel_order, null);
        setContentView(view);

        //update view
        tvOrderType = view.findViewById(R.id.tv_order_type);
        tvTimeData = view.findViewById(R.id.tv_time_date);
        tvTimeHour = view.findViewById(R.id.tv_time_hour);
        tvStartAddress = view.findViewById(R.id.tv_order_get_on_address);
        tvStartAddressDescribe = view.findViewById(R.id.tv_order_get_on_address_describe);
        tvEndAddress = view.findViewById(R.id.tv_order_get_off_address);
        tvEndAddressDescribe = view.findViewById(R.id.tv_order_get_off_address_describe);
        tvCancelReason = view.findViewById(R.id.tv_cancel_reason);
        tvCancelReasonFixed = view.findViewById(R.id.tv_cancel_reason_fixed);
        tvHeader = view.findViewById(R.id.tv_header);

        tvCargoDetailFixed = view.findViewById(R.id.tv_cargo_detail_fixed);
        tvCargoDetail = view.findViewById(R.id.tv_cargo_detail);

        btnIKnow = view.findViewById(R.id.btn_i_know);
        tvSeeDetailAction = view.findViewById(R.id.tv_see_detail_action);

        //不允许外部取消
        setCanceledOnTouchOutside(false);
        //屏蔽返回键
        setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                }
                return false;
            }
        });
    }

    public void setData(Order order) {

        //订单类型
        String orderTypeText = OrderTypeInfo.getStatusText(order.getOrderType(), order.getChannel());
        if (order.getOrderType() == Order.ORDER_TYPE_REALTIME_CARPOOL) {
            orderTypeText = orderTypeText + "(" + order.getPassengerNum() + "人)";
        }
        tvOrderType.setText(orderTypeText);

        tvStartAddress.setText(order.getStartAddr().getName());
        tvStartAddressDescribe.setText(AMapUtil.getAddress(order.getStartAddr().getAddressDetail()));
        tvEndAddress.setText(order.getEndAddr().getName());
        tvEndAddressDescribe.setText(AMapUtil.getAddress(order.getEndAddr().getAddressDetail()));

        if (order.getOrderCancellerMsg() != null && !("").equals(order.getOrderCancellerMsg())) {
            tvCancelReason.setText(order.getOrderCancellerMsg());
        } else {
            tvCancelReasonFixed.setVisibility(View.GONE);
            tvCancelReason.setVisibility(View.GONE);
        }

        //货单描述
        if (order.getGoodsDescription() != null && !("").equals(order.getGoodsDescription())) {
            tvCargoDetail.setText(order.getGoodsDescription());
        } else {
            tvCargoDetailFixed.setVisibility(View.GONE);
            tvCargoDetail.setVisibility(View.GONE);
        }

        //时间显示
        String orderTime = order.getOrderTime();
        String dateString = "2019-12-01";
        try {
            long milliSecond = BusinessUtils.stringToLong(orderTime, "yyyy-MM-dd HH:mm");
            dateString = BusinessUtils.getDateString(milliSecond, null);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        tvTimeData.setText(dateString);
        tvTimeHour.setText(orderTime.substring(11));

        if (order.getOrderType() == Order.ORDER_TYPE_SEND_CHILD) {
            if (order.getOrderCancellerType() == 4) {
                tvHeader.setText("系统自动取消订单");
                TTSUtil.playVoice(context.getString(R.string.dialog_voice_msg_system_cancel_order));
            } else {
                TTSUtil.playVoice(context.getString(R.string.dialog_voice_msg_cancel_order));
            }
        } else if (order.getOrderType() == Order.ORDER_TYPE_CARGO) {
            tvHeader.setText("货单已取消");
            TTSUtil.playVoice(context.getString(R.string.dialog_voice_msg_cancel_cargo_order));

        } else if (order.getOrderType() == Order.ORDER_TYPE_BOOK) {
            tvHeader.setText("订单已取消");
            TTSUtil.playVoice(context.getString(R.string.dialog_voice_msg_cancel_appointment));

        } else {
            if (order.getOrderCancellerType() == 4) { // web后台操作取消
                tvHeader.setText("系统自动取消订单");
                TTSUtil.playVoice(context.getString(R.string.dialog_voice_msg_system_cancel_order));
            } else {
                TTSUtil.playVoice(context.getString(R.string.dialog_voice_msg_cancel_order));
            }
        }

        LogUtils.d(TAG, "current order Id:" + MyApplication.getOrderIdRunning()
                + ", cancel Order Id:" + order.getOrderId());
        btnIKnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (isInRelationActivity()) {
                    if (MyApplication.getOrderIdRunning() == order.getOrderId()) {
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } else if (order.getOrderType() == Order.ORDER_TYPE_CARGO) {//取消的货单，当前页面是关联的客单，则需要刷新客单页面。
                        OrderDetailActivity.go(context, MyApplication.getOrderIdRunning());

                    } else {
                        LogUtils.d(TAG, "收到取消消息，不是当前执行订单，页面不变化");
                    }
                } else {
                    LogUtils.d(TAG, "收到取消消息，非受影响页面，页面不变化");
                }
            }
        });

        tvSeeDetailAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (order.getOrderType() == Order.ORDER_TYPE_BOOK) {
                    AppointmentDetailsActivity.start(getContext(), order.getOrderId());
                } else {
                    TripDetailActivity.go(context, order.getOrderId(), false, false, true);
                }
            }
        });

        setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                TTSUtil.stop();
                //通知主页刷新数据
                EventMsg eventMsg = new EventMsg();
                eventMsg.setType(0);
                eventMsg.setCmd(EventMsg.CMD_ORDER_CANCELED);
                eventMsg.setMessage("刷新订单信息");
                EventBus.getDefault().post(eventMsg);
            }
        });
      /*  if (Build.VERSION.SDK_INT >= 26) {
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        } else {
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }*/
       /* show();
        getWindow().setContentView(view);*/
    }

    /**
     * 当前是否在受影响的页面
     *
     * @return
     */
    private boolean isInRelationActivity() {
        String currentActivityName = AppManager.getAppManager().currentActivity().toString();
        LogUtils.d(TAG, "currentActivityName:" + currentActivityName);
        if (!TextUtils.isEmpty(currentActivityName) &&
                (currentActivityName.contains("OrderDetailActivity") ||
                        currentActivityName.contains("AppointmentDetailsActivity") ||
                        currentActivityName.contains("AMapNaviViewActivity") ||
                        currentActivityName.contains("TripOperateActivity") ||
                        currentActivityName.contains("CancelOrderActivity"))) {
            return true;
        } else {
            return false;
        }
    }
}
