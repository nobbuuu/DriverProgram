package com.haylion.android.common.map;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveStep;
import com.amap.api.services.route.TMC;
import com.haylion.android.R;
import com.haylion.android.data.model.AddressInfo;
import com.haylion.android.mvp.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dengzh
 * @date 2019/11/20
 * Description:
 * 1.显示规划路线
 * 1）起点（上车点）、终点（下车点）、单色路段、箭头
 * 2）起点（车辆）、终点（上下车点）、多彩路段、箭头
 * <p>
 * 2.真实路线
 * 起点（上车点）、终点（下车点）、灰色路段、箭头
 * <p>
 * 问题：
 * 1）PolylineOptions 必须每次都重建，否则会出现路线不绘制问题
 */
public class MaasTaxiOverlay extends RouteOverlay {

    private String TAG = getClass().getSimpleName();

    /**
     * 规划路线
     * 定义了驾车路径规划的一个方案。
     */
    private DrivePath mDrivePath;
    /**
     * 交通信息类集合
     * 可以获取本段路况的坐标点集合，路况状态等
     */
    private List<TMC> mTmcList = new ArrayList<>();
    /**
     * 规划路线坐标点集合
     */
    private List<LatLng> mPlanLatLngList = new ArrayList<>();
    /**
     * 真实路线坐标点集合
     */
    private List<LatLng> mTrueLatLngList;

    private PolylineOptions mPolylineOptions;  //单色线段的选项类
    private PolylineOptions mPolylineOptionsColor; //多彩线段的选项类
    private PolylineOptions mArrowPolylineOptions; //箭头轨迹线
    private List<BitmapDescriptor> mTexTureList;  //纹理列表
    private Polyline mPolyline;    //线段

    private int resIdStart = R.mipmap.get_on;  //起点图标
    private int resIdEnd = R.mipmap.get_off;     //终点图标
    private String startAddress;   //起点地址名
    private String endAddress;      //终点地址名
    private float orientation = -1;  //车辆方向,范围[0,360],为-1不显示方向
    private float lastOrientation;  //最近一次的方向
    private boolean isCarStart;   //是否以车辆位置为起点
    private boolean isColorFulLine = true; //是否显示多彩线段
    private boolean isShowPolyline = true;  //是否显示轨迹路线

    private boolean isUpdateStartMarker = true;  //是否更新起点Marker
    private boolean isUpdateEndMarker = true;    //是否更新终点Marker

    private List<LatLonPoint> mWayPoints;
    /**
     * 途径点 Marker
     */
    private List<Marker> mWayPointMarkers;


    public MaasTaxiOverlay(Context context, AMap map) {
        super(context);
        mAMap = map;
    }

    /**
     * 是否是多彩路段
     *
     * @param colorFulLine
     */
    public MaasTaxiOverlay setColorFulLine(boolean colorFulLine) {
        isColorFulLine = colorFulLine;
        return this;
    }

    /**
     * 是否显示路线
     *
     * @param isShowPolyline
     */
    public MaasTaxiOverlay setShowPolyline(boolean isShowPolyline) {
        this.isShowPolyline = isShowPolyline;
        return this;
    }

    /**
     * 是否更新起点Marker
     *
     * @param isUpdateStartMarker
     * @return
     */
    public MaasTaxiOverlay setUpdateStartMarker(boolean isUpdateStartMarker) {
        this.isUpdateStartMarker = isUpdateStartMarker;
        return this;
    }

    /**
     * 是否更新终点Marker
     *
     * @param isUpdateEndMarker
     * @return
     */
    public MaasTaxiOverlay setUpdateEndMarker(boolean isUpdateEndMarker) {
        this.isUpdateEndMarker = isUpdateEndMarker;
        return this;
    }


    /**
     * 设置起点和终点icon
     *
     * @param start
     * @param end
     */
    public void setStartAndEndIcon(int start, int end) {
        resIdStart = start;
        resIdEnd = end;
        isCarStart = false;
        orientation = -1;
    }

    /**
     * 设置起点(车辆)和终点 icon
     *
     * @param start
     * @param end
     * @param ori   旋转方向
     */
    public void setCarAndEndIcon(int start, int end, float ori) {
        resIdStart = start;
        resIdEnd = end;
        isCarStart = true;
        orientation = ori;
    }

    /**
     * 设置起点，终点 地名
     *
     * @param start
     * @param end
     */
    public void setStartAndEndAddress(String start, String end) {
        startAddress = start;
        endAddress = end;
    }

    /**
     * 显示规划路线
     * 可能需要保持一些覆盖物不变，所以不清空画布
     *
     * @param drivePath 规划路线
     * @param start     起点坐标
     * @param end       终点坐标
     */
    public void showDrivePath(DrivePath drivePath, LatLonPoint start, LatLonPoint end) {
        showDrivePath(drivePath, start, end, null);
    }

