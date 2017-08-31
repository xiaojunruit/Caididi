package com.laoodao.caididi.ui.my.holder;

import android.view.View;

import com.laoodao.caididi.R;
import com.laoodao.caididi.common.binding.BindingHolder;
import com.laoodao.caididi.databinding.ItemBidderBinding;
import com.laoodao.caididi.retrofit.user.CollectionDetail;
import com.laoodao.caididi.ui.my.activity.OrderDetailActivity;

import ezy.lite.itemholder.annotation.ItemLayout;

/**
 * Created by WORK on 2017/3/9.
 */
@ItemLayout(R.layout.item_bidder)
public class BiddersHolder extends BindingHolder<CollectionDetail.CollectionDetailList, ItemBidderBinding> {
    public BiddersHolder(View v) {
        super(v);
        v.setOnClickListener(v1 -> {
            OrderDetailActivity.start(v.getContext(),item.id,item.purchaseCode);
        });
    }

    @Override
    public void refresh() {
        binding.setItem(item);
    }
}
