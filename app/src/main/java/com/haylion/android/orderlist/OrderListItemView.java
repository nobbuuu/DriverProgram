package com.haylion.android.orderlist;

import android.content.Context;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.maps.AMap;
import com.haylion.android.R;
import com.haylion.android.common.map.AMapUtil;
import com.haylion.android.data.base.BaseItemView;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.model.OrderStatus;
import com.haylion.android.data.model.OrderTypeInfo;
import com.haylion.android.data.util.BusinessUtils;
import com.haylion.android.data.util.ResourceUtil;

import java.text.ParseException;

import butterknife.BindView;

public class OrderListItemView extends BaseItemView<Order> {
    private static final String TAG = "OrderListItemView";

    @BindView(R.id.order_status)
    TextView orderStatus;
    @BindView(R.id.order_type)
    TextView orderType;
    @BindView(R.id.order_type_fixed)
    TextView orderTypeFixed;

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

    @BindView(R.id.order_status_header)
    TextView tvOrderStatusHeader;

    @BindView(R.id.tv_order_number)
    TextView tvOrderNumber;

    public OrderListItemView(Context context) {
        super(context);
    }

    @Override
    protected int getOrientation() {
        return RecyclerView.VERTICAL;
    }

    @Override
    public int getLayoutId() {
        return R.layout.order_list_item;
    }

    @Override
    public void bind(Order order) {
//        if (order.getOrderStatus() != Order.ORDER_STATUS_CANCELED) {
//        orderStatus.setOnClickListener(v -> notifyItemAction(OrderClickArea.SHOW_IN_MAP));
        rlOrderContent.setOnClickListener(v -> notifyItemAction(OrderClickArea.SHOW_IN_MAP));
//        }

        //是否展示listview的头部信息
        if (order.getHeaderNameDisplay() != null && !("").equals(order.getHeaderNameDisplay())) {
            tvOrderStatusHeader.setText(order.getHeaderNameDisplay());
            tvOrderStatusHeader.setVisibility(VISIBLE);
        } else {
            tvOrderStatusHeader.setVisibility(GONE);
        }

        //订单编号
        tvOrderNumber.setText(order.getOrderCode());

        //地址信息
        tvOrderGetOn.setText(order.getStartAddr().getName());
        tvOrderGetOff.setText(order.getEndAddr().getName());
        tvGetOnDesc.setText(AMapUtil.getAddress(order.getStartAddr().getAddressDetail()));
        tvGetOffDesc.setText(AMapUtil.getAddress(order.getEndAddr().getAddressDetail()));

        //订单时间信息
        //时间展示日期和小时信息
        String time = order.getOrderTime();
        String timeFormat;
        try {
            long milliSecond = BusinessUtils.stringToLong(time, "yyyy-MM-dd HH:mm");
            timeFormat = BusinessUtils.getDateToStringIncludeYear(milliSecond, null);
            Log.d(TAG, "" + "order time, timeFormat: " + timeFormat);
            orderTime.setText(timeFormat);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //订单状态
        orderStatus.setText(OrderStatus.getOrderStatusText(order));

        //订单类型展示
        String orderTypeText;
        orderTypeText = OrderTypeInfo.getStatusText(order.getOrderType(), order.getChannel());
        orderType.setText(orderTypeText);
        orderType.setVisibility(VISIBLE);
    }

}
