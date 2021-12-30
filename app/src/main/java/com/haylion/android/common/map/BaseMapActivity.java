package com.haylion.android.common.map;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.CustomMapStyleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.haylion.android.R;
import com.haylion.android.data.repo.PrefserHelper;
import com.haylion.android.data.util.LocationUtils;
import com.haylion.android.data.base.ConfirmDialog;
import com.haylion.android.mvp.base.AbstractPresenter;
import com.haylion.android.data.base.BaseActivity;
import com.haylion.android.mvp.util.LogUtils;
import com.haylion.android.orderdetail.map.MapErrorMsg;
import com.haylion.android.service.FloatDialogService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author dengzh
 * @date 2019/11/6
 * Description: 基本地图页，与具体代码逻辑分离
 * 显示起点，终点，和路线
 */
public abstract class BaseMapActivity<P extends AbstractPresenter> extends BaseActivity<P> implements AMap.OnMapClickListener,
        AMap.OnMarkerClickListener, AMap.OnInfoWindowClickListener, AMap.InfoWindowAdapter, RouteSearch.OnRouteSearchListener {

    private final String TAG = getClass().getSimpleName();

    /**
     * 高德地图
     */
    protected MapView mMapView;
    protected AMap mAMap;
    private UiSettings mUiSettings;//定义一个UiSettings对象

    /**
     * 路径规划搜索的入口
     */
    protected RouteSearch mRouteSearch;
    /**
     * 驾车路径规划的结果集
     * 可以得到规划路线 和 打的费用
     */
    protected DriveRouteResult mDriveRouteResult;
    /**
     * 起点和终点 坐标
     */
    protected LatLonPoint mStartPoint;// = new LatLonPoint(39.942295, 116.335891);//起点，39.942295,116.335891
    protected LatLonPoint mEndPoint; //= new LatLonPoint(39.995576, 116.481288);//终点，39.995576,116.481288
    /**
     * 途径点坐标
     */
    protected List<LatLonPoint> mWayPoints = null;

    /**
     * 司机路线规划覆盖层，起点id，终点id，车id
     */
    protected MaasTaxiOverlay routeOverlay;
    protected int resIdStart = R.mipmap.get_on;
    protected int resIdEnd = R.mipmap.get_off;
    protected int resIdCar = R.mipmap.driving_car;
    private String markerTitle = "取货地址";


    /**
     * 是否因为数据刷新更新地图
     */
    protected boolean isUpdateMapByRefreshData = true;

    /**
     * 搜索时进度条
     */
    private ProgressDialog progDialog = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setEndMarkerTitle(String title){
        markerTitle = title;
        if (routeOverlay != null){
            routeOverlay.setEndMarkerTitle(markerTitle);
        }
    }
    public void setStartMarkerTitle(String title){
        if (routeOverlay != null){
            routeOverlay.setStartMarkerTitle(title);
        }
    }
    /**
     * 子类必须在 onCreate() 中调用
     *
     * @param savedInstanceState
     */
    protected void initMap(@Nullable Bundle savedInstanceState) {
        mMapView = findViewById(R.id.route_map);
        mMapView.onCreate(savedInstanceState);// 此方法必须重写
        if (mAMap == null) {
            mAMap = mMapView.getMap();
        }
        mAMap.setOnMapClickListener(this);
        mAMap.setOnMarkerClickListener(this);
        mAMap.setOnInfoWindowClickListener(this);

        mRouteSearch = new RouteSearch(this);
        mRouteSearch.setRouteSearchListener(this);


        //地图内置UI及手势控制器
        mUiSettings = mAMap.getUiSettings();//实例化UiSettings类对象
        mUiSettings.setZoomControlsEnabled(false);//设置缩放按钮显示
        mUiSettings.setMyLocationButtonEnabled(false);//设置定位按钮显示
        mUiSettings.setZoomGesturesEnabled(true);//缩放手势
        mUiSettings.setScrollGesturesEnabled(true);//顺滑手势
        mUiSettings.setRotateGesturesEnabled(false);//旋转手势
        mUiSettings.setTiltGesturesEnabled(false);//倾斜手势

        routeOverlay = new MaasTaxiOverlay(this, mAMap);
        routeOverlay.setEndMarkerTitle(markerTitle);
        //设置自定义地图
        //setMapCustomStyleFile();
        setInitLocation();
    }


    /**
     * 地图默认位置显示当前位置
     */
    private void setInitLocation() {
        if (FloatDialogService.getCurrentGps() != null) {
            //构造一个位置
            LatLng latLng = new LatLng(FloatDialogService.getCurrentGps().getLatitude(), FloatDialogService.getCurrentGps().getLongitude());
            mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (mMapView != null) {
            mMapView.onResume();
        }
        openGPSSettings();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mMapView != null) {
            mMapView.onPause();
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMapView != null) {
            mMapView.onSaveInstanceState(outState);
        }
    }


    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (routeOverlay != null) {
            routeOverlay.removeFromMap();
        }
        if (mMapView != null) {
            mMapView.onDestroy();
        }
    }

    /**
     * 显示进度框
     */
    protected void showProgressDialog() {
        if (progDialog == null) {
            progDialog = new ProgressDialog(this);
        }
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在搜索");
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    protected void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }


    @Override
    public View getInfoWindow(Marker marker) {
        TextView textView = new TextView(this);
        textView.setText(marker.getTitle());
        return textView;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    /**
     * 路线规划成功
     *
     * @param result
     * @param rCode
     */
    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int rCode) {
        if (isUpdateMapByRefreshData) { //获取数据后第一次刷新，清空地图
            mAMap.clear();
            routeOverlay.removeFromMap();
        }
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mDriveRouteResult = result;
                    final DrivePath drivePath = mDriveRouteResult.getPaths().get(0);
                    if (drivePath == null) {
                        return;
                    }
                    //处理路线规划结果-业务逻辑
                    handleDriveRouteSearchedResult(result, rCode);
                    //显示
                    routeOverlay.showDrivePath(drivePath,
                            mDriveRouteResult.getStartPos(),
                            mDriveRouteResult.getTargetPos(),
                            mWayPoints
                    );

                    if (isUpdateMapByRefreshData) {
                        zoomToSpan();
                        isUpdateMapByRefreshData = false; //只在订单信息刷新时，才更新地图的视窗。
                    }
                } else if (result.getPaths() == null) {
                    toast(R.string.toast_map_drive_route_search_fail);
                }
            } else {
                toast(R.string.toast_map_drive_route_search_fail);
            }
        } else {
            MapErrorMsg.showerror(rCode);
        }
    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }

    /**
     * 开始路线规划，接受数据是在 onDriveRouteSearched() 方法
     */
    protected void searchRoute() {
        int mode = PrefserHelper.getNaviSetInfo().getDrivingMode();
        if (mStartPoint == null) {
            Log.e(TAG, "起点未设置");
            return;
        }
        if (mEndPoint == null) {
            Log.e(TAG, "终点未设置");
            return;
        }
        //路径的起点终点
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(mStartPoint, mEndPoint);
        // 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
        RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, mode, mWayPoints, null, "");
        // 异步路径规划驾车模式查询
        Log.e(TAG, "开始规划路线");
        mRouteSearch.calculateDriveRouteAsyn(query);
    }

    /**
     * 路线规划成功回调
     * 已经做了前置处理，只需处理与逻辑相关的功能即可（例如状态不同，起点终点图标和名字不同）。
     *
     * @param driveRouteResult
     * @param i
     */
    public abstract void handleDriveRouteSearchedResult(DriveRouteResult driveRouteResult, int i);

    /**
     * 起点终点显示范围
     */
    public abstract void zoomToSpan();

    /**
     * 设置自定义地图样式
     */
    private CustomMapStyleOptions mapStyleOptions = new CustomMapStyleOptions();

    private void setMapCustomStyleFile() {
        String styleName = "style_new.data";
        InputStream inputStream = null;
        try {
            inputStream = getAssets().open(styleName);
            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);

            // 设置自定义样式
            mapStyleOptions.setStyleData(b);
            mapStyleOptions.setEnable(true);
            mAMap.setCustomMapStyle(mapStyleOptions);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 跳转GPS设置
     */
    private void openGPSSettings() {
        if (!LocationUtils.checkLocationIsOpen(this)) {
            LogUtils.d("定位服务不可用,GPS定位和网络定位都未开启");
            showGpsNotOpenedTipsDialog();
        }
    }

    private ConfirmDialog mGpsNotOpenedTipsDialog;

    /**
     * @param
     * @return
     * @method
     * @description 判断定位功能是否
     * @date: 2019/11/21 16:03
     * @author: tandongdong
     */
    private void showGpsNotOpenedTipsDialog() {
        if (mGpsNotOpenedTipsDialog == null) {
            mGpsNotOpenedTipsDialog = ConfirmDialog.newInstance().setMessage(getString(R.string.dialog_msg_gps_not_open))
                    .setOnClickListener(new ConfirmDialog.OnClickListener() {
                        @Override
                        public void onPositiveClick(View view) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }

                        @Override
                        public void onDismiss() {

                        }
                    })
                    .setPositiveText(getString(R.string.go_open))
                    .setType(ConfirmDialog.TYPE_ONE_BUTTON)
                    .setCancelOutside(false);
        }
        if (mGpsNotOpenedTipsDialog.getDialog() != null && mGpsNotOpenedTipsDialog.getDialog().isShowing()) {
            //dialog is showing so do something
        } else {
            mGpsNotOpenedTipsDialog.show(getSupportFragmentManager(), "");
        }
    }
}
