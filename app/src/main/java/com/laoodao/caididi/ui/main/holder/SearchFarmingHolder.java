package com.laoodao.caididi.ui.main.holder;

import android.view.View;

import com.laoodao.caididi.R;
import com.laoodao.caididi.common.binding.BindingHolder;
import com.laoodao.caididi.databinding.ItemFarmingBinding;
import com.laoodao.caididi.databinding.ItemFarmingListBinding;
import com.laoodao.caididi.retrofit.main.FarmingList;
import com.laoodao.caididi.ui.nongye.activity.FarmingDetailActivity;

import ezy.lite.itemholder.annotation.ItemLayout;

/**
 * Created by WORK on 2017/3/16.
 */

@ItemLayout(R.layout.item_farming_list)
public class SearchFarmingHolder extends BindingHolder<FarmingList,ItemFarmingListBinding> {
    public SearchFarmingHolder(View v) {
        super(v);
        v.setOnClickListener(v1 -> {
            FarmingDetailActivity.start(v.getContext(),item.id,item.gcName);
        });
    }

    @Override
    public void refresh() {
        binding.setItem(item);
        binding.executePendingBindings();
    }
}
