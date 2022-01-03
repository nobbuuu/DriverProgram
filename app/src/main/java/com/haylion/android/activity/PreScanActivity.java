package com.haylion.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DistanceResult;
import com.amap.api.services.route.DistanceSearch;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.google.zxing.Result;
import com.haylion.android.R;
import com.haylion.android.adapter.GoodsAdapter;
import com.haylion.android.common.Const;
import com.haylion.android.constract.ClaimActionListener;
import com.haylion.android.constract.PreSignContract;
import com.haylion.android.data.base.BaseActivity;
import com.haylion.android.data.bean.OrderDetailBean;
import com.haylion.android.data.bean.PhoneBean;
import com.haylion.android.data.model.AddressInfo;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.util.BusinessUtils;
import com.haylion.android.data.util.OrderConvert;
import com.haylion.android.data.widgt.SlideView;
import com.haylion.android.dialog.ChoicePhoneDialog;
import com.haylion.android.dialog.ScanEndDialog;
import com.haylion.android.mvp.util.ToastUtils;
import com.haylion.android.orderdetail.OrderDetailActivity;
import com.haylion.android.orderdetail.map.ShowInMapNewActivity;
import com.haylion.android.permissions.OnPermission;
import com.haylion.android.permissions.Permission;
import com.haylion.android.permissions.XXPermissions;
import com.haylion.android.presenter.PreSignPresenter;
import com.haylion.android.scaner.OnRxScanerListener;
import com.haylion.android.utils.AmapUtils;
import com.haylion.android.utils.PhoneUtils;
import com.haylion.android.utils.SpUtils;
import com.king.zxing.CameraScan;
import com.king.zxing.CaptureActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.haylion.android.orderdetail.OrderDetailActivity.ORDER_ID;

