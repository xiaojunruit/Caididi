package com.laoodao.caididi.ui.my.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.common.app.ListActivity;
import com.laoodao.caididi.common.util.RxBus;
import com.laoodao.caididi.event.MyUnAnswerEvent;
import com.laoodao.caididi.ui.my.holder.MyProblemHolder;
import com.laoodao.caididi.ui.my.holder.MyUnAnswerHolder;
import com.laoodao.caididi.ui.wenda.holder.AnswerHolder;

import ezy.lite.itemholder.adapter.BaseAdapter;
import ezy.lite.itemholder.adapter.ItemAdapter;

/**
 * Created by WORK on 2017/3/6.
 */

public class MyUnAnswerActivity extends ListActivity {

    @Override
    protected BaseAdapter onCreateAdapter() {
        return new ItemAdapter<>(MyUnAnswerHolder.class);
    }

    @Override
    protected void onBodyCreated() {
        super.onBodyCreated();
        RxBus.ofType(MyUnAnswerEvent.class).compose(bindToLifecycle()).subscribe(event ->{
            onRefresh();
        });
    }

    @Override
    protected void onPage(int page) {
        super.onPage(page);
        API.user().myUnAnswer(page).compose(transform()).subscribe(this::onPageLoaded);
    }
}
