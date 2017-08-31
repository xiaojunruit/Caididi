package com.laoodao.caididi.ui.my.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.api.Result;
import com.laoodao.caididi.common.app.BaseActivity;
import com.laoodao.caididi.common.util.RxBus;
import com.laoodao.caididi.databinding.ActivityChatBinding;
import com.laoodao.caididi.retrofit.user.ChatAvatar;
import com.laoodao.caididi.retrofit.user.ChatGoodsInit;
import com.laoodao.caididi.retrofit.user.ExperList;
import com.laoodao.caididi.ui.my.fragment.ChatFragment;
import com.trello.rxlifecycle.android.ActivityEvent;

import java.io.File;
import java.util.List;

import ezy.lite.util.LogUtil;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by WORK on 2017/8/23.
 */

public class ChatActivity extends BaseActivity {
    private ActivityChatBinding mBinding;
    private String toChatUsername;
    private String mType = "";
    private EaseChatFragment chatFragment;
    private String avatar = "";
    private String userAvatar = "";
    private ExperList mData;
    private String mPhone;
    private String nameTxt="";

    public static void start(Context context, String userId) {
        Bundle bundle = new Bundle();
        bundle.putString("userId", userId);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        intent.setClass(context, ChatActivity.class);
        context.startActivity(intent);
    }

    public static void start(Context context, String userId, String avatar, String userAvatar, String phone,String nameTxt) {
        start(context, userId, avatar, userAvatar, "", null, phone,nameTxt);
    }

    public static void start(Context context, String userId, String avatar, String userAvatar, String type, ExperList data, String phone,String nameTxt) {
        Bundle bundle = new Bundle();
        bundle.putString("userId", userId);
        bundle.putString("avatar", avatar);
        bundle.putString("userAvatar", userAvatar);
        bundle.putString("type", type);
        bundle.putSerializable("ExperList", data);
        bundle.putString("phone", phone);
        bundle.putString("nameTxt",nameTxt);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        intent.setClass(context, ChatActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_chat);
        toChatUsername = getIntent().getExtras().getString("userId");
        avatar = getIntent().getStringExtra("avatar");
        userAvatar = getIntent().getStringExtra("userAvatar");
        mType = getIntent().getStringExtra("type");
        mData = (ExperList) getIntent().getSerializableExtra("ExperList");
        mPhone = getIntent().getStringExtra("phone");
        nameTxt=getIntent().getStringExtra("nameTxt");
        mBinding.tvName.setText(mData == null ? nameTxt : mData.truename);
        mBinding.llGoods.setVisibility(TextUtils.isEmpty(mType) ? View.GONE : View.VISIBLE);
        if (mData != null) {
            mBinding.tcg.setList(mData.crops);
            mBinding.setExperList(mData);
        }
        chatFragment = new ChatFragment();
        listener();
        if (TextUtils.isEmpty(avatar)) {
            API.user().getChatAvatar(toChatUsername)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<Result<List<ChatAvatar>>>() {
                                   @Override
                                   public void onCompleted() {
                                   }

                                   @Override
                                   public void onError(Throwable e) {
                                       e.printStackTrace();
                                   }

                                   @Override
                                   public void onNext(Result<List<ChatAvatar>> result) {
                                       if (result.data.size() >= 1) {
                                           getIntent().putExtra("avatar", result.data.get(0).avatar);
                                           chatFragment.setArguments(getIntent().getExtras());
                                           getSupportFragmentManager().beginTransaction().add(R.id.container, chatFragment).commit();
                                       }
                                   }
                               }
                    );
            return;
        }
        chatFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.container, chatFragment).commit();
    }

    private void listener() {
        mBinding.llCallPhone.setOnClickListener(v -> {
            if (TextUtils.isEmpty(mPhone)) {
                return;
            }
            Uri uri = Uri.parse("tel:" + mPhone);
            Intent it = new Intent(Intent.ACTION_DIAL, uri);
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(it);
        });
        mBinding.ivBack.setOnClickListener(v -> {
            finish();
        });
        mBinding.ivClose.setOnClickListener(v -> {
            mBinding.llGoods.setVisibility(View.GONE);
        });
    }


    private MultipartBody.Builder builderData(String tag, int type, String toName, String content, File image, File voice) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM)
                .addFormDataPart("tag", tag)
                .addFormDataPart("type", String.valueOf(type))
                .addFormDataPart("to_member_name", toName)
                .addFormDataPart("content", content);
        if (image.length() > 1) {
            RequestBody content1 = RequestBody.create(MediaType.parse("text/x-markdown; charset=utf-8"), image);
            builder.addFormDataPart("image", image.getPath(), content1);
            builder.addFormDataPart("voice", "");
        } else if (voice.length() > 1) {
            RequestBody content1 = RequestBody.create(MediaType.parse("text/x-markdown; charset=utf-8"), voice);
            builder.addFormDataPart("voice", voice.getPath(), content1);
            builder.addFormDataPart("image", "");
        }
        return builder;
    }

    public void requestSendText(String content) {
        API.user().getSend(builderData("", 1, toChatUsername, content, new File(""), new File("")).build())
                .compose(transform())
                .subscribe(response -> {

                });
    }

    public void requestSendVoice(File content) {
        API.user().getSend(builderData("", 3, toChatUsername, "", new File(""), content).build())
                .compose(transform())
                .subscribe(response -> {

                });
    }

    public void requestSendImage(File content) {
        API.user().getSend(builderData("", 2, toChatUsername, "", content, new File("")).build())
                .compose(transform())
                .subscribe(response -> {

                });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // make sure only one chat activity is opened
        String username = intent.getStringExtra("userId");
        if (toChatUsername.equals(username))
            super.onNewIntent(intent);
        else {
            finish();
            startActivity(intent);
        }
    }
}
