package com.laoodao.caididi.ui.my.activity;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.laoodao.caididi.R;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.common.util.RxBus;
import com.laoodao.caididi.databinding.ActivitySaleRecordBinding;
import com.laoodao.caididi.event.SaleRecorEvent;
import com.laoodao.caididi.ui.my.fragment.SuCalculateListFragment;
import com.laoodao.caididi.ui.my.fragment.UnCalculateListFragment;

import ezy.lite.util.ContextUtil;
import ezy.widget.adapter.TabsAdapter;

/**
 * Created by Administrator on 2017/2/28.
 */

public class SaleRecordActivity extends BaseActivity {

    private final Fragment mFragments[] = {new UnCalculateListFragment(), new SuCalculateListFragment()};
    private String[] mTitles = new String[]{"结算待收款", "结算已收款"};
    private ActivitySaleRecordBinding mBinding;
    private int position;

    public static void start(Context context, int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        ContextUtil.startActivity(context, SaleRecordActivity.class, bundle);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_sale_record);
        mBinding.viewpager.setAdapter(new TabsAdapter(getSupportFragmentManager(), mFragments));
        mBinding.tabs.setViewPager(mBinding.viewpager, mTitles);
        position = getIntent().getIntExtra("position", 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.post(new SaleRecorEvent(position));
    }
}
