package com.haylion.android.ordersetting;


import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.haylion.android.R;
import com.haylion.android.address.SearchAddrActivity;
import com.haylion.android.common.map.AMapUtil;
import com.haylion.android.data.model.AddressInfo;
import com.haylion.android.data.model.ListenOrderSetting;
import com.haylion.android.data.repo.PrefserHelper;

import com.haylion.android.data.base.BaseActivity;
import com.haylion.android.data.base.ConfirmDialog;
import com.haylion.android.mvp.util.LogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrderSettingActivity extends BaseActivity<OrderSettingContract.Presenter>
        implements OrderSettingContract.View {
    private static final String TAG = "OrderSettingActivity";

    @BindView(R.id.iv_back)
    ImageView ivBack;

    @BindView(R.id.sw_audio_setting)
    Switch swAudioSetting;
    @BindView(R.id.sw_only_get_car_home_order)
    Switch swOnlyGetBackHomeOrder;
    @BindView(R.id.sw_get_cargo_order)
    Switch swGetCargoOrder;

    @BindView(R.id.ll_get_cargo_order)
    LinearLayout llGetCargoOrder;

    @BindView(R.id.order_type_time_realtime)
    TextView tvOrderTypeTimeRealtime;
    @BindView(R.id.order_type_time_book)
    TextView tvOrderTypeTimeBook;
    @BindView(R.id.order_type_time_all)
    TextView tvOrderTypeTimeAll;

    @BindView(R.id.order_type_carpool)
    TextView tvOrderTypeCarpool;
    @BindView(R.id.order_type_no_carpool)
    TextView tvOrderTypeNoCarpool;
    @BindView(R.id.order_type_all)
    TextView tvOrderTypeAll;

    @BindView(R.id.rl_have_address)
    RelativeLayout rlHaveAddress;
    @BindView(R.id.tv_add_address)
    TextView tvAddAddress;
    @BindView(R.id.tv_change_address)
    TextView tvChangeAddress;
    @BindView(R.id.tv_address_name)
    TextView tvAddressName;
    @BindView(R.id.tv_address_detail)
    TextView tvAddressDetail;
    @BindView(R.id.line_get_cargo_order)
    View lineGetCargoOrder;
    @BindView(R.id.tv_save_setting)
    TextView tvSaveSetting;


    private ListenOrderSetting listenOrderSetting;

    @OnClick({R.id.ll_back, R.id.tv_add_address, R.id.tv_change_address, R.id.tv_save_setting})
    public void onButtonClick(View view) {
        String header;
        switch (view.getId()) {
            case R.id.tv_add_address:
            case R.id.tv_change_address:
                if (view.getId() == R.id.tv_add_address) {
                    header = "添加收车地址";
                } else {
                    header = "修改收车地址";
                }
                Intent intent = new Intent(this, SearchAddrActivity.class);
                intent.putExtra("header", header);
                startActivityForResult(intent, 1);
                break;
            case R.id.ll_back:
                finish();
                break;
            case R.id.tv_save_setting:
                presenter.setListenOrderSetting(listenOrderSetting);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_setting);
        ButterKnife.bind(this);


//        initViewClickAction();
        initView(); //默认从本地读取配置，服务器上的配置和本地不一致后，再更新本地配置。
        presenter.getListenOrderSetting();

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

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
//        initViewClickAction();

/*        initView(); //默认从本地读取配置，服务器上的配置和本地不一致后，再更新本地配置。
        presenter.getListenOrderSetting();*/
//        presenter.getBackHomeAddr();

    }

    private void initView() {
        ListenOrderSetting localSetting = PrefserHelper.getOrderSettingInfo();
        if (localSetting != null) {
            LogUtils.d(TAG, "local setting:" + localSetting.toString());
            orderListenSettingDisplay(localSetting);
        }
    }

    @OnClick({R.id.order_type_time_realtime, R.id.order_type_time_book, R.id.order_type_time_all,
            R.id.order_type_carpool, R.id.order_type_no_carpool, R.id.order_type_all})
    public void onClick(View view) {
        if (listenOrderSetting == null) {
            LogUtils.e(TAG, "listenOrderSetting is null");
            return;
        }
        switch (view.getId()) {
            case R.id.order_type_time_realtime:
                setOrderTypeTimeSelectDefault();
                tvOrderTypeTimeRealtime.setTextColor(getResources().getColor(R.color.text_white));
                tvOrderTypeTimeRealtime.setBackground(getResources().getDrawable(R.drawable.switch_btn_bg_enable));
                break;
            case R.id.order_type_time_book:
                setOrderTypeTimeSelectDefault();
                tvOrderTypeTimeBook.setTextColor(getResources().getColor(R.color.text_white));
                tvOrderTypeTimeBook.setBackground(getResources().getDrawable(R.drawable.switch_btn_bg_enable));
                break;
            case R.id.order_type_time_all:
                setOrderTypeTimeSelectDefault();
                tvOrderTypeTimeAll.setTextColor(getResources().getColor(R.color.text_white));
                tvOrderTypeTimeAll.setBackground(getResources().getDrawable(R.drawable.switch_btn_bg_enable));
                break;
            case R.id.order_type_carpool:
                setTvOrderTypeCarpoolSelectDefault();
                tvOrderTypeCarpool.setTextColor(getResources().getColor(R.color.text_white));
                tvOrderTypeCarpool.setBackground(getResources().getDrawable(R.drawable.switch_btn_bg_enable));
                listenOrderSetting.setCarpool(1);
                break;
            case R.id.order_type_no_carpool:
                setTvOrderTypeCarpoolSelectDefault();
                tvOrderTypeNoCarpool.setTextColor(getResources().getColor(R.color.text_white));
                tvOrderTypeNoCarpool.setBackground(getResources().getDrawable(R.drawable.switch_btn_bg_enable));
                listenOrderSetting.setCarpool(2);
                break;
            case R.id.order_type_all:
                setTvOrderTypeCarpoolSelectDefault();
                tvOrderTypeAll.setTextColor(getResources().getColor(R.color.text_white));
                tvOrderTypeAll.setBackground(getResources().getDrawable(R.drawable.switch_btn_bg_enable));
                listenOrderSetting.setCarpool(0);
                break;
            default:
                break;
        }

//        presenter.setListenOrderSetting(listenOrderSetting);
    }

    @OnClick({R.id.order_distance_2, R.id.order_distance_3, R.id.order_distance_4,
            R.id.order_distance_5, R.id.order_distance_3_5})
    public void onDistanceClick(View view) {
        if (listenOrderSetting == null) {
            return;
        }
        double receiveRange;
        switch (view.getId()) {
            case R.id.order_distance_2:
                receiveRange = 2;
                break;
            case R.id.order_distance_3:
                receiveRange = 3;
                break;
            case R.id.order_distance_4:
                receiveRange = 4;
                break;
            case R.id.order_distance_5:
                receiveRange = 5;
                break;
            case R.id.order_distance_3_5:
            default:
                receiveRange = 3.5;
                break;
        }
        TextView tv = findViewById(view.getId());
        clearDistanceSelect();
        tv.setTextColor(getResources().getColor(R.color.text_white));
        tv.setBackground(getResources().getDrawable(R.drawable.switch_btn_bg_enable));

        listenOrderSetting.setReceivingRange(receiveRange);
//        presenter.setListenOrderSetting(listenOrderSetting);
    }

    private void clearDistanceSelect() {
        TextView tv;
        tv = findViewById(R.id.order_distance_2);
        tv.setTextColor(getResources().getColor(R.color.text_black));
        tv.setBackground(getResources().getDrawable(R.drawable.switch_btn_bg_disable));
        tv = findViewById(R.id.order_distance_3);
        tv.setTextColor(getResources().getColor(R.color.text_black));
        tv.setBackground(getResources().getDrawable(R.drawable.switch_btn_bg_disable));
        tv = findViewById(R.id.order_distance_4);
        tv.setTextColor(getResources().getColor(R.color.text_black));
        tv.setBackground(getResources().getDrawable(R.drawable.switch_btn_bg_disable));
        tv = findViewById(R.id.order_distance_5);
        tv.setTextColor(getResources().getColor(R.color.text_black));
        tv.setBackground(getResources().getDrawable(R.drawable.switch_btn_bg_disable));
        tv = findViewById(R.id.order_distance_3_5);
        tv.setTextColor(getResources().getColor(R.color.text_black));
        tv.setBackground(getResources().getDrawable(R.drawable.switch_btn_bg_disable));

    }

    private void setOrderTypeTimeSelectDefault() {
        tvOrderTypeTimeRealtime.setTextColor(getResources().getColor(R.color.text_black));
        tvOrderTypeTimeRealtime.setBackground(getResources().getDrawable(R.drawable.switch_btn_bg_disable));
        tvOrderTypeTimeBook.setTextColor(getResources().getColor(R.color.text_black));
        tvOrderTypeTimeBook.setBackground(getResources().getDrawable(R.drawable.switch_btn_bg_disable));
        tvOrderTypeTimeAll.setTextColor(getResources().getColor(R.color.text_black));
        tvOrderTypeTimeAll.setBackground(getResources().getDrawable(R.drawable.switch_btn_bg_disable));
    }

    private void setTvOrderTypeCarpoolSelectDefault() {
        tvOrderTypeCarpool.setTextColor(getResources().getColor(R.color.maas_text_deep_gray));
        tvOrderTypeCarpool.setBackground(getResources().getDrawable(R.drawable.switch_btn_bg_disable));
        tvOrderTypeNoCarpool.setTextColor(getResources().getColor(R.color.maas_text_deep_gray));
        tvOrderTypeNoCarpool.setBackground(getResources().getDrawable(R.drawable.switch_btn_bg_disable));
        tvOrderTypeAll.setTextColor(getResources().getColor(R.color.maas_text_deep_gray));
        tvOrderTypeAll.setBackground(getResources().getDrawable(R.drawable.switch_btn_bg_disable));
    }

    public void initViewClickAction() {
        if (listenOrderSetting == null) {
            LogUtils.d(TAG, "listenOrderSetting is null");
            return;
        }
        swAudioSetting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
//                    swAudioSetting.setText("开启");
                    listenOrderSetting.setVoiceBroadcast(1);
                } else {
//                    swAudioSetting.setText("关闭");
                    listenOrderSetting.setVoiceBroadcast(0);
                }
//                presenter.setListenOrderSetting(listenOrderSetting);
            }
        });

        swOnlyGetBackHomeOrder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
