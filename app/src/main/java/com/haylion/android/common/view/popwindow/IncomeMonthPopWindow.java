package com.haylion.android.common.view.popwindow;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.haylion.android.R;
import com.haylion.android.data.model.SimpleSelectBean;
import com.haylion.android.user.money.SimpleSelectAdapter;

import java.util.List;

/**
 * @author dengzh
 * @date 2019/11/10
 * Description: 收益明细-月份列表弹窗
 */
public class IncomeMonthPopWindow extends BasePopupWindow implements View.OnClickListener {

    private RecyclerView recyclerView;
    private SimpleSelectAdapter mAdapter;


    public IncomeMonthPopWindow(Context mContext) {
        super(mContext, R.layout.income_month_popwindow, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        init();
    }

    private void init(){
        recyclerView = getView(R.id.recyclerView);
        getView(R.id.rl_main).setOnClickListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setHasFixedSize(true);
        mAdapter = new SimpleSelectAdapter(mContext);
        recyclerView.setAdapter(mAdapter);
    }

    public void setData(List<SimpleSelectBean> list){
        mAdapter.setData(list);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        mPopupWindow.dismiss();
    }

    public void setOnItemClickListener(SimpleSelectAdapter.OnItemClickListener listener){
        mAdapter.setOnItemClickListener(listener);
    }
}
