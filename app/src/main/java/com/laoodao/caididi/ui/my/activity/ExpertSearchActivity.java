package com.laoodao.caididi.ui.my.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.api.Page;
import com.laoodao.caididi.common.api.Response;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.databinding.ActivityExpertSearchBinding;
import com.laoodao.caididi.ui.my.holder.ExpertHolder;

import ezy.lite.itemholder.adapter.ItemAdapter;
import ezy.lite.util.LogUtil;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by long on 2017/8/28.
 */

public class ExpertSearchActivity extends BaseActivity implements View.OnClickListener,XRecyclerView.LoadingListener{
    private ItemAdapter mAdapter=new ItemAdapter(ExpertHolder.class);
    private ActivityExpertSearchBinding mBinding;
    private int page=1;
    private int cursor = 0;
    private String kw="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding= DataBindingUtil.setContentView(this,R.layout.activity_expert_search);
        mBinding.setListener(this);
        mBinding.list.setLayoutManager(new LinearLayoutManager(this));
        mBinding.list.setAdapter(mAdapter);
        mBinding.txtKeyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                kw=editable.toString();
                if (kw.length()<1){
                    mBinding.list.setVisibility(View.GONE);
                }else{
                    onRefresh();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_search:
                break;
        }
    }

    public <Item> void onPageLoaded(Page<Item> result) {
        if (result.data.items.size()>=1){
            mBinding.list.setVisibility(View.VISIBLE);
        }
        mAdapter.addAll(result.data.items, page < 2);
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
        API.user().getExper(page,kw).compose(transform()).subscribe(result ->{
            onPageLoaded(result);
        });
    }
}
