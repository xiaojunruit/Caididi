package com.laoodao.caididi.ui.wenda.holder;

import android.content.Context;
import android.view.View;

import com.laoodao.caididi.Const;
import com.laoodao.caididi.Global;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.binding.BindingHolder;
import com.laoodao.caididi.databinding.ItemFollowUserBinding;
import com.laoodao.caididi.retrofit.main.FollowUser;
import com.laoodao.caididi.ui.main.MainActivity;
import com.laoodao.caididi.ui.main.user.LoginPhoneActivity;
import com.laoodao.caididi.ui.my.activity.OtherUserInfoActivity;

import ezy.lite.itemholder.annotation.ItemLayout;
import ezy.lite.util.ContextUtil;

/**
 * Created by Administrator on 2016/12/22.
 */
@ItemLayout(R.layout.item_follow_user)
public class FollowUserHolder extends BindingHolder<FollowUser, ItemFollowUserBinding> implements View.OnClickListener {
    private Context context;

    public FollowUserHolder(View v) {
        super(v);
        context = v.getContext();
        binding.setOnClick(this);
        binding.txtFollow.setOnClickListener(v1 -> {
            //0未关注  1单方面关注  2互相关注
            switch (item.state) {
                case 1:
                case 2:
                case 0:
                    if (!Global.session().isLoggedIn()){
                        ContextUtil.startActivity(context, LoginPhoneActivity.class);
                        break;
                    }
                    stateClick(item.state);
                    break;
            }

        });
    }

    private void stateClick(int state){
        switch (state) {
            case 1:
                followUserInterface(Const.CANCEL_FOLLOW);
                break;
            case 2:
                followUserInterface(Const.CANCEL_FOLLOW);
                break;
            case 0:
                followUserInterface(Const.FOLLOW);
                break;
        }
    }

    @Override
    public void refresh() {

        binding.setItem(item);
        binding.executePendingBindings();
    }

    private void followUserInterface(int type) {
        API.main().followUser(item.memberId, type).compose(new API.Transformer<>(context)).subscribe(result -> {
            if (result.data == null || result.data.isEmpty()) {
                return;
            }
            Global.info().myAttention = Integer.parseInt(result.data.get("my_attention"));
            Global.info().attentionMe = Integer.parseInt(result.data.get("attention_me"));
            item.state = Integer.parseInt(result.data.get("state"));
            refresh();
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cl_avatar:
                if (Global.info().cid==item.cid){
                    MainActivity.start(v.getContext(),Const.TAB_USER);
                    break;
                }
                OtherUserInfoActivity.start(v.getContext(), item.cid);
                break;
        }
    }
}
