package com.haylion.android.orderlist;

import com.haylion.android.data.model.Order;
import com.haylion.android.mvp.base.AbstractPresenter;
import com.haylion.android.mvp.base.AbstractView;

import java.util.List;

public class AppointmentListContract {

    public interface View extends AbstractView {

        void showAppointmentList(List<Order> unfinished, List<Order> finished);

        void showAppointmentHall(List<Order> orders);

        void dismissGrabDialog();

        void showChildrenOrderCenter(List<Order> childrenOrders);

        void showAccessibilityOrders(List<Order> accessibilityOrders);

        void showShunfengOrders(List<Order> shunfengOrders);

    }

    public interface Presenter extends AbstractPresenter {

        void getAppointmentList();

        void refreshAppointmentList();

        void refreshShunfengList();

        void appointmentHall();

        void refreshAppointmentHall();

        void grabOrder(Order order,List<String> list);

        void childrenOrderCenter();

        void refreshChildrenOrderCenter();

        void getAccessibilityOrder();

        void getShunfengOrder();

        void refreshAccessibilityOrder();

    }

}
