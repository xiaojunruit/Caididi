package com.laoodao.caididi.ui.my.fragment;

import com.laoodao.caididi.Const;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.ListFragment;
import com.laoodao.caididi.ui.my.holder.GreensAllHolder;

import ezy.lite.itemholder.adapter.BaseAdapter;
import ezy.lite.itemholder.adapter.ItemAdapter;

/**
 * Created by WORK on 2017/3/15.
 */

public class GreensAllFragment extends ListFragment{
    @Override
    protected BaseAdapter onCreateAdapter() {
        return new ItemAdapter(GreensAllHolder.class);
    }

    @Override
    protected void onPage(int page) {
        API.user().orderList(page, Const.ALL).compose(transform()).subscribe(this::onPageLoaded);
    }
}
