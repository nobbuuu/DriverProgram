package com.haylion.android.ordersetting;

import com.haylion.android.data.model.AddressInfo;
import com.haylion.android.data.model.ListenOrderSetting;
import com.haylion.android.mvp.base.AbstractPresenter;
import com.haylion.android.mvp.base.AbstractView;

public class OrderSettingContract {

    interface View extends AbstractView {
        void updateSettingView(ListenOrderSetting setting);
        void updateBackHomeAddr(AddressInfo addr);
        void saveSettingSuccess();
    }

    interface Presenter extends AbstractPresenter {
        void getListenOrderSetting();
        void setListenOrderSetting(ListenOrderSetting setting);
        void getBackHomeAddr();
        void setBackHomeAddr(AddressInfo addr);
    }

}
