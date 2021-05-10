package com.haylion.android.data.model;

/**
 * @class MessageDetail
 * @description 通知消息类
 * @date: 2019/12/17 10:26
 * @author: tandongdong
 */
public class MessageDetail {

    private int messageId; /*消息ID*/
    private int total;
    private int unviewedCount;
    private int cmdSn; /*消息命令字，目前是和websocket消息中对应的。*/
    private int orderId;
    private String content;
    private int viewed; /*1：已读, 0:未读*/
    private int display; /*1：需要在系统消息列表中展示， 0：不需要展示*/
    private int deleted; /*1：删除消息， 0：不删除*/
    private String pushTime; /*推送消息的时间*/
    private int orderType;   //订单种类（可能为空）(只有支付类推送会有)

    /**
     * 送你上学单 - 拼车状态
     */
    private int carpoolStatus;
    /**
     * 送你上学单 - 拼车码
     */
    private String carpoolCode;

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
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

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getUnviewedCount() {
        return unviewedCount;
    }

    public void setUnviewedCount(int unviewedCount) {
        this.unviewedCount = unviewedCount;
    }

    public int getCmdSn() {
        return cmdSn;
    }

    public void setCmdSn(int cmdSn) {
        this.cmdSn = cmdSn;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getViewed() {
        return viewed;
    }

    public void setViewed(int viewed) {
        this.viewed = viewed;
    }

    public String getPushTime() {
        return pushTime;
    }

    public void setPushTime(String pushTime) {
        this.pushTime = pushTime;
    }

    public int getCarpoolStatus() {
        return carpoolStatus;
    }

    public void setCarpoolStatus(int carpoolStatus) {
        this.carpoolStatus = carpoolStatus;
    }

    public String getCarpoolCode() {
        return carpoolCode;
    }

    public void setCarpoolCode(String carpoolCode) {
        this.carpoolCode = carpoolCode;
    }

    @Override
    public String toString() {
        return "MessageDetail{" +
                "messageId=" + messageId +
                ", total=" + total +
                ", unviewedCount=" + unviewedCount +
                ", cmdSn=" + cmdSn +
                ", orderId=" + orderId +
                ", content='" + content + '\'' +
                ", viewed=" + viewed +
                ", display=" + display +
                ", deleted=" + deleted +
                ", pushTime='" + pushTime + '\'' +
                ", orderType=" + orderType +
                '}';
    }
}
