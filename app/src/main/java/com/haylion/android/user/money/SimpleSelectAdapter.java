package com.haylion.android.user.money;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.haylion.android.R;
import com.haylion.android.data.model.SimpleSelectBean;

import java.util.List;

/**
 * 简单的选中 Adapter
 */
public class SimpleSelectAdapter extends RecyclerView.Adapter<SimpleSelectAdapter.ViewHolder> {

    private Context mContext;
    private List<SimpleSelectBean> mList;
    private OnItemClickListener listener;
    private int lastSelectPosition = -1;  //最近一次选中的位置

    public SimpleSelectAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(List<SimpleSelectBean> list){
        this.mList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_simple_select,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_desc.setText(mList.get(position).getDesc());
        if(mList.get(position).isSelect()){
            lastSelectPosition = position;
            holder.tv_desc.setTextColor(ContextCompat.getColor(mContext,R.color.maas_text_blue));
        }else{
            holder.tv_desc.setTextColor(ContextCompat.getColor(mContext,R.color.maas_text_primary));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lastSelectPosition == position){ //选中两次不做处理
                    return;
                }
                if(lastSelectPosition != -1){
                    //上一次选中的置为false
                    mList.get(lastSelectPosition).setSelect(false);
                }
                //最新选择置为true
                lastSelectPosition = position;
                mList.get(lastSelectPosition).setSelect(true);
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_desc;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_desc = itemView.findViewById(R.id.tv_desc);
        }
    }


    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
    public interface OnItemClickListener{
        void onItemClick(int position);
    }
}
