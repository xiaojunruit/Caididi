package com.laoodao.caididi.ui.my.activity;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.text.InputType;
import android.view.View;

import com.jaeger.library.StatusBarUtil;
import com.laoodao.caididi.Const;
import com.laoodao.caididi.Global;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.common.util.RxBus;
import com.laoodao.caididi.databinding.ActivityBindLoginBinding;
import com.laoodao.caididi.event.JointSuccessEvent;
import com.laoodao.caididi.ui.user.RegisterAcitivity;
import com.laoodao.caididi.validator.BindLoginValidator;

import ezy.lite.util.ContextUtil;
import ezy.lite.util.LogUtil;
import ezy.lite.util.UI;

/**
 * Created by WORK on 2017/2/25.
 */

public class BindLoginActivity extends BaseActivity implements View.OnClickListener {
    private ActivityBindLoginBinding binding;
    private String platform;
    private String openId;
    private String accessToken;
    BindLoginValidator mBindLoginValidator;
    private String mark;
    public static void start(Context context, String platform, String openId, String accessToken,String mark) {
        Bundle bundle = new Bundle();
        bundle.putString("platform", platform);
        bundle.putString("openId", openId);
        bundle.putString("accessToken", accessToken);
        bundle.putString("mark",mark);
        ContextUtil.startActivity(context, BindLoginActivity.class, bundle);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bind_login);
        StatusBarUtil.setColor(this, ResourcesCompat.getColor(getResources(), R.color.textBlack, getTheme()), 0);
        binding.setListener(this);
        platform = getIntent().getStringExtra("platform");
        openId = getIntent().getStringExtra("openId");
        accessToken = getIntent().getStringExtra("accessToken");
        mark=getIntent().getStringExtra("mark");
        binding.setMark(mark);
        refresh();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.checkbox_pwd:
                binding.etPassword.setInputType(binding.checkboxPwd.isChecked() ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD : InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                break;
        }
    }

    private void refresh() {
        mBindLoginValidator = new BindLoginValidator(binding.etMobile, binding.etPassword).clicked(binding.btnBind).succeeded(() -> {
            final String phone = UI.toString(binding.etMobile);
            final String password = UI.toString(binding.etPassword);
            API.user()
                    .login3rd(platform, openId, accessToken, Global.getPushId(), Const.OLD, phone, "", password)
                    .compose(transform())
                    .subscribe(result -> {
                        Global.session().login(accessToken.substring(0, 20), result.data);
                        RxBus.post(new JointSuccessEvent());
                        finish();
                    });
        });

    }
}
