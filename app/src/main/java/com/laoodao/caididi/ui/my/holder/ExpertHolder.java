package com.laoodao.caididi.ui.my.holder;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.laoodao.caididi.Global;
import com.laoodao.caididi.R;
import com.laoodao.caididi.Route;
import com.laoodao.caididi.common.binding.BindingHolder;
import com.laoodao.caididi.databinding.ItemExpertBinding;
import com.laoodao.caididi.retrofit.user.ExperList;
import com.laoodao.caididi.retrofit.user.LoginInfo;
import com.laoodao.caididi.ui.my.activity.ChatActivity;
import com.laoodao.caididi.utils.SharedPreferencesUtil;

import ezy.lite.itemholder.annotation.ItemLayout;
import ezy.lite.util.ContextUtil;

/**
 * Created by Administrator on 2017/8/24 0024.
 */

@ItemLayout(R.layout.item_expert)
public class ExpertHolder extends BindingHolder<ExperList, ItemExpertBinding> {

    private String imPwd;

    public ExpertHolder(View v) {
        super(v);
        itemView.setOnClickListener(view -> {
            if (!Global.session().isLoggedIn()) {
                return;
            }
            if (!TextUtils.isEmpty(item.username)) {
                LoginInfo info = Global.info();
                if (item.cid.equals(info.cid)){
                    Toast.makeText(view.getContext(), "您不能和自己聊天", Toast.LENGTH_SHORT).show();
                    return;
                }
                loginHuanXin(view.getContext(),info);
            }
        });
    }

    private void loginHuanXin(Context context,LoginInfo info){

        if (!EMClient.getInstance().isLoggedInBefore()) {
            Toast.makeText(context, "登录异常,正在重新登录,请稍后...", Toast.LENGTH_SHORT).show();

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
                    ChatActivity.start(context, item.username, item.memberAvatar, info.avatar, "experlist",item,item.mobile,item.truename);
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
            ChatActivity.start(context, item.username, item.memberAvatar, info.avatar, "experlist",item,item.mobile,item.truename);
        }
    }

    @Override
    public void refresh() {
        binding.tcg.setList(item.crops);
        binding.setExperList(item);
        binding.executePendingBindings();
    }


}
