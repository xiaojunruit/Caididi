package com.laoodao.caididi.ui.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.common.util.KeyboardUtil;
import com.laoodao.caididi.common.util.RxBus;
import com.laoodao.caididi.databinding.ActivityBuySearchBinding;
import com.laoodao.caididi.event.BuyEvent;
import com.laoodao.caididi.event.VegetableEvent;

import java.util.ArrayList;

/**
 * Created by XiaoGe on 2017/2/13.
 */

public class VegetableSearchActivity extends BaseActivity implements TextView.OnEditorActionListener, View.OnClickListener, TextWatcher {
    private ActivityBuySearchBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_buy_search);
        binding.setListener(this);
        binding.txtKeyword.addTextChangedListener(this);
        binding.tags.setOnTagItemClickListener((view, position, tag) -> {
            search((String) tag);
        });
        binding.llRecommend.setVisibility(View.GONE);
        /**
         * 触摸其他地方，隐藏软键盘
         */
        binding.getRoot().setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (binding.txtKeyword.isFocused()) {
                    Rect outRect = new Rect();
                    binding.txtKeyword.getGlobalVisibleRect(outRect);
                    if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                        binding.txtKeyword.clearFocus();
                        KeyboardUtil.hideSoftKeyboard(binding.txtKeyword);
                    }
                }
            }
            return false;
        });
        recommendedKeyword();
    }

    /**
     * 历史记录
     */
    public void recommendedKeyword() {
        API.main().vhistory().compose(transform()).subscribe(result -> {
            binding.tags.setList(result.data);
        });
    }


    private void search(String keyword) {
        if (TextUtils.isEmpty(keyword)) {
            return;
        }
        //保存搜索
        API.main().vkw(keyword).compose(new API.Transformer<>(this)).subscribe(result -> {
        });
        binding.txtKeyword.setText("");
        RxBus.post(new VegetableEvent(keyword));
        finish();
    }

    /**
     * 软键盘搜索按钮，点击隐藏
     *
     * @param v
     * @param actionId
     * @param event
     * @return
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            KeyboardUtil.hideSoftKeyboard(binding.txtKeyword);
            search(binding.txtKeyword.getText().toString());
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_search:
                search(binding.txtKeyword.getText().toString());
                break;
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_clean_history:
                clearHistory();
                break;
        }
    }

    private void clearHistory() {
        API.main().vdel().compose(new API.Transformer<>(this)).subscribe(result -> {
            binding.tags.setList(new ArrayList());
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        binding.btnDelete.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
    }
}
