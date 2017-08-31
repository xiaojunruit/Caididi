package com.laoodao.caididi.ui.my.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.api.FarlandPage;
import com.laoodao.caididi.common.api.Response;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.databinding.ActivityFarmlandManagementBinding;
import com.laoodao.caididi.ui.my.holder.FarmlandManagementHolder;

import ezy.lite.itemholder.adapter.BaseAdapter;
import ezy.lite.itemholder.adapter.ItemAdapter;
import ezy.lite.util.ContextUtil;
import ezy.lite.util.LogUtil;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by XiaoGe on 2017/2/8.
 */

/**
 * 农田管理
 */
public class FarmlandManagementActivity extends BaseActivity implements XRecyclerView.LoadingListener, View.OnClickListener {
    ActivityFarmlandManagementBinding mBinding;
    private BaseAdapter adapter = new ItemAdapter<>(FarmlandManagementHolder.class);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_farmland_management);
        initView();
        mBinding.setListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        onRefresh();
    }

    private void initView() {
        mBinding.loading.showLoading();
        mBinding.loading.getEmptyView().findViewById(R.id.empty_button).setOnClickListener(this);
        mBinding.list.setLayoutManager(new LinearLayoutManager(this));
        mBinding.list.setAdapter(adapter);
        mBinding.list.setLoadingMoreEnabled(true);
        mBinding.list.setLoadingListener(this);

    }

    protected int cursor = 0;
    int page = 1;

    public <Item> void onPageLoaded(FarlandPage<Item> result) {

        if (page < 2) {
            adapter.addAll(result.data.items, true);
        } else {
            int size = adapter.getItemCount();
            adapter.getList().addAll(result.data.items);
            adapter.notifyItemRangeChanged(size + 2
                    , adapter.getList().size() - 1);
        }
        if (result.data.total != 0) {
            mBinding.loading.showContent();
        } else {
            mBinding.loading.showEmpty();

        }
        cursor = result.data.cursor;
        boolean noMore = result.data.size * page >= result.data.total;
        if (noMore) {
            mBinding.list.noMoreLoading();
            mBinding.list.loadMoreComplete();
        }
        LogUtil.e("x = " + (result.data.size * page) + ", size = " + result.data.size + ", page = " + page + ", cursor = " + result.data.cursor + ", total = " + result.data.total);
    }

    public <T extends Response> Observable.Transformer<T, T> transform() {
        return observable -> observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(throwable -> {
                    mBinding.loading.showError();
                    LogUtil.e(throwable.toString());
                    return Observable.empty();
                })
                .filter(result -> API.doCheck(this, result))
                .doAfterTerminate(() -> {
                    if (page < 2) {
                        mBinding.list.refreshComplete();
                    } else {
                        mBinding.list.loadMoreComplete();
                    }
                });
    }

    @Override
    public void onRefresh() {
        mBinding.list.setIsnomore(false);
        page = 1;
        cursor = 0;
        onPage(page);
    }

    @Override
    public void onLoadMore() {
        page++;
        onPage(page);
    }

    private void onPage(int page) {
        API.user().cropland(page).compose(transform()).subscribe(result -> {
            onPageLoaded(result);
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.empty_button:
            case R.id.btn_add_farming:
            case R.id.txt_add_farmland:
                ContextUtil.startActivity(this, AddFarmlandActivity.class);
                break;

        }
    }
}
