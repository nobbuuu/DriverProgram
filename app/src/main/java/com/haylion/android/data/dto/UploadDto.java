package com.haylion.android.data.dto;

import com.haylion.android.data.model.InsertKidImageBean;
import com.haylion.android.data.model.UploadImgBean;
import com.haylion.android.mvp.base.BaseResponse;

/**
 * Created by dengzh on 2019/9/25
 */
public class UploadDto {

    public static class UploadImgResponse extends BaseResponse<UploadImgBean> {

        public UploadImgResponse(String msg, int code, UploadImgBean data) {
            super(msg, code, data);
        }
    }

    public static class InsertKidImageResponse extends BaseResponse<InsertKidImageBean> {

        public InsertKidImageResponse(String msg, int code, InsertKidImageBean data) {
            super(msg, code, data);
        }
    }

    public static class InsertKidImageRequest {

        private int id;
        private String onPicUrl;
        private String offPicUrl;
        private String stateType;

        public InsertKidImageRequest(int id, String onPicUrl, String offPicUrl,String stateType) {
            this.id = id;
            this.onPicUrl = onPicUrl;
            this.offPicUrl = offPicUrl;
            this.stateType = stateType;
        }
    }

    public static class ReCommitKidPicUrlRequest{
        private int id;
        private String picUrl;
        private int onOff;  //0-上车照片  1-下车照片

        public ReCommitKidPicUrlRequest(int id, String picUrl, int onOff) {
            this.id = id;
            this.picUrl = picUrl;
            this.onOff = onOff;
        }
    }

}