    /**
     * 显示规划路线
     * 可能需要保持一些覆盖物不变，所以不清空画布
     *
     * @param drivePath 规划路线
     * @param start     起点坐标
     * @param end       终点坐标
     * @param wayPoints 途径点坐标
     */
    public void showDrivePath(DrivePath drivePath, LatLonPoint start, LatLonPoint end, List<LatLonPoint> wayPoints) {
        mDrivePath = drivePath;
        startPoint = AMapUtil.convertToLatLng(start);
        endPoint = AMapUtil.convertToLatLng(end);
        mWayPoints = wayPoints;
        addStartAndEndMarker();
        addWayPointMaker();
        addPlanPolyline();
    }


    /**
     * 添加起点和终点Marker
     * 起点：车辆当前位置/上车点。
     * 终点：上车点/下车点。
     * 注意：车辆icon旋转中心点要在icon中心，所以要调整setAnchor。
     */
    protected void addStartAndEndMarker() {

        //起点
        if (isUpdateStartMarker || isCarStart) {
            View startMarkerView = AMapUtil.getView(mContext, resIdStart, startAddress);
            if (startMarker != null) {
                startMarker.remove();
            }
            startMarker = mAMap.addMarker(new MarkerOptions()
                    .position(startPoint)
                    .icon(BitmapDescriptorFactory.fromView(startMarkerView)));
            if (isCarStart) {
                //车辆icon瞄点
                startMarker.setAnchor(0.5f, 0.5f);
                if (orientation >= 0) {
                    startMarker.setRotateAngle(360 - orientation);
                    lastOrientation = orientation;
                } else {
                    //orientation<0，说明此时速度为0，保持最近一次的角度
                    startMarker.setRotateAngle(360 - lastOrientation);
                }
            } else {
                //上车点icon瞄点
                startMarker.setAnchor(0.5f, 1);
                startMarker.setRotateAngle(0);
            }
            startMarker.showInfoWindow();
            Log.e(TAG, "lastOrientation:" + lastOrientation);
            Log.e(TAG, "startMarker 更新");
        }

        //终点
        if (isUpdateEndMarker) {
            View endMarkerView = AMapUtil.getView(mContext, resIdEnd, endAddress);
            if (endMarker != null) {
                endMarker.remove();
            }
            endMarker = mAMap.addMarker(new MarkerOptions()
                    .position(endPoint)
                    .icon(BitmapDescriptorFactory.fromView(endMarkerView)));
            endMarker.setAnchor(0.5f, 1);
            endMarker.setToTop(); //防止被车辆图标覆盖
            endMarker.showInfoWindow();
            Log.e(TAG, "endMarker 更新");
        }
    }

