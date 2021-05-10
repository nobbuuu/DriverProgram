package com.haylion.android.common.map.offlinemap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.maps.AMapException;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.offlinemap.OfflineMapCity;
import com.amap.api.maps.offlinemap.OfflineMapManager;
import com.amap.api.maps.offlinemap.OfflineMapProvince;
import com.amap.api.maps.offlinemap.OfflineMapStatus;
import com.haylion.android.R;
import com.haylion.android.data.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author dengzh
 * @date 2019/11/27
 * Description:离线地图下载页面
 */
public class OfflineMapActivity extends BaseActivity implements OfflineMapManager.OfflineMapDownloadListener, OfflineMapManager.OfflineLoadedListener {

    @BindView(R.id.tv_status)
    TextView tvStatus;
    private String TAG = getClass().getSimpleName();

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private OfflineMapManager amapManager = null;// 离线地图下载控制器
    private OfflineMapAdapter mAdapter;
    private ArrayList<OfflineMapCity> cityList = new ArrayList<OfflineMapCity>();

    public static void go(Context context) {
        Intent intent = new Intent(context, OfflineMapActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_map);
        ButterKnife.bind(this);
        tvTitle.setText("离线地图下载");

        MapsInitializer.sdcardDir = OffLineMapUtils.getSdCacheDir(this);
        //构造离线地图类
        amapManager = new OfflineMapManager(this, this);
        //离线地图初始化完成监听
        amapManager.setOnOfflineLoadedListener(this);

        initView();
        initData();
    }

