package com.laoodao.caididi.ui.my.activity;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.databinding.ActivityReserveDetailBinding;
import com.laoodao.caididi.retrofit.main.Answer;
import com.laoodao.caididi.ui.wenda.activity.ImgViewPagerActivity;
import com.laoodao.caididi.ui.widget.ShapedImageView;

import java.util.ArrayList;

import ezy.lite.util.ContextUtil;
import ezy.lite.util.Device;

/**
 * Created by WORK on 2017/3/7.
 */

public class ReserveDetailActivity extends BaseActivity {
    ActivityReserveDetailBinding mBinding;
    private int id;

    public static void start(Context context, int id) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        ContextUtil.startActivity(context, ReserveDetailActivity.class, bundle);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_reserve_detail);
        id = getIntent().getIntExtra("id", 0);
        refresh();
    }

    private void refresh() {
        API.user().reserDetail(id).compose(transform()).subscribe(result -> {
            mBinding.setItem(result.data);
            addImageView(result.data.imgArr);
            addImages(result.data.finish.imgArr);
        });
    }


    private void addImageView(ArrayList<Answer.Img> imgArr) {
        int width = Device.dm.widthPixels - Device.dp2px(30);
        mBinding.llImgContent.removeAllViewsInLayout();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = Device.dp2px(10);
        for (int i = 0; i < imgArr.size(); i++) {
            ShapedImageView view = new ShapedImageView(this);
            int finalI = i;
            Glide.with(this).load(imgArr.get(i).url).asBitmap().diskCacheStrategy(DiskCacheStrategy.RESULT).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    Matrix matrix = new Matrix();
                    float sale = width * 1f / resource.getWidth();
                    matrix.postScale(sale, sale);
                    Bitmap bitmap = Bitmap.createBitmap(resource, 0, 0, resource.getWidth(), resource.getHeight(), matrix, true);
                    view.setImageBitmap(bitmap);
                    view.setLayoutParams(params);
                    view.setOnClickListener(v -> {
                        ImgViewPagerActivity.start(ReserveDetailActivity.this, imgArr, finalI);
                    });
                }
            });
            mBinding.llImgContent.addView(view);
        }
    }

    private void addImages(ArrayList<Answer.Img> imgList) {
        mBinding.imageContent.removeAllViews();
        for (Answer.Img url : imgList) {
            ShapedImageView imageView = new ShapedImageView(this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.rightMargin = Device.dp2px(10);
            params.width = (Device.dm.widthPixels - Device.dp2px(40)) / 3;
            params.height = Device.dp2px(80);
            imageView.setLayoutParams(params);
            Glide.with(this).load(url.url).thumbnail(0.5f).into(imageView);
            mBinding.imageContent.addView(imageView);
        }
    }


}
