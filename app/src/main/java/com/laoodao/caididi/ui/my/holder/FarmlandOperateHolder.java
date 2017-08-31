package com.laoodao.caididi.ui.my.holder;

import android.view.View;

import com.laoodao.caididi.R;
import com.laoodao.caididi.common.binding.BindingHolder;
import com.laoodao.caididi.common.util.RxBus;
import com.laoodao.caididi.databinding.ItemFarmlandBinding;
import com.laoodao.caididi.event.FarmlandOperateEvent;
import com.laoodao.caididi.retrofit.user.LandOperate;
import com.laoodao.caididi.ui.my.activity.AddOperationActivity;
import com.laoodao.caididi.ui.my.activity.FarmlandOperateActivity;

import ezy.lite.itemholder.annotation.ItemLayout;
import ezy.lite.util.ContextUtil;

/**
 * Created by Administrator on 2017/1/11.
 */
@ItemLayout(R.layout.item_farmland)
public class FarmlandOperateHolder extends BindingHolder<LandOperate, ItemFarmlandBinding> {

    public FarmlandOperateHolder(View v) {
        super(v);
        v.setOnClickListener(v1 -> {
            FarmlandOperateActivity activity = ContextUtil.from(v1.getContext(), FarmlandOperateActivity.class);
            RxBus.post(new FarmlandOperateEvent(item.id,activity.farmlandId,item.name));
            activity.finish();
        });
    }

    @Override
    public void refresh() {
        binding.setItem(item);
        binding.executePendingBindings();
    }
}
