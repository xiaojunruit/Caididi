package com.laoodao.caididi.ui.my.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;

import com.bumptech.glide.Glide;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.laoodao.caididi.BuildConfig;
import com.laoodao.caididi.Const;
import com.laoodao.caididi.Global;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.common.app.BindingActivity;
import com.laoodao.caididi.common.util.RxBus;
import com.laoodao.caididi.databinding.ActivityBoundAccountBinding;
import com.laoodao.caididi.event.Login3rdResultEvent;
import com.laoodao.caididi.ui.wenda.activity.FollowCropActivity;
import com.laoodao.caididi.wxapi.QQSdk;
import com.laoodao.caididi.wxapi.WechatSdk;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.android.FragmentEvent;

import javax.microedition.khronos.opengles.GL;

import ezy.lite.util.ContextUtil;
import ezy.lite.util.LogUtil;
import ezy.lite.util.UI;

/**
 * Created by XiaoGe on 2017/1/18.
 */

public class BindAccountActivity extends BindingActivity<ActivityBoundAccountBinding> implements View.OnClickListener {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding.setListener(this);
        binding.btnWx.setValue(!Global.info().isWxBind ? "未绑定" : Global.info().wxBindName);
        binding.btnQq.setValue(!Global.info().isQqBind ? "未绑定" : Global.info().qqBindName);
        RxBus.ofType(Login3rdResultEvent.class).compose(bindUntilEvent(ActivityEvent.DESTROY)).subscribe(event ->{
            LogUtil.e("requestCode = " + event.requestCode + ", resultCode = " + event.resultCode + " =====>" + event.data.getExtras().toString());
            if (mWechatSdk != null) {
                mWechatSdk.onActivityResult(event.requestCode, event.resultCode, event.data);
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_bound_account;
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.setItem(Global.info());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_update_pwd:
                ContextUtil.startActivity(this, UpdatePasswordActivity.class);
                break;
            case R.id.txt_update_phone:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("是否更换绑定的手机号?");
                builder.setPositiveButton("确定",((dialog1, which) -> {
                    ContextUtil.startActivity(view.getContext(), UpdatePhoneActivity.class);
                }));
                builder.setNegativeButton("取消", null);
                builder.show();
                break;
            case R.id.btn_wx:
                bindingLogin2(false, Const.WECHAT);
                break;
            case R.id.btn_qq:
                bindingLogin2(true, Const.QQ_SMALL);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null || data.getExtras() == null) {
            return;
        }

        if (mQQSdk != null) {
            mQQSdk.onActivityResult(requestCode, resultCode, data);
        }
    }


    QQSdk mQQSdk;
    WechatSdk mWechatSdk;

    private void bindingLogin2(boolean isQQ, String platForm){
        String bind = "";
        if (isQQ) {
            bind = Global.info().isQqBind ? "解绑" : "绑定";
        } else {
            bind = Global.info().isWxBind ? "解绑" : "绑定";
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("是否" + bind + "" + (isQQ ? "QQ" : "微信") + "？");
        builder.setPositiveButton("确定",((dialog1, which) -> {
            if (Const.QQ_SMALL.equals(platForm)) {
                qqAccredit();
            } else {
                wechatAccredit();
            }
        }));
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    //qq授权
    private void qqAccredit() {
        //解绑
        if (Global.info().isQqBind) {
            thirdRefresh(Const.QQ_SMALL, "", "", Const.UN_BIND);
            return;
        }
        //绑定
        if (UI.isFastClick(1000)) {
            return;
        }
        if (mQQSdk == null) {
            mQQSdk = new QQSdk(this, BuildConfig.APP_ID_QQ);
        }
        mQQSdk.authorize(info -> {
            LogUtil.e("qq ===> token = " + info.getAccessToken() + ", id = " + info.getOpenId());
            thirdRefresh(Const.QQ_SMALL, info.getOpenId(), info.getAccessToken(), Const.SU_BIND);
        });
    }

    //微信授权
    private void wechatAccredit() {
        //解绑
        if (Global.info().isWxBind) {
            thirdRefresh(Const.WECHAT, "", "", Const.UN_BIND);
            return;
        }
        //绑定
        if (UI.isFastClick(1000)) {
            return;
        }
        if (mWechatSdk == null) {
            mWechatSdk = new WechatSdk(this, BuildConfig.APP_ID_WECHAT);
        }
        mWechatSdk.authorize(info -> {
            LogUtil.e("wechat ===> token = " + info.code + ", state = " + info.state);
            thirdRefresh(Const.WECHAT, "", info.code, Const.SU_BIND);
        });
    }

    //第三方授权绑定接口
    private void thirdRefresh(String platform, String openid, String token, int type) {
        API.user().thirdBind(platform, openid, token, type).compose(transform()).subscribe(result -> {
            if (Const.QQ_SMALL.equals(platform)){
                Global.info().isQqBind = !Global.info().isQqBind;
                Global.info().qqBindName=result.data.get("nickname");
                binding.btnQq.setValue(Global.info().isQqBind ? Global.info().qqBindName : "未绑定");
            }else{
                Global.info().isWxBind=!Global.info().isWxBind;
                Global.info().wxBindName=result.data.get("nickname");
                binding.btnWx.setValue(Global.info().isWxBind ? Global.info().wxBindName : "未绑定");
            }
        });
    }


}
