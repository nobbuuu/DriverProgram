package com.haylion.android.notification;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.haylion.android.R;
import com.haylion.android.common.view.dialog.DeleteConfirmDialog;
import com.haylion.android.data.base.BaseActivity;
import com.haylion.android.data.model.MessageDetail;
import com.haylion.android.data.model.NotificationData;
import com.haylion.android.data.model.Order;
import com.haylion.android.data.model.OrderDetail;
import com.haylion.android.data.model.OrderTimeType;
import com.haylion.android.data.widgt.MassLoadingMoreFooter;
import com.haylion.android.orderdetail.OrderDetailActivity;
import com.haylion.android.orderdetail.multiday.MultiDayDetailActivity;
import com.haylion.android.orderdetail.trip.TripDetailActivity;
import com.haylion.android.orderlist.OrderClickArea;
import com.haylion.android.orderlist.OrderListActivity;
import com.haylion.android.service.WsCommands;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.nlopez.smartadapters.SmartAdapter;
import io.nlopez.smartadapters.adapters.RecyclerMultiAdapter;
import io.nlopez.smartadapters.utils.ViewEventListener;

import static com.haylion.android.orderdetail.OrderDetailActivity.ORDER_ID;


/**
 * @class NotificationListActivity
 * @description 系统消息页面
 * @date: 2019/12/18 16:08
 * @author: tandongdong
 */
public class NotificationListActivity extends BaseActivity<NotificationContract.Presenter> implements
        NotificationContract.View, ViewEventListener<MessageDetail> {

    private static final String TAG = "NotificationActivity";

    @BindView(R.id.tv_message_unread)
    TextView tvMessageUnread;
    @BindView(R.id.tv_set_all_message_readed)
    TextView tvSetAllMessageReaded;

    @BindView(R.id.lv_message_list)
    XRecyclerView rvMessageList;
    @BindView(R.id.empty_view)
    TextView mEmptyView;

    private RecyclerMultiAdapter mAdapter;

    public static void start(Context context, OrderTimeType type) {
        Intent intent = new Intent(context, OrderListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_list);
        ButterKnife.bind(this);
        initFooterView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.refreshOrderList();
    }

    @OnClick({R.id.iv_back, R.id.tv_set_all_message_readed, R.id.empty_view})
    public void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_set_all_message_readed: //显示月份选择弹窗
                if (unReadMessageCount > 0) {
                    presenter.allMessageReaded();
                } else {
                    toast("没有未读消息");
                }
                break;
            case R.id.empty_view:  //点击空视图
                rvMessageList.refresh();
                break;
            default:
                break;
        }
    }

    /**
     * 自定义 footerView
     */
    private void initFooterView() {
        MassLoadingMoreFooter footerView = new MassLoadingMoreFooter(this);
        rvMessageList.setFootView(footerView, footerView.callBack);
    }

    @Override
    protected void beforeCreatePresenter() {
        rvMessageList.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                presenter.refreshOrderList();
            }

            @Override
            public void onLoadMore() {
                presenter.loadMoreOrders();
            }
        });
        rvMessageList.setEmptyView(mEmptyView);
        mAdapter = SmartAdapter.empty()
                .map(MessageDetail.class, NotificationListItemView.class)
                .listener(this)
                .into(rvMessageList);
