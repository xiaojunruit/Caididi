package com.laoodao.caididi.validator;

import android.support.annotation.NonNull;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.annotation.DecimalMax;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;

public class NicknameValidator extends DefaultValidator {

    @Order(1)
    @NotEmpty(message = "请输入昵称")
    TextView txtNickname;


    public NicknameValidator(@NonNull TextView nickname) {
        txtNickname = nickname;
        init(this);
    }

}