package com.laoodao.caididi.common.holder;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.laoodao.caididi.R;
import com.laoodao.caididi.retrofit.main.LoadMore;

import ezy.lite.itemholder.ItemHolder;
import ezy.lite.itemholder.annotation.ItemClass;
import ezy.lite.itemholder.annotation.ItemLayout;

@ItemLayout(R.layout.item_loadmore)
@ItemClass(LoadMore.class)
public class LoadMoreHolder extends ItemHolder<LoadMore> {
    ProgressBar vProgress;
    TextView vText;
    public LoadMoreHolder(View v) {
        super(v);
        vProgress = (ProgressBar)v.findViewById(R.id.progress);
        vText = (TextView)v.findViewById(R.id.text);
    }
    @Override
    public void refresh() {
        if (item.hasMore) {
            vProgress.setVisibility(View.VISIBLE);
            vText.setText("加载中...");
        } else {
            vProgress.setVisibility(View.GONE);
            vText.setText("没有更多了");
        }
    }
}