public class PreScanActivity extends BaseActivity<PreSignContract.Presenter> implements PreSignContract.View {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.scan_iv)
    ImageView scanIv;
    @BindView(R.id.rl_title)
    RelativeLayout rlTitle;
    @BindView(R.id.tv_arrive_time)
    TextView tvArriveTime;
    @BindView(R.id.taketime_tv)
    TextView taketimeTv;
    @BindView(R.id.tv_arrive_addr)
    TextView tvArriveAddr;
    @BindView(R.id.viewmap_tv)
    TextView viewmapTv;
    @BindView(R.id.rl_info_top)
    ConstraintLayout rlInfoTop;
    @BindView(R.id.phone_number1)
    TextView phoneNumber1;
    @BindView(R.id.phone_take)
    ImageView phoneTake;
    @BindView(R.id.phone_number2)
    TextView phoneNumber2;
    @BindView(R.id.goods_num)
    TextView goods_num;
    @BindView(R.id.phone_sevice)
    ImageView phoneSevice;
    @BindView(R.id.goods_rv)
    RecyclerView goodsRv;
    @BindView(R.id.slideview)
    SlideView slideview;
    private int mOrderId;

    private GoodsAdapter mGoodsAdapter;
    private List<String> mGoodsList = new ArrayList<>();
    private Order mOrder;
    private String mServicePhone;

    public static void go(Context context, int orderId) {
        Intent intent = new Intent(context, PreScanActivity.class);
        intent.putExtra(ORDER_ID, orderId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scangoods);
        ButterKnife.bind(this);
        onEvent();

        mOrderId = getIntent().getIntExtra(ORDER_ID, 0);
        mGoodsAdapter = new GoodsAdapter(this, mGoodsList, R.layout.rvitem_goods);
        goodsRv.setAdapter(mGoodsAdapter);
        presenter.getOrderDetail(mOrderId);
        presenter.getServicePhoneNum();
    }

    @Override
    protected void onResume() {
        super.onResume();
        slideview.reset();
    }

    private void startScanner() {
        XXPermissions.with(this).constantRequest().permission(Permission.Group.CAMERA).request(new OnPermission() {
            @Override
            public void hasPermission(List<String> granted, boolean isAll) {
                if (isAll) {
                    ScannerCodeActivity.start(PreScanActivity.this, new OnRxScanerListener() {
                        @Override
                        public void onSuccess(String type, Result result) {
                            String name = result.getText();
                            for (String code : mGoodsList) {
                                if (name.equals(code)) {
                                    ToastUtils.showLong(getContext(), "该货物已扫描");
                                    return;
                                }
                            }
                            mGoodsList.add(name);
                            mGoodsAdapter.notifyDataSetChanged();
                            goods_num.setText("货物列表（" + mGoodsList.size() + "）");
                            new ScanEndDialog(getContext(), name, new ClaimActionListener() {
                                @Override
                                public void onClaim() {
                                    startScanner();
                                }
                            }).show();
                        }

                        @Override
                        public void onFail(String type, String message) {
                            ToastUtils.showLong(getContext(), message);
                        }
                    });
                    //跳转的默认扫码界面
//                    startActivityForResult(new Intent(getContext(), CaptureActivity.class), 111);
                } else {
                    toast("请先同意权限");
                }
            }

            @Override
            public void noPermission(List<String> denied, boolean quick) {
                if (quick) {

                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case 111:
                    String result = CameraScan.parseScanResult(data);
                    break;
            }

        }
    }

    @Override
    protected PreSignContract.Presenter onCreatePresenter() {
        return new PreSignPresenter(this);
    }

    private void onEvent() {

        slideview.addSlideListener(new SlideView.OnSlideListener() {
            @Override
            public void onSlideSuccess() {
                if (mGoodsList == null || mGoodsList.size() <= 0) {
                    toast("请先扫描货物");
                    slideview.reset();
                    return;
                }
                OrderSignActivity.go(getContext(), mOrderId, 4, mGoodsList);
            }
        });
    }

    @Override
    public void showOrderInfo(OrderDetailBean data) {

        if (data != null) {
            mOrder = OrderConvert.orderShunfengDetailToOrder(data);
            tvArriveTime.setText("预约" + data.getDeliveryTime() + "送达");
            String latitute = (String) SpUtils.getParam(Const.CUR_LATITUTE, "0");
            String longTitute = (String) SpUtils.getParam(Const.CUR_LONGITUDE, "0");
            LatLng startLatLng = null;
            if (latitute.equals("0") || longTitute.equals("0")) {
                startLatLng = new LatLng(data.getDepotStartLatitude(), data.getDepotStartLongitude());
            } else {
                startLatLng = new LatLng(Double.valueOf(latitute), Double.valueOf(longTitute));
            }
            LatLng endLatLng = new LatLng(data.getDepotEndLatitude(), data.getDepotEndLongitude());

            AmapUtils.caculateDistance(startLatLng, endLatLng, new RouteSearch.OnRouteSearchListener() {
                @Override
                public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

                }

                @Override
                public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {
                    float distance = driveRouteResult.getPaths().get(0).getDistance();
                    taketimeTv.setText("送货距离" + BusinessUtils.formatDistance(distance));
                }

                @Override
                public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

                }

                @Override
                public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

                }
            });
            tvArriveAddr.setText(data.getDepotEndDetailAddress());
            List<String> cargoList = data.getCargoList();
            if (cargoList != null && cargoList.size() > 0) {
                mGoodsList.addAll(cargoList);
                mGoodsAdapter.notifyDataSetChanged();
                goods_num.setText("货物列表（" + mGoodsList.size() + "）");
            }
            String mobile = mOrder.getPickupContactMobile();
            String mobile1 = mOrder.getPickupContactMobile1();
            String mobile2 = mOrder.getPickupContactMobile2();
            int sumMobile = 0;
            if (mobile != null && !mobile.isEmpty()) {
                sumMobile++;
            }
            if (mobile1 != null && !mobile1.isEmpty()) {
                sumMobile++;
            }
            if (mobile2 != null && !mobile2.isEmpty()) {
                sumMobile++;
            }

            if (sumMobile != 0) {
                phoneNumber1.setText("取货电话（" + sumMobile + "位联系人)");
            }
        }
    }

    @Override
    public void showServicePhoneNum(PhoneBean bean) {
        mServicePhone = bean.getPhone();
        phoneNumber2.setText("客服电话：" + bean.getPhone());
    }

    @OnClick({R.id.iv_back, R.id.scan_iv, R.id.viewmap_tv, R.id.phone_take, R.id.phone_sevice})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.scan_iv:
                startScanner();
                break;
            case R.id.viewmap_tv:
                if (mOrder != null) {
                    Intent intent = new Intent(getContext(), ShowInMapNewActivity.class);
                    intent.putExtra(ShowInMapNewActivity.EXTRA_GRAB_ENABLED, false);
                    AddressInfo start = mOrder.getStartAddr();
                    String latitute = (String) SpUtils.getParam(Const.CUR_LATITUTE, "0");
                    String longTitute = (String) SpUtils.getParam(Const.CUR_LONGITUDE, "0");
                    if (!latitute.equals("0") && !longTitute.equals("0")) {
                        start = new AddressInfo();
                        start.setLatLng(new LatLng(Double.valueOf(latitute), Double.valueOf(longTitute)));
                        start.setAddressDetail("出发地址");
                    }
                    intent.putExtra(ShowInMapNewActivity.ORDER_START_ADDR, start);
                    AddressInfo end = mOrder.getEndAddr();
                    intent.putExtra(ShowInMapNewActivity.ORDER_END_ADDR, end);
                    intent.putExtra("startAddress", "出发地址");
                    startActivity(intent);
                }
                break;
            case R.id.phone_take:
                if (mOrder != null) {
                    String mobile = mOrder.getPickupContactMobile();
                    String mobile1 = mOrder.getPickupContactMobile1();
                    String mobile2 = mOrder.getPickupContactMobile2();
                    if (mobile.isEmpty() && mobile1.isEmpty() && mobile2.isEmpty()) {
                        ToastUtils.showLong(getContext(), "暂无电话数据");
                        return;
                    }
                    new ChoicePhoneDialog(getContext(), mOrder).show();
                }
                break;
            case R.id.phone_sevice:
                if (!TextUtils.isEmpty(mServicePhone)) {
                    PhoneUtils.call(getContext(), mServicePhone);
                }
                break;
        }
    }
}
