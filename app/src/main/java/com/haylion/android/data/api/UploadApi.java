package com.haylion.android.data.api;

import com.haylion.android.data.dto.UploadDto;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by dengzh on 2019/9/25
 */
public interface UploadApi {

    //照片上传
    @Multipart
    @POST("/driver/travelling-order/upload/image")
    Observable<UploadDto.UploadImgResponse> uploadSingleImg(@Part MultipartBody.Part file);

    //小孩照片地址插入
    @POST("/driver/travelling-order/insertKidImage")
    Observable<UploadDto.InsertKidImageResponse> insertKidImage(@Body UploadDto.InsertKidImageRequest request);

    //重新提交小孩上下车照片
    @POST("/driver/travelling-order/reCommitKidPicUrl")
    Observable<UploadDto.InsertKidImageResponse> reCommitKidPicUrl(@Body UploadDto.ReCommitKidPicUrlRequest request);

}
