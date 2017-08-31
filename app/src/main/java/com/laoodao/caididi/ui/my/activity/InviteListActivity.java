package com.laoodao.caididi.ui.my.activity;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.ListActivity;
import com.laoodao.caididi.databinding.HeaderInviteListBinding;
import com.laoodao.caididi.ui.my.holder.InviteListHolder;

import ezy.lite.itemholder.adapter.BaseAdapter;
import ezy.lite.itemholder.adapter.ItemAdapter;
import ezy.lite.util.ContextUtil;

/**
 * Created by WORK on 2017/3/28.
 */

public class InviteListActivity extends ListActivity {

    private HeaderInviteListBinding headerBinding;
    @Override
    protected BaseAdapter onCreateAdapter() {
        return new ItemAdapter<>(InviteListHolder.class);
    }


    public static void start(Context context, String inviteNum) {
        Bundle bundle = new Bundle();
        bundle.putString("inviteNum", inviteNum);
        ContextUtil.startActivity(context, InviteListActivity.class, bundle);
    }

    @Override
    protected void onBodyCreated() {
        super.onBodyCreated();
        headerBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.header_invite_list, null, false);
        binding.list.addHeaderView(headerBinding.getRoot());
        headerBinding.setInviteNum(getIntent().getStringExtra("inviteNum"));
        binding.list.setPullRefreshEnabled(false);
    }

    @Override
    protected void onPage(int page) {
        API.user().inviteList(page).compose(transform()).subscribe(this::onPageLoaded);
    }


}
