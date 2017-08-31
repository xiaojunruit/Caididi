package com.laoodao.caididi.ui.my.holder;

import android.view.View;

import com.laoodao.caididi.R;
import com.laoodao.caididi.common.binding.BindingHolder;
import com.laoodao.caididi.databinding.ItemMesDynamicBinding;
import com.laoodao.caididi.retrofit.user.Message;
import com.laoodao.caididi.ui.wenda.activity.AnswerDetailActivity;

import ezy.lite.itemholder.annotation.ItemLayout;

/**
 * Created by Administrator on 2017/1/11.
 */
@ItemLayout(R.layout.item_mes_dynamic)
public class MesDynamicHolder extends BindingHolder<Message,ItemMesDynamicBinding>{
    public MesDynamicHolder(View v) {
        super(v);
        v.setOnClickListener(v1 -> {
            AnswerDetailActivity.start(v1.getContext(),item.id,false);
        });
    }

    @Override
    public void refresh() {
        binding.setItem(item);
    }
}
