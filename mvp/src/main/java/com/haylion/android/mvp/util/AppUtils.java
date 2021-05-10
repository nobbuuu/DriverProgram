package com.haylion.android.mvp.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public final class AppUtils {

    private AppUtils() {

    }

    public static String getPackageName(Context context) {
        return context.getPackageName();
    }

    /**
     * 杀掉APP进程
     */
    public static void killSelf() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }

    /**
     * 重启APP
     *
     * @param context 上下文
     */
    public static void restartSelf(Context context) {
        Intent launchIntent = getLaunchIntent(context, getPackageName(context));
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(launchIntent);
            killSelf();
        }
    }

    /**
     * 获取应用启动Intent
     *
     * @param context     上下文
     * @param packageName 包名
     */
    private static Intent getLaunchIntent(Context context, String packageName) {
        return context.getPackageManager().getLaunchIntentForPackage(packageName);
    }

    public static String getCurrentProcessName() {
        File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            return bufferedReader.readLine().trim();
        } catch (Exception e) {
            LogUtils.e(e);
        }
        return null;
    }

    /**
     * 是否是主进程
     *
     * @param context 上下文
     */
    public static boolean isInMainProcess(Context context) {
        return TextUtils.equals(getCurrentProcessName(), getPackageName(context));
    }

    /**
     * 检查服务是否正在运行
     */
    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (TextUtils.equals(serviceClass.getName(), service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 打开本应用的设置页面
     *
     * @param context 上下文
     */
    public static void openAppSettings(Context context) {
        openAppSettings(context, null);
    }

    /**
     * 打开应用设置
     *
     * @param context     上下文
     * @param packageName 包名
     */
    public static void openAppSettings(Context context, String packageName) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        if (TextUtils.isEmpty(packageName)) {
            intent.setData(Uri.parse("package:" + context.getPackageName()));
        } else {
            intent.setData(Uri.parse("package:" + packageName));
        }
        context.startActivity(intent);
    }

    /**
     * 检查某个app是否已经安装
     *
     * @param context     上下文
     * @param packageName 包名
     */
    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            return context.getPackageManager().getApplicationInfo(packageName, 0).enabled;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 获取当前应用信息
     *
     * @param context 上下文
     */
    public static PackageInfo getPackageInfo(Context context) {
        return getPackageInfo(context, getPackageName(context));
    }

    /**
     * 获取应用信息
     *
     * @param context     上下文
     * @param packageName 包名
     */
    public static PackageInfo getPackageInfo(Context context, String packageName) {
        try {
            return context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取apk文件的信息
     *
     * @param context         上下文
     * @param archiveFilePath apk文件完整路径
     */
    public static PackageInfo getPackageArchiveInfo(Context context, String archiveFilePath) {
        return context.getPackageManager().getPackageArchiveInfo(archiveFilePath, 0);
    }


}
