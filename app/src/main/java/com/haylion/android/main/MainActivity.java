package com.haylion.android.main;


import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.model.GuidePage;
import com.app.hubert.guide.model.RelativeGuide;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.gyf.immersionbar.ImmersionBar;
import com.haylion.android.R;
import com.haylion.android.activity.OrderCompleteActivity;
import com.haylion.android.activity.PreScanActivity;
import com.haylion.android.adapter.MainShunfengAdapter;
import com.haylion.android.common.Const;
import com.haylion.android.common.aibus_location.DeviceLocationManager;
import com.haylion.android.common.aibus_location.LocationListener;
import com.haylion.android.common.aibus_location.data.GpsData;
import com.haylion.android.common.map.AMapUtil;
import com.haylion.android.common.view.OrderCargoContainerView;
import com.haylion.android.common.view.dialog.DBaseDialog;
import com.haylion.android.common.view.dialog.DialogUtils;
import com.haylion.android.common.view.popwindow.VehiclePopUpWindow;
import com.haylion.android.constract.ItemClickListener;
import com.haylion.android.data.base.BaseActivity;
import com.haylion.android.data.base.BaseDialog;
import com.haylion.android.data.base.ConfirmDialog;
import com.haylion.android.data.bean.DateLenthBean;
import com.haylion.android.data.bean.ShunfengWaitBean;
import com.haylion.android.data.event.VehicleSyncEvent;
import com.haylion.android.data.model.AddressForSuggestLine;
import com.haylion.android.data.model.AmapTrack;
import com.haylion.android.data.model.Driver;
import com.haylion.android.data.model.EventMsg;
import com.haylion.android.data.model.LatestVersionBean;
import com.haylion.android.data.model.MessageDetail;
import com.haylion.android.data.model.NotificationData;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.model.OrderAbstract;
import com.haylion.android.data.model.OrderPayInfo;
import com.haylion.android.data.model.OrderTimeType;
import com.haylion.android.data.model.SwitchVehicleJudgeBean;
import com.haylion.android.data.model.Vehicle;
import com.haylion.android.data.model.WebsocketData;
import com.haylion.android.data.repo.OrderRepository;
import com.haylion.android.data.repo.PrefserHelper;
import com.haylion.android.data.util.BusinessUtils;
import com.haylion.android.data.util.LocationUtils;

import com.haylion.android.data.util.StatusBarUtils;
import com.haylion.android.data.util.TTSUtil;
import com.haylion.android.data.widgt.MyListView;
import com.haylion.android.mvp.rx.ApiSubscriber;
import com.haylion.android.mvp.util.LogUtils;
import com.haylion.android.mvp.util.SizeUtil;
import com.haylion.android.notification.NotificationListActivity;
import com.haylion.android.orderdetail.OrderDetailActivity;
import com.haylion.android.orderdetail.amapNavi.AMapNaviViewActivity;
import com.haylion.android.orderdetail.multiday.MultiDayDetailActivity;
import com.haylion.android.orderdetail.trip.CarpoolTripEndActivity;
import com.haylion.android.orderdetail.trip.TripDetailActivity;
import com.haylion.android.orderlist.AppointmentListActivity;
import com.haylion.android.orderlist.OrderListActivity;
import com.haylion.android.orderlist.achievement.AchievementListActivity;
import com.haylion.android.ordersetting.OrderSettingActivity;
import com.haylion.android.pay.PayMainActivity;
import com.haylion.android.service.AmapTrackUploadService;
import com.haylion.android.service.FloatDialogService;
import com.haylion.android.service.WebsocketService;
import com.haylion.android.service.WsCommands;
import com.haylion.android.updateApk.VersionDialog;
import com.haylion.android.user.money.IncomeActivity;
import com.haylion.android.user.setting.SettingActivity;
import com.haylion.android.user.shift.ShiftInfoActivity;
import com.haylion.android.user.vehicle.MyVehicleActivity;
import com.haylion.android.user.wallet.MyWalletActivity;
import com.haylion.android.utils.AmapUtils;
import com.haylion.android.utils.DateUtils;
import com.haylion.android.utils.SpUtils;
import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import zhy.com.highlight.HighLight;
import zhy.com.highlight.position.OnLeftPosCallback;
import zhy.com.highlight.shape.RectLightShape;

import static com.haylion.android.BuildConfig.APPLICATION_ID;
import static com.haylion.android.data.model.Order.ORDER_TYPE_SHUNFENG;
import static com.haylion.android.orderdetail.OrderDetailActivity.ORDER_ID;


/**
 * 听单页
 */
