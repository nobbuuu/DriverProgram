package com.haylion.android.user.shift;

import com.haylion.android.data.model.ShiftInfo;
import com.haylion.android.mvp.base.AbstractPresenter;
import com.haylion.android.mvp.base.AbstractView;

class ShiftInfoContract {

    interface View extends AbstractView {

        void showShiftInfo(ShiftInfo shiftInfo);

    }

    interface Presenter extends AbstractPresenter {

        void modifyShiftInfo(ShiftInfo shiftInfo);

    }


}
