package com.haylion.android.common.map.offlinemap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.maps.offlinemap.OfflineMapCity;
import com.haylion.android.R;

import java.util.List;

/**
 * 简单的选中 Adapter
 */
public class OfflineMapAdapter extends RecyclerView.Adapter<OfflineMapAdapter.ViewHolder> {

    private Context mContext;
    private List<OfflineMapCity> mList;
    private OnItemClickListener listener;

    public OfflineMapAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(List<OfflineMapCity> list){
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
        holder.tv_desc.setText(mList.get(position).getCity());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
