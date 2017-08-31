package com.laoodao.caididi.ui.nongye.holder;

import android.view.View;

import com.laoodao.caididi.R;
import com.laoodao.caididi.common.binding.BindingHolder;
import com.laoodao.caididi.common.util.RxBus;
import com.laoodao.caididi.databinding.ItemFarmingSearchBinding;
import com.laoodao.caididi.event.FarmingSearchEvent;
import com.laoodao.caididi.ui.nongye.activity.FarmingDetailActivity;

import ezy.lite.itemholder.annotation.ItemLayout;
import ezy.lite.util.ContextUtil;
import ezy.lite.util.LogUtil;

/**
 * Created by WORK on 2017/2/18.
 */
@ItemLayout(R.layout.item_farming_search)
public class FarmingSearchHolder extends BindingHolder<String, ItemFarmingSearchBinding> {
    public OnClick mOnClick;
    public void setOnClick(OnClick onClick){
        this.mOnClick=onClick;
    }
    public FarmingSearchHolder(View v) {
        super(v);
        v.setOnClickListener(v1 -> {
            RxBus.post(new FarmingSearchEvent(item));
        });
    }

    @Override
    public void refresh() {
        binding.setItem(item);
    }

    public interface OnClick{
       public void setOnClick(String kw);
    }
}
