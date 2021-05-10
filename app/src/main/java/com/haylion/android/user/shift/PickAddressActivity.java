package com.haylion.android.user.shift;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;

import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.haylion.android.R;
import com.haylion.android.data.base.BaseActivity;
import com.haylion.android.data.model.AddressInfo;
import com.haylion.android.mvp.util.LogUtils;
import com.haylion.android.pay.PayMainActivity;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import io.nlopez.smartadapters.SmartAdapter;
import io.nlopez.smartadapters.adapters.RecyclerMultiAdapter;
import io.nlopez.smartadapters.utils.ViewEventListener;

public class PickAddressActivity extends BaseActivity implements
        ViewEventListener<PoiItem> {

    @OnClick({
            R.id.btn_back, R.id.pick_address, R.id.current_location
    })
    void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.pick_address:
                returnAddressInfo();
                break;
            case R.id.current_location:
                if (checkLatLng(mCurrLocation)) {
                    moveMapCamera(mCurrLocation);
                    doPoiSearch(null, mCurrLocation);
                }
                break;
        }
    }

    @BindView(R.id.map_view)
    MapView mMapView;

    @BindView(R.id.address_keyword)
    EditText mAddressKeyword;

    @OnEditorAction(R.id.address_keyword)
    boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            String keyword = view.getText().toString();
            if (TextUtils.isEmpty(keyword)) {
                toast("请输入关键字");
            } else {
                PayMainActivity.hideSoftInput(this);
                doPoiSearch(keyword, null);
            }
            return true;
        }
        return false;
    }

    @BindView(R.id.selected_address)
    TextView mSelectedAddress;

    @BindView(R.id.address_list)
    RecyclerView mAddressList;
    private RecyclerMultiAdapter mAddressAdapter;

    private AMap mAmap;
    private LatLng mCurrLocation;

    private PoiItem mSelectedPoi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_address);
        mMapView.onCreate(savedInstanceState);
        initMapView();
        mAddressAdapter = SmartAdapter.empty()
                .map(PoiItem.class, SearchAddressItemView.class)
                .listener(this)
                .into(mAddressList);
    }

    private void initMapView() {
        mAmap = mMapView.getMap();
        // 默认将地图移动到深圳市
        moveMapCamera(new LatLng(22.543527, 114.057939));
        UiSettings uiSettings = mAmap.getUiSettings();
        // 目前不支持拖动地图搜索，禁用所有手势，只做展示
        uiSettings.setAllGesturesEnabled(false);

        uiSettings.setRotateGesturesEnabled(false);
        uiSettings.setTiltGesturesEnabled(false);
        uiSettings.setZoomControlsEnabled(false);
        uiSettings.setLogoBottomMargin(-64);
        mAmap.setOnMyLocationChangeListener(location -> {
            if (location != null) {
                mCurrLocation = new LatLng(location.getLatitude(),
                        location.getLongitude());
                if (checkLatLng(mCurrLocation)) {
                    doPoiSearch(null, mCurrLocation);
                }
            }
        });
        mAmap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                if (cameraPosition.target != null) {
//                    doPoiSearch(cameraPosition.target);
                } else {
                    LogUtils.e("地图中心位置数据为空");
                }
            }
        });
        MyLocationStyle locationStyle = new MyLocationStyle();
        locationStyle.strokeColor(Color.TRANSPARENT);
        locationStyle.radiusFillColor(Color.TRANSPARENT);
        locationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
        locationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_my_location));
        locationStyle.showMyLocation(true);
        mAmap.setMyLocationStyle(locationStyle);
        mAmap.setMyLocationEnabled(true);
    }

    private boolean checkLatLng(LatLng latLng) {
        if (latLng == null) {
            return false;
        }
        return latLng.latitude != 0 || latLng.longitude != 0;
    }

    private void moveMapCamera(LatLng latLng) {
        if (mAmap == null) {
            return;
        }
        mAmap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    private void doPoiSearch(String keyword, LatLng latLng) {
        PoiSearch.Query poiQuery;
        if (TextUtils.isEmpty(keyword)) {
            poiQuery = new PoiSearch.Query("", "", "");
        } else {
            poiQuery = new PoiSearch.Query(keyword, "", "深圳市");
        }
        poiQuery.setPageNum(20);
        poiQuery.setPageNum(1);
        PoiSearch poiSearch = new PoiSearch(this, poiQuery);
        if (latLng != null) {
            poiSearch.setBound(new PoiSearch.SearchBound(
                    new LatLonPoint(latLng.latitude, latLng.longitude), 1000
            )); // 1000米半径范围内
        }
        poiSearch.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {
            @Override
            public void onPoiSearched(PoiResult poiResult, int errorCode) {
                if (errorCode == 1000) {
                    mSelectedAddress.setVisibility(View.GONE);
                    List<PoiItem> poiItems = poiResult.getPois();
                    mAddressAdapter.setItems(poiItems);
                    if (poiItems != null && !poiItems.isEmpty()) {
                        mAddressList.post(() -> {
                            LinearLayoutManager layoutManager = (LinearLayoutManager)
                                    mAddressList.getLayoutManager();
                            if (layoutManager != null) {
                                layoutManager.scrollToPositionWithOffset(0, 0);
                            }
                        });
                    }
                } else {
                    toast("搜索失败");
                    LogUtils.e("poi搜索失败：" + errorCode);
                }
            }

            @Override
            public void onPoiItemSearched(PoiItem poiItem, int errorCode) {

            }
        });
        poiSearch.searchPOIAsyn();
    }

    @Override
    public void onViewEvent(int actionType, PoiItem poiItem, int position, View view) {
        if (actionType == SearchAddressItemView.ACTION_SELECT_ADDRESS) {
            mSelectedPoi = poiItem;
            mSelectedAddress.setVisibility(View.VISIBLE);
            mSelectedAddress.setText(poiItem.getTitle());
            LatLng selLatLng = buildLatLng(poiItem);
            moveMapCamera(selLatLng);
        }
    }

    private LatLng buildLatLng(PoiItem poiItem) {
        if (poiItem == null) {
            return null;
        }
        LatLonPoint point = poiItem.getLatLonPoint();
        return new LatLng(point.getLatitude(),
                point.getLongitude());
    }

    private void returnAddressInfo() {
        if (mSelectedPoi == null) {
            toast("请选择一个地址");
            return;
        }
        AddressInfo addressInfo = new AddressInfo();
        addressInfo.setName(mSelectedPoi.getTitle());
        addressInfo.setAddressDetail(mSelectedPoi.getSnippet());
        addressInfo.setLatLng(buildLatLng(mSelectedPoi));

        Intent intent = new Intent();
        intent.putExtra(EXTRA_ADDRESS, addressInfo);
        setResult(RESULT_OK, intent);
        finish();
    }

    public static AddressInfo parseAddress(Intent intent) {
        if (intent == null) {
            return null;
        }
        return intent.getParcelableExtra(EXTRA_ADDRESS);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    private static final String EXTRA_ADDRESS = "address";


}
