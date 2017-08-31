package com.laoodao.caididi.ui.my.fragment;

import com.laoodao.caididi.Const;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.ListFragment;
import com.laoodao.caididi.ui.my.holder.ReserveListHolder;

import ezy.lite.itemholder.adapter.BaseAdapter;
import ezy.lite.itemholder.adapter.ItemAdapter;

/**
 * Created by WORK on 2017/3/7.
 */

public class UnReserveFragment extends ListFragment{
    @Override
    protected BaseAdapter onCreateAdapter() {
        return new ItemAdapter(ReserveListHolder.class);
    }

    @Override
    protected void onPage(int page) {
        API.user().myReserver(page, Const.UN).compose(transform()).subscribe(this::onPageLoaded);
    }
}
