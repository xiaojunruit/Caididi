package com.laoodao.caididi.ui.my.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.PasswordTransformationMethod;

import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.databinding.ActivityUpdatePasswordBinding;
import com.laoodao.caididi.ui.dialog.FollowCropsDialog;
import com.laoodao.caididi.ui.dialog.UpdatePasswordDialog;
import com.laoodao.caididi.validator.ChangePwdValidator;

import ezy.lite.util.UI;

/**
 * Created by XiaoGe on 2017/1/18.
 */

public class UpdatePasswordActivity extends BaseActivity{
    ActivityUpdatePasswordBinding mBinding;
    ChangePwdValidator mValidator;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding= DataBindingUtil.setContentView(this, R.layout.activity_update_password);
        mBinding.txtOldPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
        mBinding.txtNewPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
        mValidator = new ChangePwdValidator(mBinding.txtOldPwd, mBinding.txtNewPwd).clicked(mBinding.btnSubmit).succeeded(() -> {
            String beforePwd = UI.toString(mBinding.txtOldPwd);
            String newPwd = UI.toString(mBinding.txtNewPwd);
            String confirmPwd=UI.toString(mBinding.txtConfirmPwd);
            if (!newPwd.equals(confirmPwd)){
                UI.showToast(this,"两次输入密码不一致");
                return;
            }
            API.user().updatePassword(beforePwd,newPwd, confirmPwd).compose(new API.Transformer<>(this)).subscribe(result -> {
                if (result.code!=0){
                    UI.showToast(this,result.message);
                    return;
                }
                UpdatePasswordDialog.passwordDialogCustomAttr(this);
            });
        });
    }
}
