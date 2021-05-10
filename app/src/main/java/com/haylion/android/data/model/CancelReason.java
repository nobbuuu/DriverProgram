package com.haylion.android.data.model;

public class CancelReason {
    private int id;
    private String cancellingReason;   //原因描述
    private int tag;         //0-不需添加取消凭证  1-需要添加取消凭证
    private boolean isSelected = false;

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReason() {
        return cancellingReason;
    }

    public void setReason(String reason) {
        this.cancellingReason = reason;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
