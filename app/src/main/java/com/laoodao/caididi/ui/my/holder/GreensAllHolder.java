package com.laoodao.caididi.ui.my.holder;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;

import com.laoodao.caididi.R;
import com.laoodao.caididi.common.binding.BindingHolder;
import com.laoodao.caididi.databinding.ItemGreensAllBinding;
import com.laoodao.caididi.retrofit.user.GreensRecord;
import com.laoodao.caididi.ui.my.activity.OrderDetailActivity;

import java.text.DecimalFormat;

import ezy.lite.itemholder.annotation.ItemLayout;

/**
 * Created by WORK on 2017/3/15.
 */
@ItemLayout(R.layout.item_greens_all)
public class GreensAllHolder extends BindingHolder<GreensRecord, ItemGreensAllBinding> {
    private Context mContext;

    public GreensAllHolder(View v) {
        super(v);
        mContext = v.getContext();
        v.setOnClickListener(v1 -> {
            OrderDetailActivity.start(v.getContext(), item.id, item.purchaseCode);
        });
    }

    @Override
    public void refresh() {
        binding.setItem(item);
    }
}
