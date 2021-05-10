package com.haylion.android.push;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.haylion.android.R;
import com.haylion.android.data.model.NotificationData;
import com.haylion.android.data.model.UmengPushBodyAndExtraData;
import com.haylion.android.data.model.UmengPushBodyData;
import com.haylion.android.data.model.UmengPushExtraData;
import com.haylion.android.data.repo.PrefserHelper;
import com.haylion.android.main.MainActivity;
import com.haylion.android.service.FloatDialogService;
import com.haylion.android.user.account.LoginActivity;
import com.umeng.message.UmengNotifyClickActivity;

import org.android.agoo.common.AgooConstants;

import java.security.MessageDigest;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class MipushTestActivity extends UmengNotifyClickActivity
        implements EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {

    private static String TAG = MipushTestActivity.class.getName();

    private static final int GET_CONFIG = 1000;
    private boolean startNewActivity = false;
    /**
     * 权限
     */
    String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    @Override
    protected void onCreate(Bundle bundle) {
        Log.d(TAG, "onCreate");
        super.onCreate(bundle);
        setContentView(R.layout.activity_welcome);
    }

     /**
      * @method
      * @description 收到推送消息（系统通道）
      * @date: 2019/12/8 11:24
      * @author: tandongdong
      * @param
      * @return
      */
    @Override
    public void onMessage(Intent intent) {
        Log.d(TAG, "onMessage");
        super.onMessage(intent);  //此方法必须调用，否则无法统计打开数
        String body = intent.getStringExtra(AgooConstants.MESSAGE_BODY);
        String extra = intent.getStringExtra(AgooConstants.MESSAGE_EXT);

        Log.i(TAG, "umeng json, body:" + body + ",       extra: " + extra);

        //收到消息后，将信息传递给下一个页面
        startNewActivity = true;
        FloatDialogService.overlayWindowFlag = true;
        if (PrefserHelper.isLoggedIn()) {
            startMainActivity(body);
        } else {
            LoginActivity.start(this);
            finish();
        }
    }

    public void startMainActivity(String body){
        Log.d(TAG, "body:" + body);
        Intent intent = new Intent(this, MainActivity.class);
//        intent.putExtra(AgooConstants.MESSAGE_BODY, body);

        Gson gson = new Gson();
        UmengPushBodyAndExtraData data = gson.fromJson(body, UmengPushBodyAndExtraData.class);
        UmengPushBodyData pushBodyData = data.getBody();
        UmengPushExtraData pushExtraData = data.getExtra();

        NotificationData notificationData = new NotificationData();
        notificationData.setMessageId(Integer.parseInt(pushExtraData.getMessageId()));
        notificationData.setMessageType(Integer.parseInt(pushExtraData.getCmdSn()));
        notificationData.setOrderId(Integer.parseInt(pushExtraData.getOrderId()));
        intent.putExtra(MainActivity.PUSH_MESSAGE_DATA, notificationData);
        startActivity(intent);
//        finish();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
/*            switch (msg.what) {
                case GET_CONFIG:
                    //获取配置
                    break;
                default:
                    break;
            }*/
        }

    };

    // MD5加密，32位小写
    public static String md5(String str) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        md5.update(str.getBytes());
        byte[] md5Bytes = md5.digest();
        StringBuilder hexValue = new StringBuilder();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    //检查权限，READ_PHONE_STATE在API>=23需要用户手动赋予权限
    public static boolean checkPermission(Context context, String permission) {
        boolean result = false;
        if (Build.VERSION.SDK_INT >= 23) {
            if (context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                result = true;
            }
        } else {
            PackageManager pm = context.getPackageManager();
            if (pm.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
                result = true;
            }
        }
        return result;
    }

    //判空
    private String validate(String value) {
        if (value == null) {
            return "";
        }
        return value;
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        if (handler != null) {
            handler.removeMessages(GET_CONFIG);
            handler.removeCallbacksAndMessages(null);
        }

        if (dialog != null) {
            dialog.dismiss();
        }
    }

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.  This means
     * that in some cases the previous state may still be saved, not allowing
     * fragment transactions that modify the state.  To correctly interact
     * with fragments in their proper state, you should instead override
     * {@link #onResumeFragments()}.
     */
    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
//        overlayWindowJudge();
        requestStoragePermission();
    }

    private void requestStoragePermission() {
        requestAppPermission();
/*        addPermissionRequest(rxPermissions.requestEach(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_PHONE_STATE)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            Log.d(TAG, "已授予权限：" + permission.toString());
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            Log.d(TAG, "请求权限：" + permission.toString());
                            WelcomeActivity.this.requestStoragePermission();
                        } else {
                            Log.d(TAG, "未授权：" + permission.toString());
                            toast("未授予权限" + permission.toString());
                            WelcomeActivity.this.finish();
                        }

                        WelcomeActivity.this.overlayWindow();

                    }
                }, throwable -> Log.d(TAG, throwable.getMessage())));*/
    }

    public void overlayWindow() {
        //某些手机判断悬浮窗开启有延迟，获取不到延迟0.5s再请求一次
        handler.postDelayed(this::overlayWindowCheck, 500);
    }

    public void overlayWindowCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this)) {
                FloatDialogService.overlayWindowFlag = true;
                checkLoginStatus();
            } else {
                FloatDialogService.overlayWindowFlag = false;
                showDialog();
            }
        } else {

        }
    }


    Dialog dialog;

    private void showDialog() {
        if (dialog == null) {
            dialog = new Dialog(this, R.style.Translucent_NoTitle);
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_premission_open, null);

        TextView massage = view.findViewById(R.id.tv_message);
        massage.setText("未开启浮窗权限\n不能使用抢单功能");

        dialog.show();
        dialog.getWindow().setContentView(view);
        dialog.setCanceledOnTouchOutside(false);

        TextView confirm = view.findViewById(R.id.btn_confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        });
    }

    private void checkLoginStatus() {
        if(startNewActivity){
            Log.d(TAG, "startNewActivity:" + startNewActivity);
            return;
        }
        if (PrefserHelper.isLoggedIn()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            LoginActivity.start(this);
            finish();
        }
    }

    /**
     * 请求权限
     */
    private void requestAppPermission() {
        if (EasyPermissions.hasPermissions(this, perms)) {
            overlayWindow();
        } else {
            EasyPermissions.requestPermissions(this, "缺少必要的权限,会影响功能的使用！", 100, perms);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.e(TAG, "onRequestPermissionsResult");
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Log.e(TAG, "获取成功的权限" + perms);
        overlayWindow();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Log.e(TAG, "获取失败的权限" + perms);
    }


    /**
     * dialog提醒点击“确定”
     *
     * @param requestCode
     */
    @Override
    public void onRationaleAccepted(int requestCode) {
        Log.e(TAG, "onRationaleAccepted");
    }

    /**
     * dialog提醒点击“取消”
     *
     * @param requestCode
     */
    @Override
    public void onRationaleDenied(int requestCode) {
        Log.e(TAG, "onRationaleDenied");
        finish();
    }

    @Override
    public void onStop(){
        super.onStop();
        finish();
    }
}


