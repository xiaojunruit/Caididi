package com.laoodao.caididi.validator;

import android.support.annotation.NonNull;
import android.widget.TextView;

import com.mobsandgeeks.saripaar.annotation.Length;
import com.mobsandgeeks.saripaar.annotation.Max;
import com.mobsandgeeks.saripaar.annotation.Min;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Order;

public class ChangePwdValidator extends DefaultValidator {

    @Order(1)
    @NotEmpty(sequence = 1, message = "原密码不能为空")
    private TextView txtBeforePwd;

    @Order(2)
    @NotEmpty(sequence = 1, message = "新密码不能为空")
    @Length(min = 6,max = 12,message = "请输入6-12位数的新密码")
    private TextView txtNewPwd;



    public ChangePwdValidator(@NonNull TextView beforePwd, @NonNull TextView newPwd) {
        txtBeforePwd = beforePwd;
        txtNewPwd = newPwd;
        init(this);
    }
}