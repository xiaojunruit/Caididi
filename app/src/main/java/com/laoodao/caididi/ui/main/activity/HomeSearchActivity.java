package com.laoodao.caididi.ui.main.activity;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.common.util.RxBus;
import com.laoodao.caididi.databinding.ActivityHomeSearchBinding;
import com.laoodao.caididi.event.HomeSearchClearEvent;
import com.laoodao.caididi.event.HomeSearchEvent;
import com.laoodao.caididi.ui.main.fragment.SearchAnswerFragment;
import com.laoodao.caididi.ui.main.fragment.SearchFarmingFragment;

import ezy.lite.util.LogUtil;
import ezy.lite.util.UI;
import ezy.widget.adapter.TabsAdapter;

/**
 * Created by WORK on 2017/1/4.
 */

public class HomeSearchActivity extends BaseActivity implements View.OnClickListener {
    private final Fragment mFragments[] = {new SearchFarmingFragment(), new SearchAnswerFragment()};
    private String[] mTitles = new String[]{"农业信息", "问答"};
    private ActivityHomeSearchBinding binding;
    public String keyword = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home_search);
        binding.setListener(this);
        binding.viewpager.setAdapter(new TabsAdapter(getSupportFragmentManager(), mFragments));
        binding.tabs.setViewPager(binding.viewpager, mTitles);
        listener();
        recommendedKeyword();
    }

    private void listener() {
        binding.txtKeyword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (TextUtils.isEmpty(keyword)) {
                    UI.showToast(HomeSearchActivity.this, "请输入搜索信息");
                    return false;
                }
                search();
                return true;
            }
            return false;
        });
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
                keyword = s.toString();
                if (s.length() <= 0) {
                    binding.llList.setVisibility(View.GONE);
                    RxBus.post(new HomeSearchClearEvent());
                }
            }
        });
        binding.tagRecommend.setOnTagItemClickListener((view, position, tag) -> {
            binding.txtKeyword.setText((String) tag);
            search();
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
    }

    private void search() {
        binding.llList.setVisibility(View.VISIBLE);
        binding.llList.post(new Runnable() {
            @Override
            public void run() {
                RxBus.post(new HomeSearchEvent(keyword));
            }
        });
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
                    break;
                }
                search();
                break;
        }
    }

    public void recommendedKeyword() {
        API.main().recommendedKeyword().compose(transform()).subscribe(result -> {
            binding.tagRecommend.setList(result.data.items);
        });

    }
}
