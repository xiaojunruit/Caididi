package com.laoodao.caididi.ui.my.activity;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;

import com.bumptech.glide.Glide;
import com.jaeger.library.StatusBarUtil;
import com.laoodao.caididi.BuildConfig;
import com.laoodao.caididi.Global;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.common.util.RxBus;
import com.laoodao.caididi.databinding.ActivityJointLoginBinding;
import com.laoodao.caididi.event.JointSuccessEvent;
import com.laoodao.caididi.ui.user.RegisterAcitivity;

import org.json.JSONException;
import org.json.JSONObject;

import javax.microedition.khronos.opengles.GL;

import ezy.lite.util.ContextUtil;
import ezy.lite.util.LogUtil;

/**
 * Created by WORK on 2017/2/25.
 */

public class JointLoginActivity extends BaseActivity implements View.OnClickListener {
    private ActivityJointLoginBinding binding;
    private String mark;
    private String platform;
    private String openId;
    private String accessToken;
    private String avatar;
    private String nickname;

    public static void start(Context context, String mark, String platform, String openId, String accessToken, String avatar, String nickname) {
        Bundle bundle = new Bundle();
        bundle.putString("mark", mark);
        bundle.putString("platform", platform);
        bundle.putString("openId", openId);
        bundle.putString("accessToken", accessToken);
        bundle.putString("avatar", avatar);
        bundle.putString("nickname", nickname);
        ContextUtil.startActivity(context, JointLoginActivity.class, bundle);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_joint_login);
        binding.setListener(this);
        StatusBarUtil.setColor(this, ResourcesCompat.getColor(getResources(), R.color.textBlack, getTheme()), 0);
        mark = getIntent().getStringExtra("mark");
        platform = getIntent().getStringExtra("platform");
        openId = getIntent().getStringExtra("openId");
        accessToken = getIntent().getStringExtra("accessToken");
        avatar = getIntent().getStringExtra("avatar");
        nickname = getIntent().getStringExtra("nickname");
        binding.setMark(mark);
        binding.setAvatar(avatar);
        binding.setNickname(nickname);
        RxBus.ofType(JointSuccessEvent.class).compose(bindToLifecycle()).subscribe(event -> {
            finish();
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                RegisterAcitivity.start(this, platform, openId, accessToken,mark);
                break;
            case R.id.btn_joint:
                BindLoginActivity.start(this, platform, openId, accessToken,mark);
                break;
        }
    }
}
