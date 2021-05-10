package com.haylion.android.main;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haylion.android.R;
import com.haylion.android.common.map.AMapUtil;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.model.OrderStatus;
import com.haylion.android.data.model.OrderTypeInfo;
import com.haylion.android.data.util.BusinessUtils;
import com.haylion.android.data.util.DensityUtils;
import com.haylion.android.data.util.OrderConvert;
import com.haylion.android.mvp.util.SizeUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class OrderInfoAdapter extends BaseAdapter implements View.OnClickListener {

    private static final String TAG = "OrderInfoAdapter";
    private List<Order> list;
    private LayoutInflater layoutInflater;
    private InnerItemOnclickListener listener;
    private Context mContext;
    private String suggestLine;


    public OrderInfoAdapter(Context context, List<Order> list) {
        mContext = context;
        this.list = list;
        layoutInflater = LayoutInflater.from(context);
    }

    public void setSuggestLine(String suggestLine) {
        this.suggestLine = suggestLine;
    }

    @Override
    public int getCount() {
        return list.size();
    }


    @Override
    public Object getItem(int position) {
        return list.get(position);
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_order_info, null);

            holder = new ViewHolder();

            holder.orderNumber = convertView.findViewById(R.id.tv_order_number);
            holder.rlOrderNumber = convertView.findViewById(R.id.rl_order_number);

            holder.startAddress = convertView.findViewById(R.id.tv_start_address);
            holder.endAddress = convertView.findViewById(R.id.tv_end_address);
            holder.startAddressDetail = convertView.findViewById(R.id.tv_start_address_detail);
            holder.endAddressDetail = convertView.findViewById(R.id.tv_end_address_detail);
            holder.ivStartAddress = convertView.findViewById(R.id.iv_start_address);
            holder.ivEndAddress = convertView.findViewById(R.id.iv_end_address);

            holder.passengerNumber = convertView.findViewById(R.id.tv_passenger_num);
            holder.timeHour = convertView.findViewById(R.id.tv_time_hour);
            holder.timeDate = convertView.findViewById(R.id.tv_time_date);
            holder.llTime = convertView.findViewById(R.id.ll_time);


            holder.money = convertView.findViewById(R.id.tv_money);
            holder.viewDetails = convertView.findViewById(R.id.ic_view_details);
            holder.viewDetails2 = convertView.findViewById(R.id.ic_view_details_2);
            holder.container = convertView.findViewById(R.id.rl_order_container);
            holder.llOrderContainer = convertView.findViewById(R.id.ll_order_container);

            holder.ivHeaderMore = convertView.findViewById(R.id.iv_header_more);
            holder.tvOrderType = convertView.findViewById(R.id.tv_order_type);
            holder.rlOrderTypeHeaderContainer = convertView.findViewById(R.id.rl_order_type_header);

            //小孩信息
            holder.tvChildNames = convertView.findViewById(R.id.tv_child_names);
            holder.btnTransferOrder = convertView.findViewById(R.id.btn_transfor_order);
            holder.rlChildInfo = convertView.findViewById(R.id.rl_child_info);
            holder.tvMessage = convertView.findViewById(R.id.tv_message);
            holder.rlMessage = convertView.findViewById(R.id.rl_message);
            holder.tvCarpoolingOrder = convertView.findViewById(R.id.carpooling_order);

            holder.tvMessageDividedLine = convertView.findViewById(R.id.divided_message);

            //建议行驶路线
            holder.tvSuggestDetailLine = convertView.findViewById(R.id.tv_suggest_detail);
            holder.rlSuggest = convertView.findViewById(R.id.rl_suggest_line);

            //图标信息展示
            holder.ivStartAddrDetail = convertView.findViewById(R.id.iv_start_address_detail);
            holder.ilvTime = convertView.findViewById(R.id.ilv_time);
            holder.ivTime = convertView.findViewById(R.id.iv_time);
            holder.ilvGetOffDes = convertView.findViewById(R.id.iv_end_addess_detail);
            holder.ilvOtherInfo = convertView.findViewById(R.id.iv_other_info);

            //多日订单信息
            holder.tvNextOrderTimeTitle = convertView.findViewById(R.id.tv_next_order_title);
            holder.tvNextOrderTime = convertView.findViewById(R.id.tv_next_order_day);
            holder.tvOrderTimeDays = convertView.findViewById(R.id.tv_order_days);
            holder.llMultiOrderInfo = convertView.findViewById(R.id.ll_multi_order_info);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Order order = list.get(position);
        Drawable drawable;

        if (order.getOrderType() == OrderTypeInfo.ORDER_TYPE_SEND_CHILD.getStatus()) {
            SpannableStringBuilder typeBuilder = new SpannableStringBuilder("送你上学 ");
            SpannableString spannableString = new SpannableString("(待服务)");
            spannableString.setSpan(new AbsoluteSizeSpan(DensityUtils.sp2px(mContext, 14)),
                    0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            typeBuilder.append(spannableString);

            holder.tvOrderType.setText(typeBuilder);
        } else {
            holder.tvOrderType.setText(OrderTypeInfo.getStatusText(order.getOrderType()));
        }

        if (order.isOrderTypeheaderDisplay()) {
            holder.rlOrderTypeHeaderContainer.setVisibility(View.VISIBLE);
            if (order.getOrderType() == Order.ORDER_TYPE_REALTIME_CARPOOL) {
                if (suggestLine != null && !suggestLine.equals("")) {
                    holder.rlSuggest.setVisibility(View.VISIBLE);
                }
            } else {
                holder.rlSuggest.setVisibility(View.GONE);
            }
        } else {
            holder.rlOrderTypeHeaderContainer.setVisibility(View.GONE);
        }

        //订单编号
        holder.orderNumber.setText(order.getOrderCode());
        if (order.getOrderType() == Order.ORDER_TYPE_CARGO_PASSENGER) {
            holder.rlOrderNumber.setPadding(SizeUtil.dp2px(6), 0, 0, SizeUtil.dp2px(20));
        }

        //进度条的样式
        int colorBlue = 0xFF4B7EFB;
        int colorGray = 0xFFB3B3B3;
        if (order.getOrderType() == Order.ORDER_TYPE_CARGO_PASSENGER) {
            holder.ilvTime.setLineAttribute(colorGray, SizeUtil.dp2px(1), new float[]{10, 10, 10, 10});
            holder.ivStartAddrDetail.setLineAttribute(colorGray, SizeUtil.dp2px(20), new float[]{10, 0, 10, 0});
            holder.ilvGetOffDes.setLineAttribute(colorGray, SizeUtil.dp2px(1), new float[]{10, 10, 10, 10});
            holder.ilvOtherInfo.setLineAttribute(colorGray, SizeUtil.dp2px(1), new float[]{10, 10, 10, 10});
            if (order.getOrderStatus() == Order.ORDER_STATUS_GET_ON || order.getOrderStatus() == Order.ORDER_STATUS_GET_OFF) {
                drawable = mContext.getResources().getDrawable(R.mipmap.icon_get_on_gray);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                holder.ivStartAddress.setBackground(drawable);

                drawable = mContext.getResources().getDrawable(R.mipmap.ic_main_get_off);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                holder.ivEndAddress.setBackground(drawable);

                holder.ilvGetOffDes.setLineAttribute(colorBlue, SizeUtil.dp2px(1), new float[]{10, 10, 10, 10});
                holder.ilvOtherInfo.setLineAttribute(colorBlue, SizeUtil.dp2px(1), new float[]{10, 10, 10, 10});

            } else if (/*order.getOrderStatus() == Order.ORDER_STATUS_GET_OFF
                    || */order.getOrderStatus() == Order.ORDER_STATUS_WAIT_PAY
                    || order.getOrderStatus() == Order.ORDER_STATUS_CLOSED) {
                drawable = mContext.getResources().getDrawable(R.mipmap.icon_get_on_gray);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                holder.ivStartAddress.setBackground(drawable);

                drawable = mContext.getResources().getDrawable(R.mipmap.icon_get_off_gray);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                holder.ivEndAddress.setBackground(drawable);
            } else {
                drawable = mContext.getResources().getDrawable(R.mipmap.ic_main_get_on);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                holder.ivStartAddress.setBackground(drawable);

                drawable = mContext.getResources().getDrawable(R.mipmap.ic_main_get_off);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                holder.ivEndAddress.setBackground(drawable);

                holder.ivStartAddrDetail.setLineAttribute(colorBlue, SizeUtil.dp2px(20), new float[]{10, 0, 10, 0});
                holder.ilvGetOffDes.setLineAttribute(colorBlue, SizeUtil.dp2px(1), new float[]{10, 10, 10, 10});
                holder.ilvOtherInfo.setLineAttribute(colorBlue, SizeUtil.dp2px(1), new float[]{10, 10, 10, 10});
            }
        } else {
            drawable = mContext.getResources().getDrawable(R.mipmap.ic_main_get_on);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            holder.ivStartAddress.setBackground(drawable);

            drawable = mContext.getResources().getDrawable(R.mipmap.ic_main_get_off);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            holder.ivEndAddress.setBackground(drawable);

            holder.ivStartAddrDetail.setLineAttribute(colorBlue, SizeUtil.dp2px(20), new float[]{10, 0, 10, 0});
        }

        holder.startAddress.setText(order.getStartAddr().getName());
        holder.endAddress.setText(order.getEndAddr().getName());
        holder.startAddressDetail.setText(AMapUtil.getAddress(order.getStartAddr().getAddressDetail()));
        holder.endAddressDetail.setText(AMapUtil.getAddress(order.getEndAddr().getAddressDetail()));

        holder.passengerNumber.setText("" + order.getPassengerNum() + "人");

        //实时单，只是显示时和分
        //小时和分
        holder.timeHour.setText(order.getOrderTime().substring(11));

        //日期
        String orderTime = order.getOrderTime();
        String dateString = "2019-12-01";
        try {
            long milliSecond = BusinessUtils.stringToLong(orderTime, "yyyy-MM-dd HH:mm");
            dateString = BusinessUtils.getDateStringOnlyDay(milliSecond, null);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.timeDate.setText(dateString);

        if (order.getOrderType() == Order.ORDER_TYPE_CARGO_PASSENGER) {
            holder.ivTime.setVisibility(View.GONE);
            holder.llTime.setPadding(0, SizeUtil.dp2px(6), 0, SizeUtil.dp2px(10));
//            Log.d(TAG, "R.dimen.dp_10: " + mContext.getResources().getDimension(R.dimen.dp_10));
//            Log.d(TAG, "R.dimen.dp_10: " + mContext.getResources().getDimensionPixelSize(R.dimen.dp_10));
//            Log.d(TAG, "SizeUtil.dp2px(10): " + SizeUtil.dp2px(10));

        } else {
            holder.ivTime.setVisibility(View.VISIBLE);
        }
        if (order.getOrderType() == Order.ORDER_TYPE_SEND_CHILD) {
            if (order.isParentOrder()) {
                holder.timeDate.setVisibility(View.GONE);
            } else {
                holder.timeDate.setVisibility(View.VISIBLE);
            }
            if (order.isParentOrder()) {
                holder.timeDate.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelSize(R.dimen.sp_16));
                holder.timeHour.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelSize(R.dimen.sp_16));
            } else {
                holder.timeDate.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelSize(R.dimen.sp_18));
                holder.timeHour.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelSize(R.dimen.sp_18));
            }
            holder.timeHour.setVisibility(View.VISIBLE);

            if (order.isCarpoolOrder()) { // 拼车单
                holder.tvCarpoolingOrder.setVisibility(View.VISIBLE);
            } else {
                holder.tvCarpoolingOrder.setVisibility(View.GONE);
            }
        } else {
            // 应产品口头要求，需显示 “今天”
//            if (dateString.equals("今天")) {
//                holder.timeDate.setVisibility(View.GONE);
//            } else {
//                holder.timeDate.setVisibility(View.VISIBLE);
//            }
            holder.timeHour.setVisibility(View.VISIBLE);

            // 非送小孩单均隐藏“拼车”字样
            holder.tvCarpoolingOrder.setVisibility(View.GONE);
        }

        holder.money.setText("￥ " + BusinessUtils.moneySpec(order.getTotalMoney()));

        //送小孩上学的显示
        String names;
        names = OrderConvert.getChildNames(order);
        holder.tvChildNames.setText(names);
        holder.tvMessage.setText(order.getParentMessage());

