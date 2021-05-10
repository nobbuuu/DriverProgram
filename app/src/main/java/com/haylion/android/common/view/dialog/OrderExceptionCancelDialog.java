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
import com.haylion.android.data.base.BaseDialog;
import com.haylion.android.data.model.EventMsg;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.util.BusinessUtils;
import com.haylion.android.data.util.TTSUtil;
import com.haylion.android.main.MainActivity;
import com.haylion.android.mvp.base.AppManager;
import com.haylion.android.mvp.util.LogUtils;
import com.haylion.android.orderdetail.trip.TripDetailActivity;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;

/**
 * 订单异常结束弹窗
 */
public class OrderExceptionCancelDialog extends BaseDialog {

    private String TAG = "OrderExceptionCancelDialog";

    private View view;

    private Context context;
    private TextView tvOrderType;
    private TextView tvTimeData;
    private TextView tvTimeHour;
    private TextView tvStartAddress;
    private TextView tvEndAddress;
    private TextView tvHeader;

    private TextView btnIKnow;
    private TextView tvSeeDetailAction;

    public OrderExceptionCancelDialog(@NonNull Context context) {
        super(context, R.style.Translucent_NoTitle);
        this.context = context;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.layout_cancel_order_abnormal, null);
        setContentView(view);

        //update view
        tvOrderType = view.findViewById(R.id.tv_order_type);
        tvTimeData = view.findViewById(R.id.tv_time_date);
        tvTimeHour = view.findViewById(R.id.tv_time_hour);
        tvStartAddress = view.findViewById(R.id.tv_order_get_on_address);
        tvEndAddress = view.findViewById(R.id.tv_order_get_off_address);
        tvHeader = view.findViewById(R.id.tv_header);
        btnIKnow = view.findViewById(R.id.btn_i_know);
        tvSeeDetailAction = view.findViewById(R.id.tv_see_detail_action);

        //不允许外部取消
        setCanceledOnTouchOutside(false);
        //屏蔽返回键
        setOnKeyListener(new OnKeyListener() {
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
        tvOrderType.setText("送你上学");

        tvStartAddress.setText(order.getStartAddr().getName());
        tvEndAddress.setText(order.getEndAddr().getName());
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
        tvHeader.setText("系统异常结束订单");
        TTSUtil.playVoice(context.getString(R.string.dialog_voice_msg_adnormal_cancel_order));

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
                TripDetailActivity.go(context, order.getOrderId(), false, false, true);
            }
        });

        setOnDismissListener(new OnDismissListener() {
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
                        currentActivityName.contains("AMapNaviViewActivity") ||
                        currentActivityName.contains("TripOperateActivity") ||
                        currentActivityName.contains("CancelOrderActivity"))) {
            return true;
        } else {
            return false;
        }
    }
}
