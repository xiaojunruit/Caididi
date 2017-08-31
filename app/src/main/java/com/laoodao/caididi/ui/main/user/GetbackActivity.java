package com.laoodao.caididi.ui.main.user;

import android.databinding.DataBindingUtil;
import android.os.Bundle;


import com.laoodao.caididi.Global;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.databinding.ActivityAccountGetbackBinding;
import com.laoodao.caididi.validator.CodeCountDown;
import com.laoodao.caididi.validator.PhoneValidator;
import com.laoodao.caididi.validator.ResetPasswordValidator;
import com.laoodao.caididi.validator.UpdatePhoneValidator;

import ezy.lite.util.UI;

public class GetbackActivity extends BaseActivity {

    private ActivityAccountGetbackBinding binding;

    private String mToken = "";

    PhoneValidator mPhoneValidator;
    UpdatePhoneValidator mCodeValidator;
    ResetPasswordValidator mResetValidator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_account_getback);
        binding.setIsStepReset(false);

        mPhoneValidator = new PhoneValidator(binding.txtPhone).succeeded(PhoneValidator.EXISTED).clicked(binding.btnSendCode);

        mCodeValidator = new UpdatePhoneValidator(binding.txtPhone, binding.txtCode).clicked(binding.btnNext).succeeded(() -> {
            final String phone = UI.toString(binding.txtPhone);
            final String code = UI.toString(binding.txtCode);
            API.user().getbackAuth(phone, code).compose(transform()).subscribe(result -> {
                mToken = result.data.get("token");
                binding.setIsStepReset(true);
            });
        });

        mResetValidator = new ResetPasswordValidator(binding.txtPassword, binding.txtPasswordConfirm).clicked(binding.btnSubmit).succeeded(() -> {
            final String password = UI.toString(binding.txtPassword);
            final String confirm = UI.toString(binding.txtPasswordConfirm);

            API.user().getbackReset(mToken, password, confirm, Global.getPushId()).compose(transform()).subscribe(result -> {
                /*UI.showToast(this, "重置密码成功");*/
                Global.session().login(password, result.data);

                finish();
            });
        });


    }

}
