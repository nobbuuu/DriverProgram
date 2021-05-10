package com.haylion.android.common.view.popwindow;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haylion.android.R;
import com.haylion.android.common.view.dialog.DialogUtils;
import com.haylion.android.data.model.Order;

/**
 * @author dengzh
 * @date 2019/11/10
 * Description: 导航页 - 打电话
 */
public class ContactPopWindow extends BasePopupWindow implements View.OnClickListener {

    private TextView tv_contact_child;
    private TextView tv_contact_parent;
    private View lineView;

    private Order order;

    public ContactPopWindow(Context mContext) {
        super(mContext, R.layout.pop_amap_navi_contact, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        init();
    }

    private void init(){
        tv_contact_child = getView(R.id.tv_contact_child);
        tv_contact_parent = getView(R.id.tv_contact_parent);
        lineView = getView(R.id.lineView);
        tv_contact_child.setOnClickListener(this);
        tv_contact_parent.setOnClickListener(this);

        getView(R.id.rl_main).setOnClickListener(this);
    }

    public void setData(Order order){
        this.order = order;
        //去接乘客，可联系小孩和家长
        if(order.getOrderStatus() == Order.ORDER_STATUS_GET_ON){
            tv_contact_child.setVisibility(View.GONE);
            lineView.setVisibility(View.GONE);
            tv_contact_parent.setVisibility(View.VISIBLE);
        }else{
            //去送乘客，可联系家长
            tv_contact_child.setVisibility(View.VISIBLE);
            lineView.setVisibility(View.VISIBLE);
            tv_contact_parent.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        mPopupWindow.dismiss();
        switch (v.getId()){
            case R.id.tv_contact_child:
                //联系客服
                if (order.getOrderStatus() == Order.ORDER_STATUS_GET_OFF
                        && order.getOrderSubStatus() == Order.ORDER_SUB_STATUS_GET_OFF_WAIT_PARENT_CONFIRM) {
                    if(listener!=null){
                        listener.callService();
                    }
                } else {
                    //联系小孩
                    DialogUtils.showRealCallDialog(mContext,order.getChildList().get(0).getMobile());
                }
                break;
            case R.id.tv_contact_parent:  //联系家长
                DialogUtils.showRealCallDialog(mContext,order.getUserInfo().getPhoneNum());
                break;
                default:
                    break;
        }
    }

    public OnDialogSelectListener listener;

    public void setOnDialogSelectListener(OnDialogSelectListener listener){
        this.listener = listener;
    }
    public interface OnDialogSelectListener{
        void callService();
    }

}
