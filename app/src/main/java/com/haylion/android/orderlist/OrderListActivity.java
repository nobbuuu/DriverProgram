package com.haylion.android.orderlist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.haylion.android.R;
import com.haylion.android.activity.OrderCompleteActivity;
import com.haylion.android.activity.PreScanActivity;
import com.haylion.android.data.model.EventMsg;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.model.OrderTimeType;
import com.haylion.android.data.model.WebsocketData;

import com.haylion.android.data.widgt.MassLoadingMoreFooter;
import com.haylion.android.data.base.BaseActivity;
import com.haylion.android.main.MainActivity;
import com.haylion.android.mvp.util.LogUtils;
import com.haylion.android.orderdetail.OrderDetailActivity;
import com.haylion.android.orderdetail.amapNavi.AMapNaviViewActivity;
import com.haylion.android.orderdetail.trip.TripDetailActivity;
import com.haylion.android.service.WsCommands;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.nlopez.smartadapters.SmartAdapter;
import io.nlopez.smartadapters.adapters.RecyclerMultiAdapter;
import io.nlopez.smartadapters.utils.ViewEventListener;

import static com.haylion.android.data.model.Order.ORDER_TYPE_SHUNFENG;


/**
 * 订单列表页面
 */
public class OrderListActivity extends BaseActivity<OrderListContract.Presenter> implements
        OrderListContract.View, ViewEventListener<Order> {
    private static final String TAG = "OrderListActivity";

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

    @BindView(R.id.tv_order_type_shunfeng)
    TextView tvTypeShunfeng;

    @BindView(R.id.ll_order_type_header)
    LinearLayout llOrderTypeHeader;
    @BindView(R.id.line_order_type_passenger)
    View lineOrderTypePassenger;
    @BindView(R.id.line_order_type_goods)
    View lineOrderTypeGoods;

    @BindView(R.id.line_order_type_shunfeng)
    View lineOrderTypeShunfeng;
    @BindView(R.id.smartRefresh)
    SmartRefreshLayout smartRefresh;

    private static final String EXTRA_ORDER_TYPE = "order_type";
    private static final String IS_FORCE_BACK_TO_MAIN = "isForceBackToMain";

    private OrderTimeType mOrderTimeType;   //订单类型
    private int orderType = 1;   //订单类型
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
        start(context,type,false);
    }

    /**
     * 跳转
     * @param context
     * @param type     类型
     * @param isForceBackToMain 是否强制返回听单页
     */
    public static void start(Context context, OrderTimeType type,boolean isForceBackToMain) {
        Intent intent = new Intent(context, OrderListActivity.class);
        intent.putExtra(EXTRA_ORDER_TYPE, type);
        intent.putExtra(IS_FORCE_BACK_TO_MAIN,isForceBackToMain);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_list_act);
        isForceBackToMain = getIntent().getBooleanExtra(IS_FORCE_BACK_TO_MAIN,false);
        mOrderTimeType = parseOrderType();
        if (mOrderTimeType == OrderTimeType.HISTORY) {
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
        }
        initFooterView();
        EventBus.getDefault().register(this);
        initEvent();
    }

    private void initEvent() {
        smartRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                presenter.refreshOrderList(orderType);
            }
        });
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

    @OnClick({R.id.iv_back, R.id.ll_order_type_passenger, R.id.ll_order_type_goods,R.id.ll_order_type_shunfeng})
    public void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.ll_order_type_passenger:  //客单
                orderType = 1;
                tvTypePassenger.setTextColor(getResources().getColor(R.color.maas_text_blue));
                tvTypeGoods.setTextColor(getResources().getColor(R.color.maas_text_primary));
                tvTypeShunfeng.setTextColor(getResources().getColor(R.color.maas_text_primary));
                lineOrderTypePassenger.setVisibility(View.VISIBLE);
                lineOrderTypeGoods.setVisibility(View.GONE);
                lineOrderTypeShunfeng.setVisibility(View.GONE);

                showPassenger = true;

                presenter.setOrderType(true);
                mAdapter = SmartAdapter.empty()
                        .map(Order.class, OrderListItemView.class)
                        .listener(this)
                        .into(mOrderList);
                presenter.refreshOrderList(orderType);
                break;
            case R.id.ll_order_type_goods:  //货单
                orderType = 2;
                tvTypePassenger.setTextColor(getResources().getColor(R.color.maas_text_primary));
                tvTypeShunfeng.setTextColor(getResources().getColor(R.color.maas_text_primary));
                tvTypeGoods.setTextColor(getResources().getColor(R.color.maas_text_blue));
                lineOrderTypePassenger.setVisibility(View.GONE);
                lineOrderTypeShunfeng.setVisibility(View.GONE);
                lineOrderTypeGoods.setVisibility(View.VISIBLE);

                showPassenger = false;

                presenter.setOrderType(false);
                mAdapter = SmartAdapter.empty()
                        .map(Order.class, OrderGoodsListItemView.class)
                        .listener(this)
                        .into(mOrderList);
                presenter.refreshOrderList(orderType);
                break;
            case R.id.ll_order_type_shunfeng:  //顺丰单
                orderType = 3;
                tvTypePassenger.setTextColor(getResources().getColor(R.color.maas_text_primary));
                tvTypeGoods.setTextColor(getResources().getColor(R.color.maas_text_primary));
                tvTypeShunfeng.setTextColor(getResources().getColor(R.color.maas_text_blue));
                lineOrderTypePassenger.setVisibility(View.GONE);
                lineOrderTypeGoods.setVisibility(View.GONE);
                lineOrderTypeShunfeng.setVisibility(View.VISIBLE);

                showPassenger = false;
                presenter.setOrderType(false);
                mAdapter = SmartAdapter.empty()
                        .map(Order.class, OrderListItemView.class)
                        .listener(this)
                        .into(mOrderList);
                presenter.refreshOrderList(orderType);
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
                presenter.refreshOrderList(orderType);
            }

            @Override
            public void onLoadMore() {
                presenter.loadMoreOrders();
            }
        });
        mOrderList.setEmptyView(mEmptyView);
        mAdapter = SmartAdapter.empty()
                .map(Order.class, OrderListItemView.class)
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
        smartRefresh.finishRefresh();
        Log.d("aaa","setOrderList list =" + list);
        if (list == null || list.isEmpty()) {
            if (mOrderTimeType == OrderTimeType.HISTORY) {
                mEmptyView.setText("暂无历史订单");
            } else if (mOrderTimeType == OrderTimeType.TODAY) {
                mEmptyView.setText("今日暂无服务订单");
            } else {
                mEmptyView.setText(R.string.order_empty);
            }
        }else {
            Log.d("aaa","setOrderList  list,size() = " + list.size());
            mAdapter.setItems(list);
            mOrderList.refreshComplete();
        }

