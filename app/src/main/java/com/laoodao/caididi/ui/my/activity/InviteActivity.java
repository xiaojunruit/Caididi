package com.laoodao.caididi.ui.my.activity;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.laoodao.caididi.BuildConfig;
import com.laoodao.caididi.Global;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.databinding.ActivityInviteBinding;
import com.laoodao.caididi.retrofit.main.ShareInfo;
import com.laoodao.caididi.wxapi.QQSdk;
import com.laoodao.caididi.wxapi.WechatSdk;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;

import ezy.lite.util.ContextUtil;
import ezy.lite.util.LogUtil;

/**
 * Created by WORK on 2017/3/28.
 */

public class InviteActivity extends BaseActivity implements View.OnClickListener {
    private ActivityInviteBinding mBinding;
    private ShareInfo info;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_invite);
        mBinding.setListener(this);
        refresh();
    }

    private void refresh() {
        API.user().invite().compose(transform()).subscribe(result -> {
            mBinding.setItem(result.data);
            info = result.data.shareInfo;
        });
    }

    WechatSdk mWechatSdk;
    QQSdk mQQSdk;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_num:
            case R.id.btn_detail:
                InviteListActivity.start(this, mBinding.getItem().inviteNum);
                break;
            case R.id.btn_qq:
            case R.id.btn_qzone:
            case R.id.btn_friend:
            case R.id.btn_wx:
                onShare(v.getId());
                break;

        }
    }

    void onShare(int id) {
        if (info == null) {
            return;
        }
        switch (id) {
            case R.id.btn_qq:
            case R.id.btn_qzone:
                mQQSdk = new QQSdk(this, BuildConfig.APP_ID_QQ);
                mQQSdk.shareToQQ(info.title, info.content, info.img, info.url, id == R.id.btn_qq);
                break;
            case R.id.btn_friend:
            case R.id.btn_wx:
                int scene = id == R.id.btn_friend ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
                mWechatSdk = new WechatSdk(this, BuildConfig.APP_ID_WECHAT);
                mWechatSdk.share(info.title, info.content, info.img, info.url, scene);
                break;
        }

    }

}
