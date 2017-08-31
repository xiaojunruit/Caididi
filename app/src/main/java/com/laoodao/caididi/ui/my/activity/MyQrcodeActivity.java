package com.laoodao.caididi.ui.my.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.laoodao.caididi.BuildConfig;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.databinding.ActivityMyQrcodeBinding;
import com.laoodao.caididi.retrofit.main.ShareInfo;
import com.laoodao.caididi.ui.wenda.activity.ImgViewPagerActivity;
import com.laoodao.caididi.wxapi.QQSdk;
import com.laoodao.caididi.wxapi.WechatSdk;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

import ezy.lite.util.LogUtil;
import ezy.lite.util.UI;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by WORK on 2017/2/28.
 */

public class MyQrcodeActivity extends BaseActivity implements View.OnClickListener {

    ActivityMyQrcodeBinding binding;
    private ShareInfo info;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_qrcode);
        binding.setListener(this);
        refresh();
    }

    private void refresh() {
        API.user().sharecode().compose(transform()).subscribe(result -> {
            binding.setItem(result.data);
            info = result.data.shareInfo;
        });
    }

    WechatSdk mWechatSdk;
    QQSdk mQQSdk;

    void onShare(int id) {
        if (info == null) {
            return;
        }
        switch (id) {
            case R.id.btn_qq:
            case R.id.btn_qzone:
                mQQSdk = new QQSdk(this, BuildConfig.APP_ID_QQ);
                Observable.just(1).subscribeOn(Schedulers.newThread()).subscribe(a -> {
                    try {
                        Bitmap bmp = BitmapFactory.decodeStream(new URL(info.qrcode).openStream());
                        String path = saveBitmap(bmp);
                        mQQSdk.shareImg(path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                break;
            case R.id.btn_friend:
                mWechatSdk = new WechatSdk(this, BuildConfig.APP_ID_WECHAT);
                mWechatSdk.share(info.qrcode, SendMessageToWX.Req.WXSceneTimeline);
                break;
            case R.id.btn_wx:
                mWechatSdk = new WechatSdk(this, BuildConfig.APP_ID_WECHAT);
                mWechatSdk.share(info.qrcode, SendMessageToWX.Req.WXSceneSession);
                break;
        }
    }

    FileOutputStream out;
    String saveBitmap(Bitmap bitmap) {
        File file = new File(Environment.getExternalStorageDirectory(), "share.jpg");
        try {
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LogUtil.e("file.getPath():" + file.getPath());
        return file.getPath();
    }

    private void downloadImg(String url) {
        Glide.with(this).load(url).downloadOnly(new SimpleTarget<File>() {
            @Override
            public void onResourceReady(File resource, GlideAnimation<? super File> glideAnimation) {
                String destFileDir = Environment.getExternalStorageDirectory() + "/caididi";
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(resource));
                    File dir = new File(destFileDir);
                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                    File imageFile = new File(destFileDir, new Date().getTime() + ".jpg");
                    FileOutputStream out = new FileOutputStream(imageFile);
                    if (bitmap != null) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    }
                    out.flush();
                    out.close();
                    bitmap.recycle();
                    bitmap = null;
                    Intent mediaScanIntent = new Intent(
                            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    File file = new File(imageFile.getAbsolutePath());
                    Uri contentUri = Uri.fromFile(file);
                    mediaScanIntent.setData(contentUri);
                    sendBroadcast(mediaScanIntent);
                    UI.showToast(MyQrcodeActivity.this, "保存图片至" + imageFile.getAbsolutePath());
                } catch (Exception e) {
                    e.printStackTrace();
                    UI.showToast(MyQrcodeActivity.this, "保存失败");
                }
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                super.onLoadFailed(e, errorDrawable);
                UI.showToast(MyQrcodeActivity.this, "保存失败");
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_qq:
            case R.id.btn_qzone:
            case R.id.btn_friend:
            case R.id.btn_wx:
                onShare(v.getId());
                break;
            case R.id.btn_download:
                if (!TextUtils.isEmpty(binding.getItem().qrcode)) {
                    downloadImg(binding.getItem().qrcode);
                }
                break;
        }
    }
}
