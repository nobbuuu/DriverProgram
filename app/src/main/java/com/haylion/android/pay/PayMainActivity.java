package com.haylion.android.pay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.gyf.immersionbar.ImmersionBar;
import com.haylion.android.R;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.model.OrderPayInfo;
import com.haylion.android.data.model.PayInfo;
import com.haylion.android.data.model.ServiceFee;
import com.haylion.android.data.model.WebsocketData;
import com.haylion.android.data.util.BusinessUtils;
import com.haylion.android.data.util.DecimalDigitsInputFilter;

import com.haylion.android.data.base.BaseActivity;
import com.haylion.android.mvp.util.LogUtils;
import com.haylion.android.orderdetail.trip.CarpoolTripEndActivity;
import com.haylion.android.orderdetail.trip.TripEndActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.NumberFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;


/**
 * 确认账单页面
 */
public class PayMainActivity extends BaseActivity<PayMainContract.Presenter> implements PayMainContract.View {

    private static final String TAG = "PayMainActivity";
    private static final String EXTRA_ORDER_PAY_INFO = "order_pay_info";
    private static final int COST_MAX_VALUE = 10000;   //最大支付金额


    private int orderId;
    private int orderType;
    private double cost = 0;
    private int orderChannel; //订单来源
    private double baseCost = 0;
    private double otherCost = 0;
    private double serviceCost = 0;
    /**
     * 实际的服务费，请求接口时提交
     */
    private double realServiceCost = 0;

    private static int PAY_BY_ONLINE = 1;
    private static int PAY_BY_CASH = 2;

    private static int CONFIRM_PAIED = 1;
    private static int CONFIRM_NO_PAIED = 2;

    private boolean carpoolFlag = false;
    private String carpoolCode;

    /**
     * tips 提示
     */
    @BindView(R.id.tv_cost_tip1)
    TextView tvCostTip1;
    @BindView(R.id.tv_cost_tip2)
    TextView tvCostTip2;

    /**
     * 车费
     */
    @BindView(R.id.et_base_cost)
    EditText etBaseCost;
    @BindView(R.id.tv_base_cost_carpool)
    TextView tvBaseCostCarpool;

    /**
     * 其他费用
     */
    @BindView(R.id.rl_other_cost)
    RelativeLayout rlOtherCost;
    @BindView(R.id.other_cost)
    EditText etOtherCost;
    @BindView(R.id.other_cost_post)
    TextView tvOtherCostPost;

    /**
     * 服务费用
     */
    @BindView(R.id.rl_service_cost)
    RelativeLayout rlServiceCost;
    @BindView(R.id.tv_service_cost)
    TextView tvServiceCost;

    /**
     * 总费用
     */
    @BindView(R.id.rl_total_cost)
    RelativeLayout rlTotalCost;
    @BindView(R.id.tv_total_cost)
    TextView tvTotalCost;


    @BindView(R.id.pay_by_app)
    TextView tvPayByApp;
    @BindView(R.id.pay_by_cash)
    TextView tvPayByCash;
    @BindView(R.id.ll_pay_by_app)
    LinearLayout llPayByApp;
    @BindView(R.id.ll_pay_by_cash)
    LinearLayout llPayByCash;

    @BindView(R.id.divide_line_other)
    View divideLineOther;
    @BindView(R.id.divide_line_service)
    View divideLineService;
    @BindView(R.id.divide_line_total)
    View divideLineTotal;

    public static void start(Context context, OrderPayInfo orderPayInfo) {
        LogUtils.d(TAG, "pay info:" + orderPayInfo.toString());
        Intent intent = new Intent(context, PayMainActivity.class);
        intent.putExtra(EXTRA_ORDER_PAY_INFO, orderPayInfo);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_main);
        ButterKnife.bind(this);
        OrderPayInfo orderPayInfo = getIntent().getParcelableExtra(EXTRA_ORDER_PAY_INFO);
        if (orderPayInfo == null) {
            LogUtils.e(TAG, "orderPayInfo is null");
            finish();
            return;
        }
        carpoolFlag = orderPayInfo.isCarpoolOrder();
        if (carpoolFlag) {
            carpoolCode = orderPayInfo.getCarpoolCode();
            presenter.getCarpoolServiceFee(carpoolCode);
        } else {
            orderId = orderPayInfo.getOrderId();
        }
        orderType = orderPayInfo.getOrderType();
        orderChannel = orderPayInfo.getOrderChannel();
        cost = orderPayInfo.getCost();
        LogUtils.d(TAG, "orderPayInfo:" + orderPayInfo.toString());

        EventBus.getDefault().register(this);

        ImmersionBar.with(this).statusBarColor(R.color.c_4b8afb)
                .statusBarDarkFont(true, 0.2f)
                .init();

