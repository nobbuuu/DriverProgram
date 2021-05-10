package com.haylion.android.data.base;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;


import com.haylion.android.data.qualifier.ActionType;

import butterknife.ButterKnife;
import io.nlopez.smartadapters.views.BindableFrameLayout;

public abstract class BaseItemView<T> extends BindableFrameLayout<T> {

    public BaseItemView(Context context) {
        super(context);
    }

    @Override
    public void onViewInflated() {
        ButterKnife.bind(this);
        if (getOrientation() == LinearLayout.HORIZONTAL) {
            setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        } else {
            setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }

    @RecyclerView.Orientation
    protected abstract int getOrientation();

    /**
     * 偶数行（列）
     */
    protected boolean isEvenPos() {
        return (getPosition() & 1) == 0;
    }

    /**
     * 奇数行（列）
     */
    protected boolean isOddPos() {
        return !isEvenPos();
    }

    protected void listenShortClick() {
        setOnClickListener(v -> notifyItemAction(ActionType.CLICK_TYPE_SHORT_CLICKED));
    }

}
