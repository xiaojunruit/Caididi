package com.laoodao.caididi.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;

import com.tencent.connect.common.Constants;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.net.URL;
import java.util.Map;

import ezy.lite.util.LogUtil;
import ezy.lite.util.RandomUtil;
import ezy.lite.util.UI;
import rx.Observable;
import rx.schedulers.Schedulers;


public class WechatSdk implements IWXAPIEventHandler {

    String mAppId;
    Activity mContext;
    IWXAPI api;
    OnResult mListener;

    public WechatSdk(Activity context, String appId) {
        //        LogUtil.e(appId);
        mAppId = appId;
        mContext = context;
        api = WXAPIFactory.createWXAPI(mContext, mAppId, true);
        api.registerApp(mAppId);
    }

    public void authorize(final OnResult<SendAuth.Resp> listener) {
        //        if (!api.isWXAppInstalled()) {
        //
        //            UI.showToast(mContext, "您未安装微信!");
        //            return;
        //        }
        mListener = listener;

        LogUtil.e("send start");
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = RandomUtil.numbers(5);
        boolean ret = api.sendReq(req);
        LogUtil.e("send end = " + ret);

    }

    public void share(String title, String desc, String photo, String url, int scene) {
        WXWebpageObject obj = new WXWebpageObject();
        obj.webpageUrl = url;

        WXMediaMessage msg = new WXMediaMessage(obj);
        msg.mediaObject = obj;
        msg.title = title;
        msg.description = desc;

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = scene;
        Observable.just(req).subscribeOn(Schedulers.newThread()).subscribe(q -> {

            try {
                Bitmap bmp = BitmapFactory.decodeStream(new URL(photo).openStream());
                Bitmap thumb = thumb(bmp, 150, 150);
                bmp.recycle();
                msg.setThumbImage(thumb);
                api.sendReq(q);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

    public void share(String photo, int scene) {


        Observable.just(1).subscribeOn(Schedulers.newThread()).subscribe(q -> {
            try {
                Bitmap bmp = BitmapFactory.decodeStream(new URL(photo).openStream());
                WXImageObject imgObj = new WXImageObject(bmp);
                WXMediaMessage msg = new WXMediaMessage();
                msg.mediaObject = imgObj;
                SendMessageToWX.Req req = new SendMessageToWX.Req();

                req.transaction = buildTransaction("img");
                req.message = msg;
                req.scene = scene;
                Bitmap thumbBmp = thumb(bmp, 150, 150);
                bmp.recycle();
                msg.thumbData = bmpToByteArray(thumbBmp, true);
                api.sendReq(req);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
    public static Bitmap thumb(Bitmap src, int dstWidth, int dstHeight) {
        final int width = src.getWidth();
        final int height = src.getHeight();
        final float sx = dstWidth / (float) width;
        final float sy = dstHeight / (float) height;
        Matrix m = new Matrix();
        m.setScale(sx, sy);
        Bitmap dst = Bitmap.createBitmap(dstWidth, dstHeight, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(dst);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(src, m, null);
        return dst;
    }

    public void pay(Map<String, String> params) {
        PayReq req = new PayReq();
        req.appId = mAppId;
        req.partnerId = params.get("partnerid");
        req.prepayId = params.get("prepayid");
        req.packageValue = params.get("packageValue");
        req.nonceStr = params.get("noncestr");
        req.timeStamp = params.get("timestamp");
        req.sign = params.get("sign");
        boolean ret = api.sendReq(req);
        LogUtil.e("send end = " + ret);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode != 1999 || mListener == null) {
            return;
        }
        api.handleIntent(data, this);
    }


    public interface OnResult<T> {
        void onResult(T info);
    }


    @Override
    public void onReq(BaseReq req) {
        LogUtil.e("" + req.getType());
        switch (req.getType()) {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
                break;
            default:
                break;
        }
    }

    @Override
    public void onResp(BaseResp resp) {
        LogUtil.e("t = " + resp.transaction + ", type = " + resp.getType() + ", errCode = " + resp.errCode + ", err = " + resp.errStr);

        switch (resp.getType()) {
            case ConstantsAPI.COMMAND_SENDAUTH:
                onAuthResult(resp);
                break;
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
                break;
            default:
                break;
        }
    }

    void onShareResult(BaseResp resp) {
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                break;
            default:
                break;
        }
    }

    void onAuthResult(BaseResp resp) {
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                SendAuth.Resp sp = (SendAuth.Resp) resp;
                String code = sp.code;

                LogUtil.e("code = " + code);
                mListener.onResult(sp);
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                UI.showToast(mContext, "验证失败!");
                break;
            default:
                break;
        }
    }
}