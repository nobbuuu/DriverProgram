package com.haylion.android.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * @author dengzh
 * @date 2019/12/30
 * Description: 听单页 货拼客单容器
 * 因为卡片作为一个整体点击效果，所以不允许向下继续事件分发。
 */
public class OrderCargoContainerView extends RelativeLayout {

    private boolean isInterceptTouchEvent = false;

    public OrderCargoContainerView(Context context) {
        super(context);
    }

    public OrderCargoContainerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OrderCargoContainerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }



    public void setInterceptTouchEvent(boolean flag){
        isInterceptTouchEvent = flag;
    }

    /**
     * 是否拦截点击事件
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(isInterceptTouchEvent){
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }
}
