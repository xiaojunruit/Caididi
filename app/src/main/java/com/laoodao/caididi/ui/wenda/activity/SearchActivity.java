package com.laoodao.caididi.ui.wenda.activity;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.api.Page;
import com.laoodao.caididi.common.api.Response;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.databinding.ActivitySearchBinding;
import com.laoodao.caididi.ui.wenda.holder.SearchHolder;

import ezy.lite.itemholder.adapter.BaseAdapter;
import ezy.lite.itemholder.adapter.ItemAdapter;
import ezy.lite.util.LogUtil;
import ezy.lite.util.UI;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by WORK on 2017/1/4.
 */

public class SearchActivity extends BaseActivity implements View.OnClickListener, XRecyclerView.LoadingListener {

    private ActivitySearchBinding binding;
    protected BaseAdapter adapter;
    private String keyword = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        binding.setListener(this);
        adapter = new ItemAdapter<>(SearchHolder.class);
        binding.searchList.setLayoutManager(new LinearLayoutManager(this));
        binding.searchList.setAdapter(adapter);
        binding.searchList.setLoadingMoreEnabled(true);
        binding.searchList.setLoadingListener(this);
        binding.txtKeyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                binding.btnDelete.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                binding.llHistorySearch.setVisibility(s.length() > 0 ? View.GONE : View.VISIBLE);
                binding.searchList.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                keyword = s.toString();
                searchSuggest();
            }
        });
        binding.tagRecommend.setOnTagItemClickListener((view, position, tag) -> {
            binding.txtKeyword.setText((String) tag);
        });

        binding.getRoot().setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (binding.txtKeyword.isFocused()) {
                    Rect outRect = new Rect();
                    binding.txtKeyword.getGlobalVisibleRect(outRect);
                    if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                        binding.txtKeyword.clearFocus();
                        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
            return false;
        });
        recommendedKeyword();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_delete:
                binding.txtKeyword.setText("");
                break;
            case R.id.btn_search:
                if (TextUtils.isEmpty(keyword)) {
                    UI.showToast(this, "请输入搜索信息");
                }
                searchSuggest();
                break;
        }

    }

    public void recommendedKeyword() {
        API.main().recommendedKeyword().compose(transform()).subscribe(result -> {
            binding.tagRecommend.setList(result.data.items);
        });

    }

    private void searchSuggest() {
        if (TextUtils.isEmpty(keyword)) {
            adapter.clear();
            return;
        }
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
                .filter(result -> API.doCheck(this, result))
                .doAfterTerminate(() -> {
                    if (page < 2) {
                        binding.searchList.refreshComplete();
                    } else {
                        binding.searchList.loadMoreComplete();
                    }
                });
    }

    @Override
    public void onRefresh() {
        binding.searchList.setIsnomore(false);
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
        API.main().newAnswerList(page, "", "", "", keyword).compose(transform()).subscribe(this::onPageLoaded);
    }
}