        initView();
    }

    /**
     * 根据订单类型，显示不一样的支付样式。实时拼车订单，为固定的价格
     */
    private void initView() {
        if (orderType == Order.ORDER_TYPE_REALTIME_CARPOOL) {
            //拼车单，固定价格
            tvBaseCostCarpool.setText(BusinessUtils.moneySpec(cost));
            etBaseCost.setText(BusinessUtils.moneySpec(cost));
        } else {
            //限制两位小数点
            etBaseCost.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2), new InputFilter.LengthFilter(7)});
            etOtherCost.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2), new InputFilter.LengthFilter(7)});

            etBaseCost.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showSoftInput(etBaseCost, getContext());
                }
            }, 300);
        }


        //默认显示的设置,只显示车费
        tvCostTip1.setVisibility(View.VISIBLE);
        tvCostTip2.setVisibility(View.GONE);

        tvBaseCostCarpool.setVisibility(View.GONE);
        etBaseCost.setVisibility(View.VISIBLE);

        rlOtherCost.setVisibility(View.GONE);
        rlServiceCost.setVisibility(View.GONE);
        rlTotalCost.setVisibility(View.GONE);

        //根据订单类型确认需要显示的内容
        switch (orderType) {
            case Order.ORDER_TYPE_REALTIME_CARPOOL://实时拼车单
                //固定价格
                tvBaseCostCarpool.setVisibility(View.VISIBLE);
                etBaseCost.setVisibility(View.INVISIBLE);
                break;
            case Order.ORDER_TYPE_REALTIME://实时订单
            case Order.ORDER_TYPE_CARGO_PASSENGER: //货拼客单
            case Order.ORDER_TYPE_BOOK: // 预约订单
            case Order.ORDER_TYPE_ACCESSIBILITY: // 女性专车订单
                rlOtherCost.setVisibility(View.VISIBLE);
                divideLineOther.setVisibility(View.VISIBLE);

                rlServiceCost.setVisibility(View.GONE);

                rlTotalCost.setVisibility(View.VISIBLE);
                divideLineTotal.setVisibility(View.VISIBLE);
                tvCostTip1.setVisibility(View.GONE);
                tvCostTip2.setVisibility(View.VISIBLE);
                break;
            case Order.ORDER_TYPE_SEND_CHILD: // 送小孩上学单
                //只展示线上支付
                llPayByCash.setVisibility(View.GONE);

                rlOtherCost.setVisibility(View.VISIBLE);
                divideLineOther.setVisibility(View.VISIBLE);

                rlServiceCost.setVisibility(View.VISIBLE);
                divideLineService.setVisibility(View.VISIBLE);

                rlTotalCost.setVisibility(View.VISIBLE);
                divideLineTotal.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }

    }


    /**
     * 根据输入的服务费计算总费用
     */
    @OnTextChanged(value = {R.id.et_base_cost, R.id.tv_base_cost_carpool})
    public void onTextChanged() {
        String costString = etBaseCost.getText().toString();
        if (!TextUtils.isEmpty(costString) || orderType == Order.ORDER_TYPE_REALTIME_CARPOOL) {
            tvPayByApp.setEnabled(true);
            tvPayByCash.setEnabled(true);
            llPayByApp.setEnabled(true);
            llPayByCash.setEnabled(true);
        }

        if (orderType == Order.ORDER_TYPE_SEND_CHILD) {
            if (("").equals(costString) || ("0").equals(costString)) {
                baseCost = 0;
            } else if (StringValidate(costString)) {
                baseCost = Double.valueOf(costString);
            } else {
                if (!carpoolFlag) { // 非拼车单
                    updateServiceCost(new ServiceFee());
                }
                return;
            }
            if (carpoolFlag) { // 非拼车单不反复调用服务费接口
                updateTotalCost();
            } else {
                presenter.getServiceCost(baseCost, orderId);
            }
        } else {
            updateTotalCost();
        }
    }

    @OnTextChanged(value = {R.id.other_cost})
    public void onOtherCostChanged() {
        updateTotalCost();
    }

    @OnClick({R.id.ll_pay_by_app, R.id.ll_pay_by_cash, R.id.iv_back})
    public void onClick(View view) {
        if (isFastClick()) {
            return;
        }
        int payType;
        switch (view.getId()) {
            case R.id.ll_pay_by_app:   //提交账单
                payType = PAY_BY_ONLINE;
                payRequeset(payType);
                break;
            case R.id.ll_pay_by_cash:   //已线下支付
                payType = PAY_BY_CASH;
                payRequeset(payType);
                break;
            case R.id.iv_back:
                finish();
                return;
            default:
                break;
        }
    }

    /**
     * 发起支付请求，订单状态转换成待支付状态
     *
     * @param payType --线上or 线下支付
     */
    private void payRequeset(int payType) {
        if (isPayRequesting) {
            LogUtils.d(TAG, "正在发起支付请求，请稍等。。。");
            return;
        }
        //输入有效性判断
        String costString;
        //基础费用
        costString = etBaseCost.getText().toString();
        if (StringValidate(costString)) {
            baseCost = Double.valueOf(costString);
        } else {
            toast(R.string.toast_please_enter_valid_fare);
            return;
        }

        //其他费用
        costString = etOtherCost.getText().toString();
        if (("").equals(costString) || ("0").equals(costString)) {
            otherCost = 0;
        } else if (StringValidate(costString)) {
            otherCost = Double.valueOf(costString);
        } else {
            toast(R.string.toast_please_enter_valid_fare);
            return;
        }

        if (baseCost >= COST_MAX_VALUE || otherCost >= COST_MAX_VALUE) {
            toast(R.string.toast_please_enter_valid_fare);
            return;
        }

        PayInfo payInfo = new PayInfo();
        if (carpoolFlag) {
            payInfo.setCarpoolCode(carpoolCode);
        } else {
            payInfo.setOrderId(orderId);
        }
        payInfo.setMoney(baseCost);
        payInfo.setOtherMoney(otherCost);
        payInfo.setServiceAmount(realServiceCost);
        payInfo.setPaymentMode(payType);
        isPayRequesting = true;
        if (carpoolFlag) {
            presenter.payRequestCarpool(payInfo);
        } else {
            presenter.payRequest(payInfo);
        }
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected PayMainContract.Presenter onCreatePresenter() {
        return new PayMainPresenter(this);
    }


    @Override
    public void payConfirmSuccess() {
        paymentActionCompleted(orderId);
    }

    private boolean isPayRequesting; //是否正在发起支付请求

    /**
     * 请求支付成功，包括提交账单 or 乘客已线下支付
     *
     * @param payType
     */
    @Override
    public void payRequestSuccess(int payType) {
        isPayRequesting = false;
        paymentActionCompleted(orderId);
    }

    /**
     * 请求支付失败
     */
    @Override
    public void payRequestFail() {
        isPayRequesting = false;
    }

    /**
     * 更新服务费
     *
     * @param serviceFee
     */
    @Override
    public void updateServiceCost(ServiceFee serviceFee) {
        //   tvServiceCost.setText("" + formatDouble(serviceFee));

        tvServiceCost.setText(BusinessUtils.moneySpec(serviceFee.getShowServiceFee()));
        serviceCost = serviceFee.getShowServiceFee();
        realServiceCost = serviceFee.getServiceFee();
        updateTotalCost();
    }

    @Override
    public void onCarpoolOrderPaid() {
        CarpoolTripEndActivity.start(getContext(), carpoolCode);
        finish();
    }


    /**
     * 计算服务费和总费用
     *
     * @param
     */
    public void updateTotalCost() {
        String costString;
        //基础费用
        costString = etBaseCost.getText().toString();
        if (("").equals(costString) || ("0").equals(costString)) {
            baseCost = 0;
        } else if (StringValidate(costString)) {
            baseCost = Double.valueOf(costString);
        } else {
            baseCost = 0;
        }

        //其他费用
        costString = etOtherCost.getText().toString();
        if (("").equals(costString) || ("0").equals(costString)) {
            otherCost = 0;
        } else if (StringValidate(costString)) {
            otherCost = Double.valueOf(costString);
        } else {
            otherCost = 0;
        }

        //总的费用
        double totalCost = baseCost + serviceCost + otherCost;
        LogUtils.d(TAG, "baseCost:" + baseCost + ",serviceCost:" + serviceCost + ",formatDouble(totalCost):" + formatDouble(totalCost));
        tvTotalCost.setText(BusinessUtils.moneySpec(totalCost));
    }

    private static String formatDouble(double d) {
        NumberFormat nf = NumberFormat.getInstance();
        //设置保留多少位小数
        nf.setMaximumFractionDigits(2);
        // 取消科学计数法
        nf.setGroupingUsed(false);
        //返回结果
        return nf.format(d);
    }

    /**
     * 接受支付完成的消息推送
     *
     * @param websocketData
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(WebsocketData websocketData) {
        // Log.d(TAG, "on event:" + websocketData.toString());
        if (("orderPaid").equals(websocketData.getCmd())) {
            int orderId = websocketData.getData().getOrderId();
            paymentActionCompleted(orderId);
        }
    }

    /**
     * 支付完成
     *
     * @param orderId
     */
    private void paymentActionCompleted(int orderId) {
        if (carpoolFlag) {
            CarpoolTripEndActivity.start(getContext(), carpoolCode);
        } else {
            TripEndActivity.go(this, orderId, true);
        }
        finish();

       /* Intent intent;
        if (orderType == Order.ORDER_TYPE_REALTIME || orderType == Order.ORDER_TYPE_SEND_CHILD || orderType == Order.ORDER_TYPE_CARGO_PASSENGER) {
            //实时订单/货拼客/送你上学单，跳转到行程结束页面
            intent = new Intent(this, TripEndActivity.class);
        } else {
            intent = new Intent(this, ShowInMapNewActivity.class);
        }
        intent.putExtra(ShowInMapNewActivity.ORDER_ID, orderId);
        startActivity(intent);
        finish();*/
    }

    /**
     * 动态隐藏软键盘
     *
     * @param activity activity
     */
    public static void hideSoftInput(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view == null) view = new View(activity);
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 动态显示软键盘
     *
     * @param edit 输入框
     */
    public static void showSoftInput(EditText edit, Context context) {
        if (edit == null) {
            return;
        }
        edit.setFocusable(true);
        edit.setFocusableInTouchMode(true);
        edit.requestFocus();
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(edit, 0);
    }

}
