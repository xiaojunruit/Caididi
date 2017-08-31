package com.laoodao.caididi.ui.wenda.holder;

import android.view.View;

import com.laoodao.caididi.R;
import com.laoodao.caididi.common.binding.BindingHolder;
import com.laoodao.caididi.databinding.ItemSearchBinding;
import com.laoodao.caididi.retrofit.main.Answer;
import com.laoodao.caididi.ui.wenda.activity.AnswerDetailActivity;

import ezy.lite.itemholder.annotation.ItemLayout;

/**
 * Created by Administrator on 2017/1/7.
 */
@ItemLayout(R.layout.item_search)
public class SearchHolder extends BindingHolder<Answer,ItemSearchBinding>{
    public SearchHolder(View v) {
        super(v);
        v.setOnClickListener(v1 -> {
            AnswerDetailActivity.start(itemView.getContext(), item.id, false);
        });
    }

    @Override
    public void refresh() {
        binding.setItem(item);
        binding.executePendingBindings();
    }
}
