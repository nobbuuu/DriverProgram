package com.haylion.android.uploadPhoto;

import com.haylion.android.mvp.base.AbstractPresenter;
import com.haylion.android.mvp.base.AbstractView;

import java.io.File;

/**
 * 图片上传
 */
public class UploadImgContract {

    public interface View extends AbstractView {
        void uploadImgSuccess(String url);
        void uploadImgFail();
    }

    public interface Presenter extends AbstractPresenter {
        void uploadImg(File file);
    }
}
