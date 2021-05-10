package com.haylion.android.orderdetail.trip;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haylion.android.R;
import com.haylion.android.cancelorder.CancelOrderActivity;
import com.haylion.android.common.view.dialog.DialogUtils;
import com.haylion.android.common.view.dialog.OrderAbnormalFinishDialog;
import com.haylion.android.data.base.BaseActivity;
import com.haylion.android.data.base.ConfirmDialog;
import com.haylion.android.data.model.CarpoolOrderExt;
import com.haylion.android.data.model.ChildInfo;
import com.haylion.android.data.model.EventMsg;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.model.OrderTypeInfo;
import com.haylion.android.data.repo.OrderRepository;
import com.haylion.android.data.util.BusinessUtils;
import com.haylion.android.data.util.OrderConvert;
import com.haylion.android.main.MainActivity;
import com.haylion.android.mvp.base.AppManager;
import com.haylion.android.mvp.util.LogUtils;
import com.haylion.android.orderdetail.ContactParentsChooser;
import com.haylion.android.orderdetail.OrderDetailActivity;
import com.haylion.android.orderlist.CancelAppointmentDialog;
import com.haylion.android.uploadPhoto.ImageDialog;
import com.haylion.android.uploadPhoto.LargeImageDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * @author dengzh
 * @date 2019/10/23
 * Description: 行程操作页 - 行程中
 * 将订单详情页的详细信息和一些操作放到此页面处理。
 * <p>
 * 行程中可操作的逻辑
 * 所有订单：转单，取消。
 * 小孩单：查看乘客头像，上车照片，下车照片。
 * <p>
 * <p>
 * 3.需要实时监听这个订单状态，是否取消，是否支付完成。
 */
