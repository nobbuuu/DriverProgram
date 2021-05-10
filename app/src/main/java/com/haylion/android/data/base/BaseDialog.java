package com.haylion.android.data.base;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;


import com.haylion.android.mvp.util.SizeUtil;
import com.haylion.android.mvp.util.ToastUtils;

import butterknife.ButterKnife;

public class BaseDialog extends Dialog {

    protected Context mContext;

    private OnShowListener mOnShowListener;

    private int mWidth;
    private int mHeight;

    public BaseDialog(@NonNull Context context) {
        this(context, 0, 0);
    }

    public BaseDialog(@NonNull Context context, int width, int height) {
        this(context, width, height, 0);
    }

    public BaseDialog(@NonNull Context context, int themeResId) {
        this(context, 0, 0, themeResId);
    }

    public BaseDialog(@NonNull Context context, int width, int height, int themeResId) {
        super(context, themeResId);
        if (width == 0) {
            this.mWidth = (int) (SizeUtil.getScreenWidth(context) * 0.8);
        } else {
            this.mWidth = width;
        }
        if (height == 0) {
            this.mHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        } else {
            this.mHeight = height;
        }
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public final void setContentView(int layoutResId) {
        super.setContentView(layoutResId);
        initDialogWindow();
    }

    @Override
    public final void setContentView(@NonNull View view) {
        super.setContentView(view);
        initDialogWindow();
    }

    private void initDialogWindow() {
        ButterKnife.bind(this);
        super.setOnShowListener(dialog -> {
            Window window = getWindow();
            if (window != null) {
                window.setBackgroundDrawableResource(android.R.color.transparent);
                window.setLayout(mWidth, mHeight);
            }
            if (mOnShowListener != null) {
                mOnShowListener.onShow(dialog);
            }
        });
        didCreateView();
    }

    @Override
    public final void setOnShowListener(@Nullable OnShowListener listener) {
        this.mOnShowListener = listener;
    }

    protected void didCreateView() {

    }

    protected final String getString(@StringRes int idRes) {
        return mContext.getString(idRes);
    }

    protected final String getString(@StringRes int resId, Object... formatArgs) {
        return mContext.getString(resId, formatArgs);
    }

    protected final Drawable getDrawable(@DrawableRes int drawableRes) {
        return mContext.getDrawable(drawableRes);
    }

    protected void toast(CharSequence msg) {
        ToastUtils.showShort(getContext(), msg);
    }

    //todo
    public void toast(@StringRes int msgRes) {
        ToastUtils.showShort(getContext(), msgRes);
    }


}
