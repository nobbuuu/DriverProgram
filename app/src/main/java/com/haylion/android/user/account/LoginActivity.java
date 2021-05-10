package com.haylion.android.user.account;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.dianping.logan.Logan;
import com.haylion.android.BuildConfig;
import com.haylion.android.R;
import com.haylion.android.common.aibus_location.DeviceLocationManager;
import com.haylion.android.data.base.BaseActivity;
import com.haylion.android.data.base.ConfirmDialog;
import com.haylion.android.data.model.Vehicle;
import com.haylion.android.data.repo.PrefserHelper;
import com.haylion.android.main.MainActivity;
import com.haylion.android.mvp.base.AppManager;
import com.haylion.android.mvp.util.LogUtils;
import com.haylion.android.pay.PayMainActivity;
import com.haylion.android.service.AmapTrackUploadService;
import com.haylion.android.service.FloatDialogService;
import com.haylion.android.service.WebsocketService;
import com.haylion.android.updater.AppUpdater;
import com.haylion.android.updater.UpdateListener;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;


public class LoginActivity extends BaseActivity<LoginContract.Presenter>
        implements LoginContract.View {

    private static final String TAG = "LoginActivity";
    private static final String EXTRA_RE_LOGIN = "re_login";

    @BindView(R.id.et_username)
    EditText mUsername;
    @BindView(R.id.et_password)
    EditText mPassword;
    @BindView(R.id.tv_tips)
    TextView tvTips;
    @BindView(R.id.bt_login)
    TextView tvLoginBtn;
    @BindView(R.id.tv_forget_pwd)
    TextView tvForgetPwd;
    @BindView(R.id.slView)
    ScrollView slView;
    @BindView(R.id.view_tel_line)
    View viewTelLine;
    @BindView(R.id.view_pwd_line)
    View viewPwdLine;

    @BindView(R.id.version_name)
    TextView mVersionName;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_act);
        ButterKnife.bind(this);
        if (isReLogin()) {
            PrefserHelper.clearPrefser();
            toast(R.string.toast_login_status_has_expired);
            Logan.w("isReLogin", 1);
        }

        //清除登录信息
        PrefserHelper.clearLoginInfo();

        ToggleButton togglePwd = (ToggleButton) findViewById(R.id.togglePwd);
        togglePwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //如果选中，显示密码
                    mPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    togglePwd.setBackgroundResource(R.mipmap.kejian);
                } else {
                    //否则隐藏密码
                    mPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    togglePwd.setBackgroundResource(R.mipmap.bukejian);
                }
            }
        });

        DeviceLocationManager deviceLocationManager = DeviceLocationManager.getInstance(getApplicationContext());
        if (deviceLocationManager != null) {
            Log.d(TAG, "停止上报位置");
            MainActivity.setLocationServiceHasStarted(false);
            //deviceLocationManager.stopLocation();
            deviceLocationManager.destroyLocation();
        }

        //todo 停止掉websocket service
        Logan.w("stop service", 1);
        Intent intent = new Intent(this, WebsocketService.class);
        stopService(intent);

        intent = new Intent(this, FloatDialogService.class);
        stopService(intent);

        intent = new Intent(this, AmapTrackUploadService.class);
        stopService(intent);

        initView();
    }

    private void initView() {
        mUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                viewTelLine.setPressed(hasFocus);
            }
        });
        mPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    changeScrollView();
                    viewPwdLine.setPressed(true);
                } else {
                    viewPwdLine.setPressed(false);
                }
            }
        });
        mPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                changeScrollView();
                return false;
            }
        });

        String versionName = String.format("v %s", BuildConfig.VERSION_NAME);
        mVersionName.setText(versionName);
    }

    @OnClick({R.id.bt_login, R.id.updater, R.id.tv_forget_pwd})
    public void onButtonClick(View view) {
        if (isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.bt_login:
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();
                tvLoginBtn.setClickable(false);

                // 2020/4/2，隐藏软键盘
                PayMainActivity.hideSoftInput(this);

                presenter.login(username, password, "");
                break;
            case R.id.tv_forget_pwd:
                presenter.getServiceTelNumber(true);
                break;
            case R.id.updater:
                checkNewVersion();
                break;
            default:
                break;
        }
    }


    @OnTextChanged(value = {R.id.et_username, R.id.et_password})
    public void onTextChanged() {
        String username = mUsername.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        tvLoginBtn.setEnabled(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password));
        tvTips.setText("");
        tvTips.setVisibility(View.INVISIBLE);
    }


    public static void start(Context context) {
        start(context, false);
    }

    public static void start(Context context, boolean reLogin) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(EXTRA_RE_LOGIN, reLogin);
        context.startActivity(intent);
    }

    private boolean isReLogin() {
        return getIntent().getBooleanExtra(EXTRA_RE_LOGIN, false);
    }


    @Override
    protected LoginContract.Presenter onCreatePresenter() {
        return new LoginPresenter(this);
    }

    @Override
    public void onLoginSuccess() {
        toast("登录成功");

        //将token绑定到推送功能
        String token = PrefserHelper.getToken();
        if (!TextUtils.isEmpty(token)) {
            PushAgent mPushAgent = PushAgent.getInstance(this);
            mPushAgent.setAlias(token, "token", new UTrack.ICallBack() {
                @Override
                public void onMessage(boolean b, String s) {
                    LogUtils.d("别名绑定成功" + s);
                }
            });
        }
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void showLoginFailTips(String msg) {
        tvTips.setText("* " + msg);
        tvTips.setVisibility(View.VISIBLE);
        tvLoginBtn.setClickable(true);
    }

    @Override
    public void updateVehicleView(List<Vehicle> vehcileList) {

    }

    @Override
    public void showDialDialog(String phone) {
        callCustomerService(phone);
    }

    private void checkNewVersion() {
        AppUpdater.Builder builder = new AppUpdater.Builder(getContext()).listener(new UpdateListener() {
            @Override
            public void onCanceled() {
                toast("取消更新");
            }

            @Override
            public void onCompleted() {
                toast("下载完成");
            }

            @Override
            public void onProgress(long currentOffset, long totalLength, int percent) {
                Log.d(TAG, "正在下载：" + percent);
            }

            @Override
            public void onError(String message) {
                Log.e(TAG, "出错了：" + message);
            }
        }).downloadUrl("http://down.360safe.com/360mse/360mse_nh00002.apk");
        builder.updateTitle("").updateMessage("性能优化和稳定性提升");
        File saveDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        builder.saveDir(saveDir, BuildConfig.APPLICATION_ID + ".provider");
        builder.enableNotification(R.mipmap.ic_launcher).build().showUpdateDialog();
    }


    /**
     * 使ScrollView指向底部
     */
    public void changeScrollView() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                slView.smoothScrollTo(0, slView.getHeight());
            }
        }, 300);
    }

    @Override
    protected void onDestroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }

    /**
     * 返回退出整个应用
     */
    @Override
    public void onBackPressed() {
        AppManager.getAppManager().AppExit(this);
    }

    /**
     * 拨打客服电话
     *
     * @param phoneNum
     */
    public void callCustomerService(String phoneNum) {
        ConfirmDialog.newInstance().setMessage(getString(R.string.dialog_forget_pwd_content))
                .setOnClickListener(new ConfirmDialog.OnClickListener() {
                    @Override
                    public void onPositiveClick(View view) {
                        if (!TextUtils.isEmpty(phoneNum)) {
                            Uri data = Uri.parse("tel:" + phoneNum);
                            Intent intent = new Intent(Intent.ACTION_DIAL); //跳到拨号界面
                            intent.setData(data);
                            startActivity(intent);
                        } else {
                            toast(R.string.toast_get_customer_service_phone_error);
                        }
                    }

                    @Override
                    public void onDismiss() {

                    }
                })
                .setNegativeText(getString(R.string.cancel))
                .setPositiveText(getString(R.string.ok))
                .setGravity(Gravity.CENTER)
                .show(getSupportFragmentManager(), "");

    /*    ConfirmDialogFragment dialog = ConfirmDialogFragment.newInstance();
        dialog.setOnClickListener(new ConfirmDialogFragment.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onPositiveClick(View view) {
                if (!(phoneNum == null || phoneNum.equals("") || phoneNum.equals("无"))) {
                    Uri data = Uri.parse("tel:" + phoneNum);
                  //  Intent intent = new Intent(Intent.ACTION_CALL); //直接拨打电话
                    Intent intent = new Intent(Intent.ACTION_DIAL); //跳到拨号界面
                    intent.setData(data);
                   *//* if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }*//*
                    startActivity(intent);
                } else{
                    toast(R.string.toast_get_customer_service_phone_error);
                }
            }

            @Override
            public void onNegativeClick(View view) {
                super.onNegativeClick(view);
            }
        });
        dialog.setMessage(R.string.dialog_forget_pwd_content).show(getSupportFragmentManager(), "");
        dialog.setPositiveText(R.string.ok);*/
    }


    /**
     * 测试用
     */
    @OnClick(R.id.btn_test)
    public void onViewClicked() {
        if (isFastClick()) {
            return;
        }
        toast("测试通知栏关闭");

       /* NewOrderDialog dialog = new NewOrderDialog(this);
        dialog.show();*/
        //startActivity(new Intent(this, StatusBarActivity.class));
        //OfflineMapActivity.go(this);
        //AMapNaviViewTestActivity.go(this);
    }


}
