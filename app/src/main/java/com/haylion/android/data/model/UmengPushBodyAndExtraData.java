package com.haylion.android.data.model;

 /**
  * @class  UmengPushBodyData
  * @description 友盟推送的extra对象
  * @date: 2019/12/9 11:15
  * @author: tandongdong
  */
public class UmengPushBodyAndExtraData {
    private UmengPushBodyData body;
    private UmengPushExtraData extra;

     public UmengPushBodyData getBody() {
         return body;
     }

     public void setBody(UmengPushBodyData body) {
         this.body = body;
     }

     public UmengPushExtraData getExtra() {
         return extra;
     }

     public void setExtra(UmengPushExtraData extra) {
         this.extra = extra;
     }
 }
