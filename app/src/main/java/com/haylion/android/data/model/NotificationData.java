package com.haylion.android.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @class NotificationData
 * @description 系统消息
 * @date: 2019/12/9 11:15
 * @author: tandongdong
 */
public class NotificationData implements Parcelable {
    private int messageId;
    private int messageType;
    private String messageTitle;
    private String messageContent;
    private int orderId;
    private String pushTime;

    public NotificationData() {
    }

    protected NotificationData(Parcel in) {
        messageId = in.readInt();
        messageType = in.readInt();
        messageTitle = in.readString();
        messageContent = in.readString();
        orderId = in.readInt();
        pushTime = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(messageId);
        dest.writeInt(messageType);
        dest.writeString(messageTitle);
        dest.writeString(messageContent);
        dest.writeInt(orderId);
        dest.writeString(pushTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NotificationData> CREATOR = new Creator<NotificationData>() {
        @Override
        public NotificationData createFromParcel(Parcel in) {
            return new NotificationData(in);
        }

        @Override
        public NotificationData[] newArray(int size) {
            return new NotificationData[size];
        }
    };

    @Override
    public String toString() {
        return "NotificationData{" +
                "messageId=" + messageId +
                ", messageType=" + messageType +
                ", messageTitle='" + messageTitle + '\'' +
                ", messageContent='" + messageContent + '\'' +
                ", orderId=" + orderId +
                ", pushTime='" + pushTime + '\'' +
                '}';
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getMessageTitle() {
        return messageTitle;
    }

    public void setMessageTitle(String messageTitle) {
        this.messageTitle = messageTitle;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getPushTime() {
        return pushTime;
    }

    public void setPushTime(String pushTime) {
        this.pushTime = pushTime;
    }
}
