package com.haylion.android.orderlist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.haylion.android.R;
import com.haylion.android.common.view.dialog.DialogUtils;
import com.haylion.android.data.base.BaseActivity;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.model.OrderTimeType;
import com.haylion.android.data.widgt.MassLoadingMoreFooter;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.nlopez.smartadapters.SmartAdapter;
import io.nlopez.smartadapters.adapters.RecyclerMultiAdapter;
import io.nlopez.smartadapters.utils.ViewEventListener;


/**
 * @class BookOrderListActivity
 * @description 预约订单列表展示页面，基于迭代15版本做的紧急需求。
 * @date: 2020/2/26 10:08
 * @author: tandongdong
 */
public class BookOrderListActivity extends BaseActivity<BookOrderListContract.Presenter> implements
        BookOrderListContract.View, ViewEventListener<Order> {
    private static final String TAG = "BookOrderListActivity";

    @BindView(R.id.rl_main)
    RelativeLayout rlMain;
    @BindView(R.id.order_list)
    XRecyclerView mOrderList;

    @BindView(R.id.empty_view)
    TextView mEmptyView;

    @BindView(R.id.header_name)
    TextView tvHeaderName;

    @BindView(R.id.tv_order_type_passenger)
    TextView tvTypePassenger;

    @BindView(R.id.tv_order_type_goods)
    TextView tvTypeGoods;

    @BindView(R.id.ll_order_type_header)
    LinearLayout llOrderTypeHeader;
    @BindView(R.id.line_order_type_passenger)
    View lineOrderTypePassenger;
    @BindView(R.id.line_order_type_goods)
    View lineOrderTypeGoods;

    private static final String EXTRA_ORDER_TYPE = "order_type";
    private static final String IS_FORCE_BACK_TO_MAIN = "isForceBackToMain";

    private OrderTimeType mOrderTimeType;   //订单类型
    private RecyclerMultiAdapter mAdapter;

    boolean showPassenger = true;
    private boolean isForceBackToMain = false;     //是否强制返回听单页

    /**
     * 跳转
     *
     * @param context
     * @param type
     */
    public static void start(Context context, OrderTimeType type) {
        start(context, type, false);
    }

    /**
     * 跳转
     *
     * @param context
     * @param type              类型
     * @param isForceBackToMain 是否强制返回听单页
     */
    public static void start(Context context, OrderTimeType type, boolean isForceBackToMain) {
        Intent intent = new Intent(context, BookOrderListActivity.class);
        intent.putExtra(EXTRA_ORDER_TYPE, type);
        intent.putExtra(IS_FORCE_BACK_TO_MAIN, isForceBackToMain);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_list_act);
        isForceBackToMain = getIntent().getBooleanExtra(IS_FORCE_BACK_TO_MAIN, false);
        mOrderTimeType = parseOrderType();
/*        if (mOrderTimeType == OrderTimeType.HISTORY) {
            tvHeaderName.setText("历史订单");
            llOrderTypeHeader.setVisibility(View.VISIBLE);
        } else if (mOrderTimeType == OrderTimeType.PLANNED) {
            tvHeaderName.setText("计划订单");
        } else if (mOrderTimeType == OrderTimeType.IN_PROGRESS) {
            tvHeaderName.setText("进行中的订单");
        } else if (mOrderTimeType == OrderTimeType.TODAY) {
            tvHeaderName.setText("今日完成客单");
          //  rlMain.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
            llOrderTypeHeader.setVisibility(View.GONE);
            //  mEmptyView.setText("今日暂无服务订单");
        }*/

        tvHeaderName.setText("预约订单");
        llOrderTypeHeader.setVisibility(View.GONE);

        initFooterView();
//        EventBus.getDefault().register(this);
    }

    /**
     * 自定义 footerView
     */
    private void initFooterView() {
        MassLoadingMoreFooter footerView = new MassLoadingMoreFooter(this);
        mOrderList.setFootView(footerView, footerView.callBack);
    }

    @OnClick(R.id.empty_view)
    public void onEmptyViewClick() {
        if (mOrderTimeType == OrderTimeType.HISTORY) {
            return;
        }
        mOrderList.refresh();
        Toast.makeText(this, "刷新", Toast.LENGTH_SHORT).show();
    }

    @OnClick({R.id.iv_back, R.id.ll_order_type_passenger, R.id.ll_order_type_goods})
    public void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.ll_order_type_passenger:  //客单
                tvTypePassenger.setTextColor(getResources().getColor(R.color.maas_text_blue));
                tvTypeGoods.setTextColor(getResources().getColor(R.color.maas_text_primary));
                lineOrderTypePassenger.setVisibility(View.VISIBLE);
                lineOrderTypeGoods.setVisibility(View.GONE);

                showPassenger = true;

                presenter.setOrderType(true);
                mAdapter = SmartAdapter.empty()
                        .map(Order.class, BookOrderListItemView.class)
                        .listener(this)
                        .into(mOrderList);
                presenter.refreshOrderList();
                break;
            case R.id.ll_order_type_goods:  //货单
                tvTypePassenger.setTextColor(getResources().getColor(R.color.maas_text_primary));
                tvTypeGoods.setTextColor(getResources().getColor(R.color.maas_text_blue));
                lineOrderTypePassenger.setVisibility(View.GONE);
                lineOrderTypeGoods.setVisibility(View.VISIBLE);

                showPassenger = false;

                presenter.setOrderType(false);
                mAdapter = SmartAdapter.empty()
                        .map(Order.class, OrderGoodsListItemView.class)
                        .listener(this)
                        .into(mOrderList);
                presenter.refreshOrderList();
                break;
            default:
                break;
        }
    }


    /**
     * 创建Presenter之前执行的逻辑
     */
    @Override
    protected void beforeCreatePresenter() {
        mOrderList.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                presenter.refreshOrderList();
            }

            @Override
            public void onLoadMore() {
                presenter.loadMoreOrders();
            }
        });
        mOrderList.setEmptyView(mEmptyView);
        mAdapter = SmartAdapter.empty()
                .map(Order.class, BookOrderListItemView.class)
                .listener(this)
                .into(mOrderList);
    }


    /**
     * 数据-刷新成功
     *
     * @param list
     */
    @Override
    public void setOrderList(List<Order> list) {
        if (list == null || list.isEmpty()) {
/*            if (mOrderTimeType == OrderTimeType.HISTORY) {
                mEmptyView.setText("暂无历史订单");
            } else if (mOrderTimeType == OrderTimeType.TODAY) {
                mEmptyView.setText("今日暂无服务订单");
            } else {
                mEmptyView.setText(R.string.order_empty);
            }*/
            mEmptyView.setText(R.string.order_empty);
        }

//        LogUtils.d(TAG, "showPassenger:" + showPassenger + ", orderType:" + list.get(0).getOrderType());
/*
        if (showPassenger && (list != null && list.size() > 0 && list.get(0).getOrderType() != Order.ORDER_TYPE_CARGO)) {
            LogUtils.d(TAG, "passenger, show:");
        } else if (!showPassenger && (list != null && list.size() > 0 && list.get(0).getOrderType() == Order.ORDER_TYPE_CARGO)) {
            LogUtils.d(TAG, "cargo, show:");
        } else {
            LogUtils.d(TAG, "return:");
            return;
        }
*/

        mAdapter.setItems(list);
        mOrderList.refreshComplete();
    }

    /**
     * 数据-加载更多成功
     *
     * @param list
     */
    @Override
    public void addMoreOrders(List<Order> list) {
        mAdapter.addItems(list);
        mOrderList.loadMoreComplete();
    }

    /**
     * 数据-没有更多
     */
    @Override
    public void noMoreOrders() {
        mOrderList.setNoMore(true);
    }

    @Override
    public void onViewEvent(int actionType, Order order, int position, View view) {
        if (actionType == OrderClickArea.CONTACT_PASSENGER
                || actionType == OrderClickArea.CONTACT_SEND_PASSENGER
                || actionType == OrderClickArea.CONTACT_RECEIVE_PASSENGER) {
            DialogUtils.showRealCallDialog(this, order.getUserInfo().getPhoneNum());
        } else if (actionType == OrderClickArea.SHOW_IN_MAP) {
            //实时订单/送小孩单/货拼客单 跳转到新的行程详情页面
//            TripDetailActivity.go(this, order.getOrderId(), false);

            /*if (order.getOrderType() == Order.ORDER_TYPE_REALTIME || order.getOrderType() == Order.ORDER_TYPE_SEND_CHILD
                    || order.getOrderType() == Order.ORDER_TYPE_CARGO_PASSENGER || order.getOrderType() == Order.ORDER_TYPE_CARGO) {

            } else {
                Intent intent = new Intent(this, ShowInMapNewActivity.class);
                intent.putExtra(ShowInMapNewActivity.ORDER_ID, order.getOrderId());
                startActivity(intent);
            }*/
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(this);
        if (mOrderList != null) {
            mOrderList.destroy();
            mOrderList = null;
        }
    }

    @Override
    protected BookOrderListContract.Presenter onCreatePresenter() {
        return new BookOrderListPresenter(parseOrderType(), this);
    }

    private OrderTimeType parseOrderType() {
        return (OrderTimeType) getIntent().getSerializableExtra(EXTRA_ORDER_TYPE);
//        return (OrderTimeType) getIntent().getIntExtra(EXTRA_ORDER_TYPE, OrderTimeType.TODAY);
    }

    @Override
    public void onBackPressed() {
/*        if(isForceBackToMain){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }else{
            super.onBackPressed();
        }*/

        super.onBackPressed();
    }

/*    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventMsg data) {
        Log.d(TAG, "on event:" + data.toString());
        if(data.getCmd() == EventMsg.CMD_ORDER_STATUS_TO_CONTROVERSY){
            //争议状态
            presenter.refreshOrderList();
        }
    }*/

/*    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(WebsocketData websocketData) {
        Log.d(TAG, "on event:" + websocketData.toString());
        if (websocketData.getCmdSn() == WsCommands.DRIVER_ORDER_PAID.getSn()) {
            //支付完成
            presenter.refreshOrderList();
        }
    }*/
}
