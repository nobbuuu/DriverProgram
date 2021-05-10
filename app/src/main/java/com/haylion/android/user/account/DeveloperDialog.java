package com.haylion.android.user.account;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import androidx.annotation.NonNull;

import com.haylion.android.R;
import com.haylion.android.data.base.BaseDialog;
import com.haylion.android.data.util.IntentUtils;
import com.haylion.android.data.util.StorageUtils;
import com.haylion.android.mvp.util.AppUtils;

import java.io.File;

import butterknife.BindString;
import butterknife.OnClick;

public class DeveloperDialog extends BaseDialog {

    @BindString(R.string.developer_dialog_send_log_file)
    String strSendLogFile;
    @BindString(R.string.developer_dialog_view_log)
    String strViewLogContent;

    // 微信包名
    private static final String WX_PKG_NAME = "com.tencent.mm";
    // WPS包名
    private static final String WPS_PKG_NAME = "cn.wps.moffice_eng";

    private Context mContext;

    @OnClick({R.id.send_log, R.id.view_log, R.id.restart_app,
            R.id.developer_options, R.id.app_settings, R.id.dismiss
    })
    void onItemClick(View view) {
        dismiss();
        switch (view.getId()) {
            case R.id.send_log:
                sendLogFile();
                break;
            case R.id.view_log:
                viewLogContent();
                break;
            case R.id.restart_app:
                AppUtils.restartSelf(mContext);
                break;
            case R.id.app_settings:
                AppUtils.openAppSettings(mContext);
                break;
            case R.id.developer_options:
                openDeveloperOptions();
                break;
            default:
                break;
        }
    }

    public DeveloperDialog(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.developer_dlg);
        setCanceledOnTouchOutside(false);
    }

    /**
     * 发送日志文件
     */
    private void sendLogFile() {
        Uri logUri = getUriForLogFile();
        if (logUri == null) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (AppUtils.isAppInstalled(mContext, WX_PKG_NAME)) { // 通过微信发送
            intent.setClassName(WX_PKG_NAME, "com.tencent.mm.ui.tools.ShareImgUI");
        }
        intent.putExtra(Intent.EXTRA_STREAM, logUri);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setType("text/plain");
        Intent chooser = Intent.createChooser(intent, strSendLogFile);
        mContext.startActivity(chooser);
    }

    /**
     * 查看日志内容
     */
    private void viewLogContent() {
        Uri logUri = getUriForLogFile();
        if (logUri == null) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (AppUtils.isAppInstalled(mContext, WPS_PKG_NAME)) { // 通过WPS打开
            intent.setClassName(WPS_PKG_NAME, "cn.wps.moffice.documentmanager.PreStartActivity2");
            Bundle bundle = new Bundle();
            bundle.putString("OpenMode", "ReadOnly"); // 只读模式
            intent.putExtras(bundle);
        }
        intent.setDataAndType(logUri, "text/plain");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        Intent chooser = Intent.createChooser(intent, strViewLogContent);
        mContext.startActivity(chooser);
    }

    /**
     * 获取当天日志文件标识符
     */
    private Uri getUriForLogFile() {
        String logName = String.format("%tF", System.currentTimeMillis());
        File logFile = new File(StorageUtils.getLogPath(), logName);
        if (logFile.exists()) {
            return IntentUtils.getUriForFile(mContext, logFile);
        } else {
//            toast("未找到日志文件");
//            Toast.makeText(MineActivity., "未找到日志文件", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    /**
     * 打开开发者选项
     */
    private void openDeveloperOptions() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
        mContext.startActivity(intent);
    }


}
