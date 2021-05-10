package com.haylion.android.data.model;

import java.util.List;

 /**
  * @class  OrderExt
  * @description 订单类，包含多日订单细腻
  * @date: 2019/12/17 10:35
  * @author: tandongdong
  */
public class OrderExt extends Order {
    private List<OrderDateSimple> travelDateOutputFormList;

    public List<OrderDateSimple> getTravelDateOutputFormList() {
        return travelDateOutputFormList;
    }

    public void setTravelDateOutputFormList(List<OrderDateSimple> travelDateOutputFormList) {
        this.travelDateOutputFormList = travelDateOutputFormList;
    }
}
