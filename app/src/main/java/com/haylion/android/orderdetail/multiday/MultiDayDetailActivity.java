package com.haylion.android.orderdetail.multiday;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.haylion.android.R;
import com.haylion.android.common.view.dialog.DialogUtils;
import com.haylion.android.data.base.BaseActivity;
import com.haylion.android.data.event.CarpoolOrderPaid;
import com.haylion.android.data.model.CarpoolOrderExt;
import com.haylion.android.data.model.EventMsg;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.model.OrderDateSimple;
import com.haylion.android.data.model.OrderExt;
import com.haylion.android.data.model.OrderStatus;
import com.haylion.android.data.model.OrderTypeInfo;
import com.haylion.android.data.model.WebsocketData;
import com.haylion.android.data.util.BusinessUtils;
import com.haylion.android.mvp.util.LogUtils;
import com.haylion.android.data.util.OrderConvert;
import com.haylion.android.main.MainActivity;
import com.haylion.android.mvp.util.SizeUtil;
import com.haylion.android.orderdetail.ContactParentsChooser;
import com.haylion.android.orderdetail.OrderDetailActivity;
import com.haylion.android.orderdetail.trip.TripDetailActivity;
import com.haylion.android.service.WsCommands;
import com.haylion.android.uploadPhoto.LargeImageDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * @class MultiDayDetailActivity
 * @description 多日订单信息展示
 * @date: 2019/11/8 11:25
 * @author: tandongdong
 */
