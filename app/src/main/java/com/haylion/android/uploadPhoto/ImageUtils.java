package com.haylion.android.uploadPhoto;

import android.content.Context;
import android.os.Environment;

import com.haylion.android.MyApplication;

import java.io.File;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * Created by dengzh on 2019/9/28
 */
public class ImageUtils {

    private final String TAG = getClass().getSimpleName();

    /**
     * 时间戳作为文件名
     * @return
     */
    public static String getFileName(){
        return "Maas_" + System.currentTimeMillis();
    }


    /**
     * 获取缓存目录
     * @return
     */
    public static File getCacheDir() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return MyApplication.getContext().getExternalCacheDir();
        }
        return MyApplication.getContext().getCacheDir();
    }


    /**
     * Luban 压缩
     *
     * @param context
     * @param filePath
     * @return
     */
    public static void compress(Context context, String filePath, OnCompressListener listener) {
        String targetPath = getCacheDir() + File.separator + "luban_disk_cache";
        File dirFile = new File(targetPath);
        boolean flag = true;
        if (!dirFile.exists()) {
            flag = dirFile.mkdirs();
        }
        if(flag){
            Luban.with(context)
                    .load(filePath)
                    .ignoreBy(200)
                    .setTargetDir(targetPath)
                    .setCompressListener(listener)
                    .launch();
        }
    }

    /**
     * 删除拍照，裁剪，luban压缩的文件
     */
    public static void deleteCacheDirImgFile() {
        File compressDir = new File(getCacheDir() + File.separator + "picture_cache");
        File lubanDir = new File(getCacheDir() + File.separator + "luban_disk_cache");

        if (compressDir.exists()) {
            File[] files = compressDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        boolean flag = file.delete();
                    }
                }
            }
        }

        if (lubanDir.exists()) {
            File[] files = lubanDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        boolean flag = file.delete();
                    }
                }
            }
        }
    }
}
