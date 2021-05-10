package com.haylion.android.common.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.haylion.android.R;
import com.haylion.android.mvp.util.LogUtils;


import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @author dengzh
 * @date 2019/11/23
 * Description:美团抢单中弹窗
 * 美团抢单流程如下：
 * 1.向后台发起抢单，显示计时弹窗
 * 2.后台推送抢单结果，隐藏计时弹窗（如果断流重连，主动查询该订单状态）
 * 异常情况：
 * 1.后台先推送了抢单结果，再返回显示计时，导致计时弹窗永远不关闭，须在详情页做异常处理。（少见）
 */
public class MeiTuanGrabingDialog extends Dialog {

    private Context mContext;
    private ImageView ivTipsImage;
    private TextView tvTime;
    private Disposable disposable;

    public MeiTuanGrabingDialog(@NonNull Context context) {
        super(context, R.style.Translucent_NoTitle);
        mContext = context;
        initView();
    }

    private void initView(){
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_grab_order_meituan, null);
        ivTipsImage = view.findViewById(R.id.iv_tips_image);
        tvTime = view.findViewById(R.id.tv_time);

        setCanceledOnTouchOutside(false);
        setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(disposable != null){
                    disposable.dispose();
                    disposable = null;
                }
            }
        });
        if (Build.VERSION.SDK_INT >= 26) {
            getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        } else {
            getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        getWindow().setContentView(view);
    }

    /**
     * 显示弹窗
     */
    public void showDialog(){
        if(isShowing()){
            return;
        }
        disposable = Observable.interval(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        LogUtils.e("Disposable", (aLong + 1) + "");
                        tvTime.setText(aLong + "s");
                    }
                });
        //开启动画,补间动画不会内存泄漏
        ivTipsImage.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.anim_turn_around));
        show();
    }

}
