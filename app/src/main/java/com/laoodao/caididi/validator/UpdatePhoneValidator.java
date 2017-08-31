package com.laoodao.caididi.validator;

import android.support.annotation.NonNull;
import android.widget.TextView;

import com.laoodao.caididi.Const;
import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.mobsandgeeks.saripaar.annotation.Pattern;

/**
 * Created by Administrator on 2017/1/6.
 */

public class UpdatePhoneValidator extends DefaultValidator{
    @Order(1)
    @NotEmpty(message = "手机号不能为空")
    @Pattern(regex = Const.REGEX_PHONE, message = "请输入正确的手机号")
    TextView txtPhone;

    @Order(2)
    @NotEmpty(message = "请输入验证码")
    TextView txtCode;
    public UpdatePhoneValidator(@NonNull TextView phone, @NonNull TextView code) {
        txtPhone = phone;
        txtCode = code;
        init(this);
    }
}
