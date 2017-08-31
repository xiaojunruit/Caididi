package com.laoodao.caididi.ui.main.holder;

import android.view.View;
import android.view.ViewGroup;

import com.laoodao.caididi.R;
import com.laoodao.caididi.Route;
import com.laoodao.caididi.common.binding.BindingHolder;
import com.laoodao.caididi.databinding.ItemLabelHomeBinding;
import com.laoodao.caididi.retrofit.main.Menu;

import ezy.lite.itemholder.annotation.ItemLayout;
import ezy.lite.util.Device;
import ezy.lite.util.LogUtil;

/**
 * Created by WORK on 2017/2/20.
 */
@ItemLayout(R.layout.item_label_home)
public class HomeLabelHolder extends BindingHolder<Menu, ItemLabelHomeBinding> {
    public HomeLabelHolder(View v) {
        super(v);
        ViewGroup.LayoutParams params = v.getLayoutParams();
        params.width = (Device.dm.widthPixels - Device.dp2px(20)) / 4;
        v.setLayoutParams(params);
        v.setOnClickListener(v1 -> {
            Route.go(v.getContext(), item.url + "&isInsect=" + item.isInsect+"&title="+item.title);
        });
    }

    @Override
    public void refresh() {
        binding.setItem(item);
    }
}
