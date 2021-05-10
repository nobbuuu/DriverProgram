package com.haylion.android.orderlist.achievement;

import com.haylion.android.data.model.OrderAbstract;
import com.haylion.android.mvp.base.AbstractPresenter;
import com.haylion.android.mvp.base.AbstractView;

import java.util.List;

public class AchievementListContract {

    public interface View extends AbstractView {

        void setOrderList(List<OrderAbstract> list);

        void addMoreOrders(List<OrderAbstract> list);

        void noMoreOrders();
    }

    public interface Presenter extends AbstractPresenter {

        void loadMoreOrders();


        void refreshOrderList();

        void loadCurrentMonthData();

        void loadMoreDataByMonth();

    }

}
