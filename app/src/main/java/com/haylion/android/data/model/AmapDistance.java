package com.haylion.android.data.model;

import com.google.gson.annotations.SerializedName;

 /**
  * @class  AmapDistance
  * @description 高德的距离信息
  * @date: 2019/12/17 10:19
  * @author: tandongdong
  */
public class AmapDistance {

    @SerializedName("origin_id")
    private String originId; // 起点坐标序列号（从１开始）

    @SerializedName("dest_id")
    private String destId; // 终点坐标序列号（从１开始）

    private String distance; // 路径距离，单位：米

    private String duration; // 预计行驶时间，单位：秒

    public AmapDistance(String originId, String destId, String distance, String duration) {
        this.originId = originId;
        this.destId = destId;
        this.distance = distance;
        this.duration = duration;
    }

    public AmapDistance(String distance, String duration) {
        this.distance = distance;
        this.duration = duration;
    }

    public String getOriginId() {
        return originId;
    }

    public void setOriginId(String originId) {
        this.originId = originId;
    }

    public String getDestId() {
        return destId;
    }

    public void setDestId(String destId) {
        this.destId = destId;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }


}
