package com.haylion.android.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.haylion.android.R;
import com.haylion.android.calendar.DateFormatUtil;
import com.haylion.android.common.Const;
import com.haylion.android.constract.ClaimActionListener;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.util.StringUtil;
import com.haylion.android.utils.AmapUtils;
import com.haylion.android.utils.DateUtils;
import com.haylion.android.utils.SpUtils;

import java.util.Date;

public class ScanEndDialog extends Dialog {

    private TextView sureTv;

    public ScanEndDialog(@NonNull Context context, String code,ClaimActionListener listaner) {
        super(context, R.style.ActionSheetDialogStyle);
        setContentView(R.layout.dialog_scanend);
        sureTv = findViewById(R.id.sure_tv);
        TextView contentTv = findViewById(R.id.contentTv);
        TextView cancel_tv = findViewById(R.id.cancel_tv);
        if (code != null) {
            contentTv.setText("货物编号：" +code);
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
                if (listaner != null){
                    listaner.onClaim();
                }
            }
        });
        cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public ScanEndDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }
}
