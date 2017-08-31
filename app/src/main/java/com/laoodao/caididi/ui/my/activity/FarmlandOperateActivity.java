package com.laoodao.caididi.ui.my.activity;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;

import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.common.util.RxBus;
import com.laoodao.caididi.databinding.ActivityFarmlandBinding;
import com.laoodao.caididi.event.CloseOperation;
import com.laoodao.caididi.ui.my.holder.FarmlandOperateHolder;
import com.laoodao.caididi.ui.widget.SupportGridItemDecoration;

import ezy.lite.itemholder.adapter.ItemAdapter;
import ezy.lite.util.ContextUtil;

/**
 * Created by WORK on 2017/2/8.
 */

public class FarmlandOperateActivity extends BaseActivity {


    private ActivityFarmlandBinding binding;
    private ItemAdapter mAdapter = new ItemAdapter(FarmlandOperateHolder.class);
    public int farmlandId;

    public static void start(Context context, int farmlandId) {
        Bundle bundle = new Bundle();
        bundle.putInt("farmlandId", farmlandId);
        ContextUtil.startActivity(context, FarmlandOperateActivity.class, bundle);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_farmland);
        farmlandId = getIntent().getIntExtra("farmlandId", 0);
        binding.list.setHasFixedSize(true);
        binding.list.setNestedScrollingEnabled(false);
        binding.list.addItemDecoration(new SupportGridItemDecoration(this));
        binding.list.setLayoutManager(new GridLayoutManager(this, 3) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        binding.list.setAdapter(mAdapter);

        API.user().farmlandOperate().compose(transform()).subscribe(result -> {
            mAdapter.addAll(result.data, true);
        });

        RxBus.ofType(CloseOperation.class).compose(bindToLifecycle()).subscribe(event -> {
            finish();
        });

    }
}