    private void addWayPointMaker() {
        if (mWayPoints == null || mWayPoints.isEmpty()) {
            return;
        }
        if (mWayPointMarkers == null) {
            mWayPointMarkers = new ArrayList<>();
        } else {
            for (Marker marker : mWayPointMarkers) {
                marker.remove();
            }
        }
        for (LatLonPoint latLonPoint : mWayPoints) {
            Marker marker = mAMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude()))
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_way_point)));
            mWayPointMarkers.add(marker);
        }
    }

    /**
     * 添加规划轨迹线
     */
    private void addPlanPolyline() {
        if (mDrivePath == null) {
            return;
        }
        mPlanLatLngList.clear();
        mTmcList.clear();
        //返回驾车规划方案的路段列表
        List<DriveStep> drivePaths = mDrivePath.getSteps();
        for (DriveStep step : drivePaths) {
            //返回驾车路段的坐标点集合
            List<LatLonPoint> latlonPoints = step.getPolyline();
            //获取搜索返回的路径规划交通拥堵信息
            List<TMC> tmclist = step.getTMCs();
            mTmcList.addAll(tmclist);
            //将该路段所有坐标点转换后，添加到轨迹列表
            for (LatLonPoint latlonpoint : latlonPoints) {
                mPlanLatLngList.add(AMapUtil.convertToLatLng(latlonpoint));
            }
        }

        if (mTmcList.size() > 0) {
            if (isColorFulLine) {
                addPolylinesWithTexture(mTmcList);
            } else {
                addPolylineWithSingleColor();
            }
        }
    }


  /*  private void addPolylinesWithTexture(List<TMC> tmcSections) {
        //创建纹理列表
        if (mTexTureList == null) {
            mTexTureList = new ArrayList<BitmapDescriptor>();
            mTexTureList.add(BitmapDescriptorFactory.fromResource(R.drawable.custtexture_green));
            mTexTureList.add(BitmapDescriptorFactory.fromResource(R.drawable.custtexture_slow));
            mTexTureList.add(BitmapDescriptorFactory.fromResource(R.drawable.custtexture_bad));
            mTexTureList.add(BitmapDescriptorFactory.fromResource(R.drawable.custtexture_grayred));
            mTexTureList.add(BitmapDescriptorFactory.fromResource(R.drawable.custtexture_no));
        }
        List<LatLng> tmcLatLngList = new ArrayList<>();
        tmcLatLngList.add(AMapUtil.convertToLatLng(tmcSections.get(0).getPolyline().get(0)));
        List<Integer> texIndexList = new ArrayList<Integer>();
        //手动添加第一个和最后一个纹理，如此才能和Polyline坐标点对应
        texIndexList.add(0);
        for (TMC tmc : tmcSections) {
            int textureIndex = getTextureIndex(tmc.getStatus());
            List<LatLonPoint> mployline = tmc.getPolyline();
            //位置从1开始，因为第n个tmc的最后一个，和第n+1个tmc的第一个坐标点一样
            for (int j = 0; j < mployline.size(); j++) {
                //添加坐标点
                tmcLatLngList.add(AMapUtil.convertToLatLng(mployline.get(j)));
                //添加纹理位置
                texIndexList.add(textureIndex);
            }
        }
        texIndexList.add(4);

        mPolylineOptionsColor = new PolylineOptions();
        mPolylineOptionsColor.width(getRouteWidth())
                .visible(isShowPolyline)
                .addAll(tmcLatLngList)
                .setCustomTextureList(mTexTureList)
                .setCustomTextureIndex(texIndexList);
        // .lineJoinType(PolylineOptions.LineJoinType.LineJoinRound); //Polyline连接处形状，放大地图时会有路段变色bug
        if(mPolyline!=null){
            mPolyline.remove();
        }
        mPolyline = mAMap.addPolyline(mPolylineOptionsColor);
        Log.e(TAG,"PolylineOptionsColor 更新");
        addArrowPolyline(tmcLatLngList);
    }
*/

    /**
     * 添加纹理线多彩路线
     * 1）遍历tmcSections时不要从1开始，不然会漏点，路线没有完全贴合道路
     * 官方demo的从1开始，可参考https://lbs.amap.com/dev/demo/drive-route#Android，下载源代码 去调整
     */
    private void addPolylinesWithTexture(List<TMC> tmcSections) {
        //创建纹理列表
        if (mTexTureList == null) {
            mTexTureList = new ArrayList<BitmapDescriptor>();
            mTexTureList.add(BitmapDescriptorFactory.fromResource(R.drawable.custtexture_green));
            mTexTureList.add(BitmapDescriptorFactory.fromResource(R.drawable.custtexture_slow));
            mTexTureList.add(BitmapDescriptorFactory.fromResource(R.drawable.custtexture_bad));
            mTexTureList.add(BitmapDescriptorFactory.fromResource(R.drawable.custtexture_grayred));
            mTexTureList.add(BitmapDescriptorFactory.fromResource(R.drawable.custtexture_no));
        }
        List<LatLng> tmcLatLngList = new ArrayList<>();
        tmcLatLngList.add(startPoint);
        tmcLatLngList.add(AMapUtil.convertToLatLng(tmcSections.get(0).getPolyline().get(0)));
        List<Integer> texIndexList = new ArrayList<Integer>();
        int textIndex = 0;
        texIndexList.add(textIndex);
        texIndexList.add(++textIndex);
        for (TMC tmc : tmcSections) {
            int textureIndex = getTextureIndex(tmc.getStatus());
            List<LatLonPoint> mployline = tmc.getPolyline();
            //位置从0开始，不然会漏点
            for (int j = 0; j < mployline.size(); j++) {
                //添加坐标点
                tmcLatLngList.add(AMapUtil.convertToLatLng(mployline.get(j)));
                //添加纹理位置
                texIndexList.add(textureIndex);
            }
        }
        tmcLatLngList.add(endPoint);
        texIndexList.add(4);

        mPolylineOptionsColor = new PolylineOptions();
        mPolylineOptionsColor.width(getRouteWidth())
                .visible(isShowPolyline)
                .addAll(tmcLatLngList)
                .setCustomTextureList(mTexTureList);
        // .setCustomTextureIndex(texIndexList).lineJoinType(PolylineOptions.LineJoinType.LineJoinRound); //Polyline连接处形状，放大地图时会有路段变色bug
        if (mPolyline != null) {
            mPolyline.remove();
        }
        mPolyline = mAMap.addPolyline(mPolylineOptionsColor);
        Log.e(TAG, "PolylineOptionsColor 更新");
        addArrowPolyline(tmcLatLngList);
    }


    /**
     * 添加规划轨迹线，单色
     */
    private void addPolylineWithSingleColor() {
        if (mPlanLatLngList == null || mPlanLatLngList.size() == 0) {
            return;
        }
        mPolylineOptions = new PolylineOptions();
        mPolylineOptions.setCustomTexture(BitmapDescriptorFactory.fromResource(R.drawable.custtexture_no))
                .width(getRouteWidth())
                .addAll(mPlanLatLngList)
                .visible(isShowPolyline);

        if (mPolyline != null) {
            mPolyline.remove();
        }
        mPolyline = mAMap.addPolyline(mPolylineOptions);
        Log.e(TAG, "PolylineSingleColor 更新");
        addArrowPolyline(mPlanLatLngList);
    }

    /**
     * 显示真实路线
     * 行程结束的，显示上车点，下车点，真实轨迹。
     *
     * @param startAddressInfo 起点
     * @param endAddressInfo   终点
     * @param list             真实轨迹列表
     */
    public void showTrueLatLng(AddressInfo startAddressInfo, AddressInfo endAddressInfo, List<LatLng> list) {
        mAMap.clear();
        setStartAndEndAddress(startAddressInfo.getName(), endAddressInfo.getName());
        startPoint = startAddressInfo.getLatLng();
        endPoint = endAddressInfo.getLatLng();
        this.mTrueLatLngList = list;
        addTrueStartAndEndMarker();
        addTruePolyline();
    }

    /**
     * 添加真实的轨迹线
     */
    private void addTruePolyline() {
        if (mTrueLatLngList == null || mTrueLatLngList.size() == 0) {
            return;
        }
        mPolylineOptions = new PolylineOptions();
        mPolylineOptions.setCustomTexture(BitmapDescriptorFactory.fromResource(R.drawable.custtexture_pass))
                .width(getRouteWidth())
                .addAll(mTrueLatLngList)
                .visible(isShowPolyline);
        addPolyLine(mPolylineOptions);

        //添加箭头
        addArrowPolyline(mTrueLatLngList);
    }

    /**
     * 添加真实的起点和终点Marker
     */
    protected void addTrueStartAndEndMarker() {
        //终点
        View endMarkerView = AMapUtil.getView(mContext, resIdEnd, endAddress);
        endMarker = mAMap.addMarker(new MarkerOptions()
                .position(endPoint)
                .icon(BitmapDescriptorFactory.fromView(endMarkerView)));
        endMarker.setAnchor(0.5f, 1);
        endMarker.showInfoWindow();

        //起点
        View startMarkerView = AMapUtil.getView(mContext, resIdStart, startAddress);
        startMarker = mAMap.addMarker(new MarkerOptions()
                .position(startPoint)
                .icon(BitmapDescriptorFactory.fromView(startMarkerView)));
        startMarker.setAnchor(0.5f, 1);
        startMarker.showInfoWindow();
    }


    private Polyline mArrowPolyline;

    /**
     * 添加箭头
     *
     * @param list
     */
    private void addArrowPolyline(List<LatLng> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        mArrowPolylineOptions = new PolylineOptions();
        mArrowPolylineOptions.width(getRouteWidth())
                .visible(isShowPolyline)
                .addAll(list)
                .setCustomTexture(BitmapDescriptorFactory.fromResource(R.drawable.custtexture_aolr));

        if (mArrowPolyline != null) {
            mArrowPolyline.remove();
        }
        mArrowPolyline = mAMap.addPolyline(mArrowPolylineOptions);
        Log.e(TAG, "ArrowPolyline 更新");
    }

    /**
     * 获取纹理所在列表的位置
     *
     * @param status
     * @return
     */
    private int getTextureIndex(String status) {
        if (status.equals("畅通")) {
            return 0;
        } else if (status.equals("缓行")) {
            return 1;
        } else if (status.equals("拥堵")) {
            return 2;
        } else if (status.equals("严重拥堵")) {
            return 3;
        } else {
            return 4;
        }
    }

    /**
     * 设置显示在规定屏幕范围内的地图经纬度范围
     *
     * @param paddingLeft
     * @param paddingRight
     * @param paddingTop
     * @param paddingBottom
     */
    public void zoomToSpan(int paddingLeft, int paddingRight, int paddingTop, int paddingBottom) {
        if (startPoint != null) {
            if (mAMap == null) {
                return;
            }
            try {
                LatLngBounds bounds = getLatLngBounds();
                mAMap.animateCamera(CameraUpdateFactory
                        .newLatLngBoundsRect(bounds, paddingLeft, paddingRight, paddingTop, paddingBottom));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }


    public void removeFromMap() {
        if (startMarker != null) {
            startMarker.remove();
        }
        if (endMarker != null) {
            endMarker.remove();
        }
        if (mWayPointMarkers != null) {
            for (Marker marker : mWayPointMarkers) {
                marker.remove();
            }
        }
        if (mPolyline != null) {
            mPolyline.remove();
        }
        if (mArrowPolyline != null) {
            mArrowPolyline.remove();
        }
    }
}
