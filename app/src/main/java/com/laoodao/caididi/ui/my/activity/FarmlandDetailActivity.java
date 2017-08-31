package com.laoodao.caididi.ui.my.activity;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.api.Page;
import com.laoodao.caididi.common.api.Response;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.databinding.ActivityFarmlandDetailBinding;
import com.laoodao.caididi.retrofit.user.FarmlandDetail;
import com.laoodao.caididi.ui.my.holder.FarmlandDetaiListlHolder;

import ezy.lite.itemholder.adapter.ItemAdapter;
import ezy.lite.util.ContextUtil;
import ezy.lite.util.LogUtil;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by WORK on 2017/2/8.
 */

public class FarmlandDetailActivity extends BaseActivity implements View.OnClickListener, XRecyclerView.LoadingListener {

    private ActivityFarmlandDetailBinding binding;

    private ItemAdapter adapter = new ItemAdapter(FarmlandDetaiListlHolder.class);

    public int id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_farmland_detail);
        binding.setOnClick(this);
        id = getIntent().getIntExtra("id", 0);
        binding.list.setAdapter(adapter);
        binding.list.setLoadingListener(this);
        binding.loading.showLoading();
        View emptyView = binding.loading.getEmptyView();
        emptyView.findViewById(R.id.empty_button).setOnClickListener(this);
        TextView tvText = (TextView) emptyView.findViewById(R.id.empty_text);
        tvText.setText("您还没有农田操作");



    }

    @Override
    protected void onResume() {
        super.onResume();
        onRefresh();
        API.user().landDetail(id).compose(transform()).subscribe(result -> {
            binding.setItem(result.data);
        });
    }

    public static void start(Context context, int id) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        ContextUtil.startActivity(context, FarmlandDetailActivity.class, bundle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                AddFarmlandActivity.start(this, binding.getItem());
                return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_edit:
                FarmlandDetail fd = binding.getItem();
                AddFarmlandActivity.start(this, fd);
                break;
            case R.id.btn_back:
                finish();
                break;
            case R.id.empty_button:
            case R.id.btn_add_operation:
                AddOperationActivity.start(this,id);
                break;


        }
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
                    binding.loading.showError();
                    return Observable.empty();
                })
                .filter(result -> API.doCheck(this, result))
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
        API.user().landDetailList(id, page).compose(transform()).subscribe(result -> {
            onPageLoaded(result);
        });
    }
}
