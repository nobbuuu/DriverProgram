package com.haylion.android.common.view.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.amap.api.navi.enums.BroadcastMode;
import com.haylion.android.R;
import com.haylion.android.data.model.AmapNaviSettingBean;
import com.haylion.android.data.repo.PrefserHelper;

/**
 * @author dengzh
 * @date 2019/11/7
 * Description:地图导航设置 Dialog
 */
public class AmapNaviSettingDialog extends DBaseDialog implements View.OnClickListener {

    /**
     * 策略
     * */
    private RelativeLayout[] rlStrategy = new RelativeLayout[4];
    private ImageView[] ivStrategy = new ImageView[4];
    private TextView[] tvStrategy = new TextView[4];
    private ImageView[] ivStrategySelectSign = new ImageView[4];

    /**
     * 播报
     * */
    private RelativeLayout[] rlBroadcast = new RelativeLayout[3];
    private TextView[] tvBroadcast = new TextView[3];
    private ImageView[] ivBroadcastSelectSign = new ImageView[3];

    /**
     * 导航设置数据
     * */
    private AmapNaviSettingBean mSetBean = new AmapNaviSettingBean();


    public AmapNaviSettingDialog(Context mContext) {
        super(mContext, R.layout.dialog_amap_navi_setting, Gravity.BOTTOM, true, true);
        init();
    }

    private void init(){
        /**
         * 策略 -- 躲避拥堵
         * */
        rlStrategy[0] = getView(R.id.rl_congestion);
        ivStrategy[0] = getView(R.id.iv_congestion);
        tvStrategy[0] = getView(R.id.tv_congestion);
        ivStrategySelectSign[0] = getView(R.id.iv_congestion_select_sign);

        /**
         * 避免收费
         * */
        rlStrategy[1] = getView(R.id.rl_cost);
        ivStrategy[1] = getView(R.id.iv_cost);
        tvStrategy[1] = getView(R.id.tv_cost);
        ivStrategySelectSign[1] = getView(R.id.iv_cost_select_sign);

        /**
         * 不走高速
         * */
        rlStrategy[2] = getView(R.id.rl_avoid_high_speed);
        ivStrategy[2] = getView(R.id.iv_avoid_high_speed);
        tvStrategy[2] = getView(R.id.tv_avoid_high_speed);
        ivStrategySelectSign[2] = getView(R.id.iv_avoid_high_speed_select_sign);

        /**
         * 高速优先
         * */
        rlStrategy[3] = getView(R.id.rl_high_speed);
        ivStrategy[3] = getView(R.id.iv_high_speed);
        tvStrategy[3] = getView(R.id.tv_high_speed);
        ivStrategySelectSign[3] = getView(R.id.iv_high_speed_select_sign);

        for (int i =0;i<rlStrategy.length;i++){
            rlStrategy[i].setOnClickListener(this);
        }

        /**
         * 广播 - 详细播报
         * */
        rlBroadcast[0] = getView(R.id.rl_broadcast_detail);
        tvBroadcast[0] = getView(R.id.tv_broadcast_detail);
        ivBroadcastSelectSign[0] = getView(R.id.iv_broadcast_detail_select_sign);

        /**
         * 简洁播报
         * */
        rlBroadcast[1] = getView(R.id.rl_broadcast_concise);
        tvBroadcast[1] = getView(R.id.tv_broadcast_concise);
        ivBroadcastSelectSign[1] = getView(R.id.iv_broadcast_concise_select_sign);

        /**
         * 静音
         * */
        rlBroadcast[2] = getView(R.id.rl_broadcast_none);
        tvBroadcast[2] = getView(R.id.tv_broadcast_none);
        ivBroadcastSelectSign[2] = getView(R.id.iv_broadcast_none_select_sign);

        for (int i = 0 ;i< rlBroadcast.length;i++){
            rlBroadcast[i].setOnClickListener(this);
        }

        getView(R.id.tv_confirm).setOnClickListener(this);
    }

    public void updateData(){
        //从缓存里取之前的数据
        AmapNaviSettingBean bean = PrefserHelper.getNaviSetInfo();

        mSetBean.setCongestion(bean.isCongestion());
        mSetBean.setCost(bean.isCost());
        mSetBean.setAvoidhightspeed(bean.isAvoidhightspeed());
        mSetBean.setHightspeed(bean.isHightspeed());
        mSetBean.setBroadcastMode(bean.getBroadcastMode());

        updateStrategyView();
        updateBroadcastView();
    }

    /**
     * 策略View更新
     */
    private void updateStrategyView(){
        //策略
        ivStrategy[0].setImageResource(mSetBean.isCongestion()? R.mipmap.ic_amap_navi_setting_congestion_selected:R.mipmap.ic_amap_navi_setting_congestion);
        ivStrategy[1].setImageResource(mSetBean.isCost()? R.mipmap.ic_amap_navi_setting_cost_selected:R.mipmap.ic_amap_navi_setting_cost);
        ivStrategy[2].setImageResource(mSetBean.isAvoidhightspeed()? R.mipmap.ic_amap_navi_setting_avoid_high_speed_selected:R.mipmap.ic_amap_navi_setting_avoid_high_speed);
        ivStrategy[3].setImageResource(mSetBean.isHightspeed()? R.mipmap.ic_amap_navi_setting_high_speed_selected:R.mipmap.ic_amap_navi_setting_high_speed);


        updateSingleStrategyView(mSetBean.isCongestion(),rlStrategy[0],tvStrategy[0],ivStrategySelectSign[0]);
        updateSingleStrategyView(mSetBean.isCost(),rlStrategy[1],tvStrategy[1],ivStrategySelectSign[1]);
        updateSingleStrategyView(mSetBean.isAvoidhightspeed(),rlStrategy[2],tvStrategy[2],ivStrategySelectSign[2]);
        updateSingleStrategyView(mSetBean.isHightspeed(),rlStrategy[3],tvStrategy[3],ivStrategySelectSign[3]);
    }

