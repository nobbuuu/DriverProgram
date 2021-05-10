package com.haylion.android.common.map;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.AMapNaviViewOptions;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.AMapCalcRouteResult;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapModelCross;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviRouteNotifyData;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AMapTrafficStatus;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.navi.model.NaviPoi;
import com.amap.api.navi.model.RouteOverlayOptions;
import com.amap.api.navi.view.DirectionView;
import com.amap.api.navi.view.DriveWayView;
import com.amap.api.navi.view.NextTurnTipView;
import com.amap.api.navi.view.OverviewButtonView;
import com.amap.api.navi.view.TrafficButtonView;
import com.amap.api.navi.view.TrafficProgressBar;
import com.amap.api.navi.view.ZoomButtonView;
import com.amap.api.navi.view.ZoomInIntersectionView;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.haylion.android.R;
import com.haylion.android.common.view.dialog.AmapNaviSettingDialog;
import com.haylion.android.data.model.AmapNaviSettingBean;
import com.haylion.android.data.repo.PrefserHelper;
import com.haylion.android.data.util.LocationUtils;
import com.haylion.android.data.util.TTSUtil;
import com.haylion.android.data.base.ConfirmDialog;
import com.haylion.android.mvp.callback.SimpleActivityLifecycleCallbacks;
import com.haylion.android.mvp.base.AbstractPresenter;
import com.haylion.android.data.base.BaseActivity;
import com.haylion.android.mvp.util.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author dengzh
 * @date 2019/11/5
 * Description: 自定义导航地图基类，与订单逻辑代码解耦，深度定制自己的UI
 */
public abstract class BaseMapNaviActivity<P extends AbstractPresenter> extends BaseActivity<P> implements AMapNaviListener, AMapNaviViewListener {

    protected final String TAG = getClass().getSimpleName();

    /**
     * 导航地图View，在此基础上自定义控件，内部已实现了很多逻辑
     */
    protected AMapNaviView mAMapNaviView;

    /**
     * 是导航对外控制类，提供计算规划路线、偏航以及拥堵重新算路等方法。
     */
    protected AMapNavi mAMapNavi;

    /**
     * 导航地图配置类
     */
    protected AMapNaviViewOptions options;

    /**
     * 车道View
     */
    protected DriveWayView mDriveWayView;
    /**
     * 导航光柱
     */
    protected TrafficProgressBar mTrafficBar;
    /**
     * 指南针
     */
    protected DirectionView mDirectionView;
    /**
     * 路况按钮
     */
    protected TrafficButtonView mTrafficButtonView;
    /**
     * 路口放大图
     */
    protected ZoomInIntersectionView mZoomInIntersectionView;
    /**
     * 转向图标
     */
    protected NextTurnTipView mNextTurnTipView;
    /**
     * 全览按钮
     */
    protected OverviewButtonView mOverviewButtonView;
    /**
     * 放大缩小按钮
     */
    protected ZoomButtonView mZoomButtonView;


    /**
     * 规划 起点 和 终点
     */
    protected final List<NaviLatLng> sList = new ArrayList<NaviLatLng>();
    protected final List<NaviLatLng> eList = new ArrayList<NaviLatLng>();

