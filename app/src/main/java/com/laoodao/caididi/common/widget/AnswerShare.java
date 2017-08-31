package com.laoodao.caididi.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.laoodao.caididi.R;

/**
 * Created by Administrator on 2016/12/14.
 */

public class AnswerShare extends LinearLayout {
    private Drawable icShare;
    private String textShare;
    private ImageView imgShare;
    private TextView tvShare;
    public AnswerShare(Context context) {
        this(context, null);
    }

    public AnswerShare(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnswerShare(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a=context.obtainStyledAttributes(attrs, R.styleable.AnswerShare);
        icShare=a.getDrawable(R.styleable.AnswerShare_icShare);
        textShare=a.getString(R.styleable.AnswerShare_textShare);
        a.recycle();
        inflate(context,R.layout.layout_answer_share,this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        imgShare= (ImageView) findViewById(R.id.img_share);
        tvShare= (TextView) findViewById(R.id.tv_share);
        imgShare.setImageDrawable(icShare);
        tvShare.setText(textShare);
    }

    public AnswerShare LLShare(){
        return this;
    }

}
