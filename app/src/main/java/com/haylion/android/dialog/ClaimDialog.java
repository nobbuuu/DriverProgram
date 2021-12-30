package com.haylion.android.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.text.SpannableString;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DistanceResult;
import com.amap.api.services.route.DistanceSearch;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.haylion.android.R;
import com.haylion.android.adapter.ChosedDateAdapter;
import com.haylion.android.calendar.DateFormatUtil;
import com.haylion.android.calendar.DateStyle;
import com.haylion.android.common.Const;
import com.haylion.android.constract.ClaimActionListener;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.util.BusinessUtils;
import com.haylion.android.data.util.StringUtil;
import com.haylion.android.utils.AmapUtils;
import com.haylion.android.utils.SpUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClaimDialog extends Dialog {

    private TextView sureTv;

    public ClaimDialog(@NonNull Context context, Order order, Map<Long, Boolean> selectMap) {
        super(context, R.style.ActionSheetDialogStyle);
        setContentView(R.layout.dialog_claimorder);
        sureTv = findViewById(R.id.sure_tv);
        TextView cancelTv = findViewById(R.id.cancel_tv);
        TextView tv_arrive = findViewById(R.id.tv_arrive);
        TextView taketime_tv = findViewById(R.id.taketime_tv);
        TextView tv_start_address = findViewById(R.id.tv_start_address);
        TextView tv_end_address = findViewById(R.id.tv_end_address);
        TextView takekm_tv = findViewById(R.id.takekm_tv);
        GridView claimdate_gv = findViewById(R.id.claimdate_gv);
        View line2 = findViewById(R.id.line2);
        if (order != null) {
            tv_arrive.setText("预约" + order.getDeliveryTime() + "送达");
            tv_start_address.setText(order.getStartAddr().getName());
            tv_end_address.setText(order.getEndAddr().getName());
            AmapUtils.caculateDistance(order.getStartAddr().getLatLng(), order.getEndAddr().getLatLng(), new RouteSearch.OnRouteSearchListener() {
                @Override
                public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

                }

                @Override
                public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {
                    float distance = driveRouteResult.getPaths().get(0).getDistance();
                    takekm_tv.setText("取送距离" + BusinessUtils.formatDistance(distance));
                }

                @Override
                public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

                }

                @Override
                public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

                }
            });

            List<String> orderDates = order.getOrderDates();
            if (orderDates != null && orderDates.size() > 1) {
                line2.setVisibility(View.VISIBLE);
                claimdate_gv.setVisibility(View.VISIBLE);
                if (selectMap != null) {
                    List<String> dates = new ArrayList<>();
                    for (Map.Entry<Long, Boolean> map : selectMap.entrySet()) {
                        if (map.getValue()) {
                            String day = DateFormatUtil.getTime(map.getKey(), "MM月dd日");
                            dates.add(day);
                        }
                    }
                    claimdate_gv.setAdapter(new ChosedDateAdapter(getContext(), dates, R.layout.gvitem_claimdate));
                }
                SpannableString takeSpan = StringUtil.setTextPartSizeColor("每日 ", order.getTakeTime(), " 取货", R.color.part_text_bg);
                taketime_tv.setText(takeSpan);
            } else {
                line2.setVisibility(View.GONE);
                claimdate_gv.setVisibility(View.GONE);
                String endTime = order.getEndTime();
                String curTime = DateFormatUtil.getTime(System.currentTimeMillis(), DateStyle.YYYY_MM_DD.getValue());
                String takeTimeStr = endTime;
                if (curTime.equals(endTime)){
                    takeTimeStr = "今日";
                }
                SpannableString takeSpan = StringUtil.setTextPartSizeColor(takeTimeStr+" ", order.getTakeTime(), " 取货", R.color.part_text_bg);
                taketime_tv.setText(takeSpan);
            }
        }
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams attr = window.getAttributes();
            if (attr != null) {
                attr.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                attr.width = ViewGroup.LayoutParams.MATCH_PARENT;
                attr.gravity = Gravity.CENTER;
                window.setAttributes(attr);
            }
        }
        cancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        sureTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if (mActionListener != null) {
                    mActionListener.onClaim();
                }
            }
        });
    }

    public ClaimDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public void showDialog() {
        show();
        mHandler.removeCallbacks(mRunnable);
        mHandler.post(mRunnable);
    }

    private int time = 10;
    private Handler mHandler = new Handler();
    private ClaimActionListener mActionListener;
    public void setClaimListaner(ClaimActionListener listaner) {
        mActionListener = listaner;
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (time > 0) {
                sureTv.setText("确认抢单(" + time + "秒)");
                time--;
                mHandler.postDelayed(this, 1000);
            } else {
                if (isShowing()) {
                    dismiss();
                    mHandler.removeCallbacks(this);
                }
            }
        }
    };

}
