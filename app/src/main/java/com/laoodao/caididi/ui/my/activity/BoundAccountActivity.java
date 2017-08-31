package com.laoodao.caididi.ui.my.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.laoodao.caididi.Global;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.databinding.ActivityBoundAccountBinding;

import ezy.lite.util.ContextUtil;

/**
 * Created by XiaoGe on 2017/1/18.
 */

public class BoundAccountActivity extends BaseActivity implements View.OnClickListener{
    ActivityBoundAccountBinding mBinding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding= DataBindingUtil.setContentView(this, R.layout.activity_bound_account);
        mBinding.setListener(this);
        mBinding.setItem(Global.info());

    }

    @Override
    protected void onResume() {
        super.onResume();
        mBinding.setItem(Global.info());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txt_update_pwd:
                ContextUtil.startActivity(this,UpdatePasswordActivity.class);
                break;
            case R.id.txt_update_phone:
                ContextUtil.startActivity(this,UpdatePhoneActivity.class);
                break;
        }
    }
}
