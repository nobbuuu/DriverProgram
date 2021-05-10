package com.haylion.android.orderlist;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.haylion.android.R;
import com.haylion.android.common.map.AMapUtil;
import com.haylion.android.data.base.BaseItemView;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.util.BusinessUtils;

import java.text.ParseException;

import butterknife.BindView;

public class OrderGoodsListItemView extends BaseItemView<Order> {
    private static final String TAG = "OrderGoodsListItemView";

    @BindView(R.id.order_status)
    TextView orderStatus;
    @BindView(R.id.order_type)
    TextView orderType;

    @BindView(R.id.rl_order_content)
    RelativeLayout rlOrderContent;

    @BindView(R.id.tv_get_on_addr)
    TextView tvOrderGetOn;
    @BindView(R.id.tv_get_on_desc)
    TextView tvGetOnDesc;
    @BindView(R.id.tv_get_off_addr)
    TextView tvOrderGetOff;
    @BindView(R.id.tv_get_off_desc)
    TextView tvGetOffDesc;

    //预约订单的，为预约时间； 实时订单的，为下单时间
    @BindView(R.id.tv_order_time)
    TextView orderTime;
    @BindView(R.id.tv_total_money)
    TextView totalMoney;
    @BindView(R.id.order_cancel_reason)
    TextView tvCancelReason;
    @BindView(R.id.tv_service_total_time_fixed)
    TextView tvServiceTimeFixed;
    @BindView(R.id.tv_service_total_time)
    TextView tvServiceTime;
    @BindView(R.id.order_status_header)
    TextView tvOrderStatusHeader;
    @BindView(R.id.iv_order_type_passenger_or_goods)
    ImageView ivOrderTypePassengerOrGoods;
    @BindView(R.id.order_goods_detail)
    TextView tvGoodsDetail;

    @BindView(R.id.ll_cargo_contact)
    LinearLayout llCargoContact;

    @BindView(R.id.tv_send_phone)
    TextView tvSendPhone;
    @BindView(R.id.tv_receive_phone)
    TextView tvReceivePhone;

    @BindView(R.id.tv_order_number)
    TextView tvOrderNumber;

    public OrderGoodsListItemView(Context context) {
        super(context);
    }

    @Override
    protected int getOrientation() {
        return RecyclerView.VERTICAL;
    }

    @Override
    public int getLayoutId() {
        return R.layout.order_goods_list_item;
    }

