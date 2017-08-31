package com.laoodao.caididi.ui.my.holder;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.laoodao.caididi.Global;
import com.laoodao.caididi.R;
import com.laoodao.caididi.Route;
import com.laoodao.caididi.common.binding.BindingHolder;
import com.laoodao.caididi.databinding.ItemDynamicMenuBinding;
import com.laoodao.caididi.retrofit.user.DynamicMenu;
import com.laoodao.caididi.retrofit.user.LoginInfo;
import com.laoodao.caididi.ui.main.user.LoginPhoneActivity;
import com.laoodao.caididi.utils.SharedPreferencesUtil;

import ezy.lite.itemholder.annotation.ItemLayout;
import ezy.lite.util.ContextUtil;
import ezy.lite.util.LogUtil;
import ezy.widget.view.SettingView;

/**
 * Created by XiaoGe on 2017/2/4.
 */
@ItemLayout(R.layout.item_dynamic_menu)
public class DynamicMenuHolder extends BindingHolder<DynamicMenu, ItemDynamicMenuBinding> {
    private String imPwd="";
    public DynamicMenuHolder(View v) {
        super(v);
        v.setOnClickListener(view -> {
            if (Global.session().isLoggedIn()) {
                if (item.url.contains("chat_list")){
                    loginHuanXin(view.getContext());
                    return;
                }
                Route.go(v.getContext(), item.url);
            } else {
                ContextUtil.startActivity(v.getContext(), LoginPhoneActivity.class);
            }
        });
    }

    private void loginHuanXin(Context context){
        if (!EMClient.getInstance().isLoggedInBefore()) {
            Toast.makeText(context, "登录异常,正在重新登录,请稍后...", Toast.LENGTH_SHORT).show();
            LoginInfo info = Global.info();
            if (!TextUtils.isEmpty(info.emcode)) {
                imPwd = info.emcode;
            } else {
                String pwd = SharedPreferencesUtil.getInstance().getString("im_pwd", "");
                if (!TextUtils.isEmpty(pwd)) {
                    imPwd = pwd;
                }
            }
            if (TextUtils.isEmpty(imPwd)) {
                Toast.makeText(context, "密码获取失败，请重新获取密码", Toast.LENGTH_SHORT).show();
                return;
            }
            EMClient.getInstance().login(info.username, imPwd, new EMCallBack() {//回调
                @Override
                public void onSuccess() {
                    EMClient.getInstance().groupManager().loadAllGroups();
                    EMClient.getInstance().chatManager().loadAllConversations();
                    Route.go(context, item.url);
                    Log.e("main", "登录聊天服务器成功！");
                }

                @Override
                public void onProgress(int progress, String status) {

                }

                @Override
                public void onError(int code, String message) {
                    Log.e("main", "登录聊天服务器失败！code=" + code + "----" + message);
                }
            });
        }else{
            Route.go(context, item.url);
        }
    }

    @Override
    public void refresh() {
        binding.setItem(item);
        if (!Global.session().isLoggedIn()||!item.url.contains("chat_list")){
            binding.msgCount.setVisibility(View.GONE);
        }else{
            if (!EMClient.getInstance().isLoggedInBefore()){
                return;
            }
            if (getUnreadMsgCountTotal()>=1) {
                binding.msgCount.setVisibility(View.VISIBLE);
                binding.msgCount.setText(String.valueOf(getUnreadMsgCountTotal()));
            }
        }
        binding.executePendingBindings();
    }

    public int getUnreadMsgCountTotal() {
        return EMClient.getInstance().chatManager().getUnreadMsgsCount();
    }

    public static class EntryViewTarget extends ViewTarget<SettingView, GlideDrawable> {
        public EntryViewTarget(SettingView view) {
            super(view);

        }

        @Override
        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
            this.view.setIcon(resource.getCurrent());
        }
    }
}
