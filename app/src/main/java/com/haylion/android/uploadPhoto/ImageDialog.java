package com.haylion.android.uploadPhoto;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.haylion.android.R;
import com.haylion.android.data.model.Order;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * Created by dengzh on 2019/9/25
 */
public class ImageDialog extends DialogFragment {

    public static final int TYPE_ON_PICTURE = 0;
    public static final int TYPE_OFF_PICTURE = 1;

    @BindView(R.id.iv_show)
    ImageView ivShow;
    @BindView(R.id.btn_reset_photo)
    Button btnResetPhoto;

    private int type = TYPE_ON_PICTURE;  //0-上车 1-下车
    private Order order;
    private Unbinder unbinder;

    public static ImageDialog newInstance() {
        return new ImageDialog();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogFullScreen); //dialog全屏
    }

    @Nullable
    @Override
    public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_image, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        if (type == TYPE_ON_PICTURE) { //上车照片
            Glide.with(this).load(order.getGetOnPicUrl()).into(ivShow);
            //上车照片 只有状态为8-1才可以提交成功
            /*if (order.getOrderStatus() == Order.ORDER_STATUS_ARRIVED_START_ADDR &&
                    order.getOrderSubStatus() == Order.ORDER_SUB_STATUS_ARRIVED_START_ADDR_WAIT_PARENT_CONFIRM) {
                btnResetPhoto.setVisibility(View.VISIBLE);
            }else{
                btnResetPhoto.setVisibility(View.GONE);
            }*/
        } else { //下车照片
            Glide.with(this).load(order.getGetOffPicUrl()).into(ivShow);
            //下车照片 只有状态为9-1才可以提交成功
           /* if (order.getOrderStatus() == Order.ORDER_STATUS_GET_OFF &&
                    order.getOrderSubStatus() == Order.ORDER_SUB_STATUS_GET_OFF_WAIT_PARENT_CONFIRM) {
                btnResetPhoto.setVisibility(View.VISIBLE);
            }else{
                btnResetPhoto.setVisibility(View.GONE);
            }*/
        }
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        if (isAdded()) {
            return;
        }
        super.show(manager, tag);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    /**
     * 设置 Order
     *
     * @param type
     * @param order
     * @return
     */
    public ImageDialog setOrder(int type, Order order) {
        this.type = type;
        this.order = order;
        return this;
    }

    @OnClick({R.id.iv_close, R.id.ll_main, R.id.btn_reset_photo, R.id.iv_show})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_close:
            case R.id.ll_main:
                dismiss();
                break;
            case R.id.btn_reset_photo:
                UploadChildImgActivity.reCommitGo(getActivity(),order.getOrderId(),order.getOrderStatus());
                dismiss();
                break;
            case R.id.iv_show:
                break;
        }
    }
}
