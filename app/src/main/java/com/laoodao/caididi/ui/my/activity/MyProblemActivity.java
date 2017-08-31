package com.laoodao.caididi.ui.my.activity;

import android.content.Context;
import android.os.Bundle;

import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.ListActivity;
import com.laoodao.caididi.ui.my.holder.MyProblemHolder;

import ezy.lite.itemholder.adapter.BaseAdapter;
import ezy.lite.itemholder.adapter.ItemAdapter;
import ezy.lite.util.ContextUtil;
import ezy.lite.util.LogUtil;

/**
 * Created by Administrator on 2016/12/29.
 */

public class MyProblemActivity extends ListActivity {
    private int cid;

    public static void start(Context context, int cid) {
        Bundle bundle = new Bundle();
        bundle.putInt("cid", cid);
        ContextUtil.startActivity(context, MyProblemActivity.class, bundle);
    }

    @Override
    protected BaseAdapter onCreateAdapter() {
        return new ItemAdapter<>(MyProblemHolder.class);
    }

    @Override
    protected void onBodyCreated() {
        cid = getIntent().getIntExtra("cid", 0);
        setTitle(cid == 0 ? "我的提问" : "他的提问");
    }

    @Override
    protected void onPage(int page) {
        if (cid == 0) {
            API.user().myProblem(page).compose(transform()).subscribe(this::onPageLoaded);
        } else {
            API.user().userProblem(cid, page).compose(transform()).subscribe(this::onPageLoaded);
        }
    }
}