//        LogUtils.d(TAG, "showPassenger:" + showPassenger + ", orderType:" + list.get(0).getOrderType());
        /*if (showPassenger && (list != null && list.size() > 0 && list.get(0).getOrderType() != Order.ORDER_TYPE_CARGO)) {
            LogUtils.d(TAG, "passenger, show:");
        } else if (!showPassenger && (list != null && list.size() > 0 && list.get(0).getOrderType() == Order.ORDER_TYPE_CARGO)) {
            LogUtils.d(TAG, "cargo, show:");
        } else {
            LogUtils.d(TAG, "return:");
            return;
        }*/
    }

    /**
     * 数据-加载更多成功
     *
     * @param list
     */
    @Override
    public void addMoreOrders(List<Order> list) {
        smartRefresh.finishRefresh();
        mAdapter.addItems(list);
        mOrderList.loadMoreComplete();
    }

    /**
     * 数据-没有更多
     */
    @Override
    public void noMoreOrders() {
        smartRefresh.finishRefresh();
        mOrderList.setNoMore(true);
    }

    @Override
    public void onViewEvent(int actionType, Order order, int position, View view) {
        if (order.getOrderType() == ORDER_TYPE_SHUNFENG){
            switch (order.getOrderStatus()){
//                        待开始 = 0、待到店 = 1、待扫描=2、待取货签名=3、送货中=4、已完成=5
                case 0:
                case 1:
                case 4:
                    OrderDetailActivity.go(getContext(), order.getOrderId(), ORDER_TYPE_SHUNFENG);
                    break;
                case 2:
                case 3:
                    PreScanActivity.go(getContext(),order.getOrderId());
                    break;
                case 5:
                    OrderCompleteActivity.go(getContext(), order.getOrderId(),2);
                    break;
            }
        }else {
            if (actionType == OrderClickArea.CONTACT_PASSENGER
                    || actionType == OrderClickArea.CONTACT_SEND_PASSENGER
                    || actionType == OrderClickArea.CONTACT_RECEIVE_PASSENGER) {
                showCallDialog(actionType, order);
            } else if (actionType == OrderClickArea.SHOW_IN_MAP) {
                //实时订单/送小孩单/货拼客单 跳转到新的行程详情页面
                TripDetailActivity.go(this, order.getOrderId(), false);

            /*if (order.getOrderType() == Order.ORDER_TYPE_REALTIME || order.getOrderType() == Order.ORDER_TYPE_SEND_CHILD
                    || order.getOrderType() == Order.ORDER_TYPE_CARGO_PASSENGER || order.getOrderType() == Order.ORDER_TYPE_CARGO) {

            } else {
                Intent intent = new Intent(this, ShowInMapNewActivity.class);
                intent.putExtra(ShowInMapNewActivity.ORDER_ID, order.getOrderId());
                startActivity(intent);
            }*/
            }
        }
    }

    /**
     * 显示拨打电话的弹窗,已废弃
     *
     * @param actionType
     * @param order
     */
    private void showCallDialog(int actionType, Order order) {
        String phoneNum;
        if (actionType == OrderClickArea.CONTACT_PASSENGER) {
            phoneNum = order.getUserInfo().getVirtualNum();
        } else if (actionType == OrderClickArea.CONTACT_SEND_PASSENGER) {
            phoneNum = order.getPickupContactMobile();
        } else if (actionType == OrderClickArea.CONTACT_RECEIVE_PASSENGER) {
            phoneNum = order.getDropOffContactMobile();
        } else {
            LogUtils.e(TAG, "未识别的的操作");
            return;
        }

      /*  ConfirmDialog dialog = ConfirmDialog.newInstance();
        dialog.setMessage(StringUtil.maskPhoneNumber(phoneNum)).setOnClickListener(new ConfirmDialog.OnClickListener() {
            @Override
            public void onPositiveClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phoneNum));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }).setPositiveText(R.string.order_phone_call).show(getSupportFragmentManager(), "");*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mOrderList != null) {
            mOrderList.destroy();
            mOrderList = null;
        }
    }

    @Override
    protected OrderListContract.Presenter onCreatePresenter() {
        return new OrderListPresenter(parseOrderType(), this);
    }

    private OrderTimeType parseOrderType() {
        //todo
        return (OrderTimeType) getIntent().getSerializableExtra(EXTRA_ORDER_TYPE);
//        return (OrderTimeType) getIntent().getIntExtra(EXTRA_ORDER_TYPE, OrderTimeType.TODAY);
    }

    @Override
    public void onBackPressed() {
        if(isForceBackToMain){
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }else{
            super.onBackPressed();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventMsg data) {
        Log.d(TAG, "on event:" + data.toString());
        if(data.getCmd() == EventMsg.CMD_ORDER_STATUS_TO_CONTROVERSY){
            //争议状态
            presenter.refreshOrderList(orderType);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(WebsocketData websocketData) {
        Log.d(TAG, "on event:" + websocketData.toString());
        if (websocketData.getCmdSn() == WsCommands.DRIVER_ORDER_PAID.getSn()) {
            //支付完成
            presenter.refreshOrderList(orderType);
        }
    }
}
