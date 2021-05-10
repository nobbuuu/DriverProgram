package com.haylion.android.common.view.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.haylion.android.MyApplication;
import com.haylion.android.R;
import com.haylion.android.common.map.AMapUtil;
import com.haylion.android.data.base.BaseDialog;
import com.haylion.android.data.model.AddressInfo;
import com.haylion.android.data.model.EventMsg;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.model.OrderTypeInfo;
import com.haylion.android.data.util.BusinessUtils;

import com.haylion.android.data.util.SpannableStringUtil;
import com.haylion.android.data.util.TTSUtil;
import com.haylion.android.mvp.base.AppManager;
import com.haylion.android.mvp.util.LogUtils;
import com.haylion.android.orderdetail.OrderDetailActivity;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;

/**
 * @author dengzh
 * @date 2019/12/12
 * Description:FloatService - 订单下车地址修改弹窗
 *
 * 修改地址影响：
 * 1) 订单流程相关页面：OrderDetailActivity、AMapNaviViewActivity、CancelOrderActivity、TripOperateActivity。
 * 2) 数据影响页面：MainActivity、MultiDayDetailActivity
 *
 * 规则：
 * a：点击“我知道了”
 *    不做任何处理。
 * b：点击“查看详情”
 *    如果不是当前正在执行订单，跳转到订单详情页。
 * c：消失时，停止语音播报，且发通知给相应页面更新数据。
 * */
public class OrderChangeAddressDialog extends BaseDialog {

    private String TAG = "OrderChangeAddressDialog";

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
    private ImageView ivOldGetOffAddrIcon;
    private TextView tvEndAddressNew;
    private TextView tvSeeDetailAction;

    public OrderChangeAddressDialog(@NonNull Context context) {
        super(context, R.style.Translucent_NoTitle);
        this.context = context;
        initView();
    }

    private void initView(){
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.layout_change_address_order, null);
        setContentView(view);

        //update view
         tvTimeData = view.findViewById(R.id.tv_time_date);
         tvTimeHour = view.findViewById(R.id.tv_time_hour);
         tvOrderType = view.findViewById(R.id.tv_order_type);
         tvStartAddress = view.findViewById(R.id.tv_order_get_on_address);
         tvStartAddressDescribe = view.findViewById(R.id.tv_order_get_on_address_describe);
         tvEndAddress = view.findViewById(R.id.tv_order_get_off_address);
         tvEndAddressDescribe = view.findViewById(R.id.tv_order_get_off_address_describe);
         ivOldGetOffAddrIcon = view.findViewById(R.id.ic_order_get_off_address);
        //新地址
         tvEndAddressNew = view.findViewById(R.id.tv_order_get_off_address_new);
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

    public void setData(Order order, AddressInfo oldAddress){
        //订单类型
        String orderTypeText = OrderTypeInfo.getStatusText(order.getOrderType(), order.getChannel());
        if (order.getOrderType() == Order.ORDER_TYPE_REALTIME_CARPOOL) {
            orderTypeText = orderTypeText + "(" + order.getPassengerNum() + "人)";
        }
        tvOrderType.setText(orderTypeText);
        //时间显示
        String orderTime = order.getOrderTime();
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

        tvStartAddress.setText(order.getStartAddr().getName());
        tvStartAddressDescribe.setText(AMapUtil.getAddress(order.getStartAddr().getAddressDetail()));

        //新地址
        SpannableString spannableString = SpannableStringUtil.setSpan("(新)" + order.getEndAddr().getName(), R.color.maas_text_orange, 0, 3);
        tvEndAddressNew.setText(spannableString);

        //旧地址
        String oldAddressStr = "(旧)" + oldAddress.getName();
        spannableString = SpannableStringUtil.setSpan(oldAddressStr, R.color.maas_text_gray, 0, oldAddressStr.length());
        tvEndAddress.setText(spannableString);
        ivOldGetOffAddrIcon.setImageResource(R.mipmap.ic_dot_grey);
        tvEndAddressDescribe.setText(AMapUtil.getAddress(oldAddress.getAddressDetail()));

        TTSUtil.playVoice(context.getString(R.string.dialog_voice_msg_change_addr));

        //我知道了
        btnIKnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        //查看详情
        tvSeeDetailAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                //不是当前执行的订单，进入详情页
                if (MyApplication.getOrderIdRunning() != order.getOrderId()) {
                    OrderDetailActivity.go(context,order.getOrderId());
                }
            }
        });

        setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                TTSUtil.stop();
                //通知主页及相关页面刷新数据
                EventMsg eventMsg = new EventMsg();
                eventMsg.setType(0);
                eventMsg.setCmd(EventMsg.CMD_ORDER_ADDRESS_HAS_CHANGED);
                eventMsg.setOrderId(order.getOrderId());
                eventMsg.setMessage("修改下车地址");
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
                (currentActivityName.contains("OrderDetailActivity")||
                        currentActivityName.contains("AMapNaviViewActivity")||
                        currentActivityName.contains("TripOperateActivity")||
                        currentActivityName.contains("CancelOrderActivity"))) {
            return true;
        }else{
            return false;
        }
    }
}
