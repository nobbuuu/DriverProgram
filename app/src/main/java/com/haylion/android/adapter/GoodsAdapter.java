package com.haylion.android.adapter;

import android.content.Context;
import android.view.View;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.route.DistanceResult;
import com.amap.api.services.route.DistanceSearch;
import com.haylion.android.R;
import com.haylion.android.common.Const;
import com.haylion.android.constract.ItemClickListener;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.util.BusinessUtils;
import com.haylion.android.utils.AmapUtils;
import com.haylion.android.utils.SpUtils;

import java.util.List;

public class GoodsAdapter extends RVBaseAdapter<String> {
    public GoodsAdapter(Context context, List<String> mDatas, int itemLayoutId) {
        super(context, mDatas, itemLayoutId);
    }

    @Override
    public void onBind(RVBaseHolder holder, String data, int position) {
        holder.setText(R.id.goodscode_tv,"货物编码："+data);
    }

    private ItemClickListener mItemClickListener;
    public void setItemClickListener(ItemClickListener listener) {
        mItemClickListener = listener;
    }

}
