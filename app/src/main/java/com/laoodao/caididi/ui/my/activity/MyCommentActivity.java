package com.laoodao.caididi.ui.my.activity;

import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.ListActivity;
import com.laoodao.caididi.ui.my.holder.MyCommentHolder;
import com.laoodao.caididi.ui.my.holder.MyProblemHolder;

import ezy.lite.itemholder.adapter.BaseAdapter;
import ezy.lite.itemholder.adapter.ItemAdapter;

/**
 * Created by Administrator on 2016/12/29.
 */

public class MyCommentActivity extends ListActivity{
    @Override
    protected BaseAdapter onCreateAdapter() {
        return new ItemAdapter<>(MyCommentHolder.class);
    }

    @Override
    protected void onBodyCreated() {
        super.onBodyCreated();
    }

    @Override
    protected void onPage(int page) {
        API.user().myComments(page).compose(transform()).subscribe(this::onPageLoaded);
    }
}
