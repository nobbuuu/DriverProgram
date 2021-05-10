package com.haylion.android.data.model;

 /**
  * @class  AmapTrack
  * @description 高德轨迹信息
  * @date: 2019/12/17 10:20
  * @author: tandongdong
  */
public class AmapTrack {
    private long sid;
    private long tid;
    private long trid;

    public long getSid() {
        return sid;
    }

    public void setSid(long sid) {
        this.sid = sid;
    }

    public long getTid() {
        return tid;
    }

    public void setTid(long tid) {
        this.tid = tid;
    }

    public long getTrid() {
        return trid;
    }

    public void setTrid(long trid) {
        this.trid = trid;
    }

    @Override
    public String toString() {
        return "AmapTrack{" +
                "sid=" + sid +
                ", tid=" + tid +
                ", trid=" + trid +
                '}';
    }
}
