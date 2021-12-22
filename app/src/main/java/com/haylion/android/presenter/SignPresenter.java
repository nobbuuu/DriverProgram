package com.haylion.android.presenter;

import android.text.TextUtils;
import android.util.Log;

import com.haylion.android.R;
import com.haylion.android.constract.ScanContract;
import com.haylion.android.constract.SignContract;
import com.haylion.android.data.bean.ChangeOrderStatusBean;
import com.haylion.android.data.model.UploadImgBean;
import com.haylion.android.data.repo.UploadRepository;
import com.haylion.android.mvp.base.BasePresenter;
import com.haylion.android.mvp.rx.ApiSubscriber;
import com.haylion.android.mvp.util.LogUtils;
import com.haylion.android.uploadPhoto.UploadImgContract;

import java.io.File;

/**
 * 签名
 */
public class SignPresenter extends BasePresenter<SignContract.View, UploadRepository> implements SignContract.Presenter {

    private static final String TAG = "SignPresenter";

    public SignPresenter(SignContract.View view) {
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
                    view.showUpload(uploadImgBean.getUrl());
                }
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.e(TAG, "照片上传失败:" + code + "," + msg);
                toastError(R.string.toast_picture_upload_fail,msg);

            }
        });
    }

    @Override
    public void updateOrder(ChangeOrderStatusBean bean) {
        repo.changeOrderStatus(bean,new ApiSubscriber<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                Log.d("netLog", "changeOrderStatus success");
                view.showUpdateOrder(aBoolean);
            }

            @Override
            public void onError(int code, String msg) {
                Log.e("netLog", "code = " + code + ";msg = " + msg);
                toast(msg);
            }
        });
    }

}
