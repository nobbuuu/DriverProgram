package com.haylion.android.data.model;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public  class AppointmentList {

    @SerializedName("unfinished")
    private List<OrderForMainActivity> unfinishedList;
    @SerializedName("finished")
    private List<OrderForMainActivity> finishedList;

    public List<OrderForMainActivity> getUnfinishedList() {
        return unfinishedList;
    }

    public void setUnfinishedList(List<OrderForMainActivity> unfinishedList) {
        this.unfinishedList = unfinishedList;
    }

    public List<OrderForMainActivity> getFinishedList() {
        return finishedList;
    }

    public void setFinishedList(List<OrderForMainActivity> finishedList) {
        this.finishedList = finishedList;
    }
}
