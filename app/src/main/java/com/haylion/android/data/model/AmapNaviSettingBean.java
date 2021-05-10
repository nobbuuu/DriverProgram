package com.haylion.android.data.model;

import android.util.Log;

import com.amap.api.navi.enums.BroadcastMode;
import com.amap.api.navi.enums.PathPlanningStrategy;

/**
 * @author dengzh
 * @date 2019/11/12
 * Description:地图导航设置数据 封装类
 */
public class AmapNaviSettingBean {


    /**
     * 导航SDK提供21种驾车策略，分成两种类型：返回单一路径的策略和返回多条路径的策略，对应 PathPlanningStrategy 枚举。
     * */
    //与高德地图的默认策略（也就是不进行任何勾选）一致
    private static final int DRIVING_MULTIPLE_ROUTES_DEFAULT = PathPlanningStrategy.DRIVING_MULTIPLE_ROUTES_DEFAULT;
    //与高德地图的“躲避拥堵”策略一致
    private static final int DRIVING_MULTIPLE_ROUTES_AVOID_CONGESTION = PathPlanningStrategy.DRIVING_MULTIPLE_ROUTES_AVOID_CONGESTION;
    //与高德地图“不走高速”策略一致
    private static final int DRIVING_MULTIPLE_ROUTES_AVOID_HIGHSPEED = PathPlanningStrategy.DRIVING_MULTIPLE_ROUTES_AVOID_HIGHSPEED;
    //与高德地图“避免收费”策略一致
    private static final int DRIVING_MULTIPLE_ROUTES_AVOID_COST = PathPlanningStrategy.DRIVING_MULTIPLE_ROUTES_AVOID_COST;
    //与高德地图的“躲避拥堵&不走高速”策略一致
    private static final int DRIVING_MULTIPLE_ROUTES_AVOID_HIGHSPEED_CONGESTION = PathPlanningStrategy.DRIVING_MULTIPLE_ROUTES_AVOID_HIGHSPEED_CONGESTION;
    //与高德地图的“避免收费&不走高速”策略一致
    private static final int DRIVING_MULTIPLE_ROUTES_AVOID_HIGHTSPEED_COST = PathPlanningStrategy.DRIVING_MULTIPLE_ROUTES_AVOID_HIGHTSPEED_COST;
    //与高德地图的“躲避拥堵&避免收费”策略一致
    private static final int DRIVING_MULTIPLE_ROUTES_AVOID_COST_CONGESTION = PathPlanningStrategy.DRIVING_MULTIPLE_ROUTES_AVOID_COST_CONGESTION;
    //与高德地图的“避免拥堵&避免收费&不走高速”策略一致
    private static final int DRIVING_MULTIPLE_ROUTES_AVOID_HIGHSPEED_COST_CONGESTION = PathPlanningStrategy.DRIVING_MULTIPLE_ROUTES_AVOID_HIGHSPEED_COST_CONGESTION;
    //与高德地图的“高速优先”策略一致
    private static final int DRIVING_MULTIPLE_ROUTES_PRIORITY_HIGHSPEED = PathPlanningStrategy.DRIVING_MULTIPLE_ROUTES_PRIORITY_HIGHSPEED;
    //与高德地图的“躲避拥堵&高速优先”策略一致
    private static final int DRIVING_MULTIPLE_ROUTES_PRIORITY_HIGHSPEED_AVOID_CONGESTION = PathPlanningStrategy.DRIVING_MULTIPLE_ROUTES_PRIORITY_HIGHSPEED_AVOID_CONGESTION;

    /**
     * 路线规划策略
     * 不走高速与高速优先不能同时为true
     * 高速优先与避免收费不能同时为true
     * */
    private boolean congestion;  //躲避拥堵
    private boolean avoidhightspeed;  //不走高速
    private boolean cost;         //避免收费
    private boolean hightspeed;   //高速优先
    private int drivingMode;     //路径规划的策略，在地图sdk规划路线使用，需要和导航地图的策略一一对应
    /**
     * 语音播报模式  1-简洁播报 2-详细播报 -1-静音
     * 默认：详细播报
     * */
    private int broadcastMode = BroadcastMode.DETAIL;

    public AmapNaviSettingBean() {
    }

    public AmapNaviSettingBean(boolean congestion, boolean avoidhightspeed, boolean cost, boolean hightspeed, int broadcastMode) {
        this.congestion = congestion;
        this.avoidhightspeed = avoidhightspeed;
        this.cost = cost;
        this.hightspeed = hightspeed;
        this.broadcastMode = broadcastMode;
    }

    public boolean isCongestion() {
        return congestion;
    }

    public void setCongestion(boolean congestion) {
        this.congestion = congestion;
    }

    public boolean isAvoidhightspeed() {
        return avoidhightspeed;
    }

    public void setAvoidhightspeed(boolean avoidhightspeed) {
        this.avoidhightspeed = avoidhightspeed;
    }

    public boolean isCost() {
        return cost;
    }

    public void setCost(boolean cost) {
        this.cost = cost;
    }

    public boolean isHightspeed() {
        return hightspeed;
    }

    public void setHightspeed(boolean hightspeed) {
        this.hightspeed = hightspeed;
    }

    public int getBroadcastMode() {
        return broadcastMode;
    }

    public void setBroadcastMode(int broadcastMode) {
        this.broadcastMode = broadcastMode;
    }


    /**
     * 路径规划的策略，在地图sdk规划路线使用，需要和导航地图的策略一一对应
     * @return
     */
    public int getDrivingMode(){
        if(congestion){  //躲避拥堵
            drivingMode = DRIVING_MULTIPLE_ROUTES_AVOID_CONGESTION;
            if(avoidhightspeed){ //躲避拥堵 & 不走高速
                drivingMode = DRIVING_MULTIPLE_ROUTES_AVOID_HIGHSPEED_CONGESTION;
                if(cost){  //“躲避拥堵&不走高速&避免收费
                    drivingMode = DRIVING_MULTIPLE_ROUTES_AVOID_HIGHSPEED_COST_CONGESTION;
                }
            }else if(cost){ //躲避拥堵&避免收费
                drivingMode = DRIVING_MULTIPLE_ROUTES_AVOID_COST_CONGESTION;
            }else if(hightspeed){ //躲避拥堵&高速优先
                drivingMode = DRIVING_MULTIPLE_ROUTES_PRIORITY_HIGHSPEED_AVOID_CONGESTION;
            }

        }else if(avoidhightspeed){   //不走高速
            drivingMode = DRIVING_MULTIPLE_ROUTES_AVOID_HIGHSPEED;
            if(cost){ //不走高速&避免收费
                drivingMode = DRIVING_MULTIPLE_ROUTES_AVOID_HIGHTSPEED_COST;
            }

        }else if(cost){  //避免收费
            drivingMode = DRIVING_MULTIPLE_ROUTES_AVOID_COST;

        }else if(hightspeed){   //高速优先
            drivingMode = DRIVING_MULTIPLE_ROUTES_PRIORITY_HIGHSPEED;
        }else{ //默认不勾选
            drivingMode = DRIVING_MULTIPLE_ROUTES_DEFAULT;
        }
        Log.d("AmapNaviSettingBean","规划策略" + drivingMode);
        return drivingMode;
    }
}
