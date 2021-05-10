package com.haylion.android.uploadPhoto;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.gyf.immersionbar.ImmersionBar;
import com.haylion.android.R;
import com.haylion.android.data.base.BaseActivity;
import com.haylion.android.data.base.ConfirmDialog;
import com.haylion.android.data.util.PermissionManager;
import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.CropCallback;
import com.isseiaoki.simplecropview.callback.SaveCallback;
import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import top.zibin.luban.OnCompressListener;

/**
 * @author dengzh
 * 图片上传页面
 * 拍照上传图片后，返回url给上个页面
 */
public class UploadImgActivity extends BaseActivity<UploadImgContract.Presenter> implements UploadImgContract.View {

    public static final int PHOTO_REQUEST_TAKEPHOTO = 1; // 拍照请求码
    public static final String PIC_URL = "pic_url";      //图片url返回标识

    @BindView(R.id.iv_show)
    CropImageView ivShow;
    @BindView(R.id.ll_control)
    LinearLayout llControl;
    @BindView(R.id.needOffsetView)
    View needOffsetView;
    @BindView(R.id.btn_submit)
    Button btnSubmit;

    private String path;      //原图路径
    private String cutPath;   //剪切图路径
    private boolean isCropped;  //是否裁剪成功
    private File compressFile;  //压缩后文件
    private ConfirmDialog permissionDialog;

    /**
     * @param context
     * @param requestCode 请求码
     */
    public static void go(Activity context, int requestCode) {
        Intent intent = new Intent(context, UploadImgActivity.class);
        context.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo);
        ButterKnife.bind(this);
        ImmersionBar.with(this)
                .transparentStatusBar()
                .fitsSystemWindows(false)   //先取消BaseActivity 的统一设置
                .titleBarMarginTop(needOffsetView)
                .init();

        addPermissionRequest(rxPermissions.requestEach(Manifest.permission.CAMERA).subscribe(permission -> {
            if (permission.granted) {
                takePhoto();
            } else {
                permissionDialog = ConfirmDialog.newInstance();
                permissionDialog.setMessage("请您开启相机权限，否则您将无法正常使用麦诗鹏电司机")
                        .setOnClickListener(new ConfirmDialog.OnClickListener() {
                            @Override
                            public void onPositiveClick(View view) {
                                PermissionManager.goApplicationInfo(getContext());
                            }

                            @Override
                            public void onNegativeClick(View view) {
                                finish();
                            }

                            @Override
                            public void onDismiss() {
                                //检查有没有权限
                                if(!PermissionManager.checkSelfPermission(getContext(),new String[]{Manifest.permission.CAMERA})){
                                    finish();
                                }
                            }
                        })
                        .setPositiveText("去开启")
                        .setNegativeText("取消")
                        .setClickDismiss(false)
                        .show(getSupportFragmentManager(), "");
            }
        }));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(permissionDialog!=null){
            if(PermissionManager.checkSelfPermission(getContext(),new String[]{Manifest.permission.CAMERA})){
                permissionDialog.getDialog().dismiss();
                permissionDialog = null;
                takePhoto();
            }
        }
    }

    @Override
    protected UploadImgContract.Presenter onCreatePresenter() {
        return new UploadImgPresenter(this);
    }

    /**
     * 相机回调
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PHOTO_REQUEST_TAKEPHOTO) {
            Glide.with(this).load(path).into(ivShow);
            llControl.setVisibility(View.VISIBLE);
        } else {
            finish();
        }
    }


    @OnClick({R.id.iv_close, R.id.btn_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
                //重新拍照,返回键不退出
                takePhoto();
                break;
            case R.id.btn_submit:
                btnSubmit.setEnabled(false);
                crop();
                break;
            default:
                break;
        }
    }

    /**
     * 拍照
     */
    private void takePhoto() {
        // 1、创建存储照片的文件
        File file = new File(ImageUtils.getCacheDir() + File.separator + "picture_cache", ImageUtils.getFileName() + ".jpg");
        boolean flag = true;
        if (!file.getParentFile().exists()) {
            flag = file.getParentFile().mkdirs();
        }
        if(!flag){
            return;
        }
        path = file.getPath();
        Uri saveUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            saveUri = FileProvider.getUriForFile(this, getPackageName() + ".fileProvider", file);
        } else {
            saveUri = Uri.fromFile(file);
        }

        //2.调取系统相机拍照
        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        it.putExtra(MediaStore.EXTRA_OUTPUT, saveUri);
        startActivityForResult(it, PHOTO_REQUEST_TAKEPHOTO);
    }

    /**
     * 裁剪
     */
    private void crop() {
        showProgressDialog("照片上传中...");
        //1.创建裁剪保存路径
        File file = new File(ImageUtils.getCacheDir() + File.separator + "picture_cache", ImageUtils.getFileName() + ".jpg");
        boolean flag = true;
        if (!file.getParentFile().exists()) {
            flag = file.getParentFile().mkdirs();
        }
        if(!flag){
            return;
        }
        cutPath = file.getPath();
        Uri cutSaveUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cutSaveUri = FileProvider.getUriForFile(this, getPackageName() + ".fileProvider", file);
        } else {
            cutSaveUri = Uri.fromFile(file);
        }

        //2.开始裁剪且保存
        ivShow.startCrop(cutSaveUri, new CropCallback() {
            @Override
            public void onSuccess(Bitmap cropped) {
                isCropped = true;
            }

            @Override
            public void onError(Throwable e) {
                isCropped = false;
                dismissProgressDialog();
                btnSubmit.setEnabled(true);
                toast(R.string.toast_picture_crop_fail);
            }
        }, new SaveCallback() {
            @Override
            public void onSuccess(Uri uri) {
                compress();
            }

            @Override
            public void onError(Throwable e) {
                //由于保存到cache中，不能更新到图库，会返回onError，不用理会。
                compress();
            }
        });
    }

    /**
     * 压缩
     */
    private void compress() {
        if (!isCropped) {
            return;
        }
        ImageUtils.compress(this, cutPath, new OnCompressListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(File file) {
              /* Log.e(TAG, "压缩成功，图片路径：" + file.getPath());
                Log.e(TAG, "压缩成功，图片大小：" + file.length() / 1024 + "KB");*/
                compressFile = file;
                presenter.uploadImg(compressFile);
            }

            @Override
            public void onError(Throwable e) {
                dismissProgressDialog();
                btnSubmit.setEnabled(true);
                toast(R.string.toast_picture_compress_fail);
            }
        });
    }

    /**
     * 销毁前，删除保存的图片
     */
    @Override
    protected void onDestroy() {
        ImageUtils.deleteCacheDirImgFile();
        super.onDestroy();
    }

    /**
     * 图片上传成功
     * @param url
     */
    @Override
    public void uploadImgSuccess(String url) {
        dismissProgressDialog();
        Intent intent = new Intent();
        intent.putExtra(PIC_URL,url);
        setResult(RESULT_OK,intent);
        finish();
    }

    /**
     * 图片上传失败
     */
    @Override
    public void uploadImgFail() {
        dismissProgressDialog();
        btnSubmit.setEnabled(true);
    }
}
