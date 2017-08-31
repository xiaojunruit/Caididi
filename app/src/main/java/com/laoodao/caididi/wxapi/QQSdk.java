package com.laoodao.caididi.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.laoodao.caididi.Const;
import com.laoodao.caididi.R;
import com.laoodao.caididi.ui.my.activity.JointLoginActivity;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

import java.util.ArrayList;

import ezy.lite.util.ContextUtil;
import ezy.lite.util.LogUtil;

public class QQSdk {

    Activity mContext;
    Tencent mTencent;
    QQListener mListener;

    public QQSdk(Activity context, String appId) {
        mContext = context;
        mTencent = Tencent.createInstance(appId, context);
    }

    public void authorize(final OnLoginOk<QQToken> listener) {
        if (mTencent.isSessionValid()) {
            LogUtil.e("QQToken = " + mTencent.getQQToken());
            listener.onLogin(mTencent.getQQToken());
        } else {
            mListener = new QQListener() {
                @Override
                protected void doComplete(JSONObject values) {
                    initOpenidAndToken(values);
                    listener.onLogin(mTencent.getQQToken());
                }
            };
            mTencent.login(mContext, "get_simple_userinfo", mListener);
        }
    }

//    public void setUserInfo(QQToken token) {
//
//        UserInfo userInfo = new UserInfo(mContext, token);
//        userInfo.getUserInfo(new QQListener() {
//            @Override
//            protected void doComplete(JSONObject values) {
//                LogUtil.e("json:" + values.toString());
//                JointLoginActivity.start(mContext,values.toString(), Const.QQ);
//            }
//        });
//    }


    public void shareImg(String imageUrl) {
        Bundle params = new Bundle();
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, imageUrl);
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
        IUiListener listener = new IUiListener() {
            @Override
            public void onComplete(Object o) {
            }

            @Override
            public void onError(UiError uiError) {

            }

            @Override
            public void onCancel() {
            }
        };
        mTencent.shareToQQ(mContext, params, listener);
    }


    public void shareToQQ(String title, String desc, String photo, String url, boolean isQQ) {

        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putString("summary", desc);
        if (isQQ) {
            bundle.putString("imageUrl", photo);
        } else {
            ArrayList images = new ArrayList();
            images.add(photo);
            bundle.putStringArrayList("imageUrl", images);
        }
        bundle.putString("targetUrl", url);
        bundle.putString("appName", mContext.getResources().getString(R.string.app_name));

        IUiListener listener = new IUiListener() {
            @Override
            public void onComplete(Object o) {

            }

            @Override
            public void onError(UiError uiError) {

            }

            @Override
            public void onCancel() {

            }
        };

        if (isQQ) {
            mTencent.shareToQQ(mContext, bundle, listener);
        } else {
            mTencent.shareToQzone(mContext, bundle, listener);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Tencent.onActivityResultData(requestCode, resultCode, data, mListener);
    }


    private void initOpenidAndToken(JSONObject jsonObject) {
        try {
            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires) && !TextUtils.isEmpty(openId)) {
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(openId);

            }
            LogUtil.e("=======>>>>>>" + !TextUtils.isEmpty(token));
        } catch (Exception e) {
            LogUtil.e("===>>>" + e.getMessage());
            e.printStackTrace();
        }
    }


    public interface OnLoginOk<T> {
        void onLogin(T info);
    }

    public static class QQListener implements IUiListener {

        @Override
        public void onComplete(Object response) {
            if (null == response) {
                LogUtil.e("登录失败: 返回为空");
                return;
            }
            JSONObject jsonResponse = (JSONObject) response;
            if (null != jsonResponse && jsonResponse.length() == 0) {
                LogUtil.e("登录失败: 返回为空");
                return;
            }
            LogUtil.e("登录成功: " + response.toString());
            doComplete((JSONObject) response);
        }

        protected void doComplete(final JSONObject values) {

        }


        @Override
        public void onError(UiError e) {
            LogUtil.e("错误信息:" + e.errorDetail);
        }

        @Override
        public void onCancel() {
            LogUtil.e("取消了当前的登录操作");
        }
    }


}