package com.haylion.android.orderlist.achievement;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.haylion.android.R;
import com.haylion.android.data.base.BaseActivity;
import com.haylion.android.data.model.OrderAbstract;
import com.haylion.android.data.model.OrderTimeType;
import com.haylion.android.data.widgt.AchievementLoadingMoreFooter;
import com.haylion.android.orderlist.OrderListActivity;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.nlopez.smartadapters.SmartAdapter;
import io.nlopez.smartadapters.adapters.RecyclerMultiAdapter;
import io.nlopez.smartadapters.utils.ViewEventListener;


/**
 * 历史成就页面
 */
public class AchievementListActivity extends BaseActivity<AchievementListContract.Presenter> implements
        AchievementListContract.View, ViewEventListener<OrderAbstract> {
    private static final String TAG = "AchievementListActivity";

    @BindView(R.id.order_list)
    XRecyclerView mDataList;

    @BindView(R.id.empty_view)
    TextView mEmptyView;

    @BindView(R.id.header_name)
    TextView tvHeaderName;

    private static final String EXTRA_ORDER_TYPE = "order_type";
    private RecyclerMultiAdapter mAdapter;
    private OrderTimeType mOrderTimeType;

    @OnClick(R.id.empty_view)
    public void onEmptyViewClick() {
        if (mOrderTimeType == OrderTimeType.HISTORY) {
            return;
        }
        mDataList.refresh();
        Toast.makeText(this, "刷新", Toast.LENGTH_SHORT).show();
    }

    @OnClick({R.id.iv_back})
    public void onButtonClick(View view) {
        if (view.getId() == R.id.iv_back) {
            finish();
        }
    }


    public static void start(Context context, OrderTimeType type) {
        Intent intent = new Intent(context, OrderListActivity.class);
        Log.d(TAG, "type:" + type);
        intent.putExtra(EXTRA_ORDER_TYPE, type);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化view:
        setContentView(R.layout.achievement_list_act);
        initFooterView();
    }

    /**
     * 自定义 footerView
     */
    private void initFooterView() {
        AchievementLoadingMoreFooter footerView = new AchievementLoadingMoreFooter(this);
        footerView.setLoadingDoneHint("显示更多历史成就");

        footerView.setAddMoreViewCallBack(new AchievementLoadingMoreFooter.ViewClickCallBack() {
            @Override
            public void addMoreViewClick() {
                presenter.loadMoreDataByMonth();
            }
        });
        mDataList.setFootView(footerView, footerView.callBack);
    }

    @Override
    protected void beforeCreatePresenter() {
        mDataList.setPullRefreshEnabled(false);
//        mDataList.setLoadingMoreEnabled(false);
        mDataList.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
//                presenter.refreshOrderList();
                presenter.loadCurrentMonthData();
            }

            @Override
            public void onLoadMore() {
//                presenter.loadMoreOrders();
                mDataList.loadMoreComplete();
            }
        });
        mDataList.setEmptyView(mEmptyView);
        mAdapter = SmartAdapter.empty()
                .map(OrderAbstract.class, AchievementItemView.class)
                .listener(this)
                .into(mDataList);
//        mDataList.addItemDecoration(new VerticalSpaceItemDecoration(SizeUtil.dp2px(16)));
    }


    @Override
    public void setOrderList(List<OrderAbstract> list) {
        if (list == null || list.isEmpty()) {
            mEmptyView.setText(R.string.data_empty);
        }
        mAdapter.setItems(list);
        mDataList.refreshComplete();
    }

    @Override
    public void addMoreOrders(List<OrderAbstract> list) {
        mAdapter.addItems(list);
        mDataList.loadMoreComplete();
    }


    @Override
    public void noMoreOrders() {
        mDataList.setNoMore(true);
    }

    @Override
    public void onViewEvent(int actionType, OrderAbstract order, int position, View view) {
/*        if (actionType == OrderClickArea.CONTACT_PASSENGER) {
            ConfirmDialog dialog = ConfirmDialog.newInstance();
            dialog.setMessage(StringUtil.maskPhoneNumber(order.getUserInfo().getPhoneNum())).setOnClickListener(new ConfirmDialog.OnClickListener() {
                @Override
                public void onPositiveClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + order.getUserInfo().getPhoneNum()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }).setPositiveText(R.string.order_phone_call).show(getSupportFragmentManager(), "");
        }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDataList != null) {
            mDataList.destroy();
            mDataList = null;
        }
    }

    @Override
    protected AchievementListContract.Presenter onCreatePresenter() {
        return new AchievementListPresenter(parseOrderType(), this);
    }

    private OrderTimeType parseOrderType() {
        //todo
        return (OrderTimeType) getIntent().getSerializableExtra(EXTRA_ORDER_TYPE);
//        return (OrderTimeType) getIntent().getIntExtra(EXTRA_ORDER_TYPE, OrderTimeType.TODAY);

    }

}
