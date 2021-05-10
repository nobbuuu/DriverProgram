package com.haylion.android.data.model;

 /**
  * @class  UmengPushBodyData
  * @description 友盟推送的extra对象
  * @date: 2019/12/9 11:15
  * @author: tandongdong
  */
public class UmengPushExtraData {
    private String cmdSn;
    private String orderId;
    private String cmd;
    private String type;
    private String messageId;

     public String getType() {
         return type;
     }

     public void setType(String type) {
         this.type = type;
     }

     public String getMessageId() {
         return messageId;
     }

     public void setMessageId(String messageId) {
         this.messageId = messageId;
     }

     public String getCmdSn() {
         return cmdSn;
     }

     public void setCmdSn(String cmdSn) {
         this.cmdSn = cmdSn;
     }

     public String getOrderId() {
         return orderId;
     }

     public void setOrderId(String orderId) {
         this.orderId = orderId;
     }
 }
