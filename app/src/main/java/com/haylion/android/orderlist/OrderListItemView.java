package com.haylion.android.orderlist;

import android.content.Context;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.haylion.android.R;
import com.haylion.android.calendar.DateFormatUtil;
import com.haylion.android.calendar.DateStyle;
import com.haylion.android.data.base.BaseItemView;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.model.OrderStatus;
import com.haylion.android.data.model.OrderTypeInfo;

import java.util.Date;

import butterknife.BindView;

import static com.haylion.android.data.model.Order.ORDER_TYPE_SHUNFENG;

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
        if (order != null) {

//        if (order.getOrderStatus() != Order.ORDER_STATUS_CANCELED) {
//        orderStatus.setOnClickListener(v -> notifyItemAction(OrderClickArea.SHOW_IN_MAP));
            rlOrderContent.setOnClickListener(v -> notifyItemAction(OrderClickArea.SHOW_IN_MAP));
//        }

            //是否展示listview的头部信息
            if (order.getOrderType() != ORDER_TYPE_SHUNFENG && order.getHeaderNameDisplay() != null && !("").equals(order.getHeaderNameDisplay())) {
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
//        tvGetOnDesc.setText(AMapUtil.getAddress(order.getStartAddr().getAddressDetail()));
//        tvGetOffDesc.setText(AMapUtil.getAddress(order.getEndAddr().getAddressDetail()));

            //订单时间信息
            //时间展示日期和小时信息
            String deliveryTime = order.getDeliveryTime();
            String actualDeliveryTime = order.getActualDeliveryTime();
            String actualDeliveryTimeSecond = order.getActualDeliveryTimeSecond();
            String yyEndTime = order.getEndTime();
            String curTime = DateFormatUtil.getTime(new Date(), DateStyle.YYYY_MM_DD.getValue());
            if (order.getOrderStatus() == 5) {
                String endTime = DateFormatUtil.getTime(Long.valueOf(actualDeliveryTimeSecond), DateStyle.YYYY_MM_DD.getValue());
                String realTime = DateFormatUtil.getTime(Long.valueOf(actualDeliveryTimeSecond), DateStyle.MM_DD.getValue());
                if (endTime != null && curTime.equals(endTime)) {
                    orderTime.setText("今日" + actualDeliveryTime + "送达");
                } else {
                    orderTime.setText(realTime + " " + actualDeliveryTime + "送达");
                }
            } else if (yyEndTime != null) {
                if (curTime.equals(yyEndTime)) {
                    orderTime.setText("预约今日" + deliveryTime + "送达");
                } else {
                    String[] split = yyEndTime.split("-");
                    if (split.length >= 3) {
                        orderTime.setText("预约" + split[1] + "-" + split[2] + " " + deliveryTime + "送达");
                    } else {
                        orderTime.setText("预约" + deliveryTime + "送达");
                    }
                }
            }

            //订单状态
            orderStatus.setText(OrderStatus.getOrderStatusText(order));
            if (order.getOrderType() == ORDER_TYPE_SHUNFENG) {
                switch (order.getOrderStatus()) {
//                        待开始 = 0、待到店 = 1、待扫描=2、待取货签名=3、送货中=4、已完成=5
                    case 0:
                        orderStatus.setText("待开始");
                        break;
                    case 1:
                    case 2:
                    case 3:
                        orderStatus.setText("待取货");
                        break;
                    case 4:
                        orderStatus.setText("送货中");
                        break;
                    case 5:
                        orderStatus.setText("已完成");
                        break;
                }
            }
            //订单类型展示
            String orderTypeText;
            int orderType = order.getOrderType();
            Log.d("aaa", "orderType = " + orderType);
            orderTypeText = OrderTypeInfo.getStatusText(order.getOrderType(), order.getChannel());
            this.orderType.setText(orderTypeText);
            this.orderType.setVisibility(VISIBLE);
        }
    }

}
