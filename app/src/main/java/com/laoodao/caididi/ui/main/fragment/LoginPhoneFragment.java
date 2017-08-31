package com.laoodao.caididi.ui.main.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.bumptech.glide.Glide;
import com.laoodao.caididi.BuildConfig;
import com.laoodao.caididi.Const;
import com.laoodao.caididi.Global;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.BindingFragment;
import com.laoodao.caididi.common.util.ProgressOperator;
import com.laoodao.caididi.common.util.RxBus;
import com.laoodao.caididi.databinding.LayoutLoginBinding;
import com.laoodao.caididi.event.Login3rdResultEvent;
import com.laoodao.caididi.retrofit.user.User;
import com.laoodao.caididi.ui.main.user.GetbackActivity;
import com.laoodao.caididi.ui.main.user.RegisterActivity;
import com.laoodao.caididi.ui.my.activity.JointLoginActivity;
import com.laoodao.caididi.validator.LoginValidator;
import com.laoodao.caididi.wxapi.QQSdk;
import com.laoodao.caididi.wxapi.WechatSdk;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.trello.rxlifecycle.android.FragmentEvent;

import ezy.lite.util.ContextUtil;
import ezy.lite.util.LogUtil;
import ezy.lite.util.UI;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class LoginPhoneFragment extends BindingFragment<LayoutLoginBinding> implements View.OnClickListener {

    private LoginValidator mValidator;


    @Override
    public int getLayoutId() {
        return R.layout.layout_login;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.setOnClick(this);
        mValidator = new LoginValidator(binding.txtUsername, binding.txtPassword).clicked(binding.btnLogin).succeeded(() -> {
            String username = UI.toString(binding.txtUsername);
            String password = UI.toString(binding.txtPassword);
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(binding.getRoot().getWindowToken(), 0);
            API.user().login(username, password, Global.getPushId()).compose(transform()).subscribe(result -> {
                Global.info().attentionCrops = result.data.attentionCrops;
                Global.info().myAttention = result.data.myAttention;
                Global.session().login(password, result.data);
                getActivity().finish();
            });
        });
        RxBus.ofType(Login3rdResultEvent.class).compose(bindUntilEvent(FragmentEvent.DESTROY)).subscribe(event -> {
            LogUtil.e("requestCode = " + event.requestCode + ", resultCode = " + event.resultCode + " =====>" + event.data.getExtras().toString());
            if (mWechatSdk != null) {
                mWechatSdk.onActivityResult(event.requestCode, event.resultCode, event.data);
            }
            if (mQQSdk != null) {
                mQQSdk.onActivityResult(event.requestCode, event.resultCode, event.data);
            }
        });
    }

    QQSdk mQQSdk;
    WechatSdk mWechatSdk;
    private QQToken qqToken;
    private String mark;

    @Override
    public void onClick(View v) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        switch (v.getId()) {
            case R.id.btn_phone_register:
                ContextUtil.startActivity(getActivity(), RegisterActivity.class);
                break;

            case R.id.btn_weixin:
                if (UI.isFastClick(1000)) {
                    return;
                }
                if (mWechatSdk == null) {
                    mWechatSdk = new WechatSdk(getActivity(), BuildConfig.APP_ID_WECHAT);
                }
                mWechatSdk.authorize(info -> {
                    mark = Const.WX;
                    LogUtil.e("wechat ===> token = " + info.code + ", state = " + info.state);
                    LogUtil.e("wechat ===> token = " + info.code.substring(0, 20));
                    login3rd("wechat", "", info.code, "微信登陆中...");
                });
                break;
            case R.id.btn_qq:
                if (UI.isFastClick(1000)) {
                    return;
                }
                if (mQQSdk == null) {
                    mQQSdk = new QQSdk(getActivity(), BuildConfig.APP_ID_QQ);
                }
                mQQSdk.authorize(info -> {
                    qqToken = info;
                    mark = Const.QQ;
                    login3rd("qq", info.getOpenId(), info.getAccessToken(), "QQ登陆中...");
                });
                break;
            case R.id.btn_forget:
                ContextUtil.startActivity(getContext(), GetbackActivity.class);
                break;
        }
    }


    void login3rd(String platform, String openId, String accessToken, String tips) {
        LogUtil.e(platform + " ===> token = " + accessToken + ", id = " + openId + "clientId = " + Global.getPushId());

        API.user()
                .login3rd(platform, openId, accessToken, Global.getPushId())
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .lift(new ProgressOperator<>(getActivity(), tips))
                .subscribe(result -> {
                    if (result == null) {
                        return;
                    }
                    if (result.code == 4001) {
                        JointLoginActivity.start(getContext(), mark, platform, openId, accessToken, result.data.avatar, result.data.nickname);
                        getActivity().finish();
                        return;
                    }
                    if (result.code == 0) {
                        Global.session().login(accessToken.substring(0, 20), result.data);
//                    mQQSdk.setUserInfo(qqToken);
                        getActivity().finish();
                    }

                });
    }
}
