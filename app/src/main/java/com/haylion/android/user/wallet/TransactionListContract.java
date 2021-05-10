package com.haylion.android.user.wallet;

import com.haylion.android.data.model.Transaction;
import com.haylion.android.mvp.base.AbstractPresenter;
import com.haylion.android.mvp.base.AbstractView;
import com.haylion.android.mvp.base.ListData;

class TransactionListContract {

    interface View extends AbstractView {

        void showTransactionList(ListData<Transaction> transactions);

    }

    interface Presenter extends AbstractPresenter {

        void getTransactionList();

        void loadMoreTransactions();

    }


}
