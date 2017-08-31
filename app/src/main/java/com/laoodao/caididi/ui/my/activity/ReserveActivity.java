package com.laoodao.caididi.ui.my.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.laoodao.caididi.R;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.databinding.ActivityReserveBinding;
import com.laoodao.caididi.ui.my.fragment.ReserveFragment;
import com.laoodao.caididi.ui.my.fragment.UnReserveFragment;
import ezy.widget.adapter.TabsAdapter;

/**
 * Created by WORK on 2017/3/7.
 */

public class ReserveActivity extends BaseActivity{
    private final Fragment mFragments[] = {new UnReserveFragment(), new ReserveFragment()};
    private String[] mTitles = new String[]{"未完成", "已完成"};
    ActivityReserveBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding= DataBindingUtil.setContentView(this, R.layout.activity_reserve);
        mBinding.viewpager.setAdapter(new TabsAdapter(getSupportFragmentManager(), mFragments));
        mBinding.tabs.setViewPager(mBinding.viewpager, mTitles);
    }
}
