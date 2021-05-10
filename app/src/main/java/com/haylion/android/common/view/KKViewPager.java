package com.haylion.android.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.haylion.android.mvp.util.LogUtils;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

public class KKViewPager extends ViewPager {

    private boolean paging = false;

    public KKViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * disable smoothly scroll
     */
    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item, false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            return this.paging && super.onTouchEvent(event);
        } catch (IllegalArgumentException ex) {
            LogUtils.e(ex);
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        try {
            return this.paging && super.onInterceptTouchEvent(event);
        } catch (IllegalArgumentException ex) {
            LogUtils.e(ex);
        }
        return false;
    }

    @Override
    public boolean executeKeyEvent(@NonNull KeyEvent event) {
        return this.paging && super.executeKeyEvent(event);
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (Math.abs(dx) > 50) {
            return super.canScroll(v, checkV, dx, x, y);
        } else {
            return true;
        }
    }

    /**
     * Enable or disable navigation
     */
    public void setPagingEnabled(boolean paging) {
        this.paging = paging;
    }


}
