package com.laoodao.caididi.ui.my.activity;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.databinding.ActivityMessageDetailBinding;
import com.zzhoujay.richtext.RichText;

import ezy.lite.util.ContextUtil;

/**
 * Created by WORK on 2017/6/13.
 */

public class MessageDetailActivity extends BaseActivity {

    private ActivityMessageDetailBinding binding;
    private int id;

    public static void start(Context context, int id) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        ContextUtil.startActivity(context, MessageDetailActivity.class, bundle);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_message_detail);
        id = getIntent().getIntExtra("id", 0);
        API.user().getMessageDetail(id).compose(transform()).subscribe(result -> {
            binding.tvTime.setText(result.data.addTime);
            binding.tvTitle.setText(result.data.title);
            RichText.fromHtml(result.data.content).into(binding.tvContent);
        });

    }
}
