package com.haylion.android.main;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.haylion.android.R;
import com.haylion.android.data.model.Vehicle;

import java.util.List;

/**
 * 车辆列表 Adapter
 */
public class VehicleSelectAdapter extends RecyclerView.Adapter<VehicleSelectAdapter.ViewHolder> {

    private Context mContext;
    private List<Vehicle> mList;
    private OnItemClickListener listener;
    private int lastSelectPosition = -1;  //最近一次选中的位置

    public VehicleSelectAdapter(Context mContext,List<Vehicle> list) {
        this.mContext = mContext;
        mList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.vehicle_select_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_number.setText(mList.get(position).getNumber());
        if(mList.get(position).isSelect()){
            holder.tv_number.setTextColor(ContextCompat.getColor(mContext,R.color.maas_text_blue));
            holder.tv_number.setTypeface(Typeface.DEFAULT_BOLD);
        }else{
            holder.tv_number.setTextColor(ContextCompat.getColor(mContext,R.color.maas_text_deep_gray));
            holder.tv_number.setTypeface(Typeface.DEFAULT);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lastSelectPosition == position){ //选中两次不做处理
                    return;
                }
                if(lastSelectPosition != -1){
                    //上一次选择置为false
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

    public int getLastSelectPosition() {
        return lastSelectPosition;
    }

    @Override
    public int getItemCount() {
        return mList == null? 0: mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_number;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_number = itemView.findViewById(R.id.tv_number);
        }
    }


    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
    public interface OnItemClickListener{
        void onItemClick(int position);
    }
}