public class MainActivity extends BaseActivity<MainContract.Presenter>
        implements MainContract.View, OrderInfoAdapter.InnerItemOnclickListener,
        NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    @BindView(R.id.needOffsetView)
    View needOffsetView;
    @BindView(R.id.rl_title)
    RelativeLayout rlTitle;
    @BindView(R.id.tv_vehicle_selected)
    TextView tvVehicleNumberDisplay;

    /**
     * 听单概况
     */
    @BindView(R.id.tv_today_time)
    TextView tvTodayTime;
    @BindView(R.id.unitTd)
    TextView unitTd;
    @BindView(R.id.tv_today_time1)
    TextView tvTodayTime1;
    @BindView(R.id.unitTd1)
    TextView unitTd1;
    @BindView(R.id.tv_today_order_number)
    TextView tvTodayOrderNumber;
    @BindView(R.id.iv_toady_order_detail)
    ImageView ivTodayOrderDetail;
    @BindView(R.id.tv_today_order_income)
    TextView tvTodayOrderIncome;

    /**
     * 订单数据
     */
    @BindView(R.id.sl_order_list)
    NestedScrollView slOrderList;
    @BindView(R.id.ll_order_list_is_null)
    LinearLayout llOrderListIsNull;
    @BindView(R.id.rl_realtime_cargo_container)
    RelativeLayout rlRealtimeCargoContainer;
    @BindView(R.id.tv_realtime_cargo_header)
    TextView tvRealtimeCargoHeader;
    @BindView(R.id.tv_realtime_cargo_passenger_order)
    MyListView lvCargoPassenger;
    @BindView(R.id.rl_have_wait_pay_order)
    RelativeLayout rlHavaWaitPayOrder;
    @BindView(R.id.smartRefresh)
    SmartRefreshLayout smartRefresh;

    /**
     * 底部按钮
     */
    @BindView(R.id.btn_order_action)
    TextView tvListenOrderAction;

    /**
     * 侧栏
     */
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.rl_tips)
    RelativeLayout rlTips;
    @BindView(R.id.iv_msg)
    ImageView ivMsg;

    @BindView(R.id.tv_book_order)
    TextView mOrderPool;
    @BindView(R.id.iv_header)
    ImageView mUserCenter;
    @BindView(R.id.new_order_badge)
    TextView newOrderBadge;

    /**
     * 抽屉栏目信息
     */
    private CircleImageView ivUserPhoto;
    private TextView tvWorkNumber;
    private TextView tvDriverRealName;


    private VehiclePopUpWindow mVehiclePopWindow; //车辆切换弹窗
    private Vehicle selectedVehicle; //已选择车辆
    private int currentOrderListenStatus = 0; //听单状态
    private String suggest = ""; /*建议行驶线路*/

    private long restTimeToSendCargo = 0;

    public static final String PUSH_MESSAGE_DATA = "push_message_data";

    /**
     * 新版本轮询，据需求 #1156149801001003678
     */
    private Disposable mNewVersionPoller; // 新版本轮询
    private VersionDialog mVersionDialog; // 新版本对话框

    /**
     * 新的预约订单轮询
     */
    private Disposable mNewOrderPoller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtils.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        int overTimeOrderId = getIntent().getIntExtra("overTimeOrderId", 0);
        LogUtils.d(TAG, "overTimeOrderId:" + overTimeOrderId);

        setContentView(R.layout.activity_main_naviview);
        ButterKnife.bind(this);
        initView();
        EventBus.getDefault().register(this);
        /*启动后台的websocket &  全局悬浮窗服务*/
        startService();
        //展示听单的车辆信息
        updateSelectVehicle();
        //启动定时刷新今日成就
        handler.sendEmptyMessageDelayed(HANDLE_MSG_GET_TOADY_ACHIEVE, 10000);

        handler.sendEmptyMessageDelayed(HANDLE_PUSH_MSG, 1000);

        mNewOrderPoller = Observable.interval(0, 30, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(aLong -> {
            if (!FloatDialogService.isBackground(getContext()) &&
                    !FloatDialogService.isScreenOff(getContext())) { // 应用在前台且未锁屏
                presenter.queryNewOrder();
            }
        }, throwable -> LogUtils.e(TAG, "轮询新预约订单出错：" + throwable.getMessage()));

    }

    private void initView() {
        ImmersionBar.with(this)
                .fitsSystemWindows(false) //设置为true，图片无法置顶状态栏
//                .transparentStatusBar()
                .statusBarDarkFont(true, 1f)
                .titleBarMarginTop(needOffsetView)
                .init();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        //设置应用于菜单项图标的色调,设为null就可以显示原图标颜色
        navigationView.setItemIconTintList(null);

        // 因菜单取消抽屉设计，需要取消滑动呼出抽屉。2020/8/11
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        //侧滑栏的司机信息
        View headerView = navigationView.getHeaderView(0);
        tvWorkNumber = headerView.findViewById(R.id.work_number);
        tvDriverRealName = headerView.findViewById(R.id.driver_real_name);
        ivUserPhoto = headerView.findViewById(R.id.iv_user_photo);
        //状态栏置顶，要预留一定高度
        View navNeedOffsetView = headerView.findViewById(R.id.nav_needOffsetView);
        LinearLayout.LayoutParams statusParams = (LinearLayout.LayoutParams) navNeedOffsetView.getLayoutParams();
        statusParams.height = StatusBarUtils.getStatusBarHeight(this);
        navNeedOffsetView.setLayoutParams(statusParams);


        //下拉刷新
       /* PullToRefreshLayout pullToRefreshLayout = findViewById(R.id.ptr_order_refresh);
        pullToRefreshLayout.setRefreshListener(new BaseRefreshListener() {
            @Override
            public void refresh() {
                presenter.getTodayOrderList();
                presenter.getOrderList(); // 2020/6/12，出现过订单支付后“未支付”提示依然显示的情况
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 结束刷新
                        pullToRefreshLayout.finishRefresh();
                    }
                }, 1000);
            }

            @Override
            public void loadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 结束加载更多
                        pullToRefreshLayout.finishLoadMore();
                    }
                }, 1000);
            }
        });*/

        setupGuidePage();
        smartRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                presenter.getTodayAchieve();
                presenter.queryShunfengOrder();
            }
        });
    }

    private void setupGuidePage() {
        NewbieGuide.with(this).setLabel("label")
                .addGuidePage(
                        GuidePage.newInstance()
                                .addHighLight(mOrderPool, new RelativeGuide(
                                        R.layout.guide_page_1, Gravity.LEFT)
                                ).setEverywhereCancelable(true)
                ).addGuidePage(
                GuidePage.newInstance()
                        .addHighLight(mUserCenter, new RelativeGuide(
                                R.layout.guide_page_2, Gravity.BOTTOM)
                        ).setEverywhereCancelable(true)
        ).alwaysShow(false).show();
    }

    private List<Vehicle> vehicleList;

    /**
     * 显示规则：
     * a：如果绑定列表为空，显示“未绑定车辆”，不显示下拉按钮。
     * b：如果绑定列表不为空
     * 1）如果当前已有选中车辆，显示选中车辆。
     * 2）如果当前没有选中车辆，且列表只有一辆车，默认选中且显示这辆车。
     * 3）如果当前没有选中车辆，且列表有多辆车，显示“选择车辆”。
     * <p>
     * 切换规则：
     * 1）如果处于听单中，toast“请暂停听单后再切换车辆”
     * 2）切换判断交给后台
     */
    public void updateSelectVehicle() {
        LogUtils.d(TAG, "updateSelectVehicle()");
        //绑定的车辆列表
        vehicleList = PrefserHelper.getVehicleList();
        LogUtils.d(TAG, "vehicleList:" + vehicleList);

        //已经选中的车辆信息
        if (PrefserHelper.getVehicleInfo() != null) {
            selectedVehicle = PrefserHelper.getVehicleInfo();
        } else {
            selectedVehicle = null;
        }

        Drawable drawable = getResources().getDrawable(R.mipmap.ic_arrow_white_down);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        String tips;
        if (vehicleList == null || vehicleList.size() == 0) {
            //绑定列表为空
            tips = "未绑定车辆";
            tvVehicleNumberDisplay.setText(tips);
            tvVehicleNumberDisplay.setCompoundDrawables(null, null, null, null);

            // 2020/3/24，应产品要求，不再显示提示
//            boolean flag = PrefserHelper.getBoolean(PrefserHelper.KEY_VEHICLE_LIST_EMPTY_TIPS_IS_CLOSE);
//            rlTips.setVisibility(flag ? View.GONE : View.VISIBLE);
            return;
        } else {
//            rlTips.setVisibility(View.GONE);
            if (selectedVehicle == null) {
                tips = "选择车辆";
                tvVehicleNumberDisplay.setCompoundDrawables(null, null, drawable, null);
            } else {
                tips = selectedVehicle.getNumber();
            }
            tvVehicleNumberDisplay.setText(tips);
        }

        if (vehicleList.size() == 1) {
            PrefserHelper.saveVehicleInfo(vehicleList.get(0));
            selectedVehicle = vehicleList.get(0);
            tvVehicleNumberDisplay.setText(selectedVehicle.getNumber());
            tvVehicleNumberDisplay.setCompoundDrawables(null, null, null, null);
            tvVehicleNumberDisplay.setClickable(false);
            tvVehicleNumberDisplay.setEnabled(false);
        } else if (vehicleList.size() > 1) {
            LogUtils.d(TAG, "currentOrderListenStatus:" + currentOrderListenStatus);
            tvVehicleNumberDisplay.setCompoundDrawables(null, null, drawable, null);
            if (currentOrderListenStatus == 0) {
                tvVehicleNumberDisplay.setClickable(true);
                tvVehicleNumberDisplay.setEnabled(true);
            }
        }
        tvVehicleNumberDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentOrderListenStatus == 0) {
                    LogUtils.d(TAG, "currentOrderListenStatus:" + currentOrderListenStatus);
                } else {
                    toast(R.string.toast_please_stop_listening_and_switch_vehicles);
                    return;
                }
                if (selectedVehicle != null) { //当前车辆不为空，询问是否允许切换车辆
                    presenter.isSwitchVehicleAllowed(selectedVehicle.getId());
                } else {
                    showVehiclePopUpWindow(rlTitle);
                }
            }
        });
    }


    /**
     * 展示车辆选择的PopWindow
     *
     * @param v
     */
    private void showVehiclePopUpWindow(View v) {
        if (mVehiclePopWindow == null) {
            mVehiclePopWindow = new VehiclePopUpWindow(this);
            mVehiclePopWindow.setOnSelectListener(new VehiclePopUpWindow.OnSelectListener() {
                @Override
                public void onConfirm(int position) {
                    LogUtils.d(TAG, "onItemSelected, position: " + position);
                    updateSelectedVehicle(position);
                    if (mVehiclePopWindow != null) {
                        mVehiclePopWindow.dismiss();
                    }
                }
            });
        }
        mVehiclePopWindow.setData(vehicleList);
        mVehiclePopWindow.showAtLocation(drawerLayout, Gravity.BOTTOM, 0, 0);
        //mVehiclePopWindow.showAsDropDown(rlTitle, 0, 0);
        Drawable drawable = getResources().getDrawable(R.mipmap.ic_arrow_white_up);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        tvVehicleNumberDisplay.setCompoundDrawables(null, null, drawable, null);
        mVehiclePopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Drawable drawable = getResources().getDrawable(R.mipmap.ic_arrow_white_down);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                tvVehicleNumberDisplay.setCompoundDrawables(null, null, drawable, null);
            }
        });
    }

    /**
     * 更新选定的车辆信息
     *
     * @param position --选定的item position
     */
    public void updateSelectedVehicle(int position) {
        LogUtils.d(TAG, "updateSelectedVehicle, position: " + position);
        if (currentOrderListenStatus == 0) {
            selectedVehicle = vehicleList.get(position);
            tvVehicleNumberDisplay.setText(selectedVehicle.getNumber());
            PrefserHelper.saveVehicleInfo(selectedVehicle);
        } else {
            toast(R.string.toast_please_stop_listening_and_switch_vehicles);
        }
    }

    /**
     * 启动后台服务
     */
    public void startService() {
        //启动悬浮窗的后台服务
        Intent intent = new Intent(this, FloatDialogService.class);
        startService(intent);

        //启动websocket后台服务，用于数据推送
        intent = new Intent(this, WebsocketService.class);
        startService(intent);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        LogUtils.d(TAG, "onClick view id: " + id);
        Intent intent;
        switch (id) {
            case R.id.ll_money:
//                LogUtils.d(TAG, "start WalletHomeActivity");
//                intent = new Intent(this, WalletHomeActivity.class);
//                intent.putExtra("header", "钱包");
//                startActivity(intent);
                intent = new Intent(this, MyWalletActivity.class);
                startActivity(intent);
                break;

            case R.id.ll_income_list:
                LogUtils.d(TAG, "start IncomeActivity");
                intent = new Intent(this, IncomeActivity.class);
                intent.putExtra("header", "收益明细");
                startActivity(intent);
                break;
            case R.id.ll_history_order:
                LogUtils.d(TAG, "start OrderListActivity");
                OrderListActivity.start(this, OrderTimeType.HISTORY);
                break;
            case R.id.ll_history_achieve:
                LogUtils.d(TAG, "start AchievementListActivity");
                intent = new Intent(this, AchievementListActivity.class);
                intent.putExtra("header", "历史成绩");
                startActivity(intent);
                break;
            case R.id.ll_setting:
                intent = new Intent(this, SettingActivity.class);
                intent.putExtra("header", "设置");
                startActivity(intent);
                break;
            case R.id.ll_my_vehicle:
                MyVehicleActivity.start(getContext());
                break;
            case R.id.ll_shift_ino:
                ShiftInfoActivity.start(getContext());
                break;
            case R.id.ll_notification_list:
                intent = new Intent(this, NotificationListActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * @param
     * @return
     * @method
     * @description 获取首页的数据信息
     * @date: 2019/11/20 11:56
     * @author: tandongdong
     */
    private void getInitData() {
        presenter.getTodayAchieve();
        presenter.getTodayOrderList();
        presenter.getSuggestLine();
        presenter.getListenOrderStatus();//当前听单状态
        presenter.getDriverInfo();
        presenter.getAmapTrackArgs();
        presenter.getMessageList();
        presenter.getOrderList();
    }

    /**
     * 接受支付完成的消息推送
     *
     * @param websocketData
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(WebsocketData websocketData) {
        if (("orderPaid").equals(websocketData.getCmd())) {
            presenter.getOrderList();
        }
    }

    private static int lastMessageId = 0;

    @Override
    public void onResume() {
        LogUtils.d(TAG, "onResume");
        super.onResume();
        getInitData(); //获取页面数据

        startLocation(); //开启定位功能

        checkVehicleList();
        // 2020/3/23，先检查是否绑定车辆，
        // 随后再检查GPS设置，避免同时弹两个对话框
//        openGPSSettings();      //GPS功能是否已经打开。

        presenter.getServiceTelNumber(false);
        presenter.getListenOrderSetting(); //获取听单状态
        presenter.queryShunfengOrder();//获取顺丰听单列表
        //   presenter.checkUpdates();
    }

    private ConfirmDialog mNoVehicleDialog;

    private void checkVehicleList() {
        if (vehicleList == null || vehicleList.isEmpty()) {
            if (mNoVehicleDialog == null) {
                mNoVehicleDialog = ConfirmDialog.newInstance()
                        .setMessage(getString(R.string.tips_no_vehicle_bound_now))
                        .setOnClickListener(new ConfirmDialog.OnClickListener() {
                            @Override
                            public void onPositiveClick(View view) {
                                MyVehicleActivity.start(getContext());
                            }

                            @Override
                            public void onDismiss() {

                            }
                        })
                        .setPositiveText(getString(R.string.action_bound_vehicle))
                        .setType(ConfirmDialog.TYPE_ONE_BUTTON)
                        .setCancelOutside(false).setClickDismiss(true);
            }
            if (!mNoVehicleDialog.isShowing()) {
                mNoVehicleDialog.show(getSupportFragmentManager(), "");
            }

        } else {
            openGPSSettings();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mNewVersionPoller = Observable.interval(5, 60, TimeUnit.MINUTES).observeOn(AndroidSchedulers.mainThread()).subscribe(aLong -> {
            if (VersionDialog.isDownloading) {
                return;
            }
            presenter.checkUpdates();
        }, throwable -> LogUtils.e(TAG, "轮询新版本出错：" + throwable.getMessage()));
    }

    @Override
    public void onNewOrder(int newOrderAmount, String latestOrderTime) {
        if (newOrderAmount > 0) {
            if (newOrderAmount > 99) {
                newOrderBadge.setText("99+");
            } else {
                newOrderBadge.setText(String.valueOf(newOrderAmount));
            }
            newOrderBadge.setVisibility(View.VISIBLE);
        } else {
            newOrderBadge.setVisibility(View.GONE);
        }
        newOrderBadge.setTag(latestOrderTime); // 通过tag保存最新一个预约单的时间
    }

    @Override
    public void onShunfengOrders(List<Order> list) {
        smartRefresh.finishRefresh();
        RecyclerView mShunfengListview = findViewById(R.id.myListView_shunfeng_order);
        MainShunfengAdapter adapter = new MainShunfengAdapter(this, list, R.layout.main_shunfeng_list_item);
        mShunfengListview.setAdapter(adapter);
        adapter.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(Order order) {
                switch (order.getOrderStatus()) {
//                        待开始 = 0、待到店 = 1、待扫描=2、待取货签名=3、送货中=4、已完成=5
                    case 0:
                    case 1:
                    case 4:
                        OrderDetailActivity.go(getContext(), order.getOrderId(), ORDER_TYPE_SHUNFENG);
                        break;
                    case 2:
                    case 3:
                        PreScanActivity.go(getContext(), order.getOrderId());
                        break;
                    case 5:
                        OrderCompleteActivity.go(getContext(), order.getOrderId(), 2);
                        break;
                }
            }
        });
        if (list != null && list.size() > 0) {
            llOrderListIsNull.setVisibility(View.GONE);
        }else {
            llOrderListIsNull.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mNewVersionPoller != null && !mNewVersionPoller.isDisposed()) {
            mNewVersionPoller.dispose();
            mNewVersionPoller = null;
        }
    }

    /**
     * @param
     * @return
     * @method
     * @description 处理推送消息
     * @date: 2019/12/10 15:33
     * @author: tandongdong
     */
    private void handlePushMsg() {
        Log.d(TAG, "handlePushMsg");
        //判断是否从系统推送消息进入的页面
        NotificationData notificationData = getIntent().getParcelableExtra(PUSH_MESSAGE_DATA);
        if (notificationData != null && notificationData.getMessageId() != lastMessageId) {
            Log.d(TAG, "notificationData:" + notificationData.toString());
            lastMessageId = notificationData.getMessageId();

            //上报消息已读,再跳转页面
            MessageDetail messageDetail = new MessageDetail();
            List<MessageDetail> messageDetails = new ArrayList<>();
            messageDetail.setViewed(1);
            messageDetail.setDisplay(1);
            messageDetail.setDeleted(0);
            messageDetail.setOrderId(notificationData.getOrderId());
            messageDetail.setMessageId(notificationData.getMessageId());
            messageDetail.setCmdSn(notificationData.getMessageType());
            Log.d(TAG, "messageDetail：" + messageDetail.toString());
            messageDetails.add(messageDetail);
            presenter.messageHasReaded(messageDetails);
        }
    }

    @Override
    public void enterNewActivity(MessageDetail messageDetail) {
        //判断是否从系统推送消息进入的页面
        NotificationData notificationData = new NotificationData();
        notificationData.setMessageType(messageDetail.getCmdSn());
        notificationData.setOrderId(messageDetail.getOrderId());
        notificationData.setMessageId(messageDetail.getMessageId());

        Log.d(TAG, "notificationData:" + notificationData.toString());

        if (notificationData.getMessageType() == WsCommands.DRIVER_ORDER_PAID.getSn()) {
            //订单已支付
            TripDetailActivity.go(this, notificationData.getOrderId(), false);
        } else if (notificationData.getMessageType() == WsCommands.DRIVER_ORDER_CANCEL_BY_USER.getSn()
                || notificationData.getMessageType() == WsCommands.DRIVER_ORDER_CANCEL_BY_SYSTEM.getSn()) {
            //订单取消
            TripDetailActivity.go(this, notificationData.getOrderId(), false);
        } else if (notificationData.getMessageType() == WsCommands.DRIVER_UNPAID_ORDER_TO_CONTROVERSY_ORDER.getSn()) {
            //订单争议
            TripDetailActivity.go(this, notificationData.getOrderId(), false);
        } else if (notificationData.getMessageType() == WsCommands.DRIVER_ORDER_DESTINATION_CHANGED.getSn()) {
            //不是订单的终态，需要根据当前订单的状态进行跳转。
            presenter.getOrderDetailFromNotify(notificationData.getOrderId());
        }
    }

    /**
     * 从通知栏获取到订单id，去请求订单详情
     *
     * @param order
     */
    @Override
    public void getOrderDetailFromNotifySuccess(Order order) {
        Log.d(TAG, "getOrderDetailSuccess:" + order.toString());
        if (order.getOrderStatus() == Order.ORDER_STATUS_ARRIVED_START_ADDR
                || order.getOrderStatus() == Order.ORDER_STATUS_GET_ON
                || order.getOrderStatus() == Order.ORDER_STATUS_WAIT_CAR) {
            OrderDetailActivity.go(this, order.getOrderId());
        } else {
            toast(getString(R.string.message_expired));
        }
    }

    /**
     * 是否允许更换听单车辆
     *
     * @param bean
     */
    @Override
    public void isSwitchVehicleAllowedSuccess(SwitchVehicleJudgeBean bean) {
        if (bean.getAllowed() == 0) { //不允许
            if (bean.getReason() == 1) {
                toast(R.string.toast_please_stop_listening_and_switch_vehicles);
            } else if (bean.getReason() == 2) {
                toast(R.string.toast_please_complete_order_and_switch_vehicles);
            } else {
                toast("不允许切换车辆，原因未知");
            }
        } else { //允许
            showVehiclePopUpWindow(rlTitle);
        }
    }

    @Override
    public void isSwitchVehicleAllowedFail() {

    }


    @Override
    public void checkUpdatesSuccess(LatestVersionBean version) {
        if (mVersionDialog != null && mVersionDialog.isShowing()) {
            mVersionDialog.dismiss();
        }
        mVersionDialog = new VersionDialog(getContext(), 2);
        mVersionDialog.setData(version);
        mVersionDialog.show();
    }

    @Override
    public void haveWaitPayOrder(boolean have) {
        rlHavaWaitPayOrder.setVisibility(have ? View.VISIBLE : View.GONE);
    }


    @Override
    protected MainContract.Presenter onCreatePresenter() {
        return new MainPresenter(this);
    }

    /**
     * @param
     * @return
     * @method
     * @description 展示今日成就
     * @date: 2019/11/26 10:26
     * @author: tandongdong
     */
    @Override
    public void showTodayAchieve(OrderAbstract orderAbstract) {
        smartRefresh.finishRefresh();
        if (orderAbstract == null) {
            tvTodayTime.setText("0");
            tvTodayOrderNumber.setText("0");
            ivTodayOrderDetail.setVisibility(View.INVISIBLE);
            tvTodayOrderIncome.setText("0");
            return;
        }
        DateLenthBean timeLenth = DateUtils.getTimeLenth(orderAbstract.getOnlineTime() * 1000);
        tvTodayTime.setText(timeLenth.getTime() + "");
        unitTd.setText(timeLenth.getUnit());
        if (timeLenth.getTime1() != 0) {
            tvTodayTime1.setText(timeLenth.getTime1() + "");
            if (timeLenth.getUnit1() != null) {
                unitTd1.setText(timeLenth.getUnit1() + "");
            }
        }
        tvTodayOrderNumber.setText("" + orderAbstract.getOrderCompletionCount());
        if (orderAbstract.getOrderCompletionCount() == 0) {
            ivTodayOrderDetail.setVisibility(View.INVISIBLE);
        } else {
            ivTodayOrderDetail.setVisibility(View.VISIBLE);
        }
        tvTodayOrderIncome.setText("" + BusinessUtils.moneySpec(orderAbstract.getIncome()));
    }

    /**
     * @param
     * @return
     * @method
     * @description 所有订单列表的展示
     * @date: 2019/11/26 10:27
     * @author: tandongdong
     */
    @Override
    public void updateTodayOrderListDisplay(List<Order> passengerList, List<Order> cargoList) {
        if ((passengerList != null && passengerList.size() != 0) || (cargoList != null && cargoList.size() != 0)) {
            slOrderList.setVisibility(View.VISIBLE);
            llOrderListIsNull.setVisibility(View.INVISIBLE);
        } else {
            slOrderList.setVisibility(View.INVISIBLE);
            llOrderListIsNull.setVisibility(View.VISIBLE);
            return;
        }

        updateTodayCargoOrderListDisplay(cargoList); //更新货单

        updateTodayOrderDisplay(passengerList); //更新客单
    }

    OrderInfoAdapter adapter;

    /**
     * @param
     * @return
     * @method
     * @description 展示客单（除去货单和货拼客单以外的单），展示顺序：实时单，实时拼车，货单，货拼客单，送小孩单，顺丰单
     * @date: 2019/11/26 10:06
     * @author: tandongdong
     */
    public void updateTodayOrderDisplay(List<Order> orderList) {
/*        if(orderList == null){
            return;
        }*/
        //小孩单以外的客单
        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < orderList.size(); i++) {
            int orderType = orderList.get(i).getOrderType();
            if (orderType == Order.ORDER_TYPE_REALTIME
                    || orderType == Order.ORDER_TYPE_REALTIME_CARPOOL
                    || orderType == Order.ORDER_TYPE_BOOK
                    || orderType == Order.ORDER_TYPE_ACCESSIBILITY) // 无障碍
            {
                orders.add(orderList.get(i));
            }
        }
        //扩展listview
        MyListView myLvOrder = findViewById(R.id.myListView_order);
        adapter = new OrderInfoAdapter(this, orders);
        adapter.setSuggestLine(suggest);
        adapter.setItemClickListener(this);
        myLvOrder.setAdapter(adapter);

        //小孩单
        List<Order> childOrders = new ArrayList<>();
        for (int i = 0; i < orderList.size(); i++) {
            int orderType = orderList.get(i).getOrderType();
            if (orderType == Order.ORDER_TYPE_SEND_CHILD) {
                childOrders.add(orderList.get(i));
            }
        }
        MyListView myLvChildOrder = findViewById(R.id.myListView_child_order);
        adapter = new OrderInfoAdapter(this, childOrders);
        adapter.setItemClickListener(this);
        myLvChildOrder.setAdapter(adapter);

    }

    //货单
    @BindView(R.id.iv_time)
    ImageView ivTime;
    @BindView(R.id.tv_cargo_start_address)
    TextView tvCargoStartAddress;
    @BindView(R.id.tv_cargo_start_address_detail)
    TextView tvCargoStartAddressDetail;
    @BindView(R.id.tv_cargo_end_address)
    TextView tvCargoEndAddress;
    @BindView(R.id.tv_cargo_end_address_detail)
    TextView tvCargoEndAddressDetail;
    @BindView(R.id.rl_order_cargo_container)
    OrderCargoContainerView rlOrderCargoContainer;
    @BindView(R.id.tv_time_to_arrive_destination)
    TextView tvTimeShouldArriveDestination;

    List<Order> passengerList = new ArrayList<>();
    List<Order> cargoList = new ArrayList<>();

    /**
     * @param
     * @return
     * @method
     * @description 货单和货拼客单展示
     * @date: 2019/11/26 10:09
     * @author: tandongdong
     */
    @Override
    public void updateTodayCargoOrderListDisplay(List<Order> list) {
        List<Order> passengerList = new ArrayList<>();
        List<Order> cargoList = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            LogUtils.d(TAG, "index: " + i + ", type: " + list.get(i).getOrderType());
            if (list.get(i).getOrderType() == Order.ORDER_TYPE_CARGO_PASSENGER) {
                passengerList.add(list.get(i));
            } else if (list.get(i).getOrderType() == Order.ORDER_TYPE_CARGO) {
                cargoList.add(list.get(i));
            }
            this.cargoList = cargoList;
            this.passengerList = passengerList;
        }

        if (cargoList.size() > 0) {
            LogUtils.d(TAG, "cargoId:" + cargoList.get(0).getOrderId());
            //将货物ID存起来
            PrefserHelper.setCache(PrefserHelper.KEY_GOODS_ORDER_ID, String.valueOf(cargoList.get(0).getOrderId()));
        }

        if (list.size() == 0) {
            rlRealtimeCargoContainer.setVisibility(View.GONE);
        } else {
            rlRealtimeCargoContainer.setVisibility(View.VISIBLE);
        }

/*        //header显示，有客单，则显示货拼客单；没有则显示货单
        if (passengerList.size() <= 0) {
            tvRealtimeCargoHeader.setText("货单");
        } else {
            tvRealtimeCargoHeader.setText("货拼客");
        }*/

        //乘客单
        if (passengerList.size() == 0) {
            lvCargoPassenger.setVisibility(View.GONE);
        } else {
            lvCargoPassenger.setVisibility(View.VISIBLE);
            //扩展listview
            OrderInfoAdapter adapter = new OrderInfoAdapter(this, passengerList);
            adapter.setItemClickListener(this);
            lvCargoPassenger.setAdapter(adapter);
        }

        //货物单
        if (cargoList.size() != 0) {
            Order cargoOrder;
            cargoOrder = cargoList.get(0);
            tvCargoStartAddress.setText(cargoOrder.getStartAddr().getName());
            tvCargoStartAddressDetail.setText(AMapUtil.getAddress(cargoOrder.getStartAddr().getAddressDetail()));
            tvCargoStartAddressDetail.setVisibility(View.GONE);

            tvCargoEndAddress.setText(cargoOrder.getEndAddr().getName());
            tvCargoEndAddressDetail.setText(AMapUtil.getAddress(cargoOrder.getEndAddr().getAddressDetail()));

            //时间显示
            ImageView ivTime = findViewById(R.id.iv_time);
            TextView timeHour = findViewById(R.id.tv_time_hour);
            TextView timeDate = findViewById(R.id.tv_time_date);
            //小时和分
            if (cargoOrder.getOrderTime().length() > 11) {
                timeHour.setText(cargoOrder.getOrderTime().substring(11));
            }

            //日期
            String orderTime = cargoOrder.getOrderTime();
            String dateString = "2019-12-01";
            try {
                long milliSecond = BusinessUtils.stringToLong(orderTime, "yyyy-MM-dd HH:mm");
                dateString = BusinessUtils.getDateString(milliSecond, null);
                LogUtils.d(TAG, "" + "dateString: " + dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            timeDate.setText(dateString);

            ImaginaryLineView ivCargoStartAddrDetail = findViewById(R.id.iv_cargo_start_address_detail);
            timeDate.setVisibility(View.GONE);
            timeHour.setVisibility(View.GONE);
            if (passengerList.size() <= 0) {
//                ivTime.setVisibility(View.VISIBLE);
                ivCargoStartAddrDetail.setVisibility(View.INVISIBLE);
            } else {
//                ivTime.setVisibility(View.INVISIBLE);
                ivCargoStartAddrDetail.setVisibility(View.VISIBLE);
                ivCargoStartAddrDetail.setLineAttribute(0xFFB3B3B3, SizeUtil.dp2px(5), new float[]{20, 20, 20, 20});
            }

            //计算剩余多少时间内必须到达送货点
            String estimateArriveTimeStr = cargoOrder.getEstimateArriveTime();
            Date estimateArriveTime = new Date(System.currentTimeMillis());

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            try {
                estimateArriveTime = dateFormat.parse(estimateArriveTimeStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Date current = new Date(System.currentTimeMillis());
//            long deltaSeconds = (estimateArriveTime.getTime() - current.getTime()) > 0 ? (estimateArriveTime.getTime() - current.getTime()) / 1000 : 0;
            long deltaSeconds = (estimateArriveTime.getTime() - current.getTime()) / 1000;

            //启动定时刷新今日成就
            restTimeToSendCargo = deltaSeconds; //保存剩余时间，用于倒计时计数。
            handler.sendEmptyMessageDelayed(HANDLE_MSG_GET_TOADY_ACHIEVE, 10 * 1000);

            if (deltaSeconds > 0) {
                tvTimeShouldArriveDestination.setText(BusinessUtils.formatEstimateTimeText(deltaSeconds, BusinessUtils.PrecisionMode.MINUTE) + "内送达");
            } else {
                tvTimeShouldArriveDestination.setText("已超时 " + BusinessUtils.formatEstimateTimeText(-deltaSeconds, BusinessUtils.PrecisionMode.MINUTE) + "");
                tvTimeShouldArriveDestination.setTextColor(getResources().getColor(R.color.maas_text_orange));
            }
        } else {
            LogUtils.d(TAG, "货单为空");
        }

        //货单信息的范围
        rlOrderCargoContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargoOrderClick();
            }
        });
        rlOrderCargoContainer.setInterceptTouchEvent(true);
        /*lvCargoPassenger.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cargoOrderClick();
            }
        });*/
    }

    /**
     * @param
     * @return
     * @method
     * @description 货单，更新剩余多少时间去送货。
     * @date: 2020/1/17 11:20
     * @author: tandongdong
     */
    private void updateRestTimeToSendCargoTimer(Order cargoOrder) {
        //计算剩余多少时间内必须到达送货点
        String estimateArriveTimeStr = cargoOrder.getEstimateArriveTime();
        Date estimateArriveTime = new Date(System.currentTimeMillis());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            estimateArriveTime = dateFormat.parse(estimateArriveTimeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date current = new Date(System.currentTimeMillis());
//            long deltaSeconds = (estimateArriveTime.getTime() - current.getTime()) > 0 ? (estimateArriveTime.getTime() - current.getTime()) / 1000 : 0;
        long deltaSeconds = (estimateArriveTime.getTime() - current.getTime()) / 1000;

        restTimeToSendCargo = deltaSeconds;

        if (deltaSeconds > 0) {
            tvTimeShouldArriveDestination.setText(BusinessUtils.formatEstimateTimeText(deltaSeconds, BusinessUtils.PrecisionMode.MINUTE) + "内送达");
        } else {
            tvTimeShouldArriveDestination.setText("已超时 " + BusinessUtils.formatEstimateTimeText(-deltaSeconds, BusinessUtils.PrecisionMode.MINUTE) + "");
            tvTimeShouldArriveDestination.setTextColor(getResources().getColor(R.color.maas_text_orange));
        }
    }


    public void cargoOrderClick() {
        //先接货
        if (cargoList.get(0).getOrderStatus() == Order.ORDER_STATUS_WAIT_CAR) {
            presenter.getOrderDetail(cargoList.get(0).getOrderId());
            return;
        }

        //有乘客单，则先送乘客
        for (int i = 0; i < passengerList.size(); i++) {
            if (passengerList.get(i).getOrderStatus() != Order.ORDER_STATUS_CLOSED
                    && passengerList.get(i).getOrderStatus() != Order.ORDER_STATUS_WAIT_PAY) {
                presenter.getOrderDetail(passengerList.get(i).getOrderId());
                return;
            }
        }

        //最后去送货
        if (cargoList.get(0).getOrderStatus() != Order.ORDER_STATUS_CLOSED) {
            presenter.getOrderDetail(cargoList.get(0).getOrderId());
        }
    }

    private static final int HANDLE_MSG_IF_SHOW_TRANSFOR_BUTTION = 100 + 1;
    private static final int HANDLE_MSG_GET_TOADY_ACHIEVE = 100 + 2;
    private static final int HANDLE_PUSH_MSG = 100 + 3;
    private static final int HANDLE_UPDATE_CARGO_ORDER_REST_TIME = 100 + 4;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 100: {
                    int viewId = msg.getData().getInt("viewId");
                    showNextKnownTipView(null, viewId);
                    //获取配置
                    break;
                }
                case HANDLE_MSG_GET_TOADY_ACHIEVE:
                    presenter.getTodayAchieve();
                    handler.removeMessages(HANDLE_MSG_GET_TOADY_ACHIEVE);
                    handler.sendEmptyMessageDelayed(HANDLE_MSG_GET_TOADY_ACHIEVE, 60000);
                    break;
                case HANDLE_MSG_IF_SHOW_TRANSFOR_BUTTION:
                    handler.removeMessages(HANDLE_MSG_IF_SHOW_TRANSFOR_BUTTION);
                    handler.sendEmptyMessageDelayed(HANDLE_MSG_IF_SHOW_TRANSFOR_BUTTION, 60000);
                    break;
                case HANDLE_UPDATE_CARGO_ORDER_REST_TIME:
                    restTimeToSendCargo = restTimeToSendCargo - 60;
                    if (restTimeToSendCargo > 0) {
                        tvTimeShouldArriveDestination.setText(BusinessUtils.formatEstimateTimeText(restTimeToSendCargo, BusinessUtils.PrecisionMode.MINUTE) + "内送达");
                    } else {
                        tvTimeShouldArriveDestination.setText("已超时 " + BusinessUtils.formatEstimateTimeText(-restTimeToSendCargo, BusinessUtils.PrecisionMode.MINUTE) + "");
                    }
                    handler.removeMessages(HANDLE_UPDATE_CARGO_ORDER_REST_TIME);
                    handler.sendEmptyMessageDelayed(HANDLE_UPDATE_CARGO_ORDER_REST_TIME, 60 * 1000);
                    break;
                case HANDLE_PUSH_MSG:
                    handlePushMsg();
                    handler.removeMessages(HANDLE_PUSH_MSG);
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    public void changeOrderActionSuccess(int status, boolean showDialog) {
        LinearLayout llListenOrder = findViewById(R.id.ll_listen_order);
        LinearLayout llListenOrderStatus = findViewById(R.id.ll_listen_order_status);
//        RelativeLayout rlBottom = findViewById(R.id.rl_bottom);
        TextView tvStatus = findViewById(R.id.tv_order_status);
        currentOrderListenStatus = status;
        if (status == 0) {
            tvListenOrderAction.setText("开始听单");
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(SizeUtil.dp2px(10), 0, 0, 0);
            lp.addRule(RelativeLayout.RIGHT_OF, R.id.ll_order_setting);
            lp.addRule(RelativeLayout.CENTER_VERTICAL);
            llListenOrder.setLayoutParams(lp);
            llListenOrderStatus.setVisibility(View.GONE);
            presenter.getTodayAchieve();
        } else if (status == 1) {
            tvListenOrderAction.setText("休息");
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.addRule(RelativeLayout.ALIGN_PARENT_END);
            lp.addRule(RelativeLayout.CENTER_VERTICAL);

            llListenOrder.setLayoutParams(lp);
            llListenOrderStatus.setVisibility(View.VISIBLE);

            if (showDialog) {
                showDialogWhenStartListenOrder();
            }
        }

        //更新车辆选择显示
        updateSelectVehicle();

        PrefserHelper.setCache(PrefserHelper.KEY_ORDER_LISTEN_STATUS, String.valueOf(status));

    }

    @Override
    public void updateListenOrderType(boolean backhomeOrder) {
        TextView tvStatus = findViewById(R.id.tv_order_status);
        if (backhomeOrder) {
            tvStatus.setText("听收车单");
        } else {
            tvStatus.setText("听单中");
        }
    }

    /**
     * 显示建议的线路（只在实时拼车的情况下展示）
     *
     * @param list
     */
    @Override
    public void showSuggestLine(List<AddressForSuggestLine> list) {
        if (list == null) {

        } else {
            suggest = "";
            for (int i = 0; i < list.size(); i++) {
                suggest = suggest + list.get(i).getLocationName();
                if (i < list.size() - 1) {
                    suggest = suggest + " --> ";
                }
            }
            if (adapter != null) {
                adapter.setSuggestLine(suggest);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @OnClick({R.id.btn_order_action, R.id.iv_header, R.id.order_setting, R.id.ll_order_setting, R.id.ll_listen_order, R.id.rl_today_order_container,
            R.id.ll_order_list_is_null, R.id.iv_msg, R.id.iv_tips_close, R.id.rl_have_wait_pay_order, R.id.tv_book_order})
    public void onButtonClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.iv_tips_close:
                PrefserHelper.setBoolean(PrefserHelper.KEY_VEHICLE_LIST_EMPTY_TIPS_IS_CLOSE, true);
                rlTips.setVisibility(View.GONE);
                break;
            case R.id.iv_header:
//                drawerLayout.openDrawer(Gravity.LEFT);
                // 2020.6.28，修改为打开单独的菜单页面
                startActivity(new Intent(getContext(), MainMenuActivity.class));
                break;
            case R.id.iv_msg: //系统消息
                intent = new Intent(this, NotificationListActivity.class);
                startActivity(intent);
                break;
            case R.id.order_setting:
            case R.id.ll_order_setting:
                intent = new Intent(this, OrderSettingActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_today_order_container: //今日完成客单
                TextView textView = findViewById(R.id.tv_today_order_number);
                if (!("0").equals(textView.getText().toString())) {
                    OrderListActivity.start(this, OrderTimeType.TODAY);
                }
                break;
            case R.id.btn_order_action:
            case R.id.ll_listen_order:
                //开始听单，停止听单
                startListenOrder();
                break;
            case R.id.ll_order_list_is_null:
                presenter.getTodayOrderList();
                break;
            case R.id.rl_have_wait_pay_order:
                //待支付订单
                OrderListActivity.start(this, OrderTimeType.HISTORY);
                break;
            case R.id.tv_book_order:
                String latestOrderTime = (String) newOrderBadge.getTag();
                if (!TextUtils.isEmpty(latestOrderTime)) {
                    LogUtils.d("查看预约单，保存最新预约单的时间：" + latestOrderTime);
                    PrefserHelper.saveNewOrderTime(latestOrderTime);
                }
                newOrderBadge.setVisibility(View.GONE);
//                BookOrderListActivity.start(this, OrderTimeType.TODAY);
                AppointmentListActivity.start(this);
                break;
            default:
                break;
        }
    }

    /**
     * @param
     * @return
     * @method
     * @description 开始听单
     * @date: 2019/11/21 15:41
     * @author: tandongdong
     */
    public void startListenOrder() {
        if (currentOrderListenStatus == 0) {
            //开始听单
            if (PrefserHelper.getVehicleList() == null || PrefserHelper.getVehicleList().size() == 0) {
                toast(R.string.toast_not_bind_vehicle);
                return;
            }
            if (PrefserHelper.getVehicleInfo() == null || PrefserHelper.getVehicleInfo().getId() == 0) {
                toast(R.string.toast_please_select_vehicle);
                return;
            }
            presenter.changeListenOrderStatus(true);
            //showDialogWhenStartListenOrder();
        } else {
            TTSUtil.playVoice("结束听单");
            presenter.changeListenOrderStatus(false);
        }
    }

    /**
     * 模拟发送消息，调试用
     */
    public void mockMessage() {
        LogUtils.d(TAG, "mockMessage");
/*
        WebsocketOrderInfo wsOrderInfo = new WebsocketOrderInfo();
        wsOrderInfo.setOrderId(1000000768);
        wsOrderInfo.setVehicleId(104);
        wsOrderInfo.setOldAddress("旧地址描述");
        wsOrderInfo.setOldPlace("旧地址");

        WebsocketData websocketData = new WebsocketData(WsCommands.DRIVER_VEHICLE_DISABLE.getSn(),
                "", "", wsOrderInfo);
        LogUtils.d(TAG, "" + wsOrderInfo.toString());
        EventBus.getDefault().post(websocketData);
*/

        isServiceRunning(this, APPLICATION_ID + ".service.AmapTrackUploadService");
    }

    @Override
    public void itemClick(int orderId, boolean cargoPassenger) {
        if (cargoPassenger) {
            cargoOrderClick();
            return;
        }
        presenter.getOrderDetail(orderId);
    }

    @Override
    public void itemClick(Order order) {
        if (order.isParentOrder() || order.getOrderType() == ORDER_TYPE_SHUNFENG) {
            Intent intent = new Intent(MainActivity.this, MultiDayDetailActivity.class);
            intent.putExtra(MultiDayDetailActivity.ORDER_ID, order.getOrderCode());
            startActivity(intent);
        }
    }

    @Override
    public void onCarpoolOrderClick(Order carpoolOrder) {
        // 复用多日订单页面展示拼单信息
        MultiDayDetailActivity.go(getContext(), carpoolOrder.getCarpoolCode(), true);
    }

    /**
     * 点击转单，弹出拨打电话功能
     */
    @Override
    public void transforOrder() {
        presenter.getServiceTelNumber(true);
    }

    @Override
    public void onGetDriverInfoSuccess(Driver driver) {
        tvWorkNumber.setText(driver.getCode());
        tvDriverRealName.setText(driver.getLastName() + driver.getFirstName());
        // tvVehicleNumber.setText(driver.getPlateNumber());
        //头像信息
        if (driver.getPhotoUrl() != null && !("").equals(driver.getPhotoUrl())) {
            Glide.with(this).load(driver.getPhotoUrl()).into(ivUserPhoto);
        }
    }

    @Override
    public void showDialog() {
        LogUtils.d(TAG, "showDialog");
        BaseDialog dialog = new BaseDialog(this, R.style.Translucent_NoTitle);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_vehicle_is_running, null);
        dialog.setContentView(view);
        TextView tvConfirm = view.findViewById(R.id.tv_confirm);
        //操作
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void showVehcileIsStoppedDialog(String vehicleNum) {
        LogUtils.d(TAG, "showVehcileIsStoppedDialog");
        Dialog dialog = new Dialog(this, R.style.Translucent_NoTitle);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_vehicle_is_stopped, null);
        TextView tips = view.findViewById(R.id.tv_tips);
        tips.setText("您绑定的车辆 " + vehicleNum + " 已停用，请选择其他车辆");

        Button btnIKnow = view.findViewById(R.id.btn_i_know);
        dialog.show();
        dialog.getWindow().setContentView(view);

        //操作
        btnIKnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    @Override
    public void startAmapTrackService(AmapTrack amapTrack) {
        Intent intent = new Intent(this, AmapTrackUploadService.class);
        intent.putExtra("sid", amapTrack.getSid());
        intent.putExtra("tid", amapTrack.getTid());
        intent.putExtra("trid", amapTrack.getTrid());
        startService(intent);
    }

    /**
     * 小孩订单，还需要判断子状态和主状态
     *
     * @param order
     */
    @Override
    public void getOrderDetailSuccess(Order order) {

        if (order.isParentOrder()) {
            Intent intent = new Intent(MainActivity.this, MultiDayDetailActivity.class);
            intent.putExtra(MultiDayDetailActivity.ORDER_ID, order.getOrderCode());
            startActivity(intent);
        }

        //货单 or 货拼客单 ，在此页面不刷新数据情况下，可能出现货单or货拼客单，已完成的情况，此时是要跳到行程详情页
        if ((order.getOrderType() == Order.ORDER_TYPE_CARGO_PASSENGER || order.getOrderType() == Order.ORDER_TYPE_CARGO)
                && order.getOrderStatus() == Order.ORDER_STATUS_CLOSED) {
            TripDetailActivity.go(this, order.getOrderId(), true, true);
            return;
        }

        // 2020/3/26，预约订单，跳到单独的详情页面，#1003749
//        if (order.getOrderType() == Order.ORDER_TYPE_BOOK) {
//            AppointmentDetailsActivity.start(getContext(), order.getOrderId());
//            return;
//        }

        //根据订单状态进入不同的页面
        if ((order.getOrderStatus() == Order.ORDER_STATUS_WAIT_PAY ||
                order.getOrderStatus() == Order.ORDER_STATUS_GET_OFF)) {

            if (order.getOrderType() == Order.ORDER_TYPE_REALTIME_CARPOOL
                    || order.getOrderType() == Order.ORDER_TYPE_REALTIME
                    || order.getOrderType() == Order.ORDER_TYPE_BOOK) {

                if (order.getOrderStatus() == Order.ORDER_STATUS_WAIT_PAY) {
                    TripDetailActivity.go(getContext(), order.getOrderId(), true);
                } else {
                    OrderPayInfo orderPayInfo = new OrderPayInfo();
                    orderPayInfo.setCarpoolOrder(order.isCarpoolOrder());
                    orderPayInfo.setOrderId(order.getOrderId());
                    orderPayInfo.setCarpoolCode(order.getCarpoolCode());
                    orderPayInfo.setOrderType(order.getOrderType());
                    orderPayInfo.setOrderChannel(order.getChannel());
                    orderPayInfo.setCost(order.getTotalMoney());
                    PayMainActivity.start(this, orderPayInfo);
                }

            } else if (order.getOrderType() == Order.ORDER_TYPE_CARGO_PASSENGER) {
                if (order.getOrderStatus() == Order.ORDER_STATUS_GET_OFF) {
                    Intent intent = new Intent(MainActivity.this, OrderDetailActivity.class);
                    intent.putExtra(ORDER_ID, order.getOrderId());

                    if (cargoList != null && cargoList.size() > 0) {
                        intent.putExtra(OrderDetailActivity.CARGO_ORDER_ID, cargoList.get(0).getOrderId()); //对应的货单ID
                        LogUtils.d(TAG, "orderId:" + order.getOrderId() + "cargoId:" + cargoList.get(0).getOrderId());

                        //将货物ID存起来
                        PrefserHelper.setCache(PrefserHelper.KEY_GOODS_ORDER_ID, String.valueOf(cargoList.get(0).getOrderId()));
                    }
                    startActivity(intent);
                } else {
                    //货拼客单，乘客待支付，进入行程详情页，且显示按钮
                    TripDetailActivity.go(this, order.getOrderId(), true, true);
                }

            } else if (order.getOrderType() == Order.ORDER_TYPE_CARGO) {
                OrderDetailActivity.go(this, order.getOrderId());

            } else if (order.getOrderType() == Order.ORDER_TYPE_SEND_CHILD) {
                if (order.getOrderSubStatus() == Order.ORDER_SUB_STATUS_GET_OFF_PARENT_CONFIRMED) {
                    OrderPayInfo orderPayInfo = new OrderPayInfo();
                    orderPayInfo.setCarpoolOrder(order.isCarpoolOrder());
                    orderPayInfo.setOrderId(order.getOrderId());
                    orderPayInfo.setCarpoolCode(order.getCarpoolCode());
                    orderPayInfo.setOrderType(order.getOrderType());
                    orderPayInfo.setOrderChannel(order.getChannel());
                    orderPayInfo.setCost(order.getTotalMoney());
                    PayMainActivity.start(this, orderPayInfo);
                } else {
                    OrderDetailActivity.go(this, order.getOrderId());
                }
            }
        } else if (order.getOrderStatus() == Order.ORDER_STATUS_CLOSED ||
                order.getOrderStatus() == Order.ORDER_STATUS_CANCELED) {
            TripDetailActivity.go(this, order.getOrderId(), false);

        } else {
            Intent intent = new Intent(MainActivity.this, OrderDetailActivity.class);
            intent.putExtra(ORDER_ID, order.getOrderId());
            if (cargoList != null && cargoList.size() > 0) {
                intent.putExtra(OrderDetailActivity.CARGO_ORDER_ID, cargoList.get(0).getOrderId()); //对应的货单ID
                LogUtils.d(TAG, "---orderId:" + order.getOrderId() + "cargoId:" + cargoList.get(0).getOrderId());

                //将货物ID存起来
                PrefserHelper.setCache(PrefserHelper.KEY_GOODS_ORDER_ID, String.valueOf(cargoList.get(0).getOrderId()));
            }
            startActivity(intent);
        }
    }

    @Override
    public void getOrderDetailFail(String msg) {

    }

    @Override
    public void showDialDialog(String phoneNum) {
        DialogUtils.showCustomerServiceCallDialog(this, phoneNum, true);
    }

    @Override
    public void showMessageStatus(int unviewedCount) {
        if (unviewedCount > 0) {
            ivMsg.setImageResource(R.mipmap.message);
        } else {
            ivMsg.setImageResource(R.mipmap.main_ic_msg);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mNewOrderPoller != null && !mNewOrderPoller.isDisposed()) {
            mNewOrderPoller.dispose();
            mNewOrderPoller = null;
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        if (deviceLocationManager != null) {
            deviceLocationManager.destroyLocation();
            deviceLocationManager = null;
        }
        EventBus.getDefault().unregister(this);
    }

    /**
     * 刷新数据
     *
     * @param data
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventMsg data) {
        LogUtils.d(TAG, "on event:" + data.toString());
        if (data.getCmd() == EventMsg.CMD_ORDER_CANCELED || data.getCmd() == EventMsg.CMD_ORDER_GRAB_SUCCESS) {
            presenter.getTodayAchieve();
            presenter.getTodayOrderList();
            presenter.getSuggestLine();
        } else if (data.getCmd() == EventMsg.CMD_ORDER_VEHICLE_DISABLE) { //车辆停用，刷新车辆列表和选择的车辆信息，听单状态
            presenter.getListenOrderStatus();
            updateSelectVehicle();
        } else if (data.getCmd() == EventMsg.CMD_ORDER_ADDRESS_HAS_CHANGED) {
            //下车点地址发生改变
            presenter.getTodayOrderList();
        }
    }

    /**
     * 司机端主动更改（添加、删除、修改）的车辆信息
     *
     * @param event 更改事件
     */
    @Subscribe
    public void onVehicleChanged(VehicleSyncEvent event) {
        presenter.getListenOrderStatus();
        updateSelectVehicle();
    }

    public static boolean isLocationServiceHasStarted() {
        return locationServiceHasStarted;
    }

    public static void setLocationServiceHasStarted(boolean locationServiceHasStarted) {
        MainActivity.locationServiceHasStarted = locationServiceHasStarted;
    }

    public static boolean locationServiceHasStarted = false;

    private DeviceLocationManager deviceLocationManager;

    /**
     * @param
     * @return
     * @method
     * @description 开启定位
     * @date: 2019/11/20 12:03
     * @author: tandongdong
     */
    public void startLocation() {
        if (!isLocationServiceHasStarted()) {
            setLocationServiceHasStarted(true);
        } else {
            LogUtils.d(TAG, "location service has started");
            return;
        }
        LogUtils.d(TAG, "startLocation");
        if (deviceLocationManager == null) {
            deviceLocationManager = DeviceLocationManager.getInstance(getApplicationContext());
            deviceLocationManager.init();
            deviceLocationManager.setInterval(5000); //5s
            deviceLocationManager.setLocationListener(new LocationListener() {
                @Override
                public void updateGpsData(GpsData var1) {
                    Log.e(TAG, "LocationListener");
                    if (var1 == null) {
                        LogUtils.e(TAG, "GpsData得到数据为null");
                        return;
                    }
                    LogUtils.d(TAG, "updateGpsData " + var1.getLatitude() + "," + var1.getLongitude() +
                            ",朝向=" + var1.getBearing() + ",速度=" + var1.getSpeed());
                    EventBus.getDefault().post(var1);
                    OrderRepository repository = new OrderRepository();
                    //上报司机位置
                    /*repository.gpsDriverUpdata(var1, new ApiSubscriber<Boolean>() {
                        @Override
                        public void onSuccess(Boolean aBoolean) {
                            LogUtils.d(TAG, "上报位置成功");
                        }

                        @Override
                        public void onError(int code, String msg) {
                            LogUtils.d(TAG, "上报位置失败,code = " + code + ",msg = " + msg);
//                            toast("上报位置失败");
                        }
                    });*/
                }
            });
        }
        deviceLocationManager.startLocation();
    }


    private void highLight() {
        // single example
        new MaterialShowcaseView.Builder(this)
                .setTarget(findViewById(R.id.ll_listen_order))
                .setDismissText("GOT IT")
                .setContentText("This is some amazing feature you should know about")
                .setDelay(500) // optional but starting animations immediately in onCreate can make them choppy
                .singleUse("001") // provide a unique ID used to ensure it is only shown once
                .show();
    }

    /**
     * 显示 next模式 我知道了提示高亮布局
     *
     * @param view id为R.id.iv_known的控件
     * @author isanwenyu@163.com
     */
    public void showNextKnownTipView(View view, int viewId) {
        LogUtils.d(TAG, "showNextKnownTipView");
        HighLight mHightLight = new HighLight(MainActivity.this)//
                .autoRemove(true)//设置背景点击高亮布局自动移除为false 默认为true
//                .intercept(false)//设置拦截属性为false 高亮布局不影响后面布局的滑动效果
                .intercept(true)//拦截属性默认为true 使下方ClickCallback生效
                .enableNext()//开启next模式并通过show方法显示 然后通过调用next()方法切换到下一个提示布局，直到移除自身
//                .setClickCallback(new HighLight.OnClickCallback() {
//                    @Override
//                    public void onClick() {
//                        Toast.makeText(MainActivity.this, "clicked and remove HightLight view by yourself", Toast.LENGTH_SHORT).show();
//                        remove(null);
//                    }
//                })
//                .anchor(findViewById(R.id.ll_listen_order))//如果是Activity上增加引导层，不需要设置anchor
                .addHighLight(viewId, R.layout.layout_pay_confirm, new OnLeftPosCallback(45), new RectLightShape(0, 0, 15, 0, 0));//矩形去除圆角
        /*                .addHighLight(R.id.btn_light,R.rvitem_goods.info_known,new OnRightPosCallback(5),
                        new BaseLightShape(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                5,getResources().getDisplayMetrics()), TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,5,getResources().getDisplayMetrics()),0) {
                    @Override
                    protected void resetRectF4Shape(RectF viewPosInfoRectF, float dx, float dy) {
                        //缩小高亮控件范围
                        viewPosInfoRectF.inset(dx,dy);
                    }

                    @Override
                    protected void drawShape(Bitmap bitmap, HighLight.ViewPosInfo viewPosInfo) {
                        //custom your hight light shape 自定义高亮形状
                        Canvas canvas = new Canvas(bitmap);
                        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                        paint.setDither(true);
                        paint.setAntiAlias(true);
                        //blurRadius必须大于0
                        if(blurRadius>0){
                            paint.setMaskFilter(new BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.SOLID));
                        }
                        RectF rectF = viewPosInfo.rectF;
                        canvas.drawOval(rectF, paint);
                    }
                })
                .addHighLight(R.id.btn_bottomLight,R.rvitem_goods.info_known,new OnTopPosCallback(),new CircleLightShape())
                .addHighLight(view,R.rvitem_goods.info_known,new OnBottomPosCallback(10),new OvalLightShape(5,5,20))
                .setOnRemoveCallback(new HighLightInterface.OnRemoveCallback() {//监听移除回调
                    @Override
                    public void onRemove() {
                        Toast.makeText(MainActivity.this, "The HightLight view has been removed", Toast.LENGTH_SHORT).show();

                    }
                })
                .setOnShowCallback(new HighLightInterface.OnShowCallback() {//监听显示回调
                    @Override
                    public void onShow(HightLightView hightLightView) {
                        Toast.makeText(MainActivity.this, "The HightLight view has been shown", Toast.LENGTH_SHORT).show();
                    }
                }).setOnNextCallback(new HighLightInterface.OnNextCallback() {
                    @Override
                    public void onNext(HightLightView hightLightView, View targetView, View tipView) {
                        // targetView 目标按钮 tipView添加的提示布局 可以直接找到'我知道了'按钮添加监听事件等处理
                        Toast.makeText(MainActivity.this, "The HightLight show next TipView，targetViewID:"+(targetView==null?null:targetView.getId())+",tipViewID:"+(tipView==null?null:tipView.getId()), Toast.LENGTH_SHORT).show();
                    }
                });*/
        mHightLight.show();
    }

    /**
     * 跳转GPS设置
     */
    private void openGPSSettings() {
        if (!LocationUtils.checkLocationIsOpen(this)) {
            Log.e(TAG, "定位服务不可用,GPS定位和网络定位都未开启");
            showGpsNotOpenedTipsDialog();
        }
    }

    private int GPS_REQUEST_CODE = 10;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GPS_REQUEST_CODE) {
            //做需要做的事情，比如再次检测是否打开GPS了 或者定位
//            openGPSSettings();
        }
    }

    private void showDialogWhenStartListenOrder() {
        DBaseDialog dialog = new DBaseDialog(this, R.layout.dialog_start_listen_order, Gravity.CENTER, true);
        dialog.setOnViewClick(R.id.tv_confirm, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.toggleDialog();
                //presenter.changeListenOrderStatus();
                TTSUtil.stop();
            }
        });
        dialog.toggleDialog();
        TTSUtil.playVoice(getString(R.string.voice_prompt_driver_read_tips));
    }

    private ConfirmDialog mGpsNotOpenedTipsDialog;

    /**
     * @param
     * @return
     * @method
     * @description 判断定位功能是否
     * @date: 2019/11/21 16:03
     * @author: tandongdong
     */
    private void showGpsNotOpenedTipsDialog() {
        if (mGpsNotOpenedTipsDialog == null) {
            mGpsNotOpenedTipsDialog = ConfirmDialog.newInstance().setMessage(getString(R.string.dialog_msg_gps_not_open))
                    .setOnClickListener(new ConfirmDialog.OnClickListener() {
                        @Override
                        public void onPositiveClick(View view) {
                            //跳转GPS设置界面
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, GPS_REQUEST_CODE);
                        }

                        @Override
                        public void onDismiss() {

                        }
                    })
                    .setPositiveText(getString(R.string.go_open))
                    .setType(ConfirmDialog.TYPE_ONE_BUTTON)
                    .setCancelOutside(false);
        }

        if (mGpsNotOpenedTipsDialog.getDialog() != null && mGpsNotOpenedTipsDialog.getDialog().isShowing()) {
            //dialog is showing so do something
        } else {
            mGpsNotOpenedTipsDialog.show(getSupportFragmentManager(), "");
        }

    }

    /**
     * 校验某个服务是否还存在
     */
    public static boolean isServiceRunning(Context context, String serviceName) {
        // 校验服务是否还存在
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = am.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo info : services) {
            // 得到所有正在运行的服务的名称
            String name = info.service.getClassName();
            Log.d(TAG, "name:" + name);
            if (serviceName.equals(name)) {
                return true;
            }
        }
        return false;
    }

}

