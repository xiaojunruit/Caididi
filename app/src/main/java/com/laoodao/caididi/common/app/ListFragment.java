package com.laoodao.caididi.common.app;

import android.content.ClipData;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.Page;
import com.laoodao.caididi.common.api.Response;
import com.laoodao.caididi.common.api.Result;
import com.laoodao.caididi.databinding.LayoutListBinding;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ezy.lite.itemholder.adapter.BaseAdapter;
import ezy.lite.util.LogUtil;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ListFragment extends BaseFragment implements XRecyclerView.LoadingListener {

    protected BaseAdapter adapter;
    protected LayoutListBinding binding;
    protected boolean mIsLoaded = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        adapter = onCreateAdapter();
        binding = DataBindingUtil.inflate(inflater, R.layout.layout_list, container, false);
        binding.list.setLayoutManager(onCreateLayoutManager());
        binding.list.setAdapter(adapter);
        binding.list.setLoadingMoreEnabled(true);
        binding.list.setLoadingListener(this);
        binding.loading.showLoading();
        binding.loading.setOnRetryListener(() -> onRefresh());
        ViewGroup root = (ViewGroup) binding.getRoot();
        if (getHeaderLayoutId() > 0) {
            View header = inflater.inflate(getHeaderLayoutId(), root, false);
            root.addView(header, 0);
            onHeaderCreated(header);
        }
        if (getFooterLayoutId() > 0) {
            View footer = inflater.inflate(getFooterLayoutId(), root, false);
            root.addView(footer, root.getChildCount());
            onFooterCreated(footer);
        }
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onBodyCreated();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint() && !mIsLoaded) {
            onRefresh();
            mIsLoaded = true;
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisible) {
        super.setUserVisibleHint(isVisible);
        if (isVisible && isResumed() && !mIsLoaded) {
            onRefresh();
            mIsLoaded = true;
        }
    }

    protected void onPage(int page) {

    }

    protected void onBodyCreated() {

    }

    protected void onHeaderCreated(View view) {
    }

    protected void onFooterCreated(View view) {

    }

    protected int getHeaderLayoutId() {
        return 0;
    }

    protected int getFooterLayoutId() {
        return 0;
    }

    protected BaseAdapter onCreateAdapter() {
        return null;
    }

    protected RecyclerView.LayoutManager onCreateLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }


    int page = 1;
    protected int cursor = 0;

    @Override
    public void onRefresh() {
        page = 1;
        cursor = 0;
        onPage(page);
    }

    @Override
    public void onLoadMore() {
        page++;
        onPage(page);
    }

    @Override
    public <T extends Response> Observable.Transformer<T, T> transform() {
        return observable -> observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(throwable -> {
                    LogUtil.e(throwable.toString());
                    throwable.printStackTrace();
                    binding.loading.showError();
                    return Observable.empty();
                })
                /*.filter(result -> API.doCheck(getContext(), result))*/
                .doAfterTerminate(() -> {
                    if (page < 2) {
                        binding.list.refreshComplete();
                    } else {
                        binding.list.loadMoreComplete();
                    }
                });
    }

    public <Item> void onPageLoaded(Page<Item> result) {
        adapter.addAll(result.data.items, page < 2);
        if (result.data.total != 0) {
            binding.loading.showContent();
        } else {
            binding.loading.showEmpty();
        }
        cursor = result.data.cursor;
        boolean noMore = result.data.size * page >= result.data.total;
        if (noMore) {
            binding.list.noMoreLoading();
            binding.list.loadMoreComplete();
        }
        LogUtil.e("x = " + (result.data.size * page) + ", size = " + result.data.size + ", page = " + page + ", cursor = " + result.data.cursor + ", total = " + result.data.total);
    }

    public <Item> void onListLoaded(Result<List<Item>> result) {
        adapter.addAll(result.data, true);
        if (result.data.size() != 0) {
            binding.loading.showContent();
        } else {
            binding.loading.showEmpty();
        }
    }
}

