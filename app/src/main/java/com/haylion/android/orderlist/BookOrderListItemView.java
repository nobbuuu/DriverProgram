package com.haylion.android.orderlist;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.ListPopupWindow;
import androidx.recyclerview.widget.RecyclerView;

import com.haylion.android.R;
import com.haylion.android.data.base.BaseItemView;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.model.OrderStatus;
import com.haylion.android.data.util.BusinessUtils;
import com.haylion.android.mvp.util.LogUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;

public class BookOrderListItemView extends BaseItemView<Order> {
    private static final String TAG = "OrderListItemView";

    @BindView(R.id.order_status)
    TextView orderStatus;
    @BindView(R.id.order_type)
    TextView orderType;
    @BindView(R.id.order_type_fixed)
    TextView orderTypeFixed;

    @BindView(R.id.carpooling_order)
    View carpoolingOrder;

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
    @BindView(R.id.order_money)
    TextView order_money;

    //预约订单的，为预约时间； 实时订单的，为下单时间
    @BindView(R.id.tv_order_time)
    TextView orderTime;

    @BindView(R.id.order_status_header)
    TextView tvOrderStatusHeader;

    @BindView(R.id.tv_order_number)
    TextView tvOrderNumber;

    @BindView(R.id.tv_contact_passenger)
    TextView tvContactPassenger;

    @BindView(R.id.tv_total_distance)
    TextView tvTotalDistance;

    @BindView(R.id.show_map)
    View tvShowMap;
    @BindView(R.id.grab_order)
    TextView grabOrder;

    public BookOrderListItemView(Context context) {
        super(context);
    }

    @Override
    protected int getOrientation() {
        return RecyclerView.VERTICAL;
    }

    @Override
    public int getLayoutId() {
        return R.layout.book_order_list_item;
    }

