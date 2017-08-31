package com.laoodao.caididi.ui.widget.citySelector.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.laoodao.caididi.R;
import com.laoodao.caididi.retrofit.main.City;

import java.util.ArrayList;
import java.util.List;

/**
 * author zaaach on 2016/1/26.
 */
public class HotCityGridAdapter extends BaseAdapter {
    private Context mContext;
    private List<City> mCities = new ArrayList<>();

    public HotCityGridAdapter(Context context, List<City> historyList) {
        this.mContext = context;
        mCities.clear();
        mCities.addAll(historyList);
    }

    public HotCityGridAdapter(Context context) {
        this.mContext = context;
    }

    public void addAll(List<City> historyList){
        mCities.clear();
        mCities.addAll(historyList);
        notifyDataSetChanged();
    }

    public void clear(){
        mCities.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mCities == null ? 0 : mCities.size();
    }

    @Override
    public City getItem(int position) {
        return mCities == null ? null : mCities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        HotCityViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.cp_item_hot_city_gridview, parent, false);
            holder = new HotCityViewHolder();
            holder.name = (TextView) view.findViewById(R.id.tv_hot_city_name);
            view.setTag(holder);
        } else {
            holder = (HotCityViewHolder) view.getTag();
        }

        holder.name.setText(mCities.get(position).n);
        return view;
    }

    public static class HotCityViewHolder {
        TextView name;
    }
}
