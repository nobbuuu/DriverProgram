package com.haylion.android.customview;

import android.content.Context;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.haylion.android.R;
import com.haylion.android.common.Const;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.util.BusinessUtils;
import com.haylion.android.data.util.StringUtil;
import com.haylion.android.orderdetail.amapNavi.AMapNaviViewActivity;
import com.haylion.android.utils.AmapUtils;
import com.haylion.android.utils.DateUtils;
import com.haylion.android.utils.SpUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailTopPickGoodsView extends RelativeLayout {


    @BindView(R.id.tv_start_addr)
    TextView tvStartAddr;
    @BindView(R.id.remain_tv)
    TextView remainTv;
    @BindView(R.id.line1)
    View line1;
    @BindView(R.id.taketime_tip)
    TextView taketimeTip;
    @BindView(R.id.line2)
    View line2;
    @BindView(R.id.pickcode_tv)
    TextView pickcodeTv;
    @BindView(R.id.nav_tv)
    TextView navTv;
    @BindView(R.id.nav_tv1)
    TextView navTv1;
    @BindView(R.id.botLay)
    ConstraintLayout botLay;

    public DetailTopPickGoodsView(Context context, Order order) {
        super(context);
        View view = inflate(context, R.layout.view_detail_top_pickgoods, this);
        ButterKnife.bind(this, view);

        if (order != null) {
            String location_lat = (String) SpUtils.getParam(Const.CUR_LATITUTE, "0");
            String location_long = (String) SpUtils.getParam(Const.CUR_LONGITUDE, "0");
            LatLng endLnt = null;
            if (order.getOrderStatus() < 4) {
                endLnt = order.getStartAddr().getLatLng();
            } else {
                endLnt = order.getEndAddr().getLatLng();
            }
            if (!location_lat.equals("0") && !location_long.equals("0")) {
                AmapUtils.caculateDistance(new LatLng(Double.valueOf(location_lat), Double.valueOf(location_long)), endLnt, new RouteSearch.OnRouteSearchListener() {
                    @Override
                    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

                    }

                    @Override
                    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {
                        DrivePath drivePath = driveRouteResult.getPaths().get(0);
                        if (drivePath != null) {
                            float distance = drivePath.getDistance();
                            remainTv.setText("剩余" + BusinessUtils.formatDistance(distance) + " " + DateUtils.getTimeLenthStr(drivePath.getDuration() * 1000));
                        }
                    }

                    @Override
                    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

                    }

                    @Override
                    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

                    }
                });
            }
            if (order.getOrderStatus() == 1) {
                SpannableString spannableString = StringUtil.setTextPartSizeColor("请在", order.getTakeTime(), "前到达取货点", R.color.part_text_bg);
                taketimeTip.setText(spannableString);
                tvStartAddr.setText(order.getStartAddr().getName());
            }
            if (order.getOrderStatus() == 4) {
                SpannableString spannableString = StringUtil.setTextPartSizeColor("请在", order.getDeliveryTime(), "前送达", R.color.part_text_bg);
                taketimeTip.setText(spannableString);
                navTv.setVisibility(VISIBLE);
                botLay.setVisibility(GONE);
                tvStartAddr.setText(order.getEndAddr().getName());
            }
            if (!TextUtils.isEmpty(order.getPickupCode())) {
                pickcodeTv.setText("取货码：" + order.getPickupCode());
            }
        }

        navTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                AMapNaviViewActivity.go(context, order.getOrderId(), Order.ORDER_TYPE_SHUNFENG);
            }
        });
        navTv1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                AMapNaviViewActivity.go(context, order.getOrderId(), Order.ORDER_TYPE_SHUNFENG);
            }
        });
    }

    public DetailTopPickGoodsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
}
