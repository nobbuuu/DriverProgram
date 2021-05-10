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
import com.haylion.android.Constants;
import com.haylion.android.R;
import com.haylion.android.data.model.EventBean;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.base.BaseActivity;
import com.haylion.android.orderdetail.OrderTimeStore;
import com.haylion.android.data.util.PermissionManager;
import com.haylion.android.data.base.ConfirmDialog;
import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.CropCallback;
import com.isseiaoki.simplecropview.callback.SaveCallback;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import top.zibin.luban.OnCompressListener;

/**
 * @author dengzh
 * 上下车照片上传
 * 拍照-裁剪-压缩-上传
 */
public class UploadChildImgActivity extends BaseActivity<UploadChildImgContract.Presenter> implements UploadChildImgContract.View {

    public static final int PHOTO_REQUEST_TAKEPHOTO = 1; // 拍照请求码

    @BindView(R.id.iv_show)
    CropImageView ivShow;
    @BindView(R.id.ll_control)
    LinearLayout llControl;
    @BindView(R.id.needOffsetView)
    View needOffsetView;
    @BindView(R.id.btn_submit)
    Button btnSubmit;

    private int orderId;         //订单id
    private int orderStatus;     //订单状态
    private int onAutoCheck;     //上车免确认（0：需要确认 1：免确认）
    private int offAutoCheck;    //到达免确认（0：需要确认 1：免确认）
    private int orderType;       //订单类型

    private String path;      //原图路径
    private String cutPath;   //剪切图路径
    private boolean isCropped;  //是否裁剪成功
    private File compressFile;  //压缩后文件

    private boolean isRepeatCommit;  //是否重复提交


    /**
     * 第一次提交
     *
     * @param context
     * @param orderId      订单id
     * @param orderStatus  订单状态
     * @param offAutoCheck 是否自动确认下车
     * @param onAutoCheck  是否自动确认上车
     * @param orderType    订单类型
     */
    public static void go(Activity context, int orderId, int orderStatus, int offAutoCheck, int onAutoCheck, int orderType) {
        Intent intent = new Intent(context, UploadChildImgActivity.class);
        intent.putExtra("orderId", orderId);
        intent.putExtra("orderStatus", orderStatus);
        intent.putExtra("offAutoCheck", offAutoCheck);
        intent.putExtra("onAutoCheck", onAutoCheck);
        intent.putExtra("orderType", orderType);
        context.startActivity(intent);
        /*if (requestCode == -1) {
            context.startActivity(intent);
        } else {
            context.startActivityForResult(intent, requestCode);
        }*/
    }

    /**
     * 重新提交
     *
     * @param context
     * @param orderId
     */
    public static void reCommitGo(Activity context, int orderId, int orderStatus) {
        Intent intent = new Intent(context, UploadChildImgActivity.class);
        intent.putExtra("orderId", orderId);
        intent.putExtra("orderStatus", orderStatus);
        intent.putExtra("isRepeatCommit", true);
        context.startActivity(intent);
    }

    private ConfirmDialog permissionDialog;

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

        orderId = getIntent().getIntExtra("orderId", -1);
        orderStatus = getIntent().getIntExtra("orderStatus", -1);
        onAutoCheck = getIntent().getIntExtra("onAutoCheck", -1);
        offAutoCheck = getIntent().getIntExtra("offAutoCheck", -1);
        orderType = getIntent().getIntExtra("orderType", -1);
        isRepeatCommit = getIntent().getBooleanExtra("isRepeatCommit", false);

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
                                if (!PermissionManager.checkSelfPermission(getContext(), new String[]{Manifest.permission.CAMERA})) {
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
        if (permissionDialog != null) {
            if (PermissionManager.checkSelfPermission(getContext(), new String[]{Manifest.permission.CAMERA})) {
                permissionDialog.getDialog().dismiss();
                permissionDialog = null;
                takePhoto();
            }
        }
    }

    @Override
    protected UploadChildImgContract.Presenter onCreatePresenter() {
        return new UploadChildImgPresenter(this);
    }

    /**
     * 相机回调
     *
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
        if (!flag) {
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
        if (!flag) {
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
                presenter.uploadChildImg(orderId, orderStatus, compressFile, isRepeatCommit);
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
     * 图片上传失败
     */
    @Override
    public void uploadChildImgFail() {
        dismissProgressDialog();
        btnSubmit.setEnabled(true);
    }

    /**
     * 订单插入图片成功
     */
    @Override
    public void insertKidImageSuccess() {
        dismissProgressDialog();
        if (orderStatus == Order.ORDER_STATUS_ARRIVED_START_ADDR) { //上车
            if (onAutoCheck == 1) {
                toast(R.string.toast_parents_have_authorized_automatic_photo_confirmation);
            } else {
                toast(R.string.toast_picture_upload_success);
            }
            EventBus.getDefault().post(new EventBean(Constants.EventCode.UPLOAD_CHILD_ON_PHOTO_SUCCESS, null));

            //语音播报，等待家长确认照片
            if (onAutoCheck == 0) {
                // 保存提交上车照片的时间，以进行倒计时
//                OrderTimeStore.getInstance(getContext()).saveOrderTime((long) orderId);
                //等待家长确认
                EventBus.getDefault().post(new EventBean(Constants.EventCode.VOICE_PROMPT_WATTING_PASSENGER_CONFIRM_GET_ON_PHOTO, 1000));
            } else {
                //免确认，则播报，继续前往目的地。
                EventBus.getDefault().post(new EventBean(Constants.EventCode.VOICE_PROMPT_GO_TO_DESTINATION, 1000));
            }
        } else { //下车
            if (offAutoCheck == 1) {
                toast(R.string.toast_parents_have_authorized_automatic_photo_confirmation);
            } else {
                toast(R.string.toast_picture_upload_success);
            }
            EventBus.getDefault().post(new EventBean(Constants.EventCode.UPLOAD_CHILD_OFF_PHOTO_SUCCESS, null));

            //语音播报，等待家长确认照片
            if (offAutoCheck == 0) {
                // 保存提交下车照片的时间，以进行倒计时
//                OrderTimeStore.getInstance(getContext()).saveOrderTime((long) orderId);
                EventBus.getDefault().post(new EventBean(Constants.EventCode.VOICE_PROMPT_WATTING_PASSENGER_CONFIRM_GET_OFF_PHOTO, 1000));
            } else {
                //免确认，则不播报。
            }
        }
        finish();
    }

    /**
     * 订单插入失败
     */
    @Override
    public void insertKidImageFail() {
        dismissProgressDialog();
        finish();
    }

    /**
     * 重新提交成功
     */
    @Override
    public void reCommitKidPicSuccess() {
        dismissProgressDialog();
        toast(R.string.toast_picture_recommit_success);
        finish();
    }

    /**
     * 重新提交失败
     */
    @Override
    public void reCommitKidPicFail() {
        dismissProgressDialog();
        finish();
    }

    /**
     * 销毁前，删除保存的图片
     */
    @Override
    protected void onDestroy() {
        ImageUtils.deleteCacheDirImgFile();
        super.onDestroy();
    }

}
