package com.laoodao.caididi.ui.my.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.api.Page;
import com.laoodao.caididi.common.api.Response;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.databinding.ActivityExpertListBinding;
import com.laoodao.caididi.ui.my.holder.ExpertHolder;

import ezy.lite.itemholder.adapter.ItemAdapter;
import ezy.lite.util.ContextUtil;
import ezy.lite.util.LogUtil;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2017/8/23 0023.
 */

public class ExpertActivity extends BaseActivity implements XRecyclerView.LoadingListener, View.OnClickListener{

    private ItemAdapter mAdapter=new ItemAdapter(ExpertHolder.class);
    private ActivityExpertListBinding mBinding;
    private int page=1;
    private int cursor = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding= DataBindingUtil.setContentView(this,R.layout.activity_expert_list);
        mBinding.setListener(this);
        mBinding.list.setLayoutManager(new LinearLayoutManager(this));
        mBinding.list.setAdapter(mAdapter);
        mBinding.list.setLoadingListener(this);
        mBinding.loading.showLoading();
        mBinding.loading.setOnRetryListener(() -> onRefresh());
        onRefresh();
    }


    public <Item> void onPageLoaded(Page<Item> result) {

        mAdapter.addAll(result.data.items, page < 2);
        cursor = result.data.cursor;
        boolean noMore = result.data.size * page >= result.data.total;
        if (noMore) {
            mBinding.list.noMoreLoading();
            mBinding.list.loadMoreComplete();
        }
        if (result.data.total != 0) {
            mBinding.loading.showContent();
        } else {
            mBinding.loading.showEmpty();
        }
        LogUtil.e("x = " + (result.data.size * page) + ", size = " + result.data.size + ", page = " + page + ", cursor = " + result.data.cursor + ", total = " + result.data.total);
    }

    public <T extends Response> Observable.Transformer<T, T> transform() {
        return observable -> observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(throwable -> {
                    LogUtil.e(throwable.toString());
                    mBinding.loading.showError();
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
        API.user().getExper(page,"").compose(transform()).subscribe(result ->{
            onPageLoaded(result);
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_search:
                ContextUtil.startActivity(this,ExpertSearchActivity.class);
                break;
        }
    }


    //    @Override
//    protected BaseAdapter onCreateAdapter() {
//        return new ItemAdapter<>(ExpertHolder.class);
//    }
//
//    @Override
//    protected void onBodyCreated() {
//        super.onBodyCreated();
//    }
//
//    @Override
//    protected void onPage(int page) {
//        API.user().getExper(page).compose(transform()).subscribe(this::onPageLoaded);
//    }


}
