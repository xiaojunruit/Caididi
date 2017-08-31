package com.laoodao.caididi.ui.my.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.laoodao.caididi.BuildConfig;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.databinding.ActivityAboutBinding;

/**
 * Created by XiaoGe on 2017/1/18.
 */

public class AboutActivity extends BaseActivity {
    ActivityAboutBinding mBinding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding= DataBindingUtil.setContentView(this, R.layout.activity_about);
        mBinding.txtVersion.setText("当前版本 v" + BuildConfig.VERSION_NAME);
    }
}
