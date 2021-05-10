package com.haylion.android.updater;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.listener.DownloadListener1;
import com.liulishuo.okdownload.core.listener.assist.Listener1Assist;

import java.io.File;
import java.io.FileNotFoundException;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.FileProvider;

public class AppUpdater {

    private static final String TAG = "AppUpdater";

    private Context mContext;
    private UpdateListener mUpdateListener;
    private String mDownloadUrl;

    // 下载文件保存目录
    private File mSaveDir;
    private String mAuthority;
    private String mSaveName;
    private boolean wifiRequired;

    // 是否强制升级
    private boolean forceUpdate;
    // 更新提示对话框
    private Dialog mUpdateDialog;
    private CharSequence mUpdateTitle;
    private CharSequence mUpdateMessage;
    private CharSequence mPositiveText;
    private CharSequence mNegativeText;

    // 是否显示下载进度对话框
    private boolean showProgress;
    private CharSequence mProgressText;
    private ProgressDialog mProgressDialog;

    // 下载进度通知
    private NotificationManagerCompat mNotificationManager;
    private NotificationCompat.Builder mNotificationBuilder;
    private int notificationIcon;

    private DownloadTask mDownloadTask;

    public AppUpdater(Builder builder) {
        this.mContext = builder.context;
        this.mDownloadUrl = builder.downloadUrl;
        this.mUpdateListener = builder.listener;

        this.mSaveDir = builder.saveDir;
        this.mAuthority = builder.authority;
        this.mSaveName = builder.saveName;
        this.wifiRequired = builder.wifiRequired;

        this.forceUpdate = builder.forceUpdate;
        this.mUpdateTitle = builder.updateTitle;
        this.mUpdateMessage = builder.updateMessage;
        this.mPositiveText = builder.positiveText;
        this.mNegativeText = builder.negativeText;
        this.mUpdateDialog = builder.updateDialog;
        this.showProgress = builder.showProgress;
        this.notificationIcon = builder.notificationIcon;
        this.mProgressText = builder.progressText;

        this.mDownloadTask = createDownloadTask();
    }

    /**
     * 创建下载任务
     */
    private DownloadTask createDownloadTask() {
        DownloadTask.Builder builder = new DownloadTask.Builder(mDownloadUrl, mSaveDir);
        builder.setAutoCallbackToUIThread(true);
        if (TextUtils.isEmpty(mSaveName)) {
            builder.setFilenameFromResponse(true);
        } else {
            builder.setFilenameFromResponse(false);
            builder.setFilename(mSaveName);
        }
        builder.setWifiRequired(wifiRequired);
        builder.setMinIntervalMillisCallbackProcess(500);
        return builder.build();
    }

    /**
     * 显示更新提示对话框
     */
    public void showUpdateDialog() {
        if (mUpdateDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setPositiveButton(mPositiveText, (dialog, which) -> {
                dialog.dismiss();
                startDownload();
            }).setMessage(mUpdateMessage);
            if (!TextUtils.isEmpty(mUpdateTitle)) {
                builder.setTitle(mUpdateTitle);
            }
            if (!forceUpdate) {
                builder.setNegativeButton(mNegativeText, (dialog, which) -> {
                    dialog.dismiss();
                    if (mUpdateListener != null) {
                        mUpdateListener.onCanceled();
                    }
                });
            }
            mUpdateDialog = builder.setCancelable(!forceUpdate).create();
        }
        mUpdateDialog.show();
    }

    /**
     * 开始下载
     */
    public void startDownload() {
        mDownloadTask.enqueue(mDownloadListener);
        if (showProgress) {
            showProgressDialog();
        }
        if (notificationIcon != -1) {
            sendNotification();
        }
    }

    /**
     * 停止下载
     */
    public void stopDownload() {
        mDownloadTask.cancel();
        dismissProgressDialog();
        cancelNotification();
        this.mUpdateListener = null;
    }


    public static class Builder {

        private Context context;
        private String downloadUrl;
        private UpdateListener listener;

        private File saveDir;
        private String authority;
        private String saveName;

        private boolean wifiRequired = false;

        private boolean forceUpdate = false;
        private CharSequence updateTitle;
        private CharSequence updateMessage;
        private CharSequence positiveText;
        private CharSequence negativeText;
        private Dialog updateDialog;

        private boolean showProgress = true;
        private CharSequence progressText;
        private int notificationIcon = -1;

        public Builder(Context context) {
            this.context = context;
            this.progressText = context.getString(R.string.notification_content_text);
            this.positiveText = context.getString(android.R.string.ok);
            this.negativeText = context.getString(android.R.string.cancel);
        }

        public Builder downloadUrl(String downloadUrl) {
            this.downloadUrl = downloadUrl;
            return this;
        }

        public Builder listener(UpdateListener listener) {
            this.listener = listener;
            return this;
        }

        public Builder saveDir(File saveDir, String authority) {
            this.saveDir = saveDir;
            this.authority = authority;
            return this;
        }

