package com.laoodao.caididi.validator;

import android.support.annotation.NonNull;
import android.widget.TextView;

import com.laoodao.caididi.Const;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.mobsandgeeks.saripaar.annotation.Pattern;

public class BindLoginValidator extends DefaultValidator {

    @Order(1)
    @NotEmpty(message = "手机号不能为空")
    @Pattern(regex = Const.REGEX_PHONE, message = "请输入正确的手机号")
    TextView txtPhone;

    @Order(2)
    @Length(trim = true, min = 6, max = 32, message = "请输入6位以上的密码")
    TextView txtPassword;
    public BindLoginValidator(@NonNull TextView phone, @NonNull TextView password) {
        txtPhone = phone;
        txtPassword = password;
        init(this);
    }
}