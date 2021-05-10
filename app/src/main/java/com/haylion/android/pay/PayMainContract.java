package com.haylion.android.pay;

import com.haylion.android.data.model.PayInfo;
import com.haylion.android.data.model.ServiceFee;
import com.haylion.android.mvp.base.AbstractPresenter;
import com.haylion.android.mvp.base.AbstractView;

public class PayMainContract {

    public interface View extends AbstractView {
        void payConfirmSuccess();

        void payRequestSuccess(int payType);

        void payRequestFail();

        void updateServiceCost(ServiceFee serviceFee);

        void onCarpoolOrderPaid();
    }

    public interface Presenter extends AbstractPresenter {

        void payConfirm(int orderId, int state);

        void payRequest(PayInfo payInfo);

        void payRequestCarpool(PayInfo payInfo);

        void getServiceCost(Double cost, int orderId);

        void getCarpoolServiceFee(String carpoolCode);

    }

}
