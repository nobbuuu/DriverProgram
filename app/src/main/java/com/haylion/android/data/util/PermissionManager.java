package com.haylion.android.data.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AppOpsManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.haylion.android.R;
import com.haylion.android.data.base.ConfirmDialog;
import com.haylion.android.service.FloatDialogService;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

/**
 * Created by dengzh on 2019/12/5
 * 权限管理类
 * 必须：悬浮窗、定位、读写
 * 非必须：
 * 拍照，上传照片检查。
 *
 *
 * ACCESS_COARSE_LOCATION 通过WiFi或移动基站的方式获取用户错略的经纬度信息，定位精度大概误差在30~1500米
 * ACCESS_FINE_LOCATION 通过GPS芯片接收卫星的定位信息，定位精度达10米以内
 *
 * 问题：
 * 1）请求权限 不能在 onResume 请求，会死循环。
 * 2) 在onStart()中，后台->前台判断。
 * 3) 在使用应用过程中，关闭权限，会导致应用重新启动，且默认打开申请权限的页面。因此会出现白屏，可能应用奔溃等问题。
 */
public class PermissionManager {

    private final String TAG = getClass().getSimpleName();
    //必要，定位、读写权限
    private String[] mustPerms = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            /*Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_PHONE_STATE*/};

    private RxPermissions rxPermissions;
    private CompositeDisposable permissionRequests;
    private FragmentActivity context;

    public PermissionManager(FragmentActivity context) {
        this.context = context;
        rxPermissions = new RxPermissions(context);
        permissionRequests = new CompositeDisposable();
    }

    /**
     * 实时检测
     */
    public void check(){
        //悬浮窗
        boolean overlayWindowFlag = checkOverlayWindow();
        Log.e(TAG,"overlayWindowFlag" + overlayWindowFlag);
        if(!overlayWindowFlag){
            return;
        }
        //必要权限
        checkMustPermission();
    }

    public void onDestroy(){
        if (permissionRequests != null && !permissionRequests.isDisposed()) {
            permissionRequests.dispose();
            permissionRequests = null;
        }
        rxPermissions = null;
        context = null;
    }

    /**
     * 检查是否有悬浮窗权限，必须
     */
    private boolean checkOverlayWindow(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(context)) {
                Log.e(TAG,"悬浮窗已授权");
                FloatDialogService.overlayWindowFlag = true;
                return true;
            } else {
                Log.e(TAG,"悬浮窗没有授权");
                showPermissionDialog(TYPE_OVERLAY);
                FloatDialogService.overlayWindowFlag = false;
                return false;
            }
        }else{
            return true;
        }
    }

    /**
     * 去悬浮窗设置页面
     * 6.0 以上，google 统一悬浮窗管理
     */

    private void goOverLaySetting(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            context.startActivity(intent);
        }
    }

    /**
     * 检查是否有定位权限，必须
     */
    private void checkMustPermission(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return;
        }
        permissionRequests.add(rxPermissions.requestEach(mustPerms).subscribe(permission -> {
            //会返回四个 permission
            Log.e(TAG,"权限名称:"+permission.name+",申请结果:"+permission.granted);
            if(permission.granted){
                //已授权，不需处理
            }else {
                //没授权
                //决定是显示哪个弹窗
                if(Manifest.permission.ACCESS_COARSE_LOCATION.equals(permission.name)
                        ||Manifest.permission.ACCESS_FINE_LOCATION.equals(permission.name)){
                    //显示定位权限提示弹窗
                    Log.e(TAG,"定位没有授权");
                    showPermissionDialog(TYPE_lOCATION);
                }else if(Manifest.permission.CALL_PHONE.equals(permission.name)
                        ||Manifest.permission.READ_PHONE_STATE.equals(permission.name)){
                    //显示呼号权限提示弹窗
                    Log.e(TAG,"呼号没有授权");
                    showPermissionDialog(TYPE_CALL);
                }else if(Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permission.name)
                        ||Manifest.permission.READ_EXTERNAL_STORAGE.equals(permission.name)){
                    //显示呼号权限提示弹窗
                    Log.e(TAG,"读写没有授权");
                    showPermissionDialog(TYPE_EXTERNAL_STORAGE);
                }
            }
        }));
    }

    private final int TYPE_OVERLAY = 0;
    private final int TYPE_lOCATION = 1;
    private final int TYPE_CALL = 2;
    private final int TYPE_EXTERNAL_STORAGE = 3;
    private boolean isShowing;

    private void showPermissionDialog(int type){
        if(isShowing){
            return;
        }
        isShowing = true;
        String msg = "";
        switch (type){
            case TYPE_OVERLAY:
                msg = "为了不影响抢单，请您开启浮窗权限，否则您将无法正常使用麦诗鹏电司机";
                break;
            case TYPE_lOCATION:
                msg = "为了提供更准确的数据，我们需要获取实时位置，请您开启定位权限，否则您将无法正常使用麦诗鹏电司机";
                break;
            case TYPE_CALL:
                msg = "请您开启拨号权限，否则您将无法正常使用麦诗鹏电司机";
                break;
            case TYPE_EXTERNAL_STORAGE:
                msg = "请您开启存储权限，否则您将无法正常使用麦诗鹏电司机";
                break;
                default:
                    break;
        }
        ConfirmDialog.newInstance().setMessage(msg)
                .setOnClickListener(new ConfirmDialog.OnClickListener() {
                    @Override
                    public void onPositiveClick(View view) {
                        //根据类型去对应界面
                        if(type == TYPE_OVERLAY){
                            goOverLaySetting();
                        }else{
                            goApplicationInfo(context);
                        }
                    }

                    @Override
                    public void onDismiss() {
                        //取消
                        isShowing = false;
                    }
                })
                .setPositiveText("去开启")
                .setType(ConfirmDialog.TYPE_ONE_BUTTON)
                .setCancelOutside(false)
                .show(context.getSupportFragmentManager(), "");
    }

    /**
     * 跳转 - 应用信息界面
     * 考虑到稳定性和兼容性，不直接跳转到权限设置页
     */
    public static  void goApplicationInfo(Context context){
        Intent localIntent = new Intent();
        localIntent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        localIntent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(localIntent);
    }


    /**
     * 检查悬浮窗是否已开启和必要权限是否已授权
     * @return
     */
    public boolean checkOverLayOpenAndPermissionGrant(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean overLayEnable = false;
            if (Settings.canDrawOverlays(context)) {
                Log.e(TAG,"checkOverLayOpenAndPermissionGrant, 悬浮窗已授权");
                FloatDialogService.overlayWindowFlag = true;
                overLayEnable =  true;
            } else {
                Log.e(TAG,"checkOverLayOpenAndPermissionGrant, 悬浮窗没有授权");
                FloatDialogService.overlayWindowFlag = false;
                overLayEnable =  false;
            }
            if (overLayEnable && checkSelfPermission(context,mustPerms) ) {
                return true;
            } else {
                return false;
            }
        }else{
           return true;
        }
    }

    /**
     * 只是检查权限是否已全部授权
     * @param permissions
     * @return
     */
    public static boolean checkSelfPermission(Context context,String[] permissions){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){  //6.0才申请权限
            return true;
        }
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
