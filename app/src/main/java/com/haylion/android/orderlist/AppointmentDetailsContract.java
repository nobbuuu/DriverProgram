package com.haylion.android.orderlist;

import com.haylion.android.data.model.Order;
import com.haylion.android.mvp.base.AbstractPresenter;
import com.haylion.android.mvp.base.AbstractView;

public class AppointmentDetailsContract {

    public interface View extends AbstractView {

        void showOrderDetails(Order order);

        void closePage();

    }

    public interface Presenter extends AbstractPresenter {

        void processOrder();

        void cancelOrder(String reason);

    }

}
