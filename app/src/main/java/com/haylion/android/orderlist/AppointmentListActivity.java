package com.haylion.android.orderlist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.ListPopupWindow;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.haylion.android.R;
import com.haylion.android.calendar.DateFormatUtil;
import com.haylion.android.calendar.DateStyle;
import com.haylion.android.common.view.dialog.DialogUtils;
import com.haylion.android.constract.ChoseDateCallBack;
import com.haylion.android.constract.ClaimActionListener;
import com.haylion.android.data.base.BaseActivity;
import com.haylion.android.data.base.BaseDialog;
import com.haylion.android.data.model.AddressInfo;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.model.OrderPayInfo;
import com.haylion.android.data.model.OrderStatus;
import com.haylion.android.dialog.ChoseDateDialog;
import com.haylion.android.dialog.ClaimDialog;
import com.haylion.android.mvp.util.LogUtils;
import com.haylion.android.mvp.util.ToastUtils;
import com.haylion.android.orderdetail.OrderDetailActivity;
import com.haylion.android.orderdetail.map.ShowInMapNewActivity;
import com.haylion.android.orderdetail.trip.TripDetailActivity;
import com.haylion.android.pay.PayMainActivity;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindFont;
import butterknife.BindView;
import butterknife.OnClick;
import io.nlopez.smartadapters.SmartAdapter;
import io.nlopez.smartadapters.adapters.RecyclerMultiAdapter;
import io.nlopez.smartadapters.utils.ViewEventListener;

/**
 * 预约订单列表页面
 */
