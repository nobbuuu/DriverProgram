package com.haylion.android.data.event;

public class AppointmentChangedEvent {

    private boolean canceled;

    public AppointmentChangedEvent() {
        this(false);
    }

    public AppointmentChangedEvent(boolean canceled) {
        this.canceled = canceled;
    }

    public boolean isCanceled() {
        return canceled;
    }

}
