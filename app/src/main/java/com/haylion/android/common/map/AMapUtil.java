/**
 *
 */
package com.haylion.android.common.map;


import android.content.Context;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.DistanceSearch;
import com.amap.api.services.route.DrivePath;
import com.haylion.android.R;
import com.haylion.android.common.aibus_location.data.GpsData;
import com.haylion.android.common.view.StrokeTextView;
import com.haylion.android.data.util.BusinessUtils;
import com.haylion.android.data.util.StringUtil;
import com.haylion.android.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AMapUtil {

    public static final int RESPONSE_SUCCESS = 1000; //服务调用正常，有结果返回

    /**
     * 把LatLng对象转化为LatLonPoint对象
     */
    public static LatLonPoint convertToLatLonPoint(LatLng latlon) {
        return new LatLonPoint(latlon.latitude, latlon.longitude);
    }

    /**
     * 把LatLonPoint对象转化为LatLon对象
     */
    public static LatLng convertToLatLng(LatLonPoint latLonPoint) {
        return new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
    }

    /**
     * 获取MarkView 的 填充View
     *
     * @param resId 图标id
     * @param title 文字
     * @return
     */
    public static View getView(Context context, int resId, String title) {
        LayoutInflater factory = LayoutInflater.from(context);
        View view = factory.inflate(R.layout.item_marker_title, null);
        TextView tv_title = view.findViewById(R.id.tv_marker_title);
        ImageView iv_icon = view.findViewById(R.id.iv_marker);
        tv_title.setText(title);
		/*if("".equals(title)){
			tv_title.setVisibility(View.GONE);
		}*/
        iv_icon.setImageResource(resId);
        return view;
    }

    /**
     * 获取MarkView 的 填充View
     *
     * @param resId 图标id
     * @param title 文字
     * @return
     */
    public static View getCurMarkerView(Context context, int resId, String title) {
        LayoutInflater factory = LayoutInflater.from(context);
        View view = factory.inflate(R.layout.item_marker_title, null);
        TextView tv_title = view.findViewById(R.id.tv_marker_title);
        ImageView iv_icon = view.findViewById(R.id.iv_marker);
//		tv_title.setText(title);
        iv_icon.setImageResource(resId);
        return view;
    }

    /**
     * 获取MarkView 的 填充View
     *
     * @param resId 图标id
     * @return
     */
    public static View getEndMarkerView(Context context, int resId, DrivePath drivePath) {
        LayoutInflater factory = LayoutInflater.from(context);
        View view = factory.inflate(R.layout.item_marker_end, null);
        TextView distance_tv = view.findViewById(R.id.distance_tv);
        TextView costTime_tv = view.findViewById(R.id.costTime_tv);
        TextView unit_distance = view.findViewById(R.id.unit_distance);
        TextView unit_time = view.findViewById(R.id.unit_time);
        ImageView iv_icon = view.findViewById(R.id.iv_marker);
        if (drivePath != null) {
            float pathDistance = drivePath.getDistance();
            if (pathDistance > 1000) {
                String format = String.format(Locale.getDefault(), "%.1f", pathDistance / 1000);
                distance_tv.setText(format);
                unit_distance.setText(" km");
            } else {
                String format = String.format(Locale.getDefault(), "%.0f", pathDistance);
                distance_tv.setText(format);
                unit_distance.setText(" m");
            }
            long estimateTime = drivePath.getDuration();
            Log.d("aaa", "duration = " + estimateTime);
            if (estimateTime > 60 * 60) {
                String timeLenthStr = DateUtils.getTimeLenthStr(estimateTime * 1000);
                costTime_tv.setText(" " + timeLenthStr);
                unit_time.setVisibility(View.GONE);
            } else if (estimateTime > 60) {  //只显示分钟
                long minutes = estimateTime / 60;
                costTime_tv.setText(" " + minutes + "");
                unit_time.setText(" 分钟");
            } else { //只显示秒
                costTime_tv.setText(" " + estimateTime + "");
                unit_time.setText(" 秒");
            }

        }
//		tv_title.setText(title);
        iv_icon.setImageResource(resId);
        return view;
    }


    /**
     * 计算路线距离
     * @param context
     * @param start
     * @param end
     * @param listener
     */
    public static void calculateDistance(Context context, GpsData start, GpsData end, DistanceSearch.OnDistanceSearchListener listener) {
        DistanceSearch distanceSearch = new DistanceSearch(context);
        distanceSearch.setDistanceSearchListener(listener);
        DistanceSearch.DistanceQuery distanceQuery = new DistanceSearch.DistanceQuery();
        //起点
        List<LatLonPoint> latLonPoints = new ArrayList<LatLonPoint>();
        latLonPoints.add(new LatLonPoint(start.getLatitude(), start.getLongitude()));
        distanceQuery.setOrigins(latLonPoints);
        //终点
        distanceQuery.setDestination(new LatLonPoint(end.getLatitude(), end.getLongitude()));
        //设置测量方式，支持直线和驾车
        distanceQuery.setType(DistanceSearch.TYPE_DRIVING_DISTANCE); //测量驾车距离
        //发送请求
        distanceSearch.calculateRouteDistanceAsyn(distanceQuery);
    }

    public static void calculateDistance(Context context, LatLng start, LatLng end, DistanceSearch.OnDistanceSearchListener listener) {
        DistanceSearch distanceSearch = new DistanceSearch(context);
        distanceSearch.setDistanceSearchListener(listener);
        DistanceSearch.DistanceQuery distanceQuery = new DistanceSearch.DistanceQuery();
        //起点
        List<LatLonPoint> latLonPoints = new ArrayList<LatLonPoint>();
        latLonPoints.add(convertToLatLonPoint(start));
        distanceQuery.setOrigins(latLonPoints);
        //终点
        distanceQuery.setDestination(convertToLatLonPoint(end));
        //设置测量方式，支持直线和驾车
        distanceQuery.setType(DistanceSearch.TYPE_DRIVING_DISTANCE); //测量驾车距离
        //发送请求
        distanceSearch.calculateRouteDistanceAsyn(distanceQuery);
    }

    /**
     * 获取两点间距离 直接用官方 MapUtils提供的即可
     *
     * @param start
     * @param end
     * @return
     */
/*	public static float calculateDistance(LatLng start, LatLng end) {
		double x1 = start.longitude;
		double y1 = start.latitude;
		double x2 = end.longitude;
		double y2 = end.latitude;
		return calculateDistance(x1, y1, x2, y2);
	}

	public static float calculateDistance(double x1, double y1, double x2, double y2) {
		final double NF_pi = 0.01745329251994329; // 弧度 PI/180
		x1 *= NF_pi;
		y1 *= NF_pi;
		x2 *= NF_pi;
		y2 *= NF_pi;
		double sinx1 = Math.sin(x1);
		double siny1 = Math.sin(y1);
		double cosx1 = Math.cos(x1);
		double cosy1 = Math.cos(y1);
		double sinx2 = Math.sin(x2);
		double siny2 = Math.sin(y2);
		double cosx2 = Math.cos(x2);
		double cosy2 = Math.cos(y2);
		double[] v1 = new double[3];
		v1[0] = cosy1 * cosx1 - cosy2 * cosx2;
		v1[1] = cosy1 * sinx1 - cosy2 * sinx2;
		v1[2] = siny1 - siny2;
		double dist = Math.sqrt(v1[0] * v1[0] + v1[1] * v1[1] + v1[2] * v1[2]);

		return (float) (Math.asin(dist / 2) * 12742001.5798544);
	}*/

    /**
     * 替换掉地址中的广东省深圳市字样
     * @param address
     * @return
     */
    public static String getAddress(String address) {
        if (address == null) {
            return null;
        }
        return address.replace("广东省深圳市", "");
    }

}
