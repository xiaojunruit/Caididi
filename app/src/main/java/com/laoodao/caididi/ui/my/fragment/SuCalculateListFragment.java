package com.laoodao.caididi.ui.my.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.laoodao.caididi.Const;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.api.Page;
import com.laoodao.caididi.common.api.Response;
import com.laoodao.caididi.common.app.BaseFragment;
import com.laoodao.caididi.common.util.RxBus;
import com.laoodao.caididi.databinding.FragmentUncalculatelistBinding;
import com.laoodao.caididi.event.SettleEvent;
import com.laoodao.caididi.retrofit.user.Collection;
import com.laoodao.caididi.ui.my.adapter.UnCalculateAdapter;

import ezy.lite.util.LogUtil;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by WORK on 2017/3/9.
 */

public class SuCalculateListFragment extends BaseFragment implements XRecyclerView.LoadingListener{

    private FragmentUncalculatelistBinding binding;

    private UnCalculateAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_uncalculatelist, null, false);
        initData();
        RxBus.ofType(SettleEvent.class).compose(bindToLifecycle()).subscribe(event ->{
           onRefresh();
        });
        return binding.getRoot();
    }

    private void initData() {
        adapter = new UnCalculateAdapter(getContext());
        adapter.setStart(Const.START);
        binding.list.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.list.setAdapter(adapter);
        binding.list.setLoadingMoreEnabled(true);
        binding.list.setLoadingListener(this);
        binding.loading.showLoading();
        binding.loading.setOnRetryListener(() ->{onRefresh();});
        adapter.setPageState(Const.START);
        binding.llBottom.setVisibility(View.GONE);
        onRefresh();
    }

    protected int cursor = 0;
    int page = 1;

    public void onPageLoaded(Page<Collection> result) {

        if (page < 2) {
            adapter.addAll(result.data.items, true);
        } else {
            int size = adapter.getItemCount();
            adapter.getList().addAll(result.data.items);
            adapter.notifyItemRangeChanged(size + 2
                    , adapter.getList().size() - 1);
        }
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

    public <T extends Response> Observable.Transformer<T, T> transform() {
        return observable -> observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(throwable -> {
                    LogUtil.e(throwable.toString());
                    /*binding.loading.showError();*/
                    return Observable.empty();
                })
                .filter(result -> API.doCheck(getContext(), result))
                .doAfterTerminate(() -> {
                    if (page < 2) {
                        binding.list.refreshComplete();
                    } else {
                        binding.list.loadMoreComplete();
                    }
                });
    }

    @Override
    public void onRefresh() {
        binding.list.setIsnomore(false);
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
        API.user().myPurchase(page, Const.START).compose(transform()).subscribe(this::onPageLoaded);
    }

}
