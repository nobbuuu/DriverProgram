package com.haylion.android.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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
import com.haylion.android.constract.ItemClickListener;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.util.BusinessUtils;
import com.haylion.android.data.util.StringUtil;
import com.haylion.android.main.OrderInfoAdapter;
import com.haylion.android.utils.AmapUtils;
import com.haylion.android.utils.SpUtils;

import java.util.List;

public class MainShunfengAdapter extends RVBaseAdapter<Order> {
    public MainShunfengAdapter(Context context, List<Order> mDatas, int itemLayoutId) {
        super(context, mDatas, itemLayoutId);
    }

    @Override
    public void onBind(RVBaseHolder holder, Order order, int position) {
        holder.setText(R.id.tv_order_time, "预约" + order.getDeliveryTime() + "送达");
        String startTime = order.getStartTime();
        long l = System.currentTimeMillis();
        String curTime = DateFormatUtil.getTime(l, DateStyle.YYYY_MM_DD.getValue());
        if (startTime.equals(curTime)) {
            startTime = "今日";
        }else {
            String[] startSplit = startTime.split(" ");
            startTime = startSplit[0];
        }
        SpannableString takeSpan = StringUtil.setTextPartSizeColor(startTime + " ", order.getTakeTime(), " 取货", R.color.part_text_bg);
        TextView takeTv = holder.getView(R.id.order_status);
        takeTv.setText(takeSpan);
        holder.setText(R.id.tv_get_on_addr, order.getStartAddr().getName());
        holder.setText(R.id.tv_get_off_addr, order.getEndAddr().getName());
        holder.setText(R.id.order_money, order.getTotalMoney() / 100
                + "");
        AmapUtils.caculateDistance(order.getStartAddr().getLatLng(), order.getEndAddr().getLatLng(), new RouteSearch.OnRouteSearchListener() {
            @Override
            public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

            }

            @Override
            public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {
                float distance = driveRouteResult.getPaths().get(0).getDistance();
                holder.setText(R.id.tv_total_distance, "取送距离" + BusinessUtils.formatDistance(distance));
            }

            @Override
            public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

            }

            @Override
            public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

            }
        });
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
                    holder.setText(R.id.instance_fromme, "距你" + BusinessUtils.formatDistance(distance));
                }

                @Override
                public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

                }

                @Override
                public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

                }
            });
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(order);
                }
            }
        });
    }

    private ItemClickListener mItemClickListener;

    public void setItemClickListener(ItemClickListener listener) {
        mItemClickListener = listener;
    }

}
