package com.haylion.android.data.event;

public class ScreenLockEvent {

    private boolean screenLock;

    public ScreenLockEvent(boolean screenLock) {
        this.screenLock = screenLock;
    }

    public boolean isScreenLock() {
        return screenLock;
    }

    public void setScreenLock(boolean screenLock) {
        this.screenLock = screenLock;
    }
}
