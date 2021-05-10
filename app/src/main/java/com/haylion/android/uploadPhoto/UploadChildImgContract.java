package com.haylion.android.uploadPhoto;

import com.haylion.android.mvp.base.AbstractPresenter;
import com.haylion.android.mvp.base.AbstractView;

import java.io.File;

public class UploadChildImgContract {

    interface View extends AbstractView {
        void uploadChildImgFail();
        void insertKidImageSuccess();
        void insertKidImageFail();

        void reCommitKidPicSuccess();
        void reCommitKidPicFail();
    }

    interface Presenter extends AbstractPresenter {
        void uploadChildImg(int orderId, int orderStatus, File file,boolean isRepeatCommit);
    }
}
