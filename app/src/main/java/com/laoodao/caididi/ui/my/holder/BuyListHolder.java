package com.laoodao.caididi.ui.my.holder;

import android.view.View;

import com.laoodao.caididi.R;
import com.laoodao.caididi.common.binding.BindingHolder;
import com.laoodao.caididi.databinding.ItemBuylistBinding;
import com.laoodao.caididi.retrofit.user.BuyListInfo;
import com.laoodao.caididi.ui.qiugou.activity.DemandDetailActivity;

import ezy.lite.itemholder.annotation.ItemLayout;

/**
 * Created by WORK on 2017/2/13.
 */
@ItemLayout(R.layout.item_buylist)
public class BuyListHolder extends BindingHolder<BuyListInfo, ItemBuylistBinding> {

    public BuyListHolder(View v) {
        super(v);
        v.setOnClickListener(v1 -> {
            DemandDetailActivity.start(v.getContext(), item.id);
        });
    }

    @Override
    public void refresh() {
        binding.setItem(item);
        binding.executePendingBindings();
    }
}
