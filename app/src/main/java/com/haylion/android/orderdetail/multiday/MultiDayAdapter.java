package com.haylion.android.orderdetail.multiday;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.haylion.android.R;
import com.haylion.android.data.model.OrderDateSimple;
import com.haylion.android.data.model.OrderStatus;
import com.haylion.android.data.util.BusinessUtils;

import java.text.ParseException;
import java.util.List;

public class MultiDayAdapter extends RecyclerView.Adapter<MultiDayAdapter.MyViewHolder> {
    private static final String TAG = "MultiDayAdapter";
    private List<OrderDateSimple> mDateList;
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        TextView tvStatus;
        LinearLayout llOrderInfo;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tvDate = itemView.findViewById(R.id.tv_order_date);
            this.tvStatus = itemView.findViewById(R.id.tv_order_status);
            this.llOrderInfo = itemView.findViewById(R.id.ll_order_info);
        }
    }

    public MultiDayAdapter(Context context, List<OrderDateSimple> list) {
        mDateList = list;
        mContext = context;
    }

    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     * <p>
     * This new MyViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new MyViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(ViewHolder, int, List)}. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new MyViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(ViewHolder, int)
     */
    @NonNull
    @Override
    public MultiDayAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_multi_day, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link ViewHolder#itemView} to reflect the item at the given
     * position.
     * <p>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link ViewHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     * <p>
     * Override {@link #onBindViewHolder(ViewHolder, int, List)} instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder   The MyViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull MultiDayAdapter.MyViewHolder holder, int position) {

        String orderTime = mDateList.get(position).getDate();
        String dayString = "2019-01-01";
        try {
            long milliSecond = BusinessUtils.stringToLong(orderTime, "yyyy-MM-dd HH:mm");
            dayString = BusinessUtils.getDateStringOnlyMonthAndDay(milliSecond, null);
            Log.d(TAG, "" + "dayString: " + dayString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.tvDate.setText(dayString);

        //订单状态显示
        String statusText = "";
        int statusTextColor = mContext.getResources().getColor(R.color.maas_text_blue);
        switch (OrderStatus.forStatus(mDateList.get(position).getStatus())) {
            case ORDER_STATUS_CLOSED:
                statusText = "已完成";
                statusTextColor = mContext.getResources().getColor(R.color.maas_text_gray);
                break;
            case ORDER_STATUS_WAIT_PAY:
                statusText = "未支付";
                statusTextColor = mContext.getResources().getColor(R.color.maas_text_orange);
                break;
            case ORDER_STATUS_WAIT_CAR:
            case ORDER_STATUS_GET_ON:
            case ORDER_STATUS_ARRIVED_START_ADDR:
            case ORDER_STATUS_GET_OFF:
                statusText = "进行中";
                statusTextColor = mContext.getResources().getColor(R.color.maas_text_green);
                break;
            case ORDER_STATUS_READY:
            case ORDER_STATUS_INIT:
                statusTextColor = mContext.getResources().getColor(R.color.maas_text_primary);
                break;
            case ORDER_STATUS_CANCELED:
                statusText = "已取消";
                break;
            case UNKNOWN:
                statusText = "未知状态";
                break;
        }

        if (!statusText.equals("")) {
            holder.tvStatus.setText(statusText);
            holder.tvStatus.setVisibility(View.VISIBLE);
            holder.tvDate.setTextColor(statusTextColor);
            holder.tvStatus.setTextColor(statusTextColor);
        }

        holder.llOrderInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onClick(position);
            }
        });

    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return mDateList.size();
    }

    public interface OnItemClickListener {
        void onClick(int position);

//        void onLongClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
}
