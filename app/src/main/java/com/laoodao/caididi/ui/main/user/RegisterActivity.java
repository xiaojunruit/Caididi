package com.laoodao.caididi.ui.main.user;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;

import com.laoodao.caididi.Global;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.databinding.ActivityAccountRegisterBinding;
import com.laoodao.caididi.ui.wenda.activity.FollowCropActivity;
import com.laoodao.caididi.validator.CodeCountDown;
import com.laoodao.caididi.validator.PhoneValidator;
import com.laoodao.caididi.validator.RegisterValidator;

import ezy.lite.util.UI;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    ActivityAccountRegisterBinding binding;

    PhoneValidator mPhoneValidator;
    RegisterValidator mRegisterValidator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_account_register);
        mPhoneValidator = new PhoneValidator(binding.txtPhone).succeeded(PhoneValidator.UNUSED).clicked(binding.btnSendCode);
        mRegisterValidator = new RegisterValidator(binding.txtPhone, binding.txtCode, binding.txtPassword).clicked(binding.btnSubmit).succeeded(() -> {
            final String phone = UI.toString(binding.txtPhone);
            final String code = UI.toString(binding.txtCode);
            final String password = UI.toString(binding.txtPassword);

            API.user().register(phone, code, password, Global.getPushId()).compose(transform()).subscribe(result -> {
                UI.showToast(this, "注册成功");
                Global.session().login(password, result.data);
                if ((result.data.plantsList == null || result.data.plantsList.size() == 0) && Global.session().isLoggedIn()) {
                    FollowCropActivity.start(this, null);
                }
                finish();
            });
        });
    }

    @Override
    public boolean isShowFloat() {
        return false;
    }

    @Override
    public void onClick(View v) {
    }
}
