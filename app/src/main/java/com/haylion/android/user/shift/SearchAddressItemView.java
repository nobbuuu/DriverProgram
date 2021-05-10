package com.haylion.android.user.shift;

import android.content.Context;
import android.view.View;
import android.widget.TextView;


import com.amap.api.services.core.PoiItem;
import com.haylion.android.R;
import com.haylion.android.data.base.BaseItemView;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

public class SearchAddressItemView extends BaseItemView<PoiItem> {

    @BindView(R.id.address_title)
    TextView addressTitle;
    @BindView(R.id.address_desc)
    TextView addressDesc;

    public SearchAddressItemView(Context context) {
        super(context);
    }

    @Override
    protected int getOrientation() {
        return RecyclerView.VERTICAL;
    }

    @Override
    public int getLayoutId() {
        return R.layout.search_address_item;
    }

    @Override
    public void bind(PoiItem poiItem) {
        setOnClickListener(v -> notifyItemAction(ACTION_SELECT_ADDRESS));
        addressTitle.setText(poiItem.getTitle());
        addressDesc.setText(poiItem.getSnippet());
    }

    public static final int ACTION_SELECT_ADDRESS = 0x365;

}