    //导航模式
    protected int naviType = NaviType.GPS;
    private boolean isNeedPlayBackVoice = true;  //是否需要播放后台语音提示

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 导航地图初始化，子View要调用这个方法，且保证对应的资源id一致
     */
    protected void initAMapNaviView(@Nullable Bundle savedInstanceState) {
        // 初始化地图并创建地图
        mAMapNaviView = findViewById(R.id.navi_view);
        mAMapNaviView.setAMapNaviViewListener(this);
        mAMapNaviView.onCreate(savedInstanceState);

        mDriveWayView = findViewById(R.id.myDriveWayView);
        mTrafficBar = findViewById(R.id.myTrafficBar);
        mDirectionView = findViewById(R.id.myDirectionView);
        mTrafficButtonView = findViewById(R.id.myTrafficButtonView);
        mZoomInIntersectionView = findViewById(R.id.myZoomInIntersectionView);
        mNextTurnTipView = findViewById(R.id.myNextTurnTipView);
        mZoomButtonView = findViewById(R.id.myZoomButtonView);


        setViewOptions();
        setCustomView();
        initAMapNavi();

        initMaasView();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mAMapNaviView.onResume();
        openGPSSettings();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAMapNaviView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(!SimpleActivityLifecycleCallbacks.isForeground() && isNeedPlayBackVoice){
            TTSUtil.playAmapNavi(getString(R.string.amap_navi_voice_tips_on_background));
            isNeedPlayBackVoice = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TTSUtil.stop();
        if (mAMapNaviView != null) {
            mAMapNaviView.onDestroy();
        }
        if (mAMapNavi != null) {
            mAMapNavi.stopNavi();
            mAMapNavi.destroy();
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    private void initAMapNavi() {
        //获取AMapNavi实例
        mAMapNavi = AMapNavi.getInstance(getApplicationContext());
        //添加监听回调，用于处理算路成功
        mAMapNavi.addAMapNaviListener(this);
        //设置模拟导航的行车速度
        mAMapNavi.setEmulatorNaviSpeed(120);
        //设置使用内部语音播报，用户注册的AMapNaviListener中的onGetNavigationText 方法将不再回调
        mAMapNavi.setUseInnerVoice(false);
       // mAMapNavi.setBroadcastMode(BroadcastMode.DETAIL);  //1-专家播报 简洁播报 ，2-新手播报 详细播报
        //设置自身的语音可用
        //PrefserHelper.setCache(PrefserHelper.KEY_AMAP_NAVI_VOICE_ENABLE, "enable");
    }


    /**
     * 设置导航地图相关属性
     */
    private void setViewOptions() {
        // 获取地图属性 并设置相关属性
        options = mAMapNaviView.getViewOptions();
        // 设置导航界面UI是否显示
        options.setLayoutVisible(false);
        // 设置自车icon
        options.setCarBitmap(BitmapFactory.decodeResource(this.getResources(), R.mipmap.driving_car));
        //自定义方向罗盘，方向罗盘图标的方向朝上（即：指北），否则会导致导航过程中方向显示不正确。
        // options.setFourCornersBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.sou9));
        //设置起点终点bitmap
        setStartAndEndPoint();
        //设置路段显示
        setRouteOverlayOptions();
        //设置路口放大图
        // 设置是否显示路口放大图(路口模型图)
        options.setModeCrossDisplayShow(false);
        // 设置是否显示路口放大图(实景图)
        options.setRealCrossDisplayShow(true);
        //setCrossOptions();
        //设置6秒后是否自动锁车
        options.setAutoLockCar(true);
        //设置是否自动改变缩放等级(行驶过程中，地图会缩放)
        //options.setAutoChangeZoom(true);
        //设置倾角,取值范围[0-60] 倾角为0时地图模式是2D模式。
        options.setTilt(30);
        // 设置导航属性
        mAMapNaviView.setViewOptions(options);
    }

    private void setCustomView() {
        //自定义指南针
        setLazyDirectionView();
        //自定义全览按钮
        //setOverviewButtonView();
        //自定义路况按钮
        setLazyTrafficButtonView();
        //自定义路口放大图
        setZoomInIntersectionView();
        //自定义放大缩小按钮
        setLazyZoomButtonView();
        //自定义转向图标
        setLazyNextTurnTipView();
        //自定义导航光柱
        setTrafficProgressBar();

        //设置正北模式
        setNaviMode();
        //设置车道view类
        setDriveWayView();
    }

    /****************************** 导航UI定制化 *****************************************/

    /**
     * 设置起点终点Bitmap
     */
    private void setStartAndEndPoint() {
        //设置起点图标
        BitmapDescriptor startBitmap = BitmapDescriptorFactory.fromView(AMapUtil.getView(this, R.mipmap.get_on, ""));
        options.setStartPointBitmap(startBitmap.getBitmap());
        //设置下车点图标
        BitmapDescriptor endBitmap = BitmapDescriptorFactory.fromView(AMapUtil.getView(this, R.mipmap.get_off, ""));
        options.setEndPointBitmap(endBitmap.getBitmap());
    }


    /**
     * 设置路段Bitmap
     */
    private void setRouteOverlayOptions() {
        //关闭自动绘制路线（如果你想自行绘制路线的话，必须关闭！！！）
        options.setAutoDrawRoute(true);
        //通过路线是否自动置灰，可以使用RouteOverlayOptions.setPassRoute(Bitmap)改变纹理
        options.setAfterRouteAutoGray(true);


        RouteOverlayOptions routeOverlayOptions = new RouteOverlayOptions();
        //设置默认的路线纹理位图（未开启路况时）
        routeOverlayOptions.setNormalRoute(BitmapFactory.decodeResource(getResources(), R.drawable.custtexture));
        //routeOverlayOptions.setLineWidth(64f);
        //开启路况
        //设置浮于道路上的『小箭头』图标的纹理位图。
        routeOverlayOptions.setArrowOnTrafficRoute(BitmapFactory.decodeResource(getResources(), R.drawable.custtexture_aolr));
        //设置未知路况下的纹理位图
        routeOverlayOptions.setUnknownTraffic(BitmapFactory.decodeResource(getResources(), R.drawable.custtexture_no));
        //设置畅通路况下的纹理位图
        routeOverlayOptions.setSmoothTraffic(BitmapFactory.decodeResource(getResources(), R.drawable.custtexture_green));
        //设置缓慢路况下的纹理位图
        routeOverlayOptions.setSlowTraffic(BitmapFactory.decodeResource(getResources(), R.drawable.custtexture_slow));
        //设置拥堵路况下的纹理位图
        routeOverlayOptions.setJamTraffic(BitmapFactory.decodeResource(getResources(), R.drawable.custtexture_bad));
        //设置严重拥堵路况下的纹理位图
        routeOverlayOptions.setVeryJamTraffic(BitmapFactory.decodeResource(getResources(), R.drawable.custtexture_grayred));
        //设置通过的纹理位图
        routeOverlayOptions.setPassRoute(BitmapFactory.decodeResource(getResources(),R.drawable.custtexture_pass));
        options.setRouteOverlayOptions(routeOverlayOptions);

    }

    /**
     * 设置路口放大图的显示位置
     */
    private void setCrossOptions() {
        /**
         * 设置路口放大图的显示位置。
         * 路口放大图分为模型图和实景图，这里设置的是模型图的位置.
         * 实景图可通过自定义ZoomInIntersectionView进行设置.
         *
         *  第一个参数：横屏路口放大图显示位置。
         *  第二个参数： 竖屏路口放大图显示位置。
         */
       /* mDriveWayView.post(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG,"mDriveWayView.getTop():" + mDriveWayView.getTop());
                options.setCrossLocation(new Rect(0,30,260,300),
                        new Rect(SizeUtil.getScreenWidth(this)/2 - 195, mDriveWayView.getBottom(),
                                SizeUtil.getScreenWidth(this)/2 + 195, mDriveWayView.getBottom() + 285));

                mAMapNaviView.setViewOptions(options);
            }
        });*/
    }

    /**
     * 自定义指南针
     */
    private void setLazyDirectionView() {
        mAMapNaviView.setLazyDirectionView(mDirectionView);
    }

    /**
     * 设置用户自定义的DriveWayView
     * 车道view类 提示用户当前的车道有几个 且不同车道对应的前进方向为什么（比如：直行+右转；掉头）
     */
    private void setDriveWayView() {
        mAMapNaviView.setLazyDriveWayView(mDriveWayView);
    }

    /**
     * 自定义导航光柱，右边那一条
     */
    private void setTrafficProgressBar() {
        //主动隐藏导航光柱
        options.setTrafficBarEnabled(false);

        //设置各状态颜色
     /*   mTrafficBar.setUnknownTrafficColor(Color.WHITE);  //未知路况的颜色值
        mTrafficBar.setSmoothTrafficColor(Color.GREEN);   //畅通路况的颜色值。
        mTrafficBar.setSlowTrafficColor(Color.YELLOW);    //缓慢路况的颜色值
        mTrafficBar.setJamTrafficColor(Color.DKGRAY);     //拥堵路况的颜色值
        mTrafficBar.setVeryJamTrafficColor(Color.BLACK);  //严重拥堵路况的颜色值*/
    }


    /**
     * 自定义路况按钮
     */
    private void setLazyTrafficButtonView() {
        mAMapNaviView.setLazyTrafficButtonView(mTrafficButtonView);
    }


    /**
     * 自定义路口放大图
     */
    private void setZoomInIntersectionView() {
        mAMapNaviView.setLazyZoomInIntersectionView(mZoomInIntersectionView);
    }

    /**
     * 自定义转向图标，图标的名称不能更改。
     */
  /*  private int[] customIconTypes = {R.drawable.sou2, R.drawable.sou3,
            R.drawable.sou4, R.drawable.sou5, R.drawable.sou6, R.drawable.sou7,
            R.drawable.sou8, R.drawable.sou9, R.drawable.sou10,
            R.drawable.sou11, R.drawable.sou12, R.drawable.sou13,
            R.drawable.sou14, R.drawable.sou15, R.drawable.sou16,
            R.drawable.sou17, R.drawable.sou18, R.drawable.sou19,
    };*/

    /**
     * 自定义下一个路口转向提示View
     */
    private void setLazyNextTurnTipView() {
        //设置自定义UI样式，这个是修改样式后的图片ID数组
        // mNextTurnTipView.setCustomIconTypes(getResources(), customIconTypes);
        //设置自定义的NextTurnTipView到AMapNaviView中，使其生效
        mAMapNaviView.setLazyNextTurnTipView(mNextTurnTipView);

    }

    /**
     * 自定义全览按钮
     */
    private void setOverviewButtonView() {
        mOverviewButtonView = findViewById(R.id.myOverviewButtonView);
        mAMapNaviView.setLazyOverviewButtonView(mOverviewButtonView);
    }

    /**
     * 自定义放大缩小按钮
     */
    private void setLazyZoomButtonView() {
        mAMapNaviView.setLazyZoomButtonView(mZoomButtonView);
    }


    /**
     * 设置正北模式
     */
    private void setNaviMode() {
        //地图上方是北
        //mAMapNaviView.setNaviMode(AMapNaviView.NORTH_UP_MODE);
        //地图上方是车头方向
        mAMapNaviView.setNaviMode(AMapNaviView.CAR_UP_MODE);
    }

    /****************************** 导航UI定制化 结束 *****************************************/


    /****************************** NaviView 相关 ********************************************/
    /**
     * 界面右下角功能设置按钮的回调接口
     */
    @Override
    public void onNaviSetting() {
        Log.e(TAG, "onNaviSetting");
    }

    /**
     * 导航页面左下角返回按钮点击后弹出的『退出导航对话框』中选择『确定』后的回调接口
     */
    @Override
    public void onNaviCancel() {
        Log.e(TAG, "onNaviCancel");
    }

    /**
     * 导航页面左下角返回按钮的回调接口
     *
     * @return
     */
    @Override
    public boolean onNaviBackClick() {
        Log.e(TAG, "onNaviBackClick");
        return false;
    }

    /**
     * 导航视角变化回调
     *
     * @param i 导航视角，0:车头朝上状态；1:正北朝上模式。
     */
    @Override
    public void onNaviMapMode(int i) {
        Log.e(TAG, "onNaviMapMode:------" + i);
    }

    /**
     * 界面左上角转向操作的点击回调
     */
    @Override
    public void onNaviTurnClick() {
        Log.e(TAG, "onNaviTurnClick");
    }

    /**
     * 界面下一道路名称的点击回调。
     */
    @Override
    public void onNextRoadClick() {
        Log.e(TAG, "onNextRoadClick");
    }

    /**
     * 界面全览按钮的点击回调。
     */
    @Override
    public void onScanViewButtonClick() {
        Log.e(TAG, "onScanViewButtonClick: " + mAMapNaviView.isRouteOverviewNow());
        if (mAMapNaviView.isRouteOverviewNow()) {
            //全览模式，取消锁车
            options.setAutoLockCar(false);
        } else {
            //非全览模式，6s后锁车
            options.setAutoLockCar(true);
            //恢复锁车状态:用于用户主动恢复之前的导航锁车状态（比如从全览画面，挪动地图后画面返回）
            // mAMapNaviView.recoverLockMode();
        }
        mAMapNaviView.setViewOptions(options);
    }

    /**
     * 是否锁定地图的回调
     *
     * @param b true代表锁车状态，地图未锁定。false代表非锁车状态，地图锁定。
     */
    @Override
    public void onLockMap(boolean b) {
        Log.e(TAG, "onLockMap-----" + b);
        onLockMapMass(b);
    }

    /**
     * 导航view加载完成回调
     */
    @Override
    public void onNaviViewLoaded() {
        Log.e(TAG, "onNaviViewLoaded");
    }

    /**
     * AMapNaviView地图白天黑夜模式切换回调
     *
     * @param mapType - 枚举值参考AMap类 3-夜间模式 4-白天模式
     */
    @Override
    public void onMapTypeChanged(int mapType) {
        Log.e(TAG, "onMapTypeChanged------" + mapType);
    }

    /**
     * 导航视图展示模式变化回调
     *
     * @param i 具体类型可参考AMapNaviViewShowMode, 0-普通 1-全览 2-锁车
     */
    @Override
    public void onNaviViewShowMode(int i) {
        Log.e(TAG, "onNaviViewShowMode ----- " + i);
    }

    /****************************** AMapNavi 相关 ********************************************/

    /**
     * 导航初始化失败时的回调函数。
     */
    @Override
    public void onInitNaviFailure() {
        Log.e(TAG, "onInitNaviFailure");
        toast(R.string.toast_navi_init_fail);
    }

    /**
     * 导航初始化成功时的回调函数。
     * 当 AMapNavi 对象初始化成功后，会进入 onInitNaviSuccess 回调函数，在该回调函数中调用路径规划方法计算路径。
     */
    @Override
    public void onInitNaviSuccess() {
        Log.e(TAG, "onInitNaviSuccess");
        //calculateDriveRoute();
    }


    /**
     * 启动导航后的回调函数
     *
     * @param type - 导航类型参见NaviType
     */
    @Override
    public void onStartNavi(int type) {
        Log.e(TAG, "onStartNavi: " + type);
    }

    /**
     * 当前方路况光柱信息有更新时回调函数。
     */
    @Override
    public void onTrafficStatusUpdate() {

    }

    /**
     * 当GPS位置有更新时的回调函数。
     *
     * @param aMapNaviLocation 当前位置的定位信息
     */
    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {
       /* Log.e(TAG, "onLocationChange----" + aMapNaviLocation.getAltitude());
        Log.e(TAG, "GPS定位数据2--Latitude:" + aMapNaviLocation.getCoord().getLatitude());
        Log.e(TAG, "GPS定位数据2----Longitude:" + aMapNaviLocation.getCoord().getLongitude());*/
    }

    //已过时，用 onGetNavigationText(String s)
    @Override
    public void onGetNavigationText(int i, String s) {

    }

    /**
     * 导航播报信息回调函数
     *
     * @param s 播报文字,例如“准备出发,全程五点六公里,大约需要十五分钟”
     */
    @Override
    public void onGetNavigationText(String s) {
        Log.e(TAG, "onGetNavigationText:" + s);
        if (!TextUtils.isEmpty(s)) {
            TTSUtil.playAmapNavi(s);
        }
    }

    /**
     * 模拟导航停止后回调函数。
     */
    @Override
    public void onEndEmulatorNavi() {

    }

    /**
     * 到达目的地后回调函数。
     */
    @Override
    public void onArriveDestination() {
        Log.e(TAG, "onArriveDestination() -- 到达目的地");
        onArriveDestinationMaas();
    }

    //已过时，用 AMapNaviListener.onCalculateRouteFailure(AMapCalcRouteResult)
    @Override
    public void onCalculateRouteFailure(int i) {

    }

    //已过时，用 AMapNaviListener.onCalculateRouteSuccess(AMapCalcRouteResult)
    @Override
    public void onReCalculateRouteForYaw() {

    }

    //已过时，AMapNaviListener.onCalculateRouteSuccess(AMapCalcRouteResult)
    @Override
    public void onReCalculateRouteForTrafficJam() {

    }

    /**
     * 驾车路径导航到达某个途经点的回调函数。
     *
     * @param i 到达途径点的编号，标号从0开始，依次累加。
     */
    @Override
    public void onArrivedWayPoint(int i) {

    }

    /**
     * 用户手机GPS设置是否开启的回调函数。
     *
     * @param b b - true,开启;false,未开启。
     */
    @Override
    public void onGpsOpenStatus(boolean b) {
        Log.e(TAG, "onGpsOpenStatus:" + b);
    }

    /**
     * 导航引导信息回调 naviinfo 是导航信息类。
     *
     * @param naviInfo 导航信息对象
     */
    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {
        Log.e(TAG, "onNaviInfoUpdate: " + naviInfo.toString());

        int allLength = mAMapNavi.getNaviPath().getAllLength();
        /**
         * 导航路况条 更新
         */
        List<AMapTrafficStatus> trafficStatuses = mAMapNavi.getTrafficStatuses(0, 0);
        mTrafficBar.update(allLength, naviInfo.getPathRetainDistance(), trafficStatuses);

        /**
         * 更新路口转向图标
         */
        mNextTurnTipView.setIconType(naviInfo.getIconType());

        onNaviInfoUpdateMaas(naviInfo);
    }

    //已过时，用 onNaviInfoUpdate(NaviInfo naviInfo)
    @Override
    public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {

    }

    /**
     * 导航过程中的摄像头信息回调函数
     *
     * @param infoArray 摄像头对象数组
     */
    @Override
    public void updateCameraInfo(AMapNaviCameraInfo[] infoArray) {

    }

    /**
     * 导航过程中的区间测速信息回调函数
     *
     * @param startCameraInfo 区间测速起点信息
     * @param endCameraInfo   区间测速终点信息
     * @param status          具体类型可参考 CarEnterCameraStatus
     */
    @Override
    public void updateIntervalCameraInfo(AMapNaviCameraInfo startCameraInfo, AMapNaviCameraInfo endCameraInfo, int status) {

    }

    /**
     * 服务区信息回调函数
     *
     * @param infoArray 服务区对象数组
     */
    @Override
    public void onServiceAreaUpdate(AMapServiceAreaInfo[] infoArray) {

    }

    /**
     * 显示路口放大图回调(实景图)。
     *
     * @param aMapNaviCross 路口放大图类，可以获得此路口放大图bitmap
     */
    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {
        Log.e(TAG, "showCross");
        mZoomInIntersectionView.setImageBitmap(aMapNaviCross.getBitmap());
        mZoomInIntersectionView.setVisibility(View.VISIBLE);
    }


    /**
     * 关闭路口放大图回调(实景图)
     */
    @Override
    public void hideCross() {
        Log.e(TAG, "hideCross");
        mZoomInIntersectionView.setVisibility(View.GONE);
    }

    /**
     * 显示路口放大图回调(模型图)。
     *
     * @param aMapModelCross 模型图数据类,可以获取绘制模型图需要的矢量数据
     */
    @Override
    public void showModeCross(AMapModelCross aMapModelCross) {

    }

    /**
     * 关闭路口放大图回调(模型图)。
     */
    @Override
    public void hideModeCross() {

    }

    //已过时,用 AMapNaviListener.showLaneInfo(AMapLaneInfo)
    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

    }

    /**
     * 显示道路信息回调。
     *
     * @param laneInfo 道路信息，可获得当前道路信息，可用于用户使用自己的素材完全自定义显示。
     */
    @Override
    public void showLaneInfo(AMapLaneInfo laneInfo) {
        //当前车道数量，车道的转向。
        Log.e(TAG, "showLaneInfo:" + laneInfo.toString());
    }

    /**
     * 关闭道路信息回调
     */
    @Override
    public void hideLaneInfo() {
        Log.e(TAG, "hideLaneInfo");
    }

    /**
     * 已过时，建议 用 AMapNaviListener.onCalculateRouteSuccess(AMapCalcRouteResult)
     * 当驾车路线规划成功时，若是单一策略，会进 onCalculateRouteSuccess 回调，
     * 若是多策略，会进 nCalculateMultipleRoutesSuccess(int[] routeIds) 回调，
     * 在该回调函数中，可以进行规划路线显示或开始导航。
     *
     * @param ints
     */
    @Override
    public void onCalculateRouteSuccess(int[] ints) {

    }

    //已过时，用 ParallelRoadListener.notifyParallelRoad(AMapNaviParallelRoadStatus)
    @Override
    public void notifyParallelRoad(int i) {

    }

    //已过时，AimlessModeListener.onUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[])
    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

    }

    //已过时
    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

    }

