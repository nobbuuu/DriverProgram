package com.haylion.android.customview;

import android.content.Context;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DistanceResult;
import com.amap.api.services.route.DistanceSearch;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.haylion.android.R;
import com.haylion.android.calendar.DateFormatUtil;
import com.haylion.android.calendar.DateStyle;
import com.haylion.android.common.Const;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.util.BusinessUtils;
import com.haylion.android.data.util.StringUtil;
import com.haylion.android.dialog.ChoicePhoneDialog;
import com.haylion.android.mvp.util.ToastUtils;
import com.haylion.android.utils.AmapUtils;
import com.haylion.android.utils.SpUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailTopStartOrderView extends RelativeLayout {

    @BindView(R.id.tv_arrive_time)
    TextView tvArriveTime;
    @BindView(R.id.carpooling_order)
    TextView carpoolingOrder;
    @BindView(R.id.taketime_tv)
    TextView taketimeTv;
    @BindView(R.id.instance_fromme)
    TextView instanceFromme;
    @BindView(R.id.tv_start_addr)
    TextView tvStartAddr;
    @BindView(R.id.tv_arrive_addr)
    TextView tvArriveAddr;
    @BindView(R.id.rl_top_lay)
    ConstraintLayout rlTopLay;
    @BindView(R.id.rl_info_top)
    RelativeLayout rlInfoTop;
    @BindView(R.id.rl_middle)
    RelativeLayout rlMiddle;
    @BindView(R.id.order_type_fixed)
    TextView orderTypeFixed;
    @BindView(R.id.order_type)
    TextView orderType;
    @BindView(R.id.rl_order_type)
    RelativeLayout rlOrderType;
    @BindView(R.id.tv_order_number_fixed)
    TextView tvOrderNumberFixed;
    @BindView(R.id.tv_order_number)
    TextView tvOrderNumber;
    @BindView(R.id.rl_order_number)
    RelativeLayout rlOrderNumber;
    @BindView(R.id.tv_phones)
    TextView tvPhones;
    @BindView(R.id.tv_contact_type)
    TextView tvContactType;
    @BindView(R.id.tv_contact_passenger)
    TextView tvContactPassenger;
    @BindView(R.id.ll_contact_info)
    LinearLayout llContactInfo;
    @BindView(R.id.order_price)
    TextView orderPrice;
    @BindView(R.id.price_unit)
    TextView priceUnit;
    @BindView(R.id.order_money)
    TextView orderMoney;
    @BindView(R.id.takecode_tv)
    TextView takecodeTv;
    @BindView(R.id.expand_lay)
    LinearLayout expandLay;
    @BindView(R.id.expand_iv)
    ImageView expandIv;
    @BindView(R.id.rl_order_content)
    RelativeLayout rlOrderContent;
    @BindView(R.id.sf_orderdetail_top_lay)
    RelativeLayout sfOrderdetailTopLay;
    private boolean expand;

    public DetailTopStartOrderView(Context context, Order order) {
        super(context);
        View view = inflate(context, R.layout.view_detail_top_startorder, this);
        ButterKnife.bind(this, view);
        if (order != null) {
            tvArriveTime.setText("预约" + order.getEstimateArriveTime() + "送达");
            String startTime = order.getStartTime();
            String endTime = order.getEndTime();
            long l = System.currentTimeMillis();
            String curTime = DateFormatUtil.getTime(l, DateStyle.YYYY_MM_DD.getValue());
            if (startTime.equals(curTime)){
                startTime = "今日";
            }
            SpannableString takeSpan = StringUtil.setTextPartSizeColor(startTime + " ", order.getOrderTime() + " ", " 取货", R.color.part_text_bg);
            taketimeTv.setText(takeSpan);
            tvStartAddr.setText(order.getStartAddr().getName());
            tvArriveAddr.setText(order.getEndAddr().getName());
            orderMoney.setText(order.getTotalMoney()/100 + "");
            String pickupCode = order.getPickupCode();
            if (!TextUtils.isEmpty(pickupCode)) {
                takecodeTv.setText("取货码：" + pickupCode);
            }
            String location_lat = (String) SpUtils.getParam(Const.CUR_LATITUTE, "0");
            String location_long = (String) SpUtils.getParam(Const.CUR_LONGITUDE, "0");
            if (!location_lat.equals("0") && !location_long.equals("0")) {
                AmapUtils.caculateDistance(new LatLng(Double.valueOf(location_lat), Double.valueOf(location_long)), order.getStartAddr().getLatLng(), new RouteSearch.OnRouteSearchListener() {
                    @Override
                    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

                    }

                    @Override
                    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {
                        float distance = driveRouteResult.getPaths().get(0).getDistance();
                        instanceFromme.setText("距你" + BusinessUtils.formatDistance(distance));
                    }

                    @Override
                    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

                    }

                    @Override
                    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

                    }
                });
            }
        }

        expandIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!expand) {
                    expandLay.setVisibility(View.VISIBLE);
                    expandIv.setImageResource(R.mipmap.uparrow);
                } else {
                    expandLay.setVisibility(View.GONE);
                    expandIv.setImageResource(R.mipmap.downarrow);
                }
                expand = !expand;
            }
        });
        tvContactPassenger.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String mobile = order.getPickupContactMobile();
                String mobile1 = order.getPickupContactMobile1();
                String mobile2 = order.getPickupContactMobile2();
                if (mobile.isEmpty() && mobile1.isEmpty() && mobile2.isEmpty()){
                    ToastUtils.showLong(getContext(),"暂无电话数据");
                    return;
                }
                new ChoicePhoneDialog(getContext(),order).show();
            }
        });
    }

    public DetailTopStartOrderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
}
