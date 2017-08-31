package com.laoodao.caididi.ui.my.holder;

import android.view.View;

import com.laoodao.caididi.R;
import com.laoodao.caididi.common.binding.BindingHolder;
import com.laoodao.caididi.databinding.ItemInviteListBinding;
import com.laoodao.caididi.retrofit.user.InviteList;

import ezy.lite.itemholder.annotation.ItemLayout;

/**
 * Created by WORK on 2017/3/28.
 */
@ItemLayout(R.layout.item_invite_list)
public class InviteListHolder extends BindingHolder<InviteList, ItemInviteListBinding> {
    public InviteListHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void refresh() {
        binding.setItem(item);
        binding.txtNum.setBackgroundResource((position % 2) == 0 ? R.drawable.bg_border_gray : R.drawable.bg_border_gray_white);
        binding.txtName.setBackgroundResource(((position % 2) == 0 ? R.drawable.bg_border_gray : R.drawable.bg_border_gray_white));
    }
}