    private void initView() {
        mAdapter = new OfflineMapAdapter(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new OfflineMapAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                toast(cityList.get(position).getCity());
                try {
                    //开始下载
                    amapManager.downloadByCityName(cityList.get(position).getCity());
                } catch (AMapException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initData() {
        cityList.clear();
        //从列表里找出广东省的城市
        List<OfflineMapProvince> lists = amapManager.getOfflineMapProvinceList();
        for (int i = 0; i < lists.size(); i++) {
            OfflineMapProvince province = lists.get(i);
            if (province.getCityList().size() != 1) {
                // 普通省份
                String name = province.getProvinceName(); //省份名字
                if (name.contains("广东省")) { //城市
                    cityList.addAll(province.getCityList());
                }
            }
        }
        mAdapter.setData(cityList);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 当调用OfflineMapManager初始化方法时，如果有设置监听，离线地图列表初始化完成会回调此方法
     */
    @Override
    public void onVerifyComplete() {
        Log.e(TAG, "onVerifyComplete()");
        initData();
    }

    /**
     * 下载状态回调，在调用downloadByCityName 等下载方法的时候会启动
     *
     * @param status       状态
     * @param completeCode 下载进度，下载完成之后为解压进度
     * @param downName         当前所下载的城市的名字
     */
    @Override
    public void onDownload(int status, int completeCode, String downName) {
        switch (status) {
            case OfflineMapStatus.SUCCESS: //下载成功
                tvStatus.setText("下载成功");
                Log.e(TAG,"下载成功");
                // changeOfflineMapTitle(OfflineMapStatus.SUCCESS, downName);
                break;
            case OfflineMapStatus.LOADING: //正在下载
                tvStatus.setText("download: " + completeCode + "%" + "," + downName);
                Log.e(TAG,"download: " + completeCode + "%" + "," + downName);
                // changeOfflineMapTitle(OfflineMapStatus.LOADING, downName);
                break;
            case OfflineMapStatus.UNZIP:  //正在解压
                tvStatus.setText("unzip: " + completeCode + "%" + "," + downName);
                Log.e(TAG,"unzip: " + completeCode + "%" + "," + downName);
                // changeOfflineMapTitle(OfflineMapStatus.UNZIP);
                // changeOfflineMapTitle(OfflineMapStatus.UNZIP, downName);
                break;
            case OfflineMapStatus.WAITING: //等待下载
                tvStatus.setText("WAITING: " + completeCode + "%" + "," + downName);
                Log.e(TAG,"WAITING: " + completeCode + "%" + "," + downName);
                break;
            case OfflineMapStatus.PAUSE: //暂停下载
                tvStatus.setText("pause: " + completeCode + "%" + "," + downName);
                Log.e(TAG,"pause: " + completeCode + "%" + "," + downName);
                break;
            case OfflineMapStatus.STOP: //停止下载
                tvStatus.setText("停止下载");
                Log.e(TAG,"下载成功");
                break;
            case OfflineMapStatus.ERROR: //解压失败错误，数据有可能有问题，所以重新下载
                tvStatus.setText("download: " + " ERROR " + downName);
                Log.e(TAG,"download: " + " ERROR " + downName);
                break;
            case OfflineMapStatus.EXCEPTION_AMAP: //AMap认证等异常，请检查key，下次还可以继续下载
                tvStatus.setText("download: " + " EXCEPTION_AMAP " + downName);
                Log.e(TAG,"download: " + " EXCEPTION_AMAP " + downName);
                break;
            case OfflineMapStatus.EXCEPTION_NETWORK_LOADING: //下载过程中网络问题，不属于错误，下次还可以继续下载
                tvStatus.setText("download: " + " EXCEPTION_NETWORK_LOADING " + downName);
                Log.e(TAG,"download: " + " EXCEPTION_NETWORK_LOADING " + downName);
                amapManager.pause();
                break;
            case OfflineMapStatus.EXCEPTION_SDCARD: //SD卡读写异常,下载过程有写入文件，解压过程也有写入文件即出现IOexception
                tvStatus.setText("download: " + " EXCEPTION_SDCARD " + downName);
                Log.e(TAG,"download: " + " EXCEPTION_SDCARD " + downName);
                break;
            default:
                break;
        }

    }

    /**
     * 当调用updateOfflineMapCity 等检查更新函数的时候会被调用
     *
     * @param hasNew true表示有更新，说明官方有新版或者本地未下载
     * @param name   被检测更新的城市的名字
     */
    @Override
    public void onCheckUpdate(boolean hasNew, String name) {
        Log.e(TAG, "onCheckUpdate()");
    }

    /**
     * 当调用OfflineMapManager.remove(String)方法时，如果有设置监听，会回调此方法
     * 当删除省份时，该方法会被调用多次，返回省份内城市删除情况。
     *
     * @param success  删除成功
     * @param name     所删除的城市的名字
     * @param describe 删除描述，如 删除成功 "本地无数据"
     */
    @Override
    public void onRemove(boolean success, String name, String describe) {
        Log.e(TAG, "onRemove()");
    }


    /**
     * 暂停所有下载和等待
     */
    private void stopAll() {
        if (amapManager != null) {
            amapManager.stop();
        }
    }


    /**
     * 继续下载所有暂停中
     */
    private void startAllInPause() {
        if (amapManager == null) {
            return;
        }
        for (OfflineMapCity mapCity : amapManager.getDownloadingCityList()) {
            if (mapCity.getState() == OfflineMapStatus.PAUSE) {
                try {
                    amapManager.downloadByCityName(mapCity.getCity());
                } catch (AMapException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 取消所有<br>
     * 即：删除下载列表中除了已完成的所有<br>
     * 会在OfflineMapDownloadListener.onRemove接口中回调是否取消（删除）成功
     */
    private void cancelAll() {
        if (amapManager == null) {
            return;
        }
        for (OfflineMapCity mapCity : amapManager.getDownloadingCityList()) {
            if (mapCity.getState() == OfflineMapStatus.PAUSE) {
                amapManager.remove(mapCity.getCity());
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (amapManager != null) {
            amapManager.destroy();
        }
    }

    @OnClick({R.id.iv_back, R.id.btn_download})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_download: //下载地图

                break;
        }
    }
}
