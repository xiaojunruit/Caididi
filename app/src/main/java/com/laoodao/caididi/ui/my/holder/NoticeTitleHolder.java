package com.laoodao.caididi.ui.my.holder;

import android.view.View;

import com.laoodao.caididi.R;
import com.laoodao.caididi.common.binding.BindingHolder;
import com.laoodao.caididi.databinding.ItemNoticeTitleBinding;

import ezy.lite.itemholder.annotation.ItemLayout;

/**
 * Created by WORK on 2017/6/13.
 */
@ItemLayout(R.layout.item_notice_title)
public class NoticeTitleHolder extends BindingHolder<String, ItemNoticeTitleBinding> {
    public NoticeTitleHolder(View v) {
        super(v);
    }

    @Override
    public void refresh() {

    }
}
