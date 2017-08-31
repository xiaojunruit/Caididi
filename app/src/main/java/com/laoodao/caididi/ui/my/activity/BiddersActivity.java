package com.laoodao.caididi.ui.my.activity;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.laoodao.caididi.Const;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.common.util.RxBus;
import com.laoodao.caididi.databinding.ActivityBidderBinding;
import com.laoodao.caididi.event.SettleEvent;
import com.laoodao.caididi.ui.my.adapter.UnCalculateAdapter;
import com.laoodao.caididi.ui.my.holder.BiddersHolder;

import java.util.ArrayList;

import ezy.lite.itemholder.adapter.BaseAdapter;
import ezy.lite.itemholder.adapter.ItemAdapter;
import ezy.lite.util.ContextUtil;

/**
 * Created by WORK on 2017/3/9.
 */

public class BiddersActivity extends BaseActivity implements XRecyclerView.LoadingListener {
    private ActivityBidderBinding binding;
    private int id;
    private int start;
    private BaseAdapter adapter = new ItemAdapter(BiddersHolder.class);

    public static void start(Context context, int id) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        ContextUtil.startActivity(context, BiddersActivity.class, bundle);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bidder);
        initData();
        refresh();
        binding.btnSubmit.setOnClickListener(v -> {
            submit();
        });
    }

    private void submit() {
        API.user().collection(String.valueOf(id)).compose(transform()).subscribe(result -> {
            binding.btnSubmit.setVisibility(View.GONE);
            RxBus.post(new SettleEvent());
        });
    }

    private void initData() {
        id = getIntent().getIntExtra("id", 0);
        start = getIntent().getIntExtra("start", 0);
        binding.list.setLayoutManager(new LinearLayoutManager(this));
        binding.list.setAdapter(adapter);
        binding.list.setLoadingMoreEnabled(true);
        binding.list.setLoadingListener(this);
        binding.list.setLoadingMoreEnabled(false);
        binding.loading.showLoading();
    }

    private void refresh() {
        API.user().purpayDetail(id)
                .compose(onErrorTransform((context, throwable) -> {
                    binding.loading.showError();
                }))
                .subscribe(result -> {
                    binding.loading.showContent();
                    binding.list.refreshComplete();
                    binding.btnSubmit.setVisibility(result.data.status == Const.NO_START ? View.VISIBLE : View.GONE);
                    adapter.addAll(result.data.list, true);
                    binding.setItem(result.data);
                });
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    @Override
    public void onLoadMore() {

    }
}
