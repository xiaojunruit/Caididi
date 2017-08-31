package com.hyphenate.easeui.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.hyphenate.easeui.R;

/**
 * Created by long on 2017/8/28.
 */

public class FastReplyDialog extends Dialog{
    private FrameLayout mBtnContent1;
    private FrameLayout mBtnContent2;
    private FrameLayout mBtnCustom;
    private TextView mBtnCancel;
    private OnContentListener mOnContentListener;


    public FastReplyDialog(@NonNull Context context) {
        super(context, R.style.style_dialog);
    }

    public void setOnContentListener(OnContentListener listener) {
        this.mOnContentListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_fast_reply);

        initView();
        listener();
    }

    private void initView() {
        mBtnContent1 = (FrameLayout) findViewById(R.id.btn_content1);
        mBtnCancel = (TextView) findViewById(R.id.btn_cancel);
        mBtnContent2 = (FrameLayout) findViewById(R.id.btn_content2);
        mBtnCustom = (FrameLayout) findViewById(R.id.btn_custom);
    }

    private void listener() {
        mBtnContent1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnContentListener.setOnContentListener("现在不方便接听，稍后给您回复。");
                dismiss();
            }
        });
        mBtnContent2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnContentListener.setOnContentListener("现在不方便接听，有什么事么？");
                dismiss();
            }
        });
        mBtnCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnContentListener.setOnContentListener("");
                dismiss();
            }
        });
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public interface OnContentListener {
        void setOnContentListener(String content);
    }
}
