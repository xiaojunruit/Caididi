package com.laoodao.caididi.ui.wenda.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.jaeger.library.StatusBarUtil;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.databinding.ActivityImgViewpagerBinding;
import com.laoodao.caididi.common.adapter.ImgViewPagerAdapter;
import com.laoodao.caididi.retrofit.main.Answer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ezy.lite.util.FileUtil;
import ezy.lite.util.IoUtil;
import ezy.lite.util.LogUtil;
import ezy.lite.util.UI;

/**
 * Created by Administrator on 2016/12/9.
 */

public class ImgViewPagerActivity extends BaseActivity {
    ActivityImgViewpagerBinding binding;
    private ImageView[] tips;
    private int currentPage = 0;
    private ImgViewPagerAdapter adapter;
    private List<Answer.Img> imgs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_img_viewpager);
        StatusBarUtil.setColor(this, ResourcesCompat.getColor(getResources(), R.color.textBlack, getTheme()), 0);
        imgs = (List<Answer.Img>) getIntent().getSerializableExtra("imgs");
        initImages();
        currentPage = getIntent().getIntExtra("position", 0);
        initData();
        initListener();
        currentItem();
    }

    private void initImages() {
        for (Answer.Img img : imgs) {
            String params = "";
            String symbol = img.url.substring(img.url.length() - 1, img.url.length());
            if (!img.url.isEmpty() && img.url.contains("?") && !"?".equals(symbol)) {
                params = img.url.substring(img.url.indexOf("?") + 1, img.url.length());
                img.url = img.url.substring(0, img.url.indexOf("?")).replace("_" + params, "");
            }
        }
    }


    public static void start(Context context, ArrayList<Answer.Img> imgs, int position) {
        Intent intent = new Intent(context, ImgViewPagerActivity.class);
        intent.putExtra("imgs", imgs);
        intent.putExtra("position", position);
        context.startActivity(intent);
    }

    private void initListener() {
        binding.pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tips[currentPage].setBackgroundResource(R.mipmap.point_grey);
                currentPage = position;
                tips[position].setBackgroundResource(R.mipmap.point_white);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        adapter.setOnClickListener(() -> {
            finish();
        });

        binding.btnSave.setOnClickListener(v -> {
            Glide.with(this).load(imgs.get(currentPage).url).downloadOnly(new SimpleTarget<File>() {
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
                        ImgViewPagerActivity.this.sendBroadcast(mediaScanIntent);
                        UI.showToast(ImgViewPagerActivity.this, "保存图片至" + imageFile.getAbsolutePath());
                    } catch (Exception e) {
                        e.printStackTrace();
                        UI.showToast(ImgViewPagerActivity.this, "保存失败");
                    }
                }

                @Override
                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                    super.onLoadFailed(e, errorDrawable);
                    UI.showToast(ImgViewPagerActivity.this, "保存失败");
                }
            });
        });
    }


    private void initData() {
        binding.pager.setPageMargin((int) (getResources().getDisplayMetrics().density * 15));
        adapter = new ImgViewPagerAdapter(ImgViewPagerActivity.this, imgs);
        tips = new ImageView[imgs.size()];
        for (int i = 0; i < tips.length; i++) {
            ImageView img = new ImageView(this);
            img.setLayoutParams(new LinearLayout.LayoutParams(10, 10));
            tips[i] = img;
            if (i == currentPage) {
                img.setBackgroundResource(R.mipmap.point_white);
            } else {
                img.setBackgroundResource(R.mipmap.point_grey);
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            params.leftMargin = 5;

            params.rightMargin = 5;

            binding.tipsBox.addView(img, params);
        }
        binding.pager.setAdapter(adapter);
    }

    public void currentItem() {
        binding.pager.setCurrentItem(currentPage);
    }


}
