package com.haylion.android.customview;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

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
import com.haylion.android.orderdetail.amapNavi.AMapNaviViewActivity;
import com.haylion.android.utils.AmapUtils;
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

    public DetailTopPickGoodsView(Context context, Order order) {
        super(context);
        View view = inflate(context, R.layout.view_detail_top_pickgoods, this);
        ButterKnife.bind(this, view);

        if (order != null) {
            tvStartAddr.setText(order.getStartAddr().getName());
            String location_lat = (String) SpUtils.getParam(Const.CUR_LATITUTE, "0");
            String location_long = (String) SpUtils.getParam(Const.CUR_LONGITUDE, "0");
            if (!location_lat.equals("0") && !location_long.equals("0")) {
                AmapUtils.caculateDistance(new LatLng(Double.valueOf(location_lat), Double.valueOf(location_long)), order.getStartAddr().getLatLng(), new RouteSearch.OnRouteSearchListener() {
                    @Override
                    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

                    }

                    @Override
                    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {
                        DrivePath drivePath = driveRouteResult.getPaths().get(0);
                        if (drivePath != null) {
                            float distance = drivePath.getDistance();
                            remainTv.setText("剩余" + BusinessUtils.formatDistance(distance) + " " + AmapUtils.matchTime(drivePath.getDuration()));
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
            taketimeTip.setText("请在" + order.getOrderTime() + "前到达取货点");
            if (!TextUtils.isEmpty(order.getPickupCode())) {
                pickcodeTv.setText("取货码：" + order.getPickupCode());
            }
        }

        navTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                AMapNaviViewActivity.go(context, order.getOrderId(),-1);
            }
        });
    }

    public DetailTopPickGoodsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
}
