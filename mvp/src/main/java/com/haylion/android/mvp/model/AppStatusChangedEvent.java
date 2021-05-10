package com.haylion.android.mvp.model;

/**
 * App状态改变
 */
public class AppStatusChangedEvent {

    private int numRunningActivities;

    public AppStatusChangedEvent(int numRunningActivities) {
        this.numRunningActivities = numRunningActivities;
    }

    public int getNumRunningActivities() {
        return numRunningActivities;
    }

    public boolean isForeground() {
        return numRunningActivities > 0;
    }

}