//        rvMessageList.addItemDecoration(new VerticalSpaceItemDecoration(SizeUtil.dp2px(16)));
    }


    private List<MessageDetail> mList;
    private int unReadMessageCount = 0;

    @Override
    public void setMessageList(List<MessageDetail> list, int unRead) {
        if (list == null || list.isEmpty()) {
            mEmptyView.setText("暂无消息");
        }
        mAdapter.setItems(list);
        rvMessageList.refreshComplete();
        unReadMessageCount = unRead;
        if (unRead > 0) {
            tvMessageUnread.setText(unRead + " 条未读消息");
        } else {
            tvMessageUnread.setText("没有未读消息");
        }

        mList = list;
    }

    /**
     * @class NotificationListActivity
     * @description 展示删除消息的对话框
     * @date: 2019/12/9 23:37
     * @author: tandongdong
     */
    private void showDeleteDialog(MessageDetail messageDetail) {
        DeleteConfirmDialog deleteConfirmDialog = new DeleteConfirmDialog(this);
        deleteConfirmDialog.setOnDialogSelectListener(new DeleteConfirmDialog.OnDialogSelectListener() {
            @Override
            public void onConfirm() {
                messageDetail.setDeleted(1); //删除消息
                presenter.updateMessage(messageDetail);
            }
        });
        deleteConfirmDialog.toggleDialog();
    }

    @Override
    public void addMoreOrders(List<MessageDetail> list) {
        mAdapter.addItems(list);
        rvMessageList.loadMoreComplete();
    }


    @Override
    public void noMoreOrders() {
        rvMessageList.setNoMore(true);
    }

    /**
     * 小孩订单，还需要判断子状态和主状态
     *
     * @param order
     */
    @Override
    public void getOrderDetailSuccess(Order order) {
        Log.d(TAG, "getOrderDetailSuccess:" + order.toString());
        if (order.getOrderStatus() == Order.ORDER_STATUS_ARRIVED_START_ADDR
                || order.getOrderStatus() == Order.ORDER_STATUS_GET_ON
                || order.getOrderStatus() == Order.ORDER_STATUS_WAIT_CAR) {
            if (order.isCarpoolOrder()) {
                MultiDayDetailActivity.go(getContext(), order.getCarpoolCode(), true);
            } else {
                Intent intent = new Intent(this, OrderDetailActivity.class);
                intent.putExtra(ORDER_ID, order.getOrderId());
                startActivity(intent);
            }
        } else {
            toast(getString(R.string.message_expired));
            //需要刷新页面
            presenter.refreshOrderList();
        }
    }

    @Override
    public void getOrderDetailFail(String msg) {

    }

    @Override
    public void enterNewActivity(MessageDetail messageDetail) {
        //判断是否从系统推送消息进入的页面
        NotificationData notificationData = new NotificationData();
        notificationData.setMessageType(messageDetail.getCmdSn());
        notificationData.setOrderId(messageDetail.getOrderId());

        Log.d(TAG, "notificationData:" + notificationData.toString());
        if (notificationData.getMessageType() == WsCommands.DRIVER_ORDER_PAID.getSn()) {
            TripDetailActivity.go(this, notificationData.getOrderId(), false);
        } else if (notificationData.getMessageType() == WsCommands.DRIVER_ORDER_CANCEL_BY_USER.getSn()
                || notificationData.getMessageType() == WsCommands.DRIVER_ORDER_CANCEL_BY_SYSTEM.getSn()) {
            TripDetailActivity.go(this, notificationData.getOrderId(), false);
        } else if (notificationData.getMessageType() == WsCommands.DRIVER_UNPAID_ORDER_TO_CONTROVERSY_ORDER.getSn()) {
            TripDetailActivity.go(this, notificationData.getOrderId(), false);
        } else if (notificationData.getMessageType() == WsCommands.DRIVER_ORDER_DESTINATION_CHANGED.getSn()) {
            //不是订单的终态，需要根据当前订单的状态进行跳转。
            presenter.getOrderDetail(messageDetail);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (rvMessageList != null) {
            rvMessageList.destroy();
            rvMessageList = null;
        }
    }

    @Override
    protected NotificationContract.Presenter onCreatePresenter() {
        return new NotificationListPresenter(this);
    }

    @Override
    public void onViewEvent(int actionType, MessageDetail messageDetail, int position, View view) {
        if (actionType == OrderClickArea.PUSH_MESSAGE) {

            //上报消息已读,上报成功后，再跳转到新页面
            List<MessageDetail> messageDetails = new ArrayList<>();
            Log.d(TAG, "messageDetail：" + messageDetail.toString());
            messageDetail.setViewed(1);
            messageDetails.add(messageDetail);
            presenter.messageHasReaded(messageDetails);
        } else if (actionType == OrderClickArea.PUSH_MESSAGE_LONG_CLICK) {
            //弹出删除消息的对话框
            showDeleteDialog(messageDetail);
        }
    }
}

