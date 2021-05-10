package com.haylion.android.uploadPhoto;

import android.text.TextUtils;
import android.util.Log;

import com.haylion.android.R;
import com.haylion.android.data.model.InsertKidImageBean;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.model.UploadImgBean;
import com.haylion.android.data.repo.UploadRepository;
import com.haylion.android.mvp.base.BasePresenter;
import com.haylion.android.mvp.rx.ApiSubscriber;
import com.haylion.android.mvp.util.LogUtils;

import java.io.File;

public class UploadChildImgPresenter extends BasePresenter<UploadChildImgContract.View, UploadRepository> implements UploadChildImgContract.Presenter {

    private static final String TAG = "UploadChildImgPresenter";

    UploadChildImgPresenter(UploadChildImgContract.View view) {
        super(view, UploadRepository.INSTANCE);
    }

    @Override
    public void onViewCreated() {
        super.onViewCreated();
    }


    /**
     * @param orderId    订单id
     * @param orderStatus  订单状态
     * @param file  图片
     */
    @Override
    public void uploadChildImg(int orderId, int orderStatus, File file,boolean isRepeatCommit) {
        repo.uploadImg(file, new ApiSubscriber<UploadImgBean>() {

            @Override
            public void onSuccess(UploadImgBean uploadImgBean) {
                LogUtils.d(TAG, "照片上传成功:" + uploadImgBean.getUrl());
                if(!TextUtils.isEmpty(uploadImgBean.getUrl())){
                    //判断是插入还是重复提交
                    if(isRepeatCommit){
                        reCommitKidPicUrl(orderId,uploadImgBean.getUrl(),orderStatus);
                    }else{
                        insertKidImage(orderId,uploadImgBean.getUrl(),orderStatus);
                    }
                }
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.e(TAG, "照片上传失败:" + code + "," + msg);
                toastError(R.string.toast_picture_upload_fail,msg);
                view.uploadChildImgFail();
            }
        });
    }


    /**
     * 订单插入小孩图片
     * @param orderId
     * @param url
     * @param orderStatus
     */
    private void insertKidImage(int orderId,String url,int orderStatus){
        String onPicUrl = "";
        String offPicUrl = "";
        String stateType = null;
        if(orderStatus == Order.ORDER_STATUS_ARRIVED_START_ADDR){
            onPicUrl = url;
        }else {
            offPicUrl = url;
            stateType = "3";
        }
        repo.insertKidImage(orderId, onPicUrl, offPicUrl,stateType, new ApiSubscriber<InsertKidImageBean>() {
            @Override
            public void onSuccess(InsertKidImageBean insertKidImageBean) {
                LogUtils.e(TAG, "小孩照片插入成功");
                if(insertKidImageBean.isResult()){
                    view.insertKidImageSuccess();
                }
            }

            @Override
            public void onError(int code, String msg) {
                LogUtils.e(TAG, "小孩照片插入失败:" + code + "," + msg);
                if(code == 400001){
                    toast(R.string.toast_order_not_exist);
                }else{
                    toastError(R.string.toast_picture_upload_fail,msg);
                }
                view.insertKidImageFail();
            }
        });
    }

    /**
     * 重新提交小孩图片
     * @param id
     * @param picUrl
     * @param orderStatus
     */
    private void reCommitKidPicUrl(int id ,String picUrl,int orderStatus){
        //0-上车照片  1-下车照片
        int onOff = orderStatus == Order.ORDER_STATUS_ARRIVED_START_ADDR ? 0:1;
        repo.reCommitKidPicUrl(id, picUrl, onOff, new ApiSubscriber<InsertKidImageBean>() {
            @Override
            public void onSuccess(InsertKidImageBean insertKidImageBean) {
                if(insertKidImageBean.isResult()){
                    view.reCommitKidPicSuccess();
                }
            }

            @Override
            public void onError(int code, String msg) {
                Log.e(TAG, "code:" + code + ",msg:" + code);
                if(code == 400001){
                    toast(R.string.toast_order_not_exist);
                }else{
                    toastError(R.string.toast_picture_upload_fail,msg);
                }
                view.reCommitKidPicFail();
            }
        });
    }

}
