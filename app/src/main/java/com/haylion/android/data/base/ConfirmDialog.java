package com.haylion.android.data.base;


import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.StringRes;

import com.haylion.android.MyApplication;
import com.haylion.android.R;
import com.haylion.android.data.util.ResourceUtil;
import com.haylion.android.mvp.base.BaseDialogFragment;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * 确认弹窗
 * 全局统一
 * 2019/11/5
 */
public class ConfirmDialog extends BaseDialogFragment {

    public static final int TYPE_TWO_BUTTON = 0;  //两个按钮
    public static final int TYPE_ONE_BUTTON = 1;  //一个按钮

    @BindView(R.id.tv_message)
    TextView mMessage;
    @BindView(R.id.btn_positive)
    TextView mPositiveText;
    @BindView(R.id.btn_negative)
    TextView mNegativeText;
    @BindView(R.id.lineView)
    View lineView;

    private OnClickListener mClickListener;

    private CharSequence message;
    private CharSequence positiveText;  //确认文字
    private CharSequence negativeText;  //取消文字
    private int type;   //0-显示两个按钮  1-显示一个按钮
    private boolean cancelOutside = true; //是否可以外部取消，默认true
    private boolean clickDismiss = true;  //是否点击按钮，自动取消弹窗
    private int gravity = Gravity.LEFT;   //内容显示规则


    public static ConfirmDialog newInstance() {
        return new ConfirmDialog();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (message != null) {
            mMessage.setText(message);
        }
        if (negativeText != null) {
            mNegativeText.setText(negativeText);
        }
        if (positiveText != null) {
            mPositiveText.setText(positiveText);
        }
        if(type == TYPE_ONE_BUTTON){
            mNegativeText.setVisibility(View.GONE);
            lineView.setVisibility(View.GONE);
        }
        mMessage.setGravity(gravity);
        Objects.requireNonNull(getDialog()).setCanceledOnTouchOutside(cancelOutside);
        if(!cancelOutside){
            getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if(keyCode == KeyEvent.KEYCODE_BACK ){
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.dialog_confirm;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if(mClickListener!=null){
            mClickListener.onDismiss();
        }
        super.onDismiss(dialog);
    }

    public ConfirmDialog setMessage(CharSequence message) {
        this.message = message;
        return this;
    }

    public ConfirmDialog setMessage(@StringRes int message) {
        this.message = ResourceUtil.getString(MyApplication.getContext(), message);
        return this;
    }

    public ConfirmDialog setPositiveText(CharSequence positiveText) {
        this.positiveText = positiveText;
        return this;
    }

    public ConfirmDialog setPositiveText(@StringRes int positiveText) {
        this.positiveText = ResourceUtil.getString(MyApplication.getContext(), positiveText);
        return this;
    }

    public ConfirmDialog setNegativeText(CharSequence negativeText) {
        this.negativeText = negativeText;
        return this;
    }

    public ConfirmDialog setNegativeText(@StringRes int negativeText) {
        this.negativeText = ResourceUtil.getString(MyApplication.getContext(), negativeText);
        return this;
    }

    /**
     * 设置显示几个按钮
     * @param type  0-显示两个按钮  1-显示一个按钮
     * @return
     */
    public ConfirmDialog setType(int type) {
        this.type = type;
        return this;
    }

    /**
     * 设置是否点击button 执行 dimiss()
     * @param clickDismiss
     * @return
     */
    public ConfirmDialog setClickDismiss(boolean clickDismiss) {
        this.clickDismiss = clickDismiss;
        return this;
    }

    /**
     * 设置文字的  gravity
     * @param gravity
     * @return
     */
    public ConfirmDialog setGravity(int gravity) {
        this.gravity = gravity;
        return this;
    }

    /**
     * 设置外部是否可以取消，为false时会重写返回物理键
     * @param cancelOutside
     * @return
     */
    public ConfirmDialog setCancelOutside(boolean cancelOutside) {
        this.cancelOutside = cancelOutside;
        return this;
    }

    //todo
    @OnClick({R.id.btn_negative, R.id.btn_positive})
    public void onClick(View view) {
        if (mClickListener != null) {
            if (view.getId() == R.id.btn_negative) {
                mClickListener.onNegativeClick(view);
            } else if (view.getId() == R.id.btn_positive) {
                mClickListener.onPositiveClick(view);
            }
        }
        if(clickDismiss){
            super.dismiss();
        }
    }

    public static class OnClickListener {
        public void onPositiveClick(View view) {

        }

        public void onNegativeClick(View view) {

        }

        public void onDismiss(){

        }
    }

    public ConfirmDialog setOnClickListener(OnClickListener clickListener) {
        mClickListener = clickListener;
        return this;
    }

}
