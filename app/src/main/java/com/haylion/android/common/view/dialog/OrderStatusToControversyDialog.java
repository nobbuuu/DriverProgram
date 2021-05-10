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

import com.haylion.android.data.util.TTSUtil;
import com.haylion.android.mvp.base.AppManager;
import com.haylion.android.mvp.util.LogUtils;
import com.haylion.android.orderdetail.trip.TripDetailActivity;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;

/**
 * @author dengzh
 * @date 2019/12/12
 * Description:FloatService - 订单争议弹窗
 *
 * 订单争议状态影响：（订单已完成相关页面）
 * TripEndActivity、TripDetailActivity、MultiDayDetailActivity、OrderListActivity
 *
 * 跳转规则
 * a：点击“我知道了”
 *    1）如果处于行程结束页，且是当前订单，跳到行程详情页。其他情况不做处理。
 * b：点击“查看详情”
 *    1）如果是行程详情页，且是当前订单，则不做处理，否则跳到行程详情页。
 * c：消失时，停止语音播报，且发通知给相关页面更新数据。
 */
public class OrderStatusToControversyDialog extends BaseDialog {

    private String TAG = "OrderStatusToControversyDialog";

    private View view;

    private Context context;
    private TextView tvOrderType;
    private TextView tvTimeData;
    private TextView tvTimeHour;
    private TextView tvStartAddress;
    private TextView tvStartAddressDescribe;
    private TextView tvEndAddress;
    private TextView tvEndAddressDescribe;
    private TextView btnIKnow;
    private TextView tvSeeDetailAction;

    /**
     * @param context
     */
    public OrderStatusToControversyDialog(@NonNull Context context) {
        super(context, R.style.Translucent_NoTitle);
        this.context = context;
        initView();
    }

    private void initView(){

        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.layout_controversy_order, null);
        setContentView(view);

        tvTimeData = view.findViewById(R.id.tv_time_date);
        tvTimeHour = view.findViewById(R.id.tv_time_hour);
        tvOrderType = view.findViewById(R.id.tv_order_type);
        tvStartAddress = view.findViewById(R.id.tv_order_get_on_address);
        tvStartAddressDescribe = view.findViewById(R.id.tv_order_get_on_address_describe);
        tvEndAddress = view.findViewById(R.id.tv_order_get_off_address);
        tvEndAddressDescribe = view.findViewById(R.id.tv_order_get_off_address_describe);
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

    public void setData(Order order){
        //订单类型
        String orderTypeText = OrderTypeInfo.getStatusText(order.getOrderType(), order.getChannel());
        if (order.getOrderType() == Order.ORDER_TYPE_REALTIME_CARPOOL) {
            orderTypeText = orderTypeText + "(" + order.getPassengerNum() + "人)";
        }
        tvOrderType.setText(orderTypeText);

        //地址信息
        tvStartAddress.setText(order.getStartAddr().getName());
        tvStartAddressDescribe.setText(AMapUtil.getAddress(order.getStartAddr().getAddressDetail()));
        tvEndAddress.setText(order.getEndAddr().getName());
        tvEndAddressDescribe.setText(AMapUtil.getAddress(order.getEndAddr().getAddressDetail()));

        //时间显示
        String orderTime = order.getOrderTime();
        LogUtils.d(TAG, "order.getOrderTime():" + order.getOrderTime() + ", substring(11):" + order.getOrderTime().substring(11));
        String dateString = "2019-12-01";
        try {
            long milliSecond = BusinessUtils.stringToLong(orderTime, "yyyy-MM-dd HH:mm");
            dateString = BusinessUtils.getDateString(milliSecond, null);
            LogUtils.d(TAG, "" + "dateString: " + dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        tvTimeData.setText(dateString);
        tvTimeHour.setText(orderTime.substring(11));

        TTSUtil.playVoice(context.getString(R.string.dialog_voice_msg_controversy_order));

        //我知道了
        btnIKnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                //如果处于行程结束页，且是当前订单，跳到行程详情页
                String currentActivityName = AppManager.getAppManager().currentActivity().toString();
                if (!TextUtils.isEmpty(currentActivityName) &&
                        currentActivityName.contains("TripEndActivity")&&
                        MyApplication.getOrderIdRunning() == order.getOrderId()){
                    TripDetailActivity.go(context,order.getOrderId(),false,false,true);
                }
            }
        });
        //查看详情
        tvSeeDetailAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                //如果是行程详情页，且是当前订单，则不做处理，否则跳到行程详情页。
                if(isInRelationActivity() && MyApplication.getOrderIdRunning() == order.getOrderId()){

                }else{
                    TripDetailActivity.go(context,order.getOrderId(),false,false,true);
                }
            }
        });


        setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                TTSUtil.stop();

                EventMsg eventMsg = new EventMsg();
                eventMsg.setType(0);
                eventMsg.setCmd(EventMsg.CMD_ORDER_STATUS_TO_CONTROVERSY);
                eventMsg.setMessage("订单状态更新成争议状态");
                eventMsg.setOrderId(order.getOrderId());
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
     * @return
     */
    private boolean isInRelationActivity(){
        String currentActivityName = AppManager.getAppManager().currentActivity().toString();
        if (!TextUtils.isEmpty(currentActivityName) &&
                        currentActivityName.contains("TripDetailActivity")){
            return true;
        }else{
            return false;
        }
    }
}
