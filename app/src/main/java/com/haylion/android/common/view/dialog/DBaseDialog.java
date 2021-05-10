package com.haylion.android.common.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haylion.android.R;


/**
 * Created by dengzh on 2017/11/2.
 * Dialog基类
 */

public class DBaseDialog {

    protected Context mContext;
    private SparseArray<View> mViews;

    //Dialog的设置
    private Dialog mDialog;
    private boolean cancelOnTouchOutside;  //是否触摸外部取消弹窗
    protected int layoutResId;             //布局资源文件
    protected int gravity = Gravity.CENTER;  //显示方式，默认居中
    protected boolean isUp;                //是否从下方弹出


    /**
     * @param mContext
     * @param layoutResId   资源文件
     * @param gravity       显示方式
     * @param cancelOnTouchOutside  是否触摸外部取消
     */
    public DBaseDialog(Context mContext, int layoutResId, int gravity, boolean cancelOnTouchOutside) {
        this(mContext,layoutResId,gravity,cancelOnTouchOutside,false);
    }

    /**
     * @param mContext
     * @param layoutResId 布局id
     * @param gravity     位置
     * @param cancelOnTouchOutside  是否点击外部取消
     * @param isUp        从下往上动画
     */
    public DBaseDialog(Context mContext, int layoutResId, int gravity, boolean cancelOnTouchOutside, boolean isUp) {
        this.mContext = mContext;
        this.layoutResId = layoutResId;
        this.gravity = gravity;
        this.cancelOnTouchOutside = cancelOnTouchOutside;
        this.isUp = isUp;
        mViews = new SparseArray<>();
        initView();
    }

    private void initView(){
        mDialog = new Dialog(mContext, R.style.Translucent_NoTitle); //创建半透明背景的Dialog
        mDialog.setContentView(layoutResId);   //设置布局id
        mDialog.setCanceledOnTouchOutside(cancelOnTouchOutside);  //是否可以触摸外部取消
        mDialog.getWindow().setGravity(gravity);  //显示位置
        if(!isUp){
            //常规宽高适配
            WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
            params.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            mDialog.getWindow().setAttributes(params);
        }else{
            // 设置显示动画
            mDialog.getWindow().setWindowAnimations(R.style.main_menu_animstyle);
            //宽填满，高适配
            WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
            params.width = LinearLayout.LayoutParams.MATCH_PARENT;
            params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            mDialog.getWindow().setAttributes(params);
        }
    }

    /**
     * 取消蓝色线 4.4系统出现蓝线
     */
    private void cancelDialogTitleLineColor(){
        int divierId = mContext.getResources().getIdentifier("android:id/titleDivider", null, null);
        View divider = mDialog.findViewById(divierId);
        if(divider != null){
            divider.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    /**
     * 通过id寻找控件
     * @param viewId
     * @param <T>
     * @return
     */
    public <T extends View>T getView(int viewId){
        View view = mViews.get(viewId);
        if(view == null){
            view = mDialog.findViewById(viewId);
            mViews.put(viewId,view);
        }
        return (T) view;
    }

    /**
     * 切换dialog
     */
    public void toggleDialog(){
        if(mDialog!=null){
            if(mDialog.isShowing()){
                mDialog.dismiss();
            }else{
                mDialog.show();
            }
        }
    }


    public DBaseDialog setText(int viewId,int resId){
        TextView textView = getView(viewId);
        textView.setText(resId);
        return this;
    }

    public DBaseDialog setText(int viewId,String textContent){
        TextView textView = getView(viewId);
        textView.setText(textContent);
        return this;
    }

    public DBaseDialog setOnViewClick(int viewId, View.OnClickListener listener){
        if(listener!=null){
            getView(viewId).setOnClickListener(listener);
        }
        return this;
    }

    /**
     * 设置对话框宽度填满父控件
     */
    public void setMatchWidthDialog(){
        WindowManager.LayoutParams params = mDialog.getWindow().getAttributes();
        params.width = LinearLayout.LayoutParams.MATCH_PARENT;
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        mDialog.getWindow().setAttributes(params);
    }

    public boolean isShow(){
        return mDialog.isShowing();
    }

    public Dialog getDialog(){
        return mDialog;
    }


    /**
     * 控制返回键是否能取消dialog
     * @param flag
     */
    public void setCancelable(boolean flag){
        mDialog.setCancelable(flag);
    }

}
