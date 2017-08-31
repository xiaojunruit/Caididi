package com.laoodao.caididi.ui.wenda.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.laoodao.caididi.R;
import com.laoodao.caididi.retrofit.main.Plants;
import com.laoodao.caididi.ui.widget.commentList.CommentAdapter;

import java.util.ArrayList;
import java.util.List;

import ezy.lite.util.Device;
import ezy.lite.util.LogUtil;

/**
 * Created by WORK on 2017/1/7.
 */

public class FollowCropAdapter extends RecyclerView.Adapter<FollowCropAdapter.BottomHolder> {

    private List<Plants> lefts = new ArrayList<>();
    private Context mContext;

    public FollowCropAdapter(Context context) {

        this.mContext = context;
    }


    public void bindList(List<Plants> images) {
        this.lefts.clear();
        this.lefts.addAll(images);
        notifyDataSetChanged();
    }


    public void add(Plants plants) {
        this.lefts.add(plants);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        this.lefts.remove(position);
        notifyDataSetChanged();
    }


    @Override
    public BottomHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_follow_crop, parent, false);


        return new BottomHolder(view);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onBindViewHolder(BottomHolder holder, final int position) {
        if (position == 0) {
//            RecyclerView.LayoutParams lp2 = (RecyclerView.LayoutParams) holder.contentView.getLayoutParams();
//            lp2.leftMargin = Device.dp2px(-10);
            holder.contentView.setPadding(Device.dp2px(10),0, 0,0);
        }else{
            holder.contentView.setPadding(0,0,0,0);
        }
        Plants plants = lefts.get(position);
        holder.title.setText(Html.fromHtml(plants.name).toString());
        holder.contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null)
                    onItemClickListener.onItemClick(view, plants, position);
            }
        });
    }

    public interface Event {
        void onItemClick(View view, int position);
    }


    @Override
    public int getItemCount() {
        return lefts.size();
    }

    static class BottomHolder extends RecyclerView.ViewHolder {
        TextView title;
        View contentView;

        public BottomHolder(View itemView) {
            super(itemView);
            contentView = itemView;
            title = (TextView) itemView.findViewById(R.id.name);
        }

    }
}
