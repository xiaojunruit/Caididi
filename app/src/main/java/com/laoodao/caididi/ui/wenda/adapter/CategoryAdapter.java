package com.laoodao.caididi.ui.wenda.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.laoodao.caididi.R;
import com.laoodao.caididi.retrofit.main.Category;

import java.util.ArrayList;
import java.util.List;

import ezy.lite.itemholder.helper.ItemSelectorSingle;
import ezy.lite.util.LogUtil;

/**
 * Created by WORK on 2017/1/7.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryHolder> {
    private int mCurrentSelected;
    private List<Category> mCategoryList = new ArrayList<>();
    private Context mContext;

    public CategoryAdapter(Context context) {
        this.mContext = context;

    }

    @Override
    public CategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_left, parent, false);
        return new CategoryHolder(view);
    }

    public void bindList(List<Category> categoryList) {
        this.mCategoryList.clear();
        this.mCategoryList.addAll(categoryList);
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(CategoryHolder holder, final int position) {
        if (mCurrentSelected == position) {
            holder.contentView.setBackgroundColor(0xffffffff);
        } else {
            holder.contentView.setBackgroundColor(0xfff4f4f4);
        }
        Category category = mCategoryList.get(position);
        holder.title.setText(category.name);
        holder.contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickLintener != null) {
                    onItemClickLintener.onItemClick(view, category, position);
                    mCurrentSelected = position;
                    notifyDataSetChanged();
                }
            }
        });
    }


    public OnItemClickListener<Category> onItemClickLintener;

    public void setOnItemClickListener(OnItemClickListener onItemClickEvent) {
        this.onItemClickLintener = onItemClickEvent;
    }

    @Override
    public int getItemCount() {
        return mCategoryList.size();
    }

    static class CategoryHolder extends RecyclerView.ViewHolder {
        TextView title;
        View contentView;

        public CategoryHolder(View itemView) {
            super(itemView);
            contentView = itemView;
            title = (TextView) itemView.findViewById(R.id.title);
        }

    }
}
