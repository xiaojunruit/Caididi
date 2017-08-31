package com.laoodao.caididi.ui.main.holder;

import android.view.View;

import com.laoodao.caididi.R;
import com.laoodao.caididi.common.binding.BindingHolder;
import com.laoodao.caididi.databinding.ItemVegetablesPriceBinding;
import com.laoodao.caididi.retrofit.main.Vegetable;

import ezy.lite.itemholder.annotation.ItemLayout;

/**
 * Created by WORK on 2017/2/21.
 */
@ItemLayout(R.layout.item_vegetables_price)
public class VegetablesPriceHolder extends BindingHolder<Vegetable,ItemVegetablesPriceBinding>{
    public VegetablesPriceHolder(View v) {
        super(v);
    }

    @Override
    public void refresh() {
        binding.setItem(item);
        binding.executePendingBindings();
    }
}
