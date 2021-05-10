package com.haylion.android.data.event;

import com.haylion.android.data.model.AddressInfo;

public class AddressChangedEvent {

    public int addressType;
    public AddressInfo addressInfo;

    public AddressChangedEvent(int addressType, AddressInfo addressInfo) {
        this.addressType = addressType;
        this.addressInfo = addressInfo;
    }


}
