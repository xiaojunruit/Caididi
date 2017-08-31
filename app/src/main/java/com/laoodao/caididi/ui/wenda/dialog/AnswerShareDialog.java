package com.laoodao.caididi.ui.wenda.dialog;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.View;

import com.flyco.dialog.widget.base.BottomBaseDialog;
import com.laoodao.caididi.BuildConfig;
import com.laoodao.caididi.R;
import com.laoodao.caididi.databinding.DialogAnswerShareBinding;
import com.laoodao.caididi.retrofit.main.ShareInfo;
import com.laoodao.caididi.wxapi.QQSdk;
import com.laoodao.caididi.wxapi.WechatSdk;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;

import ezy.lite.util.LogUtil;

/**
 * Created by Administrator on 2016/12/14.
 */

public class AnswerShareDialog extends BottomBaseDialog<AnswerShareDialog> implements View.OnClickListener {
    DialogAnswerShareBinding binding;
    private ShareInfo info;

    public AnswerShareDialog(Context context, ShareInfo info ) {
        super(context);
        this.info = info;

    }

    @Override
    public View onCreateView() {
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.dialog_answer_share, null, false);
        binding.setOnClick(this);
        return binding.getRoot();
    }

    @Override
    public void setUiBeforShow() {

    }

    WechatSdk mWechatSdk;
    QQSdk mQQSdk;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_qq:
            case R.id.btn_qzone:
            case R.id.btn_friend:
            case R.id.btn_wx:
                onShare(v.getId());
                dismiss();
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
                LogUtil.e("+++++++++++++++++++++++>>>" + getOwnerActivity());
                mQQSdk = new QQSdk((Activity) mContext, BuildConfig.APP_ID_QQ);
                mQQSdk.shareToQQ(info.title, info.content, info.img, info.url, id == R.id.btn_qq);
                break;
            case R.id.btn_friend:
            case R.id.btn_wx:
                int scene = id == R.id.btn_friend ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
                mWechatSdk = new WechatSdk((Activity)mContext, BuildConfig.APP_ID_WECHAT);
                mWechatSdk.share(info.title, info.content, info.img, info.url, scene);
                break;
        }

    }
}
