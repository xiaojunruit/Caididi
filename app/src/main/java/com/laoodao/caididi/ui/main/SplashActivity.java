package com.laoodao.caididi.ui.main;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.laoodao.caididi.BuildConfig;
import com.laoodao.caididi.Global;
import com.laoodao.caididi.LocalSetting;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.util.Helper;
import com.laoodao.caididi.databinding.ActivitySplashBinding;
import com.laoodao.caididi.retrofit.main.LaunchAd;
import com.laoodao.caididi.common.util.Convert;
import com.laoodao.caididi.common.util.countdown.RxCountDown;
import com.laoodao.caididi.utils.PermissionUtil;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.trello.rxlifecycle.android.RxLifecycleAndroid;

import java.util.ArrayList;
import java.util.List;

import ezy.lite.util.LogUtil;
import ezy.lite.util.UI;
import ezy.widget.view.ZeeBannerView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by WORK on 2016/9/9.
 */

public class SplashActivity extends AppCompatActivity {

    ActivitySplashBinding binding;
    private boolean mIsStarted;

    List<String> fromDefaultN() {
        List<String> items = new ArrayList<>();
        items.add(Helper.toUri(this, R.mipmap.splash_5).toString());
//        items.add(Helper.toUri(this, R.mipmap.splash_5).toString());
//        items.add(Helper.toUri(this, R.mipmap.splash_5).toString());
//        items.add(Helper.toUri(this,R.mipmap.splash_5).toString());
        return items;
    }

    List<String> fromDefaultS() {
        List<String> items = new ArrayList<>();
        items.add(Convert.toUri(this, R.mipmap.splash).toString());
        return items;
    }

    List<String> mItems = new ArrayList<>();
    boolean mSkippable = true;
    String mUrl = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        LocalSetting setting = LocalSetting.with(this);
        LaunchAd ad = new Gson().fromJson(setting.getLaunchedAd(), LaunchAd.class);
        long now = System.currentTimeMillis();
        if (!setting.isLaunched() || BuildConfig.VERSION_CODE > setting.getVersionCode()) {
            mItems = fromDefaultN();
            setting.setLaunched(false).setVersionCode(BuildConfig.VERSION_CODE);
            mSkippable = false;
        } else if (ad != null && ad.startTime < now && ad.endTime > now && ad.images != null && !TextUtils.isEmpty(ad.images)) {
            mItems.add(ad.images);
            mSkippable = ad.skippable;
            mUrl = ad.url;
        } else {
            mItems = fromDefaultS();
        }

        binding.banner.setDataList(mItems);
        binding.banner.setItemViewFactory(new ZeeBannerView.ItemViewFactory<String>() {
            private ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#ffffff"));

            @Override
            public View create(ViewGroup container, int position, String item) {

                if (1 == mItems.size() && setting.isLaunched()) {
                    View single = LayoutInflater.from(container.getContext()).inflate(R.layout.layout_splash_single, container, false);
                    ImageView img = (ImageView) single.findViewById(R.id.img_last);
//                    Glide.with(Global.context()).load(item).into((ImageView) single.findViewById(R.id.img_last))
                    img.setImageResource(R.mipmap.splash);
                    single.findViewById(R.id.btn_skip).setOnClickListener(v -> start());
                    RxCountDown.countdown(3).compose(RxLifecycleAndroid.bindView(single)).doOnCompleted(() -> start()).subscribe(seconds -> {
                        UI.setText(single, R.id.btn_skip, "跳过" + seconds + "s");
                    });
                    single.setOnClickListener(v -> start());
                    return single;
                } else if (position + 1 == mItems.size()) {
                    setting.setLaunched(true).setVersionCode(BuildConfig.VERSION_CODE);
                    View last = LayoutInflater.from(container.getContext()).inflate(R.layout.layout_splash_last, container, false);
                    ImageView image = (ImageView) last.findViewById(R.id.img_last);
//                    Glide.with(Global.context()).load(item).diskCacheStrategy(DiskCacheStrategy.SOURCE)
////                            .centerCrop()
//                           .into(image);
                    image.setImageResource(R.mipmap.splash_5);
                    last.findViewById(R.id.btn_enter).setOnClickListener(v -> start());
                    return last;
                } else {
                    ImageView view = new ImageView(container.getContext());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
                            .MATCH_PARENT);
                    view.setLayoutParams(params);
                    Glide.with(Global.context()).load(item).diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .centerCrop()
                            .placeholder(colorDrawable).dontAnimate().into(view);
                    return view;
                }
            }
        });

//        API.main().init().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).onErrorResumeNext(throwable -> {
//            LogUtil.e("+====================>throwable" + throwable);
//            return Observable.empty();
//        }).subscribe(result -> {
//            LogUtil.e("+====================>result" + result);
//            if (result.data == null) {
//                return;
//            }
//            initLaunchAd(result.data.launchAd);
//        });
        binding.banner.start();
        PermissionUtil.externalStorage(new PermissionUtil.RequestPermissionListener() {
            @Override
            public void success() {

            }

            @Override
            public void failure() {
                Toast.makeText(SplashActivity.this, "请求权限失败,请前往设置开启权限", Toast.LENGTH_SHORT).show();
            }
        }, new RxPermissions(this));
    }

//    private void initLaunchAd(LaunchAd launchAd) {
//        if (launchAd == null) {
//            return;
//        }
//        LocalSetting.with(this).setLaunchedAd(new Gson().toJson(launchAd));
//        Glide.with(Global.context()).load(launchAd.images).diskCacheStrategy(DiskCacheStrategy.SOURCE).centerCrop();
//
//    }

    public void redirect() {
        if (mIsStarted) {
            return;
        }
        mIsStarted = true;
        LogUtil.e("url ==> " + mUrl);
    }

    public void start() {
        if (mIsStarted) {
            return;
        }
        mIsStarted = true;
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }


}
