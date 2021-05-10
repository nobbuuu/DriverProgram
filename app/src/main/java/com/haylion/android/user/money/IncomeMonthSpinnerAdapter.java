package com.haylion.android.user.money;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.haylion.android.R;
import com.haylion.android.data.model.SpinnerSelectBean;

import java.util.List;

/**
 * @author dengzh
 * @date 2019/10/21
 * Description:
 */
public class IncomeMonthSpinnerAdapter extends ArrayAdapter<SpinnerSelectBean> {

    private Context mContext;
    private List<SpinnerSelectBean> mList;
    private int lastPosition;


    public IncomeMonthSpinnerAdapter(@NonNull Context context, List<SpinnerSelectBean> list) {
        super(context, R.layout.vehicle_spinner_item, list);
        mContext = context;
        mList = list;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        //修改Spinner展开后的字体颜色
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.vehicle_spinner_down_item, parent,false);
        }

        //此处text1是Spinner默认的用来显示文字的TextView
        TextView tv =  convertView.findViewById(android.R.id.text1);
        tv.setText(mList.get(position).getValue());
        tv.setTextColor(ContextCompat.getColor(mContext,R.color.black));
        //根据点击位置改变颜色
        if(mList.get(position).isSelect()){
            tv.setTextColor(ContextCompat.getColor(mContext,R.color.order_type_realtime_carpoll_color));
        }
        return convertView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.vehicle_spinner_item, parent, false);
        }
        TextView tv =  convertView.findViewById(android.R.id.text1);
        tv.setText(mList.get(position).getValue());
        return convertView;
    }

    public void setSelect(int position) {
        if(lastPosition!=-1){
            mList.get(lastPosition).setSelect(false);
        }
        lastPosition = position;
        mList.get(lastPosition).setSelect(true);
        notifyDataSetChanged();
    }
}
