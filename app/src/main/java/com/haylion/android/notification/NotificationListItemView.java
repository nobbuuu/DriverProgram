package com.haylion.android.notification;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.haylion.android.R;
import com.haylion.android.data.base.BaseItemView;
import com.haylion.android.data.model.MessageDetail;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.util.BusinessUtils;
import com.haylion.android.orderlist.OrderClickArea;
import com.haylion.android.service.WsCommands;

import java.text.ParseException;

import butterknife.BindView;

public class NotificationListItemView extends BaseItemView<MessageDetail> {
    private static final String TAG = "OrderListItemView";

    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.iv_readed)
    ImageView ivRead;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.rl_item)
    RelativeLayout rlItem;

    @BindView(R.id.tv_type)
    TextView tvType;

    public NotificationListItemView(Context context) {
        super(context);
    }

    @Override
    protected int getOrientation() {
        return RecyclerView.VERTICAL;
    }

    @Override
    public int getLayoutId() {
        return R.layout.notification_list_item;
    }


    @Override
    public void bind(MessageDetail data) {
        rlItem.setOnClickListener(v -> notifyItemAction(OrderClickArea.PUSH_MESSAGE));
        rlItem.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                NotificationListItemView.this.notifyItemAction(OrderClickArea.PUSH_MESSAGE_LONG_CLICK);
                return true;
            }
        });

        //时间信息
        String timeString = "";
        try {
            if (data.getPushTime() != null) {
                long milliSecond = BusinessUtils.stringToLong(data.getPushTime(), "yyyy-MM-dd HH:mm");
                timeString = BusinessUtils.getDateToStringOnlyHourWhenToday(milliSecond);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        tvDate.setText(timeString);

        //是否已读
        if (data.getViewed() == 1) {
            ivRead.setVisibility(INVISIBLE);
        } else {
            ivRead.setVisibility(VISIBLE);
        }

        //
        //点击通知栏，跳转到对应的页面
        String title = "";
        String content = "";
        if (data.getCmdSn() == WsCommands.DRIVER_ORDER_PAID.getSn()) {
            title = "支付消息";
            Log.e("NotificationList","orderType:" + data.getOrderType());
            if(data.getOrderType() == Order.ORDER_TYPE_SEND_CHILD){
                content = "家长已完成支付，点击查看订单详情";
            }else{
                content = "乘客已完成支付， 点击查看订单详情";
            }
        } else if (data.getCmdSn() == WsCommands.DRIVER_ORDER_CANCEL_BY_USER.getSn()
                || data.getCmdSn() == WsCommands.DRIVER_ORDER_CANCEL_BY_SYSTEM.getSn()) {
            title = "订单已取消";
            content = "订单已取消， 点击查看订单详情";
        } else if (data.getCmdSn() == WsCommands.DRIVER_ORDER_DESTINATION_CHANGED.getSn()) {
            content = "乘客已修改下车点， 点击查看新的下车点";
            title = "下车点变更";
        } else if (data.getCmdSn() == WsCommands.DRIVER_UNPAID_ORDER_TO_CONTROVERSY_ORDER.getSn()) {
            content = "订单已更新成支付争议状态， 点击查看订单详情";
            title = "订单状态更新";
        }
        tvType.setText(title);
        tvContent.setText(content);
    }
}
