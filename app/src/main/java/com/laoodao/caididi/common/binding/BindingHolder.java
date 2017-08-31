package com.laoodao.caididi.common.binding;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.View;

import ezy.lite.itemholder.ItemHolder;

public abstract class BindingHolder<Item, Binding extends ViewDataBinding> extends ItemHolder<Item> {

    public final Binding binding;

    public BindingHolder(View v) {
        super(v);
        binding = DataBindingUtil.bind(v);

    }

}