    /**
     * 单个策略View 更新
     * @param isSelect
     * @param rl
     * @param tv
     * @param ivSelect
     */
    public void updateSingleStrategyView(boolean isSelect,RelativeLayout rl,TextView tv,ImageView ivSelect){
        if(isSelect){
            rl.setBackgroundResource(R.drawable.bg_blue_round_3dp);
            tv.setTextColor(ContextCompat.getColor(mContext,R.color.white));
            ivSelect.setVisibility(View.VISIBLE);
        }else{
            rl.setBackgroundResource(R.drawable.bg_grey_round_3dp);
            tv.setTextColor(ContextCompat.getColor(mContext,R.color.text_black));
            ivSelect.setVisibility(View.GONE);
        }
    }

    /**
     * 播报View更新
     */
    private void updateBroadcastView(){
        for (int i = 0;i< rlBroadcast.length;i++){
            updateSingleBroadcastView(false,rlBroadcast[i],tvBroadcast[i],ivBroadcastSelectSign[i]);
        }
        if(mSetBean.getBroadcastMode() == BroadcastMode.DETAIL){  //详细播报
            updateSingleBroadcastView(true,rlBroadcast[0],tvBroadcast[0],ivBroadcastSelectSign[0]);
        }else if(mSetBean.getBroadcastMode() == BroadcastMode.CONCISE){  //简洁播报
            updateSingleBroadcastView(true,rlBroadcast[1],tvBroadcast[1],ivBroadcastSelectSign[1]);
        }else{ //静音
            updateSingleBroadcastView(true,rlBroadcast[2],tvBroadcast[2],ivBroadcastSelectSign[2]);
        }
    }

    /**
     * 单个播报View 更新
     * @param isSelect
     * @param rl
     * @param tv
     * @param ivSelect
     */
    private void updateSingleBroadcastView(boolean isSelect,RelativeLayout rl,TextView tv,ImageView ivSelect){
        if(isSelect){
            rl.setBackgroundResource(R.drawable.kuang_mass_blue_round_2dp_width_1dp);
            tv.setTextColor(ContextCompat.getColor(mContext,R.color.maas_text_blue));
            ivSelect.setVisibility(View.VISIBLE);
        }else{
            rl.setBackgroundResource(R.drawable.kuang_cccccc_round_2dp_width_1dp);
            tv.setTextColor(ContextCompat.getColor(mContext,R.color.text_black));
            ivSelect.setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_congestion:
                mSetBean.setCongestion(!mSetBean.isCongestion());
                updateStrategyView();
                break;
            case R.id.rl_cost:
                //高速优先与避免收费不能同时为true
                if(mSetBean.isCost()){
                    mSetBean.setCost(false);
                }else{
                    mSetBean.setCost(true);
                    mSetBean.setHightspeed(false);
                }
                updateStrategyView();
                break;
            case R.id.rl_avoid_high_speed:
                //不走高速与高速优先不能同时为true
                if(mSetBean.isAvoidhightspeed()){
                    mSetBean.setAvoidhightspeed(false);
                }else{
                    mSetBean.setAvoidhightspeed(true);
                    mSetBean.setHightspeed(false);
                }
                updateStrategyView();
                break;
            case R.id.rl_high_speed:
                //高速优先与避免收费不能同时为true
                if(mSetBean.isHightspeed()){
                    mSetBean.setHightspeed(false);
                }else{
                    mSetBean.setHightspeed(true);
                    mSetBean.setAvoidhightspeed(false);
                    mSetBean.setCost(false);
                }
                updateStrategyView();
                break;
            case R.id.rl_broadcast_detail:
                mSetBean.setBroadcastMode(BroadcastMode.DETAIL);
                updateBroadcastView();
                break;
            case R.id.rl_broadcast_concise:
                mSetBean.setBroadcastMode(BroadcastMode.CONCISE);
                updateBroadcastView();
                break;
            case R.id.rl_broadcast_none:
                mSetBean.setBroadcastMode(-1);
                updateBroadcastView();
                break;
            case R.id.tv_confirm:
                mSetBean.getDrivingMode();
                //保存到缓存
                PrefserHelper.saveNaviSetInfo(mSetBean);
                if(mSetBean.getBroadcastMode() == -1){
                    PrefserHelper.setCache(PrefserHelper.KEY_AMAP_NAVI_VOICE_ENABLE, "disable");
                }else{
                    PrefserHelper.setCache(PrefserHelper.KEY_AMAP_NAVI_VOICE_ENABLE, "enable");
                }
                if(listener!=null){
                    listener.onConfirm();
                }
                toggleDialog();
                break;
        }
    }

    public OnDialogSelectListener listener;

    public void setOnDialogSelectListener(OnDialogSelectListener listener){
        this.listener = listener;
    }
    public interface OnDialogSelectListener{
        void onConfirm();
    }
}
