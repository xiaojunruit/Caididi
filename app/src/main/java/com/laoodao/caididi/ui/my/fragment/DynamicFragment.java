package com.laoodao.caididi.ui.my.fragment;

import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.ListFragment;
import com.laoodao.caididi.common.util.RxBus;
import com.laoodao.caididi.event.ClearMesEvent;
import com.laoodao.caididi.ui.my.activity.MessageActivity;
import com.laoodao.caididi.ui.my.holder.MesDynamicHolder;

import ezy.lite.itemholder.adapter.BaseAdapter;
import ezy.lite.itemholder.adapter.ItemAdapter;

/**
 * Created by Administrator on 2017/1/11.
 */

public class DynamicFragment extends ListFragment {
    @Override
    protected BaseAdapter onCreateAdapter() {
        return new ItemAdapter<>(MesDynamicHolder.class);
    }

    @Override
    protected void onBodyCreated() {
        super.onBodyCreated();
        RxBus.ofType(ClearMesEvent.class).compose(bindToLifecycle()).subscribe(event -> {
            onRefresh();
        });
    }

    @Override
    protected void onPage(int page) {
        API.user().readMes(MessageActivity.DYNAMIC, page).compose(transform()).subscribe(this::onPageLoaded);
    }
}
