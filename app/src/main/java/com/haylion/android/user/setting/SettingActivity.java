package com.haylion.android.user.setting;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.amap.api.services.route.DistanceResult;
import com.amap.api.services.route.DistanceSearch;
import com.haylion.android.BuildConfig;
import com.haylion.android.Constants;
import com.haylion.android.R;
import com.haylion.android.common.aibus_location.data.GpsData;
import com.haylion.android.common.map.AMapUtil;
import com.haylion.android.common.view.dialog.DialogUtils;
import com.haylion.android.data.base.BaseActivity;
import com.haylion.android.data.model.LatestVersionBean;
import com.haylion.android.data.model.Version;
import com.haylion.android.data.repo.PrefserHelper;

import com.haylion.android.updateApk.VersionDialog;
import com.haylion.android.user.account.ChangePasswordActivity;
import com.haylion.android.user.account.DeveloperDialog;
import com.haylion.android.user.account.LoginActivity;
import com.tencent.bugly.beta.Beta;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends BaseActivity<SettingContract.Presenter>
        implements SettingContract.View {
    private static final String TAG = "OrderSettingActivity";

    /*    @BindView(R.id.sw_navi_mode)
        Switch swNaviMode;
        @BindView(R.id.sw_navi_emulator)*/
    Switch swNaviEmulator;
    @BindView(R.id.tv_app_version)
    TextView tvAppVersion;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.ll_change_password)
    LinearLayout llChangePwd;

    @BindView(R.id.tv_service_phone)
    TextView tvServicePhone;
    /*    @BindView(R.id.tv_simulate_location)
        TextView tvSimulateLocation;*/
    @BindView(R.id.debugger)
    LinearLayout llDebugger;

    @BindView(R.id.btn_logout)
    TextView btnLogout;

    @BindView(R.id.ll_version)
    LinearLayout llVersion;

    @BindView(R.id.tv_header)
    TextView tvHeader;

    @OnClick({R.id.iv_back, R.id.ll_change_password, R.id.debugger, R.id.btn_logout, R.id.ll_service_phone, R.id.ll_version})
    public void onButtonClick(View view) {
        switch (view.getId()) {
/*            case R.id.debugger:
                Intent intent = new Intent(this, DisplayLogActivity.class);
                startActivity(intent);
                break;*/
            case R.id.ll_change_password:
                Intent intent = new Intent(this, ChangePasswordActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_logout:
                btnLogout.setEnabled(false);
                presenter.logout();
                break;
            case R.id.ll_service_phone:
                DialogUtils.showCustomerServiceCallDialog(this, Constants.SERVICE_PHONE, true);
                break;
            case R.id.ll_version:
                if (!VersionDialog.isDownloading) {
                    presenter.checkUpdates();
                } else {
                    toast("正在下载新版安装包...");
                }
                // Beta.checkUpgrade(true, true);
                break;
            default:
                break;
        }
    }


    @OnClick(R.id.iv_back)
    public void clickBackMenu() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_act);
        ButterKnife.bind(this);

        initView();

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenHeight = metrics.heightPixels;//屏幕高度像素
        int screenWidth = metrics.widthPixels;//屏幕宽度像素
        //density = densityDpi / 160
        float density = metrics.density;// "屏幕密度"（0.75 / 1.0 / 1.5）
        int densityDpi = metrics.densityDpi;// 屏幕密度dpi（120 / 160 / 240）每一英寸的屏幕所包含的像素数.值越高的设备，其屏幕显示画面的效果也就越精细
        // 屏幕宽度算法:屏幕宽度（像素）/"屏幕密度"   px = dp * (dpi / 160)
        int height = (int) (screenHeight / density);//屏幕高度dp
        int weight = (int) (screenWidth / density);//屏幕宽度dp

        Log.d(TAG, "device px, height:" + screenHeight + ", weight:" + screenWidth + ",density:" + density + ",densityDpi:" + densityDpi);
        Log.d(TAG, "device dp, height:" + height + ", weight:" + weight);
    }


    private DeveloperDialog mDeveloperDialog;

    public void initView() {

        //调试用，打开调试弹窗
        tvHeader.setOnClickListener(new OnMultiClickListener(5) {
            @Override
            public void onMultiClick(View view) {
                if (mDeveloperDialog == null) {
                    mDeveloperDialog = new DeveloperDialog(SettingActivity.this);
                }
                if (mDeveloperDialog.isShowing()) {
                    toast("你手速挺快的呀~");
                } else {
                    mDeveloperDialog.show();
                }
            }
        });

        //版本号显示
        Context mContext = getContext();
        String verName = "";
        try {
            verName = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
            Log.d(TAG, "verName:" + verName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        tvAppVersion.setText("当前版本 " + BuildConfig.TYPE + verName);

        if (!("").equals(Constants.SERVICE_PHONE)) {
            String phoneNum = Constants.SERVICE_PHONE;
            if (phoneNum.length() == 11) {
                tvServicePhone.setText(phoneNum.substring(0, 3) + "-" + phoneNum.substring(3, 7) + "-" + phoneNum.substring(7));
            } else {
                tvServicePhone.setText(phoneNum);
            }
            tvServicePhone.setTextColor(getResources().getColor(R.color.maas_text_blue));
        }

        //日志调试功能
/*        if (BuildConfig.DEBUG) {
            llDebugger.setVisibility(View.VISIBLE);
        }*/

/*        //手动检查版本升级功能
       if (BuildConfig.DEBUG) {
            LinearLayout llCheckAppVersion = findViewById(R.id.ll_check_app_version);
            llCheckAppVersion.setVisibility(View.VISIBLE);
        }*/

    }


    /*    */

    /**
     * 是否开启模拟定位
     *//*
    @OnClick(R.id.ll_simulate_location)
    void simulateLocation() {
        String[] locations = {"关闭模拟定位", Constants.Gps.NANKEDA, Constants.Gps.LONGSHENG};
        MaterialDialog.Builder mBuilder = new MaterialDialog.Builder(getActivity());
        mBuilder.title("设置模拟定位地点");
        mBuilder.titleGravity(GravityEnum.CENTER);
        mBuilder.items(locations);
        mBuilder.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

            }
        });

        mBuilder.itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
            @Override
            public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                if (TextUtils.isEmpty(text)) {
                    Toast.makeText(getActivity(), "请选择模拟位置", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), text, Toast.LENGTH_LONG).show();
                    TextView tvLocation = getActivity().findViewById(R.id.tv_simulate_location);
                    if (text.equals("关闭模拟定位")) {
                        LocationCfg.setMockLocationStatus(false);
                        LocationCfg.setMockLocation(text.toString());
                        tvLocation.setText("关闭");
                    } else {
                        LocationCfg.setMockLocationStatus(true);
                        LocationCfg.setMockLocation(text.toString());
                        tvLocation.setText(text);
                    }
                    dialog.dismiss();
                }
                return false;
            }
        });
        MaterialDialog materialDialog = mBuilder.build();
        materialDialog.show();

    }*/
    @Override
    protected SettingContract.Presenter onCreatePresenter() {
        return new SettingPresenter(this);
    }


    @Override
    public void logoutSuccess() {
        btnLogout.setEnabled(true);
        LoginActivity.start(this, false);

        //退出后，调用友盟推送需要移除别名
        PushAgent mPushAgent = PushAgent.getInstance(this);
        String token = PrefserHelper.getToken();
        if (!TextUtils.isEmpty(token)) {
            //移除别名ID
            mPushAgent.deleteAlias(token, "token", new UTrack.ICallBack() {
                @Override
                public void onMessage(boolean isSuccess, String message) {
                    Log.d(TAG, "移除别名 isSuccess：" + isSuccess);
                }
            });
        }
        finish();
    }

    @Override
    public void logoutFail() {
        btnLogout.setEnabled(true);
    }

    @Override
    public void checkUpdatesSuccess(LatestVersionBean version) {
        VersionDialog dialog = new VersionDialog(this, 1);
        dialog.setData(version);
        dialog.show();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1000:
                    Log.d(TAG, "正在检测版本中");
                    toast("正在检测版本中");
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    public void showCheckingLoading() {
        handler.sendEmptyMessageDelayed(1000, 1000);
    }

    @Override
    public void closeCheckingLoading() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
