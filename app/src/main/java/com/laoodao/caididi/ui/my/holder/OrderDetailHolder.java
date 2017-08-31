package com.laoodao.caididi.ui.my.holder;

import android.view.View;

import com.laoodao.caididi.R;
import com.laoodao.caididi.common.binding.BindingHolder;
import com.laoodao.caididi.databinding.ItemOrderDetailBinding;
import com.laoodao.caididi.retrofit.user.OrderDetail;
import com.laoodao.caididi.ui.my.activity.OperationHistoryActivity;

import ezy.lite.itemholder.annotation.ItemLayout;
import ezy.lite.util.ContextUtil;

/**
 * Created by WORK on 2017/3/9.
 */
@ItemLayout(R.layout.item_order_detail)
public class OrderDetailHolder extends BindingHolder<OrderDetail.DetailList,ItemOrderDetailBinding>{
    public OrderDetailHolder(View v) {
        super(v);
    }

    @Override
    public void refresh() {
        binding.setItem(item);
    }
}
