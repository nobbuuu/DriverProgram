package com.haylion.android.address;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.haylion.android.R;

import java.util.HashMap;
import java.util.List;

public class SearchAdapter extends BaseAdapter {

    private List<HashMap<String, String>> addressData;
    private LayoutInflater layoutInflater;

    public SearchAdapter(Context context, List<HashMap<String, String>> list) {
        layoutInflater = LayoutInflater.from(context);
//        addressData = new ArrayList<HashMap<String, String>>();
        addressData = list;


    }

    @Override
    public int getCount() {
        return addressData.size();
    }

    @Override
    public Object getItem(int position) {
        return addressData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.search_list_item, null);
            viewHolder.title = (TextView) convertView.findViewById(R.id.search_item_title);
            viewHolder.text = (TextView) convertView.findViewById(R.id.search_item_text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.title.setText(addressData.get(position).get("name"));
        viewHolder.text.setText(addressData.get(position).get("address"));
        return convertView;
    }

    public final static class ViewHolder{
        TextView title;
        TextView text;
    }
}
