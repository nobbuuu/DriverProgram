package com.haylion.android.common.view;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.haylion.android.R;
import com.haylion.android.data.util.BusinessUtils;
import com.haylion.android.data.util.SpannableStringUtil;

/**
 * @author dengzh
 * @date 2019/11/22
 * Description: 货单剩余时间 倒计时View
 */
public class CargoRestTimeView extends LinearLayout {

    //Msg
    private final int HANDLER_MSG_UPDATE_CARGO_ORDER_REST_TIME = 1001;

    private Context mContext;
    private TextView mTvRestTime;
    private TextView mTvRestTimePost;
    private long restTimeMillis;  //货物订单剩余时间戳
    private Handler mHandler;
    
    public CargoRestTimeView(Context context) {
        super(context);
        initView(context);
    }

    public CargoRestTimeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CargoRestTimeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context){
        mContext = context;
        View mRootView = LayoutInflater.from(mContext).inflate(R.layout.view_cargo_rest_time, this);
        mTvRestTime = mRootView.findViewById(R.id.tv_rest_time);
        mTvRestTimePost = mRootView.findViewById(R.id.tv_rest_time_post_text);
        mHandler = new Handler(mContext.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == HANDLER_MSG_UPDATE_CARGO_ORDER_REST_TIME) {
                    //更新货物剩余时间
//                    updateRestTimeView();
                    updateRestTimeView(true);
                }
            }
        };
    }

    /**
     * 实时更新货物剩余送达时间
     * 每过1分钟更新一次数据
     * 0，则移除message
     */
    private void updateRestTimeView(){
        mTvRestTime.setText(BusinessUtils.formatEstimateTimeText(restTimeMillis/1000) + "内");
        if(restTimeMillis >  0) {
            restTimeMillis = restTimeMillis - 60 * 1000;
            mHandler.sendEmptyMessageDelayed(HANDLER_MSG_UPDATE_CARGO_ORDER_REST_TIME,60 * 1000);
        } else{
            restTimeMillis = 0;
            mHandler.removeMessages(HANDLER_MSG_UPDATE_CARGO_ORDER_REST_TIME);
        }
    }

    /**
     * 实时更新货物剩余送达时间(超时，则显示超时多久)
     * 每过1分钟更新一次数据
     * 0，则移除message
     */
    private void updateRestTimeView(boolean restAndOverTime){
        if(!restAndOverTime) {
            updateRestTimeView();
            return;
        }

        if(restTimeMillis >= 0){
            mTvRestTime.setText(BusinessUtils.formatEstimateTimeText(restTimeMillis/1000, BusinessUtils.PrecisionMode.MINUTE) + "内");
            mTvRestTimePost.setVisibility(VISIBLE);
        } else {
            String str = "已超时 " + BusinessUtils.formatEstimateTimeText(- restTimeMillis/1000, BusinessUtils.PrecisionMode.MINUTE);
            SpannableString spannableString = SpannableStringUtil.getSpanString(str, 0, 4, R.color.maas_text_primary, Typeface.NORMAL, 1f);
            mTvRestTime.setText(spannableString);
            mTvRestTimePost.setVisibility(GONE);
        }

        restTimeMillis = restTimeMillis - 60 * 1000;
        mHandler.sendEmptyMessageDelayed(HANDLER_MSG_UPDATE_CARGO_ORDER_REST_TIME,60 * 1000);
    }


    /**
     * 开始倒计时
     * @param restTimeMillis  剩余时间戳，单位 毫秒
     */
    public void startCountDown(long restTimeMillis){
        this.restTimeMillis = restTimeMillis;
        //在开始之前，先结束当前倒计时
        stopCountDown();
        if(mHandler!=null){
            mHandler.sendEmptyMessage(HANDLER_MSG_UPDATE_CARGO_ORDER_REST_TIME);
        }
    }

    /**
     * 结束倒计时
     */
    public void stopCountDown(){
        if(mHandler!=null){
            mHandler.removeMessages(HANDLER_MSG_UPDATE_CARGO_ORDER_REST_TIME);
        }
    }

    /**
     * 在Activity的onDestroy() 调用销毁
     */
    public void onDestroy(){
        if(mHandler!=null){
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }


    
}