    //已过时
    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {

    }

    //已过时
    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

    }

    //已过时
    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

    }

    /**
     * 回调各种类型的提示音，类似高德导航"叮".
     *
     * @param i
     */
    @Override
    public void onPlayRing(int i) {

    }

    /**
     * 路线规划成功回调，包括算路、导航中偏航、用户改变算路策略、行程点等触发的重算，
     * 具体算路结果可以通过AMapCalcRouteResult获取 可以通过CalcRouteResult获取算路错误码、算路类型以及路线id
     *
     * @param aMapCalcRouteResult
     */
    @Override
    public void onCalculateRouteSuccess(AMapCalcRouteResult aMapCalcRouteResult) {
        Log.e(TAG, "路线规划成功");
        mAMapNavi.startNavi(naviType);
        //如果根据获取的导航路线来自定义绘制
       /* RouteOverLay routeOverlay = new RouteOverLay(mAMapNaviView.getMap(), mAMapNavi.getNaviPath(), this);
        routeOverlay.setStartPointBitmap(BitmapFactory.decodeResource(this.getResources(), R.mipmap.get_on));
        routeOverlay.setEndPointBitmap(BitmapFactory.decodeResource(this.getResources(), R.mipmap.get_off));
        routeOverlay.setTrafficLine(false);
        try {
            routeOverlay.setWidth(30);
        } catch (Throwable e) {
            //宽度须>0
            e.printStackTrace();
        }
        int[] color = new int[10];
        color[0] = Color.BLACK;
        color[1] = Color.RED;
        color[2] = Color.BLUE;
        color[3] = Color.YELLOW;
        color[4] = Color.GRAY;
        routeOverlay.addToMap(color, mAMapNavi.getNaviPath().getWayPointIndex());*/


    }

    /**
     * 路线规划失败回调，包括算路、导航中偏航、用户改变算路策略、行程点等触发的重算，
     * 具体算路结果可以通过AMapCalcRouteResult获取 可以通过CalcRouteResult获取算路错误码、算路类型以及路线id
     *
     * @param aMapCalcRouteResult
     */
    @Override
    public void onCalculateRouteFailure(AMapCalcRouteResult aMapCalcRouteResult) {
        toast(R.string.toast_navi_calculate_route_fail);
        LogUtils.e(TAG,"路线规划失败：errorCode=" + aMapCalcRouteResult.getErrorCode()
                + "\n errorDetail" + aMapCalcRouteResult.getErrorDetail());
    }

    /**
     * 导航过程中道路信息通知 导航过程中针对拥堵区域、限行区域、禁行区域、道路封闭等情况的躲避通知。
     *
     * @param aMapNaviRouteNotifyData
     */
    @Override
    public void onNaviRouteNotify(AMapNaviRouteNotifyData aMapNaviRouteNotifyData) {

    }


    /************************************ 与项目深度定制 ******************************************/
    /**
     * 导航路段信息
     */
    @BindView(R.id.tv_cur_step_retain_distance)
    TextView tvCurStepRetainDistance;   //当前路短剩余距离
    @BindView(R.id.tv_distance_unit)
    TextView tvDistanceUnit;           //距离单位
    @BindView(R.id.tv_next_road_name)
    TextView tvNextRoadName;          //下个路名字
    @BindView(R.id.ll_path_retain_info)
    LinearLayout llPathRetainInfo;   //路段剩余信息
    @BindView(R.id.tv_path_retain_distance)
    TextView tvPathRetainDistance;       //路线剩余距离
    @BindView(R.id.tv_path_retain_distance_unit)
    TextView tvPathRetainDistanceUnit;  //路线剩余距离 单位(米/公里)
    @BindView(R.id.tv_path_retain_time_hour)
    TextView tvPathRetainTimeHour;       //路线预计时间 - 小时
    @BindView(R.id.tv_path_retain_time_hour_unit)
    TextView tvPathRetainTimeHourUnit;
    @BindView(R.id.tv_path_retain_time_minute)
    TextView tvPathRetainTimeMinute;    //路线预计时间 - 分钟
    @BindView(R.id.tv_path_retain_time_minute_unit)
    TextView tvPathRetainTimeMinuteUnit;
    @BindView(R.id.tv_path_retain_time_second)
    TextView tvPathRetainTimeSecond;    //路线预计时间 - 秒
    @BindView(R.id.tv_path_retain_time_second_unit)
    TextView tvPathRetainTimeSecondUnit;
    @BindView(R.id.tv_speed)
    TextView tvSpeed;          //车速

    /**
     * 全览
     */
    @BindView(R.id.tv_overview)
    TextView tvOverview;
    /**
     * 继续导航
     */
    @BindView(R.id.ll_continue_navi)
    LinearLayout llContinueNavi;

    /**
     * 起点，只是用来限定全览范围的.
     * 并不会在导航规划中使用这个数据。
     * 去接乘客：进入此页面时的位置坐标。
     * 去送乘客：上车点。
     */
    protected NaviPoi mStartPoi;
    protected NaviPoi mEndPoi; //终点
    protected boolean isDataLoaded;  //是否已加载数据
    protected Handler mHandler = new Handler();

    private boolean isOverView; //是否全览
    private AmapNaviSettingDialog mSettingDialog; //导航设置弹窗
    private AmapNaviSettingBean mNaviSetBean;  //导航设置Bean


    private void initMaasView() {
        mNaviSetBean = PrefserHelper.getNaviSetInfo();
        mAMapNavi.setBroadcastMode(mNaviSetBean.getBroadcastMode());  //1-专家播报 简洁播报 ，2-新手播报 详细播报
    }

    @OnClick({R.id.tv_back, R.id.ll_continue_navi, R.id.fr_click_setting, R.id.fr_click_overview})
    public void onMaasViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_back:  //退出
                finish();
                break;
            case R.id.ll_continue_navi: //继续导航
                llPathRetainInfo.setVisibility(View.VISIBLE);
                llContinueNavi.setVisibility(View.GONE);
                isOverView = false;
                mAMapNaviView.recoverLockMode();
                tvOverview.setText(R.string.overview);
                break;
            case R.id.fr_click_setting:  //设置
                if (!isDataLoaded) {
                    return;
                }
                showSettingDialog();
                break;
            case R.id.fr_click_overview:  //全览
                if (!isDataLoaded) {
                    return;
                }
                isOverView = !isOverView;
                if (isOverView) { //全览
                    mAMapNaviView.displayOverview();
                    tvOverview.setText(R.string.exit_overview);
                    //考虑是否需要缩小全览范围
                    zoomToSpan();
                } else {  //锁车
                    mAMapNaviView.recoverLockMode();
                    tvOverview.setText(R.string.overview);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 导航设置弹窗
     */
    private void showSettingDialog() {
        if (mSettingDialog == null) {
            mSettingDialog = new AmapNaviSettingDialog(this);
            mSettingDialog.setOnDialogSelectListener(new AmapNaviSettingDialog.OnDialogSelectListener() {
                @Override
                public void onConfirm() {
                    //从缓存里取值
                    mNaviSetBean = PrefserHelper.getNaviSetInfo();
                    //重新设置语音播报
                    if (mNaviSetBean.getBroadcastMode() == -1) {
                        //立即结束当前语音播报
                        TTSUtil.stop();
                    } else {
                        //播报语音
                        mAMapNavi.setBroadcastMode(mNaviSetBean.getBroadcastMode());
                    }
                    //重新规划路线
                    calculateDriveRoute();
                }
            });
        }
        mSettingDialog.updateData();
        mSettingDialog.toggleDialog();
    }

    /**
     * 把起点终点显示在一定范围内
     */
    private void zoomToSpan() {
        if (mStartPoi == null) {
            return;
        }
        LatLngBounds.Builder b = LatLngBounds.builder();
        b.include(new LatLng(mStartPoi.getCoordinate().latitude, mStartPoi.getCoordinate().longitude));
        b.include(new LatLng(mEndPoi.getCoordinate().latitude, mEndPoi.getCoordinate().longitude));
        LatLngBounds bounds = b.build();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                /**
                 * CameraUpdateFactory.newLatLngBoundsRect 设置显示在规定屏幕范围内的地图经纬度范围。
                 * latlngbounds - 地图显示经纬度范围。
                 * paddingLeft - 设置经纬度范围和mapView左边缘的空隙。
                 * paddingRight - 设置经纬度范围和mapView右边缘的空隙。
                 * paddingTop - 设置经纬度范围和mapView上边缘的空隙。
                 * paddingBottom - 设置经纬度范围和mapView下边缘的空隙。
                 */
                mAMapNaviView.getMap().animateCamera(CameraUpdateFactory
                        .newLatLngBoundsRect(bounds, 200, 200, 800, 400), 500, null);
            }
        }, 200);
    }

    /**
     * 是否锁定地图的回调
     *
     * @param b true代表锁车状态，地图未锁定。false代表非锁车状态，地图锁定。
     */
    private void onLockMapMass(boolean b) {
        if (b) {  //锁车状态，全览按钮也要改变
            llPathRetainInfo.setVisibility(View.VISIBLE);
            llContinueNavi.setVisibility(View.GONE);
            isOverView = false;
            mAMapNaviView.recoverLockMode();
            tvOverview.setText(R.string.overview);
        } else {
            llPathRetainInfo.setVisibility(View.GONE);
            llContinueNavi.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 导航信息更新
     *
     * @param naviInfo 导航信息对象
     */
    private void onNaviInfoUpdateMaas(NaviInfo naviInfo) {
        //车速 单位(km/h)
        tvSpeed.setText(naviInfo.getCurrentSpeed() + "");

        /**
         * 更新下一路口 路名及 距离
         */
        String curStepRetainDistance = "";
        if (naviInfo.getCurStepRetainDistance() > 1000) {
            curStepRetainDistance = String.format(Locale.getDefault(), "%.1f", ((double) naviInfo.getCurStepRetainDistance()) / 1000);
            tvDistanceUnit.setText(R.string.km);
        } else {
            curStepRetainDistance = naviInfo.getCurStepRetainDistance() + "";
            tvDistanceUnit.setText(R.string.m);
        }
        tvCurStepRetainDistance.setText(curStepRetainDistance);
        tvNextRoadName.setText(naviInfo.getNextRoadName());

        /**
         * 整个行程剩余 距离
         * */
        String distance;
        if (naviInfo.getPathRetainDistance() > 1000) {
            distance = String.format(Locale.getDefault(), "%.1f", ((double) naviInfo.getPathRetainDistance()) / 1000);
            tvPathRetainDistanceUnit.setText(R.string.km);
        } else {
            distance = naviInfo.getPathRetainDistance() + "";
            tvPathRetainDistanceUnit.setText(R.string.m);
        }
        tvPathRetainDistance.setText(distance);

        /**
         * 整个行程预计 时间(单位：s)
         * */
        updatePathRetainTime(naviInfo.getPathRetainTime());
    }


    /**
     * 更新行程预计时间
     *
     * @param estimateTime
     */
    private void updatePathRetainTime(long estimateTime) {
        if (estimateTime > 60 * 60) {
            long hours = estimateTime / (60 * 60);
            long minutes = (estimateTime % (60 * 60)) / 60;
            if (minutes == 0) {  //只显示小时
                tvPathRetainTimeHour.setText(hours + "");
                tvPathRetainTimeHour.setVisibility(View.VISIBLE);
                tvPathRetainTimeHourUnit.setVisibility(View.VISIBLE);
                tvPathRetainTimeMinute.setVisibility(View.GONE);
                tvPathRetainTimeMinuteUnit.setVisibility(View.GONE);
                tvPathRetainTimeSecond.setVisibility(View.GONE);
                tvPathRetainTimeSecondUnit.setVisibility(View.GONE);
            } else {
                //显示小时和分钟
                tvPathRetainTimeHour.setText(hours + "");
                tvPathRetainTimeMinute.setText(minutes + "");
                tvPathRetainTimeHour.setVisibility(View.VISIBLE);
                tvPathRetainTimeHourUnit.setVisibility(View.VISIBLE);
                tvPathRetainTimeMinute.setVisibility(View.VISIBLE);
                tvPathRetainTimeMinuteUnit.setVisibility(View.VISIBLE);
                tvPathRetainTimeSecond.setVisibility(View.GONE);
                tvPathRetainTimeSecondUnit.setVisibility(View.GONE);
            }
        } else if (estimateTime > 60) {  //只显示分钟
            long minutes = estimateTime / 60;
            tvPathRetainTimeMinute.setText(minutes + "");
            tvPathRetainTimeHour.setVisibility(View.GONE);
            tvPathRetainTimeHourUnit.setVisibility(View.GONE);
            tvPathRetainTimeMinute.setVisibility(View.VISIBLE);
            tvPathRetainTimeMinuteUnit.setVisibility(View.VISIBLE);
            tvPathRetainTimeSecond.setVisibility(View.GONE);
            tvPathRetainTimeSecondUnit.setVisibility(View.GONE);
        } else { //只显示秒
            tvPathRetainTimeSecond.setText(estimateTime + "");
            tvPathRetainTimeHour.setVisibility(View.GONE);
            tvPathRetainTimeHourUnit.setVisibility(View.GONE);
            tvPathRetainTimeMinute.setVisibility(View.GONE);
            tvPathRetainTimeMinuteUnit.setVisibility(View.GONE);
            tvPathRetainTimeSecond.setVisibility(View.VISIBLE);
            tvPathRetainTimeSecondUnit.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 到达目的地后回调函数。
     * 需要把导航信息置为到达目的地数据
     */
    private void onArriveDestinationMaas() {
        tvCurStepRetainDistance.setText("0");
        tvPathRetainDistance.setText("-");
        tvPathRetainTimeSecond.setText("-");
        tvPathRetainTimeSecond.setText("-");
    }

    /**
     * 规划导航路线
     */
    protected void calculateDriveRoute() {
        /**
         * 方法:
         *   int strategy=mAMapNavi.strategyConvert(congestion, avoidhightspeed, cost, hightspeed, multipleroute);
         * 参数:
         * @congestion 躲避拥堵
         * @avoidhightspeed 不走高速
         * @cost 避免收费
         * @hightspeed 高速优先
         * @multipleroute 多路径
         *
         * 说明:
         *      以上参数都是boolean类型，其中multipleroute参数表示是否多条路线，如果为true则此策略会算出多条路线。
         * 注意:
         *      不走高速与高速优先不能同时为true
         *      高速优先与避免收费不能同时为true
         */
        int strategy = 0;
        try {
            strategy = mAMapNavi.strategyConvert(mNaviSetBean.isCongestion(), mNaviSetBean.isCost(),
                    mNaviSetBean.isAvoidhightspeed(), mNaviSetBean.isHightspeed(), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //不带起点，起点默认为当前位置
        if (eList.size() > 0) {
            mAMapNavi.calculateDriveRoute(eList, null, strategy);
        }
    }

    /**
     * 跳转GPS设置
     */
    private void openGPSSettings() {
        if (!LocationUtils.checkLocationIsOpen(this)) {
            LogUtils.d("定位服务不可用,GPS定位和网络定位都未开启");
            showGpsNotOpenedTipsDialog();
        }
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
        if(mGpsNotOpenedTipsDialog == null){
            mGpsNotOpenedTipsDialog =  ConfirmDialog.newInstance().setMessage(getString(R.string.dialog_msg_gps_not_open))
                    .setOnClickListener(new ConfirmDialog.OnClickListener() {
                        @Override
                        public void onPositiveClick(View view) {
                            //跳转GPS设置界面
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }

                        @Override
                        public void onDismiss() {

                        }
                    })
                    .setPositiveText(getString(R.string.go_open))
                    .setType(ConfirmDialog.TYPE_ONE_BUTTON)
                    .setCancelOutside(false);
        }

        if(mGpsNotOpenedTipsDialog.getDialog()!=null && mGpsNotOpenedTipsDialog.getDialog().isShowing()){
            //dialog is showing so do something
        }else{
            mGpsNotOpenedTipsDialog.show(getSupportFragmentManager(), "");
        }
    }
}
