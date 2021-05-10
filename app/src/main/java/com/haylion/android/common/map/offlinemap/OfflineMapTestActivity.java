package com.haylion.android.common.map.offlinemap;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.amap.api.maps.MapsInitializer;
import com.amap.api.services.route.DriveRouteResult;
import com.haylion.android.R;
import com.haylion.android.common.map.BaseMapActivity;

/**
 * @author dengzh
 * @date 2019/11/27
 * Description:离线地图测试页面
 */
public class OfflineMapTestActivity extends BaseMapActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_map_test);
        MapsInitializer.sdcardDir = OffLineMapUtils.getSdCacheDir(this);
        initMap(savedInstanceState);
    //    mAMap.setLoadOfflineData(true);  //重新加载离线地图数据
      //  mAMap.reloadMap();
    }

    @Override
    public void handleDriveRouteSearchedResult(DriveRouteResult driveRouteResult, int i) {

    }

    @Override
    public void zoomToSpan() {

    }
}
