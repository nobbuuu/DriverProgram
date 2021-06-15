package com.haylion.android.adapter;

import android.content.Context;

import com.haylion.android.R;
import com.haylion.android.calendar.DensityUtil;
import com.haylion.android.data.bean.WeekBean;

import java.util.List;


public class WeekAdapter extends CommonAdapter<WeekBean> {
    public WeekAdapter(Context context, List<WeekBean> data, int layoutId) {
        super(context, data, layoutId);
    }

    @Override
    public void onBind(BaseViewHolder holder, WeekBean weekBean, int position) {
        holder.setText(R.id.only_tv, weekBean.getWeek());
    }

}