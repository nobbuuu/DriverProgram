package com.haylion.android.orderlist.achievement;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.haylion.android.R;
import com.haylion.android.data.base.BaseItemView;
import com.haylion.android.data.model.OrderAbstract;
import com.haylion.android.data.util.BusinessUtils;

import butterknife.BindView;

public class AchievementItemView extends BaseItemView<OrderAbstract> {
    private static final String TAG = "OrderListItemView";

    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_listen_time)
    TextView tvLisenTime;
    @BindView(R.id.tv_order_count)
    TextView tvOrderCount;
    @BindView(R.id.tv_income)
    TextView tvIncome;
    @BindView(R.id.rl_header)
    RelativeLayout rlHeader;
    @BindView(R.id.tv_display_month_header)
    TextView tvMonthHeader;
    @BindView(R.id.card_view)
    LinearLayout llCardView;

    public static int lastYearAndMonth = 0;
    Context mContext;

    public AchievementItemView(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected int getOrientation() {
        return RecyclerView.VERTICAL;
    }

    @Override
    public int getLayoutId() {
        return R.layout.order_achievement_list_item;
    }

    @Override
    public void bind(OrderAbstract order) {
        if(order.isShowMonthView()){
            String date = order.getDate();
            tvMonthHeader.setText(date.substring(0, 4) + "年" + date.substring(5, 7) + "月");
            rlHeader.setVisibility(VISIBLE);
        } else {
            rlHeader.setVisibility(GONE);
        }


        if (order != null) {
            tvDate.setText(order.getDate().substring(5));
            tvLisenTime.setText(String.format("%.1f", (float)order.getOnlineTime() / 3600) + "小时");
            tvOrderCount.setText(order.getOrderCompletionCount() + "单");
            tvIncome.setText(BusinessUtils.moneySpec(order.getIncome()) + "元");
        }
    }
}
