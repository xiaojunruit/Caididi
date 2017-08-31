package com.laoodao.caididi.ui.main.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.laoodao.caididi.BuildConfig;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.app.BindingFragment;
import com.laoodao.caididi.common.util.RxBus;
import com.laoodao.caididi.databinding.LayoutLoginWxBinding;
import com.laoodao.caididi.event.Login3rdResultEvent;
import com.laoodao.caididi.ui.main.dialog.LoginSelectDialog;
import com.laoodao.caididi.ui.main.user.LoginPhoneActivity;
import com.laoodao.caididi.wxapi.QQSdk;
import com.laoodao.caididi.wxapi.WechatSdk;

import ezy.lite.util.ContextUtil;
import ezy.lite.util.LogUtil;
import ezy.lite.util.UI;


public class LoginWXFragment extends BindingFragment<LayoutLoginWxBinding> implements View.OnClickListener {


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.setListener(this);
        RxBus.ofType(Login3rdResultEvent.class).compose(bindToLifecycle()).subscribe(event -> {
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

    @Override
    public void onClick(View v) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        switch (v.getId()) {
            case R.id.btn_phone:
                ContextUtil.startActivity(getContext(), LoginPhoneActivity.class);
                break;
            case R.id.btn_other:
                new LoginSelectDialog(getContext(), this).show();
                break;
            case R.id.btn_weixin:
                if (UI.isFastClick(1000)) {
                    return;
                }
                if (mWechatSdk == null) {
                    mWechatSdk = new WechatSdk(getActivity(), BuildConfig.APP_ID_WECHAT);
                }
                mWechatSdk.authorize(info -> {
                    LogUtil.e("wechat ===> token = " + info.code + ", state = " + info.state);
                    LogUtil.e("wechat ===> token = " + info.code.substring(0, 20));
                    login3rd("Wechat", "", info.code, "微信登陆中...");
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
                    LogUtil.e("qq ===> token = " + info.getAccessToken() + ", id = " + info.getOpenId());
                    LogUtil.e("qq ===> token = " + info.getAccessToken().substring(0, 20));
                    login3rd("QQ", info.getOpenId(), info.getAccessToken(), "QQ登陆中...");
                });
                break;

        }
    }

    void login3rd(String platform, String openId, String accessToken, String tips) {

     /*   API.main().login3rd(platform, openId, accessToken, Global.getPushId(), Global.o2o.cityId()).compose(checkOn()).lift(new ProgressOperator<>(getActivity(), tips)).subscribe(result -> {

            UI.showToast(getActivity(), "登录成功");
            Global.login(accessToken.substring(0, 20), result.data);

            if (getActivity() instanceof LoginActivity) {
                getActivity().finish();
                if (result.data.isNew) {
                    Route.go(getContext(), Route.MAIN_MY);
                }
            }
        });*/
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_login_wx;
    }
}
