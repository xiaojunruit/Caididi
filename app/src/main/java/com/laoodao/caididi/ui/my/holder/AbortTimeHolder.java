package com.laoodao.caididi.ui.my.holder;

import android.view.View;

import com.laoodao.caididi.R;
import com.laoodao.caididi.common.binding.BindingHolder;
import com.laoodao.caididi.databinding.ItemAbortTimeBinding;
import com.laoodao.caididi.retrofit.user.AbortTime;

import ezy.lite.itemholder.annotation.ItemLayout;

/**
 * Created by XiaoGe on 2017/2/13.
 */
@ItemLayout(R.layout.item_abort_time)
public class AbortTimeHolder extends BindingHolder<AbortTime,ItemAbortTimeBinding>{
    public AbortTimeHolder(View v) {
        super(v);
    }

    @Override
    public void refresh() {
        binding.setItem(item);
        binding.txtTime.setTag(item);
    }
}
