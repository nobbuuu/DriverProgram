package com.haylion.android.constract;

import com.haylion.android.data.bean.ChangeOrderStatusBean;
import com.haylion.android.mvp.base.AbstractPresenter;
import com.haylion.android.mvp.base.AbstractView;

import java.io.File;

public interface SignContract {

    interface View extends AbstractView {
        void showUpload(String url);
        void showUpdateOrder(boolean result);
    }

    interface Presenter extends AbstractPresenter {
        void uploadImg(File file);
        void updateOrder(ChangeOrderStatusBean bean);
    }

}


