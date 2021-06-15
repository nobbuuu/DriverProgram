package com.haylion.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.haylion.android.R;
import com.haylion.android.common.Const;
import com.haylion.android.constract.SignContract;
import com.haylion.android.customview.SignatureView;
import com.haylion.android.data.base.BaseActivity;
import com.haylion.android.data.bean.ChangeOrderStatusBean;
import com.haylion.android.orderdetail.amapNavi.AMapNaviViewActivity;
import com.haylion.android.presenter.SignPresenter;
import com.haylion.android.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrderSignActivity extends BaseActivity<SignContract.Presenter> implements SignContract.View {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.signature_view)
    SignatureView signatureView;
    @BindView(R.id.sure_tv)
    TextView sureTv;
    private int mOrderId;

    private int mOrderStatus;
    private List<String> cargoList;

    public static void go(Context context, int orderId, int orderStatus, List<String> list) {
        Intent intent = new Intent(context, OrderSignActivity.class);
        intent.putExtra(Const.ORDER_ID, orderId);
        intent.putExtra(Const.ORDER_STATUS, orderStatus);
        intent.putStringArrayListExtra(Const.CARGO_LIST, (ArrayList<String>) list);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        ButterKnife.bind(this);
        mOrderId = getIntent().getIntExtra(Const.ORDER_ID, 0);
        mOrderStatus = getIntent().getIntExtra(Const.ORDER_STATUS, 0);
        cargoList = getIntent().getStringArrayListExtra(Const.CARGO_LIST);
        onEvent();
    }

    @Override
    protected SignContract.Presenter onCreatePresenter() {
        return new SignPresenter(this);
    }

    private void onEvent() {
    }

    @OnClick({R.id.iv_back, R.id.sure_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.sure_tv:
                try {
                    String mImagePath = FileUtils.getImageFilePath();
                    FileUtils.checkOrCreateDirectory(mImagePath);
                    signatureView.save(mImagePath);
                    File bitmapFile = signatureView.getBitmapFile();
                    Log.d("aaa", "bitmapFile = " + bitmapFile);
                    presenter.uploadImg(bitmapFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void showUpload(String url) {
        Log.d("aaa", "action showUpload url = " + url);
        presenter.updateOrder(new ChangeOrderStatusBean(mOrderId, mOrderStatus, url, cargoList));
    }

    @Override
    public void showUpdateOrder(boolean result) {
        Log.d("aaa", "action showUpdateOrder result = " + result);
        if (result) {
            if (mOrderStatus == 4) {//送货中
                AMapNaviViewActivity.go(getContext(), mOrderId, -1);
            } else if (mOrderStatus == 6) {//已完成
                OrderCompleteActivity.go(getContext(), mOrderId,1);
            }
            finish();
        }
    }
}
