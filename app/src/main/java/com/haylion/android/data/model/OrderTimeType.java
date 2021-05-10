package com.haylion.android.data.model;

 /**
  * @class  OrderTimeType
  * @description 订单的类型（时间维度）
  * @date: 2019/12/17 10:37
  * @author: tandongdong
  */
public enum OrderTimeType {

    UNKNOWN(-1),
    IN_PROGRESS(1), //
    HISTORY(2), // 历史订单
    PLANNED(3), // 今日之后的计划订单
    TODAY(4); // 今天订单

    private int type;

    OrderTimeType(int type) {
        this.type = type;
    }

    public static OrderTimeType forType(int type) {
        switch (type) {
            case 1:
                return IN_PROGRESS;
            case 2:
                return HISTORY;
            case 3:
                return PLANNED;
            case 4:
                return TODAY;
        }
        return UNKNOWN;
    }

    public int getType() {
        return type;
    }

}
