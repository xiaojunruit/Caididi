package com.laoodao.caididi.ui.my.activity;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.laoodao.caididi.Const;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.common.util.RxBus;
import com.laoodao.caididi.databinding.ActivityReserveAfterBinding;
import com.laoodao.caididi.event.ReserveEvent;
import com.laoodao.caididi.retrofit.main.Answer;
import com.laoodao.caididi.ui.widget.ShapedImageView;
import com.laoodao.caididi.ui.widget.imageSelect.model.LocalMedia;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ezy.lite.util.ContextUtil;
import ezy.lite.util.Device;
import ezy.lite.util.LogUtil;

/**
 * Created by WORK on 2017/3/6.
 */

public class ReserveAfterActivity extends BaseActivity {
    private ActivityReserveAfterBinding mBinding;
    private String id;
    private int state;

    public static void start(Context context, String id) {
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        ContextUtil.startActivity(context, ReserveAfterActivity.class, bundle);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_reserve_after);
        id = getIntent().getStringExtra("id");
        mBinding.txtEvaluate.setOnClickListener(v -> {
            if (state == Const.SU_SETTLE) {
                finish();
            } else {
                EvaluateReserveActivity.start(v.getContext(), id);
            }
        });
        refresh();
        RxBus.ofType(ReserveEvent.class).compose(bindToLifecycle()).subscribe(event -> {
            this.state = Integer.parseInt(event.state);
            if (state == Const.SU_SETTLE) {
                mBinding.txtEvaluate.setText("完成");
                mBinding.txtSuccess.setText(event.stateDesc);
                mBinding.imgState.setBackgroundResource(R.mipmap.bg_su_reserve);

            }
        });
    }

    private void refresh() {
        API.user().reserDetail(Integer.parseInt(id)).compose(transform()).subscribe(result -> {
            mBinding.setItem(result.data);
            addImages(result.data.imgArr);
        });
    }


    private void addImages(ArrayList<Answer.Img> imgList) {
        mBinding.imageContent.removeAllViews();
        for (Answer.Img url : imgList) {
            ShapedImageView imageView = new ShapedImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.rightMargin = Device.dp2px(10);
            params.width = (Device.dm.widthPixels - Device.dp2px(40)) / 3;
            params.height = Device.dp2px(80);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(params);
            Glide.with(this).load(url.url).thumbnail(0.5f).into(imageView);
            mBinding.imageContent.addView(imageView);
        }
    }


}
