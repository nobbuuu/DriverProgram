package com.haylion.android.common.view.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.haylion.android.R;
import com.haylion.android.data.base.BaseDialog;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.util.DecimalDigitsInputFilter;

import butterknife.OnTextChanged;

/**
 * @author dengzh
 * @date 2019/12/12
 * Description:FloatService - 异常结束订单
 * <p>
 * 规则：
 * a：点击“确定”
 * 1）回到听单页
 * b：点击“取消”
 * 1）回到原页面
 */
public class OrderAbnormalFinishDialog extends BaseDialog {

    private String TAG = "OrderAbnormalFinishDialog";

    private View view;

    private Context context;
    private LinearLayout llTaxiFee;
    private EditText etTaxiFee; //打表价格，可选
    private TextView etExceptionFinishOrderReason; //异常结束原因
    private TextView tvCancel;
    private TextView tvOK;
    private TextView tvTextNumber; //输入的字数

    public OrderAbnormalFinishDialog(@NonNull Context context) {
        super(context, R.style.Translucent_NoTitle);
        this.context = context;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.layout_exception_finish_order, null);
        setContentView(view);

        //update view
        llTaxiFee = view.findViewById(R.id.ll_taxi_fee);
        etTaxiFee = view.findViewById(R.id.et_taxi_fee);
        etExceptionFinishOrderReason = view.findViewById(R.id.et_exception_finish_reason);
        tvTextNumber = view.findViewById(R.id.tv_text_number);

        tvCancel = view.findViewById(R.id.tv_cancel);
        tvOK = view.findViewById(R.id.tv_ok);

        //限制两位小数点
        etTaxiFee.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2), new InputFilter.LengthFilter(7)});

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

    /**
     * 根据输入的服务费计算总费用
     */
    @OnTextChanged(value = {R.id.et_exception_finish_reason})
    public void onTextChanged() {
        String text = etExceptionFinishOrderReason.getText().toString();
        if (!TextUtils.isEmpty(text)) {
            tvTextNumber.setText(text.length()+ "/50");
        } else{
            tvTextNumber.setText("0/50");
        }
    }


    private CallBack callBack;

    public void setData(Order order, CallBack callBack) {
        //回调
        this.callBack = callBack;

        if (order.getOrderStatus() == Order.ORDER_STATUS_GET_ON) {
            llTaxiFee.setVisibility(View.VISIBLE);
        } else {
            llTaxiFee.setVisibility(View.GONE);
        }


        tvOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //输入有效性判断
                double meterPrice = 0;
                if (llTaxiFee.getVisibility() == View.VISIBLE) {
                    String costString;
                    costString = etTaxiFee.getText().toString();
                    if (StringValidate(costString)) {
                        meterPrice = Double.valueOf(costString);
                        if (meterPrice <= 0 || meterPrice > 10000) {
                            toast(R.string.toast_please_enter_valid_fare);
                            return;
                        }
                    } else {
                        toast(R.string.toast_please_enter_valid_fare);
                        return;
                    }
                }

                //异常结束的原因
                String reason = "";
                if (TextUtils.isEmpty(etExceptionFinishOrderReason.getText()) || etExceptionFinishOrderReason.getText().length() > 50) {
                    toast("请输入50个以内的字");
                    return;
                } else {
                    reason = etExceptionFinishOrderReason.getText().toString();

                }

                //上报异常取消原因。
                callBack.exceptionFinishOrder(meterPrice, reason);

                dismiss();
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private boolean StringValidate(String value) {
        Log.d(TAG, "value:" + value);

        if (TextUtils.isEmpty(value)
                || (".".equals(value))
                || Double.valueOf(value) <= 0) {
            return false;
        }
        //小数点前必须有数字
        int index = value.indexOf('.');
        if (index == 0) {
            return false;
        }
        return true;
    }

    //
    public interface CallBack {
        void exceptionFinishOrder(double taxiFee, String reason);
    }
}