    @Override
    public void bind(Order order) {
//        if (order.getOrderStatus() != Order.ORDER_STATUS_CANCELED) {
//        orderStatus.setOnClickListener(v -> notifyItemAction(OrderClickArea.SHOW_IN_MAP));
//        rlOrderContent.setOnClickListener(v -> notifyItemAction(OrderClickArea.SHOW_IN_MAP));
        tvContactPassenger.setOnClickListener(v -> notifyItemAction(OrderClickArea.CONTACT_PASSENGER));
        if (order.getOrderType() == Order.ORDER_TYPE_SEND_CHILD) {
//            tvContactPassenger.setText(R.string.order_contact_parent);

            if (order.isCarpoolOrder()) { // 送你上学 - 拼车单
                carpoolingOrder.setVisibility(VISIBLE);
            } else {
                carpoolingOrder.setVisibility(GONE);
            }
        } else {
//            tvContactPassenger.setText(R.string.order_contact_passenger);
            carpoolingOrder.setVisibility(GONE);
        }
//        }
        setOnClickListener(view -> notifyItemAction(OrderClickArea.ORDER_DETAILS));

        tvShowMap.setOnClickListener(v -> notifyItemAction(OrderClickArea.SHOW_IN_MAP));

/*        //是否展示listview的头部信息
        if (order.getHeaderNameDisplay() != null && !("").equals(order.getHeaderNameDisplay())) {
            tvOrderStatusHeader.setText(order.getHeaderNameDisplay());
            tvOrderStatusHeader.setVisibility(VISIBLE);
        } else {
            tvOrderStatusHeader.setVisibility(GONE);
        }*/
        tvOrderStatusHeader.setVisibility(GONE);


        //订单编号
        tvOrderNumber.setText(order.getOrderCode());

        //地址信息
        tvOrderGetOn.setText(order.getStartAddr().getName());
        tvOrderGetOff.setText(order.getEndAddr().getName());
        tvGetOnDesc.setText(order.getStartAddr().getAddressDetail());
        tvGetOffDesc.setText(order.getEndAddr().getAddressDetail());

        //订单时间信息
        //时间展示日期和小时信息
        String timeFormat = order.getEstimateArriveTime();
        if (!TextUtils.isEmpty(timeFormat)) {
            if (order.isParentOrder()) {
                timeFormat = formatParentOrderDate(order.getStartTime(), order.getEndTime());
            } else {
                try {
                    long milliSecond = BusinessUtils.stringToLong(order.getOrderTime(), "yyyy-MM-dd HH:mm");
                    timeFormat = BusinessUtils.getDateToStringIncludeYearWhenCrossYear(milliSecond, "");
                    Log.d(TAG, "" + "order time, timeFormat: " + timeFormat);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            orderTime.setText("预约" + timeFormat + "送达");
        }

        List<String> orderDates = order.getOrderDates();
        if (orderDates != null && orderDates.size() > 0) {
            String makeTime = orderDates.get(0);
            if (makeTime.contains(" ")) {
                String[] maketimeStr = makeTime.split(" ");
                if (maketimeStr.length > 1) {
                    orderStatus.setText("每日" + maketimeStr[1] + "取货");
                }
            }
        } else {
            orderStatus.setText("今日" + order.getOrderTime() + "取货");
        }
        orderStatus.setVisibility(VISIBLE);
        orderStatus.setClickable(false);
        grabOrder.setVisibility(VISIBLE);

        //订单状态
       /* if (order.getOrderStatus() == OrderStatus.ORDER_STATUS_INIT.getStatus()) {
            if (order.isParentOrder()) { // 预约大厅订单，状态区域显示子订单数量
                orderStatus.setVisibility(VISIBLE);
                orderStatus.setText(String.format(Locale.getDefault(), "共%d单", orderDates.size()));

                orderStatus.setClickable(true);
                orderStatus.setOnClickListener(v -> {
                    notifyItemAction(ACTION_ORDER_DATES);
                    showOrderDates(orderDates);
                });
            }
            grabOrder.setVisibility(VISIBLE);
            grabOrder.setOnClickListener(v -> notifyItemAction(ACTION_GRAB_ORDER));
        } else {
            if (order.getOrderStatus() == Order.ORDER_STATUS_CLOSED &&
                    order.getOrderSubStatus() == Order.ORDER_SUB_STATUS_CLOSED_COMPLAIN) {
                orderStatus.setText(OrderStatus.getStatusText(order.getOrderStatus(), order.getOrderType()) + "(支付争议)");
            } else {
                orderStatus.setText(OrderStatus.getStatusText(order.getOrderStatus(), order.getOrderType()));
            }
        }*/

        //只有取消的订单才显示状态
//        if (order.getOrderStatus() == Order.ORDER_STATUS_CANCELED) {
//            orderStatus.setVisibility(VISIBLE);
//        } else {
//            orderStatus.setVisibility(GONE);
//        }

        // 只有预约单大厅才显示“查看地图”
        if (order.getOrderStatus() == OrderStatus.ORDER_STATUS_INIT.getStatus()) {
            tvShowMap.setVisibility(VISIBLE);
        } else {
            tvShowMap.setVisibility(GONE);
        }

/*        //订单类型展示
        String orderTypeText;
        orderTypeText = OrderTypeInfo.getStatusText(order.getOrderType(), order.getChannel());
        orderType.setText(orderTypeText);
        orderType.setVisibility(VISIBLE);*/

        //订单类型
        tvTotalDistance.setVisibility(VISIBLE);
        tvTotalDistance.setText("取送距离" + BusinessUtils.formatDistance(order.getDistance()));
        order_money.setText(order.getTotalMoney() + "");
    }

    private void showOrderDates(List<String> orderDates) {
        PopupMenu popupMenu = new PopupMenu(getContext(), orderStatus);
        Menu menu = popupMenu.getMenu();
        for (String orderDate : orderDates) {
            menu.add(orderDate);
        }
        popupMenu.show();
    }

    private String formatParentOrderDate(String startTimeText, String endTimeText) {
        if (TextUtils.isEmpty(startTimeText) ||
                TextUtils.isEmpty(endTimeText)) {
            return "";
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm", Locale.getDefault());
        Date startTime = new Date();
        Date endTime = new Date();
        try {
            startTime = simpleDateFormat.parse(startTimeText);
            endTime = simpleDateFormat.parse(endTimeText);
        } catch (ParseException e) {
            LogUtils.e("订单日期格式有误：" + startTimeText + " - " + endTimeText);
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        int thisYear = calendar.get(Calendar.YEAR);
        calendar.setTime(startTime);
        int startYear = calendar.get(Calendar.YEAR);
        calendar.setTime(endTime);
        int endYear = calendar.get(Calendar.YEAR);

        if (startYear != thisYear || endYear != thisYear) { // 订单跨年了
            simpleDateFormat = new SimpleDateFormat(
                    "yyyy年MM月dd日", Locale.getDefault());
            String formattedTime = simpleDateFormat.format(startTime) + " - " + simpleDateFormat.format(endTime);

            simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            return formattedTime + " " + simpleDateFormat.format(startTime);
        }
        simpleDateFormat = new SimpleDateFormat(
                "MM月dd日", Locale.getDefault());
        String formattedTime = simpleDateFormat.format(startTime) + " - " + simpleDateFormat.format(endTime);

        simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return formattedTime + " " + simpleDateFormat.format(startTime);
    }

    /**
     * 显示子订单日期
     */
    public static final int ACTION_ORDER_DATES = 0x913;
    /**
     * 抢单
     */
    public static final int ACTION_GRAB_ORDER = 0x3545;

}
