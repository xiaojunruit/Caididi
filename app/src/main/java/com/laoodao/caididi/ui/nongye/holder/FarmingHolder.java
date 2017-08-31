package com.laoodao.caididi.ui.nongye.holder;

import android.view.View;

import com.laoodao.caididi.R;
import com.laoodao.caididi.common.binding.BindingHolder;
import com.laoodao.caididi.databinding.ItemFarmingBinding;
import com.laoodao.caididi.retrofit.main.FarmingDetail;
import com.laoodao.caididi.retrofit.main.FarmingList;
import com.laoodao.caididi.ui.nongye.activity.FarmingDetailActivity;

import ezy.lite.itemholder.annotation.ItemLayout;

/**
 * Created by WORK on 2017/2/17.
 */
@ItemLayout(R.layout.item_farming)
public class FarmingHolder extends BindingHolder<FarmingList,ItemFarmingBinding>{
    public FarmingHolder(View v) {
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
