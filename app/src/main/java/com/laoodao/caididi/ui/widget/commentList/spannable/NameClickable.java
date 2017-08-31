package com.laoodao.caididi.ui.widget.commentList.spannable;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

/**
 * des:名字点击监听
 * Created by xsf
 * on 2016.07.11:14
 */
public class NameClickable extends ClickableSpan implements View.OnClickListener {
    private final ISpanClick mListener;
    private int mPosition;

    public NameClickable(ISpanClick l, int position) {
        mListener = l;
        mPosition = position;
    }

    @Override
    public void onClick(View widget) {
        mListener.onClick(mPosition);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);

        ds.setColor(Color.parseColor("#ff00ff"));
        ds.setUnderlineText(false);
        ds.clearShadowLayer();
    }
}
