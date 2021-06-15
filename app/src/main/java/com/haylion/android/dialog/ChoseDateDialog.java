package com.haylion.android.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.haylion.android.R;
import com.haylion.android.calendar.CalendarView;
import com.haylion.android.constract.ChoseDateCallBack;

import java.util.Calendar;
import java.util.List;

public class ChoseDateDialog extends Dialog {
    private ChoseDateCallBack mCallBack;

    public void setCallBack(ChoseDateCallBack callBack) {
        mCallBack = callBack;
    }

    public ChoseDateDialog(@NonNull Context context, List<String> orderDates) {
        super(context, R.style.ActionSheetDialogStyle);
        setContentView(R.layout.dialog_calender_view);
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
        CalendarView calendar_view = findViewById(R.id.calendar_view);
        TextView date_title = findViewById(R.id.date_title);
        TextView sure_tv = findViewById(R.id.sure_tv);
        TextView cancel_tv = findViewById(R.id.cancel_tv);
        ImageView pre_iv = findViewById(R.id.pre_iv);
        ImageView next_iv = findViewById(R.id.next_iv);
        calendar_view.setMarkDates(orderDates);
        calendar_view.setTitle(new CalendarView.TitleListener() {
            @Override
            public void setTitle(String titleStr, Calendar calendar) {
                date_title.setText(titleStr);
            }
        });

        pre_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar_view.toPrevMonth();
            }
        });
        next_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar_view.toNextMonth();
            }
        });
        cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        sure_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCallBack != null){
                    mCallBack.callBack(calendar_view.getSelectMap());
                }
            }
        });

    }

    public ChoseDateDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

}
