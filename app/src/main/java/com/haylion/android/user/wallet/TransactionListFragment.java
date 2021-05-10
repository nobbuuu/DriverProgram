package com.haylion.android.user.wallet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.haylion.android.R;
import com.haylion.android.data.model.Transaction;
import com.haylion.android.mvp.base.BaseFragment;
import com.haylion.android.mvp.base.ListData;
import com.haylion.android.mvp.util.SizeUtil;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.BindView;
import io.nlopez.smartadapters.SmartAdapter;
import io.nlopez.smartadapters.adapters.RecyclerMultiAdapter;
import io.nlopez.smartadapters.utils.ViewEventListener;

public class TransactionListFragment extends BaseFragment<TransactionListContract.Presenter>
        implements ViewEventListener<Transaction>, TransactionListContract.View {

    static final int TYPE_ALL = 0;
    static final int TYPE_INCOME = 1;
    static final int TYPE_EXPENSE = 2;

    public static TransactionListFragment newInstance(int type) {

        Bundle args = new Bundle();
        args.putInt(EXTRA_TRANSACTION_TYPE, type);

        TransactionListFragment fragment = new TransactionListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @BindView(R.id.transaction_list)
    XRecyclerView mTransactionList;
    private RecyclerMultiAdapter mTransactionAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transaction_list, container, false);
    }

    @Override
    protected TransactionListContract.Presenter onCreatePresenter() {
        return new TransactionListPresenter(this, getTransactionType());
    }

    @Override
    protected void didCreate() {
        super.didCreate();
        mTransactionAdapter = SmartAdapter.empty()
                .map(Transaction.class, TransactionListItemView.class)
                .listener(this)
                .into(mTransactionList);
        mTransactionList.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                presenter.getTransactionList();
            }

            @Override
            public void onLoadMore() {
                presenter.loadMoreTransactions();
            }
        });
        int paddingVertical = SizeUtil.dp2px(12);
        mTransactionList.getFootView().setPadding(0, paddingVertical, 0, paddingVertical);
    }

    @Override
    public void showTransactionList(ListData<Transaction> transactionData) {
        if (transactionData == null) {
            mTransactionList.refreshComplete();
            mTransactionList.loadMoreComplete();
            return;
        }
        if (transactionData.getPageNumber() == 1) {
            if (transactionData.getList() == null || transactionData.getList().isEmpty()) {
                popupNoTransactions();
            }
            mTransactionList.refreshComplete();
            mTransactionAdapter.setItems(transactionData.getList());
        } else {
            mTransactionAdapter.addItems(transactionData.getList());
            mTransactionList.loadMoreComplete();
        }
        mTransactionList.setNoMore(transactionData.getPageCount() ==
                transactionData.getPageNumber());
    }

    private void popupNoTransactions() {
//        int transactionType = getTransactionType();
//        if (transactionType == TYPE_INCOME) {
//            toast("没有收入记录");
//        } else if (transactionType == TYPE_EXPENSE) {
//            toast("没有支出记录");
//        } else {
//            toast("没有记录");
//        }
    }

    @Override
    public void onViewEvent(int actionType, Transaction transaction, int position, View view) {

    }

    @Override
    protected void willDestroy() {
        super.willDestroy();
        if (mTransactionList != null) {
            mTransactionList.destroy();
            mTransactionList = null;
        }
    }

    private int getTransactionType() {
        if (getArguments() != null) {
            return getArguments().getInt(EXTRA_TRANSACTION_TYPE, TYPE_ALL);
        }
        return TYPE_ALL;
    }

    private static final String EXTRA_TRANSACTION_TYPE = "transaction_type";

}
