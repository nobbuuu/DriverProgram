package com.haylion.android.orderlist;

import com.haylion.android.data.model.Order;
import com.haylion.android.mvp.base.AbstractPresenter;
import com.haylion.android.mvp.base.AbstractView;

import java.util.List;

public class OrderListContract {

    public interface View extends AbstractView {

        void setOrderList(List<Order> list);

        void addMoreOrders(List<Order> list);

        void noMoreOrders();
    }

    public interface Presenter extends AbstractPresenter {

        void loadMoreOrders();


        void refreshOrderList(int orderType);

        void setOrderType(boolean showPassenger);

    }

}
