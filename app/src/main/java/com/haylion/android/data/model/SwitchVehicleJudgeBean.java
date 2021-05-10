package com.haylion.android.data.model;

/**
 * @author dengzh
 * @date 2019/12/19
 * Description: 是否允许更换听单车辆
 */
public class SwitchVehicleJudgeBean {

    private int allowed;  //是否允许更换车辆（0-不允许，1-允许）
    private int reason;   //更换失败原因（0-无，1-车辆听单中，2-订单未完成）



    public int getAllowed() {
        return allowed;
    }

    public void setAllowed(int allowed) {
        this.allowed = allowed;
    }

    public int getReason() {
        return reason;
    }

    public void setReason(int reason) {
        this.reason = reason;
    }
}
