package com.laoodao.caididi.validator;

import android.support.annotation.NonNull;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.mobsandgeeks.saripaar.annotation.Password;

public class LoginValidator extends DefaultValidator {

    @Order(1)
    @NotEmpty(sequence = 1, message = "请输入手机号码/邮箱/用户名")
    TextView txtUsername;

    @NotEmpty(message = "请输入密码")
    @Password(scheme = Password.Scheme.ANY, message = "密码输入格式错误")
    @Order(2)
    TextView txtPassword;

    public LoginValidator(@NonNull TextView username, @NonNull TextView password) {
        txtUsername = username;
        txtPassword = password;
        init(this);
    }
}