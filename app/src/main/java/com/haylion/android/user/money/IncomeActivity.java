package com.haylion.android.user.money;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.haylion.android.R;
import com.haylion.android.data.model.IncomeDetail;
import com.haylion.android.data.model.IncomeGeneral;
import com.haylion.android.data.model.OrderTimeType;
import com.haylion.android.data.model.SimpleSelectBean;
import com.haylion.android.data.util.BusinessUtils;
import com.haylion.android.data.widgt.MassLoadingMoreFooter;
import com.haylion.android.common.view.popwindow.IncomeMonthPopWindow;
import com.haylion.android.data.base.BaseActivity;
import com.haylion.android.orderlist.OrderListActivity;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.nlopez.smartadapters.SmartAdapter;
import io.nlopez.smartadapters.adapters.RecyclerMultiAdapter;
import io.nlopez.smartadapters.utils.ViewEventListener;


/**
 * 收益明细页面
 */
public class IncomeActivity extends BaseActivity<IncomeListContract.Presenter> implements
        IncomeListContract.View, ViewEventListener<IncomeDetail> {

    private static final String TAG = "OrderListActivity";
    private static final String EXTRA_ORDER_TYPE = "order_type";


    /**
     * 列表
     */
    @BindView(R.id.lv_income_list)
    XRecyclerView rvIncomeList;
    @BindView(R.id.empty_view)
    TextView mEmptyView;


    /**
     * 月份选择
     */
    @BindView(R.id.ll_month_select)
    LinearLayout llMonthSelect;
    @BindView(R.id.tv_month)
    TextView tvMonth;
    @BindView(R.id.iv_arrow_down)
    ImageView ivArrowDown;

    /**
     * 收益
     */
    @BindView(R.id.income_offline)
    TextView tvIncomeOffline;
    @BindView(R.id.income_online)
    TextView tvIncomeOnline;


    private RecyclerMultiAdapter mAdapter;
    private List<IncomeGeneral> mIncomeList = new ArrayList<>();
    private List<SimpleSelectBean> mMonthList = new ArrayList<>();
    private IncomeMonthPopWindow mPopWindow;



    public static void start(Context context, OrderTimeType type) {
        Intent intent = new Intent(context, OrderListActivity.class);
        Log.d(TAG, "type:" + type);
        intent.putExtra(EXTRA_ORDER_TYPE, type);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_list);
        ButterKnife.bind(this);
        initFooterView();
    }

    @OnClick({R.id.iv_back, R.id.ll_month_select,R.id.empty_view})
    public void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.ll_month_select: //显示月份选择弹窗
                showIncomeMonthPopWindow();
                break;
            case R.id.empty_view:  //点击空视图
                rvIncomeList.refresh();
                break;
            default:
                break;
        }
    }


    /**
     * 显示月份选择弹窗
     */
    private void showIncomeMonthPopWindow() {
        if(mMonthList.size()==0){
            return;
        }
        if (mPopWindow == null) {
            mPopWindow = new IncomeMonthPopWindow(this);
            mPopWindow.setOnItemClickListener(new SimpleSelectAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    tvMonth.setText(mMonthList.get(position).getDesc());
                    tvIncomeOnline.setText(BusinessUtils.moneySpec(mIncomeList.get(position).getOnlineAmount()) + "元");
                    tvIncomeOffline.setText(BusinessUtils.moneySpec(mIncomeList.get(position).getOfflineAmount()) + "元");
                    presenter.setSelectedMonth(mIncomeList.get(position).getYearMonth());
                    presenter.refreshOrderList();
                    mPopWindow.dismiss();
                }
            });
            mPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    ivArrowDown.setImageResource(R.mipmap.ic_arrow_select_down);
                }
            });
        }
        mPopWindow.setData(mMonthList);
        mPopWindow.showAsDropDown(llMonthSelect,0,0);
        ivArrowDown.setImageResource(R.mipmap.ic_arrow_select_up);
    }

    /**
     * 获取月份数据
     *
     * @param incomeGeneralList
     */
    @Override
    public void updateMonthView(List<IncomeGeneral> incomeGeneralList) {
        mIncomeList.clear();
        mIncomeList.addAll(incomeGeneralList);

        mMonthList.clear();
        for (int i = 0; i < mIncomeList.size(); i++) {
            String desc = converterMonth(mIncomeList.get(i).getYearMonth());
            mMonthList.add(new SimpleSelectBean(mIncomeList.get(i).getYearMonth(), desc, i == 0));
        }
        tvMonth.setText(mMonthList.get(0).getDesc());
        tvIncomeOnline.setText(BusinessUtils.moneySpec(mIncomeList.get(0).getOnlineAmount()) + "元");
        tvIncomeOffline.setText(BusinessUtils.moneySpec(mIncomeList.get(0).getOfflineAmount()) + "元");
        presenter.setSelectedMonth(mIncomeList.get(0).getYearMonth());
        presenter.refreshOrderList();
    }

    private String converterMonth(String yearMonth) {
        if (yearMonth.equals(getCurrentMonth())) {
            return "本月";
        } else {
            return yearMonth.substring(0, 4) + "年" + yearMonth.substring(5, 7) + "月";
        }
    }

    private String getCurrentMonth() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        return format.format(new Date());
    }


    /**
     * 自定义 footerView
     */
    private void initFooterView() {
        MassLoadingMoreFooter footerView = new MassLoadingMoreFooter(this);
        rvIncomeList.setFootView(footerView, footerView.callBack);
    }

    @Override
    protected void beforeCreatePresenter() {
        rvIncomeList.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                presenter.refreshOrderList();
            }

            @Override
            public void onLoadMore() {
                presenter.loadMoreOrders();
            }
        });
        rvIncomeList.setEmptyView(mEmptyView);
        mAdapter = SmartAdapter.empty()
                .map(IncomeDetail.class, IncomeListItemView.class)
                .listener(this)
                .into(rvIncomeList);
//        rvIncomeList.addItemDecoration(new VerticalSpaceItemDecoration(SizeUtil.dp2px(16)));
    }


    @Override
    public void setIncomeList(List<IncomeDetail> list) {
        if (list == null || list.isEmpty()) {
            mEmptyView.setText("暂无收益明细");
        }
        mAdapter.setItems(list);
        rvIncomeList.refreshComplete();
    }

    @Override
    public void addMoreOrders(List<IncomeDetail> list) {
        mAdapter.addItems(list);
        rvIncomeList.loadMoreComplete();
    }


    @Override
    public void noMoreOrders() {
        rvIncomeList.setNoMore(true);
    }

    @Override
    public void onViewEvent(int actionType, IncomeDetail order, int position, View view) {}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (rvIncomeList != null) {
            rvIncomeList.destroy();
            rvIncomeList = null;
        }
    }

    @Override
    protected IncomeListContract.Presenter onCreatePresenter() {
        return new IncomeListPresenter(this);
    }

}

