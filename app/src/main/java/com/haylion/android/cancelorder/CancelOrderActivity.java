package com.haylion.android.cancelorder;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.haylion.android.R;
import com.haylion.android.data.model.CancelReason;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.util.BusinessUtils;
import com.haylion.android.data.util.DateUtils;
import com.haylion.android.data.base.ConfirmDialog;
import com.haylion.android.main.MainActivity;
import com.haylion.android.data.base.BaseActivity;
import com.haylion.android.uploadPhoto.LargeImageDialog;
import com.haylion.android.uploadPhoto.UploadImgActivity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CancelOrderActivity extends BaseActivity<CancelOrderContract.Presenter>
        implements CancelOrderContract.View {

    public final static String ORDER_ID = "EXTRA_ORDER_ID";
    private final static String BOARDING_PLACE_ARRIVE_TIME = "BOARDING_PLACE_ARRIVE_TIME";

    @BindView(R.id.iv_pic)
    ImageView ivPic;
    @BindView(R.id.iv_del_pic)
    ImageView ivDelPic;
    @BindView(R.id.rv_reason)
    RecyclerView rvReason;
    @BindView(R.id.ll_pic_header)
    LinearLayout llPicHeader;
    @BindView(R.id.rl_camera)
    RelativeLayout rlCamera;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    /**
     * 订单id
     */
    private int orderId;
    /**
     * 司机到达上车点时间
     */
    private String boardingPlaceArriveTime;
    /**
     * 原因列表
     */
    private List<CancelReason> mList = new ArrayList<>();
    private CancelReasonAdapter mAdapter;
    private CancelReason mReasonBean;
    /**
     * 图片路径
     */
    private String url;

    /**
     * 跳转
     *
     * @param context
     * @param orderId 订单id
     */
    public static void go(Context context, int orderId, String boardingPlaceArriveTime) {
        Intent intent = new Intent(context, CancelOrderActivity.class);
        intent.putExtra(ORDER_ID, orderId);
        intent.putExtra(BOARDING_PLACE_ARRIVE_TIME, boardingPlaceArriveTime);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_order);
        ButterKnife.bind(this);
        initView();
        tvTitle.setText(R.string.cancel_reason);

        orderId = getIntent().getIntExtra(ORDER_ID, 0);
        boardingPlaceArriveTime = getIntent().getStringExtra(BOARDING_PLACE_ARRIVE_TIME);
        presenter.getCancelReasonList();
    }

    private void initView() {
        mAdapter = new CancelReasonAdapter(this, mList);
        mAdapter.setOnItemClickListener(new CancelReasonAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                mReasonBean = mList.get(position);
                if (mReasonBean.getTag() == 0) {
                    llPicHeader.setVisibility(View.GONE);
                    rlCamera.setVisibility(View.GONE);
                } else {
                    llPicHeader.setVisibility(View.VISIBLE);
                    rlCamera.setVisibility(View.VISIBLE);
                }
                btnConfirm.setVisibility(View.VISIBLE);
            }
        });
        rvReason.setHasFixedSize(true);
        rvReason.setLayoutManager(new LinearLayoutManager(this));
        rvReason.setAdapter(mAdapter);
    }

    @OnClick({R.id.iv_back, R.id.iv_camera, R.id.iv_pic, R.id.iv_del_pic, R.id.btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_camera: //拍照
                UploadImgActivity.go(this, 1001);
                break;
            case R.id.iv_pic:  //查看照片
                LargeImageDialog.newInstance().setData(url).isShowNum(false).show(getSupportFragmentManager(), null);
                break;
            case R.id.iv_del_pic:  //删除照片
                url = "";
                ivPic.setVisibility(View.GONE);
                ivDelPic.setVisibility(View.GONE);
                break;
            case R.id.btn_confirm:
                if (mReasonBean == null) {
                    toast(R.string.toast_please_select_cancel_reason);
                    return;
                }
                if (mReasonBean.getTag() == 0) {  //不需要添加取消凭证，需要判断等待时间
                    if (!TextUtils.isEmpty(boardingPlaceArriveTime)) {
                        long milliSecond = 0;
                        try {
                            milliSecond = BusinessUtils.stringToLong(boardingPlaceArriveTime, DateUtils.FORMAT_PATTERN_YMDHMS);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (System.currentTimeMillis() - milliSecond <= 5 * 60 * 1000) {
                            ConfirmDialog.newInstance()
                                    .setMessage(getString(R.string.dialog_msg_waiting_for_passenger_less_than_limit_minutes_to_cancel))
                                    .setOnClickListener(null)
                                    .setPositiveText(R.string.i_got_it)
                                    .setType(1)
                                    .show(getSupportFragmentManager(), "");
                        } else {
                            presenter.cancelOrder(orderId, mReasonBean.getReason(), "");
                        }
                    } else {
                        presenter.cancelOrder(orderId, mReasonBean.getReason(), "");
                    }
                } else {
                    if (TextUtils.isEmpty(url)) {
                        toast(R.string.toast_please_add_cancellation_voucher);
                        return;
                    }
                    presenter.cancelOrder(orderId, mReasonBean.getReason(), url);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1001) {
            url = data.getStringExtra(UploadImgActivity.PIC_URL);
            Glide.with(this).load(url).into(ivPic);
            ivPic.setVisibility(View.VISIBLE);
            ivDelPic.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.getOrderDetail(orderId);
    }

    @Override
    protected CancelOrderContract.Presenter onCreatePresenter() {
        return new OrderCancelPresenter(this);
    }

    /**
     * 获取取消原因列表
     * @param list
     */
    @Override
    public void showCancelReason(List<CancelReason> list) {
        mList.clear();
        mList.addAll(list);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void getOrderDetailSuccess(Order orderInfo) {
        if(orderInfo.getOrderStatus() == Order.ORDER_STATUS_CANCELED){
            //订单已取消
            toast(getString(R.string.order_cancelled));
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }
    }

    @Override
    public void getOrderDetailFail(String msg) {

    }

    /**
     * 取消订单成功
     */
    @Override
    public void cancelOrderSuccess() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
