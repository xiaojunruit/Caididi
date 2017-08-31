package com.laoodao.caididi.ui.my.activity;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;

import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.databinding.ActivityOrderDetailBinding;
import com.laoodao.caididi.ui.my.holder.OrderDetailHolder;

import ezy.lite.itemholder.adapter.BaseAdapter;
import ezy.lite.itemholder.adapter.ItemAdapter;
import ezy.lite.util.ContextUtil;

/**
 * Created by WORK on 2017/3/9.
 */

public class OrderDetailActivity extends BaseActivity {
    private ActivityOrderDetailBinding binding;
    private BaseAdapter adapter = new ItemAdapter(OrderDetailHolder.class);
    private int id;
    private int purchase;
    private String purchaseCode;

    public static void start(Context context, int id,String purchaseCode) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        bundle.putString("purchaseCode",purchaseCode);
        ContextUtil.startActivity(context, OrderDetailActivity.class, bundle);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_detail);
        initData();
        binding.llHistory.setOnClickListener(v -> {
            OperationHistoryActivity.start(this, purchase);
        });
    }

    private void initData() {
        id = getIntent().getIntExtra("id", 0);
        purchaseCode=getIntent().getStringExtra("purchaseCode");
        binding.list.setFocusable(false);
        binding.list.setHasFixedSize(true);
        binding.list.setLayoutManager(new LinearLayoutManager(this));
        binding.list.setAdapter(adapter);
        binding.loading.showLoading();
        binding.loading.setOnRetryListener(() -> {
            refresh();
        });
        refresh();
    }

    private void refresh() {
        API.user().purdetail(id,purchaseCode).compose(onErrorTransform((context, throwable) -> {
            binding.loading.showError();
        })).subscribe(result -> {
            binding.loading.showContent();
            adapter.addAll(result.data.detailList);
            binding.setItem(result.data);
            purchase = result.data.id;
        });
    }

}
