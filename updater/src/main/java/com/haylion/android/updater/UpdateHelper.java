package com.haylion.android.updater;

import android.content.Context;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;

class UpdateHelper {

    public static final double SPACE_KB = 1024;
    public static final double SPACE_MB = 1024 * SPACE_KB;
    public static final double SPACE_GB = 1024 * SPACE_MB;
    public static final double SPACE_TB = 1024 * SPACE_GB;

    private UpdateHelper() {
    }

    /**
     * 字节数格式化
     *
     * @param sizeInBytes 字节数
     */
    public static String bytes2String(long sizeInBytes) {
        NumberFormat nf = new DecimalFormat();
        nf.setMaximumFractionDigits(2);
        try {
            if (sizeInBytes < SPACE_KB) {
                return nf.format(sizeInBytes) + " Byte(s)";
            } else if (sizeInBytes < SPACE_MB) {
                return nf.format(sizeInBytes / SPACE_KB) + " KB";
            } else if (sizeInBytes < SPACE_GB) {
                return nf.format(sizeInBytes / SPACE_MB) + " MB";
            } else if (sizeInBytes < SPACE_TB) {
                return nf.format(sizeInBytes / SPACE_GB) + " GB";
            } else {
                return nf.format(sizeInBytes / SPACE_TB) + " TB";
            }
        } catch (Exception e) {
            return sizeInBytes + " Byte(s)";
        }

    }

    /**
     * APK文件是否已存在
     *
     * @param dir      apk所在目录
     * @param fileName apk文件名
     */
    public static boolean isApkExists(File dir, String fileName) {
        return new File(dir, fileName).exists();
    }

    /**
     * todo:检查APK是否已经下载(通过md5判定)
     *
     * @param context 上下文
     * @param apkPath 本地apk路径
     * @return
     */
    public static boolean isDownloaded(Context context, File apkPath) {
        return apkPath != null && apkPath.exists();
    }


}
