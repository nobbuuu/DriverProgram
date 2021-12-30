package com.haylion.android.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.text.SpannableString;
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
import com.haylion.android.utils.DateUtils;
import com.haylion.android.utils.SpUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class TimeoutDialog extends Dialog {

    private TextView sureTv;

    public TimeoutDialog(@NonNull Context context, Order order) {
        super(context, R.style.ActionSheetDialogStyle);
        setContentView(R.layout.dialog_timeout);
        sureTv = findViewById(R.id.sure_tv);
        TextView timeoutTv = findViewById(R.id.timeoutTv);
        if (order != null) {
            String curLatitute = (String) SpUtils.getParam(Const.CUR_LATITUTE, "0");
            String curLongtitute = (String) SpUtils.getParam(Const.CUR_LONGITUDE, "0");
            LatLng latLng = new LatLng(Double.valueOf(curLatitute), Double.valueOf(curLongtitute));
            AmapUtils.caculateDistance(latLng, order.getEndAddr().getLatLng(), new RouteSearch.OnRouteSearchListener() {
                @Override
                public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

                }

                @Override
                public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {
                    String endTime = order.getEndTime() + " " + order.getDeliveryTime();
                    Date time = DateFormatUtil.getTime(endTime, "yyyy-MM-dd HH:mm");
                    long timeOut = System.currentTimeMillis() - time.getTime() + driveRouteResult.getPaths().get(0).getDuration() * 1000;
                    String timeLenthStr = DateUtils.getTimeLenthStr(timeOut);
                    SpannableString spannableString = StringUtil.setTextPartSizeColor("本订单已到送货时间，预计超时", timeLenthStr, "送达", R.color.part_text_bg);
                    timeoutTv.setText(spannableString);
                }

                @Override
                public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

                }

                @Override
                public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

                }
            });
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
        sureTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public TimeoutDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

}
