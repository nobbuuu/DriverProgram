package com.haylion.android.common.aibus_location;

import com.haylion.android.common.aibus_location.data.GpsData;

public interface LocationListener {
    //GPS数据更新
    void updateGpsData(GpsData gpsdata);
}
