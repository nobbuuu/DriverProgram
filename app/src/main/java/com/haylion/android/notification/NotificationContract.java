package com.haylion.android.notification;

import com.haylion.android.data.model.MessageDetail;
import com.haylion.android.data.model.Order;
import com.haylion.android.mvp.base.AbstractPresenter;
import com.haylion.android.mvp.base.AbstractView;

import java.util.List;

public class NotificationContract {

    public interface View extends AbstractView {

        void setMessageList(List<MessageDetail> list, int unRead);

        void addMoreOrders(List<MessageDetail> list);

        void noMoreOrders();

        void getOrderDetailSuccess(Order orderInfo);

        void getOrderDetailFail(String msg);

        void enterNewActivity(MessageDetail messageDetail);
    }

    public interface Presenter extends AbstractPresenter {

        void loadMoreOrders();

        void refreshOrderList();

        void messageHasReaded(List<MessageDetail> messageDetails);

        void updateMessage(List<MessageDetail> messageDetails);

        void updateMessage(MessageDetail messageDetail);

        void allMessageReaded();

        void getOrderDetail(MessageDetail detail);

    }

}