public class MultiDayDetailActivity extends BaseActivity<MultiDayDetailContract.Presenter>
        implements MultiDayDetailContract.View, AMap.OnMapClickListener,
        AMap.OnMarkerClickListener, AMap.OnInfoWindowClickListener, AMap.InfoWindowAdapter {

    private final String TAG = getClass().getSimpleName();

    public final static String ORDER_ID = "EXTRA_ORDER_ID";
    public final static String CARPOOL_ORDER = "carpool_order";

    @BindView(R.id.header_name)
    TextView mHeaderName;

    //时间和位置信息
    @BindView(R.id.tv_comming_order_time_day)  //下一单的日期
            TextView tvCommingOrderDay;
    @BindView(R.id.tv_next_order_title) //下次行程的title
            TextView tvNextOrderTitle;
    @BindView(R.id.tv_enter_sub_order_detail)
    TextView tvEnterSubOrderDetail; //进入子订单详情页面

    @BindView(R.id.tv_order_time_days)  //
            TextView tvOrderTimeDays;
    @BindView(R.id.tv_order_time_hour)
    TextView tvOderTimeHour;
    @BindView(R.id.tv_get_on_addr)  //上车地址
            TextView tvOrderGetOn;
    @BindView(R.id.tv_get_off_addr)  //下车地址
            TextView tvOrderGetOff;

    @BindView(R.id.tv_order_number_info)
    TextView tvOrderNumberInfo;

    //乘客信息
    @BindView(R.id.tv_child_names)   //
            TextView tvChildNames;
    @BindView(R.id.rl_message)
    RelativeLayout rlMessage;
    @BindView(R.id.tv_message)   //
            TextView tvMessage;
    @BindView(R.id.tv_child_header)
    TextView tvChildHeader;

    //订单其他信息
    @BindView(R.id.tv_order_type)   //订单类型
            TextView tvOrderType;
    @BindView(R.id.tv_order_number)   //订单编号
            TextView tvOrderNumber;

    //手机号信息
    @BindView(R.id.tv_child_phone)
    TextView tvChildPhone;
    @BindView(R.id.tv_parent_phone)
    TextView tvParentPhone;

    @BindView(R.id.tv_transfer_order)
    TextView tvTransferOrder;

    @BindView(R.id.tv_more_days)
    TextView tvMoreDays;

    @BindView(R.id.carpooling_order)
    View tvCarpoolingOrder;
    @BindView(R.id.ll_carpool_address)
    LinearLayout llCarpoolingAddress;
    @BindView(R.id.carpool_addresses_container)
    LinearLayout carpoolAddressContainer;

    @BindView(R.id.ll_order_dates)
    LinearLayout llOrderDates;

    /**
     * 订单
     */
    private OrderExt order;
    /**
     * 拼车订单
     */
    private CarpoolOrderExt carpoolOrder;

    private String orderNum;
    private boolean carpoolFlag;

    //日期表格
    private RecyclerView recyclerView;
    private MultiDayAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    public static void go(Context context, String orderNum) {
        go(context, orderNum, false);
    }

    /**
     * 跳转入口
     *
     * @param context
     * @param orderNum --订单编号
     */
    public static void go(Context context, String orderNum, boolean carpoolOrder) {
        Intent intent = new Intent(context, MultiDayDetailActivity.class);
        intent.putExtra(ORDER_ID, orderNum);
        intent.putExtra(CARPOOL_ORDER, carpoolOrder);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_day_detail);
        EventBus.getDefault().register(this);

        orderNum = getIntent().getStringExtra(ORDER_ID);
        carpoolFlag = getIntent().getBooleanExtra(CARPOOL_ORDER, false);
        if (carpoolFlag) { // 拼车单，需隐藏部分视图
            mHeaderName.setText(R.string.title_carpool_order_info);

            tvNextOrderTitle.setVisibility(View.GONE);
            tvCommingOrderDay.setVisibility(View.GONE);

            tvCarpoolingOrder.setVisibility(View.VISIBLE);
            llCarpoolingAddress.setVisibility(View.VISIBLE);

            llOrderDates.setVisibility(View.GONE);
        } else {
            tvCarpoolingOrder.setVisibility(View.GONE);
            llCarpoolingAddress.setVisibility(View.GONE);
        }
        //多日订单表格
        recyclerView = (RecyclerView) findViewById(R.id.rv_multi_day_form);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this, 3);

        recyclerView.setLayoutManager(layoutManager);

    }

    private void refreshOrderDetails() {
        if (carpoolFlag) {
            presenter.getCarpoolOrderDetails(orderNum);
        } else {
            presenter.getMultiDayOrderDetail(orderNum);
        }
    }

    boolean expand = false; //是否展开

    @OnClick({R.id.ll_back, R.id.iv_back, R.id.tv_child_phone, R.id.tv_parent_phone, R.id.tv_transfer_order, R.id.tv_more_days,
            R.id.tv_child_header, R.id.tv_enter_sub_order_detail})
    public void onButtonClick(View view) {
        if (order == null && carpoolOrder == null) {
            return;
        }
        switch (view.getId()) {
            case R.id.ll_back:
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_child_phone:  //联系乘客
                if (carpoolFlag) {
                    ContactParentsChooser chooser = new ContactParentsChooser(getContext(), new ContactParentsChooser.OnSelectListener() {
                        @Override
                        public void onSelect(String phoneNumber) {
                            DialogUtils.showRealCallDialog(getContext(), phoneNumber);
                        }
                    });
                    chooser.setData("联系小孩", OrderConvert.getChildPhones(carpoolOrder));
                    chooser.showAtLocation(tvParentPhone, Gravity.BOTTOM, 0, 0);
                } else {
                    DialogUtils.showRealCallDialog(this, order.getChildList().get(0).getMobile());
                }
                break;
            case R.id.tv_parent_phone: //联系家长
                if (carpoolFlag) {
                    ContactParentsChooser chooser = new ContactParentsChooser(getContext(), new ContactParentsChooser.OnSelectListener() {
                        @Override
                        public void onSelect(String phoneNumber) {
                            DialogUtils.showRealCallDialog(getContext(), phoneNumber);
                        }
                    });
                    chooser.setData("联系家长", carpoolOrder.getParentPhoneList());
                    chooser.showAtLocation(tvParentPhone, Gravity.BOTTOM, 0, 0);
                } else {
                    DialogUtils.showRealCallDialog(this, order.getUserInfo().getPhoneNum());
                }
                break;
            case R.id.tv_transfer_order:
                presenter.getServiceTelNumber();
                break;
            case R.id.tv_more_days:
                Log.d(TAG, "getTravelDateOutputFormList:" + order.getTravelDateOutputFormList().size());

                int rowNum;
                int dataSize = order.getTravelDateOutputFormList().size();
                rowNum = dataSize / 3 + ((dataSize % 3 > 0) ? 1 : 0);
                if (expand) {
                    rowNum = 2;
                }
                ViewGroup.LayoutParams lp = recyclerView.getLayoutParams();
                lp.height = SizeUtil.dp2px(70f * rowNum);
                recyclerView.setLayoutParams(lp);
                TextView tvMoreDays = findViewById(R.id.tv_more_days);

                if (rowNum > 2) {
                    tvMoreDays.setVisibility(View.VISIBLE);
                }

                if (expand == false) {
                    Log.d(TAG, "getTravelDateOutputFormList:" + order.getTravelDateOutputFormList().size());
                    Drawable drawable = getResources().getDrawable(R.mipmap.arrow_shou_blue);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    tvMoreDays.setCompoundDrawables(null, null, drawable, null);
                    tvMoreDays.setText("");
                    expand = true;
                } else {
                    Drawable drawable = getResources().getDrawable(R.mipmap.arrow_xiala_blue);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                    tvMoreDays.setCompoundDrawables(null, null, drawable, null);
                    tvMoreDays.setText("更多日期");
                    expand = false;
                }

                break;
            case R.id.tv_child_header:    //乘客头像
                List<String> mPhotoList;
                if (carpoolFlag) {
                    mPhotoList = OrderConvert.getChildPhotos(carpoolOrder);
                } else {
                    mPhotoList = OrderConvert.getChildPhotos(order);
                }
                if (mPhotoList.size() > 0) {
                    LargeImageDialog.newInstance().setData(mPhotoList).show(getSupportFragmentManager(), null);
                } else {
                    toast(R.string.toast_avatar_is_empty);
                }
                break;
            case R.id.tv_enter_sub_order_detail: //进入下次行程详情页
                if (carpoolFlag) {
                    Intent intent = new Intent(getContext(), OrderDetailActivity.class);
                    intent.putExtra(OrderDetailActivity.EXTRA_CARPOOL_CODE, orderNum);
                    intent.putExtra(OrderDetailActivity.EXTRA_CARPOOL_FLAG, true);
                    startActivity(intent);
                } else {
                    enterNextOrderDetail();
                }
                break;
            default:
                break;
        }
    }


    /**
     * @param
     * @return
     * @method
     * @description 进入下次行程的详情页面
     * 如果当前订单中所有行程状态都在滑动“去接乘客”之前，或点击“提交账单”之后，就显示下次行程及信息；
     * 如果当前有行程状态在滑动“去接乘客”之后，点击“提交账单”之前，就显示进行中行程信息
     * 1）有进行中的订单，则进入正在进行中的订单。
     * 2）没有进行中的订单，则进入下次进程的订单。
     * @date: 2020/1/16 18:02
     * @author: tandongdong
     */
    private void enterNextOrderDetail() {
        if (null != getNextOrder()) {
            enterOrderDetailActivity(getNextOrder());
        }
    }

    /**
     * @param
     * @return
     * @method
     * @description 获取下一个需要完成的订单（未开始 或者 正在进行中，没有则返回null）
     * @date: 2020/1/16 18:01
     * @author: tandongdong
     */
    private OrderDateSimple getNextOrder() {
        List<OrderDateSimple> mDataList = new ArrayList<>();
        mDataList = order.getTravelDateOutputFormList();
        OrderDateSimple nextOrder = new OrderDateSimple();

        for (int i = 0; i < mDataList.size(); i++) {
            boolean haveNextOrder = false;
            switch (mDataList.get(i).getStatus()) {
                case Order.ORDER_STATUS_INIT: //6; // 初始状态，未接单
                    break;
                case Order.ORDER_STATUS_READY: //3; // 预约订单，存在的状态。在时间到达前，会进入状态 7
                case Order.ORDER_STATUS_WAIT_CAR: //7; // 等待司机接驾
                case Order.ORDER_STATUS_ARRIVED_START_ADDR: //8; //到达上车点，等待乘客上车
                case Order.ORDER_STATUS_GET_ON: //4; //乘客已上车
                case Order.ORDER_STATUS_GET_OFF: //9; //乘客已下车
                    nextOrder = mDataList.get(i);
                    haveNextOrder = true;            //找到下一个订单或者正在进行中的订单
                    break;
                case Order.ORDER_STATUS_WAIT_PAY: //2; // 待支付
                case Order.ORDER_STATUS_CLOSED: //1; //订单已完成
                case Order.ORDER_STATUS_CANCELED: //5; //订单已取消
                    break;
            }

            if (haveNextOrder) {
                return nextOrder;
            }
        }
        return null;
    }

    /**
     * @param
     * @return
     * @method
     * @description 更新下一个订单信息显示
     * @date: 2020/1/16 17:46
     * @author: tandongdong
     */
    private void updateNextOrderView() {
        if (null == getNextOrder()) {
            tvCommingOrderDay.setText("无");
            return;
        }
        //更新下一次订单的日期信息
        String orderTime = getNextOrder().getDate();
        String dayString = "无";
        try {
            long milliSecond = BusinessUtils.stringToLong(orderTime, "yyyy-MM-dd HH:mm");
            dayString = BusinessUtils.getDateStringOnlyDay(milliSecond, null);
            Log.d(TAG, "" + "dayString: " + dayString);
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            tvCommingOrderDay.setText(dayString);
        }

        //下一次订单还是当前进行中的订单
        int status = getNextOrder().getStatus();
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
        tvNextOrderTitle.setText(statusText);
    }


    /**
     * 获取客服电话成功，弹出拨打电话弹窗
     *
     * @param phoneNum
     */
    @Override
    public void showDialDialog(String phoneNum) {
        DialogUtils.showCustomerServiceCallDialog(this, phoneNum, true);
        // callCustomerService(phoneNum);
    }

    /**
     * 到听单页
     */
    private void goMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    /**
     * 线下确认支付成功，重新查询订单详情
     */
    @Override
    public void payConfirmSuccess() {
        refreshOrderDetails();
    }

    @Override
    protected MultiDayDetailContract.Presenter onCreatePresenter() {
        return new MultiDayDetailPresenter(this);
    }

    /**
     * 获取订单详情失败
     *
     * @param msg
     */
    @Override
    public void getOrderDetailFail(String msg) {
        toast(R.string.toast_get_order_info_fail);
        finish();
    }

    /**
     * 获取订单详情成功
     *
     * @param orderExt
     */
    @Override
    public void getMultiDayOrderDetailSuccess(OrderExt orderExt) {
        if (orderExt != null) {
            order = orderExt;
        } else {
            return;
        }

        //基本订单信息
        updateOrderInfoCardView(orderExt);

        //日期表格
        List<OrderDateSimple> mDataList = new ArrayList<>();
        mDataList = orderExt.getTravelDateOutputFormList();
        int totalOrderNumber = mDataList.size();
        int completedOrderNumber = 0;
        for (int i = 0; i < mDataList.size(); i++) {
            if (mDataList.get(i).getStatus() == Order.ORDER_STATUS_CLOSED) {
                completedOrderNumber++;
            }
        }

        String origin = "已完成 " + completedOrderNumber + "  单/共 " + totalOrderNumber + " 单";
        SpannableString spannableString = new SpannableString(origin);
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.maas_text_primary)),
                4, 4 + ((completedOrderNumber / 10) > 0 ? 2 : 1), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.maas_text_primary)),
                origin.indexOf("共") + 1, origin.length() - 2, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

        spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
                4, 4 + ((completedOrderNumber / 10) > 0 ? 2 : 1), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
                origin.indexOf("共") + 1, origin.length() - 2, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        tvOrderNumberInfo.setText(spannableString);

        mAdapter = new MultiDayAdapter(this, orderExt.getTravelDateOutputFormList());
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new MultiDayAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                enterOrderDetailActivity(orderExt.getTravelDateOutputFormList().get(position));
            }
        });

        if (orderExt.getTravelDateOutputFormList().size() > 6) {
            tvMoreDays.setVisibility(View.VISIBLE);
        }

    }


    /**
     * @param
     * @return
     * @method
     * @description 根据订单的状态进入不同的页面
     * @date: 2020/1/17 10:00
     * @author: tandongdong
     */
    public void enterOrderDetailActivity(OrderDateSimple orderDateSimple) {
        if (orderDateSimple.getStatus() == Order.ORDER_STATUS_CLOSED
                || orderDateSimple.getStatus() == Order.ORDER_STATUS_WAIT_PAY) {
            TripDetailActivity.go(this, orderDateSimple.getOrderId(), false);
        } else {
            OrderDetailActivity.go(this, orderDateSimple.getOrderId());
        }
    }

    /**
     * 订单信息展示
     */
    private void updateOrderInfoCardView(OrderExt order) {
        //多日订单信息模块的展示
/*        //下一次订单日期
        String orderTime = order.getOrderTime();
        String dayString = "无";
        try {
            long milliSecond = BusinessUtils.stringToLong(orderTime, "yyyy-MM-dd HH:mm");
            dayString = BusinessUtils.getDateStringOnlyDay(milliSecond, null);
            Log.d(TAG, "" + "dayString: " + dayString);
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            tvCommingOrderDay.setText(dayString);
        }*/
        updateNextOrderView();

        //起始和结束的日期
        Date startTime = new Date(System.currentTimeMillis());
        Date endTime = new Date(System.currentTimeMillis());
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
        tvOrderTimeDays.setText(daysShow);

        //订单时间的小时信息
        tvOderTimeHour.setText(order.getOrderTime().substring(11, 16));

        //地址
        tvOrderGetOn.setText(order.getStartAddr().getName());
        tvOrderGetOff.setText(order.getEndAddr().getName());

        //乘客信息
        String names;
        names = OrderConvert.getChildNames(order);
        tvChildNames.setText(names);
        tvMessage.setText(order.getParentMessage());
//        tvMessageDividedLine.setVisibility(View.VISIBLE); //分割线
        //默认不显示留言信息
        tvMessage.setVisibility(View.GONE); //留言
        rlMessage.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(order.getParentMessage())) {
            tvMessage.setVisibility(View.VISIBLE); //留言
            rlMessage.setVisibility(View.VISIBLE);
            //   holder.tvMessageDividedLine.setVisibility(View.VISIBLE); //分割线
        }

        //订单信息
        //是否显示转单按钮
        OrderDateSimple orderDateSimple = order.getTravelDateOutputFormList().get(0);
        if (orderDateSimple.getStatus() == Order.ORDER_STATUS_READY) {
            String time = orderDateSimple.getDate() + order.getOrderTime().substring(11, 16);
            try {
                long orderTimeMilliSecond = BusinessUtils.stringToLong(time, "yyyy-MM-dd HH:mm");
                Log.d(TAG, "" + "orderTimeMilliSecond: " + orderTimeMilliSecond);
                long currentTime = System.currentTimeMillis();
                if (orderTimeMilliSecond - currentTime < 12 * 3600 * 1000) {
                    tvTransferOrder.setVisibility(View.GONE);
                } else {
                    tvTransferOrder.setVisibility(View.VISIBLE);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            tvTransferOrder.setVisibility(View.GONE);
        }

        //订单编号
        tvOrderNumber.setText(order.getOrderCode());
        tvOrderType.setText("送你上学");
    }

    /**
     * 支付结果 消息监听
     *
     * @param websocketData
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(WebsocketData websocketData) {
        LogUtils.d(TAG, "on event:" + websocketData.toString());
        if (websocketData.getCmdSn() == WsCommands.DRIVER_ORDER_PAID.getSn()) {
            LogUtils.d(TAG, "支付完成");
            int orderId = websocketData.getData().getOrderId();
            if (order != null && orderId == order.getOrderId()) {
//                refreshOrderDetails();
            }
        }
    }

    /**
     * 新订单抢单 成功与否监听
     *
     * @param data
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventMsg data) {
        Log.d(TAG, "on event:" + data.toString());
        if (data.getCmd() == EventMsg.CMD_ORDER_GRAB_SUCCESS) {
            //抢单成功,页面销毁
            finish();
        } else if (data.getCmd() == EventMsg.CMD_ORDER_CANCELED ||
                data.getCmd() == EventMsg.CMD_ORDER_STATUS_TO_CONTROVERSY ||
                data.getCmd() == EventMsg.CMD_ORDER_ADDRESS_HAS_CHANGED) {
            //订单取消/支付争议/地址改变，无法判断是否与当前界面有关，直接刷新当前数据
            refreshOrderDetails();
        }
    }

    @Subscribe
    public void onCarpoolOrderPaid(CarpoolOrderPaid order) {
        if (carpoolFlag && TextUtils.equals(orderNum, order.getCarpoolCode())) {
            finish(); // 当前拼车单已提交账单，关闭本页面
        }
    }


    @Override
    public View getInfoContents(Marker arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public View getInfoWindow(Marker arg0) {
        // TODO Auto-generated method stub
        TextView textView = new TextView(this);
        textView.setText(arg0.getTitle());
        return textView;
    }

    @Override
    public void onInfoWindowClick(Marker arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onMarkerClick(Marker arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onMapClick(LatLng arg0) {
        // TODO Auto-generated method stub

    }


    @Override
    protected void onResume() {
        super.onResume();
        refreshOrderDetails();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void showCarpoolOrderDetails(CarpoolOrderExt orderInfo) {
        this.carpoolOrder = orderInfo;
        String orderTime = orderInfo.getOrderTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        Date orderDate = new Date();
        try {
            orderDate = dateFormat.parse(orderTime);
            if (orderDate.getTime() - System.currentTimeMillis() < 12 * 3600 * 1000) {
                tvTransferOrder.setVisibility(View.GONE);
            } else {
                tvTransferOrder.setVisibility(View.VISIBLE);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        int thisYear = calendar.get(Calendar.YEAR);
        calendar.setTime(orderDate);
        int orderYear = calendar.get(Calendar.YEAR);
        if (orderYear == thisYear) {
            dateFormat = new SimpleDateFormat("MM月dd日", Locale.getDefault());
        } else {
            dateFormat = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());
        }
        tvOrderTimeDays.setText(dateFormat.format(orderDate));
        tvOderTimeHour.setText(orderTime.substring(11, 16));

        tvOrderGetOn.setText(orderInfo.getStartAddr().getName());
        tvOrderGetOff.setText(orderInfo.getEndAddr().getName());

        StringBuilder nameBuilder = new StringBuilder();
        for (int i = 0; i < orderInfo.getChildList().size(); i++) {
            if (i != 0) {
                nameBuilder.append("；");
            }
            nameBuilder.append(orderInfo.getChildList().get(i).getName());
        }
        tvChildNames.setText(nameBuilder);

        tvMessage.setText(TextUtils.join("；", orderInfo.getParentMessageList()));

        showCarpoolingAddress(orderInfo.getAddressList());

        tvOrderType.setText(OrderTypeInfo.getStatusText(orderInfo.getOrderType()));
//        tvOrderNumber.setText(orderInfo.getOrderCode());
        tvOrderNumber.setText(TextUtils.join("\n", orderInfo.getOrderCodes()));
    }

    private void showCarpoolingAddress(List<String> addresses) {
        if (addresses == null || addresses.isEmpty()) {
            return;
        }
        carpoolAddressContainer.removeAllViews();
        for (int i = 0; i < addresses.size(); i++) {
            View itemView = View.inflate(getContext(), R.layout.item_carpooling_address, null);
            TextView details = itemView.findViewById(R.id.details);
            View line = itemView.findViewById(R.id.line);
            details.setText(addresses.get(i));
            if (i == addresses.size() - 1) {
                line.setVisibility(View.GONE);
            }
            carpoolAddressContainer.addView(itemView);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}

