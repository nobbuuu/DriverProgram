package com.haylion.android.constract;

import com.haylion.android.data.bean.OrderDetailBean;
import com.haylion.android.data.bean.PhoneBean;
import com.haylion.android.mvp.base.AbstractPresenter;
import com.haylion.android.mvp.base.AbstractView;

import java.io.File;

public class PreSignContract {

    public interface View extends AbstractView {
        void showOrderInfo(OrderDetailBean list);

        void showServicePhoneNum(PhoneBean bean);
    }

    public interface Presenter extends AbstractPresenter {
        void getOrderDetail(int orderId);

        void getServicePhoneNum();
    }

}


