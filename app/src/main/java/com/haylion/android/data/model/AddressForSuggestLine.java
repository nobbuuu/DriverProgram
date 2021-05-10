package com.haylion.android.data.model;

 /**
  * @class  AddressForSuggestLine
  * @description 拼车的建议行驶线路
  * @date: 2019/12/17 10:19
  * @author: tandongdong
  */
public class AddressForSuggestLine {
    private String locationName;
    private String locationDescription;
    private Double lat;
    private Double lon;

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public void setLocationDescription(String locationDescription) {
        this.locationDescription = locationDescription;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }
}
