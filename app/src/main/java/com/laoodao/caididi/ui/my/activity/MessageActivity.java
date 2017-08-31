package com.laoodao.caididi.ui.my.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import com.flyco.tablayout.listener.OnTabSelectListener;
import com.flyco.tablayout.utils.UnreadMsgUtils;
import com.flyco.tablayout.widget.MsgView;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.common.util.RxBus;
import com.laoodao.caididi.databinding.ActivityMessageBinding;
import com.laoodao.caididi.event.ClearMesEvent;
import com.laoodao.caididi.event.RedDotEvent;
import com.laoodao.caididi.ui.my.fragment.DynamicFragment;
import com.laoodao.caididi.ui.my.fragment.NoticeFragment;
import com.laoodao.caididi.ui.my.fragment.ReplyFragment;

import java.util.ArrayList;

import ezy.lite.util.Device;

/**
 * Created by WORK on 2017/1/11.
 */

public class MessageActivity extends BaseActivity {

    private ActivityMessageBinding binding;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private final String[] mTitles = {
            "通知", "回复", "动态"
    };


    public static final int NOTICE = 0;
    public static final int DYNAMIC = 1;
    public static final int REPLY = 2;
    public static final int TAB_ONE = 0;
    public static final int TAB_TWO = 1;
    public static final int TAB_THRESS = 2;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_message);
        mFragments.add(new NoticeFragment());
        mFragments.add(new ReplyFragment());
        mFragments.add(new DynamicFragment());

        binding.tabs.setViewPager(binding.viewpager, mTitles, this, mFragments);
        binding.tabs.setTabWidth(Device.px2dp(Device.dm.widthPixels) / mTitles.length);
        binding.tabs.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                if (position != 0) {
                    binding.tabs.hideMsg(position);
                }
            }

            @Override
            public void onTabReselect(int position) {
            }
        });
        initData(false);
    }
    private void initData(boolean isrxbus) {
        API.user().checkMes().compose(transform()).subscribe(result -> {
            if (result.data.notice) {
                showDot(TAB_ONE);
            } else {
                binding.tabs.hideMsg(TAB_ONE);
            }
            if (result.data.answer) {
                showDot(TAB_TWO);
            } else {
                binding.tabs.hideMsg(TAB_TWO);
            }
            if (result.data.dynamic) {
                showDot(TAB_THRESS);
            } else {
                binding.tabs.hideMsg(TAB_THRESS);
            }
            if (isrxbus) {
                RxBus.post(new RedDotEvent(result.data, true));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        initData(true);
    }

    /**
     * 设置红点
     *
     * @param position
     */
    public void showDot(int position) {
        binding.tabs.showDot(position);
        MsgView msgView = binding.tabs.getMsgView(position);
        if (msgView != null) {
            UnreadMsgUtils.setSize(msgView, Device.dp2px(7.5f));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear_msg:
                clearMsg(binding.tabs.getCurrentTab() == 0 ? "notice" : "");
                return true;
        }
        return false;
    }

    private void clearMsg(String tag) {
        API.user().clearMsg(tag).compose(transform()).subscribe(r -> {
            RxBus.post(new ClearMesEvent());
            initData(false);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.clear_msg, menu);
        return true;
    }

}
