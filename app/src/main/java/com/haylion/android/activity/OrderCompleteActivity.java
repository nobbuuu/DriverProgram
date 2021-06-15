package com.haylion.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.haylion.android.R;
import com.haylion.android.adapter.GoodsAdapter;
import com.haylion.android.constract.PreSignContract;
import com.haylion.android.data.base.BaseActivity;
import com.haylion.android.data.bean.OrderDetailBean;
import com.haylion.android.data.bean.PhoneBean;
import com.haylion.android.data.util.BusinessUtils;
import com.haylion.android.main.MainActivity;
import com.haylion.android.presenter.PreSignPresenter;
import com.haylion.android.utils.AmapUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.haylion.android.orderdetail.OrderDetailActivity.ORDER_ID;

public class OrderCompleteActivity extends BaseActivity<PreSignContract.Presenter> implements PreSignContract.View {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_title_right)
    TextView tvTitleRight;
    @BindView(R.id.rl_title)
    RelativeLayout rlTitle;
    @BindView(R.id.tv_start_addr)
    TextView tvStartAddr;
    @BindView(R.id.tv_arrive_addr)
    TextView tvArriveAddr;
    @BindView(R.id.rl_top_lay)
    ConstraintLayout rlTopLay;
    @BindView(R.id.goods_rv)
    RecyclerView goodsRv;
    @BindView(R.id.unit_tv1)
    TextView unitTv1;
    @BindView(R.id.distance_tv)
    TextView distance_tv;
    @BindView(R.id.unit_tv2)
    TextView unitTv2;
    @BindView(R.id.arrive_status)
    TextView arriveStatus;
    @BindView(R.id.tv_arrive_time)
    TextView tvArriveTime;
    @BindView(R.id.total_cost)
    TextView totalCost;
    @BindView(R.id.sigle_money)
    TextView sigleMoney;
    @BindView(R.id.today_money)
    TextView todayMoney;
    @BindView(R.id.goods_name)
    TextView goods_name;
    private int mOrderId;
    private int intentTag;

    public static void go(Context context, int orderId, int intentTag) {
        Intent intent = new Intent(context, OrderCompleteActivity.class);
        intent.putExtra(ORDER_ID, orderId);
        intent.putExtra("intentTag", intentTag);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_complete);
        ButterKnife.bind(this);
        mOrderId = getIntent().getIntExtra(ORDER_ID, 0);
        intentTag = getIntent().getIntExtra("intentTag", 0);
        tvTitle.setText("订单完成");
        onEvent();
        presenter.getOrderDetail(mOrderId);
    }

    @Override
    protected PreSignContract.Presenter onCreatePresenter() {
        return new PreSignPresenter(this);
    }

    private void onEvent() {
    }

    @Override
    public void showOrderInfo(OrderDetailBean data) {

        tvStartAddr.setText(data.getDepotStartAddress());
        tvArriveAddr.setText(data.getDepotEndAddress());
        tvArriveTime.setText(OrderPreSignActivity.arriveTime);
        totalCost.setText(OrderPreSignActivity.totalTime);
        List<String> cargoList = data.getCargoList();
        if (cargoList != null && cargoList.size() > 0) {
            Log.d("aaa", "carGoList.size = " + cargoList.size());
            goods_name.setText("货物列表（" + cargoList.size() + "）");
            GoodsAdapter mGoodsAdapter = new GoodsAdapter(this, cargoList, R.layout.rvitem_goods);
            goodsRv.setAdapter(mGoodsAdapter);
        }
        sigleMoney.setText(data.getPrice() / 100 + "");
        double todaySumPrice = data.getTodaySumPrice();
        todayMoney.setText(todaySumPrice / 100 + "");
        LatLng startLatlng = new LatLng(data.getDepotStartLatitude(), data.getDepotStartLongitude());
        LatLng endLatlng = new LatLng(data.getDepotEndLatitude(), data.getDepotEndLongitude());
        AmapUtils.caculateDistance(startLatlng, endLatlng, new RouteSearch.OnRouteSearchListener() {
            @Override
            public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

            }

            @Override
            public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {
                float distance = driveRouteResult.getPaths().get(0).getDistance();
                distance_tv.setText("取送距离" + BusinessUtils.formatDistance(distance));
            }

            @Override
            public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

            }

            @Override
            public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

            }
        });
    }

    @Override
    public void showServicePhoneNum(PhoneBean bean) {

    }

    @OnClick({R.id.iv_back, R.id.tv_title_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.tv_title_right:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (intentTag == 1) {
            Intent intent = new Intent(getContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (intentTag == 2) {
            finish();
        }
    }
}
