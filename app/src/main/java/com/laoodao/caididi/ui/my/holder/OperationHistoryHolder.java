package com.laoodao.caididi.ui.my.holder;

import android.graphics.Color;
import android.view.View;

import com.laoodao.caididi.Global;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.binding.BindingHolder;
import com.laoodao.caididi.databinding.ItemOperationHistoryBinding;
import com.laoodao.caididi.retrofit.user.OperateHistory;

import ezy.lite.itemholder.annotation.ItemLayout;
import ezy.lite.util.Device;
import ezy.lite.util.LogUtil;

/**
 * Created by WORK on 2017/3/9.
 */
@ItemLayout(R.layout.item_operation_history)
public class OperationHistoryHolder extends BindingHolder<OperateHistory.OperateList, ItemOperationHistoryBinding> {
    public OperationHistoryHolder(View v) {
        super(v);
    }

    @Override
    public void refresh() {
        if (position == 0) {
            binding.viewLine.setVisibility(View.GONE);
            binding.llContent.setPadding(0, 0, 0, Device.dp2px(10));
            binding.imgStep.setImageResource(R.mipmap.icon_step_sel);
            binding.txtContent.setTextColor(Color.parseColor("#2AB80E"));
        }
        binding.setItem(item);
    }
}
