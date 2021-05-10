package com.haylion.android.mvp.util;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.core.content.FileProvider;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class UpdateManager {

    private static UpdateManager sManager;
    private final Context mAppContext;

    private static final String TAG = "UpdateManager";

    private static final Object mLock = new Object();

    private DownloadManager mDownloadManager;
    private long mDownloadId;
    private DownloadObserver mDownloadObserver;

    private static final String DOWNLOAD_COMPONENT = "com.android.providers.downloads";
    private static final String MIME_TYPE = "application/vnd.android.package-archive";

    private Handler mMainThread;

    // 默认的错误码
    private static final int ERROR_DEFAULT = -1;

    // 状态未知
    private static final int STATUS_UNKNOWN = -1;
    // 等待下载
    private static final int STATUS_PENDING = DownloadManager.STATUS_PENDING;
    // 正在下载
    private static final int STATUS_DOWNLOADING = DownloadManager.STATUS_RUNNING;
    // 暂停下载
    private static final int STATUS_PAUSED = DownloadManager.STATUS_PAUSED;
    // 下载完成
    private static final int STATUS_COMPLETED = DownloadManager.STATUS_SUCCESSFUL;
    // 下载失败
    private static final int STATUS_FAILED = DownloadManager.STATUS_FAILED;

    private SharedPreferences mSharedPreferences;
    private static final String NAME_UPDATE_MANAGER = "update_manager";
    private static final String KEY_DOWNLOAD_ID = "download_id";

    // 新版本下载地址
    private String mDownloadUrl;
    // 新版本版本号
    private long mVersionCode = -1;
    // 本地保存路径
    private String mSavePath;

    private boolean mShowNotification = true;
    private CharSequence mNotificationTitle;
    private CharSequence mNotificationDesc;

    // 仅在Wi-Fi网络下载
    private boolean mWifiRequired = false;
    // 仅在设备充电时下载
    private boolean mChargingRequired = false;

    private Callback mCallback;

    private boolean mIsDebugEnabled = true;

    /**
     * 获取 UpdateManager 实例
     *
     * @param context 上下文
     */
    public static UpdateManager getInstance(@NonNull Context context) {
        synchronized (mLock) {
            if (sManager == null) {
                return sManager = new UpdateManager(context.getApplicationContext());
            }
            return sManager;
        }
    }

    private UpdateManager(@NonNull Context context) {
        mAppContext = context;
        mDownloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        mSharedPreferences = context.getSharedPreferences(NAME_UPDATE_MANAGER, Context.MODE_PRIVATE);
        mDownloadId = mSharedPreferences.getLong(KEY_DOWNLOAD_ID, -1);

        mDownloadObserver = new DownloadObserver();

        mMainThread = new Handler(Looper.getMainLooper());
    }

    /**
     * 设置下载地址
     *
     * @param url 下载地址
     */
    public UpdateManager downloadUrl(@NonNull String url) {
        this.mDownloadUrl = url;
        return this;
    }

    /**
     * 设置新版本版本号
     *
     * @param versionCode 版本号
     */
    public UpdateManager versionCode(long versionCode) {
        this.mVersionCode = versionCode;
        return this;
    }

    /**
     * 设置本地保存路径
     *
     * @param savePath 保存路径
     */
    public UpdateManager savePath(@NonNull String savePath) {
        this.mSavePath = savePath;
        return this;
    }

    /**
     * 设置通知标题
     *
     * @param title 标题
     */
    public UpdateManager notificationTitle(CharSequence title) {
        this.mNotificationTitle = title;
        return this;
    }

    /**
     * 设置通知标题
     *
     * @param titleRes 标题资源
     */
    public UpdateManager notificationTitle(@StringRes int titleRes) {
        this.mNotificationTitle = mAppContext.getString(titleRes);
        return this;
    }

    /**
     * 设置通知描述信息
     *
     * @param desc 描述信息
     */
    public UpdateManager notificationDescription(CharSequence desc) {
        this.mNotificationDesc = desc;
        return this;
    }

    /**
     * 设置通知描述信息
     *
     * @param descRes 描述信息资源
     */
    public UpdateManager notificationDescription(@StringRes int descRes) {
        this.mNotificationDesc = mAppContext.getString(descRes);
        return this;
    }

    /**
     * 设置不显示通知
     */
    public UpdateManager hideNotification() {
        this.mShowNotification = false;
        return this;
    }

    /**
     * 设置是否只在Wi-Fi网络下下载
     */
    public UpdateManager wifiRequired(boolean wifiRequired) {
        this.mWifiRequired = wifiRequired;
        return this;
    }

    /**
     * 设置仅在设备充电时下载
     */
    @RequiresApi(Build.VERSION_CODES.N)
    public UpdateManager chargingRequired(boolean chargingRequired) {
        this.mChargingRequired = chargingRequired;
        return this;
    }

    /**
     * 设置是否打印调试日志
     */
    public UpdateManager setDebugEnabled(boolean enabled) {
        this.mIsDebugEnabled = enabled;
        return this;
    }

    /**
     * 开始下载安装包
     */
    public void startDownload(@Nullable Callback callback) {
        mCallback = callback;
        if (checkIfDownloaded()) {
            mCallback.onCompleted();
            return;
        }
        if (!Patterns.WEB_URL.matcher(mDownloadUrl).matches()) {
            throw new IllegalArgumentException("Invalid url");
        }
        if (isDownloading()) {
            if (TextUtils.equals(mDownloadUrl, queryDownloadUrl())) {
                mDownloadObserver.start();
                return; // 正在下载该文件
            }
            stopDownload();
        }
        long downloadId = mDownloadManager.enqueue(createRequest());
        setDownloadId(downloadId);

        mDownloadObserver.start();
    }

    /**
     * 检查是否已经下载完成
     */
    public boolean checkIfDownloaded() {
        if (TextUtils.isEmpty(mSavePath)) {
            throw new IllegalArgumentException("Invalid path");
        }
        File file = new File(mSavePath);
        if (!file.exists()) {
            return false;  // 文件不存在
        }
        PackageManager pm = mAppContext.getPackageManager();
        PackageInfo packageInfo = pm.getPackageArchiveInfo(mSavePath, 0);
        if (packageInfo == null) {
            return false;
        }
        if (mVersionCode < 0) {
            throw new IllegalArgumentException("Invalid version code");
        }
        if (TextUtils.equals(packageInfo.packageName, mAppContext.getPackageName())) {
            long versionCode;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                versionCode = packageInfo.getLongVersionCode();
            } else {
                versionCode = packageInfo.versionCode;
            }
            return versionCode == mVersionCode;
        }
        return false;
    }

    /**
     * 创建下载请求
     */
    private DownloadManager.Request createRequest() {
        Uri remoteUri = Uri.parse(mDownloadUrl);
        DownloadManager.Request request = new DownloadManager.Request(remoteUri);
        request.setVisibleInDownloadsUi(false);
        Uri localUri = createLocalUri();
        request.setDestinationUri(localUri);

        if (mShowNotification) {
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
            request.setTitle(mNotificationTitle);
            request.setDescription(mNotificationDesc);
        } else {
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        }
        if (mWifiRequired) {
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            request.setRequiresCharging(mChargingRequired);
        }
        request.setMimeType(MIME_TYPE);
        return request;
    }

    /**
     * 创建下载目录
     */
    private Uri createLocalUri() {
        File localFile = new File(mSavePath);
        File localDir = localFile.getParentFile();
        if (localDir.exists()) {
            if (localFile.exists()) {
                if (localFile.delete()) {
                    logDebug("已删除旧文件");
                }
            }
        } else if (localDir.mkdirs()) {
            logDebug("下载目录创建成功");
        } else {
            logError("下载目录创建失败");
        }
        return Uri.fromFile(localFile);
    }

    /**
     * 检查是否正在下载
     */
    private boolean isDownloading() {
        return queryDownloadStatus() == STATUS_DOWNLOADING;
    }

    /**
     * 停止下载
     */
    public void stopDownload() {
        mDownloadManager.remove(mDownloadId);
        if (mDownloadObserver != null) {
            mDownloadObserver.stop();
        }
        logDebug("停止下载");
    }

    /**
     * 下载回调
     */
    public static class Callback {

        public void onCompleted() {

        }

        public void onProgress(long downloadedBytes, long totalBytes) {

        }

        public void onError(int code, String msg) {

        }

    }

    /**
     * 下载进度监听器
     */
    private class DownloadObserver {

        private ScheduledExecutorService timer;
        private Future<?> timeTask;

        private boolean isStared;

        private DownloadObserver() {
            this.isStared = false;
            this.timer = Executors.newSingleThreadScheduledExecutor();
        }

        private void start() {
            if (isStared) {
                return;
            }
            timeTask = timer.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    queryProgress();
                }
            }, 0, 200, TimeUnit.MILLISECONDS);
            isStared = true;
        }

        /**
         * 查询安装包下载情况
         */
        private void queryProgress() {
            int downloadStatus = STATUS_UNKNOWN;
            long downloadedBytes = 0, totalBytes = 0;
            try (Cursor cursor = createDownloadQuery()) {
                if (cursor != null && cursor.moveToFirst()) {
                    downloadStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    downloadedBytes = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    totalBytes = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                }
            }
            if (downloadStatus == STATUS_COMPLETED) {
                stop();
                if (mCallback != null) {
                    logDebug("下载完成!");
                    mMainThread.post(() -> mCallback.onCompleted());
                }

            } else if (downloadStatus == STATUS_DOWNLOADING) {
                if (downloadedBytes > 0 && totalBytes > 0) {
                    logDebug("下载进度 " + downloadedBytes + "/" + totalBytes);
                    if (mCallback != null) {
                        long finalDownloadedBytes = downloadedBytes;
                        long finalTotalBytes = totalBytes;
                        mMainThread.post(() -> mCallback.onProgress(finalDownloadedBytes, finalTotalBytes));
                    }
                }

            } else if (downloadStatus == STATUS_FAILED) {
                stop();
                int reason = queryFailedReason();
                logError("下载失败：" + reason);
                if (mCallback != null) {
                    mMainThread.post(() -> mCallback.onError(reason, "下载失败"));
                }

            } else if (downloadStatus == STATUS_PENDING) {
                logDebug("准备下载……");

            } else if (downloadStatus == STATUS_PAUSED) {
                logDebug("下载暂停");

            } else {
                logDebug("下载状态：" + downloadStatus);
            }
        }

        private void stop() {
            if (isStared) {
                timeTask.cancel(true);
            }
            isStared = false;
        }

        private void destroy() {
            timer.shutdown();
        }

    }

    /**
     * 下载完成监听器
     */
    private class DownloadReceiver extends BroadcastReceiver {

        private Context context;
        private boolean isRegistered;

        private DownloadReceiver(Context context) {
            this.context = context.getApplicationContext();
            this.isRegistered = false;
        }

        private void register() {
            if (isRegistered) {
                return;
            }
            IntentFilter filter = new IntentFilter();
            filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            filter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
            context.registerReceiver(this, filter);
            isRegistered = true;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.equals(action, DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            } else if (TextUtils.equals(action, DownloadManager.ACTION_NOTIFICATION_CLICKED)) {

            }
        }

        private void unregister() {
            if (isRegistered) {
                context.unregisterReceiver(this);
            }
            isRegistered = false;
        }

    }

    /**
     * 安装新版本应用
     */
    public void installPackage(String authority) {
        if (!checkIfDownloaded()) {
            String errorMessage = "安装包不存在或不正确";
            logError(errorMessage);
            if (mCallback != null) {
                mCallback.onError(ERROR_DEFAULT, errorMessage);
            }
            return;
        }
        Intent intent = new Intent();
        File apkFile = new File(mSavePath);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri apkUri = Uri.fromFile(apkFile);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setAction(Intent.ACTION_INSTALL_PACKAGE);
            apkUri = FileProvider.getUriForFile(mAppContext, authority, apkFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        } else {
            intent.setAction(Intent.ACTION_VIEW);
        }
        intent.setDataAndType(apkUri, MIME_TYPE);
        mAppContext.startActivity(intent);
    }

    /**
     * 查询安装包的uri
     */
    private Uri queryLocalUri() {
        try (Cursor cursor = createDownloadQuery()) {
            if (cursor != null && cursor.moveToFirst()) {
                String uri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                return Uri.parse(uri);
            }
        }
        return null;
    }

    /**
     * 查询下载地址uri
     */
    private String queryDownloadUrl() {
        try (Cursor cursor = createDownloadQuery()) {
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_URI));
            }
        }
        return null;
    }

    /**
     * 查询任务下载状态
     */
    private int queryDownloadStatus() {
        try (Cursor cursor = createDownloadQuery()) {
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            }
        }
        return STATUS_UNKNOWN;
    }

    /**
     * 查询下载失败的错误码
     */
    private int queryFailedReason() {
        try (Cursor cursor = createDownloadQuery()) {
            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON));
            }
        }
        return ERROR_DEFAULT;
    }

    /**
     * 获取下载查询游标
     */
    private Cursor createDownloadQuery() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(mDownloadId);
        return mDownloadManager.query(query);
    }

    /**
     * 保存下载id
     *
     * @param id 下载id
     */
    private void setDownloadId(long id) {
        mDownloadId = id;
        mSharedPreferences.edit().putLong(KEY_DOWNLOAD_ID, id).apply();
    }

    /**
     * 检查下载组件是否可用
     *
     * @param context 上下文
     */
    public static boolean isAvailable(@NonNull Context context) {
        return AppUtils.isAppInstalled(context, DOWNLOAD_COMPONENT);
    }

    /**
     * 打印错误日志
     *
     * @param message 日志
     */
    private void logError(String message) {
        if (mIsDebugEnabled) {
            Log.e(TAG, message);
        }
    }

    /**
     * 打印调试日志
     *
     * @param message 日志
     */
    private void logDebug(String message) {
        if (mIsDebugEnabled) {
            Log.d(TAG, message);
        }
    }

    /**
     * 移除回调
     */
    public void removeCallback() {
        this.mCallback = null;
    }

    /**
     * 执行销毁操作
     */
    public void destroyInstance() {
        if (mDownloadObserver != null) {
            mDownloadObserver.destroy();
        }
        mMainThread.removeCallbacksAndMessages(null);
        sManager = null;
    }

}
