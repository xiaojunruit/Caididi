package com.laoodao.caididi.ui.my.activity;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.andview.refreshview.XRefreshView;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.common.app.ListActivity;
import com.laoodao.caididi.databinding.ActivityOperationHistoryBinding;
import com.laoodao.caididi.databinding.HeaderOperationBinding;
import com.laoodao.caididi.ui.my.holder.OperationHistoryHolder;
import com.laoodao.caididi.ui.widget.xRefreshView.RefreshHeader;

import ezy.lite.itemholder.adapter.BaseAdapter;
import ezy.lite.itemholder.adapter.ItemAdapter;
import ezy.lite.util.ContextUtil;

/**
 * Created by WORK on 2017/3/9.
 */

public class OperationHistoryActivity extends BaseActivity implements  XRefreshView.XRefreshViewListener{
    private ActivityOperationHistoryBinding binding;
    private BaseAdapter adapter = new ItemAdapter(OperationHistoryHolder.class);
    private int id;

    public static void start(Context context, int id) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        ContextUtil.startActivity(context, OperationHistoryActivity.class, bundle);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_operation_history);
        initData();
        refresh();
    }

    private void initData() {
        id = getIntent().getIntExtra("id", 0);
        binding.list.setLayoutManager(new LinearLayoutManager(this));
        binding.list.setAdapter(adapter);
        binding.refresh.setXRefreshViewListener(this);
        binding.refresh.setCustomHeaderView(new RefreshHeader(this));
        binding.refresh.setMoveForHorizontal(true);
        binding.loading.showLoading();
        binding.loading.setOnRetryListener(() -> {
            refresh();
        });
    }

    private void refresh() {
        API.user().operatehis(id).compose(onErrorTransform((context, throwable) -> {
            binding.loading.showError();
            binding.refresh.stopRefresh();
        })).subscribe(result -> {
            binding.loading.showContent();
            adapter.addAll(result.data.operatelist,true);
            binding.setItem(result.data);
            binding.refresh.stopRefresh();
        });
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    @Override
    public void onLoadMore(boolean isSilence) {

    }

    @Override
    public void onRelease(float direction) {

    }

    @Override
    public void onHeaderMove(double headerMovePercent, int offsetY) {

    }
}
