package com.haylion.android.adapter;

import android.content.Context;

import com.haylion.android.R;

import java.util.List;

public class ChosedDateAdapter extends CommonAdapter<String> {
    public ChosedDateAdapter(Context context, List<String> mDatas, int itemLayoutId) {
        super(context, mDatas, itemLayoutId);
    }

    @Override
    public void onBind(BaseViewHolder holder, String s, int position) {
        holder.setText(R.id.chosed_date,s);
    }
}
