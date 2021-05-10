package com.haylion.android.common.view.popwindow;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.haylion.android.R;

/**
 * Created by dengzh on 2018/4/18.
 * LayoutInflate理解
 * 1.activity 中方法 getLayoutInflater() 等价于其他地方调用  LayoutInflater.from(context)；
 * 2.以下三种方法调用理解
 * Inflate(resId , null ) 只创建temp ,返回temp
 * Inflate(resId , parent, false )创建temp，然后执行temp.setLayoutParams(params);返回temp
 * Inflate(resId , parent, true ) 创建temp，然后执行root.addView(temp, params);最后返回root
 * 3、解释
 * Inflate(resId , null)不能正确处理宽和高是因为：layout_width,layout_height是相对了父级设置的，必须与父级的LayoutParams一致。而此temp的getLayoutParams为null。
 * Inflate(resId , parent,false) 可以正确处理，因为temp.setLayoutParams(params);这个params正是root.generateLayoutParams(attrs);得到的。
 * Inflate(resId , parent,true)不仅能够正确的处理，而且已经把resId这个view加入到了parent，并且返回的是parent，和以上两者返回值有绝对的区别。
 * 举个栗子：
 * 1）在listView中调用第三个种，会报错，此时返回的view是listView而不是itemView。
 * 2）由于BasePopupWindow不打算绑定rootView，所以无法使用第二种方法，width和height要手动设置了
 */

public class BasePopupWindow {

    protected Context mContext;
    protected SupportPopupWindow mPopupWindow;

    /************ ***************/
    private int mWidth;             //宽度
    private int mHeight;            //高度
    private boolean mIsFocusable = true;  //是否获取焦点
    private boolean mIsOutside = true;    //是否可以触摸弹出框以外区域
    private int mResLayoutId = -1;        //布局id
    private View mContentView;            //布局view
    private int mAnimationStyle = -1;     //动画样式
    private boolean mClippEnable = true;   //允许弹出窗口超出屏幕范围，默认情况下，窗口被夹到屏幕边界。设置为false将允许Windows精确定位。
    private boolean mIgnoreCheekPress = false;   //设置是否忽略“脸颊触碰”，默认为false
    private int mInputMode = -1;                //设置输入法的模式,INPUT_METHOD_FROM_FOCUSABLE(根据是否可以获得焦点决定), INPUT_METHOD_NEEDED(允许输入法), or INPUT_METHOD_NOT_NEEDED(不允许输入法)。
    private int mSoftInputMode = -1;            //设置输入法的操作模式
    private boolean mTouchable = true;          //设置是否可被触碰
    private int mBackgroudColor = Color.TRANSPARENT;  //背景色
    private PopupWindow.OnDismissListener mOnDismissListener;
    private View.OnTouchListener mOnTouchListener;


    private SparseArray<View> mViews;

    public BasePopupWindow(Context mContext) {
        this.mContext = mContext;
        mViews = new SparseArray<>();
    }

    public BasePopupWindow(Context mContext, int resLayoutId, int width, int height) {
        this.mContext = mContext;
        this.mResLayoutId = resLayoutId;
        this.mWidth = width;
        this.mHeight = height;
        mViews = new SparseArray<>();
        build();
    }

    public BasePopupWindow(Context mContext, int resLayoutId, int width, int height,int backgroudColor) {
        this.mContext = mContext;
        this.mResLayoutId = resLayoutId;
        this.mWidth = width;
        this.mHeight = height;
        this.mBackgroudColor = backgroudColor;
        mViews = new SparseArray<>();
        build();
    }

    public int getWidth() {
        mContentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        mWidth = mContentView.getMeasuredWidth();
        return mWidth;
    }

    public int getHeight() {
        mContentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        mHeight =mContentView.getMeasuredHeight();
        return mHeight;
    }

    /**
     * 添加一些属性设置
     * @param popupWindow
     */
    private void apply(PopupWindow popupWindow){
        popupWindow.setClippingEnabled(mClippEnable);
        if(mIgnoreCheekPress){
            popupWindow.setIgnoreCheekPress();
        }
        if(mInputMode!=-1){
            popupWindow.setInputMethodMode(mInputMode);
        }
        if(mSoftInputMode!=-1){
            popupWindow.setSoftInputMode(mSoftInputMode);
        }
        if(mOnDismissListener!=null){
            popupWindow.setOnDismissListener(mOnDismissListener);
        }
        if(mOnTouchListener!=null){
            popupWindow.setTouchInterceptor(mOnTouchListener);
        }
        popupWindow.setTouchable(mTouchable);
    }

