package com.laoodao.caididi.ui.wenda.activity;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.api.Page;
import com.laoodao.caididi.common.api.Response;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.databinding.ActivityFollowUserBinding;
import com.laoodao.caididi.ui.wenda.holder.FollowUserHolder;

import ezy.lite.itemholder.adapter.BaseAdapter;
import ezy.lite.itemholder.adapter.ItemAdapter;
import ezy.lite.util.LogUtil;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/12/22.
 */

public class FollowUserActivity extends BaseActivity implements XRecyclerView.LoadingListener {
    ActivityFollowUserBinding binding;

    protected BaseAdapter adapter;
    private String keyword = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_follow_user);
        initData();
        binding.edittxtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.searchList.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                binding.list.setVisibility(s.length() > 0 ? View.GONE : View.VISIBLE);
                keyword = s.toString();
                searchSuggest();
            }
        });
        binding.getRoot().setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (binding.edittxtSearch.isFocused()) {
                    Rect outRect = new Rect();
                    binding.edittxtSearch.getGlobalVisibleRect(outRect);
                    if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                        binding.edittxtSearch.clearFocus();
                        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
            return false;
        });
//        onRefresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
        onRefresh();
    }

    private void initData() {
        adapter = new ItemAdapter<>(FollowUserHolder.class);
        binding.searchList.setLayoutManager(new LinearLayoutManager(this));
        binding.searchList.setAdapter(adapter);
        binding.searchList.setLoadingMoreEnabled(true);
        binding.searchList.setLoadingListener(this);
        binding.list.setLayoutManager(new LinearLayoutManager(this));
        binding.list.setAdapter(adapter);
        binding.list.setLoadingMoreEnabled(true);
        binding.list.setLoadingListener(this);
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        RxBus.post(new FollowPeopleEvent());
//    }


    private void searchSuggest() {
        adapter.clear();
        onRefresh();
    }


    protected int cursor = 0;
    int page = 1;

    public <Item> void onPageLoaded(Page<Item> result) {

        if (page < 2) {
            adapter.addAll(result.data.items, true);
        } else {
            int size = adapter.getItemCount();
            adapter.getList().addAll(result.data.items);
            adapter.notifyItemRangeChanged(size + 2
                    , adapter.getList().size() - 1);
        }
        cursor = result.data.cursor;
        boolean noMore = result.data.size * page >= result.data.total;
        if (noMore) {
            binding.searchList.noMoreLoading();
            binding.searchList.loadMoreComplete();
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
                    return Observable.empty();
                })
                .filter(result -> API.doCheck(this, result))
                .doAfterTerminate(() -> {
                    if (page < 2) {
                        binding.searchList.refreshComplete();
                        binding.list.refreshComplete();
                    } else {
                        binding.searchList.loadMoreComplete();
                        binding.list.loadMoreComplete();
                    }
                });
    }

    @Override
    public void onRefresh() {
        binding.searchList.setIsnomore(false);
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
        API.main().followUserList(page, keyword).compose(transform()).subscribe(this::onPageLoaded);
    }

}
