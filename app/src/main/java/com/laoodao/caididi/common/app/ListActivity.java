package com.laoodao.caididi.common.app;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.api.Page;
import com.laoodao.caididi.common.api.Response;
import com.laoodao.caididi.databinding.LayoutListBinding;

import ezy.lite.itemholder.adapter.BaseAdapter;
import ezy.lite.util.LogUtil;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ListActivity extends BaseActivity implements XRecyclerView.LoadingListener {
    protected BaseAdapter adapter;
    protected LayoutListBinding binding;
    protected int cursor = 0;
    int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        binding = DataBindingUtil.bind(findViewById(R.id.body));

        LayoutInflater inflater = LayoutInflater.from(this);
        adapter = onCreateAdapter();
        binding.list.setLayoutManager(onCreateLayoutManager());
        binding.list.setAdapter(adapter);
        binding.list.setLoadingMoreEnabled(true);
        binding.list.setLoadingListener(this);
//        binding.list.setItemAnimator(new DefaultItemAnimator());
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


        onBodyCreated();
        onRefresh();
    }

    protected void onBodyCreated() {
    }

    protected void onPage(int page) {

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
        return new LinearLayoutManager(this);
    }


    @Override
    public void onLoadMore() {
        page++;
        onPage(page);
    }

    @Override
    public void onRefresh() {
        binding.list.setIsnomore(false);
        page = 1;
        cursor = 0;
        onPage(page);
    }

    @Override
    public <T extends Response> Observable.Transformer<T, T> transform() {
        return observable -> observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorResumeNext(throwable -> {
                    LogUtil.e(throwable.toString());
                    binding.loading.showError();
                    return Observable.empty();
                })
                .compose(bindToLifecycle())
                .filter(result -> API.doCheck(this, result))
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
//        if (page < 2) {
//            adapter.addAll(result.data.items, true);
//        } else {
//            int size = adapter.getItemCount();
//            adapter.getList().addAll(result.data.items);
//            adapter.notifyItemRangeChanged(size + 2
//                    , adapter.getList().size() - 1);
//        }

        if (result.data.total != 0) {
            binding.loading.showContent();
        } else {
            binding.loading.showEmpty();

        }
        cursor = result.data.cursor;
        boolean noMore = result.data.size * page >= result.data.total;
        Log.e(")))))))))))))))",noMore+""+result.data.size * page+"++++++++++"+result.data.total);
        if (noMore) {
            binding.list.noMoreLoading();
            binding.list.loadMoreComplete();
        }
//        binding.list.setIsnomore(result.data.size * page >= result.data.total);

        LogUtil.e("x = " + (result.data.size * page) + ", size = " + result.data.size + ", page = " + page + ", cursor = " + result.data.cursor + ", total = " + result.data.total);
    }
}