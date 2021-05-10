package com.haylion.android.orderlist;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.haylion.android.R;
import com.haylion.android.data.base.BaseDialog;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.model.OrderStatus;
import com.haylion.android.data.repo.OrderRepository;
import com.haylion.android.mvp.rx.ApiSubscriber;
import com.haylion.android.mvp.util.LogUtils;

import java.util.Locale;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class CancelAppointmentDialog extends BaseDialog {

    @OnClick({R.id.tv_cancel, R.id.tv_ok})
    void onButtonClick(View view) {
        if (view.getId() == R.id.tv_ok) {
            String reason = mEditText.getText().toString();
            if (TextUtils.isEmpty(reason)) {
                toast("请输入取消原因");
                return;
            }
            cancelOrder(reason);
        }
        dismiss();
    }

    @BindView(R.id.et_finish_reason)
    EditText mEditText;

    @OnTextChanged(R.id.et_finish_reason)
    void onTextChanged(CharSequence reason) {
        refreshCharacterCount(reason.length());
    }

    @BindView(R.id.character_count)
    TextView mCharacterCount;
    private final int mMaxLength;

    @BindView(R.id.order_status)
    TextView mOrderStatus;

    private Order mOrder;

    private Callback mCallback;

    public CancelAppointmentDialog(@NonNull Context context, @NonNull Order order) {
        super(context);
        this.mOrder = order;
        this.mMaxLength = 50; // 取消原因最多50个字符
    }

    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_cancel_appointment);
        mEditText.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(mMaxLength)});
        refreshCharacterCount(0);

        OrderStatus orderStatus = OrderStatus.forStatus(mOrder.getOrderStatus());
        String statusText = OrderStatus.getStatusText(orderStatus, Order.ORDER_TYPE_BOOK);
        mOrderStatus.setText(statusText);
    }

    private void refreshCharacterCount(int length) {
        String countText = String.format(Locale.getDefault(),
                "%d/%d", length, mMaxLength);
        mCharacterCount.setText(countText);
    }

    private void cancelOrder(String reason) {
        OrderRepository.INSTANCE.cancelOrder(mOrder.getOrderId(), reason, null, new ApiSubscriber<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                if (aBoolean != null && aBoolean) {
                    toast("取消成功");
                    dismiss();
                    if (mCallback != null) {
                        mCallback.onOrderCanceled();
                    }
                } else {
                    toast("取消失败");
                }
            }

            @Override
            public void onError(int code, String msg) {
                toast("取消失败");
                LogUtils.d("取消预约订单出错：" + code + ", " + msg);
            }
        });
    }

    public interface Callback {

        void onOrderCanceled();

    }


}
