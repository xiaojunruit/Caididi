package com.laoodao.caididi.ui.my.holder;

import android.view.View;

import com.laoodao.caididi.R;
import com.laoodao.caididi.common.binding.BindingHolder;
import com.laoodao.caididi.databinding.ItemReserveListBinding;
import com.laoodao.caididi.retrofit.user.ReserveList;
import com.laoodao.caididi.ui.my.activity.ReserveDetailActivity;

import ezy.lite.itemholder.annotation.ItemLayout;

/**
 * Created by WORK on 2017/3/7.
 */
@ItemLayout(R.layout.item_reserve_list)
public class ReserveListHolder extends BindingHolder<ReserveList, ItemReserveListBinding> {
    public ReserveListHolder(View v) {
        super(v);
        v.setOnClickListener(v1 -> {
            ReserveDetailActivity.start(v.getContext(), Integer.parseInt(item.id));
        });
    }

    @Override
    public void refresh() {
        binding.txtState.setTextColor(item.state == 1 ? 0xffffac2d : 0xff2AB80E);
        binding.setItem(item);
    }
}
