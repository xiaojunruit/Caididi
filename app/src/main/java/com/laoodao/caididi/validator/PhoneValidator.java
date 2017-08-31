package com.laoodao.caididi.validator;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.laoodao.caididi.Const;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.util.countdown.CodeCountDown;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;
import com.mobsandgeeks.saripaar.annotation.Pattern;

import ezy.lite.util.LogUtil;
import ezy.lite.util.UI;

public class PhoneValidator extends DefaultValidator {

    public static String EXISTED = "existed";
    public static String UNUSED = "unused";

    @Order(1)
    @NotEmpty(message = "手机号不能为空")
    @Pattern(regex = Const.REGEX_PHONE, message = "请输入正确的手机号")
    TextView txtPhone;

    TextView btnClicked;

    public PhoneValidator(@NonNull TextView phone) {
        txtPhone = phone;
        init(this);
    }

    @Override
    public <T extends DefaultValidator> T clicked(View view) {
        btnClicked = (TextView) view;
        return super.clicked(view);
    }

    public DefaultValidator succeeded(String rule) {
        return super.succeeded(() -> {
            final String phone = UI.toString(txtPhone);
            API.user().sendCode(phone, rule).compose(new API.Transformer(txtPhone).check()).subscribe(result -> {
                new CodeCountDown(btnClicked).start();
                /*UI.showToast(btnClicked.getContext(), "验证码已发送，请注意查收");*/
            });
        });
    }

    public DefaultValidator succeeded() {
        return succeeded("none");
    }

}