package com.haylion.android.user.money;

import com.haylion.android.data.model.IncomeDetail;
import com.haylion.android.data.model.IncomeGeneral;
import com.haylion.android.mvp.base.AbstractPresenter;
import com.haylion.android.mvp.base.AbstractView;

import java.util.List;

public class IncomeListContract {

    public interface View extends AbstractView {

        void setIncomeList(List<IncomeDetail> list);

        void addMoreOrders(List<IncomeDetail> list);

        void noMoreOrders();

        void updateMonthView(List<IncomeGeneral> list);
    }

    public interface Presenter extends AbstractPresenter {

        void loadMoreOrders();


        void refreshOrderList();

        void setSelectedMonth(String month);

    }

}
