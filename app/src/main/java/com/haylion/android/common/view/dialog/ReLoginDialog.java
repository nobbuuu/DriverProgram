package com.haylion.android.common.view.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dianping.logan.Logan;
import com.haylion.android.R;
import com.haylion.android.data.base.BaseDialog;
import com.haylion.android.mvp.util.LogUtils;
import com.haylion.android.user.account.LoginActivity;

/**
 * wangjianming
 * <p>
 * 重新登录弹窗
 */
public class ReLoginDialog extends BaseDialog {

    private String TAG = "ReLoginDialog";

    private View view;
    private TextView tvHint;
    private TextView tvOk;

    private Context context;


    public ReLoginDialog(@NonNull Context context) {
        super(context, R.style.Translucent_NoTitle);
        this.context = context;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.layout_relogin_dialog, null);
        setContentView(view);
        tvHint = view.findViewById(R.id.tv_hint);
        tvOk = view.findViewById(R.id.btn_ok);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logan.w("re login:", 1);
                LogUtils.d(TAG, "onRelogin");
                LoginActivity.start(getContext(), true);
                dismiss();
            }
        });
        //不允许外部取消
        setCanceledOnTouchOutside(false);
        //屏蔽返回键
        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                }
                return false;
            }
        });
    }

    public void setData(String hint) {
        tvHint.setText(hint);
    }


}
