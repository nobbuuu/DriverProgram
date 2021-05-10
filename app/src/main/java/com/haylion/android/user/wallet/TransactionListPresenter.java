package com.haylion.android.user.wallet;

import com.haylion.android.data.model.Transaction;
import com.haylion.android.data.repo.WalletRepository;
import com.haylion.android.mvp.base.BasePresenter;
import com.haylion.android.mvp.base.ListData;
import com.haylion.android.mvp.rx.ApiSubscriber;
import com.haylion.android.mvp.util.LogUtils;

class TransactionListPresenter extends BasePresenter<TransactionListContract.View,
        WalletRepository> implements TransactionListContract.Presenter {

    private int mListPage;
    private int mTransactionType;

    TransactionListPresenter(TransactionListContract.View view, int transactionType) {
        super(view, WalletRepository.INSTANCE);
        this.mListPage = 1;
        this.mTransactionType = transactionType;
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
        getTransactionList();
    }

    @Override
    public void getTransactionList() {
        mListPage = 1;
        getTransactionListInternal();
    }

    private void getTransactionListInternal() {
        repo.getTransactionList(mListPage, 20, mTransactionType, new ApiSubscriber<ListData<Transaction>>() {
            @Override
            public void onSuccess(ListData<Transaction> transactionListData) {
                view.showTransactionList(transactionListData);
            }

            @Override
            public void onError(int code, String msg) {
                view.showTransactionList(null);
                LogUtils.e("获取收支记录出错：" + code + ", " + msg);
            }
        });
    }

    @Override
    public void loadMoreTransactions() {
        mListPage++;
        getTransactionListInternal();
    }

    @Override
    public void onViewDestroy() {
        super.onViewDestroy();

    }

}
