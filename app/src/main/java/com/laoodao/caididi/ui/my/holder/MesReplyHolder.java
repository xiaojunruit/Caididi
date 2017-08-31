package com.laoodao.caididi.ui.my.holder;

import android.view.View;

import com.laoodao.caididi.Const;
import com.laoodao.caididi.R;
import com.laoodao.caididi.common.binding.BindingHolder;
import com.laoodao.caididi.databinding.ItemMesReplyBinding;
import com.laoodao.caididi.retrofit.user.Message;
import com.laoodao.caididi.ui.wenda.activity.AnswerDetailActivity;

import ezy.lite.itemholder.annotation.ItemLayout;

/**
 * Created by Administrator on 2017/1/12.
 */
@ItemLayout(R.layout.item_mes_reply)
public class MesReplyHolder extends BindingHolder<Message,ItemMesReplyBinding>{
    public MesReplyHolder(View v) {
        super(v);
        v.setOnClickListener(v1 -> {
            AnswerDetailActivity.start(v1.getContext(),item.id,false);
        });
    }

    @Override
    public void refresh() {
        if (item.noticeType== Const.DYNAMIC){
            binding.txtNamesTotal.setText(item.num>1?" 等"+item.num+"人回答了您的提问":" "+item.num+"人回答了您的提问");
        }else if(item.noticeType==Const.REPLY){
            binding.txtNamesTotal.setText(item.num>1?" 等"+item.num+"人评论了您的回答":" "+item.num+"人评论了您的回答");
        }else if(item.noticeType==Const.NOTICE){
            binding.txtNamesTotal.setText(item.num>1?" 等"+item.num+"人回复了您的评论":" "+item.num+"人回复了您的评论");
        }
        binding.setItem(item);
    }
}
