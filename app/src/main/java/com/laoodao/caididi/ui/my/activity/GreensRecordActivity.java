package com.laoodao.caididi.ui.my.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.laoodao.caididi.R;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.common.util.RxBus;
import com.laoodao.caididi.databinding.ActivityGreensRecordBinding;
import com.laoodao.caididi.event.SaleRecorEvent;
import com.laoodao.caididi.ui.my.fragment.ForTheFragment;
import com.laoodao.caididi.ui.my.fragment.GreensAllFragment;
import com.laoodao.caididi.ui.my.fragment.NoConfirmFragment;
import com.laoodao.caididi.ui.my.fragment.SuGreensFragment;

import java.util.ArrayList;

import ezy.widget.adapter.TabsAdapter;

/**
 * Created by WORK on 2017/3/15.
 */

public class GreensRecordActivity extends BaseActivity {
    private ArrayList<Fragment> fragments;
    private String[] mTitles = {"全部" ,"待确认", "待结算", "待收款","已完成"};
    private ActivityGreensRecordBinding mBinding;
    private int currentPage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_greens_record);
        initData();
        mBinding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 3) {
                    SaleRecordActivity.start(GreensRecordActivity.this, currentPage);
                    return;
                }
                currentPage = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        RxBus.ofType(SaleRecorEvent.class).compose(bindToLifecycle()).subscribe(event -> {
            mBinding.viewpager.setCurrentItem(currentPage);
        });
    }

    private void initData() {
        fragments = new ArrayList<>();
        fragments.add(new GreensAllFragment());
        fragments.add(new NoConfirmFragment());
        fragments.add(new ForTheFragment());
        fragments.add(new Fragment());
        fragments.add(new SuGreensFragment());

        mBinding.tabs.setViewPager(mBinding.viewpager, mTitles, this, fragments);
    }
}
