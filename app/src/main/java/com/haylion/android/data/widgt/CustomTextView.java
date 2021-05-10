package com.haylion.android.data.widgt;


import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.haylion.android.Constants;


/**
 * @author dengzh
 * @date 2019/11/18
 * Description: 设置字体的TextView
 */
public class CustomTextView extends AppCompatTextView {
    public CustomTextView(Context context) {
        super(context);
        init();
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //加粗的改变字体
        if(getTypeface().isBold()){
            Typeface tf = Typeface.create(Constants.TEXT_BOLD_TYPEFACE,Typeface.BOLD);
            setTypeface(tf);
        }

        //单独设置第三方字体如下
       /* if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "Roboto-Light.ttf");
            setTypeface(tf);
        }*/
    }

}
