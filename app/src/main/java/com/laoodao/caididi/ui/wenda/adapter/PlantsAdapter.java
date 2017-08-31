package com.laoodao.caididi.ui.wenda.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.common.util.AddCategoryAnimator;
import com.laoodao.caididi.retrofit.main.Plants;
import com.laoodao.caididi.ui.widget.commentList.CommentAdapter;

import java.util.ArrayList;
import java.util.List;

import ezy.lite.util.ContextUtil;
import ezy.lite.util.LogUtil;
import ezy.lite.util.UI;

/**
 * Created by WORK on 2017/1/7.
 */

public class PlantsAdapter extends RecyclerView.Adapter<PlantsAdapter.PlansHolder> {

    private List<Plants> plantses = new ArrayList<>();
    private List<Plants> selectedList = new ArrayList<>();

    private Context mContext;

    public PlantsAdapter(Context context) {

        this.mContext = context;
    }

    @Override
    public PlansHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_right, parent, false);
        return new PlansHolder(view);
    }

    public void uncheck(int position) {

        this.selectedList.remove(position);
        LogUtil.e("sieze:" + selectedList.size());
        notifyDataSetChanged();
    }

    public void check(Plants plants) {
        this.selectedList.add(plants);
        notifyDataSetChanged();
    }


    private OnPlantsSelectChangedListener onPlantsSelectChangedListener;

    public void setOnPlantsSelectChangedListener(OnPlantsSelectChangedListener onPlantsSelectChangedListener) {
        this.onPlantsSelectChangedListener = onPlantsSelectChangedListener;
    }


    public OnItemClickListener<Plants> onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 是否选中过
     *
     * @return
     */
    public boolean isSelected(Plants plants) {
        for (Plants p : selectedList) {
            if (p.id == plants.id) {
                return true;
            }
        }
        return false;
    }

    /**
     * 选择
     *
     * @param holder
     * @param isChecked
     */
    public void selectPlants(PlansHolder holder, boolean isChecked) {

        holder.btnAdd.setSelected(isChecked);
        if (isChecked) {
            holder.btnAdd.setVisibility(View.GONE);
        } else {
            holder.btnAdd.setVisibility(View.VISIBLE);
        }
    }

    public void bindList(List<Plants> plantses) {
        this.plantses.clear();
        this.plantses.addAll(plantses);
        notifyDataSetChanged();
    }

    public void bindSelectedList(List<Plants> plantses) {

        this.selectedList.clear();
        this.selectedList.addAll(plantses);
        notifyDataSetChanged();
        if (onPlantsSelectChangedListener != null) onPlantsSelectChangedListener.onChange(plantses);
    }


    public List<Plants> getSelectedPlanes() {
        return selectedList;
    }


    @Override
    public void onBindViewHolder(final PlansHolder holder, final int position) {

        final Plants plants = plantses.get(position);

        Glide.with(mContext).load(plants.cover).into(holder.img);

        holder.name.setText(Html.fromHtml(plants.name));
        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectedList.size() >= 5) {
                    UI.showToast(mContext, "最多只能添加5种作物");
                    return;
                }
                checkPlants(holder, plants);
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(holder.img, plants, position);
                }

            }
        });
        selectPlants(holder, isSelected(plants));
    }


    /**
     * 选择
     *
     * @param holder
     * @param plants
     */
    private void checkPlants(PlansHolder holder, Plants plants) {
        selectedList.add(plants);
        selectPlants(holder, isSelected(plants));
    }


    public interface OnPlantsSelectChangedListener {
        void onChange(List<Plants> selectedPlants);
    }


    @Override
    public int getItemCount() {
        return this.plantses.size();
    }

    static class PlansHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView btnAdd;
        View contentView;
        ImageView img;

        public PlansHolder(View itemView) {
            super(itemView);
            contentView = itemView;
            btnAdd = (ImageView) itemView.findViewById(R.id.btn_add);
            name = (TextView) itemView.findViewById(R.id.name);
            img = (ImageView) itemView.findViewById(R.id.img);
        }

    }
}
