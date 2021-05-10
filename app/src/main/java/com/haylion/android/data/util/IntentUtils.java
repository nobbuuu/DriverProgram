package com.haylion.android.data.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.webkit.URLUtil;

import androidx.core.content.FileProvider;

import com.haylion.android.BuildConfig;
import com.haylion.android.mvp.util.LogUtils;

import java.io.File;

public final class IntentUtils {

    private IntentUtils() {
    }

    public static void openBrowser(Context context, String url) {
        openBrowser(context, url, false, null);
    }

    public static void openBrowser(Context context, String url, boolean withChooser, CharSequence title) {
        if (!URLUtil.isValidUrl(url)) {
            LogUtils.e("地址格式有误");
            return;
        }
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            if (withChooser) {
                Intent chooser = Intent.createChooser(intent, title);
                context.startActivity(chooser);
            } else {
                context.startActivity(intent);
            }
        } else {
            LogUtils.e("没有安装浏览器");
        }
    }

    public static Uri getUriForFile(Context context, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(
                    context, BuildConfig.APPLICATION_ID + ".provider", file);
        } else {
            return Uri.fromFile(file);
        }
    }


}
