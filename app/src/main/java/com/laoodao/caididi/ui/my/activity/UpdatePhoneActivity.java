package com.laoodao.caididi.ui.my.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.laoodao.caididi.Global;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.common.util.countdown.CodeCountDown;
import com.laoodao.caididi.databinding.ActivityUpdatePhoneBinding;
import com.laoodao.caididi.validator.PhoneValidator;
import com.laoodao.caididi.validator.UpdatePhoneValidator;

import java.util.concurrent.TimeUnit;

import commons.validator.routines.CodeValidator;
import ezy.lite.util.UI;

/**
 * Created by Administrator on 2017/1/6.
 */

public class UpdatePhoneActivity extends BaseActivity {
    ActivityUpdatePhoneBinding mBinding;
    PhoneValidator mPhoneValidator;
    UpdatePhoneValidator mUpdatePhoneValidator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_update_phone);

        mPhoneValidator = new PhoneValidator(mBinding.txtPhone).succeeded(PhoneValidator.UNUSED).clicked(mBinding.btnSendCode);

        mUpdatePhoneValidator = new UpdatePhoneValidator(mBinding.txtPhone, mBinding.txtCode).succeeded(() -> {
            final String phone = UI.toString(mBinding.txtPhone);
            final String code = UI.toString(mBinding.txtCode);
            API.user().updatePassword(phone, code).compose(transform()).subscribe(result -> {
                UI.showToast(this, "手机号码修改成功");
                if (result.data != null) {
                    Global.session().getInfo().mobile = result.data.toString();
                }
                finish();
            });
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                mUpdatePhoneValidator.validate();
                return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save, menu);

        return true;
    }
}
