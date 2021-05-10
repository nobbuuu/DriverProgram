package com.haylion.android.data.model;

 /**
  * @class  FloatwindowCmd
  * @description 悬浮窗的控制类
  * @date: 2019/12/17 10:24
  * @author: tandongdong
  */
public class FloatwindowCmd {

    /**
     * 弹窗类型
     * */
    public static final int WINDOW_TYPE_NEW_ORDER_DIALOG = 0;  //抢单弹窗
    public static final int WINDOW_TYPE_MEITUAN_GRABING_DIALOG = 1;  //美团抢单中弹窗

    /**
     * 弹窗操作
     * */
    public static final int OPERATION_SHOW = 0;  //显示
    public static final int OPERATION_CANCEL = 1;  //取消
    public static final int OPERATION_HIDE = 2;  //隐藏


    private int windowType;
    private int operation;
    private int time;

    public FloatwindowCmd() {
    }

    public FloatwindowCmd(int windowType, int operation) {
        this.windowType = windowType;
        this.operation = operation;
    }

    public int getWindowType() {
        return windowType;
    }

    public void setWindowType(int windowType) {
        this.windowType = windowType;
    }

    public int getOperation() {
        return operation;
    }

    public void setOperation(int operation) {
        this.operation = operation;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "FloatwindowCmd{" +
                "windowType=" + windowType +
                ", operation=" + operation +
                ", time=" + time +
                '}';
    }
}
