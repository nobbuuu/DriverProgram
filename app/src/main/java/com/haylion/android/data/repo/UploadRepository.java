package com.haylion.android.data.repo;

import com.haylion.android.data.api.UploadApi;
import com.haylion.android.data.dto.UploadDto;
import com.haylion.android.data.model.InsertKidImageBean;
import com.haylion.android.data.model.UploadImgBean;
import com.haylion.android.mvp.base.BaseRepository;
import com.haylion.android.mvp.net.RetrofitHelper;
import com.haylion.android.mvp.rx.ApiSubscriber;
import com.haylion.android.mvp.rx.ApiTransformer;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

 /**
  * @class  UploadRepository
  * @description 文件上传
  * @date: 2019/9/25 10:11
  * @author: dengzh
  */
public class UploadRepository extends BaseRepository {

    public static final UploadRepository INSTANCE = new UploadRepository();


    /**
     * 上传照片
     * @param file
     * @param callback
     */
    public void uploadImg(File file, ApiSubscriber<UploadImgBean> callback) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpg"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        addDisposable(RetrofitHelper.getApi(UploadApi.class)
                .uploadSingleImg(body)
                .compose(new ApiTransformer<>())
                .subscribeWith(callback));
    }

    /**
     * 插入小孩照片
     * @param id       订单id
     * @param onPicUrl   上车url
     * @param offPicUrl  下车url
     * @param stateType  状态，提交下车照片时，这个一定要有 （这个值和更新状态的接口保持一至）
     * @param callback
     */
    public void insertKidImage(int id, String onPicUrl, String offPicUrl,String stateType, ApiSubscriber<InsertKidImageBean> callback){
        addDisposable(RetrofitHelper.getApi(UploadApi.class)
                .insertKidImage(new UploadDto.InsertKidImageRequest(id,onPicUrl,offPicUrl,stateType))
                .compose(new ApiTransformer<>())
                .subscribeWith(callback));
    }


    /**
     * 重新提交小孩上下车照片
     * @param id
     * @param picUrl
     * @param onOff     0:上车照片1:下车照片
     * @param callback
     */
    public void reCommitKidPicUrl(int id, String picUrl, int onOff, ApiSubscriber<InsertKidImageBean> callback){
        addDisposable(RetrofitHelper.getApi(UploadApi.class)
                .reCommitKidPicUrl(new UploadDto.ReCommitKidPicUrlRequest(id,picUrl,onOff))
                .compose(new ApiTransformer<>())
                .subscribeWith(callback));
    }


}
