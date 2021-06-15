package com.haylion.android.utils;

import android.text.SpannableString;
import android.util.Log;
import android.view.View;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.DistanceResult;
import com.amap.api.services.route.DistanceSearch;
import com.amap.api.services.route.RouteSearch;
import com.haylion.android.MyApplication;
import com.haylion.android.R;
import com.haylion.android.data.repo.PrefserHelper;
import com.haylion.android.data.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class AmapUtils {
    public static final String TAG = "AmapUtils";

    /**
     * 计算两坐标之前的距离
     *
     * @param start
     * @param end
     * @return
     */
    public static double getDistance(LatLng start, LatLng end) {

        double lon1 = (Math.PI / 180) * start.longitude;
        double lon2 = (Math.PI / 180) * end.longitude;
        double lat1 = (Math.PI / 180) * start.latitude;
        double lat2 = (Math.PI / 180) * end.latitude;

        // 地球半径
        double R = 6371;

        // 两点间距离 km，如果想要米的话，结果*1000就可以了
        double d = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1))
                * R;

        return d * 1000;
    }

    public static void caculateDistance(LatLng start, LatLng end, RouteSearch.OnRouteSearchListener callBack) {
        /*DistanceSearch.DistanceQuery distanceQuery = new DistanceSearch.DistanceQuery();
        DistanceSearch distanceSearch = new DistanceSearch(MyApplication.getContext());

        LatLonPoint startp = new LatLonPoint(start.latitude,end.longitude);
        LatLonPoint destp = new LatLonPoint(end.latitude, end.longitude);

        List<LatLonPoint> latLonPoints = new ArrayList<LatLonPoint>();
        latLonPoints.add(startp);
        distanceQuery.setOrigins(latLonPoints);
        distanceQuery.setDestination(destp);
//        设置测量方式，支持直线和驾车
        distanceQuery.setType(DistanceSearch.TYPE_DRIVING_DISTANCE);
        distanceSearch.setDistanceSearchListener(callBack);
        distanceSearch.calculateRouteDistanceAsyn(distanceQuery);*/

        RouteSearch mRouteSearch = new RouteSearch(MyApplication.getContext());
        mRouteSearch.setRouteSearchListener(callBack);
        LatLonPoint mStartPoint = new LatLonPoint(start.latitude, start.longitude);
        LatLonPoint mEndPoint = new LatLonPoint(end.latitude, end.longitude);
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
        RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, mode, null, null, "");
        // 异步路径规划驾车模式查询
        mRouteSearch.calculateDriveRouteAsyn(query);
    }

    public static String matchTime(long estimateTime) {
        String matchTime = "";
        if (estimateTime > 60 * 60) {
            long hour = estimateTime % (60 * 60);
            long minutes = (estimateTime % (60 * 60)) / 60;
            if (minutes == 0) {  //只显示小时
                matchTime = hour + "小时";
            } else {
                //显示小时和分钟
                matchTime = hour + "小时" + minutes + "分钟";
            }
        } else if (estimateTime > 60) {  //只显示分钟
            long minutes = estimateTime / 60;
            matchTime = minutes + "分钟";
        } else { //只显示秒
            matchTime = estimateTime + "秒";
        }
        return matchTime;
    }
}
