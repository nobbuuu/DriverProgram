package com.haylion.android.data.model;


import java.util.List;

 /**
  * @class  OrderDetailExt
  * @description 订单详情，包含多日订单信息。
  * @date: 2019/12/17 10:34
  * @author: tandongdong
  */
public class OrderDetailExt extends  OrderDetail{
    private List<OrderDateSimple> travelDateOutputFormList;

    public List<OrderDateSimple> getTravelDateOutputFormList() {
        return travelDateOutputFormList;
    }

    public void setTravelDateOutputFormList(List<OrderDateSimple> travelDateOutputFormList) {
        this.travelDateOutputFormList = travelDateOutputFormList;
    }

}
