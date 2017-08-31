package com.laoodao.caididi.ui.my.holder;

import android.view.View;

import com.laoodao.caididi.R;
import com.laoodao.caididi.common.binding.BindingHolder;
import com.laoodao.caididi.databinding.ItemUserDynamicBinding;
import com.laoodao.caididi.retrofit.user.Dynamic;
import com.laoodao.caididi.ui.wenda.activity.AnswerDetailActivity;

import ezy.lite.itemholder.annotation.ItemLayout;

/**
 * Created by Administrator on 2017/1/14.
 */
@ItemLayout(R.layout.item_user_dynamic)
public class UserDynamicHolder extends BindingHolder<Dynamic, ItemUserDynamicBinding> {
    public UserDynamicHolder(View v) {
        super(v);
        itemView.setOnClickListener(v1 -> {
            AnswerDetailActivity.start(v.getContext(), item.id, false);
        });
    }

    @Override
    public void refresh() {
        binding.setItem(item);
        binding.executePendingBindings();
    }
}
