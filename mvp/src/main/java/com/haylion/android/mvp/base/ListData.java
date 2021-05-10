package com.haylion.android.mvp.base;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListData<T> {

    private int total;

    @SerializedName("current")
    private int pageNumber;
    private int unviewedCount;
    private int pageCount;

    private List<T> list;

    public ListData(int total, int pageNumber, int unviewedCount, int pageCount, List<T> list) {
        this.total = total;
        this.pageNumber = pageNumber;
        this.unviewedCount = unviewedCount;
        this.pageCount = pageCount;
        this.list = list;
    }

    public ListData(int total, int pageNumber, int pageCount, List<T> list) {
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
