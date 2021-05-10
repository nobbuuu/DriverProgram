package com.haylion.android.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

 /**
  * @class  WebsocketOrderInfo
  * @description websocket 中data数据信息
  * @date: 2019/12/17 10:42
  * @author: tandongdong
  */
public class WebsocketOrderInfo implements Parcelable {
    private int orderId;
    private String oldPlace;
    private String oldAddress;
    private int driverId;
    private int vehicleId;
    private List<Integer> orderIds;
    private int messageId; //推送消息的ID，用于上报消息是否已读。

    protected WebsocketOrderInfo(Parcel in) {
        orderId = in.readInt();
        oldPlace = in.readString();
        oldAddress = in.readString();
        driverId = in.readInt();
        vehicleId = in.readInt();
        messageId = in.readInt();
    }

    public WebsocketOrderInfo() {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(orderId);
        dest.writeString(oldPlace);
        dest.writeString(oldAddress);
        dest.writeInt(driverId);
        dest.writeInt(vehicleId);
        dest.writeInt(messageId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<WebsocketOrderInfo> CREATOR = new Creator<WebsocketOrderInfo>() {
        @Override
        public WebsocketOrderInfo createFromParcel(Parcel in) {
            return new WebsocketOrderInfo(in);
        }

        @Override
        public WebsocketOrderInfo[] newArray(int size) {
            return new WebsocketOrderInfo[size];
        }
    };

    @Override
    public String toString() {
        return "WebsocketOrderInfo{" +
                "orderId=" + orderId +
                ", oldPlace='" + oldPlace + '\'' +
                ", oldAddress='" + oldAddress + '\'' +
                ", driverId=" + driverId +
                ", vehicleId=" + vehicleId +
                ", orderIds=" + orderIds +
                ", messageId=" + messageId +
                '}';
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getOldPlace() {
        return oldPlace;
    }

    public void setOldPlace(String oldPlace) {
        this.oldPlace = oldPlace;
    }

    public String getOldAddress() {
        return oldAddress;
    }

    public void setOldAddress(String oldAddress) {
        this.oldAddress = oldAddress;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public List<Integer> getOrderIds() {
        return orderIds;
    }

    public void setOrderIds(List<Integer> orderIds) {
        this.orderIds = orderIds;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }
}
