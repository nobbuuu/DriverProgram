package com.haylion.android.orderlist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.haylion.android.MyApplication;
import com.haylion.android.R;
import com.haylion.android.common.view.dialog.DialogUtils;
import com.haylion.android.data.base.BaseActivity;
import com.haylion.android.data.base.BaseDialog;
import com.haylion.android.data.base.ConfirmDialog;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.model.OrderStatus;
import com.haylion.android.data.util.BusinessUtils;
import com.haylion.android.orderdetail.map.ShowInMapNewActivity;

import java.text.ParseException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.OnTextChanged;

public class AppointmentDetailsActivity extends BaseActivity<AppointmentDetailsContract.Presenter>
        implements AppointmentDetailsContract.View {

    @BindView(R.id.tv_order_time)
    TextView mOrderTime;
    @BindView(R.id.order_status)
    TextView mOrderStatus;

    @BindView(R.id.tv_get_on_addr)
    TextView mGetOnAddress;
    @BindView(R.id.tv_get_on_desc)
    TextView mGetOnAddressDesc;

    @BindView(R.id.tv_get_off_addr)
    TextView mGetOffAddress;
    @BindView(R.id.tv_get_off_desc)
    TextView mGetOffAddressDesc;

    @BindView(R.id.tv_total_distance)
    TextView mTotalDistance;

    @BindView(R.id.action_panel)
    View mActionPanel;
    @BindView(R.id.action_text)
    TextView mActionText;
    @BindView(R.id.action_remark)
    View mActionRemark;

    @BindView(R.id.tv_contact_passenger)
    View mContactPassenger;
    @BindView(R.id.cancel_order)
    View mCancelOrder;
    @BindView(R.id.dash_line)
    View mDashLine;

    @BindView(R.id.cancel_reason_panel)
    View mCancelReasonPanel;
    @BindView(R.id.cancel_reason)
    TextView mCancelReason;

    @OnClick({R.id.btn_back, R.id.tv_contact_passenger,
            R.id.cancel_order, R.id.action_panel,
            R.id.show_map
    })
    void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.tv_contact_passenger:
                if (mOrder == null) {
                    toast("订单数据为空");
                    return;
                }
                String phone = mOrder.getUserInfo().getPhoneNum();
                if (TextUtils.isEmpty(phone)) {
                    toast("没有号码数据");
                } else {
                    DialogUtils.showRealCallDialog(this, phone);
                }
                break;
            case R.id.cancel_order:
                if (mCancelAppointmentDialog == null) {
                    mCancelAppointmentDialog = new CancelAppointmentDialog(getContext(), mOrder);
                    mCancelAppointmentDialog.setCallback(this::closePage);
                }
                mCancelAppointmentDialog.show();
                break;

            case R.id.action_panel:
                OrderStatus orderStatus = OrderStatus.forStatus(mOrder.getOrderStatus());
                if (orderStatus == OrderStatus.ORDER_STATUS_GET_ON) {
                    confirmArriveDestination();
                } else {
                    presenter.processOrder();
                }
                break;
            case R.id.show_map:
                if (mOrder == null) {
                    toast("订单数据为空");
                    return;
                }
                Intent intent = new Intent(getContext(), ShowInMapNewActivity.class);
                intent.putExtra(ShowInMapNewActivity.EXTRA_GRAB_ENABLED, false);
                intent.putExtra(ShowInMapNewActivity.ORDER_START_ADDR, mOrder.getStartAddr());
                intent.putExtra(ShowInMapNewActivity.ORDER_END_ADDR, mOrder.getEndAddr());
                startActivity(intent);
                break;
        }
    }

    private CancelAppointmentDialog mCancelAppointmentDialog;

    private Order mOrder;

    public static void start(Context context, int orderId) {
        Intent intent = new Intent(context, AppointmentDetailsActivity.class);
        intent.putExtra(EXTRA_ORDER_ID, orderId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_details);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.setOrderIdRunning(parseOrderId());
    }

    @Override
    protected AppointmentDetailsContract.Presenter onCreatePresenter() {
        return new AppointmentDetailsPresenter(this, parseOrderId());
    }

    @Override
    public void showOrderDetails(Order order) {
        mOrder = order;
        long milliSecond = -1;
        try {
            milliSecond = BusinessUtils.stringToLong(order.getOrderTime(), "yyyy-MM-dd HH:mm");
            String timeFormat = BusinessUtils.getDateToStringIncludeYear(milliSecond, null);
            mOrderTime.setText(timeFormat);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //订单状态
        if (order.getOrderStatus() == Order.ORDER_STATUS_CLOSED && order.getOrderSubStatus() == Order.ORDER_SUB_STATUS_CLOSED_COMPLAIN) {
            mOrderStatus.setText(OrderStatus.getStatusText(order.getOrderStatus(), order.getOrderType()) + "(支付争议)");
        } else {
            mOrderStatus.setText(OrderStatus.getStatusText(order.getOrderStatus(), order.getOrderType()));
        }

        //地址信息
        mGetOnAddress.setText(order.getStartAddr().getName());
        mGetOnAddressDesc.setText(order.getStartAddr().getAddressDetail());

        mGetOffAddress.setText(order.getEndAddr().getName());
        mGetOffAddressDesc.setText(order.getEndAddr().getAddressDetail());

        mTotalDistance.setText(BusinessUtils.formatDistance(order.getDistance()));

        OrderStatus orderStatus = OrderStatus.forStatus(order.getOrderStatus());
        if (orderStatus == OrderStatus.ORDER_STATUS_READY) {
            mDashLine.setVisibility(View.VISIBLE);
            mCancelOrder.setVisibility(View.VISIBLE);

            mActionPanel.setVisibility(View.VISIBLE);
            mActionText.setText(R.string.action_start_order);
            mActionRemark.setVisibility(View.VISIBLE);

            if (milliSecond != -1) {
                long currentMills = System.currentTimeMillis();
                if (currentMills > milliSecond ||
                        milliSecond - currentMills < TimeUnit.HOURS.toMillis(2)) {
                    mActionPanel.setEnabled(true);
                } else {
                    mActionPanel.setEnabled(false);
                }
            } else {
                mActionPanel.setEnabled(false);
            }
            mCancelReasonPanel.setVisibility(View.GONE);

        } else if (orderStatus == OrderStatus.ORDER_STATUS_GET_ON) {
            mDashLine.setVisibility(View.GONE);
            mCancelOrder.setVisibility(View.GONE);

            mActionPanel.setVisibility(View.VISIBLE);
            mActionText.setText(R.string.action_arrive_destination);
            mActionRemark.setVisibility(View.GONE);

            mActionPanel.setEnabled(true);

            mCancelReasonPanel.setVisibility(View.GONE);

        } else {
            if (orderStatus == OrderStatus.ORDER_STATUS_CANCELED) {
                mCancelReasonPanel.setVisibility(View.VISIBLE);
                mCancelReason.setText(order.getOrderCancellerMsg());
            } else {
                mCancelReasonPanel.setVisibility(View.GONE);
            }

            mDashLine.setVisibility(View.GONE);
            mCancelOrder.setVisibility(View.GONE);
            mActionPanel.setVisibility(View.GONE);
        }

        if (orderStatus == OrderStatus.ORDER_STATUS_CLOSED) {
            mContactPassenger.setVisibility(View.GONE);
        }
    }

    @Override
    public void closePage() {
        finish();
    }


    private void confirmArriveDestination() {
        ConfirmDialog confirmDialog = ConfirmDialog.newInstance();
        confirmDialog.setMessage(getString(R.string.tips_confirm_arrival_destination));
        confirmDialog.setOnClickListener(new ConfirmDialog.OnClickListener() {
            @Override
            public void onPositiveClick(View view) {
                presenter.processOrder();
            }
        }).setPositiveText(R.string.confirm_arrival).setType(0);
        confirmDialog.show(getSupportFragmentManager(), "");

    }

    private int parseOrderId() {
        return getIntent().getIntExtra(EXTRA_ORDER_ID, -1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCancelAppointmentDialog != null && mCancelAppointmentDialog.isShowing()) {
            mCancelAppointmentDialog.dismiss();
            mCancelAppointmentDialog = null;
        }
        MyApplication.setOrderIdRunning(-1);
    }

    private static final String EXTRA_ORDER_ID = "order_id";


}