public class TripOperateActivity extends BaseActivity<TripContract.Presenter>
        implements TripContract.View {

    private final String TAG = getClass().getSimpleName();

    public final static String ORDER_ID = "EXTRA_ORDER_ID";

    @BindView(R.id.tv_title)
    TextView tvTitle;

    /**
     * 订单时间和上下车地点
     */
    @BindView(R.id.tv_order_time)
    TextView tvOrderTime; //下单时间
    @BindView(R.id.tv_get_on_addr)
    TextView tvOrderGetOn; //上车地址
    @BindView(R.id.tv_get_off_addr)
    TextView tvOrderGetOff; //下车地址

    /**
     * 通用订单信息
     */
    @BindView(R.id.tv_order_type)
    TextView tvOrderType;  //订单类型
    @BindView(R.id.tv_order_number)
    TextView tvOrderNumber; //订单编号
    @BindView(R.id.tv_click_order_cancel)
    TextView tvClickOrderCancel; //取消订单按钮
    @BindView(R.id.tv_child_transfer_order)
    TextView tvChildTransferOrder;      //转单

    /**
     * 送你上学单
     */
    @BindView(R.id.ll_child_info)
    LinearLayout llChildInfo;
    @BindView(R.id.tv_child_get_on_pic)
    TextView tvChildGetOnPic;           //查看上车照片
    @BindView(R.id.tv_child_header)
    TextView tvChildHeader;              //查看头像
    @BindView(R.id.tv_child_get_off_pic)
    TextView tvChildGetOffPic;           //查看下车照片
    @BindView(R.id.tv_child_names)
    TextView tvChildNames;              //孩子和监护人名字
    @BindView(R.id.ll_child_message)
    LinearLayout llChildMessage;         //备注信息
    @BindView(R.id.tv_child_message)
    TextView tvChildMessage;            //备注信息


    /**
     * 底部电话
     */
    @BindView(R.id.ll_contact_passenger)
    LinearLayout llContactPassenger;  //联系乘客
    @BindView(R.id.tv_phone_number)
    TextView tvOrderPhone;  //手机号

    /**
     * 孩子单的联系电话
     */
    @BindView(R.id.ll_child_contact_container)
    LinearLayout llChildContactContainer;   //底部联系小孩和监护人布局
    @BindView(R.id.tv_child_contact)
    TextView tvChildContact;               //联系小孩
    @BindView(R.id.ll_child_contact)
    LinearLayout llChildContact;
    @BindView(R.id.line_child_contact)
    View lineChildContact;             //分割线
    @BindView(R.id.iv_bottom_shadow)
    ImageView ivBottomShadow;

    /*
     *异常结束订单
     */
    @BindView(R.id.tv_order_abnormal_finish)
    TextView tvAbnormalFinishOrder;

    /**
     * 订单
     */
    private Order order;
    private int orderId;

    /**
     * 拼车码
     */
    private String carpoolCode;
    /**
     * 拼车标记
     */
    private boolean carpoolFlag = false;

    /**
     * 跳转入口
     *
     * @param context
     * @param orderId 订单id
     */
    public static void go(Context context, int orderId) {
        Intent intent = new Intent(context, TripOperateActivity.class);
        intent.putExtra(ORDER_ID, orderId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_operate);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        tvTitle.setText(R.string.trip_detail);

        carpoolFlag = getIntent().getBooleanExtra(OrderDetailActivity.EXTRA_CARPOOL_FLAG, false);
        if (carpoolFlag) {
            carpoolCode = getIntent().getStringExtra(OrderDetailActivity.EXTRA_CARPOOL_CODE);
        } else {
            orderId = getIntent().getIntExtra(ORDER_ID, 0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (carpoolFlag) {
            presenter.getCarpoolOrderDetails(carpoolCode);
        } else {
            presenter.getOrderDetail(orderId);
        }
    }

    /**
     * 通用 点击事件
     *
     * @param view
     */
    @OnClick({R.id.iv_back, R.id.ll_contact_passenger, R.id.tv_click_order_cancel, R.id.tv_order_abnormal_finish})
    public void onButtonClick(View view) {
        if (isFastClick()) {
            return;
        }
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.ll_contact_passenger:   //联系乘客
                if (order == null) {
                    return;
                }
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
                break;
            case R.id.tv_click_order_cancel:  //取消订单
                if (order == null) {
                    toast("没有订单信息");
                    return;
                }
                if (order.getOrderType() == Order.ORDER_TYPE_BOOK) { // 预约单弹窗，填写自定义取消原因
                    CancelAppointmentDialog dialog = new CancelAppointmentDialog(getContext(), order);
                    dialog.setCallback(this::exceptionFinishOrderSuccess);
                    dialog.show();
                } else {
                    CancelOrderActivity.go(this, order.getOrderId(), order.getBoardingPlaceArriveTime());
                }
                break;
            case R.id.tv_order_abnormal_finish:
                showAbnormalFinishOrderDialog();
                break;
            default:
                break;
        }
    }

    /**
     * @param
     * @return
     * @method
     * @description 异常结束当前订单
     * @date: 2020/2/19 15:17
     * @author: tandongdong
     */
    private void showAbnormalFinishOrderDialog() {
        OrderAbnormalFinishDialog.CallBack callBack = new OrderAbnormalFinishDialog.CallBack() {
            @Override
            public void exceptionFinishOrder(double taxiFee, String reason) {
                presenter.exceptionFinishOrder(order.getOrderId(), reason, taxiFee);
            }
        };

        OrderAbnormalFinishDialog dialog = new OrderAbnormalFinishDialog(AppManager.getAppManager().currentActivity());
        dialog.setData(order, callBack);
        dialog.show();
    }

    /**
     * 异常结束订单成功
     */
    @Override
    public void exceptionFinishOrderSuccess() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected TripContract.Presenter onCreatePresenter() {
        return new TripPresenter(this);
    }

    /**
     * 获取订单详情失败
     *
     * @param msg
     */
    @Override
    public void getOrderDetailFail(String msg) {
        finish();
    }

    /**
     * 确认支付成功
     */
    @Override
    public void payConfirmSuccess() {

    }

    /**
     * 获取客服电话成功
     *
     * @param phoneNum
     */
    @Override
    public void getServiceTelNumberSuccess(String phoneNum, boolean isNeedShowDialog) {
        DialogUtils.showCustomerServiceCallDialog(this, phoneNum, isNeedShowDialog);
    }

    @Override
    public void updateCargoOrderRestTime(String deadTime) {

    }

    /**
     * 获取订单详情成功
     *
     * @param orderInfo
     */
    @Override
    public void getOrderDetailSuccess(Order orderInfo) {
        if (orderInfo != null) {
            order = orderInfo;
            if (order.getOrderStatus() == Order.ORDER_STATUS_CANCELED) { //订单已取消
                finish();
            }
            String timeString = "";
            try {
                if (order.getOrderTime() != null) {
                    long milliSecond = BusinessUtils.stringToLong(order.getOrderTime(), "yyyy-MM-dd HH:mm");
                    timeString = BusinessUtils.getDateToStringIncludeYearWhenCrossYear(milliSecond, null);
                    Log.d(TAG, "" + "timeString: " + timeString);
                    tvOrderTime.setText(timeString);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            tvOrderGetOn.setText(order.getStartAddr().getName());
            tvOrderGetOff.setText(order.getEndAddr().getName());
            if (order.getOrderType() == Order.ORDER_TYPE_REALTIME_CARPOOL) {
                tvOrderType.setText(OrderTypeInfo.getStatusText(order.getOrderType(), order.getChannel()) + "(" + order.getPassengerNum() + "人)");
            } else {
                tvOrderType.setText(OrderTypeInfo.getStatusText(order.getOrderType(), order.getChannel()));
            }
            tvOrderNumber.setText(order.getOrderCode());

            //送你上学订单，等待家长确认上车照片和乘客已上车状态，需要显示异常结束的按钮。
//            if (order.getOrderType() == Order.ORDER_TYPE_SEND_CHILD) {
//                if (order.getOrderStatus() == Order.ORDER_STATUS_GET_ON
//                        || (order.getOrderStatus() == Order.ORDER_STATUS_ARRIVED_START_ADDR
//                        && order.getOrderSubStatus() == Order.ORDER_SUB_STATUS_ARRIVED_START_ADDR_WAIT_PARENT_CONFIRM)) {
//                    LinearLayout llExceptionFinishOrder = findViewById(R.id.ll_exception_finish_order);
//                    llExceptionFinishOrder.setVisibility(View.VISIBLE);
//                }
//            } // 2020.4.21，应产品要求，“送你上学”订单暂不显示“异常结束”入口

            if (order.getOrderType() == Order.ORDER_TYPE_REALTIME ||
                    order.getOrderType() == Order.ORDER_TYPE_CARGO_PASSENGER ||
                    order.getOrderType() == Order.ORDER_TYPE_REALTIME_CARPOOL ||
                    order.getOrderType() == Order.ORDER_TYPE_BOOK) { // 2020.4.16，增加预约单的判断
                //实时订单 or 货拼客 显示
                updateOrderInfoCardView();
            } else if (order.getOrderType() == Order.ORDER_TYPE_SEND_CHILD) {
                //送你上学单
                updateChildOrderInfoView();
            }
        }
    }

    /**
     * 实时订单/货拼客 InfoView
     */
    private void updateOrderInfoCardView() {
        //取消订单按钮
        if (order.getOrderType() == Order.ORDER_TYPE_REALTIME ||
                order.getOrderType() == Order.ORDER_TYPE_CARGO_PASSENGER) {
            //实时订单，只有已到达上车点，乘客未上车，显示取消按钮
            if (order.getOrderStatus() == Order.ORDER_STATUS_ARRIVED_START_ADDR) {
                tvClickOrderCancel.setVisibility(View.VISIBLE);
            } else {
                tvClickOrderCancel.setVisibility(View.GONE);
            }
        } else if (order.getOrderType() == Order.ORDER_TYPE_BOOK) {
            // 预约订单，开始订单后即不可取消
            if (order.getOrderStatus() == Order.ORDER_STATUS_READY) {
                tvClickOrderCancel.setVisibility(View.VISIBLE);
            } else {
                tvClickOrderCancel.setVisibility(View.GONE);
            }
        }

        //电话
        llContactPassenger.setVisibility(View.VISIBLE);
        if (order.getUserInfo().getPhoneNum() != null) {
            tvOrderPhone.setText(order.getUserInfo().getPhoneNum().substring(7));
        }
    }

    /**
     * 送你上学单 InfoView
     * 1.转单按钮，只在“去接乘客”前 超过12小时 显示。
     * 2.小孩头像，只在“乘客已上车”前 显示。
     * 3.上下车照片，不为空就显示。
     * <p>
     * 4.联系小孩，只在“去送乘客”前显示。
     * 5.联系客服，只在“等待家长确认乘客下车照片”时 显示。
     */
    private void updateChildOrderInfoView() {
        llChildInfo.setVisibility(View.VISIBLE);
        llChildContactContainer.setVisibility(View.VISIBLE);
        ivBottomShadow.setVisibility(View.VISIBLE);
        //孩子和监护人姓名
        tvChildNames.setText(OrderConvert.getChildNames(order));
        //留言
        if (!TextUtils.isEmpty(order.getParentMessage())) {
            tvChildMessage.setText(order.getParentMessage());
            llChildMessage.setVisibility(View.VISIBLE);
        }
        //上下车照片
        if (!TextUtils.isEmpty(order.getGetOnPicUrl())) {
            tvChildGetOnPic.setVisibility(View.VISIBLE);
        }
        if (!TextUtils.isEmpty(order.getGetOffPicUrl())) {
            tvChildGetOffPic.setVisibility(View.VISIBLE);
        }
        //判断距离用车前多长时间
        long orderTimeMilliSecond = 0;
        try {
            orderTimeMilliSecond = BusinessUtils.stringToLong(order.getOrderTime(), "yyyy-MM-dd HH:mm");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long currentTime = System.currentTimeMillis();

        tvChildHeader.setVisibility(View.VISIBLE);
        //各个订单状态下，一些控件的显示和隐藏
        switch (order.getOrderStatus()) {
            case Order.ORDER_STATUS_INIT:  //订单初始化
            case Order.ORDER_STATUS_READY:  //订单准备中
                if (orderTimeMilliSecond - currentTime < 12 * 3600 * 1000) {
                    tvChildTransferOrder.setVisibility(View.GONE);
                } else {
                    tvChildTransferOrder.setVisibility(View.VISIBLE);
                }
                break;
            case Order.ORDER_STATUS_WAIT_CAR:  //去接乘客，尚未到达上车地点
            case Order.ORDER_STATUS_ARRIVED_START_ADDR:  //已到达上车点，乘客未上车
                tvChildTransferOrder.setVisibility(View.GONE);
                break;
            case Order.ORDER_STATUS_GET_ON:            //（去送乘客）乘客已上车，尚未到达目的地
                tvChildTransferOrder.setVisibility(View.GONE);
                //此后不再显示 联系小孩
                llChildContact.setVisibility(View.GONE);
                lineChildContact.setVisibility(View.GONE);
                break;
            case Order.ORDER_STATUS_GET_OFF:          //到达目的地，尚未设置收款金额
                tvChildTransferOrder.setVisibility(View.GONE);
                if (order.getOrderSubStatus() == Order.ORDER_SUB_STATUS_GET_OFF_WAIT_PARENT_CONFIRM) {
                    //等待乘客确认下车,显示联系客服
                    tvChildContact.setText(R.string.contact_customer_service);
                    llChildContact.setVisibility(View.VISIBLE);
                } else {
                    //乘客已确认下车
                    llChildContact.setVisibility(View.GONE);
                }
                break;
            case Order.ORDER_STATUS_WAIT_PAY:       //乘客未付款
            case Order.ORDER_STATUS_CLOSED:        //订单完成，乘客已付款
            case Order.ORDER_STATUS_CANCELED:      //取消订单
                break;
            default:
                break;
        }
    }

    /**
     * 送你上学单 点击事件处理
     *
     * @param view
     */
    @OnClick({R.id.tv_child_transfer_order, R.id.tv_child_get_on_pic, R.id.tv_child_header, R.id.tv_child_get_off_pic,
            R.id.tv_child_contact, R.id.ll_child_contact, R.id.ll_child_parent_contact})
    public void onChildClick(View view) {
        if (order == null) {
            return;
        }
        switch (view.getId()) {
            case R.id.tv_child_transfer_order:  //转单
                presenter.getServiceTelNumber(true);
                break;
            case R.id.tv_child_get_on_pic:  //上车照片
                ImageDialog.newInstance().setOrder(ImageDialog.TYPE_ON_PICTURE, order).show(getSupportFragmentManager(), null);
                break;
            case R.id.tv_child_get_off_pic:  //下车照片
                ImageDialog.newInstance().setOrder(ImageDialog.TYPE_OFF_PICTURE, order).show(getSupportFragmentManager(), null);
                break;
            case R.id.tv_child_header:    //乘客头像
                List<String> mPhotoList = OrderConvert.getChildPhotos(order);
                if (mPhotoList.size() > 0) {
                    LargeImageDialog.newInstance().setData(mPhotoList).show(getSupportFragmentManager(), null);
                } else {
                    toast(R.string.toast_avatar_is_empty);
                }
                break;
            case R.id.tv_child_contact:
            case R.id.ll_child_contact: //联系小孩 or 联系客服，处于 到达未确认下车照片 状态下，显示联系客服
                if (order.getOrderStatus() == Order.ORDER_STATUS_GET_OFF
                        && order.getOrderSubStatus() == Order.ORDER_SUB_STATUS_GET_OFF_WAIT_PARENT_CONFIRM) {
                    presenter.getServiceTelNumber(true);
                } else {
                    if (carpoolFlag) {
                        List<String> childPhones = new ArrayList<>();
                        for (ChildInfo childInfo : order.getChildList()) {
                            childPhones.add(childInfo.getMobile());
                        }
                        ContactParentsChooser chooser = new ContactParentsChooser(getContext(), phoneNumber -> {
                            DialogUtils.showRealCallDialog(getContext(), phoneNumber);
                        });
                        chooser.setData("联系小孩", childPhones);
                        chooser.showAtLocation(llChildContactContainer, Gravity.BOTTOM, 0, 0);
                    } else {
                        DialogUtils.showRealCallDialog(this, order.getChildList().get(0).getMobile());
                    }
                }
                break;  //联系家长
            case R.id.ll_child_parent_contact:
                if (carpoolFlag) {
                    ContactParentsChooser chooser = new ContactParentsChooser(getContext(), phoneNumber -> {
                        DialogUtils.showRealCallDialog(getContext(), phoneNumber);
                    });
                    chooser.setData("联系家长", presenter.getParentPhones());
                    chooser.showAtLocation(llChildContact, Gravity.BOTTOM, 0, 0);
                } else {
                    DialogUtils.showRealCallDialog(this, order.getUserInfo().getPhoneNum());
                }
                break;
            default:
                break;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}

