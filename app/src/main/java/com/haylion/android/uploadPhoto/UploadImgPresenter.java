package com.haylion.android.uploadPhoto;

import android.text.TextUtils;
import android.util.Log;

import com.haylion.android.R;
import com.haylion.android.data.model.UploadImgBean;
import com.haylion.android.data.repo.UploadRepository;
import com.haylion.android.mvp.base.BasePresenter;
import com.haylion.android.mvp.rx.ApiSubscriber;
import com.haylion.android.mvp.util.LogUtils;

import java.io.File;

/**
 * 图片上传
 */
public class UploadImgPresenter extends BasePresenter<UploadImgContract.View, UploadRepository> implements UploadImgContract.Presenter {

    private static final String TAG = "UploadImgPresenter";

    public UploadImgPresenter(UploadImgContract.View view) {
        super(view, UploadRepository.INSTANCE);
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
    }

    @Override
    public void uploadImg(File file) {
        repo.uploadImg(file, new ApiSubscriber<UploadImgBean>() {

            @Override
            public void onSuccess(UploadImgBean uploadImgBean) {
                LogUtils.e(TAG, "照片上传成功:" + uploadImgBean.getUrl());
                if(!TextUtils.isEmpty(uploadImgBean.getUrl())){
                    view.uploadImgSuccess(uploadImgBean.getUrl());
                }
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.e(TAG, "照片上传失败:" + code + "," + msg);
                toastError(R.string.toast_picture_upload_fail,msg);
                view.uploadImgFail();
            }
        });
    }

}