//                    mText.setText("开启");
//                    toast("只听收车单,开启");
                    if (rlHaveAddress.getVisibility() != View.VISIBLE) {
                        toast(R.string.toast_please_add_car_address);
                        swOnlyGetBackHomeOrder.setChecked(false);
                    } else {
                        listenOrderSetting.setReceiveOnly(1);
                        //弹窗提示
                        showDialogTipsForBackHomeOrder();
                    }
                } else {
                    listenOrderSetting.setReceiveOnly(0);
                }
//                presenter.setListenOrderSetting(listenOrderSetting);
            }
        });

        swGetCargoOrder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    listenOrderSetting.setCargoPassengerServiceStatus(1);
//                    presenter.setListenOrderSetting(listenOrderSetting);
                } else {
//                    showDialogForCloseCargo();
                    listenOrderSetting.setCargoPassengerServiceStatus(0);
                }
//                presenter.setListenOrderSetting(listenOrderSetting);
            }
        });
    }

    //打开只听收车单开关，需要弹窗提示
    private void showDialogTipsForBackHomeOrder() {
        ConfirmDialog.newInstance().setOnClickListener(new ConfirmDialog.OnClickListener() {
                    @Override
                    public void onPositiveClick(View view) {

                    }

                    @Override
                    public void onDismiss() {
                    }
                })
                .setMessage("接到订单后记得关闭开关，否则影响正常听单")
                .setPositiveText("我知道了")
                .setType(1)
                .show(getSupportFragmentManager(), "");

       /* InfoDialog dialog = InfoDialog.newInstance();
        dialog.setOnClickListener(new InfoDialog.OnClickListener() {

            @Override
            public void onPositiveClick(View view) {
                super.onPositiveClick(view);
            }

            @Override
            public void onNegativeClick(View view) {
                super.onNegativeClick(view);
            }

            @Override
            public void onCloseDialog(View view) {
                super.onCloseDialog(view);
                dialog.dismiss();
            }

            @Override
            public void onConfirm(View view) {
                super.onConfirm(view);
                dialog.dismiss();
            }
        });
        dialog.setConfirmText("我知道了");
        dialog.setMessage("接到订单后记得关闭开关，否则影响正常听单")
                .show(getSupportFragmentManager(), "");*/

    }

   /* private void showDialogForCloseCargo() {
        InfoDialog dialog = InfoDialog.newInstance();
        dialog.setDialogType(true, false, false);
        dialog.setOnClickListener(new InfoDialog.OnClickListener() {

            @Override
            public void onPositiveClick(View view) {
                dialog.dismiss();
                super.onPositiveClick(view);
                if (listenOrderSetting != null) {
                    listenOrderSetting.setCargoPassengerServiceStatus(0);
                }
            }

            @Override
            public void onNegativeClick(View view) {
                dialog.dismiss();
                super.onNegativeClick(view);
                swGetCargoOrder.setChecked(true);
            }

            @Override
            public void onCloseDialog(View view) {
                super.onCloseDialog(view);
                dialog.dismiss();
            }

            @Override
            public void onConfirm(View view) {
                super.onConfirm(view);
                dialog.dismiss();
            }
        });
        dialog.setMessage("货拼客单会带来更多收益，关闭会降低收益，确定不听货拼客单？")
                .show(getSupportFragmentManager(), "");

        dialog.setPositiveText("确定");
        dialog.setNegativeText("取消");
    }
*/
    @Override
    protected OrderSettingContract.Presenter onCreatePresenter() {
        return new OrderSettingPresenter(this);
    }


    @Override
    public void updateSettingView(ListenOrderSetting setting) {
        if (setting == null) {
            Log.e(TAG, "读取听单配置为空");
            return;
        }
        listenOrderSetting = setting;
        LogUtils.d(TAG, "server setting:" + listenOrderSetting.toString());

        ListenOrderSetting localSetting = PrefserHelper.getOrderSettingInfo();
        if (localSetting != null) {
            LogUtils.d(TAG, "local setting:" + localSetting.toString());
        }

        if (setting.equals(localSetting)) {
            initViewClickAction();
        } else {
            PrefserHelper.saveOrderSettingInfo(setting);
            orderListenSettingDisplay(setting);

            if(setting.getOffWorkAddressTitle() != null){
                AddressInfo addressInfo = new AddressInfo();
                addressInfo.setName(setting.getOffWorkAddressTitle());
                addressInfo.setAddressDetail(setting.getOffWorkAddressDescription());
                addressInfo.setLatLng(new LatLng(setting.getOffWorkAddressLat(), setting.getOffWorkAddressLon()));
                updateBackHomeAddr(addressInfo);
            }

            initViewClickAction();
        }
    }

    private void orderListenSettingDisplay(ListenOrderSetting setting) {
        //是否播报
        if (setting.getVoiceBroadcast() == 1) {
            swAudioSetting.setChecked(true);
            PrefserHelper.setCache(PrefserHelper.KEY_ORDER_VOICE_ENABLE, "enable");
        } else {
            swAudioSetting.setChecked(false);
            PrefserHelper.setCache(PrefserHelper.KEY_ORDER_VOICE_ENABLE, "disable");
        }

        //是否只听收车单
        if (setting.getReceiveOnly() == 1) {
            swOnlyGetBackHomeOrder.setChecked(true);
        } else {
            swOnlyGetBackHomeOrder.setChecked(false);
        }
        //是否收听货拼客单
        //此司机是否开启了此功能
        //2019-11-6 不再显示货拼客单的按钮。
/*        if (setting.getCargoPassengerServiceOn() == 0) {
            llGetCargoOrder.setVisibility(View.GONE);
            lineGetCargoOrder.setVisibility(View.GONE);
        } else {
            llGetCargoOrder.setVisibility(View.VISIBLE);
            lineGetCargoOrder.setVisibility(View.VISIBLE);
        }*/

        if (setting.getCargoPassengerServiceStatus() == 1) {
            swGetCargoOrder.setChecked(true);
        } else {
            swGetCargoOrder.setChecked(false);
        }

        //是否拼單
        if (setting.getCarpool() == 0) {
            setTvOrderTypeCarpoolSelectDefault();
            tvOrderTypeAll.setTextColor(getResources().getColor(R.color.text_white));
            tvOrderTypeAll.setBackground(getResources().getDrawable(R.drawable.switch_btn_bg_enable));
        } else if (setting.getCarpool() == 1) {
            setTvOrderTypeCarpoolSelectDefault();
            tvOrderTypeCarpool.setTextColor(getResources().getColor(R.color.text_white));
            tvOrderTypeCarpool.setBackground(getResources().getDrawable(R.drawable.switch_btn_bg_enable));
        } else {
            setTvOrderTypeCarpoolSelectDefault();
            tvOrderTypeNoCarpool.setTextColor(getResources().getColor(R.color.text_white));
            tvOrderTypeNoCarpool.setBackground(getResources().getDrawable(R.drawable.switch_btn_bg_enable));
        }

        //听单范围
        clearDistanceSelect();
        double distance = setting.getReceivingRange();
        int id;
        if (distance >= 5) {
            id = R.id.order_distance_5;
        } else if (distance >= 4) {
            id = R.id.order_distance_4;
        } else if (distance >= 3.5) {
            id = R.id.order_distance_3_5;
        } else if (distance >= 3) {
            id = R.id.order_distance_3;
        } else if (distance >= 2) {
            id = R.id.order_distance_2;
        } else {
            id = R.id.order_distance_3_5;
        }
        TextView tv = findViewById(id);
        tv.setTextColor(getResources().getColor(R.color.text_white));
        tv.setBackground(getResources().getDrawable(R.drawable.switch_btn_bg_enable));
    }

    @Override
    public void updateBackHomeAddr(AddressInfo addr) {
        if (addr != null) {
            rlHaveAddress.setVisibility(View.VISIBLE);
            tvAddAddress.setVisibility(View.GONE);
            tvAddressName.setText(addr.getName());
            String addrDetail = addr.getAddressDetail();
            if (addrDetail.length() > 30) {
                addrDetail = addrDetail.substring(0, 30);
            }
            tvAddressDetail.setText(AMapUtil.getAddress(addrDetail));
        } else {
            rlHaveAddress.setVisibility(View.GONE);
            tvAddAddress.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void saveSettingSuccess() {
        finish();
    }

    /**
     * 为了得到传回的数据，必须在前面的Activity中（指MainActivity类）重写onActivityResult方法
     * <p>
     * requestCode 请求码，即调用startActivityForResult()传递过去的值
     * resultCode 结果码，结果码用于标识返回数据来自哪个新Activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 1 && intent != null) {
            AddressInfo addressInfo = intent.getParcelableExtra("backHomeAddress");
            if (addressInfo != null) {
                Log.d(TAG, "addressInfo:" + addressInfo.toString());
                listenOrderSetting.setOffWorkAddressTitle(addressInfo.getName());
                listenOrderSetting.setOffWorkAddressDescription(AMapUtil.getAddress(addressInfo.getAddressDetail()));
                listenOrderSetting.setOffWorkAddressLat(addressInfo.getLatLng().latitude);
                listenOrderSetting.setOffWorkAddressLon(addressInfo.getLatLng().longitude);
                updateBackHomeAddr(addressInfo);
//                presenter.setBackHomeAddr(addressInfo);
            }
        }
    }

}
