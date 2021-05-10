package com.haylion.android.orderlist;

import com.haylion.android.data.model.Order;
import com.haylion.android.mvp.base.AbstractPresenter;
import com.haylion.android.mvp.base.AbstractView;

import java.util.List;

public class BookOrderListContract {

    public interface View extends AbstractView {

        void setOrderList(List<Order> list);

        void addMoreOrders(List<Order> list);

        void noMoreOrders();

    }

    public interface Presenter extends AbstractPresenter {

        void loadMoreOrders();

        void refreshOrderList();

        void setOrderType(boolean showPassenger);

        void getAppointmentOrderList();

    }

}
