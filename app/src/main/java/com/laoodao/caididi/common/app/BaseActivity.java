package com.laoodao.caididi.common.app;


import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.api.Response;
import com.tencent.stat.StatService;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import rx.Observable;

public class BaseActivity extends RxAppCompatActivity {

    private TextView mToolbarTitle;
    private Toolbar mToolbar;
    private ActionBar mActionBar;

    public <T extends Response> Observable.Transformer<T, T> transform() {
        return new API.Transformer<T>(this, ActivityEvent.DESTROY).check();
    }

    public <T extends Response> Observable.Transformer<T, T> onErrorTransform(API.OnErrorListener errorListener) {
        return new API.Transformer<T>(this).check().error(errorListener);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setupToolbar();
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary), 0);
    }

    public boolean isShowFloat() {

        return true;

    }

    public void setupToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar == null) {
            return;
        }
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(false);
        mActionBar.setHomeButtonEnabled(false);
        mToolbar.setContentInsetsAbsolute(0, 0);
        View btnBack = (View) mToolbar.findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                finish();
            });
        }
    }

    public Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    public void setSupportActionBar(Toolbar toolbar) {
        super.setSupportActionBar(toolbar);

        if (toolbar != null) {
            mToolbarTitle = (TextView) toolbar.findViewById(R.id.txt_title);
            if (mToolbarTitle != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
        }
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        if (mToolbarTitle != null) {
            mToolbarTitle.setText(title);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);
    }


}