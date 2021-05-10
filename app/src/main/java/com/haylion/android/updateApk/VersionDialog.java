package com.haylion.android.updateApk;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.haylion.android.BuildConfig;
import com.haylion.android.MyApplication;
import com.haylion.android.R;
import com.haylion.android.data.base.BaseDialog;
import com.haylion.android.data.model.LatestVersionBean;
import com.haylion.android.data.repo.PrefserHelper;
import com.haylion.android.data.util.IntentUtils;
import com.haylion.android.data.util.StorageUtils;
import com.haylion.android.mvp.util.ToastUtils;
import com.haylion.android.mvp.util.UpdateManager;

import org.greenrobot.eventbus.EventBus;

import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * 更新apk弹窗
 * 1.欢迎页面
 * 2.设置页面，没有显示限制
 * 此Dialog不做任何显示限制
 */
public class VersionDialog extends BaseDialog {

    public static final int REJECT_LIMIT_NUM = 2;  //最大拒绝次数

    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.tv_size)
    TextView tvSize;
    @BindView(R.id.tv_message)
    TextView tvMessage;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;

    private LatestVersionBean version;
    private UpdateManager mUpdateManager;
    public static boolean isDownloading = false;
    private int fromType;  //0-欢迎页  1-设置页 2-首页

    public VersionDialog(@NonNull Context context, int fromType) {
        super(context);
        setContentView(R.layout.dialog_version);
        this.fromType = fromType;
    }

    public void setData(LatestVersionBean bean) {
        version = bean;
        mUpdateManager = buildUpdateManager();
        tvMessage.setText(bean.getUpdateMessage());
        tvVersion.setText(bean.getVersionName());
        //getFileSize();
        if (version.isForceUpdate()) {
            tvCancel.setVisibility(View.GONE);
            setCanceledOnTouchOutside(false);
            setOnKeyListener((dialog, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                }
                return false;
            });
        }
        isDownloading = false;
    }

    @OnClick({R.id.tv_cancel, R.id.tv_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                if (fromType == 0) { //欢迎页，才统计拒绝次数
                    PrefserHelper.setCache(PrefserHelper.APK_UPDATE_LAST_REJECT_DATE, String.valueOf(System.currentTimeMillis()));
                    int num = PrefserHelper.getIntCache(PrefserHelper.APK_UPDATE_REJECT_NUMBER_IN_SAME_VERSION) + 1;
                    PrefserHelper.setIntCache(PrefserHelper.APK_UPDATE_REJECT_NUMBER_IN_SAME_VERSION, num);
                }
                dismiss();
                if (listener != null) {
                    listener.onCancel();
                }
                break;
            case R.id.tv_confirm:
                if (UpdateManager.isAvailable(mContext)) {
                    if (mUpdateManager.checkIfDownloaded()) {
                        installPackage();
                    } else {
                        if (isDownloading) {
                            ToastUtils.showShort(mContext, R.string.downloading_installation_package);
                        }

                        if (version.isForceUpdate()) {
                            dismiss();
                            showProgressDialog();
                            mUpdateManager.startDownload(mDownloadCallback);
                        } else {
                            mUpdateManager.startDownload(mStaticDownloadCallback);
                        }
                    }
                } else {
                    IntentUtils.openBrowser(mContext, version.getDownloadUrl());
                }
                if (!version.isForceUpdate()) {
                    dismiss();
                }
                if (listener != null) {
                    listener.onConfirm();
                }
                break;
        }
    }

    /**
     * 设置更新管理器的参数
     */
    private UpdateManager buildUpdateManager() {
        UpdateManager updateManager = UpdateManager.getInstance(mContext);
        //updateManager.hideNotification();
        updateManager.notificationTitle(R.string.app_name);
        updateManager.notificationDescription(version.getVersionName() + "下载中...");
        updateManager.versionCode(version.getVersionCode());
        updateManager.downloadUrl(version.getDownloadUrl());
        updateManager.savePath(StorageUtils.getLatestPath());
        updateManager.setDebugEnabled(true);
        return updateManager;
    }

    private void installPackage() {
        Log.e("VersionDialog", "installPackage");
        mUpdateManager.installPackage(BuildConfig.APPLICATION_ID + ".provider");
    }

    /**
     * 非强制更新监听
     * 用static修饰，防止内存泄漏
     * 因为dialog销毁时，下载安装仍然要执行。
     */
    private static UpdateManager.Callback mStaticDownloadCallback = new UpdateManager.Callback() {
        @Override
        public void onCompleted() {
            isDownloading = false;
            UpdateManager.getInstance(MyApplication.getContext()).installPackage(BuildConfig.APPLICATION_ID + ".provider");
        }

        @Override
        public void onProgress(long downloadedBytes, long totalBytes) {
            Log.e("VersionDialog", "非强制更新监听");
            isDownloading = true;
        }

        @Override
        public void onError(int code, String msg) {
            isDownloading = false;
            ToastUtils.showShort(MyApplication.getContext(), MyApplication.getContext().getString(R.string.format_download_update_failed, code));
        }
    };


    /**
     * 强制更新监听
     */
    private UpdateDialog updateDialog;
    private UpdateManager.Callback mDownloadCallback = new UpdateManager.Callback() {

        @Override
        public void onCompleted() {
            isDownloading = false;
            dismissProgressDialog();
            installPackage();
        }

        @Override
        public void onProgress(long downloadedBytes, long totalBytes) {
            Log.e("VersionDialog", "强制更新监听");
            isDownloading = true;
            if (updateDialog != null && updateDialog.isShowing()) {
                int progress = (int) ((downloadedBytes * 100) / totalBytes);
                updateDialog.setProgress(progress);
            }
        }

        @Override
        public void onError(int code, String msg) {
            isDownloading = false;
            dismissProgressDialog();
            ToastUtils.showShort(MyApplication.getContext(), MyApplication.getContext().getString(R.string.format_download_update_failed, code));
        }
    };

    private void showProgressDialog() {
        if (updateDialog == null) {
            updateDialog = new UpdateDialog(mContext);
        }

        updateDialog.setCanceledOnTouchOutside(false);
        updateDialog.setOnKeyListener((dialog, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                return true;
            }
            return false;
        });

        // updateDialog.setCallback(() -> mUpdateManager.stopDownload());
        updateDialog.show();
    }

    private void dismissProgressDialog() {
        if (updateDialog != null && updateDialog.isShowing()) {
            updateDialog.dismiss();
        }
    }


    public void getFileSize() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection conn = (HttpURLConnection) new URL(version.getDownloadUrl()).openConnection();
                    conn.setConnectTimeout(10 * 1000);
                    String size = conn.getHeaderField("Content-Length");
                    float sizeLen = size == null ? 0 : Float.parseFloat(size) / (1024 * 1024);
                    if (tvSize != null) {
                        tvSize.post(new Runnable() {
                            @Override
                            public void run() {
                                tvSize.setText("新版本大小 " + String.format("%.2f", sizeLen) + "MB");
                            }
                        });
                    }
                } catch (Exception e) {
                    if (tvSize != null) {
                        tvSize.post(new Runnable() {
                            @Override
                            public void run() {
                                tvSize.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            }
        }).start();
    }

    public OnDialogClickListener listener;

    public void setOnDialogClickListener(OnDialogClickListener listener) {
        this.listener = listener;
    }

    public interface OnDialogClickListener {
        void onConfirm();

        void onCancel();
    }
}
