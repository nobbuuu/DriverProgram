package com.haylion.android.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

 /**
  * @class  MessageListData
  * @description 通知消息列表
  * @date: 2019/12/17 10:26
  * @author: tandongdong
  */
public class MessageListData<T> {
    private int unviewedCount;

    private int total;

    @SerializedName("current")
    private int pageNumber;

    private int pageCount;

    private List<T> list;

    public MessageListData(int unviewedCount, int total, int pageNumber, int pageCount, List<T> list) {
        this.unviewedCount = unviewedCount;
        this.total = total;
        this.pageNumber = pageNumber;
        this.pageCount = pageCount;
        this.list = list;
    }

    public int getUnviewedCount() {
        return unviewedCount;
    }

    public void setUnviewedCount(int unviewedCount) {
        this.unviewedCount = unviewedCount;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public boolean isListEmpty() {
        return list == null || list.isEmpty();
    }

}
