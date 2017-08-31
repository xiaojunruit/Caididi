package com.laoodao.caididi.validator;

import android.support.annotation.NonNull;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.mobsandgeeks.saripaar.annotation.Password;

public class ResetPasswordValidator extends DefaultValidator {

    @Password(message = "第一次输入密码格式有误")
    @Order(1)
    TextView txtPasswordNew;

    @ConfirmPassword(message = "两次输入密码不一致")
    @Order(2)
    TextView txtPasswordConfirm;

    public ResetPasswordValidator(@NonNull TextView txtPasswordNew, @NonNull TextView txtPasswordConfirm) {
        this.txtPasswordNew = txtPasswordNew;
        this.txtPasswordConfirm = txtPasswordConfirm;
        init(this);
    }


}