        public Builder saveName(String saveName) {
            this.saveName = saveName;
            return this;
        }

        public Builder wifiRequired(boolean wifiRequired) {
            this.wifiRequired = wifiRequired;
            return this;
        }

        public Builder forceUpdate(boolean forceUpdate) {
            this.forceUpdate = forceUpdate;
            return this;
        }

        public Builder updateTitle(CharSequence updateTitle) {
            this.updateTitle = updateTitle;
            return this;
        }

        public Builder updateMessage(CharSequence updateMessage) {
            this.updateMessage = updateMessage;
            return this;
        }

        public Builder positiveText(CharSequence positiveText) {
            this.positiveText = positiveText;
            return this;
        }

        public Builder negativeText(CharSequence negativeText) {
            this.negativeText = negativeText;
            return this;
        }

        public Builder updateDialog(Dialog updateDialog) {
            this.updateDialog = updateDialog;
            return this;
        }

        public Builder showProgress(boolean showProgress) {
            this.showProgress = showProgress;
            return this;
        }

        public Builder progressText(CharSequence progressText) {
            this.progressText = progressText;
            return this;
        }

        public Builder enableNotification(@DrawableRes int notificationIcon) {
            this.notificationIcon = notificationIcon;
            return this;
        }

        public AppUpdater build() {
            return new AppUpdater(this);
        }

    }

    /**
     * 下载任务状态监听器
     */
    private DownloadListener1 mDownloadListener = new SimpleDownloadListener() {

        @Override
        public void progress(@NonNull DownloadTask task, long currentOffset, long totalLength) {
            int percent = (int) ((currentOffset * 100) / totalLength);
            if (mUpdateListener != null) {
                mUpdateListener.onProgress(currentOffset, totalLength, percent);
            }
            if (isProgressShowing()) {
                mProgressDialog.setProgress(percent);
                String format = UpdateHelper.bytes2String(currentOffset) + "/" + UpdateHelper.bytes2String(totalLength);
                mProgressDialog.setProgressNumberFormat(format);
            }
            if (notificationIcon != -1) {
                refreshNotification(percent);
            }
        }

        @Override
        public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause, @NonNull Listener1Assist.Listener1Model model) {
            if (cause == EndCause.COMPLETED) {
                if (mUpdateListener != null) {
                    mUpdateListener.onCompleted();
                }
                installUpdate(task.getFile());

            } else if (cause == EndCause.CANCELED) {
                Log.d(TAG, "CANCELED");
            } else {
                if (mUpdateListener != null) {
                    mUpdateListener.onError(cause.name());
                } else {
                    Log.e(TAG, cause.name());
                }
            }
            cancelNotification();
            dismissProgressDialog();
        }
    };

    /**
     * 安装更新包
     *
     * @param apkFile apk文件
     */
    private void installUpdate(File apkFile) {
        if (apkFile == null || !apkFile.exists()) {
            if (mUpdateListener != null) {
                mUpdateListener.onError("Apk file not found！");
            }
            return;
        }
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setAction(Intent.ACTION_INSTALL_PACKAGE);
            Uri apkUri = FileProvider.getUriForFile(mContext, mAuthority, apkFile);
            intent.setData(apkUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        mContext.startActivity(intent);
    }

    /**
     * 显示下载进度对话框
     */
    private void showProgressDialog() {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(!forceUpdate);
        mProgressDialog.setMessage(mProgressText);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.show();
    }

    /**
     * 关闭下载进度对话框
     */
    private void dismissProgressDialog() {
        if (isProgressShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    /**
     * 进度对话框是否在显示
     */
    private boolean isProgressShowing() {
        return showProgress && mProgressDialog != null && mProgressDialog.isShowing();
    }

    /**
     * 发送安装包下载进度通知
     */
    private void sendNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            String name = mContext.getString(R.string.notification_channel_name);
            String description = mContext.getString(R.string.notification_channel_desc);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = mContext.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        mNotificationManager = NotificationManagerCompat.from(mContext);
        mNotificationBuilder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                .setContentTitle(mContext.getString(R.string.app_name))
                .setContentText(mProgressText)
                .setSmallIcon(notificationIcon);
        refreshNotification(0);
    }

    /**
     * 刷新通知下载进度
     *
     * @param progress 进度
     */
    private void refreshNotification(int progress) {
        if (mNotificationBuilder != null && mNotificationManager != null) {
            mNotificationBuilder.setProgress(100, progress, false);
            mNotificationManager.notify(NOTIFICATION_ID, mNotificationBuilder.build());
        }
    }

    /**
     * 取消通知
     */
    private void cancelNotification() {
        if (mNotificationManager == null) {
            return;
        }
        mNotificationManager.cancel(NOTIFICATION_ID);
    }

    private static final int NOTIFICATION_ID = 54553;
    private static final String CHANNEL_ID = "app_update_01";

}
