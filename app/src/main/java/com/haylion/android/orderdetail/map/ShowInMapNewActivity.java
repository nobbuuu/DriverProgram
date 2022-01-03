package com.haylion.android.orderdetail.map;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.AmapPageType;
import com.amap.api.navi.INaviInfoCallback;
import com.amap.api.navi.enums.PathPlanningStrategy;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.haylion.android.R;
import com.haylion.android.common.Const;
import com.haylion.android.common.aibus_location.data.GpsData;
import com.haylion.android.common.map.AMapUtil;
import com.haylion.android.common.map.BaseMapActivity;
import com.haylion.android.data.model.AddressInfo;
import com.haylion.android.data.model.EventMsg;
import com.haylion.android.data.model.FloatwindowCmd;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.model.OrderCostInfo;
import com.haylion.android.data.model.OrderTypeInfo;
import com.haylion.android.data.model.PayInfo;
import com.haylion.android.data.model.WebsocketData;
import com.haylion.android.data.util.BusinessUtils;

import com.haylion.android.data.util.OrderConvert;
import com.haylion.android.main.MainActivity;
import com.haylion.android.mvp.util.LogUtils;
import com.haylion.android.orderdetail.OrderDetailActivity;
import com.haylion.android.service.FloatDialogService;
import com.haylion.android.service.WsCommands;
import com.haylion.android.utils.SpUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 抢单查看地图页面
 */
public class ShowInMapNewActivity extends BaseMapActivity<ShowInMapContract.Presenter> implements ShowInMapContract.View {

    private static final String TAG = "ShowInMapNewActivity";
    public final static String ORDER_START_ADDR = "EXTRA_ORDER_START_ADDR";
    public final static String ORDER_END_ADDR = "EXTRA_ORDER_END_ADDR";
    public final static String EXTRA_GRAB_ENABLED = "EXTRA_GRAB_ENABLED";

    /**
     * Handler msg：更新抢单剩余时间
     */
    private final int HANDLER_MSG_UPDATE_GRAB_ORDER_REST_TIME = 1001;

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_grab_order_reset_time)
    TextView tvGrabOrderResetTime;
    @BindView(R.id.ll_grab_order)
    LinearLayout llGrabOrder;


    private AddressInfo startAddr;
    private AddressInfo endAddr;

    /**
     * 抢单 Handler
     */
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == HANDLER_MSG_UPDATE_GRAB_ORDER_REST_TIME) {
                if (FloatDialogService.grabOrderResetTime - 1000 > FloatDialogService.GRAB_ORDER_TIME) { //处于等待抢单的3s中
                    tvGrabOrderResetTime.setText(((FloatDialogService.grabOrderResetTime - FloatDialogService.GRAB_ORDER_TIME) / 1000) + "s");
                    llGrabOrder.setEnabled(false);
                    llGrabOrder.setBackgroundResource(R.mipmap.ic_bg_grab_order_disable);
                    mHandler.sendEmptyMessageDelayed(HANDLER_MSG_UPDATE_GRAB_ORDER_REST_TIME, 1000);
                } else if (FloatDialogService.grabOrderResetTime > 0) {
                    //处于抢单时间中
                    tvGrabOrderResetTime.setText((FloatDialogService.grabOrderResetTime / 1000) + "s");
                    llGrabOrder.setEnabled(true);
                    llGrabOrder.setBackgroundResource(R.mipmap.ic_bg_grab_order_enable);
                    mHandler.sendEmptyMessageDelayed(HANDLER_MSG_UPDATE_GRAB_ORDER_REST_TIME, 1000);
                } else {
                    //抢单时间已过
                    finish();
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_in_map_act);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        String startAddress = getIntent().getStringExtra("startAddress");
        if (startAddress != null && !startAddress.isEmpty()) {
            setStartMarkerTitle(startAddress);
        } else {
            setStartMarkerTitle("取货地址");
        }
        setEndMarkerTitle("送货地址");
        initMap(savedInstanceState);

        tvTitle.setText(R.string.check_map);
        startAddr = getIntent().getParcelableExtra(ORDER_START_ADDR);
        endAddr = getIntent().getParcelableExtra(ORDER_END_ADDR);

        boolean grabEnabled = getIntent().getBooleanExtra(EXTRA_GRAB_ENABLED, true);
        if (grabEnabled) {
            llGrabOrder.setVisibility(View.VISIBLE);
            mHandler.sendEmptyMessage(HANDLER_MSG_UPDATE_GRAB_ORDER_REST_TIME);
        } else {
            llGrabOrder.setVisibility(View.GONE);
        }

        hideGrabDialog();
        showOrderInfoInMap();
    }

    /**
     * 隐藏抢单弹窗
     */
    private void hideGrabDialog() {
        FloatwindowCmd cmd = new FloatwindowCmd();
        cmd.setWindowType(FloatwindowCmd.WINDOW_TYPE_NEW_ORDER_DIALOG);
        cmd.setOperation(FloatwindowCmd.OPERATION_HIDE);
        EventBus.getDefault().post(cmd);
    }

    private void showOrderInfoInMap() {
        mStartPoint = new LatLonPoint(startAddr.getLatLng().latitude, startAddr.getLatLng().longitude);
        mEndPoint = new LatLonPoint(endAddr.getLatLng().latitude, endAddr.getLatLng().longitude);
        Log.e(TAG, "路线开始规划");
        searchRoute();
    }

    @Override
    public void handleDriveRouteSearchedResult(DriveRouteResult driveRouteResult, int i) {
        Log.e(TAG, "路线规划成功");
        routeOverlay.setStartAndEndIcon(resIdStart, resIdEnd); //设置起点和终点的图标
        routeOverlay.setStartAndEndAddress(startAddr.getName(), endAddr.getName());
        routeOverlay.setShowPolyline(true);
        routeOverlay.setColorFulLine(true);
    }

    @Override
    public void zoomToSpan() {
        routeOverlay.zoomToSpan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @OnClick({R.id.iv_back, R.id.ll_grab_order})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back: //通知抢单弹窗显示
                onBackPressed();
                break;
            case R.id.ll_grab_order: //抢单，发起抢单请求，且让dialog消失
                llGrabOrder.setVisibility(View.GONE);
                FloatwindowCmd cmd = new FloatwindowCmd();
                cmd.setWindowType(FloatwindowCmd.WINDOW_TYPE_NEW_ORDER_DIALOG);
                cmd.setOperation(FloatwindowCmd.OPERATION_CANCEL);
                EventBus.getDefault().post(cmd);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        FloatwindowCmd cmd = new FloatwindowCmd();
        cmd.setWindowType(FloatwindowCmd.WINDOW_TYPE_NEW_ORDER_DIALOG);
        cmd.setOperation(FloatwindowCmd.OPERATION_SHOW);
        EventBus.getDefault().post(cmd);
        finish();
    }


    @Override
    protected ShowInMapContract.Presenter onCreatePresenter() {
        return new ShowInMapPresenter(this);
    }

    /**
     * 订单已经分配，则回到停单首页
     *
     * @param data
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventMsg data) {
        if (data.getCmd() == EventMsg.CMD_ORDER_HAS_ALLOCATED) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else if (data.getCmd() == EventMsg.CMD_ORDER_GRAB_SUCCESS) {
            //抢单成功
            OrderDetailActivity.go(this, data.getOrderId());
            finish();
        } else if (data.getCmd() == EventMsg.CMD_ORDER_GRAB_FAIL) {
            //抢单失败
            finish();
        }
    }


}

