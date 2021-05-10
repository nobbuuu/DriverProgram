package com.haylion.android.data.model;

 /**
  * @class  MessageDetailSimple
  * @description 通知消息类
  * @date: 2019/12/17 10:26
  * @author: tandongdong
  */
public class MessageDetailSimple {

    private int messageId;
    private int viewed;
    private int display;
    private int deleted;

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getViewed() {
        return viewed;
    }

    public void setViewed(int viewed) {
        this.viewed = viewed;
    }

    public int getDisplay() {
        return display;
    }

    public void setDisplay(int display) {
        this.display = display;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }
}
