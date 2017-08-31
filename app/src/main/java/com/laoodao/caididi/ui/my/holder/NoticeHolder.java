package com.laoodao.caididi.ui.my.holder;

import android.text.TextUtils;
import android.view.View;

import com.laoodao.caididi.R;
import com.laoodao.caididi.Route;
import com.laoodao.caididi.common.api.API;
import com.laoodao.caididi.common.api.Response;
import com.laoodao.caididi.common.binding.BindingHolder;
import com.laoodao.caididi.common.util.RxBus;
import com.laoodao.caididi.databinding.ItemNoticeBinding;
import com.laoodao.caididi.event.ReadNoticeEvent;
import com.laoodao.caididi.retrofit.user.Message;
import com.laoodao.caididi.retrofit.user.Notice;
import com.laoodao.caididi.ui.my.activity.MessageDetailActivity;

import ezy.lite.itemholder.adapter.ItemAdapter;
import ezy.lite.itemholder.annotation.ItemLayout;
import rx.Observable;

/**
 * Created by WORK on 2017/1/11.
 */
@ItemLayout(R.layout.item_notice)
public class NoticeHolder extends BindingHolder<Notice, ItemNoticeBinding> {

    public NoticeHolder(View v) {
        super(v);
        v.setOnClickListener(v1 -> {
            if (item == null) {
                return;
            }
            binding.tvState.setVisibility(View.GONE);
            RxBus.post(new ReadNoticeEvent(item.id));
            if (TextUtils.isEmpty(item.url)) {
                MessageDetailActivity.start(v.getContext(), item.id);
            } else {
                Route.go(v.getContext(), item.url);
            }
        });
    }

    @Override
    public void refresh() {
        binding.setItem(item);
    }
}
