package com.haylion.android.user.money;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.haylion.android.R;
import com.haylion.android.data.base.BaseItemView;
import com.haylion.android.data.model.IncomeDetail;
import com.haylion.android.data.util.BusinessUtils;

import java.text.ParseException;

import butterknife.BindView;

public class IncomeListItemView extends BaseItemView<IncomeDetail> {
    private static final String TAG = "IncomeListItemView";

    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.tv_income)
    TextView tvIncome;

    public IncomeListItemView(Context context) {
        super(context);
    }

    @Override
    protected int getOrientation() {
        return RecyclerView.VERTICAL;
    }

    @Override
    public int getLayoutId() {
        return R.layout.income_list_item;
    }

    @Override
    public void bind(IncomeDetail income) {
/*        if (income.isDialable()) {
            contactPassenger.setVisibility(VISIBLE);
        } else {
            contactPassenger.setVisibility(GONE);
        }*/

        if (income != null) {
            String time = income.getDate();
            String timeFormat = "2019-12-01 00:00";
            try {
                long milliSecond = BusinessUtils.stringToLong(time, "yyyy-MM-dd HH:mm");
                timeFormat = BusinessUtils.getDateToString2(milliSecond, null);
                Log.d(TAG, "" + "timeFormat: " + timeFormat);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            tvDate.setText(timeFormat);

            if (income.getPaymentMode() != 3) {
                //收入
                tvType.setText("线下收益");
                tvIncome.setText("" + BusinessUtils.moneySpec(income.getAmount()) + "元");
                tvIncome.setTextColor(getResources().getColor(R.color.text_blue));
            } else {
                //提现
                tvType.setText("线下收益");
                tvIncome.setText("" + BusinessUtils.moneySpec(income.getAmount()) + "元");
                tvIncome.setTextColor(getResources().getColor(R.color.text_green));
            }
        }
    }
}
