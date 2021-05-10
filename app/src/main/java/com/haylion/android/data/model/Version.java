package com.haylion.android.data.model;

import com.google.gson.annotations.SerializedName;

public class Version {

    private static final int type = 2;

    private int oldest;

    private int latest;

    @SerializedName("latestDownloadUrl")
    private String apkUrl;

    @SerializedName("updateMessage")
    private String updateMsg;

    @SerializedName("forceUpdateMessage")
    private String forceMsg;


    public int getType() {
        return type;
    }

    public int getOldest() {
        return oldest;
    }

    public void setOldest(int oldest) {
        this.oldest = oldest;
    }

    public int getLatest() {
        return latest;
    }

    public void setLatest(int latest) {
        this.latest = latest;
    }

    public String getApkUrl() {
        return apkUrl;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }

    public String getUpdateMsg() {
        return updateMsg;
    }

    public void setUpdateMsg(String updateMsg) {
        this.updateMsg = updateMsg;
    }

    public String getForceMsg() {
        return forceMsg;
    }

    public void setForceMsg(String forceMsg) {
        this.forceMsg = forceMsg;
    }

}