    @Override
    public void bind(Order order) {
/*        if (order.isDialable()) {
            contactPassenger.setVisibility(VISIBLE);
        } else {
            contactPassenger.setVisibility(GONE);
        }*/
        tvSendPhone.setOnClickListener(v -> notifyItemAction(OrderClickArea.CONTACT_SEND_PASSENGER));
        tvReceivePhone.setOnClickListener(v -> notifyItemAction(OrderClickArea.CONTACT_RECEIVE_PASSENGER));

        //  rlOrderContent.setOnClickListener(v -> notifyItemAction(OrderClickArea.SHOW_IN_MAP));

/*        if (order.getOrderStatus() != Order.ORDER_STATUS_CANCELED) {
        orderStatus.setOnClickListener(v -> notifyItemAction(OrderClickArea.SHOW_IN_MAP));
        rlOrderContent.setOnClickListener(v -> notifyItemAction(OrderClickArea.SHOW_IN_MAP));
        }*/
        rlOrderContent.setOnClickListener(v -> notifyItemAction(OrderClickArea.SHOW_IN_MAP));

        //header
        if (order.getHeaderNameDisplay() != null && !("").equals(order.getHeaderNameDisplay())) {
            tvOrderStatusHeader.setText(order.getHeaderNameDisplay());
            tvOrderStatusHeader.setVisibility(VISIBLE);
        } else {
            tvOrderStatusHeader.setVisibility(GONE);
        }

        //订单编号
        tvOrderNumber.setText(order.getOrderCode());

        tvOrderGetOn.setText(order.getStartAddr().getName());
        Log.d(TAG, "" + order.getEndAddr().getName());
        tvOrderGetOff.setText(order.getEndAddr().getName());
        tvGetOnDesc.setText(AMapUtil.getAddress(order.getStartAddr().getAddressDetail()));
        tvGetOffDesc.setText(AMapUtil.getAddress(order.getEndAddr().getAddressDetail()));


        String time = order.getOrderTime();
        String timeFormat = "2019-12-01 00:00";
        try {
            long milliSecond = BusinessUtils.stringToLong(time, "yyyy-MM-dd HH:mm");
            timeFormat = BusinessUtils.getDateToStringIncludeYear(milliSecond, null);
            Log.d(TAG, "" + "timeFormat: " + timeFormat);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        orderTime.setText(timeFormat);

        if (order.getOrderType() == Order.ORDER_TYPE_REALTIME_CARPOOL) {
            orderType.setText("实时拼车  " + "(" + order.getPassengerNum() + "人)");
        } else if (order.getOrderType() == Order.ORDER_TYPE_REALTIME) {
            orderType.setText("实时订单");
        } else if (order.getOrderType() == Order.ORDER_TYPE_BOOK) {
            orderType.setText("预约订单");
        }

        //
        tvGoodsDetail.setText(order.getGoodsDescription());

        //取消原因
        if (order.getOrderCancellerMsg() != null) {
            String reason;
            if (order.getOrderCancellerType() == 1) {
                reason = "乘客取消";
            } else if (order.getOrderCancellerType() == 2) {
                reason = "司机取消";
            } else {
                reason = "平台公司";
            }
            tvCancelReason.setText(reason + "  (" + order.getOrderCancellerMsg() + ")");
            tvCancelReason.setVisibility(VISIBLE);
        } else {
            tvCancelReason.setVisibility(GONE);
        }

        //订单状态
        if (order.getOrderStatus() == Order.ORDER_STATUS_CANCELED) {
            orderStatus.setText("已取消");
        } else {
            orderStatus.setText("已完成");
        }
        orderStatus.setCompoundDrawables(null, null, null, null);
/*        if (order.getOrderStatus() == Order.ORDER_STATUS_CANCELED) {
            orderStatus.setCompoundDrawables(null, null, null, null);
        } else {
            Drawable drawable;
            drawable = getResources().getDrawable(R.mipmap.arrow_huise);
            // 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            orderStatus.setCompoundDrawables(null, null, drawable, null);
        }*/

        if (order.getTotalMoney() != 0) {
            totalMoney.setText("￥" + BusinessUtils.formatDouble(order.getTotalMoney()));
        }

        //总耗时
        if (order.getOrderStatus() != Order.ORDER_STATUS_CANCELED) {
            tvServiceTime.setText(BusinessUtils.formatEstimateTimeText(order.getTotalTime()));
            tvServiceTimeFixed.setVisibility(VISIBLE);
            tvServiceTime.setVisibility(VISIBLE);
        } else{
            tvServiceTimeFixed.setVisibility(GONE);
            tvServiceTime.setVisibility(GONE);
        }


        //是否显示拨打电话的按钮,2个礼拜前的不再显示拨打电话的按钮。
        time = order.getOrderTime();
        try {
            long orderTimeMilliSecond = BusinessUtils.stringToLong(time, "yyyy-MM-dd HH:mm");
            Log.d(TAG, "" + "orderTimeMilliSecond: " + orderTimeMilliSecond);
            long currentTime = System.currentTimeMillis();
            if (currentTime - orderTimeMilliSecond > 14 * 24 * 3600 * 1000) {
                llCargoContact.setVisibility(View.GONE);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //已经取消的不显示拨打电话的按钮
        if (order.getOrderStatus() == Order.ORDER_STATUS_CANCELED) {
            llCargoContact.setVisibility(View.GONE);
        }
    }

}
