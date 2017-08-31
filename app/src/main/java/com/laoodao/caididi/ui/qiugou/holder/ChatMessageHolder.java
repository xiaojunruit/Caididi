package com.laoodao.caididi.ui.qiugou.holder;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.support.v4.view.ViewCompat;
import android.text.method.LinkMovementMethod;
import android.view.View;

import com.laoodao.caididi.R;
import com.laoodao.caididi.common.binding.BindingHolder;
import com.laoodao.caididi.databinding.ItemChatMessageBinding;
import com.laoodao.caididi.retrofit.main.ChatMessage;
import com.laoodao.caididi.ui.qiugou.activity.MsgImageActivity;

import ezy.lite.itemholder.annotation.ItemLayout;
import ezy.lite.util.UI;

/**
 * Created by WORK on 2016/9/12.
 */
@ItemLayout(R.layout.item_chat_message)
public class ChatMessageHolder extends BindingHolder<ChatMessage, ItemChatMessageBinding> implements View.OnClickListener,View.OnLongClickListener {

    private ClipboardManager mClipboard;
    private ClipData clipData;


    public ChatMessageHolder(View v) {
        super(v);
        binding.txtContent.setMovementMethod(LinkMovementMethod.getInstance());
        /*binding.txtContent.setSpannableFactory(LinkSpannable.factory);*/
        binding.imgContent.setOnClickListener(this);
        binding.txtContent.setOnLongClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (item.type == 1) {
            MsgImageActivity.start(v.getContext(), item.content);
        }
    }

    @Override
    public void refresh() {
        if (!item.isSelf) {

            ViewCompat.setLayoutDirection(binding.root, ViewCompat.LAYOUT_DIRECTION_LTR);
            binding.content.setBackgroundResource(R.mipmap.chat_bg_reply);
        } else {
            ViewCompat.setLayoutDirection(binding.root, ViewCompat.LAYOUT_DIRECTION_RTL);
            binding.content.setBackgroundResource(R.mipmap.chat_bg_user);
        }
        binding.setItem(item);
        binding.executePendingBindings();
    }
    @Override
    public boolean onLongClick(View view) {

        if(item.type==0){
            mClipboard = (ClipboardManager) view.getContext().getSystemService(view.getContext().CLIPBOARD_SERVICE);
            clipData = ClipData.newPlainText("txtContent", UI.getText(view,binding.txtContent.getId()));
            mClipboard.setPrimaryClip(clipData);
            UI.showToast(view.getContext(),"已复制到剪切板！");
        }

        return false;
    }

    /*public static class LinkSpan extends ClickableSpan implements LineHeightSpan {
        final String mUrl;

        public LinkSpan(String url) {
            mUrl = url;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(ds.linkColor);
            ds.setUnderlineText(false);
        }

        @Override
        public void onClick(View widget) {
            try {
                RxBus.post(new ChatFaqClickEvent(Integer.valueOf(mUrl)));
            } catch (NumberFormatException e) {

            }
        }


        private int mSpacing = Device.sp2px(10);

        @Override
        public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int v, Paint.FontMetricsInt fm) {
            Spanned spanned = (Spanned) text;
            int st = spanned.getSpanStart(this);
            int en = spanned.getSpanEnd(this);
            if (start == st) {
                fm.ascent -= mSpacing;
                fm.top -= mSpacing;
            }
//            if (end == en) {
//                fm.descent += BOTTOM_SPACING;
//                fm.bottom += BOTTOM_SPACING;
//            }
        }
    }*/

    /*private static class LinkSpannable extends SpannableString {
        public static final Factory factory = new Factory() {
            @Override
            public Spannable newSpannable(CharSequence source) {
                return new LinkSpannable(source);
            }
        };
        public LinkSpannable(CharSequence source) {
            super(source);
        }

        @Override
        public void setSpan(Object what, int start, int end, int flags) {
            if (what instanceof URLSpan) {
                what = new LinkSpan(((URLSpan) what).getURL());
            }
            super.setSpan(what, start, end, flags);
        }
    }*/
}
