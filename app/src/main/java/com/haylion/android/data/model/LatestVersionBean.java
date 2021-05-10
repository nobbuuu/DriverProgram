package com.haylion.android.data.model;

import com.haylion.android.BuildConfig;

/**
 * @author dengzh
 * @date 2019/12/19
 * Description: App最新版本信息
 * 1）当前版本 < 最低版本，强制更新
 * 2) 最低版本 <= 当前版本 < 目标版本，普通更新
 */
public class LatestVersionBean {

    private String uploadUrl;  //app下载路径
    private int minVersionNumber;  //最低版本号
    private String updatedInstructions;  //更新说明
    private String forcedUpdatedInstructions; //强制更新说明
    private String versionName; //版本名称
    private int versionNumber; //版本号


    public String getVersionName() {
        return versionName;
    }

    public String getDownloadUrl() {
        return uploadUrl;
    }

    public String getUpdateMessage() {
        if (isForceUpdate()) {
            return forcedUpdatedInstructions;
        }
        return updatedInstructions;
    }

    public int getVersionCode() {
        return versionNumber;
    }

    public boolean hasNewVersion() {
        return versionNumber > BuildConfig.VERSION_CODE;
    }

    public boolean isForceUpdate() {
        return minVersionNumber > BuildConfig.VERSION_CODE;
    }

}
