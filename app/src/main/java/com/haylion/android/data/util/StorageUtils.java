package com.haylion.android.data.util;

import android.os.Environment;

import com.haylion.android.Constants;

import java.io.File;

public final class StorageUtils {

    private StorageUtils() {
        throw new UnsupportedOperationException("hey, buddy, easy...");
    }

    public static boolean isSDCardEnable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 获取SD卡路径
     *
     * @return SD卡路径
     */
    public static String getExternalStoragePath() {
        return Environment.getExternalStorageDirectory().getPath() + File.separator;
    }

    /**
     * 创建应用主目录
     */
    public static boolean makeHomeDirectory() {
        if (isSDCardEnable()) {
            File file = new File(getAppHomePath());
            if (file.exists()) {
                return true;
            }
            return file.mkdirs();
        }
        return false;
    }

    /**
     * 获取应用主目录
     */
    public static String getAppHomePath() {
        return StorageUtils.getExternalStoragePath() + Constants.APP_NAME + File.separator;
    }

    /**
     * 获取日志目录
     */
    public static String getLogPath() {
        return getAppHomePath() + Constants.LOG_DIR + File.separator;
    }

    /**
     * 获取下载目录
     */
    public static String getDownloadPath() {
        return getAppHomePath() + Constants.DOWNLOAD_DIR + File.separator;
    }

    /**
     * 获取最新版本的保存路径
     */
    public static String getLatestPath() {
        return getDownloadPath() + Constants.LATEST_NAME;
    }

}
