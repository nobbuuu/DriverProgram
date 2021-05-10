package com.haylion.android.data.model;

import java.util.List;

public class OrderAbstractByMonth {
    private long earliestAchievementDate;
    private List<OrderAbstract> list;

    public OrderAbstractByMonth() {
    }

    public OrderAbstractByMonth(long earliestAchievementDate, List<OrderAbstract> list) {
        this.earliestAchievementDate = earliestAchievementDate;
        this.list = list;
    }

    public long getEarliestAchievementDate() {
        return earliestAchievementDate;
    }

    public void setEarliestAchievementDate(long earliestAchievementDate) {
        this.earliestAchievementDate = earliestAchievementDate;
    }

    public List<OrderAbstract> getList() {
        return list;
    }

    public void setList(List<OrderAbstract> list) {
        this.list = list;
    }
}