    private PopupWindow build(){
        //1.创建View
        if(mContentView == null){
            mContentView = LayoutInflater.from(mContext).inflate(mResLayoutId,null);
        }
        //2.创建PopupWindow，继承SupportPopWindow是为了解决 系统 7.0以上 showAsDropDown 置顶问题
        if(mWidth != 0 && mHeight!=0 ){
            mPopupWindow = new SupportPopupWindow(mContentView,mWidth,mHeight);
        }else{
            mPopupWindow = new SupportPopupWindow(mContentView,
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        //设置动画
        if(mAnimationStyle!=-1){
            mPopupWindow.setAnimationStyle(mAnimationStyle);
        }
        //设置一些属性
        apply(mPopupWindow);
        mPopupWindow.setFocusable(mIsFocusable);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(mBackgroudColor));
        mPopupWindow.setOutsideTouchable(mIsOutside);
/*

        if(mWidth == 0 || mHeight == 0){
            mPopupWindow.getContentView().measure(View.MeasureSpec.UNSPECIFIED,
                    View.MeasureSpec.UNSPECIFIED);
            //如果外面没有设置宽高的情况下，计算宽高并赋值
            mWidth = mPopupWindow.getContentView().getMeasuredWidth();
            mHeight = mPopupWindow.getContentView().getMeasuredHeight();
        }
*/

        //真正测量的宽高
        mContentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        mWidth = mContentView.getMeasuredWidth();
        mHeight = mContentView.getMeasuredHeight();

        mPopupWindow.update();

        return mPopupWindow;
    }


    public void setOnDismissListener(PopupWindow.OnDismissListener listener){
        mPopupWindow.setOnDismissListener(listener);
    }

    /**
     * 关闭popWindow
     */
    public void dismiss(){
        if(mPopupWindow!=null){
            mPopupWindow.dismiss();
        }
    }

    /**
     * 相对于某个控件显示
     * 实际上 在某个view下方弹出
     * 系统为7.0时，showAsDropDown()不起效果 需要做如下处理
     * @param anchor
     * @param xoff
     * @param yoff
     */
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        mPopupWindow.showAsDropDown(anchor, xoff, yoff);
    }

    /**
     * 相对于整个窗口 显示
     * @param parent  并不是指把popView放到parent里，这个parent的作用是调用其
     *                getWindowToken()方法获取窗口的Token，例如DialogAndPopActivity中 parent只是给了个button
     * @param gravity  Gravity.LEFT|Gravity.BOTTOM 这些
     * @param x
     * @param y
     */
    public void showAtLocation(View parent, int gravity, int x, int y) {
        mPopupWindow.showAtLocation(parent, gravity, x, y);
    }

    /**
     * 设置显示在v上方(以v的左边距为开始位置)
     * @param v
     */
    public void showUp(View v) {
        //获取需要在其上方显示的控件的位置信息
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        //在控件上方显示
        showAtLocation(v, Gravity.NO_GRAVITY, location[0], location[1] - mHeight);
    }

    public static class PopupWindowBuilder{

        private BasePopupWindow mCustomPopWindow;

        public PopupWindowBuilder(Context context){
            mCustomPopWindow = new BasePopupWindow(context);
        }

        public PopupWindowBuilder size(int width,int height){
            mCustomPopWindow.mWidth = width;
            mCustomPopWindow.mHeight = height;
            return this;
        }

        public PopupWindowBuilder setFocusable(boolean focusable){
            mCustomPopWindow.mIsFocusable = focusable;
            return this;
        }

        public PopupWindowBuilder setBackgroud(int backgroudColor){
            mCustomPopWindow.mBackgroudColor = backgroudColor;
            return this;
        }

        public PopupWindowBuilder setView(int resLayoutId){
            mCustomPopWindow.mResLayoutId = resLayoutId;
            mCustomPopWindow.mContentView = null;
            return this;
        }

        public PopupWindowBuilder setView(View view){
            mCustomPopWindow.mContentView = view;
            mCustomPopWindow.mResLayoutId = -1;
            return this;
        }

        public PopupWindowBuilder setOutsideTouchable(boolean outsideTouchable){
            mCustomPopWindow.mIsOutside = outsideTouchable;
            return this;
        }

        /**
         * 设置弹窗动画
         * @param animationStyle
         * @return
         */
        public PopupWindowBuilder setAnimationStyle(int animationStyle){
            mCustomPopWindow.mAnimationStyle = animationStyle;
            return this;
        }


        public PopupWindowBuilder setClippingEnable(boolean enable){
            mCustomPopWindow.mClippEnable =enable;
            return this;
        }


        public PopupWindowBuilder setIgnoreCheekPress(boolean ignoreCheekPress){
            mCustomPopWindow.mIgnoreCheekPress = ignoreCheekPress;
            return this;
        }

        public PopupWindowBuilder setInputMethodMode(int mode){
            mCustomPopWindow.mInputMode = mode;
            return this;
        }

        public PopupWindowBuilder setOnDissmissListener(PopupWindow.OnDismissListener onDissmissListener){
            mCustomPopWindow.mOnDismissListener = onDissmissListener;
            return this;
        }


        public PopupWindowBuilder setSoftInputMode(int softInputMode){
            mCustomPopWindow.mSoftInputMode = softInputMode;
            return this;
        }


        public PopupWindowBuilder setTouchable(boolean touchable){
            mCustomPopWindow.mTouchable = touchable;
            return this;
        }

        public PopupWindowBuilder setTouchIntercepter(View.OnTouchListener touchIntercepter){
            mCustomPopWindow.mOnTouchListener = touchIntercepter;
            return this;
        }


        public BasePopupWindow create(){
            //构建PopWindow
            mCustomPopWindow.build();
            return mCustomPopWindow;
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
            view = mContentView.findViewById(viewId);
            mViews.put(viewId,view);
        }
        return (T) view;
    }

    public BasePopupWindow setText(int viewId,int resId){
        TextView textView = getView(viewId);
        textView.setText(resId);
        return this;
    }

    public BasePopupWindow setText(int viewId,String textContent){
        TextView textView = getView(viewId);
        textView.setText(textContent);
        return this;
    }


}
