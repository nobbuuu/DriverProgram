package com.haylion.android.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.haylion.android.R;

import java.io.File;


/**
 * 图片加载工具类
 */
public class GlideUtils {
    /**
     * 基础图片加载
     *
     * @param context
     * @param url
     * @param view
     */
    public static void loadImageBase(Context context, String url, ImageView view) {
        Glide.with(context).load(url).into(view);
    }

    public static void loadImage(Context context, String url, ImageView view) {
        Glide.with(context).load(url).placeholder(R.drawable.wait_image).error(R.drawable.error_image).into(view);
    }

    public static void loadFileImage(Context context, File url, ImageView view) {
        Glide.with(context).load(url).placeholder(R.drawable.wait_image).error(R.drawable.error_image).into(view);
    }

    /**
     * 圆形
     *
     * @param context
     * @param url
     * @param view
     */
    public static void loadImageCircleCrop(Context context, String url, ImageView view) {
        RequestOptions options = new RequestOptions().circleCrop();
        Glide.with(context).load(url)
                .placeholder(R.drawable.wait_image)
                .error(R.drawable.head_image)
                .apply(options)
                .into(view);
    }

    /**
     * 圆角矩形
     *
     * @param context
     * @param url
     * @param view
     * @param r
     */
    public static void loadImageRoundCrop(Context context, String url, ImageView view, float r) {
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .transform(new RotateTransformation(context, r));
        Glide.with(context).load(url)
                .placeholder(R.drawable.wait_image)
                .error(R.drawable.error_image)
                .apply(options)
                .into(view);
    }

    public static void getBitmap(Context context, String url, CustomTarget<Bitmap> customTarget) {
        Glide.with(context).asBitmap().load(url).into(customTarget);
    }
}
