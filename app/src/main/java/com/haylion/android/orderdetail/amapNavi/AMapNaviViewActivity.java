package com.haylion.android.orderdetail.amapNavi;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.navi.model.NaviPoi;
import com.gyf.immersionbar.ImmersionBar;
import com.haylion.android.Constants;
import com.haylion.android.R;
import com.haylion.android.activity.OrderPreSignActivity;
import com.haylion.android.common.aibus_location.data.GpsData;
import com.haylion.android.common.map.AMapUtil;
import com.haylion.android.common.map.BaseMapNaviActivity;
import com.haylion.android.common.view.CargoRestTimeView;
import com.haylion.android.common.view.dialog.DialogUtils;
import com.haylion.android.data.model.AddressInfo;
import com.haylion.android.data.model.ChildInfo;
import com.haylion.android.data.model.EventBean;
import com.haylion.android.data.model.EventMsg;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.model.OrderPayInfo;
import com.haylion.android.data.model.WebsocketData;
import com.haylion.android.data.util.DateUtils;
import com.haylion.android.data.util.DensityUtils;

import com.haylion.android.data.util.SpannableStringUtil;
import com.haylion.android.data.util.TTSUtil;
import com.haylion.android.data.base.ConfirmDialog;
import com.haylion.android.data.widgt.SlideView;
import com.haylion.android.common.view.popwindow.ContactPopWindow;
import com.haylion.android.mvp.util.LogUtils;
import com.haylion.android.orderdetail.OrderDetailActivity;
import com.haylion.android.orderdetail.OrderDetailContract;
import com.haylion.android.orderdetail.OrderDetailPresenter;
import com.haylion.android.service.FloatDialogService;
import com.haylion.android.service.WsCommands;
import com.haylion.android.uploadPhoto.UploadChildImgActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.haylion.android.orderdetail.OrderDetailActivity.ORDER_ID;
import static com.haylion.android.orderdetail.OrderDetailActivity.ORDER_TYPE;

/**
 * @author dengzh
 * @date 2019/11/5
 * Description:地图导航页
 * 能在此页面保持的，只有去接乘客 和 去送乘客 两个状态。
 * 所有与地图有关的逻辑都交由基类完成，此页面只需处理订单逻辑。
 */
public class AMapNaviViewActivity extends BaseMapNaviActivity<OrderDetailContract.Presenter> implements OrderDetailContract.View {

    @BindView(R.id.needOffsetView)
    View needOffsetView;
    /**
     * 订单相关
     */
    @BindView(R.id.rl_order_info)
    RelativeLayout rlOrderInfo;
    @BindView(R.id.tv_addr_info)
    TextView tvAddrInfo;
    @BindView(R.id.fr_contact)
    FrameLayout frContact;
    @BindView(R.id.slideview)
    SlideView slideview;

    /**
     * 货拼客单页面需要展示货单送达的时间。
     */
    @BindView(R.id.cargo_rest_time_view)
    CargoRestTimeView mCargoRestTimeView;

    /**
     * 测试用
     */
    @BindView(R.id.tv_test)
    Button tvTest;

    /**
     * 订单
     */
    private Order order;
    private int orderId;  //订单id
    private int orderType;  //订单type
    private int cargoOrderId = 0; //货单id
    private GpsData currentGps;  // 当前定位GPS信息
    private double linearDistance = -1; //距离目的地的直线距离
    /**
     * 联系电话弹窗
     */
    private ContactPopWindow mContactPopWindow;
    /**
     * 到达上车点 or 到达目的地提示弹窗
     */
    private ConfirmDialog mArriveDialog;
    private boolean isChangeOrderStatus;  //是否改变订单状态

    /**
     * 拼车码
     */
    private String carpoolCode;
    /**
     * 拼车标记
     */
    private boolean carpoolFlag = false;

    /**
     * 跳转
     *
     * @param context
     * @param orderId 订单id
     */
    public static void go(Context context, int orderId) {
        Intent intent = new Intent(context, AMapNaviViewActivity.class);
        intent.putExtra(ORDER_ID, orderId);
        context.startActivity(intent);
    }