//        holder.tvMessageDividedLine.setVisibility(View.VISIBLE); //分割线
        //默认不显示留言信息
        holder.tvMessage.setVisibility(View.GONE); //留言
        holder.rlMessage.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(order.getParentMessage())) {
            holder.tvMessage.setVisibility(View.VISIBLE); //留言
            holder.rlMessage.setVisibility(View.VISIBLE);
            //   holder.tvMessageDividedLine.setVisibility(View.VISIBLE); //分割线
        }

        //小孩名字和留言都不为空时才显示分割线
        if (!TextUtils.isEmpty(order.getParentMessage())) {
            holder.tvMessageDividedLine.setVisibility(View.VISIBLE); //分割线
        }

        switch (order.getOrderType()) {
            case Order.ORDER_TYPE_REALTIME_CARPOOL:
                holder.passengerNumber.setVisibility(View.VISIBLE);
                holder.money.setVisibility(View.VISIBLE);
                holder.tvSuggestDetailLine.setText(suggestLine);
                holder.rlChildInfo.setVisibility(View.GONE);
                break;
            case Order.ORDER_TYPE_REALTIME:
                holder.passengerNumber.setVisibility(View.GONE);
                holder.money.setVisibility(View.GONE);
                holder.rlChildInfo.setVisibility(View.GONE);
                break;
            case Order.ORDER_TYPE_BOOK:
            case Order.ORDER_TYPE_ACCESSIBILITY:
                holder.passengerNumber.setVisibility(View.GONE);
                holder.money.setVisibility(View.GONE);
                holder.rlChildInfo.setVisibility(View.GONE);
                break;
            case Order.ORDER_TYPE_CARGO:
            case Order.ORDER_TYPE_CARGO_PASSENGER:
                holder.passengerNumber.setVisibility(View.GONE);
                holder.money.setVisibility(View.GONE);
                holder.container.setBackground(null);
                holder.container.setPadding(0, 0, 0, 0);
                holder.llOrderContainer.setPadding(0, 0, 0, 0);
                holder.rlChildInfo.setVisibility(View.GONE);  //显示小孩信息
                break;
            case Order.ORDER_TYPE_SEND_CHILD:
                holder.passengerNumber.setVisibility(View.GONE);
                holder.money.setVisibility(View.GONE);
                // holder.rlChildInfo.setVisibility(View.VISIBLE);  //显示小孩信息
/*                holder.tvMessage.setVisibility(View.VISIBLE); //留言
                holder.tvSuggestDetailLine.setVisibility(View.VISIBLE); //分割线*/
                break;
            default:
                break;
        }

        //是否显示转单按钮
        String time = order.getOrderTime();
        if (order.isParentOrder()) {
            if (order.getOrderStatus() == Order.ORDER_STATUS_READY) {
                try {
                    long orderTimeMilliSecond = BusinessUtils.stringToLong(time, "yyyy-MM-dd HH:mm");
                    Log.d(TAG, "" + "orderTimeMilliSecond: " + orderTimeMilliSecond);
                    long currentTime = System.currentTimeMillis();
                    if (orderTimeMilliSecond - currentTime < 12 * 3600 * 1000) {
                        holder.btnTransferOrder.setVisibility(View.GONE);
                    } else {
                        holder.btnTransferOrder.setVisibility(View.VISIBLE);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                holder.btnTransferOrder.setVisibility(View.GONE);
            }
        } else {
            holder.btnTransferOrder.setVisibility(View.GONE);
        }

        //   ((ListView) parent).setOnItemClickListener(this);//设置点击事件

        //左边的进度图标
        if (order.getOrderType() == Order.ORDER_TYPE_CARGO_PASSENGER) {
            holder.ivStartAddrDetail.setVisibility(View.VISIBLE);
            holder.ilvTime.setVisibility(View.VISIBLE);
            holder.ilvGetOffDes.setVisibility(View.VISIBLE);
            holder.ilvOtherInfo.setVisibility(View.VISIBLE);
            holder.llOrderContainer.setPadding(0, 0, 0, 0);
            //     holder.container.setPadding(0, 0, 0, 0);
        } else {
            holder.ivStartAddrDetail.setVisibility(View.INVISIBLE);
            holder.ilvTime.setVisibility(View.GONE);
            holder.ilvGetOffDes.setVisibility(View.INVISIBLE);
            holder.ilvOtherInfo.setVisibility(View.GONE);

            int pxLeftAndRight = mContext.getResources().getDimensionPixelSize(R.dimen.dp_10);
            int pxTopAndBottom = mContext.getResources().getDimensionPixelSize(R.dimen.dp_15);
            holder.llOrderContainer.setPadding(pxLeftAndRight, pxTopAndBottom, pxLeftAndRight, pxTopAndBottom);

            //    holder.container.setPadding(SizeUtil.dp2px(22), SizeUtil.dp2px(16), SizeUtil.dp2px(22), SizeUtil.dp2px(16));
        }

        //多日订单信息模块的展示
        Log.d(TAG, "isParentOrder: " + order.isParentOrder());
        holder.llMultiOrderInfo.setVisibility(View.GONE);
        holder.viewDetails2.setVisibility(View.VISIBLE);
        if (order.isParentOrder()) {
            holder.llMultiOrderInfo.setVisibility(View.VISIBLE);
            holder.viewDetails2.setVisibility(View.GONE);
            //起始和结束的日期
            Date startTime = new Date(System.currentTimeMillis());
            ;
            Date endTime = new Date(System.currentTimeMillis());
            ;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            try {
                startTime = simpleDateFormat.parse(order.getStartTime());
                endTime = simpleDateFormat.parse(order.getEndTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Calendar calendar = Calendar.getInstance();
            int todayYear = calendar.get(Calendar.YEAR);
            calendar.setTime(startTime);
            int orderStartYear = calendar.get(Calendar.YEAR);
            calendar.setTime(endTime);
            int orderEndYear = calendar.get(Calendar.YEAR);
            String daysShow = "";
            if (todayYear != orderStartYear || todayYear != orderEndYear) {
                //显示年信息
                simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
                daysShow = simpleDateFormat.format(startTime) + " - " + simpleDateFormat.format(endTime);
            } else {
                //显示年信息
                simpleDateFormat = new SimpleDateFormat("MM月dd日");
                daysShow = simpleDateFormat.format(startTime) + " - " + simpleDateFormat.format(endTime);
            }
            holder.tvOrderTimeDays.setText(daysShow);

            //下一次订单日期
            dateString = "1970-01-01";
            try {
                long milliSecond = BusinessUtils.stringToLong(orderTime, "yyyy-MM-dd HH:mm");
                dateString = BusinessUtils.getDateStringOnlyDay(milliSecond, null);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.tvNextOrderTime.setText(dateString); //todo

            //title区分，下一次订单还是当前进行中的订单
            int status = order.getOrderStatus();
            String statusText = "下次行程";
            switch (OrderStatus.forStatus(status)) {
                case ORDER_STATUS_CLOSED:
                    break;
                case ORDER_STATUS_WAIT_PAY:
                    break;
                case ORDER_STATUS_WAIT_CAR:
                case ORDER_STATUS_GET_ON:
                case ORDER_STATUS_ARRIVED_START_ADDR:
                case ORDER_STATUS_GET_OFF:
                    statusText = "进行中行程";
                    break;
                case ORDER_STATUS_READY:
                case ORDER_STATUS_INIT:
                    break;
                case ORDER_STATUS_CANCELED:
                    break;
                case UNKNOWN:
                    statusText = "下次行程";
                    break;
            }
            holder.tvNextOrderTimeTitle.setText(statusText);
        }

        setOnClickListener(holder, position);

        return convertView;
    }

    public void setItemClickListener(InnerItemOnclickListener listener) {
        this.listener = listener;
    }

    /**
     * 点击事件
     *
     * @param holder
     * @param position getView 的 position
     *                 注意：由于复用等原因，如果直接在子控件的onClick事件中调用
     *                 getView()中的position，会出现数据混乱问题，解决这个问题的方法是，
     *                 在adapter每次加载数据的时候，为需要点击的控件设置一个tag值。
     *                 比如点击14位置的，结果holder复用了4的。
     */
    private void setOnClickListener(ViewHolder holder, int position) {
        holder.llOrderContainer.setTag(position);
        if (list.get(position).getOrderType() == Order.ORDER_TYPE_CARGO_PASSENGER) {
            //货拼客单，不监听点击事件
            holder.llOrderContainer.setOnClickListener(null);
        } else {
            holder.llOrderContainer.setOnClickListener(this);
        }


        //小孩单的转单按钮
        if (list.get(position).getOrderType() == Order.ORDER_TYPE_SEND_CHILD) {
            holder.btnTransferOrder.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_order_container:
                if (listener == null) {
                    return;
                }
                int position = (Integer) v.getTag();
                Order order = list.get(position);
                //货拼客单
                if (order.getOrderType() == Order.ORDER_TYPE_CARGO_PASSENGER) {
                    listener.itemClick(order.getOrderId(), true);
                    return;
                }
                if (order.getOrderType() == Order.ORDER_TYPE_SEND_CHILD) {
                    if (order.isCarpoolOrder()) {
                        // 拼车单
                        listener.onCarpoolOrderClick(order);
                    } else if (order.isParentOrder()) {
                        //多日订单，小孩单
                        listener.itemClick(order);
                    } else {
                        listener.itemClick(order.getOrderId(), false);
                    }

                } else {
                    //普通订单、小孩单
                    listener.itemClick(order.getOrderId(), false);
                }
                break;
            case R.id.btn_transfor_order:
                if (listener == null) {
                    return;
                }
                listener.transforOrder();
                break;
            default:
                break;
        }
    }

    interface InnerItemOnclickListener {
        void itemClick(int orderId, boolean cargoPassenger);

        void itemClick(Order order);

        void onCarpoolOrderClick(Order order);

        void transforOrder();
    }

    static class ViewHolder {
        TextView orderNumber;
        RelativeLayout rlOrderNumber;

        TextView startAddress;
        TextView endAddress;
        TextView startAddressDetail;
        TextView endAddressDetail;

        ImageView ivStartAddress;
        ImageView ivEndAddress;

        TextView passengerNumber;
        TextView timeDate;
        TextView timeHour;
        LinearLayout llTime;

        TextView money;
        ImageView viewDetails;
        ImageView viewDetails2;
        RelativeLayout container;
        LinearLayout llOrderContainer;
        TextView tvOrderType;
        ImageView ivHeaderMore;
        RelativeLayout rlOrderTypeHeaderContainer;

        //小孩信息
        TextView tvChildNames;
        Button btnTransferOrder;
        RelativeLayout rlChildInfo;
        TextView tvMessage;
        RelativeLayout rlMessage;
        TextView tvMessageDividedLine;

        //建议行驶路线
        TextView tvSuggestDetailLine;
        RelativeLayout rlSuggest;

        //左边的进度条显示
        ImaginaryLineView ivStartAddrDetail;
        ImaginaryLineView ilvTime;
        ImaginaryLineView ilvGetOffDes;
        ImaginaryLineView ilvOtherInfo;
        ImageView ivTime;

        //小孩单
        TextView tvNextOrderTimeTitle;
        TextView tvNextOrderTime;
        TextView tvOrderTimeDays;
        LinearLayout llMultiOrderInfo;
        View tvCarpoolingOrder; // “拼车”标签

    }
}
