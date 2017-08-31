package com.laoodao.caididi.ui.weather.holder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.laoodao.caididi.R;
import com.laoodao.caididi.common.binding.BindingHolder;
import com.laoodao.caididi.databinding.ItemDay7weatherBinding;
import com.laoodao.caididi.retrofit.weather.Future;
import com.laoodao.caididi.retrofit.weather.Weather;

import ezy.lite.itemholder.annotation.ItemLayout;
import ezy.lite.util.Device;

/**
 * Created by Administrator on 2017/2/17.
 */

@ItemLayout(R.layout.item_day7weather)
public class Day7WeatherHolder extends BindingHolder<Future, ItemDay7weatherBinding> {
    public Day7WeatherHolder(View v) {
        super(v);
        int width = Device.dm.widthPixels / 4;
        v.setLayoutParams(new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    public void refresh() {
        binding.setItem(item);
        binding.executePendingBindings();
    }
}