    /**
     * 跳转
     *
     * @param context
     * @param orderId 订单id
     */
    public static void go(Context context, int orderId,int orderType) {
        Intent intent = new Intent(context, AMapNaviViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(ORDER_ID, orderId);
        intent.putExtra(ORDER_TYPE, orderType);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amap_navi_view);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        ImmersionBar.with(this)
                .transparentStatusBar()
                .fitsSystemWindows(false)   //先取消BaseActivity 的统一设置
                .titleBarMarginTop(needOffsetView)
                .init();

        if (carpoolFlag) {
            carpoolCode = getIntent().getStringExtra(OrderDetailActivity.EXTRA_CARPOOL_CODE);
        } else {
            orderId = getIntent().getIntExtra(ORDER_ID, 0);
            orderType = getIntent().getIntExtra(ORDER_TYPE, 0);
        }

        currentGps = FloatDialogService.getCurrentGps();
        if (currentGps != null) {
            mStartPoi = new NaviPoi("", new LatLng(currentGps.getLatitude(), currentGps.getLongitude()), "");
        }
        //初始化导航地图
        initAMapNaviView(savedInstanceState);

        //滑动监听
        slideview.addSlideListener(new SlideView.OnSlideListener() {
            @Override
            public void onSlideSuccess() {
                if (order == null) {
                    slideview.reset();
                    refreshOrderDetails();
                    return;
                }
                linearDistance = getLineDistance();
                if (order.getOrderType() == Order.ORDER_TYPE_SEND_CHILD) {
                    //送小孩单
                    handleChildOrderSlide();
                } else if (order.getOrderType() == Order.ORDER_TYPE_REALTIME || order.getOrderType() == Order.ORDER_TYPE_CARGO_PASSENGER) {
                    //实时订单 or 货拼客
                    handleRealTimeOrderSlide();
                } else if (orderType == -1){
                    OrderPreSignActivity.go(getContext(),orderId);
                    finish();
                }else {
                    presenter.changeOrderStatus(cargoOrderId, OrderDetailPresenter.OPERATAR_BY_AMAP_NAVI);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshOrderDetails();
    }

    private void refreshOrderDetails() {
        if (carpoolFlag) {
            presenter.getCarpoolOrderDetails(carpoolCode);
        } else if (orderType == -1){
            presenter.getShunfengOrderDetail(orderId);
        }else {
            presenter.getOrderDetail(orderId);
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (mCargoRestTimeView != null) {
            mCargoRestTimeView.onDestroy();
        }
        super.onDestroy();
    }

    @OnClick(R.id.tv_test)
    public void onViewClicked() {
        if (order == null) {
            return;
        }
        mAMapNavi.stopNavi();
        if (naviType == NaviType.GPS) {
            tvTest.setText("GPS导航");
            naviType = NaviType.EMULATOR;
        } else {
            tvTest.setText("模拟导航");
            naviType = NaviType.GPS;
        }
        mAMapNavi.startNavi(naviType);
    }

    @OnClick({R.id.fr_contact})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fr_contact:  //联系人
                if (order == null) {
                    return;
                }
                if (order.getOrderType() == Order.ORDER_TYPE_SEND_CHILD) {
                    showContactPopWindow();
                } else if (order.getOrderType() == Order.ORDER_TYPE_CARGO) {
                    DialogUtils.showRealCallDialog(this, order.getDropOffContactMobile());
                } else {
                    callPassenger();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 联系乘客
     */
    private void callPassenger() {
        if (!TextUtils.isEmpty(order.getUserInfo().getVirtualNum())) {
            DialogUtils.showVirtualCallDialog(this, order.getUserInfo().getVirtualNum());
        } else if (!TextUtils.isEmpty(order.getUserInfo().getPhoneNum())) {
            if (order.getChannel() != Order.ORDER_CHANNEL_MEITUAN) {
                DialogUtils.showRealCallDialog(this, order.getUserInfo().getPhoneNum());
            }
        } else {
            //订单未结束，虚拟号失效
            ConfirmDialog.newInstance().setMessage("暂时无法联系乘客，先联系客服试试吧")
                    .setOnClickListener(new ConfirmDialog.OnClickListener() {
                        @Override
                        public void onPositiveClick(View view) {
                            presenter.getServiceTelNumber(false);
                        }

                        @Override
                        public void onDismiss() {

                        }
                    }).setPositiveText(R.string.contact_customer_service).setNegativeText(R.string.close).show(getSupportFragmentManager(), "");
        }
    }


    /**
     * 处理实时订单滑动按钮逻辑
     */
    private void handleRealTimeOrderSlide() {
        //实时订单，到达上车点和到达目的地前显示弹窗
        if (order.getOrderStatus() == Order.ORDER_STATUS_WAIT_CAR) {
            //到达上车点
            String msg = getString(linearDistance > 500 ? R.string.get_on_location_more_than_limit_tips : R.string.get_on_location_confirm_tips);
            showArriveDialog(msg);
        } else if (order.getOrderStatus() == Order.ORDER_STATUS_GET_ON) {
            //到达下车点，且距离超过500
            if (linearDistance > 500) {
                showArriveDialog(getString(R.string.get_off_location_more_than_limit_tips));
            } else {
                TTSUtil.stop();
                presenter.changeOrderStatus(cargoOrderId, OrderDetailPresenter.OPERATAR_BY_AMAP_NAVI);
            }
        }
    }

    /**
     * 处理送你上学滑动按钮逻辑
     */
    private void handleChildOrderSlide() {
        if (order.getOrderStatus() == Order.ORDER_STATUS_WAIT_CAR) {
            //到达上车点，显示上车点确认弹窗
            String msg = getString(linearDistance > 500 ? R.string.get_on_location_more_than_limit_tips : R.string.get_on_location_confirm_tips);
            showArriveDialog(msg);
        } else if (order.getOrderStatus() == Order.ORDER_STATUS_GET_ON) {
            //去拍照
            UploadChildImgActivity.go(this, orderId, Order.ORDER_STATUS_GET_OFF,
                    order.getGetOffAutoCheck(), order.getGetOnAutoCheck(), order.getOrderType());
        }
    }

    /**
     * 到达上车点 or 到达目的地 弹窗
     */
    private void showArriveDialog(String msg) {
        isChangeOrderStatus = false;
        mArriveDialog = ConfirmDialog.newInstance();
        mArriveDialog.setMessage(msg).setOnClickListener(new ConfirmDialog.OnClickListener() {
            @Override
            public void onPositiveClick(View view) {
                isChangeOrderStatus = true;
                presenter.changeOrderStatus(cargoOrderId, OrderDetailPresenter.OPERATAR_BY_AMAP_NAVI);
            }

            @Override
            public void onDismiss() {
                if (!isChangeOrderStatus) {
                    slideview.reset();
                }
            }
        }).setPositiveText(R.string.confirm_arrival).setNegativeText(R.string.cancel).show(getSupportFragmentManager(), "");
    }


    /**
     * 电话弹窗
     */
    private void showContactPopWindow() {
        if (mContactPopWindow == null) {
            mContactPopWindow = new ContactPopWindow(this);
            mContactPopWindow.setOnDialogSelectListener(new ContactPopWindow.OnDialogSelectListener() {
                @Override
                public void callService() {
                    //联系客服
                    presenter.getServiceTelNumber(true);
                }
            });
        }
        mContactPopWindow.setData(order);
        //获取需要在其上方显示的控件的位置信息
        int[] location = new int[2];
        rlOrderInfo.getLocationOnScreen(location);
        //在控件上方显示
        mContactPopWindow.showAtLocation(rlOrderInfo, Gravity.NO_GRAVITY, location[0], location[1] - mContactPopWindow.getHeight() + DensityUtils.dip2px(this, 10));
    }

    /*********************************** EventBus 事件 ********************************************/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventBean bean) {
        if (bean.getCode() == Constants.EventCode.UPLOAD_CHILD_OFF_PHOTO_SUCCESS) {
            //小孩下车照片上传成功
            finish();
        }
    }

    /**
     * 处理eventbus发送过来的推送消息
     *
     * @param gpsData
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GpsData gpsData) {
        currentGps = gpsData;
        if (currentGps != null && mStartPoi == null) {
            mStartPoi = new NaviPoi("", new LatLng(currentGps.getLatitude(), currentGps.getLongitude()), "");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventMsg data) {
        if (data.getCmd() == EventMsg.CMD_ORDER_ADDRESS_HAS_CHANGED) {
            //目的地改变，刷新数据
            if (data.getOrderId() == orderId) {
                presenter.getOrderDetail(orderId);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(WebsocketData websocketData) {
        if (websocketData.getCmdSn() == WsCommands.GOODS_ORDER_DELIVERED.getSn()) {
            finish();
        }
    }

    /**
     * 获得当前 到 终点 的直线距离
     */
    private int getLineDistance() {
        if (currentGps == null) {
            return 0;
        }
        currentGps = FloatDialogService.getCurrentGps();
        LatLng currentLatLng = new LatLng(currentGps.getLatitude(), currentGps.getLongitude());
        LatLng endLatLng;
        if (order.getOrderStatus() == Order.ORDER_STATUS_GET_ON) {
            endLatLng = order.getEndAddr().getLatLng();
        } else {
            endLatLng = order.getStartAddr().getLatLng();
        }
        return (int) AMapUtils.calculateLineDistance(currentLatLng, endLatLng);
    }


    /******************************* 后台返回 *****************************************/

    @Override
    protected OrderDetailContract.Presenter onCreatePresenter() {
        carpoolFlag = getIntent().getBooleanExtra(OrderDetailActivity.EXTRA_CARPOOL_FLAG, false);
        return new OrderDetailPresenter(this, carpoolFlag);
    }

    /**
     * 获取订单详情成功
     */
    @Override
    public void getOrderDetailSuccess(Order orderInfo) {
        if (orderInfo != null) {
            order = orderInfo;
            isDataLoaded = true;

            if (orderType == -1){//顺丰单
                changeOrderStatusSuccess(order.getOrderStatus());
                //订单信息
                updateOrderInfoCardView();
                //地图信息
                showOrderInfoInMap();
            }else {
                //联系按钮显示时机，送你上学单和货单显示，其他订单在去接乘客状态显示。
                if (order.getOrderType() == Order.ORDER_TYPE_SEND_CHILD || order.getOrderType() == Order.ORDER_TYPE_CARGO
                        || order.getOrderStatus() == Order.ORDER_STATUS_WAIT_CAR) {
                    frContact.setVisibility(View.VISIBLE);
                }
                if (order.getOrderType() == Order.ORDER_TYPE_CARGO) {
                    //货单，底部按钮隐藏
                    slideview.setVisibility(View.GONE);
                }

                //进入此页面的正确状态是，接乘客和送乘客,其他情况当做异常处理
                if (order.getOrderStatus() == Order.ORDER_STATUS_WAIT_CAR ||
                        order.getOrderStatus() == Order.ORDER_STATUS_GET_ON ||

                        ((order.getOrderType() == Order.ORDER_TYPE_BOOK ||
                                order.getOrderType() == Order.ORDER_TYPE_ACCESSIBILITY) &&
                                order.getOrderStatus() == Order.ORDER_STATUS_READY)
                ) { // 2020.2.16，ready状态为预约单添加

                    if (order.getOrderType() == Order.ORDER_TYPE_CARGO_PASSENGER) {
                        presenter.getCargoOrderSendDeadTime(order.getCargoOrderId());
                    } else if (order.getOrderType() == Order.ORDER_TYPE_CARGO) {
                        getCargoRestTimeSuccess(order.getEstimateArriveTime());
                    }
                    changeOrderStatusSuccess(order.getOrderStatus());
                    //订单信息
                    updateOrderInfoCardView();
                    //地图信息
                    showOrderInfoInMap();
                } else if (order.getOrderStatus() == Order.ORDER_STATUS_GET_OFF) {
                    //去设置收款
                    changeOrderStatusSuccess(order.getOrderStatus());
                } else {
                    finish();
                }
            }
        }
    }

    /**
     * 更新订单信息
     * 实时订单：去接乘客 - ORDER_STATUS_WAIT_CAR，去送乘客 - ORDER_STATUS_GET_ON
     */
    private void updateOrderInfoCardView() {
        SpannableString spannableString;
        if (order.getOrderStatus() == Order.ORDER_STATUS_WAIT_CAR ||
                ((order.getOrderType() == Order.ORDER_TYPE_BOOK ||
                        order.getOrderType() == Order.ORDER_TYPE_ACCESSIBILITY) &&
                        order.getOrderStatus() == Order.ORDER_STATUS_READY)) {
            //去接乘客
            String getOnAddress = getString(R.string.order_detail_get_on_address_decorate, order.getStartAddr().getName());
            spannableString = SpannableStringUtil.getMapNaviAddrSpanString(getOnAddress,
                    1, order.getStartAddr().getName().length() + 2);
            tvAddrInfo.setText(spannableString);
        } else if (order.getOrderStatus() == Order.ORDER_STATUS_GET_ON) {
            if (order.getOrderType() == Order.ORDER_TYPE_CARGO) {
                //去送货
                String getOffAddress = getString(R.string.order_detail_cargo_get_off_address_decorate) + order.getEndAddr().getName();
                spannableString = SpannableStringUtil.getMapNaviAddrSpanString(getOffAddress,
                        3, getOffAddress.length());
            } else {
                //去送乘客
                String getOffAddress = getString(R.string.order_detail_get_off_address_decorate) + order.getEndAddr().getName();
                spannableString = SpannableStringUtil.getMapNaviAddrSpanString(getOffAddress,
                        4, getOffAddress.length());
            }
            tvAddrInfo.setText(spannableString);
        }
    }


    /**
     * 更新地图信息
     * 状态不同，终点不同，重新规划路线
     */
    private void showOrderInfoInMap() {
        updateStartAndEndPoint();
        //目的地
        AddressInfo destAddr = new AddressInfo();
        if (order.getOrderStatus() == Order.ORDER_STATUS_WAIT_CAR ||
                order.getOrderStatus() == Order.ORDER_STATUS_READY) { //去接乘客
            destAddr = order.getStartAddr();
        } else if (order.getOrderStatus() == Order.ORDER_STATUS_GET_ON) { //去送乘客
            destAddr = order.getEndAddr();
            mStartPoi = new NaviPoi(order.getStartAddr().getName(), order.getStartAddr().getLatLng(), "");
        }else if (orderType == -1){
            mStartPoi = new NaviPoi(order.getStartAddr().getName(), order.getStartAddr().getLatLng(), "");
            destAddr = order.getEndAddr();
        }

        //设定终点坐标
        mEndPoi = new NaviPoi(destAddr.getName(), destAddr.getLatLng(), "");
        eList.clear();
        LatLng coordinate = mEndPoi.getCoordinate();
        if (coordinate != null){
            eList.add(new NaviLatLng(coordinate.latitude, coordinate.longitude));
        }
        calculateDriveRoute();
    }


    /**
     * 获取订单详情失败
     *
     * @param msg
     */
    @Override
    public void getOrderDetailFail(String msg) {

    }

    /**
     * 改变订单状态成功
     *
     * @param status
     */
    @Override
    public void changeOrderStatusSuccess(int status) {
        int orderType = order.getOrderType();
        //此处orderStatus 不要取 order.getOrderStatus。因为可能手动改变了这个状态
        if (orderType == Order.ORDER_TYPE_CARGO_PASSENGER
                || orderType == Order.ORDER_TYPE_REALTIME
                || orderType == Order.ORDER_TYPE_REALTIME_CARPOOL
                || orderType == Order.ORDER_TYPE_BOOK
                || orderType == Order.ORDER_TYPE_ACCESSIBILITY
        ) {

            if (status == Order.ORDER_STATUS_WAIT_CAR) {
                slideview.setBackgroundText(getString(R.string.arrive_at_get_on_address));
            } else if (status == Order.ORDER_STATUS_GET_ON) {
                slideview.setBackgroundText(getString(R.string.arrive_at_get_off_address));
            } else if (status == Order.ORDER_STATUS_GET_OFF) {  //下车
                finish();
                return;
            }
        } else if (orderType == Order.ORDER_TYPE_CARGO) { //货单

        } else if (orderType == Order.ORDER_TYPE_SEND_CHILD) { //送你上学单
            if (status == Order.ORDER_STATUS_WAIT_CAR) {
                if (carpoolFlag) {
                    slideview.setBackgroundText(getString(R.string.format_arrive_at_get_on_address_carpool_order,
                            order.getStartAddr().getName()));
                } else {
                    slideview.setBackgroundText(getString(R.string.arrive_at_get_on_address));
                }
//                slideview.setBackgroundText(getString(R.string.arrive_at_get_on_address));
            } else if (status == Order.ORDER_STATUS_GET_ON) {
                if (carpoolFlag) {
                    slideview.setBackgroundText(getString(R.string.format_take_a_picture_of_passengers_get_off_carpool_order,
                            presenter.getChildPhoneSimple()));
                } else {
                    slideview.setBackgroundText(getString(R.string.take_a_picture_of_passengers_get_off));
                }
//                slideview.setBackgroundText(getString(R.string.take_a_picture_of_passengers_get_off));
            } else if (status == Order.ORDER_STATUS_GET_OFF) { //下车
                finish();
            }
        }
        slideview.reset();
    }

    @Override
    public void changeShunFengOrderStatusSuccess(int status) {

    }

    /**
     * 改变订单状态失败
     */
    @Override
    public void changeOrderStatusFail() {
        slideview.reset();
    }

    /**
     * 更新送货剩余时间
     *
     * @param estimateArriveTimeStr
     */
    @Override
    public void getCargoRestTimeSuccess(String estimateArriveTimeStr) {
        if (TextUtils.isEmpty(estimateArriveTimeStr)) {
            LogUtils.e(TAG, "estimateArriveTimeStr is empty");
            return;
        }
        mCargoRestTimeView.setVisibility(View.VISIBLE);
        try {
            Date estimateArriveTime = new SimpleDateFormat(DateUtils.FORMAT_PATTERN_YMDHM).parse(estimateArriveTimeStr);
            Date current = new Date(System.currentTimeMillis());
//            long time = (estimateArriveTime.getTime() - current.getTime()) > 0 ? (estimateArriveTime.getTime() - current.getTime()) : 0;
            long time = estimateArriveTime.getTime() - current.getTime();
            mCargoRestTimeView.startCountDown(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * 支付成功
     */
    @Override
    public void payConfirmSuccess() {

    }

    /**
     * 联系客服
     *
     * @param phoneNum
     */
    @Override
    public void getServiceTelNumberSuccess(String phoneNum, boolean isNeedShowDialog) {
        DialogUtils.showCustomerServiceCallDialog(this, phoneNum, isNeedShowDialog);
    }


    /**
     * 设置起点终点Bitmap
     */
    private void updateStartAndEndPoint() {
        //设置起点图标
        BitmapDescriptor startBitmap = BitmapDescriptorFactory.fromView(AMapUtil.getView(this, R.mipmap.ic_transparent, ""));
        options.setStartPointBitmap(startBitmap.getBitmap());
        BitmapDescriptor endBitmap;
        if (order.getOrderStatus() == Order.ORDER_STATUS_WAIT_CAR) {  //去接乘客
            //终点图标 - 显示上车点图标和地址
            endBitmap = BitmapDescriptorFactory.fromView(AMapUtil.getView(this, R.mipmap.get_on, order.getStartAddr().getName()));
            options.setEndPointBitmap(endBitmap.getBitmap());
        } else {
            //终点图标 - 显示下车点图标和地址
            endBitmap = BitmapDescriptorFactory.fromView(AMapUtil.getView(this, R.mipmap.get_off, order.getEndAddr().getName()));
            options.setEndPointBitmap(endBitmap.getBitmap());
        }
        mAMapNaviView.setViewOptions(options);
    }

    /**
     * 路线规划成功
     *
     * @param aMapCalcRouteResult
     */
    @Override
    public void onCalculateRouteSuccess(AMapCalcRouteResult aMapCalcRouteResult) {
        super.onCalculateRouteSuccess(aMapCalcRouteResult);
    }


}
