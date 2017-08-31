package com.laoodao.caididi.ui.nongye.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.ListFragment;
import com.laoodao.caididi.ui.nongye.holder.FarmingHolder;

import java.util.List;

import ezy.lite.itemholder.adapter.BaseAdapter;
import ezy.lite.itemholder.adapter.ItemAdapter;

/**
 * Created by WORK on 2017/2/17.
 */

public class TabFarmingListFragment extends ListFragment{
    private int gcId;
    @Override
    protected BaseAdapter onCreateAdapter() {
        return new ItemAdapter<>(FarmingHolder.class);
    }

    @Override
    protected void onBodyCreated() {
        gcId=getArguments().getInt("gcId");
    }

    @Override
    protected void onPage(int page) {
        API.main().nlist(gcId,"",page).compose(transform()).subscribe(this::onPageLoaded);
    }
}