public class AppointmentListActivity extends BaseActivity<AppointmentListContract.Presenter>
        implements AppointmentListContract.View, ViewEventListener<Order> {

    @BindView(R.id.appointment_list)
    XRecyclerView mAppointmentList;
    private RecyclerMultiAdapter mAppointmentAdapter;

    @BindView(R.id.tab_tips)
    TextView mTabTips;

    @BindView(R.id.tab_indicator)
    RadioGroup mTabIndicator;

    @OnClick(R.id.btn_back)
    void onBackClick() {
        finish();
    }

    @BindView(R.id.no_orders)
    TextView mNoOrders;

    private GrabAppointmentDialog mGrabDialog;

    public static void start(Context context) {
        Intent intent = new Intent(context, AppointmentListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_list);
        mAppointmentAdapter = SmartAdapter.empty()
                .map(Order.class, BookOrderListItemView.class)
                .listener(this)
                .into(mAppointmentList);
        mAppointmentList.setPullRefreshEnabled(true);
        mAppointmentList.setLoadingMoreEnabled(false);
        mAppointmentList.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                int checkedRadioButtonId = mTabIndicator.getCheckedRadioButtonId();
                if (checkedRadioButtonId == R.id.tab_hall) {
                    presenter.refreshAppointmentHall();
                } else if (checkedRadioButtonId == R.id.tab_children) {
                    presenter.refreshChildrenOrderCenter();
                } else if (checkedRadioButtonId == R.id.tab_accessibility) {
                    presenter.refreshAccessibilityOrder();
                } else if (checkedRadioButtonId == R.id.tab_shunfeng) {
                    presenter.refreshShunfengList();
                } else {
                    presenter.refreshAppointmentList();
                }
            }

            @Override
            public void onLoadMore() {
            }
        });
        //订单tab切换
        mTabIndicator.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                View childView = radioGroup.getChildAt(i);
                if (childView instanceof RadioButton) {
                    RadioButton tab = (RadioButton) childView;
                    changeTabStyle(checkedId, tab);
                }
            }
            mAppointmentList.refreshComplete();
            if (checkedId == R.id.tab_shunfeng) {
                presenter.getShunfengOrder();
            } else if (checkedId == R.id.tab_hall) {
                presenter.appointmentHall();
                mTabTips.setText(R.string.tips_appointment_hall);
            } else if (checkedId == R.id.tab_children) {
                presenter.childrenOrderCenter();
                // 新迭代后，标签提示信息不再显示
            } else if (checkedId == R.id.tab_accessibility) { // 女性专车订单
                presenter.getAccessibilityOrder();

            } else {
                presenter.getAppointmentList();
                mTabTips.setText(R.string.tips_appointment_unfinished);
            }
        });
        presenter.getShunfengOrder(); // 初始显示预约大厅
    }

    private void changeTabStyle(int checkedTabId, RadioButton tab) {
        if (checkedTabId == tab.getId()) {
            tab.setTypeface(null, Typeface.BOLD);
//            tab.setTextSize(TypedValue.COMPLEX_UNIT_PX,
//                    getResources().getDimension(R.dimen.sp_18));
            tab.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0,
                    0, R.drawable.tab_indicator);
        } else {
            tab.setTypeface(null, Typeface.NORMAL);
//            tab.setTextSize(TypedValue.COMPLEX_UNIT_PX,
//                    getResources().getDimensionPixelSize(R.dimen.sp_16));
            tab.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0,
                    0, 0);
        }
    }

    @Override
    protected AppointmentListContract.Presenter onCreatePresenter() {
        return new AppointmentListPresenter(this);
    }

    @Override
    public void showAppointmentList(List<Order> unfinished, List<Order> finished) {
        int checkedTab = mTabIndicator.getCheckedRadioButtonId();
        if (checkedTab == R.id.tab_unfinished) {
            mAppointmentAdapter.setItems(unfinished);
            checkOrdersEmpty(unfinished, R.id.tab_unfinished);
        } else if (checkedTab == R.id.tab_finished) {
            mAppointmentAdapter.setItems(finished);
            checkOrdersEmpty(finished, R.id.tab_finished);
        } else {
            return;
        }
        mAppointmentList.refreshComplete();
        scrollListToTop();
    }

    private void checkOrdersEmpty(List<Order> orders, int orderTab) {
        boolean noOrders = false;
        if (orders == null || orders.isEmpty()) {
            if (orderTab == mTabIndicator.getCheckedRadioButtonId()) {
                if (orderTab == R.id.tab_shunfeng) {
                    mNoOrders.setText("没有顺丰订单");
                } else if (orderTab == R.id.tab_hall) {
                    mNoOrders.setText("没有预约订单");
                } else if (orderTab == R.id.tab_unfinished) {
                    mNoOrders.setText("没有未完成订单");
                } else if (orderTab == R.id.tab_finished) {
                    mNoOrders.setText("没有已完成订单");
                } else if (orderTab == R.id.tab_children) {
                    mNoOrders.setText("没有送你上学订单");
                } else if (orderTab == R.id.tab_accessibility) {
                    mNoOrders.setText("没有女性专车订单");
                }
                noOrders = true;
            }
        }
        if (noOrders) {
            mNoOrders.setVisibility(View.VISIBLE);
        } else {
            mNoOrders.setVisibility(View.GONE);
        }
    }

    @Override
    public void showAppointmentHall(List<Order> orders) {
        if (mTabIndicator.getCheckedRadioButtonId() != R.id.tab_hall) {
            return;
        }
        mAppointmentAdapter.setItems(orders);
        checkOrdersEmpty(orders, R.id.tab_hall);
        mAppointmentList.refreshComplete();
        scrollListToTop();
    }

    @Override
    public void showChildrenOrderCenter(List<Order> childrenOrders) {
        if (mTabIndicator.getCheckedRadioButtonId() != R.id.tab_children) {
            return;
        }
        mAppointmentAdapter.setItems(childrenOrders);
        checkOrdersEmpty(childrenOrders, R.id.tab_children);
        mAppointmentList.refreshComplete();
        scrollListToTop();
    }

    @Override
    public void showAccessibilityOrders(List<Order> accessibilityOrders) {
        if (mTabIndicator.getCheckedRadioButtonId() != R.id.tab_accessibility) {
            return;
        }
        mAppointmentAdapter.setItems(accessibilityOrders);
        checkOrdersEmpty(accessibilityOrders, R.id.tab_accessibility);
        mAppointmentList.refreshComplete();
        scrollListToTop();
    }

    @Override
    public void showShunfengOrders(List<Order> shunfengOrders) {
        if (mTabIndicator.getCheckedRadioButtonId() != R.id.tab_shunfeng) {
            return;
        }
        mAppointmentAdapter.setItems(shunfengOrders);
        checkOrdersEmpty(shunfengOrders, R.id.tab_shunfeng);
        mAppointmentList.refreshComplete();
        scrollListToTop();
    }

    private void scrollListToTop() {
        mAppointmentList.post(() -> {
            LinearLayoutManager layoutManager = (LinearLayoutManager)
                    mAppointmentList.getLayoutManager();
            if (layoutManager != null) {
                layoutManager.scrollToPositionWithOffset(0, 0);
            }
        });
    }

    @Override
    public void onViewEvent(int actionType, Order order, int pos, View view) {
        if (actionType == OrderClickArea.CONTACT_PASSENGER
                || actionType == OrderClickArea.CONTACT_SEND_PASSENGER
                || actionType == OrderClickArea.CONTACT_RECEIVE_PASSENGER) {
            DialogUtils.showRealCallDialog(this, order.getUserInfo().getPhoneNum());

        } else if (actionType == OrderClickArea.ORDER_DETAILS) {
            OrderStatus status = OrderStatus.forStatus(order.getOrderStatus());
            if (status == OrderStatus.ORDER_STATUS_INIT) {
                return;  // 预约大厅订单不可查看详情
            }
            if (status == OrderStatus.ORDER_STATUS_GET_OFF) {  // 乘客已下车
                OrderPayInfo orderPayInfo = new OrderPayInfo();
                orderPayInfo.setOrderId(order.getOrderId());
                orderPayInfo.setOrderType(order.getOrderType());
                orderPayInfo.setOrderChannel(order.getChannel());
                orderPayInfo.setCost(order.getTotalMoney());
                PayMainActivity.start(this, orderPayInfo);

            } else if (status == OrderStatus.ORDER_STATUS_WAIT_PAY) { // 订单等待支付
                TripDetailActivity.go(getContext(), order.getOrderId(), true);

            } else if (status == OrderStatus.ORDER_STATUS_CLOSED ||
                    status == OrderStatus.ORDER_STATUS_CANCELED) { // 订单已完成
                AppointmentDetailsActivity.start(getContext(), order.getOrderId());

            } else {
                OrderDetailActivity.go(getContext(), order.getOrderId());
            }

        } else if (actionType == OrderClickArea.SHOW_IN_MAP) {
            Intent intent = new Intent(getContext(), ShowInMapNewActivity.class);
            intent.putExtra(ShowInMapNewActivity.EXTRA_GRAB_ENABLED, false);
            AddressInfo start = order.getStartAddr();
            intent.putExtra(ShowInMapNewActivity.ORDER_START_ADDR, start);
            AddressInfo end = order.getEndAddr();
            intent.putExtra(ShowInMapNewActivity.ORDER_END_ADDR, end);
            startActivity(intent);

        } else if (actionType == BookOrderListItemView.ACTION_ORDER_DATES) {
//            showOrderDates(order.getOrderDates());

        } else if (actionType == BookOrderListItemView.ACTION_GRAB_ORDER) {
           /* if (mGrabDialog == null) {
                mGrabDialog = new GrabAppointmentDialog(getContext());
            }
            mGrabDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    presenter.grabOrder(order);
                }
            });*/
            List<String> orderDates = order.getOrderDates();
            if (orderDates != null && orderDates.size() > 0) {
                ChoseDateDialog choseDateDialog = new ChoseDateDialog(this, orderDates);
                choseDateDialog.setCallBack(new ChoseDateCallBack() {
                    @Override
                    public void callBack(Map<Long, Boolean> map) {
                        List<String> list = new ArrayList<>();
                        for (Map.Entry<Long, Boolean> ma : map.entrySet()) {
                            if (ma.getValue()) {
                                String ymd = DateFormatUtil.getTime(ma.getKey(), DateStyle.YYYY_MM_DD.getValue());
                                list.add(ymd + " 00:00:00");
                            }
                        }
                        if (list.size()>0){
                            choseDateDialog.dismiss();
                            ClaimDialog dialog = new ClaimDialog(getContext(), order, map);
                            dialog.setClaimListaner(new ClaimActionListener() {
                                @Override
                                public void onClaim() {
                                    presenter.grabOrder(order, list);
                                }
                            });
                            dialog.showDialog();
                        }else {
                            ToastUtils.showLong(getContext(), "请选择送货日期");
                        }
                    }
                });
                choseDateDialog.show();
            } else {
                ClaimDialog dialog = new ClaimDialog(this, order, null);
                dialog.setClaimListaner(new ClaimActionListener() {
                    @Override
                    public void onClaim() {
                        presenter.grabOrder(order, new ArrayList<>());
                    }
                });
                dialog.showDialog();
            }
        }
    }

    private void showOrderDates(List<String> orderDates) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, orderDates);
        builder.setAdapter(adapter, null).show();
    }

    @Override
    public void dismissGrabDialog() {
        if (mGrabDialog != null && mGrabDialog.isShowing()) {
            mGrabDialog.dismiss();
        }
    }

    static class GrabAppointmentDialog extends BaseDialog {

        GrabAppointmentDialog(@NonNull Context context) {
            super(context, ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.grab_appointment_dlg);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAppointmentList != null) {
            mAppointmentList.destroy();
            mAppointmentList = null;
        }
        dismissGrabDialog();
    }

}
