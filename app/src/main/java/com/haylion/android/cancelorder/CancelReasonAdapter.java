package com.haylion.android.cancelorder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.haylion.android.R;
import com.haylion.android.data.model.CancelReason;

import java.util.List;

public class CancelReasonAdapter extends RecyclerView.Adapter<CancelReasonAdapter.MyViewHolder> {

    private Context mContext;
    private List<CancelReason> mList;
    private OnItemClickListener listener;
    private int lastSelectPosition = -1;  //最近一次选中的位置

    public CancelReasonAdapter(Context mContext, List<CancelReason> list) {
        this.mContext = mContext;
        this.mList = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_order_cancel_reason,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tv_reason.setText(mList.get(position).getReason());
        holder.iv_select.setImageResource(mList.get(position).isSelected() ? R.mipmap.ic_select : R.mipmap.ic_unselect);
        holder.lineView.setVisibility(position != mList.size()-1 ?View.VISIBLE:View.GONE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lastSelectPosition == position){ //选中两次不做处理
                    return;
                }
                if(lastSelectPosition != -1){
                    //上一次选择置为false
                    mList.get(lastSelectPosition).setSelected(false);
                }
                //最新选择置为true
                lastSelectPosition = position;
                mList.get(lastSelectPosition).setSelected(true);
                notifyDataSetChanged();
                //回调
                if(listener!=null){
                    listener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList == null? 0: mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_reason;
        ImageView iv_select;
        View lineView;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_reason = itemView.findViewById(R.id.tv_reason);
            iv_select = itemView.findViewById(R.id.iv_select);
            lineView = itemView.findViewById(R.id.lineView);
        }
    }


    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
    public interface OnItemClickListener{
        void onItemClick(int position);
    }
}
