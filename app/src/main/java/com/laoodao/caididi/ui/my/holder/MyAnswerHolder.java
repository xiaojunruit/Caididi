package com.laoodao.caididi.ui.my.holder;

import android.view.View;

import com.laoodao.caididi.R;
import com.laoodao.caididi.common.binding.BindingHolder;
import com.laoodao.caididi.databinding.ItemMyAnswerBinding;
import com.laoodao.caididi.retrofit.user.MyAnswer;
import com.laoodao.caididi.ui.wenda.activity.AnswerDetailActivity;

import ezy.lite.itemholder.annotation.ItemLayout;

/**
 * Created by Administrator on 2016/12/29.
 */
@ItemLayout(R.layout.item_my_answer)
public class MyAnswerHolder extends BindingHolder<MyAnswer,ItemMyAnswerBinding>{
    public MyAnswerHolder(View v) {
        super(v);
        itemView.setOnClickListener(v1->{
            AnswerDetailActivity.start(itemView.getContext(), item.id,false);
        });
    }

    @Override
    public void refresh() {
        binding.setItem(item);
        binding.executePendingBindings();

    }
}
