package com.laoodao.caididi.ui.my.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.laoodao.caididi.Const;
import com.laoodao.caididi.Global;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.ListActivity;
import com.laoodao.caididi.ui.wenda.activity.FollowUserActivity;
import com.laoodao.caididi.ui.wenda.holder.FollowUserHolder;

import ezy.lite.itemholder.adapter.BaseAdapter;
import ezy.lite.itemholder.adapter.ItemAdapter;
import ezy.lite.util.ContextUtil;

/**
 * Created by Administrator on 2016/12/29.
 */

public class FollowPeopleActivity extends ListActivity {
    private int type;
    private int cid;

    public static void start(Context context, int type) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        ContextUtil.startActivity(context, FollowPeopleActivity.class, bundle);
    }

    public static void start(Context context, int type, int cid) {
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        bundle.putInt("cid", cid);
        ContextUtil.startActivity(context, FollowPeopleActivity.class, bundle);
    }

    @Override
    protected BaseAdapter onCreateAdapter() {
        return new ItemAdapter<>(FollowUserHolder.class);
    }

    @Override
    protected void onBodyCreated() {
        super.onBodyCreated();
        binding.list.setBackgroundColor(Color.parseColor("#ffffff"));
        type = getIntent().getIntExtra("type", 0);
        cid = getIntent().getIntExtra("cid", 0);
        binding.loading.setOnCustomListener(() -> {
            ContextUtil.startActivity(FollowPeopleActivity.this, FollowUserActivity.class);
        });
    }



    private void display() {
        if (type == Const.FOLLOW_MY) {
            if (cid == 0) {
                setTitle("关注我的人");
            } else {
                setTitle("他的粉丝");
            }
        } else if (type == Const.MY_FOLLOW) {
            if (cid == 0) {
                setTitle("我关注的人");
                followPeople();
            } else {
                setTitle("他的关注");
            }
        }
    }

    private void followPeople() {
        if (Global.info().myAttention > 0) {
            return;
        }
        binding.loading.customLayout(R.layout.widget_no_choose).customText("您未关注任何人").customBtnText("添加用户").showCustom();
    }


    @Override
    protected void onPage(int page) {
        if (type == 0) {
            return;
        }
        if (cid == 0) {
            API.user().myFollow(page, type).compose(transform()).subscribe(result -> {
                onPageLoaded(result);
                display();
            });
        } else {
            API.user().userFollow(page, type, cid).compose(transform()).subscribe(result -> {
                onPageLoaded(result);
                display();
            });
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        onRefresh();
        if (addItem != null && (Global.info().myAttention < 1 || type == Const.FOLLOW_MY)) {
            addItem.setVisible(false);
        }
    }

    MenuItem addItem;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        addItem = menu.findItem(R.id.action_add);
        if (Global.info().myAttention < 1 || type == Const.FOLLOW_MY || cid != 0) {
            addItem.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.follow_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                ContextUtil.startActivity(this, FollowUserActivity.class);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
