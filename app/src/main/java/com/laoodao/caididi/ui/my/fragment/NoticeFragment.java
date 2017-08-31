package com.laoodao.caididi.ui.my.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.ListFragment;
import com.laoodao.caididi.common.util.RxBus;
import com.laoodao.caididi.event.ClearMesEvent;
import com.laoodao.caididi.event.ReadNoticeEvent;
import com.laoodao.caididi.retrofit.user.Notice;
import com.laoodao.caididi.ui.my.holder.NoticeHolder;
import com.laoodao.caididi.ui.my.holder.NoticeTitleHolder;

import ezy.lite.itemholder.adapter.BaseAdapter;
import ezy.lite.itemholder.adapter.ItemAdapter;
import ezy.lite.itemholder.adapter.ItemTypedAdapter;
import ezy.lite.util.LogUtil;

/**
 * Created by WORK on 2017/1/11.
 */

public class NoticeFragment extends ListFragment {
    @Override
    protected BaseAdapter onCreateAdapter() {
        return new ItemAdapter(NoticeHolder.class);
    }


    @Override
    protected void onBodyCreated() {
        super.onBodyCreated();
        RxBus.ofType(ClearMesEvent.class).compose(bindToLifecycle()).subscribe(event -> {
            onRefresh();
        });
        RxBus.ofType(ReadNoticeEvent.class).compose(bindToLifecycle()).subscribe(event -> {
            API.user().readnotice(event.id).compose(transform()).subscribe(result -> {
            });
        });
    }


    @Override
    protected void onPage(int page) {
        API.user().messageNotice(page).compose(transform()).subscribe(this::onPageLoaded);
    }

}
