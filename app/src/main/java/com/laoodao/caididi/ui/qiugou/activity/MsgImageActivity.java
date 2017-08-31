package com.laoodao.caididi.ui.qiugou.activity;

import android.content.Context;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.app.BaseActivity;

import ezy.lite.util.ContextUtil;
import ezy.lite.util.LogUtil;
import uk.co.senab.photoview.PhotoView;

/**
 * Created by WORK on 2017/2/16.
 */

public class MsgImageActivity extends BaseActivity {
    public static void start(Context context, String url) {
        Bundle bundle = new Bundle();
        bundle.putString("URL", url);
        ContextUtil.startActivity(context, MsgImageActivity.class, bundle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_msg_image);

        final String url = getIntent().getExtras().getString("URL");
        LogUtil.e("url = " + url);
        PhotoView view = (PhotoView) findViewById(R.id.img_photo);
//        view.setImageURI(Uri.parse(url));
//        RequestOptions options = new RequestOptions();
//        Glide.with(view.getContext().getApplicationContext()).load(url).apply(options).into(view);
        Glide.with(view.getContext()).load(url).into(view);
    }
}
