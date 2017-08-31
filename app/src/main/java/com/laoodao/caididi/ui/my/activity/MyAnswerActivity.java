package com.laoodao.caididi.ui.my.activity;

import android.content.Context;
import android.os.Bundle;

import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.ListActivity;
import com.laoodao.caididi.retrofit.user.MyProblem;
import com.laoodao.caididi.ui.my.holder.MyAnswerHolder;
import com.laoodao.caididi.ui.my.holder.MyProblemHolder;

import ezy.lite.itemholder.adapter.BaseAdapter;
import ezy.lite.itemholder.adapter.ItemAdapter;
import ezy.lite.util.ContextUtil;

/**
 * Created by Administrator on 2016/12/29.
 */

public class MyAnswerActivity extends ListActivity {
    private int cid;

    public static void start(Context context, int cid) {
        Bundle bundle = new Bundle();
        bundle.putInt("cid", cid);
        ContextUtil.startActivity(context, MyAnswerActivity.class, bundle);
    }

    @Override
    protected BaseAdapter onCreateAdapter() {
        return new ItemAdapter<>(MyAnswerHolder.class);
    }

    @Override
    protected void onBodyCreated() {
        cid = getIntent().getIntExtra("cid", 0);
        setTitle(cid == 0 ? "我的回答" : "他的回答");
    }

    @Override
    protected void onPage(int page) {
        if (cid == 0) {
            API.user().myAnswer(page).compose(transform()).subscribe(this::onPageLoaded);
        } else {
            API.user().userAnswer(cid, page).compose(transform()).subscribe(this::onPageLoaded);
        }
    }
}
