package com.laoodao.caididi.ui.my.fragment;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
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

import java.text.DecimalFormat;

import ezy.lite.util.LogUtil;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by WORK on 2017/3/9.
 */

public class UnCalculateListFragment extends BaseFragment implements XRecyclerView.LoadingListener, View.OnClickListener {

    private FragmentUncalculatelistBinding binding;

    private UnCalculateAdapter adapter;
    private double totalMoney;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_uncalculatelist, null, false);
        initData();
        listener();
        RxBus.ofType(SettleEvent.class).compose(bindToLifecycle()).subscribe(event -> {
            onRefresh();
        });

        return binding.getRoot();
    }

    private void initData() {
        binding.setListener(this);
        adapter = new UnCalculateAdapter(getContext());
        adapter.setStart(Const.NO_START);
        binding.list.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.list.setAdapter(adapter);
        binding.list.setLoadingMoreEnabled(true);
        binding.list.setLoadingListener(this);
        binding.loading.showLoading();
        binding.loading.setOnRetryListener(() -> onRefresh());
        adapter.setPageState(Const.NO_START);
        binding.txtTotalMoney.setText(Html.fromHtml(getResources().getString(R.string.settle_money, (formatDouble(totalMoney)))));
        onRefresh();
    }

    public static String formatDouble(double d) {
        if (d < 0.1) {
            return "0";
        }
        DecimalFormat df = new DecimalFormat("#.00");
        return df.format(d);
    }

    private void listener() {
        adapter.setmCheckClick(new UnCalculateAdapter.CheckClick() {
            @Override
            public void OnClick(double moeny, boolean isCheck, int su, int un) {
                if (su == adapter.getList().size()) {
                    binding.cbAll.setChecked(true);
                } else if (un <= adapter.getList().size()) {
                    binding.cbAll.setChecked(false);
                }
                if (isCheck) {
                    totalMoney += moeny;
                } else {
                    totalMoney -= moeny;
                }
                binding.btnSubmit.setEnabled(su > 0 ? true : false);
                binding.txtTotalMoney.setText(Html.fromHtml(getResources().getString(R.string.settle_money, (formatDouble(totalMoney)))));
            }
        });
    }

    protected int cursor = 0;
    int page = 1;

    public void onPageLoaded(Page<Collection> result) {
        binding.llBottom.setVisibility(result.data.total == 0 ? View.GONE : View.VISIBLE);
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
                .filter(result -> API.doCheck(getActivity(), result))
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
        API.user().myPurchase(page, Const.NO_START).compose(transform()).subscribe(result -> {
            onPageLoaded(result);
            adapter.setCbState(false);
            binding.cbAll.setChecked(false);
            totalMoney = 0;
            binding.txtTotalMoney.setText(Html.fromHtml(getResources().getString(R.string.settle_money, (formatDouble(totalMoney)))));
        });
    }

    private void submit() {
        API.user().collection(adapter.getIds()).compose(transform()).subscribe(result -> {
            RxBus.post(new SettleEvent());
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cb_all:
                isState();
                break;
            case R.id.btn_submit:
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setTitle("确认收款？");
                dialog.setPositiveButton("确定", ((dialog1, which) -> {
                    submit();
                }));
                dialog.setNegativeButton("取消", null);
                dialog.show();
                break;
        }
    }

    private void isState() {
        totalMoney = 0;
        if (binding.cbAll.isChecked()) {
            for (int i = 0; i < adapter.getList().size(); i++) {
                totalMoney += Double.parseDouble(adapter.getList().get(i).money);
            }
        }
        binding.btnSubmit.setEnabled(binding.cbAll.isChecked());
        adapter.setCbState(binding.cbAll.isChecked());
        binding.txtTotalMoney.setText(Html.fromHtml(getResources().getString(R.string.settle_money, (formatDouble(totalMoney)))));
        adapter.notifyDataSetChanged();

    }


}
